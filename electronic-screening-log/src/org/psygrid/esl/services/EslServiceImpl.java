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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opensaml.SAMLAssertion;
import org.psygrid.common.identifier.IdentifierHelper;
import org.psygrid.common.identifier.InvalidIdentifierException;
import org.psygrid.esl.dao.DAOException;
import org.psygrid.esl.dao.DuplicateObjectException;
import org.psygrid.esl.dao.EslDAO;
import org.psygrid.esl.dao.NoResultsFoundException;
import org.psygrid.esl.dao.ObjectOutOfDateException;
import org.psygrid.esl.dao.SubjectExistsException;
import org.psygrid.esl.dao.SubjectLockedException;
import org.psygrid.esl.model.IRandomisation;
import org.psygrid.esl.model.ISubject;
import org.psygrid.esl.model.StrataAllocationFault;
import org.psygrid.esl.model.dto.Project;
import org.psygrid.esl.model.dto.Randomisation;
import org.psygrid.esl.model.dto.Strata;
import org.psygrid.esl.model.dto.Subject;
import org.psygrid.esl.model.hibernate.CustomEmailInfo;
import org.psygrid.esl.model.hibernate.Group;
import org.psygrid.esl.randomise.IRemoteRandomiser;
import org.psygrid.esl.randomise.StrataStats;
import org.psygrid.esl.util.AAQCWrapper;
import org.psygrid.logging.AuditLogger;
import org.psygrid.meds.rmi.MedicationClient;
import org.psygrid.randomization.Parameter;
import org.psygrid.security.PGSecurityException;
import org.psygrid.security.PGSecurityInvalidSAMLException;
import org.psygrid.security.PGSecuritySAMLVerificationException;
import org.psygrid.security.RBACAction;
import org.psygrid.security.accesscontrol.AEFGroup;
import org.psygrid.security.accesscontrol.AEFProject;
import org.psygrid.security.accesscontrol.IAccessEnforcementFunction;
import org.psygrid.security.utils.PropertyUtilities;
import org.psygrid.security.utils.SAMLUtilities;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.PrivilegeType;
import org.psygrid.www.xml.security.core.types.ProjectType;

/**
 * 
 * 
 * @author Lucy Bridges
 *
 */
public class EslServiceImpl implements EslServiceInternal {

	/**
	 * Name of the component, used for audit logging
	 */
	private static final String COMPONENT_NAME = "ESL";


	/**
	 * General purpose logger
	 */
	private static Log sLog = LogFactory.getLog(EslServiceImpl.class);

	/**
	 * Audit logger
	 */
	private static AuditLogger logHelper = null;

	/**
	 * Called to check permissions
	 */
    protected IAccessEnforcementFunction accessControl = null;

	/**
	 * DAO bean that handles all communication with the database.
	 */
	private EslDAO dao = null;
	
	private AAQCWrapper aaqc = null;

	/**
	 * Object that handles connections to the randomiser randomisation service
	 */
	private IRemoteRandomiser randomiser = null; 

	public void setAccessControl(IAccessEnforcementFunction accessControl) {
		this.accessControl = accessControl;
	}
	
	public void setDao(EslDAO dao) {
		this.dao = dao;
	}	
	
	public void setRandomiser(IRemoteRandomiser randomiser){
		this.randomiser=randomiser;
	}
	
	public void setLogHelper(AuditLogger logHelper) {
		EslServiceImpl.logHelper = logHelper;
	}

	public void setAaqc(AAQCWrapper aaqc) {
		this.aaqc = aaqc;
	}

	public String getVersion(){

		String version = null;
		try{
			Properties props = PropertyUtilities.getProperties("esl.properties");
			version = props.getProperty("org.psygrid.esl.version");
		}
		catch(Exception ex){
			//can't load the version, so set it to Unknown
			sLog.error(ex.getMessage(),ex);
			version = "Unknown";
		}
		return version;
	}


    protected String findUserName(String saml){
        
        //find invoker's username
        String userName = null;
        try{
            userName = accessControl.getUserFromUnverifiedSAML(saml);
        }
        catch(PGSecurityException ex){
            userName = "Unknown";
        }
        return userName;
    }
	

	public Project retrieveProject(long projectId, String saml) 
	throws RemoteException, ESLServiceFault, NotAuthorisedFault {

		final String METHOD_NAME = RBACAction.ACTION_ESL_RETRIEVE_PROJECT.toString();

		try {
			Project p = dao.getProject(projectId);
			String projectCode = null;
			if (p != null) {
				projectCode = p.getProjectCode();
			}
			checkAuth(saml, null, projectCode, METHOD_NAME);

			return p;
		}
		catch(DAOException ex){
			sLog.info(METHOD_NAME+": "+ex.getClass().getSimpleName(), ex);
			throw new ESLServiceFault(ex);
		}
		catch(RuntimeException ex){
			sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw ex;
		}
	}

