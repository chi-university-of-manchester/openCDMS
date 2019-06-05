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

import org.psygrid.data.model.hibernate.DataElementContainer;

public class DataElementContainerDTO {
	
	
	private ElementDTO primaryElement; //A primary element (anything deriving from element)
	private ValidationRuleDTO ruleElement; //A validation rule element
	private String elementType; //What type is being stored
	
	
	public DataElementContainerDTO(){
		
	}
	
	public DataElementContainerDTO(ElementDTO returnElement) {
		this.primaryElement = returnElement;
		elementType = DataElementContainer.ElementType.primary.toString();
	}
	
	public DataElementContainerDTO(ValidationRuleDTO rule){
		this.ruleElement = rule;
		elementType = DataElementContainer.ElementType.rule.toString();
	}

	public org.psygrid.data.model.hibernate.DataElementContainer toHibernate(){
		
		org.psygrid.data.model.hibernate.DataElementContainer hibernateContainer = null;
		
		switch(DataElementContainer.ElementType.valueOf(elementType)){
		case primary:
			hibernateContainer = new org.psygrid.data.model.hibernate.DataElementContainer(primaryElement.toHibernate());
			break;
		case rule:
			hibernateContainer = new org.psygrid.data.model.hibernate.DataElementContainer(ruleElement.toHibernate());
			break;
		}
		
		return hibernateContainer;
	}
	
	/*
	public String getElementLSID() {
		
		String lsid = null;
		
		switch(IDataElementContainer.ElementType.valueOf(elementType)){
		case primary:
			lsid = primaryElement.getLSID().toString();
			break;
		case rule:
			lsid = ruleElement.getLSID().toString();
			break;
		}
		
		return lsid;
	}
	*/
	
	/*
	public LSID getElementLSIDObject() {
		LSID lsidObj = null;
		
		switch(IDataElementContainer.ElementType.valueOf(elementType)){
		case primary:
			lsidObj = primaryElement.getLSID();
			break;
		case rule:
			lsidObj = ruleElement.getLSID();
			break;
		}
		
		return lsidObj;
	}
	*/

	/*
	public Persistent getElement() {
		Persistent element = null;
		
		switch(IDataElementContainer.ElementType.valueOf(elementType)){
		case primary:
			element = primaryElement;
			break;
		case rule:
			element = ruleElement;
			break;
		}
		
		return element;
	}
	*/

	/*
	public Class getElementClass() {
		return getElement().getClass();
	}
	*/

	/*
	public String getElementDescription() {
		String description = null;
		
		switch(IDataElementContainer.ElementType.valueOf(elementType)){
		case primary:
			description = primaryElement.getDescription();
			break;
		case rule:
			description = ruleElement.getDescription();
			break;
		}
		
		return description;
	}
	*/

	/*
	public String getElementName() {
	String name = null;
		
		switch(IDataElementContainer.ElementType.valueOf(elementType)){
		case primary:
			name = primaryElement.getName();
			break;
		case rule:
			name = ruleElement.getName();
			break;
		}
		
		return name;
	}
	*/

	
	public String getEnumElementType(){
		return elementType;
	}
	
	public void setEnumElementType(String elementType){
		this.elementType = elementType;
	}

	/**
	 * Used only for marshalling
	 * @return
	 */
	public ElementDTO getPrimaryElement() {
		return primaryElement;
	}

	/**
	 * Used only for marshalling
	 * @param primaryElement
	 */
	public void setPrimaryElement(ElementDTO primaryElement) {
		this.primaryElement = primaryElement;
	}

	/**
	 * Used only for marshalling
	 * @return
	 */
	public ValidationRuleDTO getRuleElement() {
		return ruleElement;
	}

	/**
	 * Used only for marshalling
	 * @param ruleElement
	 */
	public void setRuleElement(ValidationRuleDTO ruleElement) {
		this.ruleElement = ruleElement;
	}

}
