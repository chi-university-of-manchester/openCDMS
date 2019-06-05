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

package org.psygrid.randomization.dao;

import java.util.Calendar;
import java.util.Date;

import org.psygrid.randomization.Parameter;
import org.psygrid.randomization.model.DuplicateSubjectException;
import org.psygrid.randomization.model.RandomizerException;
import org.psygrid.randomization.model.hibernate.PersistableRNG;

/**
 * Interface that defines the DAO operations that may be performed
 * on persistent randomization objects.
 * 
 * @author Rob Harper
 *
 */
public interface RandomizationDAO {

    public Long saveRng(PersistableRNG rng);
    
    public PersistableRNG getRng(Long id);
    
    public Long saveRpmrblRandomizer(org.psygrid.randomization.model.dto.RpmrblRandomizer r);
    
    public org.psygrid.randomization.model.dto.RpmrblRandomizer getRpmrblRandomizer(Long id);
    
    public Long saveRandomizer(org.psygrid.randomization.model.hibernate.Randomizer r);
    
    /*
     * Deprecated methods above??
     */
    
    /**
     * Save or update a randomizer in the database.
     * 
     * @param r The randomizer to save.
     * @return The database unique id of the randomizer.
     * @throws DuplicateRandomizerException if a randomizer already exists with the same name.
     */
    public Long saveRandomizer(org.psygrid.randomization.model.dto.Randomizer r) throws DuplicateRandomizerException, RandomizerDAOException;
    
    /**
     * Retrieve a randomizer from the database.
     * 
     * @param name The name fo the randomizer to retrieve.
     * @return The randomizer.
     */
    public org.psygrid.randomization.model.dto.Randomizer getRandomizer(String name) throws RandomizerDAOException;
    
    /**
     * Allocate a single subject using a randomizer.
     * 
     * @param rdmzrName The name of the randomizer to use.
     * @param subject The name of the subject to randomize.
     * @param parameters The parameters to use for the randomization (e.g. strata
     * data for a stratified randomizer).
     * @return The code of the treatment the subject was allocated to.
     * @throws DuplicateSubjectException if a subject with the same name has already 
     * been randomized with the randomizer.
     * @throws UnknownRandomizerException if no randomizer exists with the given name.
     * @throws RandomizerException if an unrecoverable error occurred during allocation.
     */
    public String allocate(String rdmzrName, String subject, Parameter[] parameters) 
        throws DuplicateSubjectException, UnknownRandomizerException, RandomizerException, RandomizerDAOException;
    
    /**
     * Get the allocation of a single subject.
     * 
     * @param rdmzrName The randomizer the subject was randomized by.
     * @param subject The subject whose allocation is to be retrieved.
     * @return The code of the treatment the subject was allocated to.
     * @throws UnknownRandomizerException if no randomizer exists with the given name, 
     * or the given subject has not been randomized with the randomizer.
     */
    public String getAllocation(String rdmzrName, String subject) throws UnknownRandomizerException, RandomizerDAOException;
    
    /**
     * Get the allocation of a single subject for a given date.
     * 
     * @param rdmzrName The randomizer the subject was randomized by.
     * @param subject The subject whose allocation is to be retrieved.
     * @parm date The date of the particular randomization event
     * @return The code and name of the treatment the subject was allocated to.
     * @throws UnknownRandomizerException if no randomizer exists with the given name, 
     * or the given subject has not been randomized with the randomizer.
     */
    public String[] getAllocation(String rdmzrName, String subject, Date date) throws UnknownRandomizerException, RandomizerDAOException;
    
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
     * @param randomiserName
     * @param startBoundaryInclusive
     * @param endBoundaryDelimiter
     * @return
     * @throws UnknownRandomizerException
     * @throws RandomizerDAOException
     */
    public String[] getRandomisedParticipantsWithinTimeframe(final String randomiserName, final Date startBoundaryInclusive, final Date endBoundaryDelimiter) throws UnknownRandomizerException;
 
    
    /**
     * Check the integrity of a randomizer.
     * 
     * @param rdmzrName The name of the randomizer to check.
     * @return Boolean, True if the integrity of the randomizer is intact,
     * False otherwise.
     * @throws UnknownRandomizerException if no randomizer exists with the given name.
     * @throws RandomizerException if an unrecoverable error occurred during allocation.
     */
    public boolean checkIntegrity(String rdmzrName) throws RandomizerException, UnknownRandomizerException, RandomizerDAOException;
    
