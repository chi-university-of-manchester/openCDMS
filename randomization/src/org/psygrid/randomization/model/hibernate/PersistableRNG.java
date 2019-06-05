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

import java.util.Map;

import org.psygrid.randomization.model.RNGException;

import ec.util.MersenneTwister;

/**
 * Class to represent a persistable Random Number Generator, i.e.
 * one that can be used over different sessions with its state
 * being preserved. 
 * <p>
 * The random numbers themselves are generated using a Mersenne
 * Twister algorithm.
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_persistable_rngs"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class PersistableRNG extends Persistent {
    
    /**
     * The number used to seed the RNG.
     */
    private long seed;
    
    /**
     * The count of random numbers generated to date.
     */
    private long count;
    
    /**
     * The Mersenne Twister random number generator.
     */
    private MersenneTwister mtRng;
    
    /**
     * Flag to indicate that the RNG has been initialized.
     * <p>
     * If the RNG has not been initialized, then it will not be 
     * possible to generate random numbers.
     */
    private boolean initialized;

    public PersistableRNG(){}
    
    public PersistableRNG(long seed){
        this.seed = seed;
    }
    
    /**
     * Get the count of random numbers generated to date.
     * 
     * @return The count of random numbers.
     * 
     * @hibernate.property column="c_count"
     */
    public long getCount() {
        return count;
    }

    /**
     * Set the count of random numbers generated to date.
     * 
     * @param count The count of random numbers.
     */
    public void setCount(long count) {
        this.count = count;
    }

    /**
     * Get the number used to seed the RNG.
     * 
     * @return The seed.
     * 
     * @hibernate.property column="c_seed"
     */
    public long getSeed() {
        return seed;
    }

    /**
     * Set the number used to seed the RNG.
     * 
     * @param seed The seed.
     */
    public void setSeed(long seed) {
        this.seed = seed;
    }
    
    
    public void initialize(){
        mtRng = new MersenneTwister(seed);
        //fast forward the RNG to the state it was in the last
        //time this instance was used
        for ( int i=0; i<count; i++ ){
            mtRng.nextInt();
        }
        initialized = true;
    }
    
    public int nextInt(final int n) throws RNGException {
        if ( !initialized ){
            //TODO throw a better class of Exception
            throw new RNGException("RNG has not been initialized.");
        }
        int rn = mtRng.nextInt(n);
        count++;
        return rn;
    }
    
    public float nextFloat() throws RNGException {
        if ( !initialized ){
            //TODO throw a better class of Exception
            throw new RNGException("RNG has not been initialized.");
        }
        float rn = mtRng.nextFloat();
        count++;
        return rn;
    }
    
    public org.psygrid.randomization.model.dto.PersistableRNG toDTO(Map<Persistent, org.psygrid.randomization.model.dto.Persistent> dtoRefs){
        //check for an already existing instance of a dto object for this 
        //RNG in the set of references
        org.psygrid.randomization.model.dto.PersistableRNG dtoR = null;
        if ( dtoRefs.containsKey(this)){
            dtoR = (org.psygrid.randomization.model.dto.PersistableRNG)dtoRefs.get(this);
        }
        else{
            //an instance of the RNG has not already
            //been created, so create it and add it to the map of references
            dtoR = new org.psygrid.randomization.model.dto.PersistableRNG();
            dtoRefs.put(this, dtoR);
            toDTO(dtoR, dtoRefs);
        }
        return dtoR;
    }
    
    public void toDTO(org.psygrid.randomization.model.dto.PersistableRNG dtoR, Map<Persistent, org.psygrid.randomization.model.dto.Persistent> dtoRefs){
        super.toDTO(dtoR, dtoRefs);
        dtoR.setSeed(this.seed);
        dtoR.setCount(this.count);
    }
    
    public void fromDTO(org.psygrid.randomization.model.dto.PersistableRNG dtoR, Map<org.psygrid.randomization.model.dto.Persistent, Persistent> refs){
        super.fromDTO(dtoR, refs);
        this.seed = dtoR.getSeed();
        this.count = dtoR.getCount();
    }
        
}
