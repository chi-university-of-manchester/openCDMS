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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.NoRouteToHostException;
import java.net.URL;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.xml.rpc.ServiceException;
import javax.xml.rpc.Stub;

import org.apache.axis.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opensaml.SAMLAssertion;
import org.opensaml.SAMLAttribute;
import org.opensaml.SAMLAttributeStatement;
import org.opensaml.SAMLException;
import org.opensaml.XML;
import org.psygrid.security.ConfigurableSecureServiceClient;
import org.psygrid.security.PGSecurityConstants;
import org.psygrid.security.PGSecurityException;
import org.psygrid.security.PGSecurityInvalidSAMLException;
import org.psygrid.security.PGSecuritySAMLVerificationException;
import org.psygrid.security.attributeauthority.service.AttributeAuthorityQueryPortType;
import org.psygrid.security.attributeauthority.service.AttributeAuthorityService;
import org.psygrid.security.attributeauthority.service.AttributeAuthorityServiceLocator;
import org.psygrid.security.attributeauthority.service.InputFaultMessage;
import org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage;
import org.psygrid.security.attributeauthority.service.ProcessingFaultMessage;
import org.psygrid.security.attributeauthority.types.ChangePasswordRequestType;
import org.psygrid.security.attributeauthority.types.PostProcessLoginResponseType;
import org.psygrid.security.utils.PropertyUtilities;
import org.psygrid.www.xml.security.core.types.AttributeType;
import org.psygrid.www.xml.security.core.types.CompositeRuleType;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.ProjectType;
import org.psygrid.www.xml.security.core.types.RoleType;
import org.w3c.dom.Document;

/**
 * Exposes the API of the Attribute Authority Query PortType in an easy to use
 * way. Create a new instance of the class passing either a properties file name
 * or by supplying the configuration parameters directly.
 * 
 * @author John Ainsworth
 * 
 */
public class AAQueryClient extends ConfigurableSecureServiceClient {
	/**
	 * logger
	 */
	private static Log log = LogFactory.getLog(AAQueryClient.class);

	public AttributeAuthorityQueryPortType attributeAuthority = null;

	private String ldapDir = null;
	
	
	// A cache for SAML.
	// This is a very basic implementation of a cache 
	// the whole cache times out every sixty seconds.
	Map<String,SAMLAssertion> samlCache = Collections.synchronizedMap(new HashMap<String,SAMLAssertion>());

	// Timeout for the cache in milliseconds
	private static long CACHE_TIMEOUT_MS=60000; // One minute
	
	// The last cache timeout
	long last_cache_clear = 0;
	

	public AAQueryClient(String propFilename) throws PGSecurityException {

		if (propFilename == null) {
			throw new RuntimeException("properties cannot be null");
		}

		properties = PropertyUtilities.getProperties(propFilename);

		setSecurityProperties(
				properties
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
							.getProperty("org.psygrid.security.attributeauthority.client.serviceURL"));
			ldapDir = properties
					.getProperty("org.psygrid.security.attributeauthority.client.ldapDirectoryURL");
		} catch (MalformedURLException mue) {
			mue.printStackTrace();
			log.fatal(mue.getMessage());
			throw new RuntimeException(mue.getMessage());
		}

