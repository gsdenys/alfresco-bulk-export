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

import java.io.IOException;

import org.alfresco.extensions.bulkexport.controler.Engine;
import org.alfresco.extensions.bulkexport.dao.AlfrescoExportDao;
import org.alfresco.extensions.bulkexport.dao.AlfrescoExportDaoImpl;
import org.alfresco.extensions.bulkexport.model.FileFolder;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * This class has a function to start the export process data contained in the repository.
 * 
 * @author Denys G. Santos (gsdenys@gmail.com)
 * @version 1.0.1
 */
public class Export extends AbstractWebScript {

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
	public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
		//get URL parameters
		String nodeRef = req.getParameter("nodeRef");
		String base = req.getParameter("base");
		
		boolean scapeExported = false;
		
		if (req.getParameter("ignoreExported") != null){
			if(req.getParameter("ignoreExported").equals("true")) {
				scapeExported = true;
			}
		}
		
		//init variables
		dao = new AlfrescoExportDaoImpl(this.serviceRegistry);
		fileFolder = new FileFolder(base, scapeExported);
		engine = new Engine(dao, fileFolder);
		
		try{
			NodeRef nf = dao.getNodeRef(nodeRef);
			engine.execute(nf);
			res.getWriter().write("Process finished Successfully");
		} catch (Exception e) {
			e.printStackTrace();
			res.getWriter().write(e.toString());
		}
	}


	public ServiceRegistry getServiceRegistry() {
		return serviceRegistry;
	}


	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}
}
