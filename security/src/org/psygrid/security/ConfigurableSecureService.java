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

package org.psygrid.security;

import java.io.File;
import java.security.Principal;

import javax.xml.rpc.ServiceException;
import javax.xml.rpc.server.ServiceLifecycle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage;
import org.psygrid.security.attributeauthority.service.ProcessingFaultMessage;
import org.psygrid.security.utils.FileUtilities;
import org.psygrid.security.utils.XMLUtilities;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author jda
 * 
 */
public abstract class  ConfigurableSecureService extends SecureService implements ServiceLifecycle {

	protected static final String ssConfigNamespace = "http://www.psygrid.org/xml/security/secureservice/config";

	protected static final String dsKeydebugNamespace = "http://www.w3.org/2000/09/xmldsig#";

	public static final int ADMINISTRATION = 3;

	public static final int PROXY = 2;

	public static final int QUERY = 1;

	public static final int NONE = 0;

	/** logger */
	private static Log sLog = LogFactory.getLog(ConfigurableSecureService.class);

	/**
	 * 
	 */
	public ConfigurableSecureService() {
		super();
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
	}
	
	

	protected abstract  Document getConfigurationDOM();
	
	protected abstract  void setConfigurationDOM(Document doc);
	
	protected abstract  String getConfigurationFile();
	
	protected abstract  String getConfigurationSchemaFile();
	
	protected abstract  String getXMLSchemaLocation();
	
	protected abstract  void setConfigurationFile(String s);
	
	protected abstract  void setConfigurationSchemaFile(String s);
	
	protected abstract  void setXMLSchemaLocation(String s);

	/**
	 * @param subject
	 * @return
	 */
	protected boolean authorisedForProxyRequests(Principal subject) throws PGSecurityException {
		return checkAuthorisation(subject, ConfigurableSecureService.PROXY);
	}

	/**
	 * @param subject
	 * @return
	 */
	protected boolean authorisedForQueryRequests(Principal subject) throws PGSecurityException {
		return checkAuthorisation(subject, ConfigurableSecureService.QUERY);
	}

	/**
	 * @param subject
	 * @return
	 */
	protected boolean authorisedForAdministrationRequests(Principal subject) throws PGSecurityException {
		return checkAuthorisation(subject, ConfigurableSecureService.ADMINISTRATION);
	}

	protected boolean findIdentityInList(String id, String list, Document doc) {
		boolean rc = false;
		NodeList elements = doc.getElementsByTagNameNS(ssConfigNamespace, list);
		sLog.debug("elements.length " + elements.getLength());
		for (int i = 0; i < elements.getLength(); i++) {
			sLog.debug("nodename " + elements.item(i).getNodeName());
			NodeList dns = ((Element) elements.item(i))
			.getElementsByTagNameNS(dsKeydebugNamespace, "X509SubjectName");
			sLog.debug("DNs.length " + dns.getLength());
			sLog.debug("nodes " + dns.item(0).getNodeName());
			sLog.debug("Comparing "
					+ id
					+ " with "
					+ ((Element) dns.item(0)).getFirstChild()
					.getNodeValue());
			if (((Element) dns.item(0)).getFirstChild().getNodeValue()
					.equals(id)) {
				rc=true;
				break;
			}
		}
		return rc;
	}

