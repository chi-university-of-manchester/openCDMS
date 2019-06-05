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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JToggleButton;

import org.psygrid.data.model.hibernate.StandardCode;

import com.jgoodies.binding.BindingUtils;
import com.jgoodies.binding.value.ValueModel;

public class OptionRadioButtonModel extends JToggleButton.ToggleButtonModel  {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private ValueModel optionModel;
    private ValueModel standardCodeModel;
    private final Object choice;
    
    /**
     * 
     * @param optionModel
     * @param standardCodeModel
     * @param choice Either a StandardCode or a IOption
     */
    public OptionRadioButtonModel(ValueModel optionModel,
            ValueModel standardCodeModel, Object choice) {
        this.optionModel = optionModel;
        this.standardCodeModel = standardCodeModel;
        this.choice = choice;
        
        // Safe not to remove
        optionModel.addValueChangeListener(new ValueChangeHandler());
        
        // Safe not to remove
        standardCodeModel.addValueChangeListener(new ValueChangeHandler());
        updateSelectedState();
    }
    
    public void setOptionModel(ValueModel optionModel) {
        this.optionModel = optionModel;
        optionModel.addValueChangeListener(new ValueChangeHandler());
        updateSelectedState();
    }
    
    public void setStandardCodeModel(ValueModel standardCodeModel) {
        this.standardCodeModel = standardCodeModel;
        standardCodeModel.addValueChangeListener(new ValueChangeHandler());
        updateSelectedState();
    }
    
    @Override
    public void setSelected(boolean b) {
        if (b == isSelected()) {
            return;
        }
        if (!b) {
            standardCodeModel.setValue(null);
            optionModel.setValue(null);
            return;
        }
        
        if (choice instanceof StandardCode) {
            standardCodeModel.setValue(choice);
            optionModel.setValue(null);
            return;
        }
        standardCodeModel.setValue(null);      
        optionModel.setValue(choice);

    }
    
    private void updateSelectedState() {
        boolean holdsChoice;
        if (choice instanceof StandardCode) {
            holdsChoice = BindingUtils.equals(choice, standardCodeModel.getValue());
        }
        else {
            holdsChoice = BindingUtils.equals(choice, optionModel.getValue());
        }
        super.setSelected(holdsChoice);
    }
    
    private class ValueChangeHandler implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            updateSelectedState();
        }
    }
}
