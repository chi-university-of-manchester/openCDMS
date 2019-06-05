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

/**
 * Records a change made to a particular field in a instance of updating an object
 * 
 * @author Lucy Bridges
 *
 */
public interface IChange extends IPersistent {

	/**
	 * Retrieve the name of the field that was updated
	 * 
	 * @return the field
	 */
	public String getField();


	/**
	 * Set the name of the field that has been updated
	 * 
	 * @param field the field to set
	 */
	public void setField(String field);

	/**
	 * Get the value the field was updated to
	 * 
	 * @return the newValue
	 */
	public String getNewValue();

	/**
	 * Set the new value of the field
	 * 
	 * @param newValue the newValue to set
	 */
	public void setNewValue(String newValue);

	/**
	 * Get the previous value that was assigned to the field 
	 * 
	 * @return the prevValue
	 */
	public String getPrevValue();

	/**
	 * Set the previous value of the field
	 * 
	 * @param prevValue the prevValue to set
	 */
	public void setPrevValue(String prevValue);
	
	/**
	 * Retrieve the IProvenaceChange object this object belongs to 
	 * i.e. the particular update that this change is part of
	 * 
	 * @return IProvenanceChange
	 */
	public IProvenanceChange getProvenance();
	
	/**
	 * Set the IProvenanceChange that this object belongs to
	 * 
	 * @param provenance
	 */
	public void setProvenance(IProvenanceChange provenance);
}