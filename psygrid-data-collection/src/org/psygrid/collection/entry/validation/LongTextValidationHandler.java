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
import java.io.UnsupportedEncodingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.collection.entry.model.TextPresModel;

import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.validation.ValidationResult;

public class LongTextValidationHandler implements PropertyChangeListener {

	private static final Log LOG = LogFactory.getLog(LongTextValidationHandler.class);
	
    private static final int MAX_CHARS = 32768;
    
    private ValidationResult result;
    private TextPresModel model;
    private ValueModel valueModel;
    private ValueModel stdCodeModel;
    private int validationResultLocation = -1;

    public LongTextValidationHandler(TextPresModel model) {
        this.model = model;
        this.valueModel = model.getValueModel();
        this.stdCodeModel = model.getStandardCodeModel();
        result = new ValidationResult();
        result.addError(Messages.getString("LongTextValidationHandler.tooLong")); //$NON-NLS-1$
    }

    public void propertyChange(PropertyChangeEvent evt) {
        String newValue = null;
        if (model.getTransformedModel().getValue().equals(Boolean.TRUE)) {
            return;
        }
        try {
            if ( null != stdCodeModel.getValue() ){
            	valueModel.setValue(null);
            }
            if (stdCodeModel.getValue() == null && evt.getNewValue() != null
                    && (!evt.getNewValue().equals(""))) { //$NON-NLS-1$
                //check for overlength string
            	try{
	                if ( ((String)evt.getNewValue()).getBytes("UTF-8").length > MAX_CHARS ){
	                    throw new StringTooLongException();
	                }
            	}
            	catch(UnsupportedEncodingException ex){
            		//should never happen as encoding is hardcoded! But if ti does, log and
            		//fall back to string length check
            		LOG.error("Invalid charset!", ex);
	                if ( ((String)evt.getNewValue()).length() > MAX_CHARS ){
	                    throw new StringTooLongException();
	                }
            	}
                newValue = (String) evt.getNewValue();
            }
            if (validationResultLocation != -1) {
                model.removeValidationResult(validationResultLocation);
                validationResultLocation = -1;
            }
        }
        catch(StringTooLongException stle) {
            if (validationResultLocation == -1) {
                validationResultLocation = model.addValidationResult(result);
            }
        }
        valueModel.setValue(newValue);
    }

    private class StringTooLongException extends Exception {

        private static final long serialVersionUID = 1L;

        public StringTooLongException() {
            super();
        }

        public StringTooLongException(String message, Throwable cause) {
            super(message, cause);
        }

        public StringTooLongException(String message) {
            super(message);
        }

        public StringTooLongException(Throwable cause) {
            super(cause);
        }
        
    }
    
}
