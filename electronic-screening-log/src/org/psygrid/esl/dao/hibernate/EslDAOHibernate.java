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

package org.psygrid.esl.dao.hibernate;

import java.lang.reflect.InvocationTargetException;
import java.net.ConnectException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Restrictions;
import org.psygrid.common.email.QueuedEmail;
import org.psygrid.esl.dao.DAOException;
import org.psygrid.esl.dao.DuplicateObjectException;
import org.psygrid.esl.dao.EslDAO;
import org.psygrid.esl.dao.NoResultsFoundException;
import org.psygrid.esl.dao.ObjectOutOfDateException;
import org.psygrid.esl.dao.SubjectExistsException;
import org.psygrid.esl.dao.SubjectLockedException;
import org.psygrid.esl.dao.TooManyResultsException;
import org.psygrid.esl.model.IAuditable;
import org.psygrid.esl.model.IGroup;
import org.psygrid.esl.model.IProject;
import org.psygrid.esl.model.ISubject;
import org.psygrid.esl.model.hibernate.Change;
import org.psygrid.esl.model.hibernate.Group;
import org.psygrid.esl.model.hibernate.Persistent;
import org.psygrid.esl.model.hibernate.Project;
import org.psygrid.esl.model.hibernate.ProvenanceChange;
import org.psygrid.esl.model.hibernate.ProvenanceLog;
import org.psygrid.esl.model.hibernate.Randomisation;
import org.psygrid.esl.model.hibernate.Role;
import org.psygrid.esl.model.hibernate.Strata;
import org.psygrid.esl.model.hibernate.Subject;
import org.psygrid.esl.scheduling.hibernate.QueuedSMS;
import org.psygrid.esl.util.EmailUtil;
import org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * 
 * 
 * @author Lucy Bridges
 *
 */
public class EslDAOHibernate extends HibernateDaoSupport implements EslDAO {

	private static Log sLog = LogFactory.getLog(EslDAOHibernate.class);
			
	private static SimpleDateFormat ddMmmYyyyFormatter = new SimpleDateFormat("dd-MMM-yyyy");
		
	private EmailUtil emailUtil;

	/**
	 * The 'from address' applied to emails sent as a result of changes
	 * to Subject.
	 */
	private String emailFromAddress;

	/**
	 * The 'to address' applied to emails sent as a result of changes
	 * to Subjects
	 */
	private String emailToAddress;

	/**
	 * The role types to be emailed as a result of changes to Subjects.
	 * 
	 * All users having these roles, as specified by the security system,
	 * will be notified.
	 * 
	 * This is specified in the applicationContext.xml
	 */
	private List<String> rolesToEmail = new ArrayList<String>();

    // From PersistentDAO

    public org.psygrid.esl.model.IPersistent getPersistent(Long persistentId) throws DAOException {
        Persistent p = (Persistent)getHibernateTemplate().get(Persistent.class, persistentId);
        if ( null == p ){
            throw new DAOException("No Persistent exists in the repository for id = "+persistentId);
        }
        return p;
    }
    
    public boolean doesObjectExist(String objectName, Long objectId) throws DAOException {
    	final String query = "select id from "+objectName+" where id=?";
    	
    	Iterator it = (Iterator)getHibernateTemplate().find(query, objectId).iterator();
    	try {
    		return it.hasNext();
    	}
    	catch (NullPointerException npe) {
    		//throw DAOException("");
    		return false;
    	}
    	
    }

    // From ProjectDAO
    
	public org.psygrid.esl.model.dto.Project getProject(final Long projectId) throws DAOException {

		HibernateCallback callback = new HibernateCallback(){

			public Object doInHibernate(Session session){
				Project project = (Project)session.createCriteria(Project.class)
				.add(Restrictions.idEq(projectId))
				.uniqueResult();

				org.psygrid.esl.model.dto.Project dtoDS = null;
				if ( project != null ){
					dtoDS = project.toDTO();
				}
				return dtoDS;
			}
		};

		org.psygrid.esl.model.dto.Project project = (org.psygrid.esl.model.dto.Project)getHibernateTemplate().execute(callback);
		if ( null == project ){
			throw new NoResultsFoundException("No Project exists in the ESL for id = "+projectId);
		}
		return project;
	}

	public org.psygrid.esl.model.dto.Project getProject(final String projectCode) throws DAOException {

		HibernateCallback callback = new HibernateCallback(){

			public Object doInHibernate(Session session){
				Project project = (Project)session.createCriteria(Project.class)
				.add(Restrictions.like("projectCode", projectCode))
				.uniqueResult();

				org.psygrid.esl.model.dto.Project dtoDS = null;
				if ( project != null ){
					dtoDS = project.toDTO();
				}
				return dtoDS;
			}
		};

		org.psygrid.esl.model.dto.Project project = (org.psygrid.esl.model.dto.Project)getHibernateTemplate().execute(callback);
		if ( null == project ){
			throw new NoResultsFoundException("No Project exists in the ESL for project code = "+projectCode);
		}
		return project;
	}

	public Long saveProject(org.psygrid.esl.model.dto.Project project, String username) throws DAOException, DuplicateObjectException, ObjectOutOfDateException {

		Project proj = project.toHibernate();

		// If this is an existing project check for duplicate groups.
		if ( null != proj.getId() ){
			
			//for existing Projects, check that it has not been deleted
			Project storedProject = (Project)getHibernateTemplate().get(Project.class, proj.getId());

			if (null == storedProject){
				//project has been deleted by another session
				throw new ObjectOutOfDateException("Cannot save Project - the object is out-of-date and could have been deleted elsewhere.");
			}
			

			//check that all groups associated with this project have unique group codes
			List<IGroup> newGroups = proj.getGroups();
			List<org.psygrid.esl.model.hibernate.Group> groups = getGroups(proj);

			//retrieve existing groups and compare group codes
			for (IGroup newGroup: newGroups) {
				if (newGroup.getId() == null) {
					//group doesn't have an id so must be new
					for (IGroup g: groups) {
						if (newGroup.getGroupCode().equals(g.getGroupCode())) {
							throw new DuplicateObjectException("The group code '"+newGroup.getGroupCode()+"' already exists in the project "+project.getProjectName()+". Project cannot be saved.");
						}
					}
				}
			}
			

			//when transactions are used the same session is used
			//throughout the transaction. Need to evict the "stored"
			//project object otherwise an exception will be thrown
			//when trying to saveOrUpdate the project due to the existence
			//of an object with the same ID already being in the session
			for (IGroup g: groups) {
				getHibernateTemplate().evict(g);
			}
			getHibernateTemplate().evict(storedProject);
			
		}


		List<IGroup> newGroups = proj.getGroups();
		//compare each groupcode with other group codes to ensure it's unique
		for (IGroup newGroup: newGroups) {
			for (IGroup g: newGroups) {
				if (g != newGroup && newGroup.getGroupCode().equals(g.getGroupCode())) {
					throw new DuplicateObjectException("The group code '"+newGroup.getGroupCode()+"' is not unique within the project "+project.getProjectName()+". Project cannot be saved.");
				}
			}
		}


		try{
			//this is used to track who created or edited the objects in question
			//(objects must extend Auditable for this to have an effect)
			EntityInterceptor.setUserName(username);
			getHibernateTemplate().saveOrUpdate(proj);

			return proj.getId();
		}
		catch (DataIntegrityViolationException e) {
			throw new DuplicateObjectException("Duplicate entry exception when saving the project "+proj.getProjectCode(), e);
		}
		catch (HibernateOptimisticLockingFailureException ex){
			//Note that this catch block will NEVER be entered if
			//this method is called using Spring declarative transaction
			//management. In that case, the exception is caught in
			//SaveObjectInterceptor
			throw new ObjectOutOfDateException("Cannot save Project - the object is out-of-date",ex);
		}

	}

