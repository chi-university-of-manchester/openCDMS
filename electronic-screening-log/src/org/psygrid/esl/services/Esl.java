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

package org.psygrid.esl.services;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.rmi.RemoteException;
import java.util.Calendar;

import org.psygrid.esl.dao.NoResultsFoundException;
import org.psygrid.esl.model.dto.Project;
import org.psygrid.esl.model.dto.Randomisation;
import org.psygrid.esl.model.dto.Subject;
import org.psygrid.esl.model.hibernate.CustomEmailInfo;
import org.psygrid.esl.randomise.StrataStats;

/**
 * Web service interface to the electronic screening log.
 * <p>
 * Provides the functionality required for external 
 * clients to interact with the electronic screening log 
 * e.g to register and retrieve information on trial subjects, 
 * to setup projects, to use a remote randomisation service to
 * randomly allocate treatments, etc.
 * 
 * @author Lucy Bridges
 *
 */
public interface Esl extends java.rmi.Remote {
	
	/**
	 * Get the version of the ESL
	 * 
	 * @return String
	 * @throws RemoteException
	 */
	public String getVersion() throws RemoteException;
	
	/**
	 * Save the project
	 * 
	 * @param project
	 * @param saml
	 * @return the project's unique identifier
	 * @throws RemoteException
	 * @throws ESLServiceFault
	 * @throws ESLOutOfDateFault if the object to be saved is out-of-date
	 * @throws ESLDuplicateObjectFault if a project with the same unique identifier
	 * already exists in the ESL
	 * @throws NotAuthorisedFault
	 */
	public long saveProject(Project project, String saml) 
		throws RemoteException, ESLDuplicateObjectFault, ESLServiceFault, ESLOutOfDateFault, NotAuthorisedFault;

	
	/**
	 * Save the project's randomisation details locally and setup the remote 
	 * randomisation service
	 * 
	 * @param project 
	 * @param randomisation
	 * @param saml
	 * @throws RemoteException
	 * @throws ESLServiceFault
	 * @throws ESLOutOfDateFault if the object to be saved is out-of-date
	 * @throws ESLDuplicateObjectFault if a randomisation already exists in the ESL 
	 * with the same unique identifier
	 * @throws RandomisationException if there is a problem using the remote 
	 * randomisation service
	 * @throws NotAuthorisedFault
	 */
	public void setupRandomisation(Project project, Randomisation randomisation, String saml)
		throws RemoteException, ESLDuplicateObjectFault, ESLServiceFault, ESLOutOfDateFault, RandomisationException, NotAuthorisedFault,
		ConnectException, SocketTimeoutException ;

	/**
	 * Allocate a treatment arm to an existing subject using the remote randomisation
	 * service.
	 *  
	 * @param subject
	 * @param customInfo - provides custom email info that is required to generate custom
	 * 						randomisation emails. If customInfo is null or if the required
	 * 						constituent object is null.
	 * @param saml
	 * @throws RemoteException
	 * @throws ESLServiceFault thrown if no randomisation has been setup
	 * @throws RandomisationException thrown if there was a problem with the remote
	 * randomiser when attempting to allocate a treatment arm
	 * @throws NotAuthorisedFault
	 */
	public void randomiseSubject(Subject subject, CustomEmailInfo customInfo, String saml)throws RemoteException, ESLServiceFault, RandomisationException, 
	NotAuthorisedFault, ConnectException, SocketTimeoutException ;
			
	/**
	 * Retrieve the project with the provided identifier
	 * 
	 * @param projectId
	 * @param saml
	 * @return project
	 * @throws RemoteException
	 * @throws ESLServiceFault
	 * @throws NotAuthorisedFault
	 */
	public Project retrieveProject(long projectId, String saml)
		throws RemoteException, ESLServiceFault, NotAuthorisedFault;
	
