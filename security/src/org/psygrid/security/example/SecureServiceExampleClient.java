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


//Created on Oct 17, 2005 by John Ainsworth

package org.psygrid.security.example;

import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Properties;

import org.apache.axis.EngineConfiguration;
import org.apache.axis.configuration.FileProvider;
import org.apache.axis.utils.Options;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opensaml.SAMLAssertion;
import org.psygrid.security.attributeauthority.client.AASAMLRequestor;
import org.psygrid.security.example.service.SecureServiceExamplePortType;
import org.psygrid.security.example.service.SecureServiceExampleService;
import org.psygrid.security.example.service.SecureServiceExampleServiceLocator;
import org.psygrid.security.utils.PropertyUtilities;
import org.psygrid.www.xml.security.core.types.ProjectType;

/**
 * @author jda
 *
 */
public class SecureServiceExampleClient {

	/**
	 * logger
	 */
	private static Log _log = LogFactory
			.getLog(SecureServiceExampleClient.class);

	private static String _trustStoreLocation = "trust.jks";

	private static String _trustStorePassword = "password";

	private static String _keyStoreLocation = "client.jks";

	private static String _keyStorePassword = "password";

	/**
	 * The singleton instance of this class.
	 */
	protected static SecureServiceExampleClient _instance = null;

	/**
	 * The service port for calling methods on the Test web service.
	 */
	protected SecureServiceExamplePortType _port = null;

	/**
	 * Protected constructor for gaining access to the Test port.
	 */
	protected SecureServiceExampleClient(EngineConfiguration config)
			throws Exception {
		SecureServiceExampleService xService = new SecureServiceExampleServiceLocator(
				config);
		try {
			_port = xService.getSecureServiceExamplePortTypeSOAPPort();
		} catch (Exception e) {
			throw new Exception("Error getting AA port from service.", e);
		}
	}

	/**
	 * Get instance of the Test client using the default service endpoint address.
	 *
	 * @param pTrustStoreLocation The complete path to the CA keystore.
	 * @param pTrustStorePassword The password to access to the CA keystore.
	 * @param pKeyStoreLocation The complete path to the user's keystore.
	 * @param pKeyStorePassword The password to access the user's keystore.
	 * @return The single instance of the Test client.
	 */
	public static SecureServiceExampleClient getInstance(
			EngineConfiguration config, String pTrustStoreLocation,
			String pTrustStorePassword, String pKeyStoreLocation,
			String pKeyStorePassword) throws Exception {

		_trustStoreLocation = pTrustStoreLocation;
		_trustStorePassword = pTrustStorePassword;
		_keyStoreLocation = pKeyStoreLocation;
		_keyStorePassword = pKeyStorePassword;

		if (_instance == null)
			_instance = new SecureServiceExampleClient(config);
		return _instance;
	}

	/**
	 * Get instance of the Test client to access the AA service
	 * at the specified endpoint address.
	 *
	 * @param pServiceEndpointAddress The URL of the service to access (e.g. http://<service address>:<port>/<path>).
	 * @return The single instance of the Test client.
	 */
	public static SecureServiceExampleClient getInstance(
			EngineConfiguration config, String pServiceEndpointAddress)
			throws Exception {
		return getInstance(config, pServiceEndpointAddress, null, null, null,
				null);
	}

	/**
	 * Get instance of the Test client to access the RUS service
	 * at the specified endpoint address.
	 *
	 * @param pServiceEndpointAddress The URL of the service to access (e.g. http://<service address>:<port>/<path>).
	 * @param pTrustStoreLocation The complete path to the CA keystore.
	 * @param pTrustStorePassword The password to access to the CA keystore.
	 * @param pKeyStoreLocation The complete path to the user's keystore.
	 * @param pKeyStorePassword The password to access the user's keystore.
	 * @return The single instance of the Test client.
	 */
	public static SecureServiceExampleClient getInstance(
			EngineConfiguration config, String pServiceEndpointAddress,
			String pTrustStoreLocation, String pTrustStorePassword,
			String pKeyStoreLocation, String pKeyStorePassword)
			throws Exception {

		_trustStoreLocation = pTrustStoreLocation;
		_trustStorePassword = pTrustStorePassword;
		_keyStoreLocation = pKeyStoreLocation;
		_keyStorePassword = pKeyStorePassword;

		if (_instance == null)
			_instance = new SecureServiceExampleClient(config);
		_instance.setEndpointAddress(pServiceEndpointAddress);
		return _instance;
	}

	/**
	 * Set the endpoint address of the service to access. (Accessed through getInstance).
	 *
	 * @param pAddress Service endpoint address.
	 */
	private void setEndpointAddress(String pAddress) {
		// Set the address of the service endpoint
		((javax.xml.rpc.Stub) _port)._setProperty(
				javax.xml.rpc.Stub.ENDPOINT_ADDRESS_PROPERTY, pAddress);
		// If service address begins with https then secure connection required
		// so set up the system properties for the user and CA keystores.
		if (pAddress.startsWith("https")) {
			if (_trustStoreLocation != null)
				System.setProperty("javax.net.ssl.trustStore",
						_trustStoreLocation);
			if (_trustStorePassword != null)
				System.setProperty("javax.net.ssl.trustStorePassword",
						_trustStorePassword);
			if (_keyStoreLocation != null) {
				System.setProperty("javax.net.ssl.keyStore", _keyStoreLocation);
			}
			if (_keyStorePassword != null) {
				System.setProperty("javax.net.ssl.keyStorePassword",
						_keyStorePassword);
			}
			//System.setProperty("javax.net.debug", "ssl,handshake");
		}
	}

	/**
	 * @return
	 */
	public SecureServiceExamplePortType getPort() {
		return _port;
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		Options opts = new Options(args);

		EngineConfiguration config = new FileProvider(
				"../etc/secure-service-example/client-deploy.wsdd");

		SecureServiceExampleService abs = new SecureServiceExampleServiceLocator(
				config);

		SecureServiceExamplePortType ss1 = null;

		opts.setDefaultURL(abs.getSecureServiceExamplePortTypeSOAPPortAddress());
		System.out.println(opts.getURL());
		URL serviceURL = new URL(opts.getURL());

		SecureServiceExampleClient ssec;
		try { 
			ssec = SecureServiceExampleClient.getInstance(config, serviceURL
					.toString(), "../etc/keystores/trust.jks", "password", "../etc/keystores/client.jks",
					"password");
			ss1 = ssec.getPort();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Properties prop = PropertyUtilities.getProperties("saml.properties");
		AASAMLRequestor aasii = new AASAMLRequestor(prop);
		SAMLAssertion sa = aasii.newAssertion("HelenRoberts", new ProjectType("PsyGridFEPProject", "fep", null, null, false));
		Iterator x509 = sa.getX509Certificates();
		X509Certificate cert = (X509Certificate)x509.next();
		System.out.println(cert.getSubjectDN().toString());
		//System.out.println(sa.toString());
		String r = ss1.exampleMethod("PsyGridFEPProject", sa.toString());

		System.out.println(r);

	}
}
