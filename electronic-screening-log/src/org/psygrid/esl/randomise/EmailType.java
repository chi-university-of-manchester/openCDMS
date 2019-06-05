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

package org.psygrid.esl.randomise;

/**
 * Class providing the possible types of Email that can be sent
 * during different stages of randomisation.
 * 
 * @author Lucy Bridges
 */
public enum EmailType {

	/**
	 * The email to be sent when randomisation is invoked for a subject
	 */
	INVOCATION ("invocation"),
	SMS_INVOCATION ("sms_invocation"),
	CUSTOM_INVOCATION ("custom invocation"),
	CUSTOM_SMS_INVOCATION ("custom sms invocation"),
	
	/**
	 * The email to be sent when a treatment arm is allocated to a subject
	 * as a result of randomisation 
	 */
	DECISION ("decision"),
	SMS_DECISION ("sms_decision"),
	CUSTOM_DECISION ("custom decision"),
	CUSTOM_SMS_DECISION ("custom sms decision"),
	
	/**
	 * The email to be sent, normally to the therapist, to inform of the 
	 * treatment arm allocated by the randomisation process
	 */
	TREATMENT ("treatment"),
	SMS_TREATMENT ("sms_treatment"),
	CUSTOM_TREATMENT ("custom treatment"),
	CUSTOM_SMS_TREATMENT ("custom sms treatment");
	
	/**
	 * A string representation of the EmailType
	 */
	private final String type;
	
	
	private EmailType (String type) {
		this.type = type;
	}
	
	public String type() {
		return type;
	}
	
	/**
	 * Convert a String to its equivalent EmailType
	 * 
	 * @param type
	 * @return An EmailType
	 */
	public static EmailType getType(String type) {
		if (type.equals("invocation"))
			return EmailType.INVOCATION;
		if (type.equals("decision"))
			return EmailType.DECISION;
		if (type.equals("treatment"))
			return EmailType.TREATMENT;
		if (type.equals("sms_invocation"))
			return EmailType.SMS_INVOCATION;
		if (type.equals("sms_decision"))
			return EmailType.SMS_DECISION;
		if (type.equals("sms_treatment"))
			return EmailType.SMS_TREATMENT;
		if(type.equals("custom invocation"))
			return EmailType.CUSTOM_INVOCATION;
		if(type.equals("custom sms invocation"))
			return EmailType.CUSTOM_SMS_INVOCATION;
		if(type.equals("custom decision"))
			return EmailType.CUSTOM_DECISION;
		if(type.equals("custom sms decision"))
			return EmailType.CUSTOM_SMS_DECISION;
		if(type.equals("custom treatment"))
			return EmailType.CUSTOM_TREATMENT;
		if(type.equals("custom sms treatment"))
			return EmailType.CUSTOM_SMS_TREATMENT;
		return null;
	}
	
}
