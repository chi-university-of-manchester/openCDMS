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
import javax.swing.JTextField;

import org.psygrid.collection.entry.event.HasKeyListener;

public class OptionComboBoxRenderer implements ComboBoxEditor, HasKeyListener   {

    private JTextField editor;
    public OptionComboBoxRenderer() {
        editor = new JTextField();
        editor.setBorder(null);
        editor.setEditable(false);
    }

    public Component getEditorComponent() {
        return editor;
    }
    
    public void setItem(final Object anObject) {
        String text = anObject == null ? "" : anObject.toString(); //$NON-NLS-1$
        editor.setText(text);
    }

    public Object getItem() {
        return editor.getText();
    }

    public void selectAll() {
        editor.selectAll();
    }
    
    public void addKeyListener(KeyListener listener) {
        editor.addKeyListener(listener);
    }
    
    public void removeKeyListener(KeyListener listener) {
        editor.removeKeyListener(listener);
    }

    public void addActionListener(ActionListener l) {
        editor.addActionListener(l);
    }

    public void removeActionListener(ActionListener l) {
        editor.removeActionListener(l);
    }
}
