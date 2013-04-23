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
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.model.ContentModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.Path;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;

import com.ibm.icu.text.SimpleDateFormat;


/**
 * Implementation of {@link AlfrescoExportDao} interface
 * 
 * @author Denys Santos (gsdenys@gmail.com)
 * @version 1.0.1
 */
public class AlfrescoExportDaoImpl implements AlfrescoExportDao {

	/** Alfresco {@link ServiceRegistry} to Data Access Object */ 
	private ServiceRegistry registry;
		
	private QName ignoreAspectQname[] = {
			ContentModel.ASPECT_TAGGABLE
	};
	
	private String ignoreAspectPrefix[] = {
			"app"
	};
	
	private QName ignorePropertyQname[] = { 
			ContentModel.PROP_NODE_DBID, 
			ContentModel.PROP_NODE_UUID, 
			ContentModel.PROP_CATEGORIES,
			ContentModel.PROP_CONTENT,
			ContentModel.ASPECT_TAGGABLE
	};
	
	private String[] ignorePropertyPrefix = {
			"app",
			"exif"
	};
	
	private QName[] ignoredType = {
			ContentModel.TYPE_SYSTEM_FOLDER,
			ContentModel.TYPE_LINK,
			QName.createQName("{http://www.alfresco.org/model/action/1.0}action")
	};
	
	
	/**
	 * Data Access Object Builder
	 * 
	 * @param registry Alfresco {@link ServiceRegistry} 
	 */
	public AlfrescoExportDaoImpl(ServiceRegistry registry) {
		this.registry = registry;
	}
	
	
	/**
	 * @see com.alfresco.bulkexport.dao.AlfrescoExportDao#getProperties(java.lang.String)
	 */
	public Map<QName, Serializable> getProperties(NodeRef nodeRef) throws Exception {
		NodeService nodeService = this.registry.getNodeService();
		
		Map<QName, Serializable> properties = nodeService.getProperties(nodeRef);
		
		return properties;
	}
	
	
	/**
	 * @see com.alfresco.bulkexport.dao.AlfrescoExportDao#getMetadataAsString(java.lang.String)
	 */
	public Map<String, String> getPropertiesAsString(NodeRef nodeRef) throws Exception {
				
		Map<QName, Serializable> properties = this.getProperties(nodeRef);
		
		Map<String, String> props = new HashMap<String, String>();
		Set<QName> qNameSet = properties.keySet();
		
		for (QName qName : qNameSet) {
			//case the qname is in ignored type do nothing will do.
			if(this.isPropertyIgnored(qName)){
				continue;
			}
					
			Serializable obj = properties.get(qName);
			String name = this.getQnameStringFormat(qName);
			String value = this.formatMetadata(obj);
		
			//put key value in the property list as <prefixOfProperty:nameOfProperty, valueOfProperty>
			props.put(name, value);
		}
		
		return props;
	}

	
	/**
	 * @see com.alfresco.bulkexport.dao.AlfrescoExportDao#getChildren(java.lang.String)
	 */
	public List<NodeRef> getChildren(NodeRef nodeRef) throws Exception {
		NodeService nodeService = this.registry.getNodeService();
		List<NodeRef> listChildren = new ArrayList<NodeRef>();
		
		List<ChildAssociationRef> children = nodeService.getChildAssocs(nodeRef);
		
		for (ChildAssociationRef childAssociationRef : children) {
			NodeRef child = childAssociationRef.getChildRef();
				
			if(this.isTypeIgnored(nodeService.getType(child))){
				continue;
			}
			
			listChildren.add(child);
		}
		
		return listChildren;
	}
	

