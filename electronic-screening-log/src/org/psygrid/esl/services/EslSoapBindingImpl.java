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
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.xml.rpc.ServiceException;

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
import org.psygrid.esl.model.IProject;
import org.psygrid.esl.model.IRandomisation;
import org.psygrid.esl.model.ISubject;
import org.psygrid.esl.model.StrataAllocationFault;
import org.psygrid.esl.model.dto.Project;
import org.psygrid.esl.model.dto.Randomisation;
import org.psygrid.esl.model.dto.Subject;
import org.psygrid.esl.model.hibernate.CustomEmailInfo;
import org.psygrid.esl.randomise.StrataStats;
import org.psygrid.esl.randomise.IRemoteRandomiser;
import org.psygrid.esl.util.Pair;
import org.psygrid.logging.AuditLogger;
import org.psygrid.meds.rmi.MedicationClient;
import org.psygrid.randomization.Parameter;
import org.psygrid.security.PGSecurityException;
import org.psygrid.security.PGSecurityInvalidSAMLException;
import org.psygrid.security.PGSecuritySAMLVerificationException;
import org.psygrid.security.RBACAction;
import org.psygrid.security.utils.PropertyUtilities;
import org.springframework.context.ApplicationContext;

/**
 * 
 * 
 * @author Lucy Bridges
 *
 */
public class EslSoapBindingImpl extends SecureSoapImpl implements Esl {


	/**
	 * Transactional service implementation from application context.
	 */
	Esl service = null;
	
	
	protected void onInit() throws ServiceException {
		super.onInit();
		ApplicationContext ctx = getWebApplicationContext();
		service = (Esl)ctx.getBean("eslService");
	}

	
	public void destroy() {
		super.destroy();
	}

	public String getVersion() throws RemoteException {
		return service.getVersion();
	}


	public Project retrieveProject(long projectId, String saml) 
	throws RemoteException, ESLServiceFault, NotAuthorisedFault {
		return service.retrieveProject(projectId, saml);
	}

	public Project retrieveProjectByCode(String projectCode, String saml)
	throws RemoteException, ESLServiceFault, NotAuthorisedFault {
		return service.retrieveProjectByCode(projectCode, saml);
	}

	public long saveProject(Project project, String saml)
	throws RemoteException, ESLDuplicateObjectFault, ESLServiceFault, ESLOutOfDateFault,
	NotAuthorisedFault {
		return service.saveProject(project, saml);
	}


	public void setupRandomisation(Project project, Randomisation randomisation, String saml) 
	throws RemoteException, ESLServiceFault, ESLOutOfDateFault, ESLDuplicateObjectFault,
	RandomisationException, NotAuthorisedFault, ConnectException, SocketTimeoutException  {
		service.setupRandomisation(project, randomisation, saml);
	}

	public long saveSubject(Subject subject, String saml) throws RemoteException, 
	ESLOutOfDateFault, ESLServiceFault, ESLDuplicateObjectFault, ESLSubjectExistsException,
	NotAuthorisedFault {
		return service.saveSubject(subject, saml);
	}

	public void randomiseSubject(Subject subject, CustomEmailInfo customInfo, String saml) 
	throws RemoteException, ESLServiceFault, RandomisationException, 
	NotAuthorisedFault, ConnectException, SocketTimeoutException  {
		service.randomiseSubject(subject, customInfo, saml);
	}


	public Subject retrieveSubject(long subjectId, String saml) throws RemoteException, 
	ESLServiceFault, NotAuthorisedFault, ESLSubjectLockedFault {
		return service.retrieveSubject(subjectId, saml);
	}


	public Subject retrieveSubjectByStudyNumber(Project project, String studyNumber, 
			String saml) 
	throws RemoteException, ESLServiceFault, NotAuthorisedFault, ESLSubjectLockedFault, ESLSubjectNotFoundFault {
		return service.retrieveSubjectByStudyNumber(project, studyNumber, saml);
	}

	public String lookupStudyNumber(Project project, Subject exampleSubject, String saml)
	throws RemoteException, ESLServiceFault, NotAuthorisedFault {
		return service.lookupStudyNumber(project, exampleSubject, saml);
	}

	public Subject[] findSubjectByExample(Project project, Subject exampleSubject, 
			String saml) 
	throws RemoteException, ESLServiceFault, NotAuthorisedFault {
		return service.findSubjectByExample(project, exampleSubject, saml);
	}

