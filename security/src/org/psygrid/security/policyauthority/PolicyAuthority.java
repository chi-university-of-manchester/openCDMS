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


//Created on Oct 10, 2005 by John Ainsworth

package org.psygrid.security.policyauthority;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Enumeration;

import javax.xml.rpc.ServiceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opensaml.SAMLAssertion;
import org.opensaml.SAMLException;
import org.opensaml.XML;
import org.psygrid.logging.AuditLogger;
import org.psygrid.security.ConfigurableSecureService;
import org.psygrid.security.PGSecurityException;
import org.psygrid.security.PGSecurityInvalidSAMLException;
import org.psygrid.security.PGSecuritySAMLVerificationException;
import org.psygrid.security.attributeauthority.client.AAQueryClient;
import org.psygrid.security.policyauthority.dao.PolicyDAO;
import org.psygrid.security.policyauthority.model.Factory;
import org.psygrid.security.policyauthority.model.hibernate.HibernateFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.w3c.dom.Document;

/**
 * @author jda
 * 
 */
public class PolicyAuthority extends ConfigurableSecureService {

	protected static final String COMPONENT_NAME = "PolicyAuthority";

	static protected long STATEMENT_LIFETIME = 300L;
	
	/** logger */
	public static Log sLog = LogFactory.getLog(PolicyAuthority.class);

	protected static Factory factory = null;

	protected static String XMLSchemaLocation = null;

	protected static String configurationFile = null;
	
	protected static String configurationSchemaFile = null;
	
	protected static String aaCertsKeystoreFile = null;
	
	protected static String aaCertsKeystorePassword= null;
	
	protected static Document configurationDOM = null;

	protected static KeyStore aaCertsKeystore = null;
	
	private static Principal defaultAuthority = null;
	
	private static String defaultAuthorityAlias = null;
	
	private static boolean initialised = false;

	/**
	 * Audit logger
	 */
	protected static AuditLogger logHelper = new AuditLogger(PolicyAuthority.class);
	
	protected static AAQueryClient aaqc = null;
	
	PolicyDAO policyDAO = null;

	/**
	 * public constructor
	 */
	public PolicyAuthority() {
		super();
		sLog.debug("PolicyAuthority constructor");
	}

	/**
	 * Used for initialization of a service endpoint. After a service endpoint
	 * instance (an instance of a service endpoint class) is instantiated, the
	 * JAX-RPC runtime system invokes the init method. The service endpoint
	 * class uses the init method to initialize its configuration and setup
	 * access to any external resources. The context parameter in the init
	 * method enables the endpoint instance to access the endpoint context
	 * provided by the underlying JAX-RPC runtime system. The init method
	 * implementation should typecast the context parameter to an appropriate
	 * Java type. For service endpoints deployed on a servlet container based
	 * JAX-RPC runtime system, the context parameter is of the Java type
	 * javax.xml.rpc.server.ServletEndpointContext. The ServletEndpointContext
	 * provides an endpoint context maintained by the underlying servlet
	 * container based JAX-RPC runtime system
	 * 
	 * @param pContext
	 *            Endpoint context for a JAX-RPC service endpoint
	 * 
	 * @throws ServiceException
	 *             If any error in initialization of the service endpoint; or if
	 *             any illegal context has been provided in the init method
	 */
	public void init(Object pContext) throws ServiceException {
		
		super.init(pContext);
		if(!initialised){
			super.doInitialConfigure();
			if (xContext.getInitParameter("aaCertsKeystoreLocation") == null) {
				throw new ServiceException(
				"Service not available: configuration error: missing aaCertsKeyStoreLocation entry in web.xml");
			} else {
				setAaCertsKeystoreFile(xContext.getInitParameter("aaCertsKeystoreLocation"));
			}
			if (xContext.getInitParameter("aaCertsKeystorePassword") == null) {
				throw new ServiceException(
				"Service not available: configuration error: missing aaCertsKeyStoreLocation entry in web.xml");
			} else {
				setAaCertsKeystorePassword(xContext.getInitParameter("aaCertsKeystorePassword"));
			}
			if (xContext.getInitParameter("defaultAuthorityAlias") == null) {
				throw new ServiceException(
				"Service not available: configuration error: missing defaultAuthorityAlias entry in web.xml");
			} else {
				setDefaultAuthorityAlias(xContext.getInitParameter("defaultAuthorityAlias"));
			}
			initialiseAACertsKeyStore();
			try{
			String propertiesFile = oContext.getServletContext().getInitParameter("attributeAuthorityProperties");	
			aaqc = new AAQueryClient(propertiesFile);
			} catch (PGSecurityException p){
				throw new ServiceException(
				"Service not available: configuration error: missing attributeAuthorityProperties entry in web.xml");			
			}
		}
		factory = new HibernateFactory();
	}

