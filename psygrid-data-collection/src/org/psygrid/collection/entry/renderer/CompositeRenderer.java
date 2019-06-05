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

import org.psygrid.collection.entry.model.CompositePresModel;

public class CompositeRenderer<T extends CompositePresModel> extends PresModelRenderer<T>    {
    
    private JComponent composite;
    
    public CompositeRenderer(List<JComponent> components, 
            T presModel) {
        super(components, presModel);
    }
    
    public CompositeRenderer(JLabel validationLabel, JComponent label, 
            JComponent composite, T presModel) {
        super(presModel);
        components = new ArrayList<JComponent>(4);
        setValidationLabel(validationLabel);
        setHelpLabel();
        setLabel(label);
        setComposite(composite);
    }
    
    public JComponent getComposite() {
        return composite;
    }
    
    public void setComposite(JComponent composite) {
        if (composite == null) {
            components.remove(this.composite);
        }
        else {
            components.add(composite);
        }
        this.composite = composite;
    }
}
