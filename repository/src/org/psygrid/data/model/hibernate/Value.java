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

import java.beans.PropertyChangeListener;
import java.util.Map;

import org.psygrid.data.model.IValue;
import org.psygrid.data.model.hibernate.visitors.ValueVisitor;

import com.jgoodies.binding.beans.ExtendedPropertyChangeSupport;

/**
 * Base class of all classes that represent values of Responses.
 * 
 * @author Rob Harper
 * 
 * @hibernate.joined-subclass table="t_values"
 * @hibernate.joined-subclass-key column="c_id"
 */
public abstract class Value extends Provenanceable implements IValue {

    private static final String UNIT_PROPERTY = "unit";
    private static final String STANDARD_CODE_PROPERTY = "standardCode";
    private static final String DEPRECATED_PROPERTY = "deprecated";

    public static final String UNAUTHORIZED_VALUE = "*****";
    
    /**
     * Object that contains all the logic required to support the propagation of
     * PropertyChange events.
     */
    protected ExtendedPropertyChangeSupport propertyChangeSupport = new 
            ExtendedPropertyChangeSupport(this);
	
    /**
     * Flag to represent whether the Value has been deprecated.
     */
	private boolean deprecated;
	
    /**
     * Standard code to be used with or instead of the actual
     * value.
     */
    protected StandardCode standardCode;
	
    /**
     * The unit of measurement for the value.
     */
    protected Unit unit;
    
    protected Long unitId;
    
    protected StandardCode oldStandardCode;
    
    protected Unit oldUnit;
    
    protected boolean oldValuesStored;
    
    /**
     * Boolean, True if the value has been transformed from
     * its entered value by a transformer.
     */
    protected boolean transformed;
    
    /**
     * Boolean, True if the value has been transformed and it
     * is now not intended to be seen by the user.
     */
    protected boolean hidden;
    
    /**
     * Boolean flag to represent whether the object is currently in 
     * a read-only state.
     * <p>
     * For use at runtime only - not intended to be persisted.
     */
    protected boolean readOnly;
    
	/**
	 * Default no-arg constructor, as required by the Hibernate framework
	 * for all persistable classes.
	 */
	public Value(){};
	
	/**
	 * Get the flag to represent whether the value has been deprecated
	 * 
	 * @return The deprecated flag.
	 * 
	 * @hibernate.property column="c_deprecated"
	 */
	public boolean isDeprecated() {
		return deprecated;
	}

	/**
	 * Set the flag to represent whether the value has been deprecated
	 * 
	 * @param deprecated The deprecated flag.
	 */
	public void setDeprecated(boolean deprecated) {
        boolean oldDeprecated = this.deprecated;
		this.deprecated = deprecated;
        propertyChangeSupport.firePropertyChange(DEPRECATED_PROPERTY,
                oldDeprecated, this.deprecated);
	}

    /**
     * @hibernate.many-to-one class="org.psygrid.data.model.hibernate.StandardCode"
     *                        column="c_std_code_id"
     *                        not-null="false"
     *                        cascade="none"
     */
    public StandardCode getStandardCode() {
        return this.standardCode;
    }
	
    public void setStandardCode(StandardCode standardCode) {
        if ( this.readOnly ){
            throw new ModelException("Cannot set standard code - object is read-only");
        }
        StandardCode oldStdCode = this.standardCode;
        this.standardCode = standardCode;
        propertyChangeSupport.firePropertyChange(STANDARD_CODE_PROPERTY,
                oldStdCode, this.standardCode);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }
    
    /**
     * @hibernate.many-to-one class="org.psygrid.data.model.hibernate.Unit"
     *                        column="c_unit_id"
     *                        not-null="false"
     *                        cascade="none"
     */
    public Unit getUnit() {
        return unit;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    /**
     * Set the Unit of measurement for the Response.
     * <p>
     * This override is required by Hibernate.
     * 
     * @param unit The Unit of measurement
     */
    public void setUnit(Unit unit) {
        if ( this.readOnly ){
            throw new ModelException("Cannot set unit - object is read-only");
        }
        Unit oldUnit = this.unit;
        this.unit = unit;
        propertyChangeSupport.firePropertyChange(UNIT_PROPERTY, oldUnit,
                this.unit);
    }

    /**
     * Get the flag that represents whether the value has been 
     * transformed.
     * 
     * @return True if the value has been transformed.
     * 
     * @hibernate.property column="c_transformed"
     */
    public boolean isTransformed() {
        return transformed;
    }

    public void setTransformed(boolean transformed) {
        if ( this.readOnly ){
            throw new ModelException("Cannot set transformed - object is read-only");
        }
        this.transformed = transformed;
    }

    /**
     * @hibernate.property column="c_hidden"
     */
    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        if ( this.readOnly ){
            throw new ModelException("Cannot set hidden - object is read-only");
        }
        this.hidden = hidden;
    }

