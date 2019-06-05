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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.psygrid.data.query.IEntryStatement;
import org.psygrid.data.query.QueryOperation;
import org.psygrid.data.query.QueryStatementValue;
import org.psygrid.security.RBACAction;

/**
 * Class to represent an Entry-type Element in a DataSet.
 * <p>
 * An entry-type element is one for which a single item of data
 * is to be collected when a record based on the DataSet is created.
 * 
 * @author Rob Harper
 * 
 * @hibernate.joined-subclass table="t_entrys"
 * @hibernate.joined-subclass-key column="c_id"
 */
public abstract class Entry extends Element {

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
	protected Section section;

	/**
	 * Flag to mark whether the entry is optional or not.
	 */
	protected EntryStatus entryStatus;

	/**
	 * The toString representation of the RBACAction, which if present, will
	 * determine whether a user can view a response to this entry.
	 *
	 * If null, it is assumed that the entry is viewable by any user
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
	 * Set after the EditableAction has been checked, and then used by CoCoA
	 */
	protected EditAction editingPermitted;

    /**
     * If True then the entry has been locked, and therefore
     * effectively removed from use.
     */
    protected boolean locked = false;
	
	/**
	 * Default no-arg constructor, as required by the Hibernate framework
	 * for all persistable classes.
	 * <p>
	 * Scope is protected as all Elements must have a name
	 */
	protected Entry(){};

	/**
	 * Constructor that accepts the name of the new entry.
	 * <p>
	 * By default, all new entrys have a status of mandatory.
	 * 
	 * @param name The name of the Entry
	 */
	public Entry(String name){
		super(name);
		this.entryStatus = EntryStatus.MANDATORY;
	}

	/**
	 * Constructor that accepts the name and status of the new entry.
	 * 
	 * @param name The name of the entry.
	 * @param entryStatus The status of the entry.
	 */
	public Entry(String name, EntryStatus entryStatus){
		super(name);
		this.entryStatus = entryStatus;
	}

	/**
	 * Constructor that accepts the name and display text of 
	 * the new entry.
	 * 
	 * @param name The name of the entry.
	 * @param displayText The display text for the entry.
	 */
	public Entry(String name, String displayText){
		super(name, displayText);
		this.entryStatus = EntryStatus.MANDATORY;
	}

	/**
	 * Constructor that accepts the name, displayText and 
	 * status of the new entry.
	 * 
	 * @param name The name of the entry.
	 * @param displayText The display text for the new entry.
	 * @param entryStatus The status of the entry.
	 */
	public Entry(String name, String displayText, EntryStatus entryStatus){
		super(name, displayText);
		this.entryStatus = entryStatus;
	}

	/**
     * Get the label for the entry.
     * <p>
     * The label is intended to be used for displaying the question number 
     * of the entry within its section.
     * 
     * @return The label.
	 * @dynamic_xdoclet_EntryLabel@
	 */
	public String getLabel() {
		return label;
	}

    /**
     * Set the label for the entry.
     * <p>
     * The label is intended to be used for displaying the question number 
     * of the entry within its section.
     * 
     * @param label The label.
     */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
     * Retrieve the section that the entry is associated with.
     * 
     * @return The section.
	 * @dynamic_xdoclet_EntryToSectionRelationship@
	 */
	public Section getSection() {
		return section;
	}

    /**
     * Set the section that the entry is associated with.
     * <p>
     * Internally, the entry will be added to the new sections list of
     * entries and, if the entry is already associated with a section,
     * removed from the old sections list of entries.
     *
     * @param section The section.
     */
	public void setSection(Section section) {
		this.section = section;
	}

    /**
     * Generate a new response to the entry, for the given section
     * occurrence.
     * <p>
     * Note that for an entry for which one or more statuses are defined
     * the status of the generated response will be set by default to the 
     * status at index 0 in the list, thus ensuring that the response 
     * has a status for the its entire lifetime.
     * 
     * @param occurrence The section occurrence.
     * @return The new response.
     * @throws ModelException if the response cannot be created.
     */
    public abstract Response generateInstance(SectionOccurrence occurrence) throws ModelException;
	
    /**
     * Generate a new response to the entry, for the given section
     * occurrence instance.
     * <p>
     * Note that for an entry for which one or more statuses are defined
     * the status of the generated response will be set by default to the 
     * status at index 0 in the list, thus ensuring that the response 
     * has a status for the its entire lifetime.
     * 
     * @param secOccInst The section occurrence instance.
     * @return The new response.
     * @throws ModelException
     */
    public abstract Response generateInstance(SecOccInstance secOccInst) throws ModelException;

    
    /**
     * Create a response for the entry with a standard code value, and add it
     * to the document instance.
     * <p>
     * It is expected that one of the section occurrence and section occurrence
     * instance will ne non-<code>null</code> but not both.
     * 
     * @param docInst The document instance to add the response to.
     * @param secOcc The section occurrence for the response.
     * @param secOccInst The section occurrence instance for the response.
     * @param stdCode The standard code.
     */
    public abstract void applyStandardCode(DocumentInstance docInst, SectionOccurrence secOcc, SecOccInstance secOccInst, StandardCode stdCode);
    
