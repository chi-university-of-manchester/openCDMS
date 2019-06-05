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

public class StratumPointer extends Persistent {

    /**
     * The Stratum object that this pointer "points" to.
     */
    private Stratum stratum;
    
    /**
     * The value of the Stratum object that this pointer "points" to.
     */
    private String value;

    public Stratum getStratum() {
        return stratum;
    }

    public void setStratum(Stratum stratum) {
        this.stratum = stratum;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    
    public org.psygrid.randomization.model.hibernate.StratumPointer toHibernate(Map<Persistent, org.psygrid.randomization.model.hibernate.Persistent> refs){
        //check for an already existing instance of this 
        //stratum pointer in the set of references
        org.psygrid.randomization.model.hibernate.StratumPointer sp = null;
        if ( refs.containsKey(this)){
            sp = (org.psygrid.randomization.model.hibernate.StratumPointer)refs.get(this);
        }
        else{
            //an instance of the stratum pointer has not already
            //been created, so create it and add it to the map of references
            sp = new org.psygrid.randomization.model.hibernate.StratumPointer();
            refs.put(this, sp);
            sp.fromDTO(this, refs);
        }
        return sp;        
    }

}
