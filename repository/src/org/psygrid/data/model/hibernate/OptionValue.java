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

import org.psygrid.data.model.IOptionValue;
import org.psygrid.data.model.IValue;
import org.psygrid.data.model.hibernate.visitors.ValueVisitor;
import org.psygrid.data.model.hibernate.visitors.VisitorException;

/**
 * Class to represent a value of a response to an
 * option entry.
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_option_values"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class OptionValue extends Value implements IOptionValue {

    private static final String VALUE_PROPERTY = "value";
    private static final String TEXT_VALUE_PROPERTY = "textValue";
    /**
     * The option value 
     */
    private Option value;
    
    private Option oldValue;
    
    private Long valueId;
    
    private Long oldValueId;
    
    /**
     * The string value that is completed by the user if the associated
     * option entry is editable, and the "Other" option is selected.
     */
    private String textValue;
    
    private String oldTextValue;
    
    /**
     * Default no-arg constructor as required by Hibernate.
     */
    public OptionValue(){};
    
    /**
     * Constructor that accepts the value of the option value.
     *  
     * @param value The option that is the value of the option 
     * value.
     */
    public OptionValue(Option value){
        this.value = value;
    }
    
    /**
     * @hibernate.many-to-one class="org.psygrid.data.model.hibernate.Option"
     *                        column="c_option_id"
     *                        not-null="false"
     *                        cascade="none"
     *                        fetch="join"
     */						
    public Option getValue(){
        return this.value;
    }
    
	@Override
	public Object getTheValue() {
		// TODO Auto-generated method stub
		return value;
	}

    
    /**
     * Set the value of the option value.
     * 
     * @param value The value.
     */
    public void setValue(Option value) throws ModelException{
        if ( this.readOnly ){
            throw new ModelException("Cannot set the value - it is read-only");
        }
        Option oldValue = this.value;
        this.value = value;
        propertyChangeSupport.firePropertyChange(VALUE_PROPERTY, oldValue,
                this.value);
    }

    public Long getValueId() {
        return valueId;
    }

    public void setValueId(Long valueId) {
        this.valueId = valueId;
    }

    /**
     * @hibernate.property column="c_text_value"
     */
    public String getTextValue() {
        return textValue;
    }

    public void setTextValue(String textValue) {
        if ( this.readOnly ){
            throw new ModelException("Cannot set text value - object is read-only");
        }
        String oldTextValue = this.textValue;
        this.textValue = textValue;
        propertyChangeSupport.firePropertyChange(TEXT_VALUE_PROPERTY, oldTextValue,
                this.textValue);
    }

    @Override
    public boolean valueEquals(Value v) {
        if ( v instanceof OptionValue ){
            OptionValue optV = (OptionValue)v;
            return this.value == null ? optV.value == null : 
                this.value.equals(optV.value);
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
    public org.psygrid.data.model.dto.OptionValueDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        //check for an already existing instance of a dto object for this 
        //option value in the map of references
        org.psygrid.data.model.dto.OptionValueDTO dtoOV = null;
        if ( dtoRefs.containsKey(this)){
            dtoOV = (org.psygrid.data.model.dto.OptionValueDTO)dtoRefs.get(this);
        }
        if ( null == dtoOV ){
            //an instance of the dependent entry has not already
            //been created, so create it, and add it to the map 
            //of references
            dtoOV = new org.psygrid.data.model.dto.OptionValueDTO();
            dtoRefs.put(this, dtoOV);
            toDTO(dtoOV, dtoRefs, depth);
        }

        return dtoOV;
    }
    
    public void toDTO(org.psygrid.data.model.dto.OptionValueDTO dtoOV, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        super.toDTO(dtoOV, dtoRefs, depth);
        dtoOV.setTextValue(this.textValue);
        if ( null != this.value ){
            dtoOV.setValueId(this.value.getId());
        }
        else {
        	dtoOV.setValueId(this.valueId);
        }
    }
 
    /**
     * Attach a detached option value to its dataset objects.
     * 
     * @param e The entry that the value's response is attached to.
     * @throws ModelException if an object in the value's graph
     * cannot be attached.
     */
    @Override
    public void attach(BasicEntry e) throws ModelException{
        super.attach(e);
        OptionEntry oe = (OptionEntry)e;
        boolean attached = false;
        Long optionId = null;
        if ( null != this.valueId ){
            optionId = this.valueId;
        }
        else if ( null != this.value ) {
            //preserve backwards compatability with records detached
            //prior to the introduction of OptionValue.valueId
            optionId = this.value.getId();
        }
        Long oldOptionId = null;
        if ( null != this.oldValueId ){
        	oldOptionId = this.oldValueId;
        }
        else if ( null != this.oldValue ) {
            //preserve backwards compatability with records detached
            //prior to the introduction of OptionValue.valueId
        	oldOptionId = this.oldValue.getId();
        }
        if ( null != optionId ){
	        for (Option o:oe.getOptions()){
	            if ( o.getId().equals(optionId)){
	                this.value = o;
                    this.valueId = null;
	                attached = true;
	                break;
	            }
	        }
	        if ( !attached ){
	            throw new ModelException("Failed to attach option value id="+this.getId()+" - no option exists with id="+optionId);
	        }
        }
        
        attached = false;
        if ( null != oldOptionId ){
	        for (Option o:oe.getOptions()){
	            if ( o.getId().equals(oldOptionId)){
	                this.oldValue = o;
                    this.oldValueId = null;
	                attached = true;
	                break;
	            }
	        }
	        if ( !attached ){
	            throw new ModelException("Failed to attach old option value id="+this.getId()+" - no option exists with id="+oldOptionId);
	        }
        }
    }
    
    public void detach(){
        super.detach();
        if ( null != this.value ){
            this.valueId = this.value.getId();
            this.value = null;
        }
        if ( null != this.oldValue ){
            this.oldValueId = this.oldValue.getId();
            this.oldValue = null;
        }
    }
        
    public OptionValue copy(){
        OptionValue v = new OptionValue();
        copyProps(v);
        return v;
    }
    
    private void copyProps(OptionValue v){
        super.copyProps(v);
        v.setValue(this.value);
        v.setTextValue(this.textValue);
    }

    @Override
    public boolean isNull() {
        return (null == this.value);
    }
    
    public String getValueAsString(){
    	String val = super.getValueAsString();
    	if ( null == val ){
            if ( null != this.value ){
                StringBuilder builder = new StringBuilder();
                if ( null != this.value.getCode() ){
                    builder.append(this.value.getCode()).append(". ");
                }
                if ( null != this.value.getDisplayText() ){
                    builder.append(this.value.getDisplayText());
                }
        		if ( null != unit ){
        			builder.append(" ");
        			builder.append(unit.getAbbreviation());
        		}
                val = builder.toString();
            }
    	}
    	return val;
    }

    @Override
    public String[] getReportValueAsString(String options) throws ModelException {
        String[] result = checkForStandardCode();
        if ( null == result ){
            result = new String[3];
            if ( OPTION_CODE.equals(options) ){
                result[1] = TYPE_INTEGER;
                if ( null == this.value ){
                    result[0] = null;
                }
                else{
                    if ( null != this.value.getCode() ){
                        result[0] = this.value.getCode().toString();
                    }
                    else{
                        throw new ModelException("Cannot use code of this option value in report - no code defined (option id="+this.value.getId()+")");
                    }
                }
            }
            else if ( OPTION_TEXT.equals(options) ){
                result[1] = TYPE_STRING;
                if ( null == this.value ){
                    result[0] = null;
                }
                else{
                    StringBuilder builder = new StringBuilder();
                    builder.append(this.value.getDisplayText());
                    if ( null != this.textValue ){
                        builder.append(" - ");
                        builder.append(this.textValue);
                    }
                    result[0] = builder.toString();
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

	@Override
	public String getValueForExport() {
        if ( null == this.value ){
            return null;
        }
        return this.value.getDisplayText();
	}

	@Override
	public String exportTextValue(boolean authorized) {
		String exportValue = super.exportTextValue(authorized);
		if ( null == exportValue && null != this.value ){
			exportValue = this.value.getDisplayText();
		}
		return exportValue;
	}

	@Override
	public String exportCodeValue(boolean authorized) {
		String codeValue = super.exportCodeValue(authorized);
		if ( null == codeValue && null != this.value && null != this.value.getCode() ){
			codeValue = this.value.getCode().toString();
		}
		return codeValue;
	}

	@Override
	public String exportExtraValue(boolean authorized) {
		if ( !authorized ){
			return UNAUTHORIZED_VALUE;
		}
		return this.textValue;
	}

	public void importValue(String value, Entry entry) throws ModelException {
		OptionEntry theEntry = (OptionEntry) entry;
		IOptionValue optionValue = null;
		optionValue = (IOptionValue) theEntry.generateValue(value);
		
		if(optionValue != null){
			this.setValue(optionValue.getValue());
		}
	}

	@Override
	public IValue ddeCopy(BasicEntry primEntry, BasicEntry secEntry) {
		OptionValue v = new OptionValue();
		super.ddeCopyProps(primEntry, secEntry, v);
		//to copy an option entry for dual data entry we need to
		//find the option at the same index in the secondary dataset
		//as the option referenced here
		if ( null != this.getValue() ){
			OptionEntry primOptEnt = this.getValue().getEntry();
			OptionEntry secOptEnt = (OptionEntry)secEntry;
			int index = -1;
			for ( int i=0, c=primOptEnt.numOptions(); i<c; i++ ){
				if ( primOptEnt.getOption(i).equals(this.getValue())){
					index = i;
					break;
				}
			}
			v.setValue(secOptEnt.getOption(index));
		}
		else{
			v.setValue(null);
		}
		v.setTextValue(this.textValue);
		return v;
	}

	@Override
	public Double getValueForStats() {
		if ( null == value ){
			return null;
		}
		if ( null == value.getCode() ){
			return null;
		}
		return new Double(value.getCode().doubleValue());
	}

	@Override
	public void recordCurrentState() {
		super.recordCurrentState();
		if ( null != this.value ){
			this.oldValue = this.value;
		}
		if ( null != this.textValue ){
			this.oldTextValue = new String(this.textValue);
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
			if ( null != value ){
				return true;
			}
		}
		else{
			if ( !oldValue.equals(value)){
				return true;
			}
		}
		if ( null == oldTextValue ){
			return ( null != textValue );
		}
		else{
			return !oldTextValue.equals(textValue);
		}
	}

	@Override
	public void rollback() {
		super.rollback();
		this.value = this.oldValue;
		this.textValue = this.oldTextValue;
	}

    public String getOldValueAsString(){
    	String val = super.getOldValueAsString();
    	if ( null == val ){
            if ( null != this.oldValue ){
                StringBuilder builder = new StringBuilder();
                if ( null != this.oldValue.getCode() ){
                    builder.append(this.oldValue.getCode()).append(". ");
                }
                if ( null != this.oldValue.getDisplayText() ){
                    builder.append(this.oldValue.getDisplayText());
                }
        		if ( null != oldUnit ){
        			builder.append(" ");
        			builder.append(oldUnit.getAbbreviation());
        		}
                val = builder.toString();
            }
    	}
    	return val;
    }


}
