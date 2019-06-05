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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.psygrid.common.simplemap.Pair;

/**
 * The top-level object that represents the definition of a set
 * of data that is to be collected.
 * 
 * @author Rob Harper
 */
public class DataSetDTO extends StatusedElementDTO {

	/**
	 * Identifies whether export security is to be applied to exported entries in this dataset.
	 */
	private boolean exportSecurityActive = false;
	
	/**
	 * The version number of the data set.
	 */
	private String versionNo;
    
    /**
     * The project code of the project that the dataset relates
     * to.
     * <p>
     * Typically this property is used to link the dataset to 
     * the security system.
     */
    private String projectCode;
    
    /**
     * Question that is asked to obtain the start date for scheduling
     * for all records associated with the dataset.
     */
    private String scheduleStartQuestion;
    
    /**
     * The date when the data set was last modified.
     */
    private Date dateModified;
    
    /**
     * Boolean flag to indicate whether the data set has been published 
     * or not. 
     * <p>
     * Once a data set has been published it is ready for data
     * collection, and its structure is not intended to change in the
     * future.
     */
    private boolean published;
    
    /**
     * Electronic document that is an information sheet for the
     * dataset.
     */
    private BinaryObjectDTO info;
    
    /**
     * The collection of documents that are contained by the dataset.
     */
    private DocumentDTO[] documents = new DocumentDTO[0];
    
    /**
     * Collection of consent form groups associated with the dataset,
     * and all elements in the hierarchy underneath the dataset.
     */
    private ConsentFormGroupDTO[] allConsentFormGroups = new ConsentFormGroupDTO[0];
    
    /**
     * Collection of validation rules associated with the dataset.
     * <p>
     * All validation rules referenced by entrys in the dataset must
     * be present in this collection.
     */
    private ValidationRuleDTO[] validationRules = new ValidationRuleDTO[0];
    
    private PersistentDTO[] deletedObjects = new PersistentDTO[0];
    
    /**
     * The number of characters in the suffix of identifiers generated
     * for use by records associated with the dataset.
     */
    private int idSuffixSize;
    
    /**
     * Array of transformers that entrys contained by the dataset
     * may reference.
     */
    private TransformerDTO[] transformers = new TransformerDTO[0];
        
    /**
     * Array of document groups that documents contained by the dataset
     * may be a part of.
     */
    private DocumentGroupDTO[] documentGroups = new DocumentGroupDTO[0];
        
    /**
     * Array of units that entrys contained by the dataset
     * may utilize.
     */
    private UnitDTO[] units = new UnitDTO[0];
        
    /**
     * Array of groups associated with the dataset.
     */
    private GroupDTO[] groups = new GroupDTO[0];
    
    /**
     * Boolean flag to indicate whether the Electronic Screening Log
     * should be used to hold identifiable data for subjects in the
     * dataset.
     */
    private boolean eslUsed;
    
    /**
     * Boolean flag to indicate whether the Electronic Screening Log's
     * randomize function should be called to randomly allocate a subject
     * in the dataset to an arm of a clinical trial.
     */
    private boolean randomizationRequired;
    
    /**
     * Boolean flag to indicate whether montly summary emails should
     * be sent out for records associated with the dataset.
     */
    private boolean sendMonthlySummaries;
    
    private int reviewReminderCount;
    
    private String primaryProjectCode;
    
    private String secondaryProjectCode;
    
    /**
     * If True, externally generated identifiers are used for records
     * in the dataset.
     */
    private boolean externalIdUsed;
    
	/**
	 * If this is set to True thon the external id is to be used shown throughout the openCDMS
	 * system as the primary identifier. The openCDMS native identifier, in this case will not be visible to the user at all.
	 */
	private boolean useExternalIdAsPrimary;
	
	private boolean showRandomisationTreatment = false;
	
	private boolean useMedsService = false;
	
	private boolean forceRecordCreation = false;
	
	private Pair<String,String>[] externalIdEditableSubstringPairs = null;
	
	private Pair<String, String>[] externalIdEditableSubstringValidationMapPairs = null;
	
	
	/**
     * If True, "review and approve" is not used. 
     * <p>
     * A manager is not 
     * expected to review committed documents, so they simply move
     * from "Incomplete" to "Complete" on being committed to the
     * repository.
     */
    private boolean noReviewAndApprove;

    private EslCustomFieldDTO[] eslCustomFields = new EslCustomFieldDTO[0];
    
    /**
	 * Default no-arg constructor, as required by the Hibernate framework
	 * for all persistable classes.
	 */
	public DataSetDTO(){};
	
	/**
	 * 
	 * @return - whether export security is active for this dataset
	 * 
	 */
	public boolean getExportSecurityActive(){
		return exportSecurityActive;
	}
	
