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
import java.util.List;
import java.util.Map;

import org.psygrid.data.model.ITestable;


/**
 * Class to represent an abstract base validation rule.
 * <p>
 * Sub-classes of this interface represent specific validation
 * rules.
 * 
 * @author Rob Harper
 * 
 * @hibernate.joined-subclass table="t_val_rules"
 * @hibernate.joined-subclass-key column="c_id"
 */
public abstract class ValidationRule extends Persistent implements ITestable {

	/**
	 * Name of the validation rule.
	 */
	private String name;
	
    /**
     * Description of the validation rule.
     */
    private String description;
    
    /**
     * Message that will be displayed to the user if validation
     * fails.
     */
    protected String message;
    
    protected ElementSubmissionContext submissionContext;
   
	
	/**
	 * Used purely by the dataset designer UI to track whether the element
	 * is a candidate for revision in the data element library (i.e whether
	 * it's been edited).
	 */
	private boolean isRevisionCandidate;
    
	protected LSID lsid = null;
	
	protected LSID instanceLSID = null;
	
	protected List<ElementRelationship> elementRelationships = new ArrayList<ElementRelationship>();
	protected List<ElementMetaData> metaData = new ArrayList<ElementMetaData>();
	
	private boolean isHeadRevision;
	private DataElementStatus status; 
	
	private SingleVariableTest test;
    
    /**
     * List of associated validation rules, the results of which will be
     * "OR"ed with this one to give the overall validation result.
     */
    protected List<ValidationRule> associatedRules = new ArrayList<ValidationRule>();
    
    protected boolean isEditable = true;
    
    public abstract List<String> validate(Object arg) throws ModelException;
    

    /**
     * Use the validation rule to validate an input value.
     * <p>
     * If the input passes validation then the return value will
     * be an empty list. Otherwise, the return value will
     * be a list containing an item for each line of the error
     * message.
     * 
     * @param arg The input value to validate.
     * @return Empty list if validation passes, list of error
     * messages if validation fails.
     * @throws ModelException if the validation rule is not 
     * correctly configured.
     */
    public List<String> validateAll(Object arg) throws ModelException{
        //validate with this rule
        List<String> thisValResult = this.validate(arg);
        if ( 0 == thisValResult.size() ){
            //if this validation passes we can return, as we only need one of the
            //validation rules to pass for validation to pass overall
            return thisValResult;
        }
        //validate with the associated rules
        for ( ValidationRule rule: this.associatedRules ){
            List<String> valResult = rule.validate(arg);
            if ( 0 == valResult.size() ){
                //if this validation passes we can return, as we only need one of the
                //validation rules to pass for validation to pass overall
                return valResult;
            }
            else{
                thisValResult.addAll(valResult);
            }
        }
        //if execution reaches this point the none of the validation rules have passed,
        //which means that overall validation has failed.
        if ( this.associatedRules.size() > 0 && null != this.message ){
            //If there are associated validation rules, AND there is a user defined message
            //defined for this rule, instead of returning the 
            //automatically generated failure messages from all of the individual validation 
            //rules (which doesn't make much sense when displayed) we just return the user
            //defined message for this rule.
            List<String> result = new ArrayList<String>();
            result.add(this.message);
            return result;
        }
        else{
            return thisValResult;
        }
    }

    /**
     * Get the description of the validation rule.
     * 
     * @return The description.
     * @hibernate.property column="c_description"
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Set the description of the validation rule.
     * 
     * @param description The description.
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * @hibernate.property column="c_name"
     */
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the message that will be displayed to the user if validation
     * fails.
     * 
     * @return The message.
     * @hibernate.property column="c_message"
     */
    public String getMessage() {
        return message;
    }

    /**
     * Set the message that will be displayed to the user if validation
     * fails.
     * 
     * @param message The message.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Get the list of associated validation rules, the results of which will be
     * "OR"ed with this one to give the overall validation result.
     * 
     * @return The list of associated validation rules.
     * 
     * @hibernate.list cascade="save-update" 
     *                 table="t_rule_assocrules" batch-size="100"
     * @hibernate.key column="c_rule_id"
     * @hibernate.many-to-many class="org.psygrid.data.model.hibernate.ValidationRule"
     *                         column="c_assoc_rule_id"
     * @hibernate.list-index column="c_index"
     */
    public List<ValidationRule> getAssociatedRules() {
        return associatedRules;
    }

