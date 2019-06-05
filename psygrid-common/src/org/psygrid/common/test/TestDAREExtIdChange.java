package org.psygrid.common.test;

import java.util.HashMap;
import java.util.Map;

import org.psygrid.common.strings.IStringEditor;
import org.psygrid.common.strings.StringEditorFactory;
import org.psygrid.common.strings.StringSubsetDefinition;

public class TestDAREExtIdChange {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		//NWEL156YH
		
		//The regex that locates the editable substring is: [a-zA-Z]{2}$
		//The regex that validates the new substring is: ^[a-zA-Z]{2}$
		
		Map<String, String> regexMap = new HashMap<String, String>();
		regexMap.put("initials", "[a-zA-Z]{2}$");
		
		Map<String, String> subsetValidationMap = new HashMap<String, String>();
		subsetValidationMap.put("initials", "^[a-zA-Z]{2}$");
		
		IStringEditor editor = StringEditorFactory.generateStringEditor("NWEL156YH", regexMap, subsetValidationMap);
		
		System.out.println("The original string (from the editor) is : " + editor.getOriginalString());
		
		StringSubsetDefinition editableSubset = editor.getOriginalSubset("initials");
		
		System.out.println("The editable portion of: " + editor.getOriginalString() + " is " + editableSubset.getSubset() + " starting at " + editableSubset.getLhDelimiter() + " and ending at " + editableSubset.getRhDelimiter());
		
		boolean result = editor.setNewSubset("initials", "Y10");
		
		System.out.println("The result of trying to set the initials to " + "Y10" + " was " + (result ? "true" : "false"));
		
		result = editor.setNewSubset("initials", "PP");
		
		System.out.println("The result of trying to set the initials to " + "WV" + " was " + (result ? "true" : "false"));
		
		String newString = editor.getNewString();
		
		System.out.println("The edited string is: " + newString);

	}

}
