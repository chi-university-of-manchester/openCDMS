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

package org.psygrid.data.utils.email;

import java.util.Date;

import javax.mail.internet.InternetAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.data.utils.wrappers.AAQCWrapper;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

public class MailClientImpl implements MailClient {

	private static Log sLog = LogFactory.getLog(MailClientImpl.class);

	private AAQCWrapper aaqc;

	private JavaMailSender mailSender;

	private String sysAdminEmail;

	private boolean sendMails;

	public AAQCWrapper getAaqc() {
		return aaqc;
	}

	public void setAaqc(AAQCWrapper aaqc) {
		this.aaqc = aaqc;
	}

	public JavaMailSender getMailSender() {
		return mailSender;
	}

	public void setMailSender(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	public boolean getSendMails() {
		return sendMails;
	}

	public void setSendMails(boolean sendMails) {
		this.sendMails = sendMails;
	}

	public String getSysAdminEmail() {
		return sysAdminEmail;
	}

	public void setSysAdminEmail(String sysAdminEmail) {
		this.sysAdminEmail = sysAdminEmail;
	}

	public void sendSupportEmail(String subject, String body, String user) 
	throws MailException {
		try {
			//look up email address
			String fromAddress = null;
			InternetAddress adr = aaqc.lookUpEmailAddress(user);
			if ( null == adr ){
				fromAddress = sysAdminEmail;
			}
			else{
				fromAddress = adr.getAddress();
			}

			SimpleMailMessage msg = new SimpleMailMessage();
			msg.setTo(sysAdminEmail);
			msg.setFrom(fromAddress);
			msg.setSentDate(new Date());
			msg.setSubject(subject);
			msg.setText(body);

			sendEmail(msg);
		}
		catch(Exception e) {
			e.printStackTrace();
			//FIXME handle exception
			//throw new Exception("Problem looking up email address: "+e.getMessage());
		}
	}

	public void sendEmail(SimpleMailMessage message) throws MailException {
		if ( sendMails ){
			mailSender.send(message);
		}
		else{
			//sLog.info("Email: To "+message.getTo()+"\n Subject="+message.getSubject()+"\n Body="+message.getText());
		}
	}

}
