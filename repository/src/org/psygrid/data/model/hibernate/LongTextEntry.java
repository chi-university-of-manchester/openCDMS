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

import org.psygrid.data.model.ILongTextValue;
import org.psygrid.data.model.IValue;
import org.psygrid.data.model.dto.ElementDTO;
import org.psygrid.data.query.IEntryStatement;
import org.psygrid.data.query.QueryOperation;
import org.psygrid.data.query.QueryStatementValue;
import org.psygrid.data.query.hibernate.LongTextStatement;

/**
 * Class to represent an entry for collecting long textual data.
 * 
 * @author Rob Harper
 * 
 * @hibernate.joined-subclass table="t_long_text_entrys"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class LongTextEntry extends BasicEntry {

    /**
	 * 
	 */
	private static final long serialVersionUID = 5083641111842669225L;

	/**
     * Default no-arg constructor, as required by the Hibernate framwework
     * for all persistable classes.
     */
    public LongTextEntry(){};
    
    /**
     * Constructor that accepts the the name of the new 
     * long text entry.
     * 
     * @param name The name of the new long text entry.
     */
    public LongTextEntry(String name){
        super(name);
    }
    
    /**
     * Constructor that accepts the the name and status of the 
     * new long text entry.
     * 
     * @param name The name of the new long text entry.
     * @param entryStatus The status of the new long text entry.
     */
    public LongTextEntry(String name, EntryStatus entryStatus){
        super(name, entryStatus);
    }
    
    /**
     * Constructor that accepts the the name and display text of 
     * the new long text entry.
     * 
     * @param name The name of the new long text entry.
     * @param displayText The display text of the new long text entry.
     */
    public LongTextEntry(String name, String displayText){
        super(name, displayText);
    }
    
    /**
     * Constructor that accepts the the name, display text and 
     * status of the new long text entry.
     * 
     * @param name The name of the new long text entry.
     * @param entryStatus The status of the new long text entry.
     */
    public LongTextEntry(String name, String displayText, EntryStatus entryStatus){
        super(name, displayText, entryStatus);
    }
    
    public ILongTextValue generateValue() {
        LongTextValue ltv = new LongTextValue();
        if ( this.units.size() > 0 ){
            ltv.setUnit(this.units.get(0));
        }
        return ltv;
    }

    public String formatValue(IValue value) throws ModelException {
        LongTextValue tv = (LongTextValue)value;
        return tv.getValue();
    }

    public Class getValueClass() {
        return LongTextValue.class;
    }

    public org.psygrid.data.model.dto.LongTextEntryDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        //check for an already existing instance of a dto object for this 
        //long text entry in the map of references
        org.psygrid.data.model.dto.LongTextEntryDTO dtoLTE = null;
        if ( dtoRefs.containsKey(this)){
            dtoLTE = (org.psygrid.data.model.dto.LongTextEntryDTO)dtoRefs.get(this);
        }
        if ( null == dtoLTE ){
            //an instance of the long text entry has not already
            //been created, so create it, and add it to the map 
            //of references
            dtoLTE = new org.psygrid.data.model.dto.LongTextEntryDTO();
            dtoRefs.put(this, dtoLTE);
            toDTO(dtoLTE, dtoRefs, depth);
        }
        
        return dtoLTE;
    }
    
    public void toDTO(org.psygrid.data.model.dto.LongTextEntryDTO dtoLTE, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        super.toDTO(dtoLTE, dtoRefs, depth);
    }

	@Override
	public ElementDTO instantiateDTO() {
		return new org.psygrid.data.model.dto.LongTextEntryDTO();
	}
	
	public List<QueryOperation> getQueryOperations() {
		return QueryOperation.getOperatorsForTextEntry();
	}

	public IEntryStatement createStatement(QueryStatementValue queryStatementValue) {
		return new LongTextStatement(queryStatementValue.getTextValue());
	}

}
