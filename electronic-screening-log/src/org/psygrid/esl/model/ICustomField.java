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

package org.psygrid.esl.model;

import java.util.Map;

/**
 * Interface to represent a custom field for a project in the ESL.
 * 
 * @author Rob Harper
 *
 */
public interface ICustomField extends IPersistent {

	/**
	 * Get the name of the field.
	 * 
	 * @return The name.
	 */
	public String getName();

	/**
	 * Set the name of the field.
	 * 
	 * @param name The name.
	 */
	public void setName(String name);

	/**
	 * Get the number of values for the field.
	 * 
	 * @return Number of values.
	 */
	public int getValueCount();
	
	/**
	 * Add a value to the fields list of allowed values.
	 * 
	 * @param value The value to add.
	 */
	public void addValue(String value);
	
	/**
	 * Get a value from the fields list of allowed values.
	 * 
	 * @param index The index of the value to get.
	 * @return The value at the specified index.
	 * @throws ModelException if no value exists at the given index.
	 */
	public String getValue(int index) throws ModelException;
	
	/**
	 * Remove a value from the fields list of allowed values.
	 * 
	 * @param index The index of the value to remove.
	 * @throws ModelException if no value exists at the given index.
	 */
	public void removeValue(int index) throws ModelException;
	
	public org.psygrid.esl.model.dto.CustomField toDTO(Map<IPersistent, org.psygrid.esl.model.dto.Persistent> dtoRefs);
	
}
