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

import org.psygrid.collection.entry.model.TextPresModel;

import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.validation.ValidationResult;

public abstract class NumericValidationHandler implements PropertyChangeListener    {

    private ValidationResult result;
    private TextPresModel model;
    private ValueModel valueModel;
    private ValueModel stdCodeModel;
    private int validationResultLocation = -1;
    public NumericValidationHandler(TextPresModel model) {
        this(model, model.getValueModel());
    }
    
    public NumericValidationHandler(TextPresModel model, 
            ValueModel valueModel) {
        this.model = model;
        this.valueModel = valueModel;
        this.stdCodeModel = model.getStandardCodeModel();
        result = new ValidationResult();
        result.addError(getErrorMessage());
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        Number newValue = null;
        if (model.getTransformedModel().getValue().equals(Boolean.TRUE)) {
            return;
        }
        try {
            if ( null != stdCodeModel.getValue() ){
                valueModel.setValue(null);
            }
            if (stdCodeModel.getValue() == null && evt.getNewValue() != null
                    && (!evt.getNewValue().equals(""))) { //$NON-NLS-1$
                newValue = convertString((String) evt.getNewValue());
            }

            if (validationResultLocation != -1) {
                model.removeValidationResult(validationResultLocation);
                validationResultLocation = -1;
            }
            
            if(newValue != null && newValue instanceof Double){
            	if(Double.isInfinite((Double)newValue) || Double.isNaN((Double)newValue)){
            		validationResultLocation = model.addValidationResult(result);
            	}
            }
        }
        catch(NumberFormatException nfe) {
            if (validationResultLocation == -1) {
                validationResultLocation = model.addValidationResult(result);
            }
        }
        valueModel.setValue(newValue);
    }
    
    protected abstract Number convertString(String text);
    
    protected String getErrorMessage() {
        return Messages.getString("NumericValidationHandler.notNumber"); //$NON-NLS-1$
    }
}
