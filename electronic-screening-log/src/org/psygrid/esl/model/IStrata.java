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

import java.util.List;

/**
 * An interface to represent factors taken into account by 
 * a stratified randomisation algorithm.
 * 
 * @author Lucy Bridges
 *
 */
public interface IStrata extends IPersistent {

	/**
	 * Get the name of the stratum
	 * 
	 * @return String
	 */
	public String getName();
	
	/**
	 * Set the statum's name
	 * @param name
	 */
	public void setName(String name);
	
	/**
	 * Retrieve a list of allowed values for this stratum
	 * 
	 * @return list of values
	 */
	public List<String> getValues();
	
	/**
	 * Set the list of allowed values for this stratum
	 * 
	 * @param values
	 */
	public void setValues(List<String> values);
	
	/**
	 * Add a value to the list of allowed values for this stratum
	 * 
	 * @param value
	 */
	public void setValue(String value);
	
}