    /**
     * Get the status of the entry.
     * <p>
     * The status indicated whether the entry is mandatory, disabled, etc.
     * 
     * @return The status of the entry.
     */
	public EntryStatus getEntryStatus() {
		return entryStatus;
	}

    /**
     * Set the status of the entry.
     * <p>
     * The status indicated whether the entry is mandatory, disabled, etc.
     * 
     * @param entryStatus The status of the entry.
     */
	public void setEntryStatus(EntryStatus entryStatus) {
		this.entryStatus = entryStatus;
	}

	/**
	 * Get the string value of the enumerated entry status.
	 * <p>
	 * Only used by Hibernate to persist the string value of the enumerated
	 * entry status.
	 * 
	 * @return The string value of the enumerated entry status
	 * 
	 * @dynamic_xdoclet_EntryStatus@
	 */
	protected String getEnumEntryStatus() {
		if ( null == entryStatus ){
			return null;
		}
		else{
			return entryStatus.toString();
		}
	}

	/**
	 * Set the string value of the enumerated entry status.
	 * <p>
	 * Only used by Hibernate to un-persist the string value of the enumerated
	 * entry status.
	 * 
	 * @param enumEntryStatus The string value of the enumerated entry status.
	 */
	protected void setEnumEntryStatus(String enumEntryStatus) {
		if ( null == enumEntryStatus ){
			entryStatus = null;
		}
		else{
			entryStatus = EntryStatus.valueOf(enumEntryStatus);
		}
	}

	/**
	 * Get the toString representation of the RBACAction, which if present, will
	 * determine whether a user can view a response to this entry.
	 *
	 * If null, it is assumed that the entry is viewable by any user
	 * who can access it, this is for backwards compatibility purposes.
	 * 
	 * @return accessAction
	 * @hibernate.property column="c_can_access_action"
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
	 * Set the toString representation of the RBACAction, which if present, will
	 * determine whether a user can view a response to this entry.
	 *
	 * If null, it is assumed that the entry is viewable by any user
	 * who can access it, this is for backwards compatibility purposes.
	 * 
	 * @param accessAction
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
	 * Get the toString representation of the RBACAction used to allow users to view 
	 * responses created by this entry.
	 * 
	 * This should be the same as the entry's accessAction but include group access
	 * restrictions.
	 * 
	 * @hibernate.property column="c_can_access_response_action"
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
	 * Set the RBACAction used to allow users to view responses created by this entry.
	 * 
	 * This should be the same as the entry's accessAction but include group access
	 * restrictions.
	 * 
	 * @param responseAccessAction
	 */
	public void setResponseAccessAction(RBACAction responseAccessAction) {
		if (responseAccessAction == null) {
			this.responseAccessAction = null;
		}
		else {
			this.responseAccessAction = responseAccessAction.toString();
		}
	}

	/**
	 * Get the toString representation of the RBACAction, which if present, will 
	 * enable the relevant users to edit the entry.
	 *
	 * If null, it is assumed that the entry is editable for any user
	 * who can access it, this is for backwards compatibility purposes.
	 * 
	 * @return editableAction
	 * @hibernate.property column="c_can_edit_action"
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
	 * Set the RBACAction, which if present, will enable the relevant users to 
	 * edit the entry.
	 *
	 * If null, it is assumed that the entry is editable for any user
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
	 * Get the toString representation of the RBACACtion used to allow users to
	 * edit responses created by this entry.
	 * 
	 * This should be the same as the entry's editableAction but include
	 * group access restrictions.
	 * 
	 * @return responseEditableAction
	 * @hibernate.property column="c_can_edit_response_action"
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
	 * Set the RBACACtion used to allow users to edit responses created by this 
	 * entry.
	 * 
	 * This should be the same as the entry's editableAction but include
	 * group access restrictions.
	 * 
	 * @param responseEditableAction
	 */
	public void setResponseEditableAction(RBACAction responseEditableAction) {
		if (responseEditableAction == null) {
			this.responseEditableAction = null;
		}
		else {
			this.responseEditableAction = responseEditableAction.toString();
		}
	}

