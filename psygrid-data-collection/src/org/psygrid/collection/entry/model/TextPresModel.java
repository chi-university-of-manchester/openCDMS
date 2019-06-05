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

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.psygrid.collection.entry.action.NoAnswerAction;
import org.psygrid.collection.entry.renderer.RendererHelper;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.model.IValue;

import com.jgoodies.binding.value.ValueModel;

public class TextPresModel extends StandardPresModel    {

    private static final long serialVersionUID = 1L;

    private ValueModel displayTextModel;
    private final List<Action> noAnswerActions = new ArrayList<Action>(3);
    
    public TextPresModel(BasicResponse response, IValue bean,
            SectionPresModel sectionOccPresModel, String validationPrefix,
            DocumentInstance docInstance) {
        super(response, bean, sectionOccPresModel, validationPrefix, docInstance);
    }
    
    public final ValueModel getDisplayTextModel() {
        if (displayTextModel == null) {
            displayTextModel = getModel("displayText"); //$NON-NLS-1$
        }
        return displayTextModel;
    }
    
    public final void addAction(Action action) {
        noAnswerActions.add(action);
    }
    
    public final boolean removeAction(Action action) {
        return noAnswerActions.remove(action);
    }
    
    private NoAnswerAction getNoAnswerAction(StandardCode stdCode) {
        for (Action action : noAnswerActions) {
            if (action instanceof NoAnswerAction) {
                NoAnswerAction noAnswerAction = (NoAnswerAction) action;
                if (noAnswerAction.getStandardCode().equals(stdCode)) {
                    return noAnswerAction;
                }
            }
        }
        return null;
    }
    
    @Override
    public void setStandardCode(StandardCode stdCode) {
    	if ( this.getEntry().isDisableStandardCodes() ){
    		//skip if entry does not allow standard codes
    		return;
    	}
    	NoAnswerAction action = getNoAnswerAction(stdCode);
        if (action == null) {
            throw new IllegalArgumentException("Invalid stdCode: " + stdCode); //$NON-NLS-1$
        }
        action.actionPerformed(new ActionEvent(this,
                ActionEvent.ACTION_PERFORMED, "")); //$NON-NLS-1$
    }

	@Override
	public void reset() {
		super.reset();
		getDisplayTextModel().setValue(null);
		
		Entry entry = getResponse().getEntry();
		if ( entry instanceof NumericEntry){
			NumericEntry numericEntry = (NumericEntry)entry;
			Double defaultValue = numericEntry.getDefaultValue();
			if ( null != defaultValue ){
				getDisplayTextModel().setValue(RendererHelper.getInstance().getDoubleAsStringWithoutTrailingZero(defaultValue));
				getValueModel().setValue(defaultValue);
			}
		}
		if ( entry instanceof IntegerEntry){
			IntegerEntry integerEntry = (IntegerEntry)entry;
			Integer defaultValue = integerEntry.getDefaultValue();
			if ( null != defaultValue ){
				getDisplayTextModel().setValue(defaultValue.toString());
				getValueModel().setValue(defaultValue);
			}
		}
	}
    
    
    
}
