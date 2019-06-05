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


//Created on Oct 11, 2005 by John Ainsworth
package org.psygrid.security.attributeauthority;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.InvalidAttributesException;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;
import javax.xml.rpc.server.ServiceLifecycle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opensaml.SAMLAssertion;
import org.psygrid.security.LDAPDirectoryVendors;
import org.psygrid.security.LDAPPasswordHashScheme;
import org.psygrid.security.PGSecurityConstants;
import org.psygrid.security.PGSecurityException;
import org.psygrid.security.PGSecurityInvalidSAMLException;
import org.psygrid.security.PGSecuritySAMLVerificationException;
import org.psygrid.security.RBACAction;
import org.psygrid.security.RBACRole;
import org.psygrid.security.accesscontrol.AEFAction;
import org.psygrid.security.accesscontrol.AEFGroup;
import org.psygrid.security.accesscontrol.AEFProject;
import org.psygrid.security.attributeauthority.dao.DAOException;
import org.psygrid.security.attributeauthority.dao.GroupDAO;
import org.psygrid.security.attributeauthority.dao.ObjectOutOfDateException;
import org.psygrid.security.attributeauthority.dao.ProjectDAO;
import org.psygrid.security.attributeauthority.dao.UserDAO;
import org.psygrid.security.attributeauthority.model.IProject;
import org.psygrid.security.attributeauthority.model.IUser;
import org.psygrid.security.attributeauthority.model.hibernate.Attribute;
import org.psygrid.security.attributeauthority.model.hibernate.Group;
import org.psygrid.security.attributeauthority.model.hibernate.GroupAttribute;
import org.psygrid.security.attributeauthority.model.hibernate.GroupLink;
import org.psygrid.security.attributeauthority.model.hibernate.Project;
import org.psygrid.security.attributeauthority.model.hibernate.Role;
import org.psygrid.security.attributeauthority.model.hibernate.User;
import org.psygrid.security.attributeauthority.service.AttributeAuthorityManagementPortType;
import org.psygrid.security.attributeauthority.service.InputFaultMessage;
import org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage;
import org.psygrid.security.attributeauthority.service.ProcessingFaultMessage;
import org.psygrid.security.attributeauthority.types.ResetPasswordRequestType;
import org.psygrid.security.utils.LDAPDistinguishedNameHelper;
import org.psygrid.security.utils.PasswordUtilities;
import org.psygrid.security.policyauthority.client.PAManagementClient;
import org.psygrid.security.utils.XMLUtilities;
import org.psygrid.www.xml.security.core.types.ActionType;
import org.psygrid.www.xml.security.core.types.AttributeType;
import org.psygrid.www.xml.security.core.types.GroupAttributeType;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.PolicyType;
import org.psygrid.www.xml.security.core.types.ProjectDescriptionType;
import org.psygrid.www.xml.security.core.types.ProjectType;
import org.psygrid.www.xml.security.core.types.UserPrivilegesType;
import org.psygrid.www.xml.security.core.types.UserType;
import org.w3c.dom.Document;

/**
 * @author jda
 * 
 */

