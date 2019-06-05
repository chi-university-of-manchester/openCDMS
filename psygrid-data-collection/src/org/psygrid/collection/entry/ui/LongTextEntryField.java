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

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.JTextComponent;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.value.ValueModel;

public class LongTextEntryField extends AbstractEntryField {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public LongTextEntryField(ValueModel textModel, ValueModel standardCodeModel) {
        super(textModel, null, null, standardCodeModel);
        JTextArea textComp = (JTextArea) getTextComponent();
        textComp.setLineWrap(true);
        textComp.setWrapStyleWord(true);
    }

    @Override
    protected JTextComponent createTextComponent() {
        return BasicComponentFactory.createTextArea(getTextModel(), false);
    }
    
    @Override
    protected JComponent decorateTextComponent(JTextComponent textComp) {
        JScrollPane scrollPane = new JScrollPane(textComp,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        return scrollPane;
    }
    
    public void setColumns(int columns) {
       ((JTextArea) getTextComponent()).setColumns(columns);
    }
    
    public void setRows(int rows) {
        ((JTextArea) getTextComponent()).setRows(rows);
    }
    
    public int getColumns() {
        return ((JTextArea) getTextComponent()).getColumns();
    }
    
    public int getRows() {
        return ((JTextArea) getTextComponent()).getRows();
    }
    
    @Override
    public void setTextModel(ValueModel textModel) {
        super.setTextModel(textModel);
        Bindings.bind((JTextArea) getTextComponent(), textModel, false);
    }
}
