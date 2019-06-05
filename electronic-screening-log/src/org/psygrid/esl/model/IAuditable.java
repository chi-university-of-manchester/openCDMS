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
 * Interface specifying that a Persistent object is to be Audited. 
 * 
 * Stores the log containing a history of all changes made to the Auditable object.
 *
 * @author Lucy Bridges
 */
public interface IAuditable extends IProvenanceable {

	
    /**
     * Get the history of changes made to this object
     * 
     * @return IProvenanceLog
     */
	public IProvenanceLog getLog();
	
	/**
	 * Set the log of changes made to this object
	 * 
	 * @param changes
	 */
	public void setLog(IProvenanceLog changes);
	
    public org.psygrid.esl.model.dto.Auditable toDTO(Map<IPersistent, org.psygrid.esl.model.dto.Persistent> dtoRefs);
}