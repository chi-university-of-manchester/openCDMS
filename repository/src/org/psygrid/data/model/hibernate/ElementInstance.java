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

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.jgoodies.binding.beans.ExtendedPropertyChangeSupport;

/**
 * Class to represent an instance of an Element of a DataSet
 * in a Record.
 * <p>
 * The structure of IElementInstance objects in a record is intended
 * to mimic the structure of IElement objects in the data set that the 
 * record is an instance of. This becomes important when IElement objects
 * that have a rowIndex are considered, for which multiple 
 * IElementInstance objects referencing the same IElement may occur in
 * the same record.
 * 
 * @author Rob Harper
 * @see IElement
 * 
 * @hibernate.joined-subclass table="t_elem_insts"
 * @hibernate.joined-subclass-key column="c_id"
 */
public abstract class ElementInstance extends Provenanceable {

    private static final String PROV_ITEMS_PROPERTY = "provItems";

    /**
     * Object that contains all the logic required to support the propagation of
     * PropertyChange events.
     */
    protected ExtendedPropertyChangeSupport propertyChangeSupport = new 
        ExtendedPropertyChangeSupport(this);
    
    /**
     * The record that the instance is a part of.
     */
    protected Record record;
    
    /**
     * The collection of provenance objects that record the history
     * of state changes to the value of the response over
     * its lifetime
     */
    protected List<Provenance> provItems = new ArrayList<Provenance>();
    
	/**
	 * Default no-arg constructor, as required by the Hibernate framework
	 * for all persistable classes
	 */
	public ElementInstance(){}
	
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }
    
    /**
     * Get the record that the instance is a part of.
     * <p>
     * Hibernate note: the many-to-one relationship with Record should
     * formally have not-null="true". This had to be relaxed for the case
     * where the instance <i>is</i> a record, and the ensuing circular
     * relationship meant that the object could not be persisted.
     * 
     * @return The record.
     * 
     * @hibernate.many-to-one class="org.psygrid.data.model.hibernate.Record"
     *                        column="c_record_id"
     *                        not-null="false"
     */    
    public Record getRecord() {
        return record;
    }

    public void setRecord(Record record) {
        this.record = record;
    }

    /**
     * Get the collection of provenance objects that record the history
     * of state changes to the value of the response over its lifetime.
     * 
     * @return The collection of provenance objects.
     * 
     * @hibernate.list cascade="all"
     * @hibernate.one-to-many class="org.psygrid.data.model.hibernate.Provenance"
     * @hibernate.key column="c_response_id" not-null="false"
     * @hibernate.list-index column="c_index"
     */
    public List<Provenance> getProvItems() {
        return provItems;
    }

    /**
     * Set the collection of provenance objects that record the history
     * of state changes to the value of the response over
     * its lifetime.
     * 
     * @param provItems The collection of provenance objects.
     */
    protected void setProvItems(List<Provenance> provItems) {
        List<Provenance> oldItems = this.provItems;
        this.provItems = provItems;
        if(propertyChangeSupport.hasListeners(PROV_ITEMS_PROPERTY)){
        	propertyChangeSupport.firePropertyChange(PROV_ITEMS_PROPERTY, oldItems,
                this.provItems);
        }
    }
    
    /**
     * Get the list of provenance objects that details how the state
     * of objects associated with the element instance have changed
     * over its lifetime.
     * <p>
     * The list will contain all provenance objects, relating to 
     * changes in state of objects of any class.
     * 
     * @return List of provenance items.
     */
    public List<Provenance> getProvenance() {
        //Have to return a new list as it is not possible to cast
        //a list of type List<Provenance> to a list of type List<IProvenance>
        List<Provenance> list = new ArrayList<Provenance>(this.provItems);
        return Collections.unmodifiableList(list);
    }    
    
    public boolean removeProvenanceItem(Provenance prov){
    	return provItems.remove(prov);
    }
    
    public Provenance removeProvenanceItemByIndex(int index) throws IndexOutOfBoundsException{
    	return (Provenance)provItems.remove(index);
    }
    
    /**
     * Get the list of provenance objects that details how the state
     * of objects of a given class associated with the element instance 
     * have changed over its lifetime.
     * <p>
     * The list will contain only provenance objects that relate to
     * changes in state of objects of the given class.
     * 
     * @param c The class for which to retrieve provenance items.
     * @return List of provenance items.
     */
    @SuppressWarnings("unchecked")
    public List<Provenance> getProvenance(Class c) {
        List<Provenance> provs = new ArrayList<Provenance>();
        //iterate through all provenance items, adding only those
        //that relate to changes to objects of the given class to
        //the list that will be returned by the method.
        for ( Provenance p: this.getProvItems() ){
            if ( null != p.getTheCurrentValue() ){
                if ( c.isAssignableFrom(p.getTheCurrentValue().getClass()) ){
                    provs.add(p);
                }
            }
            else if ( null != p.getThePrevValue() ){
                if ( c.isAssignableFrom(p.getThePrevValue().getClass()) ){
                    provs.add(p);
                }
            }
        }
        return Collections.unmodifiableList(provs);
    }    
    
    public abstract org.psygrid.data.model.dto.ElementInstanceDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth);
    
    public void toDTO(org.psygrid.data.model.dto.ElementInstanceDTO dtoEI, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        super.toDTO(dtoEI, dtoRefs, depth);

        org.psygrid.data.model.dto.ProvenanceDTO[] dtoProvItems = new org.psygrid.data.model.dto.ProvenanceDTO[this.provItems.size()];
        for ( int i=0; i<this.provItems.size(); i++){
            Provenance p = this.provItems.get(i);
            dtoProvItems[i] = p.toDTO(dtoRefs, depth);
        }
        dtoEI.setProvItems(dtoProvItems);        
        
        if ( null != this.record ){
            dtoEI.setRecord(this.record.toDTO(dtoRefs, depth));
        }
    }
    
    protected abstract void addChildTasks(Record r);
    
    protected abstract Element findElement();
}
