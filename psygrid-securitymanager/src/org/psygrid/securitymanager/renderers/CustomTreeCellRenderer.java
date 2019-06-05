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

import java.awt.*;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.SwingConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.ProjectType;
import org.psygrid.www.xml.security.core.types.RoleType;

import org.psygrid.securitymanager.utils.*;

public class CustomTreeCellRenderer extends DefaultTreeCellRenderer
{

	private static final Log LOG = LogFactory.getLog(CustomTreeCellRenderer.class);
	
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		
		SortedTreeNode node = (SortedTreeNode)value;
		Object newValue = node.getUserObject();
		
		if (newValue instanceof ProjectType)
		{
			ProjectType project = (ProjectType) newValue;
			newValue = project.getName();
			return new JLabel(newValue.toString(), IconsHelper.getInstance().getImageIcon("Properties16.png"), SwingConstants.LEFT);
		} else if (newValue instanceof GroupType)
		{
			GroupType group = (GroupType) newValue;
			newValue = group.getName();
			return new JLabel(newValue.toString(), IconsHelper.getInstance().getImageIcon("Home16.png"), SwingConstants.LEFT);
		} else if (newValue instanceof RoleType)
		{
			RoleType role = (RoleType)newValue;
			newValue = role.getName();
			return new JLabel(newValue.toString(), IconsHelper.getInstance().getImageIcon("Refresh16.png"), SwingConstants.LEFT);
		} else if (newValue instanceof UserType)
		{
			UserType user = (UserType) newValue;
			newValue = user.getName();
			return new JLabel(newValue.toString(), IconsHelper.getInstance().getImageIcon("Export16.png"), SwingConstants.LEFT);
		} else if (newValue != null && newValue.equals("Groups"))
		{
			return new JLabel(newValue.toString(), IconsHelper.getInstance().getImageIcon("Home16.png"), SwingConstants.LEFT);
		} else if (newValue != null && newValue.equals("Roles"))
		{
			return new JLabel(newValue.toString(), IconsHelper.getInstance().getImageIcon("Refresh16.png"), SwingConstants.LEFT);
		}
		
		return super.getTreeCellRendererComponent(tree, newValue, sel, expanded, leaf,
				row, hasFocus);
	}
	
	
	
}