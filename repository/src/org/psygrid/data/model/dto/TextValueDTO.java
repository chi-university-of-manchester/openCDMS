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

import java.util.Map;


/**
 * Class to represent a value of a response to a TextEntry.
 * 
 * @author Rob Harper
 * 
 */
public class TextValueDTO extends ValueDTO {

	/**
	 * The textual value of the response
	 */
	private String value;
    
	/**
	 * Default no-arg constructor, as required by the Hibernate framework
	 * for all persistable classes.
	 */
	public TextValueDTO(){};
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}

    public org.psygrid.data.model.hibernate.TextValue toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        //check for an already existing instance of a hibernate object for this 
        //text value in the map of references
        org.psygrid.data.model.hibernate.TextValue hTV = null;
        if ( hRefs.containsKey(this)){
            hTV = (org.psygrid.data.model.hibernate.TextValue)hRefs.get(this);
        }
        if ( null == hTV ){
            //an instance of the text value has not already
            //been created, so create it, and add it to the map
            //of references
            hTV = new org.psygrid.data.model.hibernate.TextValue();
            hRefs.put(this, hTV);
            toHibernate(hTV, hRefs);
        }

        return hTV;
    }
    
    public void toHibernate(org.psygrid.data.model.hibernate.TextValue hTV, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        super.toHibernate(hTV, hRefs);
        hTV.setValue(this.value);
    }
    
}
