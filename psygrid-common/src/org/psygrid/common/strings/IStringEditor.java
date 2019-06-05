package org.psygrid.common.strings;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.Map;

public interface IStringEditor {
	
	
	/**
	 * Sets the string that is to be edited
	 * @param s
	 */
	//public void setOriginalString(String s);
	
	/**
	 * This method sets a map of regex expressions specifing the parts of the
	 * original string that can be modified. The key is a text description
	 * describing of the subset, and the value is the regex.
	 * 
	 * the string that can be edited
	 * @param changeableSubsetMap - text description of the subset is the key, and the regex identifying
	 * 		the subset is the value.
	 */
	//public void setRegexMap(Map<String, String> changeableSubsetMap);
	
	
	/**
	 * For each editable substring, this method allows the setting of a 'validation' regex.
	 * The text description of the substring must be the same as that provided in the
	 * 'setRegexMap' method.
	 * 
	 * @param subsetValidationRules - text description of the subset is the key, and the validation regex
	 * 		is the value. Text description must be identical to that provided in the 'setRegexMap' method.
	 */
	//public void setSubsetValidationRules(Map<String, String> subsetValidationRules);
	
	
	/**
	 * This returns the subset of the original string as identified by the text desciption. If
	 * @param textDescription
	 * @return - returns the substring as identified by the text description.
	 */
	public StringSubsetDefinition getOriginalSubset(String textDescription) throws IllegalArgumentException;
	
	/**
	 * Allows the new subset to be set. The description must match one already provided.
	 * @param textDescription
	 * @param newSubset
	 * @return - returns false if textDescription does not match the currently-held map. returns false
	 * 	if the newSubset String is null. Otherwise, returns true.
	 */
	public boolean setNewSubset(String textDescription, String newSubset) throws IllegalArgumentException;
	
	public StringSubsetDefinition getNewSubset(String textDescription) throws IllegalArgumentException;
	
	
	/**
	 * Gets the new string with all of the specified substring replacements.
	 * @return
	 */
	public String getNewString() throws IllegalArgumentException;
	
	public String getOriginalString();
	
	public List<String> getEditableSubsetKeys();
	

}
