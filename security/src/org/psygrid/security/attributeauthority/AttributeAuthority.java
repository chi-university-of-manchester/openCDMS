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
package org.psygrid.security.attributeauthority;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.StartTlsRequest;
import javax.naming.ldap.StartTlsResponse;
import javax.net.ssl.SSLSession;
import javax.xml.rpc.ServiceException;
import javax.xml.rpc.server.ServletEndpointContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opensaml.SAMLAssertion;
import org.opensaml.SAMLAttribute;
import org.opensaml.SAMLAttributeStatement;
import org.opensaml.SAMLException;
import org.opensaml.SAMLNameIdentifier;
import org.opensaml.SAMLStatement;
import org.opensaml.SAMLSubject;
import org.psygrid.logging.AuditLogger;
import org.psygrid.security.ConfigurableSecureService;
import org.psygrid.security.LDAPDirectoryVendors;
import org.psygrid.security.LDAPPasswordHashScheme;
import org.psygrid.security.PGSecurityConstants;
import org.psygrid.security.PGSecurityException;
import org.psygrid.security.accesscontrol.AccessEnforcementFunction;
import org.psygrid.security.attributeauthority.dao.DAOException;
import org.psygrid.security.attributeauthority.dao.GroupDAO;
import org.psygrid.security.attributeauthority.dao.ProjectDAO;
import org.psygrid.security.attributeauthority.dao.UserDAO;
import org.psygrid.security.attributeauthority.model.Factory;
import org.psygrid.security.attributeauthority.model.hibernate.Attribute;
import org.psygrid.security.attributeauthority.model.hibernate.Group;
import org.psygrid.security.attributeauthority.model.hibernate.HibernateFactory;
import org.psygrid.security.attributeauthority.model.hibernate.Project;
import org.psygrid.security.attributeauthority.model.hibernate.Role;
import org.psygrid.security.attributeauthority.model.hibernate.User;
import org.psygrid.security.attributeauthority.service.InputFaultMessage;
import org.psygrid.security.attributeauthority.service.ProcessingFaultMessage;
import org.psygrid.security.utils.PasswordUtilities;
import org.psygrid.security.utils.PropertyUtilities;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.w3c.dom.Document;

/**
 * @author jda
 * 
 */
public class AttributeAuthority extends ConfigurableSecureService {

	static protected long STATEMENT_LIFETIME = 120L;
	
	static protected long PASSWORD_LIFETIME_IN_S = 2592000L; // 30 days
	
	static protected long ACCOUNT_LOCKOUT_DURATION_IN_S = 1800L;	
	
	static protected int ALLOWED_LOGIN_ATTEMPTS = 3;
	
	static protected boolean PREVENT_PASSWORD_REUSE = false; 

	protected static final String COMPONENT_NAME = "AttributeAuthority";

	/** logger */
	public static Log sLog = LogFactory.getLog(AttributeAuthority.class);

	/**
	 * Audit logger
	 */
	protected static AuditLogger logHelper = new AuditLogger(
			AttributeAuthority.class);

	protected static Factory factory = null;

	protected static String XMLSchemaLocation = null;

	protected static String configurationFile = null;

	protected static String configurationSchemaFile = null;

	protected static Document configurationDOM = null;

	protected static AccessEnforcementFunction aef = null;

	protected static AASAMLIssuer si = null;

	private static boolean initialised = false;

	protected static String ldapDir = null;

	protected static String ldapRoot = null;

	protected static String ldapRootPassword = null;
	
	protected static String ldapBaseDN = null;
	
	protected static String ldapUserBaseDN = null;
	
	protected static String ldapPasswordHash = null;
	
	//OpenLDAP is the default
	protected static String ldapVendor = LDAPDirectoryVendors.OPENLDAP.toString();
	
	protected static boolean ldapUseTLS = false;

	public final int UF_NORMAL_ACCOUNT = 0x0200;
	
	UserDAO userDAO = null;

