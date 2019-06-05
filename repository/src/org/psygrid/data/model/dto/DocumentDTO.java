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

import java.util.List;
import java.util.Map;

public class DocumentDTO extends StatusedElementDTO {
		
	private String importMappingString;
	
	/**
	 * Determines whether the document is import-enabled.
	 */
	private boolean isImportEnabled = false;
	
    /**
     * The collection of entries that are contained by the document.
     */
    private EntryDTO[] entries = new EntryDTO[0];
    
    /**
     * The collection of sections that are associated with the document.
     * <p>
     * Sections are intended to logically divide up the entries in a document
     * so as to give it structure. Each entry in a document's collection of
     * entries must be associated with one of the document's sections.
     */
    private SectionDTO[] sections = new SectionDTO[0];
    
    /**
     * The collection of consent form groups associated with the 
     * document.
     * <p>
     * If multiple consent form groups are associated with a document then 
     * they are intended to have an AND relation i.e. for an instance
     * of the element to be created in a record one of the
     * all of the consent form groups in the collection must have been 
     * completed in the positive.
     */
    protected ConsentFormGroupDTO[] conFrmGrps = new ConsentFormGroupDTO[0];
    
    protected DocumentOccurrenceDTO[] occurrences = new DocumentOccurrenceDTO[0];

    protected Long primaryDocIndex;
    
    protected Long secondaryDocIndex;

    /**
     * The toString representation of the RBACAction used to control access to this
     * document 
     */
    protected String action;
    
    /**
     * The toString representation of the RBACAction used to control access to any document instances
     * created by this document.
     * 
     * This should be the same as the document's action but include group access restrictions.
     */
    protected String instanceAction;
    
    /**
     * The toString representation of the RBACAction, which if present, will indicate
     * the users that can create and edit document instances for this document. 
     * 
     * If null, it is assumed that the document (and any instances that are created) 
     * is editable for any user who can access it, this is for backwards compatibility 
     * purposes.
     */
    protected String editableAction;

    /**
     * The toString representation of the RBACAction used to allow users to edit document
     * instances created by this document.
     * 
     * This should be the same as the document's editableAction but include group access restrictions.
     */
    protected String instanceEditableAction;    
    
    /**
     * A non persisted reference to whether instances can be created for this document
     * and whether those instances can be edited.
     * 
     * Set after the RBACAction for editableAction has been checked for the user and 
     * is then used by CoCoA.
     */
    protected boolean editingPermitted;
    
    private boolean longRunning;
    
    public DocumentDTO() {}

    public DocumentOccurrenceDTO[] getOccurrences() {
        return occurrences;
    }

    public void setOccurrences(DocumentOccurrenceDTO[] occurrences) {
        this.occurrences = occurrences;
    }

    public ConsentFormGroupDTO[] getConFrmGrps() {
        return conFrmGrps;
    }

    public void setConFrmGrps(ConsentFormGroupDTO[] conFrmGrps) {
        this.conFrmGrps = conFrmGrps;
    }

    public EntryDTO[] getEntries() {
        return entries;
    }

    public void setEntries(EntryDTO[] entries) {
        this.entries = entries;
    }

    public SectionDTO[] getSections() {
        return sections;
    }

    public void setSections(SectionDTO[] sections) {
        this.sections = sections;
    }

    public Long getPrimaryDocIndex() {
		return primaryDocIndex;
	}

	public void setPrimaryDocIndex(Long primaryDocIndex) {
		this.primaryDocIndex = primaryDocIndex;
	}

	public Long getSecondaryDocIndex() {
		return secondaryDocIndex;
	}

	public void setSecondaryDocIndex(Long secondaryDocIndex) {
		this.secondaryDocIndex = secondaryDocIndex;
	}

	public String getAction() {
		return action;
	}
	
	public void setAction(String action) {
		this.action = action;
	}
	
	/**
	 * Get the toString representation of the RBACAction used to control access to any document instances created by this document. 
	 * 
	 * This should be the same as the document's action but include group access restrictions.
	 * 
	 * @return instanceAction
	 */
	public String getInstanceAction() {
		return instanceAction;
	}

	/**
	 * Set the toString representation of the RBACAction used to control access to any document instances created by this document. 
	 * 
	 * This should be the same as the document's action but include group access restrictions.
	 * 
	 * @param instanceAction
	 */
	public void setInstanceAction(String instanceAction) {
		this.instanceAction = instanceAction;	
	}
	
	/**
	 * Get the toString representation of the RBACAction, which if present, will enable
     * the relevant users to edit any document instance. 
     * 
     * If null, it is assumed that the document (and any instances that are created) 
     * is editable for any user who can access it, this is for backwards compatibility 
     * purposes.
     * 
     * @return editableAction
	 */
	public String getEditableAction() {
		return editableAction;
	}

