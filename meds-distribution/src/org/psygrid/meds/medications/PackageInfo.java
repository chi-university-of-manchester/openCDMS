package org.psygrid.meds.medications;


import java.io.Serializable;
import java.util.Date;

import org.psygrid.meds.events.MedsPackageStatusChangeEventInfo;
import org.psygrid.meds.events.PackageViewEventInfo;
import org.psygrid.meds.project.InvalidProjectException;
import org.psygrid.meds.project.PharmacyInfo;
import org.psygrid.meds.project.ProjectObjectTranslator;
import org.psygrid.meds.project.Treatment;
import org.psygrid.meds.project.TreatmentInfo;

/**
 * This class is used to return information about a medication package.
 * @author Bill Vance
 *
 */
public class PackageInfo implements Serializable{

	private String packageIdentifier;
	private PharmacyInfo pharmacyInfo;
	private TreatmentInfo treatmentInfo;
	private String packageStatus = PackageStatus.unverified.toString();
	
	private String projectCode;
	
	private String shipmentNumber;
	private String batchNumber;
	private Date expiryDate;
	private boolean qpRelease = false;
	
	public PackageInfo(){
		packageIdentifier = null;
		pharmacyInfo = null;
		treatmentInfo = null;
		shipmentNumber = null;
		batchNumber = null;
		expiryDate = null;
		qpRelease = false;
	}
	
	public PackageInfo(String packageIdentifier, PharmacyInfo c, TreatmentInfo t, String projectCode, String shipmentNumber, String batchNumber, Date expiryDate){
		this.packageIdentifier = packageIdentifier;
		treatmentInfo = t;
		pharmacyInfo = c;
		packageStatus = PackageStatus.available.toString();
		
		this.setProjectCode(projectCode);
	}
	
	public void setPackageIdentifier(String id){
		packageIdentifier = id;
	}
	
	public String getPackageIdentifier() {
		return packageIdentifier;
	}

	public void setPackageStatus(String s){
		this.packageStatus = s;
	}
	
	public String getPackageStatus() {
		return packageStatus.toString();
	}
	
	
	public void setPharmacyInfo(PharmacyInfo info){
		pharmacyInfo = info;
	}

	public PharmacyInfo getPharmacyInfo() {
		return pharmacyInfo;
	}

	public void setTreatmentInfo(TreatmentInfo info){
		treatmentInfo = info;
	}
	
	public TreatmentInfo getTreatmentInfo() {
		return treatmentInfo;
	}
	
	public String getShipmentNumber() {
		return shipmentNumber;
	}

	public void setShipmentNumber(String shipmentNumber) {
		this.shipmentNumber = shipmentNumber;
	}

	public String getBatchNumber() {
		return batchNumber;
	}

	public void setBatchNumber(String batchNumber) {
		this.batchNumber = batchNumber;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public boolean getQpRelease() {
		return qpRelease;
	}

	public void setQpRelease(boolean qpRelease) {
		this.qpRelease = qpRelease;
	}

	protected void validate() throws InvalidMedicationPackageException{
		
		//Check that there is pharmacy info, and validate it
		PharmacyInfo cI = getPharmacyInfo();
		if(cI != null){
			try{
				ProjectObjectTranslator.validatePharmacyInfo(cI);
			}catch(InvalidProjectException ex){
				throw new InvalidMedicationPackageException("The Medication package's associated pharmacy info is invalid",
						ex);
			}
		}else{
			throw new InvalidMedicationPackageException("The treatment info is null.");
		}
		
		
		//Check that there is treatment info, and validate it
		TreatmentInfo tInfo = getTreatmentInfo();
		if(tInfo != null){
			try{
				ProjectObjectTranslator.validateTreamentInfo(tInfo);
			}catch(InvalidProjectException ex1){
				throw new InvalidMedicationPackageException("The Medication package's associated treatment info is invalid",
						ex1);
			}
		}
		
		//Check that there is a non-null, non-zero-length package identifier
		if(this.packageIdentifier == null || packageIdentifier.length() == 0){
			throw new InvalidMedicationPackageException("The package identifier is null or of zero length.");
		}
		
		//Check that there is a non-null, non-zero-length project code
		if(this.projectCode == null || projectCode.length() == 0){
			throw new InvalidMedicationPackageException("The package's project code is null or of zero length.");
		}
		
		if(this.batchNumber == null || batchNumber.length() == 0){
			throw new InvalidMedicationPackageException("The package's batch number cannot be null or zero length.");
		}
		
		if(this.shipmentNumber == null || shipmentNumber.length() == 0){
			throw new InvalidMedicationPackageException("The package's shipment number cannot be null or zero length.");
		}
		
		if(this.expiryDate == null){
			throw new InvalidMedicationPackageException("The package cannot have a null expiry date.");
		}
	}

	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}

	public String getProjectCode() {
		return projectCode;
	}

}
