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
package org.psygrid.datasetdesigner.model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.esl.model.IRole;

import org.psygrid.datasetdesigner.utils.DefaultDSSettings;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.utils.Utils;

import org.psygrid.www.xml.security.core.types.RoleType;

/**
 * A wrapper class to store all settings required
 * for datasets created with the designer including 
 * the basic dataset properties (name, code, documents, occurrences
 * etc.) and additional settings for the randomiser, esl and reporting
 * 
 * @author pwhelan
 */
public class StudyDataSet {

	/**
	 * The logger for this class
	 */
	private static final Log LOG = LogFactory.getLog(StudyDataSet.class);

	/**
	 * The main <code>IDataSet</code> that this class encapsulates
	 */
	private DataSet ds;
	
	/**
	 * A list of all reports associated with this dataset
	 */
	private ArrayList<String> reports = new ArrayList<String>();
	
	/**
	 * The model used to store mappings of esl notification emails for the dataset
	 */
	private ESLEmailModel eslModel = null;
	
	/**
	 * The list of groups used by this dataset
	 */
	private ArrayList<GroupModel> groups;
	
	/**
	 * The list of restricted documents for the DEL
	 */
	private ArrayList<String> delRestrictedDocs;
	
	/**
	 * The list of roles used by this dataset
	 */
	private ArrayList<RoleType> roles;
	
	/**
	 * Model used to store the settings for randomisation
	 */
	private RandomisationHolderModel randomHolderModel = null;
	
	/**
	 * A flag to indicate if the study is to be based at a single centre
	 */
	private boolean singleCentreStudy;

	/**
	 * The code of the project as it was last saved 
	 */
	private String savedCode = null;

	/**
	 * The name of the project as it was last saved 
	 */
	private String savedName = null;
	
	/**
	 * The location where the dataset file was last stored 
	 */
	private String lastStoredLocation = null;
	
	/** Action Listeners */
	private transient Vector<ActionListener> listeners = new Vector<ActionListener>();
	
	/**
	 * Dirty flag; true if dataset has been edited since last save
	 */
	private boolean dirty = false;
	
	/**
	 * Dirty flag; true if dataset has been edited since last save
	 */
	private boolean readOnly = false;
	
	/**
	 * UKCRN Code
	 */
	private String ukcrnCode;
	
	/**
	 * Get the randomisation settings for the dataset
	 * @return The <code>RandomisationHolderModel</code> of the dataset
	 */
	public RandomisationHolderModel getRandomHolderModel() {
		return randomHolderModel;
	}

	/**
	 * Set the randomisation settings for the dataset
	 * @param The <code>RandomisationHolderModel</code> of the dataset
	 */
	public void setRandomHolderModel(RandomisationHolderModel randomHolderModel) {
		this.randomHolderModel = randomHolderModel;
	}

	/**
	 * Get the <code>IDataSet</code> of this dataset
	 * @return the associated <code>IDataSet</code>
	 */
	public DataSet getDs() {
		return ds;
	}

	/**
	 * Set the <code>IDataSet</code> of this dataset
	 * @param the associated <code>IDataSet</code>
	 */
	public void setDs(DataSet ds) {
		this.ds = ds;
	}

	/**
	 * Get the groups for this dataset
	 * @return the list of group models for this dataset
	 */
	public ArrayList<GroupModel> getGroups() {
		return groups;
	}

	/**
	 * Set the groups for this dataset
	 * @param groups a list of groups for this dataset
	 */
	public void setGroupModels(ArrayList<GroupModel> groups) {
		this.groups = groups;
	}

	/**
	 * Get the reports for this dataset
	 * @return a list of reports for this dataset
	 */
	public ArrayList<String> getReports() {
		return reports;
	}

	/**
	 * Get the ESL email settings for the datset
	 * @return the esl email settings for the dataset
	 */
	public ESLEmailModel getEslModel() {
		
		if (eslModel == null) {
			eslModel = new ESLEmailModel();
			ArrayList<IRole> eslRoles = new ArrayList<IRole>();
			org.psygrid.esl.model.hibernate.HibernateFactory factory = new org.psygrid.esl.model.hibernate.HibernateFactory();
			for (int j=0; j<getRoles().size(); j++) {
				IRole role = factory.createRole(getRoles().get(j).getName());
				role.setNotifyOfRSDecision(false);
				role.setNotifyOfRSInvocation(false);
				role.setNotifyOfRSTreatment(false);
				eslRoles.add(role);
			}

			eslModel.setRoles(eslRoles);
		}

		return eslModel;
	}

