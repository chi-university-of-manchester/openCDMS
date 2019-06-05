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

import org.psygrid.data.model.IValue;
import org.psygrid.data.model.hibernate.visitors.ValueVisitor;
import org.psygrid.data.model.hibernate.visitors.VisitorException;

/**
 * Class to represent a value of a response to a
 * boolean entry.
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_boolean_values"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class BooleanValue extends Value {

    public static final String OPTION_TRUEFALSE = "TrueFalse";
    public static final String OPTION_YESNO = "YesNo";
    public static final String OPTION_ONEZERO = "OneZero";
	
    private static final String VALUE_PROPERTY = "value";
    
    /**
     * The boolean value
     */
    private boolean value;
    
    /**
     * The old value - used to decide whether a change has
     * been made. Not persisted.
     */
    private boolean oldValue;
    
    /**
     * Default no-arg constructor as required by Hibernate.
     */
    public BooleanValue(){};
    
    /**
     * Constructor that accepts the value of the boolean value.
     *  
     * @param value The boolean that is the value of the boolean 
     * value.
     */
    public BooleanValue(boolean value){
        this.value = value;
    }
    
    /**
     * Get the boolean value of the response.
     * 
     * @return The boolean value.
     * @hibernate.property column="c_value"
     */
    public boolean getValue() {
        return this.value;
    }
    
	@Override
	public Object getTheValue() {
		// TODO Auto-generated method stub
		return new Boolean(value);
	}

    /**
     * Set the boolean value of the response.
     * 
     * @param value The boolean value.
     * @throws ModelException if the value is read-only
     */
    public void setValue(boolean value) throws ModelException{
        if ( this.readOnly ){
            throw new ModelException("Cannot set the value - it is read-only");
        }
        boolean oldValue = this.value;
        this.value = value;
        propertyChangeSupport.firePropertyChange(VALUE_PROPERTY, oldValue,
                this.value);
    }
    
    @Override
    public boolean valueEquals(Value v) {
        if ( v instanceof BooleanValue ){
            BooleanValue boolV = (BooleanValue)v;
            return (this.value == boolV.value);
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
		this.oldValue = this.value;
	}

	@Override
    public org.psygrid.data.model.dto.BooleanValueDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        //check for an already existing instance of a dto object for this 
        //boolean value in the map of references
        org.psygrid.data.model.dto.BooleanValueDTO dtoBV = null;
        if ( dtoRefs.containsKey(this)){
            dtoBV = (org.psygrid.data.model.dto.BooleanValueDTO)dtoRefs.get(this);
        }
        if ( null == dtoBV ){
            //an instance of the boolean value has not already
            //been created, so create it, and add it to the map of references
            dtoBV = new org.psygrid.data.model.dto.BooleanValueDTO();
            dtoRefs.put(this, dtoBV);
            toDTO(dtoBV, dtoRefs, depth);
        }

        return dtoBV;
    }
    
    public void toDTO(org.psygrid.data.model.dto.BooleanValueDTO dtoBV, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        super.toDTO(dtoBV, dtoRefs, depth);
        dtoBV.setValue(this.value);
    }

    /**
     * Return a copy of the boolean value object.
     * 
     * @return A copy of the boolean value object.
     */
    public BooleanValue copy(){
        BooleanValue v = new BooleanValue();
        copyProps(v);
        return v;
    }
    
    private void copyProps(BooleanValue v){
        super.copyProps(v);
        v.setValue(this.value);
    }

    @Override
	public IValue ddeCopy(BasicEntry primEntry, BasicEntry secEntry) {
        BooleanValue bv = new BooleanValue();
        super.ddeCopyProps(primEntry, secEntry, bv);
        bv.setValue(this.value);
        return bv;
	}
    
	@Override
    public boolean isNull() {
        //primitive boolean value can never be null!
        return false;
    }
    
    @Override
    public String getValueAsString(){
    	String val = super.getValueAsString();
    	if ( null == val ){
    		val = Boolean.toString(this.value);;
    		if ( null != unit ){
    			val = val + " " + unit.getAbbreviation();
    		}
    	}
    	return val;
    }

    @Override
    public String[] getReportValueAsString(String options) {
        String[] result = checkForStandardCode();
        if ( null == result ){
            result = new String[3];
            if ( OPTION_YESNO.equals(options)){
                result[1] = TYPE_STRING;
                if ( this.value ){
                    result[0] = "yes";
                }
                else{
                    result[0]=  "no";
                }
            }
            else if ( OPTION_ONEZERO.equals(options) ){
                result[1] = TYPE_INTEGER;
                if ( this.value ){
                    result[0] =  "1";
                }
                else{
                    result[0] =  "0";
                }
            }
            else{
                result[1] = TYPE_STRING;
                result[0] = getValueAsString();
            }
            result[2] = getUnitForReport();
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * @see org.psygrid.data.model.IValue#importValue(java.lang.String, org.psygrid.data.model.IEntry)
     * This can handle OPTION_YESNO, OPTION_ONEZERO, or OPTION_TRUEFALSE strings
     * Returns true if the value could be imported
     * returns false if the value does not conform to one of the before-mentioned input types
     */
	public void importValue(String value, Entry entry) throws ModelException {
		//Find out if value conforms to any of the input types
		boolean inputValueRecognized = false;
		boolean inputValue = true;
		
		if(value.equalsIgnoreCase("Yes") || value.equalsIgnoreCase("True") || value.equalsIgnoreCase("1")){
			inputValueRecognized = true;
			inputValue = true;
		}
		else if (value.equalsIgnoreCase("No") || value.equalsIgnoreCase("False") || value.equalsIgnoreCase("0")){
			inputValueRecognized = true;
			inputValue = false;
		}
		
		if(inputValueRecognized){
			this.setValue(inputValue);
		}
		else{
			throw new ModelException("The value " + value + " is not a recognized format by the BooleanValue class.");	
		}
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
		return ( oldValue != value );
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
    		val = Boolean.toString(this.oldValue);;
    		if ( null != oldUnit ){
    			val = val + " " + oldUnit.getAbbreviation();
    		}
    	}
    	return val;
    }

	@Override
	public String exportTextValue(boolean authorized) {
		String exportValue = super.exportTextValue(authorized);
		if ( null == exportValue ){
			exportValue = Boolean.toString(this.value);
		}
		return exportValue;
	}


}