	/**
	 * 
	 * @param exportSecurityActive - specifies whether export security is to be applies for entries
	 * in this dataset
	 */
	public void setExportSecurityActive(final boolean exportSecurityActive) {
		this.exportSecurityActive = exportSecurityActive;
	}
	
	public String getVersionNo() {
		return versionNo;
	}

	public void setVersionNo(final String versionNo) {
		this.versionNo = versionNo;
	}

    public String getScheduleStartQuestion() {
        return scheduleStartQuestion;
    }

    public void setScheduleStartQuestion(final String scheduleStartQuestion) {
        this.scheduleStartQuestion = scheduleStartQuestion;
    }

    public boolean isPublished() {
        return published;
    }

    /**
     * Set the boolean flag to indicate whether the data set has been 
     * published or not. 
     * <p>
     * Once a data set has been published it is ready for data
     * collection, and its structure is not intended to change in the
     * future.
     * 
     * @param published The published flag
     */
    public void setPublished(final boolean published) {
        this.published = published;
    }
	
    public BinaryObjectDTO getInfo() {
        return info;
    }

    public void setInfo(final BinaryObjectDTO info) {
        this.info = info;
    }

    public Date getDateModified() {
        return this.dateModified;
    }
    
    /**
     * Set the date when the data set was last modified.
     * 
     * @param dateModified The date the data set was last modified.
     */
    public void setDateModified(final Date dateModified){
        this.dateModified = dateModified;
    }

    /**
     * Get the collection of consent form groups associated with the dataset,
     * and all elements in the hierarchy underneath the dataset.
     * 
     * @return The collection of consent form groups.
     */
    public ConsentFormGroupDTO[] getAllConsentFormGroups() {
        return allConsentFormGroups;
    }

    /**
     * Set the collection of consent form groups associated with the dataset,
     * and all elements in the hierarchy underneath the dataset.
     * 
     * @param allConsentFormGroups The collection of consent form groups.
     */
    public void setAllConsentFormGroups(final ConsentFormGroupDTO[] allConsentFormGroups) {
        this.allConsentFormGroups = allConsentFormGroups;
    }
    
    public ValidationRuleDTO[] getValidationRules() {
        return validationRules;
    }

    public void setValidationRules(final ValidationRuleDTO[] validationRules) {
        this.validationRules = validationRules;
    }

    public PersistentDTO[] getDeletedObjects() {
        return deletedObjects;
    }

    public void setDeletedObjects(final PersistentDTO[] deletedObjects) {
        this.deletedObjects = deletedObjects;
    }

    public int getIdSuffixSize() {
        return idSuffixSize;
    }

    public void setIdSuffixSize(final int idSuffixSize) {
        this.idSuffixSize = idSuffixSize;
    }

    public TransformerDTO[] getTransformers() {
        return transformers;
    }

    public void setTransformers(final TransformerDTO[] transformers) {
        this.transformers = transformers;
    }

    public DocumentGroupDTO[] getDocumentGroups() {
        return documentGroups;
    }

    public void setDocumentGroups(final DocumentGroupDTO[] documentGroups) {
        this.documentGroups = documentGroups;
    }

    public UnitDTO[] getUnits() {
        return units;
    }

