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

package org.psygrid.securitymanager.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.psygrid.securitymanager.Application;
import org.psygrid.securitymanager.controller.AAController;
import org.psygrid.securitymanager.utils.IconsHelper;
import org.psygrid.securitymanager.utils.PropertiesHelper;
import org.psygrid.securitymanager.utils.UserType;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.ProjectType;

/**
 * Action that deletes a group from the project for the specified user.
 * @author pwhelan
 *
 */
public class DeleteGroupFromProjectAction extends AbstractAction
{
	
	private Application application;
	private ProjectType p;
	private GroupType g;
	private UserType u;
	
	public DeleteGroupFromProjectAction(Application application, ProjectType p, GroupType g, UserType u)
	{
		super(PropertiesHelper.getPropertyHelper().getStringFor("org.psygrid.securitymanager.actions.deletegroup"), 
				IconsHelper.getInstance().getImageIcon("Delete16.png"));
		this.application = application;
		this.p = p;
		this.u = u;
		this.g = g;
	}

	public void actionPerformed(ActionEvent e) {
		if (AAController.getInstance().deleteGroupInProjectFromUser(p, g, u))
		{
			JOptionPane.showMessageDialog(application, "Group " + g.getName() + " was removed from " + u.getFirstName() + " " + u.getLastName(), "Group Removal", JOptionPane.INFORMATION_MESSAGE);
			application.reinit();
		}else
		{
			JOptionPane.showMessageDialog(application, "Group was not removed", "Group Removal", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	
	
	
}