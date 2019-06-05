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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.common.sms.PsyGridSMSSenderImpl;
import org.psygrid.common.sms.SMSException;
import org.psygrid.common.sms.SMSMessage;
import org.psygrid.esl.dao.DAOException;
import org.psygrid.esl.dao.EslDAO;
import org.psygrid.esl.scheduling.hibernate.QueuedSMS;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * Process and send the queue of SMS messages.
 * 
 * @author Lucy Bridges
 *
 */
public class QueuedSMSsJob extends QuartzJobBean {

	private static final Log sLog = LogFactory.getLog(QueuedSMSsJob.class);


	private EslDAO eslDAO;
	private boolean sendSMSs; 

	private PsyGridSMSSenderImpl sender;

	
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {

		// This try-catch is needed because executeInternal should not throw RuntimeException
		// see - http://www.quartz-scheduler.org/docs/tutorial/TutorialLesson03.html
		try {
			List<QueuedSMS> smsQueue = eslDAO.getQueuedSMSs();
	
			if (smsQueue != null) {
				for (QueuedSMS sms: smsQueue) {
					sLog.info("Attempting to send SMS");
					int creditRemaining = 0;
					if (isSendSMSs()) {
						creditRemaining = send(sms);
					}
					//If we get to this point it was successful
					eslDAO.removeQueuedSMS(sms);
	
					//This will occur if credit has ran out after sending a message and 
					//will prevent attempts to send any remaining messages.
					if (isSendSMSs() && creditRemaining <= 0) {
						sLog.error("Problem occured when sending SMS messages: Out of credit");
						break;
					}
				}
			}
		}
		catch (Exception e) { 
			sLog.error("Problem occurred when sending SMS", e);
			throw new JobExecutionException(e);
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

		SMSMessage message = new SMSMessage();
		message.setMessage(sms.getMessage());
		message.setRecipientName(sms.getRecipientName());
		message.setRecipientNumber(sms.getRecipientNumber());
		
		int creditRemaining = 0;
		
		if (isSendSMSs()) {
			creditRemaining = sender.send(message);
		}

		return creditRemaining;
	}

	public boolean isSendSMSs() {
		return sendSMSs;
	}

	public void setSendSMSs(boolean sendSMSs) {
		this.sendSMSs = sendSMSs;
	}

	public EslDAO getEslDAO() {
		return eslDAO;
	}

	public void setEslDAO(EslDAO eslDAO) {
		this.eslDAO = eslDAO;
	}

	public PsyGridSMSSenderImpl getSender() {
		return sender;
	}

	public void setSender(PsyGridSMSSenderImpl sender) {
		this.sender = sender;
	}


}
