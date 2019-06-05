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

import java.util.HashMap;
import java.util.Map;


/**
 * Base class to represent any item in the hierarchical structure of 
 * a DataSet.
 * 
 * @author Rob Harper
 */
public abstract class ElementDTO extends ComponentDTO {
	protected ElementMetaDataDTO[] metaData = new ElementMetaDataDTO[0];
	
	/**
	 * The string form of the ExportSecurityValues enumeration
	 */
	protected String exportSecurityValue;
	
    /**
     * The data set that the element is a part of.
     */
    protected DataSetDTO myDataSet;
    
    /**
     * Invariant Globally Unique Identifier
     */
    protected String iguId;
    
    protected boolean isRevisionCandidate;
    
    protected boolean isEditable;
    
    protected boolean isHeadRevision;
    
    protected String elementSubmissionContext;
    
    protected String dataElementStatus;
    
    protected AuditLogDTO auditLog;
    
    private String elementPatchingAction;
    
    /**
     * Flag to indicate if the element has 
     * been changed since it was last saved
     */
    protected boolean isChanged;
    
    /**
     * The auto-incremented (on dataset save) version number
     */
    protected Integer autoVersionNo;
    
    
    /**
     * Indicates to the UI whether this item has been checked out and is allowed to be edited.
     * If not, it can only be browsed.
     */

    private ElementRelationshipDTO[] elementRelationships = new ElementRelationshipDTO[0];
    /**
	 * Default no-arg constructor, as required by the Hibernate framework
	 * for all persistable classes.
	 */
	public ElementDTO(){};
	public void setElementRelationships(ElementRelationshipDTO[] elemRelationships){
		this.elementRelationships = elemRelationships;
	}
	public ElementRelationshipDTO[] getElementRelationships(){
		return this.elementRelationships;
	}
	public void setIsEditable(boolean isEditable){
		this.isEditable = isEditable;
	}
	public boolean getIsEditable(){
		return isEditable;
	}
	
    /**
     * Get the data set that this element is a part of.
     * 
     * @return The data set.
     * 
     */
    public DataSetDTO getMyDataSet() {
        return myDataSet;
    }
    
    /**
     * Set the data set that this element is a part of.
     * 
     * @param myDataSet The data set.
     */
    public void setMyDataSet(DataSetDTO dataSet) {
        this.myDataSet = dataSet;
    }
    
	/**
	 * Get the invariant globally unique ID
	 * 
	 * @return The invariant globally unique ID
	 * 
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
	 * 
	 * @return returns the string-from of the ExportSecurityValues enumeration.
	 */
	public String getExportSecurityValue(){
		return exportSecurityValue;
	}
	
	/**
	 * 
	 * @param exportSecurityValue - this needs to be a string version of one of the values defined in ExportSecurityValues.
	 */
	public void setExportSecurityValue(String exportSecurityValue){
		this.exportSecurityValue = exportSecurityValue;
	}
	public ElementMetaDataDTO[] getMetaData() {
		return metaData;
	}
	public void setMetaData(ElementMetaDataDTO[] metaData) {
		this.metaData = metaData;
	}
	public org.psygrid.data.model.hibernate.Element toHibernate(){
        //create list to hold references to objects in the element's
        //object graph which have multiple references to them within
        //the object graph. This is used so that each object instance
        //is copied to its hibernate equivalent once and once only
        Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> dtoRefs = new HashMap<PersistentDTO, org.psygrid.data.model.hibernate.Persistent>();
        org.psygrid.data.model.hibernate.Element element = toHibernate(dtoRefs);
        dtoRefs = null;
        return element;
    }
    
    public abstract org.psygrid.data.model.hibernate.Element toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs);
    
    public void toHibernate(org.psygrid.data.model.hibernate.Element hE, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        super.toHibernate(hE, hRefs);
        hE.setIGUId(this.iguId);
        //must use the element property setter because the method is protected.
        hE.setIsEditable(isEditable);
        hE.setEnumStatus(this.getDataElementStatus());
        hE.setHeadRevision(this.isHeadRevision);
        hE.setEnumSubmissionContext(this.getElementSubmissionContext());
        hE.setRevisionCandidate(this.isRevisionCandidate);
        
        //set the audit log
        if (auditLog != null) {
            hE.setAuditLog(this.auditLog.toHibernate(hRefs));
        }
        
        hE.setEnumExportSecurity(this.exportSecurityValue); 
        
        hE.setElementPatchingAction(this.elementPatchingAction);
        
        //patching 
        hE.setChanged(this.isChanged);
        
        hE.setAutoVersionNo(this.autoVersionNo);
        
        if ( null != this.myDataSet && !getDelContext()){
            hE.setMyDataSet(this.myDataSet.toHibernate(hRefs));
        }
        
        if(this.metaData.length > 0){
        	for(int i = 0; i < metaData.length; i++){
        		hE.addMetaData((org.psygrid.data.model.hibernate.ElementMetaData)metaData[i].toHibernate(hRefs));
        	}
        }
    }
	public boolean getIsRevisionCandidate() {
		return isRevisionCandidate;
	}
	public void setIsRevisionCandidate(boolean isRevisionCandidate) {
		this.isRevisionCandidate = isRevisionCandidate;
	}
	public String getDataElementStatus() {
		return dataElementStatus;
	}
	public void setDataElementStatus(String dataElementStatus) {
		this.dataElementStatus = dataElementStatus;
	}
	public String getElementSubmissionContext() {
		return elementSubmissionContext;
	}
	public void setElementSubmissionContext(String elementSubmissionContext) {
		this.elementSubmissionContext = elementSubmissionContext;
	}
	public boolean getIsHeadRevision() {
		return isHeadRevision;
	}
	public void setIsHeadRevision(boolean isHeadRevision) {
		this.isHeadRevision = isHeadRevision;
	}

	public void setAuditLog(AuditLogDTO auditLog) {
		this.auditLog = auditLog;
	}
	
	public AuditLogDTO getAuditLog() {
		return this.auditLog;
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
	 * Set the auto-incrememted version number
	 * @param autoVersionNo the version number
	 * to set
	 */
	 public void setAutoVersionNo(Integer autoVersionNo) {
		this.autoVersionNo = autoVersionNo;
	 }
	
	/**
	 * Increment the auto version number
	 */
	public void incrementAutoVersionNo() {
		autoVersionNo++;
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
	
	public void setElementPatchingAction(String elementPatchingAction) {
		this.elementPatchingAction = elementPatchingAction;
	}
     
	public String getElementPatchingAction() {
		return elementPatchingAction;
	}
	
}
