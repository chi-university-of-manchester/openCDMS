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

import java.util.Map;

import org.psygrid.security.RBACAction;

/**
 * Class to represent a Response to an Entry.
 * 
 * @author Rob Harper
 * 
 * @hibernate.joined-subclass table="t_responses"
 * @hibernate.joined-subclass-key column="c_id"
 */
public abstract class Response extends ElementInstance {

    private static final String STATUS_PROPERTY = "status";

    /**
     * The entry that this response relates to.
     */
    protected Entry entry;
    
    protected Long entryId;
    
    /**
     * The section occurrence that this response relates to.
     */
    protected SectionOccurrence sectionOccurrence;
    
    protected Long sectionOccurrenceId;
    
    /**
     * The section occurrence instance that this response
     * relates to.
     */
    protected SecOccInstance secOccInstance;
    
    /**
     * Response status.
     */
    protected ResponseStatus status;
    
    /**
     * String that can be used to store an annotation detailing
     * why the response has been flagged as invalid.
     */
    protected String annotation;
    
    /**
     * The document instance that the Response is a part of.
     */
    protected DocumentInstance docInstance;
    
    /**
     * The toString representation of the RBACAction, which if present, will 
	 * determine whether the user can view this response.
     *
     * If null, it is assumed that the entry is viewable for any user
     * who can access it, this is for backwards compatibility purposes.
     */
    protected String accessAction;
    
    /**
     * The toString representation of the RBACAction, which if present, will 
     * enable the relevant users to edit the entry.
     *
     * If null, it is assumed that the entry is editable for any user
     * who can access it, this is for backwards compatibility purposes.
     */
    protected String editableAction;
   
    /**
     * A non persisted reference to whether this entry can be edited
     * or is to be viewed read-only.
     *
     * Set after the EditableAction has been checked, and then used by CoCoA
     */
    protected EditAction editingPermitted;
    
    protected boolean deleted;
    
	/**
	 * Default no-arg constructor, as required by the Hibernate framework
	 * for all persistable classes.
	 */
	public Response(){};
    
    /**
     * Get the entry that this response relates to.
     * 
     * @return The entry.
     * @hibernate.many-to-one class="org.psygrid.data.model.hibernate.Entry"
     *                        column="c_entry_id"
     *                        not-null="true"
     *                        cascade="none"
     */
    public Entry getEntry() {
        return entry;
    }

    public void setEntry(Entry entry) {
        this.entry = entry;
    }

    public Long getEntryId() {
        return entryId;
    }

    public void setEntryId(Long entryId) {
        this.entryId = entryId;
    }

    /**
     * Get the section occurrence that this response relates to.
     * 
     * @return The section occurrence.
     * @hibernate.many-to-one class="org.psygrid.data.model.hibernate.SectionOccurrence"
     *                        column="c_sec_occ_id"
     *                        not-null="false"
     *                        cascade="none"
     */
    public SectionOccurrence getSectionOccurrence() {
        return sectionOccurrence;
    }

    public void setSectionOccurrence(SectionOccurrence sectionOccurrence) {
        this.sectionOccurrence = sectionOccurrence;
    }

    public Long getSectionOccurrenceId() {
        return sectionOccurrenceId;
    }

    public void setSectionOccurrenceId(Long sectionOccurrenceId) {
        this.sectionOccurrenceId = sectionOccurrenceId;
    }

    /**
     * Get the section occurrence instance that this response relates to.
     * 
     * @return The section occurrence instance.
     * @hibernate.many-to-one class="org.psygrid.data.model.hibernate.SecOccInstance"
     *                        column="c_sec_occ_inst_id"
     *                        not-null="false"
     *                        cascade="none"
     */
    public SecOccInstance getSecOccInstance() {
        return secOccInstance;
    }

    public void setSecOccInstance(SecOccInstance secOccInstance) {
        this.secOccInstance = secOccInstance;
    }

