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

import java.util.HashMap;
import java.util.Map;

/**
* Class to represent response codes that are standard across
* all entrys in the entire repository.
* 
* @author Rob Harper
*
*/
public class StandardCodeDTO extends PersistentDTO {

    private String description;
    
    private int code;
    
    private boolean usedForDerivedEntry;

    public StandardCodeDTO(){};
    
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

    public boolean isUsedForDerivedEntry() {
        return usedForDerivedEntry;
    }

    public void setUsedForDerivedEntry(boolean usedForDerivedEntry) {
        this.usedForDerivedEntry = usedForDerivedEntry;
    }

    public org.psygrid.data.model.hibernate.StandardCode toHibernate(){
        Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs = new HashMap<PersistentDTO, org.psygrid.data.model.hibernate.Persistent>();
        return toHibernate(hRefs);
    }
    
    public org.psygrid.data.model.hibernate.StandardCode toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        //check for an already existing instance of a hibernate object for this 
        //standard code in the map of references
        org.psygrid.data.model.hibernate.StandardCode hSC = null;
        if ( hRefs.containsKey(this)){
            hSC = (org.psygrid.data.model.hibernate.StandardCode)hRefs.get(this);
        }
        if ( null == hSC ){
            //an instance of the standard code has not already
            //been created, so create it, and add it to the map of references
            hSC = new org.psygrid.data.model.hibernate.StandardCode();
            hRefs.put(this, hSC);
            toHibernate(hSC, hRefs);
        }

        return hSC;
    }
    
    public void toHibernate(org.psygrid.data.model.hibernate.StandardCode hSC, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        super.toHibernate(hSC, hRefs);
        hSC.setCode(this.code);
        hSC.setDescription(this.description);
        hSC.setUsedForDerivedEntry(this.usedForDerivedEntry);
    }
    
}
