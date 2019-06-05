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

import java.util.Map;


/**
 * Class to represent a value of a response to an
 * option entry.
 * 
 * @author Rob Harper
 *
 */
public class OptionValueDTO extends ValueDTO {

    /**
     * The option value 
     */
    private Long valueId;
    
    /**
     * The string value that is completed by the user if the associated
     * option entry is editable, and the "Other" option is selected.
     */
    private String textValue;
    
    /**
     * Default no-arg constructor as required by Hibernate.
     */
    public OptionValueDTO(){};
    
    public Long getValueId(){
        return this.valueId;
    }
    
    /**
     * Set the value of the option value.
     * 
     * @param value The value.
     */
    public void setValueId(Long valueId){
        this.valueId = valueId;
    }
    
    public String getTextValue() {
        return textValue;
    }

    public void setTextValue(String editableValue) {
        this.textValue = editableValue;
    }

    public org.psygrid.data.model.hibernate.OptionValue toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        //check for an already existing instance of a hibernate object for this 
        //option value in the map of references
        org.psygrid.data.model.hibernate.OptionValue hOV = null;
        if ( hRefs.containsKey(this)){
            hOV = (org.psygrid.data.model.hibernate.OptionValue)hRefs.get(this);
        }
        if ( null == hOV ){
            //an instance of the dependent entry has not already
            //been created, so create it, and add it to the map 
            //of references
            hOV = new org.psygrid.data.model.hibernate.OptionValue();
            hRefs.put(this, hOV);
            toHibernate(hOV, hRefs);
        }

        return hOV;
    }
    
    public void toHibernate(org.psygrid.data.model.hibernate.OptionValue hOV, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        super.toHibernate(hOV, hRefs);
        hOV.setTextValue(this.textValue);
        if ( null != this.valueId ){
            hOV.setValueId(this.valueId);
        }
    }
    
}
