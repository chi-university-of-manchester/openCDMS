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

public class IntegerValueDTO extends ValueDTO {

    /**
     * The integer value
     */
    private Integer value;
    
    /**
     * Default no-arg constructor as required by Hibernate.
     */
    public IntegerValueDTO(){}

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
    
    
    public org.psygrid.data.model.hibernate.IntegerValue toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        //check for an already existing instance of a hibernate object for this 
        //numeric value in the map of references
        org.psygrid.data.model.hibernate.IntegerValue hIV = null;
        if ( hRefs.containsKey(this)){
            hIV = (org.psygrid.data.model.hibernate.IntegerValue)hRefs.get(this);
        }
        else{
            //an instance of the numeric value has not already
            //been created, so create it, and add it to the map
            //of references
            hIV = new org.psygrid.data.model.hibernate.IntegerValue();
            hRefs.put(this, hIV);
            toHibernate(hIV, hRefs);
        }

        return hIV;
    }
    
    public void toHibernate(org.psygrid.data.model.hibernate.IntegerValue hIV, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        super.toHibernate(hIV, hRefs);
        hIV.setValue(this.value);
    }
    
}
