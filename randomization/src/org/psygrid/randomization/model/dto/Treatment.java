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

public class Treatment extends Persistent {

    /**
     * The name of the treatment.
     */
    private String name;
    
    /**
     * The code for the treatment.
     * <p>
     * Expected to be a one-character string only.
     */
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public org.psygrid.randomization.model.hibernate.Treatment toHibernate(Map<Persistent, org.psygrid.randomization.model.hibernate.Persistent> refs){
        //check for an already existing instance of this 
        //treatment in the set of references
        org.psygrid.randomization.model.hibernate.Treatment t = null;
        if ( refs.containsKey(this)){
            t = (org.psygrid.randomization.model.hibernate.Treatment)refs.get(this);
        }
        else{
            //an instance of the treatment has not already
            //been created, so create it and add it to the map of references
            t = new org.psygrid.randomization.model.hibernate.Treatment();
            refs.put(this, t);
            t.fromDTO(this, refs);
        }
        return t;        
    }
}
