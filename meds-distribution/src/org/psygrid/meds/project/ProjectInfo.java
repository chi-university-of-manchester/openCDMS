package org.psygrid.meds.project;


import java.util.HashSet;
import java.util.Set;

import org.psygrid.common.simplemap.Pair;


/**
 * This class is used for returning and displaying info about a project that has been set up in the meds 
 * database.
 * @author Bill
 *
 */
public class ProjectInfo {
	
	private String projectName; //friendly project name
	private String projectCode; //unique project code 
	private PharmacyInfo[] pharmacies;
	private TreatmentInfo[] treatments;
	private Pair<String, EmailInfo>[] emails;
	
	public ProjectInfo(){
		
	}
	
	public ProjectInfo(String projectName, String projectCode, PharmacyInfo[] pharmacies, TreatmentInfo[] treatments, Pair<String, EmailInfo>[] emails) throws InvalidProjectException{
		this.setProjectName(projectName);
		this.setProjectCode(projectCode);
		this.pharmacies = pharmacies;
		this.treatments = treatments;
		this.emails = emails;
		validate();
	}
	
	public void setTreatments(TreatmentInfo[] treatments){
		//TODO: Throw an illegal argument exception if the array is null;
		this.treatments = treatments;
	}
	
	/**
	 * Returns an unmodifiable list of treatments
	 * @return
	 */
	/*
	public List<TreatmentInfo> getTreatmentsAsList(){
		return (List<TreatmentInfo>) Collections.unmodifiableCollection(Arrays.asList(treatments));
	}
	*/
	
	public TreatmentInfo[] getTreatments(){
		return treatments;
	}
	
	public void setPharmacies(PharmacyInfo[] pharmacies){
		//TODO: Throw an illegal argument exception if the array is null;
		this.pharmacies = pharmacies;
	}
	
	public PharmacyInfo[] getPharmacies(){
		return pharmacies;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}

	public String getProjectCode() {
		return projectCode;
	}
	
	public Pair<String, EmailInfo>[] getEmails() {
		return emails;
	}

	public void setEmails(Pair<String, EmailInfo>[] emails) {
		this.emails = emails;
	}

	public void validate() throws InvalidProjectException{
		
		//Make sure that this has a project code and name.
		
		if(this.projectCode == null || this.projectCode.length()  == 0){
			throw new InvalidProjectException("Problem with project code.");
		}
		
		if(this.projectName == null && this.projectName.length() == 0){
			throw new InvalidProjectException("Problem with project name.");
		}
		
		//Make sure that it has at least one centre and at least two treatments.
		if(this.pharmacies == null || this.pharmacies.length < 1){
			throw new InvalidProjectException("This project's pharmacies have been incorrectly configured.");
		}
		
		if(this.treatments == null || this.treatments.length < 2){
			throw new InvalidProjectException("This project's treatments have been incorrectly configured.");
		}
		
		//It isn't mandatory that email notifications must be sent out, so we will just validate the email objects.
		for(int count = 0; count < emails.length; count++){
			emails[count].getValue().validate();
		}
		
		//Make sure that the centres and treatments are individually valid.
		for(int count = 0; count < pharmacies.length; count++){
			pharmacies[count].validate();
		}
		
		for(int count = 0; count < treatments.length; count++){
			treatments[count].validate();
		}
		
		Set<String> nameSet = new HashSet<String>();
		Set<String> codeSet = new HashSet<String>();
		
		//Make sure the pharmacies have unique codes and names.
		for(int count = 0; count < pharmacies.length; count++){
			PharmacyInfo p = pharmacies[count];
		
			if(!nameSet.add(p.getPharmacyName())){
				throw new InvalidProjectException("Invalid Project - duplicate centre name: " + p.getPharmacyName() + ".");
			}
			
			if(!codeSet.add(p.getPharmacyCode())){
				throw new InvalidProjectException("Invalid Project - duplicate centre code: " + p.getPharmacyCode() + ".");
			}	
		}
		
		nameSet.clear();
		codeSet.clear();
		
		//Make sure the treatments have unique codes and names.
		for(int count = 0; count < treatments.length; count++){
			TreatmentInfo t = treatments[count];
		
			if(!nameSet.add(t.getTreatmentName())){
				throw new InvalidProjectException("Invalid Project - duplicate treatment name: " + t.getTreatmentName() + ".");
			}
			
			if(!codeSet.add(t.getTreatmentCode())){
				throw new InvalidProjectException("Invalid Project - duplicate treatment code: " + t.getTreatmentCode() + ".");
			}	
		}
		
	}
	
}
