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

package org.psygrid.esl.dao;

import java.util.Date;
import java.util.List;

import org.psygrid.esl.model.IAuditable;
import org.psygrid.esl.model.IPersistent;
import org.psygrid.esl.model.dto.Project;
import org.psygrid.esl.model.dto.Subject;
import org.psygrid.esl.model.hibernate.Group;
import org.psygrid.common.email.EmailDAO;

/**
 * DAO for the ESL
 * 
 * @author Lucy Bridges
 *
 */
public interface EslDAO extends EmailDAO {
	
	//From PersistentDAO
    /**
     * Retrieve a single Persistent object from the esl.
     * 
     * @param persistentId Unique identifier of the object to retrieve
     * @return The IPersistent with the unique identifier provided in the argument
     * @throws DAOException if no object exists with the unique 
     * identifier specified in the argument.
     */
    public IPersistent getPersistent(Long persistentId) throws DAOException; 

    public boolean doesObjectExist(String objectName, Long objectId) throws DAOException;
	
	//From ProjectDAO
	/**
	 * Retrieve the Project having this ID
	 * 
	 * @param projectId
	 * @return A Project object
	 */
	public org.psygrid.esl.model.dto.Project getProject(final Long projectId)
		throws DAOException;
	
	/**
	 * Retrieve the Project having the specified project code
	 * 
	 * @param projectCode
	 * @return A Project object
	 */
	public org.psygrid.esl.model.dto.Project getProject(final String projectCode) 
		throws DAOException;
	
	/**
	 * Store the Project. 
	 * 
	 * @param project
	 * @param username
	 * @return Long Project unique identifier
	 */
	public Long saveProject(org.psygrid.esl.model.dto.Project project, String username)
		throws DAOException, DuplicateObjectException, ObjectOutOfDateException;
	
	/**
	 * Returns whether this project is using randomisation
	 * 
	 * @param projectCode
	 * @return boolean whether the project has setup randomisation 
	 * @throws DAOException
	 */
	public boolean isRandomised(String projectCode)
		throws DAOException;
	
	/**
	 * Delete a project from the ESL.
	 * 
	 * @param projectId The DB generated unique ID of the project
	 * @param projectCode The project code
	 * @throws DAOException
	 */
	public void deleteProject(long projectId, String projectCode)
		throws DAOException;
	
	/**
	 * Check whether a project exists in the ESL for the given project
	 * code.
	 * 
	 * @param projectCode
	 * @return boolean project exists
	 * @throws DAOException
	 */
	public boolean isEslProject(String projectCode)
	throws DAOException;
	
	//From RandomisationDAO

	/**
	 * Retrieve the Randomisation having this ID
	 * 
	 * @param randomisationID
	 * @return A Randomisation object
	 */
	public org.psygrid.esl.model.dto.Randomisation getRandomisation(final Long randomisationID)
	throws DAOException;
		
	public List<org.psygrid.esl.scheduling.hibernate.QueuedSMS> getQueuedSMSs() throws DAOException;
	public Long saveRandomisationSMS(final org.psygrid.esl.scheduling.hibernate.QueuedSMS sms) throws DAOException;
	public void removeQueuedSMS(org.psygrid.esl.scheduling.hibernate.QueuedSMS sms) throws DAOException;

		
	//From RoleDAO
	
	/**
	 * Retrieve the Role having this ID
	 * 
	 * @param roleId
	 * @return A Role object
	 */
	public org.psygrid.esl.model.dto.Role getRole(final Long roleId)
		throws DAOException;
	
	/**
	 * Retrieve all Roles for a given randomisation
	 * 
	 * @param randomId
	 * @return A array of Role objects
	 */
	public org.psygrid.esl.model.dto.Role[] getAllRoles(final Long randomId)
	throws DAOException;
	
	/**
	 * Retrieve all roles to be notified when a randomisation decision is made
	 * 
	 * @param randomId
	 * @return A array of Role objects
	 * @throws DAOException
	 */
	public org.psygrid.esl.model.dto.Role[] getNotifyOfDecisionRoles(final Long randomId)
	throws DAOException;
	
