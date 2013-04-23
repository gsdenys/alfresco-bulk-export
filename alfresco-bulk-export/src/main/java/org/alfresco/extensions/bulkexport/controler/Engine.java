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

import org.alfresco.extensions.bulkexport.dao.AlfrescoExportDao;
import org.alfresco.extensions.bulkexport.model.FileFolder;
import org.alfresco.service.cmr.repository.NodeRef;

/**
 * This classe is a engine of systems
 * 
 * @author Denys G. Santos (gsdenys@gmail.com)
 * @version 1.0.1
 */
public class Engine {

	/** Data Access Object */
	private AlfrescoExportDao dao;
	
	/** File and folder manager */
	private FileFolder fileFolder;
	
	
	/**
	 * Engine Default Builder
	 * 
	 * @param dao Data Access Object
	 * @param fileFolder File and Folder magager
	 */
	public Engine(AlfrescoExportDao dao, FileFolder fileFolder) {
		this.dao =  dao;
		this.fileFolder = fileFolder;
	}
	
	
	/**
	 * Recursive method to export alfresco nodes to file system 
	 * 
	 * @param nodeRef
	 */
	public void execute(NodeRef nodeRef) throws Exception {	
		// case node is folder create a folder and execute recursively 
		// other else create file 
		
		if(!this.dao.isNodeIgnored(nodeRef.toString())){	
			if(this.dao.isFolder(nodeRef)){
				this.createFolder(nodeRef);
	
				List<NodeRef> children= this.dao.getChildren(nodeRef);
				for (NodeRef child : children) {			
					this.execute(child);
				}
			} else {
				this.createFile(nodeRef);
			}
		}	
	}
	
	
	/**
	 * Create file (Document and Bulk XML Meta data)
	 * 
	 * @param file 
	 * @throws Exception
	 */
	private void createFile(NodeRef file) throws Exception {
		//get Informations
		ByteArrayOutputStream out = this.dao.getContent(file);
		String type = this.dao.getType(file);
		List<String> aspects = this.dao.getAspectsAsString(file);
		Map<String, String> properties = this.dao.getPropertiesAsString(file);
		String path = this.dao.getPath(file);
		
		//Create Files
		this.fileFolder.insertFileContent(out, path);
		this.fileFolder.insertFileProperties(type, aspects, properties, path);
	}
	
	
	/**
	 * Create Folder and XML Metadata
	 * 
	 * @param file
	 * @throws Exception
	 */
	private void createFolder(NodeRef folder) throws Exception {
		//Get Data
		String path = this.dao.getPath(folder);
		String type = this.dao.getType(folder);
		List<String> aspects = this.dao.getAspectsAsString(folder);
		Map<String, String> properties = this.dao.getPropertiesAsString(folder);
		
		//Create Folder and XMl Metadata
		this.fileFolder.createFolder(path);
		this.fileFolder.insertFileProperties(type, aspects, properties, path);
	}
}
