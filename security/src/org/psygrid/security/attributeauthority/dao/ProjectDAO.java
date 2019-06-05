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

import java.util.List;

import org.psygrid.security.attributeauthority.model.IProject;
import org.psygrid.security.attributeauthority.model.hibernate.Group;
import org.psygrid.security.attributeauthority.model.hibernate.Project;
import org.psygrid.security.attributeauthority.model.hibernate.Role;
import org.psygrid.www.xml.security.core.types.ProjectType;

/**
 * @author jda
 *
 */
public interface ProjectDAO {
	
	
    /**
     * Retrieve a list of the known Projects from the database.
     * 
     * @return List of projects.
     */
    public List<Project> getProjects();

    
    /**
     * Retrieve a list of th Projects with the name specified from the database.
     * 
     * @param name The name of the project
     * @return List of projects.
     */
    public Project getProjectByName(String name)
    		throws DAOException;
    
    /**
     * Retrieve a single project from the database.
     * 
     * @param projectId Unique identifier of the Project to retrieve.
     * @return The Project with the unique identifier in the argument.
     * @throws DAOException if no Project exists with the unique 
     * identifier specified in the argument.
     */
    public IProject getProject(Long projectId) 
        throws DAOException;
   
    
    /**
     * Retrieve a single project from the database.
     * 
     * @param project ProjectType of the Project to retrieve.
     * @return The Project that corresponds to the argument.
     * @throws DAOException if no Project exists with the unique 
     * identifier specified in the argument.
     */
    public Project getProject(ProjectType pt) 
        throws DAOException;
    
    /**
     * Retrieve a single project from the database.
     * 
     * @param project Project of the Project to retrieve.
     * @return The Project that corresponds to the argument.
     * @throws DAOException if no Project exists with the unique 
     * identifier specified in the argument.
     */
    public Project getProject(Project p) 
        throws DAOException;
    
    /**
     * Add a single project to the repository.
     * 
     * @param project The Project to add.
     * @throws DAOException if the project exists.
     */
    public void addProject(IProject project)
        throws DAOException, ObjectOutOfDateException;
    
    /**
     * Remove a single Project from the data repository.
     * 
     * @param projectId Unique identifier of the Project to remove.
     * @throws DAOException if no Project exists with the unique 
     * identifier specified in the argument
     */
    public void removeProject(Long projectId) 
        throws DAOException, ObjectOutOfDateException;
    
    /**
     * Update a project's record.
     * 
     * @param project. The project to update
     * @throws DAOException if no project exists with the project.projectName;
     */
    public void updateProject(IProject project) 
        throws DAOException;
    
	/**
	 * Find the groups in the specified project
	 * @param p The project
	 * @return A List of the Groups
	 */
    
	public List<Group> getGroupsInProject(Project p);

	/**
	 * Find the roles in the specified project
	 * @param p The project
	 * @return A list of the Roles
	 */
	public List<Role> getRolesInProject(Project p);

	/**
	 * This method will add a group to the project.
	 * 
	 * @param projectCode the project
	 * @param newCode the new group code
	 * @param newName the new group name
	 */
	public void addGroup(String projectCode,String code,String name);
	
	/**
	 * This method will update an existing group.
	 * 
	 * @param projectCode the project
	 * @param groupCode the existing group code
	 * @param newCode the new group code
	 * @param newName the new group name
	 */
	public void updateGroup(String projectCode,String groupCode,String newCode,String newName);

	/**
	 * This method will delete an existing group.
	 * 
	 * @param projectCode the project
	 * @param groupCode the existing group code
	 */
	public void deleteGroup(String projectCode,String groupCode);

}


