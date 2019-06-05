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

import java.util.Properties;

import java.net.URL;

/**
 * Utils class to fetch the Strings from the properties file. 
 *  @author pwhelan
 */
public class PropertiesHelper
{
	private static final String PROPERTY_STRINGS = "org/psygrid/securitymanager/properties/Strings.properties";
	
	private Properties propertyStrings;
	
	private static PropertiesHelper propertyHelperSingleton;
	
	public PropertiesHelper()
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
	
	//singleton static reference
	public static PropertiesHelper getPropertyHelper()
	{
		if (propertyHelperSingleton == null)
		{
			propertyHelperSingleton = new PropertiesHelper();
		}
		return propertyHelperSingleton;
	}
	
	public String getStringFor(String key)
	{
		return getPropertyHelper().propertyStrings.getProperty(key);
	}
	
	
}