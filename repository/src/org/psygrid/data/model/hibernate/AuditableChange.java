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
package org.psygrid.data.model.hibernate;

import java.util.Date;
import java.util.Map;

//import org.psygrid.data.model.IAuditLog;


/**
 * Class to represent provenance metadata that is stored against Responses
 * and their Values to record how the Value changes through the lifetime of 
 * the Response
 * 
 * @author pwhelan
 * 
 * @hibernate.joined-subclass table="t_auditablechange"
 * @hibernate.joined-subclass-key column="c_id"
 */

public class AuditableChange extends Persistent {

    /**
     * Item added
     */
    public static final String ACTION_ADD = "Added";

    /**
     * Item edited
     */
    public static final String ACTION_EDIT = "Edited";

    /**
     * Item deleted
     */
    public static final String ACTION_DELETE = "Deleted";

	
	/**
	 * The id of the user who is responsible for changing the state of an 
	 * object.
	 */
	private String user;
	
	/**
	 * The timestamp of when the state of an object was changed
	 * according to the local clock.
	 */
	private Date timestamp;
	
	/**
	 * The type of action that changed the state of an object.
	 */
	private String action;
    
    /**
     * Optional comment provided by a user when a state of an object
     * is changed.
     */
    private String comment;

	 /**
     * Default no-arg constructor as required by Hibernate.
     */
    public AuditableChange(){};
    
    
    /**
	  * Get the type of action that changed the state of the object.
	  * 
	  * @return The type of action
	 * @hibernate.property column="c_action"
	 */
	public String getAction() {
		return action;
	}

    /**
     * Set the optional comment provided by a user when a state of an object
     * is changed.
     * 
     * @param comment The comment.
     */
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	/**
     * Get the comment provided by a user when a state of an object
     * is changed.
     * 
     * @return The comment.
     * @hibernate.property column="c_comment" type="text"
     */
	public String getComment() {
		return comment;
	}

	/**
	 * Get the time that a particular update was made
	 * 
	 * @return Date
	 * @hibernate.property column="c_timestamp"
	 */
	public Date getTimestamp() {
		return timestamp;
	}

	/**
	 * Retrieve the name of the user who made the update
	 * 
	 * @return String
	 * @hibernate.property column="c_user"
	 */
	public String getUser() {
		return user;
	}

	/**
	 * Set the time and date of this change
	 * 
	 * @param timestamp the date and time of this change
	 */
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * Set the id of the user who was responsible for changing the 
	 * state of the response.
	 * 
	 * @param user The id of the user.
	 */
	public void setUser(String user) {
		this.user = user;
	}
	
	/**
	 * Set the action for the object
	 * 
	 * @param action the action of the object
	 */
	public void setAction(String action) {
		this.action = action;
	}

	public org.psygrid.data.model.dto.AuditableChangeDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        // check for an already existing instance of a dto object for this 
		//group in the set of references
		org.psygrid.data.model.dto.AuditableChangeDTO dtoP = null;
		if ( dtoRefs.containsKey(this)){
			dtoP = (org.psygrid.data.model.dto.AuditableChangeDTO)dtoRefs.get(this);
		}
		if ( null == dtoP ){
			dtoP = new org.psygrid.data.model.dto.AuditableChangeDTO();
			dtoRefs.put(this, dtoP);
			toDTO(dtoP, dtoRefs, depth);
		}
        return dtoP;
    }

	
	public void toDTO(org.psygrid.data.model.dto.AuditableChangeDTO dtoP, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth) {
		super.toDTO(dtoP, dtoRefs, depth);
		dtoP.setTimestamp(timestamp);
		dtoP.setUser(user);
		dtoP.setAction(action);
		dtoP.setComment(comment);
		
	}	
	
	
}
