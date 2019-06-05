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
import org.psygrid.data.model.hibernate.EntryStatus;

/**
 * Class to represent an Entry-type Element in a DataSet.
 * 
 * @author Rob Harper
 */
public abstract class EntryDTO extends ElementDTO {

	/**
	 * The label for the entry.
	 * <p>
	 * The label is intended to be used for displaying the question number 
	 * of the entry within its section.
	 */
	protected String label;

	/**
	 * The section that the entry is associated with.
	 */
	protected SectionDTO section;

	/**
	 * Flag to mark whether the entry is optional or not.
	 */
	protected String entryStatus;

	/**
	 * The toString representation of the RBACAction, which if present, will 
	 * determine whether the user can view a response to this entry.
	 *
	 * If null, it is assumed that the entry is viewable for any user
	 * who can access it, this is for backwards compatibility purposes.
	 */
	protected String accessAction;

	/**
	 * The toString representation of the RBACAction used to allow users to view 
	 * responses created by this entry.
	 * 
	 * This should be the same as the entry's accessAction but include group access
	 * restrictions.
	 */
	protected String responseAccessAction;

	/**
	 * The toString representation of the RBACAction, which if present, will enable
	 * the relevant users to edit the entry.
	 *
	 * If null, it is assumed that the entry is editable for any user
	 * who can access it, this is for backwards compatibility purposes.
	 */
	protected String editableAction;

	/**
	 * The toString representation of the RBACACtion used to allow users to
	 * edit responses created by this entry.
	 * 
	 * This should be the same as the entry's editableAction but include
	 * group access restrictions.
	 */
	protected String responseEditableAction;

	/**
	 * A non persisted reference to whether this entry can be edited
	 * or is to be viewed read-only.
	 *
	 * Set after the editableAction has been checked, and then used by CoCoA
	 */
	protected String editingPermitted;

    /**
     * If True then the entry has been locked, and therefore
     * effectively removed from use.
     */
	private boolean locked = false;

	/**
	 * Default no-arg constructor, as required by the Hibernate framework
	 * for all persistable classes.
	 * <p>
	 * Scope is protected as all Elements must have a name
	 */
	public EntryDTO(){};

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getEntryStatus() {
		return entryStatus;
	}

	public void setEntryStatus(String entryStatus) {
		this.entryStatus = entryStatus;
	}

	public SectionDTO getSection() {
		return section;
	}

	public void setSection(SectionDTO section) {
		this.section = section;
	}

	/**
	 * Get the toString representation of the RBACAction, which if present, will
	 * determine whether a user can view a response to this entry.
	 *
	 * If null, it is assumed that the entry is viewable by any user
	 * who can access it, this is for backwards compatibility purposes.
	 * 
	 * @return accessAction
	 */
	public String getAccessAction() {
		return accessAction;
	}

	/**
	 * She toString representation of the RBACAction, which if present, will
	 * determine whether a user can view a response to this entry.
	 *
	 * If null, it is assumed that the entry is viewable by any user
	 * who can access it, this is for backwards compatibility purposes.
	 * 
	 * @param accessAction
	 */
	public void setAccessAction(String accessAction) {
		this.accessAction = accessAction;
	}

	/**
	 * Get the toString representation of the RBACAction used to allow users to view 
	 * responses created by this entry.
	 * 
	 * This should be the same as the entry's accessAction but include group access
	 * restrictions.
	 * 
	 * @return responseAccessAction
	 */
	public String getResponseAccessAction() {
		return responseAccessAction;
	}

	/**
	 * Set the toString representation of the RBACAction used to allow users to view 
	 * responses created by this entry.
	 * 
	 * This should be the same as the entry's accessAction but include group access
	 * restrictions.
	 * 
	 * @param responseAccessAction
	 */
	public void setResponseAccessAction(String responseAccessAction) {
		this.responseAccessAction = responseAccessAction;
	}

	/**
	 * Get the toString representation of the RBACAction, which if present, will 
	 * enable the relevant users to edit the entry.
	 *
	 * If null, it is assumed that the entry is editable for any user
	 * who can access it, this is for backwards compatibility purposes.
	 * 
	 * @return editableAction
	 */
	public String getEditableAction() {
		return editableAction;
	}

	/**
	 * Set the toString representation of the RBACAction, which if present, will 
	 * enable the relevant users to edit the entry.
	 *
	 * If null, it is assumed that the entry is editable for any user
	 * who can access it, this is for backwards compatibility purposes.
	 * 
	 * @param editableAction
	 */
	public void setEditableAction(String editableAction) {
		this.editableAction = editableAction;
	}

	/**
	 * Get the toString representation of the RBACACtion used to allow users to
	 * edit responses created by this entry.
	 * 
	 * This should be the same as the entry's editableAction but include
	 * group access restrictions.
	 * 
	 * @return responseEditableAction
	 */
	public String getResponseEditableAction() {
		return responseEditableAction;
	}

	/**
	 * Set the toString representation of the RBACACtion used to allow users to
	 * edit responses created by this entry.
	 * 
	 * This should be the same as the entry's editableAction but include
	 * group access restrictions.
	 * 
	 * @param responseEditableAction
	 */
	public void setResponseEditableAction(String responseEditableAction) {
		this.responseEditableAction = responseEditableAction;
	}

	/**
	 * Get the non persisted reference to whether this entry can be viewed 
	 * and edited, based on the editable action and the user's role.
	 *
	 * Used by the data entry system to determine how to display this 
	 * document instance.
	 * 
	 * @return editingPermitted
	 */
	public String getEditingPermitted() {
		return editingPermitted;
	}

	/**
	 * Set the non persisted reference to whether this entry can be edited
	 * or is to be viewed read-only, based on the editable action and the
	 * user's role.
	 *
	 * Used by the data entry system to determine how to display this 
	 * document instance.
	 * 
	 * @param boolean
	 */
	public void setEditingPermitted(String editingPermitted) {
		this.editingPermitted = editingPermitted;
	}

	public abstract org.psygrid.data.model.hibernate.Entry toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs);

	public void toHibernate(org.psygrid.data.model.hibernate.Entry hE, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
		super.toHibernate(hE, hRefs);
		hE.setLabel(this.label);
		hE.setAccessAction(accessAction);
		hE.setResponseAccessAction(responseAccessAction);
		hE.setEditableAction(editableAction);
		hE.setResponseEditableAction(responseEditableAction);
		hE.setLocked(locked);
		
		if (null != this.editingPermitted) {
			hE.setEditingPermitted(EditAction.valueOf(editingPermitted));
		}
		if ( null != this.entryStatus ){
			hE.setEntryStatus(EntryStatus.valueOf(this.entryStatus));
		}

		if ( null != this.section ){
			hE.setSection(this.section.toHibernate(hRefs));
		}
	}
	
    public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}
}
