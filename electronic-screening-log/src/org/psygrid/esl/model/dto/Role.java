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

package org.psygrid.esl.model.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * People to be notified of the randomisation decision or invocation.
 * 
 * @author Lucy Bridges
 *
 */
public class Role extends Persistent {

	private String name;
	private boolean notifyOfRSDecision   = false;
	private boolean notifyOfRSInvocation = false;
	private boolean notifyOfRSTreatment  = false;
	
	/**
	 * Get the name of a Role in this project.
	 * 
	 * @return String
	 */
	public String getName() { 
		return name;
	}
	
	/**
	 * Set a name for this Role 
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Get whether this Role is to be notified of the results of randomisation 
	 * 
	 * @return boolean
	 * 
	 */
	public boolean isNotifyOfRSDecision() {
		return notifyOfRSDecision;
	}

	/**
	 * Set whether this role is to be notified of the results of each randomisation.
	 * 
	 * @param notifyOfRSDecision
	 */
	public void setNotifyOfRSDecision(boolean notifyOfRSDecision) {
		this.notifyOfRSDecision = notifyOfRSDecision;
	}

	/**
	 * Get whether this Role is to be notified when randomisation is invoked
	 * 
	 * @return boolean
	 */
	public boolean isNotifyOfRSInvocation() {
		return notifyOfRSInvocation;
	}
	
	/** 
	 * Set whether this role is to be notified when randomisation is invoked.
	 * 
	 * @param notifyOfRSInvocation
	 */
	public void setNotifyOfRSInvocation(boolean notifyOfRSInvocation) {
		this.notifyOfRSInvocation = notifyOfRSInvocation;
	}
	
	/**
	 * Get whether this Role is to be notified of the treatment allocated when
	 * a Subject is randomised
	 * 
	 * @return boolean
	 */
	public boolean isNotifyOfRSTreatment() {
		return notifyOfRSTreatment;
	}

	/**
	 * Set whether this Role is to be notified of the treatment allocated
	 * when a Subject is randomised
	 * 
	 * @param notifyOfRSTreatment the notifyOfRSTreatment to set
	 */
	public void setNotifyOfRSTreatment(boolean notifyOfRSTreatment) {
		this.notifyOfRSTreatment = notifyOfRSTreatment;
	}
	

	public org.psygrid.esl.model.hibernate.Role toHibernate(){
		//create list to hold references to objects in the project's
		//object graph which have multiple references to them within
		//the object graph. This is used so that each object instance
		//is copied to its hibernate equivalent once and once only
		Map<Persistent, org.psygrid.esl.model.hibernate.Persistent> dtoRefs = new HashMap<Persistent, org.psygrid.esl.model.hibernate.Persistent>();
		org.psygrid.esl.model.hibernate.Role hRole = toHibernate(dtoRefs);
		dtoRefs = null;
		return hRole;
	}

	public org.psygrid.esl.model.hibernate.Role toHibernate(Map<Persistent, org.psygrid.esl.model.hibernate.Persistent> hRefs){
		//check for an already existing instance of a hibernate object for this 
		//object in the set of references
		org.psygrid.esl.model.hibernate.Role hRole = null;
		if ( hRefs.containsKey(this)){
			hRole = (org.psygrid.esl.model.hibernate.Role)hRefs.get(this);
		}
		if ( null == hRole ){
			//an instance of this Role has not already
			//been created, so create it and add it to the map of references
			hRole = new org.psygrid.esl.model.hibernate.Role();
			hRefs.put(this, hRole);
			toHibernate(hRole, hRefs);
		}

		return hRole;
	}
	
	public void toHibernate(org.psygrid.esl.model.hibernate.Role hRole, Map<Persistent, org.psygrid.esl.model.hibernate.Persistent> hRefs){
		super.toHibernate(hRole, hRefs);
		hRole.setName(this.name);
		hRole.setNotifyOfRSDecision(notifyOfRSDecision);
		hRole.setNotifyOfRSInvocation(notifyOfRSInvocation);
		hRole.setNotifyOfRSTreatment(notifyOfRSTreatment);
		
	}
}
