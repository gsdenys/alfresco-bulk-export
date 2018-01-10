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
package org.alfresco.extensions.bulkexport;

import org.alfresco.extensions.bulkexport.controler.CacheGeneratedException;
import org.alfresco.extensions.bulkexport.controler.Engine;
import org.alfresco.extensions.bulkexport.dao.AlfrescoExportDao;
import org.alfresco.extensions.bulkexport.dao.AlfrescoExportDaoImpl;
import org.alfresco.extensions.bulkexport.model.FileFolder;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import java.io.IOException;

/**
 * This class has a function to start the export process data contained in the repository.
 * 
 * @author Denys G. Santos (gsdenys@gmail.com)
 * @version 1.0.1
 */
public class Export extends AbstractWebScript 
{
    Log log = LogFactory.getLog(Export.class);

    /** Alfresco {@link ServiceRegistry} populated by Spring Framework. */
    protected ServiceRegistry serviceRegistry;
    
    /** Data Access Object to Alfresco Repository. */
    protected AlfrescoExportDao dao;
    
    /** File and folder manager. */
    protected FileFolder fileFolder;
    
    /** Engine of system */
    protected Engine engine;
    
    
    /**
     * Method to start program execution. 
     * 
     * @param req  The HTTP request parameter
     * @param res  The HTTP response parameter
     * @throws IOException
     */
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException 
    {
        log.debug("execute");
        
        StopWatch timer = new StopWatch();

        //get URL parameters
        String nodeRef = req.getParameter("nodeRef");
        String base = req.getParameter("base");
        
        boolean scapeExported = false;
        boolean exportVersions = false;
        boolean revisionHead = false;
        boolean useNodeCache = false;
        int nbOfThreads = 1;
        int exportChunkSize = 10;
        int nbOfTasks = 2;
        
        if (req.getParameter("ignoreExported") != null)
        {
            if(req.getParameter("ignoreExported").equals("true")) 
            {
                scapeExported = true;
            }
        }

        // if a node has revisions, then export them as well
        if (req.getParameter("exportVersions") != null)
        {
            if(req.getParameter("exportVersions").equals("true")) 
            {
                exportVersions = true;
            }
        }

        // If this option is defined as true then all revisions are numbered
        // otherwise the bulk importer revisions are used (head is not named
        // with a revision)
        if (req.getParameter("revisionHead") != null)
        {
            if(req.getParameter("revisionHead").equals("true")) 
            {
                revisionHead = true;
            }
        }

        // If set to true then read a node.cache in the export directory as opposed to rescanning for nodes to export.
        // 
        if (req.getParameter("useNodeCache") != null)
        {
            if(req.getParameter("useNodeCache").equals("true")) 
            {
                useNodeCache = true;
            }
        }

        if (req.getParameter("nbOfThreads") != null)
        {
            if(StringUtils.isNumeric(req.getParameter("nbOfThreads")))
            {
                nbOfThreads = (int)Integer.parseInt(req.getParameter("nbOfThreads"));
            }
        }

        if (req.getParameter("exportChunkSize") != null)
        {
            if(StringUtils.isNumeric(req.getParameter("exportChunkSize")))
            {
                exportChunkSize = (int)Integer.parseInt(req.getParameter("exportChunkSize"));
            }
        }

        if (req.getParameter("nbOfTasks") != null)
        {
            if(StringUtils.isNumeric(req.getParameter("nbOfTasks")))
            {
                nbOfTasks = (int)Integer.parseInt(req.getParameter("nbOfTasks"));
            }
        }
        
        //init variables
        dao = new AlfrescoExportDaoImpl(this.serviceRegistry);
        fileFolder = new FileFolder(res, base, scapeExported);
        engine = new Engine(dao, fileFolder, exportVersions, revisionHead, useNodeCache, nbOfThreads, exportChunkSize, nbOfTasks);
        
        NodeRef nf = null;


        log.info("Bulk Export started");

        try
        {
            nf = dao.getNodeRef(nodeRef);
            engine.execute(nf);
            res.getWriter().write("Export finished Successfully\n");
        } 
        catch (CacheGeneratedException e)
        {
            res.getWriter().write("*****************************************************************************************************\n");
            res.getWriter().write("** No Export performed - Cache file generated only - re-run to use cache file\n");
            res.getWriter().write("*****************************************************************************************************\n\n\n");
        }
        catch (Exception e) 
        {
            log.error("Error found during Export (Reason): " + e.toString() + "\n");
            e.printStackTrace();
            res.getWriter().write("*****************************************************************************************************\n");
            res.getWriter().write("** ERROR occured:\n");
            res.getWriter().write("** " + e.toString() + "\n");
            res.getWriter().write("*****************************************************************************************************\n\n\n");
        }

        try {
            //
            // writes will not appear until the script is finished, flush does not help
            //
            res.getWriter().write("Performed Export with the following Parameters :\n");
            res.getWriter().write("   export folder   : " + base + "\n");
            res.getWriter().write("   node to export  : " + nodeRef + "\n");
            res.getWriter().write("   ignore exported : " + scapeExported + "\n");
            res.getWriter().write("   export versions : " + exportVersions + "\n");
            res.getWriter().write("   bulk import revision scheme: " + !revisionHead + "\n");
            res.getWriter().write("   Nb. of threads: " + nbOfThreads + "\n");
            res.getWriter().write("   Nb. of Tasks: " + nbOfTasks + "\n");
            res.getWriter().write("   Chunk size: " + exportChunkSize + "\n");

            long duration = timer.elapsedTime();
            res.getWriter().write("Export elapsed time: minutes:" + duration / 60 + " , seconds: " + duration + "\n");

            log.info("Bulk Export finished");
        }catch (Throwable e){
            log.error("Error when finishing Export (Reason): " + e.toString() + "\n");
            e.printStackTrace();
            res.getWriter().write("*****************************************************************************************************\n");
            res.getWriter().write("** ERROR occured:\n");
            res.getWriter().write("** " + e.toString() + "\n");
            res.getWriter().write("*****************************************************************************************************\n\n\n");

        }
    }


    public ServiceRegistry getServiceRegistry() 
    {
        return serviceRegistry;
    }


    public void setServiceRegistry(ServiceRegistry serviceRegistry) 
    {
        this.serviceRegistry = serviceRegistry;
    }
}
