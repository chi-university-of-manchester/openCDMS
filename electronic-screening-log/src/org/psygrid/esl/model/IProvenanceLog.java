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
import java.util.Date;

/**
 * Records all changes made to an object over its lifetime.
 * 
 * @author Lucy Bridges
 *
 */
public interface IProvenanceLog extends IPersistent {

	/**
	 * Retrieve the list of changes made to an Auditable object
	 * over its lifetime
	 *  
	 * @return list of IProvenanceChange
	 */
	public List<IProvenanceChange> getProvenanceChange();
	
	/**
	 * Add a new change to list of changes made to an Auditable 
	 * object.
	 *  
	 * @param change
	 */
	public void addProvenanceChange(IProvenanceChange change);
	
	/**
	 * Retrieve the username of the user who created the Auditable
	 * object as defined by the policy authority
	 * 
	 * @return username
	 */
	public String getCreatedBy();
	
	/**
	 * Set the creator of the Auditable object
	 * 
	 * @param createdBy
	 */
	public void setCreatedBy(String createdBy);
	
	/**
	 * Retrieve the date of when the Auditable object was created
	 * 
	 * @return Date
	 */
	public Date getCreated();
	
	/**
	 * Set the creation date for the Auditable object
	 * 
	 * @param created
	 */
	public void setCreated(Date created);
}
