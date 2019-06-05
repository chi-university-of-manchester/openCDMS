package org.psygrid.common.strings;

import java.util.Map;

public class StringEditorFactory {
	
	public static IStringEditor generateStringEditor(String originalString, Map<String, String> regexSubsetMap, Map<String, String> subsetValidationMap){
	
		return new StringEditor(originalString, regexSubsetMap, subsetValidationMap);
	}

}
