package org.psygrid.meds.utils;

public class LdapParser {
	
    public static String getFirstName(String cnName) {
        if (cnName != null && cnName.toUpperCase().startsWith("CN=")) {
            cnName = cnName.substring(3);
        }
        int position = cnName.indexOf(',');
        
        String fullName = null;
        
        if (position == -1) {
            fullName = cnName;
        } else {
            fullName = cnName.substring(0, position);
        }
        
        String firstName = null;
        
        int spacePos = fullName.indexOf(' ');
        firstName = fullName.substring(0, spacePos);
        
        return firstName;
    }
    
    public static String getSurname(String cnName) {
        if (cnName != null && cnName.toUpperCase().startsWith("CN=")) {
            cnName = cnName.substring(3);
        }
        int position = cnName.indexOf(',');
        
        String fullName = null;
        
        if (position == -1) {
            fullName = cnName;
        } else {
            fullName = cnName.substring(0, position);
        }
        
        String lastName = null;
        
        int spacePos = fullName.indexOf(' ');
        lastName = fullName.substring(spacePos+1);
        
        return lastName;
    }

}
