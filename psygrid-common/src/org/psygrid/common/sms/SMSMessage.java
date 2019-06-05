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

package org.psygrid.common.sms;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class to create an SMS message used to send an SMS using
 * PsyGridSMSSenderImpl.
 * 
 * @author Lucy Bridges
 *
 */
public class SMSMessage {

	private String recipientName;
	private String recipientNumber;
	private String message;	
	private String fromId; //If enabled?


	public String getFromId() {
		return fromId;
	}

	public void setFromId(String fromId) throws SMSException {
		if (fromId == null) {
			throw new SMSException("FromId can not be null");
		}
		this.fromId = fromId;
	}

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

	public String getRecipientName() {
		return recipientName;
	}

	public void setRecipientName(String recipientName) throws SMSException {
		if (recipientName == null || recipientName.equals("")) {
			throw new SMSException("Recipient name can not be empty");
		}
		this.recipientName = recipientName;
	}

	public String getRecipientNumber() {
		return recipientNumber;
	}

	public void setRecipientNumber(String number) throws SMSException{
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

		this.recipientNumber = number;
	}

	public boolean equals(Object arg0) {
		if((arg0 instanceof SMSMessage) 
				&& (((SMSMessage)arg0).getFromId() == this.fromId)
				&& (((SMSMessage)arg0).getMessage() == this.message)
				&& (((SMSMessage)arg0).getRecipientName() == this.recipientName)
				&& (((SMSMessage)arg0).getRecipientNumber() == this.recipientNumber))
		{
			return true;
		}
		return false;
	}

}
