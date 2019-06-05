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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Class to represent a validation rule for validating text
 * inputs.
 * <p>
 * Validation of a text value comprises one or more of the following:
 * <ul>
 * <li>Validating that the input has more characters than a defined
 * lower limit.</li>
 * <li>Validating that the input has less characters than a defined
 * upper limit.</li>
 * <li>Validating that the input matches a defined regular expression
 * pattern.
 * </li>
 * </ul> 
 * 
 * @author Rob Harper
 * 
 * @hibernate.joined-subclass table="t_text_val_rules"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class TextValidationRule extends ValidationRule {
	
    /**
     * Message returned from {@link IValidationRule#validate(Object)} if
     * the length of the input is lower than getLowerLimit()
     */
    public static final String LOWER_LIMIT_FAILURE_MESSAGE = "Input contains" +
            " fewer characters than the defined lower limit";
    
    /**
     * Message returned from {@link IValidationRule#validate(Object)} if the
     * length of the input is higher than getHigherLimit()
     */
    public static final String UPPER_LIMIT_FAILURE_MESSAGE = "Input contains" +
            " more characters than the defined upper limit";
    
    /**
     * Message returned from {@link IValidationRule#validate(Object)} if the
     * input does not match the regular expression pattern
     */
    public static final String NO_REGULAR_EXPRESSION_MATCH_MESSAGE = "Input" +
            " does not match the defined regular expression pattern";
    
    /**
     * Message returned from {@link IValidationRule#validate(Object)} if the
     * regular expression cannot be compiled
     */
    public static final String REGULAR_EXPRESSION_NOT_COMPILED_MESSAGE = "The" +
            " defined regular expression pattern cannot be compiled";


    /**
     * The lower limit on the allowed number of characters in the input.
     * <p>
     * If the input has fewer characters than the lower limit then 
     * validation fails.
     */
    private Integer lowerLimit;

    /**
     * The upper limit on the allowed number of characters in the input.
     * <p>
     * If the input has more characters than the upper limit then 
     * validation fails.
     */
    private Integer upperLimit;
    
    /**
     * The regular expression pattern that the input is validated against.
     */
    private String pattern;
    
    /**
     * Additional message to be displayed if the value fails regular
     * expression validation.
     * <p>
     * Should contain details of the required format to pass validation
     * as an aid to the end-user.
     */
    private String patternDetails;
    
    public List<String> validate(Object arg) throws ModelException {
        List<String> messages = new ArrayList<String>();
        String value = null;
        if ( arg instanceof String ){
            value = (String)arg;
        }
        else{
            throw new ModelException("Input is not of a valid type to undergo text validation");
        }
        
        if ( null != this.lowerLimit && value.length() < this.lowerLimit ){
            if ( null != this.message ){
                messages.add(this.message);
            }
            messages.add("The entered value contains fewer characters than the permitted lower limit of "+
                         this.lowerLimit+
                         ". Please enter a value containing at least "+
                         this.lowerLimit+
                         " characters.");
        }
        else if ( null != this.upperLimit && value.length() > this.upperLimit ){
            if ( null != this.message ){
                messages.add(this.message);
            }
            messages.add("The entered value contains more characters than the permitted upper limit of "+
                         this.upperLimit+
                         ". Please enter a value containing no more than "+
                         this.upperLimit+
                         " characters.");
        }
        else if ( null != this.pattern && !this.pattern.equals("") ){
            try{
                Pattern p = Pattern.compile(this.pattern);
                Matcher m = p.matcher(value);
                if ( !m.matches() ){
                    if ( null != this.message ){
                        messages.add(this.message);
                    }
                    messages.add("The entered value does not conform to the required format.");
                    if ( null != this.patternDetails ){
                        messages.add(this.patternDetails);
                    }
                }
            }
            catch(PatternSyntaxException ex){
                throw new ModelException("The defined reqular expression pattern cannot be compiled");
            }
        }
        
        return messages;

    }

    /**
     * Get the lower limit on the number of characters that the input 
     * being validated must have in order for validation to pass.
     * <p>
     * If <code>null</code>, no lower limit on the number of characters
     * is in force.
     * 
     * @return The lower limit on the number of characters.
     * @hibernate.property column="c_lower_limit"
     */
    public Integer getLowerLimit() {
        return this.lowerLimit;
    }

    /**
     * Set the lower limit on the number of characters that the input 
     * being validated must have in order for validation to pass.
     * <p>
     * If <code>null</code>, no lower limit on the number of characters
     * is in force.
     * 
     * @param lowerLimit The lower limit on the number of characters.
     */
    public void setLowerLimit(Integer lowerLimit) {
        this.lowerLimit = lowerLimit;
    }

    /**
     * Get the upper limit on the number of characters that the input 
     * being validated must have in order for validation to pass.
     * <p>
     * If <code>null</code>, no upper limit on the number of characters
     * is in force.
     * 
     * @return The upper limit on the number of characters.
     * @hibernate.property column="c_upper_limit"
     */
    public Integer getUpperLimit() {
        return this.upperLimit;
    }

    /**
     * Set the upper limit on the number of characters that the input 
     * being validated must have in order for validation to pass.
     * <p>
     * If <code>null</code>, no upper limit on the number of characters
     * is in force.
     * 
     * @param upperLimit The upper limit on the number of characters.
     */
    public void setUpperLimit(Integer upperLimit) {
        this.upperLimit = upperLimit;
    }

    /**
     * Get the regular expression pattern that the input must satisfy
     * in order for validation to pass.
     * <p>
     * If <code>null</code>, no regular expression pattern is in force.
     * 
     * @return The regular expression pattern.
     * @hibernate.property column="c_pattern"
     */
    public String getPattern() {
        return this.pattern;
    }

    /**
     * Set the regular expression pattern that the input must satisfy
     * in order for validation to pass.
     * <p>
     * If <code>null</code>, no regular expression pattern is in force.
     * 
     * @param pattern The regular expression pattern.
     */
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    /**
     * Get the additional message to be displayed if the value fails regular
     * expression validation.
     * <p>
     * Should contain details of the required format to pass validation
     * as an aid to the end-user.
     * 
     * @return The additional message.
     * @hibernate.property column="c_pattern_details"
     */
    public String getPatternDetails() {
        return patternDetails;
    }
    
    /**
     * Set the additional message to be displayed if the value fails regular
     * expression validation.
     * <p>
     * Should contain details of the required format to pass validation
     * as an aid to the end-user.
     * 
     * @param patternDetails The additional message.
     */
    public void setPatternDetails(String patternDetails) {
        this.patternDetails = patternDetails;
    }

    public org.psygrid.data.model.dto.TextValidationRuleDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        //check for an already existing instance of a dto object for this 
        //text validation rule in the map of references
        org.psygrid.data.model.dto.TextValidationRuleDTO dtoTVR = null;
        if ( dtoRefs.containsKey(this)){
            dtoTVR = (org.psygrid.data.model.dto.TextValidationRuleDTO)dtoRefs.get(this);
        }
        else{
            //an instance of the text validation rule has not already
            //been created, so create it, and add it to the
            //map of references
            dtoTVR = new org.psygrid.data.model.dto.TextValidationRuleDTO();
            dtoRefs.put(this, dtoTVR);
            toDTO(dtoTVR, dtoRefs, depth);
        }
        
        return dtoTVR;
    }
    
    public void toDTO(org.psygrid.data.model.dto.TextValidationRuleDTO dtoTVR, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        super.toDTO(dtoTVR, dtoRefs, depth);
        dtoTVR.setLowerLimit(this.lowerLimit);
        dtoTVR.setUpperLimit(this.upperLimit);
        dtoTVR.setPattern(this.pattern);
        dtoTVR.setPatternDetails(this.patternDetails);
    }
    
    public boolean isEquivalentTo(ValidationRule comparisonRule){
    	if(!(comparisonRule instanceof TextValidationRule)){
    		return false;
    	}
    	
    	if(!super.isEquivalentTo(comparisonRule)){
    		return false;
    	}
    	
    	TextValidationRule rule = (TextValidationRule) comparisonRule;
    	
    	if(lowerLimit == null){
    		if(rule.lowerLimit != null){
    			return false;
    		}
    	}else if(!lowerLimit.equals(rule.lowerLimit)){
    		return false;
    	}
    	
    	if(upperLimit == null){
    		if(rule.upperLimit != null){
    			return false;
    		}
    	}else if(!upperLimit.equals(rule.upperLimit)){
    		return false;
    	}
    	
    	if(pattern == null){
    		if(rule.pattern != null){
    			return false;
    		}
    	}else if(!pattern.equals(rule.pattern)){
    		return false;
    	}
    	
    	if(patternDetails == null){
    		if(rule.patternDetails != null){
    			return false;
    		}
    	}else if(!patternDetails.equals(rule.patternDetails)){
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
		return new org.psygrid.data.model.dto.TextValidationRuleDTO();
	}
}
