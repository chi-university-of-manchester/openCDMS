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

package org.psygrid.datasetdesigner.utils;

import java.util.Properties;

import org.psygrid.collection.entry.persistence.PersistenceManager;

import java.net.URL;


/**
 * Utils class to fetch the Strings from the properties file. 
 *  @author pwhelan
 */
public class PropertiesHelper
{
	private static final String PROPERTY_STRINGS = "org/psygrid/datasetdesigner/properties/Strings.properties";
	
	private static final String SERVICES_STRINGS = "org/psygrid/datasetdesigner/properties/services.properties";
	
	private static final String DATASETS_STRING ="dsdatasets.xml";
	
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
	
	public static Properties getServices() {
        Properties properties = new Properties();
        try {
            URL url = Thread.currentThread().getContextClassLoader()
	          .getResource(SERVICES_STRINGS);
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
	
	public static String getStringFor(String key)
	{
		return getPropertyHelper().propertyStrings.getProperty(key);
	}
	
	
	public static String getDataSetsLocation() {
		return PersistenceManager.getInstance().getUserDirLocation() + DATASETS_STRING;
	}
	
	public static String getDELAuthRolesLocation() {
		return PersistenceManager.getInstance().getUserDirLocation() + "del-roles.xml";
	}
	
	public static String getDELAuthoritiesLocation() {
		return PersistenceManager.getInstance().getUserDirLocation() + "del-auths.xml";
	}

	public static String getDELTypesLocation() {
		return PersistenceManager.getInstance().getUserDirLocation() + "del-elements.xml";
	}
	
	public static String getRandomizationLocation() {
		String randomizationURL = getServices().getProperty("randomization.url");
		return randomizationURL + "/randomization/services/randomization";
	}

	public static String getTransformersLocation() {
		return getServices().getProperty("transformers.url");

	}

	
	public static String getESLLocation() {
		String eslURL = getServices().getProperty("esl.url");
		return eslURL + "/esl/services/esl";
	}
	
}