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
import org.psygrid.www.xml.security.core.types.ProjectType;
import org.psygrid.www.xml.security.core.types.RoleType;

/**
 * Action that deletes the role from the project for the given user.
 * @author pwhelan
 *
 */
public class DeleteRoleFromProjectAction extends AbstractAction
{
	
	private Application application;
	private ProjectType p;
	private RoleType r;
	private UserType u;
	
	public DeleteRoleFromProjectAction(Application application, ProjectType p, RoleType r, UserType u)
	{
		super(PropertiesHelper.getPropertyHelper().getStringFor("org.psygrid.securitymanager.actions.deleterole"), 
				IconsHelper.getInstance().getImageIcon("Delete16.png"));
		this.application = application;
		this.p = p;
		this.u = u;
		this.r = r;
	}

	public void actionPerformed(ActionEvent e) {
		if (AAController.getInstance().deleteRoleInProjectFromUser(p, r, u))
		{
			JOptionPane.showMessageDialog(application, "Role " + r.getName() + " was removed from " + u.getFirstName() + " " + u.getLastName(), "Role Removal", JOptionPane.INFORMATION_MESSAGE);
			application.reinit();
		}else
		{
			JOptionPane.showMessageDialog(application, "Role was not removed", "Role Removal", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	
	
	
}