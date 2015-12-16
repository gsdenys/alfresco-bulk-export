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
package org.alfresco.extensions.bulkexport.dao;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;


/**
 * Interface to Data Access Object
 * 
 * @author Denys Santos (gsdenys@gmail.com)
 * @version 1.0.1
 */
public interface AlfrescoExportDao 
{
    public boolean isNodeIgnored(String nodeRef);
    
    /**
     * Method to get node properties.
     * 
     * @param nodeRef  Alfresco Node Reference 
     * @return {@link Map}
     * @throws Exception
     */
    public Map<QName, Serializable> getProperties(NodeRef nodeRef) throws Exception;
    
    
    /**
     * Method to get node properties in {@link String} properties
     * 
     * This method exclude all items in ignored properties
     * 
     * @param nodeRef Alfresco Node Reference
     * @return {@link Map}
     * @throws Exception
     */
    public Map<String, String> getPropertiesAsString(NodeRef nodeRef) throws Exception;
    
    
    /**
     * Method to get children from node Reference 
     * 
     * @param nodeRef Alfresco Node Reference
     * @return {@link List}
     * @throws Exception
     */
    public List<NodeRef> getChildren(NodeRef nodeRef) throws Exception;

    
    /**
     * Method to get the node path reference 
     * 
     * @param nodeRef Alfresco Node Reference
     * @return {@link String}
     * @throws Exception
     */
    public String getPath(NodeRef nodeRef) throws Exception;

    
    /**
     * Method to get node content
     * 
     * @param nodeRef Alfresco Node Reference
     * @return {@link ByteArrayOutputStream}
     * @throws Exception
     */
    public ByteArrayOutputStream getContent(NodeRef nodeRef) throws Exception;

    /**
     * Method to store node contents directly to File.
     * Doing this to see if it helps with the memory issues the plugin has on large datasets
     * 
     * @param nodeRef Alfresco Node Reference
     * @param outputFileName filename to use when storing data
     * @return {@link ByteArrayOutputStream}
     * @throws Exception
     */
    public boolean getContentAndStoreInFile(NodeRef nodeRef, String outputFileName) throws Exception; 
    
    /**
     * Method to get specific property
     * 
     * @param nodeRef Alfresco Node Reference
     * @param propertyQName {@link QName} object
     * @return {@link String}
     * @throws Exception
     */
    public String getProperty(NodeRef nodeRef, QName propertyQName) throws Exception;
    
    
    /**
     * Method to get node type
     * 
     * @param nodeRef Alfresco Node Reference
     * @return {@link String}
     * @throws Exception
     */
    public String getType(NodeRef nodeRef) throws Exception;
    
    
    /**
     * Method to get as {@link List} of aspect {@link QName}
     * 
     * @param nodeRef Alfresco Node Reference
     * @return {@link QName}
     * @throws Exception
     */
    public List<QName> getAspects(NodeRef nodeRef) throws Exception;
    
    
    /**
     * Method to get a {@link List} of aspects in {@link String} format
     * 
     * @param nodeRef Alfresco Node Reference
     * @return {@link String}
     * @throws Exception
     */
    public List<String> getAspectsAsString(NodeRef nodeRef) throws Exception;
    
    
    /**
     * Method to verify if {@link NodeRef} is a folder
     * 
     * @param nodeRef Alfresco Node Reference
     * @return {@link Boolean}
     * @throws Exception
     */
    public boolean isFolder(NodeRef nodeRef) throws Exception;

    
    /**
     * Method to get a {@link NodeRef} from {@link String}
     * 
     * @param nodeRef Alfresco Node Reference in {@link String} format
     * @return {@link NodeRef}
     * @throws Exception
     */
    public NodeRef getNodeRef(String nodeRef) throws Exception;

    /**
     * Method to get all available revisions for a Node (@link string)
     * 
     * @param nodeRef Alfresco Node Reference in {@link String} format
     * @return {@link Map}, null if no history
     * @throws Exception
     */
    public Map<String,NodeRefRevision> getNodeRefHistory(String nodeRef) throws Exception;
}


