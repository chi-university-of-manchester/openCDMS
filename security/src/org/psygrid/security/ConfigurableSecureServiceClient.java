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


//Created on Nov 11, 2005 by John Ainsworth



package org.psygrid.security;

import java.net.URL;
import java.util.Properties;

/**
 * @author jda
 *
 */
public abstract class ConfigurableSecureServiceClient {

	public static final String DS_NAMESPACE = "http://www.w3.org/2000/09/xmldsig#";
	protected String _trustStoreLocation = "trust.jks";
	protected String _trustStorePassword = "password";
	protected String _keyStoreLocation = "client.jks";
	protected String _keyStorePassword = "password";
	protected Properties properties = null;
	protected URL url = null;

	/**
	 * Set the endpoint address of the service to access. (Accessed through
	 * getInstance).
	 * Set the security properties if not already set and if the transport 
	 * requires them. This is done by checking to see if the address starts with https
	 * If they are already set do nothing.
	 * @param pAddress
	 *            Service endpoint address.
	 */

	protected void setEndpointAddress(String pAddress) {
		// Set the address of the service endpoint
		getStub()._setProperty(
				javax.xml.rpc.Stub.ENDPOINT_ADDRESS_PROPERTY, pAddress);
		// If service address begins with https then secure connection required
		// so set up the system properties for the user and CA keystores.
		// Do not change them if already set.
		if (pAddress.startsWith("https")) {
			if(System.getProperty("javax.net.ssl.trustStore")==null){
				if (_trustStoreLocation != null)
					System.setProperty("javax.net.ssl.trustStore",
							_trustStoreLocation);
			} 
			if(System.getProperty("javax.net.ssl.trustStorePassword")==null){
				if (_trustStorePassword != null)
					System.setProperty("javax.net.ssl.trustStorePassword",
							_trustStorePassword);
			} 
			if(System.getProperty("javax.net.ssl.keyStore")==null){
				if (_keyStoreLocation != null) {
					System.setProperty("javax.net.ssl.keyStore", _keyStoreLocation);
				}
			} 
			if(System.getProperty("javax.net.ssl.keyStorePassword")==null){
				if (_keyStorePassword != null) {
					System.setProperty("javax.net.ssl.keyStorePassword",
							_keyStorePassword);
				}
			} 
			//System.setProperty("javax.net.debug", "ssl,handshake");
		}
	}
	
	
	/**
	 * @return javax.xml.rpc.Stub The stub of the service
	 */
	abstract protected javax.xml.rpc.Stub getStub();
	
	
	 /**
	 * Set the local copies of the security properties.
	 * @param trustStoreLocation
	 * @param trustStorePassword
	 * @param keyStoreLocation
	 * @param keyStorePassword
	 */
	protected void setSecurityProperties(String trustStoreLocation,
			String trustStorePassword,
			String keyStoreLocation,
			String keyStorePassword){
		_trustStoreLocation = trustStoreLocation;
		_trustStorePassword = trustStorePassword;
		_keyStoreLocation = keyStoreLocation;
		_keyStorePassword = keyStorePassword;
	}
}
