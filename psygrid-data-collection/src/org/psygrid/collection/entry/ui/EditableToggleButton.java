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

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.MouseListener;

import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.JToggleButton;

import com.jgoodies.validation.view.ValidationComponentUtils;

public class EditableToggleButton extends AbstractEditable   {

    private static final long serialVersionUID = 1L;
    
    private JToggleButton toggleButton;
    
    private JComponent backgroundComponent;
    
    public EditableToggleButton(JToggleButton toggleButton) {
        this.toggleButton = toggleButton;
        this.toggleButton.setOpaque(true);
        add(toggleButton);
    }
    
    @Override
    public void addMouseListener(MouseListener listener) {
        toggleButton.addMouseListener(listener);
        super.addMouseListener(listener);
    }
    
    @Override
    public void removeMouseListener(MouseListener listener) {
        toggleButton.removeMouseListener(listener);
        super.removeMouseListener(listener);
    }
    
    @Override
    public void setEnabled(boolean b, boolean isStandardCode) {
        toggleButton.setEnabled(b);
        super.setEnabled(b);
    }
    
    @Override
    public void doLayout() {
        Insets insets = getInsets();
        
        int width = getWidth() - insets.left - insets.right;
        int height = getHeight() - insets.top - insets.bottom;

        toggleButton.setBounds(insets.left, insets.top, width, height);
    }
    
    @Override
    public Dimension getPreferredSize() {
        Dimension dim = toggleButton.getPreferredSize();
        Insets insets = getInsets();
        dim.width += insets.left + insets.right;
        dim.height += insets.top + insets.bottom;
        return dim;
    }

    @Override
    public void setEditable(boolean b) {
        if (b) {
            toggleButton.setEnabled(isEnabled());
        }
        else {
            toggleButton.setEnabled(false);
        }
        super.setEditable(b);
    }
    
    @Override
	public void setMandatory(boolean b) {
        if (b) {
        	toggleButton.setBackground(ValidationComponentUtils.getMandatoryBackground());
        	if ( null != getBackgroundComponent() ){
        		getBackgroundComponent().setBackground(ValidationComponentUtils.getMandatoryBackground());
        	}
        }
        else {
    		toggleButton.setBackground(null);
        	if ( null != getBackgroundComponent() ){
        		getBackgroundComponent().setBackground(null);
        	}
        }
		super.setMandatory(b);
	}

	public ButtonModel getModel() {
        return toggleButton.getModel();
    }

    public boolean isSelected() {
        return toggleButton.isSelected();
    }

    public void setModel(ButtonModel model) {
        toggleButton.setModel(model);
        
    }

    public String getText() {
        return toggleButton.getText();
    }

    public void doClick() {
        toggleButton.doClick();
    }

	public JComponent getBackgroundComponent() {
		return backgroundComponent;
	}

	public void setBackgroundComponent(JComponent backgroundComponent) {
		this.backgroundComponent = backgroundComponent;
	}
}