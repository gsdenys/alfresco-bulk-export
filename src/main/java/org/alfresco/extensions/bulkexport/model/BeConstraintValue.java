package org.alfresco.extensions.bulkexport.model;

/**
 * Created by ENGDB on 28/10/2016.
 */
public enum BeConstraintValue {
    PROJ_CREATED("CREATED"),
    PROJ_PREPARING("PREPARING"),
    PROJ_PREPARED("PREPARED"),
    PROJ_EXPORTING("EXPORTING"),
    PROJ_EXPORTED("EXPORTED"),
    PROJ_COMPRESSING("COMPRESSING"),
    PROJ_COMPRESSED("COMPRESSED"),

    CONFIG_CREATED("CREATED"),
    CONFIG_PREPARING("PREPARING"),
    CONFIG_PREPARED("PREPARED"),
    CONFIG_EXPORTING("EXPORTING"),
    CONFIG_EXPORTED("EXPORTED"),

    CONFIG_TYPE_FOLDER("FOLDER"),
    CONFIG_TYPE_CONTENT("CONTENT");


    BeConstraintValue(String value) {
        this.value = value;
    }

    private String value;
}