	/**
	 *  Set the editableAction, using the toString representation of the
	 * relevant RBACAction. If present this will indicate the users able
	 * to create and edit the document instance.
     * 
     * If null, it is assumed that the document (and any instances that are created) 
     * is editable for any user who can access it, this is for backwards compatibility 
     * purposes.
     * 
	 * @param editableAction
	 */
	public void setEditableAction(String editableAction) {
		this.editableAction = editableAction;
	}

	/**
	 * Get the toString representation of the RBACAction used to allow users to edit document
     * instances created by this document.
     * 
     * This should be the same as the document's editableAction but include group access restrictions.
     * 
     * @return editableAction
	 */
	public String getInstanceEditableAction() {
		return instanceEditableAction;
	}

	/**
	 * Set the toString representation of the RBACAction used to allow users to edit document
     * instances created by this document.
     * 
     * This should be the same as the document's editableAction but include group access restrictions.
     * 
	 * @param instanceEditableAction
	 */
	public void setInstanceEditableAction(String instanceEditableAction) {
		this.instanceEditableAction = instanceEditableAction;
	}
	
	/**
	 * A non persisted reference to whether instances can be created for this document
     * and whether those instances can be edited.
     * 
     * Set after the RBACAction for editableAction has been checked for the user and 
     * is then used by CoCoA.
	 * 
	 * @return isEditable
	 */
	public boolean isEditingPermitted() {
		return editingPermitted;
	}

	/**
	 * A non persisted reference to whether instances can be created for this document
     * and whether those instances can be edited.
     * 
     * Set after the RBACAction for editableAction has been checked for the user and 
     * is then used by CoCoA.
     * 
     * @param editingPermitted2
	 */
	public void setEditingPermitted(boolean isEditable) {
		this.editingPermitted = isEditable;
	}
	
	
	public boolean isLongRunning() {
		return longRunning;
	}

	public void setLongRunning(boolean longRunning) {
		this.longRunning = longRunning;
	}

	public org.psygrid.data.model.hibernate.Document toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        //check for an already existing instance of a hibernate object for this 
        //document in the map of references
        org.psygrid.data.model.hibernate.Document hD = null;
        if ( hRefs.containsKey(this)){
            hD = (org.psygrid.data.model.hibernate.Document)hRefs.get(this);
        }
        if ( null == hD ){
            //an instance of the document has not already
            //been created, so create it, and add it to the
            //map of references
            hD = new org.psygrid.data.model.hibernate.Document();
            hRefs.put(this, hD);
            toHibernate(hD, hRefs);
        }
        
        return hD;
    }
    
    public void toHibernate(org.psygrid.data.model.hibernate.Document hD, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        super.toHibernate(hD, hRefs);

        hD.setIsImportEnabled(this.isImportEnabled);
        hD.setImportMappingString(this.importMappingString);
        hD.setPrimaryDocIndex(this.primaryDocIndex);
        hD.setSecondaryDocIndex(this.secondaryDocIndex);
        hD.setLongRunning(this.longRunning);
        hD.setAction(action);
        hD.setInstanceAction(instanceAction);
        hD.setEditableAction(editableAction);
        hD.setInstanceEditableAction(instanceEditableAction);
        hD.setEditingPermitted(editingPermitted);
        
        
        List<org.psygrid.data.model.hibernate.DocumentOccurrence> hOccurrences = hD.getOccurrences();
        for ( int i=0; i<this.occurrences.length; i++ ){
            DocumentOccurrenceDTO o = this.occurrences[i];
            if ( null != o ){
                hOccurrences.add(o.toHibernate(hRefs));
            }
        }
        
        List<org.psygrid.data.model.hibernate.Entry> hEntries = hD.getEntries();
        for ( int i=0; i<this.entries.length; i++ ){
            EntryDTO e = this.entries[i];
            if ( null != e ){
                hEntries.add(e.toHibernate(hRefs));
            }
        }
        
        List<org.psygrid.data.model.hibernate.Section> hSections = hD.getSections();
        for ( int i=0; i<this.sections.length; i++ ){
            SectionDTO s = this.sections[i];
            if ( null != s ){
                hSections.add(s.toHibernate(hRefs));
            }
        }
        
        List<org.psygrid.data.model.hibernate.ConsentFormGroup> hCfgs = hD.getConFrmGrps();
        if (null != this.conFrmGrps) {
            for ( int i=0; i<this.conFrmGrps.length; i++ ){
                ConsentFormGroupDTO cfg = this.conFrmGrps[i];
                if ( null != cfg ){
                    hCfgs.add(cfg.toHibernate(hRefs));
                }
            }
        }
        
    }
    
    
    
    /*
     * Returns whether this document can have its data imported from an external source.
     */
	public boolean getIsImportEnabled() {
		return isImportEnabled;
	}
	
	public void setIsImportEnabled(boolean isImportEnabled) {
		this.isImportEnabled = isImportEnabled;
	}
	
	public String getImportMappingString() {
		return this.importMappingString;
	}

	public void setImportMappingString(String mappingString) {
		this.importMappingString = mappingString;
	}
}
