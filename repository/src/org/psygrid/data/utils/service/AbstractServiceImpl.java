/*
Copyright (c) 2006-2009, The University of Manchester, UK.

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

package org.psygrid.data.utils.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.common.identifier.IdentifierHelper;
import org.psygrid.common.identifier.InvalidIdentifierException;
import org.psygrid.data.repository.dao.RepositoryDAO;
import org.psygrid.data.utils.security.DocumentSecurityHelper;
import org.psygrid.data.utils.security.NotAuthorisedFault;
import org.psygrid.logging.AuditLogger;
import org.psygrid.security.PGSecurityException;
import org.psygrid.security.PGSecurityInvalidSAMLException;
import org.psygrid.security.PGSecuritySAMLVerificationException;
import org.psygrid.security.RBACAction;
import org.psygrid.security.accesscontrol.AEFGroup;
import org.psygrid.security.accesscontrol.AEFProject;
import org.psygrid.security.accesscontrol.IAccessEnforcementFunction;

/**
 * Provides a common base class for services.
 * 
 * Mostly contains security relation code.
 * 
 * @author Terry Child
 */
public abstract class AbstractServiceImpl  {

	/**
	 * General purpose logger
	 */
	private static Log sLog = LogFactory.getLog(AbstractServiceImpl.class);

	/**
	 * Audit logger
	 */
	private static AuditLogger logHelper = null;

	protected RepositoryDAO repositoryDAO = null; 
	
	/**
	 * Called to check permissions
	 */
    protected IAccessEnforcementFunction accessControl = null;

	/**
	 * Called to filter documents and document instances
	 */
	protected DocumentSecurityHelper docHelper = null;

	public IAccessEnforcementFunction getAccessControl() {
		return accessControl;
	}

	public void setAccessControl(IAccessEnforcementFunction accessControl) {
		this.accessControl = accessControl;
	}

	public DocumentSecurityHelper getDocHelper() {
		return docHelper;
	}

	public void setDocHelper(DocumentSecurityHelper docHelper) {
		this.docHelper = docHelper;
	}	

	public RepositoryDAO getRepositoryDAO() {
		return repositoryDAO;
	}

	public void setRepositoryDAO(RepositoryDAO repositoryDAO) {
		this.repositoryDAO = repositoryDAO;
	}
	
	public void setLogHelper(AuditLogger logHelper) {
		AbstractServiceImpl.logHelper = logHelper;
	}

	protected abstract String getComponentName();

	/**
	 * Gets the user name from the supplied saml assertion.
	 * @param saml
	 * @return the user name
	 */
	protected String findUserName(String saml){
        
        //find invoker's username
        String userName = null;
        try{
            userName = accessControl.getUserFromUnverifiedSAML(saml);
        }
        catch(PGSecurityException ex){
            userName = "Unknown";
        }
        return userName;
    }
	
	// These checkPermission methods should probably be in a base class.

	/**
	 * Check permissions for a given action against the project and groups of the supplied record identifier.
	 * Throw a NotAuthorisedFault exception if permission denied.
	 */
	protected void checkPermissionsByIdentifier(String saml,String method,RBACAction action,String identifier) throws NotAuthorisedFault {

		String projectCode;
		String groupCode;
		try {
			projectCode = IdentifierHelper.getProjectCodeFromIdentifier(identifier);
			groupCode = IdentifierHelper.getGroupCodeFromIdentifier(identifier);
		} catch (InvalidIdentifierException ex) {
			throw new NotAuthorisedFault("The supplied identifier is invalid.", ex);
		}
		checkPermissionsByGroup(saml, method, action, projectCode, groupCode);
	}
	
	
	/**
	 * Check permissions for a given action within a given project and group.
	 * Throw a NotAuthorisedFault exception if permission denied.
	 */
	protected void checkPermissionsByGroup(String saml,String method,RBACAction action,String projectCode,String groupCode) throws NotAuthorisedFault {

		String userName = findUserName(saml);
		String callerIdentity = accessControl.getCallersIdentity();
		try {
			logHelper.logMethodCall(getComponentName(), method, userName,callerIdentity);

			if (!accessControl.authoriseUser(saml, new AEFGroup(null,groupCode, null), action.toAEFAction(), 
												new AEFProject(null,projectCode,false))) {
				logHelper.logAccessDenied(getComponentName(), method, userName,callerIdentity);
				throw new NotAuthorisedFault("User '" + userName
						+ "' is not authorised to perform the action '"
						+ method + "' for project '" + projectCode + "', group '"
						+ groupCode + "'");
			}
		} catch (PGSecurityInvalidSAMLException ex) {
			sLog.error(method + ": " + ex.getClass().getSimpleName(), ex);
			throw new NotAuthorisedFault(
					"The supplied SAML assertion has expired", ex);
		} catch (PGSecuritySAMLVerificationException ex) {
			sLog.error(action + ": " + ex.getClass().getSimpleName(), ex);
			throw new NotAuthorisedFault(
					"The supplied SAML assertion does not come from a trusted issuer",
					ex);
		} catch (PGSecurityException ex) {
			sLog.error(action + ": " + ex.getClass().getSimpleName(), ex);
			throw new NotAuthorisedFault(
					"An error occurred during authorisation", ex);
		}
	}