    /**
     * Set the list of associated validation rules, the results of which will be
     * "OR"ed with this one to give the overall validation result.
     * 
     * @param associatedRules The list of associated validation rules.
     */
    public void setAssociatedRules(List<ValidationRule> associatedRules) {
        this.associatedRules = associatedRules;
    }

    /**
     * Add a new associated validation rule to the valdiation rule's collection
     * of associated validation rules.
     * 
     * @param associatedRule The associated validation rule to add.
     * @throws ModelException If the associated validation rule is null.
     */
    public void addAssociatedRule(ValidationRule associatedRule) throws ModelException {
        if ( null == associatedRule ){
            throw new ModelException("Cannot add a null associated validation rule.");
        }
        this.associatedRules.add((ValidationRule)associatedRule);
    }
    
    public abstract org.psygrid.data.model.dto.ValidationRuleDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth);
    
    public abstract org.psygrid.data.model.dto.ValidationRuleDTO toDTO();
    
    public void toDTO(org.psygrid.data.model.dto.ValidationRuleDTO dtoR, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        super.toDTO(dtoR, dtoRefs, depth);
        dtoR.setName(this.name);
        dtoR.setDescription(this.description);
        dtoR.setMessage(this.message);
        dtoR.setIsEditable(isEditable);
        dtoR.setElementStatus(getEnumStatus());
        dtoR.setIsHeadRevision(isHeadRevision);
        dtoR.setSubmisssionContext(getEnumSubmissionContext());
        dtoR.setIsRevisionCandidate(isRevisionCandidate);
        
        if(test != null){
            dtoR.setTest(test.toDTO(dtoRefs, depth));
        }
        
        
        //Set the metadata
        if( null != this.metaData && !this.metaData.isEmpty()){
        	org.psygrid.data.model.dto.ElementMetaDataDTO[] dtoArray = new org.psygrid.data.model.dto.ElementMetaDataDTO[metaData.size()];
        	for(int i = 0; i < metaData.size(); i++){
        		dtoArray[i] = (org.psygrid.data.model.dto.ElementMetaDataDTO)metaData.get(i).toDTO(dtoRefs, depth);
        	}

        	dtoR.setMetaData(dtoArray);
        }
 
        
        if(this.lsid != null){
            dtoR.setLSID(this.lsid.toDTO(dtoRefs, depth));
        }
        
        if(this.instanceLSID != null){
        	dtoR.setInstanceLSID(this.instanceLSID.toDTO(dtoRefs, depth));
        }
        
        org.psygrid.data.model.dto.ValidationRuleDTO[] dtoAssocRules = new org.psygrid.data.model.dto.ValidationRuleDTO[this.associatedRules.size()];
        for (int i=0; i<this.associatedRules.size(); i++){
            ValidationRule rule = associatedRules.get(i);
            dtoAssocRules[i] = rule.toDTO(dtoRefs, depth);
        }        
        dtoR.setAssociatedRules(dtoAssocRules);
        
    }
    
    public boolean isEquivalentTo(ValidationRule comparisonRule) {
    	
    	//Compare description
    	if(description == null) {
    		if(comparisonRule.description != null) {
    			return false;
    		}
    	}else if (!description.equals(comparisonRule.description)){
    		return false;
    	}
    	
    	//Compare message
    	if(message == null) {
    		if(comparisonRule.message != null) {
    			return false;
    		}
    	}else if (!message.equals(comparisonRule.message)){
    		return false;
    	}
    	
    	return true;
    }

	public boolean getIsRevisionCandidate(){
		return isRevisionCandidate;
	}
	
	/**
	 * Used purely by the dataset designer UI to track whether the element
	 * is a candidate for revising in the data element library.
	 * @param value
	 */
	public void setIsRevisionCandidate(boolean value){
		isRevisionCandidate = value;
	}

    /**
     * @hibernate.many-to-one class="org.psygrid.data.model.hibernate.LSID"
     * 						  cascade="all"
     * 						  column="c_lsid_id"
     * 						  not-null="false"
     */    
	public LSID getLSID(){
		return this.lsid;
	}
	public void setLSID(LSID lsid){
		this.lsid = lsid;
	}
	
	/**
	 * @hibernate.many-to-one class="org.psygrid.data.model.hibernate.LSID"
	 * 						cascade="all"
	 * 						column="c_lsid_instance_id"
	 * 						not-null="false"
	 */
	public LSID getInstanceLSID(){
		return this.instanceLSID;
	}
	public void setInstanceLSID(LSID lsid){
		this.instanceLSID = lsid;
	}
	
	public void addRelatedElement(ElementRelationship elemRelationship){
		elementRelationships.add(elemRelationship);
	}
	/**
	 * 
	 @dynamic_xdoclet_elementRelationship@
	 */
 	public List<ElementRelationship> getElementRelationships(){
		return elementRelationships;
	}
	public void setElementRelationships(List<ElementRelationship> elementRelationships){
		this.elementRelationships = elementRelationships;
	}
	
	/**
	 * @DEL_REP_VALIDATIONRULE_TO_METADATA_TAG@
	 */
	public List<ElementMetaData> getMetaData() {
		return metaData;
	}
	
	public void setMetaData(List<ElementMetaData> metaData){
		this.metaData = metaData;
	}
	
	
	/**
	 * It's the latest metadata object that will have the ElementHistoryItem on it.
	 * That's why it gets its own access method.
	 * @return
	 */
	public ElementMetaData getLatestMetaData(){
		ElementMetaData retObj = null;
		if (!metaData.isEmpty()){
			retObj = metaData.get(metaData.size()-1);
		}
		return retObj;
	}
	
	public void addMetaData(ElementMetaData metaData) {
		this.metaData.add(metaData);
	}
	
	/**
	 * Specifies whether the lsid is represents the head revision for the object.
	 * Subsequent pending revisions will cause the item to not be the head revision.
	 * @return - whether or not the item is the head revision.
	 * 
	 * @DEL_REP_VALIDATIONRULE_HEADREV_TAG@
	 * 
	 */
	public boolean getHeadRevision() {
		return isHeadRevision;
	}

	public void setHeadRevision(boolean isHeadRevision) {
		this.isHeadRevision = isHeadRevision;
	}
	
	
    /**
     * 
     * Get the status associated with this lsid (i.e. PENDING, APPROVED, etc.)
     * 
     * @return The status of the element with this lsid
     * 
     * @DEL_REP_VALIDATIONRULE_STATUS_TAG@
     * 
     */
	public String getEnumStatus() {
		if( null == status){
			return null;
		}else{
			return status.toString();
		}	
	}

	public void setEnumStatus(String status) {
		if(null == status){
			this.status = null;
		}else{
			this.status = DataElementStatus.valueOf(status);
		}
	}
	
	public DataElementStatus getStatus(){		
		return status;
	}
	
	public boolean hasTest() {
		return (test != null);
	}
	
	public boolean runTest() {
		return test.test(this);
	}
	
	public void resetTest() {
		if(test != null){
			test.resetTest();
		}
	}

	/**
	* @DEL_REP_VALIDATIONRULE_TEST_TAG@
	* 
	*/
	public SingleVariableTest getTest() {
		return test;
	}

	
	public void setTest(SingleVariableTest test) {
		this.test = test;
	}

	public boolean getIsEditable() {
		return isEditable;
	}

	public void setIsEditable(boolean isEditable) {
		this.isEditable = isEditable;
	}

	public ElementSubmissionContext getSubmissionContext() {
		return submissionContext;
	}
	
	
    /**
     * 
     * Get the submission context associated with this validation rule.
     * 
     * @return Submission context.
     * 
     * @DEL_REP_VALIDATIONRULE_SUBMISSION_CONTEXT_TAG@
     * 
     */
	public String getEnumSubmissionContext(){
		if( null == submissionContext){
			return null;
		}else{
			return submissionContext.toString();
		}		
	}
	
	public void setEnumSubmissionContext(String submissionContext){
		if(null == submissionContext){
			this.submissionContext = null;
		}else{
			this.submissionContext = ElementSubmissionContext.valueOf(submissionContext);
		}
	}
	
	public abstract org.psygrid.data.model.dto.ValidationRuleDTO instantiateDTO();

}
