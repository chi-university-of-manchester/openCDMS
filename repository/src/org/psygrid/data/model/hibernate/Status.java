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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Class to represent the status of an element instance.
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_statuses"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class Status extends Provenanceable {

    public static final String DOC_STATUS_INCOMPLETE = "Incomplete";
    public static final String DOC_STATUS_PENDING = "Pending";
    public static final String DOC_STATUS_REJECTED = "Rejected";
    public static final String DOC_STATUS_APPROVED = "Approved";
    public static final String DOC_STATUS_DATASET_DESIGNER = "Dataset Designer";
    public static final String DOC_STATUS_COMPLETE = "Complete";
    public static final String DOC_STATUS_CONTROLLED = "Controlled";
    public static final String DOC_STATUS_COMMIT_FAILED = "Commit Failed";

    private boolean readOnly;
    
    /**
     * The numeric code of the status.
     */
    private int code;
    
    /**
     * The short name of the status.
     * <p>
     * This is the name used internally when communicating with
     * other components i.e. the security system.
     */
    private String shortName;
    
    /**
     * The long name of the status.
     * <p>
     * This is the name that should be displayed to the user.
     */
    private String longName;
    
    /**
     * The collection of allowed status transitions.
     */
    private List<Status> statusTransitions = new ArrayList<Status>();
    
    /**
     * Boolean flag to indicate whether the status implies that the object
     * that it is applied to is inactive (True) or not.
     * <p>
     * Originally added to cater for Record statuses such as Withdrawn and
     * Deceased, which both imply that the Record is "inactive".
     */
    private boolean inactive;
    
    /**
     * The common state this status has - shared across datasets.  
     */
    private GenericState genericState;
    
    /**
     * Default no-arg constructor.
     */
    public Status(){}
    
    /**
     * Constructor that accepts the short name, long name and 
     * code of the new status.
     * 
     * @param shortName The short name of the new status.
     * @param longName The long name of the new status.
     * @param code The code of the new status.
     */
    public Status(String shortName, String longName, int code){
        this.shortName = shortName;
        this.longName = longName;
        this.code = code;
    }
    
    /**
     * Get the numeric code associated with the status.
     * 
     * @return The numeric code.
     * @hibernate.property column="c_code"
     */
    public int getCode() {
        return this.code;
    }

    /**
     * Set the numeric code associated with the status.
     * 
     * @param code The numeric code.
     */
    public void setCode(int code) {
        if ( this.readOnly ){
            throw new ModelException("Cannot change code - object is read-only");
        }
        this.code = code;
    }

    /**
     * Get the short name associated with the status.
     * <p>
     * This is the name used internally when communicating with
     * other components i.e. the security system.
     * 
     * @return The short name.
     * @hibernate.property column="c_short_name"
     *                     not-null="true"
     */
    public String getShortName() {
        if ( this.readOnly ){
            throw new ModelException("Cannot change short name - object is read-only");
        }
        return this.shortName;
    }

    /**
     * Set the short name associated with the status.
     * <p>
     * This is the name used internally when communicating with
     * other components i.e. the security system.
     * 
     * @param name The short name.
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    /**
     * Get the long name associated with the status.
     * <p>
     * This is the name that should be displayed to the user.
     * 
     * @return The long name.
     * @hibernate.property column="c_long_name"
     *                     not-null="true"
     */
    public String getLongName() {
        if ( this.readOnly ){
            throw new ModelException("Cannot change long name - object is read-only");
        }
        return this.longName;
    }

    /**
     * Set the long name associated with the status.
     * <p>
     * This is the name that should be displayed to the user.
     * 
     * @param name The long name.
     */
    public void setLongName(String longName) {
        this.longName = longName;
    }

    /**
     * Get the collection of allowed status transitions.
     * 
     * @return The collection of allowed status transitions.
     * 
     * @hibernate.list cascade="none"
     *                 table="t_status_transitions" batch-size="100"
     * @hibernate.key column="c_status_id"
     * @hibernate.many-to-many class="org.psygrid.data.model.hibernate.Status"
     *                         column="c_next_status_id"
     * @hibernate.list-index column="c_index"
     */
    public List<Status> getStatusTransitions() {
        return statusTransitions;
    }

    /**
     * Set the collection of allowed status transitions.
     * 
     * @param allowedTransitions The collection of allowed status transitions.
     */
    public void setStatusTransitions(List<Status> statusTransitions) {
        this.statusTransitions = statusTransitions;
    }

    /**
     * Add a status to the collection of allowed status transitions.
     * 
     * @param status The status to be added to the collection of allowed status transitions.
     * @throws ModelException if a <code>null</code> status is added.
     */
    public void addStatusTransition(Status status) throws ModelException {
        if ( this.readOnly ){
            throw new ModelException("Cannot add status transition - object is read-only");
        }
        if ( null == status ){
            throw new ModelException("Cannot add a null status");
        }
        statusTransitions.add((Status)status);
    }

    /**
     * Retrieve a single status from the collection of allowed status transitions.
     * 
     * @param index The index of the status to retrieve.
     * @return The status at the given index.
     * @throws ModelException if no status exists at the given index.
     */
    public Status getStatusTransition(int index) throws ModelException {
        try{
            return statusTransitions.get(index);
        }
        catch (IndexOutOfBoundsException ex){
            throw new ModelException("No status found for index "+index, ex);
        }
    }

    /**
     * Retrieve the number of statuses in the collection of allowed status transitions.
     * 
     * @return The number of statuses in the collection.
     */
    public int numStatusTransitions() {
        return statusTransitions.size();
    }

    /**
     * Remove a single status from the collection of allowed status transitions.
     * 
     * @param index The index of the status to remove.
     * @throws ModelException if no status exists at the given index.
     */
    public void removeStatusTransition(int index) throws ModelException {
        if ( this.readOnly ){
            throw new ModelException("Cannot remove status transition - object is read-only");
        }
        try{
            statusTransitions.remove(index);
        }
        catch (IndexOutOfBoundsException ex){
            throw new ModelException("No status found for index "+index, ex);
        }
    }

    /**
     * Get the Boolean flag to indicate whether the status implies that the object
     * that it is applied to is inactive (True) or not.
     * <p>
     * Originally added to cater for Record statuses such as Withdrawn and
     * Deceased, which both imply that the Record is "inactive".

     * @return The inactive flag
     * @hibernate.property column="c_inactive"
     */
    public boolean isInactive() {
        return inactive;
    }

    /**
     * Set the Boolean flag to indicate whether the status implies that the object
     * that it is applied to is inactive (True) or not.
     * <p>
     * Originally added to cater for Record statuses such as Withdrawn and
     * Deceased, which both imply that the Record is "inactive".
     * 
     * @param inactive The inactive flag.
     */
    public void setInactive(boolean inactive) {
        this.inactive = inactive;
    }

 
    /**
     * Get the generic state for this Status. 
     * 
     * A GenericState defines standard states, used to give commonality across 
     * States for Records.
     * 
     * @return genericState
     */
    public GenericState getGenericState() {
		return genericState;
	}

    /**
     * Set the generic state for this Status. A GenericState defines common 
     * standard states, used to give commonality across States for Records.
     *  
     * @param genericState
     */
	public void setGenericState(GenericState genericState) {
		this.genericState = genericState;
	}

	/**
     * 
     * @return state
     * 
     * @hibernate.property column="c_generic_state"
     */
    protected String getEnumGenericState() {
		
		 if ( null == genericState ){
	            return null;
	        }
	        else{
	            return genericState.toString();
	        }
	}

	protected void setEnumGenericState(String genericState) {
		
		if ( null == genericState ){
            genericState = null;
        }
        else{
            this.genericState = GenericState.valueOf(genericState);
        }
	}
	
	public org.psygrid.data.model.dto.StatusDTO toDTO(
            Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs,
            RetrieveDepth depth) {
        //check for an already existing instance of a dto object  
        //for this status in the map of references
        org.psygrid.data.model.dto.StatusDTO dtoS = null;
        if ( dtoRefs.containsKey(this)){
            dtoS = (org.psygrid.data.model.dto.StatusDTO)dtoRefs.get(this);
        }
        if ( null == dtoS ){
            //an instance of the status has not already
            //been created, so create it, and add it to the
            //map of references
            dtoS = new org.psygrid.data.model.dto.StatusDTO();
            dtoRefs.put(this, dtoS);
            toDTO(dtoS, dtoRefs, depth);
        }
        
        return dtoS;
    }

    public void toDTO(org.psygrid.data.model.dto.StatusDTO dtoS,
                      Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs,
                      RetrieveDepth depth) {
        
        super.toDTO(dtoS, dtoRefs, depth);
        dtoS.setCode(this.code);
        dtoS.setShortName(this.shortName);
        dtoS.setLongName(this.longName);
        dtoS.setInactive(this.inactive);
        if ( RetrieveDepth.DS_WITH_DOCS != depth ){
	        org.psygrid.data.model.dto.StatusDTO[] dtoTransitions = 
	            new org.psygrid.data.model.dto.StatusDTO[this.statusTransitions.size()];
	        for (int i=0; i<this.statusTransitions.size(); i++){
	            Status s = statusTransitions.get(i);
	            dtoTransitions[i] = s.toDTO(dtoRefs, depth);
	        }
	        dtoS.setStatusTransitions(dtoTransitions);
	        
	        if (null != genericState) {
	        	dtoS.setGenericState(genericState.toString());
	        }
        }
    }

    public void lock() {
        //no need to do anything as any changes won't 
    }
        
}
