package org.psygrid.champ.patches;

import java.util.Map;

import org.psygrid.common.email.Email;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.esl.model.IProject;
import org.psygrid.esl.randomise.EmailType;
import org.psygrid.esl.services.client.EslClient;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch3 extends AbstractPatch {

	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {
		EslClient esl = new EslClient();
		org.psygrid.esl.model.hibernate.HibernateFactory eslFactory = new org.psygrid.esl.model.hibernate.HibernateFactory();
		IProject project = esl.retrieveProjectByCode(ds.getProjectCode(), saml);
		
		
		Map<String, Email> emails = project.getRandomisation().getEmails();
		
		Email newEmail = eslFactory.createEmail();
		newEmail.setSubject("Notification of Treatment");
		newEmail.setBody("The subject '%subjectCode%' has been allocated to %treatment% (code: %treatmentCode%).\n\n" +
				"The subject's site name is: %siteName%.\n\n" +
				"The subject has the following risk issues:\n\n" +
				"%riskIssues%");
		
		/*
		
		DELETED BECAUSE a) not necessary (duplicate of above) and b) does not run on mysql -
		the following error is generated: 'Driver can not re-execute prepared statement when a parameter has been changed from a streaming type to an intrinsic data type without calling clearParameters() first.' 
		
		IEmail newEmail2 = eslFactory.createEmail();
		newEmail.setSubject("Notification of Treatment");
		newEmail.setBody("The subject '%subjectCode%' has been allocated to %treatment% (code: %treatmentCode%).\n\n" +
				"The subject's site name is: %siteName%.\n\n" +
				"The subject has the following risk issues:\n\n" +
				"%riskIssues%");
		*/
		
		emails.put(EmailType.CUSTOM_TREATMENT.type(), newEmail);
		emails.put(EmailType.CUSTOM_SMS_TREATMENT.type(), newEmail);

		
		esl.saveProject(project, saml);
	}

	@Override
	public String getName() {
		return "This patch adds a custom treatment email (and sms) template, which carries site info.";
	}

}
