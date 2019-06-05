package org.psygrid.common.strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringEditor implements IStringEditor{

	
	private final String originalString;
	private final Map<String, String> regexSubsetMap; //Map of editable subset(s). Key is name, value is regex that identifies the substring.
	private final Map<String, String> subsetValidationMap; //Map of validator for the subsets (optional).
	
	Map<String, String> subsetMap = new HashMap<String, String>(); //Map of new string values for subsets.
	
	protected StringEditor(String originalString, Map<String, String> regexSubsetMap, Map<String, String> subsetValidationMap){
		this.originalString = originalString;
		this.regexSubsetMap = regexSubsetMap;
		this.subsetValidationMap = subsetValidationMap;
	}

	public StringSubsetDefinition getOriginalSubset(String textDescription) throws IllegalArgumentException{
		
		String regex = regexSubsetMap.get(textDescription);
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(originalString);

		matcher.find(); //TODO - if this is false or if there's more than one match, throw an exception.
		
		return new StringSubsetDefinition(matcher.group(), matcher.start(), matcher.end());
	}

	public boolean setNewSubset(String textDescription, String newSubset) throws IllegalArgumentException {

		boolean validationResult = false;
	
		if(regexSubsetMap.get(textDescription) == null){
			throw new IllegalArgumentException();
		}
		
		String validationRegex = subsetValidationMap.get(textDescription);
		if(validationRegex != null){
			Pattern pattern = Pattern.compile(validationRegex);
			Matcher matcher = pattern.matcher(newSubset);
			boolean foundMatch = matcher.find();
			if(foundMatch){
				String group = matcher.group();
				int start = matcher.start();
				int end = matcher.end();
				int groupCount = matcher.groupCount();
				
				this.subsetMap.put(textDescription, newSubset);
				validationResult = true;
			}
		}else{
			subsetMap.put(textDescription, newSubset);
		}
		
		return validationResult;
	}

	public StringSubsetDefinition getNewSubset(String textDescription)throws IllegalArgumentException {
		
		String subset = subsetMap.get(textDescription);
		
		StringSubsetDefinition original = this.getOriginalSubset(textDescription);
		StringSubsetDefinition subsetDefn = new StringSubsetDefinition(subset, original.getLhDelimiter(), original.getRhDelimiter());
		
		return subsetDefn;
	}

	public String getNewString() throws IllegalArgumentException {
 		
		StringBuffer modifiedOriginalString = new StringBuffer(originalString);
		
		Set<String> keySet = regexSubsetMap.keySet();
		for(String key : keySet){
			String newSubset = subsetMap.get(key);
			if(newSubset != null){
				StringSubsetDefinition defn = getNewSubset(key);
				modifiedOriginalString.replace(defn.getLhDelimiter(), defn.getRhDelimiter(), defn.getSubset());
			}
		}
		
		return modifiedOriginalString.toString();
	}

	public String getOriginalString() {
		return originalString;
	}

	public List<String> getEditableSubsetKeys() {
		// TODO Auto-generated method stub
		List<String> keysList = new ArrayList<String>();
		Set<String> keys = regexSubsetMap.keySet();
		for(String s : keys){
			keysList.add(s);
		}
			
		return keysList;
	}
	


}
