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
package org.psygrid.data.model.utils;

import org.psygrid.data.dao.ElementRevisionException;
import org.psygrid.data.model.hibernate.*;

public class ElementRelationshipSerializer {
	
	public static ElementRelationship initialiseCompositeElementrelationship(CompositeEntry compEntry, Entry entry, int i, EntryStatus entryStatus) {
		
		String reconstituteMethod = "addEntry";
		String className = BasicEntry.class.getCanonicalName();
		org.psygrid.data.model.hibernate.CompositeEntryElementRelationship elemRelationship;
		org.psygrid.data.model.hibernate.ElementRelationship.RelationshipType elemRelationshipType = org.psygrid.data.model.hibernate.ElementRelationship.RelationshipType.delPrimaryRelationship;
		
		LSID relatedElementLSID = entry.getLSID();
		if(relatedElementLSID == null){ 
			
			elemRelationship = new org.psygrid.data.model.hibernate.CompositeEntryElementRelationship(new DataElementContainer(entry), className, reconstituteMethod, entryStatus);
		}else{
			elemRelationship = new org.psygrid.data.model.hibernate.CompositeEntryElementRelationship(entry.getLSID().toString(), className, reconstituteMethod, entryStatus);
		}
		
		elemRelationship.setRelationshipType(elemRelationshipType);
		return elemRelationship; 
	}
	
	static public org.psygrid.data.model.hibernate.OptionDependentElementRelationship initialiseOptionDependentElementRelationship(org.psygrid.data.model.hibernate.OptionEntry opEntry, org.psygrid.data.model.hibernate.Entry dependentEntry, EntryStatus status, int optionIndex){
		String reconstituteMethod = "addOptionDependent";
		String className = dependentEntry.getClass().getName();
		org.psygrid.data.model.hibernate.OptionDependentElementRelationship elemRelationship;
		org.psygrid.data.model.hibernate.ElementRelationship.RelationshipType elemRelationshipType = org.psygrid.data.model.hibernate.ElementRelationship.RelationshipType.delSecondaryRelationship;
		
		LSID dependentElementLSID = dependentEntry.getLSID();
		if(dependentElementLSID == null){
			
			elemRelationship = new org.psygrid.data.model.hibernate.OptionDependentElementRelationship(new DataElementContainer(dependentEntry), new DataElementContainer(opEntry), className, reconstituteMethod, status, optionIndex);
		}else{

			elemRelationship = new org.psygrid.data.model.hibernate.OptionDependentElementRelationship(dependentEntry.getLSID().toString(), new DataElementContainer(opEntry), className, reconstituteMethod, status, optionIndex);
		}
		
		elemRelationship.setRelationshipType(elemRelationshipType);
		return elemRelationship; 
	}
	
	static public org.psygrid.data.model.hibernate.DocumentEntryElementRelationship initialiseDocumentEntryElementRelationship(org.psygrid.data.model.hibernate.Document document, org.psygrid.data.model.hibernate.Entry entry, int index) {
		String reconstituteMethod = "addEntry";
		String className = entry.getClass().getName();
		org.psygrid.data.model.hibernate.DocumentEntryElementRelationship elemRelationship;
		org.psygrid.data.model.hibernate.ElementRelationship.RelationshipType elemRelationshipType = org.psygrid.data.model.hibernate.ElementRelationship.RelationshipType.delPrimaryRelationship;
		
		int sectionIndex = document.getSections().indexOf(entry.getSection());
		
		//If the related element's lsid is null, then this is an import job. Set the
		//object reference instead.
		LSID relatedElementLSID = entry.getLSID();
		if(relatedElementLSID == null){
			
			elemRelationship = new org.psygrid.data.model.hibernate.DocumentEntryElementRelationship(new DataElementContainer(entry), className, reconstituteMethod, index, sectionIndex, entry.getEntryStatus(), entry.getLabel());
		}else{
			elemRelationship = new org.psygrid.data.model.hibernate.DocumentEntryElementRelationship(entry.getLSID().toString(), className, reconstituteMethod, index, sectionIndex, entry.getEntryStatus(), entry.getLabel());
		}
		
		elemRelationship.setRelationshipType(elemRelationshipType);
		
		return elemRelationship; 
	}
	
