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

package org.psygrid.common.remote;

import org.psygrid.common.proxy.ProxyAuthenticationMethods;

/**
 * Interface defining methods that all Remote Managers must
 * implement.
 * 
 * @author Rob Harper
 *
 */
public interface RemoteManageable {

	/**
	 * Checks to see if a network connection is available to 
	 * the central PsyGrid servers.
	 * 
	 * @return Boolean, True if a connection is available.
	 */
	public boolean isConnectionAvailable();

	/**
	 * Checks to see if a network connection is available to 
	 * the central PsyGrid servers.
	 * 
	 * @param log Boolean, if True write messages to the log.
	 * @return Boolean, True if a connection is available.
	 */
	public boolean isConnectionAvailable(boolean log);
	
	/**
	 * Configure the proxy authentication settings for the socket
	 * factory.
	 * 
	 * @param authScheme The authentication scheme.
	 * @param ntDomain The NT domain
	 */
	public void configureProxyAuthentication(ProxyAuthenticationMethods authScheme, String ntDomain);
	
	/**
	 * Get the proxy authentication scheme setting from the socket 
	 * factory.
	 * 
	 * @return The proxy authentications scheme.
	 */
	public ProxyAuthenticationMethods getProxyAuthenticationMethod();
	
	/**
	 * Get the NT domain setting from the socket factory.
	 * 
	 * @return The NT domain.
	 */
	public String getNtDomain();
	
}
