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

import java.util.Map;

/**
 * Class to represent a single treatment in a treatment block,
 * including whether the treatment block item has yet been 
 * allocated to a subject.
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_block_items"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class BlockItem extends Persistent {

    /**
     * The treatment that has been selected for this block item.
     */
    private Treatment treatment;
    
    /**
     * Flag to indicate whether the block item has been allocated
     * to a subject or not.
     */
    private boolean allocated;
    
    public BlockItem(){}
    
    public BlockItem(Treatment treatment){
        this.treatment = treatment;
        allocated = false;
    }

    /**
     * Get the value of the flag to indicate whether the block item 
     * has been allocated to a subject or not.
     * 
     * @return The allocated flag.
     * 
     * @hibernate.property column="c_allocated"
     */
    public boolean isAllocated() {
        return allocated;
    }

    /**
     * Set the value of the flag to indicate whether the block item 
     * has been allocated to a subject or not.
     * 
     * @param allocated The allocated flag.
     */
    private void setAllocated(boolean allocated) {
        this.allocated = allocated;
    }

    /**
     * Get the treatment that has been selected for this block item.
     * 
     * @return The treatment.
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
     * Set the treatment that has been selected for this block item.
     * 
     * @param treatment The treatment.
     */
    private void setTreatment(Treatment treatment) {
        this.treatment = treatment;
    }

    public void allocate(){
        this.allocated = true;
    }
    
    public org.psygrid.randomization.model.dto.BlockItem toDTO(Map<Persistent, org.psygrid.randomization.model.dto.Persistent> dtoRefs){
        //check for an already existing instance of a dto object for this 
        //block item in the set of references
        org.psygrid.randomization.model.dto.BlockItem dtoB = null;
        if ( dtoRefs.containsKey(this)){
            dtoB = (org.psygrid.randomization.model.dto.BlockItem)dtoRefs.get(this);
        }
        else{
            //an instance of the block item has not already
            //been created, so create it and add it to the map of references
            dtoB = new org.psygrid.randomization.model.dto.BlockItem();
            dtoRefs.put(this, dtoB);
            toDTO(dtoB, dtoRefs);
        }
        return dtoB;
    }
    
    public void toDTO(org.psygrid.randomization.model.dto.BlockItem dtoB, Map<Persistent, org.psygrid.randomization.model.dto.Persistent> dtoRefs){
        super.toDTO(dtoB, dtoRefs);
        if ( null != this.treatment ){
            dtoB.setTreatment(this.treatment.toDTO(dtoRefs));
        }
        dtoB.setAllocated(this.allocated);
    }

    public void fromDTO(org.psygrid.randomization.model.dto.BlockItem dtoB, Map<org.psygrid.randomization.model.dto.Persistent, Persistent> refs){
        super.fromDTO(dtoB, refs);
        if ( null != dtoB.getTreatment() ){
            this.treatment = dtoB.getTreatment().toHibernate(refs);
        }
        this.allocated = dtoB.isAllocated();
    }
    
}
