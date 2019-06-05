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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.psygrid.data.model.IValue;

/**
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_basic_responses"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class BasicResponse extends Response {

    private static final String VALUE_PROPERTY = "value";

    /**
     * Collection of value objects for the response that represent
     * all values of the response over its lifetime.
     */
    protected Value theValue = null;

    protected List<Value> oldValues = new ArrayList<Value>();
    
    /**
     * Retrieve the collection of value objects for the response 
     * that represents all values of the response over its 
     * lifetime.
     * 
     * @return The collection of value objects.
     * 
     * @hibernate.many-to-one class="org.psygrid.data.model.hibernate.Value"
     *                        column="c_value_id"
     *                        not-null="false"
     *                        unique="true"
     *                        cascade="all"
     *                        lazy="false"
     *                        fetch="join"
     */
    public Value getTheValue() {
        return theValue;
    }

    /**
     * Set the collection of value objects for the response that 
     * represents all values of the response over its lifetime.
     * 
     * @param values The collection of value objects.
     */
    public void setTheValue(Value value) {
        Value oldValue = this.theValue;
        this.theValue = value;
        propertyChangeSupport.firePropertyChange(VALUE_PROPERTY, oldValue,
                this.theValue);
    }

    /**
     * @hibernate.list cascade="all"
     * @hibernate.one-to-many class="org.psygrid.data.model.hibernate.Value"
     * @hibernate.key column="c_old_br_id" 
     *                not-null="false"
     * @hibernate.list-index column="c_index"
     */
    public List<Value> getOldValues() {
		return oldValues;
	}
    
    public Value removeOldValueAtIndex(int index){
    	return (Value)oldValues.remove(index);
    }

	public void setOldValues(List<Value> oldValues) {
		this.oldValues = oldValues;
	}

    /**
     * Get the current value for the response, formatted as
     * a string.
     * <p>
     * The current value is the one that was most recently entered.
     * 
     * @return The current value formatted as a string.
     * @throws ModelException if the value of the response
     * cannot be formatted as a string.
     */
    public String getStringValue() throws ModelException{
        IValue value = getValue();
        String stringValue = null;
        BasicEntry be = (BasicEntry)entry;
        if ( null != value ){
            stringValue = be.formatValue(value);
        }
        return stringValue;
    }
    
    /**
     * Get the current value for the response.
     * <p>
     * The current value is the one that was most recently entered.
     * 
     * @return The current value.
     */
    public IValue getValue() {
        return theValue;
    }

    /**
     * Set the value of the response from a value object.
     * <p>
     * The value object will be used as the current value of the 
     * response, deprecating existing values.
     * <p>
     * Provenance information will be stored regarding how the
     * value of the response has been modified.
     * 
     * @param value The new value for the response.
     * @throws ModelException if the value of the response
     * cannot be set
     */
    public void setValue(IValue value) throws ModelException {
        setValue(value, null, null);
    }

    public void setValue(IValue value, ChangeHistory change) throws ModelException {
        setValue(value, null, change);
    }

    /**
     * Set the value of the response from a value object.
     * <p>
     * The value object will be used as the current value of the 
     * response, deprecating existing values.
     * <p>
     * Provenance information will be stored regarding how the
     * value of the response has been modified, including the 
     * specified comment.
     * 
     * @param value The new value for the response.
     * @param comment Comment annotating the new value of the response.
     * @throws ModelException if the value of the response
     * cannot be set
     */
    public void setValue(IValue value, String comment) throws ModelException {
    	setValue(value, comment, null);
    }

    public void setValue(IValue value, String comment, ChangeHistory change) throws ModelException {
        Value newValue = (Value)value;
        ChangeHistory c = (ChangeHistory)change;
        //check that the value is of the correct type
        checkValue(newValue);
        //create new provenance object
        Provenance prov = new Provenance(this.theValue, newValue);
        prov.setComment(comment);
        prov.setParentChange(c);
        //move current value to the old values list and set the new value
        if ( null != this.theValue ){
        	oldValues.add(this.theValue);
        }
        this.theValue = newValue;
        provItems.add(prov);
        propertyChangeSupport.firePropertyChange(null, null, null);
    }
    
    /**
     * Check that a value being added to the response is of an
     * appropriate type.
     * 
     * @param val The value being added.
     * @throws ModelException if the value being added is not of an
     * appropriate type for the response.
     */
    private void checkValue(Value val) throws ModelException {
        if ( null != val ){
            BasicEntry be = (BasicEntry)entry;
            if ( val.getClass() != be.getValueClass() ){
                throw new ModelException("Cannot add a value of "+val.getClass()+" to the response: expected value of "+be.getValueClass());
            }
        }
    }

    /**
     * Get the comment attached to the most recent provenance item
     * associated with values to the response.
     * 
     * @return The comment.
     */
    public String getLatestValueComment() {
        List<Provenance> provs = this.getProvenance(Value.class);
        if ( 0 == provs.size()){
            return null;
        }
        else{
            return provs.get(provs.size()-1).getComment();
        }
    }

    public org.psygrid.data.model.dto.BasicResponseDTO toDTO() {
      	 return toDTO(RetrieveDepth.DS_SUMMARY);
      }
      
      public org.psygrid.data.model.dto.BasicResponseDTO toDTO(RetrieveDepth depth){
          //create list to hold references to objects in the responses's
          //object graph which have multiple references to them within
          //the object graph. This is used so that each object instance
          //is copied to its DTO equivalent once and once only
          Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
          org.psygrid.data.model.dto.BasicResponseDTO dtoR = toDTO(dtoRefs, depth);
          dtoRefs = null;
          return dtoR;
      }
    
    public org.psygrid.data.model.dto.BasicResponseDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        org.psygrid.data.model.dto.BasicResponseDTO dtoR = new org.psygrid.data.model.dto.BasicResponseDTO();
        toDTO(dtoR, dtoRefs, depth);
        return dtoR;
    }
    
    public void toDTO(org.psygrid.data.model.dto.BasicResponseDTO dtoR, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        super.toDTO(dtoR, dtoRefs, depth);
        if ( null != this.theValue ){
            dtoR.setTheValue(this.theValue.toDTO(dtoRefs, depth));
        }
        
        org.psygrid.data.model.dto.ValueDTO[] dtoOldValues = 
            new org.psygrid.data.model.dto.ValueDTO[this.oldValues.size()];
        for ( int i=0; i<this.oldValues.size(); i++ ){
        	if (this.oldValues.get(i) != null) {
        		dtoOldValues[i] = this.oldValues.get(i).toDTO(dtoRefs, depth);
        	}
        }
        dtoR.setOldValues(dtoOldValues);
   }
    
    /**
     * Attach a detached response to its dataset objects.
     * 
     * @param element The element to attach the response to.
     */
    public void attach(Entry ent) throws ModelException{
        super.attach(ent);
        if ( null != this.theValue ){
            this.theValue.attach((BasicEntry)ent);
        }
        
        for ( Value v: this.oldValues ){
        	if (v != null) {
        		v.attach((BasicEntry)ent);
        	}
        }
        
    }
    
    public void detach(){
        super.detach();
        if ( null != this.theValue ){
            this.theValue.detach();
        }
        
        for ( Value v: this.oldValues ){
        	if (v != null) {
        		v.detach();
        	}
        }
    }
    
    @Override
    protected void addChildTasks(Record r) {
        //do nothing - a basic response cannot have any children
    }

	@Override
	public void recordCurrentState() {
		this.theValue.recordCurrentState();
	}

	@Override
	public void checkForChanges(ChangeHistory change) {
		if ( getTheValue().isValueChanged() ){
			//value has been changed in this editing session
			//so add provenance to record this
			Value newValue = getTheValue().copy();
			getTheValue().rollback();
			setValue(newValue, change);
		}
		else{
			//there may be provenance that we need to link to this
			//change in the change history...
			for ( Provenance p: provItems ){
				if ( null == p.getParentChange() && null == p.getId() ){
					p.setParentChange(change);
				}
			}
		}
	}

	public String exportCodeValue(boolean authorized) {
		if ( null == getValue() ){
			return null;
		}
		return getValue().exportCodeValue(authorized);
	}

	public String exportTextValue(boolean authorized) {
		if ( null == getValue() ){
			return null;
		}
		return getValue().exportTextValue(authorized);
	}

	public String exportUnitValue(boolean authorized) {
		if ( null == getValue() ){
			return null;
		}
		return getValue().exportUnitValue(authorized);
	}

	public String exportExtraValue(boolean authorized) {
		if ( null == getValue() ){
			return null;
		}
		return getValue().exportExtraValue(authorized);
	}

}
