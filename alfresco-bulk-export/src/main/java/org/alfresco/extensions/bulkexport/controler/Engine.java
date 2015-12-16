/**
 *  This file is part of Alfresco Bulk Export Tool.
 * 
 *  Alfresco Bulk Export Tool is free software: you can redistribute it 
 *  and/or modify it under the terms of the GNU General Public License as
 *  published by the Free Software Foundation, either version 3 of the 
 *  License, or (at your option) any later version.
 *
 *  Alfresco Bulk Export Tool  is distributed in the hope that it will be 
 *  useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General 
 *  Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along 
 *  with Alfresco Bulk Export Tool. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.extensions.bulkexport.controler;

import java.io.ByteArrayOutputStream;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.io.*;

import org.alfresco.extensions.bulkexport.dao.AlfrescoExportDao;
import org.alfresco.extensions.bulkexport.dao.NodeRefRevision;
import org.alfresco.extensions.bulkexport.model.FileFolder;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * This classe is a engine of systems
 * 
 * @author Denys G. Santos (gsdenys@gmail.com)
 * @version 1.0.1
 */
public class Engine 
{
    Log log = LogFactory.getLog(Engine.class);

    /** Data Access Object */
    private AlfrescoExportDao dao;
    
    /** File and folder manager */
    private FileFolder fileFolder;

    private boolean exportVersions;

    /** If true the the head revision will be named, eg. if head revision is 1.4 then filename will contain the revision. 
     * This behaviour is not how the bulk importer expects revisions */
    private boolean revisionHead;

    /** if true the look for a cache containing a list of all nodes to export
     * */
    private boolean useNodeCache;
    
    /**
     * Engine Default Builder
     * 
     * @param dao Data Access Object
     * @param fileFolder File and Folder magager
     */
    public Engine(AlfrescoExportDao dao, FileFolder fileFolder, boolean exportVersions, boolean revisionHead, boolean useNodeCache) 
    {
        this.dao =  dao;
        this.fileFolder = fileFolder;
        this.exportVersions = exportVersions;
        this.revisionHead = revisionHead;
        this.useNodeCache = useNodeCache;
    }

    /**
     * Recursive method to export alfresco nodes to file system 
     * 
     * @param nodeRef
     */
    public void execute(NodeRef nodeRef) throws Exception 
    {    
        // case node is folder create a folder and execute recursively 
        // other else create file 
        log.debug("execute (noderef)");
        
        if(!this.dao.isNodeIgnored(nodeRef.toString()))
        {    
            log.info("Find all nodes to export (no history)");
            List<NodeRef> allNodes = getNodesToExport(nodeRef);
            log.info("Nodes to export = " + allNodes.size());
            exportNodes(allNodes);
        }    
        log.debug("execute (noderef) finished");
    }

    private List<NodeRef> getNodesToExport(NodeRef rootNode) throws Exception 
    {
        List<NodeRef> nodes = null;
        if (useNodeCache)
        {
            nodes = retrieveNodeListFromCache(rootNode);
        }

        if (nodes == null)
        {
            nodes = findAllNodes(rootNode);
            storeNodeListToCache(rootNode, nodes);
            if (useNodeCache)
            {
                log.info("Generated Cached Node list");
                throw new CacheGeneratedException("Generated Cached Node List Only");
            }
        }
        else
        {
            log.info("Using Cached Node list");
        }

        return nodes;
    }

    private String nodeFileName(NodeRef rootNode)
    {
        File fname = new File(fileFolder.basePath(), rootNode.getId() + ".cache");
        return fname.getPath();
    }

    private void storeNodeListToCache(NodeRef rootNode, List<NodeRef> list) throws Exception 
    {
        // get a better name
        FileOutputStream fos= new FileOutputStream(nodeFileName(rootNode));
        ObjectOutputStream oos= new ObjectOutputStream(fos);
        oos.writeObject(list);
        oos.close();
        fos.close();
    }

    private List<NodeRef> retrieveNodeListFromCache(NodeRef rootNode) throws Exception 
    {
        List<NodeRef> list = null;

        try
        {
            FileInputStream fis = new FileInputStream(nodeFileName(rootNode));
            ObjectInputStream ois = new ObjectInputStream(fis);
            list = (List<NodeRef>) ois.readObject();
            ois.close();
        }
        catch (FileNotFoundException e)
        {
            // this exception means we have no noelist cache - we just ignore and continue
            log.debug("could not open nodelist cache file");
        }
        return list;
    }

    /**
     * Recursive find of all item head nodes from a given node ref
     * 
     * @param nodeRef
     */
    private List<NodeRef> findAllNodes(NodeRef nodeRef) throws Exception 
    {    
        List<NodeRef> nodes = new ArrayList<NodeRef>();

        log.debug("findAllNodes (noderef)");
        
        if(!this.dao.isNodeIgnored(nodeRef.toString()))
        {    
            if(this.dao.isFolder(nodeRef))
            {
                nodes.add(nodeRef); // add folder as well
                List<NodeRef> children= this.dao.getChildren(nodeRef);
                for (NodeRef child : children) 
                {            
                    nodes.addAll(this.findAllNodes(child));
                }
            } 
            else 
            {
                nodes.add(nodeRef);
            }
        }     

        log.debug("execute (noderef) finished");
        return nodes;
    }

