package org.psygrid.meds.utils.security;

public class NotAuthorisedFault extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4549309291984918467L;
	
private String message;
    
    public NotAuthorisedFault() {
        super();
    }

    public NotAuthorisedFault(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }

    public NotAuthorisedFault(String message) {
        super(message);
        this.message = message;
    }

    public NotAuthorisedFault(Throwable cause) {
        super(cause);
        this.message = cause.getMessage();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
