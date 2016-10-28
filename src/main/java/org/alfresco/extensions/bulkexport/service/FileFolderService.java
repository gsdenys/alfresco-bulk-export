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

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;

/**
 * The java service to execute the basic command at the service filesystem.
 *
 * @author Denys G. Santos
 * @since 2.0.0
 * @version 2.0.0
 */
interface FileFolderService {

    /**
     * Create a new directory at filesystem
     *
     * @param exportPath
     *        the {@link Path} to the export directory
     * @param folderPathDir
     *        the folder path to be created
     * @return File
     */
    File createFolder(final Path exportPath, final String folderPathDir);

    /**
     * Create a new File at the specified export and path.
     *
     * @param fullPath
     *        the full path to the file to be created
     * @return File
     */
    File createDocument(final Path fullPath, final InputStream is);

    /**
     * Create a new export folder based in the user and the node base. the name folder
     *
     *
     * @param userName
     *        the user that call the service
     * @param nodeBase
     *        the node root that will be exported
     * @return File
     */
    File createExportFolder(final String userName, final NodeRef nodeBase);

}
