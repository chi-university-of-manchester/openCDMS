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
 * Class to represent a value of a response to a numeric entry.
 * 
 * @author Rob Harper
 *
 */
public class NumericValueDTO extends ValueDTO {

    /**
     * The numeric value
     */
    private Double value;
    
    /**
     * Default no-arg constructor as required by Hibernate.
     */
    public NumericValueDTO(){};
    
    /**
     * Constructor that accepts the value of the numeric value.
     * 
     * @param value The value.
     */
    public NumericValueDTO(Double value){
        this.value = value;
    }
    
    public Double getValue() {
        return this.value;
    }

    public void setValue(Double value){
        this.value = value;
    }

    public org.psygrid.data.model.hibernate.NumericValue toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        //check for an already existing instance of a hibernate object for this 
        //numeric value in the map of references
        org.psygrid.data.model.hibernate.NumericValue hNV = null;
        if ( hRefs.containsKey(this)){
            hNV = (org.psygrid.data.model.hibernate.NumericValue)hRefs.get(this);
        }
        if ( null == hNV ){
            //an instance of the numeric value has not already
            //been created, so create it, and add it to the map
            //of references
            hNV = new org.psygrid.data.model.hibernate.NumericValue();
            hRefs.put(this, hNV);
            toHibernate(hNV, hRefs);
        }

        return hNV;
    }
    
    public void toHibernate(org.psygrid.data.model.hibernate.NumericValue hNV, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        super.toHibernate(hNV, hRefs);
        hNV.setValue(this.value);
    }
    
}
