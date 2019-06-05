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
 */
public class TextValidationRuleDTO extends ValidationRuleDTO {

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

    public TextValidationRuleDTO(){};
    
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

    public String getPattern() {
        return this.pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getPatternDetails() {
        return patternDetails;
    }

    public void setPatternDetails(String patternDetails) {
        this.patternDetails = patternDetails;
    }

    public org.psygrid.data.model.hibernate.TextValidationRule toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        //check for an already existing instance of a hibernate object for this 
        //text validation rule in the map of references
        org.psygrid.data.model.hibernate.TextValidationRule hIVR = null;
        if ( hRefs.containsKey(this)){
            hIVR = (org.psygrid.data.model.hibernate.TextValidationRule)hRefs.get(this);
        }
        else{
            //an instance of the text validation rule has not already
            //been created, so create it, and add it to the 
            //map of references
            hIVR = new org.psygrid.data.model.hibernate.TextValidationRule();
            hRefs.put(this, hIVR);
            toHibernate(hIVR, hRefs);
        }
        
        return hIVR;
    }
    
    public void toHibernate(org.psygrid.data.model.hibernate.TextValidationRule hTVR, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        super.toHibernate(hTVR, hRefs);
        hTVR.setLowerLimit(this.lowerLimit);
        hTVR.setUpperLimit(this.upperLimit);
        hTVR.setPattern(this.pattern);
        hTVR.setPatternDetails(this.patternDetails);
    }
}
