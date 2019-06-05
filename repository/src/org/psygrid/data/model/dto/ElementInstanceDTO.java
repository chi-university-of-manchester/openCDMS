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

import java.util.List;
import java.util.Map;


/**
 * Class to represent an instance of an Element of a DataSet
 * in a Record.
 * 
 * @author Rob Harper
 */
public abstract class ElementInstanceDTO extends ProvenanceableDTO {

    /**
     * The record that the instance is a part of.
     */
    protected RecordDTO record;
    
    /**
     * The collection of provenance objects that record the history
     * of state changes to objects associated with the element instance
     * over its lifetime
     */
    protected ProvenanceDTO[] provItems = new ProvenanceDTO[0];
    
	/**
	 * Default no-arg constructor, as required by the Hibernate framework
	 * for all persistable classes
	 */
	public ElementInstanceDTO(){}
	
    /**
     * Get the record that the instance is a part of.
     * 
     * @return The record.
     */    
    public RecordDTO getRecord() {
        return record;
    }

    public void setRecord(RecordDTO record) {
        this.record = record;
    }

    /**
     * Get the collection of provenance objects that record the history
     * of state changes to the value of the response over its lifetime.
     * 
     * @return The collection of provenance objects.
     * 
     */
    public ProvenanceDTO[] getProvItems() {
        return provItems;
    }

    /**
     * Set the collection of provenance objects that record the history
     * of state changes to the value of the response over
     * its lifetime.
     * 
     * @param provItems The collection of provenance objects.
     */
    public void setProvItems(ProvenanceDTO[] provItems) {
        this.provItems = provItems;
    }
        
    public abstract org.psygrid.data.model.hibernate.ElementInstance toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs);
    
    public void toHibernate(org.psygrid.data.model.hibernate.ElementInstance hEI, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        super.toHibernate(hEI, hRefs);
        
        List<org.psygrid.data.model.hibernate.Provenance> hProvItems = hEI.getProvItems();
        for ( int i=0; i<this.provItems.length; i++ ){
            ProvenanceDTO p = this.provItems[i];
            if ( null != p ){
                hProvItems.add(p.toHibernate(hRefs));
            }
        }
        
        if ( null != this.record ){
            hEI.setRecord(this.record.toHibernate(hRefs));
        }
    }
    
}
