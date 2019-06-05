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

import org.psygrid.data.export.security.ExportSecurityValues;

/**
 * Base class to represent any item in the hierarchical structure of 
 * a DataSet.
 * 
 * 
 * @author Rob Harper
 * 
 * @hibernate.joined-subclass table="t_elements"
 * @hibernate.joined-subclass-key column="c_id"
 */
public abstract class Element extends Component {

    /**
     * Reject all existing elements; force user to fill in
     * elements manually
     */
    public final static String REJECT_ALL_EXISTING_ELEMENTS = "Reject all existing elements";

    /**
     * Accept all existing elements  by filling in missing code for
     * missing elements
     */
    public final static String ACCEPT_ALL_EXISTING_ELEMENTS = "Accept all existing elements";

	protected List<ElementRelationship> elementRelationships = new ArrayList<ElementRelationship>();
	protected List<ElementMetaData> metaData = new ArrayList<ElementMetaData>();
	
	boolean isEditable = true;
	private boolean isHeadRevision;
	
	private boolean isChanged = false;
	
	private ElementSubmissionContext submissionContext;
	
	private DataElementStatus status; 
	
	private AuditLog auditLog;
	
	/**
     * the action to perform on existing entries when
     * an entry is added 
     * 
     * default is to accept all existing elements
	 * @hibernate.property column="c_element_patching_action"
     */
	private String elementPatchingAction = Element.ACCEPT_ALL_EXISTING_ELEMENTS;
	
	/**
	 * 	
	 */
	private boolean isRevisionCandidate;
	
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
	 * Contains the level of export security assigned to this element.
	 */
	protected ExportSecurityValues exportSecurityTag = null;
	
    /**
     * The data set that the element is a part of.
     */
    protected DataSet myDataSet;
    
    /**
     * The auto-incremented (on dataset save) version number;
     * initialise to 0
	 * @hibernate.property column="c_autoversion_no"
     */
    protected Integer autoVersionNo = new Integer(0);
    
    /**
     * Invariant Globally Unique Identifier
     */ 
    protected String iguId;
    
    /**
	 * Default no-arg constructor, as required by the Hibernate framework
	 * for all persistable classes.
	 */
	public Element(){};
	
	/**
	 * Constructor that accepts the type and name of the new element.
	 * 
	 * @param name The name of the element.
	 */
	public Element(String name){
		super(name);
	}
	
    /**
     * Constructor that accepts the type, name and display text of
     * the new element.
     * 
     * @param name The nElementame of the element.
     * @param displayText The display text of the element.
     */
    public Element(String name, String displayText){
        this.name = name;
        this.displayText = displayText;
    }
    
    /**
     * Get the data set that this element is a part of.
     * <p>
     * Hibernate note: the many-to-one relationship with DataSet should
     * formally have not-null="true". This had to be relaxed for the case
     * where the element <i>is</i> a data set, and the ensuing circular
     * relationship meant that the object could not be persisted.
     * 
     * @return The data set.
     * 
     * @dynamic_xdoclet_elementToDataSet@
     */
    public DataSet getMyDataSet() {
        return myDataSet;
    }

	/**
     * Get the data set that the element is a part of.
     * 
     * @return The data set.
     * 
     */
    public DataSet getDataSet(){
        return this.myDataSet;
    }
    
    
    /**
     * Set the data set that this element is a part of.
     * 
     * @param myDataSet The data set.
     */
    public void setMyDataSet(DataSet myDataSet) {
        this.myDataSet = myDataSet;
    }
    
	/**
	 * Get the invariant globally unique ID
	 * 
	 * @return The invariant globally unique ID
	 * 
	 * @hibernate.property column = "c_iguid"
	 */
	public String getIGUId() {
		return iguId;
	}

	/**
	 * Set the invariant globally unique ID
	 * 
	 * @param iguid The invariant globally unique ID
	 */
	public void setIGUId(String iguid) {
		this.iguId = iguid;
	}
	
	/**
     * Get the export security tag for the element.
     * 
     * @return the export security tag. NOTE: it is possible for this method to return null.
     * 
     * @hibernate.property column = "c_export_security"
     */
    public String getEnumExportSecurity(){
    	if(exportSecurityTag == null)
    		return null;
    	
    	return exportSecurityTag.toString();
    }
    
