/*
Copyright (c) 2008-2010, The University of Manchester, UK.

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

package org.psygrid.esl.test.email;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.psygrid.common.email.EmailFailureException;
import org.psygrid.common.email.EmailSendException;
import org.psygrid.common.email.PsyGridMailSenderImpl;
import org.psygrid.common.email.QueuedEmail;
import org.psygrid.common.email.QueuedEmailsJob;
import org.psygrid.common.test.utils.MailSenderInitializer;
import org.psygrid.esl.test.utils.MessageCreator;
import org.springframework.mail.SimpleMailMessage;

import junit.framework.Assert;
import junit.framework.TestCase;

public class EmailSupportTest extends TestCase {

	final private PsyGridMailSenderImpl mailSender;
	final private Properties props;
	
	public EmailSupportTest() throws FileNotFoundException, IOException{
		mailSender = new PsyGridMailSenderImpl();
		String propertiesFile = System.getProperty("test.properties.full.path");
		props = new Properties();
		props.load(new FileInputStream(propertiesFile));
	}
	
	/**
	 * Tests the ability to redirect emails to support. This is used when sendPartial is 'true' and 
	 * not all recipients could be reached due to invalid addresses.
	 */
	public void testRedirectEmail(){
		
		QueuedEmail e = MessageCreator.getInstance().generatePartialValidQueuedEmail(props);
		QueuedEmailsJob job = new QueuedEmailsJob();
		job.setSystemAddress(props.getProperty("smtp.sysAddress"));
		PsyGridMailSenderImpl mailSender = new PsyGridMailSenderImpl();
		MailSenderInitializer.initToSendPartial(mailSender, props);
		MailSenderInitializer.initLegitimateServer(mailSender, props);
		job.setMailSender(mailSender);
		
		SimpleMailMessage message = null;
		
		try {
			message = QueuedEmailsJob.convertQueuedEmailToSimpleMailMessage(e);
		} catch (EmailFailureException e1) {
			Assert.fail("Failed to create a SimpleMailMessage object from a QueuedEmail object.");
		}
		
		List<String> invalidAddresses = new ArrayList<String>();
		invalidAddresses.add(props.getProperty("smtp.invalidAddr1"));
		invalidAddresses.add(props.getProperty("smtp.invalidAddr2"));
		
		Assert.assertTrue(job.emailSupport(message, invalidAddresses));
	}
	
	/**
	 * This method tests whether the exception handling works in the QueuedEmailsJob.
	 * It simulates the exception handling implemented when processing multiple QueuedEmails
	 * in the executeInternal method.
	 * 
	 * The test creates a queue of two. The first item in the queue is partially invalid. 
	 * As such it should be sent partial & removed from the queue & support should be emailed about the unreached addresses.
	 * 
	 * The second email should be sent successfully. No exceptions should be thrown.
	 */
	public void testExceptionHanding1(){
		
		QueuedEmailsJob job = new QueuedEmailsJob();
		job.setSystemAddress(props.getProperty("smtp.sysAddress"));
		PsyGridMailSenderImpl mailSender = new PsyGridMailSenderImpl();
		MailSenderInitializer.initToSendPartial(mailSender, props);
		MailSenderInitializer.initLegitimateServer(mailSender, props);
		job.setMailSender(mailSender);
		
		List<QueuedEmail> emails = new ArrayList<QueuedEmail>();
		//if an email exists
		emails.add(MessageCreator.getInstance().generatePartialValidQueuedEmail(props));
		emails.add(MessageCreator.getInstance().generateFullValidQueuedEmail(props));

		int numExceptions =  this.executeInternalSimulation(emails, job);
		
		Assert.assertTrue(emails.size() == 0);
		Assert.assertTrue(numExceptions == 0);
		
	}
	
	/**
	 * This method simulates the looping exception handling logic in QueuedEmailsJob.executeInternal.
	 * @param emails
	 * @param job
	 * @return - returns the number of exceptions that occurred.
	 */
	private int executeInternalSimulation(List<QueuedEmail> emails, QueuedEmailsJob job){
		int numExceptions = 0;
		
		QueuedEmail[] queuedEmailArray = new QueuedEmail[emails.size()];
		queuedEmailArray = emails.toArray(queuedEmailArray);
		
		
		if (emails != null) {
			for (int i = 0; i < queuedEmailArray.length; i++) {
				try {
					QueuedEmail email = queuedEmailArray[i];
					job.send(email);
					//If we get to this point it was successful
					emails.remove(email);
				}
				catch (EmailFailureException efe) {
					numExceptions++;
				}
				catch (EmailSendException ese) {
					numExceptions++;
				}
			}
	}
		
	return numExceptions;	
		
	}
	
	/**
	 * This method tests whether the exception handling works in the QueuedEmailsJob. It simulates
	 * the exception handling implemented when processing multiple queued emails in the executeInternal method.
	 * 
	 * The test simulates an exception by setting an unreachable smtp host.
	 * The test succeedes if the method still attempts to send the two queued emails but does not remove them
	 * from the queue.
	 */
	public void testExceptionHandling2(){
		
		QueuedEmailsJob job = new QueuedEmailsJob();
		job.setSystemAddress(props.getProperty("smtp.sysAddress"));
		PsyGridMailSenderImpl mailSender = new PsyGridMailSenderImpl();
		MailSenderInitializer.initToSendPartial(mailSender, props);
		MailSenderInitializer.initBogusServer(mailSender, props);
		job.setMailSender(mailSender);
		
		List<QueuedEmail> emails = new ArrayList<QueuedEmail>();
		//if an email exists
		emails.add(MessageCreator.getInstance().generateFullValidQueuedEmail(props));
		emails.add(MessageCreator.getInstance().generateFullValidQueuedEmail(props));

		int numExceptions = this.executeInternalSimulation(emails, job);
		
		Assert.assertTrue(numExceptions == 2);
		Assert.assertTrue(emails.size() == 2);
		
	}
	
}
