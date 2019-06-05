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

package org.psygrid.esl.test.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.psygrid.common.email.QueuedEmail;

/**
 * This class is useful for creating dummy email messages for testing purposes.
 * @author Bill Vance
 *
 */
public class MessageCreator {

	private static MessageCreator mc = null;
	
	public static MessageCreator getInstance(){
		MessageCreator mcr = null;
		
		if(mc != null){
			mcr = mc;
		}else{
			MessageCreator.mc = new MessageCreator();
			mcr = mc;
		}
		
		return mcr;
	}
	
	public QueuedEmail generateFullValidQueuedEmail(Properties props){
		List<String> bccAddresses = new ArrayList<String>();
		bccAddresses.add(props.getProperty("smtp.validAddress1"));
		bccAddresses.add(props.getProperty("smtp.validAddress1"));
		
		QueuedEmail mail = new QueuedEmail();
		mail.setBody("Body text");
		mail.setFromAddress(props.getProperty("smtp.sysAddress"));
		mail.setSubject("Subject");
		mail.setBccAddresses(bccAddresses);
		
		return mail;
	}
	
	public QueuedEmail generatePartialValidQueuedEmail(Properties props){
		
		List<String> bccAddresses = new ArrayList<String>();
		bccAddresses.add(props.getProperty("smtp.validAddress1"));
		bccAddresses.add(props.getProperty("smtp.invalidAddr1"));
		
		QueuedEmail mail = new QueuedEmail();
		mail.setBody("Body text");
		mail.setFromAddress(props.getProperty("smtp.sysAddress"));
		mail.setSubject("Subject");
		mail.setBccAddresses(bccAddresses);
		
		return mail;
	}
	
	public QueuedEmail generateQueuedEmailWithSingleInvalidAddress(){
		return null;
	}
	
}
