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

import java.io.IOException;
import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.ldap.LdapContext;
import javax.xml.rpc.server.ServiceLifecycle;

import org.apache.axis.encoding.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opensaml.SAMLAssertion;
import org.psygrid.security.LDAPDirectoryVendors;
import org.psygrid.security.LDAPPasswordHashScheme;
import org.psygrid.security.PGSecurityException;
import org.psygrid.security.attributeauthority.dao.DAOException;
import org.psygrid.security.attributeauthority.dao.GroupDAO;
import org.psygrid.security.attributeauthority.dao.ProjectDAO;
import org.psygrid.security.attributeauthority.dao.UserDAO;
import org.psygrid.security.attributeauthority.model.hibernate.Attribute;
import org.psygrid.security.attributeauthority.model.hibernate.Group;
import org.psygrid.security.attributeauthority.model.hibernate.GroupAttribute;
import org.psygrid.security.attributeauthority.model.hibernate.GroupAttributeLink;
import org.psygrid.security.attributeauthority.model.hibernate.GroupLink;
import org.psygrid.security.attributeauthority.model.hibernate.LoginRecord;
import org.psygrid.security.attributeauthority.model.hibernate.PasswordRecord;
import org.psygrid.security.attributeauthority.model.hibernate.Project;
import org.psygrid.security.attributeauthority.model.hibernate.Role;
import org.psygrid.security.attributeauthority.model.hibernate.User;
import org.psygrid.security.attributeauthority.service.AttributeAuthorityQueryPortType;
import org.psygrid.security.attributeauthority.service.InputFaultMessage;
import org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage;
import org.psygrid.security.attributeauthority.service.ProcessingFaultMessage;
import org.psygrid.security.attributeauthority.types.ChangePasswordRequestType;
import org.psygrid.security.attributeauthority.types.PostProcessLoginResponseType;
import org.psygrid.security.policyauthority.model.hibernate.CompositeRule;
import org.psygrid.security.policyauthority.model.hibernate.Privilege;
import org.psygrid.security.utils.LDAPDistinguishedNameHelper;
import org.psygrid.security.utils.PasswordUtilities;
import org.psygrid.www.xml.security.core.types.AttributeType;
import org.psygrid.www.xml.security.core.types.CompositeRuleType;
import org.psygrid.www.xml.security.core.types.GroupAttributeType;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.ProjectDescriptionType;
import org.psygrid.www.xml.security.core.types.ProjectType;
import org.psygrid.www.xml.security.core.types.RoleType;

