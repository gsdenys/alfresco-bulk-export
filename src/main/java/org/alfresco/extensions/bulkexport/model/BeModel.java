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
package org.alfresco.extensions.bulkexport.model;

import org.alfresco.extensions.bulkexport.service.DataStructService;
import org.alfresco.service.namespace.QName;

/**
 * Model for Bulk Export.
 *
 * @author Denys G. Santos
 * @version 2.0.0
 * @since 2.0.0
 */
public enum BeModel {
    TYPE_PROJECT("project"),
    TYPE_CONFIG("config"),

    PROP_PRJ_STATUS("prjstatus"),

    PROP_CONFIG_STATUS("configstatus"),
    PROP_CONFIG_NUM_NODES("nNodes"),
    PROP_CONFIG_PROJ_NUM("projNum"),

    CONST_PRJ_STATUS("c_prjstatus"),
    CONST_CONFIG_STATUS("c_configstatus"),
    CONST_CONFIG_TYPE("configType");

    BeModel(String config) {
        this.type = type;
    }

    /**
     * get the {@link QName} element
     *
     * @return QName
     */
    public QName getQName() {
        return QName.createQName(uri, type);
    }

    private String uri = "{http://www.alfresco.com/model/bulkexport/2.0}";
    private String type;
}
