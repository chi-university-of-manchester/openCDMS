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
 * Class to represent a lower-layer super output area.
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_lower_soas"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class LowerSOA extends Persistent {

    /**
     * The code for the lower-layer super output area.
     * <p>
     * The code should be in a 9-digit alphanumeric format.
     */
    private String code;
    
    /**
     * The collection of output area that are associated with
     * the lower-layer super output area.
     */
    private Set<OutputArea> outputAreas = new HashSet<OutputArea>();
    
    /**
     * The middle-layer super output area that the lower-layer
     * super output area is associated with.
     */
    private MiddleSOA middleSoa ;

    /**
     * Get the code for the lower-layer super output area.
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
     * Set the code for the lower-layer super output area.
     * <p>
     * The code should be in a 9-digit alphanumeric format.
     * 
     * @param code The code.
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Get the collection of output area that are associated with
     * the lower-layer super output area.
     * 
     * @return The collection of output areas.
     * 
     * @hibernate.set inverse="true" cascade="all"
     * @hibernate.one-to-many class="org.psygrid.transformers.impl.postcode.OutputArea"
     * @hibernate.key column="c_lower_soa"
     */
    public Set<OutputArea> getOutputAreas() {
        return outputAreas;
    }

    /**
     * Set the collection of output area that are associated with
     * the lower-layer super output area.
     *  
     * @param outputAreas The collection of output areas.
     */
    public void setOutputAreas(Set<OutputArea> outputAreas) {
        this.outputAreas = outputAreas;
    }

    /**
     * Get the middle-layer super output area that the lower-layer
     * super output area is associated with.
     * 
     * @return The middle-layer super output area.
     * 
     * @hibernate.many-to-one class="org.psygrid.transformers.impl.postcode.MiddleSOA"
     *                        column="c_middle_soa"
     *                        unique="false"
     *                        not-null="true"
     *                        cascade="none"
     */
    public MiddleSOA getMiddleSoa() {
        return middleSoa;
    }

    /**
     * Set the middle-layer super output area that the lower-layer
     * super output area is associated with.
     * 
     * @param middleSoa The middle-layer super output area.
     */
    public void setMiddleSoa(MiddleSOA middleSoa) {
        this.middleSoa = middleSoa;
    }
    
    @Override
    public boolean equals(Object obj) {
        if ( null == obj || obj.getClass() != this.getClass() ){
            return false;
        }
        LowerSOA p = (LowerSOA) obj;
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
