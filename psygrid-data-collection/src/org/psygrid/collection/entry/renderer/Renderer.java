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
import java.util.Collections;
import java.util.List;

import javax.swing.JComponent;

public class Renderer {

    private JComponent label;
    protected List<JComponent> components;
    
    public Renderer(List<JComponent> components) {
        this.components = components;
    }
    
    public Renderer(JComponent label) {
        this();
        setLabel(label);
    }
    
    public Renderer() {
        components = new ArrayList<JComponent>(1);
    }

    public List<JComponent> getComponents() {
        return Collections.unmodifiableList(components);
    }
    
    public void addComponents(List<JComponent> comps) {
        for (JComponent comp : comps) {
            components.add(comp);
        }
    }
    
    
    public JComponent getLabel() {
        return label;
    }
    
    public void setLabel(JComponent label) {
        if (label == null) {
            components.remove(this.label);
        }
        else {
            components.add(label);
        }
        this.label = label;
    }
}