    /**
     * Get the status of the response.
     * 
     * @return The status of the response.
     */
    public ResponseStatus getStatus() {
        return this.status;
    }

    /**
     * Set the status of the response.
     * 
     * @param status The status of the response.
     */
    public void setStatus(ResponseStatus status) {
        ResponseStatus oldStatus = this.status;
        this.status = status;
        propertyChangeSupport.firePropertyChange(STATUS_PROPERTY, oldStatus,
                this.status);
    }

    /**
     * Get the string value of the enumerated status.
     * <p>
     * Only used by Hibernate to persist the string value of the 
     * enumerated status.
     * 
     * @return The string value of the enumerated status
     * 
     * @hibernate.property column="c_status"
     */
    protected String getEnumStatus() {
        if ( null == status ){
            return null;
        }
        else{
            return status.toString();
        }
    }

    /**
     * Set the string value of the enumerated status.
     * <p>
     * Only used by Hibernate to un-persist the string value of the 
     * enumerated status.
     * 
     * @param enumStatus The string value of the enumerated status.
     */
    protected void setEnumStatus(String enumStatus) {
        if ( null == enumStatus ){
            this.status = null;
        }
        else{
            this.status = ResponseStatus.valueOf(enumStatus);
        }
    }
    
    /**
     * Get the annotation of the response.
     * 
     * @return The annotation of the response.
     * @hibernate.property column="c_inv_annot"
     *                     type="text"
     *                     length="4000"
     */
    public String getAnnotation() {
        return annotation;
    }

    /**
     * Set the annotation of the response.
     * 
     * @return The annotation of the response.
     */
    public void setAnnotation(String invalidAnnotation) {
        this.annotation = invalidAnnotation;
    }

    /**
     * 
     * @return
     * 
     * @hibernate.many-to-one class="org.psygrid.data.model.hibernate.DocumentInstance"
     *                        column="c_doc_inst_id"
     *                        not-null="false"
     *                        cascade="none"
     */
    public DocumentInstance getDocInstance() {
        return docInstance;
    }

    public void setDocInstance(DocumentInstance docInstance) {
        this.docInstance = docInstance;
    }

	/**
	 * Get the toString representation of the RBACAction, which if present, 
	 * will determine whether the user can view a response to this entry.
	 * 
     * If null, it is assumed that the response is viewable for any user
     * who can access it, this is for backwards compatibility purposes.
     * 
	 * @return string
	 * @hibernate.property column="c_can_access_action"
	 */
	public String getAccessAction() {
		return accessAction;
	}

	public void setAccessAction(String accessAction) {
		this.accessAction = accessAction;
	}
	
    /**
     * Set the RBACAction to determine whether the user can view 
     * a response to this entry.
     *
     * If null, it is assumed that the response is viewable by any user
     * who can access it, this is for backwards compatibility purposes.
     * 
     * @param editableAction
     */
	public void setAccessAction(RBACAction accessAction) {
		if (accessAction == null) {
			this.accessAction = null;
		}
		else {
			this.accessAction = accessAction.toString();
		}
	}
    
	/**
	 * Get the toString representation of the RBACAction, which if present, 
	 * will determine whether the user can view and edit the response.
     * 
     * The editable action is set by the entry belonging to this response.
     *
     * If null, it is assumed that the response is editable for any user
     * who can access it, this is for backwards compatibility purposes.
     * 
	 * @return string
	 * @hibernate.property column="c_can_edit_action"
	 */
	public String getEditableAction() {
		return editableAction;
	}

	public void setEditableAction(String editableAction) {
		this.editableAction = editableAction;
	}
	
    /**
     * Set the RBACAction to determine whether the user can view and edit 
     * the response. This is determined by the entry belonging to this 
     * response.
     *
     * If null, it is assumed that the response is editable for any user
     * who can access it, this is for backwards compatibility purposes.
     * 
     * @param editableAction
     */
	public void setEditableAction(RBACAction editableAction) {
		if (editableAction == null) {
			this.editableAction = null;
		}
		else {
			this.editableAction = editableAction.toString();
		}
	}

