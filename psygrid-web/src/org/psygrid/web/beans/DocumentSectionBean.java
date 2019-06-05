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


package org.psygrid.web.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * Bean to represent an entry in a dataset.
 * 
 * @author Rob Harper
 *
 */
public class DocumentSectionBean {

	/**
	 * The database generated unique id of the entry
	 */
	private long id;
	
	/**
	 * The display text of the entry
	 */
	private String displayText;
	
	private List<EntryBean> entries = new ArrayList<EntryBean>();
	
	public DocumentSectionBean(){}
	
	public DocumentSectionBean(long id, String displayText){
		this.id = id;
		this.displayText = displayText;
	}
	
	public String getDisplayText() {
		return displayText;
	}

	public void setDisplayText(String displayText) {
		this.displayText = displayText;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	

	public List<EntryBean> getEntries() {
		return entries;
	}

	public void setEntries(List<EntryBean> entries) {
		this.entries = entries;
	}
	
	public void addEntry(EntryBean entry){
		this.entries.add(entry);
	}
	
	public void addEntry(long id, String displayText){
		this.entries.add(new EntryBean(id, displayText));
	}
}
