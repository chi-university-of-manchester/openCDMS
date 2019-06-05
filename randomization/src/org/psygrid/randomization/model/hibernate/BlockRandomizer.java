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

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.psygrid.randomization.model.DuplicateSubjectException;
import org.psygrid.randomization.model.NoBlockItemException;
import org.psygrid.randomization.model.RandomizerException;

/**
 * Abstract base class that should be extended by all block randomizer
 * implementations.
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_block_rndmzrs"
 * @hibernate.joined-subclass-key column="c_id"
 */
public abstract class BlockRandomizer extends Randomizer {

    /**
     * The minimum block size to use.
     */
    protected int minBlockSize;
    
    /**
     * The maximum block size to use.
     */
    protected int maxBlockSize;
    
    /**
     * Persistable Random Number Generator
     */
    protected PersistableRNG rng;
    
    /**
     * The collection of treatments into which a subject may be
     * placed by this randomizer.
     */
    protected List<Treatment> treatments = new ArrayList<Treatment>();

    /**
     * The collection of block items for all treatment blocks so far
     * selected.
     */
    protected List<BlockItem> treatmentBlocks = new ArrayList<BlockItem>();
    
    /**
     * The list of treatment allocations.
     * <p>
     * Each allocation represents the treatment that has been allocated
     * to a single subject.
     */
    protected List<Allocation> allocations = new ArrayList<Allocation>();
    
    /**
     * The list of subjects for which treatments have been allocated, in the
     * order which they were presented to the randomizer.
     */
    protected List<String> subjects = new ArrayList<String>();
    
    public BlockRandomizer(){}
    
    public BlockRandomizer(String name){
        super(name);
    }
    
    /**
     * 
     * @return
     * 
     * @hibernate.property column="c_max_bl_size"
     */
    public int getMaxBlockSize() {
        return maxBlockSize;
    }

    public void setMaxBlockSize(int maxBlockSize) {
        this.maxBlockSize = maxBlockSize;
    }

    /**
     * 
     * @return
     * 
     * @hibernate.property column="c_min_bl_size"
     */
    public int getMinBlockSize() {
        return minBlockSize;
    }

    public void setMinBlockSize(int minBlockSize) {
        this.minBlockSize = minBlockSize;
    }

    /**
     * Get the persistable Random Number Generator
     * 
     * @return The random number generator.
     * 
     * @hibernate.many-to-one class="org.psygrid.randomization.model.hibernate.PersistableRNG"
     *                        column="c_rng_id"
     *                        not-null="true"
     *                        unique="true"
     *                        cascade="all"
     */
    public PersistableRNG getRng() {
        return rng;
    }

    /**
     * Set the persistable Random Number Generator
     * 
     * @param rng The random number generator.
     */
    public void setRng(PersistableRNG rng) {
        this.rng = rng;
    }

    /**
     * Get the collection of treatments into which a subject may be
     * placed by this randomizer.
     * 
     * @return The treatments.
     * 
     * @hibernate.list cascade="save-update" 
     *                 table="t_rndmzr_trtmts"
     * @hibernate.key column="c_rndmzr_id"
     * @hibernate.many-to-many class="org.psygrid.randomization.model.hibernate.Treatment"
     *                         column="c_trtmt_id"
     * @hibernate.list-index column="c_index"
     */
    @SuppressWarnings("unused")
    public List<Treatment> getTreatments() {
        return treatments;
    }

    /**
     * Set the collection of treatments into which a subject may be
     * placed by this randomizer.
     * 
     * @param treatments The treatments.
     */
    @SuppressWarnings("unused")
    private void setTreatments(List<Treatment> treatments) {
        this.treatments = treatments;
    }

    /**
     * Get the list of treatment allocations.
     * <p>
     * Each allocation represents the treatment that has been allocated
     * to a single subject.
     * 
     * @return The list of treatment allocations.
     * 
     * @hibernate.list cascade="all"
     * @hibernate.one-to-many class="org.psygrid.randomization.model.hibernate.Allocation"
     * @hibernate.key column="c_rndmzr_id" not-null="true"
     * @hibernate.list-index column="c_index"
     */
    private List<Allocation> getAllocations() {
        return allocations;
    }

    /**
     * Set the list of treatment allocations.
     * <p>
     * Each allocation represents the treatment that has been allocated
     * to a single subject.
     * 
     * @param allocations The list of treatment allocations.
     */
    @SuppressWarnings("unused")
    private void setAllocations(List<Allocation> allocations) {
        this.allocations = allocations;
    }    
    
    /**
     * Get the collection of block items for all treatment blocks so far
     * selected.
     * 
     * @return The collection of block items.
     * 
     * @hibernate.list cascade="all"
     * @hibernate.one-to-many class="org.psygrid.randomization.model.hibernate.BlockItem"
     * @hibernate.key column="c_rndmzr_id" not-null="true"
     * @hibernate.list-index column="c_index"
     */
    private List<BlockItem> getTreatmentBlocks() {
        return treatmentBlocks;
    }

