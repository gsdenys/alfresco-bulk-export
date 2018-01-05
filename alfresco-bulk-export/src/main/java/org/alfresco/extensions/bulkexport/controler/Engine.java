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

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.extensions.bulkexport.dao.AlfrescoExportDao;
import org.alfresco.extensions.bulkexport.model.FileFolder;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * This class is a engine of systems
 *
 * @author Denys G. Santos (gsdenys@gmail.com)
 * @version 1.0.1
 */
public class Engine
{
    Log log = LogFactory.getLog(Engine.class);

    /** Data Access Object */
    private AlfrescoExportDao dao;

    /** if true the look for a cache containing a list of all nodes to export
     * */
    private boolean useNodeCache;


    /** File and folder manager */
    private FileFolder fileFolder;

    private boolean exportVersions;

    /** If true the the head revision will be named, eg. if head revision is 1.4 then filename will contain the revision.
     * This behaviour is not how the bulk importer expects revisions */
    private boolean revisionHead;

    private int nbOfWantedThreads= 5;

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
        try{
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
        }catch (Throwable e){
            e.printStackTrace();
            log.info("Error Multithreading", e);
            throw e;
        }


        log.debug("execute (noderef) finished");
        return nodes;
    }



    /**
     * Iterate over nodes to export, and do appropriate action
     *
     * @param nodesToExport
     */
    private void exportNodes(final List<NodeRef> nodesToExport)
    {
        int lastLimitNodeNumber = 0 ;
        for(int threadNumber=1; threadNumber <= nbOfWantedThreads; threadNumber++) {
            log.info("Starting thread N°"+threadNumber);

            int lowerLimitNodeNumber = lastLimitNodeNumber;
            int upperLimitNodeNumber = (int)(nodesToExport.size() * (threadNumber / (double)nbOfWantedThreads)) ;
            List<NodeRef> nodesForCurrentThread = nodesToExport.subList(lowerLimitNodeNumber, upperLimitNodeNumber);

            NodeExportProcess process = new NodeExportProcess(nodesForCurrentThread, threadNumber, exportVersions, revisionHead, dao, fileFolder);
            process.run();

            log.info("Thread is finished N°"+threadNumber);

            lastLimitNodeNumber = upperLimitNodeNumber;
        }
    }
}
