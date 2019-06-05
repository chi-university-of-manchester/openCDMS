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

package org.psygrid.randomization;

import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Date;

import org.psygrid.randomization.dao.DuplicateRandomizerException;
import org.psygrid.randomization.dao.RandomizerDAOException;
import org.psygrid.randomization.dao.UnknownRandomizerException;
import org.psygrid.randomization.model.dto.Randomizer;

/**
 * Web service interface to the PsyGrid Randomization Service.
 * 
 * @author Rob Harper
 *
 */
public interface Randomization extends java.rmi.Remote {

    /**
     * Get the software version of the randomization web-service.
     * 
     * @return The software version.
     * @throws RemoteException
     */
    public String getVersion() throws RemoteException;
    
    /**
     * Allocate a subject to a treatment arm of a clinical trial
     * using the named randomizer and the given parameters.
     * 
     * @param rdmzrName The name of the randomizer to use.
     * @param subject The name/code of the subject being randomized.
     * @param parameters The parameters that will be used during the
     * randomization.
     * @param saml SAML assertion for security system
     * @return The code of the treatment allocation for the subject.
     * @throws RemoteException
     * @throws DuplicateSubjectFault if the subject trying to be randomized as already
     * been allocated to a treatment arm.
     * @throws UnknownRandomizerFault if the named randomizer does not exist.
     * @throws RandomizationFault if an unrecoverable error occurred during randomization.
     */
    public String allocate(String rdmzrName, String subject, Parameter[] parameters, String saml) 
        throws RemoteException, DuplicateSubjectFault, UnknownRandomizerFault, RandomizationFault, NotAuthorisedFault;
    
    /**
     * Retrieve the treatment allocation fpr the given subject, as
     * allocated by the named randomizer.
     * 
     * @param rdmzrName The name of the randomizer to use.
     * @param subject The name/code of the subject whose allocation
     * is to be retrieved.
     * @param saml SAML assertion for security system
     * @return The code of the treatment allocation for the subject.
     * @throws RemoteException
     * @throws UnknownRandomizerFault if the named randomizer does not exist.
     */
    public String getAllocation(String rdmzrName, String subject, String saml) 
        throws RemoteException, UnknownRandomizerFault, RandomizationFault, NotAuthorisedFault;

    /**
     * Check the integrity of the named randomizer i.e. if it appears
     * that it has been tampered with.
     * 
     * @param rdmzrName The name of the randomizer
     * @param saml SAML assertion for security system
     * @return Boolean, True if appears that the randomizer has been
     * tampered with, False if all is OK.
     * @throws RemoteException
     * @throws UnknownRandomizerFault if the named randomizer does not exist.
     * @throws RandomizationFault if an unrecoverable error occurred whilst checking integrity.
     */
    public boolean checkIntegrity(String rdmzrName, String saml) 
        throws RemoteException, UnknownRandomizerFault, RandomizationFault, NotAuthorisedFault;

    /**
     * Save a randomizer to the database.
     * 
     * @param rdmzr The randomizer to save.
     * @param saml SAML assertion for security system
     * @throws RemoteException
     * @throws DuplicateRandomizerFault if a randomizer with the same name already exists
     */
    public void saveRandomizer(Randomizer rdmzr, String saml) 
        throws RemoteException, DuplicateRandomizerFault, RandomizationFault, NotAuthorisedFault;
    
    /**
     * Retrieve statistics on the randomizer, detailing the number of subjects
     * allocated to each of its treatment arms.
     * 
     * @param rdmzrName The name of the randomizer.
     * @param saml SAML assertion for security system
     * @return Array containing the number of allocations for each treatment arm.
     * The structure of the array is that the first array dimension retrieves data
     * for one treatment arm, the second array dimension gets either the treatment 
     * arm name or the number of allocations i.e. result[0][0] will be the name
     * of the first treatment arm; result[0][1] will be the number of allocations
     * for it.
     * @throws UnknownRandomizerFault if no randomizer exists with the given name.
     */
    public String[][] getRandomizerStatistics(String rdmzrName, String saml) 
        throws RemoteException, UnknownRandomizerFault, RandomizationFault, NotAuthorisedFault;
    