	static public org.psygrid.data.model.hibernate.DerivedEntryElementRelationship initialiseExternalDerivedEntryElementRelationship(org.psygrid.data.model.hibernate.ExternalDerivedEntry derivedEntry, org.psygrid.data.model.hibernate.Entry formulaEntry, String variableName) {
		String reconstituteMethod = "n/a"; //Will not be used for a derived entry since its repopulation is more complex than calling a particular method with particular argument.
		org.psygrid.data.model.hibernate.DerivedEntryElementRelationship elemRelationship;
		String className = derivedEntry.getClass().getName();
		LSID relatedElementLSID = formulaEntry.getLSID();
		org.psygrid.data.model.hibernate.ElementRelationship.RelationshipType elemRelationshipType = org.psygrid.data.model.hibernate.ElementRelationship.RelationshipType.delSecondaryRelationship;
		if(relatedElementLSID == null){
			elemRelationship = new org.psygrid.data.model.hibernate.DerivedEntryElementRelationship(new DataElementContainer(formulaEntry), className, reconstituteMethod, variableName);
		}else{
			elemRelationship = new org.psygrid.data.model.hibernate.DerivedEntryElementRelationship(formulaEntry.getLSID().toString(), className, reconstituteMethod, variableName);
		}
		
		elemRelationship.setRelationshipType(elemRelationshipType);
		
		return elemRelationship; 
	}
	
	
	static public org.psygrid.data.model.hibernate.DerivedEntryElementRelationship initialiseDerivedEntryElementRelationship(org.psygrid.data.model.hibernate.DerivedEntry derivedEntry, org.psygrid.data.model.hibernate.Entry formulaEntry, String variableName) {
		String reconstituteMethod = "n/a"; //Will not be used for a derived entry since its repopulation is more complex than calling a particular method with particular argument.
		org.psygrid.data.model.hibernate.DerivedEntryElementRelationship elemRelationship;
		String className = derivedEntry.getClass().getName();
		LSID relatedElementLSID = formulaEntry.getLSID();
		org.psygrid.data.model.hibernate.ElementRelationship.RelationshipType elemRelationshipType = org.psygrid.data.model.hibernate.ElementRelationship.RelationshipType.delSecondaryRelationship;
		if(relatedElementLSID == null){
				
			elemRelationship = new org.psygrid.data.model.hibernate.DerivedEntryElementRelationship(new DataElementContainer(formulaEntry), className, reconstituteMethod, variableName);
			//elemRelationship.setRelatedElement(new DataElementContainer(formulaEntry));
		}else{
			elemRelationship = new org.psygrid.data.model.hibernate.DerivedEntryElementRelationship(formulaEntry.getLSID().toString(), className, reconstituteMethod, variableName);
		}
		
		elemRelationship.setRelationshipType(elemRelationshipType);
		
		return elemRelationship; 
	}
	
