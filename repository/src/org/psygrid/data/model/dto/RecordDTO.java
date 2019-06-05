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
import java.util.Set;

/**
 * Class to represent a realization of a single DataSet.
 * 
 * @author Rob Harper
 * 
 */
public class RecordDTO extends StatusedInstanceDTO {

    /**
     * The dataset that this record represents an instance of.
     */
    private Long dataSetId;

    /**
     * The collection of document instances that are contained by the record. 
     */
    private DocumentInstanceDTO[] docInstances = new DocumentInstanceDTO[0];
    
    /**
     * The Set of Consent objects that are the responses to ConsentForms
     * belonging to the Element that this ElementInstance is related to.
     */
    protected ConsentDTO[] consents = new ConsentDTO[0];
    
    /**
     * Identifier of the record.
     */
    protected IdentifierDTO identifier;
    
    /**
     * Site from which the subject of the record originated.
     */
    protected SiteDTO site;
    
    protected String consultant;
    
    /**
     * Date to use as the zero-point for scheduling.
     */
    private Date scheduleStartDate;
    
    /**
     * Date of entry into the study - used for UKCRN reports.
     */
    private Date studyEntryDate;

    /**
     * Data about the record.
     * <p>
     * Stored in a separate object so that a provenance history may be maintained.
     */
    private RecordDataDTO theRecordData;

    /**
     * The date when the consent for the record was last modified.
     */
    private Date consentModified;
    
    /**
     * The date when the status of the record was last modified.
     */
    private Date statusModified;
    
    private PersistentDTO[] deletedObjects = new PersistentDTO[0];
    
    private String primaryIdentifier;
    
    private String secondaryIdentifier;

    private boolean deleted;
    
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
	public RecordDTO(){}

    public Date getScheduleStartDate() {
        return scheduleStartDate;
    }

    public void setScheduleStartDate(Date scheduleStartDate) {
        this.scheduleStartDate = scheduleStartDate;
    }

    public Date getStudyEntryDate() {
		return studyEntryDate;
	}

	public void setStudyEntryDate(Date studyEntryDate) {
		this.studyEntryDate = studyEntryDate;
	}

	public RecordDataDTO getTheRecordData() {
		return theRecordData;
	}

	public void setTheRecordData(RecordDataDTO theRecordData) {
		this.theRecordData = theRecordData;
	}

	public Long getDataSetId() {
        return dataSetId;
    }

    public void setDataSetId(Long dataSetId) {
        this.dataSetId = dataSetId;
    }

    public DocumentInstanceDTO[] getDocInstances() {
        return docInstances;
    }

    public void setDocInstances(DocumentInstanceDTO[] docInstances) {
        this.docInstances = docInstances;
    }

    /**
     * Get the set of Consent objects that are the responses to ConsentForms
     * belonging to the Element that this ElementInstance is related to.
     * 
     * @return The Set of Consent objects
     * 
     */
    public ConsentDTO[] getConsents() {
        return consents;
    }

    /**
     * Set the set of Consent objects that are the responses to ConsentForms
     * belonging to the Element that this ElementInstance is related to.
     * 
     * @param consents The Set of Consent objects
     */
    public void setConsents(ConsentDTO[] consents) {
        this.consents = consents;
    }

    public IdentifierDTO getIdentifier() {
        return this.identifier;
    }

    public void setIdentifier(IdentifierDTO identifier) {
        this.identifier = identifier;
    }
    
    public void setSite(SiteDTO s) {
        this.site = s;
    }
    
    public SiteDTO getSite() {
        return this.site;
    }
    
    public String getConsultant() {
		return consultant;
	}

	public void setConsultant(String consultant) {
		this.consultant = consultant;
	}

	public PersistentDTO[] getDeletedObjects() {
        return deletedObjects;
    }

    public void setDeletedObjects(PersistentDTO[] deletedObjects) {
        this.deletedObjects = deletedObjects;
    }
    
	public Date getConsentModified() {
		return consentModified;
	}

	public void setConsentModified(Date consentModified) {
		this.consentModified = consentModified;
	}

	public Date getStatusModified() {
		return statusModified;
	}

	public void setStatusModified(Date statusModified) {
		this.statusModified = statusModified;
	}

    public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

    public String getPrimaryIdentifier() {
		return primaryIdentifier;
	}

