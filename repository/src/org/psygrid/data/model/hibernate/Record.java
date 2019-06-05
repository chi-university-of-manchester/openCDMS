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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.psygrid.common.identifier.InvalidIdentifierException;

/**
 * A record contains data for a DataSet.
 * 
 * @author Rob Harper
 * 
 * @hibernate.joined-subclass table="t_records"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class Record extends StatusedInstance {

    private static final String DATASET_PROPERTY = "dataSet";
    private static final String CONSENTS_PROPERTY = "consents";
    
    /**
     * The dataset that this record represents an instance of.
     */
    private DataSet dataSet;

    private Long dataSetId;
    
    /**
     * The collection of document instances that are contained by the record. 
     */
    private Set<DocumentInstance> docInstances = new HashSet<DocumentInstance>();
    
    /**
     * The Set of Consent objects that are the responses to ConsentForms
     * belonging to the Element that this ElementInstance is related to.
     */
    protected Set<Consent> consents = new HashSet<Consent>();
    
    /**
     * Identifier of the record.
     */
    protected Identifier identifier;
    
    /**
     * Site of the record.
     */
    protected Site site;
    
    private Long siteId;
    
    /**
     * Date to use as the zero-point for scheduling.
     */
    @Deprecated
    private Date scheduleStartDate;
    
    /**
     * Date of entry into the study - used for UKCRN reports.
     */
    @Deprecated
    private Date studyEntryDate;
    
    /**
     * The consultant in charge of this record.
     * 
     * The consultant should be based at the site
     * belonging to this record and there can be 
     * one or more consultants per site.
     * 
     * Required by the UKCRN reports.
     */
    private String consultant;
    
    //TODO remove these properties
    //Need to leave these properties here so that XStream can deserialize
    //records saved when these fields were still used. Marked as transient
    //so that XStream will not serialize in the future. Should be OK to 
    //remove them after a month or two.
    /**
     * @deprecated
     */
    transient private int numIncompleteDocs = 0;
    /**
     * @deprecated
     */
    transient private int numPendingDocs = 0;
    /**
     * @deprecated
     */
    transient private int numRejectedDocs = 0;
    /**
     * @deprecated
     */
    transient private int numApprovedDocs = 0;
    
    /**
     * The date when the consent for the record was last modified.
     */
    private Date consentModified;
    
    /**
     * The date when the status of the record was last modified.
     */
    private Date statusModified;
        
    /**
     * List to store objects to be deleted when the record is saved.
     */
    private List<Persistent> deletedObjects = new ArrayList<Persistent>();
    
    /**
     * If the Record is involved in dual data entry as the secondary record (i.e.
     * data will be propagated from the primary record to this one) then this
     * property contains the identifier string of the primary record. Otherwise
     * <code>null</code>.
     */
    private String primaryIdentifier;
    
    /**
     * If the Record is involved in dual data entry as the primary record (i.e.
     * data will be propagated from this record to the secondary record) then this
     * property contains the identifier string of the secondary record. Otherwise
     * <code>null</code>.
     */
    private String secondaryIdentifier;
    
    /**
     * The Record object equivalent to the Record's secondaryIdentifier.
     * <p>
     * This property is not intended to be persisted and is only used to store
     * the reference to the secondary record for the convenience of clients.
     */
    private Record secondaryRecord;
    
    /**
     * If True then the record has been "deleted" and so should not feature in
     * reports etc.
     */
    private boolean deleted;
    
    /**
     * Data about the record.
     * <p>
     * Stored in a separate object so that a provenance history may be maintained.
     */
    private RecordData theRecordData;
    
    /**
     * Externally generated identifier for the record.
     */
    private String externalIdentifier;
    
	/**
	 * If this is set to True thon the external id is to be used shown throughout the openCDMS
	 * system as the primary identifier. The openCDMS native identifier, in this case will not be visible to the user at all.
	 */
    private boolean useExternalIdAsPrimary;
    
	/**
	 * Default no-arg constructor, as required by the Hibernate framework
	 * for all persistable classes.
	 */
	public Record(){
        super();
	    this.record = this;
    }

    /**
     * Get the date to use as the zero-point for scheduling.
     * 
     * @return The scheduling zero-point date.
     */
    public Date getScheduleStartDate() {
    	if ( null != scheduleStartDate ){
    		return scheduleStartDate;
    	}
        if ( null != this.theRecordData ){
        	return theRecordData.getScheduleStartDate();
        }
        return null;
    }

    /**
     * Set the date to use as the zero-point for scheduling.
     * 
     * @param scheduleStartDate The scheduling zero-point date.
     */
    @Deprecated
    public void setScheduleStartDate(Date scheduleStartDate) {
        this.scheduleStartDate = scheduleStartDate;
    }

    /**
     * Get the date of entry into the study - used for UKCRN reports.
     * 
     * @return The date of entry into the study.
     */
    public Date getStudyEntryDate() {
    	if ( null != studyEntryDate ){
    		return studyEntryDate;
    	}
        if ( null != this.theRecordData ){
        	return theRecordData.getStudyEntryDate();
        }
        return null;
	}

    /**
     * Set the date of entry into the study - used for UKCRN reports.
     * 
     * @param studyEntryDate The date of entry into the study.
     */
    @Deprecated
	public void setStudyEntryDate(Date studyEntryDate) {
		this.studyEntryDate = studyEntryDate;
	}

	/*
    public Date getScheduleStartDate() {
        if ( null != this.theRecordData ){
        	return theRecordData.getScheduleStartDate();
        }
        return null;
    }

    public Date getStudyEntryDate() {
        if ( null != this.theRecordData ){
        	return theRecordData.getStudyEntryDate();
        }
        return null;
	}
	*/

    /**
     * Get the textual notes on the record.
     * 
     * @return The notes.
     */
    public String getNotes(){
        if ( null != this.theRecordData ){
        	return theRecordData.getNotes();
        }
        return null;
    }
    
    /**
     * Get the dataset that this record represents an instance of.
     * 
     * @return The dataset,
     * @hibernate.many-to-one class="org.psygrid.data.model.hibernate.DataSet"
     *                        column="c_dataset_id"
     *                        not-null="true"
     *                        cascade="none"
     */
    public DataSet getDataSet() {
        return dataSet;
    }

    public void setDataSet(DataSet dataSet) {
        DataSet oldDataSet = this.dataSet;
        this.dataSet = dataSet;
        propertyChangeSupport.firePropertyChange(DATASET_PROPERTY, oldDataSet,
                this.dataSet);
    }

    public Long getDataSetId() {
        return dataSetId;
    }

    public void setDataSetId(Long dataSetId) {
        this.dataSetId = dataSetId;
    }

    /**
     * Get the set of Consent objects that are the responses to ConsentForms
     * belonging to the Element that this ElementInstance is related to.
     * 
     * @return The Set of Consent objects
     * 
     * @hibernate.set cascade="all"
     * @hibernate.one-to-many class="org.psygrid.data.model.hibernate.Consent"
     * @hibernate.key column="c_elem_inst_id"
     *                not-null="false"
     */
    public Set<Consent> getConsents() {
        return consents;
    }
    
    /**
     * Retrieve a set containing all of the consents defined for
     * the record.
     * <p>
     * Note that the returned set will be unmodifiable.
     * 
     * @return The set of consents.
     */
    public Set<Consent> getAllConsents() {
        Set<Consent> c = new HashSet<Consent>();
        for ( Consent con: this.consents ){
            c.add(con.getBasicCopy());
        }
        return Collections.unmodifiableSet(c);
    }

    /**
     * Set the set of Consent objects that are the responses to ConsentForms
     * belonging to the Element that this ElementInstance is related to.
     * 
     * @param consents The Set of Consent objects
     */
    protected void setConsents(Set<Consent> consents) {
        Set<Consent> oldConsents = this.consents;
        this.consents = consents;
        if(propertyChangeSupport.hasListeners(CONSENTS_PROPERTY)){
	        propertyChangeSupport.firePropertyChange(CONSENTS_PROPERTY, oldConsents,
	                this.consents);
        }
    }

    /**
     * Add a single consent to the record's collection of
     * consents.
     * 
     * @param c The consent to add.
     * @throws ModelException if it is attempted to add a 
     * <code>null</code> consent.
     */
    public void addConsent(Consent consent) throws ModelException{
        if ( null == consent ){
            throw new ModelException("Cannot add a null consent");
        }
        Consent c = (Consent)consent;
        this.consents.add(c);
        Provenance prov = new Provenance(null, c);
        this.provItems.add(prov);
        propertyChangeSupport.firePropertyChange(null, null, null);
    }
    
    /**
     * Retrieve the Consent that is associated with a specific
     * ConsentForm.
     * 
     * @param cf The ConsentForm to retrieve the Consent for.
     * @return The associated Consent object.
     */
    public Consent getConsent(ConsentForm cf){
        Consent consent = null;
        for (Consent c:this.getConsents()){
            if (c.getConsentForm().equals(cf)){
                consent = c;
                break;
            }
        }
        return consent;
    }

    public void removeConsent(Consent c, String reason) throws ModelException {
        if ( !this.consents.remove(c) ){
            throw new ModelException("Cannot remove consent - the record does not contain the given consent object.");
        }
        Provenance prov = new Provenance(c, null);
        prov.setComment(reason);
        this.provItems.add(prov);
    }
    
    /**
     * Get the identifier for the record.
     * 
     * @return The identifier or null if it has not yet been set.
     * @hibernate.many-to-one class="org.psygrid.data.model.hibernate.Identifier"
     *                        column="c_identifier_id"
     *                        not-null="true"
     *                        unique="true"
     *                        cascade="none"
     */
    public Identifier getIdentifier() {
        return this.identifier;
    }
    
    /**
     * Set the identifier for the record.
     * 
     * @param identifier The identifier.
     */
    public void setIdentifier(Identifier identifier){
        this.identifier = identifier;
    }

    /**
     * Get the recruitment site set for this record
     * 
     * @return The site object.
     * @hibernate.many-to-one class="org.psygrid.data.model.hibernate.Site"
     *                        column="c_site_id"
     *                        cascade="none"
     */
    public Site getSite() {
        return this.site;
    }
    
    /**
     * Set the site the subject was recruited from
     * 
     * @param site The recruitment site
     */
    public void setSite(Site s){
        this.site = (Site)s;
    }
    
    /**
     * Get the consultant in charge of this record.
     * 
     * The consultant should be based at the site belonging
     * to a record. There can be more than one consultant
     * per site.
     * 
     * Required by the UKCRN reports.
     * 
     * @return string
     * @hibernate.property column="c_consultant"
     * 
     * @return string
     */
    public String getConsultant() {
		return consultant;
	}

    /**
     * Set the consultant in charge of this record.
     * 
     * The consultant should be based at the site belonging
     * to a record. There can be more than one consultant
     * per site.
     * 
     * Required by the UKCRN reports.
     * 
     * @param consultant
     */
	public void setConsultant(String consultant) {
		this.consultant = consultant;
	}

	/**
     * Get the Set of DocumentInstance objects that are children of the Record.
     * 
     * @return The Set of child DocumentInstance objects
     * 
     * @hibernate.set cascade="all" batch-size="100"
     * @hibernate.one-to-many class="org.psygrid.data.model.hibernate.DocumentInstance"
     * @hibernate.key column="c_record_id" not-null="false"
     */
    public Set<DocumentInstance> getDocInstances() {
        return docInstances;
    }

    public void setDocInstances(Set<DocumentInstance> docInstances) {
        this.docInstances = docInstances;
    }

    /**
     * Add a single child DocumentInstance to this Record's
     * collection of document instances.
     * 
     * @param child The DocumentInstance to add as a child.
     * @throws ModelException if the child element instance cannot be added.
     */
    public void addDocumentInstance(DocumentInstance child) throws ModelException {
        DocumentInstance d = (DocumentInstance)child;
        checkNewInstance(d);
        addInstanceServer(d);
        propertyChangeSupport.firePropertyChange(null, null, null);
    }

    /**
     * Retrieve all child document instances associated with a specific
     * document from the record's collection of document instances.
     * 
     * @param document The document that the document instances to retrieve
     * are associated with.
     * @return List of document instances that are associated with the 
     * given document.
     */
    public List<DocumentInstance> getDocumentInstances(Document document) {
        List<DocumentInstance> children = new ArrayList<DocumentInstance>();
        for (DocumentInstance d:docInstances){
            if ( d.getOccurrence().getDocument().equals(document)){
                children.add(d);
            }
        }
        return children;
    }

    /**
     * Retrieve the document instance associated with a specific occurrence
     * of a specific document from the record's collection of document
     * instances.
     * 
     * @param occurrence The document occurrence to retrieve the document
     * instance for.
     * @return The document instance, or <code>null</code> if no document 
     * instance exists for the given document occurrence.
     */
    public DocumentInstance getDocumentInstance(DocumentOccurrence occurrence) {
        DocumentInstance docInst = null;
        for (DocumentInstance d:docInstances){
            if ( d.getOccurrence().equals(occurrence)){
                docInst = d;
            }
        }
        return docInst;
    }

    /**
     * Generate an identifier for the record.
     * <p>
     * This method should only be called for initializing a 
     * record to contain additional documents that are to be
     * appended to a record with the same identifier that has
     * already been persisted in the repository.
     * 
     * @param identifier The identifier string.
     * @throws InvalidIdentifierException if the supplied identifier 
     * does not have the correct format.
     */
    public void generateIdentifier(String identifier) throws InvalidIdentifierException{
        Identifier id = new Identifier();
        id.initialize(identifier);
        this.identifier = id;
    }

    /**
     * Get the date when the consent for the record was last modified.
     * 
     * @return The date when consent last modified.
     * 
     * @hibernate.property column="c_consent_modified"
     */
    public Date getConsentModified() {
        return consentModified;
    }

    /**
     * Set the date when the consent for the record was last modified.
     * 
     * @param consentModified The date when consent last modified.
     */
    public void setConsentModified(Date consentModified) {
        this.consentModified = consentModified;
    }

    /**
     * Get the date when the record's status was last modified.
     * 
     * @return The date when the status last modified.
     * 
     * @hibernate.property column="c_status_modified"
     */
    public Date getStatusModified() {
        return statusModified;
    }

    /**
     * Set the date when the record's status was last modified.
     * 
     * @param statusModified The date when the status last modified.
     */
    public void setStatusModified(Date statusModified) {
        this.statusModified = statusModified;
    }

    /**
     * @hibernate.many-to-one class="org.psygrid.data.model.hibernate.RecordData"
     *                        column="c_rcd_data_id"
     *                        not-null="true"
     *                        unique="true"
     *                        cascade="all"
     *                        fetch="join"
     */    
    public RecordData getTheRecordData() {
		return theRecordData;
	}

	public void setTheRecordData(RecordData theRecordData) {
		this.theRecordData = theRecordData;
	}

    /**
     * Get the data about the record.
     * <p>
     * Contains schedule start date, study entry date and notes.
     * 
     * @return The record data.
     */
	public RecordData getRecordData() {
		//Note a copy of the record data object is returned here to 
		//prevent clients editing its properties without going through
		//the provenance system.
		if ( null == theRecordData ){
			return null;
		}
		return theRecordData.copy();
	}

    /**
     * Set the data about the record.
     * <p>
     * The comment will be stored in the provenance history.
     * 
     * @param recordData
     * @param comment Comment for provenance history of the record data
     */
	public void setRecordData(RecordData recordData, String comment) {
        if ( null == recordData ){
            throw new ModelException("Cannot change recordData to null");
        }
        //create new provenance object
        Provenance prov = new Provenance(this.theRecordData, (RecordData)recordData);
        prov.setComment(comment);
        this.provItems.add(prov);
        this.theRecordData = (RecordData)recordData;
	}

    /**
     * Generate new RecordData object.
     * 
     * @return New RecordData object.
     */
	public RecordData generateRecordData() {
		return new RecordData();
	}

	/**
     * Get the deleted flag.
     * <p>
     * If True then the record has been "deleted" and so should not feature in
     * reports etc.
     * 
     * @return The deleted flag.
     * @hibernate.property column="c_deleted"
     */
    public boolean isDeleted() {
		return deleted;
	}

    /**
     * Set the deleted flag.
     * <p>
     * If True then the record has been "deleted" and so should not feature in
     * reports etc.
     * 
     * @param deleted The deleted flag.
     */
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public List<Persistent> getDeletedObjects() {
        return deletedObjects;
    }

    public void setDeletedObjects(List<Persistent> deletedObjects) {
        this.deletedObjects = deletedObjects;
    }

    public void addDeletedObject(Persistent p) {
        deletedObjects.add(p);
    }

    /**
     * Get the primary identifier.
     * <p>
     * If the Record is involved in dual data entry as the secondary record (i.e.
     * data will be propagated from the primary record to this one) then this
     * property contains the identifier string of the primary record. Otherwise
     * <code>null</code>.
     * 
     * @return The primary identifier
     * @hibernate.property column="c_prim_ident"
     */
    public String getPrimaryIdentifier() {
		return primaryIdentifier;
	}

    /**
     * Set the primary identifier.
     * <p>
     * If the Record is involved in dual data entry as the secondary record (i.e.
     * data will be propagated from the primary record to this one) then this
     * property contains the identifier string of the primary record. Otherwise
     * <code>null</code>.
     * 
     * @param primaryIdentifier The primary identifier
     */
	public void setPrimaryIdentifier(String primaryIdentifier) {
		this.primaryIdentifier = primaryIdentifier;
	}

	/**
	 * Get the secondary identifier.
	 * <p>
     * If the Record is involved in dual data entry as the primary record (i.e.
     * data will be propagated from this record to the secondary record) then this
     * property contains the identifier string of the secondary record. Otherwise
     * <code>null</code>.
	 *
	 * @return The secondary identifier.
	 * @hibernate.property column="c_sec_ident"
	 */
	public String getSecondaryIdentifier() {
		return secondaryIdentifier;
	}

	/**
	 * Set the secondary identifier.
	 * <p>
     * If the Record is involved in dual data entry as the primary record (i.e.
     * data will be propagated from this record to the secondary record) then this
     * property contains the identifier string of the secondary record. Otherwise
     * <code>null</code>.
	 *
	 * @param secondaryIdentifier The secondary identifier.
	 */
	public void setSecondaryIdentifier(String secondaryIdentifier) {
		this.secondaryIdentifier = secondaryIdentifier;
	}
	
	/**
     * Get the externally generated identifier for the record.
     * 
     * @return The externally generated identifier.
	 * @hibernate.property column="c_ext_id" index="ext_id_idx"
	 * 
	 * NB: DB2 index names are limited to 18 characters.
	 */
	public String getExternalIdentifier() {
		return externalIdentifier;
	}

	/**
	 * Set the externally generated identifier for the record.
	 * 
	 * @param externalIdentifier The externally generated identifier.
	 */
	public void setExternalIdentifier(String externalIdentifier) {
		this.externalIdentifier = externalIdentifier;
	}
	
	/**
	 * If this is set to True thon the external id is to be used shown throughout the openCDMS
	 * system as the primary identifier. The openCDMS native identifier, in this case will not be visible to the user at all.
	 * @hibernate.property column="c_ext_id_as_primary"
	 */
	public boolean getUseExternalIdAsPrimary() {
		return useExternalIdAsPrimary;
	}

	public void setUseExternalIdAsPrimary(boolean useExternalIdAsPrimary) {
		this.useExternalIdAsPrimary = useExternalIdAsPrimary;
	}

	public org.psygrid.data.model.dto.RecordDTO toDTO(){
        return toDTO(RetrieveDepth.RS_COMPLETE, null, null);
    }
    
    public org.psygrid.data.model.dto.RecordDTO toDTO(RetrieveDepth depth){
        return toDTO(depth, null, null);
    }
    
    public org.psygrid.data.model.dto.RecordDTO toDTO(RetrieveDepth depth, Long docInstId){
        return toDTO(depth, null, docInstId);
    }
    
    public org.psygrid.data.model.dto.RecordDTO toDTO(RetrieveDepth depth, String status){
        return toDTO(depth, status, null);
    }
  
    public org.psygrid.data.model.dto.RecordDTO toDTO(RetrieveDepth depth, String status, Long docInstId){
        //create list to hold references to objects in the record's
        //object graph which have multiple references to them within
        //the object graph. This is used so that each object instance
        //is copied to its DTO equivalent once and once only
        Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
        org.psygrid.data.model.dto.RecordDTO dtoR = toDTO(dtoRefs, depth, status, docInstId);
        dtoRefs = null;
        return dtoR;
    }
    
    public org.psygrid.data.model.dto.RecordDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        return toDTO(dtoRefs, depth, null, null);
    }    
    
    public org.psygrid.data.model.dto.RecordDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth, String status, Long docInstId){
        //check for an already existing instance of a dto object for this 
        //record in the map of references
        org.psygrid.data.model.dto.RecordDTO dtoR = null;
        if ( dtoRefs.containsKey(this)){
            dtoR = (org.psygrid.data.model.dto.RecordDTO)dtoRefs.get(this);
        }
        if ( null == dtoR ){
            //an instance of the record has not already
            //been created, so create it, and add it to 
            //the map of references
            dtoR = new org.psygrid.data.model.dto.RecordDTO();
            dtoRefs.put(this, dtoR);
            toDTO(dtoR, dtoRefs, depth, status, docInstId);
        }

        return dtoR;
    }
    
    public void toDTO(org.psygrid.data.model.dto.RecordDTO dtoR, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        toDTO(dtoR, dtoRefs, depth, null, null);
    }

    public void toDTO(org.psygrid.data.model.dto.RecordDTO dtoR, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth, String status){
        toDTO(dtoR, dtoRefs, depth, status, null);
    }

    public void toDTO(org.psygrid.data.model.dto.RecordDTO dtoR, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth, String status, Long docInstId){
        super.toDTO(dtoR, dtoRefs, depth);

        dtoR.setPrimaryIdentifier(this.primaryIdentifier);
        dtoR.setSecondaryIdentifier(this.secondaryIdentifier);
        dtoR.setExternalIdentifier(this.externalIdentifier);
        dtoR.setUseExternalIdAsPrimary(useExternalIdAsPrimary);
        dtoR.setDeleted(this.deleted);
        
        if ( null != this.identifier ){
            dtoR.setIdentifier(this.identifier.toDTO(dtoRefs, depth));
        }
        
        if ( null != this.site ){
            dtoR.setSite(this.site.toDTO(dtoRefs, depth));
        } 

        dtoR.setConsultant(consultant);
        
        if ( null != this.consentModified ){
            dtoR.setConsentModified(this.consentModified);
        } 

        if ( null != this.statusModified ){
            dtoR.setStatusModified(this.statusModified);
        } 
        
        if ( null != this.dataSet ){
            dtoR.setDataSetId(this.dataSet.getId());
        }
        else {
        	dtoR.setDataSetId(this.dataSetId);
        }
        
        if ( null == this.deletedObjects ){
            //allow this to work for users who didn't commit local records
            //before upgrade to version 1.1.4
            dtoR.setDeletedObjects(new org.psygrid.data.model.dto.PersistentDTO[0]);
        }
        else{
            org.psygrid.data.model.dto.PersistentDTO[] dtoDelObj = new org.psygrid.data.model.dto.PersistentDTO[this.deletedObjects.size()];
            for ( int i=0; i<this.deletedObjects.size(); i++ ){
                dtoDelObj[i] = this.deletedObjects.get(i).toDTO(dtoRefs, depth);
            }
            dtoR.setDeletedObjects(dtoDelObj);
        }
        
        if ( RetrieveDepth.RS_MINIMUM != depth ){
        
            dtoR.setScheduleStartDate(this.scheduleStartDate);
            dtoR.setStudyEntryDate(this.studyEntryDate);
            
        	if ( null != this.theRecordData ){
        		dtoR.setTheRecordData(this.theRecordData.toDTO(dtoRefs, depth));
        	}
        		
            Set<DocumentInstance> tempSet = null;
            if ( null != docInstId ){
                //create temporary list of document instances containing just the instance
                //for the given document id
                tempSet = new HashSet<DocumentInstance>();
                for ( DocumentInstance inst:this.getDocInstances()){
                    if ( docInstId.equals(inst.getId() ) ){
                        tempSet.add(inst);
                    }
                }
            }
            else if ( null != status ){
                //create temporary list of document instances with the given status
                tempSet = new HashSet<DocumentInstance>();
                for ( DocumentInstance inst:this.getDocInstances()){
                    if ( null != inst.getStatus() && inst.getStatus().getShortName().equals(status)){
                        tempSet.add(inst);
                    }
                }
            }
            else{
                //just let the temporary set point at the main set of document
                //instances
                tempSet = this.docInstances;
            }
            org.psygrid.data.model.dto.DocumentInstanceDTO[] dtoDocInsts = new org.psygrid.data.model.dto.DocumentInstanceDTO[tempSet.size()];
            Iterator it = tempSet.iterator();
            int counter = 0;
            while (it.hasNext()){
                DocumentInstance di = (DocumentInstance)it.next();
                dtoDocInsts[counter] = di.toDTO(dtoRefs, depth);
                counter++;
            }
            dtoR.setDocInstances(dtoDocInsts);
    
    
            org.psygrid.data.model.dto.ConsentDTO[] dtoConsents = new org.psygrid.data.model.dto.ConsentDTO[this.consents.size()];
            it = this.consents.iterator();
            counter = 0;
            while (it.hasNext()){
                Consent c = (Consent)it.next();
                dtoConsents[counter] = c.toDTO(dtoRefs, depth);
                counter++;
            }
            dtoR.setConsents(dtoConsents);
        }
    }

    /**
     * Attach the record to the dataset in the argument.
     * <p>
     * Assumes that the record has previously been detached from
     * this dataset.
     * 
     * @param dataSet The dataset to attach the record to.
     */
    public void attach(DataSet dataSet){
        
        //TODO would like to have a check here to see if the record is already attached
        //but there is no easy way to do this whilst preserving backwards-compatibility
        
        DataSet ds = (DataSet)dataSet;
        
        //check that this is the correct dataset
        Long dsId = null;
        if ( null != dataSetId ){
            dsId = this.dataSetId;
        }
        else{
            //preserve backwards compatability with records detached
            //prior to the introduction of Record.dataSetId
            dsId = this.dataSet.getId();
        }
        if ( !dsId.equals(ds.getId()) ){
            throw new ModelException("Failed to attach record id="+this.getId()+" to dataset id="+ds.getId()+" - record should be attached to dataset id="+dsId);
        }
        
        
        
        super.attach(ds);
        
        this.dataSet = ds;
        this.dataSetId = null;
        
        if(null != siteId){
        		//recover the site
        	    	List<Group> lg = ds.getGroups();
			for (Group g : lg) {
				List<Site> ls = g.getSites();
				for(Site s : ls){
					if(siteId.equals(s.getId())){
						site = s;
						break;
					}
				}
				if(site!=null){
					break;
				}
			}
        } else {
            // Backwards compatability
            // Its ok to have no sites defined
        }

        for(DocumentInstance di:this.docInstances){
            attachDocumentInstance(di);
        }
        
        for (Consent c:this.consents){
            boolean attached = false;
            Long cfId = null;
            if ( null != c.getConsentFormId() ){
                cfId = c.getConsentFormId();
            }
            else{
                //preserve backwards compatability with records detached
                //prior to the introduction of Consent.consentFormId
                cfId = c.getConsentForm().getId();
            }
            for (ConsentFormGroup cfg:ds.getAllConsentFormGroups()){
                for (PrimaryConsentForm pcf:cfg.getConsentForms()){
                    if ( pcf.getId().equals(cfId)){
                        c.attach(pcf);
                        attached = true;
                        break;
                    }
                    for (AssociatedConsentForm acf:pcf.getAssociatedConsentForms()){
                        if ( acf.getId().equals(cfId)){
                            c.attach(acf);
                            attached = true;
                            break;
                        }
                    }
                }
                if ( attached ){
                    break;
                }
            }
            if ( !attached ){
                throw new ModelException(("Failed to attach consent id="+c.getId()+" - no consent form exists with id="+cfId));
            }
        }
    }
    
    /**
     * Detach the record from its dataset.
     * <p>
     * This is done to make the object graph of the record as small as possible,
     * to make storing or sending the record more efficient.
     */
    public void detach(){
        
        super.detach();
        
        this.dataSetId = this.dataSet.getId();
        this.dataSet = null;
        if ( null != this.site ){
        	//Site can be null if new document are being added to an existing
        	//record, which has already been committed
        	this.siteId = this.site.getId();
        	this.site = null;
        }
        
        
        for(DocumentInstance di:this.docInstances){
            di.detach();        
        }
        
        for (Consent c:this.consents){
            c.detach();
        }
        
    }
    
    /**
     * Method that does most of the work for adding a child to the
     * element instance, except for performing validation checks. It is
     * assumed that these check will have been performed when the child
     * was added by the client.
     * 
     * @param e The element instance to add as a child
     */
    public void addInstanceServer(DocumentInstance d) {
        d.setRecord(this.record);
        //in case the children of this child have been added prior to
        //this point we must traverse the graph of children to ensure that
        //the references to the record are correct
        d.addChildTasks(this.record);
        docInstances.add(d);
    }
    
    private void checkNewInstance(DocumentInstance child) throws ModelException{
        //check that this is a valid child to add
        //i.e. the element that the child references must be
        //a child element of the element that this instance
        //references.
        boolean validChild = false;
        for (Document d:this.getDataSet().getDocuments()){
            if (child.getOccurrence().getDocument().equals(d)){
                validChild = true;
                break;
            }
        }
        if ( !validChild ){
            throw new ModelException("Cannot add document instance - the document occurrence it references is not valid for this record.");
        }
        
        //check that the child is not a duplicate
        boolean uniqueChild = true;
        for (DocumentInstance di:this.docInstances){
            if (di.getOccurrence().equals(child.getOccurrence())){
                uniqueChild = false;
                break;
            }
        }
        if ( !uniqueChild ){
            throw new ModelException("Cannot add document instance - a document instance for the same document occurrence already exists.");
        }
        
    }

    @Override
    protected void addChildTasks(Record r) {
        //do nothing - a record can't be added as a child
    }

    @Override
    protected StatusedElement findElement() {
        return this.dataSet;
    }
 
    /**
     * Detach a document instance from its record, for the purposes
     * of transferring it to a different record instance (that represents
     * the same actual record).
     * <p>
     * This method cannot be used to actually delete a document instance.
     * 
     * @param docInst The document instance to detach.
     */
    public void detachDocumentInstance(DocumentInstance docInst){
        this.docInstances.remove(docInst);
    }
    
    /**
     * Retrieve the number of document instances contained by
     * the record.
     * 
     * @return The number of document instances.
     */
    public int numDocumentInstances(){
        return this.docInstances.size();
    }

    /**
     * Permanently remove and delete a document instance from the record.
     * 
     * @param docInst The document instance to remove.
     */
	public void removeDocumentInstance(DocumentInstance docInst) {
		if ( this.docInstances.remove(docInst) && null != docInst.getId()){
			DocumentInstance di = (DocumentInstance)docInst;
			di.detach();
			this.deletedObjects.add(di);
		}
	}

	/**
	 * Get the secondary record.
	 * <p>
     * The Record object equivalent to the Record's secondaryIdentifier.
     * <p>
     * This property is not intended to be persisted and is only used to store
     * the reference to the secondary record for the convenience of clients.
     * 
	 * @return The secondary record.
	 */
	public Record getSecondaryRecord() {
		return secondaryRecord;
	}

	/**
	 * Set the secondary record.
	 * <p>
     * The Record object equivalent to the Record's secondaryIdentifier.
     * <p>
     * This property is not intended to be persisted and is only used to store
     * the reference to the secondary record for the convenience of clients.
     * 
	 * @param secondaryRecord The secondary record.
	 */
	public void setSecondaryRecord(Record secondaryRecord) {
		this.secondaryRecord = secondaryRecord;
	}
    
    /**
     * Check whether there is sufficient consent attached to the record to permit
     * the addition of the document instance in the argument.
     * 
     * @param docInst The document instance.
     * @return Boolean, True if there is sufficient consent, False otherwise.
     */
	public boolean checkConsent(DocumentInstance docInst){
		
        Document d = docInst.getOccurrence().getDocument();
        boolean docConsent = true;
        for (int i=0; i<d.numConsentFormGroups(); i++ ){
            ConsentFormGroup cfg = d.getConsentFormGroup(i);
            boolean grpConsent = false;
            for (int j=0; j<cfg.numConsentForms(); j++){
                PrimaryConsentForm pcf = cfg.getConsentForm(j);
                boolean pcfConsent = false;
                Consent c = findConsent(consents, pcf);
                if ( null != c ){
                    pcfConsent = c.isConsentGiven();
                    if ( pcfConsent ){
                        //check associated consent forms
                        for (int k=0; k<pcf.numAssociatedConsentForms(); k++){
                            AssociatedConsentForm acf = pcf.getAssociatedConsentForm(k);
                            Consent ac = findConsent(consents, acf);
                            if ( null == ac ){
                                pcfConsent &= false;
                            }
                            else{
                                pcfConsent &= ac.isConsentGiven();
                            }
                        }
                    }
                }
                //consent must be obtained for one of the primary consent forms
                //in the consent form group
                grpConsent |= pcfConsent;
            }
            //consent must be obtained for all of the consent form groups associated
            //with the document
            docConsent &= grpConsent;
        }
        
        return docConsent;
	}
	
    /**
     * Check whether there is sufficient consent attached to the record to permit
     * the addition of the subject to the ESL.
     * 
     * @return Boolean, True if there is sufficient consent, False otherwise.
     */
	public boolean checkConsentForEsl(){
        boolean docConsent = true;
        for (int i=0, c=dataSet.numAllConsentFormGroups(); i<c; i++ ){
            ConsentFormGroup cfg = dataSet.getAllConsentFormGroup(i);
            if ( cfg.isEslTrigger() ){
	            boolean grpConsent = false;
	            for (int j=0, d=cfg.numConsentForms(); j<d; j++){
	                PrimaryConsentForm pcf = cfg.getConsentForm(j);
	                boolean pcfConsent = false;
	                Consent consent = findConsent(consents, pcf);
	                if ( null != consent ){
	                    pcfConsent = consent.isConsentGiven();
	                    if ( pcfConsent ){
	                        //check associated consent forms
	                        for (int k=0, e=pcf.numAssociatedConsentForms(); k<e; k++){
	                            AssociatedConsentForm acf = pcf.getAssociatedConsentForm(k);
	                            Consent ac = findConsent(consents, acf);
	                            if ( null == ac ){
	                                pcfConsent &= false;
	                            }
	                            else{
	                                pcfConsent &= ac.isConsentGiven();
	                            }
	                        }
	                    }
	                }
	                //consent must be obtained for one of the primary consent forms
	                //in the consent form group
	                grpConsent |= pcfConsent;
	            }
	            //consent must be obtained for all of the consent form groups associated
	            //with the document
	            docConsent &= grpConsent;
            }
        }
        
        return docConsent;
		
	}
	
    private Consent findConsent(Set<Consent> consents, ConsentForm cf){
        for ( Consent c: consents){
            if ( c.getConsentForm().equals(cf)){
                return c;
            }
        }
        return null;
    }
	
	/**
	 * Attach a document instance to the record - the document instance
	 * having previously been detached via a call to 
	 * {@link IDocumentInstance#detachFromRecord()}
	 * 
	 * @param docInst The document instance to attach.
	 */
    public void attach(DocumentInstance docInst){
    	attachDocumentInstance((DocumentInstance)docInst);
    	addDocumentInstance(docInst);
    }
    
    private void attachDocumentInstance(DocumentInstance di) throws ModelException{
    	boolean attached = false;
        Long docOccId = null;
        if ( null != di.getOccurrenceId() ){
            docOccId = di.getOccurrenceId();
        }
        else{
            //preserve backwards compatibility with records detached
            //prior to the introduction of DocumentInstance.occurrenceId
            docOccId = di.getOccurrence().getId();
        }
        for (Document d:dataSet.getDocuments()){
            for (DocumentOccurrence docO:d.getOccurrences()){
                if ( docOccId.equals(docO.getId()) ){
                    di.attach(docO);
                    di.setOccurrenceId(null);
                    attached = true;
                    break;
                }
            }
            if (attached){
                break;
            }
        }
        if ( !attached ){
            throw new ModelException("Failed to attach document instance id="+di.getId()+" - no document occurrence exists with id="+docOccId);
        }
    }

	/**
	 * Find document instances without consent.
	 * <p>
	 * Used when consent is changed to see what document instances
	 * need to be removed.
	 * 
	 * @return List of document instances for which there is no consent
	 */
	public List<DocumentInstance> findDocInstsWithoutConsent() {
		List<DocumentInstance> noConsent = new ArrayList<DocumentInstance>();
		for ( DocumentInstance docInst: docInstances ){
			if ( !checkConsent(docInst) ){
				noConsent.add(docInst);
			}
		}
		return noConsent;
	}
    
    
}
