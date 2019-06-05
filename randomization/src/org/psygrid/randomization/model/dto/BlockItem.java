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

import java.util.Map;

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

    public boolean isAllocated() {
        return allocated;
    }

    public void setAllocated(boolean allocated) {
        this.allocated = allocated;
    }

    public Treatment getTreatment() {
        return treatment;
    }

    public void setTreatment(Treatment treatment) {
        this.treatment = treatment;
    }
    
    public org.psygrid.randomization.model.hibernate.BlockItem toHibernate(Map<Persistent, org.psygrid.randomization.model.hibernate.Persistent> refs){
        //check for an already existing instance of this 
        //block item in the set of references
        org.psygrid.randomization.model.hibernate.BlockItem bi = null;
        if ( refs.containsKey(this)){
            bi = (org.psygrid.randomization.model.hibernate.BlockItem)refs.get(this);
        }
        else{
            //an instance of the block item has not already
            //been created, so create it and add it to the map of references
            bi = new org.psygrid.randomization.model.hibernate.BlockItem();
            refs.put(this, bi);
            bi.fromDTO(this, refs);
        }
        return bi;        
    }
}
