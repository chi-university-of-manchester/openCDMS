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

import java.util.Date;
import java.util.Map;

/**
 * Class to represent a value of a response to a date entry.
 * 
 * @author Rob Harper
 */
public class DateValueDTO extends ValueDTO {

    /**
     * The date value
     */
    private Date value;
    
    /**
     * The month component of the date.
     * <p>
     * Valid values are 1 to 12.
     */
    private Integer month;
    
    /**
     * The year component of the date.
     */
    private Integer year;
    
    /**
     * Default no-arg constructor as required by Hibernate.
     */
    public DateValueDTO(){};
    
    /**
     * Constructor that accepts the value of the date value.
     * 
     * @param value The value.
     */
    public DateValueDTO(Date value){
        this.value = value;
    }
    
    public Date getValue() {
        return this.value;
    }

    public void setValue(Date value) {
        this.value = value;
    }
    
    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public org.psygrid.data.model.hibernate.DateValue toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        //check for an already existing instance of a hibernate object for this 
        //date value in the map of references
        org.psygrid.data.model.hibernate.DateValue hDV = null;
        if ( hRefs.containsKey(this)){
            hDV = (org.psygrid.data.model.hibernate.DateValue)hRefs.get(this);
        }
        if ( null == hDV ){
            //an instance of the date value has not already
            //been created, so create it, and add it to the 
            //map of references
            hDV = new org.psygrid.data.model.hibernate.DateValue();
            hRefs.put(this, hDV);
            toHibernate(hDV, hRefs);
        }

        return hDV;
    }
    
    public void toHibernate(org.psygrid.data.model.hibernate.DateValue hDV, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        super.toHibernate(hDV, hRefs);
        hDV.setValue(this.value);
        hDV.setMonth(this.month);
        hDV.setYear(this.year);
    }
    
}
