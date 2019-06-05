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

package org.psygrid.data.model.hibernate;

import java.util.HashMap;
import java.util.Map;


/**
* Class to represent response codes that are standard across
* all entrys in the entire repository.
* 
* @author Rob Harper
*
* @hibernate.joined-subclass table="t_std_responses"
* @hibernate.joined-subclass-key column="c_id"
*/
public class StandardCode extends Persistent {

    private String description;
    
    private int code;
    
    /**
     * Flag to specify whether the standard code should be
     * used for derived entries whose values cannot be calculated
     * due to missing inputs.
     */
    private boolean usedForDerivedEntry;
    
    /**
     * Default no-arg constructor.
     */
    public StandardCode(){}
    
    public StandardCode(StandardCode sc){
    	super(sc);
    	this.code = sc.getCode();
    	this.description = sc.getDescription();
    	this.usedForDerivedEntry = sc.isUsedForDerivedEntry();
    }
    
    /**
     * Constructor that accepts values for the description and numeric
     * code of the standard code.
     * 
     * @param description The description.
     * @param code The numeric code.
     */
    public StandardCode(String description, int code){
        this.description = description;
        this.code = code;
    }
    
    /**
     * Get the description of the response code.
     * 
     * @return The description.
     * @hibernate.property column="c_description"
     *                     not-null="true"
     *                     unique="true"
     */
    public String getDescription() {
        return this.description;
    }
    
    /**
     * Set the description of the response code.
     * 
     * @param description The description.
     */
    public void setDescription(String description){
        this.description = description;
    }

    /**
     * Get the numeric code of the response code.
     * 
     * @return The numeric code.
     * @hibernate.property column="c_code"
     *                     not-null="true"
     *                     unique="true"
     */
    public int getCode() {
        return this.code;
    }

    /**
     * Set the numeric code of the response code.
     * 
     * @param code The numeric code.
     */
    public void setCode(int code){
        this.code = code;
    }
        
    /**
     * Get the flag to specify whether the standard code should be
     * used for derived entries whose values cannot be calculated
     * due to missing inputs.
     * 
     * @return The used for derived entry flag.
     * @hibernate.property column="c_for_derived"
     */
    public boolean isUsedForDerivedEntry() {
        return usedForDerivedEntry;
    }

    /**
     * Set the flag to specify whether the standard code should be
     * used for derived entries whose values cannot be calculated
     * due to missing inputs.
     * 
     * @param usedForDerivedEntry The used for derived entry flag.
     */
    public void setUsedForDerivedEntry(boolean usedForDerivedEntry) {
        this.usedForDerivedEntry = usedForDerivedEntry;
    }
    
    public String getForDisplay(){
    	return Integer.toString(this.code) + ". " + this.description;
    }

    /**
     * Return a dto bean representation of the standard code object.
     * 
     * @return The dto bean representation.
     */
    public org.psygrid.data.model.dto.StandardCodeDTO toDTO(){
        Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
        return toDTO(dtoRefs, RetrieveDepth.DS_COMPLETE);
    }
    
    public org.psygrid.data.model.dto.StandardCodeDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        //check for an already existing instance of a dto object for this 
        //standard code in the map of references
        org.psygrid.data.model.dto.StandardCodeDTO dtoSC = null;
        if ( dtoRefs.containsKey(this)){
            dtoSC = (org.psygrid.data.model.dto.StandardCodeDTO)dtoRefs.get(this);
        }
        if ( null == dtoSC ){
            //an instance of the standard code has not already
            //been created, so create it, and add it to the map of references
            dtoSC = new org.psygrid.data.model.dto.StandardCodeDTO();
            dtoRefs.put(this, dtoSC);
            toDTO(dtoSC, dtoRefs, depth);
        }

        return dtoSC;
    }
    
    public void toDTO(org.psygrid.data.model.dto.StandardCodeDTO dtoSC, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        super.toDTO(dtoSC, dtoRefs, depth);
        dtoSC.setCode(this.code);
        dtoSC.setDescription(this.description);
        dtoSC.setUsedForDerivedEntry(this.usedForDerivedEntry);
    }
    
}
