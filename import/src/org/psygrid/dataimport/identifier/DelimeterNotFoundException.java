package org.psygrid.dataimport.identifier;

public class DelimeterNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4424632760417923577L;
	
	public DelimeterNotFoundException(){
		super();
	}
	
	public DelimeterNotFoundException(String exceptionDescription){
		super(exceptionDescription);
	}
	
	public DelimeterNotFoundException(String exceptionDescription, Throwable exception){
		super(exceptionDescription, exception);
	}

}
