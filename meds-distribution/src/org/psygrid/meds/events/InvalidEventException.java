package org.psygrid.meds.events;

public class InvalidEventException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7287021711142913982L;

	public InvalidEventException(String reason){
		super(reason);
	}
	
}
