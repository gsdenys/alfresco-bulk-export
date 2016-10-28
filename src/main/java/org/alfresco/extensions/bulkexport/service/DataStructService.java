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

import org.alfresco.extensions.bulkexport.model.BeConstraintValue;
import org.alfresco.service.cmr.repository.NodeRef;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * The java service to manage data structure in alfresco node.
 *
 * @author Denys G. Santos
 * @since 2.0.0
 * @version 2.0.0
 */
public interface DataStructService {

    /**
     * Recovery the Bulk Export base folder
     *
     * @return NodeRef
     */
    public NodeRef getBulkExportFolder();

    /**
     * Create the data structure folder base
     *
     * @param ddFolder
     *        the Data Dictionary Folder
     * @return NodeRef
     */
    NodeRef createBulkExportFolder(final NodeRef ddFolder);

    /**
     * Create a folder nodeRef to store the export configuration
     *
     * @param nodeRef
     *        the nodeRef to be exported
     * @return nodeRef
     */
    NodeRef createNewExportProject(final NodeRef nodeRef);

    /**
     * Create the configuration node. This service save the configuration {@link Map} in a JSON format at
     * the content node
     *
     * @param projectFolder
     *        the export project folder
     * @param configuration
     *        the configuration map
     * @return NodeRef
     */
    NodeRef createConfigNode(final NodeRef projectFolder, final Serializable configuration);

    /**
     * Load a ConfigNode. Once load this node needs to be marked as processing
     *
     * @param projectFolder
     *         the export project folder
     * @param value
     *        the constraint to be used to search documents
     * @return Map
     */
    Map<String, Map<String, String>> loadConfigNode(final NodeRef projectFolder, final BeConstraintValue value);

    /**
     * Load a config Directory List. Once load this node needs to be marked as processing
     *
     * @param projectFolder
     *         the export project folder
     * @return List
     */
    List<String> loadConfigDirectoryList(final NodeRef projectFolder);

    /**
     * Set the config node as Completed
     *
     * @param configNode
     *        the configuration node
     * @return boolean
     */
    boolean setConfigAsCompleted(final NodeRef configNode);
}
