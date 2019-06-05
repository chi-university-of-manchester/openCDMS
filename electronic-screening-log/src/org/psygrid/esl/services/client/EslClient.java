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

package org.psygrid.esl.services.client;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.rpc.ServiceException;

import org.apache.axis.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.esl.model.IProject;
import org.psygrid.esl.model.IRandomisation;
import org.psygrid.esl.model.ISubject;
import org.psygrid.esl.model.hibernate.CustomEmailInfo;
import org.psygrid.esl.randomise.StrataStats;
import org.psygrid.esl.services.ESLDuplicateObjectFault;
import org.psygrid.esl.services.ESLOutOfDateFault;
import org.psygrid.esl.services.ESLServiceFault;
import org.psygrid.esl.services.ESLSubjectExistsException;
import org.psygrid.esl.services.ESLSubjectLockedFault;
import org.psygrid.esl.services.ESLSubjectNotFoundFault;
import org.psygrid.esl.services.Esl;
import org.psygrid.esl.services.NotAuthorisedFault;
import org.psygrid.esl.services.RandomisationException;

/**
 * Class to act as a layer of abstraction between a Java client
 * and the web services exposed by the ESL
 * 
 * @author Lucy Bridges
 *
 */
public class EslClient extends org.psygrid.common.AbstractClient {

	private final static Log LOG = LogFactory.getLog(EslClient.class);

	private final Esl service;
	
	/**
	 * The url where the web-service is located.
	 */
	private URL url = null;

	/**
	 * Default no-arg constructor
	 */
	public EslClient(){
		service = getService();
	}

	/**
	 * Constructor that accepts a value for the url where the web
	 * service is located.
	 * 
	 * @param url
	 */
	public EslClient(URL url){
		this.url = url;
		service = getService();
	}