	/**
	 * Retrieve all roles to be notified when randomisation is invoked
	 * 
	 * @param randomId
	 * @return A array of Role objects
	 * @throws DAOException
	 */
	public org.psygrid.esl.model.dto.Role[] getNotifyOfInvocationRoles(final Long randomId)
	throws DAOException;
	
	/**
	 * Retrieve all roles to informed of the treatment when a subject is randomised
	 * 
	 * @param randomId
	 * @return A array of Role objects
	 * @throws DAOException
	 */
	public org.psygrid.esl.model.dto.Role[] getNotifyOfTreatmentRoles(final Long randomId)
	throws DAOException;
	
	/**
	 * Store the Role. 
	 * 
	 * @param role
	 * @return Long Role unique identifier
	 */
	public Long saveRole(org.psygrid.esl.model.dto.Role role)
		throws DAOException, ObjectOutOfDateException;

	
	//From StrataDAO
	/**
	 * Retrieve the Strata having this ID
	 * 
	 * @param strataId
	 * @return A Strata object
	 */
	public org.psygrid.esl.model.dto.Strata getStrata(final Long strataId)
		throws DAOException;
	
	/**
	 * Retrieve all Strata for a given randomisation
	 * 
	 * @param randomId
	 * @return A array of Strata objects
	 */
	public org.psygrid.esl.model.dto.Strata[] getAllStrata(final Long randomId)
	throws DAOException;
	
	/**
	 * Store the Strata. 
	 * 
	 * @param strata
	 * @return Long Strata unique identifier
	 */
	public Long saveStrata(org.psygrid.esl.model.dto.Strata strata)
		throws DAOException, ObjectOutOfDateException;

		
	//From GroupDAO
	
	/**
	 * Retrieve the Group with this ID
	 * 
	 * @param groupId
	 * @return A Group object
	 */
	public org.psygrid.esl.model.dto.Group getGroup(final Long groupId)
		throws DAOException;
	
	/**
	 * Retrieve the Groups for this project
	 * 
	 * @param projectId
	 * @return Group[]
	 */
	public org.psygrid.esl.model.dto.Group[] getAllGroups(final Long projectId)
		throws DAOException;
	
	//From SubjectDAO	
	/**
	 * Retrieve the Subject having this ID
	 * 
	 * @param subjectId
	 * @return A Subject object
	 */
	public org.psygrid.esl.model.dto.Subject getSubject(final Long subjectId)
		throws NoResultsFoundException, SubjectLockedException;
	
	/**
	 * Retrieve the Subject having this study number
	 * 
	 * @param project Project study number belongs to
	 * @param studyNumber The study number used to retrieve the subject
	 * @return Subject
	 */
	public org.psygrid.esl.model.dto.Subject getSubject(org.psygrid.esl.model.dto.Project project, final String studyNumber) 
		throws NoResultsFoundException, SubjectLockedException;
	
	/**
	 * Retrieve the Subject having this study number, taking no notice of 
	 * whether the subject is locked or not.
	 * 
	 * @param project Project study number belongs to
	 * @param studyNumber The study number used to retrieve the subject
	 * @return Subject
	 */
	public org.psygrid.esl.model.dto.Subject getSubjectEvenIfLocked(org.psygrid.esl.model.dto.Project project, final String studyNumber) 
		throws NoResultsFoundException;
	
	/**
	 * Delete the subject with the given study number
	 * 
	 * @param studyNumber	The number of the subject to be deleted
	 */
	public void deleteSubject(final String studyNumber) throws NoResultsFoundException;
	
	/**
	 * Retrieve the study number for this subject
	 * 
	 * @param project 
	 * @param exampleSubject The list of parameters to search by
	 * @param restrictBy Restrict the search to the following groups
	 * @return String The study number 
	 */
	public String getStudyNumber(org.psygrid.esl.model.dto.Project project, org.psygrid.esl.model.dto.Subject exampleSubject, String[] restrictBy) throws DAOException;

