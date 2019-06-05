package org.psygrid.data.clintouch;

import junit.framework.Assert;

import org.psygrid.common.sms.PsyGridSMSSenderImpl;
import org.psygrid.common.sms.SMSException;
import org.psygrid.data.clintouch.util.CreateSMSSettings;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Similar to MessageHandlerTest but uses the real PsyGridSMSSenderImpl rather than a stub.
 * The actual receipt of the message will have to be checked manually on a phone
 * @author Matt Machin
 *
 */
@Test(groups = {"integration-tests"})
public class MessageHandlerIntegrationTest {
	private MessageHandler messageHandler = new MessageHandler();
	
	private static final String PHONE_NUMBER = "";
	private static final String MESSAGE_TO_SEND = "first msg";
	
	@BeforeClass
	public void initialise() {
		setupMessageHandler();
	}
	
	public MessageHandler setupMessageHandler() {
		PsyGridSMSSenderImpl smsSender = new PsyGridSMSSenderImpl();
		smsSender.setSettings(CreateSMSSettings.getNewSMSSettings());
		messageHandler.setPsygridSMSSenderImpl(smsSender);
		return messageHandler;
	}
	
	// Tests that a message is sent
/*	@Test
	public void send() throws SMSException {
		int previousCredit = messageHandler.getPsygridSMSSenderImpl().checkCredit();
		messageHandler.send(PHONE_NUMBER, MESSAGE_TO_SEND);
		Assert.assertEquals(previousCredit - 1, messageHandler.getPsygridSMSSenderImpl().checkCredit());
	}*/
}