	/**
	 * Retrieve a project with the given project code
	 * 
	 * @param projectCode
	 * @param saml
	 * @return project
	 * @throws RemoteException
	 * @throws ESLServiceFault
	 * @throws NotAuthorisedFault
	 */
	public Project retrieveProjectByCode(String projectCode, String saml)
	throws RemoteException, ESLServiceFault, NotAuthorisedFault;
	
	/**
	 * Provides a list of all subjects and treatments who have been allocated 
	 * a treatment during randomisation
	 * 
	 * @param project
	 * @param saml
	 * @return Nested string array (where string[i][0] is the subject's study
	 * number and string[i][1] is the treatment allocation)
	 * @throws RemoteException
	 * @throws ESLServiceFault
	 * @throws NotAuthorisedFault
	 */
	public String[][] emergencyBreakIn(Project project, String saml) throws RemoteException, RandomisationException, ESLServiceFault, 
	NotAuthorisedFault, ConnectException, SocketTimeoutException ;

	/**
	 * Save the provided subject
	 * 
	 * @param subject
	 * @param saml
	 * @return subject's new identifier
	 * @throws RemoteException
	 * @throws ESLServiceFault
	 * @throws ESLOutOfDateFault if the object to be saved is out-of-date
	 * @throws ESLDuplicateObjectFault if a randomisation already exists in the ESL 
	 * with the same unique identifier
	 * @throws NotAuthorisedFault
	 */
	public long saveSubject(Subject subject, String saml) throws RemoteException, ESLServiceFault, ESLDuplicateObjectFault, ESLOutOfDateFault, ESLSubjectExistsException, NotAuthorisedFault;
	
	/**
	 * Retrieve the subject having the specified id
	 * 
	 * @param subjectId
	 * @param saml
	 * @return subject
	 * @throws RemoteException
	 * @throws ESLServiceFault
	 * @throws NotAuthorisedFault
	 * @throws ESLSubjectLockedFault
	 */
	public Subject retrieveSubject(long subjectId, String saml) throws RemoteException, ESLServiceFault, NotAuthorisedFault, ESLSubjectLockedFault;
	
	/**
	 * Retrieve the subject having the specified study number
	 * 
	 * @param project
	 * @param studyNumber
	 * @param saml
	 * @return subject
	 * @throws RemoteException
	 * @throws ESLServiceFault
	 * @throws NotAuthorisedFault
	 * @throws ESLSubjectLockedFault
	 */
	public Subject retrieveSubjectByStudyNumber(Project project, String studyNumber, String saml) 
	throws RemoteException, ESLServiceFault, NotAuthorisedFault, ESLSubjectNotFoundFault;
	
    /**
     * Retrieve a list of subjects matching the criteria given in the example subject
     *  
     * @param project
     * @param exampleSubject
     * @param saml
     * @return array of subjects
     * @throws RemoteException
     * @throws ESLServiceFault
     * @throws NotAuthorisedFault
     */
	public Subject[] findSubjectByExample(Project project, Subject exampleSubject, String saml) throws RemoteException, ESLServiceFault, NotAuthorisedFault;
	
	/**
	 * Lookup a subject's study number based on criteria present in the example subject
	 * 
	 * @param project
	 * @param exampleSubject
	 * @param saml
	 * @return studyNumber
	 * @throws RemoteException
	 * @throws ESLServiceFault
	 * @throws NotAuthorisedFault
	 */
	public String lookupStudyNumber(Project project, Subject exampleSubject, String saml)throws RemoteException, ESLServiceFault, NotAuthorisedFault;
	
	/**
	 * Retrieve the result of randomisation for a subject
	 * 
	 * @param project
	 * @param studyNumber
	 * @param saml
	 * @return treatment allocation
	 * @throws RemoteException
	 * @throws ESLServiceFault
	 * @throws NotAuthorisedFault
	 */
	public String lookupRandomisationResult(Project project, String studyNumber, String saml)throws RemoteException, 
	RandomisationException, ESLServiceFault, NotAuthorisedFault, ConnectException, SocketTimeoutException ;

