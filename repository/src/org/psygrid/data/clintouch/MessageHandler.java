package org.psygrid.data.clintouch;

import org.psygrid.common.sms.PsyGridSMSSenderImpl;
import org.psygrid.common.sms.SMSException;
import org.psygrid.common.sms.SMSMessage;


/**
 * Sends messages to users and parses the responses.
 * @author Matt Machin
 * @version 1.0
 * @created 18-Aug-2011 15:29:24
 */
public class MessageHandler {
	private static final String VIRTUAL_MOBILE_NUMBER = "07797885087";

	private PsyGridSMSSenderImpl psygridSMSSenderImpl;

	public PsyGridSMSSenderImpl getPsygridSMSSenderImpl() {
		return psygridSMSSenderImpl;
	}

	public void setPsygridSMSSenderImpl(PsyGridSMSSenderImpl psygridSMSSenderImpl) {
		this.psygridSMSSenderImpl = psygridSMSSenderImpl;
	}

	/**
	 * Throws an exception if sending failed.
	 * 
	 * @param destination
	 * @param message
	 * @throws SMSException 
	 */
	public void send(String destination, String message) throws SMSException {
		SMSMessage smsMessage = new SMSMessage();
		smsMessage.setMessage(message);
		smsMessage.setRecipientNumber(destination);
		smsMessage.setFromId(VIRTUAL_MOBILE_NUMBER);
		psygridSMSSenderImpl.send(smsMessage);
	}
}