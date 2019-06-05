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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Class to represent a custom field in the ESL.
 * <p>
 * We have to duplicate this between the repository and the esl
 * so that Collect can retrieve the definition of the custom fields
 * from the dataset to show then in the Esl Dialog.
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_esl_custom_fields"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class EslCustomField extends Persistent {

	/**
	 * The name of the custom field
	 */
	private String name;
	
	/**
	 * The list of permitted values for the custom field
	 */
	private List<String> values = new ArrayList<String>();

	/**
	 * Get the name of the field.
	 * 
	 * @return The name.
	 * @hibernate.property column="c_name"
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name of the field.
	 * 
	 * @param name The name.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
     * @hibernate.list cascade="all" table="t_esl_cust_field_values"
     * @hibernate.key column="c_esl_cust_field_id"
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
	
	/**
	 * Get the number of values for the field.
	 * 
	 * @return Number of values.
	 */
	public int getValueCount(){
		return values.size();
	}
	
	/**
	 * Add a value to the fields list of allowed values.
	 * 
	 * @param value The value to add.
	 */
	public void addValue(String value){
		values.add(value);
	}
	
	/**
	 * Get a value from the fields list of allowed values.
	 * 
	 * @param index The index of the value to get.
	 * @return The value at the specified index.
	 * @throws ModelException if no value exists at the given index.
	 */
	public String getValue(int index) throws ModelException {
		try{
			return values.get(index);
		}
		catch(IndexOutOfBoundsException ex){
			throw new ModelException("No value exists at index "+index, ex);
		}
	}
	
	/**
	 * Remove a value from the fields list of allowed values.
	 * 
	 * @param index The index of the value to remove.
	 * @throws ModelException if no value exists at the given index.
	 */
	public void removeValue(int index) throws ModelException {
		try{
			values.remove(index);
		}
		catch(IndexOutOfBoundsException ex){
			throw new ModelException("No value exists at index "+index, ex);
		}
	}
	
	public org.psygrid.data.model.dto.EslCustomFieldDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth) {
		//check for an already existing instance of a dto object for this 
		//class in the set of references
		org.psygrid.data.model.dto.EslCustomFieldDTO dtoCF = null;
		if ( dtoRefs.containsKey(this)){
			dtoCF = (org.psygrid.data.model.dto.EslCustomFieldDTO)dtoRefs.get(this);
		}
		if ( dtoCF == null ){
			dtoCF = new org.psygrid.data.model.dto.EslCustomFieldDTO();
			dtoRefs.put(this, dtoCF);
			toDTO(dtoCF, dtoRefs, depth);
		}

		return dtoCF;
	}

	public void toDTO(org.psygrid.data.model.dto.EslCustomFieldDTO dtoCF, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth) {
		super.toDTO(dtoCF, dtoRefs, depth);
		dtoCF.setName(this.name);
		dtoCF.setValues(new String[this.values.size()]);
		for ( int i=0, c=values.size(); i<c; i++ ){
			dtoCF.getValues()[i] = values.get(i);
		}
	}

}
