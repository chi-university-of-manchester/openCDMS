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
 * Class to represent a single stratum of a stratified randomizer.
 * <p>
 * As such it encapsulates the name of the stratum (e.g. Sex) and
 * the permitted values of the stratum (e.g. Male, Female).
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_strata"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class Stratum extends Persistent {

    /**
     * The name of the stratum (e.g. "Sex").
     */
    private String name;
    
    /**
     * The permitted values of the stratum (e.g. "Male, "Female").
     */
    private List<String> values = new ArrayList<String>();

    /**
     * Get the name of the stratum.
     * 
     * @return The name.
     * 
     * @hibernate.property column="c_name"
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the stratum.
     * 
     * @param name The name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the permitted values of the stratum.
     * 
     * @return The permitted values.
     * 
     * @hibernate.list table="t_stratum_values"
     * @hibernate.key column="c_stratum_id" not-null="true"
     * @hibernate.element column="c_value"
     *                    type="string"
     * @hibernate.list-index column="c_index"
     */
    public List<String> getValues() {
        return values;
    }

    /**
     * Set the permitted values of the stratum.
     * 
     * @param values The permitted values.
     */
    public void setValues(List<String> values) {
        this.values = values;
    }

    
    public org.psygrid.randomization.model.dto.Stratum toDTO(Map<Persistent, org.psygrid.randomization.model.dto.Persistent> dtoRefs){
        //check for an already existing instance of a dto object for this 
        //stratum in the set of references
        org.psygrid.randomization.model.dto.Stratum dtoS = null;
        if ( dtoRefs.containsKey(this)){
            dtoS = (org.psygrid.randomization.model.dto.Stratum)dtoRefs.get(this);
        }
        else{
            //an instance of the stratum has not already
            //been created, so create it and add it to the map of references
            dtoS = new org.psygrid.randomization.model.dto.Stratum();
            dtoRefs.put(this, dtoS);
            toDTO(dtoS, dtoRefs);
        }
        return dtoS;
    }
    
    public void toDTO(org.psygrid.randomization.model.dto.Stratum dtoS, Map<Persistent, org.psygrid.randomization.model.dto.Persistent> dtoRefs){
        super.toDTO(dtoS, dtoRefs);
        dtoS.setName(this.name);
        String[] dtoValues = new String[this.values.size()];
        for ( int i=0; i<this.values.size(); i++ ){
            dtoValues[i] = this.values.get(i);
        }
        dtoS.setValues(dtoValues);
    }
    
    public void fromDTO(org.psygrid.randomization.model.dto.Stratum dtoS, Map<org.psygrid.randomization.model.dto.Persistent, Persistent> refs){
        super.fromDTO(dtoS, refs);
        this.name = dtoS.getName();
        for ( String v: dtoS.getValues() ){
            this.values.add(v);
        }
    }
    
    
}
