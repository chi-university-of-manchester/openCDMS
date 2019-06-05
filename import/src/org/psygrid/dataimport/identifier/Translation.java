package org.psygrid.dataimport.identifier;

import org.psygrid.dataimport.identifier.AbstractParser.InfoType;

public abstract class Translation {

	
	protected final InfoType type;
	protected final InfoType requiredInfoTypeForTranslation;
	
	/**
	 * This constructor is used if there is to be a direct translation on the specified info type i.e. all the information required to do the
	 * translation is contained within the manifestation of the info type itself.
	 * @param infoType
	 */
	public Translation(InfoType infoType){
		this.type = infoType;
		requiredInfoTypeForTranslation = infoType;
	}
	
	/**
	 * This constructor is used if there is to be an indirect translation on the specified info type e.g. the project code is the info being returned, but
	 * the translation is executed based on a different InfoType, such as centre information.
	 * @param infoType
	 * @param requiredInfoType
	 */
	public Translation(InfoType infoType, InfoType requiredInfoType){
		this.type = infoType;
		this.requiredInfoTypeForTranslation = requiredInfoType;
	}
	

	public String getTranslation(InfoType returnInfoType, InfoType inputInfoType, String infoToTranslate) throws TranslationException{
		if (returnInfoType != this.type){
			return null;
		}
		
		return getTranslation(infoToTranslate, inputInfoType);
	}
	
	protected abstract String getTranslation(String infoToTranslate, InfoType inputInfoType) throws TranslationException;

	public InfoType getRequiredInfoTypeForTranslation() {
		return requiredInfoTypeForTranslation;
	}
	
	public InfoType getInfoType(){
		return type;
	}
}
