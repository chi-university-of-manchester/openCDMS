package org.psygrid.dataimport.identifier;

import org.psygrid.dataimport.identifier.AbstractParser.InfoType;

public class RangeTranslation extends Translation {

	private final int lowerInclusive;
	private final int upperInclusive;
	private final String translationValue;
	
	public RangeTranslation(InfoType infoType, int lowerInclusive, int upperInclusive, String translationValue) {
		super(infoType);
		this.lowerInclusive = lowerInclusive;
		this.upperInclusive = upperInclusive;
		this.translationValue = translationValue;
	}

	@Override
	protected String getTranslation(String infoToTranslate, InfoType inputInfoType) throws TranslationException {
		
		if(inputInfoType != this.requiredInfoTypeForTranslation){
			throw new TranslationException();
		}
		
		int value = Integer.valueOf(infoToTranslate);
		String returnValue = null;
		if(value >= lowerInclusive && value <= upperInclusive){
			returnValue = translationValue;
		}
		
		return returnValue;
	}

}
