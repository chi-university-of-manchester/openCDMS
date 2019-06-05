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

import org.psygrid.data.model.hibernate.EditAction;
import org.psygrid.data.model.hibernate.ResponseStatus;

/**
 * Class to represent a Response to an Entry.
 * 
 * @author Rob Harper
 * 
 */
public abstract class ResponseDTO extends ElementInstanceDTO {

    /**
     * The id of the entry that this response relates to.
     */
    protected Long entryId;
    
    /**
     * The id of the section occurrence that this response relates to.
     */
    protected Long sectionOccurrenceId;
    
    /**
     * The section occurrence instance that this response
     * relates to.
     */
    protected SecOccInstanceDTO secOccInstance;
    
    protected String status;
    
    protected String annotation;

    protected DocumentInstanceDTO docInstance;

	/**
	 * The toString representation of the RBACAction, which if present, will 
	 * determine whether the user can view the response.
	 *
	 * If null, it is assumed that the response is viewable by any user
	 * who can access it, this is for backwards compatibility purposes.
	 */
	protected String accessAction;
	
	/**
	 * The toString representation of the RBACAction, which if present, will enable
	 * the relevant users to edit the response.
	 *
	 * If null, it is assumed that the response is editable for any user
	 * who can access it, this is for backwards compatibility purposes.
	 */
	protected String editableAction;

	/**
	 * A non persisted reference to whether this entry can be edited
	 * or is to be viewed read-only.
	 *
	 * Set after the editableAction has been checked, and then used by CoCoA
	 */
	protected String editingPermitted;

    protected boolean deleted;
    
	/**
	 * Default no-arg constructor, as required by the Hibernate framework
	 * for all persistable classes.
	 */
	public ResponseDTO(){};

    public Long getEntryId() {
        return entryId;
    }

    public void setEntryId(Long entryId) {
        this.entryId = entryId;
    }

    public Long getSectionOccurrenceId() {
        return sectionOccurrenceId;
    }

    public void setSectionOccurrenceId(Long sectionOccurrenceId) {
        this.sectionOccurrenceId = sectionOccurrenceId;
    }

    public SecOccInstanceDTO getSecOccInstance() {
        return secOccInstance;
    }

    public void setSecOccInstance(SecOccInstanceDTO secOccInstance) {
        this.secOccInstance = secOccInstance;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String invalidAnnotation) {
        this.annotation = invalidAnnotation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public DocumentInstanceDTO getDocInstance() {
        return docInstance;
    }

    public void setDocInstance(DocumentInstanceDTO docInstance) {
        this.docInstance = docInstance;
    }

    public String getAccessAction() {
    	return accessAction;
    }
    
    public void setAccessAction(String accessAction) {
    	this.accessAction = accessAction;
    }
    
	public String getEditableAction() {
		return editableAction;
	}

	public void setEditableAction(String editableAction) {
		this.editableAction = editableAction;
	}

	public String getEditingPermitted() {
		return editingPermitted;
	}

	public void setEditingPermitted(String editingPermitted) {
		this.editingPermitted = editingPermitted;
	}
    
    public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public abstract org.psygrid.data.model.hibernate.Response toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs);
    
    public void toHibernate(org.psygrid.data.model.hibernate.Response hR, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        super.toHibernate(hR, hRefs);
        hR.setAccessAction(accessAction);
        hR.setEditableAction(editableAction);
        hR.setDeleted(deleted);
		if (null != this.editingPermitted) {
			hR.setEditingPermitted(EditAction.valueOf(editingPermitted));
		}
        if ( null != this.status ){
            hR.setStatus(ResponseStatus.valueOf(this.status));
        }
        hR.setAnnotation(this.annotation);
        if ( null != this.entryId ){
            hR.setEntryId(this.entryId);
        }
        
        if ( null != this.sectionOccurrenceId ){
            hR.setSectionOccurrenceId(this.sectionOccurrenceId);
        }

        if ( null != this.secOccInstance ){
            hR.setSecOccInstance(this.secOccInstance.toHibernate(hRefs));
        }
        if ( null != this.docInstance ){
            hR.setDocInstance(this.docInstance.toHibernate(hRefs));
        }
    }
    
}
