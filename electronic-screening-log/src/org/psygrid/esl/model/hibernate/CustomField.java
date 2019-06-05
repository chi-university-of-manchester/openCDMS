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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.psygrid.esl.model.ICustomField;
import org.psygrid.esl.model.IPersistent;
import org.psygrid.esl.model.ModelException;

/**
 * Class to represent a custom field for a project in the ESL.
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_custom_fields"
 * 							  proxy="org.psygrid.esl.model.hibernate.CustomField"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class CustomField extends Persistent implements ICustomField{

	/**
	 * The name of the custom field
	 */
	private String name;
	
	/**
	 * The list of permitted values for the custom field
	 */
	private List<String> values = new ArrayList<String>();

	public CustomField() {
	}

	public CustomField(String name) {
		this.name = name;
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
     * @hibernate.list cascade="all" table="t_cust_field_values"
     * @hibernate.key column="c_cust_field_id"
     *                not-null="true"
     * @hibernate.element column="c_value"
     *                    type="string"
     * @hibernate.list-index column="c_index"
	 */
	public List<String> getValues() {
		return values;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}
	
	public int getValueCount(){
		return values.size();
	}
	
	public void addValue(String value){
		values.add(value);
	}
	
	public String getValue(int index) throws ModelException {
		try{
			return values.get(index);
		}
		catch(IndexOutOfBoundsException ex){
			throw new ModelException("No value exists at index "+index, ex);
		}
	}
	
	public void removeValue(int index) throws ModelException {
		try{
			values.remove(index);
		}
		catch(IndexOutOfBoundsException ex){
			throw new ModelException("No value exists at index "+index, ex);
		}
	}
	
	public org.psygrid.esl.model.dto.CustomField toDTO() {
		Map<IPersistent, org.psygrid.esl.model.dto.Persistent> dtoRefs = new HashMap<IPersistent, org.psygrid.esl.model.dto.Persistent>();
		org.psygrid.esl.model.dto.CustomField dtoCF = toDTO(dtoRefs);
		dtoRefs = null;
		return dtoCF;
	}

	public org.psygrid.esl.model.dto.CustomField toDTO(Map<IPersistent, org.psygrid.esl.model.dto.Persistent> dtoRefs) {
		//check for an already existing instance of a dto object for this 
		//class in the set of references
		org.psygrid.esl.model.dto.CustomField dtoCF = null;
		if ( dtoRefs.containsKey(this)){
			dtoCF = (org.psygrid.esl.model.dto.CustomField)dtoRefs.get(this);
		}
		if ( dtoCF == null ){
			dtoCF = new org.psygrid.esl.model.dto.CustomField();
			dtoRefs.put(this, dtoCF);
			toDTO(dtoCF, dtoRefs);
		}

		return dtoCF;
	}

	public void toDTO(org.psygrid.esl.model.dto.CustomField dtoCF, Map<IPersistent, org.psygrid.esl.model.dto.Persistent> dtoRefs) {
		super.toDTO(dtoCF, dtoRefs);
		dtoCF.setName(this.name);
		dtoCF.setValues(new String[this.values.size()]);
		for ( int i=0, c=values.size(); i<c; i++ ){
			dtoCF.getValues()[i] = values.get(i);
		}
	}

}
