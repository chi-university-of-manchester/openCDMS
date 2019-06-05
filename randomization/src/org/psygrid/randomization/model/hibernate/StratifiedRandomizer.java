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
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.psygrid.randomization.model.DuplicateSubjectException;
import org.psygrid.randomization.model.RandomizerException;

/**
 * Class to represent a stratified randomizer.
 * <p>
 * A stratified randomizer is used to ensure that treatment allocation
 * for subjects is balanced across groups of subjects who share certain
 * key attributes - the strata.
 * <p>
 * As an example, we could select two strata for a trial; Sex and Centre.
 * Obviously there are two permitted values for Sex (Male, Female), and 
 * suppose that there are four different centres. This gives us 2*4=8 
 * unique groups defined by these two strata. Each of these groups will have 
 * its own independent randomizer (Random Permuted Block, or Random Permuted 
 * Block with Random Block Length). When a subject is randomized there values
 * for the strata are passed into the randomization process. These will be
 * used to select which of the groups the subject belongs to, then they
 * will be allocated a treatment using this group's randomizer.
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_strat_rdmzrs"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class StratifiedRandomizer extends Randomizer {

    /**
     * The strata defined for the randomizer.
     */
    private List<Stratum> strata = new ArrayList<Stratum>();
    
    /**
     * The unique combinations of the values of the strata.
     */
    private List<StratumCombination> combinations = new ArrayList<StratumCombination>();
    
    /**
     * Map to hold the parameters for a randomization.
     * <p>
     * The parameters for a stratified randomization will be the values
     * for each of the defined strata.
     */
    private transient Map<String, String> parameters = new HashMap<String, String>();
    
    public StratifiedRandomizer(){}
    
    public StratifiedRandomizer(String name){
        super(name);
    }
    
    /**
     * Get the unique combinations of the values of the strata.
     * 
     * @return The unique combinations.
     * 
     * @hibernate.list cascade="all"
     * @hibernate.one-to-many class="org.psygrid.randomization.model.hibernate.StratumCombination"
     * @hibernate.key column="c_strat_rdmzr_id" not-null="true"
     * @hibernate.list-index column="c_index"
     */
    @SuppressWarnings("unused")
    public List<StratumCombination> getCombinations() {
        return combinations;
    }

    /**
     * Set the unique combinations of the values of the strata.
     * 
     * @param combinations The unique combinations.
     */
    @SuppressWarnings("unused")
    private void setCombinations(List<StratumCombination> combinations) {
        this.combinations = combinations;
    }

    /**
     * Get the strata defined for the randomizer.
     * 
     * @return The strata.
     * 
     * @hibernate.list cascade="all"
     * @hibernate.one-to-many class="org.psygrid.randomization.model.hibernate.Stratum"
     * @hibernate.key column="c_strat_rdmzr_id" not-null="true"
     * @hibernate.list-index column="c_index"
     */
    @SuppressWarnings("unused")
    public List<Stratum> getStrata() {
        return strata;
    }

    /**
     * Set the strata defined for the randomizer.
     * 
     * @param strata The strata.
     */
    @SuppressWarnings("unused")
    private void setStrata(List<Stratum> strata) {
        this.strata = strata;
    }
    
    /**
     * Add a new stratum to the randomizer list of strata.
     * 
     * @param stratum The new stratum.
     * 
     * @throws RandomizerException if the new stratum is 
     * <code>null</code>, or if the combinations have already
     * been generated.
     */
    public void addStratum(Stratum stratum) throws RandomizerException {
        if ( null == stratum ){
            throw new RandomizerException("Cannot add a null stratum");
        }
        if ( combinations.size() > 0 ){
            throw new RandomizerException("Stratum combinations have already been generated; it is no longer possible to modify the strata in any way.");
        }
        this.strata.add(stratum);
    }

    /**
     * Generate the stratum combinations from the strata defined
     * for the randomizer.
     * <p>
     * This should only be called after the strata for the randomizer
     * are correctly defined; once this method has been called it will
     * not be possible to modify the strata.
     * <p>
     * Default min and max block size values of 2 and 3 are used for
     * the block randomizer for each stratum combination.
     * 
     * @param blockRandomizerClass The class name of the subclass of BlockRandomizer
     * to instantiate as the block randomizer for each stratum combination. 
     * @throws RandomizerException if the combinations have already been
     * generated.
     */
    public void generateCombinations(String blockRandomizerClass) throws RandomizerException {
        generateCombinations(blockRandomizerClass, 2, 3);
    }
    
    /**
     * Generate the stratum combinations from the strata defined
     * for the randomizer.
     * <p>
     * This should only be called after the strata for the randomizer
     * are correctly defined; once this method has been called it will
     * not be possible to modify the strata.
     * <p>
     * Use specified min and max block size values are used for
     * the block randomizer for each stratum combination.
     * 
     * @param blockRandomizerClass The class name of the subclass of BlockRandomizer
     * to instantiate as the block randomizer for each stratum combination. 
     * @param minBlockSize Minimum block size.
     * @param maxBlockSize Maximum block size.
     * @throws RandomizerException if the combinations have already been
     * generated.
     */
    public void generateCombinations(String blockRandomizerClass, int minBlockSize, int maxBlockSize) throws RandomizerException {
        if ( combinations.size() > 0 ){
            throw new RandomizerException("Stratum combinations have already been generated.");
        }
        int nComb = 1;
        for ( Stratum s: strata ){
            nComb*=s.getValues().size();
        }
        int[] counters = new int[strata.size()];
        for ( int i=0; i<nComb; i++ ){
            StratumCombination sc = new StratumCombination();
            BlockRandomizer br = null;
            try{
                br = (BlockRandomizer)Class.forName(blockRandomizerClass).getConstructor(new Class[]{java.lang.String.class}).newInstance(new Object[]{this.getName()+"_"+i});
            }
            catch(Exception ex){
                throw new RandomizerException("Unable to create block randomizer for stratum combination", ex);
            }
            br.setMinBlockSize(minBlockSize);
            br.setMaxBlockSize(maxBlockSize);
            sc.setRandomizer(br);
            for ( int j=0; j<strata.size(); j++ ){
                StratumPointer p = new StratumPointer();
                p.setStratum(strata.get(j));
                p.setValue(strata.get(j).getValues().get(counters[j]));
                sc.getPointers().add(p);
            }
            combinations.add(sc);
            boolean incNext = true;
            for ( int j=counters.length-1; j>=0; j-- ){
                if ( incNext ){
                    counters[j]++;
                    if ( counters[j] > (strata.get(j).getValues().size()-1) ){
                        counters[j] = 0;
                        incNext = true;
                    }
                    else{
                        incNext = false;
                    }
                }
            }
        }
    }
    
    /**
     * Create the Random Number Generators for all of the randomizers used
     * by the stratified randomizer.
     * <p>
     * Enough seeds must be provided for all of the RNGs (there is one RNG
     * for each stratum combination).
     * 
     * @param seeds The array of seeds for the RNGs.
     * @throws RandomizerException if not enough seeds have been provided.
     */
    public void createRngs(long[] seeds) throws RandomizerException {
        //check that there are enough seeds
        if ( seeds.length < numCombinations() ){
            throw new RandomizerException("Not enough seeds have been provided; the randomizer requires "+numCombinations()+" seeds.");
        }
        for ( int i=0; i<combinations.size(); i++ ){
            combinations.get(i).getRandomizer().createRng(seeds[i]);
        }
    }
    
    /**
     * Add a treatment, with the given name and code.
     * <p>
     * The new treatment will be added to each of the individual randomizers
     * defined for the stratum combinations, a treatment cannot be added
     * until these combinations have been generated.
     * 
     * @param name The name of the treatment.
     * @param code The code of the treatment.
     * @throws RandomizerException if the stratum combinations (and
     * hence the individual randomizers) have not been generated.
     */
    public void addTreatment(String name, String code) throws RandomizerException {
        //check that the combinations have been generated
        if ( 0 == combinations.size() ){
            throw new RandomizerException("It is not possible to add a treatment until the stratum combinations have been generated.");
        }
        for ( StratumCombination sc: combinations ){
            sc.getRandomizer().addTreatment(name, code);
        }
    }
    
    /**
     * Return the number of combinations that have been derived
     * from the assigned strata.
     * <p>
     * As each combination has its own randomizer the same number
     * of seeds needs to be provided when creating the Random
     * Number Generators.
     * 
     * @return The number of combinations.
     */
    public int numCombinations(){
        return this.combinations.size();
    }
    
    /**
     * Set a parameter for the randomizer.
     * <p>
     * In practice, for the stratified randomizer a parameter should be set
     * for each of the strata, with the key being the name of the stratum and the
     * value being one of the permitted values for the stratum.
     * 
     * @param key The key for the parameter.
     * @param value The value of the parameter.
     */
    public void setParameter(String key, String value){
        parameters.put(key, value);
    }
    
    public String getParameter(String key){
        return parameters.get(key);
    }
    
    
    public void initialize() {
        for ( StratumCombination sc: combinations ){
            sc.getRandomizer().initialize();
        }
    }

    /**
     * Allocate a subject to a treatment.
     * <p>
     * Before calling this method setParameter should be called
     * once for each of the strata to set the values relevant for
     * the subject.
     * 
     * @param subject The name/code of the subject to be allocated a
     * treatment.
     * @return The code of the treatment they have been allocated to.
     * @throws RandomizerException if the parameters have not been
     * correctly initialized.
     */
    public String allocate(String subject) throws RandomizerException, DuplicateSubjectException {
    	//check all stratum combinations for an existing allocation for the subject
    	//this is needed in case the strata parameters for the subject have been changed
    	//after it was already randomized
    	checkForAllocation(subject);
        //find the correct stratum combination for the current parameters
        StratumCombination comb = findCombination();
        //allocate the subject using this combination's randomizer
        return comb.getRandomizer().allocate(subject);
    }
    
    /**
     * Find the stratum combination that matches the parameters set
     * for the randomizer.
     * 
     * @return The stratum combination.
     * @throws RandomizerException if no parameter has been set for one of 
     * the strata
     */
    private StratumCombination findCombination() throws RandomizerException {
        //check the parameters - there should be one parameter
        //for each of the strata, and the value should be one of
        //the permitted values for the stratum
        for ( Stratum s: strata ){
            String value = parameters.get(s.getName());
            if ( null == value ){
                throw new RandomizerException("No parameter has been set for the stratum '"+s.getName()+"'.");
            }
            if ( !s.getValues().contains(value) ){
                throw new RandomizerException("The value '"+value+"' for the stratum '"+s.getName()+"' is not valid.");
            }
        }
        
        //find the correct StratumCombination for the given parameters
        StratumCombination comb = null;
        for ( StratumCombination sc: combinations ){
            boolean found = true;
            for ( StratumPointer sp: sc.getPointers() ){
                String value = parameters.get(sp.getStratum().getName());
                if ( !value.equals(sp.getValue()) ){
                    found = false;
                }
            }
            if (found){
                comb = sc;
                break;
            }
        }
        
        if ( null == comb ){
            throw new RandomizerException("No stratum combination found for the current parameters.");
        }
        
        return comb;
        
    }
    
    
    public boolean checkIntegrity() throws RandomizerException {
        //TODO is this sufficient? Or do we need to store subject plus
        //parameters for all randomizations at this level?
        boolean allOk = true;
        for ( StratumCombination sc: combinations ){
            allOk &= sc.getRandomizer().checkIntegrity();
        }
        return allOk;
    }

    private void checkForAllocation(String subject) throws DuplicateSubjectException {
        for ( StratumCombination sc: combinations ){
            try{
                String treatment = sc.getRandomizer().getAllocation(subject);
                if ( null != treatment ){
                	throw new DuplicateSubjectException("The subject '"+subject+
                    "' has already been allocated to a treatment arm by this randomizer.");
                }
            }
            catch(RandomizerException ex){
                //exception thrown by RpmrblRandomizer when it can't find an
                //allocation for the given subject. Do nothing, as for the stratified
                //randomizer we potentially have other RpmrblRandomizers to check.
                //If none of the individual randomizers contain the subject then an
                //exception is thrown below, if treatment is still null after all
                //randomizers have been checked.
            }
        }
    }
    
    
    public String getAllocation(String subject) throws RandomizerException {
        String treatment = null;
        for ( StratumCombination sc: combinations ){
            try{
                treatment = sc.getRandomizer().getAllocation(subject);
            }
            catch(RandomizerException ex){
                //exception thrown by RpmrblRandomizer when it can't find an
                //allocation for the given subject. Do nothing, as for the stratified
                //randomizer we potentially have other RpmrblRandomizers to check.
                //If none of the individual randomizers contain the subject then an
                //exception is thrown below, if treatment is still null after all
                //randomizers have been checked.
            }
        }
        if ( null == treatment ){
            throw new RandomizerException("No allocation exists for the subject '"+subject+"'.");
        }
        return treatment;
    }
    
    public String[] getRandomisedParticipantsWithinTimeframe(Date startBoundaryInclusive, Date endBoundaryDelimiter){
    	
    	List<String> participantsRandomised = new ArrayList<String>();
    	
    	for ( StratumCombination sc: combinations ){
    		BlockRandomizer bR = sc.getRandomizer();
    		String[] randomisations = bR.getRandomisedParticipantsWithinTimeframe(startBoundaryInclusive, endBoundaryDelimiter);
    		participantsRandomised.addAll(Arrays.asList(randomisations));
    	}
    		
    	int numParticipants = participantsRandomised.size();
    	String[] theParticipants = new String[numParticipants];
    	return participantsRandomised.toArray(theParticipants);
    }

    
    public Treatment getAllocation(String subject, Date date) throws RandomizerException {
        Treatment treatment = null;
        for ( StratumCombination sc: combinations ){
            try{
                treatment = sc.getRandomizer().getAllocation(subject, date);
            }
            catch(RandomizerException ex){
                //exception thrown by RpmrblRandomizer when it can't find an
                //allocation for the given subject. Do nothing, as for the stratified
                //randomizer we potentially have other RpmrblRandomizers to check.
                //If none of the individual randomizers contain the subject then an
                //exception is thrown below, if treatment is still null after all
                //randomizers have been checked.
            }
        }
        if ( null == treatment ){
            throw new RandomizerException("No allocation exists for the subject '"+subject+"' for the date "+date.toString());
        }
        return treatment;
    }
    
    
    public Map<String, String> getAllAllocations() {
        Map<String, String> map = new TreeMap<String, String>();
        for ( StratumCombination sc: combinations ){
            map.putAll(sc.getRandomizer().getAllAllocations());
        }
        return map;
    }

    
    public Map<String, Long> getRandomizerStatistics() throws RandomizerException {
        Map<String, Long> map = new HashMap<String, Long>();
        if ( parameters.size() > 0 ){
            //find the correct stratum combination for the current parameters
            StratumCombination comb = findCombination();
            return comb.getRandomizer().getRandomizerStatistics();
        }
        else{
            for ( StratumCombination sc: combinations ){
                Map<String, Long> m = sc.getRandomizer().getRandomizerStatistics();
                for ( Entry<String, Long> e: m.entrySet() ){
                    if ( map.containsKey(e.getKey()) ){
                        map.put(e.getKey(), new Long(e.getValue().longValue()+map.get(e.getKey()).longValue()));
                    }
                    else{
                        map.put(e.getKey(), e.getValue());
                    }
                }
            }
            //TODO sort the map by key (treatment arm)
            return map;
        }
    }

    public org.psygrid.randomization.model.dto.StratifiedRandomizer toDTO(){
        Map<Persistent, org.psygrid.randomization.model.dto.Persistent> dtoRefs = new HashMap<Persistent, org.psygrid.randomization.model.dto.Persistent>();
        org.psygrid.randomization.model.dto.StratifiedRandomizer dtoR = toDTO(dtoRefs);
        dtoRefs = null;
        return dtoR;
      
    }
    
    public org.psygrid.randomization.model.dto.StratifiedRandomizer toDTO(Map<Persistent, org.psygrid.randomization.model.dto.Persistent> dtoRefs){
        //check for an already existing instance of a dto object for this 
        //stratified randomizer in the set of references
        org.psygrid.randomization.model.dto.StratifiedRandomizer dtoR = null;
        if ( dtoRefs.containsKey(this)){
            dtoR = (org.psygrid.randomization.model.dto.StratifiedRandomizer)dtoRefs.get(this);
        }
        else{
            //an instance of the stratified randomizer has not already
            //been created, so create it and add it to the map of references
            dtoR = new org.psygrid.randomization.model.dto.StratifiedRandomizer();
            dtoRefs.put(this, dtoR);
            toDTO(dtoR, dtoRefs);
        }
        return dtoR;
    }
    
    public void toDTO(org.psygrid.randomization.model.dto.StratifiedRandomizer dtoR, Map<Persistent, org.psygrid.randomization.model.dto.Persistent> dtoRefs){
        super.toDTO(dtoR, dtoRefs);
        org.psygrid.randomization.model.dto.Stratum[] dtoSs = new org.psygrid.randomization.model.dto.Stratum[this.strata.size()];
        for (int i=0; i<this.strata.size(); i++){
            Stratum s = strata.get(i);
            dtoSs[i] = s.toDTO(dtoRefs);
        }        
        dtoR.setStrata(dtoSs);
        org.psygrid.randomization.model.dto.StratumCombination[] dtoSCs = new org.psygrid.randomization.model.dto.StratumCombination[this.combinations.size()];
        for (int i=0; i<this.combinations.size(); i++){
            StratumCombination sc = combinations.get(i);
            dtoSCs[i] = sc.toDTO(dtoRefs);
        }        
        dtoR.setCombinations(dtoSCs);
    }
    
    public void fromDTO(org.psygrid.randomization.model.dto.StratifiedRandomizer dtoR, Map<org.psygrid.randomization.model.dto.Persistent, Persistent> refs){
        super.fromDTO(dtoR, refs);

        for (int i=0; i<dtoR.getStrata().length; i++){
            org.psygrid.randomization.model.dto.Stratum s = dtoR.getStrata()[i];
            if ( null != s ){
                this.strata.add(s.toHibernate(refs));
            }
        }

        for (int i=0; i<dtoR.getCombinations().length; i++){
            org.psygrid.randomization.model.dto.StratumCombination sc = dtoR.getCombinations()[i];
            if ( null != sc ){
                this.combinations.add(sc.toHibernate(refs));
            }
        }

    }
    
    
}
