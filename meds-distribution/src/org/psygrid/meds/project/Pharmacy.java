package org.psygrid.meds.project;

import java.util.ArrayList;
import java.util.List;

import org.psygrid.meds.events.PharmacyViewEvent;


/**
 * 
 * @author Bill Vance
 * @hibernate.class table="t_pharmacies"
 */
public class Pharmacy {
	
	private Long id; //hibernate id
	
	private String pharmacyCode;
	private String pharmacyName;
	private Project project;
	
	//private List<PharmacyViewEvent> viewEvents = new ArrayList<PharmacyViewEvent>();
	
	protected Pharmacy(){
	}
	
	public Pharmacy(String pharmacyName, String pharmacyCode){
		this.pharmacyName = pharmacyName;
		this.pharmacyCode = pharmacyCode;
	}
	
	/**
	 * @return the id
	 * @hibernate.id column = "c_id" generator-class="native"
	 */
	public Long getId() {
		return id;
	}
	
	protected void setId(Long id) {
		this.id = id;
	}
	
	/**
	 * 
	 * @return
	 * @hibernate.property column="c_name"
	 * 								not-null="true"
	 */
	public String getPharmacyName() {
		return pharmacyName;
	}
	
	public void setPharmacyName(String pharmacyName) {
		this.pharmacyName = pharmacyName;
	}
	
	/**
	 * 
	 * @return
	 * @hibernate.property column="c_code"
	 * 								not-null="true"
	 */
	public String getPharmacyCode() {
		return pharmacyCode;
	}

	public void setPharmacyCode(String pharmacyCode) {
		this.pharmacyCode = pharmacyCode;
	}

	/**
	 * 
	 * @return - the pharmacy with which the Package is affiliated
	 * @hibernate.many-to-one class="org.psygrid.meds.project.Project"
	 *                        column="c_project_id"
	 *                        not-null="true"
	 *                        cascade="none"
	 *                        update="false"
	 *                        insert="false"
	 */
	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

}