	/**
	 * @param dn
	 * @param operation
	 * @return
	 * @throws NotAuthorisedFaultMessage
	 * @throws ProcessingFaultMessage
	 */
	boolean checkAuthorisation(Principal dn, int operation) throws PGSecurityException {
	 
		if(dn == null){
			return false;
		}
		String uid = dn.getName();
		if(getUserCertificates()==null){
			return false;
		}
		//Only check the first certificate
		//There should only be one
		String issuer = getUserCertificates()[0].getIssuerDN().getName();
		
		Document doc = null;
		
		synchronized (getConfigurationDOM()) {
			if (getConfigurationDOM() == null) {
				String sxml = FileUtilities.readFileAsString(getConfigurationFile());
				try {
					doc = (new XMLUtilities()).toDocument(sxml, true, 
							getConfigurationSchemaFile());
				} catch (Exception e) {
					sLog.fatal("failed document creation");
					throw new PGSecurityException(
							"cannot parse config file");
				}
			} else {
				doc = getConfigurationDOM();
				doc.normalizeDocument();
			}
		}
	
		if ((operation == ConfigurableSecureService.ADMINISTRATION)
				|| (operation == ConfigurableSecureService.PROXY)
				|| (operation == ConfigurableSecureService.QUERY)) {
			if (findIdentityInList(uid, "administrator", doc)) {
				return true;
			}
			if (findIdentityInList(issuer, "administration-authority", doc)) {
				return true;
			}
		}
		if ((operation == ConfigurableSecureService.PROXY)
				|| (operation == ConfigurableSecureService.QUERY)) {
			if (findIdentityInList(uid, "proxy", doc)) {
				return true;
			}
			if (findIdentityInList(issuer, "proxy-authority", doc)) {
				return true;
			}
		}
		if (operation == ConfigurableSecureService.QUERY) {
			if (findIdentityInList(uid, "user", doc)) {
				return true;
			}
			if(findIdentityInList(issuer, "user-authority", doc)){
				return true;
			}			
		} 
		return false;
	}

	/**
	 * 
	 * @param file
	 * @return success indication
	 * @see
	 */
	protected boolean configure(String file) {
		boolean rc = false;
		Document doc = null;
		String sxml = FileUtilities.readFileAsString(file);
		if (sxml != null) {
			try {
				synchronized (getConfigurationDOM()) {
					if ((doc = (new XMLUtilities()).toDocument(sxml, true, 
							getXMLSchemaLocation() + File.separator + getConfigurationSchemaFile())) 
							!= null) {
						rc = true;
						setConfigurationDOM(doc);
					}
				}
			} catch (Exception e) {
				sLog.fatal("invalid XML in " + file + " " + e.getMessage());
			}
		}
		return rc;
	}
	
	protected void doInitialConfigure() throws ServiceException{
		if (xContext.getInitParameter("configurationSchemaFile") == null) {
			throw new ServiceException(
			"Service not available: configuration error: missing configurationSchemaFile entry in web.xml");
		} else {
			setConfigurationSchemaFile(xContext.getInitParameter("configurationSchemaFile"));
		}
		if (xContext.getInitParameter("configurationFile") == null) {
			throw new ServiceException(
			"Service not available: configuration error: missing configurationFile entry in web.xml");
		} else {
			setConfigurationFile(xContext.getInitParameter("configurationFile"));
		}

		sLog.debug("identity: " + xContext.getInitParameter("identity") + "\n");

		sLog.debug("configurationFile: "
				+ xContext.getInitParameter("configurationFile") + "\n");
		
		sLog.debug("configurationSchemaFile: "
				+ xContext.getInitParameter("configurationSchemaFile") + "\n");			
		
		setXMLSchemaLocation(xContext.getRealPath("/WEB-INF/wsdl"));
		
		if (null != getXMLSchemaLocation()) {
			sLog.debug("Will look for XML Schemas in " + getXMLSchemaLocation());
		} else {
			throw new ServiceException(
					"Service not available: XMLParser::XML Schemas not found or not accessible at "
							+ getXMLSchemaLocation());
		}

		String sxml = FileUtilities.readFileAsString(getConfigurationFile());
		if (sxml != null) {
			setConfigurationDOM((new XMLUtilities()).toDocument(sxml,
					true, getXMLSchemaLocation() + File.separator
					+ getConfigurationSchemaFile()));
			if (getConfigurationDOM() == null) {
				throw new ServiceException(
						"Service not available: configuration failure");
			}
		}
	}
}
