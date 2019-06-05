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

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

import org.psygrid.data.model.hibernate.*;
import org.psygrid.datasetdesigner.model.DSOption;
import org.psygrid.datasetdesigner.utils.IconsHelper;
import org.psygrid.esl.model.IRole;

import org.psygrid.www.xml.security.core.types.ProjectType;

public class EntryTableCellRenderer extends DefaultTableCellRenderer {

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		
		if (value instanceof OptionEntry) {
			value = ((OptionEntry)value).getName();
		}
		
		if (value instanceof Option) {
			value = ((Option)value).getName(); 
		}
		
		if (value instanceof DSOption) {
			value = ((DSOption)value).getEntryName() + " - " + ((DSOption)value).getOption().getName();
		}

		if (value instanceof NumericEntry) {
			value = (((NumericEntry)value).getName());
		}

		if (value instanceof IntegerEntry) {
			value = (((IntegerEntry)value).getName());
		}
		
		if (value instanceof DateEntry) {
			value = (((DateEntry)value).getName());
		}

		if (value instanceof DerivedEntry) {
			value = (((DerivedEntry)value).getName());
		}
		
		if (value instanceof IRole) {
			value = (((IRole)value)).getName();
		}
		
		if (value instanceof Document) {
			value = ((Document)value).getName();
		}
		
		if (value instanceof ProjectType) {
			return new JLabel(((ProjectType)value).getName(), IconsHelper.getInstance().getImageIcon("dataset.png"), SwingConstants.LEFT);
		}
		
		if (value instanceof DataSet) {
			if (((DataSet)value).isPublished()) {
				return new JLabel(((DataSet)value).getName(), IconsHelper.getInstance().getImageIcon("dataset_published.png"), SwingConstants.LEFT);
			} else {
				return new JLabel(((DataSet)value).getName(), IconsHelper.getInstance().getImageIcon("dataset.png"), SwingConstants.LEFT);
			}
		}
		
		return super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
				row, column);
	}
	
	
}
	