	public boolean isRandomised(final String projectCode)
	throws DAOException {
		if ( projectCode == null ){
			throw new DAOException("No project code specified");
		}
		try {
			HibernateCallback callback = new HibernateCallback(){
				public Object doInHibernate(Session session){
					Long id = (Long)session.createQuery("select p.id from Project p inner join p.randomisation as r" +
							" where p.projectCode=?")
					.setString(0, projectCode)
					.uniqueResult();
					return id;
				}
			};
			
			Object id = getHibernateTemplate().execute(callback);
			System.out.println("Found "+id+" for project "+projectCode);
			if ( id == null ){
				return false;
			}
		}
		catch (Exception e) {
			throw new DAOException("Problem retrieving randomisation information for this project "+e.getMessage(), e);
		}

		return true;

	}

	public boolean isEslProject(final String projectCode)
	throws DAOException {
		if ( projectCode == null ){
			throw new DAOException("No project code specified");
		}
		try {
			@SuppressWarnings("unchecked")
			HibernateCallback callback = new HibernateCallback(){
				public Object doInHibernate(Session session){
					Long id = (Long)session.createQuery("select id from Project p where p.projectCode=?")
					.setString(0, projectCode)
					.uniqueResult();
					return id;
				}
			};

			Object id = getHibernateTemplate().execute(callback);
			if ( id == null ){
				return false;
			}
		}
		catch (Exception e) {
			sLog.error("In isEslProject for code "+projectCode+": ", e);
			return false;
		}

		return true;
	}

	/** 
	 * Finds the existing groups for the given project
	 */
	private List<org.psygrid.esl.model.hibernate.Group> getGroups(Project project) {

		final Project hProject = project;
		@SuppressWarnings("unchecked")
		HibernateCallback callback = new HibernateCallback(){

			public Object doInHibernate(Session session){
				List<org.psygrid.esl.model.hibernate.Group> group = session.createCriteria(org.psygrid.esl.model.hibernate.Group.class)
				.add(Restrictions.eq("project", hProject)).list();
				return group;
			}
		};
		@SuppressWarnings("unchecked")
		List<org.psygrid.esl.model.hibernate.Group> group = (List<org.psygrid.esl.model.hibernate.Group>)getHibernateTemplate().execute(callback);

		return group;
	}

	public void deleteProject(final long projectId, final String projectCode) throws DAOException {

		HibernateCallback callback = new HibernateCallback(){

			public Object doInHibernate(Session session){
				Project p = (Project)session.createQuery("from Project p where p.id=? and p.projectCode=?")
				.setLong(0, projectId)
				.setString(1, projectCode)
				.uniqueResult();
				if ( null == p ){
					return new DAOException("No project found with id="+projectId+" and code="+projectCode);
				}

				session.delete(p);

				return null;
			}
		};

		Object result = getHibernateTemplate().execute(callback);
		if ( result instanceof DAOException ){
			throw (DAOException)result;
		}

	}

  		
	// From GroupDAO		

	public org.psygrid.esl.model.dto.Group getGroup(final Long groupId)
	throws DAOException { 
		HibernateCallback callback = new HibernateCallback(){

			public Object doInHibernate(Session session){
				Group group = (Group)session.createCriteria(Group.class)
				.add(Restrictions.idEq(groupId))
				.uniqueResult();

				org.psygrid.esl.model.dto.Group dtoDS = null;
				if ( group != null ){
					dtoDS = group.toDTO();
				}
				return dtoDS;
			}
		};

		org.psygrid.esl.model.dto.Group group = (org.psygrid.esl.model.dto.Group)getHibernateTemplate().execute(callback);
		if ( null == group ){
			throw new DAOException("No group exists in the repository for id = "+groupId);
		}
		return group;
	}

	public org.psygrid.esl.model.dto.Group[] getAllGroups(final Long projectId)
	throws DAOException {
		
		org.psygrid.esl.model.dto.Group[] groups = null;
		HibernateCallback callback = new HibernateCallback(){

			public Object doInHibernate(Session session){
				List group = session.createQuery(
						"from Group g where g.project_id=?")
						.setLong(0, projectId)
						.list();

				org.psygrid.esl.model.dto.Group[] dtoGroups = new org.psygrid.esl.model.dto.Group[group.size()];
				
				for (int i = 0; i < group.size(); i++) {
					if ( group != null ){
						dtoGroups[i] = ((org.psygrid.esl.model.hibernate.Group)group.get(i)).toDTO();
					}
				}
				
				return dtoGroups;
			}
		};

		groups = (org.psygrid.esl.model.dto.Group[])getHibernateTemplate().execute(callback);
		if ( null == groups ){
			throw new DAOException("No project exists with id = "+projectId);
		}
		return groups;
	}
	
	
	// From SubjectDAO	

	public String getEmailFromAddress() {
		return emailFromAddress;
	}

	public void setEmailFromAddress(String emailFromAddress) {
		this.emailFromAddress = emailFromAddress;
	}

	public String getEmailToAddress() {
		return emailToAddress;
	}

	public void setEmailToAddress(String emailToAddress) {
		this.emailToAddress = emailToAddress;
	}

	public List<String> getRolesToEmail() {
		return rolesToEmail;
	}

	public void setRolesToEmail(List<String> rolesToEmail) {
		this.rolesToEmail = rolesToEmail;
	}

	public EmailUtil getEmailUtil() {
		return emailUtil;
	}

	public void setEmailUtil(EmailUtil emailUtil) {
		this.emailUtil = emailUtil;
	}

	public org.psygrid.esl.model.dto.Subject getSubject(final Long subjectId) throws NoResultsFoundException, SubjectLockedException {

		HibernateCallback callback = new HibernateCallback(){

			public Object doInHibernate(Session session){
				Subject subject = (Subject)session.createCriteria(Subject.class)
				.add(Restrictions.idEq(subjectId))
				.uniqueResult();

				if ( null == subject ){
					return new NoResultsFoundException("No Subject exists in the repository for id = "+subjectId);
				}

				if ( subject.isLocked() ){
					return new SubjectLockedException("The Subject has been locked");
				}

				org.psygrid.esl.model.dto.Subject dtoSubject = null;
				if ( subject != null ){
					dtoSubject = subject.toDTO();
				}
				return dtoSubject;
			}
		};

		Object result = getHibernateTemplate().execute(callback);
		if ( result instanceof NoResultsFoundException ){
			throw (NoResultsFoundException)result;
		}
		if ( result instanceof SubjectLockedException ){
			throw (SubjectLockedException)result;
		}
		return (org.psygrid.esl.model.dto.Subject)result;
	}


