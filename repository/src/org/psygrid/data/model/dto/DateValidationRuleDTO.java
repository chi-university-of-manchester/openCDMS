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

import java.util.Date;
import java.util.Map;

import org.psygrid.data.model.hibernate.TimeUnits;

/**
 * Class to represent a validation rule for validating date/time
 * inputs.
 * <p>
 * If the input to the rule is a string, the rule will first check
 * that the string can be parsed into a valid date. The date
 * being validated is then tested for compliance with the lower and 
 * upper limits.
 * 
 * @author Rob Harper
 */
public class DateValidationRuleDTO extends ValidationRuleDTO {

    /**
     * Absolute lower limit for permitted date values.
     * <p>
     * If the input to the validation rule is before the lower limit
     * then validation will fail.
     */
    private Date absLowerLimit;
    
    /**
     * Absolute upper limit for permitted date values.
     * <p>
     * If the input to the validation rule is after the upper limit
     * then validation will fail.
     */
    private Date absUpperLimit;
    
    private Integer relLowerLimit;
    
    private Integer relUpperLimit;
    
    private String relLowerLimitUnits;
    
    private String relUpperLimitUnits;
    
    public DateValidationRuleDTO(){};
    
    public Date getAbsLowerLimit() {
        return this.absLowerLimit;
    }

    public void setAbsLowerLimit(Date lowerLimit) {
        this.absLowerLimit = lowerLimit;
    }

    public Date getAbsUpperLimit() {
        return this.absUpperLimit;
    }

    public void setAbsUpperLimit(Date upperLimit) {
        this.absUpperLimit = upperLimit;
    }

    public Integer getRelLowerLimit() {
        return relLowerLimit;
    }

    public void setRelLowerLimit(Integer relLowerLimit) {
        this.relLowerLimit = relLowerLimit;
    }

    public String getRelLowerLimitUnits() {
        return relLowerLimitUnits;
    }

    public void setRelLowerLimitUnits(String relLowerLimitUnits) {
        this.relLowerLimitUnits = relLowerLimitUnits;
    }

    public Integer getRelUpperLimit() {
        return relUpperLimit;
    }

    public void setRelUpperLimit(Integer relUpperLimit) {
        this.relUpperLimit = relUpperLimit;
    }

    public String getRelUpperLimitUnits() {
        return relUpperLimitUnits;
    }

    public void setRelUpperLimitUnits(String relUpperLimitUnits) {
        this.relUpperLimitUnits = relUpperLimitUnits;
    }

    public org.psygrid.data.model.hibernate.DateValidationRule toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        //check for an already existing instance of a hibernate object for this 
        //date validation rule in the map of references
        org.psygrid.data.model.hibernate.DateValidationRule hIVR = null;
        if ( hRefs.containsKey(this)){
            hIVR = (org.psygrid.data.model.hibernate.DateValidationRule)hRefs.get(this);
        }
        else{
            //an instance of the date validation rule has not already
            //been created, so create it, and add it to the 
            //map of references
            hIVR = new org.psygrid.data.model.hibernate.DateValidationRule();
            hRefs.put(this, hIVR);
            toHibernate(hIVR, hRefs);
        }
        
        return hIVR;
    }
    
    public void toHibernate(org.psygrid.data.model.hibernate.DateValidationRule hDVR, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        super.toHibernate(hDVR, hRefs);
        hDVR.setAbsLowerLimit(this.absLowerLimit);
        hDVR.setAbsUpperLimit(this.absUpperLimit);
        hDVR.setRelLowerLimit(this.relLowerLimit);
        hDVR.setRelUpperLimit(this.relUpperLimit);
        if ( null != this.relLowerLimitUnits ){
            hDVR.setRelLowerLimitUnits(TimeUnits.valueOf(this.relLowerLimitUnits));
        }
        if ( null != this.relUpperLimitUnits ){
            hDVR.setRelUpperLimitUnits(TimeUnits.valueOf(this.relUpperLimitUnits));
        }
    }
    
}
