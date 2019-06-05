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

import java.util.List;
import java.util.Map;

import org.psygrid.data.model.IIntegerValue;
import org.psygrid.data.model.IValue;
import org.psygrid.data.model.dto.ElementDTO;
import org.psygrid.data.query.IEntryStatement;
import org.psygrid.data.query.QueryOperation;
import org.psygrid.data.query.QueryStatementValue;
import org.psygrid.data.query.hibernate.IntegerStatement;

/**
 * Class to represent an Entry whose value is an integer.
 * 
 * @author Rob Harper
 * 
 * @hibernate.joined-subclass table="t_integer_entrys"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class IntegerEntry extends BasicEntry {

    /**
     * Default value for the entry
     */
    private Integer defaultValue;
    
    /**
     * Default no-arg constructor, as required by the Hibernate framwework
     * for all persistable classes.
     */
    public IntegerEntry(){}
    
    /**
     * Constructor that accepts the name of the new integer entry.
     * 
     * @param name The name of the new integer entry.
     */
    public IntegerEntry(String name){
        super(name);
    }
 
    /**
     * Constructor that accepts the name and status of the
     * new integer entry.
     * 
     * @param name The name of the new integer entry.
     * @param entryStatus The status of the new integer entry.
     */
    public IntegerEntry(String name, EntryStatus entryStatus){
        super(name, entryStatus);
    }

    /**
     * Constructor that accepts the name and display text
     * of the new integer entry.
     * 
     * @param name The name of the new integer entry.
     * @param displayText The display text of the new integer entry.
     */
    public IntegerEntry(String name, String displayText){
        super(name, displayText);
    }

    /**
     * Constructor that accepts the name, display text and
     * status of the new integer entry.
     * 
     * @param name The name of the new integer entry.
     * @param displayText The display text of the new integer entry.
     * @param entryStatus The status of the new integer entry.
     */
    public IntegerEntry(String name, String displayText, EntryStatus entryStatus){
        super(name, displayText, entryStatus);
    }

    /**
     * Get the default value for the entry.
     * 
     * @return The default value.
     * @hibernate.property column="c_default"
     */
    public Integer getDefaultValue() {
        return defaultValue;
    }

    /**
     * Set the default value for the entry.
     * 
     * @param defaultValue The default value.
     */
    public void setDefaultValue(Integer defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String formatValue(IValue value) throws ModelException {
        IntegerValue iv = (IntegerValue)value;
        return iv.getValue().toString();
    }

    public IIntegerValue generateValue() {
        IntegerValue iv = new IntegerValue(this.defaultValue);
        if ( this.units.size() > 0 ){
            iv.setUnit(this.units.get(0));
        }
        return iv;
    }

    public Class getValueClass() {
        return IntegerValue.class;
    }
    
    public org.psygrid.data.model.dto.IntegerEntryDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        //check for an already existing instance of a dto object for this 
        //numeric entry in the map of references
        org.psygrid.data.model.dto.IntegerEntryDTO dtoIE = null;
        if ( dtoRefs.containsKey(this)){
            dtoIE = (org.psygrid.data.model.dto.IntegerEntryDTO)dtoRefs.get(this);
        }
        else{
            //an instance of the numeric entry has not already
            //been created, so create it, and add it to the map 
            //of references
            dtoIE = new org.psygrid.data.model.dto.IntegerEntryDTO();
            dtoRefs.put(this, dtoIE);
            toDTO(dtoIE, dtoRefs, depth);
        }
        
        return dtoIE;
    }
    
    public void toDTO(org.psygrid.data.model.dto.IntegerEntryDTO dtoIE, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        super.toDTO(dtoIE, dtoRefs, depth);
        if ( depth != RetrieveDepth.REP_SAVE ){
            dtoIE.setDefaultValue(this.defaultValue);
        }
    }

	@Override
	public boolean isForBasicStatistics() {
		return true;
	}

	@Override
	public ElementDTO instantiateDTO() {
		return new org.psygrid.data.model.dto.IntegerEntryDTO();
	}
	
	public List<QueryOperation> getQueryOperations() {
		return QueryOperation.getOperatorsForNumericEntry();
	}

	public IEntryStatement createStatement(QueryStatementValue queryStatementValue) {
		return new IntegerStatement(queryStatementValue.getIntegerValue());
	}

}
