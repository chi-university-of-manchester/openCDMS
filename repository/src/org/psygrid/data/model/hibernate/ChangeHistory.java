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

import org.psygrid.data.utils.time.TimeOffset;
import org.psygrid.data.utils.time.TimeResult;

/**
 * Class to store the edit history of statused instances
 * (i.e. records and document instances).
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_change_history"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class ChangeHistory extends Persistent {

    public static final String SAVED = "Saved";
    public static final String CREATED = "Created";
    public static final String EDITED = "Edited";
    public static final String DATA_REP = "Data replication";

	/**
	 * The DN of the user making the change.
	 */
	protected String user;
	
	/**
	 * The date when the change was made, according to the users
	 * local clock.
	 */
	protected Date when;
	
	/**
	 * The date when the change was made, with the local time
	 * adjusted to represent the PsyGrid system time.
	 */
	protected Date whenSystem;
	
	/**
	 * The action taken (e.d. add, edit)
	 */
	protected String action;
	
	/**
	 * The database id of a "parent" for this ChangeHistory item.
	 * <p>
	 * Used to relate (for example) changes to document instances
	 * to the change to the record as they were committed.
	 * <p>
	 * Will be set by an interceptor when the record is saved, not 
	 * intended to be set manually.
	 */
	protected Long parentId;
	
	/**
	 * Default no-arg constructor, as required by the Hibernate framework
	 */
	public ChangeHistory(){};
    
	public ChangeHistory(String user, String action){
		this.user = user;
		TimeResult time = TimeOffset.getInstance().getTime();
		this.when = time.getLocalTime();
		this.whenSystem = time.getSystemTime();
		this.action = action;
	}
	
	public ChangeHistory(String user, String action, Date when){
		this.user = user;
		this.when = when;
		this.action = action;
	}
	
	/**
	 * Get the DN of the user who made the change.
	 * 
	 * @return The DN of the user.
	 * @hibernate.property column="c_user"
	 */
	public String getUser() {
		return user;
	}

	/**
	 * Set the DN of the user who made the change.
	 * 
	 * @param user The DN of the user
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * Get the date when the change was made according to the
	 * local clock.
	 * 
	 * @return The date of the local clock.
	 * @hibernate.property column="c_when"
	 */
	public Date getWhen() {
		return when;
	}

	/**
	 * Set the date when the change was made according to the
	 * local clock.
	 * 
	 * @param when The date of the local clock.
	 */
	public void setWhen(Date when) {
		this.when = when;
	}

	/**
	 * Get the date when the change was made according to the
	 * PsyGrid system clock.
	 * 
	 * @return The date of the PsyGrid system clock.
	 * @hibernate.property column="c_when_system"
	 */
	public Date getWhenSystem() {
		return whenSystem;
	}

	/**
	 * Set the date when the change was made according to the
	 * PsyGrid system clock.
	 * 
	 * @param when The date of the PsyGrid system clock.
	 */
	public void setWhenSystem(Date whenSystem) {
		this.whenSystem = whenSystem;
	}

	/**
	 * Get the action taken (e.d. add, edit).
	 * 
	 * @return The action taken.
	 * @hibernate.property column="c_action"
	 */
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	/**
	 * Get the database id of a "parent" for this ChangeHistory item.
	 * <p>
	 * Used to relate (for example) changes to document instances
	 * to the change to the record as they were committed.
	 * 
	 * @return The id of the parent.
	 * @hibernate.property column="c_parentid"
	 */
	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	@Override
	public org.psygrid.data.model.dto.ChangeHistoryDTO toDTO(
			Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs,
			RetrieveDepth depth) {
        //check for an already existing instance of a dto object for this 
        //change history in the map of references
        org.psygrid.data.model.dto.ChangeHistoryDTO dtoCH = null;
        if ( dtoRefs.containsKey(this)){
        	dtoCH = (org.psygrid.data.model.dto.ChangeHistoryDTO)dtoRefs.get(this);
        }
        if ( null == dtoCH ){
            //an instance of the change history has not already
            //been created, so create it, and add it to the 
            //map of references
        	dtoCH = new org.psygrid.data.model.dto.ChangeHistoryDTO();
            dtoRefs.put(this, dtoCH);
            toDTO(dtoCH, dtoRefs, depth);
        }

        return dtoCH;
	}

	public void toDTO(org.psygrid.data.model.dto.ChangeHistoryDTO dtoCH,
			Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs,
			RetrieveDepth depth) {
		super.toDTO(dtoCH, dtoRefs, depth);
		dtoCH.setUser(this.user);
		dtoCH.setWhen(this.when);
		dtoCH.setWhenSystem(this.whenSystem);
		dtoCH.setAction(this.action);
		dtoCH.setParentId(this.parentId);
	}

}
