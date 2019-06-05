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
package org.psygrid.datasetdesigner.custom;

import javax.swing.JTable;

import javax.swing.table.TableCellRenderer;

import org.psygrid.datasetdesigner.renderer.EntryTableCellRenderer;

/**
 * A renderer for the table that has 
 * an entry as the first column
 * @author pwhelan
 */
public class DSDRendererTable extends JTable {
	
	
	/**
	 * use the EntryTableCellRenderer for the first column
	 * for all else, use the default
	 * @param row the current row 
	 * @param column the current column
	 * @return the renderer for the row, col
	 */
	public TableCellRenderer getCellRenderer(int row, int column) {
		if (column == 0) {
			return new EntryTableCellRenderer();
		}
		return super.getCellRenderer(row, column);
	}
	
}