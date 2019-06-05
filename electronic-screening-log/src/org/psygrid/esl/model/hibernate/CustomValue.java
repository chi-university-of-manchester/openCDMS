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

import java.util.HashMap;
import java.util.Map;

import org.psygrid.esl.model.ICustomValue;
import org.psygrid.esl.model.IPersistent;

/**
 * Class to represent the value of a custom field for a subject.
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_custom_values"
 * 							  proxy="org.psygrid.esl.model.hibernate.CustomValue"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class CustomValue extends Persistent implements ICustomValue {

	/**
	 * The name of the custom field this value is for.
	 */
	private String name;
	
	/**
	 * The value. Will be one of the allowed values specified for 
	 * the field.
	 */
	private String value;

	public CustomValue(){}
	
	public CustomValue(String name, String value){
		this.name = name;
		this.value = value;
	}
	
	/**
	 * @hibernate.property column="c_name"
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @hibernate.property column="c_value"
	 */
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public org.psygrid.esl.model.dto.CustomValue toDTO() {
		Map<IPersistent, org.psygrid.esl.model.dto.Persistent> dtoRefs = new HashMap<IPersistent, org.psygrid.esl.model.dto.Persistent>();
		org.psygrid.esl.model.dto.CustomValue dtoCV = toDTO(dtoRefs);
		dtoRefs = null;
		return dtoCV;
	}

	public org.psygrid.esl.model.dto.CustomValue toDTO(Map<IPersistent, org.psygrid.esl.model.dto.Persistent> dtoRefs) {
		//check for an already existing instance of a dto object for this 
		//class in the set of references
		org.psygrid.esl.model.dto.CustomValue dtoCV = null;
		if ( dtoRefs.containsKey(this)){
			dtoCV = (org.psygrid.esl.model.dto.CustomValue)dtoRefs.get(this);
		}
		if ( dtoCV == null ){
			dtoCV = new org.psygrid.esl.model.dto.CustomValue();
			dtoRefs.put(this, dtoCV);
			toDTO(dtoCV, dtoRefs);
		}

		return dtoCV;
	}

	public void toDTO(org.psygrid.esl.model.dto.CustomValue dtoCF, Map<IPersistent, org.psygrid.esl.model.dto.Persistent> dtoRefs) {
		super.toDTO(dtoCF, dtoRefs);
		dtoCF.setName(this.name);
		dtoCF.setValue(this.value);
	}

}