	/**
	 * Set the ESL email settings for the dataset
	 * @param eslModel the email settings for the dataset
	 */
	public void setEslModel(ESLEmailModel eslModel) {
		this.eslModel = eslModel;
	}

	/**
	 * Set the reports for the dataset
	 * @param the list of reports names for the dataset
	 */
	public void setReports(ArrayList<String> reports) {
		this.reports = reports;
	}

	/**
	 * Get the role for the dataset
	 * @return the list of roles for the dataset
	 */
	public ArrayList<RoleType> getRoles() {
		
		//remove ProjectManager and System Administrator at this point
		//so that they are never displayed to the user
		for (RoleType r: roles) {
			if (r.getName().equals("ProjectManager") ||
					r.getName().equals("SystemAdministrator")) {
				roles.remove(r);
			}
		}
		
		return roles;
	}
	
	public List<Unit> getUnits() {
		return ((DataSet)getDs()).getUnits();
	}
	
	public ArrayList<ValidationRule> getValidationRules() {
		return new ArrayList<ValidationRule>(((DataSet)getDs()).getValidationRules());
	}
	
	public ArrayList<ConsentFormGroup> getConsentGroups() {
		return new ArrayList<ConsentFormGroup>(((DataSet)getDs()).getAllConsentFormGroups());
	}
	
	public ArrayList<DocumentGroup> getDocumentGroups() {
		return new ArrayList<DocumentGroup>(((DataSet)getDs()).getDocumentGroups());
	}
	
	public ArrayList<EslCustomField> getEslCustomFields() {
		return new ArrayList<EslCustomField>(((DataSet)getDs()).getEslCustomFields());
	}
		
	public ArrayList<Status> getStatuses() {
		return new ArrayList<Status>(((DataSet)getDs()).getStatuses());
	}
	
	public ArrayList<Transformer> getTransformers() {
		return new ArrayList<Transformer>(((DataSet)getDs()).getTransformers());
	}


	/**
	 * Set the roles for the dataset
	 * @param roles the list of roles for the dataset
	 */
	public void setRoles(ArrayList<RoleType> roles) {
		this.roles = roles;
	}
	
	public void setUnits(List<Unit> units) {
		DataSet dataset = (DataSet)getDs();
		ArrayList<Unit> oldUnits = new ArrayList<Unit>(dataset.getUnits());

		//remove old refs
		for (Unit oldUnit: oldUnits){
			if (!units.contains(oldUnit)) {
				for (Document doc: dataset.getDocuments()) {
					for (Entry entry: doc.getEntries()) {
						if (entry instanceof BasicEntry) {
							BasicEntry bEntry = (BasicEntry)entry;
							for (int z=bEntry.numUnits()-1; z>=0; z--) {
								if (bEntry.getUnit(z).equals(oldUnit)) {
									bEntry.removeUnit(z);
								}
							}
						}
					}
				}
			}
		}

		dataset.setUnits(units);

		//fire event that can be listened for in the units panel of entry config dialogs
		fireActionEvent();
	}
	
	public void setTransformers(ArrayList<Transformer> transformers) {

		DataSet dataset = (DataSet)getDs();
		ArrayList<Transformer> oldTrans = new ArrayList<Transformer>(dataset.getTransformers());

		for (Transformer oldTran: oldTrans){
			if (!transformers.contains(oldTran)) {
				for (int i=0; i<dataset.numDocuments(); i++) {
					for (int j=0; j<dataset.getDocument(i).numEntries(); j++) {
						if (dataset.getDocument(i).getEntry(j) instanceof BasicEntry) {
							BasicEntry bEntry = (BasicEntry)dataset.getDocument(i).getEntry(j);
							for (int z=bEntry.numTransformers()-1; z>=0; z--) {
								if (bEntry.getTransformer(z).equals(oldTran)) {
									bEntry.removeTransformer(z);
								}
							}
						}
					}
				}
			}
		}

		dataset.setTransformers(transformers);

		//fire event that can be listened for in the transformers entry panel
		fireActionEvent();
	}
	
