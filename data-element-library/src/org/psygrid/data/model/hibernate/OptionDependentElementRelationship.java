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
 * @hibernate.joined-subclass table="t_option_dependent_element_relationship"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class OptionDependentElementRelationship extends ElementRelationship {

	int optionIndex;
	EntryStatus status;
	String optionElementLSID;
	DataElementContainer optionEntry;
	
	public OptionDependentElementRelationship(){
		
	}
	
	//At the moment the assumption is that the dependent entry and the option entry will always be in the same state
	//concerning having or not having an lsid. However, this may not be the case.
	
	public OptionDependentElementRelationship(String lsidOfControlledElement, String lsidOfOptionEntry, String elementClass, String repopulateMethod, EntryStatus status, int optionIndex) throws IllegalArgumentException {
		super(lsidOfControlledElement, elementClass, repopulateMethod);
		this.optionIndex = optionIndex;
		this.status = status;
		this.optionElementLSID = lsidOfOptionEntry;
	}
	
	
	public OptionDependentElementRelationship(String lsidOfControlledElement, DataElementContainer optionEntry, String elementClass, String repopulateMethod, EntryStatus status, int optionIndex) throws IllegalArgumentException {
		super(lsidOfControlledElement, elementClass, repopulateMethod);
		this.optionIndex = optionIndex;
		this.status = status;
		this.optionElementLSID = null;
		this.optionEntry = optionEntry;
	}
	
	
	public OptionDependentElementRelationship(DataElementContainer controlledElement, DataElementContainer optionEntry, String elementClass, String repopulateMethod, EntryStatus status, int optionIndex) throws IllegalArgumentException {
		super(controlledElement, elementClass, repopulateMethod);
		this.optionIndex = optionIndex;
		this.status = status;
		this.optionElementLSID = null;
		this.optionEntry = optionEntry;
	}
	


	@Override
	public void toDTO(org.psygrid.data.model.dto.ElementRelationshipDTO elementRelationship, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth) {
		super.toDTO(elementRelationship, dtoRefs, depth);
		org.psygrid.data.model.dto.OptionDependentElementRelationship odRel = (org.psygrid.data.model.dto.OptionDependentElementRelationship)elementRelationship;
		odRel.setOptionIndex(getOptionIndex());
		odRel.setStatus(getStatusEnum());
		odRel.setOptionElementLSID(optionElementLSID);
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

	/**
	 * @hibernate.property column="c_option_index"
	 */
	public int getOptionIndex() {
		return optionIndex;
	}

	protected void setOptionIndex(int optionIndex) {
		this.optionIndex = optionIndex;
	}
	
	@Override
	public void reconstituteNativeRelationship(DataElementContainer baseElement, DataElementContainer relatedElement) {
		
		//The base element will be the document element.
		//The related element will be the 
		
		Entry entry = (Entry)relatedElement.getElement();
		Option option = ((org.psygrid.data.model.hibernate.OptionEntry)baseElement.getElement()).getOption(this.optionIndex);
		//We need to create an option dependency.
		org.psygrid.data.model.hibernate.HibernateFactory factory = new org.psygrid.data.model.hibernate.HibernateFactory();
		OptionDependent opDep = factory.createOptionDependent();
		opDep.setDependentEntry(entry);
		opDep.setEntryStatus(this.status); 
		option.addOptionDependent(opDep);
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

	/**
	 * @hibernate.property column="c_option_lsid"
	 */
	public String getOptionElementLSID() {
		
		if(optionEntry != null){
			return optionEntry.getElementLSIDObject().toString();
		}else{
			return optionElementLSID;
		}
	}

	public DataElementContainer getOptionEntry() {
		return optionEntry;
	}

	public void setOptionElementLSID(String optionElementLSID) {
		this.optionElementLSID = optionElementLSID;
	}

}
