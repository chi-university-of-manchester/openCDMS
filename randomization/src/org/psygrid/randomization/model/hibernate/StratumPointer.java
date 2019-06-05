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
 * Class to represent a "pointer" to a specific value of
 * a given Stratum.
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_stratum_ptrs"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class StratumPointer extends Persistent {

    /**
     * The Stratum object that this pointer "points" to.
     */
    private Stratum stratum;
    
    /**
     * The value of the Stratum object that this pointer "points" to.
     */
    private String value;

    /**
     * Get the Stratum object that this pointer "points" to.
     * 
     * @return The Stratum object.
     * 
     * @hibernate.many-to-one class="org.psygrid.randomization.model.hibernate.Stratum"
     *                        column="c_stratum_id"
     *                        not-null="false"
     *                        cascade="none"
     */
    public Stratum getStratum() {
        return stratum;
    }

    /**
     * Set the Stratum object that this pointer "points" to.
     * 
     * @param stratum The Stratum object.
     */
    public void setStratum(Stratum stratum) {
        this.stratum = stratum;
    }

    /**
     * Get the value of the Stratum object that this pointer "points" to.
     * 
     * @return The value.
     * 
     * @hibernate.property column="c_value"
     */
    public String getValue() {
        return value;
    }

    /**
     * Set the value of the Stratum object that this pointer "points" to.
     * 
     * @param value The value.
     */
    public void setValue(String value) {
        this.value = value;
    }
    
    
    public org.psygrid.randomization.model.dto.StratumPointer toDTO(Map<Persistent, org.psygrid.randomization.model.dto.Persistent> dtoRefs){
        //check for an already existing instance of a dto object for this 
        //stratum pointer in the set of references
        org.psygrid.randomization.model.dto.StratumPointer dtoSP = null;
        if ( dtoRefs.containsKey(this)){
            dtoSP = (org.psygrid.randomization.model.dto.StratumPointer)dtoRefs.get(this);
        }
        else{
            //an instance of the stratum pointer has not already
            //been created, so create it and add it to the map of references
            dtoSP = new org.psygrid.randomization.model.dto.StratumPointer();
            dtoRefs.put(this, dtoSP);
            toDTO(dtoSP, dtoRefs);
        }
        return dtoSP;
    }
    
    public void toDTO(org.psygrid.randomization.model.dto.StratumPointer dtoSP, Map<Persistent, org.psygrid.randomization.model.dto.Persistent> dtoRefs){
        super.toDTO(dtoSP, dtoRefs);
        if ( null != this.stratum ){
            dtoSP.setStratum(this.stratum.toDTO(dtoRefs));
        }
        dtoSP.setValue(this.value);
    }
    
    public void fromDTO(org.psygrid.randomization.model.dto.StratumPointer dtoSP, Map<org.psygrid.randomization.model.dto.Persistent, Persistent> refs){
        super.fromDTO(dtoSP, refs);
        if ( null != dtoSP.getStratum() ){
            this.stratum = dtoSP.getStratum().toHibernate(refs);
        }
        this.value = dtoSP.getValue();
    }
    
}
