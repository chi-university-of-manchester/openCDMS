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
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

/**
 * Class to to represent factors taken into account by 
 * a stratified randomisation algorithm.
 * 
 * @author Lucy Bridges
 *
 */
public class Strata extends Persistent {

	/**
	 * The name given to this Strata.
	 */
	private String name;

	/**
	 * The values this strata can take.
	 * i.e the strata 'Sex' can have the values: 'male' or 'female'
	 */
	private String[] values = null;
	

	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the values
	 */
	public String[] getValues() {
		return values;
	}

	/**
	 * @param values the values to set
	 */
	public void setValues(String[] values) {
		this.values = values;
	}
	
	
	public org.psygrid.esl.model.hibernate.Strata toHibernate(){
		//create list to hold references to objects in the project's
		//object graph which have multiple references to them within
		//the object graph. This is used so that each object instance
		//is copied to its hibernate equivalent once and once only
		Map<Persistent, org.psygrid.esl.model.hibernate.Persistent> dtoRefs = new HashMap<Persistent, org.psygrid.esl.model.hibernate.Persistent>();
		org.psygrid.esl.model.hibernate.Strata hStrata = toHibernate(dtoRefs);
		dtoRefs = null;
		return hStrata;
	}

	public org.psygrid.esl.model.hibernate.Strata toHibernate(Map<Persistent, org.psygrid.esl.model.hibernate.Persistent> hRefs){
		//check for an already existing instance of a hibernate object for this 
		//randomisation in the set of references
		org.psygrid.esl.model.hibernate.Strata hStrata = null;
		if ( hRefs.containsKey(this)){
			hStrata = (org.psygrid.esl.model.hibernate.Strata)hRefs.get(this);
		}
		if ( null == hStrata ){
			//an instance of the randomisation has not already
			//been created, so create it and add it to the map of references
			hStrata = new org.psygrid.esl.model.hibernate.Strata();
			hRefs.put(this, hStrata);
			toHibernate(hStrata, hRefs);
		}

		return hStrata;

	}

	public void toHibernate(org.psygrid.esl.model.hibernate.Strata hStrata, Map<Persistent, org.psygrid.esl.model.hibernate.Persistent> hRefs){
		super.toHibernate(hStrata, hRefs);

		hStrata.setName(this.name);
		hStrata.setVersion(this.version);
		
		if (values != null) {
			List<String> dtoValues = new ArrayList<String>();
			for (String v: values){
				dtoValues.add(v);
			}
			hStrata.setValues(dtoValues);
		}
	}
	
}
