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

public class Stratum extends Persistent {

    /**
     * The name of the stratum (e.g. "Sex").
     */
    private String name;
    
    /**
     * The permitted values of the stratum (e.g. "Male, "Female").
     */
    private String[] values = new String[0];

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getValues() {
        return values;
    }

    public void setValues(String[] values) {
        this.values = values;
    }

    
    public org.psygrid.randomization.model.hibernate.Stratum toHibernate(Map<Persistent, org.psygrid.randomization.model.hibernate.Persistent> refs){
        //check for an already existing instance of this 
        //stratum in the set of references
        org.psygrid.randomization.model.hibernate.Stratum a = null;
        if ( refs.containsKey(this)){
            a = (org.psygrid.randomization.model.hibernate.Stratum)refs.get(this);
        }
        else{
            //an instance of the stratum has not already
            //been created, so create it and add it to the map of references
            a = new org.psygrid.randomization.model.hibernate.Stratum();
            refs.put(this, a);
            a.fromDTO(this, refs);
        }
        return a;        
    }

}
