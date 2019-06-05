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
 * Interface to represent a value of a response to an
 * integer entry.
 * 
 * @author Rob Harper
 *
 */
public interface IIntegerValue extends IValue {

    /**
     * Get the integer value of the response.
     * 
     * @return The integer value.
     */
    public Integer getValue();
    
    /**
     * Set the integer value of the response.
     * 
     * @param value The integer value.
     * @throws ModelException if the value is read-only.
     */
    public void setValue(Integer value) throws ModelException;

    /**
     * Return a copy of the integer value object.
     * 
     * @return A copy of the integer value object.
     */
    public IIntegerValue copy();
}
