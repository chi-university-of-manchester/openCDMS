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

import org.psygrid.data.model.ILongTextValue;
import org.psygrid.data.model.hibernate.Entry;
import org.psygrid.data.model.hibernate.ModelException;


public class LongTextValueAdapter extends ValueAdapter implements ILongTextValue {

    public LongTextValueAdapter(ILongTextValue value) {
        super(value);
    }
    
    public String getValue() {
        return ((ILongTextValue) value).getValue();
    }

    public void setValue(String value) throws ModelException {
        ((ILongTextValue) this.value).setValue(value);
    }

    @Override
    public ILongTextValue copy() {
        return (ILongTextValue) super.copy();
    }
    
    @Override
    public LongTextValueAdapter copyAdapter() {
        LongTextValueAdapter adapterCopy = new LongTextValueAdapter(copy());
        adapterCopy.setDisplayText(getDisplayText());
        return adapterCopy;
    }

	public void importValue(String value, Entry entry) {
		return;
	}

}
