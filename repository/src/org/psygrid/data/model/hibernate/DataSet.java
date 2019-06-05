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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.psygrid.common.simplemap.Pair;
import org.psygrid.data.model.dto.ElementDTO;

/**
 * The top-level object that represents the definition of a set
 * of data that is to be collected.
 * 
 * @author Rob Harper
 * 
 * @hibernate.joined-subclass table="t_datasets"
 *                            proxy="org.psygrid.data.model.hibernate.DataSet"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class DataSet extends StatusedElement {

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
	private BinaryObject info;

	/**
	 * The collection of documents that are contained by the dataset.
	 */
	private List<Document> documents = new ArrayList<Document>();

	/**
	 * Collection of consent form groups associated with the dataset,
	 * and all elements in the hierarchy underneath the dataset.
	 */
	private List<ConsentFormGroup> allConsentFormGroups = new ArrayList<ConsentFormGroup>();

	/**
	 * Collection of validation rules associated with the dataset.
	 * <p>
	 * All validation rules referenced by entrys in the dataset must
	 * be present in this collection.
	 */
	private List<ValidationRule> validationRules = new ArrayList<ValidationRule>();

	/**
	 * Collection of objects that have been deleted from the dataset
	 * outside of the Hibernate session.
	 */
	private List<Persistent> deletedObjects = new ArrayList<Persistent>();

	/**
	 * The number of characters in the suffix of identifiers generated
	 * for use by records associated with the dataset.
	 */
	private int idSuffixSize;

	/**
	 * Collection of transformers that entrys contained by the dataset
	 * may reference.
	 */
	private List<Transformer> transformers = new ArrayList<Transformer>();

	/**
	 * Collection of document groups that documents contained by the
	 * dataset may be members of.
	 */
	private List<DocumentGroup> documentGroups = new ArrayList<DocumentGroup>();

	/**
	 * Collection of units that entrys contained by the dataset
	 * may utilize.
	 */
	private List<Unit> units = new ArrayList<Unit>();

	/**
	 * Collection of groups associated with the dataset.
	 */
	private List<Group> groups = new ArrayList<Group>();

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
	 * Boolean flag to indicate whether monthly summary emails should
	 * be sent out for records associated with the dataset.
	 */
	private boolean sendMonthlySummaries;

	/**
	 * Count to define how often review and approve reminders are emailed
	 * to the project manager.
	 * <p>
	 * For instance, if this is set as 20 then an email is sent when the 20th
	 * record is added to each group.
	 */
	private int reviewReminderCount;

	/**
	 * If this DataSet is the secondary dataset in a dual data entry
	 * relationship then this property contains the project code of the
	 * primary dataset. Otherwise, <code>null</code>.
	 */
	private String primaryProjectCode;

	/**
	 * If this DataSet is the primary dataset in a dual data entry
	 * relationship then this property contains the project code of the
	 * secondary dataset. Otherwise, <code>null</code>.
	 */
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

	/**
	 * If True, "review and approve" is not used. 
	 * <p>
	 * A manager is not 
	 * expected to review committed documents, so they simply move
	 * from "Incomplete" to "Complete" on being committed to the
	 * repository.
	 */
	private boolean noReviewAndApprove;

	/**
	 * ESL custom fields
	 */
	private List<EslCustomField> eslCustomFields = new ArrayList<EslCustomField>();
	
	
	/**
	 * Denotes whether Collect should enforce record creation when creating a new participant
	 * (i.e. disallows cancelling, the side-effect of which is that participant identifiers are
	 * then accessible only to Windows-user/openCDMS-user combo on a particular laptop).
	 * This can only be used for studies that:
	 * 	- do NOT use the ESL
	 * 	- do NOT need to work in offline mode.
	 */
	private boolean forceRecordCreation = false;
	
	private Map<String, String> externalIdEditableSubstringMap = null;
	
	private Map<String, String> externalIdEditableSubstringValidationMap = null;

	/**
	 * Default no-arg constructor, as required by the Hibernate framework
	 * for all persistable classes.
	 */
	public DataSet(){};

	/**
	 * Constructor that accepts the name of the data set.
	 *  
	 * @param name The name of the data set.
	 */
	public DataSet(final String name){
		super(name);
	}

	/**
	 * Constructor that accepts the name and display text
	 * of the data set.
	 *  
	 * @param name The name of the data set.
	 * @param displayText The display text of the data set.
	 */
	public DataSet(final String name, final String displayText){
		super(name, displayText);
	}

	/**
     * Get the version number of the data set.
     * 
     * @return The version number.
	 * @hibernate.property column="c_version"
	 */
	public String getVersionNo() {
		return versionNo;
	}

    /**
     * Set the version number of the data set.
     * 
     * @param versionNo The version number.
     */
	public void setVersionNo(final String versionNo) {
		this.versionNo = versionNo;
	}

	/**
     * Get the boolean flag to indicate whether the data set has been 
     * published or not. 
     * <p>
     * Once a data set has been published it is ready for data
     * collection, and its structure is not intended to change in the
     * future.
     * 
     * @return The published flag.
	 * @hibernate.property column="c_published" 
	 *                     not-null="true"
	 */
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

	/**
	 * Publish the data set.
	 * <p>
	 * Sets the published attribute of the data set to true.
	 */
	public void publish(){
		this.published = true;
	}

	/**
	 * Standard JavaBean getter for the info sheet.
	 * 
	 * @return The info sheet.
	 * 
	 * @hibernate.many-to-one class="org.psygrid.data.model.hibernate.BinaryObject"
	 *                        column="c_e_doc"
	 *                        not-null="false"
	 *                        cascade="all"
	 */
	public BinaryObject getInfo() {
		return this.info;
	}

	/**
	 * Standard JavaBean setter for the info sheet.
	 * 
	 * @param info The info sheet.
	 */
	public void setInfo(final BinaryObject info) {
		this.info = info;
	}

    /**
     * Get the electronic document that is an information sheet for the
     * dataset.
     * 
     * @return The info sheet electronic document.
     * 
     */
	public BinaryObject getInfoSheet() {
		return this.info;
	}

    /**
     * Set the electronic document that is an information sheet for the
     * dataset.
     * 
     * @param infoSheet The info sheet electronic document.
     */
	public void setInfoSheet(final BinaryObject infoSheet) {
		final BinaryObject oldInfo = this.info;
		final BinaryObject newInfo = (BinaryObject)infoSheet;
		if ( null != oldInfo && null != newInfo ){
			if ( !newInfo.equals(oldInfo) ){
				this.info = newInfo;
				if ( null != oldInfo.getId() ){
					//infosheet object being replaced has already been
					//persisted so add it to the list of objects to delete
					this.deletedObjects.add(oldInfo);
				}
			}
		}
		else if ( null != oldInfo && null == newInfo ){
			this.info = newInfo;
			if ( null != oldInfo.getId() ){
				//infosheet object being replaced has already been
				//persisted so add it to the list of objects to delete
				this.deletedObjects.add(oldInfo);
			}
		}
		else if ( null == oldInfo && null != newInfo ){
			this.info = newInfo;
		}
	}

	/**
     * Get the date when the data set was last modified.
     * 
     * @return The date the data set was last modified.
	 * @hibernate.property column="c_date_modified"
	 */
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
     * Generate a new instance of the dataset - a record.
     * <p>
     * Note that for a dataset for which one or more statuses are defined
     * the status of the generated record will be set by default to the 
     * status at index 0 in the list, thus ensuring that the record 
     * has a status for the its entire lifetime.
     * 
     * @return The new record.
     * @throws ModelException if the record cannot be created.
     */
	public Record generateInstance() throws ModelException{
		final Record record = new Record();
		record.setDataSet(this);
		//set default status to the zeroth status in the list
		if ( this.statuses.size() > 0 ){
			record.changeStatus(this.statuses.get(0));
		}
		return record;
	}

	/**
	 * Get the collection of consent form groups associated with the dataset,
	 * and all elements in the hierarchy underneath the dataset.
	 * 
	 * @return The collection of consent form groups.
	 * 
	 * @hibernate.list cascade="all"
	 * @hibernate.one-to-many class="org.psygrid.data.model.hibernate.ConsentFormGroup"
	 * @hibernate.key column="c_dataset_id" 
	 *                not-null="true"
	 * @hibernate.list-index column="c_index"
	 */
	public List<ConsentFormGroup> getAllConsentFormGroups() {
		return allConsentFormGroups;
	}

	/**
	 * Set the collection of consent form groups associated with the dataset,
	 * and all elements in the hierarchy underneath the dataset.
	 * 
	 * @param allConsentFormGroups The collection of consent form groups.
	 */
	public void setAllConsentFormGroups(final List<ConsentFormGroup> allConsentFormGroups) {
		this.allConsentFormGroups = allConsentFormGroups;
	}

    /**
     * Get a single consent form group, at the specified index in the
     * dataset's collection of consent form groups.
     * <p>
     * The dataset's collection of consent form groups contains all
     * consent form groups for the dataset and all elements in the
     * hierarchy underneath the dataset.
     * 
     * @param index The index to retrieve the consent form group for.
     * @return The consent form group.
     * @throws ModelException if no consent form group exists for the
     * given index.
     */
	public ConsentFormGroup getAllConsentFormGroup(final int index) throws ModelException {
		try{
			return allConsentFormGroups.get(index);
		}
		catch(final IndexOutOfBoundsException ex){
			throw new ModelException("No consent form group found at index "+index);
		}
	}

    /**
     * Add a single consent form group to the datasets collection of
     * consent form groups.
     * 
     * @param group The consent form group to add.
     * @throws ModelException if the consent form group being added is 
     * <code>null</code>.
     */
	public void addAllConsentFormGroup(final ConsentFormGroup group) throws ModelException {
		if ( null == group ){
			throw new ModelException("Cannot add a null consent form group");
		}
		final ConsentFormGroup g = (ConsentFormGroup)group;
		g.setDataSet(this);
		allConsentFormGroups.add(g);
	}

    /**
     * Get the number of consent form groups.
     * <p>
     * This is the sum of all consent form groups over the
     * dataset and all of its child elements.
     * 
     * @return The number of consent form groups.
     */
	public int numAllConsentFormGroups() {
		return allConsentFormGroups.size();
	}    

	/**
	 * Get the list of validation rules.
	 * 
	 * @return The list of validation rules.
	 * 
	 * @hibernate.list cascade="all-delete-orphan"
	 * @hibernate.one-to-many class="org.psygrid.data.model.hibernate.ValidationRule"
	 * @hibernate.key column="c_dataset_id" not-null="false"
	 * @hibernate.list-index column="c_index"
	 */
	public List<ValidationRule> getValidationRules() {
		return validationRules;
	}

	/**
	 * Set the list of validation rules.
	 * 
	 * @param validationRules The list of validation rules.
	 */
	public void setValidationRules(final List<ValidationRule> validationRules) {
		this.validationRules = validationRules;
	}

    /**
     * Add a new validation rule to the dataset's collection of validation
     * rules.
     * <p>
     * All validation rules used by members of a dataset must be present in
     * this collection.
     * 
     * @param rule The validation rule to add.
     * @throws ModelException if it is attempted to add a <code>null</code> 
     * validation rule.
     */
	public void addValidationRule(ValidationRule rule) throws ModelException {
		if ( null == rule ){
			throw new ModelException("Cannot add a null validation rule");
		}
		this.validationRules.add((ValidationRule)rule);
	}

    /**
     * Retrieve a validation rule from the dataset's collection of validation
     * rules.
     * 
     * @param index The index in the collection of the rule to retrieve.
     * @return The validation rule.
     * @throws ModelException if no validation rule exists for the given index.
     */
	public ValidationRule getValidationRule(int index) throws ModelException {
		try{
			return validationRules.get(index);
		}
		catch (final IndexOutOfBoundsException ex){
			throw new ModelException("No validation rule found for index "+index, ex);
		}
	}

    /**
     * Retrieve the number of validation rules associated with the dataset.
     * 
     * @return The number of validation rules.
     */
	public int numValidationRules() {
		return this.validationRules.size();
	}

    /**
     * Remove a validation rule from the dataset's collection of validation
     * rules.
     * 
     * @param index The index in the collection of the rule to remove.
     * @throws ModelException if no validation rule exists for the given index.
     */
	public void removeValidationRule(final int index) throws ModelException {
		try{
			final ValidationRule r = validationRules.remove(index);
			if ( null != r.getId() ){
				//validation rule object being replaced has already been
				//persisted so add it to the list of objects to delete
				this.deletedObjects.add(r);
			}
		}
		catch (final IndexOutOfBoundsException ex){
			throw new ModelException("No validation rule found for index "+index, ex);
		}
	}

	/**
	 * Get the collection of deleted objects.
	 * 
	 * @return The collection of deleted objects.
	 */
	public List<Persistent> getDeletedObjects() {
		return deletedObjects;
	}

	/**
	 * Set the collection of deleted objects.
	 * 
	 * @param deletedObjects The collection of deleted objects.
	 */
	public void setDeletedObjects(final List<Persistent> deletedObjects) {
		this.deletedObjects = deletedObjects;
	}

	/**
     * Get the number of characters in the suffix of identifiers generated
     * for use by records associated with the dataset.
     * 
     * @return The number of characters in the suffix.
	 * @hibernate.property column="c_suffix_size"
	 */
	public int getIdSuffixSize() {
		return idSuffixSize;
	}

    /**
     * Set the number of characters in the suffix of identifiers generated
     * for use by records associated with the dataset.
     * 
     * @param idSuffixSize The number of characters in the suffix.
     */
	public void setIdSuffixSize(final int idSuffixSize) {
		this.idSuffixSize = idSuffixSize;
	}

	/**
	 * Get the collection of transformers that entrys contained by 
	 * the dataset may reference.
	 * 
	 * @return The collection of transformers.
	 * 
	 * @hibernate.list cascade="all"
	 * @hibernate.one-to-many class="org.psygrid.data.model.hibernate.Transformer"
	 * @hibernate.key column="c_dataset_id" not-null="@DEL_REP_TRANSFORMER_SWITCH@"
	 * @hibernate.list-index column="c_index"
	 */ 
	public List<Transformer> getTransformers() {
		return transformers;
	}

	/**
	 * Set the collection of transformers that entrys contained by 
	 * the dataset may reference.
	 * 
	 * @param transformers The collection of transformers.
	 */
	public void setTransformers(final List<Transformer> transformers) {
		this.transformers = transformers;
	}

    /**
     * Add a single transformer to the dataset's collection of transformers.
     * <p>
     * All transformers used by entrys in the dataset must be present in
     * this collection.
     * 
     * @param transformer The transformer to add.
     * @throws ModelException if it is attempted to add a <code>null</code> 
     * transformer.
     */
	public void addTransformer(final Transformer transformer) throws ModelException {
		if ( null == transformer ){
			throw new ModelException("Cannot ass a null transformer");
		}
		this.transformers.add((Transformer)transformer);
	}

    /**
     * Retrieve a single transformer from the dataset's collection of 
     * transformers.
     * 
     * @param index The index in the collection of the transformer to retrieve.
     * @return The transformer at the given index.
     * @throws ModelException if no transformer exists for the given index.
     */
	public Transformer getTransformer(final int index) throws ModelException {
		try{
			return transformers.get(index);
		}
		catch (final IndexOutOfBoundsException ex){
			throw new ModelException("No transformer found for index "+index, ex);
		}
	}

    /**
     * Retrieve the number of transformers associated with the dataset.
     * 
     * @return The number of transformers.
     */
	public int numTransformers() {
		return this.transformers.size();
	}

    /**
     * Remove a single transformer from the dataset's collection of transformers.
     * 
     * @param index The index in the collection of the transformer to remove.
     * @throws ModelException if no transformer exists for the given index.
     */
	public void removeTransformer(final int index) throws ModelException {
		try{
			final Transformer t = transformers.remove(index);
			if ( null != t.getId() ){
				//transformer object being removed has already been
				//persisted so add it to the list of objects to delete
				this.deletedObjects.add(t);
			}
		}
		catch (final IndexOutOfBoundsException ex){
			throw new ModelException("No transformer found for index "+index, ex);
		}
	}

	/**
	 * Get the collection of document groups that documents contained by 
	 * the dataset may be a part of.
	 * 
	 * @return The collection of document groups.
	 * 
	 * @hibernate.list cascade="all"
	 * @hibernate.one-to-many class="org.psygrid.data.model.hibernate.DocumentGroup"
	 * @hibernate.key column="c_dataset_id" not-null="true"
	 * @hibernate.list-index column="c_index"
	 */
	public List<DocumentGroup> getDocumentGroups() {
		return documentGroups;
	}

	public void setDocumentGroups(final List<DocumentGroup> documentGroups) {
		this.documentGroups = documentGroups;
	}

    /**
     * Add a single document group to the dataset's collection of document
     * groups.
     * 
     * @param group The document group to add.
     * @throws ModelException if it is attempted to add a <code>null</code> 
     * document group.
     */
	public void addDocumentGroup(final DocumentGroup group) throws ModelException {
		if ( null == group ){
			throw new ModelException("Cannot add a null document group");
		}
		this.documentGroups.add((DocumentGroup)group);
	}

    /**
     * Retrieve a single document group from the dataset's collection of 
     * document groups.
     * 
     * @param index The index in the collection of the document group to retrieve.
     * @return The document group at the given index.
     * @throws ModelException if no document group exists for the given index.
     */
	public DocumentGroup getDocumentGroup(final int index) throws ModelException {
		try{
			return documentGroups.get(index);
		}
		catch (final IndexOutOfBoundsException ex){
			throw new ModelException("No document group found for index "+index, ex);
		}
	}

    /**
     * Retrieve the number of document groups associated with the dataset.
     * 
     * @return The number of document groups.
     */
	public int numDocumentGroups() {
		return this.documentGroups.size();
	}

    /**
     * Remove a single document group from the dataset's collection of 
     * document groups.
     * 
     * @param index The index in the collection of the document group to remove.
     * @throws ModelException if no document group exists for the given index.
     */
	public void removeDocumentGroup(final int index) throws ModelException {
		try{
			final DocumentGroup dg = documentGroups.remove(index);
			if ( null != dg.getId() ){
				//document group object being removed has already been
				//persisted so add it to the list of objects to delete
				this.deletedObjects.add(dg);
			}
		}
		catch (final IndexOutOfBoundsException ex){
			throw new ModelException("No document group found for index "+index, ex);
		}
	}

	/**
	 * Get the collection of units that entrys contained by the dataset
	 * may utilize.
	 * 
	 * @return The collection of units.
	 * 
	 * @hibernate.list cascade="all"
	 * @hibernate.one-to-many class="org.psygrid.data.model.hibernate.Unit"
	 * @hibernate.key column="c_dataset_id" not-null="@DEL_REP_UNIT_SWITCH@"
	 * @hibernate.list-index column="c_index"
	 */
	public List<Unit> getUnits() {
		return units;
	}

	public void setUnits(final List<Unit> units) {
		this.units = units;
	}

    /**
     * Add a single unit to the dataset's collection of units.
     * 
     * @param unit The unit to add.
     * @throws ModelException if it is attempted to add a <code>null</code> 
     * unit.
     */
	public void addUnit(final Unit unit) throws ModelException {
		if ( null == unit ){
			throw new ModelException("Cannot add a null unit");
		}
		this.units.add((Unit)unit);
	}

    /**
     * Retrieve a single unit from the dataset's collection of 
     * units.
     * 
     * @param index The index in the collection of the unit to retrieve.
     * @return The unit at the given index.
     * @throws ModelException if no unit exists for the given index.
     */
	public Unit getUnit(final int index) throws ModelException {
		try{
			return units.get(index);
		}
		catch (final IndexOutOfBoundsException ex){
			throw new ModelException("No unit found for index "+index, ex);
		}
	}

    /**
     * Retrieve the number of units associated with the dataset.
     * 
     * @return The number of units.
     */
	public int numUnits() {
		return this.units.size();
	}

    /**
     * Remove a single unit from the dataset's collection of units.
     * 
     * @param index The index in the collection of the unit to remove.
     * @throws ModelException if no unit exists for the given index.
     */
	public void removeUnit(final int index) throws ModelException {
		try{
			final Unit u = units.remove(index);
			if ( null != u.getId() ){
				//unit object being removed has already been
				//persisted so add it to the list of objects to delete
				this.deletedObjects.add(u);
			}
		}
		catch (final IndexOutOfBoundsException ex){
			throw new ModelException("No unit found for index "+index, ex);
		}
	}

	/**
     * Get the project code of the project that the dataset relates
     * to.
     * <p>
     * Typically this property is used to link the dataset to 
     * the security system.
     * 
     * @return The project code.
	 * @hibernate.property column="c_project_code"
	 *                     not-null="true"
	 *                     unique="true"
	 */
	public String getProjectCode() {
		return projectCode;
	}

    /**
     * Set the project code of the project that the dataset relates
     * to.
     * <p>
     * Typically this property is used to link the dataset to 
     * the security system.
     * 
     * @param projectCode The project code.
     */
	public void setProjectCode(final String projectCode) {
		this.projectCode = projectCode;
	}

	/**
     * Get the schedule start question, the question that is to 
     * be asked when the user creates a new record to obtain the 
     * date that acts as the zero-point for scheduling purposes.
     * <p>
     * If <code>null</code> then the zero-point for scheduling 
     * should just be set as the date when the record is created.
     * 
     * @return The schedule start question.
	 * @hibernate.property column="c_sch_st_qu"
	 */
	public String getScheduleStartQuestion() {
		return scheduleStartQuestion;
	}

    /**
     * Set the schedule start question, the question that is to 
     * be asked when the user creates a new record to obtain the 
     * date that acts as the zero-point for scheduling purposes.
     * <p>
     * If <code>null</code> then the zero-point for scheduling 
     * should just be set as the date when the record is created.
     * 
     * @param scheduleStartQuestion The schedule start question.
     */
	public void setScheduleStartQuestion(final String scheduleStartQuestion) {
		this.scheduleStartQuestion = scheduleStartQuestion;
	}

	/**
	 * Get the collection of documents that are contained by the dataset.
	 * 
	 * @return The collection of documents.
	 * 
	 * @dynamic_xdoclet_DataSetDocumentElementRelationship@
	 */
	public List<Document> getDocuments() {
		return documents;
	}

	/**
	 * Set the collection of documents that are contained by the dataset.
	 * 
	 * @param documents The collection of documents.
	 */
	public void setDocuments(final List<Document> documents) {
		this.documents = documents;
	}

    /**
     * Return the number of documents belonging to the dataset.
     * 
     * @return The number of documents.
     */
	public int numDocuments(){
		return documents.size();
	}

    /**
     * Method used to add a document to the collection of documents associated
     * with the dataset.
     * 
     * @param document The document to add to the collection of documents.
     * 
     * @throws ModelException if the document in the argument is <code>null</code>.
     */
	public void addDocument(final Document document) throws ModelException{
		if ( null == document ){
			throw new ModelException("Cannot add a null document");
		}
		final Document d = (Document)document;        
		d.setMyDataSet(this);
		d.addChildTasks(this);
		documents.add(d);
	}

    /**
     * Retrieve a single document at the specified index in
     * the dataset's collection of documents.
     * 
     * @param index The index in the collection of documents.
     * @return The document at the given index.
     * @throws ModelException if there is no document for the given index.
     */
	public Document getDocument(final int index) throws ModelException{
		try{
			return documents.get(index);
		}
		catch (final IndexOutOfBoundsException ex){
			throw new ModelException("No child Element found for index "+index, ex);
		}
	}
	
	/**
	 * Retrieve a document by its name.
	 * @param name the document name
	 * @return the document or null if non exists
	 */
	public Document getDocument(final String name){
		Document result = null;
		for(Document doc: documents){
			if(doc.getName().equals(name)){
				result = doc;
				break;
			}
		}
		return result;
	}

    /**
     * Remove a single document at the specified index from
     * the dataset's collection of documents.
     * 
     * @param index The index in the collection of documents.
     * @throws ModelException if there is no document for the given
     * index.
     */
	public void removeDocument(final int index) throws ModelException{
		try{
			final Persistent p = documents.remove(index);
			if ( null != p.getId() ){
				//the object being removed has previously been persisted
				//store it in the collection of deleted objects so that 
				//it may be manually deleted when the dataset is next saved
				getDataSet().deletedObjects.add(p);
			}
		}
		catch(final IndexOutOfBoundsException ex){
			throw new ModelException("No child document found for index "+index, ex);
		}
	}

    /**
     * Move a single document to a new index in the dataset's 
     * collection of documents.
     * 
     * @param currentPosition The current index of the document to move.
     * @param newPosition The new index to move the document to.
     * @throws ModelException if there is no document for the given 
     * current index, or if the new index to move it to is not valid.
     */
	public void moveDocument(final int currentIndex, final int newIndex) throws ModelException {
		Document d = null;
		try{
			d = documents.remove(currentIndex);
		}
		catch(final IndexOutOfBoundsException ex){
			throw new ModelException("No child document found for index "+currentIndex, ex);
		}
		try{
			documents.add(newIndex, d);
		}
		catch(final IndexOutOfBoundsException ex){
			//roll back - re-insert child element to its old position
			documents.add(currentIndex, d);
			throw new ModelException("Cannot move child document to index "+newIndex+" - invalid index", ex);
		}

	}

    /**
     * Method used to insert a document into the collection of documents
     * associated with the dataset, at the specified index.
     * 
     * @param document The document to insert into the collection of documents.
     * @param index The index in the collection of documents to insert the
     * documents into.
     * @throws ModelException if the specified index is not valid; 
     * if the document in the argument is <code>null</code>.
     */    
	public void insertDocument(final Document document, final int index) throws ModelException {
		if ( null == document ){
			throw new ModelException("Cannot insert a null document");
		}
		final Document d = (Document)document;
		d.setMyDataSet(myDataSet);
		d.addChildTasks(this.myDataSet);
		try{
			documents.add(index,d);
		}
		catch(final IndexOutOfBoundsException ex){
			throw new ModelException("Cannot insert child Element at index "+index+" - invalid index", ex);            
		}
	}

	/**
	 * For a DataSet the Element.myDataSet property is always <code>null</code>
	 * to prevent circular references.
	 * <p>
	 * So just return <code>this</code> instead.
	 */
	@Override
	public DataSet getDataSet() {
		return this;
	}

	/**
	 * @hibernate.list cascade="all"
	 * @hibernate.one-to-many class="org.psygrid.data.model.hibernate.Group"
	 * @hibernate.key column="c_dataset_id" not-null="true"
	 * @hibernate.list-index column="c_index"
	 */
	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(final List<Group> groups) {
		this.groups = groups;
	}

    /**
     * Get the number of groups associated with the dataset.
     * 
     * @return The number of groups.
     */
	public int numGroups(){
		return groups.size();
	}

    /**
     * Add a new group to the dataset.
     * 
     * @param group The new group to add.
     * @throws ModelException if the new group is <code>null</code>.
     */
	public void addGroup(final Group group) throws ModelException {
		if ( null == group ){
			throw new ModelException("Cannot add a null group");
		}
		this.groups.add((Group)group);
	}

    /**
     * Get a single group that is associated with the dataset.
     * 
     * @param index The index of the group to retrieve.
     * @return The group at the specified index.
     * @throws ModelException if no group exists for the given index.
     */
	public Group getGroup(final int index) throws ModelException {
		try{
			return groups.get(index);
		}
		catch(final IndexOutOfBoundsException ex){
			throw new ModelException("No group exists for index="+index, ex);
		}
	}

	/**
     * Get the Boolean flag to indicate whether the Electronic Screening Log
     * should be used to hold identifiable data for subjects in the dataset.
     * 
     * @return The "esl used" boolean flag.
	 * @hibernate.property column="c_esl_used"
	 */
	public boolean isEslUsed() {
		return eslUsed;
	}

    /**
     * Set the Boolean flag to indicate whether the Electronic Screening Log
     * should be used to hold identifiable data for subjects in the dataset.
     * 
     * @param eslUsed The "esl used" boolean flag.
     */
	public void setEslUsed(final boolean eslUsed) {
		this.eslUsed = eslUsed;
	}

	/**
     * Get the Boolean flag to indicate whether the Electronic Screening Log's
     * randomize function should be called to randomly allocate a subject
     * in the dataset to an arm of a clinical trial.
     * 
     * @return The "randomization required" Boolean flag.
	 * @hibernate.property column="c_rnd_req"
	 */
	public boolean isRandomizationRequired() {
		return randomizationRequired;
	}

    /**
     * Set the Boolean flag to indicate whether the Electronic Screening Log's
     * randomize function should be called to randomly allocate a subject
     * in the dataset to an arm of a clinical trial.
     * 
     * @param randomizationRequired The "randomization required" Boolean flag.
     */
	public void setRandomizationRequired(final boolean randomizationRequired) {
		this.randomizationRequired = randomizationRequired;
	}

	/**
     * Get the Boolean flag to indicate whether monthly summary emails should
     * be sent out for records associated with the dataset.
     * 
     * @return The send monthly summaries flag.
	 * @hibernate.property column="c_send_monthly"
	 */
	public boolean isSendMonthlySummaries() {
		return sendMonthlySummaries;
	}

    /**
     * Set the Boolean flag to indicate whether monthly summary emails should
     * be sent out for records associated with the dataset.
     * 
     * @param sendMonthlySummaries The send monthly summaries flag.
     */
	public void setSendMonthlySummaries(final boolean sendMonthlySummaries) {
		this.sendMonthlySummaries = sendMonthlySummaries;
	}

	/**
     * Get the count to define how often review and approve reminders are emailed
     * to the project manager.
     * <p>
     * For instance, if this is set as 20 then an email is sent when the 20th
     * record is added to each group.
     * 
     * @return The review and approve reminder count.
	 * @hibernate.property column="c_rev_rem_count"
	 */
	public int getReviewReminderCount() {
		return reviewReminderCount;
	}

    /**
     * Set the count to define how often review and approve reminders are emailed
     * to the project manager.
     * <p>
     * For instance, if this is set as 20 then an email is sent when the 20th
     * record is added to each group.
     * 
     * @param reviewReminderCount The review and approve reminder count.
     */
	public void setReviewReminderCount(final int reviewReminderCount) {
		this.reviewReminderCount = reviewReminderCount;
	}

	/**
	 * Get the project code of the primary DataSet that the DataSet is involved
	 * in a dual data entry relationship with.
	 * <p>
	 * If the DataSet is not involved with dual data entry, or is the primary 
	 * DataSet in dual data entry, then this returns <code>null</code>.
     * 
	 * @return The project code of the primary DataSet 
	 * @hibernate.property column="c_prim_proj_code"
	 */
	public String getPrimaryProjectCode() {
		return primaryProjectCode;
	}

	/**
	 * Set the project code of the primary DataSet that the DataSet is involved
	 * in a dual data entry relationship with.
	 * <p>
	 * If the DataSet is not involved with dual data entry, or is the primary 
	 * DataSet in dual data entry, then this should be set to <code>null</code>.
	 * 
	 * @param primaryProjectCode The project code of the primary DataSet 
	 */
	public void setPrimaryProjectCode(final String primaryProjectCode) {
		this.primaryProjectCode = primaryProjectCode;
	}

	/**
	 * Get the project code of the secondary DataSet that the DataSet is involved
	 * in a dual data entry relationship with.
	 * <p>
	 * If the DataSet is not involved with dual data entry, or is the secondary 
	 * DataSet in dual data entry, then this returns <code>null</code>.
     * 
	 * @return The project code of the secondary DataSet 
	 * @hibernate.property column="c_sec_proj_code"
	 */
	public String getSecondaryProjectCode() {
		return secondaryProjectCode;
	}

	/**
	 * Set the project code of the secondary DataSet that the DataSet is involved
	 * in a dual data entry relationship with.
	 * <p>
	 * If the DataSet is not involved with dual data entry, or is the secondary 
	 * DataSet in dual data entry, then this should be set to <code>null</code>.
	 * 
	 * @param primaryProjectCode The project code of the secondary DataSet 
	 */
	public void setSecondaryProjectCode(final String secondaryProjectCode) {
		this.secondaryProjectCode = secondaryProjectCode;
	}

	/**
	 * Set the flag to specify whether the dataset uses externally generated
	 * identifiers as well as the normal PsyGrid identifiers.
	 * 
	 * @return Boolean, True if externally generated identifiers are used.
	 * @hibernate.property column="c_ext_id_used"
	 */
	public boolean isExternalIdUsed() {
		return externalIdUsed;
	}

	/**
	 * Get the flag to specify whether the dataset uses externally generated
	 * identifiers as well as the normal PsyGrid identifiers.
	 * 
	 * @param externalIdUsed Boolean, True if externally generated identifiers are used.
	 */
	public void setExternalIdUsed(final boolean externalIdUsed) {
		this.externalIdUsed = externalIdUsed;
	}

	/**
	 * If this is set to True thon the external id is to be used shown throughout the openCDMS
	 * system as the primary identifier. The openCDMS native identifier, in this case will not be visible to the user at all.
	 * @hibernate.property column="c_ext_id_as_primary"
	 */
	public boolean getUseExternalIdAsPrimary() {
		return useExternalIdAsPrimary;
	}

	/**
	 * @hibernate.property column="c_show_rand_treatment"
	 */
	public boolean getShowRandomisationTreatment() {
		return showRandomisationTreatment;
	}

	public void setShowRandomisationTreatment(final boolean showRandomisationTreatment) {
		this.showRandomisationTreatment = showRandomisationTreatment;
	}
	
	/**
	 * @hibernate.property column="c_use_meds_service"
	 */
	public boolean getUseMedsService() {
		return useMedsService;
	}

	public void setUseMedsService(boolean useMedsService) {
		this.useMedsService = useMedsService;
	}

	public void setUseExternalIdAsPrimary(final boolean useExternalIdAsPrimary) {
		this.useExternalIdAsPrimary = useExternalIdAsPrimary;
	}

	/**
	 * @hibernate.list cascade="all"
	 * @hibernate.one-to-many class="org.psygrid.data.model.hibernate.EslCustomField"
	 * @hibernate.key column="c_dataset_id" not-null="true"
	 * @hibernate.list-index column="c_index"
	 */
	public List<EslCustomField> getEslCustomFields() {
		return eslCustomFields;
	}

	public void setEslCustomFields(final List<EslCustomField> eslCustomFields) {
		this.eslCustomFields = eslCustomFields;
	}

	/**
	 * Get the number of ESL custom fields for the dataset.
	 * 
	 * @return Number of ESL custom fields.
	 */
	public int getEslCustomFieldCount(){
		return eslCustomFields.size();
	}

	/**
	 * Add a ESL custom field to the dataset.
	 * 
	 * @param field The field to add.
	 * @throws ModelException if the field to add is null
	 */
	public void addEslCustomField(final EslCustomField value) throws ModelException {
		if ( null == value ){
			throw new ModelException("Cannot add a null esl custom field");
		}
		eslCustomFields.add((EslCustomField)value);
	}

	/**
	 * Get one of the dataset's ESL custom fields
	 * 
	 * @param index The index of the field to get.
	 * @return The field at the specified index.
	 * @throws ModelException if no field exists at the specified index.
	 */
	public EslCustomField getEslCustomField(final int index) throws ModelException {
		try{
			return eslCustomFields.get(index);
		}
		catch (final IndexOutOfBoundsException ex){
			throw new ModelException("No custom field exists for index "+index, ex);
		}
	}


	public org.psygrid.data.model.dto.DataSetDTO toDTO(){
		return toDTO(RetrieveDepth.DS_COMPLETE);
	}

	public org.psygrid.data.model.dto.DataSetDTO toDTO(final RetrieveDepth depth){
		//create list to hold references to objects in the dataset's
		//object graph which have multiple references to them within
		//the object graph. This is used so that each object instance
		//is copied to its DTO equivalent once and once only
		Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
		final org.psygrid.data.model.dto.DataSetDTO dtoDS = toDTO(dtoRefs, depth);
		dtoRefs = null;
		return dtoDS;
	}

	public org.psygrid.data.model.dto.DataSetDTO toDTO(final Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, final RetrieveDepth depth){
		//check for an already existing instance of a dto object for this 
		//dataset in the set of references
		org.psygrid.data.model.dto.DataSetDTO dtoDS = null;
		if ( dtoRefs.containsKey(this)){
			dtoDS = (org.psygrid.data.model.dto.DataSetDTO)dtoRefs.get(this);
		}
		if ( null == dtoDS ){
			//an instance of the dataset has not already
			//been created, so create it and add it to the map of references
			dtoDS = new org.psygrid.data.model.dto.DataSetDTO();
			dtoRefs.put(this, dtoDS);
			toDTO(dtoDS, dtoRefs, depth);
		}

		return dtoDS;

	}

	public void toDTO(final org.psygrid.data.model.dto.DataSetDTO dtoDS, final Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, final RetrieveDepth depth){
		//note that the list of identifiers is not copied over to
		//the dto representation of the dataset - identifiers are
		//retrieved by a separate process
		super.toDTO(dtoDS, dtoRefs, depth);
		dtoDS.setProjectCode(this.projectCode);
		if ( depth != RetrieveDepth.REP_SAVE ){
			dtoDS.setExportSecurityActive(getExportSecurityActive());
			dtoDS.setDateModified(this.dateModified);
			dtoDS.setPublished(this.published);
			dtoDS.setVersionNo(this.versionNo);
			dtoDS.setIdSuffixSize(this.idSuffixSize);
			dtoDS.setScheduleStartQuestion(this.scheduleStartQuestion);
			dtoDS.setEslUsed(this.eslUsed);
			dtoDS.setRandomizationRequired(this.randomizationRequired);
			dtoDS.setSendMonthlySummaries(this.sendMonthlySummaries);
			dtoDS.setReviewReminderCount(this.reviewReminderCount);
			dtoDS.setPrimaryProjectCode(this.primaryProjectCode);
			dtoDS.setSecondaryProjectCode(this.secondaryProjectCode);
			dtoDS.setExternalIdUsed(this.externalIdUsed);
			dtoDS.setUseExternalIdAsPrimary(useExternalIdAsPrimary);
			dtoDS.setShowRandomisationTreatment(showRandomisationTreatment);
			dtoDS.setUseMedsService(useMedsService);
			dtoDS.setNoReviewAndApprove(this.noReviewAndApprove);
			dtoDS.setForceRecordCreation(forceRecordCreation);
			
			if(this.externalIdEditableSubstringMap != null && this.externalIdEditableSubstringMap.size() > 0){
				final Set<String> keys = externalIdEditableSubstringMap.keySet();
				final Pair<String, String>[] simpleMap = new Pair[externalIdEditableSubstringMap.size()];
				int count = 0;
				for(final String key: keys){
					simpleMap[count] = new Pair<String, String>(key, externalIdEditableSubstringMap.get(key));
					count++;
				}
				
				dtoDS.setExternalIdEditableSubstringPairs(simpleMap);
			}
			
			if(this.externalIdEditableSubstringValidationMap != null && this.externalIdEditableSubstringValidationMap.size() > 0){
				final Set<String> keys = externalIdEditableSubstringValidationMap.keySet();
				final Pair<String, String>[] simpleMap = new Pair[externalIdEditableSubstringValidationMap.size()];
				int count = 0;
				for(final String key: keys){
					simpleMap[count] = new Pair<String, String>(key, externalIdEditableSubstringValidationMap.get(key));
					count++;
				}
				
				dtoDS.setExternalIdEditableSubstringValidationMapPairs(simpleMap);
			}
			
			if(this.externalIdEditableSubstringValidationMap != null && this.externalIdEditableSubstringValidationMap.size() > 0){
				
			}
			
			if ( RetrieveDepth.RS_COMPLETE != depth &&
					RetrieveDepth.RS_NO_BINARY != depth &&
					RetrieveDepth.RS_SUMMARY != depth &&
					RetrieveDepth.DS_SUMMARY != depth )
			{

				if ( RetrieveDepth.DS_WITH_DOCS != depth ){

					if ( null != this.info ){
						dtoDS.setInfo(this.info.toDTO(dtoRefs, depth));
					}
					final org.psygrid.data.model.dto.ConsentFormGroupDTO[] dtoAllCFGs = new org.psygrid.data.model.dto.ConsentFormGroupDTO[this.allConsentFormGroups.size()];
					for (int i=0; i<this.allConsentFormGroups.size(); i++){
						final ConsentFormGroup group = allConsentFormGroups.get(i);
						dtoAllCFGs[i] = group.toDTO(dtoRefs, depth);
					}        
					dtoDS.setAllConsentFormGroups(dtoAllCFGs);

					final org.psygrid.data.model.dto.ValidationRuleDTO[] dtoRules = new org.psygrid.data.model.dto.ValidationRuleDTO[this.validationRules.size()];
					for (int i=0; i<this.validationRules.size(); i++){
						final ValidationRule rule = validationRules.get(i);
						dtoRules[i] = rule.toDTO(dtoRefs, depth);
					}        
					dtoDS.setValidationRules(dtoRules);

					final org.psygrid.data.model.dto.PersistentDTO[] dtoDeleted = new org.psygrid.data.model.dto.PersistentDTO[this.deletedObjects.size()];
					for (int i=0; i<this.deletedObjects.size(); i++){
						final Persistent p = deletedObjects.get(i);
						dtoDeleted[i] = p.toDTO(dtoRefs, depth);
					}        
					dtoDS.setDeletedObjects(dtoDeleted);

					final org.psygrid.data.model.dto.TransformerDTO[] dtoTransformers = new org.psygrid.data.model.dto.TransformerDTO[this.transformers.size()];
					for (int i=0; i<this.transformers.size(); i++){
						final Transformer t = transformers.get(i);
						dtoTransformers[i] = t.toDTO(dtoRefs, depth);
					}        
					dtoDS.setTransformers(dtoTransformers);            

					final org.psygrid.data.model.dto.UnitDTO[] dtoUnits = new org.psygrid.data.model.dto.UnitDTO[this.units.size()];
					for (int i=0; i<this.units.size(); i++){
						final Unit u = units.get(i);
						dtoUnits[i] = u.toDTO(dtoRefs, depth);
					}        
					dtoDS.setUnits(dtoUnits);            

					if (this.eslCustomFields != null) {
						final org.psygrid.data.model.dto.EslCustomFieldDTO[] dtoEslCustomFields = new org.psygrid.data.model.dto.EslCustomFieldDTO[this.eslCustomFields.size()];
						for (int i=0; i<this.eslCustomFields.size(); i++){
							final EslCustomField ecf = eslCustomFields.get(i);
							dtoEslCustomFields[i] = ecf.toDTO(dtoRefs, depth);
						}        
						dtoDS.setEslCustomFields(dtoEslCustomFields);            
					}
				}

				final org.psygrid.data.model.dto.GroupDTO[] dtoGrps = new org.psygrid.data.model.dto.GroupDTO[this.groups.size()];
				for (int i=0; i<this.groups.size(); i++){
					final Group g = groups.get(i);
					dtoGrps[i] = g.toDTO(dtoRefs, depth);
				}        
				dtoDS.setGroups(dtoGrps);  

				final org.psygrid.data.model.dto.DocumentGroupDTO[] dtoDocGroups = new org.psygrid.data.model.dto.DocumentGroupDTO[this.documentGroups.size()];
				for (int i=0; i<this.documentGroups.size(); i++){
					final DocumentGroup dg = documentGroups.get(i);
					dtoDocGroups[i] = dg.toDTO(dtoRefs, depth);
				}        
				dtoDS.setDocumentGroups(dtoDocGroups);            

				final org.psygrid.data.model.dto.DocumentDTO[] dtoDocs = new org.psygrid.data.model.dto.DocumentDTO[this.documents.size()];
				for (int i=0; i<this.documents.size(); i++){
					final Document d = documents.get(i);
					dtoDocs[i] = d.toDTO(dtoRefs, depth);
				}        
				dtoDS.setDocuments(dtoDocs);            

			}
		}
	}

	@Override
	protected void addChildTasks(final DataSet ds) {
		//do nothing, as a dataset can not be added as a child 
		//of another object
	}

	/**
	 * 
	 * @return - whether export security is active for this dataset
	 * 
	 * @hibernate.property column="c_export_security_active"
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

	/**
	 * 
	 * @hibernate.map cascade="all" table="t_ext_id_editable_parts"
	 * @hibernate.key column="c_editable_part_id" not-null="true"
	 * 
	 * @hibernate.map-key column="c_substring_name"
	 *                    type="string"
	 * @hibernate.element column="c_regex_defn"
	 *                    type="string"
	 * 
	 */
	public Map<String, String> getExternalIdEditableSubstringMap() {
		return externalIdEditableSubstringMap;
	}

	public void setExternalIdEditableSubstringMap(
			final Map<String, String> externalIdEditableSubstringMap) {
		this.externalIdEditableSubstringMap = externalIdEditableSubstringMap;
	}

	/**
	 * 
	 * @hibernate.map cascade="all" table="t_ext_id_substr_validation"
	 * @hibernate.key column="c_validation_id" not-null="true"
	 * 
	 * @hibernate.map-key column="c_substring_name"
	 *                    type="string"
	 * @hibernate.element column="c_regex_validator"
	 *                    type="string"
	 * 
	 */
	public Map<String, String> getExternalIdEditableSubstringValidationMap() {
		return externalIdEditableSubstringValidationMap;
	}

	public void setExternalIdEditableSubstringValidationMap(
			final Map<String, String> externalIdEditableSubstringValidationMap) {
		this.externalIdEditableSubstringValidationMap = externalIdEditableSubstringValidationMap;
	}

	@Override
	public ElementDTO instantiateDTO() {
		return new org.psygrid.data.model.dto.DataSetDTO();
	}

	/**
	 * Get the value of the "no review and approve" flag.
	 * <p>
     * If True, "review and approve" is not used. A manager is not 
     * expected to review committed documents, so they simply move
     * from "Incomplete" to "Complete" on being committed to the
     * repository.
     * 
	 * @return Boolean, the value of the "no review and approve" flag.
	 * @hibernate.property column="c_no_review"
	 */
	public boolean isNoReviewAndApprove() {
		return noReviewAndApprove;
	}

	/**
	 * Set the value of the "no review and approve" flag.
	 * <p>
     * If True, "review and approve" is not used. A manager is not 
     * expected to review committed documents, so they simply move
     * from "Incomplete" to "Complete" on being committed to the
     * repository.
     * 
	 * @param noReviewAndApprove The value of the "no review and approve" flag.
	 */
	public void setNoReviewAndApprove(final boolean noReviewAndApprove) {
		this.noReviewAndApprove = noReviewAndApprove;
	}
	
	/**
	 * Returns whether Collect must force record creation when a new participant identifier
	 * is generated from the repository service. Studies that use this feature cannot
	 * run Collect in offline mode.
	 * @return whether Collect should enforce record creation.
	 * @hibernate.property column="c_force_record_creation"
	 */
	public boolean getForceRecordCreation() {
		return forceRecordCreation;
	}

	public void setForceRecordCreation(final boolean forceRecordCreation) {
		this.forceRecordCreation = forceRecordCreation;
	}



}
