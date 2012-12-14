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
package org.alfresco.extensions.bulkexport.manager;


/**
 * <b><i>Component Manager</i></b>
 * 
 * This class is a <i>interface</i> to the Component Manager.
 * 
 * @author Denys Santos (gsdenys@gmail.com)
 * @version 1.0.0
 * @since 4.x
 */
public interface Manager {

	/**
	 * <b>Initiate software execution</b> 
	 * 
	 * This method is a core of class. It has responsibility to create 
	 * the set of Data Structure used as execution control, create the 
	 * initial system folder that will be used to store the exported 
	 * files, instantiate and run the correct engine, compress the file
	 * exported and execute the  selected action at end of export process
	 */
	public void run();
	
	
	/**
	 * <b>Configure execution parameters</b>
	 * 
	 * This method has a responsibility to read the configuration file and
	 * put the entry parameters in this class.
	 */
	public void configure();

	
	/**
	 * <b>Create the Data Structure</b>
	 * 
	 * This method has a responsibility to create the data structure that
	 * will be used during the export process. 
	 */
	public void createDataStructure();
	
	
	/**
	 * <b>Create Zip Package</b>
	 * 
	 * This method has a responsibility to create a zip package with all exported
	 * data. The local where this file will be created is a responsibility of 
	 * implementation, but we really suggest the creation inside the export root 
	 * folder structure and after use the method {@link Manager#executeAction()} 
	 * to do the action to move or store or share this content in your bast location.
	 * 
	 * @param fileName  The name of file that will be created
	 */
	public void createZipPackage(String fileName);

	
	/**
	 * <b>Execute Action</b>
	 * 
	 * This method has a responsibility call the right method to execute the selected
	 * action. We suggest to use reflection means to call a right method that can passed
	 * by parameter.   
	 * 
	 * @param actionString  {@link String} that represents a engine method to be called 
	 */
	public void executeAction(String actionString);	
	
}
