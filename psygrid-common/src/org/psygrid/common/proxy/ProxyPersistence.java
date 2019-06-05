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

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.psygrid.collection.entry.remote.ProxySetting;

/**
 * Interface to define methods for proxy configuration that must
 * be implemented by all persistence managers.
 * 
 * @author Rob Harper
 *
 */
public interface ProxyPersistence {

	/**
	 * Load the proxy settings from file.
	 * 
	 * @return The proxy settings.
	 * @throws IOException
	 */
	public List<ProxySetting> loadProxySettings() throws IOException;

	/**
	 * Save the proxy settings to file.
	 * 
	 * @param proxySettings The proxy settings.
	 * @throws IOException
	 */
	public void saveProxySettings(List<ProxySetting> proxySettings) throws IOException;
	
	/**
	 * Import the proxy settings from the given file.
	 * 
	 * @param fromFile The file to import the proxy settings from.
	 * @throws IOException
	 */
	public void importProxySettingsFile(File fromFile) throws IOException;
	
	/**
	 * Export the proxy settings to the given file, just by copying
	 * the proxy settings as they currently are on disk.
	 * 
	 * @param toFile The file to export the proxy settings to.
	 * @throws IOException
	 */
	public void exportProxySettingsFile(File toFile) throws IOException;

	/**
	 * Export the given proxy settings to the given file. The proxy 
	 * settings in the argument are exported, not those on disk.
	 * 
	 * @param toFile The file to export the proxy settings to.
	 * @param proxies The proxy settings to export.
	 * @throws IOException
	 */
	public void exportProxySettingsFile(File toFile, List<ProxySetting> proxies) throws IOException;

	/**
	 * Check a file to see if it contains a valid proxy settings.
	 * 
	 * @param file
	 * @return Boolean True if OK, False if not.
	 */
	public boolean checkProxySettings(File file);
	
}
