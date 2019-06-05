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

import junit.framework.TestCase;

import org.psygrid.common.email.Email;
import org.psygrid.common.email.QueuedEmail;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * @author Lucy Bridges
 *
 */
public class EmailTest extends TestCase {

	protected String toAddress = "lucy.bridges@manchester.ac.uk";
	protected String fromAddress = "lucy.bridges@manchester.ac.uk";
	protected String smtpServer = "localhost";
	protected String[] bccAddresses = new String[1];
	
	
	protected ApplicationContext ctx = null;
	private JavaMailSenderImpl mailSender;
	private QueuedEmail email = new QueuedEmail();
	
	
	
	public EmailTest() {
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
	}


	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for {@link org.psygrid.esl.randomise.EmailNotification#send()}.
	 */
	public void testSend() {
			
		Email email = new Email();
		
		bccAddresses[0] = "lucy.bridges@manchester.ac.uk";
		
		email.setSubject("Unit test for org.psygrid.esl.randomise.EmailNotification");
		email.setBody("This is a test email sent by the PsyGrid system.\n\n"+
                "The mail was sent to email address "+toAddress+" using the mail server "+
                smtpServer+".\n\n"+"If you are not the intended recipient " +
                "of this message please contact support@psygrid.org.");
		
		//send email
		assertTrue(sendEmail(email));
	}
	
	
	private boolean sendEmail(Email email)  {
		SimpleMailMessage message = new SimpleMailMessage();
				
		message.setText(email.getBody());
		message.setSubject(email.getSubject());
		message.setTo(toAddress);
		message.setBcc(bccAddresses);
		message.setFrom(fromAddress);
		mailSender.setHost(smtpServer);
		
		
		try {
			mailSender.send(message);
		}
		catch (Exception re) {
			re.getStackTrace();
			fail("Failure trying to send email");
		}
		return true;
	}

}
