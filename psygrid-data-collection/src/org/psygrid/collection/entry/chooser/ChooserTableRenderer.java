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

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public abstract class ChooserTableRenderer implements TableCellRenderer  {

    protected final JLabel label;
    
    public ChooserTableRenderer() {
        label = new JLabel();
        label.setOpaque(true);
    }
    
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        Choosable choosable = (Choosable) value;
        
        switch (column) {
        case 0:
            label.setText(choosable.getDisplayText());
            break;
        case 1:
            label.setText(choosable.getType().toString());
            break;
        }
        
        if (column == 0) {
            setIcon(choosable);
        }
        
        if (isSelected) {
            label.setForeground(table.getSelectionForeground());
            label.setBackground(table.getSelectionBackground());
        }
        else {
            label.setForeground(table.getForeground());
            label.setBackground(table.getBackground());
        }
        label.setFont(table.getFont());
        return label;
    }

    protected abstract void setIcon(Choosable choosable);
}
