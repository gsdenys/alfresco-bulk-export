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
package org.alfresco.extensions.bulkexport.engine;


/**
 * This classe is a engine of systems
 * 
 * @author Denys G. Santos (gsdenys@gmail.com)
 * @version 1.0.1
 */
public interface Engine {

	public static final String EXECUTION_THREAD_MODE = "Execution-in-thread-mode";
	public static final String EXECUTION_SIMPLE_MODE = "Execution-in-simple-mode";
	
	public static final String ACTION_DOWNLOAD = "action-download-file";
	public static final String ACTION_STORE_IN_ALFRESCO = "action-store-in-alfresco";
	public static final String ACTION_DO_NOTHING = "action-do-nothing";
	
	
	
	
	/**
	 * Method to start engine execution
	 */
	public void run();
	
	/**
	 * Method to environment configure and start exportation in thread mode.   
	 */
	public void runAsThreadControler();
	
	/**
	 * Method to environment configure and start exportation in single mode.
	 */
	public void runAsSimpleProgram();
	
}
