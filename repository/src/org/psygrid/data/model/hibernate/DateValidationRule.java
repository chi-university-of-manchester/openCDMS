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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Class to represent a validation rule for validating date/time
 * inputs.
 * <p>
 * <p>
 * A date validation rule is able to perform validation of the input
 * date in two ways. Absolute validation valdiates the input date 
 * against a specified date. Relative validation validates the input
 * date against a point in time relative to the date when validation
 * occurs.
 * <p>
 * If the input to the rule is a string, the rule will first check
 * that the string can be parsed into a valid date. The date
 * being validated is then tested for compliance with the absoulte 
 * and relative, lower and upper limits.
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_date_val_rules"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class DateValidationRule extends ValidationRule {
    
    /**
     * Absolute lower limit for permitted date values.
     * <p>
     * If the input to the validation rule is before this date
     * then validation will fail.
     */
    private Date absLowerLimit;
    
    /**
     * Absolute upper limit for permitted date values.
     * <p>
     * If the input to the validation rule is after this date
     * then validation will fail.
     */
    private Date absUpperLimit;
    
    /**
     * Relative lower limit for permitted date values.
     * <p>
     * The value of this property is used in combination with the units to
     * calculate a date relative to the current date. Validation will fail if
     * the input is before this date.
     * <p>
     * For example, to prevent a date being entered that is less than two weeks 
     * ago a relative lower limit of -14 Days would be used (i.e. today's date 
     * minus 14 days).
     */
    private Integer relLowerLimit;
    
    /**
     * Relative upper limit for permitted date values.
     * <p>
     * The value of this property is used in combination with the units to
     * calculate a date relative to the current date. Validation will fail if
     * the input is after this date.
     * <p>
     * For example, to prevent a date being entered that is more than two weeks 
     * in the future a relative upper limit of 14 Days would be used (i.e. today's date 
     * plus 14 days).
     */
    private Integer relUpperLimit;
    
    /**
     * Units of relative lower limit.
     */
    private TimeUnits relLowerLimitUnits;
    
    /**
     * Units of relative upper limit.
     */
    private TimeUnits relUpperLimitUnits;
    
    public List<String> validate(Object arg) throws ModelException {
        List<String> messages = new ArrayList<String>();
        Date value = null;
        if ( arg instanceof Date ){
            value = (Date)arg;
        }
        else{
            throw new ModelException("Input is not of a valid type to undergo date validation");
        }
        
        //this is the date that all relative validation will be performed against
        Date now = new Date();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy");
        
        if ( null != this.absLowerLimit && value.before(this.absLowerLimit) ){
            if ( null != this.message ){
                messages.add(this.message);
            }
            messages.add("The entered date is earlier than the earliest permitted date of '"+
                         dateFormatter.format(this.absLowerLimit)+
                         "'. Please enter a date equal to or later than this.");
        }
        
        if ( null != this.absUpperLimit && value.after(this.absUpperLimit) ){
            if ( null != this.message ){
                messages.add(this.message);
            }
            messages.add("The entered date is after the latest permitted date of '"+
                         dateFormatter.format(this.absUpperLimit)+
                         "'. Please enter a date earlier than or equal to this.");
        }
        
        if ( null != this.relLowerLimit ){
            //check that units have been specified
            if ( null == this.relLowerLimitUnits ){
                throw new ModelException("No units have been specified for the relative lower limit");
            }
            Calendar cal = Calendar.getInstance();
            cal.setTime(now);
            cal.add(getCalendarUnit(this.relLowerLimitUnits), this.relLowerLimit);
            Date lowerLimit = removeTimeComponents(cal.getTime());
            if ( removeTimeComponents(value).before(lowerLimit)){
                if ( null != this.message ){
                    messages.add(this.message);
                }
                messages.add("The entered date is earlier than the earliest permitted date of '"+
                             dateFormatter.format(lowerLimit)+
                             "'. Please enter a date equal to or later than this.");
            }
        }
        
        if ( null != this.relUpperLimit ){
            //check that units have been specified
            if ( null == this.relUpperLimitUnits ){
                throw new ModelException("No units have been specified for the relative upper limit");
            }
            Calendar cal = Calendar.getInstance();
            cal.setTime(now);
            cal.add(getCalendarUnit(this.relUpperLimitUnits), this.relUpperLimit);
            Date upperLimit = removeTimeComponents(cal.getTime());
            if ( removeTimeComponents(value).after(upperLimit)){
                if ( null != this.message ){
                    messages.add(this.message);
                }
                messages.add("The entered date is after the latest permitted date of '"+
                             dateFormatter.format(upperLimit)+
                             "'. Please enter a date earlier than or equal to this.");
            }
        }
        
        return messages;

    }

    /**
     * Get the absolute lower limit that an input being validated 
     * must be greater than or equal to in order for validation to 
     * pass.
     * <p>
     * If <code>null</code>, no absolute lower limit is in force.
     * 
     * @return The absolute lower limit.
     * @hibernate.property column="c_abs_lower_limit"
     */
    public Date getAbsLowerLimit() {
        return this.absLowerLimit;
    }

    /**
     * Set the absolute lower limit that an input being validated 
     * must be greater than or equal to in order for validation to 
     * pass.
     * <p>
     * If <code>null</code>, no absolute lower limit is in force.
     * 
     * @param absLowerLimit The absolute lower limit.
     */
    public void setAbsLowerLimit(Date relLowerLimitUnits) {
        this.absLowerLimit = relLowerLimitUnits;
    }

    /**
     * Get the absolute upper limit that an input being validated must be
     * less than or equal to in order for validation to pass.
     * <p>
     * If <code>null</code>, no absolute upper limit is in force.
     * 
     * @return The absolute upper limit.
     * @hibernate.property column="c_abs_upper_limit"
     */
    public Date getAbsUpperLimit() {
        return this.absUpperLimit;
    }

    /**
     * Set the absolute upper limit that an input being validated must be
     * less than or equal to in order for validation to pass.
     * <p>
     * If <code>null</code>, no absolute upper limit is in force.
     * 
     * @param absUpperLimit The absolute upper limit.
     */
    public void setAbsUpperLimit(Date absUpperLimit) {
        this.absUpperLimit = absUpperLimit;
    }
    
    /**
     * Get the relative lower limit that, when combined with the relative
     * lower limit units and the current system date, an input must be
     * greater than or equal to in order for validation to pass
     * <p>
     * If <code>null</code>, no relative lower limit is in force.
     * 
     * @return The relative lower limit.
     * @hibernate.property column="c_rel_lower_limit"
     */
    public Integer getRelLowerLimit() {
        return relLowerLimit;
    }

    /**
     * Get the units of the relative lower limit.
     * 
     * @return The units of the relative lower limit.
     */
    public TimeUnits getRelLowerLimitUnits() {
        return relLowerLimitUnits;
    }

    /**
     * Get the relative upper limit that, when combined with the relative
     * lower limit units and the current system date, an input must be
     * less than or equal to in order for validation to pass
     * <p>
     * If <code>null</code>, no relative upper limit is in force.
     * 
     * @return The relative upper limit.
     * @hibernate.property column="c_rel_upper_limit"
     */
    public Integer getRelUpperLimit() {
        return relUpperLimit;
    }

    /**
     * Get the units of the relative upper limit.
     * 
     * @return The units of the relative upper limit.
     */
    public TimeUnits getRelUpperLimitUnits() {
        return relUpperLimitUnits;
    }

    /**
     * Set the relative lower limit that, when combined with the relative
     * lower limit units and the current system date, an input must be
     * greater than or equal to in order for validation to pass
     * <p>
     * If <code>null</code>, no relative lower limit is in force.
     * 
     * @param relUpperLimit The relative lower limit.
     */
    public void setRelLowerLimit(Integer relLowerLimit) {
        this.relLowerLimit = relLowerLimit;
    }

    /**
     * Set the units of the relative lower limit.
     * 
     * @param relLowerLimitUnits The units of the relative lower limit.
     */
    public void setRelLowerLimitUnits(TimeUnits relLowerLimitUnits) {
        this.relLowerLimitUnits = relLowerLimitUnits;
    }

    /**
     * Set the relative upper limit that, when combined with the relative
     * lower limit units and the current system date, an input must be
     * less than or equal to in order for validation to pass
     * <p>
     * If <code>null</code>, no relative upper limit is in force.
     * 
     * @param relLowerLimitUnits The relative upper limit.
     */
    public void setRelUpperLimit(Integer relUpperLimit) {
        this.relUpperLimit = relUpperLimit;
    }

    /**
     * Set the units of the relative upper limit.
     * 
     * @param relUpperLimitUnits The units of the relative upper limit.
     */
    public void setRelUpperLimitUnits(TimeUnits relUpperLimitUnits) {
        this.relUpperLimitUnits = relUpperLimitUnits;
    }

    /**
     * Get the string value of the enumerated time units for the
     * relative lower limit.
     * <p>
     * Only used internally by Hibernate for persistence.
     * 
     * @return String representation of the enumerated time units.
     * 
     * @hibernate.property column="c_rel_lower_units"
     */
    protected String getEnumRelLowerUnits(){
        if ( null == relLowerLimitUnits ){
            return null;
        }
        else{
            return relLowerLimitUnits.toString();
        }
    }
    
    /**
     * Set the string value of the enumerated time units for the
     * relative lower limit.
     * <p>
     * Only used internally by Hibernate for persistence.
     * 
     * @param enumTimeUnits String representation of the 
     * enumerated time units.
     */
    protected void setEnumRelLowerUnits(String enumTimeUnits){
        if ( null == enumTimeUnits ){
            relLowerLimitUnits = null;
        }
        else{
            relLowerLimitUnits = TimeUnits.valueOf(enumTimeUnits);
        }
    }

    /**
     * Get the string value of the enumerated time units for the
     * relative upper limit.
     * <p>
     * Only used internally by Hibernate for persistence.
     * 
     * @return String representation of the enumerated time units.
     * 
     * @hibernate.property column="c_rel_upper_units"
     */
    protected String getEnumRelUpperUnits(){
        if ( null == relUpperLimitUnits ){
            return null;
        }
        else{
            return relUpperLimitUnits.toString();
        }
    }
    
    /**
     * Set the string value of the enumerated time units for the
     * relative upper limit.
     * <p>
     * Only used internally by Hibernate for persistence.
     * 
     * @param enumTimeUnits String representation of the 
     * enumerated time units.
     */
    protected void setEnumRelUpperUnits(String enumTimeUnits){
        if ( null == enumTimeUnits ){
            relUpperLimitUnits = null;
        }
        else{
            relUpperLimitUnits = TimeUnits.valueOf(enumTimeUnits);
        }
    }

    public org.psygrid.data.model.dto.DateValidationRuleDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        //check for an already existing instance of a dto object for this 
        //date validation rule in the map of references
        org.psygrid.data.model.dto.DateValidationRuleDTO dtoDVR = null;
        if ( dtoRefs.containsKey(this)){
            dtoDVR = (org.psygrid.data.model.dto.DateValidationRuleDTO)dtoRefs.get(this);
        }
        else{
            //an instance of the date validation rule has not already
            //been created, so create it, and add it to the
            //map of references
            dtoDVR = new org.psygrid.data.model.dto.DateValidationRuleDTO();
            dtoRefs.put(this, dtoDVR);
            toDTO(dtoDVR, dtoRefs, depth);
        }
        
        return dtoDVR;
    }
    
    public void toDTO(org.psygrid.data.model.dto.DateValidationRuleDTO dtoDVR, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        super.toDTO(dtoDVR, dtoRefs, depth);
        dtoDVR.setAbsLowerLimit(this.absLowerLimit);
        dtoDVR.setAbsUpperLimit(this.absUpperLimit);
        dtoDVR.setRelLowerLimit(this.relLowerLimit);
        dtoDVR.setRelUpperLimit(this.relUpperLimit);
        if ( null != this.relLowerLimitUnits ){
            dtoDVR.setRelLowerLimitUnits(this.relLowerLimitUnits.toString());
        }
        if ( null != this.relUpperLimitUnits ){
            dtoDVR.setRelUpperLimitUnits(this.relUpperLimitUnits.toString());
        }
    }
    
    public org.psygrid.data.model.dto.ValidationRuleDTO toDTO(){
        Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
        org.psygrid.data.model.dto.ValidationRuleDTO rule = toDTO(dtoRefs, RetrieveDepth.DS_COMPLETE);
        dtoRefs = null;
        return rule;
    }
    
    /**
     * Get the equivalent Calendar constant value for a given time unit.
     * 
     * @param timeUnit The time unit.
     * @return The Calendar constant.
     */
    private int getCalendarUnit(TimeUnits timeUnit){
        int unit = 0;
        if ( TimeUnits.DAYS == timeUnit){
            unit = Calendar.DAY_OF_MONTH;
        }
        else if ( TimeUnits.WEEKS == timeUnit){
            unit = Calendar.WEEK_OF_YEAR;
        }
        else if ( TimeUnits.MONTHS == timeUnit){
            unit = Calendar.MONTH;
        }
        else if ( TimeUnits.YEARS == timeUnit){
            unit = Calendar.YEAR;
        }
        return unit;
    }
    
    /**
     * Change the precision of a date so that it is suitable to be used
     * in a relative validation with the given time units.
     * <ul>
     * <li>If validation is against a number of years, months, weeks or days
     * then all of the time elements of the date are set to zero.</li>
     * <li>If validation is against a number of hours then set the minute,
     * second and millisecond elements to zero.</li>
     * <li>If validation is against a number of minutes then set the second
     * and millisecond elements to zero.</li>
     * </ul>
     * 
     * @param date The date whose precision is to be changed.
     * @return The date with modified precision.
     */
    private Date removeTimeComponents(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.clear(Calendar.MILLISECOND);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.HOUR);
        cal.clear(Calendar.HOUR_OF_DAY);
        cal.clear(Calendar.AM_PM);
        return cal.getTime();
    }
    
    public boolean isEquivalentTo(ValidationRule comparisonRule) {
    	if(!(comparisonRule instanceof DateValidationRule)){
    		return false;
    	}
    	
    	DateValidationRule rule = (DateValidationRule)comparisonRule;
    	
    	if(!super.isEquivalentTo(comparisonRule)){
    		return false;
    	}
    	
    	if(relLowerLimitUnits == null){
    		if(rule.relLowerLimitUnits != null){
    			return false;
    		}
    	}else if(!relLowerLimitUnits.equals(rule.relLowerLimitUnits)){
    		return false;
    	}
    	
    	if(absUpperLimit == null){
    		if(rule.absUpperLimit != null){
    			return false;
    		}
    	}else if(!absUpperLimit.equals(rule.absUpperLimit)){
    		return false;
    	}
    	
    	if(relLowerLimit == null){
    		if(rule.relLowerLimit != null){
    			return false;
    		}
    	}else if(!relLowerLimit.equals(rule.relLowerLimit)){
    		return false;
    	}
    	
    	if(relUpperLimit == null){
    		if(rule.relUpperLimit != null){
    			return false;
    		}
    	}else if(!relUpperLimit.equals(rule.relUpperLimit)){
    		return false;
    	}
    	
    	if(relLowerLimitUnits == null){
    		if(rule.relLowerLimitUnits != null){
    			return false;
    		}
    	}else if(!relLowerLimitUnits.equals(rule.relLowerLimitUnits)){
    		return false;
    	}

    	
    	if(relUpperLimitUnits == null){
    		if(rule.relUpperLimitUnits != null){
    			return false;
    		}
    	}else if(!relUpperLimitUnits.equals(rule.relUpperLimitUnits)){
    		return false;
    	}
    	   	
    	return true;
    }

	@Override
	public org.psygrid.data.model.dto.ValidationRuleDTO instantiateDTO() {
		return new org.psygrid.data.model.dto.DateValidationRuleDTO();
	}


}
