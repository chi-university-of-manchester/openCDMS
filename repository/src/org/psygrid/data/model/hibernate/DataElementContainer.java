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

import java.util.List;

/**
 * This class has been created to provide a unified accessor to data elements that do have a diverged
 * inheritance structure. For example, validation rules are not 'elements', but do not derive
 * from the 'elment' class.
 * 
 * The class encapsulates the underlying object, and allows retrieval of common information
 * about the underlying object.
 * 
 * @author williamvance
 *
 */
public class DataElementContainer {

    public enum ElementType{
        primary,
        rule
    }

	private Element primaryElement; //A primary element (anything deriving from element)
	private ValidationRule ruleElement; //A validation rule element
	private ElementType elementType; //What type is being stored

	/**
	 * Default constructor
	 *
	 */
	public DataElementContainer(){}

	/**
	 * Constructor for creating a container with a 'primary' data element.
	 * @param dataElement
	 */
	public DataElementContainer(Element dataElement){
		ruleElement = null;
		primaryElement = dataElement;
		elementType = ElementType.primary;
	}

	/**
	 * Constructor for creating a container with a 'validation rule' data element.
	 * @param rule
	 */
	public DataElementContainer(ValidationRule rule){
		ruleElement = rule;
		primaryElement = null;
		elementType = ElementType.rule;
	}

	/**
	 * Returns the class of the underlying element.
	 */
	public Class getElementClass() {

		Class elemClass = null;

		switch(elementType){
		case primary:
			elemClass = primaryElement.getClass();
			break;
		case rule:
			elemClass = ruleElement.getClass();
			break;
		default:
			break;
		}

		return elemClass;
	}

	/**
	 * Returns the element type of the underlying element.
	 * @return
	 */
	public ElementType getElementType() {
		return elementType;
	}

	/**
	 * Provides access to the underlying element as a Persistent.
	 * Will require casting for full use.
	 * @return
	 */
	public Persistent getElement() {
		Persistent element = null;
		switch(elementType){
		case primary:
			element = primaryElement;
			break;
		case rule:
			element = ruleElement;
			break;
		default:
			break;
		}

		return element;
	}

	public org.psygrid.data.model.dto.DataElementContainerDTO toDTO(){

		//TODO:DEL 
		org.psygrid.data.model.dto.DataElementContainerDTO dtoContainer = null;
		switch(elementType){
		case primary:
			dtoContainer = new org.psygrid.data.model.dto.DataElementContainerDTO(primaryElement.toDTO());
			break;
		case rule:
			dtoContainer = new org.psygrid.data.model.dto.DataElementContainerDTO(ruleElement.toDTO());
			break;
		}

		return dtoContainer;

	}

	/**
	 * Returns a string version of the underlying element's LSID.
	 * @return
	 */
	public String getElementLSID() {
		String lsid = null;
		switch(elementType){
		case primary:
		{
			lsid = primaryElement.getLSID().toString();
		}
		break;
		case rule:
		{
			lsid = ruleElement.getLSID().toString();
		}
		break;
		}

		return lsid;
	}

	/**
	 * Returns the underlying element's description.
	 * @return
	 */
	public String getElementDescription() {

		String description = null;

		switch(elementType){
		case primary:
			description = primaryElement.getDescription();
			break;	
		case rule:
			description = ruleElement.getDescription();
			break;
		}

		return description;
	}

	/**
	 * Returns whether the underlying element is currently editable.
	 * 
	 * @return isEditable
	 */
	public boolean getIsEditable(){
		boolean isEditable = false;

		switch(elementType){
		case primary:
			isEditable = primaryElement.getIsEditable();
			break;	
		case rule:
			isEditable = ruleElement.getIsEditable();
			break;
		}

		return isEditable;
	}

	public void setIsEditable(boolean isEditable){

		switch(elementType){
		case primary:
			primaryElement.setIsEditable(isEditable);
			break;	
		case rule:
			ruleElement.setIsEditable(isEditable);
			break;
		}
	}

	/**
	 * return's the underlying element's name.
	 * @return
	 */
	public String getElementName() {
		String elementName = null;

		switch(elementType){
		case primary:
			elementName = primaryElement.getName();
			break;	
		case rule:
			elementName = ruleElement.getName();
			break;
		}

		return elementName;
	}

	public void setPrepareElementForNewRevision(boolean b) {
		switch(elementType){
		case primary:
			primaryElement.setPrepareElementForNewRevision(b);
			break;	
		case rule:
			ruleElement.setPrepareElementForNewRevision(b);
			break;
		}
	}

	/**
	 * Returns the underlying element's LSID object.
	 * @return
	 */
	public LSID getElementLSIDObject() {
		LSID lsidObj = null;

		switch(elementType){
		case primary:
			lsidObj = primaryElement.getLSID();
			break;	
		case rule:
			lsidObj = ruleElement.getLSID();;
			break;
		}
		return lsidObj;
	}