	public void setStatuses(ArrayList<Status> statuses) {
		DataSet dataset = (DataSet)getDs();
		//set new statuses	
		dataset.setStatuses(statuses);

		//clean up state transitions
		for (int f=0; f<statuses.size(); f++) {
			Status curStatus = statuses.get(f);
			for (int j=curStatus.numStatusTransitions()-1; j>=0; j--) {
				if (!statuses.contains(curStatus.getStatusTransition(j))) {
					curStatus.removeStatusTransition(j);
				}
			}
		}

		//clean statuses from document groups
		for (int i=0; i<dataset.numDocumentGroups(); i++) {
			DocumentGroup docGroup = dataset.getDocumentGroup(i);

			Status updateStatus = docGroup.getUpdateStatus();
			if (!statuses.contains(updateStatus)) {
				docGroup.setUpdateStatus(null);
			}

			ArrayList<Status> allowedStatuses = new ArrayList<Status>(docGroup.getAllowedRecordStatus());
			ArrayList<Status> newAllowedStatuses = new ArrayList<Status>();

			for (int j=0; j<allowedStatuses.size(); j++) {
				if (statuses.contains(allowedStatuses.get(j))){
					newAllowedStatuses.add(allowedStatuses.get(j));
				}
			}
			((DocumentGroup)docGroup).setAllowedRecordStatus(newAllowedStatuses);


		}

	}
	

	public void setValidationRules(ArrayList<ValidationRule> rules) {
		DataSet dataset = (DataSet)getDs();
		
		ArrayList<ValidationRule> oldRules = new ArrayList<ValidationRule>(dataset.getValidationRules());

		for (ValidationRule oldRule: oldRules){
			if (!rules.contains(oldRule)) {
				for (int i=0; i<dataset.numDocuments(); i++) {
					for (int j=0; j<dataset.getDocument(i).numEntries(); j++) {
						if (dataset.getDocument(i).getEntry(j) instanceof BasicEntry) {
							BasicEntry bEntry = (BasicEntry)dataset.getDocument(i).getEntry(j);
							for (int z=bEntry.numValidationRules()-1; z>=0; z--) {
								if (bEntry.getValidationRule(z).equals(oldRule)) {
									bEntry.removeValidationRule((z));
								}
							}
						}
					}
				}
			}
		}

		dataset.setValidationRules(rules);

		//fire an event so that listeners (used in entry panels will be forced to update)
		fireActionEvent();
	}
	
	public void setDocumentGroups(ArrayList<DocumentGroup> docGroups) {
		DataSet dataset = getDs();
		((DataSet)dataset).setDocumentGroups(docGroups);

		//clean up prerequisite groups
		for (int y=0; y<docGroups.size(); y++) {
			DocumentGroup curGroup = docGroups.get(y);
			ArrayList<DocumentGroup> newPreReqs = new ArrayList<DocumentGroup>();
			if (curGroup.getPrerequisiteGroups() != null) {
				for (int n=0; n<curGroup.getPrerequisiteGroups().size(); n++) {
					DocumentGroup curPreReqGroup = (DocumentGroup)curGroup.getPrerequisiteGroups().get(n);
					if (docGroups.contains(curPreReqGroup)) {
						newPreReqs.add(curPreReqGroup);
					}
				}
			}
			curGroup.setPrerequisiteGroups(newPreReqs);			
		}

		// clean up references from document occurrences
		for (int z=0; z<dataset.numDocuments(); z++) {
			Document curDoc = dataset.getDocument(z);
			for (int s=0; s<curDoc.numOccurrences(); s++) {
				DocumentOccurrence docOcc = curDoc.getOccurrence(s);
				if (!docGroups.contains(docOcc.getDocumentGroup())) {
					docOcc.setDocumentGroup(null);
				}
			}
		}
		
		//update the tree model with the latest doc group information information
		//needed particularly in the schedule view
		DocTreeModel.getInstance().fireTreeModelChanged(this);
		
	}
	
