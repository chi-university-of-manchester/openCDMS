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

package org.psygrid.securitymanager.renderers;

import java.awt.Component;
import java.awt.Font;

import org.psygrid.securitymanager.utils.DisplayTool;
import org.psygrid.www.xml.security.core.types.ProjectType;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.RoleType;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;


public class AttributeListCellRenderer extends DefaultListCellRenderer
{

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		
		Component superComponent = null;
		
		if (value instanceof ProjectType)
		{
			ProjectType pValue = (ProjectType)value;
			superComponent = getListCellRendererComponent(list, pValue.getName(), index, isSelected,
					cellHasFocus);
			((JLabel)superComponent).setToolTipText(pValue.getName());
		} 
		else if (value instanceof GroupType)
		{
			GroupType gValue = (GroupType)value;
			String fullGroupName = DisplayTool.getFullGroupName(gValue);
			 superComponent = getListCellRendererComponent(list, fullGroupName, index, isSelected,
					cellHasFocus);
			 ((JLabel)superComponent).setToolTipText(fullGroupName);
		}
		else if (value instanceof RoleType)
		{
			RoleType rValue = (RoleType)value;
			superComponent = getListCellRendererComponent(list, rValue.getName(), index, isSelected,
					cellHasFocus);
			((JLabel)superComponent).setToolTipText(rValue.getName());
		}
		
		if (superComponent != null)
		{
			superComponent.setFont(superComponent.getFont().deriveFont(Font.PLAIN));
			return superComponent;
		} else
		{
			return super.getListCellRendererComponent(list, value, index, isSelected,
			cellHasFocus);
		}
	}
	
	
}
