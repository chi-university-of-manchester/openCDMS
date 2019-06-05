package org.psygrid.meds.medications;

public class ParticipantNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ParticipantNotFoundException() {

	}

	public ParticipantNotFoundException(String message) {
		super(message);
	}

	public ParticipantNotFoundException(Throwable cause) {
		super(cause);
	}

	public ParticipantNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
