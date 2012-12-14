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
package org.alfresco.extensions.bulkexport.controler;

import org.alfresco.extensions.bulkexport.controler.Engine;

/**
 * This classe is a implementation of system engine
 * 
 * @author Denys G. Santos (gsdenys@gmail.com)
 * @version 1.0.1
 * @since 4.x
 */
public class EngineImpl implements Engine {

	private String executionMode = Engine.EXECUTION_SIMPLE_MODE;

	private String actionAfterExecution = Engine.ACTION_DO_NOTHING;

	private String nodeRef = "";

	private String exportRootPath = "";

	/**
	 * Default Builder
	 * 
	 * @param nodeRef
	 *            - node to represent the export Root Node
	 * @param exportRootPath
	 *            - Export temp area path
	 */
	public EngineImpl(String nodeRef, String exportRootPath) {
		this.nodeRef = nodeRef;
		this.exportRootPath = exportRootPath;
	}

	/**
	 * @see org.alfresco.extensions.bulkexport.controler.Engine#run()
	 */
	public void run() {
		if (this.executionMode.equals(Engine.EXECUTION_SIMPLE_MODE)) {
			this.runAsSimpleProgram();
		} else {
			this.runAsThreadControler();
		}
	}

	/**
	 * @see org.alfresco.extensions.bulkexport.controler.Engine#runAsThreadControler()
	 */
	public void runAsThreadControler() {
		// TODO: Implementar este método
	}

	/**
	 * @see org.alfresco.extensions.bulkexport.controler.Engine#runAsSimpleProgram()
	 */
	public void runAsSimpleProgram() {
		
	}

	/**
	 * @return the executionMode
	 */
	public String getExecutionMode() {
		return executionMode;
	}

	/**
	 * @param executionMode - the executionMode to set
	 */
	public void setExecutionMode(String executionMode) {
		this.executionMode = executionMode;
	}

	/**
	 * @return the actionAfterExecution
	 */
	public String getActionAfterExecution() {
		return actionAfterExecution;
	}

	/**
	 * @param actionAfterExecution - the actionAfterExecution to set
	 */
	public void setActionAfterExecution(String actionAfterExecution) {
		this.actionAfterExecution = actionAfterExecution;
	}

	/**
	 * @return the nodeRef
	 */
	public String getNodeRef() {
		return nodeRef;
	}

	/**
	 * @param nodeRef - the nodeRef to set
	 */
	public void setNodeRef(String nodeRef) {
		this.nodeRef = nodeRef;
	}

	/**
	 * @return the exportRootPath
	 */
	public String getExportRootPath() {
		return exportRootPath;
	}

	/**
	 * @param exportRootPath
	 *            the exportRootPath to set
	 */
	public void setExportRootPath(String exportRootPath) {
		this.exportRootPath = exportRootPath;
	}

}
