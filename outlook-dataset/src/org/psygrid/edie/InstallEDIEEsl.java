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

package org.psygrid.edie;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.axis.encoding.Base64;
import org.apache.axis.utils.Options;
import org.opensaml.SAMLAssertion;
import org.psygrid.common.email.Email;
import org.psygrid.esl.model.IFactory;
import org.psygrid.esl.model.IProject;
import org.psygrid.esl.model.IRandomisation;
import org.psygrid.esl.model.IRole;
import org.psygrid.esl.model.IStrata;
import org.psygrid.esl.model.hibernate.HibernateFactory;
import org.psygrid.esl.randomise.EmailType;
import org.psygrid.esl.services.client.EslClient;
import org.psygrid.security.attributeauthority.client.AAQueryClient;
import org.psygrid.security.authentication.client.LoginClient;
import org.psygrid.security.authentication.service.LoginServicePortType;
import org.psygrid.security.components.net.PsyGridClientSocketFactory;
import org.psygrid.security.utils.PropertyUtilities;
import org.psygrid.www.xml.security.core.types.ProjectType;



/**
 * @author Lucy Bridges
 *
 */
public class InstallEDIEEsl {


	private static EslClient client = new EslClient();
	private static IFactory factory = new HibernateFactory();
	private static LoginServicePortType aa1 = null;


	public static void main(String[] args){

		SAMLAssertion sa = null;
		IProject project = null;
        try{
        	//sa = login(args);
        	String saml = null;//sa.toString();

        	project = createProject();
        	client.saveProject(project, saml);
        	System.out.println(project.getProjectCode()+" project has been setup");
        	project = client.retrieveProjectByCode(project.getProjectCode(), saml);
        	project = createGroups(project);
        	client.saveProject(project, saml);
        	System.out.println("Groups have been setup");
        	project = client.retrieveProjectByCode(project.getProjectCode(), saml);
        	project = setupRandomisation(project);
        	client.saveProject(project, saml);
        	System.out.println("Randomisation has been setup");
        }
        catch(Exception ex){
            ex.printStackTrace();
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
		SAMLAssertion sa = qc.getSAMLAssertion(new ProjectType("EDIE 2", "ED2", "EDIE-2", "2171", false));

		return sa;
	}

	private static IProject createProject() {
		IProject project = factory.createProject("ED2");
		project.setProjectCode("ED2");
		project.setProjectName("EDIE 2");
		return project;
	}

	private static IProject createGroups(IProject project) {

		project.setGroup(factory.createGroup("Manchester",  "001001"));
		project.setGroup(factory.createGroup("Birmingham",  "002001"));
		project.setGroup(factory.createGroup("Cambridge",   "003001"));
		project.setGroup(factory.createGroup("East Anglia", "004001"));
		project.setGroup(factory.createGroup("Glasgow",     "005001"));

		return project;
	}

	private static IProject setupRandomisation(IProject project) throws Exception {

		IRandomisation r = null;

		r = project.getRandomisation();

		if ( r == null) {
			r = factory.createRandomisation("ED2");
		}

        IStrata strata1 = factory.createStrata("sex");
        strata1.setValue("Male");
        strata1.setValue("Female");
        r.getStrata().add(strata1);
        IStrata strata2 = factory.createStrata("centreNumber");
        strata2.setValue("001001");
        strata2.setValue("002001");
        strata2.setValue("003001");
        strata2.setValue("004001");
        strata2.setValue("005001");
        r.getStrata().add(strata2);
        r.getTreatments().put("ED2-000", "EDIE 2 Control");
        r.getTreatments().put("ED2-001", "EDIE 2 CognitiveBehaviourTherapy");

		List<IRole> roles = new ArrayList<IRole>();

		IRole r1 = factory.createRole("SystemAdministrator");
		r1.setNotifyOfRSDecision(false);
		r1.setNotifyOfRSInvocation(false);
		r1.setNotifyOfRSTreatment(false);
		roles.add(r1);

		IRole r2 = factory.createRole("ChiefInvestigator");
		r2.setNotifyOfRSDecision(false);
		r2.setNotifyOfRSInvocation(false);
		r2.setNotifyOfRSTreatment(false);
		roles.add(r2);

		IRole r3 = factory.createRole("ProjectManager");
		r3.setNotifyOfRSDecision(false);
		r3.setNotifyOfRSInvocation(false);
		r3.setNotifyOfRSTreatment(false);
		roles.add(r3);

		IRole r4 = factory.createRole("PrincipalInvestigator");
		r4.setNotifyOfRSDecision(false);
		r4.setNotifyOfRSInvocation(false);
		r4.setNotifyOfRSTreatment(false);
		roles.add(r4);

		IRole r5 = factory.createRole("ProjectAdministrator");
		r5.setNotifyOfRSDecision(false);
		r5.setNotifyOfRSInvocation(false);
		r5.setNotifyOfRSTreatment(false);
		roles.add(r5);

		IRole r6 = factory.createRole("ClinicalResearchManager");
		r6.setNotifyOfRSDecision(true);
		r6.setNotifyOfRSInvocation(true);
		r6.setNotifyOfRSTreatment(true);
		roles.add(r6);

		IRole r7 = factory.createRole("ClinicalResearchOfficer");
		r7.setNotifyOfRSDecision(true);
		r7.setNotifyOfRSInvocation(true);
		r7.setNotifyOfRSTreatment(false);
		roles.add(r7);

		IRole r8 = factory.createRole("ScientificResearchOfficer");
		r8.setNotifyOfRSDecision(false);
		r8.setNotifyOfRSInvocation(false);
		r8.setNotifyOfRSTreatment(false);
		roles.add(r8);

		IRole r9 = factory.createRole("TreatmentAdministrator");
		r9.setNotifyOfRSDecision(true);
		r9.setNotifyOfRSInvocation(false);
		r9.setNotifyOfRSTreatment(true);
		roles.add(r9);


		Map<String,Email> emails = new HashMap<String,Email>();
		Email e1 = factory.createEmail();
		e1.setSubject("Notification of Invocation");
		e1.setBody("Notification of Invocation");
		Email e2 = factory.createEmail();
		e2.setSubject("Notification of Decision");
		e2.setBody("A treatment arm has been allocated to the subject '%subjectCode%'.");
		Email e3 = factory.createEmail();
		e3.setSubject("Notification of Treatment");
		e3.setBody("The subject '%subjectCode%' has been allocated the treatment %treatment% (code: %treatmentCode%).\n\n" +
				"The subject has the following risk issues:\n\n" +
				"%riskIssues%");

		emails.put(EmailType.INVOCATION.type(), e1);
		emails.put(EmailType.DECISION.type(), e2);
		emails.put(EmailType.TREATMENT.type(), e3);


		r.setRolesToNotify(roles);
		r.setEmails(emails);
		project.setRandomisation(r);


		return project;

	}
}
