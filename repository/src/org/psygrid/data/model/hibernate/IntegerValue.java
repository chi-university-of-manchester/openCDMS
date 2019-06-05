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

import org.psygrid.data.model.IIntegerValue;
import org.psygrid.data.model.IValue;
import org.psygrid.data.model.hibernate.visitors.ValueVisitor;
import org.psygrid.data.model.hibernate.visitors.VisitorException;

/**
 * Class to represent a value of a response to an integer entry.
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_integer_values"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class IntegerValue extends Value implements IIntegerValue {

    /**
     * The integer value
     */
    private Integer value;
    
    private Integer oldValue;
    
    private static final String VALUE_PROPERTY = "value";
    
    /**
     * Default no-arg constructor as required by Hibernate.
     */
    public IntegerValue(){};
    
    /**
     * Constructor that accepts the value of the integer value.
     * 
     * @param value The value.
     */
    public IntegerValue(Integer value){
        this.value = value;
    }
    
    /**
     * @hibernate.property column="c_value" index="integer_value_index"
     */
    public Integer getValue() {
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
    public void setValue(Integer value) throws ModelException{
        if ( this.readOnly ){
            throw new ModelException("Cannot set the value - it is read-only");
        }
        Integer oldValue = this.value;
        this.value = value;
        propertyChangeSupport.firePropertyChange(VALUE_PROPERTY, oldValue,
                this.value);
    }
    
    @Override
    public boolean valueEquals(Value v) {
        if ( v instanceof IntegerValue ){
            IntegerValue numV = (IntegerValue)v;
            
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
			this.oldValue = new Integer(this.value.intValue());
		}
	}

	@Override
    public org.psygrid.data.model.dto.IntegerValueDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        //check for an already existing instance of a dto object for this 
        //numeric value in the map of references
        org.psygrid.data.model.dto.IntegerValueDTO dtoIV = null;
        if ( dtoRefs.containsKey(this)){
            dtoIV = (org.psygrid.data.model.dto.IntegerValueDTO)dtoRefs.get(this);
        }
        else{
            //an instance of the numeric value has not already
            //been created, so create it, and add it to the map
            //of references
            dtoIV = new org.psygrid.data.model.dto.IntegerValueDTO();
            dtoRefs.put(this, dtoIV);
            toDTO(dtoIV, dtoRefs, depth);
        }

        return dtoIV;
    }
    
    public void toDTO(org.psygrid.data.model.dto.IntegerValueDTO dtoIV, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        super.toDTO(dtoIV, dtoRefs, depth);
        dtoIV.setValue(this.value);
    }
    
    public IntegerValue copy(){
        IntegerValue v = new IntegerValue();
        copyProps(v);
        return v;
    }
    
    private void copyProps(IntegerValue v){
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
        //no options for an integer value so argument ignored
        String[] result = checkForStandardCode();
        if ( null == result ){
            result = new String[3];
            result[0] = getValueAsString();
            result[1] = TYPE_INTEGER;
            result[2] = getUnitForReport();
        }
        return result;
    }

	public void importValue(String value, Entry entry) throws ModelException {

		int integerValue = 0;
		try{
			integerValue = Integer.parseInt(value, 10);
		} catch(NumberFormatException ex){
			throw new ModelException("IntegerValue could not import '" + value + "' - could not be parsed as an integer");
		}
		this.setValue(integerValue);
	}

	@Override
	public IValue ddeCopy(BasicEntry primEntry, BasicEntry secEntry) {
        IntegerValue v = new IntegerValue();
        super.ddeCopyProps(primEntry, secEntry, v);
        v.setValue(this.value);
        return v;
	}

	@Override
	public Double getValueForStats() {
		if ( null == this.value ){
			return null;
		}
		return new Double(this.value.doubleValue());
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
			return ( null != value );
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