     /**
     * Set the export security tag for the element.
     * 
     * @param securityTag - the value to which this element will be assigned. This must be a string returned from the toString method of ExportSecurityValues.
     * 			Note that it is acceptable to pass a null value into this method.
     */
    public void setEnumExportSecurity(String securityTag) throws ModelException {
   		if(securityTag == null)
   			exportSecurityTag = null;
   		else{
   			try{
   	 			exportSecurityTag = ExportSecurityValues.valueOf(securityTag); 
   			}catch(IllegalArgumentException ex){
   				throw new ModelException("The argument passed into  Element.setExportSecurity - " + securityTag +  " - is outside the range of values defined in ExportSecurityValues.");
   			}
   		}
    }
    
    /**
     * Get the export security tag for the element. (from IElement)
     * 
     * @return the export security tag.
     */
    public ExportSecurityValues getExportSecurity(){
    	return exportSecurityTag;
    }
    
    /**
     * Set the export security tag for the element. (from IElement)
     * 
     * @param security - the value to which this element will be assigned
     */
    public void setExportSecurity(ExportSecurityValues security){
    	exportSecurityTag = security;
    }
    
    
    public abstract org.psygrid.data.model.dto.ElementDTO toDTO();
    
    @Override
	public abstract org.psygrid.data.model.dto.ElementDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth);
    
    public void toDTO(org.psygrid.data.model.dto.ElementDTO dtoE, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        super.toDTO(dtoE, dtoRefs, depth);
        
        dtoE.setIsEditable(this.isEditable);
        dtoE.setDataElementStatus(this.getEnumStatus());
        dtoE.setIsHeadRevision(this.getHeadRevision());
        dtoE.setElementSubmissionContext(this.getEnumSubmissionContext());
        dtoE.setIsRevisionCandidate(isRevisionCandidate);
        
        //set the audit log
        if (auditLog != null) {
            dtoE.setAuditLog(((org.psygrid.data.model.hibernate.AuditLog)auditLog).toDTO(dtoRefs, depth));
        }
        
        dtoE.setElementPatchingAction(elementPatchingAction);
        
        //patching
        dtoE.setAutoVersionNo(autoVersionNo);
        dtoE.setChanged(isChanged);
        
        if(exportSecurityTag != null){
            dtoE.setExportSecurityValue(exportSecurityTag.toString());
        } //The element is not guaranteed to have an export security tag.

        //Set the metadata
        if( null != this.metaData && !this.metaData.isEmpty()){
        	org.psygrid.data.model.dto.ElementMetaDataDTO[] dtoArray = new org.psygrid.data.model.dto.ElementMetaDataDTO[metaData.size()];
        	for(int i = 0; i < metaData.size(); i++){
        		dtoArray[i] = (org.psygrid.data.model.dto.ElementMetaDataDTO)metaData.get(i).toDTO(dtoRefs, depth);
        	}

        	dtoE.setMetaData(dtoArray);
        }
 
        dtoE.setIGUId(this.iguId);
        if ( RetrieveDepth.RS_COMPLETE != depth &&  
                RetrieveDepth.RS_NO_BINARY != depth &&
                RetrieveDepth.RS_SUMMARY != depth ){
        
            if ( null != this.myDataSet ){
                dtoE.setMyDataSet(this.myDataSet.toDTO(dtoRefs, depth));
            }
        }
    }
 
    protected abstract void addChildTasks(DataSet ds);
    
    /**
     * This is provided in the event that HQL query retrieval is too slow and it is preferable
     * desirable to manually populate a small subset of the dto from a SQL-style query.
     * This may be used when retrieving object summary info, when only small subset of the
     * number of possible fields need to be populated.
     * @return - Element
     * 
     * @DEL_REP_ELEMENT_TO_METADATA_TAG@
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
     
    public abstract org.psygrid.data.model.dto.ElementDTO instantiateDTO();
	
	public boolean getIsEditable(){
		return isEditable;
	}
	
	/**
	 * This is set by the element library to true if the returned element is in a state
	 * suitable for revising and then re-submission.
	 * @param isEditable
	 */
	public void setIsEditable(boolean isEditable){
		this.isEditable = isEditable;
	}

	
	/**
	 * Specifies whether the lsid is represents the head revision for the object.
	 * Subsequent pending revisions will cause the item to not be the head revision.
	 * @return - whether or not the item is the head revision.
	 * 
	 * @DEL_REP_ELEMENT_TO_HEADREV_TAG@
	 */
	public boolean getHeadRevision() {
		return isHeadRevision;
	}

	public void setHeadRevision(boolean isHeadRevision) {
		this.isHeadRevision = isHeadRevision;
	}
	
	
    /**
     * Get the status associated with this lsid (i.e. PENDING, APPROVED, etc.)
     * 
     * @return The status of the element with this lsid
     * 
     * @DEL_REP_ELEMENT_TO_STATUS_TAG@
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
	
	
    /**
     * Get the submission context of this element.
     * This is recorded because subordinate pending elements
     * cannot be viewed or downloaded individually. They must be
     * viewed/downloaded within the context of their original submission.
     * 
     * @return The status of the element with this lsid
     * 
     * @DEL_REP_ELEMENT_TO_SUBMISSIONCONTEXT_TAG@
     */
	public String getEnumSubmissionContext() {
		if (null == submissionContext){
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
	
	public ElementSubmissionContext getSubmissionContext(){
		return submissionContext;
	}
	
	public void setSubmissionContext(ElementSubmissionContext context) {
		this.submissionContext = context;
	}
	
	public boolean isRevisionCandidate() {
		return isRevisionCandidate;
	}
	public void setRevisionCandidate(boolean isRevisionCandidate) {
		this.isRevisionCandidate = isRevisionCandidate;
	}
	public boolean getIsRevisionCandidate() {
		return isRevisionCandidate;
	}
	public void setIsRevisionCandidate(boolean isRevisionCandidate) {
		this.isRevisionCandidate = isRevisionCandidate;
	}
	
	/**
     * Get the audit log
     * 
	 * @return the audit log for this element
	 * 
	 * @hibernate.many-to-one class="org.psygrid.data.model.hibernate.AuditLog"
	 *                        column="c_audit_log_id"
	 *                        not-null="false"
	 *                        unique="true"
	 *                        cascade="all"                   
	 */
	public AuditLog getAuditLog() {
		return auditLog;
	}
	
	/**
	 * Set the audit log for this element 
	 * @param IAuditLog the audit log for this element
	 */
	public void setAuditLog(AuditLog auditLog) {
		this.auditLog = auditLog;
	}
	
	/**
	 * Return the action that determines
	 * how to handle patched elements 
	 * 
	 * @return the patching action
	 */
	public String getElementPatchingAction() {
		return this.elementPatchingAction;
	}
	
	/**
	 * Set the action that determines who to handle 
	 * the patched elements
	 * 
	 * @param elementPatchingAction the patching action
	 */
	public void setElementPatchingAction(String elementPatchingAction) {
		this.elementPatchingAction = elementPatchingAction;
	}
	
	/**
	 * Get the auto-incremented version number 
	 * associated with this element
	 * @return the version number
	 */
	public Integer getAutoVersionNo() {
		return autoVersionNo;
	}
	
	
	/**
	 * Set the auto-incremented version number
	 * associated with this element
	 * @param autoVersionNo the version number 
	 */
	public void setAutoVersionNo(Integer autoVersionNo) {
		this.autoVersionNo = autoVersionNo;
	}

	/**
	 * Increment the auto version number
	 */
	public void incrementAutoVersionNo() {
		this.autoVersionNo++;
	}
	
	/**
	 * Flag to indicate if the element has changed
	 * since it was last changed
	 * @return true if element has been changed since  
	 * it was last saved; false if not
	 */
	public boolean isChanged() {
		return isChanged;
	}
	
	/**
	 * Set the changed flag on the element to show
	 * that the element has changed
	 * 
	 * @param isChanged true if element has changed; false if not
	 */
	public void setChanged(boolean isChanged) {
		this.isChanged = isChanged;
	}

}