	/**
	 * Test whether a subject exists for a given study number
	 * 
	 * @param project
	 * @param studyNumber
	 * @param saml
	 * @return boolean
	 * @throws RemoteException
	 * @throws ESLServiceFault
	 * @throws NotAuthorisedFault
	 */
	public boolean exists(Project project, String studyNumber, String saml)throws RemoteException, ESLServiceFault, NotAuthorisedFault;
	
	/**
	 * Find out if a project is using randomisation.
	 * 
	 * @param projectCode
	 * @param saml
	 * @return boolean
	 * @throws RemoteException
	 * @throws ESLServiceFault
	 * @throws NotAuthorisedFault
	 */
	public boolean isProjectRandomised(String projectCode, String saml) 
	throws RemoteException, ESLServiceFault, NotAuthorisedFault, ConnectException, SocketTimeoutException ;
	
	/**
	 * Retrieve the dates of randomisations in a trial for a given subject.
	 * 
	 * Returns null if the subject has not been randomised.
	 * 
	 * @param projectCode
	 * @param studyNumber
	 * @param saml
	 * @return list of the dates of randomisations
	 * @throws RemoteException
	 * @throws ESLServiceFault
	 * @throws NotAuthorisedFault
	 */
	public Calendar[] retrieveSubjectRandomisationEvents(String projectCode, String studyNumber, String saml) 
	throws RemoteException, RandomisationException, NotAuthorisedFault, ConnectException, SocketTimeoutException ;
	
    /**
     * Retrieve the randomization statistics for a project.
     * 
     * @param project
     * @param saml
     * @return Array containing the number of allocations for each treatment arm.
     * The structure of the array is that the first array dimension retrieves data
     * for one treatment arm, the second array dimension gets either the treatment 
     * arm name or the number of allocations i.e. result[0][0] will be the name
     * of the first treatment arm; result[0][1] will be the number of allocations
     * for it.
     * @throws RemoteException
     * @throws RandomisationException
     * @throws ESLServiceFault
     * @throws NotAuthorisedFault
     */
    public String[][] lookupRandomizerStatistics(Project project, String saml) throws RemoteException, 
    RandomisationException, ESLServiceFault, NotAuthorisedFault, ConnectException, SocketTimeoutException; 
    
    /**
     * Retrieve the randomization statistics for a project, with the 
     * statistics stated for each combination of strata.
     * 
     * @param project
     * @param saml
     * @return 
     * @throws RemoteException
     * @throws RandomisationException
     * @throws ESLServiceFault
     * @throws NotAuthorisedFault
     */
    public StrataStats[] lookupStratifiedRandomizerStatistics(Project project, String saml) 
    throws RemoteException, RandomisationException, ESLServiceFault, NotAuthorisedFault, ConnectException, SocketTimeoutException ; 
    
    /**
     * Lookup the randomisation event for the a given subject and date.
     * @param projectCode
     * @param studyNumber
     * @param date
     * @param saml
     * @return randomisation result (id and name)
     * @throws RemoteException
     * @throws RandomisationException
     * @throws ESLServiceFault
     * @throws NotAuthorisedFault
     */
    public String[] lookupRandomisationResultForDate(String projectCode, String studyNumber, Calendar date, String saml) 
    throws RemoteException, RandomisationException, ESLServiceFault, NotAuthorisedFault, ConnectException, SocketTimeoutException ;
    
    /**
     * Retrieve the nhs numbers for a list of study numbers in the specified project
     * 
     * @param projectCode
     * @param studyNumbers
     * @param saml
     * @return array of studyNumbers and nhsNumbers
     * @throws RemoteException
     * @throws ESLServiceFault
     * @throws NotAuthorisedFault
     */
    public String[][] retrieveNhsNumbers(String projectCode, String[] studyNumbers, String saml) throws RemoteException, ESLServiceFault, NotAuthorisedFault; 

