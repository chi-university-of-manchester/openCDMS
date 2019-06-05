package org.psygrid.dataimport.identifier;

public class TranslationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2801050601618195258L;

	public TranslationException(){
		super();
	}
	
	public TranslationException(String exceptionDescription){
		super(exceptionDescription);
	}
	
	public TranslationException(String exceptionDescription, Throwable exception){
		super(exceptionDescription, exception);
	}

}
