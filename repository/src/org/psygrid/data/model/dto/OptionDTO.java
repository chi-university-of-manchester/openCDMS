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

import java.util.List;
import java.util.Map;


/**
 * Class to represent an individual option that will appear in the
 * list of options for an OptionEntry
 * 
 * @author Rob Harper
 *
 */
public class OptionDTO extends ComponentDTO {

    /**
     * Code value of the Option
     */
    private Integer code;
    
    /**
     * Boolean flag to define whether it is permitted for additional
     * textual data to be entered if this option is selected.
     */
    private boolean textEntryAllowed;
    
    /**
     * List of option dependents.
     * <p>
     * These are the entrys whose status is modified in response
     * to this option being selected.
     */
    private OptionDependentDTO[] optionDependents = new OptionDependentDTO[0];
    
    /**
     * The option entry that this option is a part of.
     */
    private OptionEntryDTO entry;
    
    /**
     * Default no-arg constructor, as required by the Hibernate framwework
     * for all persistable classes.
     * 
     * Scope is protected as all Options must have a name
     */
    public OptionDTO(){}
    
    /**
     * Get the code value of the Option
     * 
     * @return The code value
     */
    public Integer getCode() {
        return code;
    }

    /**
     * Set the code value of the Option
     * 
     * @param codeValue The code value
     */
    public void setCode(Integer code) {
        this.code = code;
    }

    public boolean isTextEntryAllowed() {
        return textEntryAllowed;
    }

    public void setTextEntryAllowed(boolean textEntryAllowed) {
        this.textEntryAllowed = textEntryAllowed;
    }

    /**
     * Get the list of option dependents.
     * <p>
     * These are the entrys whose status is modified in response
     * to this option being selected.
     * 
     * @return The list of option dependents.
     */
    public OptionDependentDTO[] getOptionDependents() {
        return optionDependents;
    }

    /**
     * Set the list of option dependents.
     * 
     * @param optionDependents The list of option dependents.
     */
    public void setOptionDependents(OptionDependentDTO[] optionDependents) {
        this.optionDependents = optionDependents;
    }
    
    public OptionEntryDTO getEntry() {
        return entry;
    }

    public void setEntry(OptionEntryDTO entry) {
        this.entry = entry;
    }

    public org.psygrid.data.model.hibernate.Option toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        //check for an already existing instance of a hibernate object for this 
        //option in the map of references
        org.psygrid.data.model.hibernate.Option hO = null;
        if ( hRefs.containsKey(this)){
            hO = (org.psygrid.data.model.hibernate.Option)hRefs.get(this);
        }
        if ( null == hO ){
            //an instance of the option has not already
            //been created, so create it, and add it to the
            //map of references
            hO = new org.psygrid.data.model.hibernate.Option();
            hRefs.put(this, hO);
            toHibernate(hO, hRefs);
        }
        
        return hO;
    }
    
    public void toHibernate(org.psygrid.data.model.hibernate.Option hO, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        super.toHibernate(hO, hRefs);
        hO.setCode(this.code);
        hO.setTextEntryAllowed(this.textEntryAllowed);
        List<org.psygrid.data.model.hibernate.OptionDependent> hODs = hO.getOptionDependents();
        for (int i=0; i<this.optionDependents.length; i++){
            OptionDependentDTO od = optionDependents[i];
            if ( null != od ){
                hODs.add(od.toHibernate(hRefs));
            }
        }
        if ( null != this.entry ){
            hO.setEntry(this.entry.toHibernate(hRefs));
        }
    }
}
