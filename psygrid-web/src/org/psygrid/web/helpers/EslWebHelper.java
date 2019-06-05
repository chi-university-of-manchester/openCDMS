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


package org.psygrid.web.helpers;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.esl.model.IAddress;
import org.psygrid.esl.model.ISubject;
import org.psygrid.esl.model.hibernate.Address;
import org.psygrid.web.details.PsygridUserDetails;
import org.psygrid.web.forms.PersonalDetails;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;
import org.springframework.web.servlet.view.RedirectView;

/**
 * @author Rob Harper
 *
 */
public class EslWebHelper {

	private static final Log log = LogFactory.getLog(EslWebHelper.class);

	private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");

	public static ISubject createSubjectFromForm(PersonalDetails formSubject, ISubject eslSubject){

		String title = formSubject.getTitle();
		if (title != null && !title.equals(""))
		{
			eslSubject.setTitle(title);
		}

		String firstName = formSubject.getFirstName();
		if (firstName != null && !firstName.equals(""))
		{
			eslSubject.setFirstName(firstName);
		}

		String lastName = formSubject.getLastName();
		if (lastName != null && !lastName.equals(""))
		{
			eslSubject.setLastName(lastName);
		}

		String emailAddress = formSubject.getEmailAddress();
		if (emailAddress != null && ! emailAddress.equals(""))
		{
			eslSubject.setEmailAddress(emailAddress);
		}

		if (formSubject.getDateOfBirth() != null) {
			try {
				eslSubject.setDateOfBirth(dateFormatter.parse(formSubject.getDateOfBirth()));
			}
			catch (ParseException e) {
				log.debug("PDQ : Parse Exception casting date of birth to a date object" + e.getMessage());
			}
		}

		String sex = formSubject.getSex();
		if (sex != null && !sex.equals(""))
		{
			eslSubject.setSex(sex);
		}


		String hospitalNumber = formSubject.getHospitalNumber();
		if (hospitalNumber != null && !hospitalNumber.equals(""))
		{
			eslSubject.setHospitalNumber(hospitalNumber);
		}

		String nhsNumber = formSubject.getNhsNumber();
		if (nhsNumber != null && !nhsNumber.equals(""))
		{
			eslSubject.setNhsNumber(nhsNumber);
		}

		String mobilePhoneNumber = formSubject.getMobilePhone();
		if (mobilePhoneNumber != null && !mobilePhoneNumber.equals(""))
		{
			eslSubject.setMobilePhone(mobilePhoneNumber);
		}

		String workPhoneNumber = formSubject.getWorkPhone();
		if (workPhoneNumber != null && !workPhoneNumber.equals(""))
		{
			eslSubject.setWorkPhone(workPhoneNumber);
		}

		String centreNumber = formSubject.getCentreNumber();
		if (centreNumber != null && !centreNumber.equals(""))
		{
			eslSubject.setCentreNumber(centreNumber);
		}

		String riskIssues = formSubject.getRiskIssues();
		if (riskIssues !=null && !riskIssues.equals(""))
		{
			eslSubject.setRiskIssues(riskIssues);
		}

		IAddress address = eslSubject.getAddress();

		if (address == null) {
			address = new Address();
		}

		String address1 = formSubject.getAddress1();
		if (address1 != null &&!address1.equals(""))
		{
			address.setAddress1(address1);
		}

		String address2 = formSubject.getAddress2();
		if (address2 != null && !address2.equals(""))
		{
			address.setAddress2(address2);
		}

		String address3 = formSubject.getAddress3();
		if (address3 != null && !address3.equals(""))
		{
			address.setAddress3(address3);
		}

		String city = formSubject.getCity();
		if (city != null && !city.equals(""))
		{
			address.setCity(city);
		}

		String country = formSubject.getCountry();
		if (country !=null && !country.equals(""))
		{
			address.setCountry(country);
		}

		String region = formSubject.getRegion();
		if (region !=null && !region.equals(""))
		{
			address.setRegion(region);
		}

		String homePhone = formSubject.getHomePhone();
		if (homePhone !=null && !homePhone.equals(""))
		{
			address.setHomePhone(homePhone);
		}

		String postCode = formSubject.getPostCode();
		if (postCode !=null && !postCode.equals(""))
		{
			address.setPostCode(postCode);
		}

		eslSubject.setAddress(address);

		return eslSubject;

	}

	public static ISubject createSubjectFromForm(PersonalDetails formSubject){

		ISubject eslSubject = new org.psygrid.esl.model.hibernate.Subject();
		return createSubjectFromForm(formSubject, eslSubject);

	}

	public static void populatePersonalDetailsFromSubject(PersonalDetails details, ISubject subject){
		details.setTitle(subject.getTitle());
		details.setFirstName(subject.getFirstName());
		details.setLastName(subject.getLastName());
		details.setSex(subject.getSex());
		IAddress address = subject.getAddress();
		if (address != null) {
			details.setAddress1(address.getAddress1());
			details.setAddress2(address.getAddress2());
			details.setAddress3(address.getAddress3());
			details.setCity(address.getCity());
			details.setRegion(address.getRegion());
			details.setCountry(address.getCountry());
			details.setPostCode(address.getPostCode());
			details.setHomePhone(address.getHomePhone());
		}
		details.setEmailAddress(subject.getEmailAddress());
		details.setWorkPhone(subject.getWorkPhone());
		details.setNhsNumber(subject.getNhsNumber());
		details.setCentreNumber(subject.getCentreNumber());
		details.setRiskIssues(subject.getRiskIssues());
		details.setHospitalNumber(subject.getHospitalNumber());
		details.setMobilePhone(subject.getMobilePhone());
		details.setStudyNumber(subject.getStudyNumber());
		details.setGroupCode(subject.getGroup().getGroupCode());
		details.setGroupName(subject.getGroup().getGroupName());
		if (subject.getDateOfBirth() != null) {
			details.setDateOfBirth(dateFormatter.format(subject.getDateOfBirth()));
		}
	}

	public static void checkActiveProjectAndGroup(PsygridUserDetails user) throws ModelAndViewDefiningException{
		if ( null == user.getActiveProject() || null == user.getActiveGroup() ){
			throw new ModelAndViewDefiningException(new ModelAndView(new RedirectView("index.html")));
		}
	}

}