	public String lookupRandomisationResult(Project project, String studyNumber, 
			String saml) 
	throws RemoteException, ESLServiceFault, RandomisationException, NotAuthorisedFault, ConnectException, SocketTimeoutException  {
		return service.lookupRandomisationResult(project, studyNumber, saml);
	}

	public boolean exists(Project project, String studyNumber, String saml) 
	throws RemoteException, ESLServiceFault, NotAuthorisedFault {
		return service.exists(project, studyNumber, saml);
	}


	public String[][] emergencyBreakIn(Project project, String saml) throws 
	RemoteException, RandomisationException, ESLServiceFault, NotAuthorisedFault, ConnectException, SocketTimeoutException  {
		return service.emergencyBreakIn(project, saml);
	}
	
	public Calendar[] retrieveSubjectRandomisationEvents(String projectCode, String studyNumber, String saml) 
		throws RemoteException, RandomisationException, NotAuthorisedFault, ConnectException, SocketTimeoutException  {
		return service.retrieveSubjectRandomisationEvents(projectCode, studyNumber, saml);
	}

	public boolean isProjectRandomised(String projectCode, String saml) 
		throws RemoteException, ESLServiceFault, NotAuthorisedFault, ConnectException,SocketTimeoutException {
		return service.isProjectRandomised(projectCode, saml);
	}
	
	public boolean isEslProject(String projectCode, String saml) 
	throws RemoteException, ESLServiceFault, NotAuthorisedFault {
		return service.isEslProject(projectCode, saml);
	}	

    public String[][] lookupRandomizerStatistics(Project project, String saml) throws RemoteException, 
    RandomisationException, ESLServiceFault, NotAuthorisedFault, ConnectException, SocketTimeoutException  {
    	return service.lookupRandomizerStatistics(project, saml);
    }

    public StrataStats[] lookupStratifiedRandomizerStatistics(Project project, String saml) throws RemoteException, 
    RandomisationException, ESLServiceFault, NotAuthorisedFault, ConnectException, SocketTimeoutException  {
    	return service.lookupStratifiedRandomizerStatistics(project, saml);
    }

   public String[] lookupRandomisationResultForDate(String projectCode, String studyNumber, Calendar date, String saml) 
   throws RemoteException, RandomisationException, ESLServiceFault, NotAuthorisedFault, ConnectException, SocketTimeoutException  {
	   return service.lookupRandomisationResultForDate(projectCode, studyNumber, date, saml);
   }
    
    public String[][] retrieveNhsNumbers(String projectCode, String[] studyNumbers, String saml) 
    throws RemoteException, ESLServiceFault, NotAuthorisedFault {
    	return service.retrieveNhsNumbers(projectCode, studyNumbers, saml);
    }

    
	public void lockSubject(String identifier, String saml) throws RemoteException, ESLServiceFault, ESLSubjectNotFoundFault, NotAuthorisedFault {
		service.lockSubject(identifier, saml);
	}

	public void unlockSubject(String identifier, String saml) throws RemoteException, ESLServiceFault, ESLSubjectNotFoundFault, NotAuthorisedFault {
		service.unlockSubject(identifier, saml);
	}
	
	public void deleteSubject(String identifier, String saml) throws RemoteException, ESLServiceFault, NotAuthorisedFault {
		service.deleteSubject(identifier, saml);
	}

	public String getProperty(String identifier, String property, String saml) throws RemoteException, ESLServiceFault, NotAuthorisedFault {
		return service.getProperty(identifier, property, saml);
	}

	public void deleteProject(long projectId, String projectCode, String saml) throws RemoteException, ESLServiceFault, NotAuthorisedFault {
		service.deleteProject(projectId, projectCode, saml);
	}

	public void handleConsentWithdrawn(String identifier, String saml)
			throws RemoteException, ESLServiceFault, NotAuthorisedFault {
		service.handleConsentWithdrawn(identifier, saml);		
	}

	public boolean willSubjectBeDeletedWhenConsentIsWithdrawn(
			String identifier, String saml) throws RemoteException,
			ESLServiceFault, NotAuthorisedFault {
		return service.willSubjectBeDeletedWhenConsentIsWithdrawn(identifier, saml);
	}


	public String allocateMedicationPackage(String projectCode,
			String centreCode, String participantIdentifier, String saml)
			throws RemoteException, ESLServiceFault, NotAuthorisedFault {
		return service.allocateMedicationPackage(projectCode, centreCode, participantIdentifier, saml);
	}



}
