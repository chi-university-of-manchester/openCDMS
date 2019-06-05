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

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.security.attributeauthority.dao.ProjectDAO;
import org.psygrid.security.attributeauthority.dao.UserDAO;
import org.psygrid.security.attributeauthority.model.hibernate.Group;
import org.psygrid.security.attributeauthority.model.hibernate.PasswordRecord;
import org.psygrid.security.attributeauthority.model.hibernate.Project;
import org.psygrid.security.attributeauthority.model.hibernate.User;

/**
 * Implementation of AttributeAuthorityService.
 * 
 * @see AttributeAuthorityService
 * @author Terry Child
 */
public class AttributeAuthorityServiceImpl implements AttributeAuthorityService {
	
	private static Log log = LogFactory.getLog(AttributeAuthorityServiceImpl.class);

	/*
	 * The following are injected by the application context.
	 */

	UserDAO userDAO;

	ProjectDAO projectDAO;

	LDAPHelper ldapHelper;
		
	/**
	 * 
	 */
	public AttributeAuthorityServiceImpl() {
	}

	/**
	 * @param userDAO the userDAO to set
	 */
	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	/**
	 * @param projectDAO the projectDAO to set
	 */
	public void setProjectDAO(ProjectDAO projectDAO) {
		this.projectDAO = projectDAO;
	}
	
	/**
	 * @param ldapHelper the ldapHelper to set
	 */
	public void setLdapHelper(LDAPHelper ldapHelper) {
		this.ldapHelper = ldapHelper;
	}

	public String getUserDN(String userID) {
		return ldapHelper.getUserDN(userID);
	}
	
	public String generatePasswordResetUUID(String userDN){
		
		String result = null;

		User user = userDAO.getUserByName(userDN);

		if(user==null){
			log.warn("No matching user in the AA for DN='"+userDN+"'");						
		}
		else {
			
			// Generate a UUID and store it against the user
	
			String uuid = UUID.randomUUID().toString();
			
			user.setPasswordResetDate(new Date());
			user.setPasswordResetUUID(uuid);
			
			userDAO.updateUser(user);
			
			result = uuid;
		}

		return result;
	}

	public String getUserEmailAddress(String userDN) {
		return ldapHelper.getUserEmailAddress(userDN);
	}

	public User getPasswordResetUser(String uuid) {
		return userDAO.getPasswordResetUser(uuid);
	}

	public boolean setPassword(String userDN, String password) {
		
		// The ldapHelper may return a hashed version of the new password.
		String savedPassword = ldapHelper.setPassword(userDN, password);
		if(savedPassword!=null){
			User u = userDAO.getUserByName(userDN);
			if (u != null) {
				u.setPasswordChangeRequired(false);
				u.setPasswordResetDate(null);
				u.setPasswordResetUUID(null);
				u.getPreviousPasswords().add(new PasswordRecord(new Date(), savedPassword));
				userDAO.updateUser(u);
			}
		}
		return savedPassword!=null;
	}

	public List<Project> getProjects(String userDN,String roleID) {
		return userDAO.getProjects(userDN,roleID);
	}
	
	public void groupUpdated(String projectCode, String groupCode, String newCode, String newName) {
		projectDAO.updateGroup(projectCode, groupCode, newCode, newName);
	}

	public void groupAdded(String projectCode, String code, String name) {
		projectDAO.addGroup(projectCode, code, name);
	}

	public void groupDeleted(String projectCode, String groupCode) {
		projectDAO.deleteGroup(projectCode, groupCode);
	}


}
