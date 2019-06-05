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

package org.psygrid.esl.scheduling.hibernate;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.psygrid.common.sms.SMSException;
import org.psygrid.esl.model.IPersistent;
import org.psygrid.esl.model.hibernate.Persistent;


/**
 * An SMS message waiting to be sent.
 * 
 * SMS messages are typically used to notify of a randomisation event
 * in addition to sending an email.
 * 
 * @author Lucy Bridges
 * 
 * @hibernate.joined-subclass table="t_queued_sms"
 * 								proxy="org.psygrid.esl.scheduling.hibernate.QueuedSMS"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class QueuedSMS extends Persistent {

	private String recipientName;
	private String recipientNumber;
	private String message;	

	public QueuedSMS() {
	}


	/**
	 * @return the body
	 * 
	 * @hibernate.property column="c_message" type="text" length="160"
	 */
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) throws SMSException {
		if (message == null) {
			if (getRecipientName() != null) {
				throw new SMSException("[User: "+getRecipientName()+"] Message cannot be null");
			}
			throw new SMSException("Message cannot be null");
		}
		if (message.length() > 160) {
			if (getRecipientName() != null) {
				throw new SMSException("[User: "+getRecipientName()+"] Message length may not be longer than 160 characters");
			}
			throw new SMSException("Message length may not be longer than 160 characters");
		}
		this.message = message;
	}

	/**
	 * @return the subject
	 * 
	 * @hibernate.property column="c_recipient_name"
	 */
	public String getRecipientName() {
		return recipientName;
	}

	/**
	 * @param subject the subject to set
	 */
	public void setRecipientName(String name) {
		recipientName = name;
	}


	/**
	 * @return the toAddresses
	 * 
	 * @hibernate.property column="c_recipient_number"
	 */
	public String getRecipientNumber() {
		return recipientNumber;
	}

	/**
	 * @param toAddress
	 *            the toAddresses to set
	 */
	public void setRecipientNumber(String number) throws SMSException {
		if (number == null) {
			if (getRecipientName() != null) {
				throw new SMSException("[User: "+getRecipientName()+"] Number cannot be null");
			}
			throw new SMSException("Number cannot be null");
		}
		if (number.startsWith("+") || number.startsWith("00")) {
			if (getRecipientName() != null) {
				throw new SMSException("[User: "+getRecipientName()+"] Invalid phone number format. Numbers must not start with '+' or '00'. If sending internationally prepend the correct country code instead.");
			}
			throw new SMSException("Invalid phone number format. Numbers must not start with '+' or '00'. If sending internationally prepend the correct country code instead.");
		}

		Pattern pattern = Pattern.compile("[^0-9]", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(number);
		if (matcher.find()) {
			if (getRecipientName() != null) {
				throw new SMSException("[User: "+getRecipientName()+"] Number must contain characters 0-9 only, with no spaces");
			}
			throw new SMSException("Number must contain characters 0-9 only, with no spaces");
		}

		recipientNumber = number;
	}

	//method is not required in this class
	public org.psygrid.esl.model.dto.Persistent toDTO(Map<IPersistent, org.psygrid.esl.model.dto.Persistent> dtoRefs) {
		return null; 
	}


}