    /**
     * Retrieve statistics on the randomizer, detailing the number of subjects
     * allocated to each of its treatment arms.
     * 
     * @param rdmzrName The name of the randomizer.
     * @return Array containing the number of allocations for each treatment arm.
     * The structure of the array is that the first array dimension retrieves data
     * for one treatment arm, the second array dimension gets either the treatment 
     * arm name or the number of allocations i.e. result[0][0] will be the name
     * of the first treatment arm; result[0][1] will be the number of allocations
     * for it.
     * @throws UnknownRandomizerException if no randomizer exists with the given name.
     * @throws RandomizerDAOException
     * @throws RandomizerException
     */
    public String[][] getRandomizerStatistics(String rdmzrName) 
            throws UnknownRandomizerException, RandomizerDAOException, RandomizerException;
    
    /**
     * Retrieve statistics on the randomizer, detailing the number of subjects
     * allocated to each of its treatment arms.
     * <p>
     * Only subjects who were allocated with the given parameters will
     * feature in the statistics.
     * <p>
     * This method may therefore be used to get statistics on a particular branch
     * of a stratified randomizer.
     * 
     * @param rdmzrName The name of the randomizer.
     * @param parameters The parameters to use to generate the statistics (e.g. strata
     * data for a stratified randomizer).
     * @return Array containing the number of allocations for each treatment arm.
     * The structure of the array is that the first array dimension retrieves data
     * for one treatment arm, the second array dimension gets either the treatment 
     * arm name or the number of allocations i.e. result[0][0] will be the name
     * of the first treatment arm; result[0][1] will be the number of allocations
     * for it.
     * @throws UnknownRandomizerException if no randomizer exists with the given name.
     * @throws RandomizerDAOException
     * @throws RandomizerException
     */
    public String[][] getRandomizerStatistics(String rdmzrName, Parameter[] parameters) 
            throws UnknownRandomizerException, RandomizerDAOException, RandomizerException;
    
    /**
     * Retrieve all allocations for a given randomizer, detailing the treatment
     * arm allocated for each subject so far.
     * 
     * @param rdmzrName The name of the randomizer.
     * @return Array containing all treatment allocations for the randomizer.
     * The structure of the array is that the first array dimension retrieves data
     * for one subject, the second array dimension gets either the subject name 
     * or the treatment arm they were allocated to i.e. result[0][0] will be the name
     * of the subject; result[0][1] will be the treatment arm they were allocated to.
     * @throws UnknownRandomizerException if no randomizer exists with the given name.
     */
    public String[][] getAllAllocations(String rdmzrName) throws UnknownRandomizerException, RandomizerDAOException;
    
    /**
     * Retrieve the dates of all randomization events (i.e treatment allocations)
     * for a given subject.
     * 
     * Returns null if a subject hasn't been randomized.
     * 
     * @param rdmzrName
     * @param subjectCode
     * @return dates of randomizations
     * @throws UnknownRandomizerException
     * @throws RandomizerDAOException
     */
    public Calendar[] getSubjectRandomizationEvents(final String rdmzrName, final String subjectCode) throws UnknownRandomizerException, RandomizerDAOException;

    /**
     * Delete a randomizer in the database.
     * 
     * @param rdmzr The name of the randomizer to delete.
     * @throws UnknownRandomizerException
     * @throws RandomizerDAOException
     */
    public void deleteRandomizer(final String rdmzrName) throws UnknownRandomizerException, RandomizerDAOException;

    
}
