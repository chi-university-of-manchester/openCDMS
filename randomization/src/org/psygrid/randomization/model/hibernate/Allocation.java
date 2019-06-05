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

package org.psygrid.randomization.model.hibernate;

import java.util.Date;
import java.util.Map;

/**
 * Class to represent the allocation of a single subject
 * in a trial to a treatment arm.
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_allocations"
 * @hibernate.joined-subclass-key column="c_id"
 */
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
    
    public Allocation(){}
    
    public Allocation(Treatment treatment, String subject){
        this.treatment = treatment;
        this.subject = subject;
        this.date = new Date();
    }
    
    /**
     * Get the subject.
     * 
     * @return The subject.
     * 
     * @hibernate.property column="c_subject"
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Set the subject.
     * 
     * @param subject The subject.
     */
    private void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * Get the treatment arm the subject has been allocated to.
     * 
     * @return The treatment arm.
     * 
     * @hibernate.many-to-one class="org.psygrid.randomization.model.hibernate.Treatment"
     *                        cascade="none"
     *                        column="c_trtmnt_id"
     *                        not-null="true"
     */
    public Treatment getTreatment() {
        return treatment;
    }

    /**
     * Set the arm the subject has been allocated to.
     * 
     * @param treatment The treatment arm.
     */
    private void setTreatment(Treatment treatment) {
        this.treatment = treatment;
    }
    
    /**
     * Get the date/time of allocation.
     * 
     * @return The date/time of allocation
     * 
     * @hibernate.property column="c_date"
     */
    public Date getDate() {
        return date;
    }

    /**
     * Set the date/time of allocation.
     * 
     * @param date The date/time of allocation
     */
    private void setDate(Date date) {
        this.date = date;
    }

    public org.psygrid.randomization.model.dto.Allocation toDTO(Map<Persistent, org.psygrid.randomization.model.dto.Persistent> dtoRefs){
        //check for an already existing instance of a dto object for this 
        //treatment in the set of references
        org.psygrid.randomization.model.dto.Allocation dtoA = null;
        if ( dtoRefs.containsKey(this)){
            dtoA = (org.psygrid.randomization.model.dto.Allocation)dtoRefs.get(this);
        }
        else{
            //an instance of the treatment has not already
            //been created, so create it and add it to the map of references
            dtoA = new org.psygrid.randomization.model.dto.Allocation();
            dtoRefs.put(this, dtoA);
            toDTO(dtoA, dtoRefs);
        }
        return dtoA;
    }
    
    public void toDTO(org.psygrid.randomization.model.dto.Allocation dtoA, Map<Persistent, org.psygrid.randomization.model.dto.Persistent> dtoRefs){
        super.toDTO(dtoA, dtoRefs);
        if ( null != this.treatment ){
            dtoA.setTreatment(this.treatment.toDTO(dtoRefs));
        }
        dtoA.setSubject(this.subject);
        dtoA.setDate(this.date);
    }
    
    public void fromDTO(org.psygrid.randomization.model.dto.Allocation dtoA, Map<org.psygrid.randomization.model.dto.Persistent, Persistent> refs){
        super.fromDTO(dtoA, refs);
        if ( null != dtoA.getTreatment() ){
            this.treatment = dtoA.getTreatment().toHibernate(refs);
        }
        this.subject = dtoA.getSubject();
        this.date = dtoA.getDate();
    }
    
}
