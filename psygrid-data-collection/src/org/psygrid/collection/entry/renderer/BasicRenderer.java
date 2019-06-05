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

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.psygrid.collection.entry.model.BasicPresModel;

public class BasicRenderer<T extends BasicPresModel> extends PresModelRenderer<T> {
    
    private JComponent field;
    public BasicRenderer(List<JComponent> components, T model) {
        super(components, model);
    }
    
    public BasicRenderer(JComponent label, JLabel validationLabel, 
            JComponent field, T model) {
        super(model);
        components = new ArrayList<JComponent>();
        setLabel(label);
        setValidationLabel(validationLabel);
        setHelpLabel();
        setRestrictedLabel();
        setField(field);
    }

    public JComponent getField() {
        return field;
    }
    
    public void setField(JComponent field) {
        if (field == null) {
            components.remove(this.field);
        }
        else {
            components.add(field);
        }
        this.field = field;
    }
}
