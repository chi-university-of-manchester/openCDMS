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

import java.util.Map;

/**
 * Class to contain common properties that are required by 
 * all classes that will be rendered by the data entry 
 * application.
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_components"
 * @hibernate.joined-subclass-key column="c_id"
 */
public abstract class Component extends Persistent {

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
    
	protected LSID lsid = null;

	/**
	 * Meta data reference, points to a description of the object
	 */
	protected String metaDataReference;


	protected LSID instanceLSID = null;
	
    
    public Component(){}
    
    public Component(String name){
        this.name = name;
    }
    
    public Component(String name, String displayText){
        this.name = name;
        this.displayText = displayText;
    }
    
    /**
     * Get name of the component.
     * <p>
     * The name of the component is intended to be used
     * when designing a dataset. It should not be displayed
     * to a user when a record is being created.
     * 
     * @return The name.
     * @hibernate.property column = "c_name" index="component_name_index"
     */
    public String getName() {
        return name;
    }

    /**
     * Set name of the component.
     * <p>
     * The name of the component is intended to be used
     * when designing a dataset. It should not be displayed
     * to a user when a record is being created.
     * 
     * @param name The name.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Get the description of the component.
     * <p>
     * The description of the component is intended to provide
     * additional information and instruction, over and above
     * the display text.
     * <p>
     * Components may or may not have a description, depending
     * upon the requirements of the component.
     * 
     * @return The description
     * @hibernate.property column="c_description" 
     *                     type="text"
     *                     length="4000"
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the description of the component.
     * <p>
     * The description of the component is intended to provide
     * additional information and instruction, over and above
     * the display text.
     * <p>
     * Components may or may not have a description, depending
     * upon the requirements of the component.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the display text for the component.
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
     * 
     * @return The display text.
     * @hibernate.property column="c_display_text" 
     *                     type="text"
     *                     length="4000"
     */
    public String getDisplayText() {
        return displayText;
    }

    /**
     * Set the display text for the component.
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
     * 
     * @param displayText The display text.
     */
    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }
        
    /**
     * @hibernate.many-to-one class="org.psygrid.data.model.hibernate.LSID"
     * 						  cascade="all"
     * 						  column="c_lsid_id"
     * 						  not-null="false"
     * 						  fetch="join"              
     */    
	public LSID getLSID(){
		return this.lsid;
	}
	public void setLSID(LSID lsid){
		this.lsid = lsid;
	}

	/**
	 * Get the meta data reference
	 * 
	 * @return The meta data reference
	 * 
	 * @hibernate.property column = "c_metadataref"
	 */
	public String getMetaDataReference() {
		return metaDataReference;
	}
	
	/**
	 * @hibernate.many-to-one class="org.psygrid.data.model.hibernate.LSID"
	 * 						cascade="all"
	 * 						column="c_lsid_instance_id"
	 * 						not-null="false"
	 * 						fetch="join"
	 */
	public LSID getInstanceLSID(){
		return this.instanceLSID;
	}
	public void setInstanceLSID(LSID lsid){
		this.instanceLSID = lsid;
	}
	/**
	 * Set the meta data reference
	 * 
	 * @param urn The URN for the meta data reference
	 */
	public void setMetaDataReference(String urn) {
		this.metaDataReference = urn;
	}
	
    public abstract org.psygrid.data.model.dto.ComponentDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth);
    
    public void toDTO(org.psygrid.data.model.dto.ComponentDTO dtoE, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        super.toDTO(dtoE, dtoRefs, depth);
        if ( depth != RetrieveDepth.REP_SAVE ){
            dtoE.setName(this.name);
            dtoE.setDescription(this.description);
            dtoE.setDisplayText(this.displayText);
            if(this.lsid != null){
                dtoE.setLSID(this.lsid.toDTO(dtoRefs, depth));
            }
            
            if(this.instanceLSID != null){
            	dtoE.setInstanceLSID(this.instanceLSID.toDTO(dtoRefs, depth));
            }
            
    	dtoE.setMetaDataReference(this.metaDataReference);
         }
    }

}
