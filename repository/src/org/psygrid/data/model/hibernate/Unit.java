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
 * Class representing a unit of measurement
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_units"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class Unit extends Persistent {

    public enum UnitChainDescriptor{
    	Start,
    	Middle,
    	End
    }

	/**
	 * Long textual description of the unit
	 */
	private String description;
	
	/**
	 * Short abbreviation of the unit
	 */
	private String abbreviation;
    
    /**
     * The base unit for this unit.
     * <p>
     * If the base unit is not <code>null</code> then it is assumed that
     * the factor property is also not null, and this can be used to convert
     * any value having this unit to the units of the base unit by multiplying
     * the value by the factor.
     */
    private Unit baseUnit;
    
    /**
     * The factor used to convert a value having this unit to the units of
     * the base unit by multiplication.
     * <p>
     * If the base unit is <code> null then this property is redundant and 
     * should be <code>null</code> also.
     */
    private Double factor;
	
	/**
	 * Default no-arg constructor, as required by the Hibernate framework
	 * for all persistable classes.
	 */
	public Unit(){};
    
    /**
     * Constructor that accepts the abbreviation of the Unit
     * 
     * @param abbreviation The abbreviation
     */
    public Unit(String abbreviation){
        this.abbreviation = abbreviation;
    }

    /**
     * Constructor that accepts the abbreviation and description
     * of the Unit.
     * 
     * @param abbreviation The abbreviation
     * @param description The description
     */
    public Unit(String abbreviation, String description){
        this.abbreviation = abbreviation;
        this.description = description;
    }

	/**
	 * Get the short abbreviation of the unit
	 * 
	 * @return The abbreviation
	 * 
	 * @hibernate.property column="c_abbrev" 
     *                     not-null="true"
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
	 * 
	 * @hibernate.property column="c_desc"
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
	
    /**
     * Get the base unit for this unit.
     * <p>
     * If the base unit is not <code>null</code> then it is assumed that
     * the factor property is also not null, and this can be used to convert
     * any value having this unit to the units of the base unit by multiplying
     * the value by the factor.
     * 
     * @return The base unit.
     * @hibernate.many-to-one class="org.psygrid.data.model.hibernate.Unit"
     *                        column="c_base_unit_id"
     *                        unique="false"
     *                        not-null="false"
     *                        cascade="none"
     */
    public Unit getBaseUnit() {
        return baseUnit;
    }

    /**
     * Set the base unit for this unit.
     * <p>
     * If the base unit is not <code>null</code> then it is assumed that
     * the factor property is also not null, and this can be used to convert
     * any value having this unit to the units of the base unit by multiplying
     * the value by the factor.
     *
     * @param baseUnit The base unit.
     */
    public void setBaseUnit(Unit baseUnit) {
        this.baseUnit = baseUnit;
    }

    /**
     * Get the factor used to convert a value having this unit to the units of
     * the base unit by multiplication.
     * <p>
     * If the base unit is <code> null then this property is redundant and 
     * should be <code>null</code> also.
     * 
     * @return The factor to convert to the base unit.
     * @hibernate.property column="c_factor"
     */
    public Double getFactor() {
        return factor;
    }

    /**
     * Set the factor used to convert a value having this unit to the units of
     * the base unit by multiplication.
     * <p>
     * If the base unit is <code> null then this property is redundant and 
     * should be <code>null</code> also.
     * 
     * @param factor The factor to convert to the base unit.
     */
    public void setFactor(Double factor) {
        this.factor = factor;
    }

    public org.psygrid.data.model.dto.UnitDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        //check for an already existing instance of a dto object for this 
        //unit in the map of references
        org.psygrid.data.model.dto.UnitDTO dtoU = null;
        if ( dtoRefs.containsKey(this)){
            dtoU = (org.psygrid.data.model.dto.UnitDTO)dtoRefs.get(this);
        }
        if ( null == dtoU ){
            //an instance of the unit has not already
            //been created, so create it, and add it to the map 
            //of references
            dtoU = new org.psygrid.data.model.dto.UnitDTO();
            dtoRefs.put(this, dtoU);
            toDTO(dtoU, dtoRefs, depth);
        }

        return dtoU;
    }
    
    public void toDTO(org.psygrid.data.model.dto.UnitDTO dtoU, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        super.toDTO(dtoU, dtoRefs, depth);
        dtoU.setAbbreviation(this.abbreviation);
        dtoU.setDescription(this.description);
        if ( null != this.baseUnit ){
            dtoU.setBaseUnit(this.baseUnit.toDTO(dtoRefs, depth));
        }
        dtoU.setFactor(this.factor);
    }
    
    public class UnitChainLink {
    	
    	private boolean isRecursionPoint = false;
    	private List<UnitChainDescriptor> unitDescriptor = new ArrayList<UnitChainDescriptor>();
    	private Unit unit;
    	
    	public UnitChainLink(Unit unit) {
    		this.unit = unit;
    	}
    	
    	public void setUnitChainType(UnitChainDescriptor unitDescriptor) {
    		this.unitDescriptor.add(unitDescriptor);
    	}
    	
    	public Unit getUnit() {
    		return unit;
    	}
    	
    	public List<UnitChainDescriptor> getUnitChainType() {
    		return this.unitDescriptor;
    	}

		public boolean isRecursionPoint() {
			return isRecursionPoint;
		}

		public void setRecursionPoint(boolean isRecursionPoint) {
			this.isRecursionPoint = isRecursionPoint;
		}
		
	    public boolean matchesType(UnitChainDescriptor descriptor){
	    	for(UnitChainDescriptor thisDescriptor: this.unitDescriptor){
	    		if(thisDescriptor.equals(descriptor)){
	    			return true;
	    		}
	    	}
	    	return false;
	    }
	    
    }
    

    /**
     * Unit - equivalence does not take base units into account.
     * @param comparisonUnit
     * @return
     */
    protected boolean isUnitEquivalentTo(Unit comparisonUnit) {
    	if(comparisonUnit == null) {
    		return false;
    	}
    	
    	//Compare description
    	if(description == null) {
    		if(comparisonUnit.description != null) {
    			return false;
    		}
    	}else if (!description.equals(comparisonUnit.description)){
    		return false;
    	}
    	
    	//Compare abbreviation
    	if(!abbreviation.equals(comparisonUnit.abbreviation)) {
    		return false;
    	}
    	
    	//Compare factor
    	if(factor == null) {
    		if(comparisonUnit.factor != null) {
    			return false;
    		}
    	}else if(!factor.equals(comparisonUnit.factor)){
    		return false;
    	}
    	
    	return true;
    }
    
    /**
     * @param alreadyComparedBaseUnits
     * @param chainIndex
     * @param comparisonUnit
     * @return
     */
    protected boolean isEquivalentTo(Unit comparisonUnit, List<UnitChainLink> unitChainList) {
    	
    	
    	if(!this.isUnitEquivalentTo(comparisonUnit)){
    		return false;
    	}
    	
    	//Compare up to the end point.
    	//If the end point has a base unit (i.e. recursion), make sure that
    	//the recursion point is logically equivalent to the end point's base unit.
    	
    	boolean returnValue;
    	
    	UnitChainLink thisUnit = this.getSelfInUnitChain(unitChainList);
    	
    	if(thisUnit.matchesType(UnitChainDescriptor.End)){
    		
    		if(getBaseUnit() == null){ //NO RECURSION
    			returnValue = true;
    		}else{ //Make sure the recursion points are unit-equivalent
    			returnValue =  getBaseUnit().isUnitEquivalentTo(comparisonUnit.getBaseUnit());
    		}
    		
    		
    	}else{ //Keep comparing up the chain
    		returnValue =  getBaseUnit().isEquivalentTo(comparisonUnit.getBaseUnit(), unitChainList);
    	}
    	
    	return returnValue;
    }
    
    protected void buildUnitChainInfo(List<UnitChainLink> unitList, Unit unit) {
    	UnitChainLink link = new UnitChainLink(unit);
    	if(unitList.size() == 0) { //UNIT LIST SIZE IS ZERO - FIRST ITERATION
    		link.setUnitChainType(UnitChainDescriptor.Start);
    		unitList.add(link);
    	}else { //NEED TO CHECK AND SEE IF WE HAVE COME UPON A RECURSION POINT
    		int linkListSize = unitList.size();
    		int recursionIndex = -1;
    		for(int i = 0; i < linkListSize; i++) {
    			if(unitList.get(i).getUnit().isUnitEquivalentTo(unit)) {
    				recursionIndex = i;
    				break;
    			}
    		}
    		
    		if(recursionIndex != -1) { //RECURSION POINT FOUND
    			unitList.get(recursionIndex).setRecursionPoint(true);
    			
    			//Now find the item in the liset with its base unit equivalent to the item at the recursion index.
    			//at the recursionIndex. That's the END of the chain.
    			for(int i = recursionIndex + 1;  i < unitList.size(); i++) {
    				if(unitList.get(i).getUnit().getBaseUnit().isUnitEquivalentTo(unitList.get(recursionIndex).getUnit())) {
    					unitList.get(i).setUnitChainType(UnitChainDescriptor.End);
    					break;
    				}
    			}
    			
    			//The chain has been completely mapped, so return.
    			return;
    		}else { //RECURSION POINT NOT FOUND
    			unitList.add(link);
    		}
    	}
    		
		if(unit.getBaseUnit() == null) {
			//This is also the end of the link!
			link.setUnitChainType(UnitChainDescriptor.End);
			return;
		}else {
			buildUnitChainInfo(unitList, unit.getBaseUnit());
		}	
    }
    
    public List<UnitChainLink> buildUnitChainInfo() {
    	List<UnitChainLink> unitList = new ArrayList<UnitChainLink>();
    	buildUnitChainInfo(unitList, this);
    	return unitList;
    }
    
    private UnitChainLink getSelfInUnitChain(List<UnitChainLink> unitChainList){
    	int chainLength = unitChainList.size();
    	int index = -1;
    	UnitChainLink link = null;
    	for(int i = 0; i < chainLength; i++){
    		if(unitChainList.get(i).getUnit() == this){
    			link = unitChainList.get(i);
    			break;
    		}
    	}
    	
    	return link;
    }
    
    /**
     * NOTE: Checks the for equivalence for a base chain of up to five. If equivalent at that point,
     * the method simply returns true. This is to prevent infinite looping due to bi-directional relationships
     * @param alreadyComparedBaseUnits
     * @param comparisonUnit
     * @return
     */
    public boolean isEquivalentTo(Unit comparisonUnit) {
    	List<UnitChainLink> unitChainList = buildUnitChainInfo();
    	return isEquivalentTo(comparisonUnit, unitChainList);
    }
}
