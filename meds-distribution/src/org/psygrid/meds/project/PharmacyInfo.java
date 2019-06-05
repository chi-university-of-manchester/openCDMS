package org.psygrid.meds.project;

import java.io.Serializable;

public class PharmacyInfo implements Serializable{
	
	private String pharmacyName;
	private String pharmacyCode;
	
	public PharmacyInfo(){
	}
	
	public PharmacyInfo(String pharmacyName, String pharmacyCode) throws InvalidProjectException{
		this.pharmacyName = pharmacyName;
		this.pharmacyCode = pharmacyCode;
		validate();
	}

	public void setPharmacyName(String pharmacyName) {
		this.pharmacyName = pharmacyName;
	}

	public String getPharmacyName() {
		return pharmacyName;
	}

	public void setPharmacyCode(String pharmacyCode) {
		this.pharmacyCode = pharmacyCode;
	}

	public String getPharmacyCode() {
		return pharmacyCode;
	}
	
	public void validate() throws InvalidProjectException{
		if(pharmacyCode == null || pharmacyCode.length() == 0){
			throw new InvalidProjectException("Pharmacy configured incorrectly - problem with pharmacy code.");
		}
		
		if(pharmacyName == null || pharmacyName.length() == 0){
			throw new InvalidProjectException("Pharmacy configured incorrectly - problem with pharmacy name.");
		}
	}


}
