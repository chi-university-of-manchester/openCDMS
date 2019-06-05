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

import java.io.Serializable;

/**
 * Class to hold a result from {@link RepositoryDAO#getProvenanceForChange}
 * 
 * @author Rob Harper
 *
 */
public class ProvenanceForChangeResult implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String entry;
	
	private String prevValue;
	
	private String currentValue;

	private String comment;
	
	public ProvenanceForChangeResult() {
		super();
	}

	public ProvenanceForChangeResult(String entry, String prevValue,
			String currentValue) {
		super();
		this.entry = entry;
		this.prevValue = prevValue;
		this.currentValue = currentValue;
	}

	public String getEntry() {
		return entry;
	}

	public void setEntry(String entry) {
		this.entry = entry;
	}

	public String getPrevValue() {
		return prevValue;
	}

	public void setPrevValue(String prevValue) {
		this.prevValue = prevValue;
	}

	public String getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(String currentValue) {
		this.currentValue = currentValue;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

}
