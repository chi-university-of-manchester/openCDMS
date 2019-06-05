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
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.psygrid.collection.entry.DocumentStatus;
import org.psygrid.collection.entry.Selectable;
import org.psygrid.collection.entry.persistence.RecordsList;
import org.psygrid.collection.entry.persistence.RecordsListWrapper;
import org.psygrid.collection.entry.persistence.RecordsListWrapper.Item;

/**
 * @author Rob Harper
 *
 */
public class CommitTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	public enum Column { SELECTED, IDENTIFIER, STATUS, COMMITRESULT }
    private final EnumSet<Column> columns;
    private final List<Selectable<Item>> items;
    
    public CommitTableModel(List<RecordsListWrapper.Item> recordsList, EnumSet<Column> columns) {
        this.columns = columns;
        this.items = getItems(recordsList);
    }

    public CommitTableModel(RecordsListWrapper recordsList, EnumSet<Column> columns) {
        this.columns = columns;
        this.items = getItems(recordsList);
    }
	public int getColumnCount() {
        return columns.size();
	}

	public int getRowCount() {
		return items.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
        Selectable<Item> selectable = items.get(rowIndex);
        Item item = selectable.getObject();
        Column column = getColumn(columnIndex);
        switch (column) {
        case IDENTIFIER:
            return item.getIdentifierRepresentation();
        case SELECTED:
            return Boolean.valueOf(selectable.isSelected());
        case STATUS:
            return (item.isReadyToCommit() ? DocumentStatus.READY_TO_SUBMIT.toString() : DocumentStatus.LOCALLY_INCOMPLETE.toString());
        case COMMITRESULT:
        	return item.getResult();
        }
        return null;
	}
	
	private List<Selectable<Item>> getItems(List<RecordsListWrapper.Item> recordListItems){
		List<Selectable<Item>> selItems = 
            new ArrayList<Selectable<Item>>(recordListItems.size());
		for(RecordsListWrapper.Item item: recordListItems){
			selItems.add(new Selectable<Item>(item, item.isReadyToCommit()));
		}
		return selItems;
	}

    private List<Selectable<Item>> getItems(RecordsListWrapper recordsList) {
        List<Selectable<Item>> selItems = 
            new ArrayList<Selectable<Item>>(recordsList.getItems().size());
        for (Item item : recordsList.getItems()) {
            selItems.add(new Selectable<Item>(item, item.isReadyToCommit()));
        }
        return selItems;
    }

    private Column getColumn(int columnIndex) {
        Iterator<Column> it = columns.iterator();
        for (int i = 0; i < columnIndex; ++i) {
            it.next();
        }
        return it.next();
    }

    private void setItemValueAt(int rowIndex, boolean selected) {
        items.get(rowIndex).setSelected(selected);
    }
    
    public List<Selectable<Item>> getSelectedItems()    {
        return getSelectedObjects(items);
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

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        if ((columnIndex == Column.SELECTED.ordinal()) 
                && columns.contains(Column.SELECTED)) {
            boolean selected = ((Boolean) value).booleanValue();
            setItemValueAt(rowIndex, selected);
        }
    }

    public final List<Selectable<Item>> getItems() {
        return items;
    }
  
    public Item getItemValueAtRow(int rowIndex) {
        if (items == null) {
            return null;
        }
        return items.get(rowIndex).getObject();
    }
    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if ((columnIndex == Column.SELECTED.ordinal()) 
                && columns.contains(Column.SELECTED)) {
            return Boolean.class;
        }
        return Object.class;
    }
    
    @Override
    public String getColumnName(int columnIndex) {
        Column column = getColumn(columnIndex);
        return getText(column);
    }
    
    private String getText(Column column) {
        switch (column) {
        case STATUS:
            return "Status";
        case IDENTIFIER:
            return "Identifier";
        case SELECTED:
            return "Commit";
        case COMMITRESULT:
        	return "Commit Result";
        }
        return null;
    }
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if ((columnIndex == Column.SELECTED.ordinal()) 
                && columns.contains(Column.SELECTED)) {
            return true;
        }
        return false;
    }

}
