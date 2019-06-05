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

package org.psygrid.common.email;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * @author Lucy Bridges
 *
 */
public class QueuedEmailsJob extends QuartzJobBean {

	private static final Log sLog = LogFactory.getLog(QueuedEmailsJob.class);

	private JavaMailSender mailSender;

	private EmailDAO emailDAO;
	private boolean sendEmails; 
	private String systemAddress = null;

	public void setSystemAddress(String systemAddress) {
		this.systemAddress = systemAddress;
	}

	public String getSystemAddress() {
		return systemAddress;
	}

	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {

		// This try-catch is needed because executeInternal should not throw RuntimeException
		// see - http://www.quartz-scheduler.org/docs/tutorial/TutorialLesson03.html
		try {			
			List<QueuedEmail> emails = emailDAO.getQueuedEmails();
	
			if (emails != null) {
					for (QueuedEmail email: emails) {
						try {		
							if (isSendEmails()) {
								send(email);
							}
							emailDAO.removeQueuedEmail(email);
						}
						catch (EmailFailureException efe) {
							sLog.error("Problem when creating email: "+email.getSubject(), efe);
						}
						catch (EmailSendException ese) {
							//Log and try again next time
							sLog.error("Problem occurred when attempting to send email", ese);
						}
					}
			}
		}catch (Exception ex) { 
			sLog.error("Problem attempting to send emails.", ex);
			throw new JobExecutionException(ex);
		}
	}
	
	public static SimpleMailMessage convertQueuedEmailToSimpleMailMessage(QueuedEmail email)  throws EmailFailureException{
		String fromAddress = email.getFromAddress();	
		String toAddress   = email.getToAddress();

		String[] bccAddresses = new String[email.getBccAddresses().size()];
		for (int i = 0; i < email.getBccAddresses().size(); i++) {
			bccAddresses[i] = email.getBccAddresses().get(i);
		}

		String subject     = email.getSubject();
		String body		   = email.getBody();


		//should populate to and from addresses automatically if invoked via ctx
		if (fromAddress == null) {
			throw new EmailFailureException("No from address set in configuration file");
		}
		if (body == null) {
			throw new EmailFailureException("Body of email has not been set");
		}
		if (subject == null) {
			throw new EmailFailureException("Subject of email has not been set");
		}

		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(fromAddress);

		if (toAddress != null && !toAddress.equals("")) {
			message.setTo(toAddress);
		}

		if (bccAddresses != null) {
			message.setBcc(bccAddresses);
		}
		message.setSentDate(new Date());
		message.setSubject(subject);
		message.setText(body);
		
		return message;
	}

	/**
	 * Send the given email using the mail sender
	 * 
	 * @param email
	 * @throws EmailSendException
	 * @throws EmailFailureException
	 */
	public void send(QueuedEmail email) throws EmailSendException, EmailFailureException {

		// Send emails even if some recipients have invalid addresses
		// see - http://java.sun.com/products/javamail/javadocs/com/sun/mail/smtp/package-summary.html
		Properties props = new Properties();
		props.put("mail.smtp.sendpartial", "true");
		((JavaMailSenderImpl) mailSender).setJavaMailProperties(props);

		SimpleMailMessage message = convertQueuedEmailToSimpleMailMessage(email);
		
		try {
			List<String> invalidAddresses = new ArrayList<String>();
			((PsyGridMailSenderImpl)mailSender).send(message, invalidAddresses);
			
			if(invalidAddresses.size() > 0){
				//Need to email support with the message not received and the recipients who didn't receive it.
				this.emailSupport(message, invalidAddresses);
			}
			
		}
		catch (org.springframework.mail.MailParseException mpe) {
			throw new EmailFailureException("Failure when parsing the message", mpe);	
		}
		catch (org.springframework.mail.MailAuthenticationException mae) {
			throw new EmailFailureException("Authentication failure trying to send message", mae);
		}catch(MailException me){
			throw new EmailFailureException("Failure when trying to send the message", me);
		}


	}
	
	public boolean emailSupport(SimpleMailMessage mail, List<String> invalidAddresses){
		
		boolean success = false;
		
		String subj = mail.getSubject();
		subj = "Failed to send: " + subj;
		mail.setSubject(subj);
		mail.setBcc(new String[0]);
		mail.setFrom(this.systemAddress);
		mail.setTo(this.systemAddress);
		String body = mail.getText();
		StringBuilder precursor = new StringBuilder();
		precursor.append("The email below was not sent to the following recipients:\n\n");
		for(String addr : invalidAddresses){
			precursor.append("- " + addr + "\n\n");
		}
		precursor.append("----------------------------------------------\n\n");
		body = precursor.toString() + body;
		mail.setText(body);
		
		try{
			mailSender.send(mail);
			success = true;
		}catch(Exception e){
			sLog.error("Partial send notification did not get sent", e);
		}
		
		return success;
	}
	

	public JavaMailSender getMailSender() {
		return mailSender;
	}

	public void setMailSender(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	public boolean isSendEmails() {
		return sendEmails;
	}

	public void setSendEmails(boolean sendEmails) {
		this.sendEmails = sendEmails;
	}

	public EmailDAO getEmailDAO() {
		return emailDAO;
	}

	public void setEmailDAO(EmailDAO emailDAO) {
		this.emailDAO = emailDAO;
	}


}
