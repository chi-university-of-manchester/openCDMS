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
package org.psygrid.data.model.dto;

import java.util.Map;

import org.psygrid.data.model.hibernate.EntryStatus;

public class DocumentEntryElementRelationship extends ElementRelationshipDTO {
	
	private int entrySectionIndex;
	private String entryStatus;
	private String entryLabel;

	public String getEntryStatus() {
		return entryStatus;
	}

	public void setEntryStatus(String entryStatus) {
		this.entryStatus = entryStatus;
	}

	public int getEntrySectionIndex() {
		return entrySectionIndex;
	}

	public void setEntrySectionIndex(int entrySectionIndex) {
		this.entrySectionIndex = entrySectionIndex;
	}

	@Override
	public void toHibernate(org.psygrid.data.model.hibernate.ElementRelationship elemRelationship, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs) {
		super.toHibernate(elemRelationship, hRefs);
	}

	@Override
	public org.psygrid.data.model.hibernate.Persistent toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs) {
		org.psygrid.data.model.hibernate.DocumentEntryElementRelationship elemRelationship = null;
		if(hRefs.containsKey(this)){
			elemRelationship = (org.psygrid.data.model.hibernate.DocumentEntryElementRelationship)hRefs.get(this);
			return elemRelationship;
		}	

		elemRelationship = new org.psygrid.data.model.hibernate.DocumentEntryElementRelationship(getElementLSID(), getElementClass(), getRepopulateMethod(), 5, getEntrySectionIndex(), EntryStatus.valueOf(getEntryStatus()), getEntryLabel());
		hRefs.put(this, elemRelationship);
		toHibernate(elemRelationship, hRefs);	
	
		return elemRelationship;
	}

	public String getEntryLabel() {
		return entryLabel;
	}

	public void setEntryLabel(String entryLabel) {
		this.entryLabel = entryLabel;
	}
}
