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

/**
 * Class to represent a UK postcode.
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_postcodes"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class PostCode extends Persistent {

    /**
     * The value of the postcode, in 7 character format.
     * <p>
     * For examples, valid postcodes would be AB1 1AB to ZZ999ZZ.
     */
    private String value;
    
    /**
     * The output area that the postcode is associated with.
     */
    private OutputArea outputArea;

    /**
     * Get the value of the postcode, in 7 character format.
     * <p>
     * For examples, valid postcodes would be AB1 1AB to ZZ999ZZ.
     * 
     * @return The value of the postcode.
     * 
     * @hibernate.property column="c_value" 
     *                     length="7"
     *                     unique="true"
     *                     not-null="true"
     */
    public String getValue() {
        return value;
    }

    /**
     * The value of the postcode, in 7 character format.
     * <p>
     * For examples, valid postcodes would be AB1 1AB to ZZ999ZZ.
     * 
     * @param value The value of the postcode.
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Get the output area that the postcode is associated with.
     * 
     * @return The output area.
     * 
     * @hibernate.many-to-one class="org.psygrid.transformers.impl.postcode.OutputArea"
     *                        column="c_output_area"
     *                        unique="false"
     *                        not-null="true"
     *                        cascade="all"
     */
    public OutputArea getOutputArea() {
        return outputArea;
    }

    /**
     * Set the output area that the postcode is associated with.
     * 
     * @param outputArea The output area.
     */
    public void setOutputArea(OutputArea outputArea) {
        this.outputArea = outputArea;
    }

    @Override
    public boolean equals(Object obj) {
        if ( null == obj || obj.getClass() != this.getClass() ){
            return false;
        }
        PostCode p = (PostCode) obj;
        if (this.value == null || p.value == null){ 
            return false;
        }
        return this.value.equals(p.value);
    }

    @Override
    public int hashCode() {
        if (this.value == null){
            return super.hashCode();
        }
        return this.value.hashCode();
    }
    
}
