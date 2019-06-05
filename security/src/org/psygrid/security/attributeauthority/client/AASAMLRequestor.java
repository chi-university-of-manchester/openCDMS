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


//Created on Oct 13, 2005 by John Ainsworth
package org.psygrid.security.attributeauthority.client;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.apache.axis.MessageContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ws.security.components.crypto.Crypto;
import org.apache.ws.security.components.crypto.CryptoFactory;
import org.apache.ws.security.handler.RequestData;
import org.apache.ws.security.saml.SAMLIssuer;
import org.opensaml.SAMLAssertion;
import org.opensaml.SAMLException;
import org.opensaml.SAMLSubject;
import org.opensaml.XML;
import org.psygrid.security.PGSecurityException;
import org.psygrid.security.attributeauthority.service.AttributeAuthorityQueryPortType;
import org.psygrid.security.attributeauthority.service.AttributeAuthorityService;
import org.psygrid.security.attributeauthority.service.AttributeAuthorityServiceLocator;
import org.psygrid.www.xml.security.core.types.ProjectType;
import org.w3c.dom.Document;

/**
 * @author jda
 * 
 */
public class AASAMLRequestor implements SAMLIssuer {

	private AASAMLRequestor theInstance = null;
	
	public static final String DS_NAMESPACE = "http://www.w3.org/2000/09/xmldsig#";

	public static final String SAML_NAMESPACE = "urn:oasis:names:tc:SAML:1.0:assertion";

	//Defaults. These should be set in properties files
	private static String _trustStoreLocation = "../etc/keystores/trust.jks";

	private static String _trustStorePassword = "password";

	private static String _keyStoreLocation = "../etc/keystores/client.jks";

	private static String _keyStorePassword = "password";

	private static Log log = LogFactory
			.getLog(AASAMLRequestor.class.getName());

	private URL url = null;

	private AttributeAuthorityQueryPortType attributeAuthority = null;

	private SAMLAssertion sa = null;

	private Document instanceDoc = null;

	private Properties properties = null;

	private Crypto issuerCrypto = null;

	private String issuerKeyPassword = null;

	private String issuerKeyName = null;

	private boolean senderVouches = true;

	private String[] confirmationMethods = new String[1];

	private Crypto userCrypto = null;

	private String username = null;

	private RequestData reqData = null;

	/**
	 * Constructor.
	 */
	public AASAMLRequestor() {
		//default
	}

	public AASAMLRequestor getInstance(Properties prop) throws PGSecurityException {	
		if(theInstance!=null){
			return theInstance;
		}
		return new AASAMLRequestor(prop);	
	}
	
	public AASAMLRequestor(Properties prop) throws PGSecurityException{
		/*
		 * if no properties .. just return an instance, the rest will be done
		 * later or this instance is just used to handle certificate conversions
		 * in this implementatio
		 */


		if (prop == null) {
			return;
		}
		properties = prop;

		String cryptoProp = properties
				.getProperty("org.apache.ws.security.saml.issuer.cryptoProp.file");
		if (cryptoProp != null) {
			issuerCrypto = CryptoFactory.getInstance(cryptoProp);
			issuerKeyName = properties
					.getProperty("org.apache.ws.security.saml.issuer.key.name");
			issuerKeyPassword = properties
					.getProperty("org.apache.ws.security.saml.issuer.key.password");
		}

		if ("senderVouches".equals(properties
				.getProperty("org.apache.ws.security.saml.confirmationMethod"))) {
			confirmationMethods[0] = SAMLSubject.CONF_SENDER_VOUCHES;
		} else if ("keyHolder".equals(properties
				.getProperty("org.apache.ws.security.saml.confirmationMethod"))) {
			confirmationMethods[0] = SAMLSubject.CONF_HOLDER_KEY;
			senderVouches = false;
		} else {
			throw new PGSecurityException("Either senderVouches or keyHolder property must be set");
		}

		_trustStoreLocation = properties
				.getProperty("org.psygrid.security.attributeauthority.client.trustStoreLocation");
		_keyStoreLocation = properties
				.getProperty("org.psygrid.security.attributeauthority.client.keyStoreLocation");
		_trustStorePassword = properties
				.getProperty("org.psygrid.security.attributeauthority.client.trustStorePassword");
		_keyStorePassword = properties
				.getProperty("org.psygrid.security.attributeauthority.client.keyStorePassword");
		try {
			url = new URL(
					properties
							.getProperty("org.psygrid.security.attributeauthority.client.serviceURL"));
		} catch (MalformedURLException mue) {
			mue.printStackTrace();
			log.fatal(mue.getMessage());
			throw new PGSecurityException(mue.getMessage());
		}

		AttributeAuthorityService xService = new AttributeAuthorityServiceLocator();
		try {
			attributeAuthority = xService
					.getAttributeAuthorityPortTypeQuerySOAPPort();
		} catch (Exception e) {
			e.printStackTrace();
			log.fatal(e.getMessage());
			throw new PGSecurityException(e.getMessage());
		}
		this.setEndpointAddress(url.toString());
	}

