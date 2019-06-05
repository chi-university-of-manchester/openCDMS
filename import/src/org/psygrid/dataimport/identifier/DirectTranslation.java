package org.psygrid.dataimport.identifier;

import org.psygrid.dataimport.identifier.AbstractParser.InfoType;

public class DirectTranslation extends Translation {

	private final String input;
	private final String output;
	
	public DirectTranslation(InfoType infoType, InfoType requiredInfoType, String output, String input) {
		super(infoType, requiredInfoType);
		
		this.input = input;
		this.output = output;
	}

	@Override
	protected String getTranslation(String infoToTranslate,
			InfoType inputInfoType) throws TranslationException {
		
		String output = null;
		
		if(inputInfoType != this.requiredInfoTypeForTranslation){
			throw new TranslationException();
		}
		
		if(infoToTranslate.equals(input)){
			output = this.output;
		}
		
		return output;
	}

}
