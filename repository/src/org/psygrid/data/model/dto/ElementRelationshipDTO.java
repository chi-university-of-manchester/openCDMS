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

public class ElementRelationshipDTO extends PersistentDTO {
	
	private String elementLSID;
	private String repopulateMethod;
	private String elementClass;
	private int elementIndex;
	private boolean indexedRelationship;
	
	public int getElementIndex() {
		return elementIndex;
	}

	public void setElementIndex(int elementIndex) {
		this.elementIndex = elementIndex;
	}

	public ElementRelationshipDTO(){
		super();
	}

	public String getElementClass() {
		return elementClass;
	}


	public void setElementClass(String elementClass) {
		this.elementClass = elementClass;
	}


	public String getElementLSID() {
		return elementLSID;
	}


	public void setElementLSID(String elementLSID) {
		this.elementLSID = elementLSID;
	}


	public String getRepopulateMethod() {
		return repopulateMethod;
	}


	public void setRepopulateMethod(String repopulateMethod) {
		this.repopulateMethod = repopulateMethod;
	}

	
	@Override
	public org.psygrid.data.model.hibernate.Persistent toHibernate(
			Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs) {
		org.psygrid.data.model.hibernate.ElementRelationship elemRelationship = null;
		if(hRefs.containsKey(this)){
			elemRelationship = (org.psygrid.data.model.hibernate.ElementRelationship)hRefs.get(this);
			return elemRelationship;
		}
		
		elemRelationship = new org.psygrid.data.model.hibernate.ElementRelationship();
		toHibernate(elemRelationship, hRefs);		
		
		return elemRelationship;
	}

	
	public void toHibernate(org.psygrid.data.model.hibernate.ElementRelationship elemRelationship, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
		super.toHibernate(elemRelationship, hRefs);
		elemRelationship.setRelatedElementLSID(getElementLSID());
		elemRelationship.setRepopulateMethod(getRepopulateMethod());
		elemRelationship.setElementClass(getElementClass());
		elemRelationship.setElementIndex(elementIndex);
	}

	public boolean getIndexedRelationship() {
		return indexedRelationship;
	}

	public void setIndexedRelationship(boolean indexedRelationship) {
		this.indexedRelationship = indexedRelationship;
	}
	

}
