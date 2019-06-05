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

import java.util.Date;
import java.util.Map;

/**
 * Records the changes made during one update instance of an Auditable object
 * 
 * @author Lucy Bridges
 *
 */
public interface IProvenanceChange extends IPersistent {

	/**
	 * Get the time that a particular update was made
	 * 
	 * @return Date
	 */
	public Date getTimestamp();

	/**
	 * Set the time an update was made
	 * 
	 * @param timestamp
	 */
	public void setTimestamp(Date timestamp);

	/**
	 * Retrieve the name of the user who made the update
	 * 
	 * @return String
	 */
	public String getUser();

	/**
	 * Set the username of the user who made the update
	 * 
	 * @param user
	 */
	public void setUser(String user);

	/**
	 * Get the ProvenanceLog that this update belongs to
	 * 
	 * @return IProvenanceLog
	 */
	public IProvenanceLog getProvenanceLog();

	/**
	 * Set the ProvenanceLog taht thsi update belongs to
	 * 
	 * @param provenanceLog
	 */
	public void setProvenanceLog(IProvenanceLog provenanceLog);

	public org.psygrid.esl.model.dto.ProvenanceChange toDTO(Map<IPersistent, org.psygrid.esl.model.dto.Persistent> dtoRefs);
}