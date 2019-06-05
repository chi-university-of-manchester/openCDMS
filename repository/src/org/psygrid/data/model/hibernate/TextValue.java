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

import org.psygrid.data.model.ITextValue;
import org.psygrid.data.model.IValue;
import org.psygrid.data.model.hibernate.visitors.ValueVisitor;
import org.psygrid.data.model.hibernate.visitors.VisitorException;

/**
 * Class to represent a value of a response to a TextEntry.
 * 
 * @author Rob Harper
 * 
 * @hibernate.joined-subclass table="t_text_values"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class TextValue extends Value implements ITextValue {

	/**
	 * The textual value of the response
	 */
	private String value;
    
	private String oldValue;
	
    private static final String VALUE_PROPERTY = "value";
	
	/**
	 * Default no-arg constructor, as required by the Hibernate framework
	 * for all persistable classes.
	 */
	public TextValue(){};
	
	/**
	 * Constructor that accepts the value of the TextValue
	 * 
	 * @param value The value of the TextValue
	 */
	public TextValue(String value) throws ModelException{
		setValue(value);
	}
	
	/**
	 * See the file repository\sql\mysql\mysql_indexes.sql for 
	 * the sql to create the index for this property.
	 * 
	 * @hibernate.property column="c_value" length="256"
	 */
	public String getValue() {
		return value;
	}
	
	@Override
	public Object getTheValue() {
		// TODO Auto-generated method stub
		return value;
	}
	
	public void setValue(String value) throws ModelException {
        if ( this.readOnly ){
            throw new ModelException("Cannot set the value - it is read-only");
        }
        if ( null != value ){
            if ( value.length() > MAX_CHARS ){
                throw new ModelException("Cannot set the value with a string of "+value.length()+" characters: "+MAX_CHARS+" is the maximum allowed.");
            }
        }
        String oldValue = this.value;
		this.value = value;
        propertyChangeSupport.firePropertyChange(VALUE_PROPERTY, oldValue, this.value);
	}

    @Override
    public boolean valueEquals(Value v) {
        if ( v instanceof TextValue ){
            TextValue textV = (TextValue)v;
            return this.value == null ? textV.value == null : 
                (this.value.equals(textV.value));
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
    public org.psygrid.data.model.dto.TextValueDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        //check for an already existing instance of a dto object for this 
        //text value in the map of references
        org.psygrid.data.model.dto.TextValueDTO dtoTV = null;
        if ( dtoRefs.containsKey(this)){
            dtoTV = (org.psygrid.data.model.dto.TextValueDTO)dtoRefs.get(this);
        }
        if ( null == dtoTV ){
            //an instance of the text value has not already
            //been created, so create it, and add it to the map
            //of references
            dtoTV = new org.psygrid.data.model.dto.TextValueDTO();
            dtoRefs.put(this, dtoTV);
            toDTO(dtoTV, dtoRefs, depth);
        }

        return dtoTV;
    }
    
    public void toDTO(org.psygrid.data.model.dto.TextValueDTO dtoTV, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        super.toDTO(dtoTV, dtoRefs, depth);
        dtoTV.setValue(this.value);
    }
    
    public TextValue copy(){
        TextValue v = new TextValue();
        copyProps(v);
        return v;
    }
    
    private void copyProps(TextValue v){
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
    		val = this.value;
    		if ( null != unit ){
    			val = val + " " + unit.getAbbreviation();
    		}
    	}
    	return val;
    }

    @Override
    public String[] getReportValueAsString(String options) {
        //no options for a text value so argument ignored
        String[] result = checkForStandardCode();
        if ( null == result ){
            result = new String[3];
            result[0] = getValueAsString();
            result[1] = TYPE_STRING;
            result[2] = getUnitForReport();
        }
        return result;
    }

	public void importValue(String value, Entry entry) throws ModelException {
		this.setValue(value);
	}

	@Override
	public IValue ddeCopy(BasicEntry primEntry, BasicEntry secEntry) {
        TextValue v = new TextValue();
        super.ddeCopyProps(primEntry, secEntry, v);
        v.setValue(this.value);
        return v;
	}

	@Override
	public void recordCurrentState() {
		super.recordCurrentState();
		if ( null != this.value ){
			this.oldValue = new String(this.value);
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
    		val = this.oldValue;
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
			exportValue = this.value;
		}
		return exportValue;
	}


}