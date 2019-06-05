package org.psygrid.meds.project;

public class TreatmentInfoCodeOnly extends TreatmentInfo {
	
	public TreatmentInfoCodeOnly(String treatmentCode){
		setTreatmentCode(treatmentCode);
	}
	
	public void validate() throws InvalidProjectException{
		if(this.getTreatmentCode() == null || this.getTreatmentCode().length() == 0){
			throw new InvalidProjectException("Treatment configured incorrectly - problem with treatment code.");
		}
		
	}

}
