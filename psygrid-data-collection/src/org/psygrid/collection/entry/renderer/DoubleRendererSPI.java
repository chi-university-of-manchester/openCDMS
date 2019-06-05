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


package org.psygrid.collection.entry.renderer;

import org.psygrid.collection.entry.adapter.NumericValueAdapter;
import org.psygrid.collection.entry.adapter.ValueAdapter;
import org.psygrid.collection.entry.model.TextPresModel;
import org.psygrid.collection.entry.validation.DoubleValidationHandler;
import org.psygrid.collection.entry.validation.NumericValidationHandler;
import org.psygrid.data.model.INumericValue;
import org.psygrid.data.model.IValue;
import org.psygrid.data.model.hibernate.Entry;
import org.psygrid.data.model.hibernate.NumericEntry;

public class DoubleRendererSPI extends NumericRendererSPI   {

    public boolean canHandle(Entry modelObject, Entry parent) {
        if (modelObject instanceof NumericEntry) {
            return true;
        }

        return false;
    }
    
    @Override
    protected NumericValidationHandler getValidationHandler(
            TextPresModel presModel) {
        return new DoubleValidationHandler(presModel);
    }
    
    @Override
    protected void setDisplayText(IValue value, ValueAdapter valueAdapter) {
        INumericValue numericValue = (INumericValue) value;
        String displayText = 
            RendererHelper.getInstance().getDoubleAsStringWithoutTrailingZero(numericValue.getValue());
        valueAdapter.setDisplayText(displayText);
    }

    @Override
    protected ValueAdapter getValueAdapter(IValue value) {
        return new NumericValueAdapter((INumericValue) value);
    }

}
