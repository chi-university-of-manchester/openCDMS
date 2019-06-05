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

import org.psygrid.data.model.ITextValue;
import org.psygrid.data.model.hibernate.Entry;
import org.psygrid.data.model.hibernate.ModelException;


public class TextValueAdapter extends ValueAdapter  implements ITextValue {

    public TextValueAdapter(ITextValue value) {
        super(value);
    }

    public String getValue() {
        return ((ITextValue) value).getValue();
    }

    public void setValue(String value) throws ModelException {
        ((ITextValue) this.value).setValue(value);
    }

    @Override
    public ITextValue copy() {
        return (ITextValue) super.copy();
    }
    
    @Override
    public TextValueAdapter copyAdapter() {
        TextValueAdapter adapterCopy = new TextValueAdapter(copy());
        adapterCopy.setDisplayText(getDisplayText());
        return adapterCopy;
    }

	public void importValue(String value, Entry entry) throws ModelException {
	}

}