	public void setEslCustomFields(ArrayList<EslCustomField> customFields) {
		((DataSet)getDs()).setEslCustomFields(customFields);
	}
	
	public void setDocuments(ArrayList<Document> documents) {
		((DataSet)getDs()).setDocuments(documents);
	}
	
	public void setGroups(ArrayList<GroupModel> paraGroups) {
		DataSet dataset = (DataSet)getDs();
		ArrayList<Group> iGroups = new ArrayList<Group>();

		for (GroupModel group: paraGroups) {
			iGroups.add((Group)group.getGroup());
		}

		dataset.setGroups(iGroups);
		setGroupModels(paraGroups);
	}

	
	public void setConsentFormGroups(ArrayList<ConsentFormGroup> groups) {
		((DataSet)getDs()).setAllConsentFormGroups(groups);
		
		//clean documents that have a consent form group that is no longer present here
		for (Document d: (((DataSet)getDs()).getDocuments())) {
			List<ConsentFormGroup> groupsToRemove = new ArrayList<ConsentFormGroup>();
			for (ConsentFormGroup c: d.getConFrmGrps()) {
				if (!groups.contains(d)) {
					groupsToRemove.add(c);
				}
			}
			d.getConFrmGrps().removeAll(groupsToRemove);
		}
		
	}

	/**
	 * Clean the dataset of document occurrences used for previewing,
	 * and also check that all documents have at least one occurrence.
	 * 
	 * @return String, error message if one or more document have no
	 * occurrences; otherwise <code>null</code>
	 */
	public String cleanAndCheckDataset(){
		for (int i=0; i<ds.numDocuments(); i++) {
			int numOccs = ds.getDocument(i).numOccurrences(); 

			for (int y=numOccs-1; y>=0; y--) {
				//preview occs are used for rendering only!
				String occName = ds.getDocument(i).getOccurrence(y).getName();
				if (occName.startsWith("Preview")) {
					ds.getDocument(i).removeOccurrence(y);
				}
			}
		}

		ArrayList<String> docsNoOccs = new ArrayList<String>();
		for (int z=0; z<ds.numDocuments(); z++) {
			if (ds.getDocument(z).numOccurrences() ==0) {
				docsNoOccs.add(ds.getDocument(z).getName());
			}
		}

		if (docsNoOccs.size() > 0) {

			String allNoOccs = "";

			for (int m=0; m<docsNoOccs.size(); m++) {
				allNoOccs += ("\n" + docsNoOccs.get(m));
			}

			return PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.model.dsdataset.configuredococcs")+ allNoOccs;
		}
		
		HibernateFactory factory = new HibernateFactory();
		
		//configure the document statuses according to review and approve
		if (ds.isNoReviewAndApprove()) {
			for (Document d: ((DataSet)ds).getDocuments()) {
				//check here because we don't want to reassign new statuses if they 
				//already exist
				if (d.numStatus() != 3) {
					Utils.createNoReviewAndApproveStatuses(factory, d);
				}
			}
		} else {
			for (Document d: ((DataSet)ds).getDocuments()) {
				//check here because we don't want to create new statuses
				//if they already exist
				if (d.numStatus() != 5) {
					Utils.createReviewAndApproveStatuses(factory, d);
				}
			}
		}
		
		//check that some statuses have been set for the study stages
		for (DocumentGroup dg : ((DataSet)ds).getDocumentGroups()) {
			if (dg.getAllowedRecordStatus().isEmpty()) {
				//if none set, allow all record statuses
				dg.setAllowedRecordStatus(getStatuses());
			}
		}

		return null;
	}

	/**
	 * Checks for a document occurrence that is a randomisation triggeer
	 * @return false if no document occurrence is a randomsation trigger; true if so
	 */
	public boolean isRandomizationTriggerSet() {
		int randomizationTrigger = 0;

		for (int i=0; i<getDs().numDocuments(); i++) {
			for (int j=0; j<getDs().getDocument(i).numOccurrences(); j++) {
				if (getDs().getDocument(i).getOccurrence(j).isRandomizationTrigger()) {
					randomizationTrigger++;
				}
			}
		}

		//only one randomization trigger can be set
		if (randomizationTrigger == 1) {
			return true;
		}
		
		return false;
	}

