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


package org.psygrid.collection.entry.model;

import org.psygrid.data.model.IValue;
import org.psygrid.data.model.hibernate.BasicResponse;
import org.psygrid.data.model.hibernate.DocumentInstance;
import org.psygrid.data.model.hibernate.StandardCode;

import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.validation.ValidationResult;

public class StandardPresModel extends BasicPresModel {

    private static final long serialVersionUID = 1L;

    private ValueModel standardCodeModel;
    
    public StandardPresModel(BasicResponse response,
            IValue bean, SectionPresModel sectionOccPresModel, 
            String validationPrefix, DocumentInstance docInstance) {
        super(response, bean, sectionOccPresModel, validationPrefix, docInstance);
    }
    
    public final ValueModel getStandardCodeModel() {
        if (standardCodeModel == null) {
            standardCodeModel = getModel("standardCode"); //$NON-NLS-1$
        }
        return standardCodeModel;
    }
    
    public void setStandardCode(StandardCode stdCode) {
    	if ( getEntry().isDisableStandardCodes() ){
    		return;
    	}
        getStandardCodeModel().setValue(stdCode);
        getValueModel().setValue(null);
    }
    
    @Override 
    public ValidationResult validate(boolean partial) {
        ValidationResult result = preValidate();
        
        if (result.hasMessages()) {
            return result;
        }
        
        if (getStandardCodeModel().getValue() != null) {
            return result;
        }
        
        return super.validate(partial);
    }

	@Override
	public void reset() {
		super.reset();
		getStandardCodeModel().setValue(null);
	}
    
    
}