    /**
     * Retrieve all allocations for a given randomizer, detailing the treatment
     * arm allocated for each subject so far.
     * 
     * @param rdmzrName The name of the randomizer.
     * @param saml SAML assertion for security system
     * @return Array containing all treatment allocations for the randomizer.
     * The structure of the array is that the first array dimension retrieves data
     * for one subject, the second array dimension gets either the subject name 
     * or the treatment arm they were allocated to i.e. result[0][0] will be the name
     * of the subject; result[0][1] will be the treatment arm they were allocated to.
     * @throws UnknownRandomizerFault if no randomizer exists with the given name.
     */
    public String[][] getAllAllocations(String rdmzrName, String saml) 
        throws RemoteException, UnknownRandomizerFault, RandomizationFault, NotAuthorisedFault;
    
    /**
     * Retrieve statistics on the randomizer, detailing the number of subjects
     * allocated to each of its treatment arms.
     * <p>
     * Only subjects who were allocated with the given parameters will
     * feature in the statistics.
     * <p>
     * This endpoint may therefore be used to get statistics on a particular branch
     * of a stratified randomizer.
     * 
     * @param rdmzrName The name of the randomizer.
     * @param parameters The parameters that will be used whilst retrieving
     * the statistics.
     * @param saml SAML assertion for security system
     * @return Array containing the number of allocations for each treatment arm.
     * The structure of the array is that the first array dimension retrieves data
     * for one treatment arm, the second array dimension gets either the treatment 
     * arm name or the number of allocations i.e. result[0][0] will be the name
     * of the first treatment arm; result[0][1] will be the number of allocations
     * for it.
     * @throws UnknownRandomizerFault if no randomizer exists with the given name.
     */
    public String[][] getRandomizerStatistics(String rdmzrName, Parameter[] parameters, String saml) 
        throws RemoteException, UnknownRandomizerFault, RandomizationFault, NotAuthorisedFault;
    
    /**
     * Retrieve the dates of all randomization events (i.e treatment allocations)
     * for a given subject.
     * 
     * @param rdmzrName
     * @param subjectCode
     * @param saml
     * @return dates of randomizations
     * @throws RemoteException
     * @throws UnknownRandomizerFault
     * @throws RandomizationFault
     */
    public Calendar[] getSubjectRandomizationEvents(String rdmzrName, String subjectCode, String saml) 
    	throws RemoteException, UnknownRandomizerFault, RandomizationFault, NotAuthorisedFault;

    /**
     * Retrieve the randomisation result for a subject and date.
     * 
     * @param rdmzrName
     * @param subject
     * @param date
     * @param saml
     * @return randomisation result id and name
     * @throws RemoteException
     * @throws UnknownRandomizerFault
     * @throws RandomizationFault
     * @throws NotAuthorisedFault
     */
    public String[] getRandomizationResultForDate(String rdmzrName, String subject, Calendar date, String saml) 
    throws RemoteException, UnknownRandomizerFault, RandomizationFault, NotAuthorisedFault;

    /**
     * Delete a randomizer in the database.
     * 
     * @param rdmzr The name of the randomizer to delete.
     * @param saml
     * @throws RemoteException
     * @throws UnknownRandomizerFault
     * @throws RandomizationFault
     * @throws NotAuthorisedFault
     */
    public void deleteRandomizer(final String rdmzrName, String saml) 
    	throws RemoteException, UnknownRandomizerFault, RandomizationFault, NotAuthorisedFault;

    /**
     * Get the randomizer from the database.
     * 
     * @param rdmzr The name of the randomizer to fetch.
     * @param saml
     * @throws RemoteException
     * @throws UnknownRandomizerFault
     * @throws RandomizationFault
     * @throws NotAuthorisedFault
     */
    public Randomizer getRandomizer(final String rdmzrName, String saml) 
    	throws RemoteException, UnknownRandomizerFault, RandomizationFault, NotAuthorisedFault;
    
    /**
     * Retrieve an array of subjects that have been randomised between the specified boundary dates.
     * The starting boundary date is inclusive and the ending boundary date is exclusive 
     * (e.g. if it were necessary to retrieve all participants randomised 
     * in June and July 2011, the required boundary dates would be 1 June 2011 00:00 and 1 August 2011 00:00)
     * 
     * @param randomiserName
     * @param startBoundaryInclusive
     * @param endBoundaryDelimiter
     * @return - an array of all subjects randomised within the specified timeframe. Returns a non-null empty
     * array if there are no matching results.
     * @throws RemoteException
     * @throws UnknownRandomizerFault
     * @throws NotAuthorisedFault
     */
    public String[] getRandomisedParticipantsWithinTimeframe(String randomiserName, Calendar startBoundaryInclusive, Calendar endBoundaryDelimiter, String saml)
    	throws RemoteException, NotAuthorisedFault, UnknownRandomizerFault;
    
}
