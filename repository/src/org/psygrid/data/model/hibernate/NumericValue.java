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

import org.psygrid.data.model.INumericValue;
import org.psygrid.data.model.IValue;
import org.psygrid.data.model.hibernate.visitors.ValueVisitor;
import org.psygrid.data.model.hibernate.visitors.VisitorException;

/**
 * Class to represent a value of a response to a numeric entry.
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_numeric_values"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class NumericValue extends Value implements INumericValue {

    /**
     * The numeric value
     */
    private Double value;
    
    private static final String VALUE_PROPERTY = "value";
    
    private Double oldValue;
    
    /**
     * Default no-arg constructor as required by Hibernate.
     */
    public NumericValue(){};
    
    /**
     * Constructor that accepts the value of the numeric value.
     * 
     * @param value The value.
     */
    public NumericValue(Double value){
        this.value = value;
    }
    
    /**
     * @hibernate.property column="c_value" index="numeric_value_index"
     */
    public Double getValue() {
        return this.value;
    }
    
	@Override
	public Object getTheValue() {
		// TODO Auto-generated method stub
		return value;
	}


    /**
     * Set the value of the numeric value.
     * 
     * @param value The value.
     */
    public void setValue(Double value) throws ModelException{
        if ( this.readOnly ){
            throw new ModelException("Cannot set the value - it is read-only");
        }
        Double oldValue = this.value;
        this.value = value;
        propertyChangeSupport.firePropertyChange(VALUE_PROPERTY, oldValue,
                this.value);
    }
    
    @Override
    public boolean valueEquals(Value v) {
        if ( v instanceof NumericValue ){
            NumericValue numV = (NumericValue)v;
            
            return this.value == null ? numV.value == null :
                    (this.value.equals(numV.value));
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
			this.oldValue = new Double(this.value.doubleValue());
		}
	}

	@Override
    public org.psygrid.data.model.dto.NumericValueDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        //check for an already existing instance of a dto object for this 
        //numeric value in the map of references
        org.psygrid.data.model.dto.NumericValueDTO dtoNV = null;
        if ( dtoRefs.containsKey(this)){
            dtoNV = (org.psygrid.data.model.dto.NumericValueDTO)dtoRefs.get(this);
        }
        if ( null == dtoNV ){
            //an instance of the numeric value has not already
            //been created, so create it, and add it to the map
            //of references
            dtoNV = new org.psygrid.data.model.dto.NumericValueDTO();
            dtoRefs.put(this, dtoNV);
            toDTO(dtoNV, dtoRefs, depth);
        }

        return dtoNV;
    }
    
    public void toDTO(org.psygrid.data.model.dto.NumericValueDTO dtoNV, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        super.toDTO(dtoNV, dtoRefs, depth);
        dtoNV.setValue(this.value);
    }
    
    public NumericValue copy(){
        NumericValue v = new NumericValue();
        copyProps(v);
        return v;
    }
    
    private void copyProps(NumericValue v){
        super.copyProps(v);
        v.setValue(this.value);
    }

    @Override
    public boolean isNull() {
        return (null == this.value);
    }
    
    @Override
    public String getValueAsString(){
    	String val = super.getValueAsString();
    	if ( null == val ){
            if ( null != this.value ){
	    		val = this.value.toString();
	    		if ( null != unit ){
	    			val = val + " " + unit.getAbbreviation();
	    		}
            }
    	}
    	return val;
    }

    @Override
    public String[] getReportValueAsString(String options) {
        //no options for a numeric value so argument ignored
        String[] result = checkForStandardCode();
        if ( null == result ){
            result = new String[3];
            result[0] = getValueAsString();
            result[1] = TYPE_DOUBLE;
            result[2] = getUnitForReport();
        }
        return result;
    }

	public void importValue(String value, Entry entry) throws ModelException {
		Double numericValue;
		try{
			numericValue = Double.parseDouble(value);
		} catch(NumberFormatException ex){
			throw new ModelException("NumericValue could not import '" + value + "' - could not be parsed as a type double.");
		}
		this.setValue(numericValue);
	}

	@Override
	public IValue ddeCopy(BasicEntry primEntry, BasicEntry secEntry) {
        NumericValue v = new NumericValue();
        super.ddeCopyProps(primEntry, secEntry, v);
        v.setValue(this.value);
        return v;
	}

	@Override
	public Double getValueForStats() {
		return value;
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
		if ( null == oldValue ){
			return (null != value);
		}
		else{
			return !oldValue.equals(value);
		}
	}

	@Override
	public void rollback() {
		super.rollback();
		this.value = this.oldValue;
	}

    @Override
    public String getOldValueAsString(){
    	String val = super.getOldValueAsString();
    	if ( null == val ){
            if ( null != this.oldValue ){
	    		val = this.oldValue.toString();
	    		if ( null != oldUnit ){
	    			val = val + " " + oldUnit.getAbbreviation();
	    		}
            }
    	}
    	return val;
    }

	@Override
	public String exportTextValue(boolean authorized) {
		String exportValue = super.exportTextValue(authorized);
		if ( null == exportValue && null != value ){
			exportValue = value.toString();
		}
		return exportValue;
	}

}
