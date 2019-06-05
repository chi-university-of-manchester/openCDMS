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
 * Class to represent an Entry whose value is selected from a defined 
 * list of Options.
 * 
 * @author Rob Harper
 * 
 */
public class OptionEntryDTO extends BasicEntryDTO {

    /**
     * The collection of options from which a value may 
     * be selected.
     */
    private OptionDTO[] options = new OptionDTO[0];
    
    /**
     * The option that is the default value for the option entry.
     */
    private OptionDTO defaultValue;

    /**
     * Flag to indicate whether a client should display option codes 
     * or not.
     */
    private boolean optionCodesDisplayed;
    
    /**
     * Flag to indicate whether a client should display options
     * as drop-selector or not.
     */
    private boolean dropDownDisplay;
    
    
    /**
     * Default no-arg constructor, as required by the Hibernate framwework
     * for all persistable classes.
     */
    public OptionEntryDTO(){};
    
    public OptionDTO getDefaultValue() {
        return defaultValue;
    }

    /**
     * Set the option that is the default value for the option
     * entry.
     * 
     * @param defaultValue The default option.
     */
    public void setDefaultValue(OptionDTO defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * Get the collection of options from which a value may be 
     * selected.
     * 
     * @return The collection of options.
     * 
     */
    public OptionDTO[] getOptions() {
        return options;
    }

    /**
     * Set the collection of options from which a value may be 
     * selected.
     * 
     * @param options The collection of options.
     */
    public void setOptions(OptionDTO[] options) {
        this.options = options;
    }

    public boolean isOptionCodesDisplayed() {
        return optionCodesDisplayed;
    }

    public void setOptionCodesDisplayed(boolean optionCodesDisplayed) {
        this.optionCodesDisplayed = optionCodesDisplayed;
    }
    
    public boolean isDropDownDisplay() {
    	return dropDownDisplay;
    }
    
    public void setDropDownDisplay(boolean dropDownDisplay) {
    	this.dropDownDisplay = dropDownDisplay;
    }

    public org.psygrid.data.model.hibernate.OptionEntry toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        //check for an already existing instance of a hibernate object for this 
        //option entry in the map of references
        org.psygrid.data.model.hibernate.OptionEntry hOE = null;
        if ( hRefs.containsKey(this)){
            hOE = (org.psygrid.data.model.hibernate.OptionEntry)hRefs.get(this);
        }
        if ( null == hOE ){
            //an instance of the option entry has not already
            //been created, so create it, and add it to the 
            //map of references
            hOE = new org.psygrid.data.model.hibernate.OptionEntry();
            hRefs.put(this, hOE);
            toHibernate(hOE, hRefs);
        }
        
        return hOE;
    }
    
    public void toHibernate(org.psygrid.data.model.hibernate.OptionEntry hOE, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        super.toHibernate(hOE, hRefs);

        if ( null != this.defaultValue ){
            hOE.setDefaultValue(this.defaultValue.toHibernate(hRefs));
        }
        
        List<org.psygrid.data.model.hibernate.Option> hOptions = hOE.getOptions();
        for (int i=0; i<this.options.length; i++){
            OptionDTO o = options[i];
            if ( null != o ){
                hOptions.add(o.toHibernate(hRefs));
            }
        }
        
        hOE.setOptionCodesDisplayed(this.optionCodesDisplayed);
        hOE.setDropDownDisplay(this.dropDownDisplay);
    }
    
}
