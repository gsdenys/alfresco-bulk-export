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
package org.alfresco.extensions.bulkexport.manager.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.extensions.bulkexport.manager.DataManager;
import org.apache.commons.httpclient.NameValuePair;

/**
 * <b><i>Data Manager</i></b>
 * 
 * This class is a Data Manager Implementation and has a responsibility to
 * manager two set of data (<i>In process</i> and <i>To Be Processed</i>)
 * 
 * @author Denys Santos (gsdenys@gmail.com)
 * @version 1.0.0
 * @since 4.x
 */
public class DataManagerImpl implements DataManager {

	/**
	 * {@link List} of {@link NameValuePair} to store entries to be processed
	 */
	private List<NameValuePair> entryToBeProcessed = null;

	/**
	 * {@link Map} to store all entries that are in process
	 */
	private Map<String, String> entryInProcess = null;

	/**
	 * Default Builder
	 */
	public DataManagerImpl() {
		this.entryToBeProcessed = new ArrayList<NameValuePair>();
		this.entryInProcess = new HashMap<String, String>();
	}

	/**
	 * @see org.alfresco.extensions.bulkexport.manager.DataManager#add(org.apache.commons.httpclient.NameValuePair)
	 */
	public void add(NameValuePair data) {
		this.entryToBeProcessed.add(data);
	}

	/**
	 * @see org.alfresco.extensions.bulkexport.manager.DataManager#add(java.lang.String,
	 *      java.lang.String)
	 */
	public void add(String nodeRef, String folderPath) {
		NameValuePair data = new NameValuePair(nodeRef, folderPath);
		this.add(data);
	}

	/**
	 * @see org.alfresco.extensions.bulkexport.manager.DataManager#addAll(java.util.Collection)
	 */
	public void addAll(Collection<NameValuePair> dataSet) {
		this.entryToBeProcessed.addAll(dataSet);
	}

	/**
	 * @see org.alfresco.extensions.bulkexport.manager.DataManager#execute()
	 */
	public NameValuePair execute() {

		if (this.entryToBeProcessed.isEmpty()) {
			return null;
		}

		NameValuePair pair = this.entryToBeProcessed.remove(0);
		this.entryInProcess.put(pair.getName(), pair.getValue());

		return pair;
	}

	/**
	 * @see org.alfresco.extensions.bulkexport.manager.DataManager#remove(java.lang.String)
	 */
	public NameValuePair remove(String nodeRef) {
		String value = this.entryInProcess.remove(nodeRef);
		NameValuePair pair = new NameValuePair(nodeRef, value);

		return pair;
	}

	/**
	 * @see org.alfresco.extensions.bulkexport.manager.DataManager#remove()
	 */
	public Collection<NameValuePair> remove() {
		Set<String> set = this.entryInProcess.keySet();
		Collection<NameValuePair> collection = new ArrayList<NameValuePair>();

		for (String key : set) {
			String value = this.entryInProcess.remove(key);
			collection.add(new NameValuePair(key, value));
		}

		return collection;
	}

	/**
	 * @see org.alfresco.extensions.bulkexport.manager.DataManager#getEntriesToBeProcessed()
	 */
	public Collection<NameValuePair> getEntriesToBeProcessed() {
		return this.entryToBeProcessed;
	}

	/**
	 * @see org.alfresco.extensions.bulkexport.manager.DataManager#getEntriesInProcess()
	 */
	public Collection<NameValuePair> getEntriesInProcess() {

		Collection<NameValuePair> collection = new ArrayList<NameValuePair>();

		for (String nodeRef : this.entryInProcess.keySet()) {
			String path = this.entryInProcess.get(nodeRef);
			collection.add(new NameValuePair(nodeRef, path));
		}

		return collection;
	}

	/**
	 * @see org.alfresco.extensions.bulkexport.manager.DataManager#isEmpty()
	 */
	public boolean isEmpty() {
		if (this.entryInProcess.isEmpty() && this.entryToBeProcessed.isEmpty()) {
			return true;
		}

		return false;
	}

}
