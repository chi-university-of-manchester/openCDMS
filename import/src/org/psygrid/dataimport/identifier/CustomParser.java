package org.psygrid.dataimport.identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.psygrid.data.model.hibernate.Identifier;

public class CustomParser extends AbstractParser {
	
	private final List<IdentifierInfo> info;
	private final Map<InfoType, Object> implicitInfo;
	private InfoTranslator translator = null;
	

	/**
	 * The constructor checks to see whether it has been given a full complement of information regarding
	 * all of the InfoType enum members across 'info' and 'implicitInfo'. If not all members have been defined, or
	 * there are duplicate entries, then IllegalArumentException is thrown.
	 * @param projectCode
	 * @param suffixSize
	 * @param info - a list of IdentifierInfo objects (can be null if all info is implicit within the idendifier)
	 * @param implicitInfo a map of any info that is not explicitly stated within the external identifiers (can be null if all info is explicit within the identifier)
	 * 				As an example, consider an identifier that embeds no centre information because it is a one centre study.
	 */
	public CustomParser(String projectCode, int suffixSize, List<IdentifierInfo> info, Map<InfoType, Object> implicitInfo) throws IllegalArgumentException{
		super(projectCode, suffixSize);
		
		if(info != null && info.size() == 0){
			throw new IllegalArgumentException("The 'info' list passed into CustomParser constructor cannot be empty.");
		}
		
		if(implicitInfo != null && implicitInfo.size() == 0){
			throw new IllegalArgumentException("The 'implicitInfo' map passed into CustomParser constructor cannot be empty.");
		}
		
		//TODO - check all InfoTypes are all represented, and only once, across info & implicitInfo. If not, throw exception.
		this.info = info;
		this.implicitInfo = implicitInfo;

	}
	
	public CustomParser(List<String> projectCodes, int suffixSize, List<IdentifierInfo> info, Map<InfoType, Object> implicitInfo) throws IllegalArgumentException{
		super(projectCodes, suffixSize);
		
		if(info != null && info.size() == 0){
			throw new IllegalArgumentException("The 'info' list passed into CustomParser constructor cannot be empty.");
		}
		
		if(implicitInfo != null && implicitInfo.size() == 0){
			throw new IllegalArgumentException("The 'implicitInfo' map passed into CustomParser constructor cannot be empty.");
		}
		
		//TODO - check all InfoTypes are all represented, and only once, across info & implicitInfo. If not, throw exception.
		this.info = info;
		this.implicitInfo = implicitInfo;
		
	}
	
	
	/**
	 * Gets the identifier info and returns it as a type object.
	 * The caller must then convert this to the appropriate type depending on the type of requested 
	 * information.
	 * 
	 * @param type
	 * @param identifier
	 * @return
	 * @throws DelimeterNotFoundException 
	 */
	public Object getIdentifierInfo(InfoType type, String identifier) throws DelimeterNotFoundException{
		
		IdentifierInfo match = null;
		for(IdentifierInfo idInfo: info){
			if(idInfo.getInformationType().equals(type)){
				match = idInfo;
				break;
			}
		}
		
		if(match != null){
			return match.getInfo(identifier);
		}else
			return null;
		
	}

	@Override
	public Identifier createIdentifier(String input) throws ParserException, DelimeterNotFoundException, TranslationException {
		
		Map<InfoType, Object> valuesList = new HashMap<InfoType, Object>(); //A map containing the extracted values.
				
		if(implicitInfo != null){
			Set<InfoType> keySet = implicitInfo.keySet();
			for(InfoType type: keySet){
				Object value = implicitInfo.get(type);
				valuesList.put(type, value);
			}
		}
		
		if(info != null){
			for(IdentifierInfo idInfo: info){
				Object value = idInfo.getInfo(input);
				valuesList.put(idInfo.getInformationType(), value);
			}
		}
		
		//All identifier data has been gathered now - so it is time to assemble it.
		return assembleIdentifier(valuesList);
	}
	
	private Identifier assembleIdentifier(Map<InfoType, Object> idInfo) throws TranslationException{
		
		String centreCode = null, participantId = null;
		
		int uniqueParticipantId = Integer.valueOf(((String)idInfo.get(InfoType.UNIQUE_STUDY_ID))).intValue();
		
		if(this.translator != null){
			participantId = this.translator.translate(InfoType.UNIQUE_STUDY_ID, InfoType.UNIQUE_STUDY_ID, String.valueOf(uniqueParticipantId));
			if(participantId != null){
				uniqueParticipantId = Integer.valueOf(participantId);
			}else{
				participantId = new Integer(uniqueParticipantId).toString();
			}
		}else{
			participantId = new Integer(uniqueParticipantId).toString();
		}
		
		String rawCentre = (String)idInfo.get(InfoType.CENTRE_INFO);
		
		if(this.translator != null){
			String centreTranslation = this.translator.translate(InfoType.CENTRE_INFO, InfoType.CENTRE_INFO, rawCentre);
			if(centreTranslation != null){
				centreCode = centreTranslation;
			}else{
				centreCode = rawCentre;
			}
		}else{
			centreCode = rawCentre;
		}
		
		Identifier psygridIdentifier = new Identifier();
		
		if(projectCode.size() == 1){
			psygridIdentifier.initialize(this.projectCode.get(0), centreCode, Integer.valueOf(participantId), this.suffixSize);	
		}else{
			//We need to determine what project code to use!
			//1) Find the translator for project code
			//2) Find what InfoType the translator uses (probably centre at this point).
			//3) Feed in the required info and get the translation.
		
			InfoType type = translator.getRequiredInputTypeForTranslation(InfoType.PROJECT); 
			String input = null;
			switch (type){
				case UNIQUE_STUDY_ID:
					input = participantId;
					break;
				case CENTRE_INFO:
					input = centreCode;
					break;
				case PROJECT:
					throw new TranslationException();
			}
			
			String project = translator.translate(InfoType.PROJECT, InfoType.CENTRE_INFO, input);
			
			//Now make sure that this is one of the projects known to the parser...
			boolean matchFound = false;
			for(String projectCode: this.projectCode){
				if(project.equals(projectCode)){
					matchFound = true;
					break;
				}
			}
			
			if(!matchFound){
				throw new TranslationException();
			}
			
			psygridIdentifier.initialize(project, centreCode, Integer.valueOf(participantId), this.suffixSize);
		}
	    
		
		
	    return psygridIdentifier;
	}

	public void setTranslator(InfoTranslator translator) {
		this.translator = translator;
	}

}