	public void setPrimaryIdentifier(String primaryIdentifier) {
		this.primaryIdentifier = primaryIdentifier;
	}

	public String getSecondaryIdentifier() {
		return secondaryIdentifier;
	}

	public void setSecondaryIdentifier(String secondaryIdentifier) {
		this.secondaryIdentifier = secondaryIdentifier;
	}

	public String getExternalIdentifier() {
		return externalIdentifier;
	}

	public void setExternalIdentifier(String externalIdentifier) {
		this.externalIdentifier = externalIdentifier;
	}
	
	
	public boolean getUseExternalIdAsPrimary() {
		return useExternalIdAsPrimary;
	}

	public void setUseExternalIdAsPrimary(boolean useExternalIdAsPrimary) {
		this.useExternalIdAsPrimary = useExternalIdAsPrimary;
	}

	public org.psygrid.data.model.hibernate.Record toHibernate(){
        //create list to hold references to objects in the record's
        //object graph which have multiple references to them within
        //the object graph. This is used so that each object instance
        //is copied to its hibernate equivalent once and once only
        Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> dtoRefs = new HashMap<PersistentDTO, org.psygrid.data.model.hibernate.Persistent>();
        org.psygrid.data.model.hibernate.Record hR = toHibernate(dtoRefs);
        dtoRefs = null;
        return hR;
    }
    
    public org.psygrid.data.model.hibernate.Record toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        //check for an already existing instance of a hibernate object for this 
        //record in the map of references
        org.psygrid.data.model.hibernate.Record hR = null;
        if ( hRefs.containsKey(this)){
            hR = (org.psygrid.data.model.hibernate.Record)hRefs.get(this);
        }
        if ( null == hR ){
            //an instance of the record has not already
            //been created, so create it, and add it to 
            //the map of references
            hR = new org.psygrid.data.model.hibernate.Record();
            hRefs.put(this, hR);
            toHibernate(hR, hRefs);
        }

        return hR;
    }
    
    public void toHibernate(org.psygrid.data.model.hibernate.Record hR, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        super.toHibernate(hR, hRefs);
        
        hR.setScheduleStartDate(this.scheduleStartDate);
        hR.setStudyEntryDate(this.studyEntryDate);
        hR.setExternalIdentifier(this.externalIdentifier);
        hR.setUseExternalIdAsPrimary(useExternalIdAsPrimary);

        if ( null != this.theRecordData ){
        	hR.setTheRecordData(this.theRecordData.toHibernate(hRefs));
        }
        
        hR.setPrimaryIdentifier(this.primaryIdentifier);
        hR.setSecondaryIdentifier(this.secondaryIdentifier);
        hR.setDeleted(this.deleted);
        
        if ( null != this.identifier ){
            hR.setIdentifier(this.identifier.toHibernate(hRefs));
        }
        if ( null != this.site ){
            hR.setSite(this.site.toHibernate(hRefs));
        }
        
        hR.setConsultant(consultant);
        
        if ( null != this.dataSetId ){
            hR.setDataSetId(this.dataSetId);
        }
        if ( null != this.consentModified ){
            hR.setConsentModified(this.consentModified);
        }
        if ( null != this.statusModified ){
            hR.setStatusModified(this.statusModified);
        }
        
        Set<org.psygrid.data.model.hibernate.Consent> hConsents = hR.getConsents();
        for ( int i=0; i<this.consents.length; i++ ){
            ConsentDTO c = this.consents[i];
            if ( null != c ){
                hConsents.add(c.toHibernate(hRefs));
            }
        }
        
        Set<org.psygrid.data.model.hibernate.DocumentInstance> hDocInsts = hR.getDocInstances();
        for ( int i=0; i<this.docInstances.length; i++ ){
            DocumentInstanceDTO di = this.docInstances[i];
            if ( null != di ){
                hDocInsts.add(di.toHibernate(hRefs));
            }
        }
        
        List<org.psygrid.data.model.hibernate.Persistent> hDelObjs = hR.getDeletedObjects();
        for ( int i=0; i<this.deletedObjects.length; i++ ){
        	if (null != this.deletedObjects[i]) {
        		hDelObjs.add(this.deletedObjects[i].toHibernate(hRefs));
        	}
        }
        
    }

}