    public boolean isReadOnly() {
        return readOnly;
    }
    
    /**
     * Set the boolean flag to represent whether the object is currently in 
     * a read-only state.
     * <p>
     * This should be called with argument "true" as soon as is practical,
     * typically when the user clicks "Next" or "Save" in the view where
     * the provenanceable object is rendered.
     * <p>
     * For use at runtime only - not intended to be persisted.
     * 
     * @param readOnly
     */
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public void publish(){
        this.readOnly = true;
    }
    
    /**
     * See if the actual value of the value object is equal
     * to that of another value object.
     * <p>
     * Note that the equals method is not being used here because
     * it is used to tell whether two objects are the same. In this
     * case, we want to tell whether two different objects (for
     * which equals will return false) have the same value.
     * 
     * @param v The value to check against this value.
     * @return True if the actual value of the value objects
     * is the same.
     */
    public abstract boolean valueEquals(Value v);
   
    public abstract org.psygrid.data.model.dto.ValueDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth);

    public void toDTO(org.psygrid.data.model.dto.ValueDTO dtoV, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        super.toDTO(dtoV, dtoRefs, depth);
        dtoV.setDeprecated(this.deprecated);
        dtoV.setTransformed(this.transformed);
        dtoV.setHidden(this.hidden);
        if ( null != this.standardCode ){
            dtoV.setStandardCode(this.standardCode.toDTO(dtoRefs, depth));
        }
        if ( null != this.unit ){
            dtoV.setUnitId(this.unit.getId());
        }
        else {
        	dtoV.setUnitId(this.unitId);
        }
    }
    
    /**
     * Attach a detached value to its dataset objects.
     * 
     * @param e The entry that the value's response is attached to.
     * @throws ModelException if an object in the value's graph
     * cannot be attached.
     */
    public void attach(BasicEntry e) throws ModelException{
        Long uId = null;
        if ( null != this.unitId ){
            uId = this.unitId;
        }
        else if ( null != this.unit ){
            //preserve backwards compatability with records detached
            //prior to the introduction of Value.unitId
            uId = this.unit.getId();
        }
        if ( null != uId ){
            boolean attached = false;
            for (Unit u:e.getUnits()){
                if ( u.getId().equals(uId) ){
                    this.unit = u;
                    this.unitId = null;
                    attached = true;
                    break;
                }
            }
            if ( !attached ){
                throw new ModelException("Failed to attach unit of value id="+this.getId()+" - no unit exists with id="+uId);
            }
        }
    }
    
    public void detach(){
        if ( null != this.unit ){
            this.unitId = this.unit.getId();
            this.unit = null;
        }
    }
    
    /**
     * Accept a visitor to the class.
     * 
     * @param visitor The visitor.
     * @throws ModelException
     */
    public abstract void accept(ValueVisitor visitor) throws ModelException;
    
    public void copy(Value newValue){
        newValue.setDeprecated(this.deprecated);
        newValue.setStandardCode(this.standardCode);
        newValue.setTransformed(this.transformed);
        newValue.setHidden(this.hidden);
        newValue.setUnit(this.unit);
    }

    public void lock() {
        this.readOnly = true;
    }
        
    public void unlock() {
        this.readOnly = false;
    }
        
    public abstract Value copy();
    
    protected void copyProps(Value v){
        v.setDeprecated(this.deprecated);
        v.setStandardCode(this.standardCode);
        v.setTransformed(this.transformed);
        v.setHidden(this.hidden);
        v.setUnit(this.unit);
    }
    
    protected void ddeCopyProps(BasicEntry primEntry, BasicEntry secEntry, Value v){
        v.setDeprecated(this.deprecated);
        v.setStandardCode(this.standardCode);
        v.setTransformed(this.transformed);
        v.setHidden(this.hidden);
        if ( null != this.unit ){
	        //find the corresponding unit associated with the entry in the secondary
	        //dataset with the same index as
	        int index = -1;
	        for ( int i=0, c=primEntry.numUnits(); i<c; i++ ){
	        	Unit u = primEntry.getUnit(i);
	        	if ( u.equals(this.unit)){
	        		index = i;
	        		break;
	        	}
	        }
	        v.setUnit(secEntry.getUnit(index));
        }
        else {
        	v.setUnit(null);
        }
    }
    
    public abstract boolean isNull();
    
    public String getValueAsString(){
    	if ( null == standardCode ){
    		return null;
    	}
    	return standardCode.getForDisplay();
    }
    
    /**
     * This method returns the value as an Object to allow values to be treated
     * polymorphically.
     * @return
     */
    public abstract Object getTheValue();
    
    /**
     * Get the value of the Value object as a String, with the
     * String created from the actual value subject to the
     * given formatting options. Also gets the type that the string 
     * value represents.
     * <p>
     * Used to get the value when generating a report.
     * 
     * @param options The formatting options.
     * @return String array with three elements; zeroth is the string 
     * representation of the value, first is the type this string 
     * represents (i.e. number, string, etc.), second is the unit of
     * the value (<code>null</code> if no unit)
     */
    public abstract String[] getReportValueAsString(String options);
    
    public String getValueForExport(){
    	return getValueAsString();
    }
    
    protected String[] checkForStandardCode(){
        String[] result = null;
        if ( null != this.standardCode ){
            result = new String[3];
            result[0] = this.standardCode.getForDisplay();
            result[1] = TYPE_STRING;
            result[2] = null;
        }
        return result;
    }

    public String export() {
        //Default behaviour
        if ( null != this.standardCode ){
            return this.standardCode.getForDisplay();
        }
        else{
            return getValueForExport();
        }
    }
    
    public String exportTextValue(boolean authorized){
    	if ( !authorized ){
    		return UNAUTHORIZED_VALUE;
    	}
    	if ( null != standardCode ){
    		return standardCode.getDescription();
    	}
    	return null;
    }

	public String exportCodeValue(boolean authorized) {
    	if ( !authorized ){
    		return UNAUTHORIZED_VALUE;
    	}
    	if ( null != standardCode ){
    		return Integer.toString(standardCode.getCode());
    	}
    	return null;
	}

	public String exportUnitValue(boolean authorized) {
    	if ( null != unit ){
        	if ( !authorized ){
        		return UNAUTHORIZED_VALUE;
        	}
    		return unit.getAbbreviation();
    	}
    	return null;
	}

	public String exportExtraValue(boolean authorized) {
    	return null;
	}

	public abstract IValue ddeCopy(BasicEntry primEntry, BasicEntry secEntry);
    
	public Double getValueForStats(){
		return null;
	}
	
	protected String getUnitForReport(){
		if ( null == unit ){
			return null;
		}
		else{
			return unit.getAbbreviation();
		}
	}
	
	/**
	 * Record the state of the Value object
	 */
	public void recordCurrentState(){
		this.oldValuesStored = true;
		this.oldStandardCode = this.standardCode;
		this.oldUnit = this.unit;
	}
	
	/**
	 * Compare the current state of the Value object with
	 * that stored by a call to storeCurrentValue
	 * 
	 * @return Boolen, True if the state has changed, false
	 * if it hasn't.
	 */
	public boolean isValueChanged(){
		if ( null == oldStandardCode ){
			if ( null != standardCode ){
				//standard code has been changed
				return true;
			}
		}
		else{
			if ( !oldStandardCode.equals(standardCode) ){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Roll the state of the Value object back to what
	 * it was when the state was stored by a call to
	 * recordCurrentState. 
	 */
	public void rollback(){
		this.standardCode = oldStandardCode;
		this.unit = oldUnit;
	}
	
    public String getOldValueAsString(){
    	if ( null == oldStandardCode ){
    		return null;
    	}
    	return oldStandardCode.getForDisplay();
    }
    

}
