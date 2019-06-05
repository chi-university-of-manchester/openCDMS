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


//Created on Apr 21, 2006 by John Ainsworth
package org.psygrid.security.authentication.client;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.rpc.Stub;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.security.ConfigurableSecureServiceClient;
import org.psygrid.security.PGSecurityException;
import org.psygrid.security.authentication.service.Login;
import org.psygrid.security.authentication.service.LoginLocator;
import org.psygrid.security.authentication.service.LoginServicePortType;
import org.psygrid.security.utils.PropertyUtilities;

/**
 * @author jda
 *
 */
public class LoginClient extends ConfigurableSecureServiceClient {

	/**
	 * logger
	 */
	private static Log log = LogFactory.getLog(LoginClient.class);

	private LoginServicePortType portType = null;

	public LoginClient(String propFilename) throws PGSecurityException {

		if (propFilename == null) {
			throw new PGSecurityException("properties cannot be null");
		}

		properties = PropertyUtilities.getProperties(propFilename);

		setSecurityProperties(
				properties
						.getProperty("org.psygrid.security.authentication.client.trustStoreLocation"),
				properties
						.getProperty("org.psygrid.security.authentication.client.trustStorePassword"),
				properties
						.getProperty("org.psygrid.security.authentication.client.keyStoreLocation"),
				properties
						.getProperty("org.psygrid.security.authentication.client.keyStorePassword"));
		try {
			url = new URL(
					properties
							.getProperty("org.psygrid.security.authentication.client.serviceURL"));
		} catch (MalformedURLException mue) {
			mue.printStackTrace();
			log.fatal(mue.getMessage());
			throw new PGSecurityException(mue.getMessage());
		}

		Login xService = new LoginLocator();
		try {
			portType = xService.getLoginServicePortTypeSOAPPort();
		} catch (Exception e) {
			e.printStackTrace();
			log.fatal(e.getMessage());
			throw new PGSecurityException(e.getMessage());
		}
		this.setEndpointAddress(url.toString());
	}

	/**
	 * Get instance of the login client to access the service at the
	 * specified endpoint address.
	 * 
	 * @param pServiceEndpointAddress
	 *            The URL of the service to access (e.g. http://<service
	 *            address>:<port>/<path>).
	 * @param pTrustStoreLocation
	 *            The complete path to the CA keystore.
	 * @param pTrustStorePassword
	 *            The password to access to the CA keystore.
	 * @return The single instance of the Test client.
	 */
	public LoginClient(String pServiceEndpointAddress,
			String pTrustStoreLocation, String pTrustStorePassword)
			throws PGSecurityException {

		setSecurityProperties(pTrustStoreLocation, pTrustStorePassword, null,
				null);

		Login xService = new LoginLocator();
		try {
			portType = xService.getLoginServicePortTypeSOAPPort();
		} catch (Exception e) {
			e.printStackTrace();
			log.fatal(e.getMessage());
			throw new PGSecurityException(e.getMessage());
		}
		setEndpointAddress(pServiceEndpointAddress);
	}

	public Stub getStub() {
		return (javax.xml.rpc.Stub) portType;
	}

	public LoginServicePortType getPort() {
		return portType;
	}

}