	ProjectDAO projectDAO = null;
	
	GroupDAO groupDAO = null;

	/**
	 * public constructor
	 */
	public AttributeAuthority() {
		super();
		sLog.debug("AttributeAuthority constructor");
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
		if (!initialised) {
			super.doInitialConfigure();
			if (xContext.getInitParameter("statementLifetime") == null) {
				throw new ServiceException(
						"Service not available: configuration error: missing statementLifetime entry in web.xml");
			} else {
				STATEMENT_LIFETIME = (new Long(xContext
						.getInitParameter("statementLifetime"))).longValue();
			}
			
			if (xContext.getInitParameter("passwordLifetime") == null) {
				throw new ServiceException(
						"Service not available: configuration error: missing passwordLifetime entry in web.xml");
			} else {
				PASSWORD_LIFETIME_IN_S = (new Long(xContext
						.getInitParameter("passwordLifetime"))).longValue();
			}
			
			if (xContext.getInitParameter("preventPasswordResuse") == null) {
				throw new ServiceException(
						"Service not available: configuration error: missing preventPasswordResuse entry in web.xml");
			} else {
				PREVENT_PASSWORD_REUSE = (new Boolean(xContext
						.getInitParameter("preventPasswordResuse"))).booleanValue();
			}
			
			if (xContext.getInitParameter("allowedLoginAttempts") == null) {
				throw new ServiceException(
						"Service not available: configuration error: missing allowedLoginAttempts entry in web.xml");
			} else {
				ALLOWED_LOGIN_ATTEMPTS = (new Integer(xContext
						.getInitParameter("allowedLoginAttempts"))).intValue();
			}

			if (xContext.getInitParameter("accountLockoutDuration") == null) {
				throw new ServiceException(
						"Service not available: configuration error: missing accountLockoutDuration entry in web.xml");
			} else {
				ACCOUNT_LOCKOUT_DURATION_IN_S = (new Long(xContext
						.getInitParameter("accountLockoutDuration"))).longValue();
			}
			
			sLog.debug("AA-statementLifetime: " + STATEMENT_LIFETIME + "\n");

			aef = new AccessEnforcementFunction();
			try {
				aef.initialise((ServletEndpointContext) oContext);
			} catch (PGSecurityException pgse) {
				throw new ServiceException("failed to initialise AEF");
			}
			if (xContext.getInitParameter("aaIssuerProperties") == null) {
				throw new ServiceException(
						"Service not available: configuration error: missing aaIssuerProperties entry in web.xml");
			} else {
				si = AASAMLIssuer.getInstance(xContext
						.getInitParameter("aaIssuerProperties"));
			}
			if (xContext.getInitParameter("attributeAuthorityProperties") == null) {
				throw new ServiceException(
						"Service not available: configuration error: missing attributeAuthorityProperties entry in web.xml");
			} else {
				Properties aaProperties = PropertyUtilities
						.getProperties(xContext
								.getInitParameter("attributeAuthorityProperties"));
				ldapDir = aaProperties
						.getProperty("org.psygrid.security.attributeauthority.ldapDirectoryURL");
				ldapRoot = aaProperties
						.getProperty("org.psygrid.security.attributeauthority.ldapRoot");
				ldapPasswordHash = aaProperties
						.getProperty("org.psygrid.security.attributeauthority.ldapPasswordHash");
				ldapUseTLS = Boolean.parseBoolean(aaProperties
						.getProperty("org.psygrid.security.attributeauthority.ldapUseTLS"));
				ldapVendor = aaProperties
						.getProperty("org.psygrid.security.attributeauthority.ldapVendor");
				ldapRootPassword = aaProperties
						.getProperty("org.psygrid.security.attributeauthority.ldapRootPassword");
				ldapBaseDN = aaProperties
						.getProperty("org.psygrid.security.attributeauthority.ldapBaseDN");
				ldapUserBaseDN = aaProperties
						.getProperty("org.psygrid.security.attributeauthority.ldapUserBaseDN");
			}
			factory = new HibernateFactory();
			initialised = true;
		}
	}

