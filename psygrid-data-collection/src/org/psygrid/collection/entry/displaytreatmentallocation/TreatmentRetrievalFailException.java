package org.psygrid.collection.entry.displaytreatmentallocation;

public class TreatmentRetrievalFailException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4291191843597312048L;

	private final TreatmentRetrievalFailReason reason;
	
	public TreatmentRetrievalFailException(TreatmentRetrievalFailReason reason){
		this.reason = reason;
	}

	public TreatmentRetrievalFailReason getReason() {
		return reason;
	}
	
}
