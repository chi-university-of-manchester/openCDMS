package org.psygrid.meds.medications;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.psygrid.meds.events.MedsPackageStatusChangeEvent;
import org.psygrid.meds.events.PackageViewEvent;
import org.psygrid.meds.project.Pharmacy;
import org.psygrid.meds.project.Treatment;

/**
 * Holds info about a medication package
 * @author Bill
 *@hibernate.class table="t_meds_package"
 *@hibernate.properties name="multicolumn_unique" unique="true"
 */ 
public class MedicationPackage {
	
	private Long id;
	private int version; //hibernate version for optimistic locking.
	
	private Date importDate;
	private String status = PackageStatus.available.toString();
	private String projectCode;
	private String packageId;
	
	private String shipmentNumber;
	private String batchNumber;
	private Date expiryDate;
	private boolean qpRelease = false;
	
	private Pharmacy pharmacy; 
	private Treatment treatment; //The treatment the package is for
	
	private List<PackageViewEvent> viewEvents = null;
	private List<MedsPackageStatusChangeEvent> statusChangeEvents = null;
	
	
	public MedicationPackage(){
	}


	public void setImportDate(Date importDate) {
		this.importDate = importDate;
	}


	/**
	 * 
	 * @return
	 * @hibernate.property column="c_import_date"
	 * 									not-null="true"
	 */
	public Date getImportDate() {
		return importDate;
	}

	/**
	 * 
	 * @param status
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * 
	 * @return
	 * @hibernate.property column="c_status"
	 */
	public String getStatus() {
		return status;
	}
	
	public PackageStatus getStatusEnum(){
		return PackageStatus.valueOf(status);
	}


	public void setPharmacy(Pharmacy pharmacy) {
		this.pharmacy = pharmacy;
	}

	/**
	 * 
	 * @return - the pharmacy with which the Package is affiliated
	 * @hibernate.many-to-one class="org.psygrid.meds.project.Pharmacy"
	 *                        column="c_pharmacy_id" 
	 *                        not-null="true"
	 *                        cascade="none"
	 */
	public Pharmacy getPharmacy() {
		return pharmacy;
	}

	public void setTreatment(Treatment treatment) {
		this.treatment = treatment;
	}

	/**
	 * 
	 * @return
	 * @hibernate.many-to-one class="org.psygrid.meds.project.Treatment"
	 *                        column="c_treatment_id"
	 *                        not-null="true"
	 *                        cascade="none"
	 */
	public Treatment getTreatment() {
		return treatment;
	}
	


	public void setVersion(int version) {
		this.version = version;
	}
	
	/**
	 * 
	 * @return
	 * @hibernate.property column="c_version"
	 * 										not-null="true"
	 */
	public int getVersion() {
		return version;
	}

	public void setViewEvents(List<PackageViewEvent> viewEvents) {
		this.viewEvents = viewEvents;
	}

	/**
	 * 
	 * @return
	 * @hibernate.list cascade="none" inverse="true"
	 * @hibernate.one-to-many class="org.psygrid.meds.events.PackageViewEvent"
	 * @hibernate.key column="c_package_id" not-null="true"
	 * @hibernate.list-index column="c_index"
	 */
	public List<PackageViewEvent> getViewEvents() {
		return viewEvents;
	}

	/**
	 * 
	 * @return
	 * @hibernate.list cascade="none" inverse="true"
	 * @hibernate.one-to-many class="org.psygrid.meds.events.MedsPackageStatusChangeEvent"
	 * @hibernate.key column="c_package_id" not-null="true"
	 * @hibernate.list-index column="c_index"
	 */
	public List<MedsPackageStatusChangeEvent> getStatusChangeEvents() {
		return statusChangeEvents;
	}


	public void setStatusChangeEvents(
			List<MedsPackageStatusChangeEvent> statusChangeEvents) {
		this.statusChangeEvents = statusChangeEvents;
	}
	
	public void addStatusChangeEvent(MedsPackageStatusChangeEvent e){
		statusChangeEvents.add(e);
	}
	

	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}


	/**
	 * 
	 * @return
	 * @hibernate.property column="c_proj_code" properties-name="multicolumn_unique" not-null="true"
	 */ 
	public String getProjectCode() {
		return projectCode;
	}


	public void setPackageId(String packageId) {
		this.packageId = packageId;
	}

	/**
	 * 
	 * @return
	 * @hibernate.property column="c_package_id" properties-name="multicolumn_unique" not-null="true"
	 */ 
	public String getPackageId() {
		return packageId;
	}


	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the id
	 * @hibernate.id column = "c_id" generator-class="native"
	 */
	public Long getId() {
		return id;
	}

	/**
	 * 
	 * @return
	 * @hibernate.property column="c_shipment_number"
	 */ 
	public String getShipmentNumber() {
		return shipmentNumber;
	}


	public void setShipmentNumber(String shipmentNumber) {
		this.shipmentNumber = shipmentNumber;
	}


	/**
	 * 
	 * @return
	 * @hibernate.property column="c_batch_number"
	 */ 
	public String getBatchNumber() {
		return batchNumber;
	}


	public void setBatchNumber(String batchNumber) {
		this.batchNumber = batchNumber;
	}

	/**
	 * 
	 * @return
	 * @hibernate.property column="c_expiry_date"
	 * 									not-null="true"
	 */
	public Date getExpiryDate() {
		return expiryDate;
	}


	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}


	/**
	 * 
	 * @return
	 * @hibernate.property column="c_qp_release"
	 */
	public boolean getQpRelease() {
		return qpRelease;
	}


	public void setQpRelease(boolean qpRelease) {
		this.qpRelease = qpRelease;
	}
	
}