    /**
     * Mark the subject as locked.
     * 
     * @param identifier
     * @param saml
     * @throws RemoteException
     * @throws ESLServiceFault
     * @throws NotAuthorisedFault
     */
    public void lockSubject(String identifier, String saml) throws RemoteException, ESLServiceFault, ESLSubjectNotFoundFault, NotAuthorisedFault;

    /**
     * Un-mark the subject as locked.
     * 
     * @param identifier
     * @param saml
     * @throws RemoteException
     * @throws ESLServiceFault
     * @throws ESLSubjectNotFoundFault
     * @throws NotAuthorisedFault
     */
    public void unlockSubject(String identifier, String saml) throws RemoteException, ESLServiceFault, ESLSubjectNotFoundFault, NotAuthorisedFault;

    /**
     * Delete the subject from the esl
     * 
     * @param identifier				The subject to be deleted
     * @param saml						For authorisation checking
     * @throws ESLServiceFault
     * @throws NotAuthorisedFault		The user is not authorised to perform this action
     * @throws NoResultsFoundException 	The subject was not found
     */
    public void deleteSubject(String identifier, String saml) throws RemoteException, ESLServiceFault, NotAuthorisedFault;
    
    /**
     * Retrieve the value of a single property in the ESL for the
     * subject with the specified identifier.
     * 
     * @param identifier
     * @param property
     * @param saml
     * @return String, value of the property
     * @throws RemoteException
     * @throws ESLServiceFault
     * @throws NotAuthorisedFault
     */
    public String getProperty(String identifier, String property, String saml) throws RemoteException, ESLServiceFault, NotAuthorisedFault;
    
    /**
     * Delete a project from the ESL permanently.
     * 
     * @param projectId The DB generated unique ID of the project.
     * @param projectCode The unique code of the project.
     * @param saml
     * @throws RemoteException
     * @throws ESLServiceFault
     * @throws NotAuthorisedFault
     */
    public void deleteProject(long projectId, String projectCode, String saml) throws RemoteException, ESLServiceFault, NotAuthorisedFault;

    /**
     * Check whether a project exists in the ESL for the given project code
     * 
     * @param projectCode
     * @param saml
     * @return boolean
     * @throws RemoteException
     * @throws ESLServiceFault
     * @throws NotAuthorisedFault
     */
    public boolean isEslProject(String projectCode, String saml) throws RemoteException, ESLServiceFault, NotAuthorisedFault;
    
    /**
     * Allocates a medication package for a randomised participant.
     * @param participantIdentifier
     * @param saml
     * @return
     * @throws RemoteException
     * @throws ESLServiceFault
     * @throws NotAuthorisedFault
     */
    public String allocateMedicationPackage(String projectCode, String centreCode, String participantIdentifier, String saml) throws RemoteException, ESLServiceFault, NotAuthorisedFault;
    
    /**
     * The subject should be deleted if the project does not use randomisation
     * (may need to contact patients later in this case) and the flag is set to
     * indicate it's ok to delete esl data (configured on a per study basis)
     * @param identifier	The subject to be dealt with
     * @param saml			For authorisation checking
     * @throws NotAuthorisedFault 
     * @throws ESLServiceFault 
     * @throws RemoteException 
     */
    public void handleConsentWithdrawn(String identifier, String saml) throws RemoteException, ESLServiceFault, NotAuthorisedFault;
    
    /**
     * Check whether withdrawing consent will cause the subject to be deleted.
     * The subject should be deleted if the project does not use randomisation
     * (may need to contact patients later in this case) and the flag is set to
     * indicate it's ok to delete esl data (configured on a per study basis)
     * @param identifier	The subject to be dealt with
     * @param saml			For authorisation checking
     * @return
     * @throws RemoteException
     * @throws ESLServiceFault
     * @throws NotAuthorisedFault
     */
    public boolean willSubjectBeDeletedWhenConsentIsWithdrawn(String identifier, String saml) throws RemoteException, ESLServiceFault, NotAuthorisedFault;
}
