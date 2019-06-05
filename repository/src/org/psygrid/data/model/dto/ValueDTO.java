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

import java.util.HashMap;
import java.util.Map;


/**
 * Base class of all classes that represent values of Responses.
 * 
 * @author Rob Harper
 * 
 */
public abstract class ValueDTO extends ProvenanceableDTO {

    /**
     * Flag to represent whether the Value has been deprecated.
     */
	private boolean deprecated;
	
    /**
     * Standard code to be used with or instead of the actual
     * value.
     */
    private StandardCodeDTO standardCode;
	
    /**
     * The id of the unit of measurement for the value.
     */
    protected Long unitId;
    
    protected boolean transformed;
    
    protected boolean hidden;
    
	/**
	 * Default no-arg constructor, as required by the Hibernate framework
	 * for all persistable classes.
	 */
	public ValueDTO(){};
	
	/**
	 * Get the flag to represent whether the value has been deprecated
	 * 
	 * @return The deprecated flag.
	 */
	public boolean isDeprecated() {
		return deprecated;
	}

	/**
	 * Set the flag to represent whether the value has been deprecated
	 * 
	 * @param deprecated The deprecated flag.
	 */
	public void setDeprecated(boolean deprecated) {
		this.deprecated = deprecated;
	}

    public StandardCodeDTO getStandardCode() {
        return this.standardCode;
    }

    public void setStandardCode(StandardCodeDTO standardCode) {
        this.standardCode = standardCode;
    }

    public Long getUnitId() {
        return unitId;
    }

    /**
     * Set the Unit of measurement for the Response.
     * <p>
     * This override is required by Hibernate.
     * 
     * @param unit The Unit of measurement
     */
    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public boolean isTransformed() {
        return transformed;
    }

    public void setTransformed(boolean transformed) {
        this.transformed = transformed;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public org.psygrid.data.model.hibernate.Value toHibernate(){
        //create list to hold references to objects in the Value's
        //object graph which have multiple references to them within
        //the object graph. This is used so that each object instance
        //is copied to its hibernate equivalent once and once only
        Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> dtoRefs = new HashMap<PersistentDTO, org.psygrid.data.model.hibernate.Persistent>();
        org.psygrid.data.model.hibernate.Value hV = toHibernate(dtoRefs);
        dtoRefs = null;
        return hV;
    }
    
    public abstract org.psygrid.data.model.hibernate.Value toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs);

    public void toHibernate(org.psygrid.data.model.hibernate.Value hV, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        super.toHibernate(hV, hRefs);
        hV.setDeprecated(this.deprecated);
        hV.setTransformed(this.transformed);
        hV.setHidden(this.hidden);
        if ( null != this.standardCode ){
            hV.setStandardCode(this.standardCode.toHibernate(hRefs));
        }
        if ( null != this.unitId ){
            hV.setUnitId(this.unitId);
        }
    }
    
    
}
