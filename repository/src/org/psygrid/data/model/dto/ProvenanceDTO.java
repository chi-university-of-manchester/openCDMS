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
 * Class to represent provenance metadata that is stored against Responses
 * and their Values to record how the Value changes through the lifetime of 
 * the Response
 * 
 * @author Rob Harper
 * 
 */
public class ProvenanceDTO extends PersistentDTO {

	/**
	 * The id of the user who is responsible for changing the state of the 
	 * Response
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
	 * The type of action that changed the state of the Response
	 */
	private String action;
    
    /**
     * Optional comment provided by a user when a state of an object
     * is changed.
     */
    private String comment;
	
	/**
	 * The Value of the Response after its state was changed
	 */
	private ProvenanceableDTO theCurrentValue;
	
	/**
	 * The Value of the Response before its state was changed
	 */
	private ProvenanceableDTO thePrevValue;
	
	private ChangeHistoryDTO parentChange;
	
	/**
	 * Default no-arg constructor, as required by the Hibernate framework
	 */
	public ProvenanceDTO(){}

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public ProvenanceableDTO getTheCurrentValue() {
        return theCurrentValue;
    }

    public void setTheCurrentValue(ProvenanceableDTO currentValue) {
        this.theCurrentValue = currentValue;
    }

    public ProvenanceableDTO getThePrevValue() {
        return thePrevValue;
    }

    public void setThePrevValue(ProvenanceableDTO prevValue) {
        this.thePrevValue = prevValue;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Date getTimestampSystem() {
		return timestampSystem;
	}

	public void setTimestampSystem(Date timestampSystem) {
		this.timestampSystem = timestampSystem;
	}

	public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
    
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public ChangeHistoryDTO getParentChange() {
		return parentChange;
	}

	public void setParentChange(ChangeHistoryDTO parentChange) {
		this.parentChange = parentChange;
	}

	public org.psygrid.data.model.hibernate.Provenance toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        org.psygrid.data.model.hibernate.Provenance hP = new org.psygrid.data.model.hibernate.Provenance();
        toHibernate(hP, hRefs);
        return hP;
    }
    
    public void toHibernate(org.psygrid.data.model.hibernate.Provenance hP, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        super.toHibernate(hP, hRefs);
        hP.setAction(this.action);
        if ( null != this.theCurrentValue ){
            hP.setTheCurrentValue(this.theCurrentValue.toHibernate(hRefs));
        }
        if ( null != this.thePrevValue ){
            hP.setThePrevValue(this.thePrevValue.toHibernate(hRefs));
        }
        hP.setTimestamp(this.timestamp);
        hP.setTimestampSystem(this.timestampSystem);
        hP.setUser(this.user);
        hP.setComment(this.comment);
        if ( null != this.parentChange ){
            hP.setParentChange(this.parentChange.toHibernate(hRefs));
        }

    }
    
}
