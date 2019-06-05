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
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.randomization.model.DuplicateSubjectException;
import org.psygrid.randomization.model.NoBlockItemException;
import org.psygrid.randomization.model.RNGException;
import org.psygrid.randomization.model.RandomizerException;

/**
 * Class to perform randomization using the "Random Permuted Blocks
 * of Random Block Size" method.
 * <p>
 * The algorithm used is adapted from Visual Basic code supplied by
 * Iain Buchan (buchan@manchester.ac.uk).
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_ib_rpbrbl_rndmzrs"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class IBRpbrblRandomizer extends BlockRandomizer {

    private static Log sLog = LogFactory.getLog(IBRpbrblRandomizer.class);
    private static boolean logDebugEnabled = sLog.isDebugEnabled();
    
    public IBRpbrblRandomizer(){}
    
    public IBRpbrblRandomizer(String name){
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
            int blockLength = (int)Math.floor((maxBlockSize - minBlockSize + 1) * rng.nextFloat() + minBlockSize );
            if ( logDebugEnabled ){
                sLog.debug("Next block has length "+blockLength);
            }
            
            //Step 2. Allocate the block pattern as treatments in alphanumeric order
            TwoLng[] blockTreatments = new TwoLng[blockLength*treatments.size()];
            int counter = 0;
            for ( int j=0; j<blockLength; j++ ){
                for ( int i=0; i<treatments.size(); i++ ){
                    blockTreatments[counter] = new TwoLng();
                    blockTreatments[counter].setRx(i);
                    counter++;
                }
            }
            //Step 3. Randomize the order of the block pattern by allocating an order 
            //number at random for each element then bubble sort the array
            for ( int i=blockLength*treatments.size()-1; i>=0; i-- ){
                blockTreatments[i].setId( (int)Math.floor( blockLength*treatments.size() * rng.nextFloat() ));
            }
            bubbleSort(blockTreatments);
            //Step 4. Store the randomized block
            for ( int i=0; i<blockTreatments.length; i++ ){
                BlockItem bi = new BlockItem(treatments.get(blockTreatments[i].getRx()));
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

    protected IBRpbrblRandomizer copy(){
        IBRpbrblRandomizer copy = new IBRpbrblRandomizer();
        copy.createRng(this.rng.getSeed());
        copy.setMinBlockSize(this.minBlockSize);
        copy.setMaxBlockSize(this.maxBlockSize);
        for ( Treatment t: this.treatments ){
            copy.addTreatment(t.getName(), t.getCode());
        }
        return copy;
    }
    
    private void bubbleSort(TwoLng[] data){
        TwoLng temp1 = null;
        TwoLng temp2 = null;
        while ( true ){
            boolean swap = false;
            for ( int i=0; i<(data.length - 1); i++ ){
                temp1 = data[i];
                temp2 = data[i+1];
                if ( temp1.getId() > temp2.getId() ){
                    data[i] = temp2;
                    data[i+1] = temp1;
                    swap = true;
                }
            }
            if ( !swap ){
                break;
            }
        }
    }
    
    private class TwoLng{
        private int id;
        private int rx;
        public int getId() {
            return id;
        }
        public void setId(int id) {
            this.id = id;
        }
        public int getRx() {
            return rx;
        }
        public void setRx(int rx) {
            this.rx = rx;
        }        
    }
    
    public org.psygrid.randomization.model.dto.IBRpbrblRandomizer toDTO(){
        Map<Persistent, org.psygrid.randomization.model.dto.Persistent> dtoRefs = new HashMap<Persistent, org.psygrid.randomization.model.dto.Persistent>();
        org.psygrid.randomization.model.dto.IBRpbrblRandomizer dtoR = toDTO(dtoRefs);
        dtoRefs = null;
        return dtoR;
      
    }
    
    public org.psygrid.randomization.model.dto.IBRpbrblRandomizer toDTO(Map<Persistent, org.psygrid.randomization.model.dto.Persistent> dtoRefs){
        //check for an already existing instance of a dto object for this 
        //randomizer in the set of references
        org.psygrid.randomization.model.dto.IBRpbrblRandomizer dtoR = null;
        if ( dtoRefs.containsKey(this)){
            dtoR = (org.psygrid.randomization.model.dto.IBRpbrblRandomizer)dtoRefs.get(this);
        }
        else{
            //an instance of the randomizer has not already
            //been created, so create it and add it to the map of references
            dtoR = new org.psygrid.randomization.model.dto.IBRpbrblRandomizer();
            dtoRefs.put(this, dtoR);
            toDTO(dtoR, dtoRefs);
        }
        return dtoR;
    }
    
    public void toDTO(org.psygrid.randomization.model.dto.IBRpbrblRandomizer dtoR, Map<Persistent, org.psygrid.randomization.model.dto.Persistent> dtoRefs){
        super.toDTO(dtoR, dtoRefs);
    }
    
    public void fromDTO(org.psygrid.randomization.model.dto.RpmrblRandomizer dtoR, Map<org.psygrid.randomization.model.dto.Persistent, Persistent> refs){
        super.fromDTO(dtoR, refs);
    }
}
