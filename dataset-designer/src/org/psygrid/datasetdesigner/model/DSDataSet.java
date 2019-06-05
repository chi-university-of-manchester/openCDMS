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

import java.util.ArrayList;
import java.util.List;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.GenericState;
import org.psygrid.data.model.hibernate.HibernateFactory;
import org.psygrid.data.model.hibernate.Status;

import org.psygrid.www.xml.security.core.types.PolicyType;


/**
 * A DSD representation of all attributes associated with
 * a dataset - the repository dataset, randomization and esl
 * settings
 * 
 * @deprecated  As of release 6.1, replaced by StudyDataSet
 */
@Deprecated
public class DSDataSet {
	
	private DataSet ds;
	private ArrayList<String> reports;
	private ESLEmailModel eslModel = null;
	private ArrayList<GroupModel> groups;
	private ArrayList<String> delRestrictedDocs;
	private ArrayList roles;
	private RandomisationHolderModel randomHolderModel = null;
	private PolicyType pt;
	private boolean singleCentreStudy;
	
	private String savedCode;
	
	private String savedName;
	
	private boolean fromRepository = false;
	
	public PolicyType getPt() {
		return pt;
	}

	public void setPt(PolicyType pt) {
		this.pt = pt;
	}

	public RandomisationHolderModel getRandomHolderModel() {
		return randomHolderModel;
	}

	public void setRandomHolderModel(RandomisationHolderModel randomHolderModel) {
		this.randomHolderModel = randomHolderModel;
	}

	public DSDataSet() {
	}
	
	public DataSet getDs() {
		return ds;
	}


	public void setDs(DataSet ds) {
		this.ds = ds;
	}


	public ArrayList<GroupModel> getGroups() {
		return groups;
	}


	public void setGroups(ArrayList<GroupModel> groups) {
		this.groups = groups;
	}


	public ArrayList<String> getReports() {
		return reports;
	}


	public ESLEmailModel getEslModel() {
		return eslModel;
	}

	public void setEslModel(ESLEmailModel eslModel) {
		this.eslModel = eslModel;
	}

	public void setReports(ArrayList<String> reports) {
		this.reports = reports;
	}

	public ArrayList getRoles() {
		return roles;
	}
	
	public void setRoles(ArrayList roles) {
		this.roles = roles;
	}
	
	public void setFromRepository(boolean fromRepository) {
		this.fromRepository = fromRepository;
	}
	
	public boolean isFromRepository() {
		return fromRepository;
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
			
			return "You must configure occurrences for the documents:"+ allNoOccs;
		}
		
		return null;
	}
	
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
		} else {
			return false;
		}
		
	}
	
	public boolean isFullyConfigured() {
		boolean isFullyConfigured = true;
		
		if (getGroups().size() == 0) {
			isFullyConfigured = false;
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

		if (ds.isRandomizationRequired()) {
			if (!isRandomizationTriggerSet()) {
				isFullyConfigured = false;
			}
		}
		
		return isFullyConfigured;
	}
	
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
	
	public List<String> getDelRestrictedDocs() {
		return delRestrictedDocs;
	}

	public void setDelRestrictedDocs(ArrayList<String> restrictedDocs) {
		this.delRestrictedDocs = restrictedDocs;
	}

	public boolean isSingleCentreStudy() {
		return singleCentreStudy;
	}

	public void setSingleCentreStudy(boolean singleCentreStudy) {
		this.singleCentreStudy = singleCentreStudy;
	}
	
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
//		DocTreeModel.getInstance().saveStatusesForDataset(statuses, this);
	}

	public void assignDefaultRoles() {
	//	setRoles(DocTreeModel.getInstance().getAllRoles());
	}

	public String getSavedCode() {
		return savedCode;
	}

	public void setSavedCode(String savedCode) {
		this.savedCode = savedCode;
	}

	public String getSavedName() {
		return savedName;
	}

	public void setSavedName(String savedName) {
		this.savedName = savedName;
	}
	
}