		AttributeAuthorityService xService = new AttributeAuthorityServiceLocator();
		try {
			attributeAuthority = xService
					.getAttributeAuthorityPortTypeQuerySOAPPort();
		} catch (ServiceException e) {
			log.fatal(e.getMessage());
			throw new RuntimeException(e.getMessage());
		}
		this.setEndpointAddress(url.toString());
	}

	/**
	 * Get instance of the Test client to access the service at the specified
	 * endpoint address.
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
	public AAQueryClient(String pServiceEndpointAddress,
			String pTrustStoreLocation, String pTrustStorePassword,
			String pKeyStoreLocation, String pKeyStorePassword)
			throws PGSecurityException {

		setSecurityProperties(pTrustStoreLocation, pTrustStorePassword,
				pKeyStoreLocation, pKeyStorePassword);

		AttributeAuthorityService xService = new AttributeAuthorityServiceLocator();
		try {
			attributeAuthority = xService
					.getAttributeAuthorityPortTypeQuerySOAPPort();
		} catch (ServiceException e) {
			log.fatal(e.getMessage());
			throw new RuntimeException(e.getMessage());
		}
		setEndpointAddress(pServiceEndpointAddress);
	}

	public Stub getStub() {
		return (javax.xml.rpc.Stub) attributeAuthority;
	}

	public AttributeAuthorityQueryPortType getPort() {
		return attributeAuthority;
	}

	/**
	 * Get a signed SAML asertion listing all the priveleges a user has in one
	 * assertion. Returns null if the user does not exist
	 * 
	 * @param user
	 * @return SAMLAssertion
	 * @throws PGSecurityException
	 */
	public SAMLAssertion getSAMLAssertion(String user) throws ConnectException,
			PGSecuritySAMLVerificationException,
			PGSecurityInvalidSAMLException, PGSecurityException,
			NotAuthorisedFaultMessage {
		return getSAMLAssertion(user, null);
	}

	/**
	 * Get a signed SAML asertion listing the priveleges a user has in the
	 * specified project. Returns null if the user does not exist
	 * 
	 * @param project
	 * @return SAMLAssertion
	 * @throws PGSecurityException
	 */
	public SAMLAssertion getSAMLAssertion(ProjectType project)
			throws ConnectException, PGSecuritySAMLVerificationException,
			PGSecurityInvalidSAMLException, PGSecurityException,
			NotAuthorisedFaultMessage {
		return getSAMLAssertion(null, project);
	}

	/**
	 * Get a signed SAML asertion listing all the priveleges a user has in one
	 * assertion. Returns null if the user does not exist
	 * 
	 * @return SAMLAssertion
	 * @throws PGSecurityException
	 */
	public SAMLAssertion getSAMLAssertion() throws ConnectException,
			PGSecuritySAMLVerificationException,
			PGSecurityInvalidSAMLException, PGSecurityException,
			NotAuthorisedFaultMessage {
		return getSAMLAssertion(null, null);
	}

	
	public SAMLAssertion getSystemSAMLAssertion()
		throws ConnectException, PGSecuritySAMLVerificationException,
		PGSecurityInvalidSAMLException, PGSecurityException,
		NotAuthorisedFaultMessage {
		
		String assertion = null;
		
		try {
			assertion = attributeAuthority.getSystemLevelPrivilege();
		} catch (ProcessingFaultMessage pfm) {
			log.error(pfm.getMessage());
			throw new PGSecurityException(pfm.getMessage());
		} catch (AxisFault fault) {
			if (fault.getCause() instanceof ConnectException) {
				throw (ConnectException) fault.getCause();
			} else if (fault.getCause() instanceof NoRouteToHostException) {
				throw  new ConnectException(fault.getCause().getMessage());
			} else if (fault.getCause() instanceof UnknownHostException) {
				throw  new ConnectException(fault.getCause().getMessage());
			} else {
				throw new RuntimeException(fault);
			}
		} catch (RemoteException ex) {
			throw new RuntimeException(ex);		}
		
		if (assertion != null) {
			Document doc = null;
			SAMLAssertion sa = null;
			try {
				InputStream is = new ByteArrayInputStream(assertion
						.getBytes());
				doc = XML.parserPool.parse(is);
				sa = new SAMLAssertion(doc.getDocumentElement());
			} catch (Exception ioe) {
				log.error(ioe.getMessage());
				throw new PGSecurityException(ioe.getMessage());
			}
			try {
				sa.checkValidity();
			} catch (SAMLException se) {
				log.info("NOT VALID" + se.getMessage());
				throw new PGSecurityInvalidSAMLException(se.getMessage());
			}
			try {
				sa.verify();
				return sa;
			} catch (SAMLException se) {
				log.info("NOT VERIFIED" + se.getMessage());
				throw new PGSecuritySAMLVerificationException(se.getMessage());
			}
		}
		return null;

	}
	
	/**
	 * Get a signed SAML assertion listing all the privileges a user has in the
	 * specified group. Returns null if the user does not have membership of the
	 * group
	 * 
	 * @param user
	 * @param project
	 * @return SAMLAssertion
	 * @throws PGSecurityException
	 */
	public SAMLAssertion getSAMLAssertion(String user, ProjectType project)
			throws ConnectException, PGSecuritySAMLVerificationException,
			PGSecurityInvalidSAMLException, PGSecurityException,
			NotAuthorisedFaultMessage {

		// Clear the cache after CACHE_TIMEOUT_MS milliseconds
		long now=System.currentTimeMillis();
		if(now-last_cache_clear>CACHE_TIMEOUT_MS){
			log.debug("SAML Cache timeout");
			samlCache.clear();
			last_cache_clear=now;
		}
		
		// Build the cache key
		String key = user+"::"+(project!=null?project.getIdCode():"");
		
//		SAMLAssertion sa = samlCache.get(key);
		SAMLAssertion sa = null;
		
		if(sa==null) {

			log.debug("SAML cache miss");
		
			String stringAssertion = null;		
			
			try {
				if (user != null) {
					if (project != null) {
						stringAssertion = attributeAuthority
								.getAttributesForUserInProject(user, project);
					} else {
						stringAssertion = attributeAuthority
								.getAttributesForUser(user);
					}
	
				} else {
					if (project != null) {
						stringAssertion = attributeAuthority
								.getMyAttributesInProject(project);
					} else {
						stringAssertion = attributeAuthority.getMyAttributes();
					}
				}
			} catch (ProcessingFaultMessage pfm) {
				log.error(pfm.getMessage());
				throw new PGSecurityException(pfm.getMessage());
			} catch (NotAuthorisedFaultMessage nafm) {
				throw nafm;
			} catch (AxisFault fault) {
				if (fault.getCause() instanceof ConnectException) {
					throw (ConnectException) fault.getCause();
				} else if (fault.getCause() instanceof NoRouteToHostException) {
					throw  new ConnectException(fault.getCause().getMessage());
				} else if (fault.getCause() instanceof UnknownHostException) {
					throw  new ConnectException(fault.getCause().getMessage());
				} else {
					throw new RuntimeException(fault);
				}
			} catch (RemoteException ex) {
				throw new RuntimeException(ex);
			}
	
	
			if (stringAssertion != null) {
				Document doc = null;
				try {
					InputStream is = new ByteArrayInputStream(stringAssertion
							.getBytes());
					doc = XML.parserPool.parse(is);
					sa = new SAMLAssertion(doc.getDocumentElement());
				} catch (Exception ioe) {
					log.error(ioe.getMessage());
					throw new PGSecurityException(ioe.getMessage());
				}
				try {
					sa.checkValidity();
				} catch (SAMLException se) {
					log.info("NOT VALID" + se.getMessage());
					throw new PGSecurityInvalidSAMLException(se.getMessage());
				}
				try {
					sa.verify();
				} catch (SAMLException se) {
					log.info("NOT VERIFIED" + se.getMessage());
					throw new PGSecuritySAMLVerificationException(se.getMessage());
				}
				samlCache.put(key, sa);
			}
		}
		else {
			log.debug("SAML cache hit");
		}

		return sa;
	}

	/**
	 * Retrieve the list of the projects which the user is a member of.
	 * 
	 * @return List<ProjectType> A list of the projects which the user is a
	 *         member of
	 * @throws PGSecurityException
	 */
	public List<ProjectType> getMyProjects() throws ConnectException,
			PGSecuritySAMLVerificationException,
			PGSecurityInvalidSAMLException, PGSecurityException,
			NotAuthorisedFaultMessage {
		List<ProjectType> lpt = new ArrayList<ProjectType>();
		try{
			AttributeType[] gar = attributeAuthority.getMyProjects();
			for(AttributeType a : gar){
				lpt.add(a.getProject());
			}
		} catch (ProcessingFaultMessage pfm) {
			log.error(pfm.getMessage());
			throw new PGSecurityException(pfm.getMessage());
		} catch (NotAuthorisedFaultMessage nafm) {
			throw nafm;
		} catch (AxisFault fault) {
			if (fault.getCause() instanceof ConnectException) {
				throw (ConnectException) fault.getCause();
			} else if (fault.getCause() instanceof NoRouteToHostException) {
				throw  new ConnectException(fault.getCause().getMessage());
			} else if (fault.getCause() instanceof UnknownHostException) {
				throw  new ConnectException(fault.getCause().getMessage());
			} else {
				throw new RuntimeException(fault);
			}
		} catch (RemoteException ex) {
			throw new RuntimeException(ex);
		}
		return lpt;
	}

	/**
	 * Retrieve the list of the projects which the user is a member of.
	 * 
	 * @param user
	 * @return List<ProjectType> A list of the projects which the user is a
	 *         member of
	 * @throws PGSecurityException
	 */
	public List<ProjectType> getUsersProjects(String user)
			throws ConnectException, PGSecuritySAMLVerificationException,
			PGSecurityInvalidSAMLException, PGSecurityException,
			NotAuthorisedFaultMessage {

		List<ProjectType> lpt = new ArrayList<ProjectType>();
		try{
			AttributeType[] gar = attributeAuthority.getProjectsForUser(user);
			for(AttributeType a : gar){
				lpt.add(a.getProject());
			}
		} catch (ProcessingFaultMessage pfm) {
			log.error(pfm.getMessage());
			throw new PGSecurityException(pfm.getMessage());
		} catch (NotAuthorisedFaultMessage nafm) {
			throw nafm;
		} catch (AxisFault fault) {
			if (fault.getCause() instanceof ConnectException) {
				throw (ConnectException) fault.getCause();
			} else if (fault.getCause() instanceof NoRouteToHostException) {
				throw  new ConnectException(fault.getCause().getMessage());
			} else if (fault.getCause() instanceof UnknownHostException) {
				throw  new ConnectException(fault.getCause().getMessage());
			} else {
				throw new RuntimeException(fault);
			}
		} catch (RemoteException ex) {
			throw new RuntimeException(ex);
		}
		return lpt;
	}

	/**
	 * Get the roles I have in a project
	 * 
	 * @param project
	 * @return List<RoleType> A list of the user's roles in the specified
	 *         project
	 * @throws PGSecurityException
	 */
	public List<RoleType> getMyRolesInProject(ProjectType project)
			throws ConnectException, PGSecuritySAMLVerificationException,
			PGSecurityInvalidSAMLException, PGSecurityException,
			NotAuthorisedFaultMessage {
		return getUsersRolesInProject(null, project);
	}

	/**
	 * Get the roles a user has in a project
	 * 
	 * @param user
	 * @param project
	 * @return List<RoleType> A list of the user's roles in the specified
	 *         project
	 * @throws PGSecurityException
	 */
	public List<RoleType> getUsersRolesInProject(String user,
			ProjectType project) throws ConnectException,
			PGSecuritySAMLVerificationException,
			PGSecurityInvalidSAMLException, PGSecurityException,
			NotAuthorisedFaultMessage {
		String stringAssertion = null;
		boolean groupFound = false;
		List<RoleType> lgt = new ArrayList<RoleType>();

		SAMLAssertion sa = getSAMLAssertion(user, project);
		if (sa != null) {
			Iterator<SAMLAttributeStatement> it = sa.getStatements();
			SAMLAttributeStatement st = null;
			SAMLAttribute attribute = null;

			while (it.hasNext() && !groupFound) {
				st = it.next();
				Iterator<SAMLAttribute> it2 = st.getAttributes();
				while (it2.hasNext()) {
					attribute = it2.next();
					if (attribute.getName().equals(
							PGSecurityConstants.SAML_ATTRIBUTE_MEMBERSHIP)) {
						Iterator<String> it3 = attribute.getValues();
						while (it3.hasNext()) {
							String g = it3.next();
							if (g.equals(project.getName())) {
								groupFound = true;
							}
							String id = it3.next();
							if (id.equals(project.getIdCode())) {
								groupFound = true;
							}
						}
						if (groupFound) {
							while (it2.hasNext()) {
								attribute = it2.next();
								if (attribute
										.getName()
										.equals(
												PGSecurityConstants.SAML_ATTRIBUTE_ROLE)) {
									it3 = attribute.getValues();
									while (it3.hasNext()) {
										String r = it3.next();
										lgt.add(new RoleType(r, null));
									}
								}
							}
						}
					}
				}
			}
		}

		return lgt;
	}

	/**
	 * Get the groups I have in a project
	 * 
	 * @param project
	 * @return List<GroupType> A list of the user's groups in the specified
	 *         project
	 * @throws PGSecurityException
	 */
	public List<GroupType> getMyGroupsInProject(ProjectType project)
			throws ConnectException, PGSecuritySAMLVerificationException,
			PGSecurityInvalidSAMLException, PGSecurityException,
			NotAuthorisedFaultMessage {
		return getUsersGroupsInProject(null, project);
	}

	/**
	 * Get the groups a user has in a project
	 * 
	 * @param user
	 * @param project
	 * @return List<GroupType> A list of the user's groups in the specified
	 *         project
	 * @throws PGSecurityException
	 */
	public List<GroupType> getUsersGroupsInProject(String user,
			ProjectType project) throws ConnectException,
			PGSecuritySAMLVerificationException,
			PGSecurityInvalidSAMLException, PGSecurityException,
			NotAuthorisedFaultMessage {
		String stringAssertion = null;
		boolean groupFound = false;
		List<GroupType> lgt = new ArrayList<GroupType>();

		SAMLAssertion sa = getSAMLAssertion(user, project);
		if (sa != null) {
			Iterator<SAMLAttributeStatement> it = sa.getStatements();
			SAMLAttributeStatement st = null;
			SAMLAttribute attribute = null;

			while (it.hasNext() && !groupFound) {
				st = it.next();
				Iterator<SAMLAttribute> it2 = st.getAttributes();
				while (it2.hasNext()) {
					attribute = it2.next();
					if (attribute.getName().equals(
							PGSecurityConstants.SAML_ATTRIBUTE_MEMBERSHIP)) {
						Iterator<String> it3 = attribute.getValues();
						while (it3.hasNext()) {
							String g = it3.next();
							if (g.equals(project.getName())) {
								groupFound = true;
							}
							String id = it3.next();
							if (id.equals(project.getIdCode())) {
								groupFound = true;
							}
						}
						if (groupFound) {
							while (it2.hasNext()) {
								attribute = it2.next();
								if (attribute
										.getName()
										.equals(
												PGSecurityConstants.SAML_ATTRIBUTE_GROUP)) {
									it3 = attribute.getValues();
									while (it3.hasNext()) {
										String r = it3.next();
										String s = it3.next();
										lgt.add(new GroupType(r, s, null));
									}
								}
							}
						}
					}
				}
			}
		}

		return lgt;
	}

	public GroupType[] getGroupsInProject(ProjectType pt)
			throws ConnectException, PGSecurityException,
			NotAuthorisedFaultMessage {
		try {
			return attributeAuthority.getGroupsInProject(pt);
		} catch (ProcessingFaultMessage pfm) {
			log.error(pfm.getMessage());
			throw new PGSecurityException(pfm.getMessage());
		} catch (NotAuthorisedFaultMessage nafm) {
			throw nafm;
		} catch (AxisFault fault) {
			if (fault.getCause() instanceof ConnectException) {
				throw (ConnectException) fault.getCause();
			} else if (fault.getCause() instanceof NoRouteToHostException) {
				throw  new ConnectException(fault.getCause().getMessage());
			} else if (fault.getCause() instanceof UnknownHostException) {
				throw  new ConnectException(fault.getCause().getMessage());
			} else {
				throw new RuntimeException(fault);
			}
		} catch (RemoteException ex) {
			throw new RuntimeException(ex);
		}
	}

	public RoleType[] getRolesInProject(ProjectType pt) throws ConnectException,
			PGSecurityException, NotAuthorisedFaultMessage {
		try {
			return attributeAuthority.getRolesInProject(pt);
		} catch (ProcessingFaultMessage pfm) {
			log.error(pfm.getMessage());
			throw new PGSecurityException(pfm.getMessage());
		} catch (NotAuthorisedFaultMessage nafm) {
			throw nafm;
		} catch (AxisFault fault) {
			if (fault.getCause() instanceof ConnectException) {
				throw (ConnectException) fault.getCause();
			} else if (fault.getCause() instanceof NoRouteToHostException) {
				throw  new ConnectException(fault.getCause().getMessage());
			} else if (fault.getCause() instanceof UnknownHostException) {
				throw  new ConnectException(fault.getCause().getMessage());
			} else {
				throw new RuntimeException(fault);
			}
		} catch (RemoteException ex) {
			throw new RuntimeException(ex);
		}
	}

	public String[] getUsersInProject(ProjectType pt) throws ConnectException,
			PGSecurityException, NotAuthorisedFaultMessage {
		try {
			return attributeAuthority.getUsersInProject(pt);
		} catch (ProcessingFaultMessage pfm) {
			log.error(pfm.getMessage());
			throw new PGSecurityException(pfm.getMessage());
		} catch (NotAuthorisedFaultMessage nafm) {
			throw nafm;
		} catch (AxisFault fault) {
			if (fault.getCause() instanceof ConnectException) {
				throw (ConnectException) fault.getCause();
			} else if (fault.getCause() instanceof NoRouteToHostException) {
				throw  new ConnectException(fault.getCause().getMessage());
			} else if (fault.getCause() instanceof UnknownHostException) {
				throw  new ConnectException(fault.getCause().getMessage());
			} else {
				throw new RuntimeException(fault);
			}
		} catch (RemoteException ex) {
			throw new RuntimeException(ex);
		}
	}

	public String[] getUsersInGroupInProject(ProjectType pt, GroupType gt)
			throws ConnectException, PGSecurityException,
			NotAuthorisedFaultMessage {
		try {
			return attributeAuthority.getUsersInGroupInProject(gt, pt);
		} catch (ProcessingFaultMessage pfm) {
			log.error(pfm.getMessage());
			throw new PGSecurityException(pfm.getMessage());
		} catch (NotAuthorisedFaultMessage nafm) {
			throw nafm;
		} catch (AxisFault fault) {
			if (fault.getCause() instanceof ConnectException) {
				throw (ConnectException) fault.getCause();
			} else if (fault.getCause() instanceof NoRouteToHostException) {
				throw  new ConnectException(fault.getCause().getMessage());
			} else if (fault.getCause() instanceof UnknownHostException) {
				throw  new ConnectException(fault.getCause().getMessage());
			} else {
				throw new RuntimeException(fault);
			}
		} catch (RemoteException ex) {
			throw new RuntimeException(ex);
		}
	}

	public String[] getUsersInProjectWithRole(ProjectType pt, RoleType rt)
			throws ConnectException, PGSecurityException,
			NotAuthorisedFaultMessage {
		try {
			return attributeAuthority.getUsersInProjectWithRole(rt, pt);
		} catch (ProcessingFaultMessage pfm) {
			log.error(pfm.getMessage());
			throw new PGSecurityException(pfm.getMessage());
		} catch (NotAuthorisedFaultMessage nafm) {
			throw nafm;
		} catch (AxisFault fault) {
			if (fault.getCause() instanceof ConnectException) {
				throw (ConnectException) fault.getCause();
			} else if (fault.getCause() instanceof NoRouteToHostException) {
				throw  new ConnectException(fault.getCause().getMessage());
			} else if (fault.getCause() instanceof UnknownHostException) {
				throw  new ConnectException(fault.getCause().getMessage());
			} else {
				throw new RuntimeException(fault);
			}
		} catch (RemoteException ex) {
			throw new RuntimeException(ex);
		}
	}

	public String[] getUsersInGroupInProjectWithRole(ProjectType pt,
			GroupType gt, RoleType rt) throws ConnectException,
			PGSecurityException, NotAuthorisedFaultMessage {
		try {
			return attributeAuthority.getUsersInGroupInProjectWithRole(gt, rt,
					pt);
		} catch (ProcessingFaultMessage pfm) {
			log.error(pfm.getMessage());
			throw new PGSecurityException(pfm.getMessage());
		} catch (NotAuthorisedFaultMessage nafm) {
			throw nafm;
		} catch (AxisFault fault) {
			if (fault.getCause() instanceof ConnectException) {
				throw (ConnectException) fault.getCause();
			} else if (fault.getCause() instanceof NoRouteToHostException) {
				throw  new ConnectException(fault.getCause().getMessage());
			} else if (fault.getCause() instanceof UnknownHostException) {
				throw  new ConnectException(fault.getCause().getMessage());
			} else {
				throw new RuntimeException(fault);
			}
		} catch (RemoteException ex) {
			throw new RuntimeException(ex);
		}
	}
	 
	public boolean changePassword(String uid, char[] newPassword, char[] oldPassword) throws ConnectException,
			PGSecurityException, NotAuthorisedFaultMessage {
		try {
			short[] nPassword = new short[newPassword.length];
			for (int i = 0; i < nPassword.length; i++) {
				nPassword[i] = (short) newPassword[i];
			}
			short[] oPassword = new short[oldPassword.length];
			for (int i = 0; i < oPassword.length; i++) {
				oPassword[i] = (short) oldPassword[i];
			}
			return attributeAuthority.changePassword(new ChangePasswordRequestType(uid, nPassword, oPassword));
		} catch (ProcessingFaultMessage pfm) {
			log.error(pfm.getMessage());
			throw new PGSecurityException(pfm.getMessage());
		} catch (NotAuthorisedFaultMessage nafm) {
			throw nafm;
		} catch (AxisFault fault) {
			if (fault.getCause() instanceof ConnectException) {
				throw (ConnectException) fault.getCause();
			} else if (fault.getCause() instanceof NoRouteToHostException) {
				throw  new ConnectException(fault.getCause().getMessage());
			} else if (fault.getCause() instanceof UnknownHostException) {
				throw  new ConnectException(fault.getCause().getMessage());
			} else {
				throw new RuntimeException(fault);
			}
		} catch (RemoteException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public boolean changePassword(char[] newPassword, char[] oldPassword) throws ConnectException,
	PGSecurityException, NotAuthorisedFaultMessage {
		return changePassword(null, newPassword, oldPassword);
	}

	public List<InternetAddress> lookUpEmailAddress(ProjectType pt,
			GroupType gt, RoleType rt) throws ConnectException,
			PGSecurityException, NotAuthorisedFaultMessage {

		List<InternetAddress> lia = new ArrayList<InternetAddress>();
		String[] smail = null;
		try {
			smail = attributeAuthority.getEmailAddressForUserWithPrivileges(pt, gt, rt);
		} catch (ProcessingFaultMessage pfm) {
			log.error(pfm.getMessage());
		} catch (InputFaultMessage ifm) {
			log.error(ifm.getMessage());
		} catch (NotAuthorisedFaultMessage nafm) {
			log.error(nafm.getMessage());
		} catch (AxisFault fault) {
			log.error(fault.getMessage());
		} catch (RemoteException ex) {
			log.error(ex.getMessage());
		}
		
		for(int i=0;i<smail.length;i++){
			try{
				InternetAddress email = new InternetAddress(smail[i]);
				lia.add(email);
			} catch (AddressException ae) {
				log.error("invalid email address found " + smail[i]);
			}
		}
		return lia;
	}
	
	/**
	 * Look up a user's email address given their Distinguished Name
	 * 
	 * @param user. The DN of the user to look up
	 * @return InternetAddress if found, otherwise null
	 */
	public InternetAddress lookUpEmailAddress(String user) {
		InternetAddress email = null;
		String smail = null;
		if(user!=null){
			try {
				smail = attributeAuthority.getEmailAddressForUser(user);
				if(smail!=null){
					email = new InternetAddress(smail);
				}
			} catch (AddressException ae) {
				log.error("invalid email address found " + smail
						+ " for user " + user);
			} catch (ProcessingFaultMessage pfm) {
				log.error(pfm.getMessage());
			} catch (InputFaultMessage ifm) {
				log.error(ifm.getMessage());
			} catch (NotAuthorisedFaultMessage nafm) {
				log.error(nafm.getMessage());
			} catch (AxisFault fault) {
				log.error(fault.getMessage());
			} catch (RemoteException ex) {
				log.error(ex.getMessage());
			}
		}
		return email;	
	}

	/**
	 * Look up a user's mobile phone number given their Distinguished Name.
	 * 
	 * This is used to send SMS notifications.
	 * 
	 * @param user. The DN of the user to look up
	 * @return mobileNumber if found, otherwise null
	 */
	public String lookUpMobileNumber(String user) {
		try {
			return attributeAuthority.getMobileNumberForUser(user);
		} catch (ProcessingFaultMessage pfm) {
			log.error(pfm.getMessage());
		} catch (InputFaultMessage ifm) {
			log.error(ifm.getMessage());
		} catch (NotAuthorisedFaultMessage nafm) {
			log.error(nafm.getMessage());
		} catch (AxisFault fault) {
			log.error(fault.getMessage());
		} catch (RemoteException ex) {
			log.error(ex.getMessage());
		}
		return null;
	}
	
	public PostProcessLoginResponseType postProcessLogin()
	throws ConnectException, PGSecurityException,
	NotAuthorisedFaultMessage {
		return postProcessLogin(null);
	}
	
	public PostProcessLoginResponseType postProcessLogin(String uid)
			throws ConnectException, PGSecurityException,
			NotAuthorisedFaultMessage {
		try {
			return attributeAuthority.postProcessLogin(uid);
		} catch (ProcessingFaultMessage pfm) {
			log.error(pfm.getMessage());
			throw new PGSecurityException(pfm.getMessage());
		} catch (InputFaultMessage ifm) {
			log.error(ifm.getMessage());
			throw new PGSecurityException(ifm.getMessage());
		} catch (NotAuthorisedFaultMessage nafm) {
			throw nafm;
		} catch (AxisFault fault) {
			if (fault.getCause() instanceof ConnectException) {
				throw (ConnectException) fault.getCause();
			} else if (fault.getCause() instanceof NoRouteToHostException) {
				throw new ConnectException(fault.getCause().getMessage());
			} else if (fault.getCause() instanceof UnknownHostException) {
				throw new ConnectException(fault.getCause().getMessage());
			} else {
				throw new RuntimeException(fault);
			}
		} catch (RemoteException ex) {
			throw new RuntimeException(ex);
		}
	}
	public boolean recordLoginAttempt(String username, boolean auth, Date ts, String ip,
			String credential) throws ConnectException, PGSecurityException,
			NotAuthorisedFaultMessage {
		try {
			Calendar c = Calendar.getInstance();
			c.setTime(ts);
			return attributeAuthority.recordLoginAttempt(username, auth, c, ip, credential);
		} catch (ProcessingFaultMessage pfm) {
			log.error(pfm.getMessage());
			throw new PGSecurityException(pfm.getMessage());
		} catch (InputFaultMessage ifm) {
			log.error(ifm.getMessage());
			throw new PGSecurityException(ifm.getMessage());
		} catch (NotAuthorisedFaultMessage nafm) {
			throw nafm;
		} catch (AxisFault fault) {
			if (fault.getCause() instanceof ConnectException) {
				throw (ConnectException) fault.getCause();
			} else if (fault.getCause() instanceof NoRouteToHostException) {
				throw new ConnectException(fault.getCause().getMessage());
			} else if (fault.getCause() instanceof UnknownHostException) {
				throw new ConnectException(fault.getCause().getMessage());
			} else {
				throw new RuntimeException(fault);
			}
		} catch (RemoteException ex) {
			throw new RuntimeException(ex);
		}
	}
	public boolean isAccountDormant(String username) throws ConnectException, PGSecurityException,
			NotAuthorisedFaultMessage {
		try {
			return attributeAuthority.isAccountDormant(username);
		} catch (ProcessingFaultMessage pfm) {
			log.error(pfm.getMessage());
			throw new PGSecurityException(pfm.getMessage());
		} catch (InputFaultMessage ifm) {
			log.error(ifm.getMessage());
			throw new PGSecurityException(ifm.getMessage());
		} catch (NotAuthorisedFaultMessage nafm) {
			throw nafm;
		} catch (AxisFault fault) {
			if (fault.getCause() instanceof ConnectException) {
				throw (ConnectException) fault.getCause();
			} else if (fault.getCause() instanceof NoRouteToHostException) {
				throw new ConnectException(fault.getCause().getMessage());
			} else if (fault.getCause() instanceof UnknownHostException) {
				throw new ConnectException(fault.getCause().getMessage());
			} else {
				throw new RuntimeException(fault);
			}
		} catch (RemoteException ex) {
			throw new RuntimeException(ex);
		}
	}
}
