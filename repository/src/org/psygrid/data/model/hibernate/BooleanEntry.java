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

import org.psygrid.data.model.IValue;
import org.psygrid.data.model.dto.ElementDTO;
import org.psygrid.data.query.IEntryStatement;
import org.psygrid.data.query.QueryOperation;
import org.psygrid.data.query.QueryStatementValue;

/**
 * Interface to represent an entry for collecting boolean data.
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_boolean_entrys"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class BooleanEntry extends BasicEntry {

    /**
     * Default no-arg constructor, as required by the Hibernate framwework
     * for all persistable classes.
     * 
     * Scope is protected as all boolean entrys must have a name.
     */
    public BooleanEntry(){}
    
    /**
     * Constructor that accepts the name of the new boolean entry.
     * 
     * @param name The name of the new boolean entry.
     */
    public BooleanEntry(String name){
        super(name);
    }
 
    /**
     * Constructor that accepts the name and status of the 
     * new boolean entry.
     * 
     * @param name The name of the new boolean entry.
     * @param entryStatus The status of the new boolean entry.
     */
    public BooleanEntry(String name, EntryStatus entryStatus){
        super(name, entryStatus);
    }

    /**
     * Constructor that accepts the name and display text of the 
     * new boolean entry.
     * 
     * @param name The name of the new boolean entry.
     * @param displayText The display text of the new boolean entry.
     */
    public BooleanEntry(String name, String displayText){
        super(name, displayText);
    }

    /**
     * Constructor that accepts the name and display text of the 
     * new boolean entry.
     * 
     * @param name The name of the new boolean entry.
     * @param displayText The display text of the new boolean entry.
     * @param entryStatus The status of the new boolean entry.
     */
    public BooleanEntry(String name, String displayText, EntryStatus entryStatus){
        super(name, displayText, entryStatus);
    }

    public BooleanValue generateValue() {
        BooleanValue bv = new BooleanValue();
        if ( this.units.size() > 0 ){
            bv.setUnit(this.units.get(0));
        }
        return bv;
    }

    public String formatValue(IValue value) throws ModelException {
        BooleanValue bv = (BooleanValue)value;
        return Boolean.toString(bv.getValue());
    }

    public Class getValueClass() {
        return BooleanValue.class;
    }

    public org.psygrid.data.model.dto.BooleanEntryDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        //check for an already existing instance of a dto object for this 
        //boolean entry in the map of references
        org.psygrid.data.model.dto.BooleanEntryDTO dtoBE = null;
        if ( dtoRefs.containsKey(this)){
            dtoBE = (org.psygrid.data.model.dto.BooleanEntryDTO)dtoRefs.get(this);
        }
        if ( null == dtoBE ){
            //an instance of the boolean entry has not already
            //been created, so create it, and add it to the
            //map of references
            dtoBE = new org.psygrid.data.model.dto.BooleanEntryDTO();
            dtoRefs.put(this, dtoBE);
            toDTO(dtoBE, dtoRefs, depth);
        }
        
        return dtoBE;
    }
    
    public void toDTO(org.psygrid.data.model.dto.BooleanEntryDTO dtoBE, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        super.toDTO(dtoBE, dtoRefs, depth);
    }

	@Override
	public void applyStandardCode(DocumentInstance docInst, SectionOccurrence secOcc, SecOccInstance secOccInst, StandardCode stdCode) {
		//do nothing - standard codes cannot be used as the response to a boolean entry
	}

	@Override
	public ElementDTO instantiateDTO() {
		return new org.psygrid.data.model.dto.BooleanEntryDTO();
	}

	public List<QueryOperation> getQueryOperations() {
		return QueryOperation.getOperatorsForBooleanEntry();
	}

	public IEntryStatement createStatement(QueryStatementValue queryStatementValue) {
		// TODO: Currently, there is no BooleanStatement that can be created.
		return null;
	}

}
