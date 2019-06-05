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

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JPopupMenu;

import org.psygrid.collection.entry.adapter.DateValueAdapter;
import org.psygrid.collection.entry.model.BasicPresModel;
import org.psygrid.collection.entry.model.DatePresModel;
import org.psygrid.collection.entry.model.TextPresModel;
import org.psygrid.collection.entry.ui.DatePicker;
import org.psygrid.collection.entry.ui.EntryComponent;
import org.psygrid.collection.entry.validation.DateEntryValidationHandler;
import org.psygrid.collection.entry.validation.IntegerValidationHandler;
import org.psygrid.data.model.IDateValue;
import org.psygrid.data.model.IValue;
import org.psygrid.data.model.hibernate.*;

public class DateRendererSPI extends AbstractRendererSPI {
	
	StandardCode s;

    public boolean canHandle(Entry modelObject, Entry parent) {
        if (modelObject instanceof DateEntry) {
            return true;
        }
        return false;
    }
   
    @Override
    protected EntryComponent createField(BasicPresModel model,
            List<Unit> units, List<StandardCode> stdCodes, boolean disableStandardCodes) {
        DatePresModel presModel = (DatePresModel) model;
        DatePicker field = new DatePicker(presModel.getDisplayTextModel(), 
                presModel.getMonthModel(), presModel.getYearTextModel());
        DateFormat format = RendererHelper.getInstance().getDateFormat();
        field.setFormat(format);
        postFieldCreation(model, stdCodes, field, disableStandardCodes);
        return field;
    }
    
    @Override
    protected JPopupMenu getPopupMenu(List<StandardCode> stdCodes, 
            TextPresModel presModel, EntryComponent field, boolean disableStandardCodes) {
        return RendererHelper.getInstance().getDatePopupMenu(stdCodes, 
                (DatePresModel) presModel, (DatePicker) field, disableStandardCodes);
    }
    
    @Override
    protected void postFieldCreation(BasicPresModel model,
            List<StandardCode> stdCodes, EntryComponent field, boolean disableStandardCodes) {
        super.postFieldCreation(model, stdCodes, field, disableStandardCodes);
        DatePresModel presModel = (DatePresModel) model;
        presModel.getDisplayTextModel().addValueChangeListener(
                new DateEntryValidationHandler(presModel));
        presModel.getYearTextModel().addValueChangeListener(
                new IntegerValidationHandler(presModel, 
                        presModel.getYearModel()));
    }
    
    private void initValueAdapter(IDateValue currentValue, DateValueAdapter valueAdapter) {
        if (currentValue.isHidden()) {
          //  return;
        	valueAdapter.setDisplayText(HIDDEN_VALUE);
        	return;
        }
        if (currentValue.isTransformed()) {
            valueAdapter.setDisplayText(currentValue.getValueAsString());
        }
        Date value = currentValue.getValue();
        Integer year = currentValue.getYear();
        DateFormat format = RendererHelper.getInstance().getDateFormat();
        String formattedDate = null;
        if (value != null) {
            formattedDate = format.format(value);
            valueAdapter.setDisplayText(formattedDate);
        }
        if (year != null) {
            valueAdapter.setYearText(String.valueOf(year));
        }
    }
    
    @Override
    protected IValue createValue(BasicEntry entry) {
        IDateValue dateValue = ((DateEntry) entry).generateValue();
        DateValueAdapter valueAdapter = new DateValueAdapter(dateValue);
        initValueAdapter(dateValue, valueAdapter);
        return valueAdapter;
    }
        
    @Override
    protected IValue getValue(BasicEntry entry, BasicResponse response,
            boolean copy) {
        DateEntry dateEntry = (DateEntry) entry;
        if (response == null) {
            return createValue(dateEntry);
        }
        IDateValue currentValue = 
            (IDateValue) RendererHelper.getInstance().checkAndGetValue(response);
        
        if (copy) {
            if (currentValue.isTransformed()) {
                return createValue(dateEntry);
            }
            currentValue = currentValue.copy();
        }

        DateValueAdapter valueAdapter = new DateValueAdapter(currentValue);
        initValueAdapter(currentValue, valueAdapter);
        
        return valueAdapter;
    }
    
    @Override
    protected TextPresModel createPresModel(RendererHandler rendererHandler,
            BasicResponse response, IValue value,
            String validationPrefix) {
        return rendererHandler.createDatePresModel(this, response, 
                (IDateValue) value, validationPrefix);
    }
}