	/**
	 * @return PolicyDAO
	 */
	protected PolicyDAO getPolicyDAO() {
		if (policyDAO == null) {
//			HttpServlet servlet = (HttpServlet) MessageContext
//					.getCurrentContext().getProperty(
//							HTTPConstants.MC_HTTP_SERVLET);
			ApplicationContext context = WebApplicationContextUtils
					.getWebApplicationContext(getServletContext());
			policyDAO = (PolicyDAO) context.getBean("policyDAOService");
		}
		return policyDAO;
	}

	/**
	 * @return Returns the configurationDOM.
	 */
	protected Document getConfigurationDOM() {
		return configurationDOM;
	}

	/**
	 * @param configurationDOM The configurationDOM to set.
	 */
	protected  void setConfigurationDOM(Document configurationDOM) {
		PolicyAuthority.configurationDOM = configurationDOM;
	}

	/**
	 * @return Returns the configurationFile.
	 */
	protected String getConfigurationFile() {
		return PolicyAuthority.configurationFile;
	}

	/**
	 * @param configurationFile The configurationFile to set.
	 */
	protected  void setConfigurationFile(String configurationFile) {
		PolicyAuthority.configurationFile = configurationFile;
	}

	/**
	 * @return Returns the xMLSchemaLocation.
	 */
	protected  String getXMLSchemaLocation() {
		return PolicyAuthority.XMLSchemaLocation;
	}

	/**
	 * @param schemaLocation The xMLSchemaLocation to set.
	 */
	protected  void setXMLSchemaLocation(String schemaLocation) {
		PolicyAuthority.XMLSchemaLocation = schemaLocation;
	}

	/**
	 * @return Returns the configurationSchemaFile.
	 */
	protected  String getConfigurationSchemaFile() {
		return PolicyAuthority.configurationSchemaFile;
	}

	/**
	 * @param configurationSchemaFile The configurationSchemaFile to set.
	 */
	protected  void setConfigurationSchemaFile(String configurationSchemaFile) {
		PolicyAuthority.configurationSchemaFile = configurationSchemaFile;
	}

	/**
	 * @return Returns the aaCertsKeystoreFile.
	 */
	protected static String getAaCertsKeystoreFile() {
		return aaCertsKeystoreFile;
	}

	/**
	 * @param aaCertsKeystoreFile The aaCertsKeystoreFile to set.
	 */
	protected static void setAaCertsKeystoreFile(String aaCertsKeystoreFile) {
		PolicyAuthority.aaCertsKeystoreFile = aaCertsKeystoreFile;
	}

	/**
	 * @return Returns the aaCertsKeystorePassword.
	 */
	protected static String getAaCertsKeystorePassword() {
		return aaCertsKeystorePassword;
	}

