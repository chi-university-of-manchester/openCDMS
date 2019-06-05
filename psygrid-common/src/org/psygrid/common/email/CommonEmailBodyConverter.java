package org.psygrid.common.email;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonEmailBodyConverter {
	
	public static String substituteParamsIntoEmailBody(String body, Map<String,String> params){
		
		for(String parameter: params.keySet()) {
			 // Compile regular expression
            Pattern pattern = Pattern.compile(parameter);
    
            // Replace all occurrences of the parameter in the body of the email
            Matcher matcher = pattern.matcher(body);
            body = matcher.replaceAll(params.get(parameter));
		}
		return body;
	}

}
