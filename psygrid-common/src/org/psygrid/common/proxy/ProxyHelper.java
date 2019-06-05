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

package org.psygrid.common.proxy;

import java.util.List;

import org.psygrid.collection.entry.remote.ProxySetting;

/**
 * @author Rob Harper
 *
 */
public class ProxyHelper {

	/**
	 * Convert a list of proxy settings from the old format (where
	 * the seroth item in the list was taken as the default) to
	 * the old format (where the default is explicitly defined)
	 * 
	 * @param proxies The proxy settings
	 * @return True if the proxy settings have been modified
	 */
	public static boolean convertProxies(List<ProxySetting> proxies){
		boolean modified = false;
		for ( int i=0, c=proxies.size(); i<c; i++ ){
			ProxySetting proxy = proxies.get(i);
			if ( null == proxy.getDefaultProxy() ){
				proxy.setDefaultProxy(Boolean.valueOf(0==i));
				modified = true;
			}
		}
		return modified;
	}
	
}
