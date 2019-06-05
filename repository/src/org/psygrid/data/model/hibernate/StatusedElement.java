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
 * Class to represent an element for which a collection of
 * statuses may be defined.
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_statused_elements"
 * @hibernate.joined-subclass-key column="c_id"
 */
public abstract class StatusedElement extends Element {

    /**
     * Collection of statuses that instances of the element
     * may have.
     */
    protected List<Status> statuses = new ArrayList<Status>();
    
    public StatusedElement(){
    }
    
    public StatusedElement(String name){
        super(name);
    }
    
    public StatusedElement(String name, String displayText){
        super(name, displayText);
    }
    
    /**
     * Get the collection of status objects associated with the element.
     * 
     * @return The collection of status elements.
     * 
     * @hibernate.list cascade="all-delete-orphan" batch-size="100"
     * @hibernate.one-to-many class="org.psygrid.data.model.hibernate.Status"
     * @hibernate.key column="c_element_id" not-null="true"
     * @hibernate.list-index column="c_index"
     */
    public List<Status> getStatuses() {
        return statuses;
    }

    /**
     * Set the collection of status objects associated with the element.
     * 
     * @param statuses The collection of status elements.
     */
    public void setStatuses(List<Status> statuses) {
        this.statuses = statuses;
    }

    /**
     * Add a status object to the element's collection of status
     * objects.
     * 
     * @param status The status object to add.
     * @throws ModelException if the status object being added 
     * is <code>null</code>.
     */
    public void addStatus(Status status) throws ModelException {
        if ( null == status ){
            throw new ModelException("Cannot add a null status object");
        }
        statuses.add((Status)status);
    }

    /**
     * Get a single status object from the element's collection
     * of status objects.
     * 
     * @param index The index in the collection to retrieve the 
     * status from.
     * @return The status object.
     * @throws ModelException if no status object exists for the 
     * given index.
     */
    public Status getStatus(int index) throws ModelException {
        try{
            return statuses.get(index);
        }
        catch (IndexOutOfBoundsException ex){
            throw new ModelException("No status found for index "+index, ex);
        }
    }

    /**
     * Get the number of status objects associated with the element.
     * 
     * @return The number of status objects.
     */
    public int numStatus() {
        return this.statuses.size();
    }

    /**
     * Remove a single status object from the element's collection
     * of status objects.
     * 
     * @param index The index in the collection to retrieve the 
     * status from.
     * @throws ModelException if no status object exists for the 
     * given index.
     */
    public void removeStatus(int index) throws ModelException {
        try{
            Status s = statuses.remove(index);
            if ( null != s.getId() && null != this.getDataSet() ){
                //status object being removed has already been
                //persisted so add it to the list of objects to delete
                this.getDataSet().getDeletedObjects().add(s);
            }
        }
        catch(IndexOutOfBoundsException ex){
            throw new ModelException("No status found for index "+index, ex);
        }
    }
    
    protected void initializeInstance(StatusedInstance instance){
        //set default status to the zeroth status in the list
        if ( this.statuses.size() > 0 ){
            instance.changeStatus(this.statuses.get(0));
        }        
    }
    
    public abstract org.psygrid.data.model.dto.StatusedElementDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth);
    
    public void toDTO(org.psygrid.data.model.dto.StatusedElementDTO dtoE, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        super.toDTO(dtoE, dtoRefs, depth);
        if ( RetrieveDepth.RS_COMPLETE != depth &&
        	 RetrieveDepth.RS_NO_BINARY != depth &&
        	 RetrieveDepth.RS_SUMMARY != depth ){
        
            org.psygrid.data.model.dto.StatusDTO[] dtoStatuses = 
                new org.psygrid.data.model.dto.StatusDTO[this.statuses.size()];
            for (int i=0; i<this.statuses.size(); i++){
                Status s = statuses.get(i);
                dtoStatuses[i] = s.toDTO(dtoRefs, depth);
            }
            dtoE.setStatuses(dtoStatuses);
            
        }
    }

}
