package org.psygrid.meds.project;

public class InvalidParameter extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2008877391540461626L;
	
	public InvalidParameter(String reason){
		super(reason);
	}
}
