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

import org.psygrid.data.model.ITextValue;
import org.psygrid.data.model.IValue;
import org.psygrid.data.model.dto.ElementDTO;
import org.psygrid.data.query.IEntryStatement;
import org.psygrid.data.query.QueryOperation;
import org.psygrid.data.query.QueryStatementValue;
import org.psygrid.data.query.hibernate.TextStatement;

/**
 * Class to represent an entry for collecting textual data.
 * 
 * @author Rob Harper
 * 
 * @hibernate.joined-subclass table="t_text_entrys"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class TextEntry extends BasicEntry {

	/**
	 * The maximum size (in number of characters) of the textual data that
	 * can be collected by the text entry.
	 */
	private int size;

	/**
	 * Default no-arg constructor, as required by the Hibernate framwework
	 * for all persistable classes.
	 */
	public TextEntry(){};
	
	/**
	 * Constructor that accepts the the name of the new 
     * text entry.
	 * 
	 * @param name The name of the new text entry.
	 */
	public TextEntry(String name){
		super(name);
    }
	
    /**
     * Constructor that accepts the the name and status of the 
     * new text entry.
     * 
     * @param name The name of the new text entry.
     * @param entryStatus The status of the new text entry.
     */
    public TextEntry(String name, EntryStatus entryStatus){
        super(name, entryStatus);
    }
    
    /**
     * Constructor that accepts the the name and display text of 
     * the new text entry.
     * 
     * @param name The name of the new text entry.
     * @param displayText The display text of the new text entry.
     */
    public TextEntry(String name, String displayText){
        super(name, displayText);
    }
    
    /**
     * Constructor that accepts the the name, display text and 
     * status of the new text entry.
     * 
     * @param name The name of the new text entry.
     * @param entryStatus The status of the new text entry.
     */
    public TextEntry(String name, String displayText, EntryStatus entryStatus){
        super(name, displayText, entryStatus);
    }
    
	/**
     * Get the maximum size (in number of characters) of the textual data that
     * can be collected by the TextEntry
     * 
     * @return The maximum size
	 * @hibernate.property column="c_text_size"
	 */
	public int getSize() {
		return size;
	}

    /**
     * Set the maximum size (in number of characters) of the textual data that
     * can be collected by the TextEntry
     * 
     * @param size The maximum size
     */
	public void setSize(int size) {
		this.size = size;
	}

    public ITextValue generateValue(){
        TextValue tv = new TextValue();
        if ( this.units.size() > 0 ){
            tv.setUnit(this.units.get(0));
        }
        return tv;
    }

    public String formatValue(IValue value) throws ModelException {
        TextValue tv = (TextValue)value;
        return tv.getValue();
    }
    
    public Class getValueClass() {
        return TextValue.class;
    }

    public org.psygrid.data.model.dto.TextEntryDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        //check for an already existing instance of a dto object for this 
        //text entry in the map of references
        org.psygrid.data.model.dto.TextEntryDTO dtoTE = null;
        if ( dtoRefs.containsKey(this)){
            dtoTE = (org.psygrid.data.model.dto.TextEntryDTO)dtoRefs.get(this);
        }
        if ( null == dtoTE ){
            //an instance of the text entry has not already
            //been created, so create it, and add it to the map 
            //of references
            dtoTE = new org.psygrid.data.model.dto.TextEntryDTO();
            dtoRefs.put(this, dtoTE);
            toDTO(dtoTE, dtoRefs, depth);
        }

        return dtoTE;
    }
    
    public void toDTO(org.psygrid.data.model.dto.TextEntryDTO dtoTE, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        super.toDTO(dtoTE, dtoRefs, depth);
        if ( depth != RetrieveDepth.REP_SAVE ){
            dtoTE.setSize(this.size);
        }
    }

	@Override
	public ElementDTO instantiateDTO() {
		return new org.psygrid.data.model.dto.TextEntryDTO();
	}
	
	public List<QueryOperation> getQueryOperations() {
		return QueryOperation.getOperatorsForTextEntry();
	}

	public IEntryStatement createStatement(QueryStatementValue queryStatementValue) {
		return new TextStatement(queryStatementValue.getTextValue());
	}

}
