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
package org.psygrid.data.model.hibernate;

import java.util.Map;



/**
 * 
 * @hibernate.joined-subclass table="t_comp_entry_elem_relationship"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class CompositeEntryElementRelationship extends ElementRelationship {
	
	EntryStatus status;
	
	public CompositeEntryElementRelationship(){
	}

	public CompositeEntryElementRelationship(String lsid, String elementClass, String repopulateMethod, EntryStatus status) throws IllegalArgumentException {
		super(lsid, elementClass, repopulateMethod);
		this.status = status;
	}
	
	public CompositeEntryElementRelationship(DataElementContainer relatedElement, String elementClass, String repopulateMethod, EntryStatus status) throws IllegalArgumentException {
		super(relatedElement, elementClass, repopulateMethod);
		this.status = status;
	}
	
	@Override
	public void toDTO(org.psygrid.data.model.dto.ElementRelationshipDTO elementRelationship, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth) {
		super.toDTO(elementRelationship, dtoRefs, depth);
		org.psygrid.data.model.dto.OptionDependentElementRelationship odRel = (org.psygrid.data.model.dto.OptionDependentElementRelationship)elementRelationship;
		odRel.setStatus(getStatusEnum());
	}

	@Override
	public org.psygrid.data.model.dto.PersistentDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth) {
		org.psygrid.data.model.dto.OptionDependentElementRelationship elemRelationship = null;
		//check to see if we're already in the map.
       if ( dtoRefs.containsKey(this)){
           elemRelationship = (org.psygrid.data.model.dto.OptionDependentElementRelationship)dtoRefs.get(this);
       }
       if( null == elemRelationship){
    	   elemRelationship = new org.psygrid.data.model.dto.OptionDependentElementRelationship();
    	   dtoRefs.put(this, elemRelationship);
    	   toDTO(elemRelationship, dtoRefs, depth);
       }
		return elemRelationship;
	}
	
	public EntryStatus getStatus() {
		return status;
	}
	
	/**
	 * @hibernate.property column="c_entry_status"
	 */
	public String getStatusEnum() {
		if(status == null)
			return null;
		else
			return status.toString();
	}

	public void setStatus(EntryStatus status) {
		this.status = status;
	}
	
	public void setStatusEnum(String status) {
		if(status == null)
			this.status = null;
		else
			this.status = EntryStatus.valueOf(status);
	}
	
	@Override
	public void reconstituteNativeRelationship(DataElementContainer baseElement, DataElementContainer relatedElement) {
		CompositeEntry compEntry = (CompositeEntry)baseElement.getElement();
		BasicEntry basicEntry = (BasicEntry) relatedElement.getElement();
		compEntry.addEntry(basicEntry);
		basicEntry.setEntryStatus(this.status);
	}
	

}
