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


//Created on Oct 27, 2005 by John Ainsworth



package org.psygrid.security.attributeauthority.dao;

import java.util.Calendar;
import java.util.List;

import org.psygrid.security.attributeauthority.model.IUser;
import org.psygrid.security.attributeauthority.model.hibernate.Attribute;
import org.psygrid.security.attributeauthority.model.hibernate.Group;
import org.psygrid.security.attributeauthority.model.hibernate.Project;
import org.psygrid.security.attributeauthority.model.hibernate.Role;
import org.psygrid.security.attributeauthority.model.hibernate.User;
import org.psygrid.security.attributeauthority.service.InputFaultMessage;

/**
 * @author John Ainsworth
 *
 */
public interface UserDAO {

	
    /**
     * Retrieve a list of the known Users from the database.
     * 
     * @return List of users.
     */
    public List<User> getUsers();

    
    /**
     * Retrieve a list of th Users with the name specified from the database.
     * 
     * @param name The name of the user
     * @return List of users.
     */
    public User getUserByName(String name)
    		throws DAOException;

    /**
     * Retrieve a user with the specified password reset UUID.
     * 
     * @param uuid the uuid generated during a password reset request.
     * @return the User or null if non exists.
     */
    public User getPasswordResetUser(String uuid);

    /**
     * Record a user login attempt
     * 
     * @param name The name of the user
     * @return List of users.
     */
    public boolean recordLoginAttempt(String username, boolean authenticated,
			Calendar timeStamp, String ipAddr, String credential)
    		throws DAOException,InputFaultMessage;
    
    /**
     * Retrieve a single user from the database.
     * 
     * @param userId Unique identifier of the User to retrieve.
     * @return The User with the unique identifier in the argument.
     * @throws DAOException if no User exists with the unique 
     * identifier specified in the argument.
     */
    public IUser getUser(Long userId) 
        throws DAOException;
   
    
    /**
     * Add a single user to the repository.
     * 
     * @param user The User to add.
     * @throws DAOException if the user exists.
     */
    public void addUser(IUser user)
        throws DAOException, ObjectOutOfDateException;
    
    /**
     * Remove a single User from the data repository.
     * 
     * @param userId Unique identifier of the User to remove.
     * @throws DAOException if no User exists with the unique 
     * identifier specified in the argument
     */
    public void removeUser(Long userId) 
        throws DAOException, ObjectOutOfDateException;
    
    /**
     * Update a user's record.
     * 
     * @param user. The user to update
     * @throws DAOException if no user exists with the user.userName;
     */
    public void updateUser(IUser user) 
        throws DAOException;
    
	/**
	 * Query for users in a group in a project with the specified role 
	 * @param p The project
	 * @param g The group
	 * @param r The role
	 * @return A List of user names as String
	 */
	public List<String> getUsersInGroupInProjectWithRole(Project p, Group g, Role r);
	/**
	 * Query for users in a group in a project
	 * @param p The project
	 * @param g The group
	 * @return A List of user names as String
	 */
	public List<String> getUsersInGroupInProject(Project p, Group g);
	/**
	 * Query for users in  a project with the specified role
	 * @param p The project
	 * @param r The role
	 * @return A List of user names as String
	 */
	public List<String> getUsersInProjectWithRole(Project p, Role r);
	/**
	 * Query for users in a project
	 * @param p The project
	 * @return A List of user names as String
	 */
	public List<String> getUsersInProject(Project p);
	/**
	 * Query for users in a project
	 * @param p The project
	 * @return A List of user names as String
	 */
	public List<User> getUsers(Project p);
	/**
	 * Query for users in a project
	 * @param p The project
	 * @return A List of user names as String
	 */
	public List<User> getUsersAndPrivilegesInProject(Project p);
	/**
	 * Find the attributes for a user in the specified project
	 * @param u The user
	 * @param p The project
	 * @return A List of the user's Attributes in the Project
	 */
	public List<Attribute> getAttributesForUserInProject(String u, Project p);

	/**
	 * Mark a user as being dormant.
	 * <p>
	 * When dormant a user can no longer use the system; they are
	 * only kept on record to prevent the same user name from being 
	 * re-used.
	 * 
	 * @param userId The id of the user to mark as dormant.
	 * @throws DAOException
	 */
	public void markUserAsDormant(Long userId) throws DAOException;
		
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

}
