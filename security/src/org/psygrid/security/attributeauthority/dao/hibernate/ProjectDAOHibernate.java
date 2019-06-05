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



package org.psygrid.security.attributeauthority.dao.hibernate;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.psygrid.security.attributeauthority.dao.DAOException;
import org.psygrid.security.attributeauthority.dao.ObjectOutOfDateException;
import org.psygrid.security.attributeauthority.dao.ProjectDAO;
import org.psygrid.security.attributeauthority.model.IProject;
import org.psygrid.security.attributeauthority.model.hibernate.Group;
import org.psygrid.security.attributeauthority.model.hibernate.Project;
import org.psygrid.security.attributeauthority.model.hibernate.Role;
import org.psygrid.www.xml.security.core.types.ProjectType;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * @author John Ainsworth (stolen, cloned and adapted from the Rob Harper original)
 *
 */

public class ProjectDAOHibernate extends HibernateDaoSupport implements ProjectDAO {
	
	/**
     * Retrieve a list of the known Projects from the database.
     * 
     * @return List of projects.
     */
    @SuppressWarnings("unchecked")
    public List<Project> getProjects(){
        return getHibernateTemplate().find("from org.psygrid.security.attributeauthority.model.hibernate.Project");
    }
    
	/**
	 * Retrieve a Project from the database with the specified name
	 * 
	 * @param name The name of the project
	 * @return List of projects.
	 */
	@SuppressWarnings("unchecked")
	public Project getProjectByName(String name) throws DAOException {
		Project found = null;
		List<Project> pl = getHibernateTemplate().find(
				"from Project x where x.projectName = ?", name);
		if (!pl.isEmpty()) {
			for (int i = 1; i < pl.size(); i++) {
				// delete the other projects
				// should not be any!
				removeProject(pl.get(i).getId());
			}
			found = pl.get(0);
		}
		return found;
	}
	
	/**
	 * Retrieve a Project from the database with the specified name
	 * 
	 * @param name The name of the project
	 * @return List of projects.
	 */
	@SuppressWarnings("unchecked")
	public Project getProjectByUniqueId(String id) throws DAOException {
		Project found = null;
		List<Project> pl = getHibernateTemplate().find(
				"from Project x where x.idCode = ?", id);
		if (!pl.isEmpty()) {
			for (int i = 1; i < pl.size(); i++) {
				// delete the other projects
				// should not be any!
				removeProject(pl.get(i).getId());
			}
			found = pl.get(0);
		}
		return found;
	}
	
	/**
	 * Retrieve a Project from the database from the project type
	 * 
	 * @param name The name of the project
	 * @return List of projects.
	 */
	@SuppressWarnings("unchecked")
	public Project getProject(ProjectType pt) throws DAOException {
		Project found = null;
		if(pt.getName()!=null){
			found = getProjectByName(pt.getName());
		}
		if(pt.getIdCode()!=null && found==null){
			found = getProjectByUniqueId(pt.getIdCode());
		}
		return found;
	}
	/**
	 * Retrieve a Project from the database from the project type
	 * 
	 * @param name The name of the project
	 * @return List of projects.
	 */
	@SuppressWarnings("unchecked")
	public Project getProject(Project p) throws DAOException {
		Project found = null;
		if(p.getProjectName()!=null){
			found = getProjectByName(p.getProjectName());
		}
		if(p.getIdCode()!=null && found==null){
			found = getProjectByUniqueId(p.getIdCode());
		}
		return found;
	}
    /**
	 * Retrieve a single project from the database.
	 * 
	 * @param projectId
	 *            Unique identifier of the Project to retrieve.
	 * @return The Project with the unique identifier in the argument.
	 * @throws DAOException
	 *             if no Project exists with the unique identifier specified in the
	 *             argument.
	 */
    public IProject getProject(final Long projectId) throws DAOException {
    	HibernateCallback callback = new HibernateCallback(){
    		public Object doInHibernate(Session session){
    			Project u = (Project)session.createCriteria(Project.class)
    			.add(Restrictions.idEq(projectId))
    			.uniqueResult();
    			if ( null != u ){
    				//initialize all lazy collections containing
    				//children of the DataSet or its child Folders
    				//initializeFolders(ds.getChildren());
    			}
    			return u;
    		}
    	};
    	
    	Project u = (Project)getHibernateTemplate().execute(callback);
    	if ( null == u ){
    		throw new DAOException("No DataSet exists in the repository for id = "+projectId);
    	}
    	return u;
    }
   
    
    /**
     * Add a single project to the repository.
     * 
     * @param project The Project to add.
     * @throws DAOException if the project exists.
     */
    public void addProject(IProject project)
        throws DAOException,ObjectOutOfDateException {

        Project p = (Project)project;
        try{
            getHibernateTemplate().saveOrUpdate(p);
        } catch (Exception e){
        		e.printStackTrace();
        }
    }
    
