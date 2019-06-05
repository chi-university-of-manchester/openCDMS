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

public class PersistableRNG extends Persistent {

    /**
     * The number used to seed the RNG.
     */
    private long seed;
    
    /**
     * The count of random numbers generated to date.
     */
    private long count;

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }
    
    public org.psygrid.randomization.model.hibernate.PersistableRNG toHibernate(Map<Persistent, org.psygrid.randomization.model.hibernate.Persistent> refs){
        //check for an already existing instance of this 
        //RNG in the set of references
        org.psygrid.randomization.model.hibernate.PersistableRNG r = null;
        if ( refs.containsKey(this)){
            r = (org.psygrid.randomization.model.hibernate.PersistableRNG)refs.get(this);
        }
        else{
            //an instance of the RNG has not already
            //been created, so create it and add it to the map of references
            r = new org.psygrid.randomization.model.hibernate.PersistableRNG();
            refs.put(this, r);
            r.fromDTO(this, refs);
        }
        return r;        
    }
}