	/**
	 * Get the non persisted reference to whether this response can be edited,
     * is to be viewed read-only or is not to be viewed at all, based on the 
     * editable action and the user's role.
	 * Temporary variable, not persisted by hibernate
     * 
	 * @return boolean
	 * 
	 * @return isEditable
	 */
	public EditAction getEditingPermitted() {
		return editingPermitted;
	}

	/**
	 * Set the non persisted reference to whether this response can be edited,
     * is to be viewed read-only or is not to be viewed at all, based on the 
     * editable action and the user's role.
     * 
	 * @param boolean
	 */
	public void setEditingPermitted(EditAction editingPermitted) {
		this.editingPermitted = editingPermitted;
	}
    
	/**
	 * @hibernate.property column="c_deleted"
	 */
    public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public abstract org.psygrid.data.model.dto.ResponseDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth);
    
    public void toDTO(org.psygrid.data.model.dto.ResponseDTO dtoR, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        super.toDTO(dtoR, dtoRefs, depth);
        
        if ( null != this.status ){
            dtoR.setStatus(this.status.toString());
        }
        dtoR.setAnnotation(this.annotation);
        dtoR.setAccessAction(accessAction);
        dtoR.setEditableAction(editableAction);
        dtoR.setDeleted(deleted);
        if (null != this.editingPermitted) {
        	dtoR.setEditingPermitted(editingPermitted.toString());
        }
        if ( null != this.entry ){
            dtoR.setEntryId(this.entry.getId());
        }
        else {
        	dtoR.setEntryId(this.entryId);
        }
        
        if ( null != this.sectionOccurrence ){
            dtoR.setSectionOccurrenceId(this.sectionOccurrence.getId());
        }
        else {
        	 dtoR.setSectionOccurrenceId(this.sectionOccurrenceId);
        }
        
        if ( null != this.secOccInstance ){
            dtoR.setSecOccInstance(this.secOccInstance.toDTO(dtoRefs, depth));
        }
        
        if ( null != this.docInstance ){
            dtoR.setDocInstance(this.docInstance.toDTO(dtoRefs, depth));
        }
    }

    @Override
    protected Element findElement() {
        return this.entry;
    }
    
    public void attach(Entry ent){
        
        this.entry = ent;
        this.entryId = null;
        
        Long secOccId = null;
        if ( null != this.sectionOccurrenceId ){
            secOccId = this.sectionOccurrenceId;
        }
        else if ( null != this.sectionOccurrence ){
            //preserve backwards compatability with records detached
            //prior to the introduction of Response.sectionOccurrenceId
            secOccId = this.sectionOccurrence.getId();
        }
        if ( null != secOccId ){
            boolean attached = false;
            if ( ent.getSection() != null ){
                for ( SectionOccurrence so: ent.getSection().getOccurrences() ){
                    if ( secOccId.equals(so.getId()) ){
                        sectionOccurrence = so;
                        sectionOccurrenceId = null;
                        attached = true;
                        break;
                    }
                    
                }
            }
            if ( !attached ){
                throw new ModelException("Failed to attach section occurrence of entry id="+this.getId()+" - no section occurrence exists with id="+secOccId);
            }
        }
        
    }
    
    public void detach() throws ModelException {
    	if(null != this.entry){
	        this.entryId = this.entry.getId();
	        this.entry = null;
    	}
        
        if ( null != this.sectionOccurrence ){
            this.sectionOccurrenceId = this.sectionOccurrence.getId();
            this.sectionOccurrence = null;
        }
            
    }
    
    /**
     * Record the current state of the Response.
     */
    public abstract void recordCurrentState();

    /**
     * Check for any changes in the state of the Response
     * since the last call to storeCurrentValues
     * 
	 * @param change The ChangeHistory object that represents the changes
	 * made during this editing session.
     */
    public abstract void checkForChanges(ChangeHistory change);
}
