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

package org.psygrid.esl.model.hibernate;

import java.util.HashMap;
import java.util.Map;

import org.psygrid.esl.model.IPersistent;
import org.psygrid.esl.model.IRole;

/**
 * 
 * @author Lucy Bridges
 * 
 * @hibernate.joined-subclass table="t_roles"
 * 								proxy="org.psygrid.esl.model.hibernate.Role"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class Role extends Persistent implements IRole {

	private String name;
	private boolean notifyOfRSDecision   = false;
	private boolean notifyOfRSInvocation = false;
	private boolean notifyOfRSTreatment  = false;

	
	public Role() {
	}
	
	public Role(String name) {
		this.name = name;
	}
	
	/**
	 * @hibernate.property column="c_name"
	 */
	public String getName() {
		return name;
	}

	/**
	 * @see org.psygrid.esl.model.IRole#setRole(boolean)
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get whether this Role is to be notified of the results of randomisation 
	 * 
	 * @return boolean
	 * 
	 * @hibernate.property column="c_notify_of_decision"
	 */
	public boolean isNotifyOfRSDecision() {
		return notifyOfRSDecision;
	}

	/**
	 * @see org.psygrid.esl.model.IRole#setNotifyOfRSDecision(boolean)
	 */
	public void setNotifyOfRSDecision(boolean notifyOfRSDecision) {
		this.notifyOfRSDecision = notifyOfRSDecision;
	}

	/**
	 * Get whether this Role is to be notified when randomisation is invoked
	 * 
	 * @return boolean
	 * 
	 * @hibernate.property column="c_notify_of_invocation"
	 */
	public boolean isNotifyOfRSInvocation() {
		return notifyOfRSInvocation;
	}

	/**
	 * @see org.psygrid.esl.model.IProject#setNotifyOfRSInvocation(java.util.List)
	 */
	public void setNotifyOfRSInvocation(boolean notifyOfRSInvocation) {
		this.notifyOfRSInvocation = notifyOfRSInvocation;
	}


	/**
	 * @return the notifyOfRSTreatment
	 * 
	 * @hibernate.property column="c_notify_of_treatment"
	 */
	public boolean isNotifyOfRSTreatment() {
		return notifyOfRSTreatment;
	}

	/**
	 * @param notifyOfRSTreatment the notifyOfRSTreatment to set
	 */
	public void setNotifyOfRSTreatment(boolean notifyOfRSTreatment) {
		this.notifyOfRSTreatment = notifyOfRSTreatment;
	}

	/**
	 * Store object reference to maintain persistence
	 * 
	 * @return dto.Role
	 */
	public org.psygrid.esl.model.dto.Role toDTO() {
		Map<IPersistent, org.psygrid.esl.model.dto.Persistent> dtoRefs = new HashMap<IPersistent, org.psygrid.esl.model.dto.Persistent>();
		org.psygrid.esl.model.dto.Role dtoRole = toDTO(dtoRefs);
		dtoRefs = null;
		return dtoRole;
	}

	public org.psygrid.esl.model.dto.Role toDTO(Map<IPersistent, org.psygrid.esl.model.dto.Persistent> dtoRefs) {
		//check for an already existing instance of a dto object for this 
		//class in the set of references
		org.psygrid.esl.model.dto.Role dtoRole = null;
		if ( dtoRefs.containsKey(this)){
			dtoRole = (org.psygrid.esl.model.dto.Role)dtoRefs.get(this);
		}
		if ( null == dtoRole ){
			dtoRole = new org.psygrid.esl.model.dto.Role();
			dtoRefs.put(this, dtoRole);
			toDTO(dtoRole, dtoRefs);
		}

		return dtoRole;
	}

	public void toDTO(org.psygrid.esl.model.dto.Role dtoRole, Map<IPersistent, org.psygrid.esl.model.dto.Persistent> dtoRefs) {
		super.toDTO(dtoRole, dtoRefs);
		dtoRole.setName(this.name);
		dtoRole.setVersion(this.version);  
		dtoRole.setNotifyOfRSDecision(notifyOfRSDecision);
		dtoRole.setNotifyOfRSInvocation(notifyOfRSInvocation);
		dtoRole.setNotifyOfRSTreatment(notifyOfRSTreatment);
		
	}
	
}
