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
import java.util.Date;
import java.util.Properties;

import org.apache.axis.encoding.Base64;
import org.apache.axis.utils.Options;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opensaml.SAMLAssertion;
import org.psygrid.esl.model.IProject;
import org.psygrid.esl.model.ISubject;
import org.psygrid.esl.services.client.EslClient;
import org.psygrid.security.attributeauthority.client.AAQueryClient;
import org.psygrid.security.authentication.client.LoginClient;
import org.psygrid.security.authentication.service.LoginServicePortType;
import org.psygrid.security.components.net.PsyGridClientSocketFactory;
import org.psygrid.security.utils.PropertyUtilities;
import org.psygrid.www.xml.security.core.types.ProjectType;

/**
 * This test updates a Subject's details, to test that the email updates
 * are sent.
 * 
 * @author Lucy Bridges
 *
 */
public class EslUpdatesTest {

	private static LoginServicePortType aa1 = null;

	private static Log _log = LogFactory.getLog(EslUpdatesTest.class);

	private static EslClient client;

	private static String studyNumber = "ED2/002001-92";

	/**
	 * @param args the username and password required for authentication with the ws
	 */
	public static void main(String[] args) throws Exception {

		SAMLAssertion saml = login2(args);

		client = new EslClient();

	/*	System.out.println("Esl version is: "+ client.getVersion());

		IProject project = client.retrieveProjectByCode("ED2", saml.toString());
	
		System.out.println("Project retrieved. Id is:" + project.getId() );

		System.out.println("Updating a subject");
		updateSubject(project, saml.toString());

		System.out.println("Updating a subject");
		updateSubject_dob(project, saml.toString());

		System.out.println("Updating a subject's address");
		updateAddress(project, saml.toString());*/
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
		//SAMLAssertion sa = qc.getSAMLAssertion(new ProjectType("EDIE 2", "ED2", "EDIE-2",
		//		"2171", false));
		SAMLAssertion sa = qc.getSAMLAssertion();
		return sa;
	}
	
	private static SAMLAssertion login2(String[] args) throws Exception {

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
		System.out.println("User is: "+opts.getUser());
		System.out.println("Password is: "+opts.getPassword());
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
	System.out.println(qc.getMyProjects().size());
		System.out.println("getAssertion");
		
		SAMLAssertion sa = qc.getSAMLAssertion();

		return sa;
	}
	
	private static void updateSubject(IProject project, String saml) throws Exception {
		Long subjectId = client.retrieveSubjectByStudyNumber(project, studyNumber, saml.toString()).getId();
		ISubject s = client.retrieveSubject(subjectId, saml.toString());
		System.out.println("The subject "+s.getStudyNumber()+" has been retrieved");
		s.setFirstName(s.getFirstName()+"-updated");
		s.setHospitalNumber("1234");
		Long sId = client.saveSubject(s, saml.toString());
		System.out.println("The subject "+s.getStudyNumber()+" has been saved with id: "+sId);
	}
	
	private static void updateSubject_dob(IProject project, String saml) throws Exception {
		Long subjectId = client.retrieveSubjectByStudyNumber(project, studyNumber, saml.toString()).getId();
		ISubject s = client.retrieveSubject(subjectId, saml.toString());
		System.out.println("The subject "+s.getStudyNumber()+" has been retrieved");
		Date dob = new Date();
		dob.setYear(1972);
		s.setDateOfBirth(dob);
		Long sId = client.saveSubject(s, saml.toString());
		System.out.println("The subject "+s.getStudyNumber()+" has been saved with id: "+sId);
	}

	private static void updateAddress(IProject project, String saml) throws Exception {
		Long subjectId = client.retrieveSubjectByStudyNumber(project, studyNumber, saml.toString()).getId();
		ISubject s = client.retrieveSubject(subjectId, saml.toString());
		System.out.println("The subject "+s.getStudyNumber()+" has been retrieved");
		s.getAddress().setAddress1("New address line");
		Long sId = client.saveSubject(s, saml.toString());
		System.out.println("The subject "+s.getStudyNumber()+" has been saved with id: "+sId);
	}
}
