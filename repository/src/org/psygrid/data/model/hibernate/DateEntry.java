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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.psygrid.data.model.IDateValue;
import org.psygrid.data.model.IValue;
import org.psygrid.data.model.dto.ElementDTO;
import org.psygrid.data.query.IEntryStatement;
import org.psygrid.data.query.QueryOperation;
import org.psygrid.data.query.QueryStatementValue;
import org.psygrid.data.query.hibernate.DateStatement;

/**
 * Class to represent an entry for collecting date and time data.
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_date_entrys"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class DateEntry extends BasicEntry {
    
    /**
     * The format string of the date entry.
     * <p>
     * The format string should be compatible with the Java API
     * class java.text.SimpleDateFormat.
     */
    private String format;
    
    /**
     * If True it is not allowable for a partial date to be
     * selected as the response to the entry.
     */
    private boolean disablePartialDate;
    
    /**
     * Default no-arg constructor, as required by the Hibernate framwework
     * for all persistable classes.
     */
    public DateEntry(){}
    
    /**
     * Constructor that accepts the the name of the new date entry.
     * 
     * @param name The name of the new date entry.
     */
    public DateEntry(String name){
        super(name);
    }

    /**
     * Constructor that accepts the the name and status of the new
     * date entry.
     * 
     * @param name The name of the new date entry.
     * @param entryStatus The status of the new date entry.
     */
    public DateEntry(String name, EntryStatus entryStatus){
        super(name, entryStatus);
    }

    /**
     * Constructor that accepts the the name and display text of 
     * the new date entry.
     * 
     * @param name The name of the new date entry.
     * @param displayText The display text of the new date entry.
     */
    public DateEntry(String name, String displayText){
        super(name, displayText);
    }

    /**
     * Constructor that accepts the the name, display text and
     * status of the new date entry.
     * 
     * @param name The name of the new date entry.
     * @param displayText The display text of the new date entry.
     * @param entryStatus The status of the new date entry.
     */
    public DateEntry(String name, String displayText, EntryStatus entryStatus){
        super(name, displayText, entryStatus);
    }

    /**
     * Set the format string of the date entry.
     * <p>
     * The format string should be compatible with the Java API
     * class java.text.SimpleDateFormat
     * 
     * @return The format string.
     * @see java.text.SimpleDateFormat
     * @hibernate.property column="c_format"
     */
    public String getFormat() {
        return this.format;
    }

    /**
     * Set the format string of the date entry.
     * <p>
     * The format string should be compatible with the Java API
     * class java.text.SimpleDateFormat
     * 
     * @param format The format string.
     * @see java.text.SimpleDateFormat
     */
    public void setFormat(String format) {
        this.format = format;
    }
    
    /**
     * Get the disable partial date flag.
     * <p>
     * If True it is not allowable for a partial date to be
     * selected as the response to the entry.
	 *
     * @return The disable partial date flag.
     * @hibernate.property column="c_dis_part_dates"
     */
    public boolean isDisablePartialDate() {
		return disablePartialDate;
	}

    /**
     * Set the disable partial date flag.
     * <p>
     * If True it is not allowable for a partial date to be
     * selected as the response to the entry.
	 *
     * @param disablePartialDate The disable partial date flag.
     */
	public void setDisablePartialDate(boolean disablePartialDate) {
		this.disablePartialDate = disablePartialDate;
	}

	public IValue generateValue(String value) throws ModelException {
    	
    	String format = this.format != null ? this.format : "dd-MMM-yyyy"; 
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        Date dateValue = null;
        try{
            dateValue = formatter.parse(value);
        }
        catch(ParseException ex){
            throw new ModelException("Cannot parse the value into a date.",ex);
        }
        return new DateValue(dateValue);
    }

    public IDateValue generateValue() {
        DateValue dv = new DateValue();
        if ( this.units.size() > 0 ){
            dv.setUnit(this.units.get(0));
        }
        return dv;
    }
    
    public String formatValue(IValue value) throws ModelException {
        DateValue dv = (DateValue)value;
        SimpleDateFormat formatter = new SimpleDateFormat(this.format);
        String stringValue = null;
        try{
            stringValue = formatter.format(dv.getValue());
        }
        catch(NullPointerException ex){
            throw new ModelException("Unable to format the data value",ex);
        }
        return stringValue;
    }

    public Class getValueClass() {
        return DateValue.class;
    }
    
    public org.psygrid.data.model.dto.DateEntryDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        //check for an already existing instance of a dto object for this 
        //numeric entry in the map of references
        org.psygrid.data.model.dto.DateEntryDTO dtoDE = null;
        if ( dtoRefs.containsKey(this)){
            dtoDE = (org.psygrid.data.model.dto.DateEntryDTO)dtoRefs.get(this);
        }
        if ( null == dtoDE ){
            //an instance of the date entry has not already
            //been created, so create it, and add it to the map of 
            //references
            dtoDE = new org.psygrid.data.model.dto.DateEntryDTO();
            dtoRefs.put(this, dtoDE);
            toDTO(dtoDE, dtoRefs, depth);
        }
        return dtoDE;
    }
    
    public void toDTO(org.psygrid.data.model.dto.DateEntryDTO dtoDE, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        super.toDTO(dtoDE, dtoRefs, depth);
        if ( depth != RetrieveDepth.REP_SAVE ){
            dtoDE.setFormat(this.format);
            dtoDE.setDisablePartialDate(this.disablePartialDate);
        }
    }

	@Override
	public ElementDTO instantiateDTO() {
		return new org.psygrid.data.model.dto.DateEntryDTO();
	}

	public List<QueryOperation> getQueryOperations() {
		return QueryOperation.getOperatorsForDateEntry();
	}

	public IEntryStatement createStatement(QueryStatementValue queryStatementValue) {
		return new DateStatement(queryStatementValue.getDateValue());
	}
	
	
}
