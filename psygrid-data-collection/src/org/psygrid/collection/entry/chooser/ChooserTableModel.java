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


package org.psygrid.collection.entry.chooser;

import java.util.List;

import javax.swing.table.AbstractTableModel;

public class ChooserTableModel extends AbstractTableModel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    protected List<? extends Choosable> choosables;
    protected Choosable parent;
    
    public ChooserTableModel(Choosable parent) throws ChoosableException   { 
        this.choosables = parent.getChildren();
        this.parent = parent;
    }
    
    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
        case 0:
            return Messages.getString("ChooserTableModel.name"); //$NON-NLS-1$

        default:
            throw new IllegalStateException("Number of columns is fixed at 1"); //$NON-NLS-1$
        }
    }
    
    public int getRowCount() {
        return choosables.size();
    }

    public int getColumnCount() {
        return 1;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        return choosables.get(rowIndex);
    }

    public Choosable getValueAtRow(int row) {
        return choosables.get(row);
    }
    
    public Choosable getParent() {
        return parent;
    }

}
