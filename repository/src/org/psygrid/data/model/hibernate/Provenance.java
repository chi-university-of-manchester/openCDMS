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
 * Class to represent provenance metadata that is stored against Responses
 * and their Values to record how the Value changes through the lifetime of 
 * the Response
 * 
 * @author Rob Harper
 * 
 * @hibernate.joined-subclass table="t_provenance"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class Provenance extends Persistent {

    public static final String ACTION_ADD = "Added";

    public static final String ACTION_EDIT = "Edited";

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
	 * The timestamp of when the state of an object was changed
	 * according to the PsyGrid system clock.
	 */
	private Date timestampSystem;
	
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
	 * The object after its state was changed.
	 */
	private Provenanceable theCurrentValue;
    
    private Long theCurrentValueId;
	
	/**
	 * The object before its state was changed.
	 */
	private Provenanceable thePrevValue;
    
    private Long thePrevValueId;
	
    private ChangeHistory parentChange;
    
	/**
	 * Default no-arg constructor, as required by the Hibernate framework
	 */
	public Provenance(){};
    
    /**
     * Constructor that accepts the previous and current values.
     * <p>
     * All properties of the Provenance object are set by this
     * constructor except for the user. The user is set when the
     * object is persisted to the database, which assumes that the
     * person who saves a record is responsible for all data added
     * or modified within it.
     * 
     * @param previousValue The previous value.
     * @param currentValue The current value.
     */
    public Provenance(Provenanceable previousValue, Provenanceable currentValue)
            throws ModelException{
    	TimeResult time = TimeOffset.getInstance().getTime();
        this.timestamp = time.getLocalTime();
        this.timestampSystem = time.getSystemTime();
        if ( null == previousValue && null != currentValue ){
            this.action = ACTION_ADD;
        }
        else if ( null != previousValue && null != currentValue ){
            this.action = ACTION_EDIT;
        }
        else if ( null != previousValue && null == currentValue ){
            this.action = ACTION_DELETE;
        }
        else{
            throw new ModelException("Cannot create a new provenance object with null for both current and previous values ");
        }
        this.theCurrentValue = currentValue;
        this.thePrevValue = previousValue;
    }
	
    /**
     * @hibernate.many-to-one class="org.psygrid.data.model.hibernate.Provenanceable"
     *                        column="c_current_value"
     *                        not-null="false"
     *                        cascade="none"
     *                        lazy="false"
     *                        fetch="join"
     */
	public Provenanceable getTheCurrentValue() {
        return theCurrentValue;
    }

    public void setTheCurrentValue(Provenanceable theCurrentValue) {
        this.theCurrentValue = theCurrentValue;
    }

	public Long getTheCurrentValueId() {
        return theCurrentValueId;
    }

    public void setTheCurrentValueId(Long theCurrentValueId) {
        this.theCurrentValueId = theCurrentValueId;
    }

    /**
     * Get the value of an object after its state was changed
     * 
     * @return The value.
     */
    public Provenanceable getCurrentValue() {
        if ( null != theCurrentValue ){
            theCurrentValue.lock();
        }
        return theCurrentValue;
	}
	
    /**
     * @hibernate.many-to-one class="org.psygrid.data.model.hibernate.Provenanceable"
     *                        column="c_prev_value"
     *                        not-null="false"
     *                        cascade="none"
     */
    public Provenanceable getThePrevValue() {
        return thePrevValue;
    }

    public void setThePrevValue(Provenanceable thePrevValue) {
        this.thePrevValue = thePrevValue;
    }

	public Long getThePrevValueId() {
        return thePrevValueId;
    }

    public void setThePrevValueId(Long thePrevValueId) {
        this.thePrevValueId = thePrevValueId;
    }

    /**
     * Get the value of an object before its state was changed
     * 
     * @return The value
     */
    public Provenanceable getPrevValue() {
        if ( null != thePrevValue ){
            thePrevValue.lock();
        }
        return thePrevValue;
	}
	
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
	 * Set the type of action that changed the state of the response.
	 * 
	 * @param action The type of action.
	 */
	public void setAction(String action) {
		this.action = action;
	}
	
	/**
     * Get the timestamp of when the state of the object was changed
     * according to the local clock.
     * 
     * @return The timestamp according to the local clock.
	 * @hibernate.property column="c_timestamp"
	 */
	public Date getTimestamp() {
		return timestamp;
	}
	
	/**
	 * Set the timestamp of when the state of the response was changed,
	 * according to the local clock.
	 * 
	 * @param timestamp The timestamp according to the local clock.
	 */
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
	/**
     * Get the timestamp of when the state of the object was changed
     * according to the system clock.
     * 
     * @return The timestamp according to the system clock.
	 * @hibernate.property column="c_timestamp_system"
	 */
	public Date getTimestampSystem() {
		return timestampSystem;
	}

	/**
	 * Set the timestamp of when the state of the response was changed,
	 * according to the PsyGrid system clock.
	 * 
	 * @param timestamp The timestamp according to the local clock.
	 */
	public void setTimestampSystem(Date timestampSystem) {
		this.timestampSystem = timestampSystem;
	}

	/**
     * Get the id of the user who is responsible for changing the state of the 
     * object.
     * 
     * @return The id of the user
	 * @hibernate.property column="c_user"
	 */
	public String getUser() {
		return user;
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
     * Set the optional comment provided by a user when a state of an object
     * is changed.
     * 
     * @param comment The comment.
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Get the change history item that the records the change
     * of which the Provenance item is a part of.
     * 
     * @return The parent change.
     * @hibernate.many-to-one class="org.psygrid.data.model.hibernate.ChangeHistory"
     *                        column="c_parent_change"
     *                        not-null="false"
     *                        cascade="none"
     */
    public ChangeHistory getParentChange() {
		return parentChange;
	}

	public void setParentChange(ChangeHistory parentChange) {
		this.parentChange = parentChange;
	}

	public org.psygrid.data.model.dto.ProvenanceDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        org.psygrid.data.model.dto.ProvenanceDTO dtoP = new org.psygrid.data.model.dto.ProvenanceDTO();
        toDTO(dtoP, dtoRefs, depth);
        return dtoP;
    }
    
    public void toDTO(org.psygrid.data.model.dto.ProvenanceDTO dtoP, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        super.toDTO(dtoP, dtoRefs, depth);
        dtoP.setAction(this.action);
        if ( null != this.theCurrentValue ){
            dtoP.setTheCurrentValue(this.theCurrentValue.toDTO(dtoRefs, depth));
        }
        if ( null != this.thePrevValue ){
            dtoP.setThePrevValue(this.thePrevValue.toDTO(dtoRefs, depth));
        }
        dtoP.setTimestamp(this.timestamp);
        dtoP.setTimestampSystem(this.timestampSystem);
        dtoP.setUser(this.user);
        dtoP.setComment(this.comment);
        if ( null != this.parentChange ){
            dtoP.setParentChange(this.parentChange.toDTO(dtoRefs, depth));
        }
    }
    
    /**
     * Unlock the prevValue and currentValue provenanceables.
     * <p>
     * Only need to do this if there is a possibility of them
     * being edited again after being accessed by (for example)
     * a provenance browser.
     */
    public void unlock(){
        if ( null != thePrevValue ){
            thePrevValue.unlock();
        }
        if ( null != theCurrentValue ){
        	theCurrentValue.unlock();
        }
    }
}
