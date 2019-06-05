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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.psygrid.security.attributeauthority.dao.DAOException;
import org.psygrid.security.attributeauthority.dao.ObjectOutOfDateException;
import org.psygrid.security.attributeauthority.dao.UserDAO;
import org.psygrid.security.attributeauthority.model.IUser;
import org.psygrid.security.attributeauthority.model.hibernate.Attribute;
import org.psygrid.security.attributeauthority.model.hibernate.Group;
import org.psygrid.security.attributeauthority.model.hibernate.GroupAttribute;
import org.psygrid.security.attributeauthority.model.hibernate.LoginRecord;
import org.psygrid.security.attributeauthority.model.hibernate.Project;
import org.psygrid.security.attributeauthority.model.hibernate.Role;
import org.psygrid.security.attributeauthority.model.hibernate.User;
import org.psygrid.security.attributeauthority.service.InputFaultMessage;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * @author John Ainsworth (stolen, cloned and adapted from the Rob Harper original)
 *
 */

public class UserDAOHibernate extends HibernateDaoSupport implements UserDAO {
	
	static protected long ACCOUNT_LOCKOUT_DURATION_IN_S = 1800L;	
	
	static protected int ALLOWED_LOGIN_ATTEMPTS = 3;

	
	/**
     * Retrieve a list of the known Users from the database.
     * 
     * @return List of users.
     */
    @SuppressWarnings("unchecked")
    public List<User> getUsers(){
        return getHibernateTemplate().find("from org.psygrid.security.attributeauthority.model.hibernate.User");
    }
    
	/**
	 * Retrieve a Users from the database with the specified name
	 * 
	 * @param name The name of the user
	 * @return List of users.
	 */
	@SuppressWarnings("unchecked")
	public User getUserByName(String name) throws DAOException {
		User found = null;
		List<User> ul = getHibernateTemplate().find(
				"from User x where x.userName = ?", name);
		if (!ul.isEmpty()) {
			for (int i = 1; i < ul.size(); i++) {
				// delete the other users
				// should not be any!
				removeUser(ul.get(i).getId());
			}
			found = ul.get(0);
		}
		return found;
	}
	
	public User getPasswordResetUser(String uuid) {
		User user = (User)DataAccessUtils.singleResult(
				getHibernateTemplate().find("from User u where u.passwordResetUUID = ?", uuid));
		return user;
	}

	
	/**
	 * Move this code here to make it transactional.
	 * @throws InputFaultMessage 
	 */
    public boolean recordLoginAttempt(String username, boolean authenticated,
			Calendar timeStamp, String ipAddr, String credential)
    		throws DAOException, InputFaultMessage {

    	boolean accountDisabled = false;

    	User u = this.getUserByName(username);
		if (u != null) {
			u.getLoginHistory().add(
					new LoginRecord(authenticated, timeStamp.getTime(),
							ipAddr, credential));

			int failedCount=0;
			Date now = new Date();		
			for (LoginRecord lr : u.getLoginHistory()){
				if(now.getTime()-lr.getTimeStamp().getTime()<ACCOUNT_LOCKOUT_DURATION_IN_S*1000){
					if(!lr.getAuthenticated()){
						failedCount++;
					}
				}
			}
		
			if(failedCount>ALLOWED_LOGIN_ATTEMPTS){
				accountDisabled = true;
			}
			if(u.isDormant()){
				accountDisabled = true;
			}
		} else {
			throw new InputFaultMessage("User "+username+" does not exist");
		}

		return accountDisabled;
    }
    
