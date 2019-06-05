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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.randomization.model.DuplicateSubjectException;
import org.psygrid.randomization.model.NoBlockItemException;
import org.psygrid.randomization.model.RNGException;
import org.psygrid.randomization.model.RandomizerException;
import org.psygrid.randomization.util.UniquePermutationGenerator;

/**
 * Class to perform randomization using the "Random Permuted Blocks
 * of Random Block Size" method.
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_rpmrbl_rndmzrs"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class RpmrblRandomizer extends BlockRandomizer {

    private static Log sLog = LogFactory.getLog(RpmrblRandomizer.class);
    private static boolean logDebugEnabled = sLog.isDebugEnabled();
    
    public RpmrblRandomizer(){}
    
    public RpmrblRandomizer(String name){
        super(name);
    }
    
    
    public String allocate(String subject) throws RandomizerException, DuplicateSubjectException {
        
        //check that the subject has not already been allocated
        for ( Allocation a : this.allocations ){
            if ( a.getSubject().equals(subject) ){
                throw new DuplicateSubjectException("The subject '"+subject+
                        "' has already been allocated to a treatment arm by this randomizer.");
            }
        }
        
        //find the next available treatment arm allocation
        try{
            return allocateFromBlocks(subject);
        }
        catch(NoBlockItemException ex){
            //there are no more un-allocated block items from the blocks so far
            //generated - we try to generate the next block below so do nothing here.
        }
        
        //if execution reaches this point then all previously generated
        //treatment arm allocations have been used - so generate the next batch
        if ( logDebugEnabled ){
            sLog.debug("Generating new allocations...");
        }
        
        try{
        
            //Step 1. Randomly choose the block length; this will be either 
            //2 or 3 (* number of treatment arms)
            int blockLength = rng.nextInt(2)+2;
            if ( logDebugEnabled ){
                sLog.debug("Next block has length "+blockLength);
            }
            
            //Step 2. Randomly choose a block
            if ( 0 == treatments.size() ){
                throw new RandomizerException("The randomizer does not have any treatment arms.");
            }
            String[] elements = new String[blockLength*treatments.size()];
            int arm = -1;
            for ( int i=0; i<elements.length; i++ ){
                if ( 0 == i % blockLength ){
                    arm++;
                }
                elements[i] = Integer.toString(arm);
            }
            UniquePermutationGenerator pGen = new UniquePermutationGenerator();
            List<String> blocks = pGen.getUniquePermutations(elements);
            String block = blocks.get(rng.nextInt(blocks.size()-1));
            if ( logDebugEnabled ){
                sLog.debug("Next block is "+block);
            }
            
            //Step 3. Store the next set of allocations as provided
            //by the randomly selected block
            String[] blockTreatments = block.split(",");
            for ( int i=0; i<blockTreatments.length; i++ ){
                BlockItem bi = new BlockItem(treatments.get(Integer.parseInt(blockTreatments[i])));
                treatmentBlocks.add(bi);
            }
            
        }
        catch(RNGException ex){
            throw new RandomizerException(ex);
        }
        
        //Now that we have created a new treatment block we can again try to 
        //allocate a treatment to the subject
        try{
            return allocateFromBlocks(subject);
        }
        catch (NoBlockItemException ex){
            //this time something really has gone wrong!
            throw new RandomizerException("Critical error: creation of new treatment block appears to have failed!", ex);
        }
    }
    
    protected String allocateFromBlocks(String subject) throws NoBlockItemException {
        for ( BlockItem bi: treatmentBlocks ){
            if ( !bi.isAllocated() ){
                bi.allocate();
                subjects.add(subject);
                Allocation a = new Allocation(bi.getTreatment(), subject);
                allocations.add(a);
                return a.getTreatment().getCode();
            }
        }
        throw new NoBlockItemException("All items in treatment blocks so far generated have been allocated.");
    }
    
    protected RpmrblRandomizer copy(){
        RpmrblRandomizer copy = new RpmrblRandomizer();
        copy.createRng(this.rng.getSeed());
        for ( Treatment t: this.treatments ){
            copy.addTreatment(t.getName(), t.getCode());
        }
        return copy;
    }
    
    public org.psygrid.randomization.model.dto.RpmrblRandomizer toDTO(){
        Map<Persistent, org.psygrid.randomization.model.dto.Persistent> dtoRefs = new HashMap<Persistent, org.psygrid.randomization.model.dto.Persistent>();
        org.psygrid.randomization.model.dto.RpmrblRandomizer dtoR = toDTO(dtoRefs);
        dtoRefs = null;
        return dtoR;
      
    }
    
    public org.psygrid.randomization.model.dto.RpmrblRandomizer toDTO(Map<Persistent, org.psygrid.randomization.model.dto.Persistent> dtoRefs){
        //check for an already existing instance of a dto object for this 
        //randomizer in the set of references
        org.psygrid.randomization.model.dto.RpmrblRandomizer dtoR = null;
        if ( dtoRefs.containsKey(this)){
            dtoR = (org.psygrid.randomization.model.dto.RpmrblRandomizer)dtoRefs.get(this);
        }
        else{
            //an instance of the randomizer has not already
            //been created, so create it and add it to the map of references
            dtoR = new org.psygrid.randomization.model.dto.RpmrblRandomizer();
            dtoRefs.put(this, dtoR);
            toDTO(dtoR, dtoRefs);
        }
        return dtoR;
    }
    
    public void toDTO(org.psygrid.randomization.model.dto.RpmrblRandomizer dtoR, Map<Persistent, org.psygrid.randomization.model.dto.Persistent> dtoRefs){
        super.toDTO(dtoR, dtoRefs);
    }
    
    public void fromDTO(org.psygrid.randomization.model.dto.RpmrblRandomizer dtoR, Map<org.psygrid.randomization.model.dto.Persistent, Persistent> refs){
        super.fromDTO(dtoR, refs);
    }
    
}
