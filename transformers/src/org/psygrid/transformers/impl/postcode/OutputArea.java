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
 * Class to represent an output area.
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_output_areas"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class OutputArea extends Persistent {

    /**
     * The code for the output area.
     * <p>
     * The code should be in a 10-digit alphanumeric format.
     */
    private String code;
    
    /**
     * The lower-layer super output area that the output area
     * is associated with.
     */
    private LowerSOA lowerSoa;

    /**
     * The collection of postcodes that are associated with the
     * output area.
     */
    private Set<PostCode> postCodes = new HashSet<PostCode>();
    
    /**
     * Get the code for the output area.
     * <p>
     * The code should be in a 10-digit alphanumeric format.
     * 
     * @return The code for the output area.
     * 
     * @hibernate.property column="c_code" 
     *                     length="10"
     *                     unique="true"
     *                     not-null="true"
     */
    public String getCode() {
        return code;
    }

    /**
     * Set the code for the output area.
     * <p>
     * The code should be in a 10-digit alphanumeric format.
     * 
     * @param code The code for the output area.
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Get the lower-layer super output area that the output area
     * is associated with.
     * 
     * @return The lower-layer super output area.
     * 
     * @hibernate.many-to-one class="org.psygrid.transformers.impl.postcode.LowerSOA"
     *                        column="c_lower_soa"
     *                        unique="false"
     *                        not-null="true"
     *                        cascade="none"
     */
    public LowerSOA getLowerSoa() {
        return lowerSoa;
    }

    /**
     * Set the lower-layer super output area that the output area
     * is associated with.
     *  
     * @param lowerSoa The lower-layer super output area.
     */
    public void setLowerSoa(LowerSOA lowerSoa) {
        this.lowerSoa = lowerSoa;
    }

    /**
     * Get the collection of postcodes that are associated with the
     * output area.
     * 
     * @return The collection of postcodes.
     * 
     * @hibernate.set inverse="true" cascade="all"
     * @hibernate.one-to-many class="org.psygrid.transformers.impl.postcode.PostCode"
     * @hibernate.key column="c_output_area"
     */
    public Set<PostCode> getPostCodes() {
        return postCodes;
    }

    /**
     * Set the collection of postcodes that are associated with the
     * output area.
     * 
     * @param postCodes The collection of postcodes.
     */
    public void setPostCodes(Set<PostCode> postCodes) {
        this.postCodes = postCodes;
    }
    
    @Override
    public boolean equals(Object obj) {
        if ( null == obj || obj.getClass() != this.getClass() ){
            return false;
        }
        OutputArea p = (OutputArea) obj;
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
