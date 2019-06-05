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

import java.util.Date;

import org.psygrid.data.model.IDateValue;
import org.psygrid.data.model.hibernate.Entry;
import org.psygrid.data.model.hibernate.ModelException;

public class DateValueAdapter extends ValueAdapter implements IDateValue {

    private static final String YEAR_TEXT_PROPERTY = "yearText"; //$NON-NLS-1$
    
    private String yearText;
    
    public DateValueAdapter(IDateValue value) {
        super(value);
    }

    public Date getValue() {
        return ((IDateValue) value).getValue();
    }

    public void setValue(Date value) throws ModelException {
        ((IDateValue) this.value).setValue(value);
    }

    public Integer getMonth() {
        return ((IDateValue) value).getMonth();
    }

    public void setMonth(Integer month) {
        ((IDateValue) this.value).setMonth(month);
        
    }

    public Integer getYear() {
        return ((IDateValue) value).getYear();
    }

    public void setYear(Integer year) {
        ((IDateValue) this.value).setYear(year);
    }
    
    @Override
    public IDateValue copy() {
        return (IDateValue) super.copy();
    }
    
    @Override
    public DateValueAdapter copyAdapter() {
        DateValueAdapter adapterCopy = new DateValueAdapter(copy());
        adapterCopy.setDisplayText(getDisplayText());
        adapterCopy.setYearText(getYearText());
        return adapterCopy;
    }

    public String getYearText() {
        return yearText;
    }
    
    public void setYearText(String yearText) {
        String oldValue = this.yearText;
        this.yearText = yearText;
        propertyChangeSupport.firePropertyChange(YEAR_TEXT_PROPERTY, oldValue, 
                this.yearText);
    }

	public void importValue(String value, Entry entry) throws ModelException {
	}

}
