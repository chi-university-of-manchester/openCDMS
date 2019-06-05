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

package org.psygrid.esl.scheduling;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.common.email.EmailDAO;
import org.psygrid.common.email.QueuedEmail;
import org.psygrid.common.sms.PsyGridSMSSenderImpl;
import org.psygrid.common.sms.SMSException;
import org.psygrid.esl.dao.DAOException;
import org.psygrid.esl.dao.EslDAO;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * Class to check the SMS logs, which show a list of all SMS's
 * sent that day.
 * 
 * @author Lucy Bridges
 *
 */
public class CheckSMSAccountJob extends QuartzJobBean {

	private static final Log sLog = LogFactory.getLog(CheckSMSAccountJob.class);

	/**
	 * The SMS Sender connector..
	 */
	private PsyGridSMSSenderImpl sender;

	/**
	 * Whether to email the results of account checking.
	 * 
	 * Should always be true except for testing
	 */
	private boolean sendEmail; 

	/**
	 * Address to email the results of account checking
	 */
	private String emailAddress;

	/**
	 * Used to email results of account checking
	 */
	private JavaMailSender mailSender;

	private EmailDAO dao;

	
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		// This try-catch is needed because executeInternal should not throw RuntimeException
		// see - http://www.quartz-scheduler.org/docs/tutorial/TutorialLesson03.html
		try {			
			String results = sender.checkAccount();

			if (results == null) {
				sLog.error("No SMS Log returned");
			}
			if (results.equals("")) {
				sLog.info("No SMS log today");
				return;
			}
			
			if (sendEmail) {
				sendEmail(results);
			}
		}catch (Exception e) { 
			sLog.error("Problem occurred when attempting to get the SMS account", e);
			throw new JobExecutionException(e);
		}

	}

	private void sendEmail(String results)  {
		
		QueuedEmail message = new QueuedEmail();
		message.setFromAddress(emailAddress);
		message.setToAddress(emailAddress);
		message.setSubject("SMS Log");
		message.setBody(results);

		//add email to queue to be sent
		dao.saveEmail(message);	
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public boolean isSendEmail() {
		return sendEmail;
	}

	public void setSendEmail(boolean sendEmail) {
		this.sendEmail = sendEmail;
	}

	public PsyGridSMSSenderImpl getSender() {
		return sender;
	}

	public void setSender(PsyGridSMSSenderImpl sender) {
		this.sender = sender;
	}

	public JavaMailSender getMailSender() {
		return mailSender;
	}

	public void setMailSender(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	public EmailDAO getDao() {
		return dao;
	}

	public void setDao(EmailDAO dao) {
		this.dao = dao;
	}


}
