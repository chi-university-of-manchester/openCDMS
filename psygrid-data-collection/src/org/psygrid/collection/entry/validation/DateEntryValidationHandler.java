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


package org.psygrid.collection.entry.validation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import org.psygrid.collection.entry.model.BasicPresModel;
import org.psygrid.collection.entry.model.TextPresModel;
import org.psygrid.collection.entry.renderer.RendererHelper;

import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.validation.ValidationResult;

public class DateEntryValidationHandler implements PropertyChangeListener {

    private ValidationResult result;

    private BasicPresModel model;

    private ValueModel valueModel;
    private ValueModel stdCodeModel;
    private int validationResultLocation = -1;

    private DateFormat formatter;

    public DateEntryValidationHandler(TextPresModel model) {
        this.model = model;
        this.valueModel = model.getValueModel();
        this.formatter = RendererHelper.getInstance().getDateFormat();
        this.stdCodeModel = model.getStandardCodeModel();
        result = new ValidationResult();
        result.addError(Messages.getString("DateEntryValidationHandler.valueNotDate")); //$NON-NLS-1$
    }

    public void propertyChange(PropertyChangeEvent evt) {
        Date newValue = null;
        Object evtNewValue = evt.getNewValue();
        try {
            if ( null != stdCodeModel.getValue() ){
                valueModel.setValue(null);
            }
            if (stdCodeModel.getValue() == null && evtNewValue != null
                    && (!evtNewValue.equals(""))) { //$NON-NLS-1$
                
                String evtNewText = (String) evtNewValue;
                checkDateElementsLength(evtNewText);
                newValue = formatter.parse(evtNewText);   
            }
            
            if (validationResultLocation != -1) {
                model.removeValidationResult(validationResultLocation);
                validationResultLocation = -1;
            }

        } catch (ParseException pe) {
            if (validationResultLocation == -1) {
                validationResultLocation = model.addValidationResult(result);
            }
        }
        valueModel.setValue(newValue);
    }

    private void checkDateElementsLength(String dateText) throws ParseException {
        int indexOfFirstDash = dateText.indexOf('-');
        if (indexOfFirstDash > -1) {
            int indexOfSecondDash = dateText.indexOf('-', indexOfFirstDash + 1);
            if (indexOfSecondDash > -1) {
                checkDateElementsLength(dateText, indexOfFirstDash, indexOfSecondDash);
            }
        }
    }
    
    private void checkDateElementsLength(String dateText, int indexOfFirstDash,
            int indexOfSecondDash) throws ParseException {
        
        String dayText = dateText.substring(0, indexOfFirstDash);
        if (dayText.length() != 2) {
            throw new ParseException("Day has the wrong length", 0); //$NON-NLS-1$
        }
        String monthText = dateText.substring(indexOfFirstDash + 1, indexOfSecondDash);
        if (monthText.length() != 3) {
            throw new ParseException("Month has the wrong length", indexOfFirstDash); //$NON-NLS-1$
        }
        String yearText = dateText.substring(indexOfSecondDash + 1, dateText.length());
        if (yearText.length() != 4) {
            throw new ParseException("Year has the wrong length", indexOfSecondDash); //$NON-NLS-1$
        }
    }
}