	public boolean isStudyStagesValidated() {
		//check that prerequisites are higher in the list
		ArrayList<DocumentGroup> docGroups = new ArrayList<DocumentGroup>(((DataSet)getDs()).getDocumentGroups());

		boolean studyStagesValidated = true;
		
		if (docGroups.size() == 0) {
			studyStagesValidated = false;
		}
		
		for (DocumentGroup docGroup: docGroups) {
			for (DocumentGroup group: docGroup.getPrerequisiteGroups()) {
				if (docGroups.indexOf(group) > docGroups.indexOf(docGroup)) {
					studyStagesValidated = false;
				}
			}
			
			//update status must be a valid transition from one of the allowed rec statuses
			if (docGroup != null) {
				if (docGroup.getUpdateStatus() != null) {
					boolean transPossible = false;
					for (Status status: docGroup.getAllowedRecordStatus()) {
						ArrayList<Status> trans = new ArrayList<Status>(((Status)status).getStatusTransitions());
						if (trans.contains(docGroup.getUpdateStatus())) {
							transPossible = true;
						}
					}
					
					if (!transPossible) {
						studyStagesValidated = false;
					}
				}
			}
		}

		return studyStagesValidated;
		
	}
	
	/**
	 * Validates the existing dataset; checks that all necesssary fields have
	 * been completed (groups, roles, occurrences, and randomisation)
	 * @return true if dataset is sufficiently configured; false if not
	 */
	public boolean isFullyConfigured() {
		boolean isFullyConfigured = true;

		if (getGroups().size() == 0) {
			isFullyConfigured = false;
		}
		
		for (Group group: ((DataSet)getDs()).getGroups()) {
			if (group.numSites() == 0) {
				isFullyConfigured = false;
			}
		}

		//check if doc groups are configured
		if (getDs().numDocumentGroups() == 0) {
			isFullyConfigured = false;
		}

		if (getDs().numDocuments() == 0) {
			isFullyConfigured = false;
		}

		for (int z=0; z<getDs().numDocuments(); z++) {
			if (getDs().getDocument(z).numOccurrences() ==0) {
				isFullyConfigured = false;
			} else {
				//remove the preview occurrences
				int realNumDocOccs = getDs().getDocument(z).numOccurrences();
				for (int y=0;y<getDs().getDocument(z).numOccurrences(); y++){

					if (getDs().getDocument(z).getOccurrence(y).getName().startsWith("Preview")) {
						realNumDocOccs--;
					}
					if (realNumDocOccs == 0) {
						isFullyConfigured = false;
					}
				}
			}
		}

		if (!isStudyStagesValidated()) {
			isFullyConfigured = false;
		} 
		
		if (ds.isRandomizationRequired()) {
			if (!isRandomizationTriggerSet()) {
				isFullyConfigured = false;
			}
		}

		return isFullyConfigured;
	}

	/**
	 * Add a DEL restricted code
	 * @param docName the docName to set
	 */
	public void addDELRestrictedDoc(String docName){
		if(delRestrictedDocs == null){
			delRestrictedDocs = new ArrayList<String>();
		}

		//Check to see if the name is already there.
		boolean nameAlreadyInList = false;

		for(String name : delRestrictedDocs){
			if(name.equals(docName)){
				nameAlreadyInList = true;
				break;
			}
		}

		if(!nameAlreadyInList){
			delRestrictedDocs.add(docName);
		}
	}

	/**
	 * Checks to see if a given document is restricted in the DEL
	 * @param docName the name of the document to check
	 * @return true if document is restricted; false if not
	 */
	public boolean isDelRestricted(String docName){
		boolean isRestricted = false;

		if(delRestrictedDocs != null){
			for(String str : delRestrictedDocs){
				if(str.equals(docName)){
					isRestricted = true;
					break;
				}
			}
		}

		return isRestricted;
	}

