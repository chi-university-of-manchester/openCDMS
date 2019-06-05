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


package org.psygrid.data.model.dto.extra;

/**
 * @author Rob Harper
 *
 */
public class MinimalEntry {

	private long id;
	
	private String displayText;

	/**
	 * The toString representation of the RBACAction, defining 
	 * whether a user can view this entry.
	 */
	private String accessAction;
	
	public MinimalEntry(){}
	
	public MinimalEntry(long id, String displayText, String accessAction){
		this.id = id;
		this.displayText = displayText;
		this.accessAction = accessAction;
	}
	
	public String getDisplayText() {
		return displayText;
	}

	public void setDisplayText(String displayText) {
		this.displayText = displayText;
	}

	public String getAccessAction() {
		return accessAction;
	}

	public void setAccessAction(String accessAction) {
		this.accessAction = accessAction;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
}
