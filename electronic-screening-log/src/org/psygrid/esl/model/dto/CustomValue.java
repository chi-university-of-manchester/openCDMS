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

import java.util.HashMap;
import java.util.Map;

/**
 * @author Rob Harper
 *
 */
public class CustomValue extends Persistent {

	/**
	 * The name of the custom field this value is for.
	 */
	private String name;
	
	/**
	 * The value. Will be one of the allowed values specified for 
	 * the field.
	 */
	private String value;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public org.psygrid.esl.model.hibernate.CustomValue toHibernate(){
		//create list to hold references to objects in the project's
		//object graph which have multiple references to them within
		//the object graph. This is used so that each object instance
		//is copied to its hibernate equivalent once and once only
		Map<Persistent, org.psygrid.esl.model.hibernate.Persistent> dtoRefs = new HashMap<Persistent, org.psygrid.esl.model.hibernate.Persistent>();
		org.psygrid.esl.model.hibernate.CustomValue hCV = toHibernate(dtoRefs);
		dtoRefs = null;
		return hCV;
	}

	public org.psygrid.esl.model.hibernate.CustomValue toHibernate(Map<Persistent, org.psygrid.esl.model.hibernate.Persistent> hRefs){
				
	    //check for an already existing instance of a hibernate object for this 
        //record in the map of references
		org.psygrid.esl.model.hibernate.CustomValue hCV = null;
        if ( hRefs.containsKey(this)){
            hCV = (org.psygrid.esl.model.hibernate.CustomValue)hRefs.get(this);
        }
        if ( null == hCV ){
        	hCV = new org.psygrid.esl.model.hibernate.CustomValue();
        	hRefs.put(this, hCV);
        	toHibernate(hCV, hRefs);
        }
        return hCV;
	}

	public void toHibernate(org.psygrid.esl.model.hibernate.CustomValue hCV, Map<Persistent, org.psygrid.esl.model.hibernate.Persistent> hRefs){
		super.toHibernate(hCV, hRefs);		
		hCV.setName(name);
		hCV.setValue(this.value);
	}

}