	/**
	 * Remove a document from the restricted list
	 * @param docName the name of the document to remove
	 */
	public void removeDelRestrictedDoc(String docName){
		if(delRestrictedDocs != null){
			int docIndex = -1;
			for(int i = 0; i < delRestrictedDocs.size(); i++){
				if(delRestrictedDocs.get(i).equals(docName)){
					docIndex = i;
					break;
				}
			}

			if(docIndex != -1){
				delRestrictedDocs.remove(docIndex);
			}
		}
	}

	/**
	 * Returns the list of restricted documents
	 * @return the list of restricted documents
	 */
	public List<String> getDelRestrictedDocs() {
		return delRestrictedDocs;
	}

	/**
	 * Sets the list restricted documents
	 * @param restrictedDocs the list of restricted documents
	 */
	public void setDelRestrictedDocs(ArrayList<String> restrictedDocs) {
		this.delRestrictedDocs = restrictedDocs;
	}

	/**
	 * Checks if the study is to be run at just one centre
	 * @return true if study is single-centre; false if not
	 */
	public boolean isSingleCentreStudy() {
		return singleCentreStudy;
	}

	/**
	 * Sets the scope of the study
	 * @param singleCentreStudy true if study is single-centre; false if not
	 */
	public void setSingleCentreStudy(boolean singleCentreStudy) {
		this.singleCentreStudy = singleCentreStudy;
	}

	/**
	 * Assigns the default statuses to the dataset
	 * These are referred, consented, refused, active,
	 * completed, withdrew, invalid, lost
	 */
	public void assignDefaultStatuses() {
		HibernateFactory factory = new HibernateFactory();
		//dataset statuses and their transitions
		Status statReferred = (Status)factory.createStatus("Referred", "Referred",	0);
		statReferred.setGenericState(GenericState.REFERRED);
		Status statScreenInelig = (Status)factory.createStatus("Ineligible",
				"Screened; ineligible", 1);
		statScreenInelig.setInactive(true);
		statScreenInelig.setGenericState(GenericState.INACTIVE);
		Status statUnableToConsent =(Status)factory.createStatus("Unable", "Unable to consent", 2);
		statUnableToConsent.setInactive(true);
		statUnableToConsent.setGenericState(GenericState.INACTIVE);
		Status statConsented = (Status)factory.createStatus("Consented",
				"Consented", 3);
		statConsented.setGenericState(GenericState.ACTIVE);
		Status statConsentRefused = (Status)factory.createStatus("Refused", "Consent refused", 4);
		statConsentRefused.setInactive(true);
		statConsentRefused.setGenericState(GenericState.INACTIVE);
		Status statClinicianWithdrew = (Status)factory.createStatus("Withdrawn",
				"Clinician withdrew referral", 5);
		statClinicianWithdrew.setInactive(true);
		statClinicianWithdrew.setGenericState(GenericState.INACTIVE);
		Status statActive = (Status)factory.createStatus("Active", "Active", 6);
		statActive.setGenericState(GenericState.ACTIVE);
		Status statComplete = (Status)factory.createStatus("Complete", "Complete", 7);
		statComplete.setInactive(true);
		statComplete.setGenericState(GenericState.COMPLETED);
		Status statDeceased = (Status)factory.createStatus("Deceased", "Deceased", 8);
		statDeceased.setInactive(true);
		statDeceased.setGenericState(GenericState.LEFT);
		Status statWithdrew = (Status)factory.createStatus("Withdrew", "Withdrew", 9);
		statWithdrew.setInactive(true);
		statWithdrew.setGenericState(GenericState.INACTIVE);
		Status statLost = (Status)factory.createStatus("Lost", "Lost", 10);
		statLost.setInactive(true);
		statLost.setGenericState(GenericState.LEFT);
		Status statInvalid = (Status)factory.createStatus("Invalid", "Invalid", 11);	//Record was added by mistake and shouldn't exist
		statInvalid.setInactive(true);
		statInvalid.setGenericState(GenericState.INVALID);

		statReferred.addStatusTransition(statScreenInelig); //referred -> Screened; ineligible
		statReferred.addStatusTransition(statUnableToConsent); //referred -> Unable to consent
		statReferred.addStatusTransition(statConsented); //referred ->	consented
		statReferred.addStatusTransition(statConsentRefused); //referred -> consent refused
		statReferred.addStatusTransition(statClinicianWithdrew); //referred -> clinician withdrew referral
		//statReferred.addStatusTransition(statActive); //referred -> active
		statReferred.addStatusTransition(statComplete); //referred -> completed
		statReferred.addStatusTransition(statDeceased); //referred -> deceased
		statReferred.addStatusTransition(statWithdrew); //referred -> withdrew
		statReferred.addStatusTransition(statLost); //referred -> lost
		statReferred.addStatusTransition(statInvalid); //referred -> invalid

		statConsented.addStatusTransition(statActive); //consented -> active
		statConsented.addStatusTransition(statComplete); //consented -> completed
		statConsented.addStatusTransition(statDeceased); //consented -> deceased
		statConsented.addStatusTransition(statWithdrew); //consented -> withdrew
		statConsented.addStatusTransition(statLost); //consented -> lost
		statConsented.addStatusTransition(statInvalid); //consented -> invalid

		statActive.addStatusTransition(statComplete); //active -> completed
		statActive.addStatusTransition(statDeceased); //active -> deceased
		statActive.addStatusTransition(statWithdrew); //active -> withdrew
		statActive.addStatusTransition(statLost); //active -> lost
		statActive.addStatusTransition(statInvalid); //active -> invalid

		ArrayList<Status> statuses = new ArrayList<Status>();
		statuses.add(statReferred);
		statuses.add(statScreenInelig);
		statuses.add(statUnableToConsent);
		statuses.add(statConsented);
		statuses.add(statConsentRefused);
		statuses.add(statClinicianWithdrew);
		statuses.add(statActive);
		statuses.add(statComplete);
		statuses.add(statDeceased);
		statuses.add(statWithdrew);
		statuses.add(statLost);
		statuses.add(statInvalid);
		setStatuses(statuses);
	}