	/**
	 * @return UserDAO
	 */
	protected UserDAO getUserDAO() {
		if (userDAO == null) {
			ApplicationContext context = WebApplicationContextUtils
					.getWebApplicationContext(getServletContext());
			userDAO = (UserDAO) context.getBean("userDAOService");
		}
		return userDAO;
	}

	/**
	 * @return ProjectDAO
	 */
	protected ProjectDAO getProjectDAO() {
		if (projectDAO == null) {
			ApplicationContext context = WebApplicationContextUtils
					.getWebApplicationContext(getServletContext());
			projectDAO = (ProjectDAO) context.getBean("projectDAOService");
		}
		return projectDAO;
	}
	
	protected GroupDAO getGroupDAO() {
		
		if (groupDAO == null) {
			ApplicationContext context = WebApplicationContextUtils
					.getWebApplicationContext(getServletContext());
			groupDAO = (GroupDAO) context.getBean("groupDAOService");
		}
		return groupDAO;
		
	}

	/**
	 * @return Returns the configurationDOM.
	 */
	protected Document getConfigurationDOM() {
		return configurationDOM;
	}

	/**
	 * @param configurationDOM
	 *            The configurationDOM to set.
	 */
	protected void setConfigurationDOM(Document configurationDOM) {
		AttributeAuthority.configurationDOM = configurationDOM;
	}

	/**
	 * @return Returns the configurationFile.
	 */
	protected String getConfigurationFile() {
		return AttributeAuthority.configurationFile;
	}

	/**
	 * @param configurationFile
	 *            The configurationFile to set.
	 */
	protected void setConfigurationFile(String configurationFile) {
		AttributeAuthority.configurationFile = configurationFile;
	}

	/**
	 * @return Returns the xMLSchemaLocation.
	 */
	protected String getXMLSchemaLocation() {
		return AttributeAuthority.XMLSchemaLocation;
	}

	/**
	 * @param schemaLocation
	 *            The xMLSchemaLocation to set.
	 */
	protected void setXMLSchemaLocation(String schemaLocation) {
		AttributeAuthority.XMLSchemaLocation = schemaLocation;
	}

	/**
	 * @return Returns the configurationSchemaFile.
	 */
	protected String getConfigurationSchemaFile() {
		return AttributeAuthority.configurationSchemaFile;
	}

	/**
	 * @param configurationSchemaFile
	 *            The configurationSchemaFile to set.
	 */
	protected void setConfigurationSchemaFile(String configurationSchemaFile) {
		AttributeAuthority.configurationSchemaFile = configurationSchemaFile;
	}

	protected SAMLAssertion createNewSAMLAssertion(User u)
			throws ProcessingFaultMessage {
		Iterator<Attribute> it = u.getAttributes().iterator();
		List<SAMLStatement> lsas = new ArrayList<SAMLStatement>();
		if (it.hasNext()) {
			while (it.hasNext()) {
				Attribute a = it.next();
				lsas.add(createNewAttributeStatement(u, a.getProject()));
			}
			return createNewSAMLAssertion(lsas);
		}
		return null;
	}

	protected SAMLAssertion createNewSAMLAssertion(String u, List<Attribute> la)
			throws ProcessingFaultMessage {
		Iterator<Attribute> it = la.iterator();
		List<SAMLStatement> lsas = new ArrayList<SAMLStatement>();
		if (it.hasNext()) {
			while (it.hasNext()) {
				Attribute a = it.next();
				lsas.add(createNewAttributeStatement(u, a));
			}
		}
		return createNewSAMLAssertion(lsas);
	}
	
