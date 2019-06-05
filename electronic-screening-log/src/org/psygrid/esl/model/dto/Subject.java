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

package org.psygrid.esl.model.dto;

import java.util.Date;
import java.util.Map;
import java.util.HashMap;


/**
 * Class representing a Subject participating in the trial held by the ESL
 * 
 * @author Lucy Bridges
 *
 */
public class Subject extends Auditable {

	private String title;

	private String firstName;

	private String lastName;

	private String studyNumber;

	private String sex;

	private Date dateOfBirth = null;

	private Address address = null;

	private String workPhone;

	private String mobilePhone;

	private String nhsNumber;

	private String hospitalNumber;

	private String centreNumber;
	
	private String emailAddress;

	private Group group = null;
	
	private String riskIssues;
	
	private boolean locked;
	
	private CustomValue[] customValues = new CustomValue[0];
	
	/**
	 * @return the studyNumber
	 */
	public String getStudyNumber() {
		return studyNumber;
	}

	/**
	 * @param studyNumber the studyNumber to set
	 */
	public void setStudyNumber(String studyNumber) {
		this.studyNumber = studyNumber;
	}

	/**
	 * @return the address
	 */
	public Address getAddress() {
		return address;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(Address address) {
		this.address = address;
	}

	/**
	 * @return the centreNumber
	 */
	public String getCentreNumber() {
		return centreNumber;
	}

	/**
	 * @param centreNumber the centreNumber to set
	 */
	public void setCentreNumber(String centreNumber) {
		this.centreNumber = centreNumber;
	}

	/**
	 * @return the dateOfBirth
	 */
	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	/**
	 * @param dateOfBirth the dateOfBirth to set
	 */
	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the hospitalNumber
	 */
	public String getHospitalNumber() {
		return hospitalNumber;
	}

	/**
	 * @param hospitalNumber the hospitalNumber to set
	 */
	public void setHospitalNumber(String hospitalNumber) {
		this.hospitalNumber = hospitalNumber;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the mobilePhone
	 */
	public String getMobilePhone() {
		return mobilePhone;
	}

	/**
	 * @param mobilePhone the mobilePhone to set
	 */
	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	/**
	 * @return the nhsNumber
	 */
	public String getNhsNumber() {
		return nhsNumber;
	}

	/**
	 * @param nhsNumber the nhsNumber to set
	 */
	public void setNhsNumber(String nhsNumber) {
		this.nhsNumber = nhsNumber;
	}

	/**
	 * @return the sex
	 */
	public String getSex() {
		return sex;
	}

	/**
	 * @param sex the sex to set
	 */
	public void setSex(String sex) {
		this.sex = sex;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the workPhone
	 */
	public String getWorkPhone() {
		return workPhone;
	}

	/**
	 * @param workPhone the workPhone to set
	 */
	public void setWorkPhone(String workPhone) {
		this.workPhone = workPhone;
	}

	/**
	 * @return emailAddress
	 */
	public String getEmailAddress() {
		return emailAddress;
	}

	/**
	 * @param emailAddress the emailAddress to set
	 */
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	
	/**
	 * @return the group
	 */
	public Group getGroup() {
		return group;
	}

	/**
	 * @param group the group to set
	 */
	public void setGroup(Group group) {
		this.group = group;
	}

	/**
	 * @return the riskIssues
	 */
	public String getRiskIssues() {
		return riskIssues;
	}

	/**
	 * @param riskIssues the riskIssues to set
	 */
	public void setRiskIssues(String riskIssues) {
		this.riskIssues = riskIssues;
	}
	
	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public CustomValue[] getCustomValues() {
		return customValues;
	}

	public void setCustomValues(CustomValue[] customValues) {
		this.customValues = customValues;
	}

	public org.psygrid.esl.model.hibernate.Subject toHibernate(){
		//create list to hold references to objects in the project's
		//object graph which have multiple references to them within
		//the object graph. This is used so that each object instance
		//is copied to its hibernate equivalent once and once only
		Map<Persistent, org.psygrid.esl.model.hibernate.Persistent> dtoRefs = new HashMap<Persistent, org.psygrid.esl.model.hibernate.Persistent>();
		org.psygrid.esl.model.hibernate.Subject hSubject = toHibernate(dtoRefs);
		dtoRefs = null;
		return hSubject;
	}

	public org.psygrid.esl.model.hibernate.Subject toHibernate(Map<Persistent, org.psygrid.esl.model.hibernate.Persistent> hRefs){
	     //check for an already existing instance of a hibernate object for this 
        //record in the map of references
		org.psygrid.esl.model.hibernate.Subject hS = null;
        if ( hRefs.containsKey(this)){
            hS = (org.psygrid.esl.model.hibernate.Subject)hRefs.get(this);
        }
        if ( null == hS ){
            //an instance of the record has not already
            //been created, so create it, and add it to 
            //the map of references	
        	hS = new org.psygrid.esl.model.hibernate.Subject();
        	hRefs.put(this, hS);
        	toHibernate(hS, hRefs);
        }
		return hS;
	}

	public void toHibernate(org.psygrid.esl.model.hibernate.Subject hS, Map<Persistent, org.psygrid.esl.model.hibernate.Persistent> hRefs){
		super.toHibernate(hS, hRefs);
		hS.setStudyNumber(this.studyNumber);  

		if (address != null) {
			address.setSubject(this);
			hS.setAddress(address.toHibernate(hRefs));
		}
		hS.setCentreNumber(centreNumber);
		hS.setDateOfBirth(dateOfBirth);
		hS.setFirstName(firstName);
		hS.setLastName(lastName);
		hS.setTitle(title);
		hS.setSex(sex);
		hS.setHospitalNumber(hospitalNumber);
		hS.setMobilePhone(mobilePhone);
		hS.setNhsNumber(nhsNumber);
		hS.setWorkPhone(workPhone);
		hS.setEmailAddress(emailAddress);
		hS.setRiskIssues(riskIssues);
		hS.setLocked(locked);
		
		if (group != null) {
			hS.setGroup(group.toHibernate(hRefs));
		}
		
		if (customValues != null) {
			for (int i=0, c=customValues.length; i<c; i++){
				CustomValue cv = customValues[i];
				if ( null != cv ){
					hS.getCustomValues().add(cv.toHibernate(hRefs));
				}
			}    
		}

	}

}
