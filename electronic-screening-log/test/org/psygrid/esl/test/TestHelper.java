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

package org.psygrid.esl.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.psygrid.common.email.Email;
import org.psygrid.esl.model.IAddress;
import org.psygrid.esl.model.IFactory;
import org.psygrid.esl.model.IGroup;
import org.psygrid.esl.model.IProject;
import org.psygrid.esl.model.IRandomisation;
import org.psygrid.esl.model.IRole;
import org.psygrid.esl.model.IStrata;
import org.psygrid.esl.model.ISubject;
import org.psygrid.esl.randomise.EmailType;

/**
 * Generic helper methods for writing test cases for the esl.
 * 
 * @author Lucy Bridges
 *
 */
public class TestHelper {
	
	public final String SUBJECT_FIRSTNAME = "subject name";
	public final String STUDY_NUMBER 	  = getUnique();
	public final String SUBJECT_LASTNAME  = "Test 1";
	public final String CENTRE_NUMBER     = "123";
	
	public final String GROUP_NAME1 = "g1";
	public final String GROUP_NAME2 = "g2";
	
	public final String ROLE_NAME   = "Therapist";
	public final String ROLE_NAME2  = "Another role";
	
	public final String STRATA_NAME = "sex";
	
	public final String RANDOMISATION_NAME = "random"+getUnique();
	
	public final String EMAIL_NAME    = EmailType.DECISION.type();
	public final String EMAIL_SUBJECT = "Test 1";
	public final String EMAIL2_NAME   = EmailType.TREATMENT.type();

	public final String TREATMENT_NAME  = "code a";
	public final String TREATMENT_VALUE = "treatment name a";
	
	public final String ADDRESS_LINE_1 = "Address line 1";
	public final String ADDRESS_LINE_2 = "Address line 2";
	public final String ADDRESS_LINE_3 = "Address line 3";
	public final String ADDRESS_CITY   = "Manchester";
    
	/**
	 * Generate data to populate an example project.
	 * 
	 * @param newproject
	 * @return newproject
	 */
	public IProject populateProject(String projectName, IFactory factory) {
		
		IProject newproject = factory.createProject(projectName);
		
		//This used to generate a unique code but that meant there was no way to 
		//clear up the project after the test. 
		newproject.setProjectCode("TST");
        //newproject.setProjectName(projectName);
        
        IRandomisation random = factory.createRandomisation(RANDOMISATION_NAME);

		IRole role = factory.createRole(ROLE_NAME);
		List<IRole> roles = new ArrayList<IRole>();
		
		role.setNotifyOfRSDecision(true);
		role.setNotifyOfRSInvocation(true);
		role.setNotifyOfRSTreatment(true);
		
		roles.add(role);
		random.setRolesToNotify(roles);

		Email email = factory.createEmail();
		email.setBody("The subject %subjectCode% has been allocated a treatment arm.");
		email.setSubject(EMAIL_SUBJECT);
		
		Email email2 = factory.createEmail();
		email2.setBody("The subject %subjectCode% has been assigned treatment %treatment%." +
				"\n" +
				"The subject has the following risk issues:\n\n" +
				"%riskIssues%");
		email2.setSubject(EMAIL_SUBJECT);
		
		
		Map<String, Email> emails = new HashMap<String,Email>();
		emails.put(EMAIL_NAME, email);
		emails.put(EMAIL2_NAME, email2);
		
		random.setEmails(emails);
		
		Map<String,String> treatments = new HashMap<String,String>();
		treatments.put(TREATMENT_NAME, TREATMENT_VALUE);
		treatments.put("code2", "Treatment name2");
		random.setTreatments(treatments);
		
		IStrata stratum = factory.createStrata(STRATA_NAME);
		List<String> values = new ArrayList<String>();
		values.add("male");
		values.add("female");
		stratum.setValues(values);
		
		IStrata stratum2 = factory.createStrata("centreNumber");
		List<String> stratavalues = new ArrayList<String>();
		stratavalues.add("abc");
		stratavalues.add("123");
		stratum2.setValues(stratavalues);
		
		List<IStrata> strata = new ArrayList<IStrata>();
		strata.add(stratum);
		strata.add(stratum2);
		
		random.setStrata(strata);
		
		newproject.setRandomisation(random);
		
		IGroup grp1 = factory.createGroup(GROUP_NAME1);
        grp1.setGroupCode(GROUP_NAME1+"-"+getUnique());
        grp1.setGroupName(GROUP_NAME1);

        IGroup grp2 = factory.createGroup(GROUP_NAME2);
		grp2.setGroupCode(GROUP_NAME2+"-"+getUnique());
        grp2.setGroupName(GROUP_NAME2);
        
        newproject.setGroup(grp1);
        newproject.setGroup(grp2);
      
        ISubject s1 = factory.createSubject(SUBJECT_LASTNAME);
        s1.setStudyNumber(STUDY_NUMBER);
        s1.setLastName(SUBJECT_LASTNAME);
        s1.setCentreNumber(CENTRE_NUMBER);
        s1.setDateOfBirth(new Date());
        s1.setFirstName(SUBJECT_FIRSTNAME);
        s1.setHospitalNumber("hospital no.");
        s1.setMobilePhone("123123123");
        s1.setNhsNumber("123-123-nhs");
        s1.setSex("female");
        s1.setEmailAddress("subject@domain.com");
        s1.setTitle("miss");
        s1.setWorkPhone("+12 123123");
        s1.setRiskIssues("A description of potential risks associated with this patient." +
        		"\nRisk 1\nRisk 2\nRisk 3");
        
        IAddress a1 = factory.createAddress();
        a1.setAddress1(ADDRESS_LINE_1);
        a1.setAddress2(ADDRESS_LINE_2);
        a1.setAddress3(ADDRESS_LINE_3);
        a1.setCity(ADDRESS_CITY);
        a1.setPostCode("ABC 123");
        a1.setRegion("region");
        a1.setCountry("Britain");
        a1.setHomePhone("123123");
        
        s1.setAddress(a1);
       
        grp1.setSubject(s1);  
     
        return newproject;
	}
	
	public static String getUnique() {
		//generate unique project code/name of something
		java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
		return guid.toString();
	}
}
