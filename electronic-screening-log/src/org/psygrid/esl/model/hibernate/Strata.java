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
import java.util.List;
import java.util.ArrayList;

import org.psygrid.esl.model.IPersistent;
import org.psygrid.esl.model.IStrata;

/**
 * @author Lucy Bridges
 *
 * @hibernate.joined-subclass table="t_strata"
 * 								proxy="org.psygrid.esl.model.hibernate.Strata"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class Strata extends Persistent implements IStrata {

	/**
	 * The name given to this Strata.
	 * i.e 'Sex' or 'Location'
	 */
	private String name;
	
	/**
	 * The values this strata can take.
	 * i.e the strata 'Sex' can have the values: 'male' or 'female'
	 */
	private List<String> values = new ArrayList<String>();

	private Randomisation random;
	
	public Strata() {
	}
	
	public Strata(String name) {
		this.name = name;
	}
	public Strata(String name, List<String> values) {
		this.name = name;
		this.values = values;
	}
	/**
	 * @hibernate.property column="c_strata_name"
	 */
	public String getName() {
		return name;
	}

	/**
	 * @see org.psygrid.esl.model.IStrata#setName(java.lang.String)
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the values
	 * 
	 * @hibernate.list cascade="all" table="t_strata_values"
     * @hibernate.key column="c_strata_id" not-null="true"
     * 
     * @hibernate.element column="c_value"
     *                    type="string"
     * @hibernate.list-index column="c_index"
	 */
	public List<String> getValues() {
		return values;
	}

	/**
	 * @param values the values to set
	 */
	public void setValues(List<String> values) {
		this.values = values;
	}

	/**
	 * Add a value to the list
	 * 
	 * @param value
	 */
	public void setValue(String value) {
		values.add(value);
	}

	/**
     * @hibernate.many-to-one class="org.psygrid.esl.model.hibernate.Randomisation"
     *                        column="c_randomisation_id"
     *                        not-null="true"
     *                        insert="false"
     *                        update="false"
     */
	public Randomisation getRandomisation() {
		return random;
	}
	
	public void setRandomisation(Randomisation random) {
		this.random = random;
	}
    
	/**
	 * Store object reference to maintain persistence
	 * 
	 * @return dto.Strata
	 */
	public org.psygrid.esl.model.dto.Strata toDTO(){
		Map<IPersistent, org.psygrid.esl.model.dto.Persistent> dtoRefs = new HashMap<IPersistent, org.psygrid.esl.model.dto.Persistent>();
		org.psygrid.esl.model.dto.Strata dtoStrata = toDTO(dtoRefs);
		dtoRefs = null;
		return dtoStrata;
	}

	public org.psygrid.esl.model.dto.Strata toDTO(Map<IPersistent, org.psygrid.esl.model.dto.Persistent> dtoRefs) {
		//check for an already existing instance of a dto object for this 
		//class in the set of references
		org.psygrid.esl.model.dto.Strata dtoStrata = null;
		if ( dtoRefs.containsKey(this)){
			dtoStrata = (org.psygrid.esl.model.dto.Strata)dtoRefs.get(this);
		}
		if ( null == dtoStrata ){
			//an instance of the dataset has not already
			//been created, so create it and add it to the map of references
			dtoStrata = new org.psygrid.esl.model.dto.Strata();
			dtoRefs.put(this, dtoStrata);
			toDTO(dtoStrata, dtoRefs);
		}

		return dtoStrata;
	}

	public void toDTO(org.psygrid.esl.model.dto.Strata dtoStrata, Map<IPersistent, org.psygrid.esl.model.dto.Persistent> dtoRefs){
		super.toDTO(dtoStrata, dtoRefs);
		dtoStrata.setName(this.name);
		dtoStrata.setVersion(this.version);  
		
		String[] v = new String[values.size()]; 
		values.toArray(v);
		if (values != null) {
			dtoStrata.setValues(v);	
		}
	}
	
}
