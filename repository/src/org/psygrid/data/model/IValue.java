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

package org.psygrid.data.model;

import org.psygrid.data.model.hibernate.BasicEntry;
import org.psygrid.data.model.hibernate.Entry;
import org.psygrid.data.model.hibernate.ModelException;
import org.psygrid.data.model.hibernate.StandardCode;

import com.jgoodies.binding.beans.Observable;
import org.psygrid.data.model.hibernate.Unit;

/**
 * Interface that represents a value to a response.
 * <p>
 * Sub-interfaces of this interface cater for specific types of value.
 * 
 * @author Rob Harper
 *
 */
public interface IValue extends IProvenanceable, Observable {

    public static final String TYPE_STRING = "String";
    public static final String TYPE_DATE = "Date";
    public static final String TYPE_DOUBLE = "Double";
    public static final String TYPE_INTEGER = "Integer";
    
    /**
     * Get the unit of measurement for the value.
     * 
     * @return The unit of measurement.
     */
    public Unit getUnit();

    /**
     * Set the unit of measurement for the value.
     * 
     * @param unit The unit of measurement.
     */
    public void setUnit(Unit unit);
    
    /**
     * Get the flag to represent whether the value has been 
     * deprecated.
     * 
     * @return The deprecated flag.
     */
    public boolean isDeprecated();
    
    /**
     * Get the standard code for the value.
     * 
     * @return The standard code.
     */
    public StandardCode getStandardCode();
    
    /**
     * Set the standard code for the value.
     * 
     * @param standardCode The standard code.
     */
    public void setStandardCode(StandardCode standardCode);
    
    /**
     * Get the transformed flag for the value.
     * <p>
     * If True, then the value has been transformed by one
     * or more transformers since it was initially created.
     * 
     * @return True if the value has been transformed.
     */
    public boolean isTransformed();
    
    /**
     * Get the hidden flag for the value.
     * <p>
     * If True, then the value has been transformed and it
     * is now not intended to be seen by the user.
     * 
     * @return True if the value should be hidden.
     */
    public boolean isHidden();
    
    public boolean isNull();
    
    /**
     * Publish the value, after which it becomes immutable.
     */
    public void publish();
    
    /**
     * Return a copy of the value object.
     * 
     * @return A copy of the value object.
     */
    public IValue copy();

    /**
     * Return the value of the value as a string.
     * 
     * @return The value as a string.
     */
    public String getValueAsString();
    
    /**
     * @deprecated
     */
    public String export();
    
    /**
     * Get the textual value for export.
     * <p>
     * All sub-interfaces should provide this method as it
     * is the primary method of export.
     * 
     * @return The text for export.
     */
    public String exportTextValue(boolean authorized);
    
    /**
     * Get the code of this value for export.
     * <p>
     * Only applicable for {@link IOptionValue}, or when a standard code 
     * has been used.
     * 
     * @return The code for export.
     */
    public String exportCodeValue(boolean authorized);
    
    /**
     * Get the unit of this value for export.
     * 
     * @return The unit for export.
     */
    public String exportUnitValue(boolean authorized);
    
    /**
     * Get the extra value of the value for export.
     * 
     * @return The extra value for export.
     */
    public String exportExtraValue(boolean authorized);
    
    /**
     * Set the value from a string format. This method has been added
     * to allow data to be imported from other sources, such as a .csv,
     * which is to be read as a string-based representation and parsed
     * as subsets of this string.
     * Returns true if the value was imported, returns false otherwise.
     * Note that this return value is independent of whether the value 
     * passes validation rules.
     */
    public void importValue(String value, Entry entry) throws ModelException;
    
    /**
     * Return a copy of the value object for use when copying the value
     * for dual data entry i.e. into a record associated with a different 
     * dataset.
     * 
     * @param primEntry The entry in the primary dataset with which the 
     * original value is associated.
     * @param secEntry The entry in the secondary dataset to which the copied 
     * value will be associated.
     * @return The copy of the value.
     */
    public IValue ddeCopy(BasicEntry primEntry, BasicEntry secEntry);
    
	/**
	 * Compare the current state of the Value object with
	 * that stored by a call to storeCurrentValue
	 * 
	 * @return Boolen, True if the state has changed, false
	 * if it hasn't.
	 */
	public boolean isValueChanged();
	
    /**
     * Return the old value of the value as a string i.e. the value it had
     * before any editing tok place in the current editing session.
     * 
     * @return The old value as a string.
     */
    public String getOldValueAsString();
    

}