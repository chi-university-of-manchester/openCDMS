package org.psygrid.data.clintouch;

import org.psygrid.common.sms.SMSException;
import org.psygrid.common.sms.SMSMessage;
import org.psygrid.data.clintouch.util.CreateSMSSettings;
import org.psygrid.data.clintouch.util.PsyGridSMSSenderImplStub;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class MessageHandlerTest {
	private MessageHandler messageHandler;
	private PsyGridSMSSenderImplStub stub = new PsyGridSMSSenderImplStub();
	
	private static final String PHONE_NUMBER = "01234567";
	private static final String MESSAGE = "First SMS message";
	private SMSMessage expectedMessage = new SMSMessage();
	
	@BeforeClass
	public void initialise() {
		messageHandler = getMessageHandlerWithStubs();
		stub.setSettings(CreateSMSSettings.getNewSMSSettings());
	}
	
	/**
	 * This public method is there to allow other test classes to make use of the
	 * stubbed MessageHandler
	 * @return
	 */
	public MessageHandler getMessageHandlerWithStubs() {
		MessageHandler localMessageHandler = new MessageHandler();
		localMessageHandler.setPsygridSMSSenderImpl(stub.getStubObject());
		return localMessageHandler;
	}
	
	public PsyGridSMSSenderImplStub getSMSSenderStub() {
		return stub;
	}
	
	// Tests that the correct message is sent
	@Test
	public void send() throws SMSException {		
		setupExpectedMessage();
		messageHandler.send(PHONE_NUMBER, MESSAGE);
		stub.verifyExpectedMessage(expectedMessage);
	}
	
	private void setupExpectedMessage() {
		try {
			expectedMessage.setRecipientNumber(PHONE_NUMBER);
			expectedMessage.setMessage(MESSAGE);
			expectedMessage.setFromId("07797885087");
		} catch (SMSException e1) {
			e1.printStackTrace();
		}
	}
}
