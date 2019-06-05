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

public class OptionDependentElementRelationship extends ElementRelationshipDTO {
	
	int optionIndex;
	String status;
	String optionElementLSID;
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getOptionIndex() {
		return optionIndex;
	}
	
	public void setOptionIndex(int optionIndex) {
		this.optionIndex = optionIndex;
	}
	
	public String getOptionElementLSID() {
		return optionElementLSID;
	}

	public void setOptionElementLSID(String optionElementLSID) {
		this.optionElementLSID = optionElementLSID;
	}
	
	@Override
	public void toHibernate(org.psygrid.data.model.hibernate.ElementRelationship elemRelationship, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs) {
		super.toHibernate(elemRelationship, hRefs);
	}
	@Override
	public org.psygrid.data.model.hibernate.Persistent toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs) {
		org.psygrid.data.model.hibernate.OptionDependentElementRelationship elemRelationship = null;
		if(hRefs.containsKey(this)){
			elemRelationship = (org.psygrid.data.model.hibernate.OptionDependentElementRelationship)hRefs.get(this);
			return elemRelationship;
		}
		
		EntryStatus entryStatus = EntryStatus.valueOf(getStatus());
		elemRelationship = new org.psygrid.data.model.hibernate.OptionDependentElementRelationship(getElementLSID(), getOptionElementLSID(), getElementClass(), getRepopulateMethod(), entryStatus, getOptionIndex());
		hRefs.put(this, elemRelationship);
		toHibernate(elemRelationship, hRefs);	
		
		return elemRelationship;
	}



}
