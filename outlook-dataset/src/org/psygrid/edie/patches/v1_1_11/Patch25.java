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

package org.psygrid.edie.patches.v1_1_11;

import java.util.HashMap;
import java.util.Map;

import org.psygrid.common.email.Email;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.esl.model.IFactory;
import org.psygrid.esl.model.IProject;
import org.psygrid.esl.model.hibernate.HibernateFactory;
import org.psygrid.esl.randomise.EmailType;
import org.psygrid.esl.services.client.EslClient;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch25 extends AbstractPatch {

	@Override
	public String getName() {
		return "Patch to update the randomisation notification emails. This patch is dependent on ESL SQL Patch2, which must be applied first.";
	}

	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {

		IFactory factory = new HibernateFactory();
		System.out.println("Recreating emails");

		EslClient esl = new EslClient();
		IProject project = esl.retrieveProjectByCode(ds.getProjectCode(), saml);

		Map<String,Email> emails = new HashMap<String,Email>();
		Email e1 = factory.createEmail();
		e1.setSubject("Notification of Invocation");
		e1.setBody("Notification of Invocation");
		Email e2 = factory.createEmail();
		e2.setSubject("Notification of Decision");
		e2.setBody("A treatment arm has been allocated to the subject '%subjectCode%'.");
		Email e3 = factory.createEmail();
		e3.setSubject("Notification of Treatment");
		e3.setBody("The subject '%subjectCode%' has been allocated to %treatment% (code: %treatmentCode%).\n\n" +
				"The subject has the following risk issues:\n\n" +
				"%riskIssues%");

		emails.put(EmailType.INVOCATION.type(), e1);
		emails.put(EmailType.DECISION.type(), e2);
		emails.put(EmailType.TREATMENT.type(), e3);

		project.getRandomisation().setEmails(emails);

		esl.saveProject(project, saml);
	}

}