public class AttributeAuthorityManagementPortTypeImpl extends
		AttributeAuthority implements AttributeAuthorityManagementPortType,
		ServiceLifecycle {

	static Object DB_LOCK = new Object();
	
	/** Logger */
	private static Log log = LogFactory
			.getLog(AttributeAuthorityManagementPortTypeImpl.class);

	public AttributeAuthorityManagementPortTypeImpl() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * Add a new user. If the user exists, replace their entry.
	 * 
	 * @see org.psygrid.security.attributeauthority.service.AttributeAuthorityManagementPortType#addUser(org.psygrid.www.xml.security.core.types.UserPrivilegesType[])
	 */
	public boolean addUser(
			org.psygrid.www.xml.security.core.types.UserPrivilegesType[] input)
			throws java.rmi.RemoteException,
			org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage,
			org.psygrid.security.attributeauthority.service.ProcessingFaultMessage,
			org.psygrid.security.attributeauthority.service.InputFaultMessage {
		String sa = null;

		final String METHOD_NAME = "addUser";
		String cid = getCallersIdentity();
		logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, cid, cid);

		try {

			if (!authorisedForAdministrationRequests(getCallerPrincipals())) {
				for (int i = 0; i < input.length; i++) {
					for (int j = 0; j < input[i].getAttribute().length; j++) {
						if (!_makePolicyDecision(
								new GroupType(),
								new ActionType(
										RBACAction.ACTION_AA_ADD_USER.toString(),
										RBACAction.ACTION_AA_ADD_USER.idAsString()),
								input[i].getAttribute(j).getProject())) {
							logHelper.logAccessDenied(COMPONENT_NAME,
									METHOD_NAME, cid, cid);
							throw new NotAuthorisedFaultMessage(
									"Administration Permission Denied");
						}
					}
				}
			}

		} catch (PGSecurityException pge) {
			throw new ProcessingFaultMessage(pge.getMessage());
		}
		
	
		UserDAO userDAO = getUserDAO();
		for (int i = 0; i < input.length; i++) {
			try {

				User u = userDAO.getUserByName(input[i].getUser().getDistinguishedName());
				if (u != null) {
					// user exists, so update
					updateUser(new UserPrivilegesType[] { input[i] });
				} else {
					if (addToLDAP(input[i].getUser())) {
						synchronized (DB_LOCK) {
							IUser user = factory.createUser(input[i],
									getProjectDAO());
							user.setPasswordChangeRequired(true);
							user.setDormant(false);
							userDAO.addUser(user);
						}
					} else {
						throw new ProcessingFaultMessage(RBACAction.ACTION_AA_ADD_USER+" Failed to add user to LDAP");
					}
					
				}

			} catch (DAOException doa) {
				throw new ProcessingFaultMessage(doa.getMessage());
			} catch (ObjectOutOfDateException oood) {
				throw new ProcessingFaultMessage(oood.getMessage());
			}
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psygrid.security.attributeauthority.service.AttributeAuthorityManagementPortType#deleteUser(java.lang.String[])
	 */
	public boolean deleteUser(String[] input)
			throws java.rmi.RemoteException,
			org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage,
			org.psygrid.security.attributeauthority.service.ProcessingFaultMessage,
			org.psygrid.security.attributeauthority.service.InputFaultMessage {
		
		final String METHOD_NAME = "deleteUser";
		String cid = getCallersIdentity();
		logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, cid, cid);

		if (!_makePolicyDecision(new GroupType(), new ActionType(
				RBACAction.ACTION_AA_DELETE_USER.toString(),
				RBACAction.ACTION_AA_DELETE_USER.idAsString()), new ProjectType(
				PGSecurityConstants.SYSTEM_PROJECT,
				PGSecurityConstants.SYSTEM_PROJECT_ID, null, null, true))) {
			try {
				if (!authorisedForAdministrationRequests(getCallerPrincipals())) {
					logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, cid, cid);
					throw new NotAuthorisedFaultMessage(
							"Administration Permission Denied");
				}
			} catch (PGSecurityException pge) {
				throw new ProcessingFaultMessage(pge.getMessage());
			}
		}
		UserDAO userDAO = getUserDAO();


		for (int i = 0; i < input.length; i++) {
			try{
				// UoW
				synchronized (DB_LOCK) {
					User u = userDAO.getUserByName(input[i]);
					if (u != null) {
						u.removeAllAttributes();
						userDAO.updateUser(u);
					}
				}
				// UoW
				try {
					User u = userDAO.getUserByName(input[i]);
					if(u != null){
//						//The user is not deleted, but is marked as dormant
//						//Create a password that is very difficult to guess
//						Date d = new Date();
//						String ip1 = input[i]+d.getTime()+System.getProperty("org.psygrid.security.policyauthority.client.trustStorePassword");
//						char[] ip2 = PasswordUtilities.hashPassword(ip1.toCharArray(), LDAPPasswordHashScheme.SHA, log);
//						short[] ip3 = new short[ip2.length];
//						for(int k=0;k<ip3.length;k++){
//							ip3[k]=(short)ip2[k];
//						}
//						ResetPasswordRequestType rprt = new ResetPasswordRequestType(getUIDFromCN(input[i]), ip3);
//						resetPassword(rprt);
						userDAO.markUserAsDormant(u.getId());                          
					}
				} catch (DAOException e) {
					throw new ProcessingFaultMessage(e);
				}
			} catch (DAOException doa) {
				throw new ProcessingFaultMessage(doa.getMessage());
			}

		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psygrid.security.attributeauthority.service.AttributeAuthorityManagementPortType#addGroupToUser(org.psygrid.www.xml.security.core.types.UserPrivilegesType[])
	 */
	public boolean addProjectToUser(
			org.psygrid.www.xml.security.core.types.UserPrivilegesType[] input)
			throws java.rmi.RemoteException,
			org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage,
			org.psygrid.security.attributeauthority.service.ProcessingFaultMessage,
			org.psygrid.security.attributeauthority.service.InputFaultMessage {

		final String METHOD_NAME = "addProjectToUser";
		String cid = getCallersIdentity();
		logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, cid, cid);
		
		UserDAO userDAO = getUserDAO();
		for (int i = 0; i < input.length; i++) {
			for (int j = 0; j < input[i].getAttribute().length; j++) {
				if (!_makePolicyDecision(new GroupType(), new ActionType(
						RBACAction.ACTION_AA_MODIFY_USER.toString(),
						RBACAction.ACTION_AA_MODIFY_USER.idAsString()), input[i]
						.getAttribute(j).getProject())) {
					try {
						if (!authorisedForAdministrationRequests(getCallerPrincipals())) {
				             logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, cid, cid);
				             throw new NotAuthorisedFaultMessage(
									"Administration Permission Denied");
						}
					} catch (PGSecurityException pge) {
						throw new ProcessingFaultMessage(pge.getMessage());
					}
				}
			}
		}
		for (int i = 0; i < input.length; i++) {
			try {
//				UoW
				synchronized(DB_LOCK){
				User u = userDAO.getUserByName(input[i].getUser().getDistinguishedName());
				if (u != null) {
					u.setPDAO(getProjectDAO());
					u.setUDAO(getUserDAO());
					u.addAttribute(input[i].getAttribute());
				}
				}
//				UoW
			} catch (DAOException doa) {
				throw new ProcessingFaultMessage(doa.getMessage());
			}
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psygrid.security.attributeauthority.service.AttributeAuthorityManagementPortType#addRoleInGroupToUser(org.psygrid.www.xml.security.core.types.UserPrivilegesType[])
	 */
	public boolean addRoleInProjectToUser(
			org.psygrid.www.xml.security.core.types.UserPrivilegesType[] input)
			throws java.rmi.RemoteException,
			org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage,
			org.psygrid.security.attributeauthority.service.ProcessingFaultMessage,
			org.psygrid.security.attributeauthority.service.InputFaultMessage {

		final String METHOD_NAME = "addRoleInProjectToUser";
		String cid = getCallersIdentity();
		logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, cid, cid);

		UserDAO userDAO = getUserDAO();
		for (int i = 0; i < input.length; i++) {
			for (int j = 0; j < input[i].getAttribute().length; j++) {
				if (!_makePolicyDecision(new GroupType(), new ActionType(
						RBACAction.ACTION_AA_MODIFY_USER.toString(),
						RBACAction.ACTION_AA_MODIFY_USER.idAsString()), input[i]
						.getAttribute(j).getProject())) {
					try {
						if (!authorisedForAdministrationRequests(getCallerPrincipals())) {
				             logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, cid, cid);
				             throw new NotAuthorisedFaultMessage(
									"Administration Permission Denied");
						}
					} catch (PGSecurityException pge) {
						throw new ProcessingFaultMessage(pge.getMessage());
					}
				}
			}
		}
		for (int i = 0; i < input.length; i++) {
			try {
//				UoW
				synchronized(DB_LOCK){
				User u = userDAO.getUserByName(input[i].getUser().getDistinguishedName());
				if (u != null) {
					u.setPDAO(getProjectDAO());
					u.setUDAO(getUserDAO());
					u.addRoleInProject(input[i].getAttribute(),
							getProjectDAO());
				} 
				}
//				UoW
			} catch (DAOException doa) {
				throw new ProcessingFaultMessage(doa.getMessage());
			}

		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psygrid.security.attributeauthority.service.AttributeAuthorityManagementPortType#deleteGroupFromUser(org.psygrid.www.xml.security.core.types.UserPrivilegesType[])
	 */
	public boolean deleteProjectFromUser(
			org.psygrid.www.xml.security.core.types.UserPrivilegesType[] input)
			throws java.rmi.RemoteException,
			org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage,
			org.psygrid.security.attributeauthority.service.ProcessingFaultMessage,
			org.psygrid.security.attributeauthority.service.InputFaultMessage {

		final String METHOD_NAME = "deleteProjectFromUser";
		String cid = getCallersIdentity();
		logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, cid, cid);


		UserDAO userDAO = getUserDAO();
		for (int i = 0; i < input.length; i++) {
			for (int j = 0; j < input[i].getAttribute().length; j++) {
				if (!_makePolicyDecision(new GroupType(), new ActionType(
						RBACAction.ACTION_AA_MODIFY_USER.toString(),
						RBACAction.ACTION_AA_MODIFY_USER.idAsString()), input[i]
						.getAttribute(j).getProject())) {
					try {
						if (!authorisedForAdministrationRequests(getCallerPrincipals())) {
							logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, cid, cid);
							throw new NotAuthorisedFaultMessage(
									"Administration Permission Denied");
						}
					} catch (PGSecurityException pge) {
						throw new ProcessingFaultMessage(pge.getMessage());
					}
				}
			}
		}
		for (int i = 0; i < input.length; i++) {
			try {
				// UoW
				synchronized (DB_LOCK) {
					User u = userDAO.getUserByName(input[i].getUser()
							.getDistinguishedName());
					if (u != null) {
						u.removeProject(input[i].getAttribute());
						userDAO.updateUser(u);
					}
				}
				// UoW
			} catch (DAOException doa) {
				throw new ProcessingFaultMessage(doa.getMessage());
			}
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psygrid.security.attributeauthority.service.AttributeAuthorityManagementPortType#deleteRoleInGroupFromUser(org.psygrid.www.xml.security.core.types.UserPrivilegesType[])
	 */
	public boolean deleteRoleInProjectFromUser(
			org.psygrid.www.xml.security.core.types.UserPrivilegesType[] input)
			throws java.rmi.RemoteException,
			org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage,
			org.psygrid.security.attributeauthority.service.ProcessingFaultMessage,
			org.psygrid.security.attributeauthority.service.InputFaultMessage {

		final String METHOD_NAME = "deleteRoleInProjectFromUser";
		String cid = getCallersIdentity();
		logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, cid, cid);
		
		UserDAO userDAO = getUserDAO();

		for (int i = 0; i < input.length; i++) {
			for (int j = 0; j < input[i].getAttribute().length; j++) {
				if (!_makePolicyDecision(new GroupType(), new ActionType(
						RBACAction.ACTION_AA_MODIFY_USER.toString(),
						RBACAction.ACTION_AA_MODIFY_USER.idAsString()), input[i]
						.getAttribute(j).getProject())) {
					try {
						if (!authorisedForAdministrationRequests(getCallerPrincipals())) {
							logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, cid, cid);
							throw new NotAuthorisedFaultMessage(
									"Administration Permission Denied");
						}
					} catch (PGSecurityException pge) {
						throw new ProcessingFaultMessage(pge.getMessage());
					}
				}
			}
		}
		for (int i = 0; i < input.length; i++) {
			try {
//				UoW
				synchronized(DB_LOCK){
				User u = userDAO.getUserByName(input[i].getUser().getDistinguishedName());
				if (u != null) {
					u.removeRoleFromProject(input[i].getAttribute());
					userDAO.updateUser(u);
				}
				}
//				UoW
			} catch (DAOException doa) {
				throw new ProcessingFaultMessage(doa.getMessage());
			}
		}
		return true;
	}

	/*
	 * Incremental update - preserve everything that exists for this user
	 * (non-Javadoc)
	 * 
	 * @see org.psygrid.security.attributeauthority.service.AttributeAuthorityManagementPortType#updateUser(org.psygrid.www.xml.security.core.types.UserPrivilegesType[])
	 */
	public boolean updateUser(
			org.psygrid.www.xml.security.core.types.UserPrivilegesType[] input)
			throws java.rmi.RemoteException,
			org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage,
			org.psygrid.security.attributeauthority.service.ProcessingFaultMessage,
			org.psygrid.security.attributeauthority.service.InputFaultMessage {

		boolean rc = true;

		final String METHOD_NAME = "updateUser";
		String cid = getCallersIdentity();
		logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, cid, cid);

		UserDAO userDAO = getUserDAO();
	
		for (int i = 0; i < input.length; i++) {	
			List<AttributeType> allowed = new ArrayList<AttributeType>();
			if(input[i].getAttribute()!=null){
				for (int j = 0; j < input[i].getAttribute().length; j++) {
					try{
						if (!authorisedForAdministrationRequests(getCallerPrincipals())) {
							if (!_makePolicyDecision(new GroupType(), new ActionType(
									RBACAction.ACTION_AA_MODIFY_USER.toString(),
									RBACAction.ACTION_AA_MODIFY_USER.idAsString()), 
									input[i].getAttribute(j).getProject())) {
								continue;
							}
						}
					} catch (PGSecurityException e) {
						throw new ProcessingFaultMessage(e);
					}
					allowed.add(input[i].getAttribute(j));	
				}
			}
			if(allowed.size()<1){
				//check to see if project independent data can be updated
				if (!_makePolicyDecision(new GroupType(), new ActionType(
						RBACAction.ACTION_AA_MODIFY_USER.toString(),
						RBACAction.ACTION_AA_MODIFY_USER.idAsString()),
						new ProjectType(PGSecurityConstants.ANY,
								PGSecurityConstants.ANY, null, null, true))) {
					try {
						if (!authorisedForAdministrationRequests(getCallerPrincipals())) {
							logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, cid, cid);
							throw new NotAuthorisedFaultMessage(
									"Administration Permission Denied");
						}
					} catch (PGSecurityException pge) {
						throw new ProcessingFaultMessage(pge.getMessage());
					}
				}
			}
			
			try {			
				User oldUser = userDAO.getUserByName(input[i].getUser().getDistinguishedName());
				if ((oldUser != null) && (allowed.size()>0)) {
					synchronized(DB_LOCK){
					oldUser.setPDAO(getProjectDAO());
					oldUser.setUDAO(getUserDAO());
					oldUser.addAttribute(allowed.toArray(new AttributeType[allowed.size()]));
					}		
				} else {
					rc = false;
				}


				if(input[i].getUser().getEmailAddress()!=null
						&& !input[i].getUser().getEmailAddress().equals("")){
					ModificationItem[] mods = new ModificationItem[1];
					mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
							new BasicAttribute("mail", input[i].getUser().getEmailAddress()));
					LdapContext ctx = null;
					try {
						ctx = createLDAPContext(ldapRoot, ldapRootPassword.toCharArray());	
						try {
							ctx.modifyAttributes(input[i].getUser().getDistinguishedName(), mods);
						} catch (NamingException ne) {
							sLog.error(ne.getMessage());
							throw new ProcessingFaultMessage(ne.getMessage());
						}
					} catch (NamingException ne) {
						sLog.error(ne.getMessage());
						throw new ProcessingFaultMessage(ne.getMessage());
					} catch (IOException io) {
						sLog.error("Failed TLS negotiation with LDAP server",io);
					} finally {
						closeLDAPContext(ctx);
					}
				} else {
					ModificationItem[] mods = new ModificationItem[1];
					mods[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE,
							new BasicAttribute("mail"));
					LdapContext ctx = null;
					try {
						ctx = createLDAPContext(ldapRoot, ldapRootPassword.toCharArray());	
						try {
							ctx.modifyAttributes(input[i].getUser().getDistinguishedName(), mods);
						//can throw exception if mail address was never set
						} catch (NamingException ne) {
							sLog.info(ne.getMessage());
						}
					} catch (NamingException ne) {
						sLog.info(ne.getMessage(),ne);
					}  catch (IOException io) {
						sLog.error("Failed TLS negotiation with LDAP server",io);
					} finally {
						closeLDAPContext(ctx);
					}
				}
				if(input[i].getUser().getMobileNumber()!=null && 
						!(input[i].getUser().getMobileNumber().equals(""))){
					ModificationItem[] mods = new ModificationItem[1];
					mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
							new BasicAttribute("mobile", input[i].getUser().getMobileNumber()));
					LdapContext ctx = null;
					try {
						ctx = createLDAPContext(ldapRoot, ldapRootPassword.toCharArray());	
						try {
							ctx.modifyAttributes(input[i].getUser().getDistinguishedName(), mods);
						} catch (NamingException ne) {
							sLog.error(ne.getMessage(),ne);
							throw new ProcessingFaultMessage(ne.getMessage(),ne);
						}
					} catch (NamingException ne) {
						sLog.error(ne.getMessage(),ne);
						throw new ProcessingFaultMessage(ne.getMessage(),ne);
					}  catch (IOException io) {
						sLog.error("Failed TLS negotiation with LDAP server",io);
					} finally {
						closeLDAPContext(ctx);
					}
				} else {
					ModificationItem[] mods = new ModificationItem[1];
					mods[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE,
							new BasicAttribute("mobile"));
					LdapContext ctx = null;
					try {
						ctx = createLDAPContext(ldapRoot, ldapRootPassword.toCharArray());	
						try {
							ctx.modifyAttributes(input[i].getUser().getDistinguishedName(), mods);
						//can throw exception if mobile address was never set
						} catch (NamingException ne) {
							sLog.info(ne.getMessage());
						}
					} catch (NamingException ne) {
						sLog.info(ne.getMessage(),ne);
					}  catch (IOException io) {
						sLog.error("Failed TLS negotiation with LDAP server",io);
					} finally {
						closeLDAPContext(ctx);
					}
				}
			} catch (DAOException doa) {
				throw new ProcessingFaultMessage(doa.getMessage());
			}
		}
		return rc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psygrid.security.attributeauthority.service.AttributeAuthorityConfigurationPortType#updateConfiguration(java.lang.String)
	 */
	public boolean updateConfiguration(String config) throws RemoteException,
			InputFaultMessage, ProcessingFaultMessage,
			NotAuthorisedFaultMessage {
		String sxml = null;
		boolean rc = false;
		Document doc = null;

		final String METHOD_NAME = "updateConfiguration";
		String cid = getCallersIdentity();
		logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, cid, cid);
		
		if (!_makePolicyDecision(new GroupType(), new ActionType(
				RBACAction.ACTION_AA_UPDATE_CONFIGURATION.toString(),
				RBACAction.ACTION_AA_UPDATE_CONFIGURATION.idAsString()),
				new ProjectType(PGSecurityConstants.SYSTEM_PROJECT,
						PGSecurityConstants.SYSTEM_PROJECT_ID, null, null, true))) {
			try {
				if (!authorisedForAdministrationRequests(getCallerPrincipals())) {
					logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, cid, cid);
					throw new NotAuthorisedFaultMessage(
							"Administration Permission Denied");
				}
			} catch (PGSecurityException pge) {
				throw new ProcessingFaultMessage(pge.getMessage());
			}
		}
		if (config != null) {
			if ((doc = new XMLUtilities().toDocument(config, true,
					getXMLSchemaLocation() + File.separator
							+ getConfigurationSchemaFile())) != null) {
				synchronized (getConfigurationDOM()) {
					setConfigurationDOM(doc);
					if ((sxml = XMLUtilities.domToString(getConfigurationDOM())) == null) {
						throw new ProcessingFaultMessage("internal error 007");
					}
					try {
						FileWriter configFileWriter = new FileWriter(
								getConfigurationFile());
						configFileWriter.write(sxml);
						configFileWriter.flush();
					} catch (IOException ioe) {
						log.fatal("io error " + ioe.getMessage());
						throw new ProcessingFaultMessage("internal error 008");
					}
				}
				rc = true;
			} else {
				log.fatal("invalid XML " + config);
				throw new InputFaultMessage("Non-conformist XML " + config);
			}
		} else {
			// refresh the configuration
			configure(getConfigurationFile());
		}

		return rc;
	}
	
    /* (non-Javadoc)
     * @see org.psygrid.security.attributeauthority.service.AttributeAuthorityManagementPortType#getVersion()
     */
    public java.lang.String getVersion() throws java.rmi.RemoteException
    {
		return getServletContext().getInitParameter("version");
    }
    
    public boolean isSuperUser() throws java.rmi.RemoteException
    {
    	final String METHOD_NAME = "isSuperUser";
		String cid = getCallersIdentity();
		logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, cid, cid);
		
		try {
			if (authorisedForAdministrationRequests(getCallerPrincipals())) {
				return true;
			}
		} catch (PGSecurityException pge) {
			throw new ProcessingFaultMessage(pge.getMessage());
		}
		return false;
    }

    public org.psygrid.www.xml.security.core.types.UserPrivilegesType[] getUsersAndPrivilegesInProject(
			org.psygrid.www.xml.security.core.types.ProjectType project)
			throws java.rmi.RemoteException,
			org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage,
			org.psygrid.security.attributeauthority.service.ProcessingFaultMessage,
			org.psygrid.security.attributeauthority.service.InputFaultMessage {
		
    	final String METHOD_NAME = "getUsersAndPrivilegesInProject";
		String cid = getCallersIdentity();
		logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, cid, cid);
		

		if (!_makePolicyDecision(new GroupType(), new ActionType(
				RBACAction.ACTION_AA_GET_USERS_AND_PRIVILEGES_IN_PROJECT.toString(),
				RBACAction.ACTION_AA_GET_USERS_AND_PRIVILEGES_IN_PROJECT.idAsString()), project)) {
			try {
				if (!authorisedForAdministrationRequests(getCallerPrincipals())) {
					logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, cid, cid);
					throw new NotAuthorisedFaultMessage(
					"Administration Permission Denied");
				}
			} catch (PGSecurityException pge) {
				throw new ProcessingFaultMessage(pge.getMessage());
			}
		}

		UserDAO userDAO =  getUserDAO();
		List<User> ul = userDAO.getUsersAndPrivilegesInProject(Project.fromProjectType(project));
		UserPrivilegesType[] result = new UserPrivilegesType[ul.size()];
		for(int i=0; i<ul.size(); i++){
			result[i] = ul.get(i).toUserPrivilegesType();
		}
		return result;
	}
    
    public org.psygrid.www.xml.security.core.types.UserPrivilegesType[] getUsers()
			throws java.rmi.RemoteException,
			org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage,
			org.psygrid.security.attributeauthority.service.ProcessingFaultMessage,
			org.psygrid.security.attributeauthority.service.InputFaultMessage {

		final String METHOD_NAME = "getUsers";
		String cid = getCallersIdentity();
		logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, cid, cid);

		if (!_makePolicyDecision(new GroupType(), new ActionType(
				RBACAction.ACTION_AA_GET_USERS.toString(),
				RBACAction.ACTION_AA_GET_USERS.idAsString()), new ProjectType(
				PGSecurityConstants.SYSTEM_PROJECT,
				PGSecurityConstants.SYSTEM_PROJECT_ID, null, null ,true))) {
			try {
				if (!authorisedForAdministrationRequests(getCallerPrincipals())) {
					logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, cid,
							cid);
					throw new NotAuthorisedFaultMessage(
							"Administration Permission Denied");
				}
			} catch (PGSecurityException pge) {
				throw new ProcessingFaultMessage(pge.getMessage());
			}
		}
		UserDAO userDAO = getUserDAO();
		List<User> ul = userDAO.getUsers();
		UserPrivilegesType[] result = new UserPrivilegesType[ul.size()];
		for (int i = 0; i < ul.size(); i++) {
			result[i] = ul.get(i).toUserPrivilegesType();
		}
		return result;
	}
    
    
    public org.psygrid.www.xml.security.core.types.UserPrivilegesType getUser(
			String uid)
			throws java.rmi.RemoteException,
			org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage,
			org.psygrid.security.attributeauthority.service.ProcessingFaultMessage,
			org.psygrid.security.attributeauthority.service.InputFaultMessage {

		final String METHOD_NAME = "getUser";
		String cid = getCallersIdentity();
		logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, cid, cid);

		UserDAO userDAO = getUserDAO();
		try {
			User u = userDAO.getUserByName(uid);
			if(u==null){
				throw new InputFaultMessage("User "+uid+ " does not exist");
			}
			List<AttributeType> allowed = new ArrayList<AttributeType>();
			for (Attribute a : u.getAttributes()) {
				if (!authorisedForAdministrationRequests(getCallerPrincipals())) {
					if (!_makePolicyDecision(new GroupType(), new ActionType(
							RBACAction.ACTION_AA_GET_USER.toString(),
							RBACAction.ACTION_AA_GET_USER.idAsString()), a
							.getProject().toProjectType())) {
						continue;
					}
				}
				allowed.add(a.toAttributeType());
			}		
			if(allowed.size()<1){
				if (!_makePolicyDecision(new GroupType(), new ActionType(
						RBACAction.ACTION_AA_GET_USER.toString(),
						RBACAction.ACTION_AA_GET_USER.idAsString()),
						new ProjectType(PGSecurityConstants.ANY,
								PGSecurityConstants.ANY, null, null, true))) {
					try {
						if (!authorisedForAdministrationRequests(getCallerPrincipals())) {
							logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, cid, cid);
							throw new NotAuthorisedFaultMessage(
									"Administration Permission Denied");
						}
					} catch (PGSecurityException pge) {
						throw new ProcessingFaultMessage(pge.getMessage());
					}
				}
			}
			UserPrivilegesType upt = u.toUserPrivilegesType();
			upt.setUser(_getUserFromLDAP(uid));
			if(allowed.size()>0){
				upt.setAttribute(allowed.toArray(new AttributeType[allowed.size()]));
			}
			return upt;
		} catch (DAOException e) {
			throw new ProcessingFaultMessage(e);
		} catch (PGSecurityException e) {
			throw new ProcessingFaultMessage(e);
		}
	}
    
    
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psygrid.security.attributeauthority.service.AttributeAuthorityConfigurationPortType#retrieveConfiguration(boolean)
	 */
	public String retrieveConfiguration(boolean refresh)
			throws RemoteException, ProcessingFaultMessage,
			NotAuthorisedFaultMessage {
		String s = null;
		final String METHOD_NAME = "retrieveConfiguration";
		String cid = getCallersIdentity();
		logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, cid, cid);
		if (!_makePolicyDecision(new GroupType(), new ActionType(
				RBACAction.ACTION_AA_RETRIEVE_CONFIGURATION.toString(),
				RBACAction.ACTION_AA_RETRIEVE_CONFIGURATION.idAsString()),
				new ProjectType(PGSecurityConstants.SYSTEM_PROJECT,
						PGSecurityConstants.SYSTEM_PROJECT_ID, null, null, true))) {
			try {
				if (!authorisedForAdministrationRequests(getCallerPrincipals())) {
					logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, cid, cid);
					throw new NotAuthorisedFaultMessage(
							"Administration Permission Denied");
				}
			} catch (PGSecurityException pge) {
				throw new ProcessingFaultMessage(pge.getMessage());
			}
		}
		// refresh the configuration
		if (refresh)
			configure(getConfigurationFile());
		s = XMLUtilities.domToString(getConfigurationDOM());

		return s;
	}

	public boolean addProject(
			org.psygrid.www.xml.security.core.types.ProjectDescriptionType[] input)
			throws java.rmi.RemoteException,
			org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage,
			org.psygrid.security.attributeauthority.service.ProcessingFaultMessage,
			org.psygrid.security.attributeauthority.service.InputFaultMessage {

		final String METHOD_NAME = "addProject";
		String cid = getCallersIdentity();
		logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, cid, cid);

		ProjectDAO projectDAO = getProjectDAO();
			if (!_makePolicyDecision(new GroupType(), new ActionType(
					RBACAction.ACTION_AA_ADD_PROJECT.toString(),
					RBACAction.ACTION_AA_ADD_PROJECT.idAsString()),
					new ProjectType(PGSecurityConstants.SYSTEM_PROJECT,
							PGSecurityConstants.SYSTEM_PROJECT_ID, null, null, true))) {
				try {
					if (!authorisedForAdministrationRequests(getCallerPrincipals())) {
			             logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, cid, cid);
			             throw new NotAuthorisedFaultMessage(
								"Administration Permission Denied");
					}
					
				} catch (PGSecurityException pge) {
					throw new ProcessingFaultMessage(pge.getMessage());
				}
			}

		for (int i = 0; i < input.length; i++) {
			IProject project = factory.createProject(input[i]);
			try {
				Project p = (Project) projectDAO.getProject(input[i]
						.getProject());
				if (p != null) {
					// project exists, so update
					updateProject(new ProjectDescriptionType[] { input[i] });
				} else {
					synchronized(DB_LOCK){
						projectDAO.addProject(project);
						User u = userDAO.getUserByName(cid);
						if (u != null) {
							u.setPDAO(projectDAO);
							u.setUDAO(getUserDAO());
							List<Role> rl = new ArrayList<Role>();
							rl.add(Role.fromRoleType(RBACRole.ProjectManager.toRoleType()));
							u.addAttribute(new Attribute((Project)project, ((Project)project).getGroups(), rl));
						}
					}
				}
			} catch (DAOException doa) {
				throw new ProcessingFaultMessage(doa.getMessage());
			} catch (ObjectOutOfDateException oood) {
				throw new ProcessingFaultMessage(oood.getMessage());
			}
		}
		return true;
	}

	public boolean deleteProject(
			org.psygrid.www.xml.security.core.types.ProjectType[] request)
			throws java.rmi.RemoteException,
			org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage,
			org.psygrid.security.attributeauthority.service.ProcessingFaultMessage,
			org.psygrid.security.attributeauthority.service.InputFaultMessage {

		final String METHOD_NAME = "deleteProject";
		String cid = getCallersIdentity();
		logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, cid, cid);

		
		for (int i = 0; i < request.length; i++) {
			if (!_makePolicyDecision(new GroupType(), new ActionType(
					RBACAction.ACTION_AA_DELETE_PROJECT.toString(),
					RBACAction.ACTION_AA_DELETE_PROJECT.idAsString()), request[i])) {
				try {
					if (!authorisedForAdministrationRequests(getCallerPrincipals())) {
						logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, cid, cid);
						throw new NotAuthorisedFaultMessage(
								"Administration Permission Denied");
					}
					break;
				} catch (PGSecurityException pge) {
					throw new ProcessingFaultMessage(pge.getMessage());
				}
			}
		}
		ProjectDAO projectDAO = getProjectDAO();
		UserDAO userDAO = getUserDAO();

		for (int i = 0; i < request.length; i++) {
			try {
				Project p = Project.fromProjectType(request[i]);
				synchronized (DB_LOCK) {
					List<String> lid = userDAO.getUsersInProject(p);
					for (String id : lid) {
						User u = userDAO.getUserByName(id);
						u.removeProject(p);
						userDAO.updateUser(u);
					}
					Project pp = (Project) projectDAO.getProject(request[i]);
					if (pp != null) {
						projectDAO.removeProject(pp.getId());
					}
				}
			} catch (DAOException doa) {
				doa.printStackTrace();
				throw new ProcessingFaultMessage(doa.getMessage());
			} catch (ObjectOutOfDateException oood) {
				throw new ProcessingFaultMessage(oood.getMessage());
			} 
		}
		return true;
	}
	
	public boolean deleteProjectAndPolicy(
			org.psygrid.www.xml.security.core.types.ProjectType[] request)
			throws java.rmi.RemoteException,
			org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage,
			org.psygrid.security.attributeauthority.service.ProcessingFaultMessage,
			org.psygrid.security.attributeauthority.service.InputFaultMessage {

		final String METHOD_NAME = "deleteProjectAndPolicy";
		String cid = getCallersIdentity();
		logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, cid, cid);

		for (int i = 0; i < request.length; i++) {
			if (!_makePolicyDecision(new GroupType(), new ActionType(
					RBACAction.ACTION_AA_DELETE_PROJECT.toString(),
					RBACAction.ACTION_AA_DELETE_PROJECT.idAsString()), request[i])) {
				try {
					if (!authorisedForAdministrationRequests(getCallerPrincipals())) {
						logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, cid, cid);
						throw new NotAuthorisedFaultMessage(
								"Administration Permission Denied");
					}
					break;
				} catch (PGSecurityException pge) {
					throw new ProcessingFaultMessage(pge.getMessage());
				}
			}
		}
		ProjectDAO projectDAO = getProjectDAO();
		UserDAO userDAO = getUserDAO();

		for (int i = 0; i < request.length; i++) {
			try {
				Project p = Project.fromProjectType(request[i]);
				synchronized (DB_LOCK) {
					List<String> lid = userDAO.getUsersInProject(p);
					for (String id : lid) {
						User u = userDAO.getUserByName(id);
						u.removeProject(p);
						userDAO.updateUser(u);
					}
					Project pp = (Project) projectDAO.getProject(request[i]);
					if (pp != null) {
						projectDAO.removeProject(pp.getId());
					}
					String propertiesFile = oContext.getServletContext().getInitParameter("policyAuthorityProperties");	
					try{
						PAManagementClient pamc = new PAManagementClient(propertiesFile);
						pamc.getPort().deletePolicy(new PolicyType[]{new PolicyType(request[i].getName(), request[i].getIdCode(), null, null)});
					} catch (PGSecurityException pgse) {
						sLog.error("failed to initiliase PA Management Client",pgse);
						throw new ProcessingFaultMessage("failed to initiliase PA Management Client",pgse);
					} catch (Exception e) {
						sLog.error("failed to delete policy",e);
						throw new ProcessingFaultMessage("failed to delete policy",e);
					}
				}
			} catch (DAOException doa) {
				sLog.error(doa);
				throw new ProcessingFaultMessage(doa.getMessage(),doa);
			} catch (ObjectOutOfDateException oood) {
				throw new ProcessingFaultMessage(oood.getMessage(),oood);
			} 
		}
		return true;
	}

	public boolean addGroupToProject(
			org.psygrid.www.xml.security.core.types.ProjectDescriptionType[] input)
			throws java.rmi.RemoteException,
			org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage,
			org.psygrid.security.attributeauthority.service.ProcessingFaultMessage,
			org.psygrid.security.attributeauthority.service.InputFaultMessage {

		final String METHOD_NAME = "addGroupToProject";
		String cid = getCallersIdentity();
		logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, cid, cid);
		
		for (int i = 0; i < input.length; i++) {
			if (!_makePolicyDecision(new GroupType(), new ActionType(
					RBACAction.ACTION_AA_MODIFY_PROJECT.toString(),
					RBACAction.ACTION_AA_MODIFY_PROJECT.idAsString()), input[i]
					.getProject())) {
				try {
					if (!authorisedForAdministrationRequests(getCallerPrincipals())) {
			             logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, cid, cid);
			             throw new NotAuthorisedFaultMessage(
								"Administration Permission Denied");
					}
					break;
				} catch (PGSecurityException pge) {
					throw new ProcessingFaultMessage(pge.getMessage());
				}
			}
		}
		ProjectDAO projectDAO = getProjectDAO();
		for (int i = 0; i < input.length; i++) {
			try {
//				UoW
				synchronized(DB_LOCK){
				Project p = (Project) projectDAO.getProject(input[i]
						.getProject());
				if (p != null) {
					p.addGroup(input[i].getGroup());
					projectDAO.updateProject(p);
				}
				}
//				UoW
			} catch (DAOException doa) {
				throw new ProcessingFaultMessage(doa.getMessage());
			}
		}
		return true;
	}

	public boolean deleteGroupFromProject(
			org.psygrid.www.xml.security.core.types.ProjectDescriptionType[] input)
			throws java.rmi.RemoteException,
			org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage,
			org.psygrid.security.attributeauthority.service.ProcessingFaultMessage,
			org.psygrid.security.attributeauthority.service.InputFaultMessage {

		final String METHOD_NAME = "deleteGroupFromProject";
		String cid = getCallersIdentity();
		logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, cid, cid);

		
		for (int i = 0; i < input.length; i++) {
			if (!_makePolicyDecision(new GroupType(), new ActionType(
					RBACAction.ACTION_AA_MODIFY_PROJECT.toString(),
					RBACAction.ACTION_AA_MODIFY_PROJECT.idAsString()), input[i]
					.getProject())) {
				try {
					if (!authorisedForAdministrationRequests(getCallerPrincipals())) {
						logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, cid, cid);
						throw new NotAuthorisedFaultMessage(
								"Administration Permission Denied");
					}
				} catch (PGSecurityException pge) {
					throw new ProcessingFaultMessage(pge.getMessage());
				}
			}
		}
		ProjectDAO projectDAO = getProjectDAO();
		UserDAO userDAO = getUserDAO();
		for (int i = 0; i < input.length; i++) {
			try {
				// UoW
				synchronized (DB_LOCK) {
					Project p = (Project) projectDAO.getProject(input[i]
							.getProject());
					if (p != null) {
						for (int j = 0; j < input[i].getGroup().length; j++) {
							Group g = Group
									.fromGroupType(input[i].getGroup()[j]);
							List<String> lid = userDAO
									.getUsersInGroupInProject(p, g);
							for (String id : lid) {
								User u = userDAO.getUserByName(id);
								u.removeGroupFromProject(p, g);
								userDAO.updateUser(u);
							}
						}
						p.removeGroup(input[i].getGroup());
						projectDAO.updateProject(p);
					}
				}
				// UoW
			} catch (DAOException doa) {
				throw new ProcessingFaultMessage(doa.getMessage());
			}
		}
		return true;
	}

	public boolean updateProject(
			org.psygrid.www.xml.security.core.types.ProjectDescriptionType[] input)
			throws java.rmi.RemoteException,
			org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage,
			org.psygrid.security.attributeauthority.service.ProcessingFaultMessage,
			org.psygrid.security.attributeauthority.service.InputFaultMessage {
		boolean rc = true;

		final String METHOD_NAME = "updateProject";
		String cid = getCallersIdentity();
		logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, cid, cid);
		for (int i = 0; i < input.length; i++) {
			if (!_makePolicyDecision(new GroupType(), new ActionType(
					RBACAction.ACTION_AA_MODIFY_PROJECT.toString(),
					RBACAction.ACTION_AA_MODIFY_PROJECT.idAsString()), input[i]
					.getProject())) {
				try {
					if (!authorisedForAdministrationRequests(getCallerPrincipals())) {
						logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, cid, cid);
						throw new NotAuthorisedFaultMessage(
								"Administration Permission Denied");
					}
					break;
				} catch (PGSecurityException pge) {
					throw new ProcessingFaultMessage(pge.getMessage());
				}
			}
		}
		ProjectDAO projectDAO = getProjectDAO();

		for (int i = 0; i < input.length; i++) {
			Project newProject = factory.createProject(input[i]);
			try {
//				UoW
				synchronized(DB_LOCK){
				Project oldProject = (Project) projectDAO.getProject(input[i]
						.getProject());
				if (oldProject != null) {
					for (int j = 0; j < newProject.getGroups().size(); j++) {
						if (!oldProject.isKnownGroup(newProject.getGroups()
								.get(j).getGroupName())) {
							oldProject.getGroups().add(
									newProject.getGroups().get(j));
						}
					}
					for (int j = 0; j < newProject.getRoles().size(); j++) {
						if (!oldProject.isKnownRole(newProject.getRoles()
								.get(j).getRoleName())) {
							oldProject.getRoles().add(
									newProject.getRoles().get(j));
						}
					}
					projectDAO.updateProject(oldProject);		
//					UoW
				} else {
					rc = false;
				}
				}
			} catch (DAOException doa) {
				throw new ProcessingFaultMessage(doa.getMessage());
			}
		}
		return rc;
	}

	public boolean addRoleToProject(
			org.psygrid.www.xml.security.core.types.ProjectDescriptionType[] input)
			throws java.rmi.RemoteException,
			org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage,
			org.psygrid.security.attributeauthority.service.ProcessingFaultMessage,
			org.psygrid.security.attributeauthority.service.InputFaultMessage {

		final String METHOD_NAME = "addRoleToProject";
		String cid = getCallersIdentity();
		logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, cid, cid);

		for (int i = 0; i < input.length; i++) {
			if (!_makePolicyDecision(new GroupType(), new ActionType(
					RBACAction.ACTION_AA_MODIFY_PROJECT.toString(),
					RBACAction.ACTION_AA_MODIFY_PROJECT.idAsString()), input[i]
					.getProject())) {
				try {
					if (!authorisedForAdministrationRequests(getCallerPrincipals())) {
						logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, cid, cid);
						throw new NotAuthorisedFaultMessage(
								"Administration Permission Denied");
					}
				} catch (PGSecurityException pge) {
					throw new ProcessingFaultMessage(pge.getMessage());
				}
			}
		}
		ProjectDAO projectDAO = getProjectDAO();
		for (int i = 0; i < input.length; i++) {
			try {
//				UoW
				synchronized(DB_LOCK){
				Project p = (Project) projectDAO.getProject(input[i]
						.getProject());
				if (p != null) {
					p.addRole(input[i].getRole());
					projectDAO.updateProject(p);
				}
				}
//				UoW
			} catch (DAOException doa) {
				throw new ProcessingFaultMessage(doa.getMessage());
			}
		}
		return true;
	}

	public boolean deleteGroupInProjectFromUser(
			org.psygrid.www.xml.security.core.types.UserPrivilegesType[] input)
			throws java.rmi.RemoteException,
			org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage,
			org.psygrid.security.attributeauthority.service.ProcessingFaultMessage,
			org.psygrid.security.attributeauthority.service.InputFaultMessage {

		final String METHOD_NAME = "deleteGroupInProjectFromUser";
		String cid = getCallersIdentity();
		logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, cid, cid);

		UserDAO userDAO = getUserDAO();
		for (int i = 0; i < input.length; i++) {
			for (int j = 0; j < input[i].getAttribute().length; j++) {
				if (!_makePolicyDecision(new GroupType(), new ActionType(
						RBACAction.ACTION_AA_MODIFY_USER.toString(),
						RBACAction.ACTION_AA_MODIFY_USER.idAsString()), input[i]
						.getAttribute(j).getProject())) {
					try {
						if (!authorisedForAdministrationRequests(getCallerPrincipals())) {
							logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, cid, cid);
							throw new NotAuthorisedFaultMessage(
									"Administration Permission Denied");
						}
					} catch (PGSecurityException pge) {
						throw new ProcessingFaultMessage(pge.getMessage());
					}
				}
			}
		}
		for (int i = 0; i < input.length; i++) {
			try {
//				UoW
				synchronized(DB_LOCK){
				User u = userDAO.getUserByName(input[i].getUser().getDistinguishedName());
				if (u != null) {
					u.removeGroupFromProject(input[i].getAttribute());
					userDAO.updateUser(u);
				}
				}
//				UoW
			} catch (DAOException doa) {
				throw new ProcessingFaultMessage(doa.getMessage());
			}
		}
		return true;
	}

	public boolean addGroupInProjectToUser(
			org.psygrid.www.xml.security.core.types.UserPrivilegesType[] input)
			throws java.rmi.RemoteException,
			org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage,
			org.psygrid.security.attributeauthority.service.ProcessingFaultMessage,
			org.psygrid.security.attributeauthority.service.InputFaultMessage {

		final String METHOD_NAME = "addGroupInProjectToUser";
		String cid = getCallersIdentity();
		logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, cid, cid);

		for (int i = 0; i < input.length; i++) {
			for (int j = 0; j < input[i].getAttribute().length; j++) {
				if (!_makePolicyDecision(new GroupType(), new ActionType(
						RBACAction.ACTION_AA_MODIFY_USER.toString(),
						RBACAction.ACTION_AA_MODIFY_USER.idAsString()), input[i]
						.getAttribute(j).getProject())) {
					try {
						if (!authorisedForAdministrationRequests(getCallerPrincipals())) {
				             logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, cid, cid);
				             throw new NotAuthorisedFaultMessage(
									"Administration Permission Denied");
						}
					} catch (PGSecurityException pge) {
						throw new ProcessingFaultMessage(pge.getMessage());
					}
				}
			}
		}
		for (int i = 0; i < input.length; i++) {
			try {
//				UoW
				UserDAO userDAO = getUserDAO();
				synchronized(DB_LOCK){
				User u = userDAO.getUserByName(input[i].getUser().getDistinguishedName());
				if (u != null) {
					u.setPDAO(getProjectDAO());
					u.setUDAO(getUserDAO());
					u.addGroupInProject(input[i].getAttribute());
				} 
				}
//				UoW
			} catch (DAOException doa) {
				throw new ProcessingFaultMessage(doa.getMessage());
			}
		}
		return true;
	}

	public boolean deleteRoleFromProject(
			org.psygrid.www.xml.security.core.types.ProjectDescriptionType[] input)
			throws java.rmi.RemoteException,
			org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage,
			org.psygrid.security.attributeauthority.service.ProcessingFaultMessage,
			org.psygrid.security.attributeauthority.service.InputFaultMessage {


		final String METHOD_NAME = "deleteRoleFromProject";
		String cid = getCallersIdentity();
		logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, cid, cid);

		
		for (int i = 0; i < input.length; i++) {
			if (!_makePolicyDecision(new GroupType(), new ActionType(
					RBACAction.ACTION_AA_MODIFY_PROJECT.toString(),
					RBACAction.ACTION_AA_MODIFY_PROJECT.idAsString()), input[i]
					.getProject())) {
				try {
					if (!authorisedForAdministrationRequests(getCallerPrincipals())) {
						logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, cid, cid);
						throw new NotAuthorisedFaultMessage(
								"Administration Permission Denied");
					}
					break;
				} catch (PGSecurityException pge) {
					throw new ProcessingFaultMessage(pge.getMessage());
				}
			}
		}
		ProjectDAO projectDAO = getProjectDAO();
		UserDAO userDAO = getUserDAO();
		for (int i = 0; i < input.length; i++) {
			try {
				// UoW
				synchronized (DB_LOCK) {
					Project p = (Project) projectDAO.getProject(input[i]
							.getProject());
					if (p != null) {
						for (int j = 0; j < input[i].getRole().length; j++) {
							Role r = Role
									.fromRoleType(input[i].getRole()[j]);
							List<String> lid = userDAO.getUsersInProjectWithRole(p, r);
							for (String id : lid) {
								User u = userDAO.getUserByName(id);
								u.removeRoleFromProject(p, r);
								userDAO.updateUser(u);
							}
						}
						p.removeRole(input[i].getRole());
						projectDAO.updateProject(p);
					}
				}
				// UoW
			} catch (DAOException doa) {
				throw new ProcessingFaultMessage(doa.getMessage());
			}
		}
		return true;
	}

	
	public List<ProjectType> _getProjectsForUser(java.lang.String user) {
		
		UserDAO userDAO =  this.getUserDAO();
		List<ProjectType> lp = new ArrayList<ProjectType>();
		try {
			User u = userDAO.getUserByName(user);
			if (u != null) {
				List<Attribute> la = u.getAttributes();
				for(Attribute a : la){
					lp.add(a.getProject().toProjectType());
				}
			}
		} catch (DAOException doae) {

		}
		return lp;
	}
	
	public String _getAttributesForUserInProject(String user, ProjectType p) {
		String result = null;
		UserDAO userDAO = this.getUserDAO();
		SAMLAssertion sa = null;
		try {
			Project project = Project.fromProjectType(p);	
			List<Attribute> la = userDAO.getAttributesForUserInProject(user, project);	
			sa = createNewSAMLAssertion(user, la);	
		} catch (Exception doae) {
			//project not found or user has no attributes.
			sa = null;
		}
		if (sa != null) {
			result = sa.toString();
		}
		return result;
	}
	
	public String _getAttributesForUser(String user) {
		String result = null;
		UserDAO userDAO = this.getUserDAO();
		SAMLAssertion sa = null;
		try {	
			User u = userDAO.getUserByName(user);	
			if (u != null) {
				sa = createNewSAMLAssertion(u);
			} else {
				sLog.info("User "+u+" does not exist");
			}
		} catch (Exception doae) {
			//project not found or user has no attributes.
			sa = null;
		}
		if (sa != null) {
			result = sa.toString();
		}
		return result;
	}

	boolean _makePolicyDecision(GroupType target, ActionType action,
			ProjectType project) {
		boolean result = false;
		String sa = null;
		List<ProjectType> lp = new ArrayList<ProjectType>();
		if(project.getName().equals(PGSecurityConstants.ANY)||
				project.getIdCode().equals(PGSecurityConstants.ANY)){
			sa = _getAttributesForUser(getCallerPrincipals()
					.getName());
			lp = _getProjectsForUser(getCallerPrincipals()
					.getName()); 
		} else {
			sa = _getAttributesForUserInProject(getCallerPrincipals()
				.getName(), project);
			lp.add(project);
		}
		
		if (sa != null) {
			for(ProjectType p : lp){
				try {
					result = aef.authoriseUser(sa, new AEFGroup(target), new AEFAction(action), new AEFProject(p));
					if(result){
						break;
					}
				} catch (PGSecurityException pgse) {
					pgse.printStackTrace();
					log.error(pgse.getMessage());
				} catch (PGSecurityInvalidSAMLException pgse) {
					pgse.printStackTrace();
					log.error(pgse.getMessage());
				} catch (PGSecuritySAMLVerificationException pgse) {
					pgse.printStackTrace();
					log.error(pgse.getMessage());
				}
			}
		}
		return result;
	}
	
    private boolean addToLDAP(UserType user)
    {
    	
    	boolean success = false;
    	
    	// ldap stuff
		LdapContext ctx = null;
		try {
			ctx = createLDAPContext(ldapRoot, ldapRootPassword.toCharArray());	

			Attributes atrs = new BasicAttributes(true);
			//fixed
			BasicAttribute objcls = new BasicAttribute("objectClass");
			objcls.add("inetOrgPerson");
			objcls.add("organizationalPerson");
			objcls.add("person");
			objcls.add("top");
			atrs.put(objcls);
			
			BasicAttribute sn = new BasicAttribute("sn");
			sn.add(user.getLastName());
			
			BasicAttribute first = new BasicAttribute("givenName");
			first.add(user.getFirstName());
			
			BasicAttribute cn = new BasicAttribute("cn");
			String commonName = user.getFirstName() + " " + user.getLastName();
			cn.add(commonName);
			
			BasicAttribute mail = new BasicAttribute("mail");
			mail.add(user.getEmailAddress());
			

			if(ldapPasswordHash!=null){
				if(ldapPasswordHash.equals(LDAPPasswordHashScheme.SHA.toString())){
					char[] hpwd = PasswordUtilities.hashPassword(
							user.getTemporaryPassword().toCharArray(), LDAPPasswordHashScheme.SHA, log);
					user.setTemporaryPassword(new String(hpwd));
				}
			}

			BasicAttribute uid = new BasicAttribute("uid");
			uid.add(user.getFirstName() + user.getLastName());
			
						//set all the attributes
			atrs.put(objcls);
			atrs.put(sn);
			atrs.put(first);
			atrs.put(cn);
			atrs.put(mail);
			atrs.put(uid);
			//if mobile isn't empty set it too
			if (user.getMobileNumber() != null && (!user.getMobileNumber().equals(""))) {
				BasicAttribute mobile = new BasicAttribute("mobile");
				mobile.add(user.getMobileNumber());
				atrs.put(mobile);
			}
			BasicAttribute userpassword = null;
			BasicAttribute uac = null;
			if(ldapVendor.equals(LDAPDirectoryVendors.MICROSOFT.toString())){
				userpassword = new BasicAttribute("unicodePwd");
				userpassword.add(PasswordUtilities.UTFPassword(user.getTemporaryPassword()));
				atrs.put(userpassword);
				uac = new BasicAttribute("userAccountControl");
				uac.add(Integer.toString(UF_NORMAL_ACCOUNT));
				atrs.put(uac);
			} else {
				userpassword = new BasicAttribute("userPassword");
				userpassword.add(user.getTemporaryPassword());
				atrs.put(userpassword);
			}
			//create the new user
			ctx.createSubcontext("cn="+commonName+", "+ldapUserBaseDN, atrs);
			
			//Fix up AD accounts so that passwd change not required.
			if(ldapVendor.equals(LDAPDirectoryVendors.MICROSOFT.toString())){
				ModificationItem[] mods = new ModificationItem[2];
				mods[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE,
						new BasicAttribute("pwdLastSet"));
				mods[1] = new ModificationItem(DirContext.ADD_ATTRIBUTE,
						new BasicAttribute("pwdLastSet", Integer.toString(-1)));
				ctx.modifyAttributes("cn=" + commonName + ", " + ldapUserBaseDN,
						mods);
			}
			ctx.close();
			success = true;

		} catch (NameAlreadyBoundException nex) {
			log.error("LDAPController " + nex.getMessage()); 
		} catch (InvalidAttributesException iae) {
			log.error("LDAPController " + iae.getMessage());
		} catch (InvalidNameException ine) {
			log.error("LDAPController " + ine.getMessage());
		} catch (NamingException ne) {
			log.error("LDAPController " + ne.getMessage());
		} catch (IOException io) {
			sLog.error("Failed TLS negotiation with LDAP server",io);
		} finally {
			closeLDAPContext(ctx);
		}
    	return success;
    }
    
    @Deprecated
	private boolean deleteUserFromLDAP(String uname)
    {
		boolean ldapSuccess = false;
		
		LdapContext ctx = null;
		try {
			ctx = createLDAPContext(ldapRoot, ldapRootPassword.toCharArray());
			ctx.lookup(uname);
			ctx.destroySubcontext(uname);
			ldapSuccess = true;
	   	} catch (NameNotFoundException ex) {
	   		log.info("LDAPController: name not found : " + ex.getMessage(),ex);
		} catch (NamingException nex) {
			log.info("LDAPController : naming exception " + nex.getMessage(),nex);
		} catch (IOException io) {
			sLog.error("Failed TLS negotiation with LDAP server",io);
		} finally {
			closeLDAPContext(ctx);
		}
		return ldapSuccess;
    }
    /**
     * Check first if user exists in the LDAP directory and then if
     * the user exists in the AA.
     * @return exits true if the user exists in the LDAP and AA; false if not.
     */
    public boolean userExists(String firstName, String lastName)
	throws java.rmi.RemoteException,
	org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage,
	org.psygrid.security.attributeauthority.service.ProcessingFaultMessage,
	org.psygrid.security.attributeauthority.service.InputFaultMessage 
    {
		final String METHOD_NAME = "userExists";
		String cid = getCallersIdentity();
		logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, cid, cid);
		if (!_makePolicyDecision(new GroupType(), new ActionType(
				RBACAction.ACTION_AA_LDAP_QUERY.toString(),
				RBACAction.ACTION_AA_LDAP_QUERY.idAsString()),
				new ProjectType(PGSecurityConstants.ANY,
						PGSecurityConstants.ANY, null, null, true))) {
			try {
				if (!authorisedForAdministrationRequests(getCallerPrincipals())) {
					logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, cid, cid);
					throw new NotAuthorisedFaultMessage(
							"Administration Permission Denied");
				}
			} catch (PGSecurityException pge) {
				throw new ProcessingFaultMessage(pge.getMessage());
			}
		}
    	
    	boolean exists = false;

		LdapContext ctx = null;
		try {
			ctx = createLDAPContext(ldapRoot, ldapRootPassword.toCharArray());
    		//check if the user exists in the ldap directory
			ctx.list("CN="+firstName+ " " + lastName + ", "+ldapUserBaseDN);
			exists = true;

		} catch (NamingException ex)
		{
			//user does not exist as entered by the user, check possible variations
			log.info("LDAPController exception checking user exists : " + ex.getMessage() + " CN="+firstName+ " " + lastName + ", "+ldapUserBaseDN);
			//implies user does not exists in the LDAP directory - exists = false
		} catch (IOException io) {
			sLog.error("Failed TLS negotiation with LDAP server",io);
		} finally {
			closeLDAPContext(ctx);
		}		
		return exists;
    }
    
    public String[] multiUserExists(String firstName, String lastName)
	throws java.rmi.RemoteException,
	org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage,
	org.psygrid.security.attributeauthority.service.ProcessingFaultMessage,
	org.psygrid.security.attributeauthority.service.InputFaultMessage 
    {
		final String METHOD_NAME = "multiUserExists";
		String cid = getCallersIdentity();
		logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, cid, cid);
		if (!_makePolicyDecision(new GroupType(), new ActionType(
				RBACAction.ACTION_AA_LDAP_QUERY.toString(),
				RBACAction.ACTION_AA_LDAP_QUERY.idAsString()),
				new ProjectType(PGSecurityConstants.ANY,
						PGSecurityConstants.ANY, null, null, true))) {
			try {
				if (!authorisedForAdministrationRequests(getCallerPrincipals())) {
					logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, cid, cid);
					throw new NotAuthorisedFaultMessage(
							"Administration Permission Denied");
				}
			} catch (PGSecurityException pge) {
				throw new ProcessingFaultMessage(pge.getMessage());
			}
		}
    	ArrayList<String> results = new ArrayList<String>();
		LdapContext ctx = null;
		try {
			ctx = createLDAPContext(ldapRoot, ldapRootPassword.toCharArray());
			SearchControls scontrols = new SearchControls();
			scontrols.setSearchScope(SearchControls.SUBTREE_SCOPE); // search subtree
			String filter = "cn=" + firstName + "*" + lastName + "*";
			//				 Search for objects that have those matching attributes
			NamingEnumeration answer = ctx.search(ldapUserBaseDN, filter, scontrols);	
			while(answer.hasMoreElements()) {
				SearchResult result = (SearchResult)answer.nextElement();
				javax.naming.directory.Attribute attr = result.getAttributes().get("cn");
				String commonName = (String) attr.get();
				//now verify that they exist in the 
				results.add(commonName);
			}
		} catch (NamingException exp)
		{
			log.info("LDAPController exception checking user exists : " + exp.getMessage() + " CN="+ firstName + " " + lastName + ldapUserBaseDN);
		} catch (IOException io) {
			sLog.error("Failed TLS negotiation with LDAP server",io);
		} finally {
			closeLDAPContext(ctx);
		}
		return results.toArray(new String[results.size()]);
    }
	
	public String getUIDFromCN(String commonName)
	throws java.rmi.RemoteException,
	org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage,
	org.psygrid.security.attributeauthority.service.ProcessingFaultMessage,
	org.psygrid.security.attributeauthority.service.InputFaultMessage 
	{
		final String METHOD_NAME = "getUIDFromCN";
		String cid = getCallersIdentity();
		logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, cid, cid);
		if (!_makePolicyDecision(new GroupType(), new ActionType(
				RBACAction.ACTION_AA_LDAP_QUERY.toString(),
				RBACAction.ACTION_AA_LDAP_QUERY.idAsString()),
				new ProjectType(PGSecurityConstants.ANY,
						PGSecurityConstants.ANY, null, null, true))) {
			try {
				if (!authorisedForAdministrationRequests(getCallerPrincipals())) {
					logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, cid, cid);
					throw new NotAuthorisedFaultMessage(
							"Administration Permission Denied");
				}
			} catch (PGSecurityException pge) {
				throw new ProcessingFaultMessage(pge.getMessage());
			}
		}
		String uid = "";
		LdapContext ctx = null;
		try {
			ctx = createLDAPContext(ldapRoot, ldapRootPassword.toCharArray());
			String[] attrIDs = {"uid"};
			SearchControls scontrols = new SearchControls();
			scontrols.setSearchScope(SearchControls.SUBTREE_SCOPE); // search subtree
			scontrols.setReturningAttributes(attrIDs); // get the userid
			String searchBase = ldapUserBaseDN; 
			String searchMask = "cn="+commonName; 
			NamingEnumeration answer = ctx.search(searchBase, searchMask, scontrols);
			while (answer.hasMore()) {
				SearchResult entry = (SearchResult)answer.next();
				Attributes attributes = entry.getAttributes();
				javax.naming.directory.Attribute attr = attributes.get("uid");
				uid = (String) attr.get();
			}
		} catch (NameNotFoundException ex) {
	   		log.info("LDAPController: name not found : " + ex.getMessage(),ex);
		} catch (NamingException nex) {
			log.info("LDAPController : naming exception " + nex.getMessage(),nex);
		} catch (IOException io) {
			sLog.error("Failed TLS negotiation with LDAP server",io);
		} finally {
			closeLDAPContext(ctx);
		}
		
		return uid;
	}
	
	public String getCNFromUID(String userID)			
	throws java.rmi.RemoteException,
	org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage,
	org.psygrid.security.attributeauthority.service.ProcessingFaultMessage,
	org.psygrid.security.attributeauthority.service.InputFaultMessage 
	{
		
		final String METHOD_NAME = "getCNFromUID";
		String cid = getCallersIdentity();
		logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, cid, cid);
		if (!_makePolicyDecision(new GroupType(), new ActionType(
				RBACAction.ACTION_AA_LDAP_QUERY.toString(),
				RBACAction.ACTION_AA_LDAP_QUERY.idAsString()),
				new ProjectType(PGSecurityConstants.ANY,
						PGSecurityConstants.ANY, null, null, true))) {
			try {
				if (!authorisedForAdministrationRequests(getCallerPrincipals())) {
					logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, cid, cid);
					throw new NotAuthorisedFaultMessage(
							"Administration Permission Denied");
				}
			} catch (PGSecurityException pge) {
				throw new ProcessingFaultMessage(pge.getMessage());
			}
		}
		
		String cn = "";
		LdapContext ctx = null;
		try {
			ctx = createLDAPContext(ldapRoot, ldapRootPassword.toCharArray());
			String[] attrIDs = {"cn"};
			SearchControls scontrols = new SearchControls();
			scontrols.setSearchScope(SearchControls.SUBTREE_SCOPE); // search subtree
			scontrols.setReturningAttributes(attrIDs); // get the userid
			String searchBase = ldapUserBaseDN; 
			String searchMask = "uid="+userID; 
			NamingEnumeration answer = ctx.search(searchBase, searchMask, scontrols);
			while (answer.hasMore()) {
				SearchResult entry = (SearchResult)answer.next();
				Attributes attributes = entry.getAttributes();
				javax.naming.directory.Attribute attr = attributes.get("cn");
				cn = (String) attr.get();
			}
		} catch (NameNotFoundException ex) {
	   		log.info("LDAPController: name not found : " + ex.getMessage(),ex);
		} catch (NamingException nex) {
			log.info("LDAPController : naming exception " + nex.getMessage(),nex);
		} catch (IOException io) {
			sLog.error("Failed TLS negotiation with LDAP server",io);
		} finally {
			closeLDAPContext(ctx);
		}
		
		return cn;
	}
	
	public boolean resetPassword(ResetPasswordRequestType pcrt)

			throws java.rmi.RemoteException,
			org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage,
			org.psygrid.security.attributeauthority.service.ProcessingFaultMessage,
			org.psygrid.security.attributeauthority.service.InputFaultMessage {

		String uid = pcrt.getUid();
		final String METHOD_NAME = "resetPassword";
		try {

			String cid = getCallersIdentity();
			logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, uid, cid);

			if (uid != null) {
				if (!_makePolicyDecision(new GroupType(), new ActionType(
						RBACAction.ACTION_AA_RESET_PASSWORD.toString(),
						RBACAction.ACTION_AA_RESET_PASSWORD.idAsString()),
						new ProjectType(PGSecurityConstants.ANY,
								PGSecurityConstants.ANY, null, null, true))) {

						if (!authorisedForAdministrationRequests(getCallerPrincipals())) {
				             logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, uid, cid);
				             throw new NotAuthorisedFaultMessage(
									"Administration Permission Denied");
						}
				}
			} 
		} catch (PGSecurityException pge) {
			throw new ProcessingFaultMessage(pge.getMessage());
		}

		if(this._isAccountDormantViaDN(uid)){
			logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME+": Attempt to change password for dormant account", uid, getCallersIdentity());
		}
		
		short[] newPassword = pcrt.getNewPassword();

		char[] password = new char[newPassword.length];
		for (int i = 0; i < password.length; i++) {
			password[i] = (char) newPassword[i];
		}

		if (uid != null) {


			ModificationItem[] mods = new ModificationItem[1];
			
			if(ldapPasswordHash!=null){
				if(ldapPasswordHash.equals(LDAPPasswordHashScheme.SHA.toString())){
					password = PasswordUtilities.hashPassword(password, LDAPPasswordHashScheme.SHA, sLog);
				}
			}
		
			if(ldapVendor.equals(LDAPDirectoryVendors.MICROSOFT.toString())){
				BasicAttribute unicodePwd = new BasicAttribute("unicodePwd");
				unicodePwd.add(PasswordUtilities.UTFPassword(new String(password)));
				mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, unicodePwd);
			} else {
				mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
						new BasicAttribute("userPassword", new String(password)));				
			}
			
			LdapContext ctx = null;
			try {
				ctx = createLDAPContext(ldapRoot, ldapRootPassword.toCharArray());
				try {
					ctx.modifyAttributes(uid, mods);
				} catch (NamingException ne) {
					sLog.error(ne.getMessage(),ne);
					throw new ProcessingFaultMessage(ne.getMessage(),ne);
				}
			} catch (NamingException ne) {
				sLog.error(ne.getMessage(),ne);
				throw new ProcessingFaultMessage(ne.getMessage(),ne);
			} catch (IOException io) {
				sLog.error("Failed TLS negotiation with LDAP server",io);
			} finally {
				closeLDAPContext(ctx);
			}

			UserDAO userDAO = this.getUserDAO();
			try {
				User u = userDAO.getUserByName(uid);
				if (u != null) {
					u.setPasswordChangeRequired(true);
					userDAO.updateUser(u);
				}
			} catch (DAOException doae) {
				throw new ProcessingFaultMessage(doae.getMessage(),doae);
			}
		} else {
			throw new org.psygrid.security.attributeauthority.service.InputFaultMessage(
					"User ID could not be determined");
		}
		return true;
	}
	
	public UserType getUserFromLDAP(String dn)
			throws java.rmi.RemoteException,
			org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage,
			org.psygrid.security.attributeauthority.service.ProcessingFaultMessage,
			org.psygrid.security.attributeauthority.service.InputFaultMessage 
	{
		final String METHOD_NAME = "getUserFromLDAP";
		String cid = getCallersIdentity();
		logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, cid, cid);
		if (!_makePolicyDecision(new GroupType(), new ActionType(
				RBACAction.ACTION_AA_LDAP_QUERY.toString(),
				RBACAction.ACTION_AA_LDAP_QUERY.idAsString()),
				new ProjectType(PGSecurityConstants.ANY,
						PGSecurityConstants.ANY, null, null, true))) {
			try {
				if (!authorisedForAdministrationRequests(getCallerPrincipals())) {
					logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, cid, cid);
					throw new NotAuthorisedFaultMessage(
							"Administration Permission Denied");
				}
			} catch (PGSecurityException pge) {
				throw new ProcessingFaultMessage(pge.getMessage());
			}
		}
		return _getUserFromLDAP(dn);
	}
	
	private UserType _getUserFromLDAP(String dn){
		UserType ut = new UserType();
		if (dn != null) {
			String[] attrIDs = { "mail", "sn", "uid", "mobile" };
			LdapContext ctx = null;
			try {
				ctx = createLDAPContext(ldapRoot, ldapRootPassword.toCharArray());
				try {
					Attributes attrs = ctx.getAttributes(dn, attrIDs);
					if (attrs.get("mail") == null) {
						log.error("No email address for user " + dn);
					} else {
						ut.setEmailAddress((String)attrs.get("mail").get());
					}
					if (attrs.get("sn") == null) {
						log.error("No surname for user " + dn);
					} else {
						ut.setLastName((String)attrs.get("sn").get());
					}
					if (attrs.get("uid") == null) {
						log.error("No uid for user " + dn);
					} else {
						ut.setUserId((String)attrs.get("uid").get());
					}
					if (attrs.get("mobile") == null) {
						//Its optional, don't report an error
						//log.error("No mobile number for user " + dn);
					} else {
						ut.setMobileNumber((String)attrs.get("mobile").get());
					}
					ut.setDistinguishedName(dn);
				} catch (NamingException ne) {
					log.error(ne.getMessage(),ne);
				}
			} catch (NamingException ne) {
				log.error(ne.getMessage(),ne);
			} catch (IOException io) {
				log.error("Failed TLS negotiation with LDAP server",io);
			} finally {
				closeLDAPContext(ctx);
			}
		}	
		return ut;
	}
	
    public boolean projectExists(org.psygrid.www.xml.security.core.types.ProjectType project) throws java.rmi.RemoteException, org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage, org.psygrid.security.attributeauthority.service.ProcessingFaultMessage, org.psygrid.security.attributeauthority.service.InputFaultMessage
    {
		final String METHOD_NAME = "projectExists";
		String cid = getCallersIdentity();
		logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, cid, cid);	
		
		boolean result = false;
		
		ProjectDAO projectDAO = getProjectDAO();
		try {
			if(projectDAO.getProject(project)!=null){
				result = true;
			}
		} catch (DAOException daoe){
			log.error(daoe.getMessage(),daoe);
			throw new ProcessingFaultMessage(daoe);
		}
		return result;
    }
    
	public boolean userHasAttributes(java.lang.String user)
	throws java.rmi.RemoteException,
	org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage,
	org.psygrid.security.attributeauthority.service.ProcessingFaultMessage,
	org.psygrid.security.attributeauthority.service.InputFaultMessage {

		boolean result = false;
		
		final String METHOD_NAME="userHasAttributes";
		String cid = getCallersIdentity();
		logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, cid, cid);
				
