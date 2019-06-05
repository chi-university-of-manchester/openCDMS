package org.psygrid.data.clintouch;

/**
 * Data relating to the participant that is needed in order to send questions.
 * @author Admin
 * @version 1.0
 * @created 05-Sep-2011 11:19:47
 */
public class ParticipantDetails {
	private String mobileNumber;

	public ParticipantDetails(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	
	public ParticipantDetails() {
		
	}

	public String getMobileNumber() {
		return mobileNumber;
	}
}