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

package org.psygrid.randomization.model.dto;

import java.util.Date;
import java.util.Map;

public class Allocation extends Persistent {

    /**
     * The treatment arm the subject has been allocated to.
     */
    private Treatment treatment;
    
    /**
     * The subject.
     */
    private String subject;

    /**
     * The date/time of allocation
     */
    private Date date;
    
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Treatment getTreatment() {
        return treatment;
    }

    public void setTreatment(Treatment treatment) {
        this.treatment = treatment;
    }
    
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public org.psygrid.randomization.model.hibernate.Allocation toHibernate(Map<Persistent, org.psygrid.randomization.model.hibernate.Persistent> refs){
        //check for an already existing instance of this 
        //allocation in the set of references
        org.psygrid.randomization.model.hibernate.Allocation a = null;
        if ( refs.containsKey(this)){
            a = (org.psygrid.randomization.model.hibernate.Allocation)refs.get(this);
        }
        else{
            //an instance of the allocation has not already
            //been created, so create it and add it to the map of references
            a = new org.psygrid.randomization.model.hibernate.Allocation();
            refs.put(this, a);
            a.fromDTO(this, refs);
        }
        return a;        
    }
}
