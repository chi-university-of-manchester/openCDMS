package org.psygrid.meds.project;

import java.io.Serializable;

public class TreatmentInfo implements Serializable{
	
	private String treatmentCode;
	private String treatmentName;
	
	public TreatmentInfo(){}

	public TreatmentInfo(String treatmentName, String treatmentCode) throws InvalidProjectException{
		this.treatmentName = treatmentName;
		this.treatmentCode = treatmentCode;
		validate();
	}
	
	public void setTreatmentCode(String treatmentCode) {
		this.treatmentCode = treatmentCode;
	}

	public String getTreatmentCode() {
		return treatmentCode;
	}

	public void setTreatmentName(String treatmentName) {
		this.treatmentName = treatmentName;
	}

	public String getTreatmentName() {
		return treatmentName;
	}
	
	public void validate() throws InvalidProjectException{
		if(treatmentCode == null || treatmentCode.length() == 0){
			throw new InvalidProjectException("Treatment configured incorrectly - problem with treatment code.");
		}
		
		if(treatmentName == null || treatmentName.length() == 0){
			throw new InvalidProjectException("Treatment configured incorrectly - problem with treatment name.");
		}
	}

}
