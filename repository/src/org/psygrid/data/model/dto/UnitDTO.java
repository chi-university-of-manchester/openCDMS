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


/**
 * Class representing a unit of measurement
 * 
 * @author Rob Harper
 *
 */
public class UnitDTO extends PersistentDTO {

	/**
	 * Long textual description of the unit
	 */
	private String description;
	
	/**
	 * Short abbreviation of the unit
	 */
	private String abbreviation;
	
    private UnitDTO baseUnit;
    
    private Double factor;
    
	/**
	 * Default no-arg constructor, as required by the Hibernate framework
	 * for all persistable classes.
     * 
     * Scope is protected as all units must have an abbreviation
	 */
	public UnitDTO(){};
    
	/**
	 * Get the short abbreviation of the unit
	 * 
	 * @return The abbreviation
	 */
	public String getAbbreviation() {
		return abbreviation;
	}

	/**
	 * Set the short abbreviation of the unit
	 * 
	 * @param abbreviation The abbreviation
	 */
	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}

	/**
	 * Get the long textual description of the unit
	 * 
	 * @return The description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Set the long textual description of the unit
	 * 
	 * @param description The description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
    public UnitDTO getBaseUnit() {
        return baseUnit;
    }

    public void setBaseUnit(UnitDTO baseUnit) {
        this.baseUnit = baseUnit;
    }

    public Double getFactor() {
        return factor;
    }

    public void setFactor(Double factor) {
        this.factor = factor;
    }

    public org.psygrid.data.model.hibernate.Unit toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        //check for an already existing instance of a hibernate object for this 
        //unit in the map of references
        org.psygrid.data.model.hibernate.Unit hU = null;
        if ( hRefs.containsKey(this)){
            hU = (org.psygrid.data.model.hibernate.Unit)hRefs.get(this);
        }
        if ( null == hU ){
            //an instance of the unit has not already
            //been created, so create it, and add it to the map 
            //of references
            hU = new org.psygrid.data.model.hibernate.Unit();
            hRefs.put(this, hU);
            toHibernate(hU, hRefs);
        }

        return hU;
    }
    
    public void toHibernate(org.psygrid.data.model.hibernate.Unit hU, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        super.toHibernate(hU, hRefs);
        hU.setAbbreviation(this.abbreviation);
        hU.setDescription(this.description);
        if ( null != this.baseUnit ){
            hU.setBaseUnit(baseUnit.toHibernate(hRefs));
        }
        hU.setFactor(this.factor);
    }
}
