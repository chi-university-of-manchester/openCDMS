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

import java.io.FileOutputStream;
import java.util.List;
import java.util.Properties;

import org.apache.axis.encoding.Base64;
import org.apache.axis.utils.Options;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opensaml.SAMLAssertion;
import org.psygrid.esl.model.IAddress;
import org.psygrid.esl.model.IProject;
import org.psygrid.esl.model.ISubject;
import org.psygrid.esl.model.hibernate.Address;
import org.psygrid.esl.model.hibernate.Subject;
import org.psygrid.esl.services.client.EslClient;
import org.psygrid.security.attributeauthority.client.AAQueryClient;
import org.psygrid.security.authentication.client.LoginClient;
import org.psygrid.security.authentication.service.LoginServicePortType;
import org.psygrid.security.components.net.PsyGridClientSocketFactory;
import org.psygrid.security.utils.PropertyUtilities;
import org.psygrid.www.xml.security.core.types.ProjectType;

/**
 * Populates the database with an example project to test the web services
 * 
 * @author Lucy Bridges
 *
 */
public class EslCROTest {

	private static LoginServicePortType aa1 = null;

	private static Log _log = LogFactory.getLog(EslCROTest.class);

	private static EslClient client;

	private static String studyNumber = "studyNumber-70";

	/**
	 * @param args the username and password required for authentication with the ws
	 */
	public static void main(String[] args) throws Exception {

		SAMLAssertion saml = login(args);

		client = new EslClient();

		System.out.println("Esl version is: "+ client.getVersion());


		IProject project = client.retrieveProjectByCode("EDT", saml.toString());
		System.out.println("Project retrieved. Id is:" + project.getId() );
	
		//allocate treatment arms to subjects and lookup the results
		//allocateTreatments(project, saml.toString());

		//retrieveProject
		IProject p1 = client.retrieveProject(project.getId(), saml.toString());
		System.out.println("Project retrieved. Project code is:" + p1.getProjectCode() );

	
		project = client.retrieveProjectByCode("EDT", saml.toString());
		//subject searches
		exampleSubjectSearches(project, saml.toString());

		//exists
		if (client.exists(project, studyNumber, saml.toString())) {
			System.out.println("The subject '"+studyNumber+"' exists");
		}
		else {
			System.out.println("The subject '"+studyNumber+"' doesn't exist!");
		}


	}

	private static SAMLAssertion login(String[] args) throws Exception {

		System.setProperty("axis.socketSecureFactory",
		"org.psygrid.security.components.net.PsyGridClientSocketFactory");
		Options opts = new Options(args);
		Properties properties = PropertyUtilities.getProperties("test.properties");
		System.out.println(properties.getProperty("org.psygrid.security.authentication.client.trustStoreLocation"));
		LoginClient tc = null;

		try {
			tc = new LoginClient("test.properties");
			aa1 = tc.getPort();
		} catch (Exception e) {
			e.printStackTrace();
		}
		char[] password = opts.getPassword().toCharArray();
		short[] pwd = new short[password.length];
		for (int i = 0; i < pwd.length; i++) {
			pwd[i] = (short) password[i];
		}
		String credential = tc.getPort().login(opts.getUser(), pwd);
		if (credential != null) {
			byte[] ks = Base64.decode(credential);
			FileOutputStream fos = new FileOutputStream(properties
					.getProperty("org.psygrid.security.authentication.client.keyStoreLocation"));
			fos.write(ks);
			fos.flush(); 
			fos.close();
		}
		System.out.println("loggedin");
		System.setProperty("javax.net.ssl.keyStorePassword", new String(password));
		PsyGridClientSocketFactory.reinit();
		AAQueryClient qc = new AAQueryClient("test.properties");
		System.out.println("getAssertion");
		SAMLAssertion sa = qc.getSAMLAssertion(new ProjectType("EDIE Test", "EDT", null, null, false));

		return sa;
	}




	private static void allocateTreatments(IProject project, String saml) throws Exception {

		ISubject subject = null;
		for (int i = 0; i < 10; i++) {
			subject = client.retrieveSubjectByStudyNumber(project, "studyNumber-"+i, saml);
			if (subject != null) {
				client.randomiseSubject(subject, null, saml);
			}
		}
	}


	private static void exampleSubjectSearches(IProject project, String saml) throws Exception {

		ISubject exampleSubject = new Subject();
		exampleSubject.setFirstName("firstName-71");
		exampleSubject.setStudyNumber("studyNumber-71");

		//lookupStudyNumber
		String sn = client.lookupStudyNumber(project, exampleSubject, saml.toString());
		System.out.println("Example Subject's study number is: " + sn);

		//findSubjectByExample
		List<ISubject> list = client.findSubjectByExample(project, exampleSubject, saml);
		System.out.println("FindSubjectByExample results list: "+list);

		ISubject exampleSubject2 = new Subject();
		exampleSubject2.setStudyNumber(studyNumber);
		IAddress address = new Address();
		address.setAddress1("nothing");
		exampleSubject2.setAddress(address);
		List<ISubject> emptylist = client.findSubjectByExample(project, exampleSubject2, saml);
		System.out.println("FindSubjectByExample results list (should be empty): "+emptylist);

		
		Long subjectId = list.get(0).getId();
		//retrieveSubject
		ISubject s = client.retrieveSubject(subjectId, saml);
		System.out.println("retrieved subject: "+s.getFirstName()+" "+s.getLastName());
	}

}
