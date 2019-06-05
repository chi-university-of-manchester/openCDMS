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

package org.psygrid.esl.model.hibernate;

import org.psygrid.esl.model.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.lang.reflect.*;

/**
 * A realization of an individual subject.
 * 
 * @author Lucy Bridges
 * 
 * @hibernate.joined-subclass table="t_subjects"
 * 								proxy="org.psygrid.esl.model.hibernate.Subject"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class Subject extends Auditable implements ISubject{

	private String title;

	private String firstName;

	private String lastName;

	private String studyNumber;

	private String sex;

	private Date dateOfBirth = null;

	private IAddress address = null;

	private String workPhone;

	private String mobilePhone;

	private String nhsNumber;

	private String hospitalNumber;

	private String centreNumber;

	private String emailAddress;
	
	private IGroup group = null;

	private String riskIssues;
	
	private boolean locked;
	
	private List<ICustomValue> customValues = new ArrayList<ICustomValue>();
	
	/**
	 * Default constructor, required by Hibernate
	 */
	public Subject() {
	}

	/**
	 * Constructor that accepts Subject's study number.
	 *  
	 * @param studyNumber The identifier of the Subject.
	 */
	public Subject(String studyNumber) {
		this.studyNumber = studyNumber;
	}



	/**
	 * @hibernate.property column="c_first_name" index="name_index"
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
	 * @hibernate.property column="c_hospital_number"
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
	 * @hibernate.property column="c_study_number"
	 *  	       			not-null="true"
	 *                      unique="true"
	 */
	public String getStudyNumber() {
		return studyNumber;
	}

	/**
	 * @param studyNumber the study number to set
	 */
	public void setStudyNumber(String studyNumber) {
		this.studyNumber = studyNumber;
	}

	/**
	 * @hibernate.property column="c_last_name" index="name_index"
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
	 * @hibernate.property column="c_title"
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
	 * @hibernate.property column="c_mobile_phone" index="mobile_phone_index"
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
	 * @hibernate.property column="c_nhs_number" index="nhs_number_index"
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
	 * @hibernate.property column="c_sex"
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
	 * @hibernate.property column="c_date_of_birth" type="date" index="dob_index"
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
	 * @return the address
	 * 
	 * @hibernate.many-to-one class="org.psygrid.esl.model.hibernate.Address"
	 *                        column="c_address_id"
	 *                        not-null="false"
	 *                        unique="true"
	 *                        cascade="all"                   
	 */
	public IAddress getAddress() {
		return address;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(IAddress address) {

		this.address = address;

		//Provenance prov = new Provenance(oldAddress, (Address)this.address);
		//this.provItems.add(prov);
	}

	/**
	 * @hibernate.property column="c_work_phone"
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
	 * @hibernate.property column="c_centre_number"
	 */
	public String getCentreNumber() {
		return centreNumber;
	}

	/**
	 * Set the centre number (normally based on the nhs group 
	 * a subject belongs to).
	 * 
	 * @param centreNumber to set 
	 */
	public void setCentreNumber(String centreNumber) {
		this.centreNumber = centreNumber;
	}

	/**
	 * @hibernate.property column="c_email_address"
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
	 * @hibernate.many-to-one class="org.psygrid.esl.model.hibernate.Group"
	 *                        column="c_group_id"
	 *                        not-null="true"
	 */
	public IGroup getGroup() {
		return group;
	}

	
	public void setGroup(IGroup group) {
		this.group = group;
	}

	/**
	 *  @hibernate.property column="c_risk_issues" 
	 *  					type="text"
     *                      length="4096"
	 * 
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

	/**
	 * @hibernate.property column="c_locked"
	 */
	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	/**
	 * 
	 * @hibernate.list cascade="all"
	 * @hibernate.one-to-many class="org.psygrid.esl.model.hibernate.CustomValue"
	 * @hibernate.key column="c_subject_id" not-null="true"
	 * @hibernate.list-index column="c_index"
	 */
	public List<ICustomValue> getCustomValues() {
		return customValues;
	}

	public void setCustomValues(List<ICustomValue> customValues) {
		this.customValues = customValues;
	}

	public int getCustomValueCount(){
		return customValues.size();
	}
	
	public void addCustomValue(ICustomValue value) throws ModelException {
		if ( null == value ){
			throw new ModelException("Cannot add a null custom value");
		}
		customValues.add(value);
	}
	
	public ICustomValue getCustomValue(int index) throws ModelException {
		try{
			return customValues.get(index);
		}
		catch (IndexOutOfBoundsException ex){
			throw new ModelException("No custom value exists for index "+index, ex);
		}
	}
	
	public void addOrUpdateCustomValue(String name, String value){
		if ( null == value ){
			throw new ModelException("Cannot add a null custom value");
		}
		for ( ICustomValue customValue: customValues ){
			if ( customValue.getName().equals(name) ){
				customValue.setValue(value);
				return;
			}
		}
		//Reaching this point implies that no custom value with the given name
		//exists, so create a new one
		customValues.add(new CustomValue(name, value));
	}
	
	/**
	 * Store object reference to maintain persistence
	 * Create list to hold references to objects that have multiple 
	 * references to them within the object graph. This is used so 
	 * that each object instance is copied to its DTO equivalent 
	 * once and once only.
	 *
	 * @return dto.Subject
	 */
	public org.psygrid.esl.model.dto.Subject toDTO(){
		Map<IPersistent, org.psygrid.esl.model.dto.Persistent> dtoRefs = new HashMap<IPersistent, org.psygrid.esl.model.dto.Persistent>();
		org.psygrid.esl.model.dto.Subject dtoSubject = toDTO(dtoRefs);
		dtoRefs = null;
		return dtoSubject;
	}

	public org.psygrid.esl.model.dto.Subject toDTO(Map<IPersistent, org.psygrid.esl.model.dto.Persistent> dtoRefs) {

		org.psygrid.esl.model.dto.Subject dtoS = null;
		if ( dtoRefs.containsKey(this)){
			dtoS = (org.psygrid.esl.model.dto.Subject)dtoRefs.get(this);
		}
		if ( dtoS == null ){
			dtoS = new org.psygrid.esl.model.dto.Subject();
			dtoRefs.put(this, dtoS);
			toDTO(dtoS, dtoRefs);
		}
		return dtoS;
	}

	public void toDTO(org.psygrid.esl.model.dto.Subject dtoS, Map<IPersistent, org.psygrid.esl.model.dto.Persistent> dtoRefs){
		super.toDTO(dtoS, dtoRefs);
		dtoS.setStudyNumber(this.studyNumber);

		if (address != null) {
			dtoS.setAddress(address.toDTO(dtoRefs));
		}
		dtoS.setCentreNumber(centreNumber);
		dtoS.setDateOfBirth(dateOfBirth);
		dtoS.setFirstName(firstName);
		dtoS.setLastName(lastName);
		dtoS.setTitle(title);
		dtoS.setSex(sex);
		dtoS.setHospitalNumber(hospitalNumber);
		dtoS.setMobilePhone(mobilePhone);
		dtoS.setNhsNumber(nhsNumber);
		dtoS.setWorkPhone(workPhone);
		dtoS.setEmailAddress(emailAddress);
		dtoS.setRiskIssues(riskIssues);
		dtoS.setLocked(locked);

		if (group != null) {
			dtoS.setGroup(((Group)group).toDTO(dtoRefs));
		}
		
		if (this.customValues != null) { 
			org.psygrid.esl.model.dto.CustomValue[] dtoCVs = new org.psygrid.esl.model.dto.CustomValue[this.customValues.size()];
			for (int i=0; i<this.customValues.size(); i++){
				ICustomValue cv = customValues.get(i);
				if (cv != null) {
					dtoCVs[i] = cv.toDTO(dtoRefs);
				}
			}        
			dtoS.setCustomValues(dtoCVs);            
		}
	}


	/**
	 * For a given list of Strata, use introspection to see if any of
	 * them match the fields of this class and return its value.
	 * 
	 * @param strata The strata specified for the randomisation to be applied
	 * @return Map of strata names and their values from the address object
	 * @throws StrataAllocationFault
	 */
	public Map<String, String> getStrataValues(List<IStrata> strata) throws StrataAllocationFault {

		Class c = this.getClass();
		Field[] fields = c.getDeclaredFields();

		Map<String,String> values = new HashMap<String,String>();

		for (IStrata s: strata) {

			for (Field f: fields) {
				if (s.getName().equalsIgnoreCase(f.getName())) {

					try {
						String value = (String)f.get(this);

						if (value == null) {
							throw new StrataAllocationFault("No value specified for strata "+s.getName());
						}
						//retrieve a list of the allowed values for this strata
						boolean allowed = false;
						List<String> possibleValues = s.getValues();
						for (String possible: possibleValues) {
							if (value.equals(possible)) {
								allowed = true;
								break;
							}
						}
						if (! allowed) {
							//the value given by the subject does not match any
							//of the strata's permitted values
							throw new StrataAllocationFault("The value given by the subject "+studyNumber+" is not included in the range permitted by the Strata "+s.getName()+". The allowed range is:"+possibleValues);
						}

						values.put(s.getName(),value);
					}
					catch (Exception e) {
						throw new StrataAllocationFault("No value specified for strata "+s.getName() , e);
					}
				}
			}

		}

		//Search fields within address as well
		if (address != null) {
			Map<String,String> addr = ((Address)address).getStrataValues(strata);
			if (addr != null) {
				values.putAll(addr);
			}
		}
		
		//finally, search the custom values
		for (IStrata s: strata) {
			for ( ICustomValue cv: customValues ){
				if ( s.getName().equalsIgnoreCase(cv.getName()) ){
					boolean allowed = false;
					for (String possible: s.getValues()) {
						if (cv.getValue().equals(possible)) {
							allowed = true;
							break;
						}
					}
					if (! allowed) {
						//the value given by the subject does not match any
						//of the strata's permitted values
						throw new StrataAllocationFault("The value given by the subject "+studyNumber+" is not included in the range permitted by the Strata "+s.getName()+". The allowed range is:"+s.getValues());
					}

					values.put(s.getName(), cv.getValue());
				}
			}
		}
		
		return values;
	}

}