    /**
	 * Retrieve a single user from the database.
	 * 
	 * @param userId
	 *            Unique identifier of the User to retrieve.
	 * @return The User with the unique identifier in the argument.
	 * @throws DAOException
	 *             if no User exists with the unique identifier specified in the
	 *             argument.
	 */
	public IUser getUser(final Long userId) throws DAOException {
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){
				User u = (User)session.createCriteria(User.class)
				.add(Restrictions.idEq(userId))
				.uniqueResult();
				return u;
			}
		};
		
		User u = (User)getHibernateTemplate().execute(callback);
		if ( null == u ){
			throw new DAOException("No User exists in the repository for id = "+userId);
		}
		return u;
	}
	
    
    /**
     * Add a single user to the repository.
     * 
     * @param user The User to add.
     * @throws DAOException if the user exists.
     */
    public synchronized void addUser(IUser user)
        throws DAOException,ObjectOutOfDateException {

        User u = (User)user;
        try{
            getHibernateTemplate().saveOrUpdate(u);
        }
        catch (HibernateOptimisticLockingFailureException ex){
            throw new ObjectOutOfDateException("Cannot save user - the object is out-of-date",ex);
        }
    }
    
    /**
     * Remove a single User from the data repository.
     * 
     * @param userId Unique identifier of the User to remove.
     * @throws DAOException if no User exists with the unique 
     * identifier specified in the argument
     */
    public synchronized void removeUser(Long userId) 
        throws DAOException{
        User u = (User)getHibernateTemplate().get(User.class, userId);
        if ( null == u ){
            throw new DAOException("No user exists in the database for id = "+userId);
        }
        getHibernateTemplate().delete(u);    	
    }
    
    /**
	 * Update a user's record.
	 * 
	 * @param user.
	 *            The user to update
	 * @throws DAOException
	 *             if no user exists with the user.userName;
	 */
	public synchronized void updateUser(IUser user) throws DAOException {

		User u = (User) user;

		if (null != u.getId()) {
			// for existing users, check that it has not been deleted
			User storedUser = (User) getHibernateTemplate().get(User.class,
					u.getId());
			if (null == storedUser) {
				// user has been deleted by another session
				throw new DAOException(
						"Cannot save user - the object is out-of-date");
			}
			// when transactions are used the same session is used
			// throughout the transaction. Need to evict the "stored"
			// user otherwise an exception will be thrown when
			// trying to saveOrUpdate the subject due to the existence
			// of an object with the same ID already being in the session
			getHibernateTemplate().evict(storedUser);
		}
		try {
			getHibernateTemplate().saveOrUpdate(u);
		} catch (HibernateOptimisticLockingFailureException ex) {
			throw new DAOException(
					"Cannot save User HibernateOptimisticLockingFailureException",
					ex);
		}
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psygrid.security.attributeauthority.dao.hibernate.UserDAO#getUsersInGroupInProject(org.psygrid.security.attributeauthority.model.hibernate.Project,
	 *      org.psygrid.security.attributeauthority.model.hibernate.Group)
	 */
	public List<String> getUsersInGroupInProject(Project p, Group g){
		List<String> sl = getHibernateTemplate().find(
				"select u.userName " +
				"from User as u " +
				"join u.attributes as a " +
				"join a.groupLink as gl " +
				"where (a.project.projectName = ? or a.project.idCode = ?) " +
				"and (gl.group.groupName = ? or gl.group.idCode = ?)" +
				"and u.dormant = ?", 
				new Object[]{p.getProjectName(), p.getIdCode(), 
					g.getGroupName(), g.getIdCode(), Boolean.FALSE});
		return sl;	
	}
	/* (non-Javadoc)
	 * @see org.psygrid.security.attributeauthority.dao.UserDAO#getUsersInProjectWithRole(org.psygrid.security.attributeauthority.model.hibernate.Project, org.psygrid.security.attributeauthority.model.hibernate.Role)
	 */
	public List<String> getUsersInProjectWithRole(Project p, Role r){
		List<String> sl = getHibernateTemplate().find(
				"select u.userName " +
				"from User as u " +
				"join u.attributes as a " +
				"join a.roleLink as rl " +
				"where (a.project.projectName = ? or a.project.idCode = ?) " +
				"and (rl.role.roleName = ? or rl.role.idCode = ?)" +
				"and u.dormant = ?", 
				new Object[]{p.getProjectName(), p.getIdCode(), 
					r.getRoleName(), r.getIdCode(), Boolean.FALSE});
		return sl;
	}
	/* (non-Javadoc)
	 * @see org.psygrid.security.attributeauthority.dao.UserDAO#getUsersInProject(org.psygrid.security.attributeauthority.model.hibernate.Project)
	 */
	public List<String> getUsersInProject(Project p){
		List<String> sl = getHibernateTemplate().find(
				"select u.userName " +
				"from User as u " +
				"join u.attributes as a " +
				"where (a.project.projectName = ? or a.project.idCode = ?) "+
				"and u.dormant = ?", 
				new Object[]{p.getProjectName(), p.getIdCode(), Boolean.FALSE});
		return sl;		
	}
	/* (non-Javadoc)
	 * @see org.psygrid.security.attributeauthority.dao.UserDAO#getUsersInProject(org.psygrid.security.attributeauthority.model.hibernate.Project)
	 */
	public List<User> getUsers(Project p){
		List<User> sl = getHibernateTemplate().find(
				"select u " +
				"from User as u " +
				"join u.attributes as a " +
				"where (a.project.projectName = ? or a.project.idCode = ?) ", 
				new Object[]{p.getProjectName(), p.getIdCode()});
		return sl;		
	}
	/* (non-Javadoc)
	 * @see org.psygrid.security.attributeauthority.dao.UserDAO#getUsersInProject(org.psygrid.security.attributeauthority.model.hibernate.Project)
	 */
	public List<User> getUsersAndPrivilegesInProject(Project p){
		List<User> sl = getHibernateTemplate().find(
				"select u " +
				"from User as u " +
				"join u.attributes as a " +
				"where (a.project.projectName = ? or a.project.idCode = ?) " +
				"and u.dormant = ?", 
				new Object[]{p.getProjectName(), p.getIdCode(), Boolean.FALSE});
	
		// remove all other attributes, 
		// they requestor may not be authorised to view them
		if(sl!=null){
			for(User u:sl){
				ArrayList<Attribute> ala = new ArrayList<Attribute>();
				ala.add(u.getAttributeByProject(p));
				u.setAttributes(ala);
			}
		}
		return sl;		
	}
	/* (non-Javadoc)
	 * @see org.psygrid.security.attributeauthority.dao.UserDAO#getUsersInGroupInProjectWithRole(org.psygrid.security.attributeauthority.model.hibernate.Project, org.psygrid.security.attributeauthority.model.hibernate.Group, org.psygrid.security.attributeauthority.model.hibernate.Role)
	 */
	public List<String> getUsersInGroupInProjectWithRole(Project p, Group g, Role r){
		List<String> sl = getHibernateTemplate().find(
				"select u.userName " +
				"from User as u " +
				"join u.attributes as a " +
				"join a.groupLink as gl " +
				"join a.roleLink as rl " +
				"where (a.project.projectName = ? or a.project.idCode = ?) " +
				"and (gl.group.groupName = ? or gl.group.idCode = ?) " +
				"and (rl.role.roleName = ? or rl.role.idCode = ?) " +
				"and u.dormant = ?", 
				new Object[]{p.getProjectName(), p.getIdCode(), 
					g.getGroupName(), g.getIdCode(),
					r.getRoleName(), r.getIdCode(),
					Boolean.FALSE});
		return sl;	
	}
	/* (non-Javadoc)
	 * @see org.psygrid.security.attributeauthority.dao.UserDAO#getAttributesForUserInProject(java.lang.String, org.psygrid.security.attributeauthority.model.hibernate.Project)
	 */
	public List<Attribute> getAttributesForUserInProject(String u, Project p){
		List<Attribute> al = getHibernateTemplate().find(
				"select a " +
				"from User as u " +
				"join u.attributes as a " +
				"where (a.project.projectName = ? or a.project.idCode = ?) " +
				"and (u.userName = ?)", 
				new Object[]{p.getProjectName(), p.getIdCode(), u});
		return al;		
	}
	
	public List<GroupAttribute> getGroupAttributesForUserInGroup(String u, String projectCode, String groupCode){
		
		return null;
	}

	public synchronized void markUserAsDormant(Long userId) throws DAOException {
        User u = (User)getHibernateTemplate().get(User.class, userId);
        if ( null == u ){
            throw new DAOException("No user exists in the database for id = "+userId);
        }
		u.setDormant(true);
		getHibernateTemplate().saveOrUpdate(u);
	}

	public List<String> getProjectNames(String userDN) {
		@SuppressWarnings("unchecked")
		List<String> projectNames = (List<String>)getHibernateTemplate().find("select p.projectName " +
				"from User u join u.attributes a "+
				"join a.project p where u.userName=?",userDN);	
		return projectNames;
	}	
	
	public List<Project> getProjectsByUserOld(String userDN) {
		@SuppressWarnings("unchecked")
		List<Project> projects = (List<Project>)getHibernateTemplate().find("select p " +
				"from User u join u.attributes a "+
				"join a.project p where u.userName=?",userDN);	
		return projects;
	}

	public List<Project> getProjects(String userDN,String roleID) {
		// This query should really check for a privilege instead of a role,
		// but that would mean calling the PA - so for now it checks for a role.
		@SuppressWarnings("unchecked")
		List<Project> projects = (List<Project>)getHibernateTemplate().find("select distinct p from User u " +
				"join u.attributes a "+
				"join a.project p "+
				"join a.roleLink rl "+
				"join rl.role r "+
				"where u.userName=? and p.virtual=false and r.idCode=?",new Object[]{userDN,roleID});	
		return projects;
	}

}
