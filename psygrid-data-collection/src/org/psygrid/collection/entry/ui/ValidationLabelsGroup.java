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


package org.psygrid.collection.entry.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;

import com.jgoodies.validation.view.ValidationResultViewFactory;

public class ValidationLabelsGroup extends JComponent   {

    private static final Dimension MINIMUM_DIMENSION = 
        new JLabel(ValidationResultViewFactory.getErrorIcon()).getPreferredSize();
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public ValidationLabelsGroup() {
        // Empty constructor
    }
    
    public void addLabel(JLabel label) {
        add(label);
    }
    
    public void removeLabel(JLabel label) {
        remove(label);
    }
    
    private JLabel getVisibleLabel() {
        Component[] comps = getComponents();
        for (Component comp : comps) {
            JLabel label = (JLabel) comp;
            if (label.getIcon() != null) {
                return label;
            }
        }
        return (JLabel) comps[0];
    }
    
    public Icon getIcon() {
        return getVisibleLabel().getIcon();
    }
    
    @Override
    public Dimension getPreferredSize() {
        if (getComponents().length < 1) {
            return new Dimension(MINIMUM_DIMENSION.width, MINIMUM_DIMENSION.height);
        }
        Dimension preferredSize = getVisibleLabel().getPreferredSize();
        int width = Math.max(MINIMUM_DIMENSION.width, preferredSize.width);
        int height = Math.max(MINIMUM_DIMENSION.height, preferredSize.height);
        return new Dimension(width, height);
    }
    
    @Override
    public void doLayout() {
        if (getComponents().length < 1) {
            return;
        }
        
        Insets insets = getInsets();

        int width = getWidth() - insets.left - insets.right;
        int height = getHeight() - insets.top - insets.bottom;
        
        getVisibleLabel().setBounds(insets.left, insets.top, width, height);
    }
    
    @Override
    public void setEnabled(boolean b) {
        if (isEnabled() == b) {
            return;
        }
        for (Component comp : getComponents()) {
            comp.setEnabled(b);
        }
        super.setEnabled(b);
    }
}