    private void exportHeadRevision(NodeRef nodeRef) throws Exception
    {
        this.createFile(nodeRef);
    }

    private void exportFullRevisionHistory(NodeRef nodeRef) throws Exception
    {
        Map<String,NodeRefRevision> nodes = this.dao.getNodeRefHistory(nodeRef.toString());
        if (nodes != null)
        {
            List sortedKeys=new ArrayList(nodes.keySet());

            Collections.sort(sortedKeys, new VersionNumberComparator());
            if (sortedKeys.size() < 1)
            {
                throw new Exception("no revisions available");
            }

            String headRevision = (String)sortedKeys.get(sortedKeys.size()-1);

            for (String revision : nodes.keySet()) 
            {
                NodeRefRevision nodeRevision = nodes.get(revision);
                this.createFile(nodeRef, nodeRevision.node, revision, headRevision == revision);
            }
        }
        else
        {
            // no revision history so lets just create the most recent revision
            log.debug("execute (noderef) no revision history found, dump node as head revision");
            this.createFile(nodeRef, nodeRef, "1.0", true);
        }
    }

    /**
     * Iterate over nodes to export, and do appropriate action
     * 
     * @param nodesToExport
     */
    private void exportNodes(List<NodeRef> nodesToExport) throws Exception 
    {
        final int NODES_TO_PROCESS = 100;

        int logCount = nodesToExport.size();

        for (NodeRef nodeRef : nodesToExport) 
        {
            logCount--;
            if(this.dao.isFolder(nodeRef))
            {
                this.createFolder(nodeRef);
            } 
            else
            {
                if (exportVersions)
                {
                    exportFullRevisionHistory(nodeRef);
                }
                else
                {
                    exportHeadRevision(nodeRef);
                }
            }

            if (logCount % NODES_TO_PROCESS == 0)
            {
                log.info("Remaining Parent Nodes to process " + logCount);
            }
        }
    }
    
    
    /**
     * Create file (Document and Bulk XML Meta data)
     * 
     * @param file 
     * @throws Exception
     */
    private void createFile(NodeRef headNode, NodeRef file, String revision, boolean isHeadRevision) throws Exception 
    {
        String path = null;
        if (revision == null)
        {
            log.error("createFile (headNode: "+headNode.toString() + " , filenode: )"+file.toString()+" , revision: " + revision + ")");
            throw new Exception("revision for node was not found");
        }

        path = this.dao.getPath(headNode) + "." + revision;

        // if we are exporting using the revisions compatible with alfresco bulk import then we do not number the head(most recent) revisoon
        if (!revisionHead && isHeadRevision)
        {
            path = this.dao.getPath(headNode);
        }

        doCreateFile(file, path);
    }

    private void createFile(NodeRef file) throws Exception 
    {
        String path = null;
        path = this.dao.getPath(file);
        doCreateFile(file, path);
    }

    private void doCreateFile(NodeRef file, String path) throws Exception 
    {
        //get Informations
        log.debug("doCreateFile (noderef)");

        // need these variables out of the try scope for debugging purposes when the exception is thrown
        String type = null;
        List<String> aspects = null;
        Map<String, String> properties = null;

        try
        {
            String fname = this.fileFolder.createFullPath(path);
            log.debug("doCreateFile file =" + fname);
            if (this.dao.getContentAndStoreInFile(file, fname) == false)
            {
                log.debug("doCreateFile ignore this file"); 
                return;
            }
            type = this.dao.getType(file);
            aspects = this.dao.getAspectsAsString(file);
            properties = this.dao.getPropertiesAsString(file);
            
            //Create Files
            this.fileFolder.insertFileProperties(type, aspects, properties, path);
            type = null;
            properties = null;
            aspects = null;
        }
        catch (Exception e) 
        {
            // for debugging purposes
            log.error("doCreateFile failed for noderef = " + file.toString());
            throw e;
        }
    }
    
    
    /**
     * Create Folder and XML Metadata
     * 
     * @param file
     * @throws Exception
     */
    private void createFolder(NodeRef folder) throws Exception 
    {
        //Get Data
        log.debug("createFolder");
        String path = this.dao.getPath(folder);
        log.debug("createFolder path="+path);
        String type = this.dao.getType(folder);
        log.debug("createFolder type="+type);
        List<String> aspects = this.dao.getAspectsAsString(folder);
        Map<String, String> properties = this.dao.getPropertiesAsString(folder);
        
        //Create Folder and XMl Metadata
        this.fileFolder.createFolder(path);
        this.fileFolder.insertFileProperties(type, aspects, properties, path);
    }
}
