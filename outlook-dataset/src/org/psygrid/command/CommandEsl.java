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

package org.psygrid.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.psygrid.common.email.Email;
import org.psygrid.esl.model.IFactory;
import org.psygrid.esl.model.IProject;
import org.psygrid.esl.model.IRandomisation;
import org.psygrid.esl.model.IRole;
import org.psygrid.esl.model.IStrata;
import org.psygrid.esl.model.hibernate.HibernateFactory;
import org.psygrid.esl.randomise.EmailType;
import org.psygrid.esl.services.client.EslClient;
import org.psygrid.www.xml.security.core.types.GroupType;

public class CommandEsl {

    private static EslClient client = new EslClient();
    private static IFactory factory = new HibernateFactory();

    /**
     * @param args
     */
    public static void main(String[] args) {
        try{
            createEdieEsl(null);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public CommandEsl(){

    }

    public void insert(String saml) throws Exception {
        createEdieEsl(saml);
    }

    private static void createEdieEsl(String saml) throws Exception {

    	IProject project = createProject();
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

	public static IProject createProject() {
		IProject project = factory.createProject("COM");
		project.setProjectCode("COM");
		project.setProjectName("Command");
		return project;
	}

	public static IProject createGroups(IProject project) {

		for ( GroupType g: COMGroups.allGroups() ){
			project.setGroup(factory.createGroup(g.getName(), g.getIdCode()));
		}
		return project;
	}

	public static IProject setupRandomisation(IProject project) throws Exception {

		IRandomisation r = null;

		r = project.getRandomisation();

		if ( r == null) {
			r = factory.createRandomisation("COM");
		}

        IStrata strata2 = factory.createStrata("centreNumber");
        for ( GroupType g: COMGroups.allGroups() ){
        	strata2.setValue(g.getIdCode());
        }
        r.getStrata().add(strata2);
        r.getTreatments().put("COM-000", "Control");
        r.getTreatments().put("COM-001", "CTCH");

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

		//TODO update email text
		Map<String,Email> emails = new HashMap<String,Email>();
		Email e1 = factory.createEmail();
		e1.setSubject("PSYGRID: Notification of Invocation");
		e1.setBody("Notification of Invocation");
		Email e2 = factory.createEmail();
		e2.setSubject("PSYGRID: Notification of Decision");
		e2.setBody("A treatment arm has been allocated to the subject '%subjectCode%'.");
		Email e3 = factory.createEmail();
		e3.setSubject("PSYGRID: Notification of Treatment");
		e3.setBody("The subject '%subjectCode%' has been allocated the treatment %treatment% (code: %treatmentCode%).\n\n" +
				"The subject has the following risk issues:\n\n" +
				"%riskIssues%");
		Email e4 = factory.createEmail();
		e4.setSubject("SMS Notification of Treatment");		//Reference only: not used in SMS
		//Newlines in message body need to be a \n followed by a space.
		e4.setBody("[PsyGrid] %subjectCode% has been allocated %treatment% (code: %treatmentCode%).\n " +
				"Risk issues: " +
				"%riskIssues%");

		emails.put(EmailType.INVOCATION.type(), e1);
		emails.put(EmailType.DECISION.type(), e2);
		emails.put(EmailType.TREATMENT.type(), e3);
		emails.put(EmailType.SMS_TREATMENT.type(), e4);

		r.setRolesToNotify(roles);
		r.setEmails(emails);
		project.setRandomisation(r);


		return project;

	}
}
