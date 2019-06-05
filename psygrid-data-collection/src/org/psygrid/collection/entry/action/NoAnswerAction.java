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


package org.psygrid.collection.entry.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.psygrid.collection.entry.renderer.RendererHelper;
import org.psygrid.data.model.hibernate.StandardCode;

import com.jgoodies.binding.value.ValueModel;

public class NoAnswerAction extends AbstractAction  {

    private static final long serialVersionUID = 1L;
    
    private final StandardCode standardCode;
    
    private final ValueModel displayTextModel;
    private final ValueModel stdCodeModel;
    private final Action enableEditingAction;
    
    public NoAnswerAction(StandardCode standardCode, ValueModel displayTextModel,
            ValueModel stdCodeModel, Action enableEditingAction) {
        this.standardCode = standardCode;
        this.stdCodeModel = stdCodeModel;
        this.enableEditingAction = enableEditingAction;
        this.displayTextModel = displayTextModel;
        String name = RendererHelper.getInstance().getStandardCodeText(standardCode);
        putValue(Action.NAME, name);
    }
    
    public void actionPerformed(ActionEvent e) {
        stdCodeModel.setValue(standardCode);
        displayTextModel.setValue(getValue(Action.NAME));
        enableEditingAction.setEnabled(true);
    }
    
    public final StandardCode getStandardCode() {
        return standardCode;
    }
}
