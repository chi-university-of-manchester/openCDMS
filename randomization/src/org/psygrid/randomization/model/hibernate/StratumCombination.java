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
import java.util.List;
import java.util.Map;

/**
 * Class to represent a unique combination of values of the strata
 * defined for a stratified randomizer.
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_stratum_comb"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class StratumCombination extends Persistent {

    /**
     * List of pointers to the values of the strata which this
     * combination represents.
     * <p>
     * There should be one pointer for each stratum.
     */
    private List<StratumPointer> pointers = new ArrayList<StratumPointer>();
    
    /**
     * The randomizer that will be used to allocate subjects matching
     * this combination.
     */
    private BlockRandomizer randomizer;
    
    /**
     * Get the list of pointers to the values of the strata which this
     * combination represents.
     * <p>
     * There should be one pointer for each stratum.
     * 
     * @return The list of stratum pointers.
     * 
     * @hibernate.list cascade="all"
     * @hibernate.one-to-many class="org.psygrid.randomization.model.hibernate.StratumPointer"
     * @hibernate.key column="c_strat_comb_id" not-null="true"
     * @hibernate.list-index column="c_index"
     */
    public List<StratumPointer> getPointers() {
        return pointers;
    }

    /**
     * Set the list of pointers to the values of the strata which this
     * combination represents.
     * <p>
     * There should be one pointer for each stratum.
     * 
     * @param pointers The list of stratum pointers.
     */
    public void setPointers(List<StratumPointer> pointers) {
        this.pointers = pointers;
    }

    /**
     * Get the randomizer that will be used to allocate subjects matching
     * this combination.
     * 
     * @return The randomizer.
     * 
     * @hibernate.many-to-one class="org.psygrid.randomization.model.hibernate.BlockRandomizer"
     *                        column="c_rdmzr_id"
     *                        not-null="true"
     *                        cascade="all"
     */
    public BlockRandomizer getRandomizer() {
        return randomizer;
    }

    /**
     * Set the randomizer that will be used to allocate subjects matching
     * this combination.
     * 
     * @param randomizer The randomizer.
     */
    public void setRandomizer(BlockRandomizer randomizer) {
        this.randomizer = randomizer;
    }

    
    public org.psygrid.randomization.model.dto.StratumCombination toDTO(Map<Persistent, org.psygrid.randomization.model.dto.Persistent> dtoRefs){
        //check for an already existing instance of a dto object for this 
        //stratum combination in the set of references
        org.psygrid.randomization.model.dto.StratumCombination dtoSC = null;
        if ( dtoRefs.containsKey(this)){
            dtoSC = (org.psygrid.randomization.model.dto.StratumCombination)dtoRefs.get(this);
        }
        else{
            //an instance of the stratum combination has not already
            //been created, so create it and add it to the map of references
            dtoSC = new org.psygrid.randomization.model.dto.StratumCombination();
            dtoRefs.put(this, dtoSC);
            toDTO(dtoSC, dtoRefs);
        }
        return dtoSC;
    }
    
    public void toDTO(org.psygrid.randomization.model.dto.StratumCombination dtoSC, Map<Persistent, org.psygrid.randomization.model.dto.Persistent> dtoRefs){
        super.toDTO(dtoSC, dtoRefs);
        if ( null != this.randomizer ){
            dtoSC.setRandomizer(this.randomizer.toDTO(dtoRefs));
        }
        org.psygrid.randomization.model.dto.StratumPointer[] dtoSPs = new org.psygrid.randomization.model.dto.StratumPointer[this.pointers.size()];
        for ( int i=0; i<this.pointers.size(); i++ ){
            StratumPointer sp = this.pointers.get(i);
            dtoSPs[i] = sp.toDTO(dtoRefs);
        }
        dtoSC.setPointers(dtoSPs);
    }
    
    public void fromDTO(org.psygrid.randomization.model.dto.StratumCombination dtoSC, Map<org.psygrid.randomization.model.dto.Persistent, Persistent> refs){
        super.fromDTO(dtoSC, refs);
        if ( null != dtoSC.getRandomizer() ){
            this.randomizer = dtoSC.getRandomizer().toHibernate(refs);
        }
        for (int i=0; i<dtoSC.getPointers().length; i++){
            org.psygrid.randomization.model.dto.StratumPointer sp = dtoSC.getPointers()[i];
            if ( null != sp ){
                this.pointers.add(sp.toHibernate(refs));
            }
        }
    }
    
}