	/**
	 * @param aaCertsKeystorePassword The aaCertsKeystorePassword to set.
	 */
	protected static void setAaCertsKeystorePassword(String aaCertsKeystorePassword) {
		PolicyAuthority.aaCertsKeystorePassword = aaCertsKeystorePassword;
	}
	protected void initialiseAACertsKeyStore() throws ServiceException{
		FileInputStream fis = null;
		try{
		fis = new FileInputStream(getAaCertsKeystoreFile());
		} catch (FileNotFoundException fnfe) {
			throw new ServiceException("Service not available:"+ fnfe.getMessage());
		}
		try {
			aaCertsKeystore = KeyStore.getInstance("JKS");	
			aaCertsKeystore.load(fis, getAaCertsKeystorePassword().toCharArray());
			
			X509Certificate c = (X509Certificate)aaCertsKeystore.getCertificate(getDefaultAuthorityAlias());
			c.checkValidity();
			defaultAuthority = c.getSubjectX500Principal();
				
		} catch (KeyStoreException ke) {
			throw new ServiceException("Service not available:"+ ke.getMessage());			
		} catch (NoSuchAlgorithmException nsae){
			throw new ServiceException("Service not available:"+ nsae.getMessage());			
		} catch (CertificateException ce){
			throw new ServiceException("Service not available:"+ ce.getMessage());			
		} catch (IOException ioe){
			throw new ServiceException("Service not available:"+ ioe.getMessage());		
		}
	}

	public SAMLAssertion verifySAMLAssertion(String stringAssertion) throws PGSecurityException, PGSecurityInvalidSAMLException, PGSecuritySAMLVerificationException {
		SAMLAssertion sa = null;
		boolean issuerTrusted = false;
		InputStream is = new ByteArrayInputStream(stringAssertion
				.getBytes());
		try {
			Document doc = XML.parserPool.parse(is);
			sa = new SAMLAssertion(doc.getDocumentElement());
		} catch (Exception e) {
			sLog.info(e.getMessage());
			throw new PGSecurityException(
			"failed to recover SAML Assertion");
		}
		
		if (sa == null) {
			throw new PGSecurityException("Failed to retrieve SAML Assertion");
		}
		
		Date current = new Date();
		Date notBefore = sa.getNotBefore();
		Date notAfter = sa.getNotOnOrAfter();
		if(notBefore.after(current)){
			throw new PGSecurityInvalidSAMLException("SAML Assertion is not yet valid");
		}
		if(notAfter.before(current)){
			throw new PGSecurityInvalidSAMLException("SAML Assertion has expired");
		}

		try {
			sa.checkValidity();
		} catch (SAMLException se) {
			throw new PGSecurityInvalidSAMLException("SAML Assertion not valid " + se.getMessage());		
		}
		try {
			Enumeration<String> aliases = aaCertsKeystore.aliases();
			while(aliases.hasMoreElements()){
				String alias = aliases.nextElement();
				X509Certificate c = (X509Certificate)aaCertsKeystore.getCertificate(alias);
				c.checkValidity();
				X509Certificate issuer = (X509Certificate)sa.getX509Certificates().next();
				issuer.checkValidity();
				sLog.debug(issuer.getSubjectDN().getName());
				sLog.debug(c.getSubjectDN().getName());
				if(issuer.equals(c)){
					issuerTrusted = true;
					sa.verify(c);
					break;
				}
			}
		} catch (KeyStoreException kse){
			throw new PGSecuritySAMLVerificationException("SAML Assertion can not be verified " + kse.getMessage());
		} catch (SAMLException se){
			throw new PGSecuritySAMLVerificationException("SAML Assertion can not be verified " + se.getMessage());		
		}catch (CertificateNotYetValidException nve){
			throw new PGSecuritySAMLVerificationException("SAML Assertion can not be verified " + nve.getMessage());		
		}catch (CertificateExpiredException cee){
			throw new PGSecuritySAMLVerificationException("SAML Assertion can not be verified " + cee.getMessage());		
		}
		if(!issuerTrusted){
			//sLog.info("issuer not trusted");
			throw new PGSecuritySAMLVerificationException("SAML Assertion issuer not trusted");				
		}
		return sa;
	}

	private static void setDefaultAuthority(Principal defaultAuthority) {
		PolicyAuthority.defaultAuthority = defaultAuthority;
	}

	private static void setDefaultAuthorityAlias(String defaultAuthorityAlias) {
		PolicyAuthority.defaultAuthorityAlias = defaultAuthorityAlias;
	}

	protected static Principal getDefaultAuthority() {
		return defaultAuthority;
	}

	protected static String getDefaultAuthorityAlias() {
		return defaultAuthorityAlias;
	}
}
