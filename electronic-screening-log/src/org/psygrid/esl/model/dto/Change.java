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

package org.psygrid.esl.model.dto;

import java.util.Map;


/**
 * Records change made to a particular field in a instance of updating an object
 * 
 * @author Lucy Bridges
 *
 */
public class Change extends Persistent {

	private String field;
	private String prevValue;
	private String newValue;

	private ProvenanceChange provenance = null;

	/**
	 * @return the field
	 */
	public String getField() {
		return field;
	}

	/**
	 * @param field the field to set
	 */
	public void setField(String field) {
		this.field = field;
	}

	/**
	 * @return the newValue
	 */
	public String getNewValue() {
		return newValue;
	}

	/**
	 * @param newValue the newValue to set
	 */
	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}

	/**
	 * @return the prevValue
	 */
	public String getPrevValue() {
		return prevValue;
	}

	/**
	 * @param prevValue the prevValue to set
	 */
	public void setPrevValue(String prevValue) {
		this.prevValue = prevValue;
	}


	public ProvenanceChange getProvenance() {
		return provenance;
	}

	/**
	 * @param provenance the provenance to set
	 */
	public void setProvenance(ProvenanceChange provenance) {
		this.provenance = provenance;
	}


	public org.psygrid.esl.model.hibernate.Change toHibernate(Map<Persistent, org.psygrid.esl.model.hibernate.Persistent> hRefs){
		//check for an already existing instance of a hibernate object for this 
        //instance in the map of references
		org.psygrid.esl.model.hibernate.Change hC = null;
        if ( hRefs.containsKey(this)){
            hC = (org.psygrid.esl.model.hibernate.Change)hRefs.get(this);
        }
        if ( null == hC ){
            //an instance of the class has not already
            //been created, so create it, and add it to 
            //the map of references	
        	hC = new org.psygrid.esl.model.hibernate.Change();
        	hRefs.put(this, hC);
        	toHibernate(hC, hRefs);
        }
		return hC;
	}

	public void toHibernate(org.psygrid.esl.model.hibernate.Change hP, Map<Persistent, org.psygrid.esl.model.hibernate.Persistent> hRefs){
		super.toHibernate(hP, hRefs);
		hP.setField(field);
		hP.setNewValue(newValue);
		hP.setPrevValue(prevValue);

		if (provenance != null) {
			hP.setProvenance(provenance.toHibernate(hRefs));
		}
	}

}