	public org.psygrid.esl.model.dto.Subject getSubject(final org.psygrid.esl.model.dto.Project project, final String studyNumber) throws NoResultsFoundException, SubjectLockedException {


		//retrieve the groups belonging to the specified project
		final List<Group> groups = getGroups(project);

		HibernateCallback callback = new HibernateCallback(){

			public Object doInHibernate(Session session){

				Subject subject = (Subject) session.createCriteria(Subject.class)
				.add(Restrictions.eq("studyNumber", studyNumber))
				.add(Restrictions.in("group", groups))
				.uniqueResult();

				if ( null == subject ){
					return new NoResultsFoundException("No Subject exists in the ESL for study number = "+studyNumber);
				}

				if ( subject.isLocked() ){
					return new SubjectLockedException("The Subject has been locked");
				}

				org.psygrid.esl.model.dto.Subject dtoSubject = null;
				if ( subject != null ){
					dtoSubject = subject.toDTO();
				}
				return dtoSubject;
			}
		};

		Object result = getHibernateTemplate().execute(callback);
		if ( result instanceof NoResultsFoundException ){
			throw (NoResultsFoundException)result;
		}
		if ( result instanceof SubjectLockedException ){
			throw (SubjectLockedException)result;
		}
		return (org.psygrid.esl.model.dto.Subject)result;
	}

	public org.psygrid.esl.model.dto.Subject getSubjectEvenIfLocked(final org.psygrid.esl.model.dto.Project project, final String studyNumber) throws NoResultsFoundException {

		//retrieve the groups belonging to the specified project
		final List<Group> groups = getGroups(project);

		HibernateCallback callback = new HibernateCallback(){

			public Object doInHibernate(Session session){

				Subject subject = (Subject) session.createCriteria(Subject.class)
				.add(Restrictions.eq("studyNumber", studyNumber))
				.add(Restrictions.in("group", groups))
				.uniqueResult();

				if ( null == subject ){
					return new NoResultsFoundException("No Subject exists in the ESL for study number = "+studyNumber);
				}

				org.psygrid.esl.model.dto.Subject dtoSubject = null;
				if ( subject != null ){
					dtoSubject = subject.toDTO();
				}
				return dtoSubject;
			}
		};

		Object result = getHibernateTemplate().execute(callback);
		if ( result instanceof NoResultsFoundException ){
			throw (NoResultsFoundException)result;
		}
		return (org.psygrid.esl.model.dto.Subject)result;
	}
	
