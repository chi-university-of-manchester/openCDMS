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


package org.psygrid.collection.entry.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.psygrid.collection.entry.renderer.RendererHelper;
import org.psygrid.data.model.hibernate.Option;
import org.psygrid.data.model.hibernate.OptionEntry;
import org.psygrid.data.model.hibernate.StandardCode;

import com.jgoodies.binding.value.ValueModel;

public class OptionEditableComboBoxModel extends OptionComboBoxModel {

    private ValueModel textValueModel;
    private Set<String> optionsWithText = new HashSet<String>();

    public OptionEditableComboBoxModel(ValueModel optionModel, OptionEntry entry,
            ValueModel textValueModel, List<StandardCode> standardCodes, 
            ValueModel standardCodeModel) {
        super(optionModel, entry, standardCodes, standardCodeModel);
        this.textValueModel = textValueModel;
        processOptions();
    }
    
    public boolean optionAllowsText(String optionText) {
        return optionsWithText.contains(optionText);
    }
    
    private void processOptions() {
        for (int i = 0, c = getOptionEntry().numOptions(); i < c; ++i) {
            Option option = getOptionEntry().getOption(i);
            if (option.isTextEntryAllowed()) {
                optionsWithText.add(RendererHelper.getInstance().getOptionText(getOptionEntry(),
                        option));
            }
        }
    }

    public ValueModel getTextValueModel() {
        return textValueModel;
    }
    
    public void setTextValueModel(ValueModel textValueModel) {
        this.textValueModel = textValueModel;
    }

    public String getSelectedItemFullText() {
        String selectedItem = super.getSelectedItem();
        if (optionAllowsText(selectedItem)) {
            String textValue = (String) (textValueModel.getValue() == null ? "" : //$NON-NLS-1$
                textValueModel.getValue());
            selectedItem = selectedItem + Messages.getString("OptionEditableComboBoxModel.textSeparator") + textValue;
        }
        return selectedItem;
    }
}
