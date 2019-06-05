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

import java.util.HashMap;
import java.util.Map;

/**
 * @author Rob Harper
 *
 */
public class EslCustomFieldDTO extends PersistentDTO {

	/**
	 * The name of the custom field
	 */
	private String name;
	
	/**
	 * The list of permitted values for the custom field
	 */
	private String[] values;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String[] getValues() {
		return values;
	}

	public void setValues(String[] values) {
		this.values = values;
	}

	public org.psygrid.data.model.hibernate.EslCustomField toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
		
	    //check for an already existing instance of a hibernate object for this 
        //record in the map of references
		org.psygrid.data.model.hibernate.EslCustomField hCF = null;
        if ( hRefs.containsKey(this)){
            hCF = (org.psygrid.data.model.hibernate.EslCustomField)hRefs.get(this);
        }
        if ( null == hCF ){
        	hCF = new org.psygrid.data.model.hibernate.EslCustomField();
        	hRefs.put(this, hCF);
        	toHibernate(hCF, hRefs);
        }
        return hCF;
	}

	public void toHibernate(org.psygrid.data.model.hibernate.EslCustomField hCF, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
		super.toHibernate(hCF, hRefs);		
		hCF.setName(name);
		for ( int i=0, c=values.length; i<c; i++ ){
			hCF.getValues().add(values[i]);
		}
	}

}
