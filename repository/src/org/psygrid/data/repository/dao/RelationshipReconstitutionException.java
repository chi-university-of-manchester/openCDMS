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
package org.psygrid.data.repository.dao;

import org.psygrid.data.model.hibernate.DataElementContainer;
import org.psygrid.data.model.hibernate.Element;
import org.psygrid.data.model.hibernate.ElementRelationship;


public class RelationshipReconstitutionException extends Exception {

	private static final long serialVersionUID = 667034340843260334L;
	
	private DataElementContainer baseElement;
	private DataElementContainer relatedElement;
	private ElementRelationship er;
	

	public RelationshipReconstitutionException() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	public RelationshipReconstitutionException(String message, Throwable cause, DataElementContainer baseElement, DataElementContainer relatedElement, ElementRelationship er){
		super(message, cause);
		this.baseElement = baseElement;
		this.relatedElement = relatedElement;
		this.er = er;
	}

	public RelationshipReconstitutionException(String message) {
		super(message);
	}
	
	public RelationshipReconstitutionException(String message, DataElementContainer baseElement, DataElementContainer relatedElement, ElementRelationship er){
		super(message);
		this.baseElement = baseElement;
		this.relatedElement = relatedElement;
		this.er = er;
	}


	public DataElementContainer getBaseElement() {
		return baseElement;
	}

	public void setBaseElement(DataElementContainer baseElement) {
		this.baseElement = baseElement;
	}

	public ElementRelationship getEr() {
		return er;
	}

	public void setEr(ElementRelationship er) {
		this.er = er;
	}

	public DataElementContainer getRelatedElement() {
		return relatedElement;
	}

	public void setRelatedElement(DataElementContainer relatedElement) {
		this.relatedElement = relatedElement;
	}
	
	
	

}