	/**
	 * Retrieve the Subjects matching the criteria specified in the example Subject
	 * restricted by the Groups provided.
	 * 
	 * If no criteria are specified, the method will return all Subjects in those
	 * Groups as the criteria provide a set of restrictions to narrow down the 
	 * search. 
	 * 
	 * @param project the project to search
	 * @param exampleSubject the example subject used to search against
	 * @param restrictBy The groups in which to search for the subjects
	 * @return An array of Subjects retrieved by the search
	 */
	public org.psygrid.esl.model.dto.Subject[] findSubjectByExample(org.psygrid.esl.model.dto.Project project, org.psygrid.esl.model.dto.Subject exampleSubject, String[] restrictBy) throws DAOException;

	/**
	 * Test whether a subject exists for a given study number
	 * 
	 * @param project
	 * @param studyNumber
	 * @return boolean
	 * @throws DAOException
	 */
	public boolean exists(Project project, String studyNumber) throws DAOException;
	
	/**
	 * Store the Subject
	 * 
	 * @param subject
	 * @param username the user saving or updating this subject
	 * @return Long Subject unique identifier
	 */
	public Long saveSubject(org.psygrid.esl.model.ISubject subject, String username)
		throws DAOException, ObjectOutOfDateException, DuplicateObjectException, SubjectExistsException;
	
	/**
	 * Retrieve the NHS numbers for a list of study codes
	 * 
	 * @param projectCode
	 * @param studyCodes
	 * @return studyCodes+nhsNumbers
	 * @throws DAOException
	 */
	public String[][] findNhsNumbers(String projectCode, String[] studyCodes)
		throws DAOException;
	
	/**
	 * Retrieve the value of a single ESL property for the Subject
	 * with the specified study number.
	 * 
	 * @param identifier The study number
	 * @param property The name of the property
	 * @return The value of the property
	 * @throws DAOException
	 */
	public String getValueOfProperty(String identifier, String property)
		throws DAOException;

	
	//From AuditableDAO
	/**
	 * Retrieve the full history of changes made to an auditable object.
	 * 
	 * @param auditable
	 * @return org.psygrid.esl.model.dto.ProvenanceLog
	 * @throws DAOException
	 */
	public org.psygrid.esl.model.dto.ProvenanceLog getHistory(final IAuditable auditable) throws DAOException;
	
	/**
	 * Retrieve the changes made to an auditable object at a particular instance 
	 * specified by the date provided.
	 * 
	 * @param auditable
	 * @param date
	 * @return org.psygrid.esl.model.dto.Change[]
	 * @throws DAOException
	 */
	public org.psygrid.esl.model.dto.Change[] getChanges(final IAuditable auditable, final Date date) throws DAOException;
	
	/**
	 * Retrieve the change made to a given field for an auditable object on the specified date.
	 * 
	 * @param auditable
	 * @param date
	 * @param field
	 * @return org.psygrid.esl.model.dto.Change
	 * @throws DAOException
	 */
	public org.psygrid.esl.model.dto.Change getChange(final IAuditable auditable, final Date date, final String field) throws DAOException;
	


	/**
	 * Add a new group to the ESL if the supplied project exists.
	 * 
	 * @param projectCode the projectCode
	 * @param code the group code
	 * @param name the group name
	 */
	public void addedGroup(String projectCode, String code, String name);

	/**
	 * Update a group if it exists.
	 * 
	 * @param projectCode the project
	 * @param groupCode the group code
	 * @param newCode the new group code
	 * @param newName the new group name
	 */
	public void updatedGroup(String projectCode, String groupCode, String newCode, String newName);

	/**
	 * Delete a group if it exists.
	 * 
	 * @param projectCode the project
	 * @param groupCode the group
	 */
	public void deletedGroup(String projectCode,String groupCode);

}

