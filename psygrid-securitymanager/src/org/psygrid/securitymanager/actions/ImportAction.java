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
import javax.swing.JDialog;

import org.psygrid.securitymanager.Application;
import org.psygrid.securitymanager.ui.ImportDialog;
import org.psygrid.securitymanager.utils.PropertiesHelper;

/**
 * Import Action to handle the importing of a keystore for login
 *
 * @author pwhelan
 *
 */
public class ImportAction extends AbstractAction
{
	JDialog parentDialog;
	Application application;
	
	/**
	 * Creat the action with the parent dialog and main application.
	 * @param parentDialog
	 * @param frame
	 */
	public ImportAction(JDialog parentDialog, Application application) {
		super(PropertiesHelper.getPropertyHelper().getStringFor("org.psygrid.securitymanager.actions.import"));
		this.parentDialog = parentDialog;
		this.application=application;
	}
	
	/**
	 * Show the Import dialog window.
	 */
	public void actionPerformed(ActionEvent e) {
		ImportDialog dialog = new ImportDialog(application, parentDialog);
		dialog.show();
	}
	
}