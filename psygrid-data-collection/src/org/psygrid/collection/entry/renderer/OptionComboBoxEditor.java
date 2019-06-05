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

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;

import javax.swing.ComboBoxEditor;

import org.psygrid.collection.entry.event.HasKeyListener;
import org.psygrid.collection.entry.model.OptionEditableComboBoxModel;
import org.psygrid.collection.entry.ui.DualTextField;

import com.jgoodies.binding.adapter.Bindings;

public class OptionComboBoxEditor implements ComboBoxEditor, HasKeyListener {

    private DualTextField field;
    private OptionEditableComboBoxModel model;

    public OptionComboBoxEditor(OptionEditableComboBoxModel model) {
        this.model = model;
        init();
    }
    
    private void init() {
        field = new DualTextField();
        field.getRightField().setEditable(false);
        Bindings.bind(field.getRightField(), model.getTextValueModel(), false);
        field.getLeftField().setEditable(false);
        field.setSeparator(":"); //$NON-NLS-1$
    }
    
    public Component getEditorComponent() {
        return field;
    }

    public void setItem(Object anObject) {
        if (anObject == null) {
            field.setRightFieldText(""); //$NON-NLS-1$
            field.setLeftFieldText(""); //$NON-NLS-1$
            return;
        }
        String objectString = anObject.toString();
        if (model.optionAllowsText(objectString)) {
            field.setLeftFieldText(objectString);
            field.setRightFieldActive(true);
            field.getRightField().setEditable(true);
            field.getLeftField().requestFocusInWindow();
            field.revalidate();
            field.repaint();
            return;
        }
        field.setLeftFieldText(objectString);
        field.setRightFieldText(""); //$NON-NLS-1$
        field.getRightField().setEditable(false);
        field.setRightFieldActive(false);
        field.getLeftField().requestFocusInWindow();
        field.revalidate();
        field.repaint();
    }

    public Object getItem() {
        return field.getLeftFieldText();
    }

    public void selectAll() {
        field.getLeftField().selectAll();
    }
    
    /**
     * Adds <code>listener</code> to the left field only.
     * @param listener
     */
    public void addKeyListener(KeyListener listener) {
        field.getLeftField().addKeyListener(listener);
    }
    
    /**
     * Removes <code>listener</code> from the left field only.
     * @param listener
     */
    public void removeKeyListener(KeyListener listener) {
        field.getLeftField().removeKeyListener(listener);
    }

    /**
     * Empty implementation at the moment.
     */
    public void addActionListener(ActionListener l) {
        // As per javadoc
    }

    /**
     * Empty implementation at the moment.
     */
    public void removeActionListener(ActionListener l) {
        // As per javadoc
    }
}
