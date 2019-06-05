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

import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.psygrid.collection.entry.adapter.ValueAdapter;
import org.psygrid.collection.entry.model.BasicPresModel;
import org.psygrid.collection.entry.model.TextPresModel;
import org.psygrid.collection.entry.ui.EntryComponent;
import org.psygrid.collection.entry.validation.NumericValidationHandler;
import org.psygrid.data.model.hibernate.BasicEntry;
import org.psygrid.data.model.hibernate.BasicResponse;
import org.psygrid.data.model.hibernate.StandardCode;
import org.psygrid.data.model.IValue;

public abstract class NumericRendererSPI extends AbstractRendererSPI {

    @Override
    protected void postFieldCreation(BasicPresModel model,
            List<StandardCode> stdCodes, EntryComponent field, boolean disableStandardCodes) {
        if (field.getTextComponent() instanceof JTextField) {
            ((JTextField) field.getTextComponent()).setHorizontalAlignment(
                    SwingConstants.RIGHT);
        }
        super.postFieldCreation(model, stdCodes, field, disableStandardCodes);
        
        // Safe not to release listener
        TextPresModel presModel = (TextPresModel) model;
        presModel.getDisplayTextModel().addValueChangeListener(
                getValidationHandler(presModel));
    }
    
    protected abstract NumericValidationHandler getValidationHandler(
            TextPresModel presModel);
    
    protected abstract void setDisplayText(IValue value, ValueAdapter valueAdapter);
    
    @Override
    protected IValue createValue(BasicEntry entry) {
        IValue value = entry.generateValue();
        ValueAdapter valueAdapter = getValueAdapter(value);
        setDisplayText(value, valueAdapter);
        return valueAdapter;
    }
    
    @Override
    protected IValue getValue(BasicEntry entry, BasicResponse response, boolean copy) {
        if (response == null) {
            return createValue(entry);
        }
        
        IValue currentValue = RendererHelper.getInstance().checkAndGetValue(response);
        
        if (copy) {
            if (currentValue.isTransformed()) {
                return createValue(entry);
            }
            currentValue = currentValue.copy();
        }
        ValueAdapter valueAdapter = getValueAdapter(currentValue);
        if (currentValue.isHidden()) {
        	valueAdapter.setDisplayText(HIDDEN_VALUE);
        }
        else if (!currentValue.isHidden() && currentValue.isTransformed()) {
            valueAdapter.setDisplayText(currentValue.getValueAsString());
        }
        else if (!currentValue.isTransformed()) {
            setDisplayText(currentValue, valueAdapter);
        }
        
        return valueAdapter;
    }

    protected abstract ValueAdapter getValueAdapter(IValue value);
}