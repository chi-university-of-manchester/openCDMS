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

package org.psygrid.esl.randomise;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.common.email.CommonEmailBodyConverter;
import org.psygrid.common.email.Email;
import org.psygrid.common.email.QueuedEmail;
import org.psygrid.common.sms.SMSException;
import org.psygrid.esl.dao.DAOException;
import org.psygrid.esl.dao.EslDAO;
import org.psygrid.esl.model.IProject;
import org.psygrid.esl.model.IRandomisation;
import org.psygrid.esl.model.IRole;
import org.psygrid.esl.model.IStrata;
import org.psygrid.esl.model.ISubject;
import org.psygrid.esl.model.StrataAllocationFault;
import org.psygrid.esl.model.hibernate.CustomEmailInfo;
import org.psygrid.esl.scheduling.hibernate.QueuedSMS;
import org.psygrid.esl.services.NotAuthorisedFault;
import org.psygrid.esl.util.EmailUtil;
import org.psygrid.randomization.DuplicateRandomizerFault;
import org.psygrid.randomization.DuplicateSubjectFault;
import org.psygrid.randomization.Parameter;
import org.psygrid.randomization.RandomizationFault;
import org.psygrid.randomization.UnknownRandomizerFault;
import org.psygrid.randomization.client.RandomizationClient;
import org.psygrid.randomization.model.RandomizerException;
import org.psygrid.randomization.model.hibernate.StratifiedRandomizer;
import org.psygrid.randomization.model.hibernate.Stratum;
import org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage;


/**
 * Class providing a connection to the org.psygrid.randomization service.
 * 
 * Allows the Randomizer to be setup and treatment arms to be allocated.
 * Also performs transformation between the ESL randomisation classes and 
 * what is expected by the Randomizer web services.
 * 
 * @author Lucy Bridges
 *
 */
public class RemoteRandomiser implements IRemoteRandomiser {

	private String url;

	private RandomizationClient client;

	private EmailUtil emailUtil;
	

	/**
	 * General purpose logger
	 */
	private static Log sLog = LogFactory.getLog(RemoteRandomiser.class);


	private EslDAO dao;

	private String fromAddress;
	private String toAddress;

