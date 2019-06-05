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
import javax.swing.ImageIcon;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utility class to fetch the icons.
 *  
 * @author pwhelan
 */
public class IconsHelper 
{
    private static final String PACKAGE = "org/psygrid/securitymanager/icons"; 
    private static IconsHelper _iconsHelper;
    
    private static final Log LOG = LogFactory.getLog(IconsHelper.class);
    
    public static IconsHelper getInstance()
    {
    	if (_iconsHelper == null)
    	{
    		_iconsHelper = new IconsHelper();
    	}
    	return _iconsHelper;
    }
	
	public ImageIcon getImageIcon(String iconName)
	{
		ImageIcon icon = null;
		try
		{
			URL resourceURL = Thread.currentThread().getContextClassLoader()
			.getResource(PACKAGE + "/" + iconName);
			icon = new ImageIcon(resourceURL);
		} catch (Exception ex)
		{
			LOG.error("IconsHelper: error fetching icon" + iconName);
		}
		return icon;
	}

}