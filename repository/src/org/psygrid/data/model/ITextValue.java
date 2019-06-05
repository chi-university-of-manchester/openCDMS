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
 * Interface to represent a value of a response to a text entry.
 * <p>
 * The amount of data that can be held is restricted.
 * 
 * @author Rob Harper
 *
 */
public interface ITextValue extends IValue {

    /**
     * The maximum number of chars that can be held by a
     * textual value.
     */
    public static final int MAX_CHARS = 256;
    
    /**
     * Get the textual value of the response.
     * 
     * @return The textual value.
     */
    public String getValue();

    /**
     * Set the textual value of the response.
     * 
     * @param value The textual value.
     * @throws ModelException if the value in the argument has
     * more characters than the maximum allowed number, or if
     * the value is read-only.
     */
    public void setValue(String value) throws ModelException;

    /**
     * Return a copy of the text value object.
     * 
     * @return A copy of the text value object.
     */
    public ITextValue copy();
}