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

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.psygrid.collection.entry.Selectable;

public class RecordsTableModel extends AbstractTableModel {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    public enum Column { SELECTED, IDENTIFIER, STATUS }
    private final EnumSet<Column> columns;
    private final List<Selectable<String>> records;

    public RecordsTableModel(List<Selectable<String>> records, EnumSet<Column> columns) {
        this.columns = columns;
        this.records = records;
    }
    
    @Override
    public String getColumnName(int columnIndex) {
        Column column = getColumn(columnIndex);
        return getText(column);
    }
    
    private Column getColumn(int columnIndex) {
        Iterator<Column> it = columns.iterator();
        for (int i = 0; i < columnIndex; ++i) {
            it.next();
        }
        return it.next();
    }

    private String getText(Column column) {
        switch (column) {
        case STATUS:
            return "Status";
        case IDENTIFIER:
            return "Identifier";
        case SELECTED:
            return "Commit";
        }
        return null;
    }

    public int getRowCount() {
        return records.size();
    }
    
    public int getColumnCount() {
        return columns.size();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        Selectable<String> selectable = records.get(rowIndex);
        String record = selectable.getObject();
        Column column = getColumn(columnIndex);
        switch (column) {
        case IDENTIFIER:
            return record;
        case SELECTED:
            return Boolean.valueOf(selectable.isSelected());
        }
        return null;
    }
    
    public String getRecordValueAtRow(int rowIndex) {
        if (records == null) {
            return null;
        }
        return records.get(rowIndex).getObject();
    }
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if ((columnIndex == Column.SELECTED.ordinal()) 
                && columns.contains(Column.SELECTED)) {
            return true;
        }
        return false;
    }
    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if ((columnIndex == Column.SELECTED.ordinal()) 
                && columns.contains(Column.SELECTED)) {
            return Boolean.class;
        }
        return Object.class;
    }
    
    public final List<Selectable<String>> getRecords(){
        return Collections.unmodifiableList(records);
    }
    
    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        if ((columnIndex == Column.SELECTED.ordinal()) 
                && columns.contains(Column.SELECTED)) {
            boolean selected = ((Boolean) value).booleanValue();
            records.get(rowIndex).setSelected(selected);
        }
    }


    public List<Selectable<String>> getSelectedRecords()   {
        return getSelectedObjects(records);
    }
    
    private <T>List<Selectable<T>> getSelectedObjects(List<Selectable<T>> selectables){
        List<Selectable<T>> selectedObjs = new ArrayList<Selectable<T>>();
        for (Selectable<T> obj : selectables) {
            if (obj.isSelected()) {
                selectedObjs.add(obj);
            }
        }
        return selectedObjs;
    }
}
