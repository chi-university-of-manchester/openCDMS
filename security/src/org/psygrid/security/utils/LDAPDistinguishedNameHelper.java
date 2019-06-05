package org.psygrid.security.utils;

public class LDAPDistinguishedNameHelper {
	
	public static String whitespaceFormatDistinguishedName(String distinguishedName){
		
StringBuffer userNameBuffer =  new StringBuffer(distinguishedName);
		
		int start = 0;
		while (userNameBuffer.indexOf(",", start) != -1)
		{
			start = userNameBuffer.indexOf(",", start)+1;
			
			try
			{
				if (userNameBuffer.charAt(start) != ' ')
				{
					userNameBuffer.insert(start, ' ');
				}
			} catch (Exception ex)
			{
				//if out of range, ignore
			}
		}

		return userNameBuffer.toString();

	}

}
