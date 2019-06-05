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

import org.psygrid.data.model.dto.PersistentDTO;
//import org.psygrid.data.model.hibernate.ElementRelationship.ElementExtensionInfo;
import org.psygrid.data.repository.dao.RelationshipReconstitutionException;

/**
 * 
 * @hibernate.joined-subclass table="t_derived_entry_element_relationship"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class DerivedEntryElementRelationship extends ElementRelationship {
	
	private String inputVariableName;
	
	public DerivedEntryElementRelationship(){
		
	}
	
	public DerivedEntryElementRelationship(String lsid, String elementClass, String repopulateMethod, String variableName) throws IllegalArgumentException{
		super(lsid, elementClass, repopulateMethod);

		boolean lsidValid = true;
		boolean variableNameValid = true;
		
		if(lsid != null){
			try{
				LSID id = LSID.valueOf(lsid);
			}catch(LSIDException ex){
				lsidValid = false;
			}	
		}else{
			lsidValid = true;
		}

		
		if(variableName == null || variableName.length() == 0){
			variableNameValid = false;
		}
		
		if(!variableNameValid || ! lsidValid){
			throw new IllegalArgumentException("Initial Argument(s) invalid for DerivedEntryElementRelationship");
		}
		
		this.inputVariableName = variableName;
	}
	
	public DerivedEntryElementRelationship(DataElementContainer relatedElement, String elementClass, String repopulateMethod, String variableName) throws IllegalArgumentException{
		super(relatedElement, elementClass, repopulateMethod);

		boolean variableNameValid = true;
		
		if(variableName == null || variableName.length() == 0){
			variableNameValid = false;
		}
		
		if(!variableNameValid){
			throw new IllegalArgumentException("Initial Argument(s) invalid for DerivedEntryElementRelationship");
		}
		this.inputVariableName = variableName;
	}


	/**
	 * 
	 * @return - the string-lsid of the input element element.
	 * 
	 * @hibernate.property column="c_input_element_var_name"
	 */
	public String getInputVariableName() {
		return inputVariableName;
	}

	protected void setInputVariableName(String inputVariableName) {
		this.inputVariableName = inputVariableName;
	}

	@Override
	public void reconstituteNativeRelationship(DataElementContainer baseElement, DataElementContainer relatedElement) throws RelationshipReconstitutionException {
		if(baseElement.getElement() instanceof org.psygrid.data.model.hibernate.DerivedEntry && 
				relatedElement.getElement() instanceof BasicEntry){
			
			org.psygrid.data.model.hibernate.DerivedEntry dE = (org.psygrid.data.model.hibernate.DerivedEntry)baseElement.getElement();
			BasicEntry bE = (BasicEntry)relatedElement.getElement();
			dE.addVariable(getInputVariableName(), bE);
			
		}else if (baseElement.getElement() instanceof org.psygrid.data.model.hibernate.ExternalDerivedEntry &&
				relatedElement.getElement() instanceof BasicEntry){
			
			org.psygrid.data.model.hibernate.ExternalDerivedEntry dE = (org.psygrid.data.model.hibernate.ExternalDerivedEntry)baseElement.getElement();
			BasicEntry bE = (BasicEntry)relatedElement.getElement();
			dE.addVariable(getInputVariableName(), bE);
			
		}else{
			throw new RelationshipReconstitutionException("Could not reconstitute derived entry relationship", baseElement, relatedElement, this);	
		}
	}

	@Override
	public PersistentDTO toDTO(Map<org.psygrid.data.model.hibernate.Persistent, PersistentDTO> dtoRefs, RetrieveDepth depth) {
		org.psygrid.data.model.dto.DerivedEntryElementRelationship elemRelationship = null;
		//check to see if we're already in the map.
	       if ( dtoRefs.containsKey(this)){
	           elemRelationship = (org.psygrid.data.model.dto.DerivedEntryElementRelationship)dtoRefs.get(this);
	       }
	       if( null == elemRelationship){
	    	   elemRelationship = new org.psygrid.data.model.dto.DerivedEntryElementRelationship();
	    	   dtoRefs.put(this, elemRelationship);
	    	   toDTO(elemRelationship, dtoRefs, depth);
	       }
			 
			return elemRelationship;
	}

	@Override
	public void toDTO(org.psygrid.data.model.dto.ElementRelationshipDTO elementRelationship, Map<org.psygrid.data.model.hibernate.Persistent, PersistentDTO> dtoRefs, RetrieveDepth depth) {
		super.toDTO(elementRelationship, dtoRefs, depth);
		org.psygrid.data.model.dto.DerivedEntryElementRelationship derivedEntryElementRelationship = (org.psygrid.data.model.dto.DerivedEntryElementRelationship)elementRelationship;
		derivedEntryElementRelationship.setInputVariableName(getInputVariableName());
	}
	
}
