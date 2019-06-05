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

package org.psygrid.randomization.model.dto;

import java.util.Map;

public abstract class BlockRandomizer extends Randomizer {

    /**
     * The minimum block size to use.
     */
    private int minBlockSize;
    
    /**
     * The maximum block size to use.
     */
    private int maxBlockSize;
    
    /**
     * Persistable Random Number Generator
     */
    private PersistableRNG rng;
    
    /**
     * The collection of treatments into which a subject may be
     * placed by this randomizer.
     */
    private Treatment[] treatments = new Treatment[0];

    /**
     * The collection of block items for all treatment blocks so far
     * selected.
     */
    private BlockItem[] treatmentBlocks = new BlockItem[0];
    
    /**
     * The list of treatment allocations.
     * <p>
     * Each allocation represents the treatment that has been allocated
     * to a single subject.
     */
    private Allocation[] allocations = new Allocation[0];
    
    /**
     * The list of subjects for which treatments have been allocated, in the
     * order which they were presented to the randomizer.
     */
    private String[] subjects = new String[0];

    public int getMaxBlockSize() {
        return maxBlockSize;
    }

    public void setMaxBlockSize(int maxBlockSize) {
        this.maxBlockSize = maxBlockSize;
    }

    public int getMinBlockSize() {
        return minBlockSize;
    }

    public void setMinBlockSize(int minBlockSize) {
        this.minBlockSize = minBlockSize;
    }

    public Allocation[] getAllocations() {
        return allocations;
    }

    public void setAllocations(Allocation[] allocations) {
        this.allocations = allocations;
    }

    public PersistableRNG getRng() {
        return rng;
    }

    public void setRng(PersistableRNG rng) {
        this.rng = rng;
    }

    public String[] getSubjects() {
        return subjects;
    }

    public void setSubjects(String[] subjects) {
        this.subjects = subjects;
    }

    public BlockItem[] getTreatmentBlocks() {
        return treatmentBlocks;
    }

    public void setTreatmentBlocks(BlockItem[] treatmentBlocks) {
        this.treatmentBlocks = treatmentBlocks;
    }

    public Treatment[] getTreatments() {
        return treatments;
    }

    public void setTreatments(Treatment[] treatments) {
        this.treatments = treatments;
    }
    
    public abstract org.psygrid.randomization.model.hibernate.BlockRandomizer toHibernate();
    
    public abstract org.psygrid.randomization.model.hibernate.BlockRandomizer toHibernate(Map<Persistent, org.psygrid.randomization.model.hibernate.Persistent> refs);

}
