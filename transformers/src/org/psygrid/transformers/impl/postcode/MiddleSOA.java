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

package org.psygrid.transformers.impl.postcode;

import java.util.HashSet;
import java.util.Set;

/**
 * Class to represent a middle-layer super output area.
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_middle_soas"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class MiddleSOA extends Persistent {

    /**
     * The code for the middle-layer super output area.
     * <p>
     * The code should be in a 9-digit alphanumeric format.
     */
    private String code;
    
    /**
     * The collection of lower-layer super output areas that
     * are associated with the middle-layer super output area.
     */
    private Set<LowerSOA> lowerSoas = new HashSet<LowerSOA>();

    /**
     * Get the code for the middle-layer super output area.
     * <p>
     * The code should be in a 9-digit alphanumeric format.
     * 
     * @return The code.
     * 
     * @hibernate.property column="c_code" 
     *                     length="9"
     *                     unique="true"
     *                     not-null="true"
     */
    public String getCode() {
        return code;
    }

    /**
     * Set the code for the middle-layer super output area.
     * <p>
     * The code should be in a 9-digit alphanumeric format.
     * 
     * @param code The code.
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Get the collection of lower-layer super output areas that
     * are associated with the middle-layer super output area.
     * 
     * @return The collection of lower-layer super output areas.
     * 
     * @hibernate.set inverse="true" cascade="all"
     * @hibernate.one-to-many class="org.psygrid.transformers.impl.postcode.LowerSOA"
     * @hibernate.key column="c_middle_soa"
     */
    public Set<LowerSOA> getLowerSoas() {
        return lowerSoas;
    }

    /**
     * Set the collection of lower-layer super output areas that
     * are associated with the middle-layer super output area.
     * 
     * @param lowerSoas The collection of lower-layer super output areas.
     */
    public void setLowerSoas(Set<LowerSOA> lowerSoas) {
        this.lowerSoas = lowerSoas;
    }
    
    @Override
    public boolean equals(Object obj) {
        if ( null == obj || obj.getClass() != this.getClass() ){
            return false;
        }
        MiddleSOA p = (MiddleSOA) obj;
        if (this.code == null || p.code == null){ 
            return false;
        }
        return this.code.equals(p.code);
    }

    @Override
    public int hashCode() {
        if (this.code == null){
            return super.hashCode();
        }
        return this.code.hashCode();
    }
    
}