	/**
	 * @see com.alfresco.bulkexport.dao.AlfrescoExportDao#getFolderChildren(java.lang.String)
	 */
	public List<NodeRef> getFolderChildren(NodeRef nodeRef) throws Exception {
		FileFolderService service = this.registry.getFileFolderService();
		
		List<FileInfo> folders = service.listFolders(nodeRef);
		
		List<NodeRef> listChildren = new ArrayList<NodeRef>();
		
		for (FileInfo fileInfo : folders) {
			listChildren.add(fileInfo.getNodeRef());
		}
		
		return listChildren;
	}

	
	/**
	 * @see com.alfresco.bulkexport.dao.AlfrescoExportDao#getFileChildren(java.lang.String)
	 */
	public List<NodeRef> getFileChildren(NodeRef nodeRef) throws Exception {
		FileFolderService service = this.registry.getFileFolderService();
		
		List<FileInfo> files = service.listFiles(nodeRef);
		
		List<NodeRef> listChildren = new ArrayList<NodeRef>();
		
		for (FileInfo fileInfo : files) {
			listChildren.add(fileInfo.getNodeRef());
		}
		
		return listChildren;
	}

	
	/**
	 * @see com.alfresco.bulkexport.dao.AlfrescoExportDao#getPath(java.lang.String)
	 */
	public String getPath(NodeRef nodeRef) throws Exception {
		NodeService nodeService = this.registry.getNodeService();
		PermissionService permissionService = this.registry.getPermissionService();
		
		//get element Path
		Path path = nodeService.getPath(nodeRef);
		
		//get element name 
		Serializable name = nodeService.getProperty(nodeRef, ContentModel.PROP_NAME);
		
		//get element Path as String
		String basePath = path.toDisplayPath(nodeService, permissionService);
		
		return (basePath + "/" + name);
	}

	
	/**
	 * @see com.alfresco.bulkexport.dao.AlfrescoExportDao#getContent(java.lang.String)
	 */
	public ByteArrayOutputStream getContent(NodeRef nodeRef) throws Exception {
		ContentService contentService = this.registry.getContentService();
		
		ContentReader reader = contentService.getReader(nodeRef, ContentModel.PROP_CONTENT);
		
		InputStream in = reader.getContentInputStream();
		int size = in.available();
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buf = new byte[ (size + 100) ];
		int sizeOut;
		
		while ((sizeOut=in.read(buf)) != -1 ) {
			out.write(buf, 0, sizeOut);
		}
		
		out.flush();
		out.close();
		
		in.close();
		
		
		return out;
	}

	
	/**
	 * @see com.alfresco.bulkexport.dao.AlfrescoExportDao#getProperty(java.lang.String, java.lang.String)
	 */
	public String getProperty(NodeRef nodeRef, QName propertyQName) throws Exception {
		NodeService nodeService = this.registry.getNodeService();
		
		Serializable value = nodeService.getProperty(nodeRef, propertyQName);
		
		return this.formatMetadata(value);
	}

	
	/**
	 * @see com.alfresco.bulkexport.dao.AlfrescoExportDao#getType(java.lang.String)
	 */
	public String getType(NodeRef nodeRef) throws Exception {
		NodeService nodeService = this.registry.getNodeService();
		
		QName value = nodeService.getType(nodeRef);
		
		String name = this.getQnameStringFormat(value);
		
		return name;
	}
	
	
	/**
	 * @see com.alfresco.bulkexport.dao.AlfrescoExportDao#getAspects(java.lang.String)
	 */
	public List<QName> getAspects(NodeRef nodeRef) throws Exception {
		NodeService nodeService = this.registry.getNodeService();
		
		Set<QName> aspectSet = nodeService.getAspects(nodeRef);
		List<QName> qn = new ArrayList<QName>(aspectSet);
		
		return qn;
	}
	
	
	/**
	 * @see com.alfresco.bulkexport.dao.AlfrescoExportDao#getAspectsAsString(java.lang.String)
	 */
	public List<String> getAspectsAsString(NodeRef nodeRef) throws Exception {
		List<QName> qn = this.getAspects(nodeRef);
		List<String> str = new ArrayList<String>();
		
		for (QName qName : qn) {			
			if(this.isAspectIgnored(qName)) {
				continue;
			}
			
			String name = this.getQnameStringFormat(qName);
			str.add(name); 
		}
		
		return str;
	}
	
	
	/**
	 * @see com.alfresco.bulkexport.dao.AlfrescoExportDao#isFolder(java.lang.String)
	 */
	public boolean isFolder(NodeRef nodeRef) throws Exception {
		FileFolderService service = this.registry.getFileFolderService();

		FileInfo info = service.getFileInfo(nodeRef);
		
		return info.isFolder();
	}
	
