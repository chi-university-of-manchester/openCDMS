package org.psygrid.meds.medications;

public class MedicationPackageNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MedicationPackageNotFoundException() {
	}

	public MedicationPackageNotFoundException(String message) {
		super(message);
	}

	public MedicationPackageNotFoundException(Throwable cause) {
		super(cause);
	}

	public MedicationPackageNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
