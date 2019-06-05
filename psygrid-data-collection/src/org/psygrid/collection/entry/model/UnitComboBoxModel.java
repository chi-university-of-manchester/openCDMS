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

import com.jgoodies.binding.value.ValueModel;
import org.psygrid.data.model.hibernate.Unit;

public class UnitComboBoxModel extends EntryComboBoxModel {

    private List<Unit> units;

    private int selectedItemIndex;

    private ValueModel unitModel;

    public UnitComboBoxModel(ValueModel unitModel, List<Unit> units) {
        selectedItemIndex = 0;
        this.unitModel = unitModel;
        this.units = units;
        initEventHandling();
        if (unitModel.getValue() != null) {
            updateSelectedIndex();
        }
    }
    
    private void initEventHandling() {
        unitModel.addValueChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                updateSelectedIndex();
            }
        });
    }

    public void setUnitModel(ValueModel unitModel) {
        this.unitModel = unitModel;
    }
    
    private void updateSelectedIndex() {
        if (unitModel.getValue() == null) {
            selectedItemIndex = -1;
        }
        else {
            for (int i = 0, c = units.size(); i < c; ++i) {
                Unit unit = units.get(i);
                if (unit.equals(unitModel.getValue())) {
                    selectedItemIndex = i;
                }
            }
        }
        fireListDataChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED,
                0, 0));
    }

    public void setSelectedItem(Object anItem) {
        String selectedString = (String) anItem;
        for (int i = 0, c = units.size(); i < c; ++i) {
            Unit unit = units.get(i);
            if (unit.getAbbreviation().equals(selectedString)) {
                selectedItemIndex = i;
                unitModel.setValue(unit);
            }
        }
    }

    public Object getSelectedItem() {
        if (selectedItemIndex == -1) {
            return null;
        }
        return units.get(selectedItemIndex).getAbbreviation();
    }

    public int getSize() {
        return units.size();
    }

    public Object getElementAt(int index) {
        return units.get(index).getAbbreviation();
    }
}
