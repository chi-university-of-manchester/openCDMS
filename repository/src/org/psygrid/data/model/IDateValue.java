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

import java.util.Date;

import org.psygrid.data.model.hibernate.ModelException;

/**
 * Interface to represent a value of a response to a date entry.
 * 
 * @author Rob Harper
 *
 */
public interface IDateValue extends IValue {

    /**
     * Get the value of the date value.
     * 
     * @return The value.
     */
    public Date getValue();
    
    /**
     * Set the value of the date value.
     * 
     * @param value The value.
     * @throws ModelException if the value is read-only.
     */
    public void setValue(Date value) throws ModelException;
    
    /**
     * Get the month component of the date.
     * 
     * @return The month component.
     */
    public Integer getMonth();

    /**
     * Set the month component of the date.
     * 
     * @param month The month component.
     */
    public void setMonth(Integer month);

    /**
     * Get the year component of the date.
     * 
     * @return The year component.
     */
    public Integer getYear();

    /**
     * Set the year component of the date.
     * 
     * @param year The year component.
     */
    public void setYear(Integer year);

    /**
     * Return a copy of the date value object.
     * 
     * @return A copy of the date value object.
     */
    public IDateValue copy();
}
