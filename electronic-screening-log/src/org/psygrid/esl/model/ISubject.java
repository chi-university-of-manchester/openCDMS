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

package org.psygrid.esl.model;

import java.util.Date;
import java.util.Map;
import java.util.List;


/**
 * Interface to represent the definition of a Subject in the 
 * clinical trial.
 * 
 * @author Lucy Bridges
 *
 */
public interface ISubject extends IAuditable {

	/**
	 * Set the Subject's title. 
	 * 
	 * i.e 'Mr', 'Miss', 'Mrs'..
	 * 
	 * @param title
	 */
	public void setTitle(String title);
	
	/**
	 * Get the Subject's title. 
	 * 
	 * i.e 'Mr', 'Miss', 'Mrs'..
	 * 
	 * @return String
	 */
	public String getTitle();
	
	/**
	 * Set the Subject's first name
	 * 
	 * @param firstName
	 */
	public void setFirstName(String firstName);
	
	/**
	 * Retrieve the Subject's first name
	 * 
	 * @return String
	 */
	public String getFirstName();
	
	/**
	 * Set the Subject's last name
	 * 
	 * @param lastName
	 */
	public void setLastName(String lastName);
	
	/**
	 * Retrieve the Subject's last name
	 * 
	 * @return String
	 */
	public String getLastName();

	/**
	 * Set the Subject's study number, as allocated by the repository
	 * 
	 * @param studyNumber
	 */
	public void setStudyNumber(String studyNumber);
	
	/**
	 * Retrieve the Subject's study number, as allocated by the repository
	 * 
	 * @return String
	 */
	public String getStudyNumber();
	
	/**
	 * Set the sex of the Subject
	 * 
	 * @param sex
	 */
	public void setSex(String sex);
	
	/**
	 * Get the sex of the Subject
	 * 
	 * @return String
	 */
	public String getSex();
	
	/**
	 * Set the Subject's date of birth
	 * 
	 * @param dob
	 */
	public void setDateOfBirth(Date dob);
	
	/**
	 * Get the Subject's date of birth
	 * 
	 * @return Date
	 */
	public Date getDateOfBirth();
	
	
	/**
	 * Get the Address object containing the Subject's address details
	 * 
	 * @return the address of the subject
     */
	public IAddress getAddress();

	/**
	 * Set the Address object containing the Subject's address details
	 * 
	 * @param address the address to set
	 */
	public void setAddress(IAddress address);
	
	/**
	 * Set the work telephone number of the Subject
	 *  
	 * @param workPhone
	 */
	public void setWorkPhone(String workPhone);
	
	/**
	 * Get the Subject's work telephone number
	 * 
	 * @return String
	 */
	public String getWorkPhone();
	
	/**
	 * Set the Subject's mobile phone number
	 * 
	 * @param mobilePhone number
	 */
	public void setMobilePhone(String mobilePhone);
	
	/**
	 * Get the Subject's mobile phone number
	 * 
	 * @return String
	 */
	public String getMobilePhone();
	
	/**
	 * Set the Subject's NHS number
	 * 
	 * @param nhsNumber
	 */
	public void setNhsNumber(String nhsNumber);
	
	/**
	 * Get the Subject's NHS number
	 * 
	 * @return String
	 */
	public String getNhsNumber();
	
	/**
	 * Set the hospital number for the Subject
	 * 
	 * @param hospitalNumber
	 */
	public void setHospitalNumber(String hospitalNumber);
	
	/**
	 * Get the hospital number for the Subject
	 * 
	 * @return String
	 */
	public String getHospitalNumber();
	
	/**
	 * Set the centre number for the Subject
	 * 
	 * @param centreNumber
	 */
	public void setCentreNumber(String centreNumber);
	
	/**
	 * Get the centre number for the Subject
	 * 
	 * @return String
	 */
	public String getCentreNumber();
	
	/**
	 * Set the email address for the Subject
	 * 
	 * @param emailAddress
	 */
	public void setEmailAddress(String emailAddress);
	
	/**
	 * Get the email address for a Subject
	 * 
	 * @return String
	 */
	public String getEmailAddress();
	
	/**
	 * Retrieve the Group the Subject belongs to
	 * 
	 * @return the subject's group
	 */
	public IGroup getGroup();
	
	/**
	 * Set the Group the Subject belongs to
	 * 
	 * @param group
	 */
	public void setGroup(IGroup group);
	
	/**
	 * Retrieve the Site the Subject belongs to
	 * @return
	 */
	//public ISite getSite();
	
	/**
	 * Set the Site the Subject belongs to
	 * @param site
	 */
	//public void setSite(ISite site);
	
	/**
	 * Retrieve a statement of any risks that a patient may pose. 
	 * Used to inform the therapist before treatment.
	 * 
	 * @return String
	 */
	public String getRiskIssues();
	
	/**
	 * Add a statement of any risks that a patient may pose. 
	 * Used to inform the therapist before treatment.
	 * 
	 * @param riskIssues
	 */
	public void setRiskIssues(String riskIssues);
	
	/**
	 * Retrieve whether the subject has been locked (True) or not.
	 * <p>
	 * If the Subject has been locked then their ESL data should only be
	 * made available in exceptional circumstances.
	 * 
	 * @return Boolean
	 */
	public boolean isLocked();
	
	/**
	 * Set whether the subject has been locked (True) or not.
	 * <p>
	 * If the Subject has been locked then their ESL data should only be
	 * made available in exceptional circumstances.
	 * 
	 * @param locked
	 */
	public void setLocked(boolean locked);

	/**
	 * Get the number of custom values associated with the subject.
	 * 
	 * @return The number of values.
	 */
	public int getCustomValueCount();
	
	/**
	 * Add a custom value to the subject.
	 * 
	 * @param value The custom value
	 * @throws ModelException if the custom value is null.
	 */
	public void addCustomValue(ICustomValue value) throws ModelException;
	
	/**
	 * Get a custom value from the subject.
	 * 
	 * @param index The index of the custom value to get.
	 * @return The custom value.
	 * @throws ModelException if no custom value exists at the given index.
	 */
	public ICustomValue getCustomValue(int index) throws ModelException;

	/**
	 * Updated an existing custom value, or if one does not exist for
	 * the given name create a new one and add it to the subject's list
	 * of custom values.
	 * 
	 * @param name Name of the custom value
	 * @param value Value of the custom value
	 * @throws ModelException
	 */
	public void addOrUpdateCustomValue(String name, String value) throws ModelException;
	
	/**
	 * Retrieve the values for a given set of strata, used by a stratified
	 * randomiser to determine the treatment to be allocated to the Subject.
	 * 
	 * @param strata
	 * @return a Map of strata names and values
	 * @throws StrataAllocationFault
	 */
	public Map<String, String> getStrataValues(List<IStrata> strata) throws StrataAllocationFault;
	
	
	public org.psygrid.esl.model.dto.Subject toDTO();
    public org.psygrid.esl.model.dto.Subject toDTO(Map<IPersistent, org.psygrid.esl.model.dto.Persistent> dtoRefs);

}
