package org.psygrid.meds.project;

public class PharmacyInfoCodeOnly extends PharmacyInfo {
	
	public PharmacyInfoCodeOnly(String pharmacyCode){
		setPharmacyCode(pharmacyCode);
	}
	
	public void validate() throws InvalidProjectException{
		if(this.getPharmacyCode() == null || this.getPharmacyCode().length() == 0){
			throw new InvalidProjectException("Pharmacy configured incorrectly - problem with pharmacy code.");
		}
	}

}
