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
import java.util.List;

import javax.swing.event.ListDataEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.collection.entry.renderer.RendererHelper;
import org.psygrid.data.model.hibernate.Option;
import org.psygrid.data.model.hibernate.OptionEntry;
import org.psygrid.data.model.hibernate.StandardCode;

import com.jgoodies.binding.value.ValueModel;

public class OptionComboBoxModel extends EntryComboBoxModel {
    
    private static final Log LOG = LogFactory.getLog(OptionComboBoxModel.class);
    
    private ValueModel optionModel;
    private ValueModel standardCodeModel;
    private List<StandardCode> standardCodes;

    private OptionEntry optionEntry;
    
    public OptionComboBoxModel(ValueModel optionModel, OptionEntry entry,
            List<StandardCode> standardCodes, ValueModel standardCodeModel) {
        this.standardCodeModel = standardCodeModel;
        this.optionModel = optionModel;
        this.standardCodes = standardCodes;
        this.optionEntry = entry;
        initEventHandling();
    }
    
    private void initEventHandling() {
        PropertyChangeListener listener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                fireListDataChanged(new ListDataEvent(OptionComboBoxModel.this,
                        ListDataEvent.CONTENTS_CHANGED, 0, 0));
            }
        };
        optionModel.addValueChangeListener(listener);
        standardCodeModel.addValueChangeListener(listener);
        
    }

    public void setOptionModel(ValueModel optionModel) {
        this.optionModel = optionModel;
    }
    
    public void setStandardCodeModel(ValueModel standardCodeModel) {
        this.standardCodeModel = standardCodeModel;
    }

    protected OptionEntry getOptionEntry() {
        return optionEntry;
    }
    
    public void setSelectedItem(Object anItem) {
        String selectedString = (String) anItem;

        for (int i = 0, c = optionEntry.numOptions(); i < c; ++i) {
            Option option = optionEntry.getOption(i);
            if (getElementAt(i).equals(selectedString)) {
                optionModel.setValue(option);
                standardCodeModel.setValue(null);
                return;
            }
        }

        for (int i = 0, c = standardCodes.size(), d = optionEntry.numOptions(); 
                i < c; ++i) {
            StandardCode code = standardCodes.get(i);
            if (getElementAt(i + d).equals(selectedString)) {
                optionModel.setValue(null);
                standardCodeModel.setValue(code);
                return;
            }
        }
    }

    public String getSelectedItem() {
        if (optionModel.getValue() != null) {
            String optionText = RendererHelper.getInstance().getOptionText(optionEntry,
                    (Option) optionModel.getValue());
            if (LOG.isDebugEnabled()) {
                LOG.debug("Selected item in combo box: " + optionText); //$NON-NLS-1$
            }
            return optionText;
        }
        
        if (standardCodeModel.getValue() != null) {
            return RendererHelper.getInstance().getStandardCodeText(
                    (StandardCode) standardCodeModel.getValue());
        }
        
        return null;
    }

    public int getSize() {
    	if (optionEntry.isDisableStandardCodes()) {
    		return optionEntry.numOptions();
    	}
    	return optionEntry.numOptions() + standardCodes.size();
    }

    public String getElementAt(int index) {
        if (index < optionEntry.numOptions()) {
            Option option = optionEntry.getOption(index);
            return RendererHelper.getInstance().getOptionText(optionEntry, option);
        }
        
        StandardCode code = standardCodes.get(index - optionEntry.numOptions());
        return RendererHelper.getInstance().getStandardCodeText(code);
        
    }
}
