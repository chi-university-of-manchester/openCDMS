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
 * Class to represent a validation rule for validating integer
 * inputs.
 * 
 * @author Rob Harper
 * 
 */
public class IntegerValidationRuleDTO extends ValidationRuleDTO {

    /**
     * Lower limit for allowed integer values.
     * <p>
     * If the value being validated is less than the lower limit
     * validation fails.
     */
    private Integer lowerLimit;
    
    /**
     * Lower limit comparison type.
     * <p>
     * If True, validation fails if value being validated is less than
     * or equal to the lower limit. If False, validation fails if value 
     * less than the lower limit.
     */
    private boolean lowerLte;
    
    /**
     * Upper limit for allowed integer values.
     * <p>
     * If the value being validated is greater than the upper
     * limit validation fails.
     */
    private Integer upperLimit;
    
    /**
     * Upper limit comparison type.
     * <p>
     * If True, validation fails if value being validated is greater than
     * or equal to the upper limit. If False, validation fails if value 
     * greater than the upper limit.
     */
    private boolean upperGte;
    
    public IntegerValidationRuleDTO(){};
    
    public Integer getLowerLimit() {
        return this.lowerLimit;
    }

    public void setLowerLimit(Integer lowerLimit) {
        this.lowerLimit = lowerLimit;
    }

    public Integer getUpperLimit() {
        return this.upperLimit;
    }

    public void setUpperLimit(Integer upperLimit) {
        this.upperLimit = upperLimit;
    }

    public boolean isLowerLte() {
		return lowerLte;
	}

	public void setLowerLte(boolean lowerLte) {
		this.lowerLte = lowerLte;
	}

	public boolean isUpperGte() {
		return upperGte;
	}

	public void setUpperGte(boolean upperGte) {
		this.upperGte = upperGte;
	}

	public org.psygrid.data.model.hibernate.IntegerValidationRule toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        //check for an already existing instance of a hibernate object for this 
        //integer validation rule in the map of references
        org.psygrid.data.model.hibernate.IntegerValidationRule hIVR = null;
        if ( hRefs.containsKey(this)){
            hIVR = (org.psygrid.data.model.hibernate.IntegerValidationRule)hRefs.get(this);
        }
        else{
            //an instance of the integer valdiation rule has not already
            //been created, so create it, and add it to the 
            //map of references
            hIVR = new org.psygrid.data.model.hibernate.IntegerValidationRule();
            hRefs.put(this, hIVR);
            toHibernate(hIVR, hRefs);
        }
        
        return hIVR;
    }
    
    public void toHibernate(org.psygrid.data.model.hibernate.IntegerValidationRule hIVR, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        super.toHibernate(hIVR, hRefs);
        hIVR.setLowerLimit(this.lowerLimit);
        hIVR.setUpperLimit(this.upperLimit);
        hIVR.setLowerLte(this.lowerLte);
        hIVR.setUpperGte(this.upperGte);
    }
    
}