	/**
	 * Get the non persisted reference to whether this entry can be viewed 
	 * and edited, based on the editable action and the user's role.
     *
     * Used by the data entry system to determine how to display this 
     * document instance.
	 * 
	 * @return EditAction
	 */
	public EditAction getEditingPermitted() {
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
	public void setEditingPermitted(EditAction editingPermitted) {
		this.editingPermitted = editingPermitted;
	}

	public abstract org.psygrid.data.model.dto.EntryDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth);

	public org.psygrid.data.model.dto.ElementDTO toDTO(){
		//create list to hold references to objects in the element's
		//object graph which have multiple references to them within
		//the object graph. This is used so that each object instance
		//is copied to its DTO equivalent once and once only
		Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
		org.psygrid.data.model.dto.ElementDTO elem = toDTO(dtoRefs, RetrieveDepth.DS_COMPLETE);
		dtoRefs = null;
		return elem;
	}

	public void toDTO(org.psygrid.data.model.dto.EntryDTO dtoE, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
		super.toDTO(dtoE, dtoRefs, depth);
		if ( depth != RetrieveDepth.REP_SAVE){
			dtoE.setAccessAction(accessAction);
			dtoE.setEditableAction(editableAction);
			dtoE.setLocked(locked);
			
			if (RetrieveDepth.DS_WITH_DOCS != depth) {
				dtoE.setLabel(this.label);
				dtoE.setResponseAccessAction(responseAccessAction);
				dtoE.setResponseEditableAction(responseEditableAction);

				if (null != this.editingPermitted) {
					dtoE.setEditingPermitted(editingPermitted.toString());
				}
				if ( null != this.entryStatus ){
					dtoE.setEntryStatus(this.entryStatus.toString());
				}
			}
			if ( null != this.section ){
				dtoE.setSection(this.section.toDTO(dtoRefs, depth));
			}
		}
	}

	protected void checkSectionOccurrence(SectionOccurrence occurrence) throws ModelException {
		//check that the section occurrence is valid
		boolean validSecOcc = false;
		for ( SectionOccurrence secOcc: this.section.getOccurrences()){
			if ( secOcc.equals(occurrence) ){
				validSecOcc = true;
				break;
			}
		}
		if ( !validSecOcc ){
			throw new ModelException("Section occurrence in the argument is not valid for this entry.");
		}
		//check that the referenced sec occ doe not allow multiple runtime instances
		if ( occurrence.isMultipleAllowed() ){
			throw new ModelException("Section occurrence in the argument allows "+
			"multiple runtime instances - IResponse#generateInstance(ISecOccInstance) should be used instead.");
		}
	}

	protected void checkSecOccInstance(SecOccInstance secOccInst) throws ModelException {
		//check that the section occurrence instance references a valid 
		//section occurrence
		boolean validSecOccInst = false;
		for ( SectionOccurrence secOcc: this.section.getOccurrences()){
			if ( secOcc.equals(secOccInst.getSectionOccurrence()) ){
				validSecOccInst = true;
				break;
			}
		}
		if ( !validSecOccInst ){
			throw new ModelException("Section occurrence instance in the argument does not "+
			"reference a valid section occurrence for this entry.");
		}
		//check that the referenced sec occ allows multiple runtime instances
		if ( !secOccInst.getSectionOccurrence().isMultipleAllowed() ){
			throw new ModelException("Section occurrence instance in the argument references "+
			"a section occurrence that does not permit creation of multiple runtime instances.");
		}
	}

	/**
	 * Indicate whether response to this type of entry are suitable for
	 * basic statistical analysis (e.g. are numeric).
	 * 
	 * @return Boolean True is response to this type of entry are suitable for
	 * basic statistical analysis (e.g. are numeric)
	 */
	public boolean isForBasicStatistics(){
		return false;
	}
	
    /**
     * Get the value of the locked flag.
     * <p>
     * If True then the entry has been locked, 
     * effectively removed from use.
     * @return The value of the locked flag.
     * @hibernate.property column="c_locked"
     */
    public boolean isLocked() {
		return locked;
	}

    /**
     * Set the value of the locked flag.
     * <p>
     * If True then the entry has been locked, and 
     * effectively removed from use
     * 
     * @param locked The value of the locked flag.
     */
	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	/**
	 * Get the list of {@link QueryOperation} supported by query statements
	 * against the entry.
	 * 
	 * @return The list of {@link QueryOperation}
	 */
	public abstract List<QueryOperation> getQueryOperations();
	
	/**
	 * Get whether it is possible to run query statements against the entry.
	 * 
	 * @return The is queryable flag.
	 */
	public boolean isQueryable() {
		return true;
	}
	
	/**
	 * Create a statement object
	 * @param queryStatementValue	Object containing the value to put in the created statement
	 * @return						The created object
	 */
	public abstract IEntryStatement createStatement(QueryStatementValue queryStatementValue);

}