	/**
	 * @see com.alfresco.bulkexport.dao.AlfrescoExportDao#getNodeRef(java.lang.String)
	 */
	public NodeRef getNodeRef(String nodeRef) {
		try{
			NodeRef nr = new NodeRef(nodeRef);
			return nr;
		} catch (Exception e) {
			return null;
		}
	}
	
	
	public boolean isNodeIgnored(String nodeRef) {
		NodeRef nr = getNodeRef(nodeRef);
		
		NodeService nodeService = this.registry.getNodeService();
		
		QName value = nodeService.getType(nr);
		
		return isTypeIgnored(value);
		
	}
	
	
	
	// #######################################################################################
	// ####                              PRIVATE METHODS                                   ### 
	// #######################################################################################

	/**
	 * Verify if the type qname is ignored 
	 * 
	 * @param qName
	 * @return {@link Boolean}
	 */
	private boolean isPropertyIgnored(QName qName) {
		//verify if qname is in ignored
		for (QName qn : this.ignorePropertyQname) {
			if(qn.equals(qName)){
				return true;
			}
		}
		
		//verify if qname prefix is in ignored
		//String prefix = qName.getPrefixString();
		NamespacePrefixResolver nsR = this.registry.getNamespaceService();
		String prefix = qName.getPrefixedQName(nsR).getPrefixString();
		for (String str : this.ignorePropertyPrefix) {
			
			//str.equalsIgnoreCase(prefix)
			
			if(prefix.startsWith(str)){
				return true;
			}
		}
		
		return false;
	}

	
	/**
	 * Verify if the aspect qname is ignored 
	 * 
	 * @param qName
	 * @return {@link Boolean}
	 */
	private boolean isAspectIgnored(QName qName) {
		//verify if qname is in ignored
		for (QName qn : this.ignoreAspectQname) {
			if(qn.equals(qName)){
				return true;
			}
		}
		
		//verify if qname prefix is in ignored
		//String prefix = qName.getPrefixString();
		NamespacePrefixResolver nsR = this.registry.getNamespaceService();
		String prefix = qName.getPrefixedQName(nsR).getPrefixString();
		for (String str : this.ignoreAspectPrefix) {
			if(prefix.startsWith(str)){
				return true;
			}
		}
		
		return false;
	}
	
	
	/**
	 * Verify if the tipe qname is ignored 
	 * 
	 * @param qName
	 * @return {@link Boolean}
	 */
	private boolean isTypeIgnored(QName qName) {
		//verify if qname is in ignored
		for (QName qn : this.ignoredType) {
			if(qn.equals(qName)){
				return true;
			}
		}
		
		return false;
	}
	

	/**
	 * Return Qname in String Format
	 * 
	 * @param qName
	 * @return {@link String}
	 */
	private String getQnameStringFormat(QName qName) throws Exception{
		NamespacePrefixResolver nsR = this.registry.getNamespaceService();
		return qName.getPrefixedQName(nsR).getPrefixString();
	}


	/**
	 * Format metadata guided by Bulk-Import format
	 * 
	 * @param obj
	 * @return {@link String}
	 */
	private String formatMetadata (Serializable obj){
		String returnValue = "";
		
		if(obj != null) {
			if(obj instanceof Date){
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSSZ");
				
				Date date = (Date) obj;
				returnValue = format.format(date);
				returnValue = returnValue.substring(0, 26) + ":" + returnValue.substring(26);
			} else {
				
				//
				// TODO: Format data to all bulk-import data format (list as example)
				//
				
				returnValue = obj.toString();
			}
		}
		
		return returnValue;
	}
	
	
}
