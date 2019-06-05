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

package org.psygrid.data.model;

import org.psygrid.data.model.hibernate.ModelException;

/**
 * Interface to represent a value of a response to a
 * numeric entry.
 * 
 * @author Rob Harper
 *
 */
public interface INumericValue extends IValue {

    /**
     * Get the numeric value of the response.
     * 
     * @return The numeric value.
     */
    public Double getValue();
    
    /**
     * Set the numeric value of the response.
     * 
     * @param value The numeric value.
     * @throws ModelException if the value is read-only.
     */
    public void setValue(Double value) throws ModelException;

    /**
     * Return a copy of the numeric value object.
     * 
     * @return A copy of the numeric value object.
     */
    public INumericValue copy();
}
