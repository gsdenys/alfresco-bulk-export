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

import org.alfresco.service.cmr.repository.NodeRef;

import java.util.Map;

/**
 * The java service to map all nodes that will be of exported.
 *
 * @author Denys G. Santos
 * @since 2.0.0
 * @version 2.0.0
 */
interface MapService {
    /**
     * Navigate over all descendants nodes storing information about nodes.
     *
     * @param ref
     *        the root {@link NodeRef} to be mapped
     * @param rootIncluded
     *        boolean to define if the root nodeRef will be included in the mapped
     *        or not
     * @return Map
     */
    Map<String, String> navigate(final NodeRef ref, final boolean rootIncluded);

    /**
     * Store the node at the mapping resource. The information that wanna be store are:
     * <ul>
     *     <li><b>NodeRef:</b> The NodeRef element</li>
     *     <li><b>Full Path:</b> The full path to the node</li>
     *     <li><b>Date of Map:</b> The datetime that this node map was done</li>
     *     <li><b>Base Type:</b> Link, Content or Folder </li>
     * </ul>
     *
     * @param ref
     *        reference to the node that will be mapped
     * @return boolean
     */
    boolean storeAsToBeProcess(final NodeRef ref);
}
