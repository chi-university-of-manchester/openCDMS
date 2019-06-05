package org.psygrid.meds.medications;

public class InvalidMedicationPackageException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5502115752838501713L;
	
	
	public InvalidMedicationPackageException(String reason){
		super(reason);
	}
	
	public InvalidMedicationPackageException(String reason, Throwable cause){
		super(reason, cause);
	}

}
