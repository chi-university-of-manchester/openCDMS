package org.psygrid.data.clintouch.util;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.psygrid.common.sms.PsyGridSMSSenderImpl;
import org.psygrid.common.sms.SMSException;
import org.psygrid.common.sms.SMSMessage;
import org.psygrid.common.sms.SMSSettings;

public class PsyGridSMSSenderImplStub {
	private PsyGridSMSSenderImpl psyGridSMSSenderImplStub;
	private SMSSettings smsSettings;
	
	/**
	 * Make this behave as much as possible like the real class.
	 * So, throw an exception if SMSSettings have not been set.
	 */
	public PsyGridSMSSenderImplStub() {
		psyGridSMSSenderImplStub = mock(PsyGridSMSSenderImpl.class);
		try {
			when(psyGridSMSSenderImplStub.send(any(SMSMessage.class))).thenAnswer(new Answer<Integer>() {
			     public Integer answer(InvocationOnMock invocation) throws Throwable {
			         if(smsSettings == null) {
			        	 throw new SMSException("SMSSettings has not been set");
			         }
			         return 1;
			     }
			 });
		} catch (SMSException e) {
			e.printStackTrace();
		}
	}
	
	public void setSettings(SMSSettings smsSettings) {
		this.smsSettings = smsSettings;
	}
	
	public PsyGridSMSSenderImpl getStubObject() {
		return psyGridSMSSenderImplStub;
	}
	
	public void verifyExpectedMessage(SMSMessage expectedMessage) throws SMSException {
		verify(psyGridSMSSenderImplStub).send(expectedMessage);
	}
}