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

package org.psygrid.randomization.model.hibernate;

import java.util.Date;
import java.util.Map;

import org.psygrid.randomization.dao.RandomizerDAOException;
import org.psygrid.randomization.dao.UnknownRandomizerException;
import org.psygrid.randomization.model.DuplicateSubjectException;
import org.psygrid.randomization.model.RandomizerException;

/**
 * Abstract base class that all concrete Randomizer implementations
 * must inherit from.
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_rdmzrs"
 * @hibernate.joined-subclass-key column="c_id"
 */
public abstract class Randomizer extends Persistent {

    /**
     * The name of the randomizer.
     */
    private String name;

    public Randomizer(){}
    
    public Randomizer(String name){
        this.name = name;
    }
    
    /**
     * Get the name of the randomizer.
     * 
     * @return The name.
     * 
     * @hibernate.property column="c_name"
     *                     not-null="true"
     *                     unique="true";
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the randomizer.
     * 
     * @param name The name.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Add a treatment arm to the randomizer, with the supplied name
     * and code.
     * 
     * @param name The name of the treatment arm to add.
     * @param code The code of the treatment arm to add.
     * @throws RandomizerException if the treatment could not be added.
     */
    public abstract void addTreatment(String name, String code) throws RandomizerException;
    
    /**
     * Initialize the randomizer to prepare it for generating treatment
     * allocations.
     */
    public abstract void initialize();
    
    /**
     * Randomly allocate the given subject to one of the treatment arms
     * defined for the randomizer.
     * 
     * @param subject The subject to allocate.
     * @return The code of the treatment arm they have been allocated to.
     * @throws RandomizerException if a fatal error occurs during the allocation process.
     * @throws DuplicateSubjectException if the subject has already been allocated to a 
     * treatmenr arm by the randomizer.
     */
    public abstract String allocate(String subject) throws RandomizerException, DuplicateSubjectException;
    
    /**
     * Set a parameter to be used during the allocate process.
     * 
     * @param key The key for the parameter.
     * @param value The value of the parameter.
     */
    public void setParameter(String key, String value){
        //Default behaviour is to do nothing.
        //Concrete randomizer implementations that require the
        //use of parameters must override this method
    }
    
    /**
     * Get the value of a parameter to be used during the allocate process.
     * 
     * @param key The key for the parameter.
     * @return The value of the parameter.
     */
    public String getParameter(String key){
        //Default behaviour is to do return null (i.e. no value
        //found for the given key.
        //Concrete randomizer implementations that require the
        //use of parameters must override this method
        return null;
    }
    
    /**
     * Retrieve the treatment arm allocation of a subject previously
     * randomized by the randomizer.
     * 
     * @param subject The subject whose allocation is to be retrieved.
     * @return The code of the treatment that this subject was allocated to.
     * @throws RandomizerException if no allocation exists for this subject.
     */
    public abstract String getAllocation(String subject) throws RandomizerException ;
    
    /**
     * Retrieve the treatment arm allocation of a subject previously
     * randomized by the randomizer on the given date.
     * 
     * @param subject The subject whose allocation is to be retrieved.
     * @return The code of the treatment that this subject was allocated to.
     * @throws RandomizerException if no allocation exists for this subject.
     */
    public abstract Treatment getAllocation(String subject, Date date) throws RandomizerException ;
    
    
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
    public abstract String[] getRandomisedParticipantsWithinTimeframe(Date startBoundaryInclusive, Date endBoundaryDelimiter);
    
    
    /**
     * Check the integrity of the randomizer.
     * <p>
     * If True is returned, all is OK. If False is returned then it is
     * possible that the randomizer has been tampered with.
     * 
     * @return Boolean that indicates the integrity of the randomizer.
     */
    public abstract boolean checkIntegrity() throws RandomizerException;

    /**
     * Retrieve statistics on the randomizer, detailing the number of subjects
     * allocated to each of its treatment arms.
     * 
     * @return Map containing statistics; key is treatment arm, value is number
     * of allocations.
     */
    public abstract Map<String, Long> getRandomizerStatistics() throws RandomizerException;
    
    /**
     * Retrieve all allocations for the randomizer, detailing the treatment
     * arm allocated for each subject so far.
     * 
     * @return Map containg all allocations; key is subject, valus is treatment arm
     * they were allocated to.
     */
    public abstract Map<String, String> getAllAllocations();
    
    public abstract org.psygrid.randomization.model.dto.Randomizer toDTO();
    
    
    public abstract org.psygrid.randomization.model.dto.Randomizer toDTO(Map<Persistent, org.psygrid.randomization.model.dto.Persistent> dtoRefs);

    public void toDTO(org.psygrid.randomization.model.dto.Randomizer dtoR, Map<Persistent, org.psygrid.randomization.model.dto.Persistent> dtoRefs){
        super.toDTO(dtoR, dtoRefs);
        dtoR.setName(this.name);
    }
    
    public void fromDTO(org.psygrid.randomization.model.dto.Randomizer dtoR, Map<org.psygrid.randomization.model.dto.Persistent, Persistent> refs){
        super.fromDTO(dtoR, refs);
        this.name = dtoR.getName();
    }
}
