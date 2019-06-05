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

package org.psygrid.common.email;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.mail.Address;

import org.psygrid.common.test.utils.MailSenderInitializer;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * This class offers a number of tests in order to test the partial sending of emails for which one or more 
 * recipients may be invalid.
 * @author Bill Vance
 *
 */
public class SendPartialTest extends TestCase {
	
	protected final String fromAddress;
	protected final String smtpServer;
	protected boolean useBogusServer = false;
	protected final Properties props;
	
	private final PsyGridMailSenderImpl mailSender;

	public SendPartialTest() throws FileNotFoundException, IOException {
		mailSender = new PsyGridMailSenderImpl();
		String propertiesFile = System.getProperty("test.properties.full.path");
		props = new Properties();
		props.load(new FileInputStream(propertiesFile));
		fromAddress = props.getProperty("smtp.sysAddress");
		smtpServer = props.getProperty("smtp.host");
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	

	

	/**
	 * This method tests what happens when all email addresses are valid.
	 * The method is successful if no exception is thrown AND there is nothing in the invalid addresses array.
	 */
	public void testSendAllValid() {
		
		String[] bccAddresses = new String[2];
		bccAddresses[0] = props.getProperty("smtp.validAddress1");
		bccAddresses[1] = props.getProperty("smtp.validAddress2");
		
		String subject = "Test for synchronized email sender";
		String body = "This is a test email sent by the PsyGrid system.\n\n"+
                "It has no 'to' address but was bcc'd to " + bccAddresses[0] + " and\n\n"+
                bccAddresses[1] + ". Success of this test is defined by both recipients\n\n" +
                "receiving this email.";
		
		List<String> invalidAddresses = null;
		
		try{
			invalidAddresses = this.sendEmailThrows(subject, body, bccAddresses);
		}catch(MailException e){
			Assert.fail("Failed 'send all valid test' because a MailException was thrown.");
		}
		
		boolean success = false;
		
		if(invalidAddresses != null){
			if(invalidAddresses.size() == 0){
				success = true;
			}else{
				success = false;
			}
		}
		
		assertTrue(success);
		
	}
	
	/**
	 * This test sends an email with a single, invalid recipient.
	 * The test is successful if no exception is thrown, and the returned invalid address array contains
	 * the correct invalid address only.
	 */
	public void testSendSingleInvalid(){
		
		boolean success = false;
		
		String[] bccAddresses = new String[1];
		bccAddresses[0] = props.getProperty("smtp.invalidAddr1");
		
		String subject = "Test for synchronized email sender";
		String body = "This is a test email sent by the PsyGrid system.\n\n"+
                "It has no 'to' address but was bcc'd to " + bccAddresses[0] + ", which is bogus.\n\n" +
               "Success is defined by no email being sent, and the MailSender throwing an exception.";

		List<String> invalidAddresses = null;
		
		try{
			invalidAddresses = sendEmailThrows(subject, body, bccAddresses);
		}catch(MailException e){
			Assert.fail("Send single invalid test failed due to MailException being thrown.");
		}
		
		if(invalidAddresses != null && invalidAddresses.size() == 1){
			if(invalidAddresses.get(0).equals(bccAddresses[0])){
				success = true;
			}else{
				success = false;
			}
		}else{
			success = false;
		}
		
		assertTrue(success);
		
	}

	/**
	 * An exception should NOT be thrown, but the bccAddresses array should be populated with two invalid addresses.
	 */
	public void testSendPartialValid(){
				
		String[] bccAddresses = new String[3];
		bccAddresses[0] = props.getProperty("smtp.validAddress1");
		bccAddresses[1] = props.getProperty("smtp.invalidAddr1");
		bccAddresses[2] = props.getProperty("smtp.invalidAddr2");
		
		String subject = "Test for synchronized email sender";
		String body = "This is a test email sent by the PsyGrid system.\n\n"+
                "It has no 'to' address but was bcc'd to " + bccAddresses[0] + " and\n\n"+
               bccAddresses[1] +  "and\n\n" + bccAddresses[2] + ", the latter two of which are bogus. Success is defined by the\n\n" +
               "former receiving the email but not the latter.";
		
		List<String> invalidAddresses = null;
		
		try{
			invalidAddresses = sendEmailThrows(subject, body, bccAddresses);
		}catch(MailException e){
			Assert.fail("MailException should NOT be thrown from partial valid test.");
		}
		
		boolean success = false;
		
		if(invalidAddresses != null && invalidAddresses.size() == 2){
			if((invalidAddresses.get(0).equals(bccAddresses[1]) && invalidAddresses.get(1).equals(bccAddresses[2])) ||
					(invalidAddresses.get(0).equals(bccAddresses[2]) && invalidAddresses.get(1).equals(bccAddresses[1]))){
				success = true;
			}
		}else{
			Assert.fail("The returned array of invalid addressses is not right from the partial valid test.");
		}
		
		assertTrue(success);
	}
	
	private List<String> sendEmailThrows(String subject, String body, String[] bccAddresses)  throws MailException{
		SimpleMailMessage message = new SimpleMailMessage();
				
		message.setText(body);
		message.setSubject(subject);
		message.setBcc(bccAddresses);
		message.setFrom(fromAddress);
		
		MailSenderInitializer.initToSendPartial(mailSender, props);
		
		if(this.useBogusServer){
			MailSenderInitializer.initBogusServer(mailSender, props);
			useBogusServer = false;
		}else{
			MailSenderInitializer.initLegitimateServer(mailSender, props);
		}
		
		List<String> failedAddresses = new ArrayList<String>();
		
		mailSender.send(message, failedAddresses);
		
		return failedAddresses;
	}

	/**
	 * This tests a general failure by setting a bogus mail sender. In this case, an exception should be
	 * thrown and the invalidAddress array should be empty.
	 */
	public void testGeneralFailure(){
	
		this.useBogusServer = true;
		
		String[] bccAddresses = new String[2];
		bccAddresses[0] = props.getProperty("smtp.validAddress1");
		bccAddresses[1] = props.getProperty("smtp.validAddress1");
		
		String subject = "Test for synchronized email sender";
		String body = "This is a test email sent by the PsyGrid system.\n\n"+
                "It has no 'to' address but was bcc'd to " + bccAddresses[0] + " and\n\n"+
                bccAddresses[1] + ". Success of this test is defined by both recipients\n\n" +
                "receiving this email.";
		
		List<String> invalidAddresses = null;
		
		boolean success = false;
		
		try{
			invalidAddresses = this.sendEmailThrows(subject, body, bccAddresses);
		}catch(MailException e){
			if(invalidAddresses == null || invalidAddresses.size() == 0){
				success = true;
			}else{
				success = false;
			}
			
		}
		
		this.assertTrue(success);	
	}
	


}
