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
 * Class to represent a validation rule for validating numeric
 * inputs.
 * <p>
 * If the input to the rule is a string, the rule will first check
 * that the string can be parsed into a valid number. The number
 * being validated is then tested for compliance with the lower and 
 * upper limits.
 * 
 * @author Rob Harper
 * 
 */
public class NumericValidationRuleDTO extends ValidationRuleDTO {

    /**
     * Lower limit for allowed numeric values.
     * <p>
     * If the value being validated is less than the lower limit
     * validation fails.
     */
    private Double lowerLimit;
    
    /**
     * Lower limit comparison type.
     * <p>
     * If True, validation fails if value being validated is less than
     * or equal to the lower limit. If False, validation fails if value 
     * less than the lower limit.
     */
    private boolean lowerLte;
    
    /**
     * Upper limit for allowed numeric values.
     * <p>
     * If the value being validated is greater than the upper
     * limit validation fails.
     */
    private Double upperLimit;
    
    /**
     * Upper limit comparison type.
     * <p>
     * If True, validation fails if value being validated is greater than
     * or equal to the upper limit. If False, validation fails if value 
     * greater than the upper limit.
     */
    private boolean upperGte;
    
    public NumericValidationRuleDTO(){};
    
    public Double getLowerLimit() {
        return this.lowerLimit;
    }

    public void setLowerLimit(Double lowerLimit) {
        this.lowerLimit = lowerLimit;
    }

    public Double getUpperLimit() {
        return this.upperLimit;
    }

    public void setUpperLimit(Double upperLimit) {
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

	public org.psygrid.data.model.hibernate.NumericValidationRule toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        //check for an already existing instance of a hibernate object for this 
        //numeric validation rule in the map of references
        org.psygrid.data.model.hibernate.NumericValidationRule hIVR = null;
        if ( hRefs.containsKey(this)){
            hIVR = (org.psygrid.data.model.hibernate.NumericValidationRule)hRefs.get(this);
        }
        else{
            //an instance of the numeric validation rule has not already
            //been created, so create it, and add it to the 
            //map of references
            hIVR = new org.psygrid.data.model.hibernate.NumericValidationRule();
            hRefs.put(this, hIVR);
            toHibernate(hIVR, hRefs);
        }
        
        return hIVR;
    }
    
    public void toHibernate(org.psygrid.data.model.hibernate.NumericValidationRule hNVR, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        super.toHibernate(hNVR, hRefs);
        hNVR.setLowerLimit(this.lowerLimit);
        hNVR.setUpperLimit(this.upperLimit);
        hNVR.setLowerLte(this.lowerLte);
        hNVR.setUpperGte(this.upperGte);
    }
    
}
