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

package org.psygrid.edie.test.patches.v1_1_12;

import java.util.Map;

import org.psygrid.common.email.Email;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.esl.model.IFactory;
import org.psygrid.esl.model.IProject;
import org.psygrid.esl.model.hibernate.HibernateFactory;
import org.psygrid.esl.randomise.EmailType;
import org.psygrid.esl.services.client.EslClient;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch5 extends AbstractPatch {

	public String getName() {
		return "Add the SMS message template for notification of randomisation " +
				"results to the ESL";
	}

	public void applyPatch(DataSet ds, String saml) throws Exception {

		EslClient client = new EslClient();

		IProject project = client.retrieveProjectByCode(ds.getProjectCode(), saml);
		IFactory factory = new HibernateFactory();

		/*
		 * SMS Message sent out to notify of treatment allocation, stored
		 * as an email
		 */
		Map<String,Email> emails = project.getRandomisation().getEmails();

		Email e4 = factory.createEmail();
		e4.setSubject("SMS Notification of Treatment");		//Reference only: not used in SMS
		//Newlines in message body need to be a \n followed by a space.
		e4.setBody("[PsyGrid] %subjectCode% has been allocated %treatment% (code: %treatmentCode%).\n " +
				"Risk issues: " +
				"%riskIssues%");

		emails.put(EmailType.SMS_TREATMENT.type(), e4);
		project.getRandomisation().setEmails(emails);

		client.saveProject(project, saml);

		System.out.println("Added SMS message template");
	}
}