    public void setUnits(final UnitDTO[] units) {
        this.units = units;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(final String projectCode) {
        this.projectCode = projectCode;
    }

    public DocumentDTO[] getDocuments() {
        return documents;
    }

    public void setDocuments(final DocumentDTO[] documents) {
        this.documents = documents;
    }

    public GroupDTO[] getGroups() {
        return groups;
    }

    public void setGroups(final GroupDTO[] groups) {
        this.groups = groups;
    }

    public boolean isEslUsed() {
        return eslUsed;
    }

    public void setEslUsed(final boolean eslUsed) {
        this.eslUsed = eslUsed;
    }

    public boolean isRandomizationRequired() {
        return randomizationRequired;
    }

    public void setRandomizationRequired(final boolean randomizationRequired) {
        this.randomizationRequired = randomizationRequired;
    }

    public boolean isSendMonthlySummaries() {
        return sendMonthlySummaries;
    }

    public void setSendMonthlySummaries(final boolean sendMonthlySummaries) {
        this.sendMonthlySummaries = sendMonthlySummaries;
    }

    public int getReviewReminderCount() {
		return reviewReminderCount;
	}

	public void setReviewReminderCount(final int reviewReminderCount) {
		this.reviewReminderCount = reviewReminderCount;
	}

	public String getPrimaryProjectCode() {
		return primaryProjectCode;
	}

	public void setPrimaryProjectCode(final String primaryProjectCode) {
		this.primaryProjectCode = primaryProjectCode;
	}

	public String getSecondaryProjectCode() {
		return secondaryProjectCode;
	}

	public void setSecondaryProjectCode(final String secondaryProjectCode) {
		this.secondaryProjectCode = secondaryProjectCode;
	}

	public boolean isExternalIdUsed() {
		return externalIdUsed;
	}

	public void setExternalIdUsed(final boolean externalIdUsed) {
		this.externalIdUsed = externalIdUsed;
	}

	public boolean getUseExternalIdAsPrimary() {
		return useExternalIdAsPrimary;
	}

	public void setUseExternalIdAsPrimary(final boolean useExternalIdAsPrimary) {
		this.useExternalIdAsPrimary = useExternalIdAsPrimary;
	}

	public boolean getShowRandomisationTreatment() {
		return showRandomisationTreatment;
	}

	public void setShowRandomisationTreatment(final boolean showRandomisationTreatment) {
		this.showRandomisationTreatment = showRandomisationTreatment;
	}

	public boolean getUseMedsService() {
		return useMedsService;
	}

	public void setUseMedsService(boolean useMedsService) {
		this.useMedsService = useMedsService;
	}

	public boolean isNoReviewAndApprove() {
		return noReviewAndApprove;
	}

	public void setNoReviewAndApprove(final boolean noReviewAndApprove) {
		this.noReviewAndApprove = noReviewAndApprove;
	}

	public EslCustomFieldDTO[] getEslCustomFields() {
		return eslCustomFields;
	}

	public void setEslCustomFields(final EslCustomFieldDTO[] eslCustomFields) {
		this.eslCustomFields = eslCustomFields;
	}

	public boolean getForceRecordCreation() {
		return forceRecordCreation;
	}

	public void setForceRecordCreation(final boolean forceRecordCreation) {
		this.forceRecordCreation = forceRecordCreation;
	}

	public Pair<String, String>[] getExternalIdEditableSubstringPairs() {
		return externalIdEditableSubstringPairs;
	}

	public void setExternalIdEditableSubstringPairs(
			final Pair<String, String>[] externalIdEditableSubstringPairs) {
		this.externalIdEditableSubstringPairs = externalIdEditableSubstringPairs;
	}

	public Pair<String, String>[] getExternalIdEditableSubstringValidationMapPairs() {
		return externalIdEditableSubstringValidationMapPairs;
	}

	public void setExternalIdEditableSubstringValidationMapPairs(
			final Pair<String, String>[] externalIdEditableSubstringValidationMapPairs) {
		this.externalIdEditableSubstringValidationMapPairs = externalIdEditableSubstringValidationMapPairs;
	}

	public org.psygrid.data.model.hibernate.DataSet toHibernate(){
        //create list to hold references to objects in the dataset's
        //object graph which have multiple references to them within
        //the object graph. This is used so that each object instance
        //is copied to its hibernate equivalent once and once only
        Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> dtoRefs = new HashMap<PersistentDTO, org.psygrid.data.model.hibernate.Persistent>();
        final org.psygrid.data.model.hibernate.DataSet hDS = toHibernate(dtoRefs);
        dtoRefs = null;
        return hDS;
    }
    
    public org.psygrid.data.model.hibernate.DataSet toHibernate(final Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        //check for an already existing instance of a hibernate object for this 
        //dataset in the set of references
        org.psygrid.data.model.hibernate.DataSet hDS = null;
        if ( hRefs.containsKey(this)){
            hDS = (org.psygrid.data.model.hibernate.DataSet)hRefs.get(this);
        }
        if ( null == hDS ){
            //an instance of the dataset has not already
            //been created, so create it and add it to the map of references
            hDS = new org.psygrid.data.model.hibernate.DataSet();
            hRefs.put(this, hDS);
            toHibernate(hDS, hRefs);
        }

        return hDS;
        
    }
    
    public void toHibernate(final org.psygrid.data.model.hibernate.DataSet hDS, final Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        super.toHibernate(hDS, hRefs);
        hDS.setDateModified(this.dateModified);
        hDS.setPublished(this.published);
        hDS.setVersionNo(this.versionNo);
        hDS.setIdSuffixSize(this.idSuffixSize);
        hDS.setProjectCode(this.projectCode);
        hDS.setScheduleStartQuestion(this.scheduleStartQuestion);
        hDS.setEslUsed(this.eslUsed);
        hDS.setRandomizationRequired(this.randomizationRequired);
        hDS.setSendMonthlySummaries(this.sendMonthlySummaries);
        hDS.setExportSecurityActive(exportSecurityActive);
        hDS.setReviewReminderCount(this.reviewReminderCount);
        hDS.setPrimaryProjectCode(this.primaryProjectCode);
        hDS.setSecondaryProjectCode(this.secondaryProjectCode);
        hDS.setExternalIdUsed(this.externalIdUsed);
        hDS.setUseExternalIdAsPrimary(useExternalIdAsPrimary);
        hDS.setShowRandomisationTreatment(showRandomisationTreatment);
        hDS.setUseMedsService(useMedsService);
        hDS.setNoReviewAndApprove(this.noReviewAndApprove);
        hDS.setForceRecordCreation(forceRecordCreation);
        
        if(this.externalIdEditableSubstringPairs != null && this.externalIdEditableSubstringPairs.length > 0){
        	
        	final Map<String, String> editableSubstringMap = new HashMap<String, String>();
        	for(int i = 0; i < externalIdEditableSubstringPairs.length; i++){
        		editableSubstringMap.put(externalIdEditableSubstringPairs[i].getName(), externalIdEditableSubstringPairs[i].getValue());
        	}
        	
        	hDS.setExternalIdEditableSubstringMap(editableSubstringMap);
        	
        }
        
        if(this.externalIdEditableSubstringValidationMapPairs != null && this.externalIdEditableSubstringValidationMapPairs.length > 0){
        	
        	final Map<String, String> editableSubstringValidationMap = new HashMap<String,String>();
        	for(int i = 0; i < externalIdEditableSubstringValidationMapPairs.length; i++){
        		editableSubstringValidationMap.put(this.externalIdEditableSubstringValidationMapPairs[i].getName(), this.externalIdEditableSubstringValidationMapPairs[i].getValue());
        	}
        	
        	hDS.setExternalIdEditableSubstringValidationMap(editableSubstringValidationMap);
        }
        
        if ( null != this.info ){
            hDS.setInfo(this.info.toHibernate(hRefs));
        }
        
        final List<org.psygrid.data.model.hibernate.ConsentFormGroup> hAllCFGs = 
            hDS.getAllConsentFormGroups();
        for (int i=0; i<this.allConsentFormGroups.length; i++){
            final ConsentFormGroupDTO group = allConsentFormGroups[i];
            if ( null != group ){
                hAllCFGs.add(group.toHibernate(hRefs));
            }
        }
        
        final List<org.psygrid.data.model.hibernate.ValidationRule> hRules = 
            hDS.getValidationRules();
        for (int i=0; i<this.validationRules.length; i++){
            final ValidationRuleDTO rule = validationRules[i];
            if ( null != rule ){
                hRules.add(rule.toHibernate(hRefs));
            }
        }
        
        final List<org.psygrid.data.model.hibernate.Persistent> hDeleted = 
            hDS.getDeletedObjects();
        for (int i=0; i<this.deletedObjects.length; i++){
            final PersistentDTO p = deletedObjects[i];
            if ( null != p ){
                hDeleted.add(p.toHibernate(hRefs));
            }
        }
        
        final List<org.psygrid.data.model.hibernate.Transformer> hTransformers = 
            hDS.getTransformers();
        for (int i=0; i<this.transformers.length; i++){
            final TransformerDTO t = transformers[i];
            if ( null != t ){
                hTransformers.add(t.toHibernate(hRefs));
            }
        }    

        final List<org.psygrid.data.model.hibernate.DocumentGroup> hDocGroups = 
            hDS.getDocumentGroups();
        for (int i=0; i<this.documentGroups.length; i++){
            final DocumentGroupDTO dg = documentGroups[i];
            if ( null != dg ){
                hDocGroups.add(dg.toHibernate(hRefs));
            }
        }

        final List<org.psygrid.data.model.hibernate.Unit> hUnits = 
            hDS.getUnits();
        for (int i=0; i<this.units.length; i++){
            final UnitDTO u = units[i];
            if ( null != u ){
                hUnits.add(u.toHibernate(hRefs));
            }
        }    
        
        final List<org.psygrid.data.model.hibernate.Document> hDocs = 
            hDS.getDocuments();
        for (int i=0; i<this.documents.length; i++){
            final DocumentDTO d = documents[i];
            if ( null != d ){
                hDocs.add(d.toHibernate(hRefs));
            }
        }    
        
        final List<org.psygrid.data.model.hibernate.Group> hGrps = 
            hDS.getGroups();
        for (int i=0; i<this.groups.length; i++){
            final GroupDTO g = groups[i];
            if ( null != g ){
                hGrps.add(g.toHibernate(hRefs));
            }
        }    
        
        final List<org.psygrid.data.model.hibernate.EslCustomField> hEcfs = 
            hDS.getEslCustomFields();
        for (int i=0; i<this.eslCustomFields.length; i++){
            final EslCustomFieldDTO ecf = eslCustomFields[i];
            if ( null != ecf ){
            	hEcfs.add(ecf.toHibernate(hRefs));
            }
        }    
        
    }
    
}
