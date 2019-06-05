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

package org.psygrid.esl.test.randomise;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.psygrid.common.email.EmailFailureException;
import org.psygrid.common.email.EmailSendException;
import org.psygrid.common.sms.PsyGridSMSSenderImpl;
import org.psygrid.common.sms.SMSException;
import org.psygrid.common.sms.SMSMessage;
import org.psygrid.esl.dao.DAOException;
import org.psygrid.esl.dao.EslDAO;
import org.psygrid.esl.model.dto.Project;
import org.psygrid.esl.model.dto.Subject;
import org.psygrid.esl.scheduling.hibernate.QueuedSMS;
import org.psygrid.esl.util.EmailUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * Test the Kapow! SMS sending service
 * 
 * @author Lucy Bridges
 *
 */
public class KapowSMSTest extends TestCase {

	protected static final String emailAddress = "lucy.bridges@manchester.ac.uk";
	
	protected ApplicationContext ctx = null;
	protected JavaMailSenderImpl mailSender;
	protected PsyGridSMSSenderImpl sender;
	protected EslDAO dao;
	
	private static final boolean sendSMS = false;	//this will send sms msgs and therefore cost money if enabled!
	
	public KapowSMSTest() {
		String[] paths = {"applicationContext.xml"};
		try {
			ctx = new ClassPathXmlApplicationContext(paths);
			mailSender = new JavaMailSenderImpl();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception: "+ex.toString());
		}
	}
	
	
	protected void setUp() throws Exception {
		super.setUp();
		dao = (EslDAO)ctx.getBean("eslClientDAOService");
		sender = (PsyGridSMSSenderImpl)ctx.getBean("smsSender");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		dao = null;
		sender = null;
	}       

	public void testSaveSMS() {
		QueuedSMS sms = new QueuedSMS();
		sms.setRecipientName("KapowSMSTest");
		
		try {
			sms.setRecipientNumber("0123546");
			sms.setMessage("SMS Test Message Here");
			try {
				dao.saveRandomisationSMS(sms);		//add SMS to queue to be sent
			}
			catch (DAOException e) {
				fail("Unable to save sms for randomisation: "+e.getMessage());
			}
		}
		catch (SMSException ex) {
			//Should never happen
			fail("Unable to create sms message: "+ex.getMessage());
		}
		
		try {
			int noOfSMSs = dao.getQueuedSMSs().size();
			
			assertEquals("Incorrect number of SMSs found ", 1, noOfSMSs);
		}
		catch (DAOException e) {
			fail("Unable to save sms for randomisation: "+e.getMessage());
		}
	}
	
	public void testCheckLogs() {
		try {			
			String results = sender.checkAccount();

			if (results == null) {
				fail("No SMS Log returned");
			}
			if (results.equals("")) {
				System.out.println("No SMS Log today");
			}
			else {
				sendEmail(results);
			}

		}
		catch (SMSException e) {
			fail("Problem occurred when attempting to check the SMS account "+e.getMessage());
		}
		catch (EmailSendException ese) {
			//Log and try again next time
			fail("Problem occurred when attempting to send SMS log email "+ese.getMessage());
		}
		catch (EmailFailureException efe) {
			//Log and try again next time
			fail("Problem occurred when attempting to send SMS log email "+efe.getMessage());
		}
	}
	
	public void testCheckCredit() {
		try {			
			int results = sender.checkCredit();

			if (results == 0) {
				fail("No SMS Credits");
			}
			System.out.println("Kapow has "+results+" credits");

		}catch (SMSException e) {
			e.printStackTrace();
			fail("Problem occurred when attempting to check the credits for the SMS account "+e.getMessage());
		}
	}
	
	/**
	 * Test method for {@link org.psygrid.esl.randomise.EmailNotification#send()}.
	 */
	public void testSend() {
			
		QueuedSMS sms = new QueuedSMS();
		sms.setRecipientName("KapowSMSTest");
		
		try {
			sms.setRecipientNumber("07763200783");
			sms.setMessage("SMS Test Message Here");
			try {
				dao.saveRandomisationSMS(sms);		//add SMS to queue to be sent
			}
			catch (DAOException e) {
				fail("Unable to save sms for randomisation: "+e.getMessage());
			}
		}
		catch (SMSException ex) {
			//Should never happen
			fail("Unable to create sms message: "+ex.getMessage());
		}
		
		try {
			int noOfSMSs = dao.getQueuedSMSs().size();
			
			assertTrue("No SMSs found ", noOfSMSs > 0);
			
			//send sms
			send(dao.getQueuedSMSs().get(0));
		}
		catch (DAOException e) {
			fail("Unable to save sms for randomisation: "+e.getMessage());
		}
		catch (SMSException ex) {
			ex.printStackTrace();
			fail("Problem sending SMS message "+ex.getMessage());
		}
	}
	
	public void testPhoneNumbers() {
		List<String> roles = new ArrayList<String>();
		roles.add("TreatmentAdministrator");
		
		try {
			Project ed2 = dao.getProject("ED2");
			Subject subject = dao.getSubject(ed2, "ED2/002001-1");
			
			EmailUtil util = new EmailUtil();
			Map<String, String> phoneMap = util.getPhoneNumbers(subject.toHibernate(), roles);
			
			if (phoneMap == null || phoneMap.keySet().size() == 0) {
				fail("No mobile phone numbers found");
			}
			
			for (String user: phoneMap.keySet()) {
				System.out.println(user + " has number "+phoneMap.get(user));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	/**
	 * Send the given email using the mail sender
	 * 
	 * @param email
	 * @return creditRemaining
	 * @throws SMSException
	 */
	private int send(QueuedSMS sms) throws SMSException {

		if (sms == null) {
			throw new SMSException("QueuedSMS is null");
		}

		SMSMessage message = new SMSMessage();
		message.setMessage(sms.getMessage());
		message.setRecipientName(sms.getRecipientName());
		message.setRecipientNumber(sms.getRecipientNumber());

		int creditRemaining = 0;
		
		if (sendSMS) {
			creditRemaining = sender.send(message);
			System.out.println("SMS sent. Credit remaining is "+creditRemaining);
		}
		return creditRemaining;
	}

	private void sendEmail(String results) throws EmailFailureException, EmailSendException {

		if (emailAddress == null) {
			fail("emailAddress is null");
		}

		SimpleMailMessage message = new SimpleMailMessage();

		message.setFrom(emailAddress);
		message.setTo(emailAddress);

		message.setSentDate(new Date());
		message.setSubject("SMS Log");
		message.setText(results);

		try {
			mailSender.send(message);
		}
		catch (org.springframework.mail.MailParseException mpe) {
			throw new EmailFailureException("Failure when parsing the message", mpe);	
		}
		catch (org.springframework.mail.MailAuthenticationException mae) {
			throw new EmailFailureException("Authentication failure trying to send message", mae);
		}
		catch (org.springframework.mail.MailSendException mse) {
			throw new EmailSendException("Failure sending messsage", mse);
		}

	}

}