	public String getVersion() throws ConnectException, SocketTimeoutException {
		try{
			return service.getVersion();
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(RemoteException ex){
			throw new RuntimeException(ex);
		}

	}

	/**
	 * Save a Project to the ESL.
	 * 
	 * @param project The project to save.
	 * @param saml SAML assertion.
	 * @return The unique identifier of the saved project.
	 * @throws ConnectException if the client cannot connect to the remote
	 * web-service host.
	 * @throws NotAuthorisedFault if authorisation fails or returns false.
	 * @throws EslServiceFault if the project is unable to be saved due
	 * to unrecoverable problems i.e. the client should not have allowed the
	 * project to be saved in this state.
	 * @throws ESLDuplicateObjectFault if a project exists with the same unique
	 * identifier already
	 * @throws ESLOutOfDateFault if the project is unable to be saved
	 * due to concurrency problems i.e. the user is trying to save an out-of-date
	 * project.
	 */
	public Long saveProject(IProject project, String saml)
	throws ConnectException, SocketTimeoutException,
	ESLOutOfDateFault, ESLDuplicateObjectFault, ESLServiceFault, 
	NotAuthorisedFault {

		try{
			org.psygrid.esl.model.dto.Project proj = project.toDTO();
			return service.saveProject(proj, saml);
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(RemoteException ex){
			throw new RuntimeException(ex);
		}

	}

	/**
	 * Retrieve a project from the esl.
	 * 
	 * @param projectId The ID of the project to retrieve.
	 * @param saml SAML assertion.
	 * @return The project.
	 * @throws ConnectException if the client cannot connect to the remote
	 * web-service host.
	 * @throws NotAuthorisedFault if authorisation fails or returns false.
	 * @throws EslServiceFault if no project exists for the given id, 
	 * or any other unrecoverable error.
	 */
	public IProject retrieveProject(Long projectId, String saml) 
	throws ConnectException, SocketTimeoutException, 
	ESLServiceFault, NotAuthorisedFault {

		try{
			org.psygrid.esl.model.dto.Project dtoProj = service.retrieveProject(projectId.longValue(), saml);
			IProject project = null;
			if ( null != dtoProj ){
				project = dtoProj.toHibernate();
			}
			return project;
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(RemoteException ex){
			throw new RuntimeException(ex);
		}

	}

	/**
	 * Retrieve a project from the esl.
	 * 
	 * @param projectCode The project code used to identify the project
	 * @param saml SAML assertion.
	 * @return The project.
	 * @throws ConnectException if the client cannot connect to the remote
	 * web-service host.
	 * @throws NotAuthorisedFault if authorisation fails or returns false.
	 * @throws EslServiceFault if no project exists for the given id, 
	 * or any other unrecoverable error.
	 */
	public IProject retrieveProjectByCode(String projectCode, String saml)
	throws ConnectException, SocketTimeoutException, ESLServiceFault, NotAuthorisedFault {
		try{
			org.psygrid.esl.model.dto.Project dtoProj = service.retrieveProjectByCode(projectCode, saml);
			IProject project = null;
			if ( null != dtoProj ){
				project = dtoProj.toHibernate();
			}
			return project;
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(RemoteException ex){
			throw new RuntimeException(ex);
		}

	}

	/**
	 * Save a Subject (trial participant) to the ESL.
	 * 
	 * @param subject The subject to save.
	 * @param saml SAML assertion.
	 * @return The unique identifier of the saved subject.
	 * @throws ConnectException if the client cannot connect to the remote
	 * web-service host.
	 * @throws NotAuthorisedFault if authorisation fails or returns false.
	 * @throws EslServiceFault if the subject is unable to be saved due
	 * to unrecoverable problems i.e. the client should not have allowed the
	 * subject to be saved in this state.
	 * @throws ESLDuplicateObjectFault if a subject already exists with the same 
	 * unique identifier
	 * @throws EslOutOfDateFault if the project is unable to be saved
	 * due to concurrency problems i.e. the user is trying to save an out-of-date
	 * subject.
	 */
	public Long saveSubject(ISubject subject, String saml)
	throws ConnectException, SocketTimeoutException, 
	ESLOutOfDateFault, 
	ESLDuplicateObjectFault,
	ESLSubjectExistsException,
	ESLServiceFault,
	NotAuthorisedFault{

		try{
			org.psygrid.esl.model.dto.Subject dtoSubject = subject.toDTO();
			return service.saveSubject(dtoSubject, saml);
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(RemoteException ex){
			throw new RuntimeException(ex);
		}

	}

	/**
	 * Retrieve a subject (trial participant) from the ESL.
	 * 
	 * @param subjectId The ID of the subject to retrieve.
	 * @param saml SAML assertion.
	 * @return The subject.
	 * @throws ConnectException if the client cannot connect to the remote
	 * web-service host.
	 * @throws NotAuthorisedFault if authorisation fails or returns false.
	 * @throws EslServiceFault if no subject exists for the given id, 
	 * or any other unrecoverable error.
	 * @throws ESLSubjectLockedFault
	 */
	public ISubject retrieveSubject(Long subjectId, String saml) 
	throws ConnectException, SocketTimeoutException, 
	ESLServiceFault, NotAuthorisedFault,  ESLSubjectLockedFault {

		try{
			org.psygrid.esl.model.dto.Subject dtoSubject = service.retrieveSubject(subjectId.longValue(), saml);
			ISubject subject = null;
			if ( null != dtoSubject ){
				subject = dtoSubject.toHibernate();
			}
			return subject;
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(RemoteException ex){
			throw new RuntimeException(ex);
		}
	}


	/**
	 * Retrieve a subject (trial participant) from the ESL.
	 * 
	 * @param project The project the subject belongs to
	 * @param studyNumber The unique study number assigned to the subject
	 * @param saml SAML assertion.
	 * @return The subject.
	 * @throws ConnectException if the client cannot connect to the remote
	 * web-service host.
	 * @throws NotAuthorisedFault if authorisation fails or returns false.
	 * @throws EslServiceFault if no subject exists for the given id, 
	 * or any other unrecoverable error.
	 */
	public ISubject retrieveSubjectByStudyNumber(IProject project, String studyNumber, String saml) 
	throws ConnectException, SocketTimeoutException,
	ESLServiceFault, NotAuthorisedFault, ESLSubjectNotFoundFault {

		try{
			org.psygrid.esl.model.dto.Project dtoProject = project.toDTO();
			org.psygrid.esl.model.dto.Subject dtoSubject = service.retrieveSubjectByStudyNumber(dtoProject, studyNumber, saml);
			ISubject subject = null;
			if ( null != dtoSubject ){
				subject = dtoSubject.toHibernate();
			}
			return subject;
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(RemoteException ex){
			throw new RuntimeException(ex);
		}
	}


	/**
	 * Retrieve a list of subjects (trial participants) from the ESL matching
	 * the criteria provided in an example subject object.
	 * 
	 * @param project The project to use to search for subjects
	 * @param exampleSubject an example subject, the contents of the fields 
	 * of which are used to search against subjects contained in a given project
	 * @param saml SAML assertion.
	 * @return list of subjects found matching the criteria
	 * @throws ConnectException if the client cannot connect to the remote
	 * web-service host.
	 * @throws NotAuthorisedFault if authorisation fails or returns false.
	 * @throws EslServiceFault if no subject exists for the given id, 
	 * or any other unrecoverable error.
	 */
	public List<ISubject> findSubjectByExample(IProject project, ISubject exampleSubject, String saml) throws ConnectException, 
	ESLServiceFault, SocketTimeoutException, NotAuthorisedFault {

		try{
			org.psygrid.esl.model.dto.Project dtoProject = project.toDTO();
			org.psygrid.esl.model.dto.Subject dtoExampleSubject = exampleSubject.toDTO();

			org.psygrid.esl.model.dto.Subject[] dtoSubject = service.findSubjectByExample(dtoProject, dtoExampleSubject, saml);
			List<ISubject> subject = new ArrayList<ISubject>();
			if ( null != dtoSubject ){
				for (org.psygrid.esl.model.dto.Subject individual: dtoSubject) {
					subject.add(individual.toHibernate());
				}
			}
			return subject;
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(RemoteException ex){
			throw new RuntimeException(ex);
		}
	}


	/**
	 * Lookup the study number assigned to a subject (trial participant)
	 * 
	 * @param project The project to search within
	 * @param exampleSubject The criteria used to lookup the subject's study number
	 * @param saml SAML assertion.
	 * @return The study number.
	 * @throws ConnectException if the client cannot connect to the remote
	 * web-service host.
	 * @throws NotAuthorisedFault if authorisation fails or returns false.
	 * @throws EslServiceFault if no subject exists for the given criteria, 
	 * or any other unrecoverable error.
	 */
	public String lookupStudyNumber(IProject project, ISubject exampleSubject, String saml) 
	throws ConnectException, SocketTimeoutException, 
	ESLServiceFault, NotAuthorisedFault {

		try{
			org.psygrid.esl.model.dto.Subject dtoSubject = exampleSubject.toDTO();
			org.psygrid.esl.model.dto.Project dtoProject = project.toDTO();

			String studyNumber = null;
			studyNumber = service.lookupStudyNumber(dtoProject, dtoSubject, saml);

			return studyNumber;
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(RemoteException ex){
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Save the randomisation settings for a project to the ESL
	 * and the randomisation module.
	 * 
	 * @param project The project the randomisation belongs to.
	 * @param randomisation The randomisation settings to save.
	 * @param saml SAML assertion.
	 * @throws ConnectException if the client cannot connect to the remote
	 * web-service host.
	 * @throws NotAuthorisedFault if authorisation fails or returns false.
	 * @throws EslServiceFault if the randomisation is unable to be saved due
	 * to unrecoverable problems i.e. the client should not have allowed the
	 * randomisation to be saved in this state.
	 * @throws EslOutOfDateFault if the randomisation is unable to be saved to 
	 * the ESL due to the object being out of date
	 * @throws ESLDuplicateObjectFault if a randomisation already exists within 
	 * the ESL with the same unique identifier
	 * @throws RandomisationException if the randomisation cannot be saved with
	 * the remote randomisation service.
	 * due to concurrency problems i.e. the user is trying to save an out-of-date
	 * project.
	 */
	public void setupRandomisation(IProject project, IRandomisation randomisation, String saml) throws ConnectException,
	ESLOutOfDateFault, ESLDuplicateObjectFault, ESLServiceFault, SocketTimeoutException,
	RandomisationException, NotAuthorisedFault { 

		try {
			org.psygrid.esl.model.dto.Project dtoProject = project.toDTO();
			org.psygrid.esl.model.dto.Randomisation random = randomisation.toDTO();
			service.setupRandomisation(dtoProject, random, saml);
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
		}
		catch(RemoteException ex){
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Randomises a subject by allocating a treatment arm with the randomisation
	 * service. Applies only to projects using randomisation and where the subject
	 * has been deemed eligable for randomisation.
	 * 
	 * @param subject the subject to be randomised (must have been saved to the
	 * ESL previously)
	 * @param site - the site that the subject belongs to, if this site info is to be included in randomisation
	 * 				emails. If null, the information will NOT be included in the email.
	 * @param saml SAML assertion.
	 * @throws ConnectException if the client cannot connect to the remote
	 * web-service host.
	 * @throws RandomisationException if there was a problem allocating a 
	 * treatment arm using the randomisation service
	 * @throws ESLServiceFault if there was an unrecoverable problem retrieving
	 * the details from the ESL necessary to randomise the subject, e.g no
	 * randomisation setup for the project.
	 * @throws NotAuthorisedFault if authorisation fails or returns false.
	 */
	public void randomiseSubject(ISubject subject, CustomEmailInfo customInfo, String saml) throws ConnectException, SocketTimeoutException, 
	RandomisationException, ESLServiceFault, NotAuthorisedFault {

		try {
			org.psygrid.esl.model.dto.Subject dtoSubject = subject.toDTO();
			service.randomiseSubject(dtoSubject, customInfo, saml);
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
		}
		catch(RemoteException ex){
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Retrieve the result of randomisation for a given subject.
	 * 
	 * @param project The project the subject belongs to
	 * @param studyNumber used to identify the subject
	 * @param saml SAML assertion.
	 * @return treatment allocated
	 * @throws ConnectException if the client cannot connect to the remote
	 * web-service host.
	 * @throws NotAuthorisedFault if authorisation fails or returns false.
	 * @throws RandomisationException if there was a problem retrieving the
	 * result from the randomisation service
	 * @throws EslServiceFault if the information cannot be retrieved due
	 * to unrecoverable problems e.g if the project code is not supplied.
	 */
	public String lookupRandomisationResult(IProject project, String studyNumber, String saml) throws ConnectException, SocketTimeoutException, 
	ESLServiceFault, ESLServiceFault, RandomisationException, NotAuthorisedFault {

		try{
			org.psygrid.esl.model.dto.Project dtoProject = project.toDTO();

			return service.lookupRandomisationResult(dtoProject, studyNumber, saml);
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(RemoteException ex){
			throw new RuntimeException(ex);
		}
	}

	
	/**
	 * Retrieve all subjects, who have had a treatment allocated by the
	 * remote randomisation service and their assigned treatments
	 * 
	 * @param project the project involved
	 * @param saml SAML assertion.
	 * @return map of subjects and allocated treatments
	 * @throws ConnectException if the client cannot connect to the remote
	 * web-service host.
	 * @throws NotAuthorisedFault if authorisation fails or returns false.
	 * @throws RandomisationException if an unrecoverable problem accurred
	 * when talking to the remote randomisation service.
	 * @throws ESLServiceFault if the information cannot be retrieved due
	 * to unrecoverable problems i.e. the client should not have allowed the
	 * project to be saved in this state.
	 */
	public Map<String, String> emergencyBreakIn(IProject project, String saml) throws ConnectException, SocketTimeoutException, 
	RandomisationException, ESLServiceFault, NotAuthorisedFault {

		try{
			org.psygrid.esl.model.dto.Project dtoProject = project.toDTO();

			String[][] results = service.emergencyBreakIn(dtoProject, saml);
			Map<String,String> subjects = new HashMap<String,String>();
			if ( null != results ){
				for (int i = 0; i < results.length; i++) {
					String studyNumber = results[i][0];
					if (studyNumber != null) {
						subjects.put(studyNumber, results[i][1]);
					}
				}
			}
			return subjects;
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(RemoteException ex){
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Test whether a subject exists for a given study number
	 * 
	 * @param project
	 * @param studyNumber
	 * @param saml
	 * @return boolean
	 * @throws ConnectException if the client cannot connect to the remote
	 * web-service host
	 * @throws NotAuthorisedFault if authorisation fails or returns false
	 * @throws ESLServiceFault if the information cannot be retrieved due
	 * to unrecoverable problems i.e. the client should not have allowed the
	 * project to be saved in this state
	 */
	public boolean exists(IProject project, String studyNumber, String saml) throws ConnectException, SocketTimeoutException, 
	ESLServiceFault, NotAuthorisedFault {

		try{
			org.psygrid.esl.model.dto.Project dtoProject = project.toDTO();

			return service.exists(dtoProject, studyNumber, saml);

		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			throw new ESLServiceFault(fault);
		}
		catch(RemoteException ex){
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Find out whether a project uses randomisation.
	 * 
	 * @param projectCode The project code
	 * @param saml SAML assertion
	 * @return isRandomised
	 * @throws ConnectException if the client cannot connect to the remote
	 * web-service host.
	 * @throws NotAuthorisedFault if authorisation fails or returns false.
	 * @throws EslServiceFault if no project exists for the given id, 
	 * or any other unrecoverable error.
	 */
	public boolean isProjectRandomised(String projectCode, String saml) 
	throws ConnectException, SocketTimeoutException, 
	ESLServiceFault, NotAuthorisedFault {

		try{	
			return service.isProjectRandomised(projectCode, saml);
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			throw new ESLServiceFault(fault);
		}
		catch(RemoteException ex){
			throw new RuntimeException(ex);
		}

	}

	/**
	 * Retrieve the dates of randomisations in a trial for a given subject.
	 * 
	 * Returns null if the subject has not been randomised.
	 * 
	 * @param projectCode The project code
	 * @param studyNumber The subject's study number
	 * @param saml SAML assertion
	 * @return list of dates of randomisations
	 * @throws ConnectException if the client cannot connect to the remote
	 * web-service host.
	 * @throws NotAuthorisedFault if authorisation fails or returns false.
	 * @throws EslServiceFault if no project exists for the given id, 
	 * or any other unrecoverable error.
	 */
	public List<Calendar> retrieveSubjectRandomisationEvents(String projectCode, String studyNumber, String saml) 
		throws ConnectException, SocketTimeoutException, RandomisationException, NotAuthorisedFault {

		try{
			List<Calendar> randomisations = new ArrayList<Calendar>();
			Calendar[] cal = service.retrieveSubjectRandomisationEvents(projectCode, studyNumber, saml);
			if (cal == null) {
				return null;
			}
			for (Calendar c: cal) {
				randomisations.add(c);
			}
			
			return randomisations;
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(RemoteException ex){
			throw new RuntimeException(ex);
		}

	}
	
    /**
     * Retrieve the randomization statistics for a project.
     * 
     * @param project the project involved
     * @param saml SAML assertion.
     * @return Map of treatments to number os allocations.
     * @throws ConnectException if the client cannot connect to the remote
     * web-service host.
     * @throws NotAuthorisedFault if authorisation fails or returns false.
     * @throws RandomisationException if an unrecoverable problem accurred
     * when talking to the remote randomisation service.
     * @throws ESLServiceFault if the information cannot be retrieved due
     * to unrecoverable problems i.e. the client should not have allowed the
     * project to be saved in this state.
     */
    public Map<String, String> lookupRandomizerStatistics(IProject project, String saml) 
            throws ConnectException, SocketTimeoutException, RandomisationException, ESLServiceFault, NotAuthorisedFault {

        try{
            org.psygrid.esl.model.dto.Project dtoProject = project.toDTO();

            String[][] results = service.lookupRandomizerStatistics(dtoProject, saml);
            Map<String,String> stats = new HashMap<String,String>();
            if ( null != results ){
                for (int i = 0; i < results.length; i++) {
                    String studyNumber = results[i][0];
                    if (studyNumber != null) {
                        stats.put(studyNumber, results[i][1]);
                    }
                }
            }
            return stats;
        }
        catch(AxisFault fault){
        	handleAxisFault(fault, LOG);
			return null;
        }
        catch(RemoteException ex){
            throw new RuntimeException(ex);
        }
    }

    /**
     * Retrieve the randomization statistics for a project, with stats given
     * at the level of each combination of strata.
     * 
     * @param project the project involved
     * @param saml SAML assertion.
     * @return Array of statistics, each element being the statistics for
     * a single combination of strata.
     * @throws ConnectException if the client cannot connect to the remote
     * web-service host.
     * @throws NotAuthorisedFault if authorisation fails or returns false.
     * @throws RandomisationException if an unrecoverable problem accurred
     * when talking to the remote randomisation service.
     * @throws ESLServiceFault if the information cannot be retrieved due
     * to unrecoverable problems i.e. the client should not have allowed the
     * project to be saved in this state.
     */
    public StrataStats[] lookupStratifiedRandomizerStatistics(IProject project, String saml) 
            throws ConnectException, SocketTimeoutException, RandomisationException, ESLServiceFault, NotAuthorisedFault {

        try{
            org.psygrid.esl.model.dto.Project dtoProject = project.toDTO();

            return service.lookupStratifiedRandomizerStatistics(dtoProject, saml);
        }
        catch(AxisFault fault){
        	handleAxisFault(fault, LOG);
			return null;
        }
        catch(RemoteException ex){
            throw new RuntimeException(ex);
        }
    }

	/**
	 * Retrieve the result of randomisation for a given subject and date.
	 * 
	 * @param projectCode The project the subject belongs to
	 * @param studyNumber used to identify the subject
	 * @param date of the randomisation event
	 * @param saml SAML assertion.
	 * @return treatment id and name
	 * @throws ConnectException if the client cannot connect to the remote
	 * web-service host.
	 * @throws NotAuthorisedFault if authorisation fails or returns false.
	 * @throws RandomisationException if there was a problem retrieving the
	 * result from the randomisation service
	 * @throws EslServiceFault if the information cannot be retrieved due
	 * to unrecoverable problems e.g if the project code is not supplied.
	 */
	public String[] lookupRandomisationResultForDate(String projectCode, String studyNumber, Calendar date, String saml) throws ConnectException, 
	ESLServiceFault, SocketTimeoutException, RandomisationException, NotAuthorisedFault {

		try{
			return service.lookupRandomisationResultForDate(projectCode, studyNumber, date, saml);
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(RemoteException ex){
			throw new RuntimeException(ex);
		}
	}
	
	public String allocateMedicationPackage(String projectCode,
			String centreCode, String participantIdentifier, String saml) throws ConnectException, SocketTimeoutException, ESLServiceFault, NotAuthorisedFault{
		
		String medsPackageId = null;
		
		try {
			medsPackageId = service.allocateMedicationPackage(projectCode, centreCode, participantIdentifier, saml);
		} catch (AxisFault e) {
			handleAxisFault(e,LOG);
		} catch (RemoteException ex){
			throw new RuntimeException(ex);
		}
		
		return medsPackageId;
	}
    
	/**
	 * Retrieve the NHS numbers for a list of study numbers in a project.
	 * 
	 * @param projectCode
	 * @param studyNumbers
	 * @param saml
	 * @return nhsNumbers
	 * @throws ConnectException
	 * @throws ESLServiceFault
	 * @throws NotAuthorisedFault
	 */
	public Map<String,String> retrieveNhsNumbers(String projectCode, List<String> studyNumbers, String saml) throws ConnectException, 
	ESLServiceFault, SocketTimeoutException, NotAuthorisedFault {

		try{
			String[] numbers = new String[studyNumbers.size()];
			for (int i = 0; i < studyNumbers.size(); i++) {
				numbers[i] = studyNumbers.get(i);
			}
			String[][] results = service.retrieveNhsNumbers(projectCode, numbers, saml);
			
			Map<String,String> nhsNumbers = new HashMap<String,String>();
			for (String[] result: results) {
				nhsNumbers.put(result[0], result[1]);
			}
			return nhsNumbers;
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(RemoteException ex){
			throw new RuntimeException(ex);
		}
	}
	
	public void lockSubject(String identifier, String saml) throws ConnectException, ESLServiceFault, SocketTimeoutException, ESLSubjectNotFoundFault, NotAuthorisedFault {
		
		try{
			service.lockSubject(identifier, saml);
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
		}
		catch(RemoteException ex){
			throw new RuntimeException(ex);
		}
	}
	
	public void unlockSubject(String identifier, String saml) throws ConnectException, ESLServiceFault, ESLSubjectNotFoundFault, SocketTimeoutException, NotAuthorisedFault {
		
		try{
			service.unlockSubject(identifier, saml);
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
		}
		catch(RemoteException ex){
			throw new RuntimeException(ex);
		}
	}
	
	/**
	 * Delete the subject - simply delegates to the ESL service
	 * 
	 * @param identifier			The subject to be deleted
	 * @param saml					For authorisation checking
	 * @throws ESLServiceFault
	 * @throws NotAuthorisedFault	The user is not authorised to perform this action
	 */
	public void deleteSubject(String identifier, String saml) throws ESLServiceFault, NotAuthorisedFault {
		try {
			service.deleteSubject(identifier, saml);
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}
	
	public String getProperty(String identifier, String property, String saml) throws ConnectException, ESLServiceFault, SocketTimeoutException, NotAuthorisedFault {
		try{
			return service.getProperty(identifier, property, saml);
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(RemoteException ex){
			throw new RuntimeException(ex);
		}
	}
	
	
	public void deleteProject(long projectId, String projectCode, String saml) throws ConnectException, ESLServiceFault, SocketTimeoutException, NotAuthorisedFault {
		try{
			service.deleteProject(projectId, projectCode, saml);
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
		}
		catch(RemoteException ex){
			throw new RuntimeException(ex);
		}
	}
	
	public boolean isEslProject(String projectCode, String saml) 
	throws ConnectException, SocketTimeoutException, 
	ESLServiceFault, NotAuthorisedFault {

		try{	
			return service.isEslProject(projectCode, saml);
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			throw new ESLServiceFault(fault);
		}
		catch(RemoteException ex){
			throw new RuntimeException(ex);
		}
	}
	
	public void handleConsentWithdrawn(String identifier, String saml) throws ESLServiceFault, NotAuthorisedFault {
		try {
			service.handleConsentWithdrawn(identifier, saml);
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}
	
	public boolean willSubjectBeDeletedWhenConsentIsWithdrawn(String identifier, String saml) throws ESLServiceFault, NotAuthorisedFault {
		try {
			return service.willSubjectBeDeletedWhenConsentIsWithdrawn(identifier, saml);
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}
	
	private Esl getService() {
		//class auto-generated from WSDL, using settings specified in config files.
		org.psygrid.esl.services.EslServiceLocator locator = new org.psygrid.esl.services.EslServiceLocator();	
		Esl service = null;
		try{
			if ( null == this.url ){
				service = locator.getesl();
			}
			else{
				service = locator.getesl(url);
			}
		}
		catch(ServiceException ex){
			//this can only happen if the ESL was built with
			//an incorrect URL
			throw new RuntimeException("ESL URL is invalid!", ex);
		}
		return service;
	}
}
