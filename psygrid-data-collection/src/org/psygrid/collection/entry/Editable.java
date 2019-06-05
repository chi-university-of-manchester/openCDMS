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


package org.psygrid.collection.entry;

/**
 * Implementors of this interface must provide two properties, <code>editable</code>
 * and <code>enabled</code>. See the description of the property setters and
 * getters for more information.
 * 
 * @author Ismael Juma (ismael@juma.me.uk)
 */
public interface Editable {
    
    /**
     * Sets the <code>editable</code> property to the value of <code>b</code>.
     * See {@link #isEditable()} for more information about this property.
     * 
     * @param b The value to set the <code>editable</code> property to.
     */
    public void setEditable(boolean b);
    
    /**
     * Returns the value of the <code>editable</code> property. If 
     * <code>true</code>, a component's state can be changed by the user.
     * Otherwise, its visual appearance is different and the state cannot be
     * changed by the user. The main difference with the <code>enabled</code>
     * property is that events are still processed in this case. For example,
     * a non-editable component may still launch a dialog box in case it is 
     * double-clicked. This would not be the case with a disabled component.
     *  
     * @return the value of the <code>editable</code> property.
     */
    public boolean isEditable();
    
    /**
     * Sets the <code>enabled</code> property to the value of <code>b</code>.
     * See {@link #isEnabled()} for more information about this property.
     * 
     * @param b The value to set the <code>enabled</code> property to.
     */
    public void setEnabled(boolean b, boolean isStandardCode);
    
    /**
     * Returns if the component is enabled or disabled. An enabled component
     * can react to user input and events.
     * 
     * @return the value of the <code>enabled</code> property.
     * @see #isEnabled()
     */
    public boolean isEnabled();
    
    /**
      * Sets the <code>mandatory</code> property to the value of <code>b</code>.
     * See {@link #isMandatory()} for more information about this property.
     * 
     * @param b The value to set the <code>mandatory</code> property to.
     */
    public void setMandatory(boolean b);
    
    /**
     * Returns the value of the <code>mandatory</code> property. If 
     * <code>true</code>, a value for the component must be entered by the user.
     * The visual appearance of the component should indicate this to the user.
     *  
     * @return the value of the <code>mandatory</code> property.
     */
    public boolean isMandatory();
}
