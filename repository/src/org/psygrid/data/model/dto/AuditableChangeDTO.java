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
package org.psygrid.data.model.dto;

import java.util.Date;
import java.util.Map;

/**
 * 
 * Class to represent provenance metadata that is stored against Elements
 * to record how the elemtn changes through its lifetime 
 * 
 * @author pwhelan
 */
public class AuditableChangeDTO extends PersistentDTO {

	/**
	 * The id of the user who is responsible for changing the state of the 
	 * object
	 */
	private String user;
	
	/**
	 * The timestamp of when the state of an object was changed
	 */
	private Date timestamp;
	
	/**
	 * The type of action that changed the state of the Response
	 */
	private String action;
    
    /**
     * Optional comment provided by a user when a state of an object
     * is changed.
     */
    private String comment;
    
	/**
	 * Return the action triggered by this
	 * 
	 * @return action the string describing what this action does
	 */
    public String getAction() {
		return action;
	}

	/**
	 * Get the comment associated with this change
	 * 
	 * @return the comment associated with this change
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * Get the user who made this change
	 * 
	 * @return a string describing the user who made this change 
	 */
	public String getUser() {
		return user;
	}

	/**
	 * Set the user who made this change 
	 * 
	 * @param user the name of the user who made this change
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * Get the action associated with this change 
	 *  
	 * @param action the action associated with the change
	 */
	public void setAction(String action) {
		this.action = action;
	}
	
	/**
	 * Set the comment associated with this change
	 * 
	 * @param comment the comment for this change
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	/**
	 * The date on the local clock at when this change
	 * was made 
	 * 
	 * @param timestamp the time when the change was made
	 */
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * The date on the local clock at when this change 
	 * was made 
	 * 
	 * @return the time when this change was made
	 */
	public Date getTimestamp() {
		return timestamp;
	}
	
	public org.psygrid.data.model.hibernate.Persistent toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs) {
        org.psygrid.data.model.hibernate.AuditableChange hA = new org.psygrid.data.model.hibernate.AuditableChange();
        toHibernate(hA, hRefs);
        return hA;
	}

	public void toHibernate(org.psygrid.data.model.hibernate.AuditableChange hA, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs) {
		super.toHibernate(hA, hRefs);
		hA.setUser(user);
		hA.setTimestamp(timestamp);
		hA.setComment(comment);
		hA.setAction(action);
	}
	
	
}
