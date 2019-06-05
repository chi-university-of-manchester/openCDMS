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
package org.psygrid.datasetdesigner.model;

import java.util.ArrayList;

import javax.swing.table.DefaultTableModel;

import org.psygrid.data.model.hibernate.Status;

/**
 * Status Table model
 *
 */
public class StatusTableModel extends DefaultTableModel {
    
	private ArrayList<Status> statuses;

    public StatusTableModel(ArrayList<Status> statuses) {
    	if (statuses == null) {
    		statuses = new ArrayList<Status>();
    	}
    	
        this.statuses = statuses;
    }

    public int getRowCount() {
    	if (statuses == null) {
    		return 0;
    	}
    	
    	return statuses.size();
    }

    public int getColumnCount() {
        return 3;
    }

    public Class getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
    
    public void setStatusAt(Status status, int row) {
    	statuses.set(row, status);
    	fireTableRowsUpdated(0, row);
    }

    public void addStatus(Status status) {
    	statuses.add(status);
    	fireTableRowsUpdated(0, statuses.size());
    }

    public void removeStatus(Status status) {
    	statuses.remove(status);
    	fireTableRowsUpdated(0, statuses.size());
    }
    
    public Status getStatusAt(int row) {
    	return statuses.get(row);
    }
    
    public ArrayList<Status> getAllStatuses() {
    	return statuses;
    }
    
    @Override
    public Object getValueAt(int row, int column) {
    	Status status = statuses.get(row);
    	String returnVal = null;
    	
    	switch (column)  {
    		case 0:
    			returnVal = status.getShortName();
    			break;
    		case 1:
    			returnVal = status.getGenericState().toString();
    			break;
    		case 2:
    			returnVal = new Integer(status.getCode()).toString();
    			break;
    		default: 
    			returnVal = status.getShortName();
    			break;
    	}
    	
        return returnVal;
    }

    public void setValueAt(Object value, int row, int column) {
        statuses.set(row, (Status)value);
        fireTableCellUpdated(row, column);
    }
}


