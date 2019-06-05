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

package org.psygrid.security.attributeauthority;

import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ws.security.WSSecurityException;
import org.apache.ws.security.components.crypto.Crypto;
import org.apache.ws.security.components.crypto.CryptoFactory;
import org.apache.ws.security.util.Loader;
import org.apache.xml.security.signature.XMLSignature;
import org.opensaml.SAMLAssertion;
import org.opensaml.SAMLAttribute;
import org.opensaml.SAMLAttributeStatement;
import org.opensaml.SAMLException;
import org.opensaml.SAMLStatement;
import org.opensaml.SAMLSubject;
import org.psygrid.security.attributeauthority.model.hibernate.User;

/**
 * Builds a  SAML Assertion
 *
 * @author Davanum Srinivas (dims@yahoo.com).
 * Modified by John Ainsworth (john.ainsworth@manchester.ac.uk)
 */
public class AASAMLIssuer {
	
	private static AASAMLIssuer _instance = null;
	
	private static Log log = LogFactory.getLog(AASAMLIssuer.class.getName());
	
	private Properties properties = null;
	
	private Crypto issuerCrypto = null;
	private String issuerKeyPassword = null;
	private String issuerKeyName = null;
	
	private boolean senderVouches = true;
	
	private String[] confirmationMethods = new String[1];
	
	/**
	 * Constructor.
	 */
	public AASAMLIssuer() {
	}
	
	public AASAMLIssuer(Properties prop) {
		/*
		 * if no properties .. just return an instance, the rest will be done
		 * later or this instance is just used to handle certificate
		 * conversions in this implementatio
		 */
		if (prop == null) {
			return;
		}
		properties = prop;
		String cryptoProp =
			properties.getProperty("org.apache.ws.security.saml.issuer.cryptoProp.file");
		if (cryptoProp != null) {
			issuerCrypto = CryptoFactory.getInstance(cryptoProp);
			issuerKeyName =
				properties.getProperty("org.apache.ws.security.saml.issuer.key.name");
			issuerKeyPassword =
				properties.getProperty("org.apache.ws.security.saml.issuer.key.password");
		}
		X509Certificate[] issuerCerts = null;
		try{
			issuerCerts = issuerCrypto.getCertificates(issuerKeyName);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RuntimeException(e.getMessage());			
		}
		if(issuerCerts.length==0){
			log.error("No AA signing certificate");
			throw new RuntimeException("No AA signing certificate");
		}
	
		// always sign
		confirmationMethods[0] = SAMLSubject.CONF_HOLDER_KEY;
		senderVouches = false;
		
//		if ("senderVouches"
//				.equals(properties.getProperty("org.apache.ws.security.saml.confirmationMethod"))) {
//			confirmationMethods[0] = SAMLSubject.CONF_SENDER_VOUCHES;
//		} else if (
//				"keyHolder".equals(properties.getProperty("org.apache.ws.security.saml.confirmationMethod"))) {
//			confirmationMethods[0] = SAMLSubject.CONF_HOLDER_KEY;
//			senderVouches = false;
//		} else {
//			// throw something here - this is a mandatory property
//			confirmationMethods[0] = SAMLSubject.CONF_SENDER_VOUCHES;
//		}
	}
	
	public SAMLAttribute newAttributeStatement(User u, String g, String r) throws SAMLException{
		SAMLAttribute sa = new SAMLAttribute();
		SAMLAttributeStatement sas = new SAMLAttributeStatement();
		sas.addAttribute(sa);
		return null;	
	}
	
	
	/**
	 * Creates a new <code>SAMLAssertion</code>.
	 * <p/>
	 * <p/>
	 * A complete <code>SAMLAssertion</code> is constructed.
	 *
	 * @return SAMLAssertion
	 */
	public SAMLAssertion newAssertion(List<SAMLStatement> lss, long lifetime) { // throws Exception {
		log.debug("Begin add SAMLAssertion token...");
		SAMLAssertion sa = null;
		/*
		 * if (senderVouches == false && userCrypto == null) { throw
		 * exception("need user crypto data to insert key") }
		 */
		// Issuer must enable crypto fubctions to get the issuer's certificate
		String issuer = 
			properties.getProperty("org.apache.ws.security.saml.issuer");
		Date notBefore = new Date();
	    Date notAfter = new Date(notBefore.getTime()+(lifetime*1000));
		try {
			sa = new SAMLAssertion(issuer,
					notBefore,
					notAfter,
					null,
					null,
					lss);
			if (!senderVouches) {
				// prepare to sign the SAML token
				try {
					log.debug(issuerKeyName);
					X509Certificate[] issuerCerts =
						issuerCrypto.getCertificates(issuerKeyName);
					String sigAlgo = XMLSignature.ALGO_ID_SIGNATURE_RSA;
					String pubKeyAlgo =
						issuerCerts[0].getPublicKey().getAlgorithm();
					log.debug("automatic sig algo detection: " + pubKeyAlgo);
					if (pubKeyAlgo.equalsIgnoreCase("DSA")) {
						sigAlgo = XMLSignature.ALGO_ID_SIGNATURE_DSA;
					}
					java.security.Key issuerPK =
						issuerCrypto.getPrivateKey(issuerKeyName,
								issuerKeyPassword);
					sa.sign(sigAlgo, issuerPK, Arrays.asList(issuerCerts));
				} catch (WSSecurityException e1) {
					e1.printStackTrace();
					return null;
				} catch (Exception e1) {
					e1.printStackTrace();
					return null;
				}
			}
		} catch (SAMLException ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex.toString());
		}
		return sa;
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
	 * getInstance
	 * <p/>
	 * Returns an instance of SAMLIssuer. This method uses the file
	 * <code>saml.properties</code> to determine property definitions. 
	 * These properties are handed over to the  SAMLIssuer implementation. The file
	 * <code>saml.properties</code> is loaded with the
	 * <code>Loader.getResource()</code> method.
	 * <p/>
	 *
	 * @return The SAMLIssuer implementation was defined
	 */
	public static AASAMLIssuer getInstance(String properties) {
		if(_instance==null){
			_instance = new AASAMLIssuer(getProperties(properties));
		}
		return _instance;
	}
	/**
	 * Gets the properties for SAML issuer.
	 * The functions loads the property file via
	 * {@link Loader.getResource(String)}, thus the property file
	 * should be accesible via the classpath
	 *
	 * @param propFilename the properties file to load
	 * @return a <code>Properties</code> object loaded from the filename
	 */
	private static Properties getProperties(String propFilename) {
		Properties properties = new Properties();
		try {
			URL url = Loader.getResource(propFilename);
			properties.load(url.openStream());
		} catch (Exception e) {
			log.info("Cannot find SAML property file: " + propFilename);
			throw new RuntimeException("SAMLIssuerFactory: Cannot load properties: " + propFilename);
		}
		return properties;
	}
}
