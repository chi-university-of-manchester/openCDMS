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

import org.psygrid.data.model.IOptionValue;

import com.jgoodies.binding.value.ValueModel;
import org.psygrid.data.model.hibernate.BasicResponse;
import org.psygrid.data.model.hibernate.DocumentInstance;
import org.psygrid.data.model.hibernate.OptionEntry;

public class OptionPresModel extends StandardPresModel {

    private static final long serialVersionUID = 1L;

    private ValueModel textValueModel;
    
    private boolean ignoreDocInstanceStatus = false;
    
    public OptionPresModel(BasicResponse response,
            IOptionValue bean, SectionPresModel sectionOccPresModel,
            String validationPrefix, DocumentInstance docInstance) {
        super(response, bean, sectionOccPresModel, validationPrefix, docInstance);
        if (response.getEntry() instanceof OptionEntry == false) {
            throw new IllegalArgumentException("response#getEntry() must return " + //$NON-NLS-1$
                    "an object of type IOptionEntry, but it returns: " +  //$NON-NLS-1$
                    response.getEntry().getClass());
        }
    }
    
    public final ValueModel getTextValueModel() {
        if (textValueModel == null) {
            textValueModel = getModel("textValue"); //$NON-NLS-1$
        }
        
        return textValueModel;
    }

    public final boolean ignoreDocInstanceStatus() {
        return ignoreDocInstanceStatus;
    }
    
    public final void setIgnoreDocInstanceStatus(boolean value) {
        this.ignoreDocInstanceStatus = value;
    }

	public void reset() {
		super.reset();
		getTextValueModel().setValue(null);

		OptionEntry entry = (OptionEntry)getResponse().getEntry();
		if ( null != entry.getDefaultValue() ){
			getValueModel().setValue(entry.getDefaultValue());
		}
	}
    
}
