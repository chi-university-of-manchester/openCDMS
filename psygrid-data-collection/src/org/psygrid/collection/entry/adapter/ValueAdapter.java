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


package org.psygrid.collection.entry.adapter;

import java.beans.PropertyChangeListener;

import org.psygrid.data.model.hibernate.BasicEntry;
import org.psygrid.data.model.hibernate.StandardCode;
import org.psygrid.data.model.IValue;

import com.jgoodies.binding.beans.ExtendedPropertyChangeSupport;
import org.psygrid.data.model.hibernate.Unit;

public abstract class ValueAdapter implements IValue {

    private static final String DISPLAY_TEXT_PROPERTY = "displayText"; //$NON-NLS-1$
    
    private String displayText;
    
    protected final IValue value;
    
    /**
     * Object that contains all the logic required to support the propagation of
     * PropertyChange events.
     */
    protected final ExtendedPropertyChangeSupport propertyChangeSupport = new 
        ExtendedPropertyChangeSupport(this);
    
    protected ValueAdapter(IValue value) {
        if (value == null) {
            throw new IllegalArgumentException("value cannot be null"); //$NON-NLS-1$
        }
        this.value = value;
    }
    
    public abstract ValueAdapter copyAdapter();
    
    public final IValue getValueDelegate() {
        return value;
    }

    public final Unit getUnit() {
        return value.getUnit();
    }

    public final void setUnit(Unit unit) {
        value.setUnit(unit);
    }

    public final boolean isDeprecated() {
        return value.isDeprecated();
    }

    public final StandardCode getStandardCode() {
        return value.getStandardCode();
    }

    public final void setStandardCode(StandardCode standardCode) {
        value.setStandardCode(standardCode);
    }

    public final Long getId() {
        return value.getId();
    }
    
    public IValue copy() {
        return value.copy();
    }

    public IValue ddeCopy(BasicEntry primEntry, BasicEntry secEntry) {
		return value.ddeCopy(primEntry, secEntry);
	}

	public final void addPropertyChangeListener(PropertyChangeListener listener) {
        value.addPropertyChangeListener(listener);
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public final void removePropertyChangeListener(PropertyChangeListener listener) {
        value.removePropertyChangeListener(listener);
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public final String getDisplayText() {
        return displayText;
    }

    public final void setDisplayText(String displayText) {
        String oldValue = this.displayText;
        this.displayText = displayText;
        propertyChangeSupport.firePropertyChange(DISPLAY_TEXT_PROPERTY, oldValue,
                this.displayText);
    }

    public final void publish() {
        value.publish();
    }

    public final boolean isTransformed() {
        return value.isTransformed();
    }
    
    public final boolean isHidden() {
        return value.isHidden();
    }

    public final String getValueAsString() {
        return value.getValueAsString();
    }

	public boolean isNull() {
		return value.isNull();
	}

    public String export() {
        return value.export();
    }

	public String getOldValueAsString() {
		return value.getOldValueAsString();
	}

	public boolean isValueChanged() {
		return value.isValueChanged();
	}

	public String exportCodeValue(boolean authorized) {
		return value.exportCodeValue(authorized);
	}

	public String exportExtraValue(boolean authorized) {
		return value.exportExtraValue(authorized);
	}

	public String exportTextValue(boolean authorized) {
		return value.exportTextValue(authorized);
	}

	public String exportUnitValue(boolean authorized) {
		return value.exportUnitValue(authorized);
	}
	
	
}