	/**
	 * This method is sensitive to the extant repository relationships between different the baseElement and relatedElement arguments.
	 * It creates an ElementRelationship object, which stores the information required to 'reconstitute the native relationship.
	 * It is assumed that the baseElement argument has already been assigned an LSID.
	 * 
	 * In the case of an import, it is possible that the relatedElement has not yet been given an lsid. In this case, the
	 * ElementRelationship is populated with direct reference to the relatedElement object, as a place-holder.
	 * 
	 * @param baseElement
	 * @param relatedElement
	 * @param index
	 * @return
	 * @throws ElementRevisionException 
	 */
	static public org.psygrid.data.model.hibernate.ElementRelationship initialiseBasicElementRelationship(org.psygrid.data.model.hibernate.DataElementContainer baseElement, org.psygrid.data.model.hibernate.DataElementContainer relatedElement, Integer index) {
		
		String className = relatedElement.getElementClass().getName();
		String reconstituteMethod = null;
		org.psygrid.data.model.hibernate.ElementRelationship elemRelationship;
		org.psygrid.data.model.hibernate.ElementRelationship.RelationshipType elemRelationshipType = null;
		
		boolean relationshipUnrecognised = true;
		
		if((baseElement.getElement() instanceof org.psygrid.data.model.hibernate.Element) && (relatedElement.getElement() instanceof org.psygrid.data.model.hibernate.DataSet)){
				//Set up the Element-DataSet relationship
				reconstituteMethod = "setMyDataSet";
				relationshipUnrecognised = false;
				elemRelationshipType = org.psygrid.data.model.hibernate.ElementRelationship.RelationshipType.repositoryRelationship;
		}
		
		if((baseElement.getElement() instanceof org.psygrid.data.model.hibernate.DataSet) && (relatedElement.getElement() instanceof org.psygrid.data.model.hibernate.Document)){
				//Set up the DataSet-Document element relationship
				reconstituteMethod = "addDocument";
				relationshipUnrecognised = false;
				elemRelationshipType = org.psygrid.data.model.hibernate.ElementRelationship.RelationshipType.delPrimaryRelationship;
				
		}
		
		if((baseElement.getElement() instanceof org.psygrid.data.model.hibernate.BasicEntry) && (relatedElement.getElement() instanceof org.psygrid.data.model.hibernate.ValidationRule)) {
			reconstituteMethod = "addValidationRule";
			relationshipUnrecognised = false;
			className = org.psygrid.data.model.hibernate.ValidationRule.class.getCanonicalName();
			elemRelationshipType = org.psygrid.data.model.hibernate.ElementRelationship.RelationshipType.delPrimaryRelationship;
		}
		

		if(baseElement.getElement() instanceof org.psygrid.data.model.hibernate.DerivedEntry) {
			if(relatedElement.getElement() instanceof org.psygrid.data.model.hibernate.CompositeEntry){
				reconstituteMethod = "setComposite";
				relationshipUnrecognised = false;
				className = CompositeEntry.class.getCanonicalName();
				elemRelationshipType = org.psygrid.data.model.hibernate.ElementRelationship.RelationshipType.delSecondaryRelationship;
			}
			
			if(relatedElement.getElement() instanceof org.psygrid.data.model.hibernate.MultipleVariableTest){
				reconstituteMethod = "setTest";
				relationshipUnrecognised = false;
				elemRelationshipType = org.psygrid.data.model.hibernate.ElementRelationship.RelationshipType.delPrimaryRelationship;
			}
		}
		
		if(baseElement.getElement() instanceof org.psygrid.data.model.hibernate.ExternalDerivedEntry && relatedElement.getElement() instanceof org.psygrid.data.model.hibernate.MultipleVariableTest){
			reconstituteMethod = "setTest";
			relationshipUnrecognised = false;
			elemRelationshipType = org.psygrid.data.model.hibernate.ElementRelationship.RelationshipType.delPrimaryRelationship;
		}
		
		if(baseElement.getElement() instanceof org.psygrid.data.model.hibernate.ValidationRule && relatedElement.getElement() instanceof org.psygrid.data.model.hibernate.SingleVariableTest){
			reconstituteMethod = "setTest";
			relationshipUnrecognised = false;
			elemRelationshipType = org.psygrid.data.model.hibernate.ElementRelationship.RelationshipType.delPrimaryRelationship;
		}
		
		
		if(relationshipUnrecognised){
			throw new IllegalArgumentException("Relationship unrecognised");
		}
		
		//If the related element's lsid is null, then this is an import job. Set the
		//object reference instead.
		LSID relatedElementLSID = relatedElement.getElementLSIDObject();
		if(relatedElementLSID == null || relatedElementLSID.getIsTemplate()){
			
			if(index == null)
				elemRelationship = new org.psygrid.data.model.hibernate.ElementRelationship(relatedElement, className, reconstituteMethod);
			else
				elemRelationship = new org.psygrid.data.model.hibernate.ElementRelationship(relatedElement, className, reconstituteMethod, index);
		}else{
			String lsidString = relatedElement.getElementLSID();
			if(index == null){
				elemRelationship = new org.psygrid.data.model.hibernate.ElementRelationship(lsidString, className, reconstituteMethod);
			}else{
				elemRelationship = new org.psygrid.data.model.hibernate.ElementRelationship(lsidString, className, reconstituteMethod, index);
			}
		}
		
		elemRelationship.setRelationshipType(elemRelationshipType);
		
		return elemRelationship; 
	}

}
