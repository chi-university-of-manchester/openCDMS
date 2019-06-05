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

import java.util.Map;

public abstract class ComponentDTO extends PersistentDTO {

    /**
     * The name of the component.
     * <p>
     * The name of the component is intended to be used
     * when designing a dataset. It should not be displayed
     * to a user when a record is being created.
     */
    protected String name;
    
    /**
     * Display text for the component.
     * <p>
     * The display text is intended to be the primary text
     * displayed to the user when an instance of the component
     * is being created.
     * <p>
     * Generally all components should have a piece of display text.
     * It is only in a small number of special cases where an
     * component could have no display text, for instance entrys
     * that are part of a composite element and the composite
     * defines the display text.
     */
    protected String displayText;
    
    /**
     * Description of the component.
     * <p>
     * The description of the component is intended to provide
     * additional information and instruction, over and above
     * the display text.
     * <p>
     * Components may or may not have a description, depending
     * upon the requirements of the component.
     */
    protected String description;
    
    /**
     * The Life Sciences ID of the data element that describes this data element OR this
     * data element instance.
     */
	private LSIDDTO lsid;
	
	private LSIDDTO instanceLSID;

	/**
	 * Meta data reference, points to a description of the object
	 */
	private String metaDataReference;
    
    public ComponentDTO(){}
    
    public ComponentDTO(String name){
        this.name = name;
    }
    
    public ComponentDTO(String name, String displayText){
        this.name = name;
        this.displayText = displayText;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }
    
    public LSIDDTO getInstanceLSID(){
    	return this.instanceLSID;
    }
    
    public void setInstanceLSID(LSIDDTO instanceLSID){
    	this.instanceLSID = instanceLSID;
    }
    
	public LSIDDTO getLSID(){
		return this.lsid;
	}

	/**
	 * Get the meta data reference
	 * 
	 * @return The meta data reference
	 * 
	 */
	public String getMetaDataReference() {
		return metaDataReference;
	}

	public void setLSID(LSIDDTO lsid){
		this.lsid = lsid;
	}
	/**
	 * Set the meta data reference
	 * 
	 * @param urn The URN for the meta data reference
	 */
	public void setMetaDataReference(String urn) {
		this.metaDataReference = urn;
	}
	
    public abstract org.psygrid.data.model.hibernate.Component toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs);
    
    public void toHibernate(org.psygrid.data.model.hibernate.Component hC, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        super.toHibernate(hC, hRefs);
        hC.setName(this.name);
        hC.setDescription(this.description);
        hC.setDisplayText(this.displayText);
        if(lsid != null){
            hC.setLSID(lsid.toHibernate(hRefs));
        }
        
        if(instanceLSID != null){
        	hC.setInstanceLSID(instanceLSID.toHibernate(hRefs));
        }
        
        hC.setMetaDataReference(this.metaDataReference);
    }
}
