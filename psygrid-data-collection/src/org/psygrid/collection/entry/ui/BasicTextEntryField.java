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

import java.util.List;

import javax.swing.JTextField;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;

import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.value.ValueModel;
import org.psygrid.data.model.hibernate.Unit;

public class BasicTextEntryField extends AbstractBasicEntryField {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public BasicTextEntryField(ValueModel textModel, ValueModel unitModel,
            List<Unit> units) {
        super(textModel, unitModel, units);
    }

    public BasicTextEntryField(ValueModel textModel) {
        super(textModel);
    }

    @Override
    protected JTextComponent createTextComponent() {
        JTextField textField = new JTextField();
        if (getTextModel() != null) {
            Bindings.bind(textField, getTextModel(), false);
        }
        return textField;
    }
    
    @Override
    public void setTextModel(ValueModel textModel) {
        super.setTextModel(textModel);
        if (textModel == null) {
            getTextComponent().setDocument(new PlainDocument());
        }
        else {
            Bindings.bind((JTextField) getTextComponent(), textModel);
        }
    }
}