	/**
	 * Assign the default roles to this dataset; 
	 * default set is all the available roles 
	 */
	public void assignDefaultRoles() {
		setRoles(DefaultDSSettings.getAllRoles());
	}
	
	/**
	 * Assign the default last stored location to be the user's home 
	 * directory plus the dataset name
	 */
	public void assignDefaultLastStoredLocation() {
		if (lastStoredLocation == null) {
			setLastStoredLocation(PersistenceManager.getInstance().getUserDirLocation() 
					+ getDs().getName());
		}
	}

	/**
	 * Get the project code at time of last saving
	 * @return the project code at time of last saving
	 */
	public String getSavedCode() {
		return savedCode;
	}

	/**
	 * Set the code of this dataset at time of last saving
	 * @param savedCode the saved code to store
	 */
	public void setSavedCode(String savedCode) {
		this.savedCode = savedCode;
	}

	public String getLastStoredLocation() {
		return lastStoredLocation;
	}

	public void setLastStoredLocation(String lastStoredLocation) {
		this.lastStoredLocation = lastStoredLocation;
	}
	
	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}
	
	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	
	/**
	 * Add an action listener - used for listening to model events
	 * @param listener the listener to add to the list
	 */
	public void addActionListener(ActionListener listener) {
		if (listeners == null) {
			listeners = new Vector<ActionListener>();
		}
		listeners.add(listener);
	}
	
	/**
	 * Remove an action listener
	 * @param listener the listener to add to the list
	 */
	public void removeActionListener(ActionListener listener) {
		listeners.remove(listener);
	}
	
	/**
	 * Fire an action event on all the action listeners here
	 */
	public void fireActionEvent() {
		//listeners are not persisted so can be null here
		if (listeners == null) {
			listeners = new Vector<ActionListener>();
		}
		
		for (int i=0; i<listeners.size(); i++) {
			ActionListener listener = listeners.get(i);
			listener.actionPerformed(new ActionEvent(this, 1, "doc tree"));
		}
	}

	public String getSavedName() {
		return savedName;
	}

	public void setSavedName(String savedName) {
		this.savedName = savedName;
	}

	public String getUkcrnCode() {
		return ukcrnCode;
	}

	public void setUkcrnCode(String ukcrnCode) {
		this.ukcrnCode = ukcrnCode;
	}

}