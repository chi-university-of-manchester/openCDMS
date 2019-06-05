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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Class to represent a validation rule for validating integer
 * inputs.
 * <p>
 * If the input to the rule is a string, the rule will first check
 * that the string can be parsed into a valid integer. The integer
 * being validated is then tested for compliance with the lower and 
 * upper limits.
 * 
 * @author Rob Harper
 * 
 * @hibernate.joined-subclass table="t_int_val_rules"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class IntegerValidationRule extends ValidationRule {

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
    
    public List<String> validate(Object arg) {
        List<String> messages = new ArrayList<String>();
        Integer value = null;
        if (arg instanceof Number){
            value = new Integer(((Number)arg).intValue());
        }
        else if (arg instanceof String){
            try{
                value = new Integer((String)arg);
            }
            catch(NumberFormatException ex){
                if ( null != this.message ){
                    messages.add(this.message);
                }
                messages.add("The entered value is not a valid integer.");
            }
        }
        else{
            throw new ModelException("Input is not of a valid type to undergo integer validation");
        }
        
        if ( 0 == messages.size() ){
            //the argument is a valid number, so now it may be
            //validated against the defined limits and precision.
            
            if ( null != this.lowerLimit ){
            	if ( this.lowerLte ){
            		if ( value.compareTo(this.lowerLimit) <= 0 ){
                        if ( null != this.message ){
                            messages.add(this.message);
                        }
                        messages.add("The entered value is less than or equal to the permitted lower limit of "+
                                     this.lowerLimit+
                                     ". Please enter a value greater than "+
                                     this.lowerLimit+".");
            		}            		
            	}
            	else{
            		if ( value.compareTo(this.lowerLimit) < 0 ){
                        if ( null != this.message ){
                            messages.add(this.message);
                        }
                        messages.add("The entered value is less than the permitted lower limit of "+
                                     this.lowerLimit+
                                     ". Please enter a value equal to or greater than "+
                                     this.lowerLimit+".");
            		}
            	}
            }
            if ( null != this.upperLimit ){
            	if ( this.upperGte ){
            		if ( value.compareTo(this.upperLimit) >= 0 ){
                        if ( null != this.message ){
                            messages.add(this.message);
                        }
                        messages.add("The entered value is greater than or equals to the permitted upper limit of "+
                                     this.upperLimit+
                                     ". Please enter a value less than "+
                                     this.upperLimit+".");            			
            		}
            	}
            	else{
            		if ( value.compareTo(this.upperLimit) > 0 ){
                        if ( null != this.message ){
                            messages.add(this.message);
                        }
                        messages.add("The entered value is greater than the permitted upper limit of "+
                                     this.upperLimit+
                                     ". Please enter a value equal to or less than "+
                                     this.upperLimit+".");            			
            		}
            	}
            	
            }
        }
        
        return messages;

    }

    /**
     * Get the lower limit that an input being validated must be
     * greater than or equal to in order for validation to pass.
     * <p>
     * If <code>null</code>, no lower limit is in force.
     * 
     * @return The lower limit.
     * @hibernate.property column="c_lower_limit"
     */
    public Integer getLowerLimit() {
        return this.lowerLimit;
    }

    /**
     * Set the lower limit that an input being validated must be
     * greater than or equal to in order for validation to pass.
     * <p>
     * If <code>null</code>, no lower limit is in force.
     * 
     * @param lowerLimit The lower limit.
     */
    public void setLowerLimit(Integer lowerLimit) {
        this.lowerLimit = lowerLimit;
    }

    /**
     * Get the upper limit that an input being validated must be
     * less than or equal to in order for validation to pass.
     * <p>
     * If <code>null</code>, no upper limit is in force.
     * 
     * @return The upper limit.
     * @hibernate.property column="c_upper_limit
     */
    public Integer getUpperLimit() {
        return this.upperLimit;
    }

    /**
     * Set the upper limit that an input being validated must be
     * less than or equal to in order for validation to pass.
     * <p>
     * If <code>null</code>, no upper limit is in force.
     * 
     * @param upperLimit The upper limit.
     */
    public void setUpperLimit(Integer upperLimit) {
        this.upperLimit = upperLimit;
    }

    /**
     * Get the lower limit comparison type.
     * <p>
     * If True, validation fails if value being validated is less than
     * or equal to the lower limit. If False, validation fails if value 
     * less than the lower limit.
     * 
     * @return Lower limit comparison type
     * @hibernate.property column="c_lower_lte"
     */
    public boolean isLowerLte() {
		return lowerLte;
	}

    /**
     * Set the lower limit comparison type.
     * <p>
     * If True, validation fails if value being validated is less than
     * or equal to the lower limit. If False, validation fails if value 
     * less than the lower limit.
     * 
     * @param lowerLte Lower limit comparison type
     */
	public void setLowerLte(boolean lowerLte) {
		this.lowerLte = lowerLte;
	}

	/**
	 * Get the upper limit comparison type.
     * <p>
     * If True, validation fails if value being validated is greater than
     * or equal to the upper limit. If False, validation fails if value 
     * greater than the upper limit.
     * 
	 * @return Upper limit comparison type.
	 * @hibernate.property column="c_upper_gte"
	 */
	public boolean isUpperGte() {
		return upperGte;
	}

	/**
	 * Set the upper limit comparison type.
     * <p>
     * If True, validation fails if value being validated is greater than
     * or equal to the upper limit. If False, validation fails if value 
     * greater than the upper limit.
     * 
	 * @param upperGte Upper limit comparison type.
	 */
	public void setUpperGte(boolean upperGte) {
		this.upperGte = upperGte;
	}

    public org.psygrid.data.model.dto.IntegerValidationRuleDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        //check for an already existing instance of a dto object for this 
        //integer validation rule in the map of references
        org.psygrid.data.model.dto.IntegerValidationRuleDTO dtoIVR = null;
        if ( dtoRefs.containsKey(this)){
            dtoIVR = (org.psygrid.data.model.dto.IntegerValidationRuleDTO)dtoRefs.get(this);
        }
        else{
            //an instance of the integer validation rule has not already
            //been created, so create it, and add it to the
            //map of references
            dtoIVR = new org.psygrid.data.model.dto.IntegerValidationRuleDTO();
            dtoRefs.put(this, dtoIVR);
            toDTO(dtoIVR, dtoRefs, depth);
        }
        
        return dtoIVR;
    }
    
    public void toDTO(org.psygrid.data.model.dto.IntegerValidationRuleDTO dtoIVR, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        super.toDTO(dtoIVR, dtoRefs, depth);
        dtoIVR.setLowerLimit(this.lowerLimit);
        dtoIVR.setUpperLimit(this.upperLimit);
        dtoIVR.setLowerLte(this.lowerLte);
        dtoIVR.setUpperGte(this.upperGte);
    }
    
    public boolean isEquivalentTo(ValidationRule comparisonRule){
    	if(!(comparisonRule instanceof IntegerValidationRule)){
    		return false;
    	}
    	
    	if(!super.isEquivalentTo(comparisonRule)){
    		return false;
    	}
    	
    	IntegerValidationRule rule = (IntegerValidationRule) comparisonRule;
    	
    	if(lowerLimit == null){
    		if(rule.lowerLimit != null){
    			return false;
    		}
    	}else if(!lowerLimit.equals(rule.lowerLimit)){
    		return false;
    	}
    	
    	if ( lowerLte != rule.lowerLte ){
    		return false;
    	}
    	
    	if(upperLimit == null){
    		if(rule.upperLimit != null){
    			return false;
    		}
    	}else if(!upperLimit.equals(rule.upperLimit)){
    		return false;
    	}
    	
    	if ( upperGte != rule.upperGte ){
    		return false;
    	}
    	    	
    	return true;
    }

    public org.psygrid.data.model.dto.ValidationRuleDTO toDTO(){
        Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
        org.psygrid.data.model.dto.ValidationRuleDTO rule = toDTO(dtoRefs, RetrieveDepth.DS_COMPLETE);
        dtoRefs = null;
        return rule;
    }

	@Override
	public org.psygrid.data.model.dto.ValidationRuleDTO instantiateDTO() {
		return new org.psygrid.data.model.dto.IntegerValidationRuleDTO();
	}

}
