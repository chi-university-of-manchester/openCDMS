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


//Created on Oct 12, 2005 by John Ainsworth



package org.psygrid.security.attributeauthority.client;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.rpc.Stub;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.security.ConfigurableSecureServiceClient;
import org.psygrid.security.PGSecurityException;
import org.psygrid.security.attributeauthority.service.AttributeAuthorityManagementPortType;
import org.psygrid.security.attributeauthority.service.AttributeAuthorityService;
import org.psygrid.security.attributeauthority.service.AttributeAuthorityServiceLocator;
import org.psygrid.security.utils.PropertyUtilities;

public class AAManagementClient extends ConfigurableSecureServiceClient{

	/**
	 * logger
	 */
	private static Log log = LogFactory.getLog(AAManagementClient.class);

	private AttributeAuthorityManagementPortType attributeAuthority = null;

	public AAManagementClient(String propFilename) throws PGSecurityException {
		if (propFilename == null) {
			throw new PGSecurityException("properties cannot be null");
		}
		
		properties = PropertyUtilities.getProperties(propFilename);	
		setSecurityProperties(properties
				.getProperty("org.psygrid.security.attributeauthority.client.trustStoreLocation"),
				properties
				.getProperty("org.psygrid.security.attributeauthority.client.trustStorePassword"),
				properties
				.getProperty("org.psygrid.security.attributeauthority.client.keyStoreLocation"),
				properties
				.getProperty("org.psygrid.security.attributeauthority.client.keyStorePassword"));

		try {
			url = new URL(
					properties
					.getProperty("org.psygrid.security.attributeauthority.admin.serviceURL"));
		} catch (MalformedURLException mue) {
			mue.printStackTrace();
			log.fatal(mue.getMessage());
			throw new PGSecurityException(mue.getMessage());
		}
		
		AttributeAuthorityService xService = new AttributeAuthorityServiceLocator();
		try {
			attributeAuthority = xService
			.getAttributeAuthorityPortTypeManagementSOAPPort();
		} catch (Exception e) {
			e.printStackTrace();
			log.fatal(e.getMessage());
			throw new PGSecurityException(e.getMessage());
		}
		this.setEndpointAddress(url.toString());
	}
	
	/**
	 * Get instance of the Test client to access the service at the
	 * specified endpoint address.
	 * 
	 * @param pServiceEndpointAddress
	 *            The URL of the service to access (e.g. http://<service
	 *            address>:<port>/<path>).
	 * @param pTrustStoreLocation
	 *            The complete path to the CA keystore.
	 * @param pTrustStorePassword
	 *            The password to access to the CA keystore.
	 * @param pKeyStoreLocation
	 *            The complete path to the user's keystore.
	 * @param pKeyStorePassword
	 *            The password to access the user's keystore.
	 * @return The single instance of the Test client.
	 */
	public AAManagementClient(String pServiceEndpointAddress,
			String pTrustStoreLocation, String pTrustStorePassword,
			String pKeyStoreLocation, String pKeyStorePassword)
	throws PGSecurityException {
		

		setSecurityProperties(pTrustStoreLocation,
				pTrustStorePassword,
				pKeyStoreLocation,
				pKeyStorePassword);
		
		AttributeAuthorityService xService = new AttributeAuthorityServiceLocator();
		try {
			attributeAuthority = xService
			.getAttributeAuthorityPortTypeManagementSOAPPort();
		} catch (Exception e) {
			e.printStackTrace();
			log.fatal(e.getMessage());
			throw new PGSecurityException(e.getMessage());
		}
		setEndpointAddress(pServiceEndpointAddress);
	}
	
	public Stub getStub(){ 
		return (javax.xml.rpc.Stub)attributeAuthority;
	}
	
	public AttributeAuthorityManagementPortType getPort(){
		return attributeAuthority;
	}
}