	public void setLSID(LSID lsid) {
		switch(elementType){
		case primary:
			((Element)this.getElement()).setLSID(lsid);
			break;
		case rule:
			((ValidationRule)this.getElement()).setLSID(lsid);
			break;
		default:
			break;
		}
	}

	public List<ElementRelationship> getElementRelationships() {

		List<ElementRelationship> relationships = null;

		switch(elementType){
		case primary:
			relationships = ((Element)this.getElement()).getElementRelationships();
			break;
		case rule:
			relationships = ((ValidationRule)this.getElement()).getElementRelationships();
			break;
		default:
			break;
		}

		return relationships;
	}

	public LSID getInstanceLSID(){

		LSID instanceLSID = null;

		switch(elementType){
		case primary:
			instanceLSID = ((Element)this.getElement()).getInstanceLSID();
			break;
		case rule:
			instanceLSID = ((ValidationRule)this.getElement()).getInstanceLSID();
			break;
		default:
			break;
		}

		return instanceLSID;
	}

	public void setInstanceLSID(LSID instanceLSID) {
		switch(elementType){
		case primary:
			((Element)this.getElement()).setInstanceLSID(instanceLSID);
			break;
		case rule:
			((ValidationRule)this.getElement()).setInstanceLSID(instanceLSID);
			break;
		default:
			break;
		}

	}

	public Long getId() {
		return this.getElement().getId();
	}

	public List<ElementMetaData> getMetaData() {
		List<ElementMetaData> metaData = null;

		switch(elementType){
		case primary:
			metaData = primaryElement.getMetaData();
			break;
		case rule:
			metaData = ruleElement.getMetaData();
			break;
		}

		return metaData;
	}

	public void addMetaData(ElementMetaData theMetaData) {
		switch(elementType){
		case primary:
			((Element)this.getElement()).addMetaData(theMetaData);
			break;
		case rule:
			((ValidationRule)this.getElement()).addMetaData(theMetaData);
			break;
		default:
			break;
		}

	}

	public ElementMetaData getLatestMetaData() {

		ElementMetaData mD = null;

		switch(elementType){
		case primary:
			mD = ((Element)this.getElement()).getLatestMetaData();
			break;
		case rule:
			mD = ((ValidationRule)this.getElement()).getLatestMetaData();
			break;
		default:
			break;
		}

		return mD;
	}
	
	public void setMetaData(List<ElementMetaData> metaDataList) {
		switch(elementType){
		case primary:
			((Element)this.getElement()).setMetaData(metaDataList);
			break;
		case rule:
			((ValidationRule)this.getElement()).setMetaData(metaDataList);
			break;
		default:
			break;
		}
	}

	public void setSubmissionContext(ElementSubmissionContext context){
		switch(elementType){
		case primary:
			((Element)this.getElement()).setSubmissionContext(context);
			break;
		case rule:
			((ValidationRule)this.getElement()).setEnumSubmissionContext(context.toString());
			break;
		default:
			break;

		}
	}

	public void changeElementStatus(DataElementStatus status){
		switch(elementType){
		case primary:
			((Element)this.getElement()).setEnumStatus(status.toString());
			break;
		case rule:
			((ValidationRule)this.getElement()).setEnumStatus(status.toString());
			break;
		default:
			break;

		}
	}

	/**
	 * Returns whether the underlying element is the head revision (at time of download).
	 * @return
	 */
	public boolean getHeadRevision(){

		boolean theAnswer = false;

		switch(elementType){
		case primary:
			theAnswer = ((Element)this.getElement()).getHeadRevision();
			break;
		case rule:
			theAnswer = ((ValidationRule)this.getElement()).getHeadRevision();
			break;
		default:
			break;
		}

		return theAnswer;
	}

	/**
	 * Returns the underlying element's current status. (e.g. pending, approved)
	 * @return
	 */
	public DataElementStatus getStatus() {

		DataElementStatus status = null;

		switch(elementType){
		case primary:
			status = ((Element)this.getElement()).getStatus();
			break;
		case rule:
			status = ((ValidationRule)this.getElement()).getStatus();
			break;
		default:
			break;
		}

		return status;
	}

	public boolean getIsRevisionCandidate(){

		boolean theAnswer = false;

		switch(elementType){
		case primary:
			theAnswer = ((Element)this.getElement()).getIsRevisionCandidate();
			break;
		case rule:
			theAnswer = ((ValidationRule)this.getElement()).getIsRevisionCandidate();
			break;
		default:
			break;
		}

		return theAnswer;
	}
	
	public void setIsRevisionCandidate(boolean isRevisionCandidate){
		switch(elementType){
		case primary:
			((Element)this.getElement()).setIsRevisionCandidate(isRevisionCandidate);
			break;
		case rule:
			((ValidationRule)this.getElement()).setIsRevisionCandidate(isRevisionCandidate);
			break;
		default:
			break;
		}
	}
}
