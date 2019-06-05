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


package org.psygrid.esl.randomise;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.Calendar;

import org.psygrid.esl.model.ICustomEmailInfo;
import org.psygrid.esl.model.IProject;
import org.psygrid.esl.model.IRandomisation;
import org.psygrid.esl.model.ISite;
import org.psygrid.esl.model.ISubject;
import org.psygrid.esl.model.hibernate.CustomEmailInfo;
import org.psygrid.esl.services.NotAuthorisedFault;
import org.psygrid.randomization.Parameter;

/**
 * Provides an interface to the remote randomisation service.
 * 
 * Also performs transformation between the ESL randomisation classes and 
 * what is expected by the Randomizer web services.
 * 
 * @author Lucy Bridges
 *
 */
public interface IRemoteRandomiser {

	/**
	 * Save the randomisation settings for a project.
	 * 
	 * Parses the randomisation object and sends it to the remote
	 * randomisation service.
	 * 
	 * @param rand
	 * @param saml
	 * @throws RandomisationException
     * @throws NotAuthorisedFault
	 */
	public abstract void saveRandomisation(IRandomisation rand, String saml)
			throws RandomisationException, NotAuthorisedFault, ConnectException, SocketTimeoutException;

	/**
	 * Allocates the treatment for a subject.
	 * 
	 * Gets the strata specified in the randomisation object and retrieves
	 * the subject specific values. This is then sent to the remote
	 * randomisation service, which calculates the treatment to allocate 
	 * the subject. Emails are sent to the relevent roles within the 
	 * project to inform them that this process has taken place. 
	 * 
	 * @param rand
	 * @param subject
	 * @param saml
	 * @return String the code of the allocated treatment 
	 * @throws RandomisationException
     * @throws NotAuthorisedFault
	 */
	public abstract String allocateTreatment(IRandomisation rand, ISubject subject, CustomEmailInfo customEmailInfo, String saml) 
            throws org.psygrid.esl.model.StrataAllocationFault, RandomisationException, NotAuthorisedFault, ConnectException, SocketTimeoutException ;

	/**
	 * Retrieves the treatment allocation for a given subject from the
	 * remote randomisation service.
	 * 
	 * @param rdmzrName
	 * @param subject
	 * @param saml
	 * @return String the allocated treatment arm
	 * @throws RandomisationException
     * @throws NotAuthorisedFault
	 */
	public abstract String getAllocation(String rdmzrName, ISubject subject, String saml) 
            throws RandomisationException, NotAuthorisedFault, ConnectException, SocketTimeoutException ;

	/**
	 * Retrieves all subjects and their allocated treatment for a given randomisation
	 * 
	 * @param project
	 * @param saml
	 * @return list of subjects and their treatment allocations
	 * @throws RandomisationException
     * @throws NotAuthorisedFault
	 */
	public abstract String[][] getAllAllocations(IProject project, String saml) 
		throws RandomisationException, NotAuthorisedFault, ConnectException, SocketTimeoutException ;
    
	/**
	 * Retrieve the dates of all randomization events (i.e treatment allocations)
     * for a given subject.
     * 
	 * @param rdmzrName
	 * @param subjectCode
	 * @param saml
	 * @return dates of randomisations
	 * @throws RandomisationException
	 * @throws NotAuthorisedFault
	 */
	public Calendar[] getSubjectRandomisationEvents(String rdmzrName, String subjectCode, String saml) 
		throws RandomisationException, NotAuthorisedFault, ConnectException, SocketTimeoutException ;
	
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
     * @throws RandomisationException
     * @throws NotAuthorisedFault
     */
    public String[][] getRandomizerStatistics(IProject project, String saml)
        throws RandomisationException, NotAuthorisedFault, ConnectException, SocketTimeoutException ;

    /**
     * Retrieve the randomization statistics for a project, for the given parameters.
     * 
     * @param project
     * @param parameters
     * @param saml
     * @return Array containing the number of allocations for each treatment arm.
     * The structure of the array is that the first array dimension retrieves data
     * for one treatment arm, the second array dimension gets either the treatment 
     * arm name or the number of allocations i.e. result[0][0] will be the name
     * of the first treatment arm; result[0][1] will be the number of allocations
     * for it.
     * @throws RandomisationException
     * @throws NotAuthorisedFault
     */
    public String[][] getRandomizerStatistics(IProject project, Parameter[] parameters, String saml)
        throws RandomisationException, NotAuthorisedFault, ConnectException, SocketTimeoutException ;
    
    /**
     * Get the randomisation event for a subject on a particular date.
     * 
     * @param rdmzrName
     * @param subjectCode
     * @param date
     * @param saml
     * @return randomisation result (id and name)
     * @throws RandomisationException
     * @throws NotAuthorisedFault
     */
    public String[] getRandomisationResultForDate(String rdmzrName, String subjectCode, Calendar date, String saml) 
	throws RandomisationException, NotAuthorisedFault, ConnectException, SocketTimeoutException ;
}