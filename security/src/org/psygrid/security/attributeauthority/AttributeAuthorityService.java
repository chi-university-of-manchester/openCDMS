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

import java.util.List;

import org.psygrid.security.attributeauthority.model.hibernate.Group;
import org.psygrid.security.attributeauthority.model.hibernate.Project;
import org.psygrid.security.attributeauthority.model.hibernate.User;

/**
 * Service interface to a subset of the attribute authority functionality.
 * 
 * Used directly by the web application.
 * 
 * @author Terry Child
 *
 */
public interface AttributeAuthorityService {

	/**
	 * Retrieve a user DN from a user login name.
	 * @param userID the login name
	 * @return the LDAP DN
	 */
	public String getUserDN(String userID);
	
	/**
	 * Generate a UUID and store it and the current time against the given user.
	 * 
	 * @param userID the login user name of the user
	 * @return the UUID or null if the user does not exist
	 */
	public String generatePasswordResetUUID(String userDN);

	/**
	 * Get an email address for a user.
	 * 
	 * @param userDN the DN of the user
	 * @return the email address or null if the user does not exist
	 */
	public String getUserEmailAddress(String userDN);

	/**
	 * Get the user associated with a password reset request.
	 * 
	 * @param uuid the uuid that was emailed to the user
	 * @return the user containing the 
	 */
	public User getPasswordResetUser(String uuid);
	
	/**
	 * Set a user's password.
	 * 
	 * @param userDN the user name
	 * @param password the new plain text password
	 * @return true if successful
	 */
	public boolean setPassword(String userDN,String password);
	
	/**
	 * Returns the list of projects where the given user has the given role.
	 * 
	 * Does not return 'virtual' projects.
	 * 
	 * @param userDN the user distinguished name
	 * @param roleID the role ID - usually the same as the role name
	 * @return the list of projects
	 */
	public List<Project> getProjects(String userDN,String roleID);

	/**
	 * This method should be called when a group is added to the repository.
	 * 
	 * @param projectCode the project
	 * @param newCode the new group code
	 * @param newName the new groups name
	 */
	public void groupAdded(String projectCode,String newCode,String newName);

	/**
	 * This method should be called when a group is updated in the repository.
	 * 
	 * @param projectCode the project
	 * @param groupCode the existing group code
	 * @param newCode the new group code
	 * @param newName the new groups name
	 */
	public void groupUpdated(String projectCode,String groupCode,String newCode,String newName);
	
	/**
	 * This method should be called when a group is deleted from the repository.
	 * 
	 * @param projectCode the project
	 * @param groupCode the group
	 */
	public void groupDeleted(String projectCode,String groupCode);

}
