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

package org.psygrid.security.attributeauthority.model;

import java.util.ArrayList;

import org.psygrid.security.attributeauthority.dao.ProjectDAO;
import org.psygrid.security.attributeauthority.model.hibernate.Group;
import org.psygrid.security.attributeauthority.model.hibernate.Project;
import org.psygrid.security.attributeauthority.model.hibernate.Role;
import org.psygrid.security.attributeauthority.model.hibernate.User;
import org.psygrid.www.xml.security.core.types.ProjectDescriptionType;
import org.psygrid.www.xml.security.core.types.UserPrivilegesType;

/**
 * Factory interface used to manage the creation of data repository
 * model objects.
 * 
 * @author Rob Harper, clone and adapted by John Ainsworth
 *
 */
public interface Factory {
    
    /**
     * Create a new role with the given name.
     * 
     * @param name The name of the new role.
     * @return The new role.
     */
    public IRole createRole(String name);
 
    /**
     * Create a new group with the given name.
     * 
     * @param name The name of the new group.
     * @return The new group.
     */
    public IGroup createGroup(String name);
    
    /**
     * Create a new project with the given name and list of roles
     * 
     * @param name The name of the new project.
     * @param id The id of this project
     * @param groups The list of groups for this project 
     * @param roles The list of roles for this project 
     * @return The new group.
     */
    public IProject createProject(String name, String id, ArrayList<Group> groups, ArrayList<Role> roles);
    	 
 
    /**
     * Create a new user with the given name.
     * 
     * @param name The name of the new user.
     * @param projectDAO A project data access object
     * @return The new user.
     */   
    public IUser createUser(String name, ProjectDAO projectDAO);
 
    /**
     * Create a new user from the supplied object.
     * 
     * @param ugrt The complete specification of the user
     * @param projectDAO A project data access object          
     * @return The new user.
     */   
    public User createUser(UserPrivilegesType ugrt, ProjectDAO projectDAO);
    
    /**
     * Create a new user with the given name and list of groups
     * 
     * @param name The name of the new user.
     * @param projects The list of projects for this user
     * @param projectDAO A project data access object
     * @return The new user.
     */
//    public User createUser(String name, ArrayList<Attribute> attrs, ProjectDAO projectDAO);
    
    /**
     * Create a new project from the supplied object.
     * 
     * @param pdt The complete specification of the project
     * @return The new Project.
     */   
    public Project createProject(ProjectDescriptionType pdt);
}