	/**
	 * Check permissions for a given action within a given project and set of groups.
	 * Throw a NotAuthorisedFault exception if permission denied for any of the groups.
	 */
	protected void checkPermissionsByGroups(String saml,String method,RBACAction action,String projectCode,String[] groups) throws NotAuthorisedFault {

		String userName = findUserName(saml);
		String callerIdentity = accessControl.getCallersIdentity();
		try {
			logHelper.logMethodCall(getComponentName(), method, userName,callerIdentity);
			for(String groupCode:groups){
				if (!accessControl.authoriseUser(saml, new AEFGroup(null,groupCode, null), action.toAEFAction(), 
													new AEFProject(null,projectCode,false))) {
					logHelper.logAccessDenied(getComponentName(), method, userName,callerIdentity);
					throw new NotAuthorisedFault("User '" + userName
							+ "' is not authorised to perform the action '"
							+ method + "' for project '" + projectCode + "', group '"
							+ groupCode + "'");
				}
			}
		} catch (PGSecurityInvalidSAMLException ex) {
			sLog.error(method + ": " + ex.getClass().getSimpleName(), ex);
			throw new NotAuthorisedFault(
					"The supplied SAML assertion has expired", ex);
		} catch (PGSecuritySAMLVerificationException ex) {
			sLog.error(action + ": " + ex.getClass().getSimpleName(), ex);
			throw new NotAuthorisedFault(
					"The supplied SAML assertion does not come from a trusted issuer",
					ex);
		} catch (PGSecurityException ex) {
			sLog.error(action + ": " + ex.getClass().getSimpleName(), ex);
			throw new NotAuthorisedFault(
					"An error occurred during authorisation", ex);
		}
	}
	
	/**
	 * Check permissions for a given action within a given project.
	 * Throw a NotAuthorisedFault exception if permission denied.
	 */
	protected void checkPermissionsByProject(String saml,String method,RBACAction action,String projectCode) throws NotAuthorisedFault {

		String userName = findUserName(saml);
		String callerIdentity = accessControl.getCallersIdentity();
		try {
			logHelper.logMethodCall(getComponentName(), method, userName,callerIdentity);
			if (!accessControl.authoriseUser(saml, new AEFGroup(),action.toAEFAction(),new AEFProject(null,projectCode,false))) {
				logHelper.logAccessDenied(getComponentName(), method, userName,callerIdentity);
				throw new NotAuthorisedFault("User '" + userName
						+ "' is not authorised to perform the action '"
						+ method + "' for project '" + projectCode + "'");
			}
		} catch (PGSecurityInvalidSAMLException ex) {
			sLog.error(method + ": " + ex.getClass().getSimpleName(), ex);
			throw new NotAuthorisedFault(
					"The supplied SAML assertion has expired", ex);
		} catch (PGSecuritySAMLVerificationException ex) {
			sLog.error(action + ": " + ex.getClass().getSimpleName(), ex);
			throw new NotAuthorisedFault(
					"The supplied SAML assertion does not come from a trusted issuer",
					ex);
		} catch (PGSecurityException ex) {
			sLog.error(action + ": " + ex.getClass().getSimpleName(), ex);
			throw new NotAuthorisedFault(
					"An error occurred during authorisation", ex);
		}
	}

}
