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

import javax.swing.event.ListDataEvent;

import com.jgoodies.binding.value.ValueModel;

public class MonthsComboBoxModel extends EntryComboBoxModel    {

    private final static String[] values = new String[] {
            Messages.getString("MonthsComboBoxModel.January"),  //$NON-NLS-1$
            Messages.getString("MonthsComboBoxModel.February"),  //$NON-NLS-1$
            Messages.getString("MonthsComboBoxModel.March"),  //$NON-NLS-1$
            Messages.getString("MonthsComboBoxModel.April"),  //$NON-NLS-1$
            Messages.getString("MonthsComboBoxModel.May"),  //$NON-NLS-1$
            Messages.getString("MonthsComboBoxModel.June"),  //$NON-NLS-1$
            Messages.getString("MonthsComboBoxModel.July"), //$NON-NLS-1$
            Messages.getString("MonthsComboBoxModel.August"), //$NON-NLS-1$
            Messages.getString("MonthsComboBoxModel.September"), //$NON-NLS-1$
            Messages.getString("MonthsComboBoxModel.October"), //$NON-NLS-1$
            Messages.getString("MonthsComboBoxModel.November"), //$NON-NLS-1$
            Messages.getString("MonthsComboBoxModel.December"),  //$NON-NLS-1$
            Messages.getString("MonthsComboBoxModel.Unknown") //$NON-NLS-1$ 
    };
    
    private ValueModel monthValueModel;
    private boolean itemSelected;
    
    public MonthsComboBoxModel(ValueModel monthValueModel) {
        this.monthValueModel = monthValueModel;
        itemSelected = false;
        initEventHandling();
        if (monthValueModel.getValue() != null) {
            fireContentsChangedEvent();
        }
    }
    
    public final String[] getValues() {
        String[] copy = new String[values.length];
        System.arraycopy(values, 0, copy, 0,
                values.length);
        return copy;
    }

    private void initEventHandling() {
        monthValueModel.addValueChangeListener(new PropertyChangeListener() {
           public void propertyChange(PropertyChangeEvent evt) {
               fireContentsChangedEvent();
            }
        });
    }
    
    private void fireContentsChangedEvent() {
        //  Assume that an item is selected.
        itemSelected = true;
        fireListDataChanged(new ListDataEvent(MonthsComboBoxModel.this,
                ListDataEvent.CONTENTS_CHANGED, 0, 0));
    }

    public void setSelectedItem(Object anItem) {
        if (anItem == null) {
            itemSelected = false;
            monthValueModel.setValue(null);
            return;
        }
        itemSelected = true;
        //In case "unknown" is chosen, we set the value to null
        if (values[values.length - 1].equals(anItem)) {
            monthValueModel.setValue(null);
            return;
        }
        for (int i = 0, c = values.length - 1; i < c; ++i) {
            if (values[i].equals(anItem)) {
                monthValueModel.setValue(Integer.valueOf(i));
                break;
            }
        }
        
    }

    public Object getSelectedItem() {
        if (!itemSelected) {
            return null;
        }
        if (monthValueModel.getValue() == null) {
            return values[values.length - 1];
        }
        
        int selectedIndex = ((Integer) monthValueModel.getValue()).intValue();
        return values[selectedIndex];
    }

    public int getSize() {
        return values.length;
    }

    public Object getElementAt(int index) {
        return values[index];
    }
    
    public void setMonthValueModel(ValueModel monthValueModel) {
        this.monthValueModel = monthValueModel;
    }
    
    public ValueModel getMonthValueModel() {
        return monthValueModel;
    }
}