	public  SAMLAssertion createSystemSAMLAssertion() throws ProcessingFaultMessage{
		
		String dummyUser = "CN=System System, OU=users, O=any, C=UK";
		Project sysProject = new Project("SYSTEM");
		sysProject.setIdCode("-1");
		
		Role sysRole = new Role("System", "System");
		List<Role> roles = new ArrayList<Role>();
		roles.add(sysRole);
		
		Attribute a = new Attribute(sysProject, new ArrayList<Group>(), roles);
		List<SAMLStatement> lsas = new ArrayList<SAMLStatement>();
		lsas.add(createNewAttributeStatement(dummyUser, a));
		return createNewSAMLAssertion(lsas);
	}

	protected SAMLAssertion createNewSAMLAssertion(User u, Project p)
			throws ProcessingFaultMessage {
		List<SAMLStatement> lsas = new ArrayList<SAMLStatement>();
		lsas.add(createNewAttributeStatement(u, p));
		return createNewSAMLAssertion(lsas);
	}

	private SAMLAssertion createNewSAMLAssertion(List<SAMLStatement> lstatements) {
		SAMLAssertion sa = null;
		//Bug 918
		//if (lstatements.size() != 0) {
			sa = si.newAssertion(lstatements, STATEMENT_LIFETIME);
		//}
		try {
			sLog.debug(sa.toString());
			sa.verify();
		} catch (SAMLException se) {
			sLog.info(se.getMessage());
			se.printStackTrace();
		}
		return sa;
	}

	private SAMLAttributeStatement createNewAttributeStatement(User u, Project p)
			throws ProcessingFaultMessage {
		Attribute attribute = u.getAttributeByProject(p);
		return createNewAttributeStatement(u.getUserName(), attribute);
	}

	private SAMLAttributeStatement createNewAttributeStatement(String u,
			Attribute attribute) throws ProcessingFaultMessage {
		try {
			SAMLNameIdentifier nameId = new SAMLNameIdentifier(u, "", "");
			SAMLSubject subject = new SAMLSubject(nameId, Arrays
					.asList(new String[] { SAMLSubject.CONF_SENDER_VOUCHES }),
					null, null);
			Collection<SAMLAttribute> attributes = new ArrayList<SAMLAttribute>();
			SAMLAttribute attr = new SAMLAttribute(
					PGSecurityConstants.SAML_ATTRIBUTE_MEMBERSHIP,
					PGSecurityConstants.SAML_ATTRIBUTE_NS, null,
					STATEMENT_LIFETIME, Arrays.asList(new String[] {
							attribute.getProject().getProjectName(),
							attribute.getProject().getIdCode() }));
			attributes.add(attr);
			Iterator<Role> it = attribute.getRoles().iterator();
			while (it.hasNext()) {
				Role role = it.next();
				attr = new SAMLAttribute(
						PGSecurityConstants.SAML_ATTRIBUTE_ROLE,
						PGSecurityConstants.SAML_ATTRIBUTE_NS, null,
						STATEMENT_LIFETIME, Arrays.asList(new String[] { role
								.getRoleName() }));
				attributes.add(attr);
			}
			Iterator<Group> it2 = attribute.getGroups().iterator();
			while (it2.hasNext()) {
				Group group = it2.next();
				attr = new SAMLAttribute(
						PGSecurityConstants.SAML_ATTRIBUTE_GROUP,
						PGSecurityConstants.SAML_ATTRIBUTE_NS, null,
						STATEMENT_LIFETIME, Arrays.asList(new String[] {
								group.getGroupName(), group.getIdCode() }));
				attributes.add(attr);
			}
			return new SAMLAttributeStatement(subject, attributes);
		} catch (SAMLException se) {
			se.printStackTrace();
			throw new ProcessingFaultMessage(se.getMessage());
		}
	}
	public boolean _isAccountDormantViaUID(String username)
			throws java.rmi.RemoteException,
			org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage,
			org.psygrid.security.attributeauthority.service.ProcessingFaultMessage,
			org.psygrid.security.attributeauthority.service.InputFaultMessage {


		String dn = getCallersIdentityByUID(username);

		return _isAccountDormantViaDN(dn);
	}
	
