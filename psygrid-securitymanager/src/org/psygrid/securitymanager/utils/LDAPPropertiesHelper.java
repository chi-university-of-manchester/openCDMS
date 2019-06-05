/*
Copyright (c) 2006-2008, The University of Manchester, UK.

This file is part of PsyGrid.

PsyGrid is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as 
published by the Free Software Foundation, either version 3 of 
the License, or (at your option) any later version.

PsyGrid is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public 
License along with PsyGrid.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.psygrid.securitymanager.utils;

import java.net.URL;
import java.util.Properties;


public class LDAPPropertiesHelper 
{
	private static final String PROPERTY_STRINGS = "org/psygrid/securitymanager/properties/ldap.properties";
	private static final String LDAP_URL = "org.psygrid.securitymanager.ldapDirectoryURL";
	private static final String LDAP_ROOT = "org.psygrid.securitymanager.ldaproot";
	private static final String LDAP_PASSWORD = "org.psygrid.securitymanager.ldappassword";
	private static final String DN_SUFFIX = "org.psygrid.securitymanager.dnsuffix";
	private static final String BASE_DN = "org.psygrid.securitymanager.baseDN";
	private static final String USERBASE_DN = "org.psygrid.securitymanager.userBaseDN";
	
	private Properties propertyStrings;
	
	private static LDAPPropertiesHelper propertyHelperSingleton;
	
	public LDAPPropertiesHelper()
	{
		propertyStrings = getProperties();
	}
	
	public Properties getProperties()
	{
        Properties properties = new Properties();
        try {
            URL url = Thread.currentThread().getContextClassLoader()
	          .getResource(PROPERTY_STRINGS);
            properties.load(url.openStream());
        } catch (Exception e) {
            throw new RuntimeException("PropertiesHelper: Cannot load properties: ");
        }
        return properties;
	}
	
	//singlteton static reference
	public static LDAPPropertiesHelper getPropertyHelper()
	{
		if (propertyHelperSingleton == null)
		{
			propertyHelperSingleton = new LDAPPropertiesHelper();
		}
		return propertyHelperSingleton;
	}

	public String getLDAPURL()
	{
		return propertyStrings.getProperty(LDAP_URL);
	}
	
	public String getLDAPRoot()
	{
		return propertyStrings.getProperty(LDAP_ROOT);
	}
	
	public String getLDAPPassword()
	{
		return propertyStrings.getProperty(LDAP_PASSWORD);
	}
	
	public String getDNSuffix()
	{
		return propertyStrings.getProperty(DN_SUFFIX);
	}
	
	public String getBaseDN()
	{
		return propertyStrings.getProperty(BASE_DN);
	}
	
	/**
	 * Sometimes base is specified without spaces after comma's 
	 * AA won't find the user then.
	 * @return
	 */
	public String getUserBaseDN()
	{
		StringBuffer userBaseString =  new StringBuffer(propertyStrings.getProperty(USERBASE_DN));
		
		int start = 0;
		while (userBaseString.indexOf(",", start) != -1)
		{
			start = userBaseString.indexOf(",", start)+1;
			
			try
			{
				if (userBaseString.charAt(start) != ' ')
				{
					userBaseString.insert(start, ' ');
				}
			} catch (Exception ex)
			{
				//if out of range, ignore
			}
		}
		return ", " + userBaseString.toString();
	}
	
}