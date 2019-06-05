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
 * Class to represent a value of a response to a long text entry.
 * 
 * @author Rob Harper
 * 
 */
public class LongTextValueDTO extends ValueDTO {

    /**
     * The textual value of the response
     */
    private String value;
    
    /**
     * Default no-arg constructor, as required by the Hibernate framework
     * for all persistable classes.
     */
    public LongTextValueDTO(){};
    
    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public org.psygrid.data.model.hibernate.LongTextValue toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        //check for an already existing instance of a hibernate object for this 
        //long text value in the map of references
        org.psygrid.data.model.hibernate.LongTextValue hLTV = null;
        if ( hRefs.containsKey(this)){
            hLTV = (org.psygrid.data.model.hibernate.LongTextValue)hRefs.get(this);
        }
        if ( null == hLTV ){
            //an instance of the long text value has not already
            //been created, so create it, and add it to the map 
            //of references
            hLTV = new org.psygrid.data.model.hibernate.LongTextValue();
            hRefs.put(this, hLTV);
            toHibernate(hLTV, hRefs);
        }

        return hLTV;
    }
    
    public void toHibernate(org.psygrid.data.model.hibernate.LongTextValue hLTV, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        super.toHibernate(hLTV, hRefs);
        hLTV.setValue(this.value);
    }
    
}
