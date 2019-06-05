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
import javax.swing.JFrame;

import org.psygrid.securitymanager.ui.HelpAboutDialog;
import org.psygrid.securitymanager.utils.PropertiesHelper;


/**
 * Action to display the simple about dialog
 * with version details.
 * 
 * @author pwhelan
 *
 */
public class AboutAction extends AbstractAction
{
	
	public AboutAction()
	{
		super(PropertiesHelper.getPropertyHelper().getStringFor("org.psygrid.securitymanager.actions.about"));
	}
	
	public void actionPerformed(ActionEvent aet)
	{
		HelpAboutDialog helpAboutDialog = new HelpAboutDialog(new JFrame());
		helpAboutDialog.show();
	}
	
}