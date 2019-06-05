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

package org.psygrid.esl.model.hibernate;

import java.util.Map;
import java.util.HashMap;

import org.psygrid.esl.model.IPersistent;
import org.psygrid.esl.model.IChange;
import org.psygrid.esl.model.IProvenanceChange;

/**
 * @hibernate.joined-subclass table="t_change"
 * 							  proxy="org.psygrid.esl.model.hibernate.Change"
 * @hibernate.joined-subclass-key column="c_id"
 * 
 * @author Lucy Bridges
 *
 */
public class Change extends Persistent implements IChange {

	private String field;
	private String prevValue;
	private String newValue;
	
	private IProvenanceChange provenance = null;
	
	/**
	 * @return the field
	 * 
	 * @hibernate.property column="c_field_name"
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
	 * 
	 * @hibernate.property column="c_new_value2"
	 * 	 					type="text"
     *                      length="4096"
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
	 * 
	 * @hibernate.property column="c_prev_value2"
	 * 	   					type="text"
     *                      length="4096"
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



	/**
	 * @return the provenance change
	 * 
	 * @hibernate.many-to-one class="org.psygrid.esl.model.hibernate.ProvenanceChange"
	 *                        column="c_provenance_change_id"
	 *                        not-null="false"
	*						  cascade="all"
	*						  
	 */
	public IProvenanceChange getProvenance() {
		return provenance;
	}

	/**
	 * @param provenance the provenance to set
	 */
	public void setProvenance(IProvenanceChange provenance) {
		this.provenance = provenance;
	}

	public org.psygrid.esl.model.dto.Change toDTO() {
		Map<IPersistent, org.psygrid.esl.model.dto.Persistent> dtoRefs = new HashMap<IPersistent, org.psygrid.esl.model.dto.Persistent>();
		org.psygrid.esl.model.dto.Change dtoC = toDTO(dtoRefs);
		dtoRefs = null;
		return dtoC;
	}
	
	public org.psygrid.esl.model.dto.Change toDTO(Map<IPersistent, org.psygrid.esl.model.dto.Persistent> dtoRefs){
		
		//check for an already existing instance of a dto object for this 
		//group in the set of references
		org.psygrid.esl.model.dto.Change dtoP = null;
		if ( dtoRefs.containsKey(this)){
			dtoP = (org.psygrid.esl.model.dto.Change)dtoRefs.get(this);
		}
		if ( null == dtoP ){
			dtoP = new org.psygrid.esl.model.dto.Change();
			dtoRefs.put(this, dtoP);
			toDTO(dtoP, dtoRefs);
		}
		return dtoP;
	}

	public void toDTO(org.psygrid.esl.model.dto.Change dtoP, Map<IPersistent, org.psygrid.esl.model.dto.Persistent> dtoRefs){
		super.toDTO(dtoP, dtoRefs);
		dtoP.setField(field);
		dtoP.setNewValue(newValue);
		dtoP.setPrevValue(prevValue);
		
		if (provenance != null) {
			dtoP.setProvenance(provenance.toDTO(dtoRefs));
		}
	}
	
}
