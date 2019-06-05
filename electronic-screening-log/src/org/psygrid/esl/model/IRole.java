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
 * Interface to represent the various Roles held by those involved in the trial
 * 
 * @author Lucy Bridges
 *
 */
public interface IRole extends IPersistent {

	/**
	 * Get the name of a Role in this project.
	 * 
	 * @return String
	 */
	public String getName();
	
	/**
	 * Provide a name for this Role
	 * 
	 * @param name
	 */
	public void setName(String name);
	
	/**
	 * Get whether this Role is to be notified of the results of randomisation 
	 * 
	 * @return boolean
	 */
	public boolean isNotifyOfRSDecision();

	/**
	 * Set whether this role is to be notified of the results of each randomisation.
	 * 
	 * @param notifyOfRSDecision
	 */
	public void setNotifyOfRSDecision(boolean notifyOfRSDecision);

	/**
	 * Get whether this Role is to be notified when randomisation is invoked
	 * 
	 * @return boolean
	 */
	public boolean isNotifyOfRSInvocation();

	/** 
	 * Set whether this role is to be notified when randomisation is invoked.
	 * 
	 * @param notifyOfRSInvocation
	 */
	public void setNotifyOfRSInvocation(boolean notifyOfRSInvocation);
	
	/**
	 * Get whether this role is to be informed of the treatment type allocated
	 * to a subject during randomisation. Specifically, used to specify that 
	 * the therapist and CPM should be notified of the treatment.
	 * 
	 * @return boolean 
	 */
	public boolean isNotifyOfRSTreatment();
	
	/**
	 * Set whether this role is to be notified of treatment allocation
	 * 
	 * @param notifyOfRSTreatment 
	 */
	public void setNotifyOfRSTreatment(boolean notifyOfRSTreatment);
	
	public org.psygrid.esl.model.dto.Role toDTO();
	public org.psygrid.esl.model.dto.Role toDTO(Map<IPersistent, org.psygrid.esl.model.dto.Persistent> dtoRefs);
}
