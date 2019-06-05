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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.psygrid.data.model.IDateValue;
import org.psygrid.data.model.IValue;
import org.psygrid.data.model.hibernate.visitors.ValueVisitor;
import org.psygrid.data.model.hibernate.visitors.VisitorException;

/**
 * Class to represent a value of a response to a date entry.
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_date_values"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class DateValue extends Value implements IDateValue {

    /**
     * The date value
     */
    private Date value;
    
    /**
     * The month component of the date.
     * <p>
     * Valid values are 0 (Jan) to 11 (Dec).
     */
    private Integer month;
    
    /**
     * The year component of the date.
     */
    private Integer year;
    
    /**
     * The old value of the date - used to decide whether a change has
     * been made. Not persisted.
     */
    private Date oldDate;
    
    /**
     * The old value of the month - used to decide whether a change has
     * been made. Not persisted.
     */
    private Integer oldMonth;
    
    /**
     * The old value of the year - used to decide whether a change has
     * been made. Not persisted.
     */
    private Integer oldYear;
    
    private static final String VALUE_PROPERTY = "value";
    
    private static final String MONTH_PROPERTY = "month";
    
    private static final String YEAR_PROPERTY = "year";
    
    /**
     * Default no-arg constructor as required by Hibernate.
     * <p>
     * Scope is protected as all date values must have a value.
     */
    public DateValue(){};
    
    /**
     * Constructor that accepts the value of the date value.
     * 
     * @param value The value.
     */
    public DateValue(Date value){
        this.value = value;
    }
    
    /**
     * @hibernate.property column="c_value" index="date_value_index"
     */
    public Date getValue() {
        return this.value;
    }
    
	@Override
	public Object getTheValue() {
		// TODO Auto-generated method stub
		return value;
	}

    public void setValue(Date value) throws ModelException{
        if ( this.readOnly ){
            throw new ModelException("Cannot set the value - it is read-only");
        }
        Date oldValue = this.value;
        this.value = value;
        propertyChangeSupport.firePropertyChange(VALUE_PROPERTY, oldValue,
                this.value);
    }
    
	/**
     * @hibernate.property column="c_month"
     */
    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        if ( this.readOnly ){
            throw new ModelException("Cannot set month - object is read-only");
        }
        if ( null != month && (month.intValue() < 0 || month.intValue() > 11) ){
        	throw new ModelException("Cannot set month - invalid value ("+month+")");
        }
        Integer oldValue = this.month;
        this.month = month;
        propertyChangeSupport.firePropertyChange(MONTH_PROPERTY, oldValue,
                this.month);
    }

    public String getMonthString() {
        if ( null == month ){
            return null;
        }
        int m = month.intValue();
        //NOTE for our text representation of the month we use
        //Jan=01,...,Dec=12, hence the "m+1" below (as represents month
        //as 0=Jan,...11=Dec.
        String textMonth = Integer.toString(m+1);
        if ( textMonth.length() < 2 ){
            return "0"+textMonth;
        }
        else{
            return textMonth;
        }
    }
    
    /**
     * @hibernate.property column="c_year"
     */
    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        if ( this.readOnly ){
            throw new ModelException("Cannot set year - object is read-only");
        }
        Integer oldValue = this.year;
        this.year = year;
        propertyChangeSupport.firePropertyChange(YEAR_PROPERTY, oldValue,
                this.year);
    }

    @Override
    public boolean valueEquals(Value v) {
        if ( v instanceof DateValue ){
            DateValue dateV = (DateValue)v;
            return this.value == null ? dateV.value == null : 
                this.value.equals(dateV.value);
        }
        
        return false;
    }
    
    @Override
    public void accept(ValueVisitor visitor) throws ModelException {
        try{
            visitor.visit(this);
        }
        catch(VisitorException ex){
            throw new ModelException(ex);
        }
    }

	@Override
	public void recordCurrentState() {
		super.recordCurrentState();
		if ( null != this.value ){
			this.oldDate = new Date(this.value.getTime());
		}
		if ( null != this.month ){
			this.oldMonth = new Integer(this.month.intValue());
		}
		if ( null != this.year ){
			this.oldYear = new Integer(this.year.intValue());
		}
	}

	@Override
    public org.psygrid.data.model.dto.DateValueDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        //check for an already existing instance of a dto object for this 
        //date value in the map of references
        org.psygrid.data.model.dto.DateValueDTO dtoDV = null;
        if ( dtoRefs.containsKey(this)){
            dtoDV = (org.psygrid.data.model.dto.DateValueDTO)dtoRefs.get(this);
        }
        if ( null == dtoDV ){
            //an instance of the date value has not already
            //been created, so create it, and add it to the 
            //map of references
            dtoDV = new org.psygrid.data.model.dto.DateValueDTO();
            dtoRefs.put(this, dtoDV);
            toDTO(dtoDV, dtoRefs, depth);
        }

        return dtoDV;
    }
    
    public void toDTO(org.psygrid.data.model.dto.DateValueDTO dtoDV, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        super.toDTO(dtoDV, dtoRefs, depth);
        dtoDV.setValue(this.value);
        dtoDV.setMonth(this.month);
        dtoDV.setYear(this.year);
    }
    
    public DateValue copy(){
        DateValue v = new DateValue();
        copyProps(v);
        return v;
    }
    
    private void copyProps(DateValue v){
        super.copyProps(v);
        v.setValue(this.value);
        v.setMonth(this.month);
        v.setYear(this.year);
    }

    @Override
    public boolean isNull() {
        return (null == this.value && null == this.month && null == this.year);
    }
    
    @Override
    public String getValueAsString(){
    	String val = super.getValueAsString();
    	if ( null == val ){
            val = getValueAsStringBasic();
    	}
    	return val;
    }

    private String getValueAsStringBasic(){
    	String val = null;
        if ( null != this.value ){
            SimpleDateFormat ddMmmYyyyFormatter = new SimpleDateFormat("dd-MMM-yyyy");
            val = ddMmmYyyyFormatter.format(this.value);
        }
        else if ( null != this.month && null != this.year ){
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.set(Calendar.MONTH, this.month.intValue());
            SimpleDateFormat mmmmFormatter = new SimpleDateFormat("MMMM");
            val = mmmmFormatter.format(cal.getTime())+"-"+this.year.toString();
        }
        else if ( null != this.year ){
            val = this.year.toString();
        }
        return val;
    }
    
    @Override
    public String[] getReportValueAsString(String options) {
        //no options for a date value so argument ignored
        
        String[] result = checkForStandardCode();
        if ( null == result ){
            result = new String[3];
            result[0] = getValueAsString();
            result[1] = TYPE_DATE;
            result[2] = getUnitForReport();
        }
        return result;
    }

	public void importValue(String value, Entry entry) throws ModelException {
		DateEntry dateEntry = (DateEntry)entry;
		Date date = null;
		date = ((DateValue)dateEntry.generateValue(value)).getValue();
		this.setValue(date);
	}

	@Override
	public IValue ddeCopy(BasicEntry primEntry, BasicEntry secEntry) {
        DateValue v = new DateValue();
        super.ddeCopyProps(primEntry, secEntry, v);
        v.setValue(this.value);
        return v;
	}

	@Override
	public boolean isValueChanged() {
		if ( !oldValuesStored ){
			//old values weren't stored i.e. the value was newly
			//created - so nothing needs to be done
			return false;
		}
		if ( super.isValueChanged() ){
			return true;
		}
		if ( null == oldDate ){
			if ( null != value ){
				return true;
			}
		}
		else{
			if ( !oldDate.equals(value)){
				return true;
			}
		}
		if ( null == oldMonth ){
			if ( null != month ){
				return true;
			}
		}
		else{
			if ( !oldMonth.equals(month)){
				return true;
			}
		}
		if ( null == oldYear ){
			return ( oldYear != year );
		}
		else{
			return !oldYear.equals(year);
		}
	}

	@Override
	public void rollback() {
		super.rollback();
		this.value = this.oldDate;
		this.month = this.oldMonth;
		this.year = this.oldYear;
	}

    @Override
    public String getOldValueAsString(){
    	String val = super.getOldValueAsString();
    	if ( null == val ){
            if ( null != this.oldDate ){
                SimpleDateFormat ddMmmYyyyFormatter = new SimpleDateFormat("dd-MMM-yyyy");
                val = ddMmmYyyyFormatter.format(this.oldDate);
            }
            else if ( null != this.oldMonth && null != this.oldYear ){
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.MONTH, this.oldMonth.intValue());
                SimpleDateFormat mmmmFormatter = new SimpleDateFormat("MMMM");
                val = mmmmFormatter.format(cal.getTime())+"-"+this.oldYear.toString();
            }
            else if ( null != this.oldYear ){
                val = this.oldYear.toString();
            }
    	}
    	return val;
    }

	@Override
	public String exportTextValue(boolean authorized) {
		String exportValue = super.exportTextValue(authorized);
		if ( null == exportValue ){
			exportValue = getValueAsStringBasic();
		}
		return exportValue;
	}


}
