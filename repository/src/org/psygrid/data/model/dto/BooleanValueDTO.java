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
 * Class to represent a value of a response to a
 * boolean entry.
 * 
 * @author Rob Harper
 */
public class BooleanValueDTO extends ValueDTO {

    /**
     * The boolean value
     */
    private boolean value;
    
    /**
     * Default no-arg constructor as required by Hibernate.
     */
    public BooleanValueDTO(){};
    
    public boolean getValue() {
        return this.value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }
        
    public org.psygrid.data.model.hibernate.BooleanValue toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        //check for an already existing instance of a hibernate object for this 
        //boolean value in the map of references
        org.psygrid.data.model.hibernate.BooleanValue hBV = null;
        if ( hRefs.containsKey(this)){
            hBV = (org.psygrid.data.model.hibernate.BooleanValue)hRefs.get(this);
        }
        if ( null == hBV ){
            //an instance of the boolean value has not already
            //been created, so create it, and add it to the map of references
            hBV = new org.psygrid.data.model.hibernate.BooleanValue();
            hRefs.put(this, hBV);
            toHibernate(hBV, hRefs);
        }

        return hBV;
    }
    
    public void toHibernate(org.psygrid.data.model.hibernate.BooleanValue hBV, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        super.toHibernate(hBV, hRefs);
        hBV.setValue(this.value);
    }
    
}