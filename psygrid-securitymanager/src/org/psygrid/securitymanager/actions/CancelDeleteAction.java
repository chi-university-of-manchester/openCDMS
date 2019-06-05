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

import javax.swing.JDialog;
import javax.swing.AbstractAction;

import org.psygrid.securitymanager.utils.PropertiesHelper;

/**
 * Simple action to dispose of calling dialog - called when Cancel is called.
 * 
 * @author pwhelan
 */
public class CancelDeleteAction extends AbstractAction
{
	private JDialog dialog;
	
	/**
	 * Action that handles disposal of the parent dialog.
	 * @param dialog parent calling dialog
	 */
	public CancelDeleteAction(JDialog dialog)
	{
		super(PropertiesHelper.getPropertyHelper().getStringFor("org.psygrid.securitymanager.actions.cancel"));
		this.dialog = dialog;
	}

	public void actionPerformed(ActionEvent aet)
	{
		dialog.dispose();
	}
	
}
