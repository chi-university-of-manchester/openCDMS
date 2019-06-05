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

import java.util.List;

import org.psygrid.collection.entry.adapter.LongTextValueAdapter;
import org.psygrid.collection.entry.model.BasicPresModel;
import org.psygrid.collection.entry.model.TextPresModel;
import org.psygrid.collection.entry.ui.EntryComponent;
import org.psygrid.collection.entry.ui.LongTextEntryField;
import org.psygrid.collection.entry.validation.LongTextValidationHandler;
import org.psygrid.data.model.ILongTextValue;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.model.IValue;

public class LongTextRendererSPI extends AbstractRendererSPI {

    public boolean canHandle(Entry modelObject, Entry parent) {
        if (modelObject instanceof LongTextEntry) {
            return true;
        }
        return false;
    }

    @Override
    protected void postFieldCreation(BasicPresModel model,
            List<StandardCode> stdCodes, EntryComponent field, boolean disableStandardCodes) {
        super.postFieldCreation(model, stdCodes, field, disableStandardCodes);
        TextPresModel presModel = (TextPresModel) model;
        presModel.getDisplayTextModel().addValueChangeListener(
                new LongTextValidationHandler(presModel));
    }
    
    @Override
    protected EntryComponent createField(BasicPresModel model,
            List<Unit> units, List<StandardCode> stdCodes, boolean disableStandardCodes) {
        TextPresModel textPresModel = (TextPresModel) model;
        LongTextEntryField field = new LongTextEntryField(
                textPresModel.getDisplayTextModel(), 
                textPresModel.getStandardCodeModel());
        field.setRows(6);
        field.setColumns(10);
        postFieldCreation(model, stdCodes, field, disableStandardCodes);
        return field;
    }
    
    @Override
    protected IValue createValue(BasicEntry entry) {
        ILongTextValue value = ((LongTextEntry) entry).generateValue();
        LongTextValueAdapter valueAdapter = new LongTextValueAdapter(value);
        valueAdapter.setDisplayText(value.getValue());
        return valueAdapter;
    }
    
    @Override
    protected IValue getValue(BasicEntry entry, BasicResponse response, boolean copy) {
        if (response == null) {
            return createValue(entry);
        }
        
        ILongTextValue currentValue = 
            (ILongTextValue) RendererHelper.getInstance().checkAndGetValue(response);
        
        if (copy) {
            if (currentValue.isTransformed()) {
                return createValue(entry);
            }
            currentValue = currentValue.copy();
        }
        LongTextValueAdapter valueAdapter = new LongTextValueAdapter(currentValue);
        if (!currentValue.isHidden() && currentValue.isTransformed()) {
            valueAdapter.setDisplayText(currentValue.getValueAsString());
        }
        else if (!currentValue.isTransformed()) {
            valueAdapter.setDisplayText(currentValue.getValue());
        }
        return valueAdapter;
    }
    
}