	/**
	 * Creates a new <code>SAMLAssertion</code>. <p/> <p/> A complete
	 * <code>SAMLAssertion</code> is constructed.
	 * 
	 * @return SAMLAssertion
	 */
	public SAMLAssertion newAssertion(String user, ProjectType  project) {

	
		String stringAssertion = null;
		SAMLAssertion sa = null;
		try {

				if (user != null) {
					if (project != null) {
						stringAssertion = attributeAuthority
								.getAttributesForUserInProject(user, project);
					} else {
						stringAssertion = attributeAuthority
								.getAttributesForUser(user);
					}
					if (stringAssertion != null) {
						InputStream is = new ByteArrayInputStream(
								stringAssertion.getBytes());
						Document doc = XML.parserPool.parse(is);
						sa = new SAMLAssertion(doc.getDocumentElement());
						//log.info("..............check signature");
						try {
							sa.verify();
						} catch (SAMLException se) {
							log.info("NOT VALID" + se.getMessage());
						}
//						System.out
//								.println("..............check signature done");
					}
				} else {
					log.info("user is not set");
				}
		
		} catch (Exception e) {
			log.info(e.getMessage());
			e.printStackTrace();
		}
		return sa;
	}
	
	/**
	 * Creates a new <code>SAMLAssertion</code>. <p/> <p/> A complete
	 * <code>SAMLAssertion</code> is constructed.
	 * 
	 * @return SAMLAssertion
	 */
	public SAMLAssertion newAssertion() {

		String project = null;
		String user = null;
		String stringAssertion = null;
		SAMLAssertion sa = null;
		try {
			if (reqData != null) {
				project = (String) ((MessageContext) reqData.getMsgContext())
						.getProperty("TargetGroup");
				user = (String) ((MessageContext) reqData.getMsgContext())
						.getProperty("TargetUser");
				//log.info("group = " + group + " user = " + user);
				if (user != null) {
					if (project != null) {
						stringAssertion = attributeAuthority
								.getAttributesForUserInProject(user, new ProjectType(project, "", null, null, false));
					} else {
						stringAssertion = attributeAuthority
								.getAttributesForUser(user);
					}
					if (stringAssertion != null) {
						InputStream is = new ByteArrayInputStream(
								stringAssertion.getBytes());
						Document doc = XML.parserPool.parse(is);
						sa = new SAMLAssertion(doc.getDocumentElement());
						//log.info("..............check signature");
						try {
							sa.verify();
						} catch (SAMLException se) {
							log.info("NOT VALID" + se.getMessage());
						}
//						System.out
//								.println("..............check signature done");
					}
				} else {
					log.info("user is not set");
				}
			}
		} catch (Exception e) {
			log.info(e.getMessage());
			e.printStackTrace();
		}
		return sa;
	}

	/**
	 * @param userCrypto
	 *            The userCrypto to set.
	 */
	public void setUserCrypto(Crypto userCrypto) {
		this.userCrypto = userCrypto;
	}

	/**
	 * @param username
	 *            The username to set.
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return Returns the issuerCrypto.
	 */
	public Crypto getIssuerCrypto() {
		return issuerCrypto;
	}

	/**
	 * @return Returns the issuerKeyName.
	 */
	public String getIssuerKeyName() {
		return issuerKeyName;
	}

	/**
	 * @return Returns the issuerKeyPassword.
	 */
	public String getIssuerKeyPassword() {
		return issuerKeyPassword;
	}

	/**
	 * @return Returns the senderVouches.
	 */
	public boolean isSenderVouches() {
		return senderVouches;
	}

	/**
	 * @param instanceDoc
	 *            The instanceDoc to set.
	 */
	public void setInstanceDoc(Document instanceDoc) {
		this.instanceDoc = instanceDoc;
	}

	/**
	 * @param requestData
	 *            The reqestData to set.
	 */
	public void setRequestData(RequestData reqData) {
		this.reqData = reqData;
	}

	/**
	 * @return Returns the reqData
	 */
	public RequestData getRequestData() {
		return reqData;
	}

	/**
	 * Set the endpoint address of the service to access. (Accessed through
	 * getInstance).
	 * 
	 * @param pAddress
	 *            Service endpoint address.
	 */
	private void setEndpointAddress(String pAddress) {
		// Set the address of the service endpoint
		((javax.xml.rpc.Stub) attributeAuthority)._setProperty(
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
			// System.setProperty("javax.net.debug", "ssl,handshake");
		}
	}

	public AttributeAuthorityQueryPortType getPort() {
		return attributeAuthority;
	}
}