    /**
     * Remove a single Project from the data repository.
     * 
     * @param projectId Unique identifier of the Project to remove.
     * @throws DAOException if no Project exists with the unique 
     * identifier specified in the argument
     */
    public void removeProject(Long projectId) 
        throws DAOException{
        Project u = (Project)getHibernateTemplate().get(Project.class, projectId);
        if ( null == u ){
            throw new DAOException("No project exists in the database for id = "+projectId);
        }
        getHibernateTemplate().delete(u);    	
    }
    
    /**
	 * Update a project's record.
	 * 
	 * @param project.
	 *            The project to update
	 * @throws DAOException
	 *             if no project exists with the project.projectName;
	 */
	public void updateProject(IProject project) throws DAOException {

		Project u = (Project) project;

		if (null != u.getId()) {
			// for existing projects, check that it has not been deleted or
			// published
			Project storedProject = (Project) getHibernateTemplate().get(
					Project.class, u.getId());
			if (null == storedProject) {
				throw new DAOException(
						"Cannot save project - the object is out-of-date");
			}
			// when transactions are used the same session is used
			// throughout the transaction. Need to evict the "stored"
			// user otherwise an exception will be thrown when
			// trying to saveOrUpdate the subject due to the existence
			// of an object with the same ID already being in the session
			getHibernateTemplate().evict(storedProject);
		}
		try {
			getHibernateTemplate().saveOrUpdate(u);
		} catch (HibernateOptimisticLockingFailureException ex) {
			throw new DAOException(
					"Cannot save DataSet - HibernateOptimisticLockingFailureException",
					ex);
		}
	}
    
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psygrid.security.attributeauthority.dao.ProjectDAO#getRolesInProject(org.psygrid.security.attributeauthority.model.hibernate.Project)
	 */
	public List<Role> getRolesInProject(Project p){
		List<Role> sl = getHibernateTemplate().find(
				"select r " +
				"from Project as p " +
				"join p.roles as r " +
				"where (p.projectName = ? or p.idCode = ?) ", 
				new Object[]{p.getProjectName(), p.getIdCode()});
		return sl;		
	}
	/* (non-Javadoc)
	 * @see org.psygrid.security.attributeauthority.dao.ProjectDAO#getGroupsInProject(org.psygrid.security.attributeauthority.model.hibernate.Project)
	 */
	public List<Group> getGroupsInProject(Project p){
		List<Group> sl = getHibernateTemplate().find(
				"select g " +
				"from Project as p " +
				"join p.groups as g " +
				"where (p.projectName = ? or p.idCode = ?) ", 
				new Object[]{p.getProjectName(), p.getIdCode()});
		return sl;		
	}
	
	public void addGroup(String projectCode, String code, String name) {
		
		// The inverse of the group->project relationship is not
		// modelled - so we have to load the project separately.
		Project project = getProjectByUniqueId(projectCode);
		
		Group group = new Group();
		group.setIdCode(code);
		group.setGroupName(name);
		group.setParentName(project.getIdCode());
		project.addGroup(group);
	}

	public void updateGroup(String projectCode, String groupCode,String newCode, String newName) {
				
		// We also have to retrieve the group using its 'parentName'.
		// The 'parentName' is the projectCode NOT the projectName as
		// you may have supposed.
		List<?> results = getHibernateTemplate().find("from Group g where " +
				"g.parentName = ? and g.idCode = ?", new Object[]{projectCode,groupCode});
		Group group = (Group)DataAccessUtils.requiredSingleResult(results);
		group.setIdCode(newCode);
		group.setGroupName(newName);
	}

	public void deleteGroup(String projectCode, String groupCode) {

		// We also have to retrieve the group using its 'parentName'.
		// The 'parentName' is the projectCode NOT the projectName as
		// you may have supposed.
		List<?> results = getHibernateTemplate().find("from Group g where " +
				"g.parentName = ? and g.idCode = ?", new Object[]{projectCode,groupCode});
		Group group = (Group)DataAccessUtils.requiredSingleResult(results);
		
		// The inverse of the group->project relationship is not
		// modelled - so we have to load the project separately.
		Project project = getProjectByUniqueId(projectCode);

		// The relationship is mapped using 'all-delete-orphan' so we don't need
		// to explicitly delete the group.
		project.getGroups().remove(group);
	}
	

	
}
