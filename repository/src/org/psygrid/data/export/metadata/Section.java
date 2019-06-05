/*
Copyright (c) 2006-2008, The University of Manchester, UK.

This file is part of PsyGrid.

PsyGrid is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as 
published by the Free Software Foundation, either version 3 of 
the License, or (at your option) any later version.

PsyGrid is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public 
License along with PsyGrid.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.psygrid.data.export.metadata;

import java.util.ArrayList;
import java.util.List;

/**
 * Metadata about a section of a document in an export.
 * 
 * @author Rob Harper
 *
 */
public class Section {

	/**
	 * The database id of the section (more precisely the 
	 * section occurrence)
	 */
	private final Long id;
	
	/**
	 * The name of the section.
	 */
	private String name;
	
	/**
	 * The maximum number of instances of the section for the
	 * records contained in the export.
	 */
	private int instanceCount;
	
	/**
	 * The list of entries in the section.
	 */
	List<Entry> entries = new ArrayList<Entry>();

	public Section(Long id){
		this.id = id;
	}
	
	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getInstanceCount() {
		return instanceCount;
	}

	public void setInstanceCount(int instanceCount) {
		//only set the count if the new value is greater than
		//the current one
		if ( instanceCount > this.instanceCount){
			this.instanceCount = instanceCount;
		}
	}

	public List<Entry> getEntries() {
		return entries;
	}

	public void setEntries(List<Entry> entries) {
		this.entries = entries;
	}
	
	/**
	 * Return the number of columns that will be used
	 * when converting the exported data for this section
	 * (all instances) to a tabular format.
	 * 
	 * @return The number of columns.
	 */
	public int columnCount(boolean showCode, boolean showValue){
		int count = 0;
		for ( int i=0; i<instanceCount; i++ ){
			for ( Entry e: entries ){
				count += e.columnCount(i, showCode, showValue);
			}
		}
		return count;
	}
	
	/**
	 * Return the number of columns that will be used
	 * when converting the exported data for a specified
	 * instances of this section to a tabular format.
	 * 
	 * @param instanceIndex The index of the instance
	 * @return The number of columns.
	 */
	public int columnCount(int instanceIndex, boolean showCode, boolean showValue){
		int count = 0;
		for ( Entry e: entries ){
			count += e.columnCount(instanceIndex, showCode, showValue);
		}
		return count;
	}
	
	/**
	 * Locate an entry in the list of entries by its database id.
	 * 
	 * @param id The database id.
	 * @return The entry or null if no entry is found with the 
	 * given id
	 */
	public Entry findEntryById(Long id){
		for ( Entry e: entries ){
			if ( e.getId().equals(id) ){
				return e;
			}
		}
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		if ( null != obj && obj instanceof Section ){
			return ((Section)obj).getId().equals(this.id);
		}
		return false;
	}
	
	
}
