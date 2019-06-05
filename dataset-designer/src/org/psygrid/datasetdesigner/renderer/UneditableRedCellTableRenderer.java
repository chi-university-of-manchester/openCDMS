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
package org.psygrid.datasetdesigner.renderer;

import java.awt.Component;
import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * A table renderers that displays a red label a cell in column 1 
 * is not edtiable
 * 
 * @author paulinewhelan
 *
 */
public class UneditableRedCellTableRenderer extends DefaultTableCellRenderer {

	/**
	 * Return a red label if cell in column 1 is uneditable;
	 * use default rendering if not. 
	 */
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

		if (!table.getModel().isCellEditable(row, column)) {
			JLabel label = new JLabel();
			label.setBackground(Color.red);
			return label;
		}

		return super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
				row, column);
		
	}
	
}