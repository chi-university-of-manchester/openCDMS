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

public class StratumCombination extends Persistent {

    /**
     * List of pointers to the values of the strata which this
     * combination represents.
     * <p>
     * There should be one pointer for each stratum.
     */
    private StratumPointer[] pointers = new StratumPointer[0];
    
    /**
     * The randomizer that will be used to allocate subjects matching
     * this combination.
     */
    private BlockRandomizer randomizer;
    
    public StratumPointer[] getPointers() {
        return pointers;
    }

    public void setPointers(StratumPointer[] pointers) {
        this.pointers = pointers;
    }

    public BlockRandomizer getRandomizer() {
        return randomizer;
    }

    public void setRandomizer(BlockRandomizer randomizer) {
        this.randomizer = randomizer;
    }

    
    public org.psygrid.randomization.model.hibernate.StratumCombination toHibernate(Map<Persistent, org.psygrid.randomization.model.hibernate.Persistent> refs){
        //check for an already existing instance of this 
        //stratum combination in the set of references
        org.psygrid.randomization.model.hibernate.StratumCombination sc = null;
        if ( refs.containsKey(this)){
            sc = (org.psygrid.randomization.model.hibernate.StratumCombination)refs.get(this);
        }
        else{
            //an instance of the stratum combination has not already
            //been created, so create it and add it to the map of references
            sc = new org.psygrid.randomization.model.hibernate.StratumCombination();
            refs.put(this, sc);
            sc.fromDTO(this, refs);
        }
        return sc;        
    }

}