    /**
     * Set the collection of block items for all treatment blocks so far
     * selected.
     * 
     * @param treatmentBlocks The collection of block items.
     */
    @SuppressWarnings("unused")
    private void setTreatmentBlocks(List<BlockItem> treatmentBlocks) {
        this.treatmentBlocks = treatmentBlocks;
    }

    /**
     * Get the list of subjects for which treatments have been allocated, in the
     * order which they were presented to the randomizer.
     * 
     * @return The list of subjects.
     * 
     * @hibernate.list table="t_subjects"
     * @hibernate.key column="c_rndmzr_id" not-null="true"
     * @hibernate.element column="c_name"
     *                    type="string"
     * @hibernate.list-index column="c_index"
     */
    @SuppressWarnings("unused")
    private List<String> getSubjects() {
        return subjects;
    }

    /**
     * Set the list of subjects for which treatments have been allocated, in the
     * order which they were presented to the randomizer.
     * 
     * @param subjects The list of subjects.
     */
    @SuppressWarnings("unused")
    private void setSubjects(List<String> subjects) {
        this.subjects = subjects;
    }

    
    public void addTreatment(String name, String code){
        Treatment t = new Treatment();
        t.setName(name);
        t.setCode(code);
        this.treatments.add(t);
    }
    
    /**
     * Create and initialize the Random Number Generator for the
     * randomizer, with the given seed.
     * 
     * @param seed The seed for the RNG.
     */
    public void createRng(long seed){
        this.rng = new PersistableRNG(seed);
        this.rng.initialize();
    }
    
    
    public void initialize(){
        this.rng.initialize();
    }
    
    
    public abstract String allocate(String subject) throws RandomizerException, DuplicateSubjectException;

    
    public String getAllocation(String subject) throws RandomizerException {
        String treatment = null;
        for ( Allocation a: allocations ){
            if ( null != a.getSubject() && a.getSubject().equals(subject) ){
                treatment = a.getTreatment().getCode();
            }
        }
        if ( null == treatment ){
            throw new RandomizerException("No allocation exists for the subject '"+subject+"'.");
        }
        return treatment;
    }
    
