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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class to represent an instance of a statused element.
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_statused_instances"
 * @hibernate.joined-subclass-key column="c_id"
 */
public abstract class StatusedInstance extends ElementInstance {

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd-MMM-yyyy");
	
	private static Log sLog = LogFactory.getLog(StatusedInstance.class);
	
    /**
     * The current status of the element instance.
     */
    protected Status status;
    
    protected Long statusId;
    
    /**
     * The date the instance was first created
     * @deprecated
     */
    protected Date created;
    
    /**
     * The ID of the user who first created the instance.
     * @deprecated
     */
    protected String createdBy;
    
    /**
     * The date the instance was last edited.
     */
    protected Date edited;
    
    /**
     * The ID of the user who last edited the instance.
     * @deprecated
     */
    protected String editedBy;
    
    /**
     * The change history of the instance.
     */
    protected List<ChangeHistory> history = new ArrayList<ChangeHistory>();
    
    public StatusedInstance(){
        this.created = new Date();
    }
    
    /**
     * Get the current status of the instance.
     * 
     * @return The status of the instance.
     * @hibernate.many-to-one class="org.psygrid.data.model.hibernate.Status"
     *                        column="c_status_id"
     *                        not-null="false"
     *                        cascade="none"
     */    
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = (Status)status;
    }

    public Long getStatusId() {
        return statusId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    public void changeStatus(Status status, boolean ignorePermittedTransitions) throws ModelException {
        if ( null == status ){
            throw new ModelException("Cannot change status to null");
        }
        
        //check that the status has actually changed
        if ( null == this.status || !status.equals(this.status)){
        	if ( !ignorePermittedTransitions ){
	            //check that it is permitted to change the status to the
	            //new value, given the allowed status transitions from the
	            //current status
	            if ( null != this.status && !this.status.getStatusTransitions().contains(status) ){
	                throw new ModelException("The new status is not a permitted status transition for the current status. Current status = "+this.status.getShortName()+"; new status = "+status.getShortName());
	            }
        	}
            //create new provenance object
            Status newStatus = (Status)status;
            Provenance prov = new Provenance(this.status, newStatus);
            this.status = (Status)status;
            this.provItems.add(prov);
        }
    }
    
    public void changeStatus(Status status) throws ModelException {
    	changeStatus(status, false);
    }

    /**
     * Get the change history of the instance.
     * 
     * @return The change history.
     * 
     * @hibernate.list cascade="all"
     * @hibernate.one-to-many class="org.psygrid.data.model.hibernate.ChangeHistory"
     * @hibernate.key column="c_instance_id"
     *                not-null="true"
     * @hibernate.list-index column="c_index"

     */
    public List<ChangeHistory> getHistory() {
		return history;
	}

    /**
     * Set the change history of the instance.
     * 
     * @param history The change history.
     */
	public void setHistory(List<ChangeHistory> history) {
		this.history = history;
	}

	/**
	 * Get the number of items in the change history.
	 * 
	 * @return Integer
	 */
	public int getHistoryCount(){
		return history.size();
	}
	
	/**
	 * Get the item in the change history at the specified index.
	 * 
	 * @param index The index.
	 * @return The item in the change history at the given index
	 * @throws ModelException if no change history item exists at the
	 * given index.
	 */
	public ChangeHistory getHistory(int index) throws ModelException {
		try{
			return history.get(index);
		}
		catch(IndexOutOfBoundsException ex){
			throw new ModelException(ex);
		}
	}
	
	/**
	 * Add an item to the change history, specifying the user. The
	 * action will be determined automatically.
	 * <p>
	 * Note that the user specified here is only temporary, and will be
	 * replaced when committed to the repository with the DN of the user
	 * doing the commit.
	 * 
	 * @param user The user.
	 * @return The new change history item.
	 */
	public ChangeHistory addToHistory(String user){
		String action = ChangeHistory.EDITED;
		if ( history.isEmpty() ){
			action = ChangeHistory.CREATED;
		}
		ChangeHistory ch = new ChangeHistory(user, action);
		history.add(ch);
		return ch;
	}
	
	/**
	 * Add an item to the change history, specifying the user and
	 * the action.
	 * <p>
	 * Note that the user specified here is only temporary, and will be
	 * replaced when committed to the repository with the DN of the user
	 * doing the commit.
	 * 
	 * @param user The user.
	 * @param action The action.
	 * @return The new change history item.
	 */
	public ChangeHistory addToHistory(String user, String action){
		ChangeHistory ch = new ChangeHistory(user, action);
		history.add(ch);
		return ch;
	}
	
	/**
	 * Get the most recent item in the change history. If no items
	 * exist in the change history, return <code>null</code>.
	 * 
	 * @return The latest change history item, or <code>null</code>.
	 */
	public ChangeHistory getLatestHistory() {
		if ( history.isEmpty() ){
			return null;
		}
		return history.get(history.size()-1);
	}

	/**
	 * Get the most recent item in the change history formatted
	 * into a user-friendly string. If no items
	 * exist in the change history, return <code>null</code>.
	 * 
	 * @return String representing the latest change history item,
	 * or <code>null</code>.
	 */
	public String getLatestHistoryFormatted() {
		if ( history.isEmpty() ){
			return null;
		}
		ChangeHistory latest = history.get(history.size()-1);
		String user = null;
		if ( latest.getUser().startsWith("CN") || latest.getUser().startsWith("cn")){
			user = latest.getUser().substring(latest.getUser().indexOf("=")+1, latest.getUser().indexOf(","));
		}
		else{
			user = latest.getUser();
		}
		return user + " at " + dateFormat.format(latest.getWhen()) + "";
	}

	/**
     * Get the date the instance was first created.
     * 
     * @return The date the instance was first created.
     * @deprecated
     * @hibernate.property column="c_created"
     */
    public Date getCreated() {
		return created;
	}

    /**
     * Set the date the instance was first created.
     * 
     * @param created The date the instance was first created.
     * @deprecated
     */
	public void setCreated(Date created) {
		this.created = created;
	}

	/**
	 * Get the ID of the user who first created the instance.
	 * 
	 * @return The ID of the user who first created the instance.
	 * @deprecated
     * @hibernate.property column="c_created_by"
	 */
	public String getCreatedBy() {
		return createdBy;
	}

	/**
	 * Set the ID of the user who first created the instance.
	 * 
	 * @param createdBy The ID of the user who first created the instance.
	 * @deprecated
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * Get the date the instance was last edited.
	 * 
	 * @return The date the instance was last edited.
	 * @hibernate.property column="c_edited"
	 */
	public Date getEdited() {
		return edited;
	}

	/**
	 * Set the date the instance was last edited.
	 * 
	 * @param edited The date the instance was last edited.
	 */
	public void setEdited(Date edited) {
		this.edited = edited;
	}

	/**
	 * Get the ID of the user who last edited the instance.
	 * 
	 * @return The ID of the user who last edited the instance.
	 * @deprecated
	 * @hibernate.property column="c_edited_by"
	 */
	public String getEditedBy() {
		return editedBy;
	}

	/**
	 * Set the ID of the user who last edited the instance.
	 * 
	 * @param editedBy The ID of the user who last edited the instance.
	 * @deprecated
	 */
	public void setEditedBy(String editedBy) {
		this.editedBy = editedBy;
	}

	public void attach(StatusedElement element){

        Long sId = null;
        if ( null != this.statusId ){
            sId = this.statusId;
        }
        else if ( null != this.status ){
            //preserve backwards compatability with records detached
            //prior to the introduction of StatusedInstance.statusId
            sId = this.status.getId();
        }
        if ( null != sId){
            boolean attached = false;
            for (Status s:element.getStatuses()){
                if ( s.getId().equals(sId) ){
                    this.status = s;
                    this.statusId = null;
                    attached = true;
                    break;
                }
            }
            if ( !attached ){
                throw new ModelException("Failed to attach status of instance id="+this.getId()+" - no status exists with id="+sId);
            }
        }

        for ( Provenance p: this.provItems ){
            //When the record was detached the properties theCurrentValueId and
            //thePrevValueId were only set when theCurrentValue/thePrevValue were
            //Status objects. So if theCurrentValueId and/or thePrevValueId are
            //not null we try to attach them to Status objects
            
            Long currStatusId = null;
            if ( null != p.getTheCurrentValueId() ){
                currStatusId = p.getTheCurrentValueId();
            }
            else if ( null != p.getTheCurrentValue() && p.getTheCurrentValue() instanceof Status ){
                //preserve backwards compatability with records detached
                //prior to the introduction of Provenance.theCurrentValueId
                currStatusId = p.getTheCurrentValue().getId();
            }
                
            if ( null != currStatusId ){
                boolean attached = false;
                for (Status s:element.getStatuses()){
                    if ( s.getId().equals(currStatusId) ){
                        p.setTheCurrentValue(s);
                        p.setTheCurrentValueId(null);
                        attached = true;
                        break;
                    }
                }
                if ( !attached ){
                    throw new ModelException("Failed to attach status in current value of provenance id="+p.getId()+" - no status exists with id="+currStatusId);
                }
            }

            Long prevStatusId = null;
            if ( null != p.getThePrevValueId() ){
                prevStatusId = p.getThePrevValueId();
            }
            else if ( null != p.getThePrevValue() && p.getThePrevValue() instanceof Status ){
                //preserve backwards compatability with records detached
                //prior to the introduction of Provenance.theCurrentValueId
                prevStatusId = p.getThePrevValue().getId();
            }
                
            if ( null != prevStatusId ){
                boolean attached = false;
                for (Status s:element.getStatuses()){
                    if ( s.getId().equals(prevStatusId) ){
                        p.setThePrevValue(s);
                        p.setThePrevValueId(null);
                        attached = true;
                        break;
                    }
                }
                if ( !attached ){
                    throw new ModelException("Failed to attach status in prev value of provenance id="+p.getId()+" - no status exists with id="+prevStatusId);
                }
            }
        }
        
    }
    
    public void detach(){
        
        if ( null != this.status ){
            this.statusId = this.status.getId();
            this.status = null;
        }
        
        for ( Provenance p: this.provItems ){
            if ( p.getTheCurrentValue() instanceof Status ){
                p.setTheCurrentValueId(p.getTheCurrentValue().getId());
                p.setTheCurrentValue(null);
            }
            if ( p.getThePrevValue() instanceof Status ){
                p.setThePrevValueId(p.getThePrevValue().getId());
                p.setThePrevValue(null);
            }
        }
        
    }
    
    public abstract org.psygrid.data.model.dto.StatusedInstanceDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth);
    
    public void toDTO(org.psygrid.data.model.dto.StatusedInstanceDTO dtoSI, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        super.toDTO(dtoSI, dtoRefs, depth);

        if ( null != this.status ){
            dtoSI.setStatusId(this.status.getId());
        }
        else {
        	dtoSI.setStatusId(this.statusId);
        }
        
        dtoSI.setEdited(this.edited);
        
        
        org.psygrid.data.model.dto.ChangeHistoryDTO[] dtoCH = new org.psygrid.data.model.dto.ChangeHistoryDTO[this.history.size()];
        for (int i=0; i<this.history.size(); i++){
            ChangeHistory ch = history.get(i);
            dtoCH[i] = ch.toDTO(dtoRefs, depth);
        }        
        dtoSI.setHistory(dtoCH);
    }
    
    protected abstract void addChildTasks(Record r);
    
    protected abstract StatusedElement findElement();
    
    private Object readResolve(){
    	if ( null == history ){
    		history = new ArrayList<ChangeHistory>();
    	}
    	return this;
    }

}