	public boolean _isAccountDormantViaDN(String dn)
	throws java.rmi.RemoteException,
	org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage,
	org.psygrid.security.attributeauthority.service.ProcessingFaultMessage,
	org.psygrid.security.attributeauthority.service.InputFaultMessage {

		boolean dormant = false;

		try {
			User u = userDAO.getUserByName(dn);
			if (u != null) {
				dormant = u.isDormant();
			} else {
				throw new InputFaultMessage("User " + dn
						+ " does not exist");
			}
		} catch (DAOException doae) {
			doae.printStackTrace();
			sLog.error(doae.getMessage());
			throw new ProcessingFaultMessage(doae.getMessage());
		}
		return dormant;
	}

	protected String getCallersIdentityByUID(String uid) {
		Attributes matchAttrs = new BasicAttributes(true); // ignore attribute name case
		matchAttrs.put(new BasicAttribute("uid", uid));
		String filter = "(uid="+uid+")";
		SearchControls searchCtrls = new SearchControls();
		searchCtrls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		String dn = null;
		LdapContext ctx = null;
		try {
			ctx = createLDAPContext(ldapRoot, ldapRootPassword.toCharArray());		
			NamingEnumeration answer = ctx.search(ldapBaseDN, filter, searchCtrls);
			while(answer.hasMoreElements()){
				dn = ((SearchResult)answer.next()).getNameInNamespace();
				dn = dn.replace(",", ", ");
				dn = dn.replace("cn=", "CN=");
				dn = dn.replace("ou=", "OU=");
				dn = dn.replace("o=", "O=");
				dn = dn.replace("c=", "C=");
				
				//there should only be one!
				break;
			}
		} catch (NamingException ne) {
			sLog.error(ne.getMessage(),ne);
		} catch (IOException io) {
			sLog.error("Failed TLS negotiation with LDAP server",io);
		} finally {
			closeLDAPContext(ctx);
		}
		return dn;
	}

	protected boolean performLDAPAuthentication(String cid, char[] password) {
		boolean result = false;
		Hashtable env = new Hashtable();
		
		if(ldapPasswordHash!=null){
			if(ldapPasswordHash.equals(LDAPPasswordHashScheme.SHA.toString())){
				password = PasswordUtilities.hashPassword(password, LDAPPasswordHashScheme.SHA, sLog);
			}
		}
		LdapContext ctx = null;
		try{
			ctx = createLDAPContext(cid, password);
			result=true;
		} catch (NamingException ne) {
			sLog.info("Failed to bind to LDAP server for " + cid);
		} catch (IOException io) {
			sLog.error("Failed TLS negotiation with LDAP server");
		} finally {
			closeLDAPContext(ctx);
		}
		return result;
	}
	
	protected LdapContext createLDAPContext(String cid, char[] password) throws NamingException,
			IOException {
		
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, ldapDir);
		LdapContext ctx = null;
		
		if(ldapUseTLS){
			
			StartTlsResponse tls = null;			
			ctx = new InitialLdapContext(env, null);
			tls = (StartTlsResponse) ctx.extendedOperation(new StartTlsRequest());
			tls.negotiate();
			ctx.addToEnvironment(Context.SECURITY_AUTHENTICATION, "simple");
			ctx.addToEnvironment(Context.SECURITY_PRINCIPAL, cid);
			ctx.addToEnvironment(Context.SECURITY_CREDENTIALS, password);
			ctx.reconnect(null);

		} else {
			
			env.put(Context.SECURITY_AUTHENTICATION, "simple");
			env.put(Context.SECURITY_PRINCIPAL, cid);
			env.put(Context.SECURITY_CREDENTIALS, password);
			ctx = new InitialLdapContext(env, null);	

		}
		return ctx;
	}
	
	protected void closeLDAPContext(LdapContext ctx){
		try {
			if(ctx != null){
				ctx.close();
			}
		} catch (NamingException ne) {
			sLog.info("LDAP context close failure");
		}
	}
}