	public void deleteSubject(
			final String studyNumber) throws NoResultsFoundException {
		final Subject subject = getSubjectFromStudyNumber(studyNumber);
		deleteChanges(subject);
		
		HibernateCallback callback = new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				session.delete(subject);
				
				return null;
			}
		};
		
		getHibernateTemplate().execute(callback);
	}

	public String getStudyNumber(org.psygrid.esl.model.dto.Project project, org.psygrid.esl.model.dto.Subject exampleSubject, String[] restrictBy) throws DAOException {

		if (restrictBy == null || restrictBy.length == 0) {
			throw new DAOException("Must specify the allowed groups in which to find a subject"); 
		}

		List<String> g = new ArrayList<String>();
		for (String s: restrictBy) {
			g.add(s);
		}
		final List<String> groups = g;

		org.psygrid.esl.model.hibernate.Subject hSubject = exampleSubject.toHibernate();
		@SuppressWarnings("unchecked")
		List<org.psygrid.esl.model.hibernate.Subject> examples = getHibernateTemplate().findByExample(hSubject);

		if ( examples == null || examples.size() == 0){
			throw new NoResultsFoundException("No Subjects exist with those criteria");
		}

		final List<Long> exampleId = new ArrayList<Long>();
		for (Subject s: examples) {
			exampleId.add(s.getId());
		}

		/*
		 * Narrow down the results to just those belonging to the groups in the specified project. 
		 */
		@SuppressWarnings("unchecked")
		HibernateCallback callback = new HibernateCallback(){

			public Object doInHibernate(Session session){	

				List<org.psygrid.esl.model.hibernate.Subject> examples = session.createCriteria(Subject.class)
				.add(Restrictions.in("id", exampleId))
				.createCriteria("group")
				.add(Restrictions.in("groupCode", groups))
				.list();

				return examples;
			}
		};
		@SuppressWarnings("unchecked")
		List<org.psygrid.esl.model.hibernate.Subject> dtoSubject = (List<org.psygrid.esl.model.hibernate.Subject>)getHibernateTemplate().execute(callback);

		if ( null == dtoSubject ){
			throw new NoResultsFoundException("No such subject exists");
		}
		if (dtoSubject.size() > 1) {
			throw new TooManyResultsException("More than one subject meets the criteria");
		}
		if (dtoSubject.size() == 0) {
			throw new NoResultsFoundException("No subject exists with the specified criteria");
		}

		return dtoSubject.get(0).getStudyNumber();

	}


	public org.psygrid.esl.model.dto.Subject[] findSubjectByExample(org.psygrid.esl.model.dto.Project project, org.psygrid.esl.model.dto.Subject exampleSubject, String[] restrictBy) throws DAOException {

		if (restrictBy == null || restrictBy.length == 0) {
			throw new DAOException("Must specify the allowed groups in which to find a subject"); 
		}

		//create a List of allowed group codes from the array passed in 
		List<String> g = new ArrayList<String>();
		for (String s: restrictBy) {
			g.add(s);
		}

		final List<String> groupCodes = g;
		final IProject hProject = project.toHibernate();

		if (groupCodes == null) {
			throw new DAOException("Must specify the allowed groups in which to find a subject"); 
		}

		//narrow down the results to just the relevent groups for the given project.
		HibernateCallback callback2 = new HibernateCallback(){
			@SuppressWarnings("unchecked")
			public Object doInHibernate(Session session){	

				List<org.psygrid.esl.model.hibernate.Group> grps = session.createCriteria(Group.class)
				.add(Restrictions.in("groupCode", groupCodes))
				.add(Restrictions.eq("project", hProject))
				.list();

				return grps;
			}
		};
		@SuppressWarnings("unchecked")
		List<org.psygrid.esl.model.hibernate.Group> newgroups = (List<org.psygrid.esl.model.hibernate.Group>)getHibernateTemplate().execute(callback2);

		if (newgroups.size() == 0) {
			throw new DAOException("No groups found that match list of allowed groups for user"); 
		}

		boolean found = false;
		//if the group has been specified as a search criteria, replace the list of allowed groups
		//with the specified group (assuming it's also in the list of allowed groups
		if (exampleSubject.getGroup() != null) {
			for (org.psygrid.esl.model.hibernate.Group grp: newgroups) {
				//can compare group codes because they belong to the same subject, so will be unique
				if (grp.getGroupCode().equals(exampleSubject.getGroup().getCode())) {
					newgroups = new ArrayList<org.psygrid.esl.model.hibernate.Group>();
					newgroups.add(grp);
					found = true;
					break;
				}
			}
			if (!found) {
				//if we reach this point the group hasn't been found in the list of allowed groups, so throw an error
				throw new DAOException("Group Not Allowed: The user must have access to the Group when specifying a Group to search for Subject by example");
			}
		}

		final List<org.psygrid.esl.model.hibernate.Group> groups = newgroups;

		//Construct Example objects using the contents of the exampleSubject 
		//and its address. This will then be used as search criteria.
		exampleSubject.setLocked(false);
		Example example = Example.create(exampleSubject.toHibernate());
		//example.enableLike(MatchMode.ANYWHERE); // enable partial matching
		example.ignoreCase();
		example.excludeProperty("version");
		example.excludeProperty("log");
		example.enableLike();					  // enable matching using '%' as a wildcard

		Example example2 = null;
		if (exampleSubject.getAddress() != null) {
			example2 = Example.create(exampleSubject.getAddress().toHibernate());
			example2.ignoreCase();
			example2.excludeProperty("version");
			example2.excludeProperty("log");
			example2.enableLike();

		}
		else {
			example2 = null;
		}

		final Example e1 = example;
		final Example e2 = example2; 

		/*
		 * Search for subjects based on the criteria above
		 */
		HibernateCallback callback = new HibernateCallback(){
			@SuppressWarnings("unchecked")
			public Object doInHibernate(Session session){	

				List<org.psygrid.esl.model.hibernate.Subject> examples;

				if (e2 != null) {
					examples = session.createCriteria(Subject.class)
					.add(e1)
					.add(Restrictions.in("group", groups))
					.createCriteria("address")
					.add(e2)
					.list();
				}
				else {
					//ignore the address as no address object was specified in the exampleSubject
					examples = session.createCriteria(Subject.class)
					.add(e1)
					.add(Restrictions.in("group", groups))
					.list();
				}
				return examples;
			}
		};
		@SuppressWarnings("unchecked")
		final List<org.psygrid.esl.model.hibernate.Subject> subjects = (List<org.psygrid.esl.model.hibernate.Subject>)getHibernateTemplate().execute(callback);

		org.psygrid.esl.model.dto.Subject[] dtoSubjects = new org.psygrid.esl.model.dto.Subject[subjects.size()];
		int i = 0;
		for (Subject s: subjects) {
			dtoSubjects[i] = s.toDTO();
			i++;
		}

		return dtoSubjects;

	}



	public boolean exists(org.psygrid.esl.model.dto.Project project, final String studyNumber) throws DAOException {

		final List<Group> group = getGroups(project);

		HibernateCallback callback = new HibernateCallback(){

			public Object doInHibernate(Session session){

				Subject subject = (Subject) session.createCriteria(Subject.class)
				.add(Restrictions.like("studyNumber", studyNumber))
				.add(Restrictions.in("group", group))
				.uniqueResult();

				org.psygrid.esl.model.dto.Subject dtoSubject = null;
				if ( subject != null ){
					dtoSubject = subject.toDTO();
				}
				return dtoSubject;
			}
		};

		org.psygrid.esl.model.dto.Subject subject = (org.psygrid.esl.model.dto.Subject)getHibernateTemplate().execute(callback);
		if ( null == subject ){
			return false;
		}
		return true;
	}

	public Long saveSubject(final org.psygrid.esl.model.ISubject subject, String username)
	throws DAOException, ObjectOutOfDateException, DuplicateObjectException, SubjectExistsException {

		Subject storedSubject = null;

		final org.psygrid.esl.model.hibernate.Subject hSubject = (org.psygrid.esl.model.hibernate.Subject)subject;
		if ( null != hSubject.getId() ) {
			//for existing Projects, check that it has not been deleted
			storedSubject = (Subject)getHibernateTemplate().get(Subject.class, hSubject.getId());
			if (null == storedSubject){
				//project has been deleted by another session
				throw new ObjectOutOfDateException("Cannot save Subject - the object is out-of-date");
			}

			//when transactions are used the same session is used
			//throughout the transaction. Need to evict the "stored"
			//subject otherwise an exception will be thrown when
			//trying to saveOrUpdate the subject due to the existence
			//of an object with the same ID already being in the session
			getHibernateTemplate().evict(storedSubject);

		}

		if (hSubject.getGroup() == null) {
			throw new DAOException("A group needs to be specified for the subject "+hSubject.getStudyNumber());
		}


		boolean edited = false;

		//Assume that if a subject has a hibernate assigned identifier then
		//it's been saved previously and is now being updated. This will
		//cause an email notification to be sent to the relevent people.
		if ( null != hSubject.getId() ) {
			edited = true;
		}


		/*
		 * Check that the subject hasn't been saved previously by comparing 'unique' data.
		 * 
		 * Do this for new and edited subjects, so that the user can't bypass the checks by entering
		 * minimal data at creation time and more detailed data later.
		 */
		final String studyNumber = hSubject.getStudyNumber();

		//TODO Warn if the hospital number is not unique (hospital number is local to a hospital and so is not guaranteed unique)

		//Check the NHS Number is unique
		final String nhsNumber = hSubject.getNhsNumber();
		if (nhsNumber != null && !nhsNumber.equals("")) {
			HibernateCallback callback = new HibernateCallback(){
				public Object doInHibernate(Session session){
					Group group = (Group)session.get(Group.class, hSubject.getGroup().getId());
					Project project = (Project)session.get(Project.class, group.getProject().getId());
					List<Group> groupList = getGroups(project);
					
					Subject subject = (Subject) session.createCriteria(Subject.class)
					.add(Restrictions.like("nhsNumber", nhsNumber))
					.add(Restrictions.in("group", groupList))
					.add(Restrictions.not(Restrictions.eq("studyNumber", studyNumber)))	//Ignore the subject being saved
					.uniqueResult();

					if ( subject != null ){
						String existingSubject = subject.getStudyNumber();
						return new SubjectExistsException("The NHS Number "+nhsNumber+" already exists under the participant "+existingSubject+". Please see your clinical project manager.");
					}
					return null;
				}
			};
			Object object = getHibernateTemplate().execute(callback);
			if (object != null) {
				throw (SubjectExistsException)object;
			}
		}

		//Check the first name, last name and date of birth together are unique.
		final String firstName = hSubject.getFirstName();
		final String lastName = hSubject.getLastName();
		final Date dob = hSubject.getDateOfBirth();
		if (firstName != null && !firstName.equals("")
				&& lastName != null && !lastName.equals("")
				&& dob != null) {
			HibernateCallback callback = new HibernateCallback(){
				public Object doInHibernate(Session session){
					Group group = (Group)session.get(Group.class, hSubject.getGroup().getId());
					Project project = (Project)session.get(Project.class, group.getProject().getId());
					List<Group> groupList = getGroups(project);
					
					Subject subject = (Subject) session.createCriteria(Subject.class)
					.add(Restrictions.ilike("firstName", firstName))
					.add(Restrictions.ilike("lastName", lastName))
					.add(Restrictions.eq("dateOfBirth", dob))
					.add(Restrictions.in("group", groupList))
					.add(Restrictions.not(Restrictions.eq("studyNumber", studyNumber)))
					.uniqueResult();

					if ( subject != null ){
						String existingSubject = subject.getStudyNumber();
						return new SubjectExistsException("This participant already exists as "+existingSubject+". Please see your clinical project manager.");
					}
					return null;
				}
			};
			Object object = getHibernateTemplate().execute(callback);
			if (object != null) {
				throw (SubjectExistsException)object;
			}
		}

		//Check that a person's mobile phone number is unique
		final String mobile = hSubject.getMobilePhone();
		if (mobile != null && !mobile.equals("")) {
			HibernateCallback callback = new HibernateCallback(){
				public Object doInHibernate(Session session){
					Group group = (Group)session.get(Group.class, hSubject.getGroup().getId());
					Project project = (Project)session.get(Project.class, group.getProject().getId());
					List<Group> groupList = getGroups(project);
					
					Subject subject = (Subject) session.createCriteria(Subject.class)
					.add(Restrictions.ilike("mobilePhone", mobile))
					.add(Restrictions.in("group", groupList))
					.add(Restrictions.not(Restrictions.eq("studyNumber", studyNumber)))
					.uniqueResult();

					if ( subject != null ){
						String existingSubject = subject.getStudyNumber();
						return new SubjectExistsException("The mobile phone number for the participant has already been entered for "+existingSubject+". Please see your clinical project manager.");
					}
					return null;
				}
			};
			Object object = getHibernateTemplate().execute(callback);
			if (object != null) {
				throw (SubjectExistsException)object;
			}
		}



		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){

				Group group = (Group)session.get(Group.class, hSubject.getGroup().getId());

				if ( group == null ){
					return new DAOException("No group exists with the given id");
				}
				
				hSubject.setGroup(group);

				session.saveOrUpdate(hSubject);	
				
				return group.getId();
			}

		};

		try{
			//this is used to track who created or edited the objects in question
			//(objects must extend Auditable for this to have an effect)
			EntityInterceptor.setUserName(username);

			Long groupId = null;

			//save the subject and add it to the relevent group
			groupId = (Long)getHibernateTemplate().execute(callback);

			if (groupId == null) {
				throw new DAOException("Subject's group was not updated correctly.");
			}

			if (edited) {
				//If a subject has been updated (and not saved for the first time) then send out
				//an email notification
				emailChanges(subject, getHibernateTemplate().getSessionFactory().getCurrentSession());
			}

			return subject.getId();
		}
		catch (DataIntegrityViolationException e) {
			throw new DuplicateObjectException("Duplicate entry error when saving subject "+hSubject.getStudyNumber()+" already exists.");
		}
		catch (HibernateOptimisticLockingFailureException ex){
			//Note that this catch block will NEVER be entered if
			//this method is called using Spring declarative transaction
			//management. In that case, the exception is caught in
			//SaveObjectInterceptor
			throw new ObjectOutOfDateException("Cannot save Subject - the object is out-of-date",ex);
		}
	}

	public String[][] findNhsNumbers(String projectCode, final String[] studyCodes)
	throws DAOException {

		if (studyCodes == null || studyCodes.length == 0) {
			throw new DAOException("No studyCodes were provided");
		}

		@SuppressWarnings("unchecked")
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){
				List<Subject> subjects = (List<Subject>) session.createCriteria(Subject.class)
				.add(Restrictions.in("studyNumber", studyCodes))
				.list();

				return subjects;
			}
		};

		Object results = getHibernateTemplate().execute(callback);
		if ( null == results ){
			throw new NoResultsFoundException("No Subjects exist in the ESL for study numbers provided: "+studyCodes);
		}
		if (results instanceof DAOException) {
			throw (DAOException)results;
		}

		@SuppressWarnings("unchecked")
		List<Subject> subjects = (List<Subject>)results;
		String[][] codeAndNumber = new String[subjects.size()][2];
		for (int i = 0; i < subjects.size(); i++) {
			codeAndNumber[i][0] = subjects.get(i).getStudyNumber();
			codeAndNumber[i][1] = subjects.get(i).getNhsNumber();
		}

		return codeAndNumber;
	}

	public String getValueOfProperty(final String identifier, final String property) throws DAOException {

		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){
				Subject subject = (Subject) session.createCriteria(Subject.class)
				.add(Restrictions.eq("studyNumber", identifier))
				.uniqueResult();

				if ( null == subject ){
					return null;
				}

				Object obj = null;
				Class type = null;
				try{
					try{
						obj = PropertyUtils.getProperty(subject, property);
						type = PropertyUtils.getPropertyType(subject, property);
					}
					catch (NoSuchMethodException ex){
						//field not found in the Subject, check in the Subject's Address
						try{
							obj = PropertyUtils.getProperty(subject.getAddress(), property);
							type = PropertyUtils.getPropertyType(subject.getAddress(), property);
						}
						catch (NoSuchMethodException ex2){
							//last chance - look in the group?
							try{
								obj = PropertyUtils.getProperty(subject.getGroup(), property);
								type = PropertyUtils.getPropertyType(subject.getGroup(), property);
							}
							catch (NoSuchMethodException ex3){
								//property does not exist - this is a design-time problem so
								//throw an exception
								return new DAOException("No property exists with the name '"+property+"'");
							}
						}
					}
				}
				catch(IllegalAccessException ex){
					return new DAOException("Unable to access property with the name '"+property+"'", ex);
				}
				catch(InvocationTargetException ex){
					return new DAOException("InvocationTargetException when accessing property with the name '"+property+"'", ex);
				}

				String value = null;
				if ( type.equals(Date.class)){
					Date date = (Date)obj;
					value = ddMmmYyyyFormatter.format(date);
				}
				else if ( type.equals(String.class)){
					value = (String)obj;
				}
				else{
					return new DAOException("Unhandled type ("+type.getName()+") for property with the name '"+property+"'");
				}

				return value;
			}
		};

		Object result = getHibernateTemplate().execute(callback);
		if ( result instanceof DAOException ){
			throw (DAOException)result;
		}
		return (String)result;

	}

	/** 
	 * Match subjects to the project provided by finding all 
	 * groups in the given project
	 */
	private List<Group> getGroups(org.psygrid.esl.model.dto.Project project) {

		final org.psygrid.esl.model.hibernate.Project hProject = project.toHibernate();
		@SuppressWarnings("unchecked")
		HibernateCallback callback = new HibernateCallback(){

			public Object doInHibernate(Session session){
				List<Group> group = session.createCriteria(Group.class)
				.add(Restrictions.eq("project", hProject)).list();
				return group;
			}
		};
		@SuppressWarnings("unchecked")
		List<Group> group = (List<Group>)getHibernateTemplate().execute(callback);

		return group;
	}

	/**
	 * Email any changes made to Subjects to the people involved 
	 * in their treatment.
	 * 
	 * @param object
	 * @param changes
	 * @param session
	 */
	private void emailChanges(ISubject subject, Session session) {

		if (subject != null) {
			QueuedEmail email = new QueuedEmail();

			email.setSubject("PSYGRID: The Electronic Screening Log has been updated for "+subject.getStudyNumber());

			StringBuilder body = new StringBuilder();
			body.append("The subject ");
			body.append(subject.getStudyNumber());
			body.append(" has had their details updated in the Electronic Screening Log.");
			body.append("\n\n");
			body.append("For further information please go to https://smtp.psygrid.nhs.uk/psygrid");

			email.setBody(body.toString());

			List<String> addresses = null;
			try {
				addresses = emailUtil.getEmailRecipients(subject, rolesToEmail);	
				email.setBccAddresses(addresses);
			}
			catch(NotAuthorisedFaultMessage ex){
				sLog.info("Not authorised to connect to attribute authority query client.", ex);
			}
			catch (ConnectException ex){
				sLog.info("Unable to connect to attribute authority query client.", ex);
			}

			email.setToAddress(emailToAddress);
			email.setFromAddress(emailFromAddress);

			//save email
			session.save(email);

			sLog.info("Email queued for "+email.getToAddress()+"and bcc'd to "+addresses);
		}
	}
	

	// From StrataDAO
	
	public org.psygrid.esl.model.dto.Strata getStrata(final Long strataId) throws DAOException {
		
		HibernateCallback callback = new HibernateCallback(){

			public Object doInHibernate(Session session){
				Strata strata = (Strata)session.createCriteria(Strata.class)
				.add(Restrictions.idEq(strataId))
				.uniqueResult();

				org.psygrid.esl.model.dto.Strata dtoStrata = null;
				if ( strata != null ){
					dtoStrata = strata.toDTO();
				}
				return dtoStrata;
			}
		};

		org.psygrid.esl.model.dto.Strata strata = (org.psygrid.esl.model.dto.Strata)getHibernateTemplate().execute(callback);
		if ( null == strata ){
			throw new DAOException("No Strata exists in the repository for id = "+strataId);
		}
		return strata;
	}

	public org.psygrid.esl.model.dto.Strata[] getAllStrata(final Long randomId)
	throws DAOException {
		
		org.psygrid.esl.model.dto.Strata[] strata = null;
		HibernateCallback callback = new HibernateCallback(){

			public Object doInHibernate(Session session){
				List s = session.createQuery(
						"from Strata s where s.randomisation.id=?")
						.setLong(0, randomId)
						.list();

				org.psygrid.esl.model.dto.Strata[] dtoStrata = new org.psygrid.esl.model.dto.Strata[s.size()];
				
				for (int i = 0; i < s.size(); i++) {
					if ( s != null ){
						dtoStrata[i] = ((org.psygrid.esl.model.hibernate.Strata)s.get(i)).toDTO();
					}
				}
				
				return dtoStrata;
			}
		};

		strata = (org.psygrid.esl.model.dto.Strata[])getHibernateTemplate().execute(callback);
		if ( null == strata ){
			throw new DAOException("No Strata exists in the repository for randomisation (id: "+randomId+")");
		}
		return strata;
	}
	
	public Long saveStrata(org.psygrid.esl.model.dto.Strata dtoStrata) throws DAOException,
			ObjectOutOfDateException {
		
		Strata strata = dtoStrata.toHibernate();
		if ( null != strata.getId() ){
			
			Strata storedStrata = (Strata)getHibernateTemplate().get(Strata.class, strata.getId());
			if (null == storedStrata){
				//randomisation has been deleted by another session
				throw new ObjectOutOfDateException("Cannot save Strata - the object is out-of-date");
			}

			//when transactions are used the same session is used
			//throughout the transaction. Need to evict the "stored"
			//object otherwise an exception will be thrown
			//when trying to saveOrUpdate due to the existence
			//of an object with the same ID already being in the session
			getHibernateTemplate().evict(storedStrata);
		}
		try{
			getHibernateTemplate().saveOrUpdate(strata);

			return strata.getId();
		}
		catch (HibernateOptimisticLockingFailureException ex){
			//Note that this catch block will NEVER be entered if
			//this method is called using Spring declarative transaction
			//management. In that case, the exception is caught in
			//SaveObjectInterceptor
			throw new ObjectOutOfDateException("Cannot save Strata - the object is out-of-date",ex);
		}

	}
		
		
	// From RoleDAO

	public org.psygrid.esl.model.dto.Role getRole(final Long roleId) throws DAOException {
		
		HibernateCallback callback = new HibernateCallback(){

			public Object doInHibernate(Session session){
				Role role = (Role)session.createCriteria(Role.class)
				.add(Restrictions.idEq(roleId))
				.uniqueResult();

				org.psygrid.esl.model.dto.Role dtoRole = null;
				if ( role != null ){
					dtoRole = role.toDTO();
				}
				return dtoRole;
			}
		};

		org.psygrid.esl.model.dto.Role role = (org.psygrid.esl.model.dto.Role)getHibernateTemplate().execute(callback);
		if ( null == role ){
			throw new DAOException("No Role exists in the repository for id = "+roleId);
		}
		return role;
	}

	
	public org.psygrid.esl.model.dto.Role[] getAllRoles(final Long randomId)
	throws DAOException {
		
		org.psygrid.esl.model.dto.Role[] roles = null;
		HibernateCallback callback = new HibernateCallback(){

			public Object doInHibernate(Session session){
				List role = session.createQuery(
						"from Role r where r.randomisation_id=?")
						.setLong(0, randomId)
						.list();

				org.psygrid.esl.model.dto.Role[] dtoRoles = new org.psygrid.esl.model.dto.Role[role.size()];
				
				for (int i = 0; i < role.size(); i++) {
					if ( role != null ){
						dtoRoles[i] = ((org.psygrid.esl.model.hibernate.Role)role.get(i)).toDTO();
					}
				}
				
				return dtoRoles;
			}
		};

		roles = (org.psygrid.esl.model.dto.Role[])getHibernateTemplate().execute(callback);
		if ( null == roles ){
			throw new DAOException("No Roles exist for randomisation (id: "+randomId+")");
		}
		return roles;
	}
	
	public org.psygrid.esl.model.dto.Role[] getNotifyOfDecisionRoles(final Long randomId)
	throws DAOException {
		
		org.psygrid.esl.model.dto.Role[] roles = null;
		HibernateCallback callback = new HibernateCallback(){

			public Object doInHibernate(Session session){
				List role = session.createQuery(
						"from Role r where r.randomisation_id=?" +
						"and r.notifyOfRSDecision=true")
						.setLong(0, randomId)
						.list();

				org.psygrid.esl.model.dto.Role[] dtoRoles = new org.psygrid.esl.model.dto.Role[role.size()];
				
				for (int i = 0; i < role.size(); i++) {
					if ( role != null ){
						dtoRoles[i] = ((org.psygrid.esl.model.hibernate.Role)role.get(i)).toDTO();
					}
				}
				
				return dtoRoles;
			}
		};

		roles = (org.psygrid.esl.model.dto.Role[])getHibernateTemplate().execute(callback);
		if ( null == roles ){
			throw new DAOException("No such Role exists for randomisation (id: "+randomId+")");
		}
		return roles;
	}
	
	public org.psygrid.esl.model.dto.Role[] getNotifyOfInvocationRoles(final Long randomId)
	throws DAOException {
		
		org.psygrid.esl.model.dto.Role[] roles = null;
		HibernateCallback callback = new HibernateCallback(){

			public Object doInHibernate(Session session){
				List role = session.createQuery(
						"from Role r where r.randomisation_id=?" +
						"and r.notifyOfRSInvocation=true")
						.setLong(0, randomId)
						.list();

				org.psygrid.esl.model.dto.Role[] dtoRoles = new org.psygrid.esl.model.dto.Role[role.size()];
				
				for (int i = 0; i < role.size(); i++) {
					if ( role != null ){
						dtoRoles[i] = ((org.psygrid.esl.model.hibernate.Role)role.get(i)).toDTO();
					}
				}
				
				return dtoRoles;
			}
		};

		roles = (org.psygrid.esl.model.dto.Role[])getHibernateTemplate().execute(callback);
		if ( null == roles ){
			throw new DAOException("No such Role exists for the randomisation (id = "+randomId+")");
		}
		return roles;
	}
	
	public org.psygrid.esl.model.dto.Role[] getNotifyOfTreatmentRoles(final Long randomId)
	throws DAOException {
		
		org.psygrid.esl.model.dto.Role[] roles = null;
		HibernateCallback callback = new HibernateCallback(){

			public Object doInHibernate(Session session){
				List role = session.createQuery(
						"from Role r where r.randomisation_id=?" +
						"and r.notifyOfRSTreatment=true")
						.setLong(0, randomId)
						.list();

				org.psygrid.esl.model.dto.Role[] dtoRoles = new org.psygrid.esl.model.dto.Role[role.size()];
				
				for (int i = 0; i < role.size(); i++) {
					if ( role != null ){
						dtoRoles[i] = ((org.psygrid.esl.model.hibernate.Role)role.get(i)).toDTO();
					}
				}
				
				return dtoRoles;
			}
		};

		roles = (org.psygrid.esl.model.dto.Role[])getHibernateTemplate().execute(callback);
		if ( null == roles ){
			throw new DAOException("No such Role exists for the randomisation (id = "+randomId+")");
		}
		return roles;
	}
	
	public Long saveRole(org.psygrid.esl.model.dto.Role dtoRole) throws DAOException,
			ObjectOutOfDateException {
		
		Role role = dtoRole.toHibernate();
		if ( null != role.getId() ){
			
			Role storedRole = (Role)getHibernateTemplate().get(Role.class, role.getId());
			if (null == storedRole){
				//object has been deleted by another session
				throw new ObjectOutOfDateException("Cannot save Role - the object is out-of-date");
			}

			//when transactions are used the same session is used
			//throughout the transaction. Need to evict the "stored"
			//role object otherwise an exception will be thrown
			//when trying to saveOrUpdate the role due to the existence
			//of an object with the same ID already being in the session
			getHibernateTemplate().evict(storedRole);
		}
		try{
			getHibernateTemplate().saveOrUpdate(role);

			return role.getId();
		}
		catch (HibernateOptimisticLockingFailureException ex){
			//Note that this catch block will NEVER be entered if
			//this method is called using Spring declarative transaction
			//management. In that case, the exception is caught in
			//SaveObjectInterceptor
			throw new ObjectOutOfDateException("Cannot save Role - the object is out-of-date",ex);
		}

	}
	
	// From AuditableDAO

	/**
	 * Retrieve the full history of changes made to an auditable object.
	 * 
	 * @param auditable
	 * @return org.psygrid.esl.model.dto.ProvenanceLog
	 * @throws DAOException
	 */
	public org.psygrid.esl.model.dto.ProvenanceLog getHistory(final IAuditable auditable) throws DAOException {

		HibernateCallback callback = new HibernateCallback(){

			public Object doInHibernate(Session session){
				ProvenanceLog log = (ProvenanceLog)session.createCriteria(ProvenanceLog.class)
				.add(Restrictions.idEq(auditable.getLog().getId()))
				.uniqueResult();

				org.psygrid.esl.model.dto.ProvenanceLog dtoLog = null;
				if ( log != null ){
					dtoLog = log.toDTO();
				}
				return dtoLog;
			}
		};

		org.psygrid.esl.model.dto.ProvenanceLog log = (org.psygrid.esl.model.dto.ProvenanceLog)getHibernateTemplate().execute(callback);
		if ( log == null ){
			throw new DAOException("No ProvenanceLog exists in the repository for the object "+auditable);
		}
		return log;
	}

	/**
	 * Retrieve the changes made to an auditable object on the specified date.
	 * 
	 * @param auditable
	 * @param date
	 * @return org.psygrid.esl.model.dto.ProvenanceChange[]
	 * @throws DAOException
	 */
	public org.psygrid.esl.model.dto.Change[] getChanges(final IAuditable auditable, final Date date) throws DAOException {



		//Retrieve the provenanceChange that occurred at the given time
		HibernateCallback callback = new HibernateCallback(){

			public Object doInHibernate(Session session){
				ProvenanceChange change = (ProvenanceChange)session.createCriteria(ProvenanceChange.class)
				.add(Restrictions.eq("provenanceLog", auditable.getLog()))
				.add(Restrictions.eq("timestamp", date))		//need partial match?
				.uniqueResult();

				org.psygrid.esl.model.dto.ProvenanceChange dtoChange = new org.psygrid.esl.model.dto.ProvenanceChange();
				dtoChange = change.toDTO();
				return dtoChange;
			}
		};

		final org.psygrid.esl.model.dto.ProvenanceChange provenanceChange = (org.psygrid.esl.model.dto.ProvenanceChange)getHibernateTemplate().execute(callback);
		if ( provenanceChange == null ){
			throw new DAOException("No Change exists in the repository for the object "+auditable+" on the date of "+date.toString());
		}
		final org.psygrid.esl.model.hibernate.ProvenanceChange pChange = provenanceChange.toHibernate();
		//Use the provenanceChange (which records an update instance) to get a
		//list of the changes that were made.
		HibernateCallback callback2 = new HibernateCallback(){

			@SuppressWarnings("unchecked")
			public Object doInHibernate(Session session){
				List<Change> change = session.createCriteria(Change.class)
				.add(Restrictions.eq("provenance", pChange))
				.list();

				org.psygrid.esl.model.dto.Change[] dtoChange = new org.psygrid.esl.model.dto.Change[change.size()];
				if ( change != null ){
					for(int i=0; i < change.size(); i++) {
						dtoChange[i] = change.get(i).toDTO();
					}
				}
				return dtoChange;
			}
		};

		org.psygrid.esl.model.dto.Change[] change = (org.psygrid.esl.model.dto.Change[])getHibernateTemplate().execute(callback2);
		if ( change == null ){
			throw new DAOException("No Change exists in the repository for the object "+auditable+" on the date of "+date.toString());
		}
		return change;
	}


	/**
	 * Retrieve the change made to a given field for an auditable object on the specified date.
	 * 
	 * @param auditable
	 * @param date
	 * @param field
	 * @return org.psygrid.esl.model.dto.Change
	 * @throws DAOException
	 */
	public org.psygrid.esl.model.dto.Change getChange(final IAuditable auditable, final Date date, final String field) throws DAOException {

		HibernateCallback callback = new HibernateCallback(){

			public Object doInHibernate(Session session){
				ProvenanceChange change = (ProvenanceChange)session.createCriteria(ProvenanceChange.class)
				.add(Restrictions.eq("provenanceLog", auditable.getLog()))
				.add(Restrictions.eq("timestamp", date))		//need partial match
				.uniqueResult();

				org.psygrid.esl.model.dto.ProvenanceChange dtoChange = null;
				if ( change != null ){
					dtoChange = change.toDTO();
				}
				return dtoChange;
			}
		};

		final org.psygrid.esl.model.dto.ProvenanceChange provenanceChange = (org.psygrid.esl.model.dto.ProvenanceChange)getHibernateTemplate().execute(callback);

		if ( provenanceChange == null ){
			throw new DAOException("No Change exists in the repository for the object "+auditable+" on the date of "+date.toString());
		}

		final org.psygrid.esl.model.hibernate.ProvenanceChange pChange = provenanceChange.toHibernate();
		//Use the provenanceChange (which records an update instance) to get the
		//specified change.
		HibernateCallback callback2 = new HibernateCallback(){

			public Object doInHibernate(Session session){
				Change changeInstance = (Change)session.createCriteria(Change.class)
				.add(Restrictions.eq("provenance", pChange))
				.add(Restrictions.like("field", field))
				.uniqueResult();

				org.psygrid.esl.model.dto.Change dtoChangeInst = null;
				if ( changeInstance != null ){
					dtoChangeInst = changeInstance.toDTO();
				}
				return dtoChangeInst;
			}
		};

		final org.psygrid.esl.model.dto.Change changeInstance = (org.psygrid.esl.model.dto.Change)getHibernateTemplate().execute(callback2);

		if ( changeInstance == null){
			throw new DAOException("No Change exists in the repository for the object "+auditable+" on the date of "+date.toString()+" matching the field "+ field);
		}
		
		return changeInstance;
	}
	
	// From RandomisationDAO

	public org.psygrid.esl.model.dto.Randomisation getRandomisation(final Long randomisationId) throws DAOException {

		HibernateCallback callback = new HibernateCallback(){

			public Object doInHibernate(Session session){
				Randomisation randomisation = (Randomisation)session.createCriteria(Randomisation.class)
				.add(Restrictions.idEq(randomisationId))
				.uniqueResult();

				org.psygrid.esl.model.dto.Randomisation dtoRand = null;
				if ( randomisation != null ){
					dtoRand = randomisation.toDTO();
				}
				return dtoRand;
			}
		};

		org.psygrid.esl.model.dto.Randomisation randomisation = (org.psygrid.esl.model.dto.Randomisation)getHibernateTemplate().execute(callback);
		if ( null == randomisation ){
			throw new DAOException("No Randomisation exists in the repository for id = "+randomisationId);
		}
		return randomisation;
	}

	/**
	 * Retrieve randomisation emails waiting to be sent (to provide notification and details of randomisations).
	 * 
	 * @return randomisationEmails
	 */
	public List<org.psygrid.common.email.QueuedEmail> getQueuedEmails() {

		HibernateCallback callback = new HibernateCallback(){

			public Object doInHibernate(Session session){
				List emails = session.createCriteria(QueuedEmail.class)
				.list();
				return emails;
			}
		};

		List<org.psygrid.common.email.QueuedEmail> emails = (List<org.psygrid.common.email.QueuedEmail>)getHibernateTemplate().execute(callback);

		return emails;
	}

	/**
	 * Remove a Randomisation email from the queue.
	 * 
	 * @param email to remove
	 */
	public void removeQueuedEmail(org.psygrid.common.email.QueuedEmail email) {
		
		if (email != null) {
			getHibernateTemplate().delete(email);
		}	
	}

	/**
	 * Persist a Randomisation email. This will be 'queued' and sent later.
	 * 
	 * @param email
	 * @return email id
	 */
	public Long saveEmail(final QueuedEmail email) {

		getHibernateTemplate().saveOrUpdate(email);

		return email.getId();
	}
	
	/**
	 * Retrieve randomisation SMS messages waiting to be sent (to provide notification and details of randomisations).
	 * 
	 * @return randomisationSMSs
	 */
	public List<org.psygrid.esl.scheduling.hibernate.QueuedSMS> getQueuedSMSs() throws DAOException {

		HibernateCallback callback = new HibernateCallback(){

			public Object doInHibernate(Session session){
				List emails = session.createCriteria(QueuedSMS.class)
				.list();
				return emails;
			}
		};

		List<org.psygrid.esl.scheduling.hibernate.QueuedSMS> messages = (List<org.psygrid.esl.scheduling.hibernate.QueuedSMS>)getHibernateTemplate().execute(callback);

		return messages;
	}

	/**
	 * Remove a Randomisation SMS message from the queue.
	 * 
	 * @param sms to remove
	 */
	public void removeQueuedSMS(org.psygrid.esl.scheduling.hibernate.QueuedSMS sms) throws DAOException {
		
		if (sms != null) {
			getHibernateTemplate().delete(sms);
		}	
	}
	
	/**
	 * Persist a SMS message for notification of randomisation. This will be 'queued' 
	 * and sent later.
	 * 
	 * @param sms message
	 * @return sms id
	 */
	public Long saveRandomisationSMS(final QueuedSMS sms) throws DAOException {

		getHibernateTemplate().saveOrUpdate(sms);

		return sms.getId();
	}
	
	private Subject getSubjectFromStudyNumber(final String studyNumber) throws NoResultsFoundException {
		HibernateCallback callback = new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Subject subject = (Subject) session.createCriteria(Subject.class)
					.add(Restrictions.eq("studyNumber", studyNumber))
					.uniqueResult();
				
				if(subject == null) {
					return new NoResultsFoundException("Attempting to delete subject but no subject exists in the ESL for study number = "+studyNumber);
				}
				
				return subject;
			}
		};
		
		Object result = getHibernateTemplate().execute(callback);
		if ( result instanceof NoResultsFoundException ){
			throw (NoResultsFoundException)result;
		}
		
		return (Subject)result;
	}
	
	private void deleteChanges(final IAuditable auditable) {
		//Retrieve the provenanceChange that occurred at the given time
		HibernateCallback callback = new HibernateCallback(){

			@SuppressWarnings("unchecked")
			public Object doInHibernate(Session session){
				List<ProvenanceChange> changes = (List<ProvenanceChange>)session.createCriteria(ProvenanceChange.class)
				.add(Restrictions.eq("provenanceLog", auditable.getLog()))
				.list();

				return changes;
			}
		};
		
		@SuppressWarnings("unchecked")
		List<ProvenanceChange> changes = (List<ProvenanceChange>)getHibernateTemplate().execute(callback);
		deleteChanges(changes);
	}
	
	/**
	 * Delete the changes associated with the list of provenance changes
	 * @param provenanceChanges List of provenance changes
	 */
	private void deleteChanges(final List<ProvenanceChange> provenanceChanges) {
		HibernateCallback callback = new HibernateCallback(){

			@SuppressWarnings("unchecked")
			public Object doInHibernate(Session session){
				for(ProvenanceChange provenanceChange : provenanceChanges) {
					List<Change> listOfChanges = session.createCriteria(Change.class)
					.add(Restrictions.eq("provenance", provenanceChange))
					.list();
					
					for(Change change : listOfChanges) {
						session.delete(change);
					}
				}

				return null;
			}
		};
		
		getHibernateTemplate().execute(callback);
	}

	public void addedGroup(String projectCode, String code, String name) {
		List<?> results = getHibernateTemplate().find("from Project p where p.projectCode = ?", projectCode);
		Project project = (Project)DataAccessUtils.singleResult(results);
		if(project!=null){
			// We are using list semantics for the project->group relationship.
			// So we need to add the group to its project first.
			Group group = new Group(name,code);
			project.setGroup(group);
			group.setProject(project);
			getHibernateTemplate().save(group);
		}
	}

	public void updatedGroup(String projectCode, String groupCode, String newCode, String newName) {
		List<?> results = getHibernateTemplate().find("from Group g left join fetch g.project p where " +
				"p.projectCode = ? and g.groupCode = ?", new Object[]{projectCode,groupCode});
		Group group =  (Group)DataAccessUtils.singleResult(results);
		if(group!=null){
			group.setGroupCode(newCode);
			group.setGroupName(newName);
		}
	}

	public void deletedGroup(String projectCode, String groupCode) {
		List<?> results = getHibernateTemplate().find("from Group g left join fetch g.project p where " +
				"p.projectCode = ? and g.groupCode = ?", new Object[]{projectCode,groupCode});
		Group group = (Group)DataAccessUtils.singleResult(results);
		if(group!=null){
			// We are using list semantics for the project->group relationship.
			// So we need to remove the group from its project first.
			group.getProject().getGroups().remove(group);
			getHibernateTemplate().delete(group);
		}
	}
}

