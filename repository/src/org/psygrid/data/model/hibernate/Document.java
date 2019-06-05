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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.psygrid.data.model.dto.ElementDTO;
import org.psygrid.security.RBACAction;

/**
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_documents"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class Document extends StatusedElement {

	/*
	 * FOR FUTURE
	private ArrayList<DocumentImportFeature> requiredImportParserFeatures = null;
	 */

	/*
	 * If import-enabled, this is the mapping string for porting an external document source into an internal document.
	 */
	private String importMappingString;

	/**
	 * Determines whether the document is import-enabled.
	 */
	private boolean isImportEnabled = false;

	/**
	 * The collection of entries that are contained by the document.
	 */
	private List<Entry> entries = new ArrayList<Entry>();

	/**
	 * The collection of sections that are associated with the document.
	 * <p>
	 * Sections are intended to logically divide up the entries in a document
	 * so as to give it structure. Each entry in a document's collection of
	 * entries must be associated with one of the document's sections.
	 */
	private List<Section> sections = new ArrayList<Section>();

	/**
	 * Collection of occurrences for the document.
	 * <p>
	 * When a document has occurrences there may be multiple
	 * instances of it in a single record, with the number of
	 * instances being limited to the number of occurrences defined
	 * in this list.
	 * <p>
	 * A document with no occurrences defined my only have one
	 * instance in each record (i.e. equivalent to having a single
	 * occurrence defined).
	 */
	protected List<DocumentOccurrence> occurrences = new ArrayList<DocumentOccurrence>();

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
	protected List<ConsentFormGroup> conFrmGrps = new ArrayList<ConsentFormGroup>();

	/**
	 * If this document is involved in dual data entry as part of the secondary
	 * dataset, the index of the document in the primary dataset that it is associated
	 * with. Otherwise <code>null</code>.
	 */
	protected Long primaryDocIndex;

	/**
	 * If this document is involved in dual data entry as part of the primary
	 * dataset, the index of the document in the secondary dataset that it is associated
	 * with. Otherwise <code>null</code>.
	 */
	protected Long secondaryDocIndex;

	/**
	 * The toString representation of the RBACAction used to control access to this
	 * document 
	 */
	protected String action;

	/**
	 * The toString representation of the RBACAction used to control access to any 
	 * document instances created by this document.
	 * 
	 * This should be the same as the document's action but include group access
	 * restrictins.
	 */
	protected String instanceAction;

	/**
	 * The toString representation of the RBACAction, which if present, will enable
	 * the relevant users to edit any document instance. 
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
	 * This should be the same as the document's editableAction but include group access
	 * restrictions.
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

	/**
	 * If True, implies that the document is anticipated to be 
	 * completed over a long period of time, rather than all at once.
	 */
	private boolean longRunning;

	public Document() {
		super();
	}

	public Document(String name) {
        super(name);
	}

	public Document(String name, String displayText) {
        super(name, displayText);
	}

	/**
	 * Get the collection of entries that are contained by the document.
	 * 
	 * @return The collection of entries.
	 * 
	 * @dynamic_xdoclet_DocumentEntryRelationship@
	 */
	public List<Entry> getEntries() {
		return entries;
	}

	/**
	 * Set the collection of entries that are contained by the document.
	 * 
	 * @param entries The collection of entries.
	 */
	public void setEntries(List<Entry> entries) {
		this.entries = entries;
	}

	/**
	 * Get the collection of sections that are associated with the document.
	 * <p>
	 * Sections are intended to logically divide up the entries in a document
	 * so as to give it structure. Each entry in a document's collection of
	 * entries must be associated with one of the document's sections.
	 * 
	 * @return The collection of sections.
	 * 
	 * @hibernate.list cascade="all" batch-size="100"
	 * @hibernate.one-to-many class="org.psygrid.data.model.hibernate.Section"
	 * @hibernate.key column="c_document_id" not-null="false"
	 * @hibernate.list-index column="c_index"
	 */
	public List<Section> getSections() {
		return sections;
	}

	/**
	 * Set the collection of sections that are associated with the document.
	 * <p>
	 * Sections are intended to logically divide up the entries in a document
	 * so as to give it structure. Each entry in a document's collection of
	 * entries must be associated with one of the document's sections.
	 * 
	 * @param sections The collection of sections.
	 */
	public void setSections(List<Section> sections) {
		this.sections = sections;
	}

	/**
	 * Get the collection of consent form groups associated with the 
	 * document.
	 * <p>
	 * If multiple consent form groups are associated with a document then 
	 * they are intended to have an AND relation i.e. for an instance
	 * of the element to be created in a record one of the
	 * all of the consent form groups in the collection must have been 
	 * completed in the positive.
	 * 
	 * @return The collection of consent form groups.
	 * 
	 * @hibernate.list cascade="none" 
	 *                 table="t_doc_cfgs" batch-size="100"
	 * @hibernate.key column="c_doc_id"
	 * @hibernate.many-to-many class="org.psygrid.data.model.hibernate.ConsentFormGroup"
	 *                         column="c_cfg_id"
	 * @hibernate.list-index column="c_index"
	 */
	public List<ConsentFormGroup> getConFrmGrps() {
		return conFrmGrps;
	}

	/**
	 * Set the collection of consent form groups associated with the 
	 * document.
	 * <p>
	 * If multiple consent form groups are associated with a document then 
	 * they are intended to have an AND relation i.e. for an instance
	 * of the element to be created in a record one of the
	 * all of the consent form groups in the collection must have been 
	 * completed in the positive.
	 * 
	 * @param conFrmGrps The collection of consent form groups.
	 */
	public void setConFrmGrps(List<ConsentFormGroup> conFrmGrps) {
		this.conFrmGrps = conFrmGrps;
	}

    /**
     * Set the consent form group associated with the document.
     * 
     * @param consentFormGroup The consent form group.
     */
	public void addConsentFormGroup(ConsentFormGroup consentFormGroup) throws ModelException {
		if ( null == consentFormGroup ){
			throw new ModelException("Cannot add a null consent form group");
		}
		conFrmGrps.add((ConsentFormGroup)consentFormGroup);
	}

    /**
     * Retrieve a single consent form group at the specified index in
     * the document's collection of consent form groups.
     * 
     * @param index The index in the collection of consent form groups.
     * @return The consent form group at the given index.
     * @throws ModelException if no consent form group exists for the
     * given index.
     */
	public ConsentFormGroup getConsentFormGroup(int index) throws ModelException {
		try{
			return conFrmGrps.get(index);
		}
		catch(IndexOutOfBoundsException ex){
			throw new ModelException("No consent form group exists for index "+index, ex);
		}
	}

    /**
     * Return the number of consent form groups associated with the document.
     * 
     * @return The number of consent form groups
     */
	public int numConsentFormGroups() {
		return conFrmGrps.size();
	}

    /**
     * Remove a single consent form group from
     * the document's collection of consent form groups.
     * 
     * @param index The index in the collection of consent form groups.
     * @throws ModelException if no consent form group exists for the
     * given index.
     */
    public void removeConsentFormGroup(int index) throws ModelException {
        try{
            conFrmGrps.remove(index);
        }
        catch(IndexOutOfBoundsException ex){
            throw new ModelException("No consent form group exists for index "+index, ex);
        }
    }

	/**
	 * Get the collection of occurrences for the document.
	 * <p>
	 * When a document has occurrences there may be multiple
	 * instances of it in a single record, with the number of
	 * instances being limited to the number of occurrences defined
	 * in this list.
	 * <p>
	 * A document with no occurrences defined my only have one
	 * instance in each record (i.e. equivalent to having a single
	 * occurrence defined).
	 * 
	 * @return The collection of occurrences.
	 * 
	 * @hibernate.list cascade="all" batch-size="100"
	 * @hibernate.one-to-many class="org.psygrid.data.model.hibernate.DocumentOccurrence"
	 * @hibernate.key column="c_doc_id"
	 *                not-null=@DEL_REP_DOC_TO_DOCOCC_SWITCH@
	 * @hibernate.list-index column="c_index_doc"
	 */
	public List<DocumentOccurrence> getOccurrences() {
		return occurrences;
	}

	/**
	 * Set the collection of occurrences for the document.
	 * <p>
	 * When a document has occurrences there may be multiple
	 * instances of it in a single record, with the number of
	 * instances being limited to the number of occurrences defined
	 * in this list.
	 * <p>
	 * A document with no occurrences defined my only have one
	 * instance in each record (i.e. equivalent to having a single
	 * occurrence defined).
	 * 
	 * @param occurrences The collection of occurrences.
	 */
	public void setOccurrences(List<DocumentOccurrence> occurrences) {
		this.occurrences = occurrences;
	}

    /**
     * Add a single occurrence to the collection of occurrences.
     * 
     * @param occurrence The occurrence to add.
     * @throws ModelException if a <code>null</code> occurrence is added.
     */
	public void addOccurrence(DocumentOccurrence occurrence) throws ModelException {
		if ( null == occurrence ){
			throw new ModelException("Cannot add a null occurrence");
		}
		DocumentOccurrence o = (DocumentOccurrence)occurrence;
		o.setDocument(this);
		occurrences.add(o);
	}

    /**
     * Retrieve a single occurrence from the collection of occurrences.
     * 
     * @param index The position in the collection of the occurrence to
     * retrieve.
     * @return The occurrence at the specified index.
     * @throws ModelException if no occurrence exists for the given index.
     */
	public DocumentOccurrence getOccurrence(int index) throws ModelException {
		try{
			return occurrences.get(index);
		}
		catch (IndexOutOfBoundsException ex){
			throw new ModelException("No occurrence found for index "+index, ex);
		}
	}

    /**
     * Get the number of occurrences associated with the element.
     * 
     * @return The number of occurrences.
     */
	public int numOccurrences() {
		return this.occurrences.size();
	}

    /**
     * Remove a single occurrence from the collection of occurrences.
     * 
     * @param index The position in the collection of the occurrence to
     * remove.
     * @throws ModelException if no occurrence exists for the given index.
     */
	public void removeOccurrence(int index) throws ModelException {
		try{
			DocumentOccurrence o = occurrences.remove(index);
			if ( null != o.getId() ){
				//the object being removed has previously been persisted
				//store it in the collection of deleted objects so that 
				//it may be manually deleted when the dataset is next saved
				if (myDataSet != null) {
					myDataSet.getDeletedObjects().add(o);
				}
			}
		}
		catch(IndexOutOfBoundsException ex){
			throw new ModelException("No occurrence found for index "+index, ex);
		}
	}

    /**
     * Return the number of entries belonging to the document.
     * 
     * @return The number of entries.
     */
	public int numEntries(){
		return entries.size();
	}

    /**
     * Method used to add an entry to the collection of entries associated
     * with the document.
     * 
     * @param entry The entry to add to the collection of entries.
     * 
     * @throws ModelException if the entry in the argument is <code>null</code>.
     */
	public void addEntry(Entry entry) throws ModelException{
		if ( null == entry ){
			throw new ModelException("Cannot add a null entry");
		}
		Entry e = (Entry)entry;        
		e.setMyDataSet(myDataSet);
		entries.add(e);
	}

    /**
     * Retrieve a single entry at the specified index in
     * the document's collection of entries.
     * 
     * @param index The index in the collection of entries.
     * @return The entry at the given index.
     * @throws ModelException if there is no entry for the given index.
     */
	public Entry getEntry(int index) throws ModelException{
		try{
			return entries.get(index);
		}
		catch (IndexOutOfBoundsException ex){
			throw new ModelException("No child Element found for index "+index, ex);
		}
	}
	
	/**
	 * Get an entry given its name
	 * @param name the entry name
	 * @return the entry or null of none exists
	 */
	public Entry getEntry(String name){
		Entry result = null;
		for(Entry entry: entries){
			if (entry.getName().equals(name)){
				result = entry;
				break;
			}
		}
		return result;
	}

    /**
     * Remove a single entry at the specified index from
     * the document's collection of entries.
     * 
     * @param index The index in the collection of entries.
     * @throws ModelException if there is no entry for the given
     * index.
     */
	public void removeEntry(int index) throws ModelException{
		try{
			Persistent p = entries.remove(index);
			if ( null != p.getId() ){
				//the object being removed has previously been persisted
				//store it in the collection of deleted objects so that 
				//it may be manually deleted when the dataset is next saved
				if(getDataSet() != null){
					getDataSet().getDeletedObjects().add(p);
				}
			}
		}
		catch(IndexOutOfBoundsException ex){
			throw new ModelException("No child Element found for index "+index, ex);
		}
	}

    /**
     * Move a single entry to a new index in the document's 
     * collection of entries.
     * 
     * @param currentPosition The current index of the entry to move.
     * @param newPosition The new index to move the entry to.
     * @throws ModelException if there is no entry for the given 
     * current index, or if the new index to move it to is not valid.
     */
	public void moveEntry(int currentIndex, int newIndex) throws ModelException {
		Entry e = null;
		try{
			e = entries.remove(currentIndex);
		}
		catch(IndexOutOfBoundsException ex){
			throw new ModelException("No child Element found for index "+currentIndex, ex);
		}
		try{
			entries.add(newIndex, e);
		}
		catch(IndexOutOfBoundsException ex){
			//roll back - re-insert child element to its old position
			entries.add(currentIndex, e);
			throw new ModelException("Cannot move child Element to index "+newIndex+" - invalid index", ex);
		}

	}

    /**
     * Method used to insert a entry into the collection of entries
     * associated with the document, at the specified index.
     * 
     * @param entry The entry to insert into the collection of entries.
     * @param index The index in the collection of entries to insert the
     * entry at.
     * @throws ModelException if the specified index is not valid; 
     * if the entry in the argument is <code>null</code>.
     */    
	public void insertEntry(Entry entry, int index) throws ModelException {
		if ( null == entry ){
			throw new ModelException("Cannot insert a null child");
		}
		Entry e = (Entry)entry;
		e.setMyDataSet(myDataSet);
		try{
			entries.add(index,e);
		}
		catch(IndexOutOfBoundsException ex){
			throw new ModelException("Cannot insert child Element at index "+index+" - invalid index", ex);            
		}
	}

    /**
     * Generate an instance of the document for the specified occurrence, with whether
     * to respect locked occurrences or not defined in the arguments.
     * 
     * @param o The occurrence of the document to generate an instance for.
     * @param ignoreLock If True, ignore check for locked occurrence and generate
     * instance anyway.
     * @return The document instance.
     * @throws ModelException if the occurrence is not a valid one for
     * the document.
     */
	public DocumentInstance generateInstance(DocumentOccurrence o, boolean ignoreLock) throws ModelException {
		DocumentInstance instance = new DocumentInstance();
		//check that the occurrence in the argument is associated with this
		//document
		if ( !occurrences.contains(o) ){
			throw new ModelException("Cannot create instance - the specified occurrence does not exist for this document");
		}
		//check that the occurrence is not locked
		if ( o.isLocked() && !ignoreLock ){
			throw new ModelException("Cannot create instance - the specified occurrence is locked");
		}
		instance.setOccurrence(o);
		initializeInstance(instance);
		instance.setAction(this.instanceAction);
		instance.setEditableAction(this.instanceEditableAction);
		return instance;
	}

    /**
     * Generate an instance of the document for the specified occurrence.
     * 
     * @param o The occurrence of the document to generate an instance for.
     * @return The document instance.
     * @throws ModelException if the occurrence is not a valid one for
     * the document.
     */
	public DocumentInstance generateInstance(DocumentOccurrence o) throws ModelException {
		return generateInstance(o, false);
	}

    /**
     * Method used to add a section to the collection of sections associated
     * with the document.
     * 
     * @param section The section to add to the collection of sections.
     * 
     * @throws ModelException if the section in the argument is <code>null</code>.
     */
	public void addSection(Section section) throws ModelException {
		if ( null == section ){
			throw new ModelException("Cannot add a null section");
		}
		sections.add((Section)section);
	}

    /**
     * Retrieve a single section at the specified index in
     * the document's collection of sections.
     * 
     * @param index The index in the collection of sections.
     * @return The section at the given index.
     * @throws ModelException if there is no section for the given index.
     */
	public Section getSection(int index) throws ModelException {
		try{
			return sections.get(index);
		}
		catch (IndexOutOfBoundsException ex){
			throw new ModelException("No section found for index "+index, ex);
		}
	}

    /**
     * Return the number of sections belonging to the document.
     * 
     * @return The number of sections.
     */
	public int numSections() {
		return this.sections.size();
	}

	@Override
	protected void addChildTasks(DataSet ds) {
		for (Entry e: entries){
			e.setMyDataSet(ds);
			e.addChildTasks(ds);
		}
	}

	/*
	 * FOR FUTURE
	private void publishDocumentImportFeatures(){
		if(requiredImportParserFeatures != null) //It has already been populated.
			return;

		requiredImportParserFeatures = new ArrayList<DocumentImportFeature>();

		//Analyse which features this document has out of all possible features.
		List<DocumentImportFeature> featureList = java.util.Arrays.asList(DocumentImportFeature.values());

		for(DocumentImportFeature feature: featureList) {
			switch(feature.getUniqueId()){
			case (0): //multiple sections
				{
				if(numSections() > 1){
					requiredImportParserFeatures.add(DocumentImportFeature.MultipleSections);
					break;
				}
				break;
				}
			case (1): //fixed length composite entry/entries
				{
				List<Entry> entries = getEntries();
				for(Entry entry: entries){
					if(entry instanceof CompositeEntry){
						CompositeEntry compositeEntry = (CompositeEntry)entry;
						if(compositeEntry.numRowLabels() > 0){
							requiredImportParserFeatures.add(DocumentImportFeature.FixedLengthCompositeEntry);
							break;
						}
					}
				}
				break;	
				}
			case (2): //Variable-length composite entry/entries
				{
				List<Entry> entries = getEntries();
				for(Entry entry: entries){
					if(entry instanceof CompositeEntry){
						CompositeEntry compositeEntry = (CompositeEntry) entry;
						if(compositeEntry.numRowLabels() == 0){
							requiredImportParserFeatures.add(DocumentImportFeature.VariableLengthCompositeEntry);
							break;
						}
					}
				}
				break;
				}
			default:
				break;
			}
		}
	}
	 */

	/**
     * Get the primary document index.
     * <p>
     * If this document is involved in dual data entry as part of the secondary
     * dataset, the index of the document in the primary dataset that it is associated
     * with. Otherwise <code>null</code>.
     * 
     * @return The primary document index.
	 * @hibernate.property column="c_prim_doc_index"
	 */
	public Long getPrimaryDocIndex() {
		return primaryDocIndex;
	}

    /**
     * Set the primary document index.
     * <p>
     * If this document is involved in dual data entry as part of the secondary
     * dataset, the index of the document in the primary dataset that it is associated
     * with. Otherwise <code>null</code>.
     * 
     * @param primaryDocIndex The primary document index.
     */
	public void setPrimaryDocIndex(Long primaryDocIndex) {
		this.primaryDocIndex = primaryDocIndex;
	}

	/**
	 * Get the secondary document index.
	 * <p>
     * If this document is involved in dual data entry as part of the primary
     * dataset, the index of the document in the secondary dataset that it is associated
     * with. Otherwise <code>null</code>.
	 * 
	 * @return The secondary document index.
	 * @hibernate.property column="c_sec_doc_index"
	 */
	public Long getSecondaryDocIndex() {
		return secondaryDocIndex;
	}

	/**
	 * Set the secondary document index.
	 * <p>
     * If this document is involved in dual data entry as part of the primary
     * dataset, the index of the document in the secondary dataset that it is associated
     * with. Otherwise <code>null</code>.
	 * 
	 * @param secondaryDocIndex The secondary document index.
	 */
	public void setSecondaryDocIndex(Long secondaryDocIndex) {
		this.secondaryDocIndex = secondaryDocIndex;
	}

	/**
	 * Get the RBACAction used to control access to this document, as a
	 * string.
	 * 
	 * @return action
	 * @hibernate.property column="c_rbac_action"
	 */
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	/**
	 * Set the RBACAction to control access to this document.
	 * 
	 * @param action
	 */
	public void setAction(RBACAction action) {
		if (action == null) {
			this.action = null;
		}
		else {
			this.action = action.toString();
		}
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
	 * @hibernate.property column="c_can_edit_action"
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
	 * Set the RBACAction, which if present, will indicate the users that can edit any 
	 * document instance. 
	 * 
	 * If null, it is assumed that the document (and any instances that are created) 
	 * is editable for any user who can access it, this is for backwards compatibility 
	 * purposes.
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
	 * Get the toString representation of the RBACAction used to control access to any 
	 * document instances created by this document. 
	 * 
	 * This should be the same as the document's action but include group access 
	 * restrictions.
	 * 
	 * @return instanceAction
	 * @hibernate.property column="c_inst_action"
	 */
	public String getInstanceAction() {
		return instanceAction;
	}

	/**
	 * Set the toString representation of the RBACAction used to control access to any 
	 * document instances created by this document. 
	 * 
	 * This should be the same as the document's action but include group access
	 * restrictions.
	 * 
	 * @param instanceAction
	 */
	public void setInstanceAction(String instanceAction) {
		this.instanceAction = instanceAction;	
	}

	/**
	 * Set the RBACAction used to control access to any document instances created by this 
	 * document. 
	 * 
	 * This should be the same as the document's action but include group access
	 * restrictions.
	 * 
	 * @param instanceAction
	 */
	public void setInstanceAction(RBACAction instanceAction) {
		if (instanceAction == null) {
			this.instanceAction = null;
		}
		else {
			this.instanceAction = instanceAction.toString();	
		}
	}

	/**
	 * Get the toString representation of the RBACAction used to allow users to edit document
	 * instances created by this document.
	 * 
	 * This should be the same as the document's editableAction but include group access
	 * restrictions.
	 * 
	 * @return editableAction
	 * @hibernate.property column="c_can_edit_inst_action"
	 */
	public String getInstanceEditableAction() {
		return instanceEditableAction;
	}

	/**
	 * Set the toString representation of the RBACAction used to allow users to edit document
	 * instances created by this document.
	 * 
	 * This should be the same as the document's editableAction but include group access
	 * restrictions.
	 * 
	 * @param instanceEditableAction
	 */
	public void setInstanceEditableAction(String instanceEditableAction) {
		this.instanceEditableAction = instanceEditableAction;
	}

	/**
	 * Set the RBACAction used to allow users to edit document instances created by this document.
	 * 
	 * This should be the same as the document's editableAction but include group access
	 * restrictions.
	 * 
	 * @param instanceEditableAction
	 */
	public void setInstanceEditableAction(RBACAction instanceEditableAction) {
		if (instanceEditableAction == null) {
			this.instanceEditableAction = null;
		}
		else {
			this.instanceEditableAction = instanceEditableAction.toString();
		}
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
	 * @param editingPermitted
	 */
	public void setEditingPermitted(boolean editingPermitted) {
		this.editingPermitted = editingPermitted;
	}

	/**
	 * Get the Long Running flag.
	 * <p>
     * If True, implies that the document is anticipated to be 
     * completed over a long period of time, rather than all at once.
	 * 
	 * @return Boolean, the Long Running flag.
	 * @hibernate.property column="c_long_run"
	 */
	public boolean isLongRunning() {
		return longRunning;
	}

	/**
	 * Set the Long Running flag.
	 * <p>
     * If True, implies that the document is anticipated to be 
     * completed over a long period of time, rather than all at once.
	 * 
	 * @param longRunning Boolean, the Long Running flag.
	 */
	public void setLongRunning(boolean longRunning) {
		this.longRunning = longRunning;
	}

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

	public org.psygrid.data.model.dto.DocumentDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
		//check for an already existing instance of a dto object for this 
		//element in the map of references
		org.psygrid.data.model.dto.DocumentDTO dtoD = null;
		if ( dtoRefs.containsKey(this)){
			dtoD = (org.psygrid.data.model.dto.DocumentDTO)dtoRefs.get(this);
		}
		if ( null == dtoD ){
			//an instance of the element has not already
			//been created, so create it, and add it to the
			//map of references
			dtoD = new org.psygrid.data.model.dto.DocumentDTO();
			dtoRefs.put(this, dtoD);
			toDTO(dtoD, dtoRefs, depth);
		}

		return dtoD;
	}

	public void toDTO(org.psygrid.data.model.dto.DocumentDTO dtoD, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){

		super.toDTO(dtoD, dtoRefs, depth);
		dtoD.setAction(this.action);
		dtoD.setEditableAction(editableAction);
		dtoD.setInstanceAction(instanceAction);
		dtoD.setInstanceEditableAction(instanceEditableAction);
		dtoD.setEditingPermitted(editingPermitted);

		if ( RetrieveDepth.DS_WITH_DOCS != depth ){
			dtoD.setIsImportEnabled(this.isImportEnabled);
			dtoD.setImportMappingString(this.importMappingString);
			dtoD.setPrimaryDocIndex(this.primaryDocIndex);
			dtoD.setSecondaryDocIndex(this.secondaryDocIndex);
			dtoD.setLongRunning(this.longRunning);

			if (conFrmGrps != null) {
				org.psygrid.data.model.dto.ConsentFormGroupDTO[] dtoCfgs = new org.psygrid.data.model.dto.ConsentFormGroupDTO[this.conFrmGrps.size()];
				for (int i=0; i<this.conFrmGrps.size(); i++){
					ConsentFormGroup cfg = conFrmGrps.get(i);
					dtoCfgs[i] = cfg.toDTO(dtoRefs, depth);
				}        
				dtoD.setConFrmGrps(dtoCfgs);
			} else {
				dtoD.setConFrmGrps(null);
			}
		}
		
		org.psygrid.data.model.dto.SectionDTO[] dtoSections = 
			new org.psygrid.data.model.dto.SectionDTO[this.sections.size()];
		for (int i=0; i<this.sections.size(); i++){
			Section s = sections.get(i);
			dtoSections[i] = s.toDTO(dtoRefs, depth);
		}
		dtoD.setSections(dtoSections);

		org.psygrid.data.model.dto.EntryDTO[] dtoEntries = 
			new org.psygrid.data.model.dto.EntryDTO[this.entries.size()];
		for (int i=0; i<this.entries.size(); i++){
			Entry child = entries.get(i);
			dtoEntries[i] = child.toDTO(dtoRefs, depth);
		}
		dtoD.setEntries(dtoEntries);


		org.psygrid.data.model.dto.DocumentOccurrenceDTO[] dtoOccurrences = 
			new org.psygrid.data.model.dto.DocumentOccurrenceDTO[this.occurrences.size()];
		for (int i=0; i<this.occurrences.size(); i++){
			DocumentOccurrence o = occurrences.get(i);
			dtoOccurrences[i] = o.toDTO(dtoRefs, depth);
		}
		dtoD.setOccurrences(dtoOccurrences);

	}

	/**
     * Retreive a boolean denoting whether the document can be imported from an external source
     * @return Returns whether the document is import-enabled. True means that it is import-enabled.
	 * @hibernate.property column="c_import_enabled"
	 */
	public boolean getIsImportEnabled() {
		return isImportEnabled;
	}

	/*
	 * Sets the value of isImportEnabled - the value of which determines whether the data of a doc instance of this document type can
	 * have its values imported from an external source.
	 * 
	 */
	public void setIsImportEnabled(boolean isImportEnabled) {
		this.isImportEnabled = isImportEnabled;
	}

	/**
	 * @hibernate.property column="c_import_mapping" 
	 *                     type="text"
	 *                     length="64000"
	 */
	public String getImportMappingString() {
		return this.importMappingString;
	}

	public void setImportMappingString(String mappingString) {
		this.importMappingString = mappingString;
	}

	/*
	 * FOR FUTURE
	public List<DocumentImportFeature> getDocumentFeatures() {

		if(requiredImportParserFeatures == null){
			publishDocumentImportFeatures();
		}
		return requiredImportParserFeatures;
	}
	 */

	/**
	 * Find the index of an entry in a document.
	 * 
	 * @param entry The entry whose index is to be found.
	 * @return The index of the entry.
	 * @throws ModelException if the Entry does not exist for the Document.
	 */
	public int getIndexOfEntry(Entry entry) throws ModelException {
		int index = entries.indexOf(entry);
		if ( index < 0 ){
			throw new ModelException("Entry not found");
		}
		return index;
	}

	@Override
	public ElementDTO instantiateDTO() {
		return new org.psygrid.data.model.dto.DocumentDTO();
	}

}