	public RemoteRandomiser () throws RandomisationException {
		try {
			if (url != null) {
				client = new RandomizationClient(new URL(url));
			}
			else {
				client = new RandomizationClient();
			}
			
		}
		catch (Exception e) {
			sLog.error("Problem instantiating RandomizationClient: "+e.getClass().getSimpleName(),e);
			throw new RandomisationException("Problem connecting to the randomization service");
		}
	}



	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}


	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}


	public EslDAO getDao() {
		return dao;
	}

	public void setDao(EslDAO dao) {
		this.dao = dao;
	}

	public String getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	public String getToAddress() {
		return toAddress;
	}

	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}

	public EmailUtil getEmailUtil() {
		return emailUtil;
	}



	public void setEmailUtil(EmailUtil emailUtil) {
		this.emailUtil = emailUtil;
	}



	
	public void saveRandomisation(IRandomisation rand, String saml) throws RandomisationException, NotAuthorisedFault,
	ConnectException, SocketTimeoutException {

		String name = rand.getName();
		List<IStrata> strata = rand.getStrata();

		StratifiedRandomizer rdmzr = new StratifiedRandomizer();
		rdmzr.setName(name);

		try {
			//Retrieve the strata
			Iterator i = strata.iterator();
			while (i.hasNext()) {
				Stratum stratum = new Stratum();
				IStrata is = ((IStrata)i.next());
				stratum.setName(is.getName());
				for (String s: is.getValues()) {
					stratum.getValues().add(s);
				}
				rdmzr.addStratum(stratum);
			}
			//strata can not change once combinations are generated 
			rdmzr.generateCombinations("org.psygrid.randomization.model.hibernate.IBRpbrblRandomizer");

			//Retrieve the treatments
			for (String treatment: rand.getTreatments().keySet()) {
				//name then treatment code(key)
				rdmzr.addTreatment(rand.getTreatments().get(treatment), treatment);
			}

			long seed = 1;
			long[] seeds = new long[rdmzr.numCombinations()];
			seeds[0] = seed;
			for ( int j=1; j < seeds.length; j++ ){
				seeds[j] = seeds[0] + j;
			}

			rdmzr.createRngs(seeds);

		}
		catch (RandomizerException rex) {
			throw new RandomisationException("Problem with remote Randomizer (1)", rex);
		}

		try {
			client.saveRandomizer(rdmzr, saml);
		}
		catch (DuplicateRandomizerFault drf) {
			throw new RandomisationException("Duplicate Randomizer ", drf);
		}
		catch (RandomizationFault drf) {
			throw new RandomisationException("Randomization Fault: ", drf);
		}
		catch (org.psygrid.randomization.NotAuthorisedFault naf) {
			throw new NotAuthorisedFault("Not authorized to connect to randomization service.", naf);
		}

	}


	
	
	public String allocateTreatment(IRandomisation rand, ISubject subject, CustomEmailInfo customInfo, String saml) 
	throws StrataAllocationFault, RandomisationException, NotAuthorisedFault, ConnectException, SocketTimeoutException  {

		//allocate params to be used to randomise the trial subject
		//from the specified strata
		Map<String, String> strata = null;
		try {
			strata = subject.getStrataValues(rand.getStrata());
		}
		catch (StrataAllocationFault sae) {
			throw new StrataAllocationFault("Unable to set the values for the Strata to allocate treatment to this subject.", sae);
		}

		//Check that each strata has been given a value, before sending to the remote randomizer
		for(String s: strata.keySet()) {
			if (strata.get(s) == null) {
				throw new StrataAllocationFault("No value initialised for Strata: "+s);
			}
		}

		//Convert the Hash into a Parameter array (which can be used for soap xfer)
		Parameter[] params = null;

		if (strata != null && strata.keySet() != null && strata.keySet().size() > 0) {
			int max = strata.keySet().size();
			params = new Parameter[max];

			String[] names = new String[max];
			strata.keySet().toArray(names);

			String[] values = new String[strata.values().size()];
			strata.values().toArray(values);

			for (int i=0; i < max; i++) {
				params[i] = new Parameter();
				params[i].setKey(names[i]);
				params[i].setValue(values[i]);
			}
		}

		String treatment = null;

		try {
			//allocate a treatment or return the treatment previously allocated
			try {	
				treatment = client.allocate(rand.getName(), subject.getStudyNumber(), params, saml);
				String risks = subject.getRiskIssues();

				if (risks == null) {
					risks = "No risks";
				}

				String treatmentName = null;
				try {
					treatmentName = rand.getTreatments().get(treatment);
				}
				catch (Exception e) {
					sLog.info("Treatment name for the treatment '"+treatment+"' was unable to be retrieved. Reason is: "+e.getMessage());
				}
				//get the values to be substituted in in the body of the email
				Map<String,String> emailParams = new HashMap<String,String>();
				emailParams.put("%subjectCode%", subject.getStudyNumber());
				emailParams.put("%riskIssues%", risks);
				emailParams.put("%treatment%", treatmentName);
				emailParams.put("%treatmentCode%", treatment);
				if(customInfo != null && customInfo.getSite() != null){
					emailParams.put("%siteName%", customInfo.getSite().getSiteName());
				}

				//notify the researchers, CRM and therapist of treatment allocation
				List<String> recipients = createEmail(rand, EmailType.DECISION, emailParams, subject);

				//inform therapist and CPM of the treatment arm allocated to a subject
				recipients = createEmail(rand, EmailType.TREATMENT, emailParams, subject);
				//Send an SMS to notify of the treatment allocation
				try {
					createSMS(recipients, EmailType.SMS_TREATMENT, emailParams, subject, rand);
				}
				catch (SMSException e) {
					sLog.error("Problem creating an SMS message.", e);
				}
			}
			catch (DuplicateSubjectFault dsf) {
				treatment = client.getAllocation(rand.getName(), subject.getStudyNumber(), saml);
			}
		}
		catch (UnknownRandomizerFault urf) {
			throw new RandomisationException("Unknown Randomizer - has it been setup?", urf);
		}
		catch (RandomizationFault rf) {
			throw new RandomisationException("Problem with remote Randomizer", rf);
		}
		catch (org.psygrid.randomization.NotAuthorisedFault naf) {
			throw new NotAuthorisedFault("Not authorized to connect to randomization service.", naf);
		}
		//this shouldn't happen
		if (treatment == null) {
			throw new RandomisationException("No treatment allocated, but randomiser didn't throw an error.");
		}

		return treatment;
	}
	
	


	
	public String getAllocation(String rdmzrName, ISubject subject, String saml) 
	throws RandomisationException, NotAuthorisedFault, ConnectException, SocketTimeoutException  {

		String studyCode = subject.getStudyNumber();
		String treatment = null;

		try {
			treatment = client.getAllocation(rdmzrName, studyCode, saml);
		}
		catch (UnknownRandomizerFault urf) {
			throw new RandomisationException("Unknown Randomizer - has it been setup?", urf);
		}
		catch (RandomizationFault drf) {
			throw new RandomisationException("Randomization Fault: ", drf);
		}
		catch (org.psygrid.randomization.NotAuthorisedFault naf) {
			throw new NotAuthorisedFault("Not authorized to connect to randomization service.", naf);
		}

		if (treatment == null) {
			throw new RandomisationException("No treatment allocation returned by remote Randomizer");
		}

		IRandomisation r = null;

		try {
			r = subject.getGroup().getProject().getRandomisation();
		}
		catch (Exception e) {
			throw new RandomisationException("Problem retrieving randomisation details for subject "+subject.getStudyNumber(), e); 
		}

		//retrieve the name of the treatment
		String tName = r.getTreatments().get(treatment);

		if (tName == null) {
			throw new RandomisationException("No treatment with code "+treatment+" has been found in the ESL");
		}

		return tName;
	}

	
	public String[][] getAllAllocations(IProject project, String saml) 
	throws RandomisationException, NotAuthorisedFault, ConnectException, SocketTimeoutException  {

		String rdmzrName = null;

		try {
			rdmzrName = project.getRandomisation().getName();
		}
		catch (NullPointerException npe) {
			throw new RandomisationException("No randomiser setup for project: "+project.getProjectCode());
		}
		//stores studyCode->treatment for all subjects in the project
		String[][] treatment = null;

		try {
			treatment = client.getAllAllocations(rdmzrName, saml);
		}
		catch (UnknownRandomizerFault urf) {
			throw new RandomisationException("Unknown Randomizer - has it been setup?", urf);
		}
		catch (RandomizationFault drf) {
			throw new RandomisationException("Randomization Fault: ", drf);
		}
		catch (org.psygrid.randomization.NotAuthorisedFault naf) {
			throw new NotAuthorisedFault("Not authorized to connect to randomization service.", naf);
		}

		if (treatment == null) {
			throw new RandomisationException("Treatment allocations not returned by remote Randomizer");
		}

		Map<String,String> t = project.getRandomisation().getTreatments();		

		for (String[] subject: treatment) {
			//convert provided treatment code into a treatment name
			String name = t.get(subject[1]);
			if (name == null) {
				throw new RandomisationException("No treatment with code "+subject[1]+" has been found in the ESL");
			}	
			subject[1] = name; 
		}

		return treatment;
	}

	
	public Calendar[] getSubjectRandomisationEvents(String rdmzrName, String subjectCode, String saml) 
	throws RandomisationException, NotAuthorisedFault, ConnectException, SocketTimeoutException {

		try {
			List<Calendar> list = client.getSubjectRandomizationEvents(rdmzrName, subjectCode, saml);
			if (list == null) {
				return null;
			}
			Calendar[] cal = new Calendar[list.size()];
			for (int i = 0; i<list.size(); i++) {
				cal[i] = list.get(i);
			}
			return cal;
		}
		catch (org.psygrid.randomization.NotAuthorisedFault urf) {
			throw new RandomisationException("Not authorised to connect to randomisation service", urf);
		}
		catch (UnknownRandomizerFault urf) {
			throw new RandomisationException("Unknown Randomizer - has it been setup?", urf);
		}
		catch (RandomizationFault drf) {
			throw new RandomisationException("Randomization Fault: ", drf);
		}
	} 
	
	/**
	 * This method returns the appropriate email type. The logic is to return the default email type, unless
	 * the randomisation object has been set up with a custom email template, in which case the custom one will be 
	 * returned.
	 * @param rand
	 * @param requestedType
	 * @return
	 */
	private Email getEmail(IRandomisation rand, EmailType requestedType){
		Email email = null;
		
		switch(requestedType){
		case INVOCATION:
			email = rand.getEmail(EmailType.CUSTOM_INVOCATION.type()) != null ? rand.getEmail(EmailType.CUSTOM_INVOCATION.type()) : rand.getEmail(EmailType.INVOCATION.type());
			break;
		case DECISION:
			email = rand.getEmail(EmailType.CUSTOM_DECISION.type()) != null ? rand.getEmail(EmailType.CUSTOM_DECISION.type()) : rand.getEmail(EmailType.DECISION.type());
			break;
		case TREATMENT:
			email = rand.getEmail(EmailType.CUSTOM_TREATMENT.type()) != null ? rand.getEmail(EmailType.CUSTOM_TREATMENT.type()) : rand.getEmail(EmailType.TREATMENT.type());
			break;
		case SMS_INVOCATION:
			email = rand.getEmail(EmailType.CUSTOM_SMS_INVOCATION.type()) != null ? rand.getEmail(EmailType.CUSTOM_SMS_INVOCATION.type()) : rand.getEmail(EmailType.SMS_INVOCATION.type());
			break;
		case SMS_DECISION:
			email = rand.getEmail(EmailType.CUSTOM_SMS_DECISION.type()) != null ? rand.getEmail(EmailType.CUSTOM_SMS_DECISION.type()) : rand.getEmail(EmailType.SMS_DECISION.type());
			break;
		case SMS_TREATMENT:
			email = rand.getEmail(EmailType.CUSTOM_SMS_TREATMENT.type()) != null ? rand.getEmail(EmailType.CUSTOM_SMS_TREATMENT.type()) : rand.getEmail(EmailType.SMS_TREATMENT.type());
			break;
		default:
			email = rand.getEmail(requestedType.type());
			break;
		}
		
		return email;
	}

	/**
	 * Creates an email containing the randomisation results to notify
	 * particular users.
	 * 
	 * Method requires the type of email to be sent, the randomisation 
	 * in question and the parameters with which to substitute in the 
	 * email are provided.
	 * 
	 * This email is then persisted and 'queued' to be sent later, using
	 * a Quartz scheduler bean. Queuing and sending the email separately 
	 * means that any errors in sending the email do not affect the 
	 * randomisation process.
	 * 
	 * @param rand
	 * @param type
	 * @param sendEmail
	 * @param params to substitute for variables in the body of the email
	 * @return recipients 
	 * @throws RandomisationException
	 */
	private List<String> createEmail(IRandomisation rand, EmailType type, Map<String,String> params, ISubject subject) throws RandomisationException {

		QueuedEmail newEmail = new QueuedEmail();
		Email email = getEmail(rand, type);
		
		List<IRole> roles = rand.getRolesToNotify();
		List<String> recipients = new ArrayList<String>();

		//assign the bcc addresseses by getting the people performing the role(s) to be emailed.
		for (IRole role: roles) {
			if ((type.equals(EmailType.INVOCATION) || type.equals(EmailType.CUSTOM_INVOCATION)) && role.isNotifyOfRSInvocation()) {
				recipients.add(role.getName());
			}
			else if ((type.equals(EmailType.DECISION) || type.equals(EmailType.CUSTOM_DECISION)) && role.isNotifyOfRSDecision()) {
				recipients.add(role.getName());
			}
			else if((type.equals(EmailType.TREATMENT) || type.equals(EmailType.CUSTOM_TREATMENT)) && role.isNotifyOfRSTreatment()) {
				recipients.add(role.getName());
			}
		}

		if (email == null) {
			sLog.info("No email of type "+type.type()+" was found");
			return recipients; //nothing to do
		}

		newEmail.setSubject(email.getSubject());
		
		String emailBody = email.getBody();
		String modifiedEmailBody = CommonEmailBodyConverter.substituteParamsIntoEmailBody(emailBody, params);
		newEmail.setBody(modifiedEmailBody);


		try {
			List<String> addresses = emailUtil.getEmailRecipients(subject, recipients);	
			newEmail.setBccAddresses(addresses);
		}
		catch(NotAuthorisedFaultMessage ex){
			throw new RandomisationException("Not authorised to connect to attribute authority query client.", ex);
		}
		catch (ConnectException ex){
			throw new RandomisationException("Unable to connect to attribute authority query client.", ex);
		}
		newEmail.setFromAddress(fromAddress);
		newEmail.setToAddress(toAddress);

		//save email
		sLog.info("Email queued for "+newEmail.getToAddress());
		dao.saveEmail(newEmail);	//add email to queue to be sent

		return recipients;
	}

	
	public String[][] getRandomizerStatistics(IProject project, String saml) throws RandomisationException, NotAuthorisedFault,
	ConnectException, SocketTimeoutException {
		String rdmzrName = null;

		try {
			rdmzrName = project.getRandomisation().getName();
		}
		catch (NullPointerException npe) {
			throw new RandomisationException("No randomiser setup for project: "+project.getProjectCode());
		}
		//stores treatment arm->number of allocations for all subjects in the project
		String[][] treatment = null;

		try {
			treatment = client.getRandomizerStatistics(rdmzrName, saml);
		}
		catch (UnknownRandomizerFault urf) {
			throw new RandomisationException("Unknown Randomizer - has it been setup?", urf);
		}
		catch (RandomizationFault drf) {
			throw new RandomisationException("Randomization Fault: ", drf);
		}
		catch (org.psygrid.randomization.NotAuthorisedFault naf) {
			throw new NotAuthorisedFault("Not authorized to connect to randomization service.", naf);
		}

		if (treatment == null) {
			throw new RandomisationException("Statistics not returned by remote Randomizer");
		}

		Map<String,String> t = project.getRandomisation().getTreatments();      

		for (String[] subject: treatment) {
			//convert provided treatment code into a treatment name
			String name = t.get(subject[0]);
			if (name == null) {
				throw new RandomisationException("No treatment with code "+subject[0]+" has been found in the ESL");
			}   
			subject[0] = name; 
		}

		return treatment;
	}

	
	public String[][] getRandomizerStatistics(IProject project, Parameter[] parameters, String saml) throws RandomisationException, 
	NotAuthorisedFault, ConnectException, SocketTimeoutException  {
		String rdmzrName = null;

		try {
			rdmzrName = project.getRandomisation().getName();
		}
		catch (NullPointerException npe) {
			throw new RandomisationException("No randomiser setup for project: "+project.getProjectCode());
		}
		//stores treatment arm->number of allocations for all subjects in the project
		String[][] stats = null;

		try {
			stats = client.getRandomizerStatistics(rdmzrName, parameters, saml);
		}
		catch (UnknownRandomizerFault urf) {
			throw new RandomisationException("Unknown Randomizer - has it been setup?", urf);
		}
		catch (RandomizationFault drf) {
			throw new RandomisationException("Randomization Fault: ", drf);
		}
		catch (org.psygrid.randomization.NotAuthorisedFault naf) {
			throw new NotAuthorisedFault("Not authorized to connect to randomization service.", naf);
		}

		if (stats == null) {
			throw new RandomisationException("Statistics not returned by remote Randomizer");
		}

		Map<String,String> t = project.getRandomisation().getTreatments();      

		for (String[] subject: stats) {
			//convert provided treatment code into a treatment name
			String name = t.get(subject[0]);
			if (name == null) {
				throw new RandomisationException("No treatment with code "+subject[0]+" has been found in the ESL");
			}   
			subject[0] = name; 
		}

		return stats;
	}

	
	public String[] getRandomisationResultForDate(String rdmzrName, String subjectCode, Calendar date, String saml) 
	throws RandomisationException, NotAuthorisedFault, ConnectException, SocketTimeoutException  {
		try {
			String[] result = client.getRandomizationResultForDate(rdmzrName, subjectCode, date, saml);
			return result;
		}
		catch (org.psygrid.randomization.NotAuthorisedFault urf) {
			throw new RandomisationException("Not authorised to connect to randomisation service", urf);
		}
		catch (UnknownRandomizerFault urf) {
			throw new RandomisationException("Unknown Randomizer - has it been setup?", urf);
		}
		catch (RandomizationFault drf) {
			throw new RandomisationException("Randomization Fault: ", drf);
		}
	}

	private void createSMS(List<String> recipients, EmailType type, Map<String,String> params, ISubject subject, IRandomisation rand) throws SMSException {

		try {
			/*
			 * Create SMS in addition to emails for treatment notification
			 */
			if (recipients.size() > 0) {
				Map<String,String> users = emailUtil.getPhoneNumbers(subject, recipients);
				if (rand.getEmails().get(type.type()) != null) {
					
					Email email = getEmail(rand, type);
					String emailBody = email.getBody();
					String modifiedEmailBody = CommonEmailBodyConverter.substituteParamsIntoEmailBody(emailBody, params);
					createSMS(users, modifiedEmailBody);
				}
			}
		}
		catch(NotAuthorisedFaultMessage ex){
			ex.printStackTrace();
			throw new SMSException("Not authorised to connect to attribute authority query client.", ex);
		}
		catch (ConnectException ex){
			ex.printStackTrace();
			throw new SMSException("Unable to connect to attribute authority query client.", ex);
		}
	}

	private void createSMS(Map<String,String> users, String message) throws SMSException {
		
		for (String user: users.keySet()) {
			if (user != null && users.get(user) != null) {

				sLog.info("SMS queued for "+user);
				QueuedSMS sms = new QueuedSMS();
				sms.setRecipientName(user);

				try {
					sms.setRecipientNumber(users.get(user));

					Charset charset = Charset.forName("US-ASCII");
					CharsetDecoder decoder = charset.newDecoder();
					CharsetEncoder encoder = charset.newEncoder();
					
					// This line is the key to removing "unmappable" characters, alternatively chars could be replaced with '?'
					encoder.onUnmappableCharacter(CodingErrorAction.IGNORE);
					String asciimessage = message;
					try {
						// Convert a string to bytes in a ByteBuffer
						ByteBuffer bbuf = encoder.encode(CharBuffer.wrap(message));

						// Convert bytes in a ByteBuffer to a character ByteBuffer and then to a string.
						CharBuffer cbuf = decoder.decode(bbuf);
						asciimessage = cbuf.toString();
					} catch (CharacterCodingException cce) {
						String errorMessage = "Exception during character encoding/decoding: " + cce.getMessage();
						sLog.error(errorMessage, cce);
					}

					//Truncate before saving
					if (asciimessage.length() > 160) {
						asciimessage = asciimessage.substring(0, 160);
					}
					sms.setMessage(asciimessage);
					try {
						dao.saveRandomisationSMS(sms);		//add SMS to queue to be sent
					}
					catch (DAOException e) {
						throw new SMSException("Unable to save sms for randomisation");
					}
				}
				catch (SMSException ex) {
					sLog.error("Unable to create SMS message for "+user, ex);
				}
			}
		}
	}
}