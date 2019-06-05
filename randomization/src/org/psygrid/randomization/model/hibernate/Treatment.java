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
 * Class to represent a treatment to which a randomization
 * process randomly allocates a subject.
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_treatments"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class Treatment extends Persistent {

    /**
     * The name of the treatment.
     */
    private String name;
    
    /**
     * The code for the treatment.
     * <p>
     * Expected to be a one-character string only.
     */
    private String code;

    /**
     * Get the code for the treatment.
     * 
     * @return The treatment code.
     * 
     * @hibernate.property column="c_code"
     */
    public String getCode() {
        return code;
    }

    /**
     * Set the code for the treatment.
     * 
     * @param code The treatment code.
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Get the name of the treatment.
     * 
     * @return The treatment name.
     * 
     * @hibernate.property column="c_name"
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the treatment.
     * 
     * @param name The treatment name.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    public org.psygrid.randomization.model.dto.Treatment toDTO(Map<Persistent, org.psygrid.randomization.model.dto.Persistent> dtoRefs){
        //check for an already existing instance of a dto object for this 
        //treatment in the set of references
        org.psygrid.randomization.model.dto.Treatment dtoT = null;
        if ( dtoRefs.containsKey(this)){
            dtoT = (org.psygrid.randomization.model.dto.Treatment)dtoRefs.get(this);
        }
        else{
            //an instance of the treatment has not already
            //been created, so create it and add it to the map of references
            dtoT = new org.psygrid.randomization.model.dto.Treatment();
            dtoRefs.put(this, dtoT);
            toDTO(dtoT, dtoRefs);
        }
        return dtoT;
    }
    
    public void toDTO(org.psygrid.randomization.model.dto.Treatment dtoT, Map<Persistent, org.psygrid.randomization.model.dto.Persistent> dtoRefs){
        super.toDTO(dtoT, dtoRefs);
        dtoT.setCode(this.code);
        dtoT.setName(this.name);
    }
    
    public void fromDTO(org.psygrid.randomization.model.dto.Treatment dtoT, Map<org.psygrid.randomization.model.dto.Persistent, Persistent> refs){
        super.fromDTO(dtoT, refs);
        this.code = dtoT.getCode();
        this.name = dtoT.getName();
    }
    
}
