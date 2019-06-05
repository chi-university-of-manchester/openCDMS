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
 * Interface to represent the value of a custom field for a subject.
 * 
 * @author Rob Harper
 *
 */
public interface ICustomValue extends IPersistent {
	
	/**
	 * Get the name of the custom field that the value is for.
	 * 
	 * @return The name.
	 */
	public String getName();

	/**
	 * Set the name of the custom field that the value is for.
	 * 
	 * @param name The name.
	 */
	public void setName(String name);

	/**
	 * Get the value. Should be one of the allowed values specified for 
	 * the field. 
	 * 
	 * @return The value.
	 */
	public String getValue();

	/**
	 * Set the value. Should be one of the allowed values specified for 
	 * the field. 
	 * 
	 * @param value The value.
	 */
	public void setValue(String value);
	
	public org.psygrid.esl.model.dto.CustomValue toDTO(Map<IPersistent, org.psygrid.esl.model.dto.Persistent> dtoRefs);
	
}
