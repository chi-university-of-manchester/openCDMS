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
import java.util.List;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.event.TransportEvent;
import javax.mail.event.TransportListener;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

public class PsyGridMailSenderImpl extends JavaMailSenderImpl {

	private static final Log mailLog = LogFactory.getLog("Mail."+PsyGridMailSenderImpl.class);

	private static final Log sLog = LogFactory.getLog(JavaMailSenderImpl.class);
	
	private Transport transport = null;
	
	private LocalTransportListener transportListener = null;
	
	/**
	 * This class has been addded in order to capture information regarding invalid email adressess when the 
	 * mail sender has been configured to send partial messages.
	 * @author Bill Vance
	 *
	 */
	private class LocalTransportListener implements TransportListener{
		
		//private int successResult = -1; //0 =  some or all failed (reason not known) | 1 = full success 
		
		private List<String> invalidAddresses;
		private boolean finished = false;
		
		public LocalTransportListener(List<String> addr){
			invalidAddresses = addr;
		}
		
		public synchronized void sync(){
			if(!finished){
				try {
					this.wait();
				} catch (InterruptedException e) {
					throw new MailSendException("Problem retrieving the invalid addresses after the email was sent.");
				}
			}	
		}
		
		
		public synchronized void messageDelivered(TransportEvent arg0) {
			finished = true;
			this.notify();
		}

		
		public synchronized void messageNotDelivered(TransportEvent arg0) {
		
			if(invalidAddresses.size() == 0){
				Address[] invalidAddresses = arg0.getInvalidAddresses();
				for(int i = 0; i < invalidAddresses.length; i++){
					this.invalidAddresses.add(invalidAddresses[i].toString());
				}
			}
			finished = true;
			this.notify();
		}

		
		public synchronized void messagePartiallyDelivered(TransportEvent arg0) {
			//We do not do anything because in this scenario, messageNotDelivered reliably
			//gets called as well, and it is possible to discern partial success by inspecting
			//the event.
		}
	}

	private void cleanUp(){
		transportListener = null;
		transport = null;
	}

	/**
	 * Use this method when the mail sender has been configured to send partial messages. It does NOT throw an exception
	 * in the event that a message is successfully sent to some recipients.
	 * @param simpleMessage
	 * @param invalidAddresses - a list of any invalid addresses found when sending the partial message.
	 */
	public synchronized void send(SimpleMailMessage simpleMessage, List<String> invalidAddresses) throws MailException{
		setInvalidAddressTransportListener(invalidAddresses);
		try{
			send(simpleMessage);
			cleanUp();
		}catch(MailException e){
			if(e.getCause() != null){ //This appears to be the case where there is a general error preventing the sending of the entire message
				throw e;
			}else{
				transportListener.sync();
			}
			
			cleanUp();
			
		}catch(Exception e){
			cleanUp();
		}
	}
	
	protected void setTransport(Transport t){
		transport = t;
	}
	
	private void setInvalidAddressTransportListener(List<String> invalidAddresses) throws MailSendException{
		try{
			Transport tr = this.getTransport(getSession());
			transportListener = new LocalTransportListener(invalidAddresses);
			tr.addTransportListener(transportListener);
			setTransport(tr);
		}catch (MessagingException ex) {
			throw new MailSendException("Mail server connection failed", ex);
		}
	}
	
	/**
	 * Get a Transport object for the given JavaMail Session.
	 * Can be overridden in subclasses, e.g. to return a mock Transport object.
	 * @see javax.mail.Session#getTransport
	 * @see #getProtocol
	 */
	protected Transport getTransport(Session session) throws NoSuchProviderException {
		Transport returnTransport = null;
		
		if(transport != null){
			returnTransport = transport;
			transport = null;
		}else{
			returnTransport = session.getTransport(getProtocol());
		}
		
		return returnTransport;
	}
	
	
	public synchronized void send(SimpleMailMessage simpleMessage) throws MailException {
		super.send(simpleMessage);
		try{
			logEmail(simpleMessage.getTo(), simpleMessage.getSubject(), simpleMessage.getCc(), simpleMessage.getBcc());
		}
		catch(Exception ex){
			sLog.error(ex);
		}
	}
	

	
	public synchronized void send(SimpleMailMessage[] simpleMessages) throws MailException {
		super.send(simpleMessages);
		try{
			for ( int j=0; j<simpleMessages.length; j++ ){
				SimpleMailMessage simpleMessage = simpleMessages[j];
				logEmail(simpleMessage.getTo(), simpleMessage.getSubject(), simpleMessage.getCc(), simpleMessage.getBcc());
			}
		}
		catch(Exception ex){
			sLog.error(ex);            
		}
	}

	
	
	public synchronized void send(MimeMessage mimeMessage) throws MailException {
		super.send(mimeMessage);
		try{
			String[] addressList = new String[mimeMessage.getAllRecipients().length];
			for ( int i=0; i<mimeMessage.getAllRecipients().length; i++ ){
				addressList[i] = mimeMessage.getAllRecipients()[i].toString();
			}
			logEmail(addressList, mimeMessage.getSubject(), null, null);
		}
		catch(Exception ex){
			sLog.error(ex);
		}
	}
	

	
	public synchronized void send(MimeMessage[] mimeMessages) throws MailException {
		super.send(mimeMessages);
		try{
			for ( int j=0; j<mimeMessages.length; j++ ){
				MimeMessage mimeMessage = mimeMessages[j];
				String[] addressList = new String[mimeMessage.getAllRecipients().length];
				for ( int i=0; i<mimeMessage.getAllRecipients().length; i++ ){
					addressList[i] = mimeMessage.getAllRecipients()[i].toString();
				}
				logEmail(addressList, mimeMessage.getSubject(), null, null);
			}
		}
		catch(Exception ex){
			sLog.error(ex);            
		}
	}

	private void logEmail(String[] addresses, String subject, String[] ccAddresses, String[] bccAddresses) throws Exception {
		StringBuilder msg = new StringBuilder();
		msg.append("Email sent. To: ");
		for ( int i=0; i<addresses.length; i++ ){
			if ( i > 0 ){
				msg.append(", ");
			}
			msg.append(addresses[i]);
		}
		msg.append("; Subject: "+subject);

		if (ccAddresses != null && ccAddresses.length > 0) {
			msg.append("; CC'd to ");
			for ( String cc: ccAddresses ){
				msg.append(cc);
				msg.append(", ");
			}
		}
		
		if (bccAddresses != null && bccAddresses.length > 0) {
			msg.append("; BCC'd to ");
			for ( String bcc: bccAddresses ){
				msg.append(bcc);
				msg.append(", ");
			}
		}
		System.out.println("!!! Sent Email");
		mailLog.info(msg.toString());
	}
	
}
