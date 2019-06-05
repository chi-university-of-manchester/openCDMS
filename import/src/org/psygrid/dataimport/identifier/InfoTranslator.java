package org.psygrid.dataimport.identifier;

import java.util.List;

import org.psygrid.dataimport.identifier.AbstractParser.InfoType;


/**
 * This class is a collection of Translators for a particular value and value type.
 * For example, it may hold a translator for CENTRE info - if the centre info is 1005, it may
 * hold a translator in its collection that will translate this value to something else.
 * 
 * It is possible to instantiate the object with its collection of translations, or to add translators to the
 * collection at a later time.
 * 
 * @author williamvance
 *
 */
public class InfoTranslator {

	private List<Translation> translators = null;
	
	public InfoTranslator (List<Translation> translators){
		this.translators = translators;
	}
	
	public InfoType getRequiredInputTypeForTranslation(InfoType typeToTranslate){
		
		InfoType requiredInput = null;
		
		for(Translation t: translators){
			InfoType translatorType = t.getInfoType();
			InfoType requiredInputType = t.getRequiredInfoTypeForTranslation();
			
			if(typeToTranslate.equals(translatorType)){
				requiredInput = requiredInputType;
				break;
			}
			
		}
		
		return requiredInput;
		
	}

	/**
	 * Translates the input.
	 * @param info
	 * @return - retuns null if there is no translation available.
	 * @throws TranslationException 
	 */
	public String translate(InfoType type, InfoType inputInfoType, String info) throws TranslationException{
		
		String translation = null;
		
		for(Translation tr : translators){
			translation = tr.getTranslation(type, inputInfoType, info);
			if(translation != null){
				break;
			}
		}
		return translation;
	}
}
