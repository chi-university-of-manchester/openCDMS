package org.psygrid.meds.project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.psygrid.common.email.Email;
import org.psygrid.meds.actions.notify.NotificationType;
import org.psygrid.meds.events.ProjectViewEvent;


/**
 * Holds high-level information for projects requiring a medication distribution service.
 * @author Bill Vance
 * @hibernate.class table="t_meds_project"
 */
public class Project {

	private Long id; //hibernate id
	
	private String projectName; //friendly project name
	private String projectCode; //unique project code 
	private Date creationDate; //date the project was persisted.
	private List<Treatment> treatments 	= new ArrayList<Treatment>(); //List of treatments associated with the project.
	private List<Pharmacy> 		pharmacies 		= new ArrayList<Pharmacy>(); //List of centres associated with the project.
	
	/**
	 * Emails to be sent out to various roles at different points during 
	 * the randomisation process
	 */
	private Map<String, Email> emails = new HashMap<String, Email>();
	private List<ProjectViewEvent> viewEvents = new ArrayList<ProjectViewEvent>();
	protected Project(){
		
	}
	
	public Project(String projectName, String projectCode, List<Treatment> t, List<Pharmacy> p, Map<String,Email> emails){
		this.projectName = projectName;
		this.projectCode = projectCode;
		this.treatments = t;
		this.pharmacies = p;
		this.emails = emails;
	}
	
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	
	public boolean addPharmacy(Pharmacy p){
		return pharmacies.add(p);
	}
	
	/**
	 * 
	 * @return the friendly project name
	 * @hibernate.property column="c_name"
	 * 							not-null="true"
	 */
	public String getProjectName() {
		return projectName;
	}
	
	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}
	
	/**
	 * 
	 * @return the project code
	 * @hibernate.property column="c_code"
	 * 								unique="true"
	 * 								not-null="true"
	 */
	public String getProjectCode() {
		return projectCode;
	}
	
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	
	/**
	 * 
	 * @return the date the project was persisited
	 * @hibernate.property column="c_created_date"
	 */
	public Date getCreationDate() {
		return creationDate;
	}

	/**
	 * This method is not offered through any interfaces.
	 * @param treatments
	 */
	protected void setTreatments(List<Treatment> treatments) {
		this.treatments = treatments;
	}
		
	/**
	 * 
	 * @return - the treatments designated for this clinical trial
	 * @hibernate.list cascade="all"
	 * @hibernate.one-to-many class="org.psygrid.meds.project.Treatment"
	 * @hibernate.key column="c_project_id" not-null="true"
	 * @hibernate.list-index column="c_index"
	 */
	public  List<Treatment> getTreatments() {
		return treatments;
	}

	
	/**
	 * This method is not exposed through any interface.
	 * @param centres
	 */
	protected void setPharmacies(List<Pharmacy> pharmacies) {
		this.pharmacies = pharmacies;
	}

	/**
	 * 
	 * @return - list of centres in the project
	 * @hibernate.list cascade="all"
	 * @hibernate.one-to-many class="org.psygrid.meds.project.Pharmacy
	 * @hibernate.key column="c_project_id" not-null="true"
	 * @hibernate.list-index column="c_index"
	 */
	public List<Pharmacy> getPharmacies() {
		return pharmacies;
	}

	protected void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the id
	 * @hibernate.id column = "c_id" generator-class="native"
	 */
	public Long getId() {
		return id;
	}

	protected void setViewEvents(List<ProjectViewEvent> viewEvents) {
		this.viewEvents = viewEvents;
	}

	/**
	 * 
	 * @return - the view events for this project
	 * @hibernate.list cascade="all"
	 * @hibernate.one-to-many class="org.psygrid.meds.events.ProjectViewEvent"
	 * @hibernate.key column="c_project_id" not-null="false"
	 * @hibernate.list-index column="c_index"
	 */
	public List<ProjectViewEvent> getViewEvents() {
		return viewEvents;
	}

	
	/**
	 * @return the emails
	 * 
	 * @hibernate.map cascade="all" table="t_emails_map"
	 * @hibernate.key column="c_project_id" not-null="true"
	 * @hibernate.map-key column="c_name"
     *              		type="string"
	 * @hibernate.composite-element class="org.psygrid.common.email.Email" 
	 */
	public Map<String, Email> getEmails() {
		return emails;
	}

	public void setEmails(Map<String, Email> emails) {
		this.emails = emails;
	}
	
	public Email getEmailByType(NotificationType type){
		return emails.get(type.toString());
	}






	
	
	
}