	public Project retrieveProjectByCode(String projectCode, String saml)
	throws RemoteException, ESLServiceFault, NotAuthorisedFault {

		final String METHOD_NAME = RBACAction.ACTION_ESL_RETRIEVE_PROJECT_BY_CODE.toString();

		try {
			if (projectCode == null) {
				throw new ESLServiceFault("The project code provided in "+METHOD_NAME+" is null.");
			}

			checkAuth(saml, null, projectCode, METHOD_NAME);

			return dao.getProject(projectCode);
		}
		catch(DAOException ex){
			sLog.info(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw new ESLServiceFault(ex);
		}
		catch(RuntimeException ex){
			sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw ex;
		}
	}

	public long saveProject(Project project, String saml)
	throws RemoteException, ESLDuplicateObjectFault, ESLServiceFault, ESLOutOfDateFault,
	NotAuthorisedFault {

		final String METHOD_NAME = RBACAction.ACTION_ESL_SAVE_PROJECT.toString();

		try {
			String projectCode = project.getProjectCode();

			if (projectCode == null) {
				throw new ESLServiceFault("The project code provided in "+METHOD_NAME+" is null.");
			}

			String username = checkAuth(saml, null, projectCode, METHOD_NAME);

			return dao.saveProject(project, username);
		}
		catch(DuplicateObjectException ex) {
			sLog.info(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw new ESLDuplicateObjectFault("A project with the code "+project.getProjectCode()+" already exists in the database.", ex);
		}
		catch(ObjectOutOfDateException ex){
			sLog.info(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw new ESLOutOfDateFault(ex);
		}
		catch(DAOException ex){
			sLog.info(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw new ESLServiceFault(ex);
		}
		catch(RuntimeException ex){
			throw new ESLServiceFault("Problem occurred trying to save Project "+project.getProjectCode(), ex);
		}
	}


	public void setupRandomisation(Project project, Randomisation randomisation, String saml) 
	throws RemoteException, ESLServiceFault, ESLOutOfDateFault, ESLDuplicateObjectFault,
	RandomisationException, NotAuthorisedFault, ConnectException, SocketTimeoutException  {

		final String METHOD_NAME = RBACAction.ACTION_ESL_SETUP_RANDOMISATION.toString();
		try {
			if (project.getProjectCode() == null) {
				throw new ESLServiceFault("The project code provided in "+METHOD_NAME+" is null.");
			}

			String username = checkAuth(saml, null, project.getProjectCode(), METHOD_NAME);
			project.setRandomisation(randomisation);
			dao.saveProject(project, username);

			org.psygrid.esl.model.hibernate.Randomisation rand = randomisation.toHibernate();
			randomiser.saveRandomisation(rand, saml);
		}
		catch(org.psygrid.esl.randomise.RandomisationException ex){
			sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw new RandomisationException("Problem with the randomiser: "+ex.getMessage());
		}
		catch(ObjectOutOfDateException ex){
			sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw new ESLOutOfDateFault(ex);
		}
		catch(DuplicateObjectException ex) {
			sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw new ESLDuplicateObjectFault("A randomisation with the name "+randomisation.getName()+" already exists.", ex);
		}
		catch(DAOException ex){
			sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw new ESLServiceFault(ex);
		}
		catch(RuntimeException ex){
			sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw ex;
		}
	}

	public long saveSubject(Subject subject, String saml) throws RemoteException, 
	ESLOutOfDateFault, ESLServiceFault, ESLDuplicateObjectFault, ESLSubjectExistsException,
	NotAuthorisedFault {

		final String METHOD_NAME = RBACAction.ACTION_ESL_SAVE_SUBJECT.toString();

		try {
			String projectCode = subject.getGroup().getProject().getProjectCode();

			if (projectCode == null) {
				throw new ESLServiceFault("The project code provided in "+METHOD_NAME+" is null.");
			}

			String username = checkAuth(saml, subject.getGroup().getCode(), projectCode, METHOD_NAME);

			long id = 0;

			try {
				id = dao.saveSubject(subject.toHibernate(), username);
			}
			catch(RuntimeException ex) {
				throw new ESLServiceFault("Problem occurred trying to save Subject "+subject.getStudyNumber(), ex);
			}

			return id;
		}
		catch (SubjectExistsException ex) {
			sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw new ESLSubjectExistsException(ex.getMessage(), ex);
		}
		catch (DuplicateObjectException ex) {
			sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw new ESLDuplicateObjectFault(ex);
		}
		catch(ObjectOutOfDateException ex){
			sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw new ESLOutOfDateFault(ex);
		}
		catch(DAOException ex){
			sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw new ESLServiceFault(ex);
		}
		catch(RuntimeException ex){
			sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw new ESLServiceFault(ex);
		}
	}

	public void randomiseSubject(Subject subject, CustomEmailInfo customInfo, String saml) 
	throws RemoteException, ESLServiceFault, RandomisationException, 
	NotAuthorisedFault, ConnectException, SocketTimeoutException  {

		if(customInfo.getSite() == null){
			sLog.info("The site info is null!");
		}
		
		final String METHOD_NAME = RBACAction.ACTION_ESL_RANDOMISE_SUBJECT.toString();

		try {
			String projectCode = subject.getGroup().getProject().getProjectCode();

			if (projectCode == null) {
				throw new ESLServiceFault("The project code provided in "+METHOD_NAME+" is null.");
			}

			checkAuth(saml, subject.getGroup().getCode(), projectCode, METHOD_NAME);

			//check subject has been saved.
			ISubject hSubject   = dao.getSubject(subject.getGroup().getProject(), subject.getStudyNumber()).toHibernate();

			if (hSubject == null) {
				throw new ESLServiceFault("Subject "+subject.getStudyNumber()+" not found. Has it been saved?");
			}
			System.out.println("Getting randomisation");
			IRandomisation rand = null;
			try {
				rand = hSubject.getGroup().getProject().getRandomisation();
			}
			catch(NullPointerException npe) {
				//No randomisation has been setup, so do nothing.
				throw new ESLServiceFault("No randomisation has been setup for subject "+ subject.getStudyNumber(), npe);
			}
			//Allocate a treatment to the subject if the project uses randomisation
			String treatment = null;
			if (rand == null) {
				throw new ESLServiceFault("No randomisation has been setup for subject "+ subject.getStudyNumber());
			}
			else {
				try {
					System.out.println("Allocating treatment");
					treatment = randomiser.allocateTreatment(rand, hSubject, customInfo, saml);
				}
				catch(StrataAllocationFault ex){
					sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
					throw new RandomisationException ("Problem occurred when allocating the strata values for subject "+subject.getStudyNumber(), ex);
				}
				catch(org.psygrid.esl.randomise.RandomisationException ex){
					sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
					throw new RandomisationException ("Problem occurred during randomisation of the subject "+subject.getStudyNumber(), ex);
				}
				//this should be caught by the RemoteRandomiser
				if (treatment == null) {
					throw new ESLServiceFault("No treatment allocated to subject ("+subject.getStudyNumber()+") by randomization");
				}
			}
		}
		catch(NullPointerException npe) {
			sLog.error(METHOD_NAME+": "+npe.getClass().getSimpleName(),npe);
			throw new ESLServiceFault("Unable to find randomisation details in the ESL for the subject "+subject.getStudyNumber()+". "+npe.getCause(), npe);
		}
		catch(DAOException ex){
			sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw new ESLServiceFault(ex);
		}
		catch(RuntimeException ex){
			sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw ex;
		}

	}


	public Subject retrieveSubject(long subjectId, String saml) throws RemoteException, 
	ESLServiceFault, NotAuthorisedFault, ESLSubjectLockedFault {

		final String METHOD_NAME = RBACAction.ACTION_ESL_RETRIEVE_SUBJECT.toString();

		try {
			Subject subject = dao.getSubject(subjectId);

			if (subject == null) {
				throw new ESLServiceFault("No subject with the id "+subjectId+" was found in the ESL");
			}
			if (subject.getGroup() == null) {
				throw new ESLServiceFault("No group was found for subject "+subjectId);
			}

			String groupCode = subject.getGroup().getCode();

			String projectCode = subject.getGroup().getProject().getProjectCode();

			checkAuth(saml, groupCode, projectCode, METHOD_NAME);

			return subject;
		}
		catch(DAOException ex){
			sLog.info(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw new ESLServiceFault(ex);
		}
		catch(RuntimeException ex){
			sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw ex;
		}
	}


	public Subject retrieveSubjectByStudyNumber(Project project, String studyNumber, 
			String saml) 
	throws RemoteException, ESLServiceFault, NotAuthorisedFault, ESLSubjectNotFoundFault {

		final String METHOD_NAME = RBACAction.ACTION_ESL_RETRIEVE_SUBJECT_BY_STUDY_NUMBER.toString();

		try {
			String projectCode = project.getProjectCode();

			if (projectCode == null) {
				throw new ESLServiceFault("The project code provided in "+METHOD_NAME+" is null.");
			}

			// Changed to retrieve the subject even if it is locked as the client
			// should be able to view the data in this case (although not edit it).
			Subject subject = dao.getSubjectEvenIfLocked(project, studyNumber);

			checkAuth(saml, subject.getGroup().getCode(), projectCode, METHOD_NAME);

			return subject;
		}
		catch(NoResultsFoundException ex){
			sLog.info(METHOD_NAME+": "+ex.getClass().getSimpleName()+". Problem was: "+ex.getMessage());
			throw new ESLSubjectNotFoundFault(ex);
		}
		catch(RuntimeException ex){
			sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw ex;
		}
	}

	public String lookupStudyNumber(Project project, Subject exampleSubject, String saml)
	throws RemoteException, ESLServiceFault, NotAuthorisedFault {

		final String METHOD_NAME = RBACAction.ACTION_ESL_LOOKUP_STUDY_NUMBER.toString();

		try {
			String projectCode = project.getProjectCode();

			if (projectCode == null) {
				throw new ESLServiceFault("The project code provided in "+METHOD_NAME+" is null.");
			}

			//authenticates the user against their role, rather than passing in the groupcode to check
			checkAuth(saml, null, projectCode, METHOD_NAME);

			//get a list of groups to restrict the search by
			String[] restrictBy = getGroups(project, saml);

			return dao.getStudyNumber(project, exampleSubject, restrictBy);
		}
		catch(DAOException ex){
			sLog.info(METHOD_NAME+": "+ex.getClass().getSimpleName()+". Problem was: "+ex.getMessage());
			throw new ESLServiceFault(ex);
		}
		catch(RuntimeException ex){
			sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw ex;
		}
	}

	public Subject[] findSubjectByExample(Project project, Subject exampleSubject, 
			String saml) 
	throws RemoteException, ESLServiceFault, NotAuthorisedFault {

		final String METHOD_NAME = RBACAction.ACTION_ESL_FIND_SUBJECT_BY_EXAMPLE.toString();

		try {
			String projectCode = project.getProjectCode();

			if (projectCode == null) {
				throw new ESLServiceFault("The project code provided in "+METHOD_NAME+" is null.");
			}

			checkAuth(saml, null, projectCode, METHOD_NAME);

			//get a list of groups to restrict the search by
			String[] restrictBy = getGroups(project, saml);

			return dao.findSubjectByExample(project, exampleSubject, restrictBy);
		}
		catch(DAOException ex){
			sLog.info(METHOD_NAME+": "+ex.getClass().getSimpleName()+". Problem was: "+ex.getMessage());
			throw new ESLServiceFault(ex);
		}
		catch(RuntimeException ex){
			sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw ex;
		}
	}

	/**
	 * Retrieve the randomisation result for the specified subject.
	 * @return treatment allocation
	 */
	public String lookupRandomisationResult(Project project, String studyNumber, 
			String saml) 
	throws RemoteException, ESLServiceFault, RandomisationException, NotAuthorisedFault, ConnectException, SocketTimeoutException  {

		final String METHOD_NAME = RBACAction.ACTION_ESL_LOOKUP_RANDOMISATION_RESULT.toString();

		try {
			String projectCode = project.getProjectCode();

			if (projectCode == null) {
				throw new ESLServiceFault("The project code provided in "+METHOD_NAME+" is null.");
			}

			Subject s = null;
			//retrieve subject details to get the group code for authorisation
			try {
				s = dao.getSubject(project, studyNumber);
			}
			catch (DAOException ex) {
				throw new ESLServiceFault("Unable to lookup the details in the ESL for subject "+studyNumber, ex);
			}
			if (s == null) {
				throw new ESLServiceFault("No subject with the study number: "+studyNumber+" found in the ESL");
			}

			checkAuth(saml, s.getGroup().getCode(), projectCode, METHOD_NAME);

			return randomiser.getAllocation(project.getRandomisation().getName(), s.toHibernate(), saml);
		}
		catch(org.psygrid.esl.randomise.RandomisationException ex){
			sLog.info(METHOD_NAME+": "+ex.getClass().getSimpleName()+". Problem was: "+ex.getMessage());
			throw new RandomisationException ("Problem with the randomisation service when looking up randomisation result for "+studyNumber, ex);
		}
		catch(RuntimeException ex){
			sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw ex;
		}
	}

	public boolean exists(Project project, String studyNumber, String saml) 
	throws RemoteException, ESLServiceFault, NotAuthorisedFault {

		final String METHOD_NAME = RBACAction.ACTION_ESL_EXISTS.toString();

		try {
			String projectCode = project.getProjectCode();

			if (projectCode == null) {
				throw new ESLServiceFault("The project code provided in "+METHOD_NAME+" is null.");
			}

			checkAuth(saml, null, projectCode, METHOD_NAME);

			return dao.exists(project, studyNumber);
		}
		catch(DAOException ex){
			sLog.info(METHOD_NAME+": "+ex.getClass().getSimpleName()+". Problem was: "+ex.getMessage());
			throw new ESLServiceFault(ex);
		}
		catch(RuntimeException ex){
			sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw ex;
		}
	}


	public String[][] emergencyBreakIn(Project project, String saml) throws 
	RemoteException, RandomisationException, ESLServiceFault, NotAuthorisedFault, ConnectException, SocketTimeoutException  {

		final String METHOD_NAME = RBACAction.ACTION_ESL_EMERGENCY_BREAK_IN.toString();

		if (project.getProjectCode() == null) {
			throw new ESLServiceFault("The project code provided in "+METHOD_NAME+" is null.");
		}

		try {
			String projectCode = project.getProjectCode();

			checkAuth(saml, null, projectCode, METHOD_NAME);

			return randomiser.getAllAllocations(project.toHibernate(), saml);
		}
		catch(org.psygrid.esl.randomise.RandomisationException ex){
			sLog.info(METHOD_NAME+": "+ex.getClass().getSimpleName()+". Problem was: "+ex.getMessage());
			throw new RandomisationException ("Problem with the randomisation service when looking up randomisation results.", ex);
		}
		catch(RuntimeException ex){
			sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw ex;
		}

	}
	
	public Calendar[] retrieveSubjectRandomisationEvents(String projectCode, String studyNumber, String saml) 
		throws RemoteException, RandomisationException, NotAuthorisedFault, ConnectException, SocketTimeoutException  {

		final String METHOD_NAME = RBACAction.ACTION_ESL_RETRIEVE_SUBJECT_RANDOMISATION_EVENTS.toString();

		try {
			checkAuth(saml, null, projectCode, METHOD_NAME);

			return randomiser.getSubjectRandomisationEvents(projectCode, studyNumber, saml);
		}
		catch(org.psygrid.esl.randomise.RandomisationException ex){
			throw new RandomisationException(ex);
		}
		catch(RuntimeException ex){
			sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw ex;
		}
	}

	public boolean isProjectRandomised(String projectCode, String saml) 
		throws RemoteException, ESLServiceFault, NotAuthorisedFault {

		final String METHOD_NAME = RBACAction.ACTION_ESL_IS_PROJECT_RANDOMISED.toString();

		try {
			checkAuth(saml, null, projectCode, METHOD_NAME);
			
			return dao.isRandomised(projectCode);
		}
		catch(DAOException ex){
			throw new ESLServiceFault(ex);
		}
		catch(RuntimeException ex){
			sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw ex;
		}
	}
	
	public boolean isEslProject(String projectCode, String saml) 
	throws RemoteException, ESLServiceFault, NotAuthorisedFault {

	final String METHOD_NAME = RBACAction.ACTION_ESL_IS_PROJECT_RANDOMISED.toString();

	try {
		checkAuth(saml, null, projectCode, METHOD_NAME);
		
		return dao.isEslProject(projectCode);
	}
	catch(DAOException ex){
		throw new ESLServiceFault(ex);
	}
	catch(RuntimeException ex){
		sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
		throw ex;
	}
}	

    public String[][] lookupRandomizerStatistics(Project project, String saml) throws RemoteException, 
    RandomisationException, ESLServiceFault, NotAuthorisedFault, ConnectException, SocketTimeoutException  {

        final String METHOD_NAME = RBACAction.ACTION_ESL_LOOKUP_RANDOMIZER_STATISTICS.toString();

        if (project.getProjectCode() == null) {
            throw new ESLServiceFault("The project code provided in "+METHOD_NAME+" is null.");
        }

        try {
            String projectCode = project.getProjectCode();

            checkAuth(saml, null, projectCode, METHOD_NAME);

            return randomiser.getRandomizerStatistics(project.toHibernate(), saml);
        }
        catch(org.psygrid.esl.randomise.RandomisationException ex){
            sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
            throw new RandomisationException ("Problem with the randomisation service when looking up randomisation results.", ex);
        }
        catch(RuntimeException ex){
            sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
            throw ex;
        }

    }

    public StrataStats[] lookupStratifiedRandomizerStatistics(Project project, String saml) throws RemoteException, 
    RandomisationException, ESLServiceFault, NotAuthorisedFault, ConnectException, SocketTimeoutException  {

        final String METHOD_NAME = RBACAction.ACTION_ESL_LOOKUP_STRATIFIED_RANDOMIZER_STATISTICS.toString();

        if (project.getProjectCode() == null) {
            throw new ESLServiceFault("The project code provided in "+METHOD_NAME+" is null.");
        }

        try {
            String projectCode = project.getProjectCode();

            checkAuth(saml, null, projectCode, METHOD_NAME);

            Strata[] strata = dao.getAllStrata(project.getRandomisation().getId());
            
            List<Parameter[]> combinations = new ArrayList<Parameter[]>();
            
            int nComb = 1;
            for ( Strata s: strata ){
                nComb*=s.getValues().length;
            }
            int[] counters = new int[strata.length];
            for ( int i=0; i<nComb; i++ ){
                Parameter[] params = new Parameter[strata.length];
                for ( int j=0; j<strata.length; j++ ){
                    Parameter p = new Parameter();
                    p.setKey(strata[j].getName());
                    p.setValue(strata[j].getValues()[counters[j]]);
                    params[j] = p;
                }
                combinations.add(params);
                boolean incNext = true;
                for ( int j=counters.length-1; j>=0; j-- ){
                    if ( incNext ){
                        counters[j]++;
                        if ( counters[j] > (strata[j].getValues().length-1) ){
                            counters[j] = 0;
                            incNext = true;
                        }
                        else{
                            incNext = false;
                        }
                    }
                }
            }
            
            StrataStats[] allStats = new StrataStats[combinations.size()];
            for ( int i=0; i<combinations.size(); i++ ){
                Parameter[] params = combinations.get(i);
                StrataStats stats = new StrataStats();
                String[][] s = new String[params.length][2];
                for ( int j=0; j<params.length; j++ ){
                    s[j][0] = params[j].getKey();
                    s[j][1] = params[j].getValue();
                }
                stats.setStrata(s);
                stats.setStats(randomiser.getRandomizerStatistics(project.toHibernate(), params, saml));
                allStats[i] = stats;
            }
            
            return allStats;
        }
        catch(DAOException ex){
            sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
            throw new ESLServiceFault(ex);
        }
        catch(org.psygrid.esl.randomise.RandomisationException ex){
            sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
            throw new RandomisationException ("Problem with the randomisation service when looking up randomisation results.", ex);
        }
        catch(RuntimeException ex){
            sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
            throw ex;
        }

    }

   public String[] lookupRandomisationResultForDate(String projectCode, String studyNumber, Calendar date, String saml) 
   throws RemoteException, RandomisationException, ESLServiceFault, NotAuthorisedFault, ConnectException, SocketTimeoutException  {
       final String METHOD_NAME = RBACAction.ACTION_ESL_LOOKUP_RANDOMISATION_RESULT_FOR_DATE.toString();

       if (projectCode == null) {
           throw new ESLServiceFault("The project code provided in "+METHOD_NAME+" is null.");
       }

       try {
           checkAuth(saml, null, projectCode, METHOD_NAME);
           Randomisation rand = dao.getProject(projectCode).getRandomisation();
           String rdmzrName = "";
           if (rand != null) {
        	   rdmzrName = rand.getName();
           }
           return randomiser.getRandomisationResultForDate(rdmzrName, studyNumber, date, saml);
       }
       catch(DAOException ex){
           sLog.info(METHOD_NAME+": "+ex.getClass().getSimpleName()+" "+ex.getMessage());
           throw new ESLServiceFault(ex);
       }
       catch(org.psygrid.esl.randomise.RandomisationException ex){
           sLog.info(METHOD_NAME+": "+ex.getClass().getSimpleName()+" "+ex.getMessage());
           throw new RandomisationException ("Problem with the randomisation service when looking up randomisation results.", ex);
       }
       catch(RuntimeException ex){
           sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
           throw ex;
       }
   }
    
    public String[][] retrieveNhsNumbers(String projectCode, String[] studyNumbers, String saml) 
    throws RemoteException, ESLServiceFault, NotAuthorisedFault {
		final String METHOD_NAME = RBACAction.ACTION_ESL_RETRIEVE_NHS_NUMBERS.toString();

		if (projectCode == null) {
			throw new ESLServiceFault("The project code provided in "+METHOD_NAME+" is null.");
		}

		try {
			checkAuth(saml, null, projectCode, METHOD_NAME);

			return dao.findNhsNumbers(projectCode, studyNumbers);
		}
		catch(DAOException ex){
			sLog.info(METHOD_NAME+": "+ex.getClass().getSimpleName()+". Problem was: "+ex.getMessage());
			throw new ESLServiceFault(ex);
		}
		catch(RuntimeException ex){
			sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw ex;
		}
    }

    
    private String checkAuth(String saml, String groupCode, String projectCode, 
			String METHOD_NAME) 
	throws NotAuthorisedFault {

		String userName = findUserName(saml);
		
		String callerIdentity = accessControl.getCallersIdentity();
		logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, userName, callerIdentity);

		try {
			GroupType groupType = null;

			if (groupCode == null) {
				groupType = new GroupType();
			}
			else {
				groupType = new GroupType(null, groupCode, null);
			}

			if ( !accessControl.authoriseUser(saml, new AEFGroup(groupType.getName(), groupType.getIdCode(), groupType.getParent()),
					RBACAction.valueOf(METHOD_NAME).toAEFAction(), 
					new AEFProject(null, projectCode, false) ) ){
				logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, userName, callerIdentity);
				throw new NotAuthorisedFault("User '"+userName+"' is not authorised to perform the action '"+METHOD_NAME+"' for project '"+projectCode+"'");
			}
		}
		catch(PGSecurityInvalidSAMLException ex){
			sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw new NotAuthorisedFault("The supplied SAML assertion has expired", ex);
		}
		catch(PGSecuritySAMLVerificationException ex){
			sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw new NotAuthorisedFault("The supplied SAML assertion does not come from a trusted issuer", ex);
		}
		catch(PGSecurityException ex){
			sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw new NotAuthorisedFault("An error occurred during authorisation", ex);
		}

		return userName;
	}

	private String[] getGroups(Project project, String saml) throws NotAuthorisedFault {

		SAMLAssertion sa = null;
		String[] groupCode = null;
		try {
			sa = SAMLUtilities.retrieveSAMLAssertion(saml);
		}
		catch(PGSecurityException ex){
			sLog.error("getGroups"+": "+ex.getClass().getSimpleName(),ex);
			throw new NotAuthorisedFault("An error occurred during authorisation", ex);
		}

		try {
			ProjectType projectType = new ProjectType(null, project.getProjectCode(), null, null, false); 
			PrivilegeType[] privilegeType = SAMLUtilities.getUsersPrivilegesInProjectFromST(sa, projectType);
			groupCode = new String[privilegeType.length];

			int i = 0;
			for (PrivilegeType p: privilegeType) {
				if (p.getGroup() != null) {
					groupCode[i] = p.getGroup().getIdCode();
					i++;
				}
			}

			return groupCode;
		}
		catch(NullPointerException npe) {
			return null;
		}
	}

	public void lockSubject(String identifier, String saml) throws RemoteException, ESLServiceFault, ESLSubjectNotFoundFault, NotAuthorisedFault {
		final String METHOD_NAME = RBACAction.ACTION_ESL_LOCK_SUBJECT.toString();

		if (identifier == null) {
			throw new ESLServiceFault("The identifier provided in "+METHOD_NAME+" is null.");
		}

		try {
			String projectCode = IdentifierHelper.getProjectCodeFromIdentifier(identifier);
			String groupCode = IdentifierHelper.getGroupCodeFromIdentifier(identifier);
			String username = checkAuth(saml, groupCode, projectCode, METHOD_NAME);
			//TODO this is a very inefficient way of locking the subject!
			//But trying to do it in one session was causing problems with
			//the provenance system, and as this operation will be infrequently
			//used I don't think it is too critical
			Project p = dao.getProject(projectCode);
			try{
				org.psygrid.esl.model.hibernate.Subject s = dao.getSubject(p, identifier).toHibernate();
				s.setLocked(true);
				dao.saveSubject(s, username);
			}
			catch(SubjectLockedException sle){
				//do nothing - subject is already locked
			}
		}
		catch(NoResultsFoundException ex){
			sLog.info(METHOD_NAME+": "+ex.getClass().getSimpleName()+" "+ex.getMessage());
			throw new ESLSubjectNotFoundFault(ex);
		}
		catch(InvalidIdentifierException ex){
			sLog.info(METHOD_NAME+": "+ex.getClass().getSimpleName()+" "+ex.getMessage());
			throw new ESLServiceFault(ex);
		}
		catch(DAOException ex){
			sLog.info(METHOD_NAME+": "+ex.getClass().getSimpleName()+" "+ex.getMessage());
			throw new ESLServiceFault(ex);
		}
		catch(RuntimeException ex){
			sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw ex;
		}
	}

	public void unlockSubject(String identifier, String saml) throws RemoteException, ESLServiceFault, ESLSubjectNotFoundFault, NotAuthorisedFault {
		final String METHOD_NAME = RBACAction.ACTION_ESL_UNLOCK_SUBJECT.toString();

		if (identifier == null) {
			throw new ESLServiceFault("The identifier provided in "+METHOD_NAME+" is null.");
		}

		try {
			String projectCode = IdentifierHelper.getProjectCodeFromIdentifier(identifier);
			String groupCode = IdentifierHelper.getGroupCodeFromIdentifier(identifier);
			String username = checkAuth(saml, groupCode, projectCode, METHOD_NAME);
			//TODO this is a very inefficient way of unlocking the subject!
			//But trying to do it in one session was causing problems with
			//the provenance system, and as this operation will be infrequently
			//used I don't think it is too critical
			Project p = dao.getProject(projectCode);
			org.psygrid.esl.model.hibernate.Subject s = dao.getSubjectEvenIfLocked(p, identifier).toHibernate();
			if ( s.isLocked() ){
				s.setLocked(false);
				dao.saveSubject(s, username);
			}
		}
		catch(InvalidIdentifierException ex){
			sLog.info(METHOD_NAME+": "+ex.getClass().getSimpleName()+" "+ex.getMessage());
			throw new ESLServiceFault(ex);
		}
		catch(NoResultsFoundException ex){
			sLog.info(METHOD_NAME+": "+ex.getClass().getSimpleName()+" "+ex.getMessage());
			throw new ESLSubjectNotFoundFault(ex);
		}
		catch(DAOException ex){
			sLog.info(METHOD_NAME+": "+ex.getClass().getSimpleName()+" "+ex.getMessage());
			throw new ESLServiceFault(ex);
		}
		catch(RuntimeException ex){
			sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw ex;
		}
	}
	
	public void deleteSubject(String identifier, String saml) throws RemoteException, ESLServiceFault, NotAuthorisedFault {
		final String METHOD_NAME = RBACAction.ACTION_ESL_DELETE_SUBJECT.toString();
		
		try {
			String projectCode = IdentifierHelper.getProjectCodeFromIdentifier(identifier);
			String groupCode = IdentifierHelper.getGroupCodeFromIdentifier(identifier);
			checkAuth(saml, groupCode, projectCode, METHOD_NAME);
			dao.deleteSubject(identifier);
		}
		catch(InvalidIdentifierException ex){
			sLog.info(METHOD_NAME+": "+ex.getClass().getSimpleName()+" "+ex.getMessage());
			throw new ESLServiceFault(ex);
		} catch(DAOException ex){
			sLog.info(METHOD_NAME+": "+ex.getClass().getSimpleName()+" "+ex.getMessage());
			throw new ESLServiceFault(ex);
		} 
	}

	public String getProperty(String identifier, String property, String saml) throws RemoteException, ESLServiceFault, NotAuthorisedFault {
		final String METHOD_NAME = RBACAction.ACTION_ESL_GET_PROPERTY.toString();

		if (identifier == null) {
			throw new ESLServiceFault("The identifier provided in "+METHOD_NAME+" is null.");
		}

		try {
			String projectCode = IdentifierHelper.getProjectCodeFromIdentifier(identifier);
			String groupCode = IdentifierHelper.getGroupCodeFromIdentifier(identifier);
			checkAuth(saml, groupCode, projectCode, METHOD_NAME);
			return dao.getValueOfProperty(identifier, property);
		}
		catch(InvalidIdentifierException ex){
			sLog.info(METHOD_NAME+": "+ex.getClass().getSimpleName()+" "+ex.getMessage());
			throw new ESLServiceFault(ex);
		}
		catch(DAOException ex){
			sLog.info(METHOD_NAME+": "+ex.getClass().getSimpleName()+" "+ex.getMessage());
			throw new ESLServiceFault(ex);
		}
		catch(RuntimeException ex){
			sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw ex;
		}
	}

	public void deleteProject(long projectId, String projectCode, String saml) throws RemoteException, ESLServiceFault, NotAuthorisedFault {
		final String METHOD_NAME = RBACAction.ACTION_ESL_DELETE_PROJECT.toString();

		try {

			if (projectCode == null) {
				throw new ESLServiceFault("The project code provided in "+METHOD_NAME+" is null.");
			}

			String username = checkAuth(saml, null, projectCode, METHOD_NAME);

			dao.deleteProject(projectId, projectCode);
		}
		catch(DAOException ex){
			sLog.info(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw new ESLServiceFault(ex);
		}
		catch(RuntimeException ex){
			throw new ESLServiceFault("Problem occurred trying to delete Project "+projectCode, ex);
		}
	}

	public void handleConsentWithdrawn(String identifier, String saml) throws RemoteException, ESLServiceFault, NotAuthorisedFault {
		if(willSubjectBeDeletedWhenConsentIsWithdrawn(identifier, saml)) {
    		deleteSubject(identifier, saml);
    	} else {
    		lockSubject(identifier, saml);
    	}		
	}

	public boolean willSubjectBeDeletedWhenConsentIsWithdrawn(
			String identifier, String saml) throws RemoteException,
			ESLServiceFault, NotAuthorisedFault {
		String projectCode = IdentifierHelper.getProjectCodeFromIdentifier(identifier);
		boolean isOkToDeleteEslData = false;
		try {
			isOkToDeleteEslData = dao.getProject(projectCode).getOkToDeleteEslData();
		} catch (DAOException ex) {
			throw new ESLServiceFault(ex);
		}
    	if(!isProjectRandomised(projectCode, saml) && isOkToDeleteEslData) {
    		return true;
    	}
    	return false;
	}


	
	public String allocateMedicationPackage(String projectCode,
			String centreCode, String participantIdentifier, String saml)
			throws RemoteException, ESLServiceFault, NotAuthorisedFault {
		String medsPackageId = null;
		//First thing to do is to get the system saml.
		try {
			String systemSAML = aaqc.getSystemSAMLAssertion();
			Project project = retrieveProjectByCode(projectCode, saml);
			String result = this.lookupRandomisationResult(project, participantIdentifier, systemSAML);
			org.psygrid.esl.model.hibernate.Project hProj = project.toHibernate();
			Map<String, String> treatmentCodes = hProj.getRandomisation().getTreatments();
			Set<String> keys =treatmentCodes.keySet();
			
			String treatmentCode = null;
			
			for(String key : keys){
				if(treatmentCodes.get(key).equals(result)){
					treatmentCode = key;
					break;
				}
			}
			
			if(treatmentCode != null){
				//This is where we would call the allocation method on the Meds-distribution client.
				MedicationClient medCli = new MedicationClient();
				medsPackageId = medCli.allocateInitialMedicationPackage(projectCode, centreCode, treatmentCode, participantIdentifier, saml);
			}else{
				sLog.error("allocateMedicationPackage: returned treatment code was null.");
				throw new ESLServiceFault("allocateMedicationPackage: returned treatment code was null.");
			}
			
		} catch (Exception e) {
			sLog.error("allocateMedicationPackage: unexpected exception", e);
			throw new ESLServiceFault("allocateMedicationPackage problem.");
		} 
		return medsPackageId;
	}

	public void groupAdded(String projectCode, String code, String name) {
		dao.addedGroup(projectCode,code,name);
	}

	public void groupUpdated(String projectCode, String groupCode, String newCode, String newName) {
		dao.updatedGroup(projectCode, groupCode, newCode, newName);
	}

	public void groupDeleted(String projectCode, String groupCode) {
		dao.deletedGroup(projectCode,groupCode);
	}

	
	
}
