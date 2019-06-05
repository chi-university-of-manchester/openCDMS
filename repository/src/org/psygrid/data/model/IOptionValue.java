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
import org.psygrid.data.model.hibernate.Option;

/**
 * Interface to represent a value of a response to an
 * option entry.
 * 
 * @author Rob Harper
 *
 */
public interface IOptionValue extends IValue {

    public static final String OPTION_CODE = "Code";
    public static final String OPTION_TEXT = "Text";
    public static final String OPTION_BOTH = "Both";
    
    /**
     * Get the option value of the response.
     * 
     * @return The option value
     */
    public Option getValue();
    
    /**
     * Set the option value of the response.
     * 
     * @param option The option value.
     * @throws ModelException if the value is read-only.
     */
    public void setValue(Option option) throws ModelException;
    
    /**
     * Get the text value that is completed by the user if the associated
     * option entry is editable, and the "Other" option is selected.
     * 
     * @return The text value.
     */
    public String getTextValue();

    /**
     * Set the text value that is completed by the user if the associated
     * option entry is editable, and the "Other" option is selected.
     * 
     * @param textValue The text value.
     */
    public void setTextValue(String textValue);

    /**
     * Return a copy of the option value object.
     * 
     * @return A copy of the option value object.
     */
    public IOptionValue copy();
}
