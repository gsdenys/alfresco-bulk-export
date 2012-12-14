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

import java.util.Collection;

import org.apache.commons.httpclient.NameValuePair;


/**
 * <b><i>Data Manager</i></b>
 * 
 * This class is a <i>interface</i> to the Data Manager.
 * 
 * @author Denys Santos (gsdenys@gmail.com)
 * @version 1.0.0
 * @since 4.x
 */
public interface DataManager {

	/*######################################################################################################################
	  ####                                       Block of #EXECUTE# methods                                             ####  
	  ######################################################################################################################*/
	
	
	/**
	 * <b>Add data to structure</b>
	 * 
	 * This method has a responsibility to add a new entry data to be in future
	 * processed.
	 * 
	 * @param data  A {@link NameValuePair} entry data
	 */
	public void add(NameValuePair data);
	
	
	/**
	 * <b>Add data to structure</b>
	 * 
	 * This method has a responsibility to add a new entry data to be in future
	 * processed.
	 * 
	 * @param nodeRef  A nodeRef string reference
	 * @param folderPath  A folder path string reference
	 */
	public void add(String nodeRef, String folderPath);
	
	
	/**
	 * <b>Add data set to structure</b>
	 * 
	 * This method has a responsibility to add a all entries data to be in future
	 * processed.
	 * 
	 * @param dataSet  A {@link Collection} of {@link NameValuePair} containing the data 
	 */
	public void addAll(Collection<NameValuePair> dataSet);
	

	/*######################################################################################################################
	  ####                                       Block of #EXECUTE# methods                                             ####  
	  ######################################################################################################################*/
	
	
	/**
	 * <b>Execute Data</b>
	 * 
	 * This method remove the data from <i>entries to be executed</i> and put it in the set
	 * of <i>entries in process</i>
	 * 
	 * @return {@link NameValuePair} containing the nodeRef {@link String} and a <i>export folder {@link String} path</i>
	 */
	public NameValuePair execute();
	
	
	/*######################################################################################################################
	  ####                                        Block of #REMOVE# methods                                             ####  
	  ######################################################################################################################*/
	
	
	/**
	 * <b>Remove Data</b>
	 * 
	 * This method has a responsibility to remove a entry in <i>entries in process</i> data set
	 * based in the nodeRef {@link String}. Remember that just entries in process can be removed
	 * directly.
	 * 
	 * @param nodeRef  {@link String} reference to node in alfresco
	 * @return {@link NameValuePair} containing the removed value
	 */
	public NameValuePair remove(String nodeRef);
	
	
	/**
	 * <b>Remove All Data</b>
	 * 
	 * This method has a responsibility to remove all entries in <i>entries in process</i> data 
	 * set. Remember that just entries in process can be removed directly.
	 * 
	 * @return {@link Collection} of {@link NameValuePair} with all data removed
	 */
	public Collection<NameValuePair> remove();
		
	
	/*######################################################################################################################
	  ####                                          Block of #GET# methods                                              ####  
	  ######################################################################################################################*/
	
	
	/**
	 * <b>Get a Set of Data</b>
	 * 
	 * This method has a responsibility to get a set of data in <i>entry to be processed</i>
	 * 
	 * @return {@link Collection} of {@link NameValuePair} containing the nodeRef {@link String} and a <i>export folder {@link String} path</i>
	 */
	public Collection<NameValuePair> getEntriesToBeProcessed();
	
	
	/**
	 * <b>Get a Set of Data</b>
	 * 
	 * This method has a responsibility to get a set of data in <i>entry in process</i>
	 * 
	 * @return {@link Collection} of {@link NameValuePair} containing the nodeRef {@link String} and a <i>export folder {@link String} path</i>
	 */
	public Collection<NameValuePair> getEntriesInProcess();
	
	
	/*######################################################################################################################
	  ####                                        Block of #IS EMPTY# methods                                           ####  
	  ######################################################################################################################*/
	
	
	/**
	 * <b>Is Empty</b>
	 * 
	 * This method return if has any entry in any one data (to be processed or
	 * in process)
	 * 
	 * @return {@link Boolean}
	 */
	public boolean isEmpty();
	
}
