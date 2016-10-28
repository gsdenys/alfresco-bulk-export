/*
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
package org.alfresco.extensions.bulkexport.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.extensions.bulkexport.model.BeConstraintValue;
import org.alfresco.extensions.bulkexport.model.BeModel;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * The implementation of {@link DataStructService} interface.
 *
 * @author Denys G. Santos
 * @version 2.0.0
 * @since 2.0.0
 */
public class DataStructureServiceImpl implements DataStructService {
    private static final String BE_FOLDER_NAME = "Bulk Export";
    private static final String DD_FOLDER_NAME = "Data Dictionary";

    private NodeService nodeService;
    private ContentService contentService;
    private SearchService searchService;

    private NodeRef beFolder;


    /**
     * Get node based on path
     *
     * @return NodeRef
     */
    public NodeRef getBulkExportFolder() {

        //return the Bulk Export folder that are already recovery
        if (this.beFolder != null) {
            return this.beFolder;
        }

        //Recovery Bulk Export Folder
        NodeRef root = this.nodeService.getRootNode(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        NodeRef dd = this.nodeService.getChildByName(root, ContentModel.ASSOC_CONTAINS, DD_FOLDER_NAME);
        this.beFolder = this.nodeService.getChildByName(dd, ContentModel.ASSOC_CONTAINS, BE_FOLDER_NAME);

        //Create a bulk export folder if it's recovered
        if (this.beFolder != null) {
            return this.beFolder;
        }

        //create a bulk export folder. If it's return null trows an exception
        this.beFolder = this.createBulkExportFolder(dd);
        if (this.beFolder == null) {
            throw new AlfrescoRuntimeException("Unable to create or recovery Bulk Export base folder.");
        }

        return this.beFolder;
    }


    /**
     * @see DataStructService#createBulkExportFolder(NodeRef)
     */
    @Override
    public NodeRef createBulkExportFolder(final NodeRef ddFolder) {
        Map<QName, Serializable> props = new HashMap<>(1);
        props.put(ContentModel.PROP_NAME, BE_FOLDER_NAME);

        return this.nodeService.createNode(
                ddFolder,
                ContentModel.ASSOC_CONTAINS,
                QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, BE_FOLDER_NAME),
                ContentModel.TYPE_FOLDER,
                props
        ).getChildRef();
    }

    /**
     * @see DataStructService#createNewExportProject(NodeRef)
     */
    @Override
    public NodeRef createNewExportProject(final NodeRef nodeRef) {
        NodeRef beRef = this.getBulkExportFolder();

        Map<QName, Serializable> props = new HashMap<>(1);
        String name = this.getName();

        props.put(ContentModel.PROP_NAME, name);

        return this.nodeService.createNode(
                beRef,
                ContentModel.ASSOC_CONTAINS,
                QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, name),
                BeModel.TYPE_PROJECT.getQName(),
                props
        ).getChildRef();
    }

    /**
     * @see DataStructService#createConfigNode(NodeRef, Serializable)
     */
    @Override
    public NodeRef createConfigNode(final NodeRef projectFolder, final Serializable configuration) {
        Map<QName, Serializable> props = new HashMap<>(2);
        String name = this.getName();

        props.put(ContentModel.PROP_NAME, name);
        props.put(BeModel.PROP_CONFIG_PROJ_NUM.getQName(), Integer.parseInt(name));

        NodeRef newNodeRef = this.nodeService.createNode(
                projectFolder,
                ContentModel.ASSOC_CONTAINS,
                QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, name),
                BeModel.TYPE_CONFIG.getQName(),
                props
        ).getChildRef();

        Gson gson = new Gson();
        String json = gson.toJson(configuration);

        ContentWriter writer = this.contentService.getWriter(newNodeRef, ContentModel.PROP_CONTENT, true);
        writer.setMimetype(MimetypeMap.MIMETYPE_TEXT_PLAIN);
        writer.setEncoding("UTF-8");
        writer.putContent(json);

        return newNodeRef;
    }

    @Override
    public Map<String, Map<String, String>> loadConfigNode(final NodeRef projectFolder, final BeConstraintValue value) {
        List<ChildAssociationRef> childList = this.nodeService.getChildAssocs(projectFolder);

        /*childList.forEach(childAssociation -> {
            NodeRef ref = childAssociation.getChildRef();
            String status = (String) this.nodeService.getProperty(ref, BeModel.PROP_PRJ_STATUS.getQName());
            if(status.equals(value)){
                ContentReader reader = this.contentService.getReader(ref, ContentModel.PROP_CONTENT);
                String str = reader.getContentString();

                Type mapType = new TypeToken<Map<String, Map<String,String>>>(){}.getType();
                Map<String, Map<String, String>> map = new Gson().fromJson(str, mapType);
                return map;
            }
        });*/

        return null;
    }

    @Override
    public List<String> loadConfigDirectoryList(NodeRef projectFolder) {
        return null;
    }

    @Override
    public boolean setConfigAsCompleted(NodeRef configNode) {
        return false;
    }

    private String getName(){
        Random random = new Random();
        String name = "" + random.nextInt();

        return name;
    }
}
