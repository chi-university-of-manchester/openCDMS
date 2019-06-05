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
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPopupMenu;

import org.psygrid.collection.entry.TogglePopupButtonTrigger;
import org.psygrid.collection.entry.renderer.RendererHelper;

import com.jgoodies.binding.value.ValueModel;
import org.psygrid.data.model.hibernate.Unit;

public abstract class AbstractEntryField extends AbstractBasicEntryField
        implements EntryWithButton  {

    private JPopupMenu popup;

    private JButton popupButton;

    private int popupButtonWidth = 20;
    
    private MouseListener popupHandler;

    private ValueModel standardCodeModel;

    public AbstractEntryField(ValueModel textModel, ValueModel unitModel,
            List<Unit> units, ValueModel standardCodeModel) {
        super(textModel, unitModel, units);
        this.standardCodeModel = standardCodeModel;
        initComponents();
    }
    
    @Override
    public void addMouseListener(MouseListener listener) {
        popupButton.addMouseListener(listener);
        super.addMouseListener(listener);
    }
    
    @Override
    public void removeMouseListener(MouseListener listener) {
        popupButton.removeMouseListener(listener);
        super.removeMouseListener(listener);
    }
    
    public ValueModel getStandardCodeModel() {
        return standardCodeModel;
    }
    public void setStandardCodeModel(ValueModel standardCodeModel) {
        this.standardCodeModel = standardCodeModel;
    }

    private void initComponents() {
        popupButton = RendererHelper.getInstance().getNoAnswerButton();
        add(popupButton);
    }
    
    public void removePopupButton() {
    	remove(popupButton);
    	popupButtonWidth = 0;
    }
    
    public JPopupMenu getPopup() {
        return popup;
    }
    
    public void setPopup(JPopupMenu popup) {
        if (this.popup != null) {
            if (this.popup.equals(popup)) {
                return;
            }
            popupButton.removeMouseListener(popupHandler);
        }
        
        this.popup = popup;
        if (popup != null) {
            popupHandler = new TogglePopupButtonTrigger(popup);
            popupButton.addMouseListener(popupHandler);
        }
        else {
            popupHandler = null;
        }
    }

    @Override
    public void doLayout() {
        Insets insets = getInsets();

        int width = getWidth() - insets.left - insets.right;
        int height = getHeight() - insets.top - insets.bottom;

        EditableComboBox unitsCBox = getUnitsComboBox();
        
        if (unitsCBox == null) {
            getDecoratedTextComponent().setBounds(insets.left, insets.top,
                    width - popupButtonWidth, height);
            popupButton.setBounds(width - popupButtonWidth + insets.left,
                    insets.top, popupButtonWidth, height);
        } else {
            Dimension cBoxSize = unitsCBox.getPreferredSize();
            getDecoratedTextComponent().setBounds(insets.left, insets.top,
                    width - popupButtonWidth - cBoxSize.width, height);
            unitsCBox.setBounds(width - popupButtonWidth - cBoxSize.width
                    + insets.left, insets.top, cBoxSize.width, height);
            popupButton.setBounds(width - popupButtonWidth + insets.left,
                    insets.top, popupButtonWidth, height);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension dim = getDecoratedTextComponent().getPreferredSize();
        dim.width += popupButton.getPreferredSize().width;
        
        EditableComboBox unitsCBox = getUnitsComboBox();
        if (unitsCBox != null) {
            dim.width += unitsCBox.getPreferredSize().width;
        }
        Insets insets = getInsets();
        dim.width += insets.left + insets.right;
        dim.height += insets.top + insets.bottom;
        return dim;
    }

    public final JButton getPopupButton() {
        return popupButton;
    }
    
    @Override
    public void setEnabled(boolean b, boolean isStandardCode) {
        popupButton.setEnabled(b);
        super.setEnabled(b, isStandardCode);
    }
    
    public boolean getEnabled() {
    	return popupButton.isEnabled();
    }
    
    @Override
    public void setEditable(boolean b) {
        if (!b) {
            popupButton.setEnabled(false);
        }
        else {
            popupButton.setEnabled(isEnabled());
        }
        super.setEditable(b);
    }

}