//		if (!_makePolicyDecision(new GroupType(), new ActionType(
//				RBACAction.ACTION_AA_GET_USER.toString(),
//				RBACAction.ACTION_AA_GET_USER.idAsString()),
//				new ProjectType(PGSecurityConstants.ANY,
//						PGSecurityConstants.ANY, null, null, true))) {
//			try {
//				if (!authorisedForAdministrationRequests(getCallerPrincipals())) {
//					logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, cid, cid);
//					throw new NotAuthorisedFaultMessage(
//							"Administration Permission Denied");
//				}
//			} catch (PGSecurityException pge) {
//				throw new ProcessingFaultMessage(pge.getMessage());
//			}
//		}
		
		UserDAO userDAO =  this.getUserDAO();

		try {
			User u = userDAO.getUserByName(user);
			if (u != null) {
				List<Attribute> la = u.getAttributes();
				if(la!=null){
					if(la.size()>0){
						result = true;
					}
				}
			}
		} catch (DAOException doae) {
			throw new ProcessingFaultMessage(doae.getMessage());
		}
		return result;
	}

	
	public boolean addGroupAttributeToGroup(String projectCode,
			String groupCode, GroupAttributeType groupAttribute)
			throws RemoteException, ProcessingFaultMessage,
			NotAuthorisedFaultMessage {
		
		final String METHOD_NAME = "addGroupAttributeToGroup";
		String cid = getCallersIdentity();
		logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, cid, cid);

		//Retrieve the Group, add the GroupAttribute, and then resave.
		Project p = new Project("");
		p.setIdCode(projectCode);
		
		if (!_makePolicyDecision(new GroupType(), new ActionType(
				RBACAction.ACTION_AA_MODIFY_PROJECT.toString(),
				RBACAction.ACTION_AA_MODIFY_PROJECT.idAsString()), p.toProjectType())) {
			try {
				if (!authorisedForAdministrationRequests(getCallerPrincipals())) {
		             logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, cid, cid);
		             throw new NotAuthorisedFaultMessage(
							"Administration Permission Denied");
				}
			} catch (PGSecurityException pge) {
				throw new ProcessingFaultMessage(pge.getMessage());
			}
		}
		
		try {
			
			Project proj = this.getProjectDAO().getProject(p);
			List<Group> groups = proj.getGroups();
			Group theGroup = null;
			for(Group g : groups){
				if(g.getIdCode().equals(groupCode)){
					theGroup = g;
					break;
				}
			}
			
			if(theGroup == null){
				throw new ProcessingFaultMessage("The group identified by the project code and group code could not be found in the database.");
			}
			
			GroupDAO gDAO = this.getGroupDAO();
			gDAO.addGroupAttributeToGroup(theGroup.getId(), GroupAttribute.fromGroupAttributeType(groupAttribute));
			
		} catch (DAOException e) {
			throw new ProcessingFaultMessage(e);
		}
		
		return true;
	}

	
	public boolean addGroupAttributeToUser(String forename, String surname,
			String projectCode, String groupCode,
			GroupAttributeType groupAttribute) throws RemoteException,
			ProcessingFaultMessage, NotAuthorisedFaultMessage,
			InputFaultMessage {
		
		final String METHOD_NAME = "addGroupAttributeToUser";
		String cid = getCallersIdentity();
		logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, cid, cid);

		Project p = new Project("");
		p.setIdCode(projectCode);
	
		if (!_makePolicyDecision(new GroupType(), new ActionType(
				RBACAction.ACTION_AA_MODIFY_USER.toString(),
				RBACAction.ACTION_AA_MODIFY_USER.idAsString()), p.toProjectType())) {
			try {
				if (!authorisedForAdministrationRequests(getCallerPrincipals())) {
		             logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, cid, cid);
		             throw new NotAuthorisedFaultMessage(
							"Administration Permission Denied");
				}
			} catch (PGSecurityException pge) {
				throw new ProcessingFaultMessage(pge.getMessage());
			}
		}
		
		UserDAO uDAO = this.getUserDAO();
		String userName = "CN=" + forename + " " + surname +  ", " + this.ldapUserBaseDN;
		
		userName = LDAPDistinguishedNameHelper.whitespaceFormatDistinguishedName(userName);
		
		User u = null;
		try {
			u = uDAO.getUserByName(userName);
		}catch (DAOException e) {
			throw new InputFaultMessage("User with name " + forename + " " + surname + " does not exist in the aa_db.");
		}
		
		
		Attribute a = u.getAttributeByProject(p);
		List<GroupLink> groupLinks = a.getGroupLink();
		GroupLink theGroupLink = null;
		for(GroupLink gL : groupLinks){
			if(gL.getGroup().getIdCode().equals(groupCode)){
				theGroupLink = gL;
				break;
			}
		}
		
		if(theGroupLink == null){
			throw new InputFaultMessage("GroupLink could not be found for user with groupCode " + groupCode + ".");
		}
		
		GroupDAO gDAO = this.getGroupDAO();
		List<GroupAttribute> grpAttrList;
		try {
			grpAttrList = gDAO.getGroupAttributesForGroup(theGroupLink.getGroup().getId());
		} catch (DAOException e1) {
			throw new ProcessingFaultMessage(e1);
		}
		
		GroupAttribute persistedGroupAttribute = null;
		
		for(GroupAttribute g : grpAttrList){
			if(g.getAttributeName().equals(groupAttribute.getName())){
				persistedGroupAttribute = g;
				break;
			}
		}
		
		if(persistedGroupAttribute == null){
			throw new InputFaultMessage("No Matching GroupAttribute was found in the database.");
		}
		
		
		try{
			gDAO.addGroupAttributeToGroupLink(theGroupLink.getId(), persistedGroupAttribute.getId());
		}catch(DAOException e){
			throw new ProcessingFaultMessage(e);
		}
		
		return true;
	}


}
