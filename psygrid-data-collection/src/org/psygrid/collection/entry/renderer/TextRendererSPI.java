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

import org.psygrid.collection.entry.adapter.TextValueAdapter;
import org.psygrid.collection.entry.model.BasicPresModel;
import org.psygrid.collection.entry.model.TextPresModel;
import org.psygrid.collection.entry.ui.EntryComponent;
import org.psygrid.collection.entry.validation.TextValidationHandler;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.model.ITextValue;
import org.psygrid.data.model.IValue;

public class TextRendererSPI extends AbstractRendererSPI {

    public boolean canHandle(Entry model, Entry parent) {
        if (model instanceof TextEntry) {
            return true;
        }

        return false;
    }
    

    @Override
    protected void postFieldCreation(final BasicPresModel model,
            List<StandardCode> stdCodes, EntryComponent field, boolean disableStandardCodes) {
        super.postFieldCreation(model, stdCodes, field, disableStandardCodes);
        final TextPresModel presModel = (TextPresModel) model;
        // Safe not to release listener
        presModel.getDisplayTextModel().addValueChangeListener(new TextValidationHandler(presModel));

    }
    
    @Override
    protected IValue createValue(BasicEntry entry) {
        ITextValue value = ((TextEntry) entry).generateValue();
        TextValueAdapter valueAdapter = new TextValueAdapter(value);
        valueAdapter.setDisplayText(value.getValue());
        return valueAdapter;
    }
    
    @Override
    protected IValue getValue(BasicEntry entry, BasicResponse response, boolean copy) {
        if (response == null) {
            return createValue(entry);
        }
        
        ITextValue currentValue = 
            (ITextValue) RendererHelper.getInstance().checkAndGetValue(response);
       
        if (copy) {
            if (currentValue.isTransformed()) {
                return createValue(entry);
            }
            currentValue = currentValue.copy();
        }
        TextValueAdapter valueAdapter = new TextValueAdapter(currentValue);
        if (!currentValue.isHidden() && currentValue.isTransformed()) {
            valueAdapter.setDisplayText(currentValue.getValueAsString());
        }
        else if (!currentValue.isTransformed()) {
            valueAdapter.setDisplayText(currentValue.getValue());
        }
        return valueAdapter;
    }
}