public class AttributeAuthorityQueryPortTypeImpl extends AttributeAuthority
		implements AttributeAuthorityQueryPortType, ServiceLifecycle {

	/** logger */
	static Log sLog = LogFactory
			.getLog(AttributeAuthorityQueryPortTypeImpl.class);

	public AttributeAuthorityQueryPortTypeImpl() {
		super();
	}

	public String getAttributesForUserInProject(String user, ProjectType p)
			throws NotAuthorisedFaultMessage, ProcessingFaultMessage {
		String result = null;
		sLog.debug("getAttributesForUserInGroup(" + p + "," + user + ")");

		try {
		    final String METHOD_NAME="getAttributeForUserInProject";
 			String cid = getCallersIdentity();
 			logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, cid, cid);
			if (!getCallerPrincipals().getName().equals(user)) {
				if (!authorisedForProxyRequests(getCallerPrincipals())) {
		             logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, cid, cid);
					throw new NotAuthorisedFaultMessage(
							"Proxy Permission Denied");
				}
			} else {
				if (!authorisedForQueryRequests(getCallerPrincipals())) {
		             logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, cid, cid);
					throw new NotAuthorisedFaultMessage(
							"Query Permission Denied");
				}
			}
		} catch (PGSecurityException pge) {
			throw new ProcessingFaultMessage(pge.getMessage());
		}
		UserDAO userDAO =  this.getUserDAO();
		SAMLAssertion sa = null;
		Project project = Project.fromProjectType(p);	
		List<Attribute> la = userDAO.getAttributesForUserInProject(user, project);

		sa = createNewSAMLAssertion(user, la);	
		if (sa != null) {
			result = sa.toString();
		}  else {
			sLog.info("User "+user+" does not exist");
		}
		return result;
	}

	public java.lang.String getAttributesForUser(java.lang.String user)
			throws java.rmi.RemoteException,
			org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage,
			org.psygrid.security.attributeauthority.service.ProcessingFaultMessage,
			org.psygrid.security.attributeauthority.service.InputFaultMessage {
		String result = null;
		
		try {
		    final String METHOD_NAME="getAttributeForUser";
 			String cid = getCallersIdentity();
 			logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, cid, cid);
			if (!getCallerPrincipals().getName().equals(user)) {
				if (!authorisedForProxyRequests(getCallerPrincipals())) {
		             logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, cid, cid);	 
					throw new NotAuthorisedFaultMessage(
							"Proxy Permission Denied");
				}
			} else {
				if (!authorisedForQueryRequests(getCallerPrincipals())) {
		             logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, cid, cid);	 
					throw new NotAuthorisedFaultMessage(
							"Query Permission Denied");
				}
			}
		} catch (PGSecurityException pge) {
			throw new ProcessingFaultMessage(pge.getMessage());
		}
		UserDAO userDAO =  this.getUserDAO();
		SAMLAssertion sa = null;
		try {
			User u = userDAO.getUserByName(user);
			if (u != null) {
				sa = createNewSAMLAssertion(u);
			} else {
				sLog.info("User "+u+" does not exist");
			}
		} catch (DAOException doae) {
			throw new ProcessingFaultMessage(doae.getMessage());
		}
		if (sa != null) {
			result = sa.toString();
		} 
		return result;
	}
	
	public boolean changePassword(ChangePasswordRequestType pcrt)

			throws java.rmi.RemoteException,
			org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage,
			org.psygrid.security.attributeauthority.service.ProcessingFaultMessage,
			org.psygrid.security.attributeauthority.service.InputFaultMessage {
		String cid = null;
		String uid = pcrt.getUid();
		final String METHOD_NAME = "changePassword";
		
		try {
			logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, uid, getCallersIdentity());
			
			if(uid!=null){
				cid=getCallersIdentityByUID(uid);
				if (!authorisedForProxyRequests(getCallerPrincipals())) {
					logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, cid, getCallersIdentity());
					throw new NotAuthorisedFaultMessage("Proxy Permission Denied");
				}		
			} else {
				cid = getCallersIdentity();
				if (!authorisedForQueryRequests(getCallerPrincipals())) {
					logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, cid, cid);
					throw new NotAuthorisedFaultMessage("Query Permission Denied");
				}
			}
		} catch (PGSecurityException pge) {
			throw new ProcessingFaultMessage(pge.getMessage());
		}
		if (!_isAccountDormantViaDN(cid)) {
			short[] oldPassword = pcrt.getOldPassword();
			short[] newPassword = pcrt.getNewPassword();

			char[] oPassword = new char[oldPassword.length];
			for (int i = 0; i < oPassword.length; i++) {
				oPassword[i] = (char) oldPassword[i];
			}
			if (!performLDAPAuthentication(cid, oPassword)) {
				throw new NotAuthorisedFaultMessage(
						"Could not verfiy existing credentials");
			}
			char[] nPassword = new char[newPassword.length];
			for (int i = 0; i < nPassword.length; i++) {
				nPassword[i] = (char) newPassword[i];
			}
			if (ldapPasswordHash != null) {
				if (ldapPasswordHash.equals(LDAPPasswordHashScheme.SHA
						.toString())) {
					nPassword = PasswordUtilities.hashPassword(nPassword,
							LDAPPasswordHashScheme.SHA, sLog);
				}
			}

			String sPassword = new String(nPassword);

			if (PREVENT_PASSWORD_REUSE) {
				UserDAO userDAO = this.getUserDAO();
				try {
					User u = userDAO.getUserByName(cid);
					if (u != null) {
						for (PasswordRecord pr : u.getPreviousPasswords()) {
							if (sPassword.equals(pr.getPassword())) {
								throw new NotAuthorisedFaultMessage(
										"Attempt to reuse previous password");
							}
						}
					}
				} catch (DAOException doae) {
					throw new ProcessingFaultMessage(doae.getMessage());
				}
			}
			if (cid != null) {
				LdapContext ctx = null;

				ModificationItem[] mods = new ModificationItem[1];

				if(ldapVendor.equals(LDAPDirectoryVendors.MICROSOFT.toString())){
					BasicAttribute unicodePwd = new BasicAttribute("unicodePwd");
					unicodePwd.add(PasswordUtilities.UTFPassword(sPassword));
					mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, unicodePwd);
				} else {
					mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
							new BasicAttribute("userPassword", sPassword));				
				}
				try {
					ctx = createLDAPContext(ldapRoot, ldapRootPassword.toCharArray());
					try {
						ctx.modifyAttributes(cid, mods);
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
					User u = userDAO.getUserByName(cid);
					if (u != null) {
						u.getPreviousPasswords().add(
								new PasswordRecord(new Date(), sPassword));
						u.setPasswordChangeRequired(false);
						userDAO.updateUser(u);
					}
				} catch (DAOException doae) {
					throw new ProcessingFaultMessage(doae.getMessage(),doae);
				}
			} else {
				throw new org.psygrid.security.attributeauthority.service.InputFaultMessage(
						"User ID could not be determined");
			}
		} else {
			logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME+": Attempt to change password for dormant account", uid, getCallersIdentity());
		}
		return true;
	}

	public AttributeType[] getProjectsForUser(java.lang.String user)
	throws java.rmi.RemoteException,
	org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage,
	org.psygrid.security.attributeauthority.service.ProcessingFaultMessage,
	org.psygrid.security.attributeauthority.service.InputFaultMessage {

		
		try {
			final String METHOD_NAME="getProjectsForUser";
			String cid = getCallersIdentity();
			logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, cid, cid);
			if (!getCallerPrincipals().getName().equals(user)) {
				if (!authorisedForProxyRequests(getCallerPrincipals())) {
					logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, cid, cid);	 
					throw new NotAuthorisedFaultMessage(
					"Proxy Permission Denied");
				}
			} else {
				if (!authorisedForQueryRequests(getCallerPrincipals())) {
					logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, cid, cid);	 
					throw new NotAuthorisedFaultMessage(
					"Query Permission Denied");
				}
			}
		} catch (PGSecurityException pge) {
			throw new ProcessingFaultMessage(pge.getMessage());
		}
		UserDAO userDAO =  this.getUserDAO();

		try {
			User u = userDAO.getUserByName(user);
			if (u != null) {
				List<Attribute> la = u.getAttributes();
				AttributeType[] ata = new AttributeType[la.size()];
				for(int i=0; i< la.size(); i++){
					ata[i] = la.get(i).toAttributeType();
				}
				return ata;
			}
		} catch (DAOException doae) {
			throw new ProcessingFaultMessage(doae.getMessage());
		}
		return new AttributeType[0];
	}

	public java.lang.String getMyAttributesInProject(ProjectType p)
			throws java.rmi.RemoteException,
			org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage,
			org.psygrid.security.attributeauthority.service.ProcessingFaultMessage,
			org.psygrid.security.attributeauthority.service.InputFaultMessage {

		return getAttributesForUserInProject(getCallerPrincipals().getName(),
				p);
	}

	public java.lang.String getMyAttributes()
			throws java.rmi.RemoteException,
			org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage,
			org.psygrid.security.attributeauthority.service.ProcessingFaultMessage,
			org.psygrid.security.attributeauthority.service.InputFaultMessage {

		return getAttributesForUser(getCallerPrincipals().getName());
	}
	
	public AttributeType[] getMyProjects()
	throws java.rmi.RemoteException,
	org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage,
	org.psygrid.security.attributeauthority.service.ProcessingFaultMessage,
	org.psygrid.security.attributeauthority.service.InputFaultMessage {
		
		return getProjectsForUser(getCallerPrincipals().getName());
	}

	public String[] getUsersInGroupInProject(org.psygrid.www.xml.security.core.types.GroupType group, org.psygrid.www.xml.security.core.types.ProjectType project) throws java.rmi.RemoteException, org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage, org.psygrid.security.attributeauthority.service.ProcessingFaultMessage, org.psygrid.security.attributeauthority.service.InputFaultMessage{
		sLog.debug("getUsersInGroupInProject()");
		
		try {
		    final String METHOD_NAME="getUsersInGroupInProject";
 			String cid = getCallersIdentity();
 			logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, cid, cid);
			if (!authorisedForQueryRequests(getCallerPrincipals())) {
	             logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, cid, cid);
				throw new NotAuthorisedFaultMessage(
				"Query Permission Denied");
			}
		} catch (PGSecurityException pge) {
			throw new ProcessingFaultMessage(pge.getMessage());
		}
		
		UserDAO userDAO =  this.getUserDAO();

		List<String> ul = userDAO.getUsersInGroupInProject(Project.fromProjectType(project), Group.fromGroupType(group));
		String[] result = new String[ul.size()];
		for(int i=0; i<ul.size(); i++){
			result[i] = ul.get(i);
		}
		return result;
	}
	public String[] getUsersInGroupInProjectWithRole(org.psygrid.www.xml.security.core.types.GroupType group, org.psygrid.www.xml.security.core.types.RoleType role, org.psygrid.www.xml.security.core.types.ProjectType project) throws java.rmi.RemoteException, org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage, org.psygrid.security.attributeauthority.service.ProcessingFaultMessage, org.psygrid.security.attributeauthority.service.InputFaultMessage{
		sLog.debug("getUsersInGroupInProjectWithRole()");
		
		try {
		    final String METHOD_NAME="getUsersInGroupInProjectWithRole";
 			String cid = getCallersIdentity();
 			logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, cid, cid);
			if (!authorisedForQueryRequests(getCallerPrincipals())) {
	             logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, cid, cid);
				throw new NotAuthorisedFaultMessage(
				"Query Permission Denied");
			}
		} catch (PGSecurityException pge) {
			throw new ProcessingFaultMessage(pge.getMessage());
		}
		UserDAO userDAO =  this.getUserDAO();
		List<String> ul = userDAO.getUsersInGroupInProjectWithRole(Project.fromProjectType(project), Group.fromGroupType(group), Role.fromRoleType(role));
		String[] result = new String[ul.size()];
		for(int i=0; i<ul.size(); i++){
			result[i] = ul.get(i);
		}
		return result;
	}
	public String[] getUsersInProjectWithRole(org.psygrid.www.xml.security.core.types.RoleType role, org.psygrid.www.xml.security.core.types.ProjectType project) throws java.rmi.RemoteException, org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage, org.psygrid.security.attributeauthority.service.ProcessingFaultMessage, org.psygrid.security.attributeauthority.service.InputFaultMessage{
		sLog.debug("getUsersInProjectWithRole()");
		
		try {
		    final String METHOD_NAME="getUsersInProjectWithRole";
 			String cid = getCallersIdentity();
 			logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, cid, cid);
			if (!authorisedForQueryRequests(getCallerPrincipals())) {
	             logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, cid, cid);
				throw new NotAuthorisedFaultMessage(
				"Query Permission Denied");
			}
		} catch (PGSecurityException pge) {
			throw new ProcessingFaultMessage(pge.getMessage());
		}
		UserDAO userDAO =  getUserDAO();
		List<String> ul = userDAO.getUsersInProjectWithRole(Project.fromProjectType(project), Role.fromRoleType(role));
		String[] result = new String[ul.size()];
		for(int i=0; i<ul.size(); i++){
			result[i] = ul.get(i);
		}
		return result;	
	}
	
	public String[] getUsersInProjectWithPermission(CompositeRuleType rule, org.psygrid.www.xml.security.core.types.ProjectType project) throws java.rmi.RemoteException, org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage, org.psygrid.security.attributeauthority.service.ProcessingFaultMessage, org.psygrid.security.attributeauthority.service.InputFaultMessage{
		
		List<String> res = new ArrayList<String>();
		
		try {
		    final String METHOD_NAME="getUsersInProjectWithPermission";
 			String cid = getCallersIdentity();
 			logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, cid, cid);
			if (!authorisedForQueryRequests(getCallerPrincipals())) {
	             logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, cid, cid);
				throw new NotAuthorisedFaultMessage(
				"Query Permission Denied");
			}
		} catch (PGSecurityException pge) {
			throw new ProcessingFaultMessage(pge.getMessage());
		}
		UserDAO userDAO =  getUserDAO();
		List<User> ul = userDAO.getUsersAndPrivilegesInProject(Project.fromProjectType(project));
        for(User u : ul){
        	List<Privilege> lp = new ArrayList<Privilege>();
        	Attribute a = u.getAttributeByProject(Project.fromProjectType(project));
        	List<Group> lg = a.getGroups();
        	List<Role> lr = a.getRoles();
        	for(Group g : lg){
        		Privilege p = new Privilege(g.getGroupName(), g.getIdCode());
        		lp.add(p);
        	}
        	for(Role r : lr){
        		Privilege p = new Privilege(r.getRoleName(), r.getIdCode());
        		lp.add(p);
        	}
        	CompositeRule crt = CompositeRule.fromExternalType(rule);
        	if(crt.isPermitted(lp)){
        		res.add(u.getUserName());
        	}
        }
		return res.toArray(new String[res.size()]);	
	}
	
	public GroupType[] getGroupsInProject(org.psygrid.www.xml.security.core.types.ProjectType project) throws java.rmi.RemoteException, org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage, org.psygrid.security.attributeauthority.service.ProcessingFaultMessage, org.psygrid.security.attributeauthority.service.InputFaultMessage{

		sLog.debug("getGroupsInProject()");
		
		try {
		    final String METHOD_NAME="getGroupsInProject";
 			String cid = getCallersIdentity();
 			logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, cid, cid);
			if (!authorisedForQueryRequests(getCallerPrincipals())) {
	             logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, cid, cid);
				throw new NotAuthorisedFaultMessage(
				"Query Permission Denied");
			}
		} catch (PGSecurityException pge) {
			throw new ProcessingFaultMessage(pge.getMessage());
		}
		
		ProjectDAO projectDAO =  getProjectDAO();
		List<Group> lg = projectDAO.getGroupsInProject(Project.fromProjectType(project));
		GroupType[] gt = new GroupType[lg.size()];
		for(int i=0; i< lg.size(); i++){
			gt[i] = lg.get(i).toGroupType();
		}
		return gt;
	}
	public ProjectDescriptionType[] getProjects() throws java.rmi.RemoteException, org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage, org.psygrid.security.attributeauthority.service.ProcessingFaultMessage, org.psygrid.security.attributeauthority.service.InputFaultMessage{
	
		sLog.debug("getProjects()");

		try {
		    final String METHOD_NAME="getProjects";
 			String cid = getCallersIdentity();
 			logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, cid, cid);
			if (!authorisedForQueryRequests(getCallerPrincipals())) {
	             logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, cid, cid);
				throw new NotAuthorisedFaultMessage(
				"Query Permission Denied");
			}
		} catch (PGSecurityException pge) {
			throw new ProcessingFaultMessage(pge.getMessage());
		}

		ProjectDAO projectDAO =  this.getProjectDAO();
		List<Project> lp = projectDAO.getProjects();
		
		ProjectDescriptionType[] pdta = new ProjectDescriptionType[lp.size()];
		for(int i=0; i< lp.size(); i++){
			pdta[i] = lp.get(i).toProjectDescriptionType();
		}
		return pdta;
	}
	

	public RoleType[] getRolesInProject(
			org.psygrid.www.xml.security.core.types.ProjectType project)
			throws java.rmi.RemoteException,
			org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage,
			org.psygrid.security.attributeauthority.service.ProcessingFaultMessage,
			org.psygrid.security.attributeauthority.service.InputFaultMessage {
		sLog.debug("getRolesInProject()");
		
		try {
		    final String METHOD_NAME="getRolesInProject";
 			String cid = getCallersIdentity();
 			logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, cid, cid);
			if (!authorisedForQueryRequests(getCallerPrincipals())) {
	             logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, cid, cid);
				throw new NotAuthorisedFaultMessage(
				"Query Permission Denied");
			}
		} catch (PGSecurityException pge) {
			throw new ProcessingFaultMessage(pge.getMessage());
		}
		ProjectDAO projectDAO =  getProjectDAO();
		List<Role> lg = projectDAO.getRolesInProject(Project.fromProjectType(project));
		RoleType[] rt = new RoleType[lg.size()];
		for(int i=0; i< lg.size(); i++){
			rt[i] = lg.get(i).toRoleType();
		}
		return rt;
	}

	public String[] getUsersInProject(
			org.psygrid.www.xml.security.core.types.ProjectType project)
			throws java.rmi.RemoteException,
			org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage,
			org.psygrid.security.attributeauthority.service.ProcessingFaultMessage,
			org.psygrid.security.attributeauthority.service.InputFaultMessage{
			sLog.debug("getUsersInProject()");
		
		try {
		    final String METHOD_NAME="getUsersInProject";
 			String cid = getCallersIdentity();
 			logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, cid, cid);
			if (!authorisedForQueryRequests(getCallerPrincipals())) {
	             logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, cid, cid);
				throw new NotAuthorisedFaultMessage(
				"Query Permission Denied");
			}
		} catch (PGSecurityException pge) {
			throw new ProcessingFaultMessage(pge.getMessage());
		}
		
		UserDAO userDAO =  this.getUserDAO();
		List<String> ul = userDAO.getUsersInProject(Project.fromProjectType(project));
		String[] result = new String[ul.size()];
		for(int i=0; i<ul.size(); i++){
			result[i] = ul.get(i);
		}
		return result;
	}
	
	public PostProcessLoginResponseType postProcessLogin(String uid) 
	throws java.rmi.RemoteException, 
	org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage, 
	org.psygrid.security.attributeauthority.service.ProcessingFaultMessage, 
	org.psygrid.security.attributeauthority.service.InputFaultMessage
	{
		PostProcessLoginResponseType pplrt = new PostProcessLoginResponseType();
		
		String dn = null;
		if(uid != null){
			dn = getCallersIdentityByUID(uid);
			if(dn==null){
				throw new InputFaultMessage("User "+uid+" does not exist");
			}
		} else {
			dn = getCallerPrincipals().getName();
		}
		
		final String METHOD_NAME="postProcessLogin";
		String cid = getCallersIdentity();
		logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, dn, cid);

		try {
			if (!getCallerPrincipals().getName().equals(dn)) {
				if (!authorisedForProxyRequests(getCallerPrincipals())) {
					logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, dn,
							cid);
					throw new NotAuthorisedFaultMessage(
							"Proxy Permission Denied");
				}
			} else {
				if (!authorisedForQueryRequests(getCallerPrincipals())) {
					logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, dn,
							cid);
					throw new NotAuthorisedFaultMessage(
							"Query Permission Denied");
				}
			}
		} catch (PGSecurityException pge) {
			throw new ProcessingFaultMessage(pge.getMessage());
		}
			
		UserDAO userDAO = this.getUserDAO();
		
		try {
			User u = userDAO.getUserByName(dn);
			if (u != null) {
				if(u.getPasswordChangeRequired()){
					pplrt.setForcePasswordChange(true);
				} else {
					Date now = new Date();
					LoginRecord lr = null;
					Iterator<LoginRecord> it = u.getLoginHistory().iterator();
					while(it.hasNext()){
						lr = it.next();
					}
					if(lr!=null){
						if (now.getTime() - lr.getTimeStamp().getTime() > PASSWORD_LIFETIME_IN_S * 1000
								&& PASSWORD_LIFETIME_IN_S != 0) {
							pplrt.setForcePasswordChange(true);
							u.setPasswordChangeRequired(true);
							userDAO.updateUser(u);
						}
					}
				}
				Calendar[] tsa = new Calendar[u.getLoginHistory().size()];
				String[] ipa = new String[u.getLoginHistory().size()];
				String[] hna = new String[u.getLoginHistory().size()];
				boolean[] ara = new boolean[u.getLoginHistory().size()];
				for(int i=0; i<u.getLoginHistory().size();i++){
					Calendar c = Calendar.getInstance();
					c.setTime(u.getLoginHistory().get(i).getTimeStamp());
					tsa[i]= c;
					ipa[i]=u.getLoginHistory().get(i).getIPAddress();
					hna[i]=u.getLoginHistory().get(i).getCredential();
					ara[i]=u.getLoginHistory().get(i).getAuthenticated();
				}
				pplrt.setPreviousLoginAddresses(ipa);
				pplrt.setPreviousLoginDates(tsa);
				pplrt.setPreviousLoginHosts(hna);
				pplrt.setAuthenticated(ara);
			}
		} catch (DAOException doae) {
			throw new ProcessingFaultMessage(doae.getMessage());
		}
		return pplrt;
	}
	public boolean recordLoginAttempt(String username, boolean authenticated,
			Calendar timeStamp, String ipAddr, String credential)
			throws java.rmi.RemoteException,
			org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage,
			org.psygrid.security.attributeauthority.service.ProcessingFaultMessage,
			org.psygrid.security.attributeauthority.service.InputFaultMessage {

		final String METHOD_NAME = "recordLoginAttempt";
		String cid = getCallersIdentity();
		String dn = getCallersIdentityByUID(username);
		logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, dn, cid);
		
		if(dn==null){
			throw new InputFaultMessage("User "+username+" does not exist");
		}

		try {
			if (!authorisedForAdministrationRequests(getCallerPrincipals())) {
				logHelper
						.logAccessDenied(COMPONENT_NAME, METHOD_NAME, dn, cid);
				throw new NotAuthorisedFaultMessage(
						"Administration Permission Denied");
			}
		} catch (PGSecurityException pge) {
			throw new ProcessingFaultMessage(pge.getMessage());
		}

		UserDAO userDAO = this.getUserDAO();
		
		boolean accountDisabled = false;

		try {			
			accountDisabled = userDAO.recordLoginAttempt(dn, authenticated, timeStamp, ipAddr, credential);
		} catch (DAOException doae) {
			doae.printStackTrace();
			sLog.error(doae.getMessage());
			throw new ProcessingFaultMessage(doae.getMessage());
		}
		return accountDisabled;
	}
	
	public boolean isAccountDormant(String username)
			throws java.rmi.RemoteException,
			org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage,
			org.psygrid.security.attributeauthority.service.ProcessingFaultMessage,
			org.psygrid.security.attributeauthority.service.InputFaultMessage {

		final String METHOD_NAME = "isAccountDormant";
		String cid = getCallersIdentity();

		logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, username, cid);

		try {
			if (!authorisedForQueryRequests(getCallerPrincipals())) {
				logHelper
						.logAccessDenied(COMPONENT_NAME, METHOD_NAME, username, cid);
				throw new NotAuthorisedFaultMessage(
						"Query Permission Denied");
			}
		} catch (PGSecurityException pge) {
			throw new ProcessingFaultMessage(pge.getMessage());
		}
		//Have we got uid or DN?
		try {
			return _isAccountDormantViaDN(username);
		} catch (InputFaultMessage  ifm){
			//try uid
			return _isAccountDormantViaUID(username);
		} 
	}
	
	
	
	public String[] getEmailAddressForUserWithPrivileges(ProjectType pt,
			GroupType gt, RoleType rt) throws java.rmi.RemoteException,
			org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage,
			org.psygrid.security.attributeauthority.service.ProcessingFaultMessage,
			org.psygrid.security.attributeauthority.service.InputFaultMessage {
		String[] users = null;

		List<String> lia = new ArrayList<String>();

		
		try {
		    final String METHOD_NAME="getEmailAddressForUserWithPrivileges";
 			String cid = getCallersIdentity();
 			logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, cid, cid);
			if (!authorisedForQueryRequests(getCallerPrincipals())) {
	             logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, cid, cid);
				throw new NotAuthorisedFaultMessage(
				"Query Permission Denied");
			}
		} catch (PGSecurityException pge) {
			throw new ProcessingFaultMessage(pge.getMessage());
		}
		
		if (pt != null) {
			if (gt != null) {
				if (rt != null) {
					users = getUsersInGroupInProjectWithRole(gt, rt, pt);
				} else {
					users = getUsersInGroupInProject(gt, pt);
				}
			} else {
				if (rt != null) {
					users = getUsersInProjectWithRole(rt, pt);
				} else {
					users = getUsersInProject(pt);
				}
			}
		}

		String[] attrIDs = { "mail" };
		LdapContext ctx = null;
		try {
			ctx = createLDAPContext(ldapRoot, ldapRootPassword.toCharArray());
			for (int i = 0; i < users.length; i++) {
				try {
					Attributes attrs = ctx.getAttributes(users[i], attrIDs);
					if (attrs.get("mail") == null) {
						sLog.info("No email address for user " + users[i]);
					} else {
						String mail = (String) attrs.get("mail").get();
						lia.add(mail);
					}
				} catch (NamingException ne) {
					sLog.error(users[i]+ " caused "+ne.getMessage(),ne);
				}
			}
		} catch (NamingException ne) {
			sLog.error(ne.getMessage(),ne);
		} catch (IOException io) {
			sLog.error("Failed TLS negotiation with LDAP server",io);
		} finally {
			closeLDAPContext(ctx);
		}
		return lia.toArray(new String[lia.size()]);	
	}
	
	/**
	 * Look up a user's email address given their Distinguished Name
	 * 
	 * @param user. The DN of the user to look up
	 * @return InternetAddress if found, otherwise null
	 */
	public String getEmailAddressForUser(String user) 
	throws java.rmi.RemoteException,
	org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage,
	org.psygrid.security.attributeauthority.service.ProcessingFaultMessage,
	org.psygrid.security.attributeauthority.service.InputFaultMessage{
		String email = null;
		
		try {
		    final String METHOD_NAME="getEmailAddressForUser";
 			String cid = getCallersIdentity();
 			logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, cid, cid);
			if (!authorisedForQueryRequests(getCallerPrincipals())) {
	             logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, cid, cid);
				throw new NotAuthorisedFaultMessage(
				"Query Permission Denied");
			}
		} catch (PGSecurityException pge) {
			throw new ProcessingFaultMessage(pge.getMessage(),pge);
		}
		
		if (user != null && !(_isAccountDormantViaDN(user))) {
			String[] attrIDs = { "mail" };
			LdapContext ctx = null;
			try {
				ctx = createLDAPContext(ldapRoot, ldapRootPassword.toCharArray());
				try {
					Attributes attrs = ctx.getAttributes(user, attrIDs);
					if (attrs.get("mail") == null) {
						sLog.info("No email address for user " + user);
					} else {
						email = (String) attrs.get("mail").get();
					}
				} catch (NamingException ne) {
					sLog.error(user+" caused "+ne.getMessage(),ne);
				}
			} catch (NamingException ne) {
				sLog.error(ne.getMessage(),ne);
			} catch (IOException io) {
				sLog.error("Failed TLS negotiation with LDAP server",io);
			} finally {
				closeLDAPContext(ctx);
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
	public String getMobileNumberForUser(String user) 
	throws java.rmi.RemoteException,
	org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage,
	org.psygrid.security.attributeauthority.service.ProcessingFaultMessage,
	org.psygrid.security.attributeauthority.service.InputFaultMessage {
		String number = null;
		
		try {
		    final String METHOD_NAME="getMobileNumberForUser";
 			String cid = getCallersIdentity();
 			logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, cid, cid);
			if (!authorisedForQueryRequests(getCallerPrincipals())) {
	             logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, cid, cid);
				throw new NotAuthorisedFaultMessage(
				"Query Permission Denied");
			}
		} catch (PGSecurityException pge) {
			throw new ProcessingFaultMessage(pge.getMessage());
		}
		
		if (user != null && !(_isAccountDormantViaDN(user))) {
			String[] attrIDs = { "mobile" };
			LdapContext ctx = null;
			try {
				ctx = createLDAPContext(ldapRoot, ldapRootPassword.toCharArray());
				try {
					Attributes attrs = ctx.getAttributes(user, attrIDs);
					if (attrs.get("mobile") == null) {
						sLog.info("No mobile number for user " + user);
					} else {
						number = (String) attrs.get("mobile").get();
					}
				} catch (NamingException ne) {
					sLog.error(user+" caused "+ne.getMessage());
				}
			} catch (NamingException ne) {
				sLog.error(ne.getMessage());
			}  catch (IOException io) {
				sLog.error("Failed TLS negotiation with LDAP server");
			} finally {
				closeLDAPContext(ctx);
			}
		}
		return number;	
	}
	
	@Deprecated
    private static String encrypt(char[] input) {
        String hashed = null;
		byte[] inb = new byte[input.length];
		for (int i = 0; i < inb.length; i++) {
			inb[i] = (byte) input[i];
		}
        if ( null != input ){
            try{
                MessageDigest md = MessageDigest.getInstance( "SHA" );
                byte[] result = md.digest(inb);
                hashed =  Base64.encode(result);
            }
            catch(NoSuchAlgorithmException ex){
                sLog.error("encrypt: "+ex.getClass().getSimpleName(),ex);
            }
        }
        return hashed;
    }

	
	public String getSystemLevelPrivilege() throws RemoteException,
			ProcessingFaultMessage, NotAuthorisedFaultMessage,
			InputFaultMessage {

		String result = null;
		sLog.debug("getSystemLevelPrivilege");

		try {
		    final String METHOD_NAME="getSystemLevelPrivilege";
 			String cid = getCallersIdentity();
 			logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, cid, cid);
				if (!authorisedForProxyRequests(getCallerPrincipals())) {
		             logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, cid, cid);
					throw new NotAuthorisedFaultMessage(
							"Proxy Permission Denied");
				}
		} catch (PGSecurityException pge) {
			throw new ProcessingFaultMessage(pge.getMessage());
		}
		
		SAMLAssertion sa = createSystemSAMLAssertion();
		String saml = sa.toString();
		
		return saml;
	}

	
	public GroupAttributeType[] getGroupAttributesForUserInGroup(
			String projectCode, String groupCode, String userForename,
			String userSurname) throws RemoteException, ProcessingFaultMessage,
			NotAuthorisedFaultMessage, InputFaultMessage {
		
		//Retrieve the GroupAttribute(s) assigned to the user for the given group.
		//Throw an input fault message if the user isn't in the project and/or group specified in the input parameters.
		try {
		    final String METHOD_NAME="getGroupAttributesForUserInGroup";
 			String cid = getCallersIdentity();
 			logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, cid, cid);
			if (!authorisedForQueryRequests(getCallerPrincipals())) {
	             logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, cid, cid);
				throw new NotAuthorisedFaultMessage(
				"Query Permission Denied");
			}
		} catch (PGSecurityException pge) {
			throw new ProcessingFaultMessage(pge.getMessage());
		}
		
		String userName = "CN=" + userForename + " " + userSurname +  ", " + this.ldapUserBaseDN;
		
		userName = LDAPDistinguishedNameHelper.whitespaceFormatDistinguishedName(userName);		
		
		UserDAO userDAO =  this.getUserDAO();
		SAMLAssertion sa = null;
		
		Project project = new Project(null);
		project.setIdCode(projectCode);
		
		List<Attribute> la = userDAO.getAttributesForUserInProject(userName, project);
		
		//Find the Attributes that are for the group specified in the input parameter.
		List<GroupAttribute> returnList = new ArrayList<GroupAttribute>();
		
		boolean groupCodeFound = false;
		boolean userBelongsToAtLeastOneGroup = false;
		for(Attribute a : la){
			if(a.getGroupLink()!= null && a.getGroupLink().size() > 0){
				userBelongsToAtLeastOneGroup = true;
				List<GroupLink> gLinks = a.getGroupLink();
				for(GroupLink gL : gLinks){
					if(gL.getGroup().getIdCode().equals(groupCode)){
						groupCodeFound = true; //Indicates that the user has at least one attribute that is associated with the groupCode passed into the method.
						List<GroupAttributeLink> grpAttrLinks = gL.getGroupAttriubutes();
						for(GroupAttributeLink gAL : grpAttrLinks){
							returnList.add(gAL.getGroupAttribute());
						}
					}
				}	
			}
		} // end of attribute-scanning loop
		
		if(!userBelongsToAtLeastOneGroup){
			throw new InputFaultMessage("User" + userName + " does not belong to ANY groups.");
		}
		
		if(!groupCodeFound){
			throw new InputFaultMessage("User" + userName + " is not a member of centre " + groupCode + ".");
		}

		//Now we need to convert the return list contents into an array with of type GroupAttributeType
		GroupAttributeType[] returnArray = new GroupAttributeType[returnList.size()];
		
		for(int count = 0; count < returnList.size(); count++){
			returnArray[count] = returnList.get(count).toGroupAttributeType();
		}
		
		return returnArray;
	}

	
	public GroupAttributeType[] getGroupAttributesForGroup(String projectCode,
			String groupCode) throws RemoteException, ProcessingFaultMessage,
			NotAuthorisedFaultMessage, InputFaultMessage {
		
		try {
		    final String METHOD_NAME="getGroupAttributesForGroup";
 			String cid = getCallersIdentity();
 			logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, cid, cid);
			if (!authorisedForQueryRequests(getCallerPrincipals())) {
	             logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, cid, cid);
				throw new NotAuthorisedFaultMessage(
				"Query Permission Denied");
			}
		} catch (PGSecurityException pge) {
			throw new ProcessingFaultMessage(pge.getMessage());
		}
		
		Project p = new Project(null);
		p.setIdCode(projectCode);
		List<Group> groups = getProjectDAO().getGroupsInProject(p);
		
		if (groups == null || groups.size() == 0){
			//In this case either the project code was wrong or there were no groups in the project.
			throw new InputFaultMessage("Either the specified project code is incorrect or the project has no groups");
		}
		
		Group theGroup = null;
		
		for(Group g : groups){
			if(g.getIdCode().equals(groupCode)){
				theGroup = g;
			}
		}
		
		if(theGroup == null){
			throw new InputFaultMessage("Group with code " + groupCode + " is not in project with project code " + projectCode + ".");
		}
		
		Long groupId = theGroup.getId();
		
		GroupDAO gDAO = getGroupDAO();
		List<GroupAttribute> groupAttributeList = null;
		
		try {
			groupAttributeList = gDAO.getGroupAttributesForGroup(groupId);
		} catch (DAOException e) {
			throw new ProcessingFaultMessage(e);
		}
		
		int listCount = groupAttributeList.size();
		GroupAttributeType[] returnArray = new GroupAttributeType[listCount];
		for(int i = 0; i < listCount; i++){
			returnArray[i] = groupAttributeList.get(i).toGroupAttributeType();
		}
		
		return returnArray;
	}
}
