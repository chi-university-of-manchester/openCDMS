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

import javax.swing.JPopupMenu;

import org.psygrid.collection.entry.model.BasicPresModel;
import org.psygrid.collection.entry.model.StandardPresModel;
import org.psygrid.collection.entry.model.TextPresModel;
import org.psygrid.collection.entry.ui.EntryComponent;
import org.psygrid.collection.entry.ui.EntryWithButton;
import org.psygrid.collection.entry.ui.AbstractEntryField;
import org.psygrid.collection.entry.ui.TextEntryField;
import org.psygrid.data.model.IValue;
import org.psygrid.data.model.hibernate.BasicResponse;
import org.psygrid.data.model.hibernate.StandardCode;
import org.psygrid.data.model.hibernate.Unit;

public abstract class AbstractRendererSPI extends AbstractBasicRendererSPI {

	/**
	 *
	 */ //TODO comment
	public static final String HIDDEN_VALUE = "*****";
	
    protected void postFieldCreation(BasicPresModel model,
            List<StandardCode> stdCodes, EntryComponent field, boolean disableStandardCodes) {
        if (field instanceof EntryWithButton == false) {
            throw new IllegalStateException("field is not a EntryWithButton." + //$NON-NLS-1$
                    " It is a " + field.getClass() + "." + //$NON-NLS-1$ //$NON-NLS-2$
                    " AbstractBasicEntryRenderer should be subclassed instead."); //$NON-NLS-1$
        }
        TextPresModel presModel = (TextPresModel) model;
        final EntryWithButton entryField = (EntryWithButton) field;

        //only show standard code popup if standard codes are not disabled
        if (disableStandardCodes) {
        	if (entryField instanceof AbstractEntryField) {
        		((AbstractEntryField)entryField).removePopupButton();
        	} else {
                JPopupMenu popup = getPopupMenu(stdCodes, presModel, field, disableStandardCodes);
            	entryField.setPopup(popup);
        	}
        } else {
            JPopupMenu popup = getPopupMenu(stdCodes, presModel, field, disableStandardCodes);
        	entryField.setPopup(popup);
    	}
        
        RendererHelper.getInstance().addAbstractRendererValueChangeListener(entryField, 
                presModel);
    }
    
    @Override
    public void done(BasicRenderer<?> renderer) {
        StandardPresModel presModel = (StandardPresModel) renderer.getPresModel();
        StandardCode stdCode = 
            (StandardCode) presModel.getStandardCodeModel().getValue();
        if (stdCode == null) {
            return;
        }
        presModel.setStandardCode(stdCode);
    }
    
    protected JPopupMenu getPopupMenu(List<StandardCode> stdCodes, 
            TextPresModel presModel, EntryComponent field, boolean disableStandardCodes) {
    	return RendererHelper.getInstance().getNoAnswerJPopupMenu(stdCodes, presModel, disableStandardCodes);
    }
    
    @Override
    protected EntryComponent createField(BasicPresModel model,
            List<Unit> units, List<StandardCode> stdCodes, boolean disableStandardCodes) {
        TextPresModel textPresModel = (TextPresModel) model;
        TextEntryField field =  new TextEntryField(
                textPresModel.getDisplayTextModel(), model.getUnitModel(), units,
                textPresModel.getStandardCodeModel());
        postFieldCreation(model, stdCodes, field, disableStandardCodes);
        return field;
    }
    
    @Override
    protected TextPresModel createPresModel(RendererHandler rendererHandler,
            BasicResponse response, IValue value,
            String validationPrefix) {
        return rendererHandler.createTextPresModel(this, response, value,
                validationPrefix);
    }
}