    public String[] getRandomisedParticipantsWithinTimeframe(Date startBoundaryInclusive, Date endBoundaryDelimiter) {
    	
    	List<String> participantsRandomised = new ArrayList<String>();
    	
    	for( Allocation a: allocations) {
    		if(a.getDate().compareTo(startBoundaryInclusive) >= 0 && a.getDate().before(endBoundaryDelimiter)){
    			participantsRandomised.add(a.getSubject());
    		}
    	}
    	
    	int numParticipants = participantsRandomised.size();
    	String[] theParticipants = new String[numParticipants];
    	return participantsRandomised.toArray(theParticipants);
    }
    

    
    public Treatment getAllocation(String subject, Date date) throws RandomizerException {
        Treatment treatment = null;
        for ( Allocation a: allocations ){
            if ( null != a.getSubject() && a.getSubject().equals(subject) ){
            	
            	//The Allocation's date has strange formatting, 
            	//so the time is put into a new date object. 
            	Date aDate = a.getDate();
            	Date newDate = new Date();
            	newDate.setTime(aDate.getTime());	

            	if (newDate.compareTo(date) == 0) {
            		treatment = a.getTreatment();
            	}
            }
        }
        if ( null == treatment ){
            throw new RandomizerException("No allocation exists for the subject '"+subject+"' for the date "+date.toString());
        }
        return treatment;
    }
    
    
    public Map<String, String> getAllAllocations() {
        Map<String, String> allocations = new LinkedHashMap<String, String>();
        for ( Allocation a: this.allocations ){
            allocations.put(a.getSubject(), a.getTreatment().getCode());
        }
        return allocations;
    }

    
    public Map<String, Long> getRandomizerStatistics() {
        Map <String, Long> stats =  new LinkedHashMap<String, Long>();
        for ( Treatment t: this.treatments ){
            stats.put(t.getCode(), new Long(0));
        }
        for ( Allocation a: this.allocations ){
            Long currentVal = stats.get(a.getTreatment().getCode());
            stats.put(a.getTreatment().getCode(), new Long(currentVal.longValue()+1));
        }
        return stats;
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
    
    
    public boolean checkIntegrity() throws RandomizerException {
        BlockRandomizer copy = this.copy();
        for ( String s: this.subjects ){
            try{
                copy.allocate(s);
            }
            catch(DuplicateSubjectException ex){
                //as all the subject we are allocating here come from the
                //randomizer whose integrity we are checking it should not
                //be possible to have a duplicate subject here. If we do, we
                //may assume that the integrity of the randomizer is NOT intact,
                //as the allocations must have been tampered with after randomization.
                return false;
            }
        }
        
        //check that the two lists are identical, both the generated treatment blocks
        //and the allocations
        boolean checkOk = true;
        for ( int i=0; i<treatmentBlocks.size(); i++ ){
            BlockItem bi = treatmentBlocks.get(i);
            BlockItem check = copy.getTreatmentBlocks().get(i);
            checkOk &= bi.getTreatment().getCode().equals(check.getTreatment().getCode());
            checkOk &= (bi.isAllocated() == check.isAllocated());
        }
        for ( int i=0; i<allocations.size(); i++ ){
            Allocation a = allocations.get(i);
            Allocation check = copy.getAllocations().get(i);
            checkOk &= a.getTreatment().getCode().equals(check.getTreatment().getCode());
            checkOk &= a.getSubject().equals(check.getSubject());
        }
        
        return checkOk;
    }
    
    protected abstract BlockRandomizer copy();
    
    
    public abstract org.psygrid.randomization.model.dto.BlockRandomizer toDTO();
    
    
    public abstract org.psygrid.randomization.model.dto.BlockRandomizer toDTO(Map<Persistent, org.psygrid.randomization.model.dto.Persistent> dtoRefs);
    
    public void toDTO(org.psygrid.randomization.model.dto.BlockRandomizer dtoR, Map<Persistent, org.psygrid.randomization.model.dto.Persistent> dtoRefs){
        super.toDTO(dtoR, dtoRefs);
        dtoR.setMinBlockSize(this.minBlockSize);
        dtoR.setMaxBlockSize(this.maxBlockSize);
        if ( null != this.rng ){
            dtoR.setRng(this.rng.toDTO(dtoRefs));
        }
        org.psygrid.randomization.model.dto.Treatment[] dtoTs = new org.psygrid.randomization.model.dto.Treatment[this.treatments.size()];
        for (int i=0; i<this.treatments.size(); i++){
            Treatment t = treatments.get(i);
            dtoTs[i] = t.toDTO(dtoRefs);
        }        
        dtoR.setTreatments(dtoTs);
        org.psygrid.randomization.model.dto.BlockItem[] dtoBIs = new org.psygrid.randomization.model.dto.BlockItem[this.treatmentBlocks.size()];
        for (int i=0; i<this.treatmentBlocks.size(); i++){
            BlockItem bi = treatmentBlocks.get(i);
            dtoBIs[i] = bi.toDTO(dtoRefs);
        }        
        dtoR.setTreatmentBlocks(dtoBIs);
        org.psygrid.randomization.model.dto.Allocation[] dtoAs = new org.psygrid.randomization.model.dto.Allocation[this.allocations.size()];
        for (int i=0; i<this.allocations.size(); i++){
            Allocation a = allocations.get(i);
            dtoAs[i] = a.toDTO(dtoRefs);
        }        
        dtoR.setAllocations(dtoAs);
        String[] dtoSs = new String[this.subjects.size()];
        for (int i=0; i<this.subjects.size(); i++){
            String s = subjects.get(i);
            dtoSs[i] = s;
        }        
        dtoR.setSubjects(dtoSs);
    }
    
    public void fromDTO(org.psygrid.randomization.model.dto.BlockRandomizer dtoR, Map<org.psygrid.randomization.model.dto.Persistent, Persistent> refs){
        super.fromDTO(dtoR, refs);
        
        this.minBlockSize = dtoR.getMinBlockSize();
        this.maxBlockSize = dtoR.getMaxBlockSize();
        
        if ( null != dtoR.getRng() ){
            this.rng = dtoR.getRng().toHibernate(refs);
        }

        for (int i=0; i<dtoR.getTreatments().length; i++){
            org.psygrid.randomization.model.dto.Treatment t = dtoR.getTreatments()[i];
            if ( null != t ){
                this.treatments.add(t.toHibernate(refs));
            }
        }

        for (int i=0; i<dtoR.getTreatmentBlocks().length; i++){
            org.psygrid.randomization.model.dto.BlockItem bi = dtoR.getTreatmentBlocks()[i];
            if ( null != bi ){
                this.treatmentBlocks.add(bi.toHibernate(refs));
            }
        }

        for (int i=0; i<dtoR.getAllocations().length; i++){
            org.psygrid.randomization.model.dto.Allocation a = dtoR.getAllocations()[i];
            if ( null != a ){
                this.allocations.add(a.toHibernate(refs));
            }
        }

        for (int i=0; i<dtoR.getSubjects().length; i++){
            String s = dtoR.getSubjects()[i];
            if ( null != s ){
                this.subjects.add(s);
            }
        }
    }
    
}
