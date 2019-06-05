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
package org.psygrid.datasetdesigner.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.psygrid.datasetdesigner.ui.configurationdialogs.ConfigureConsentGroupDialog;

import org.psygrid.datasetdesigner.ui.MainFrame;

import org.psygrid.datasetdesigner.utils.PropertiesHelper;

public class ConfigureConsentAction extends AbstractAction {

	/**
	 * Main window of the application
	 */
	private MainFrame frame;
	
	/**
	 * Open the dialog in read-only mode
	 */
	private boolean readOnly;
	
	/**
	 * Construct the action
	 * @param frame the main window of the application
	 */
	public ConfigureConsentAction(MainFrame frame) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.configureconsent"));
		this.frame = frame;
		this.readOnly = false;
	}
	
	/**
	 * Construct the action
	 * @param frame the main window of the application
	 * @param readOnly currently the presense of this parameter indicates true (pretty much)
	 */
	public ConfigureConsentAction(MainFrame frame, boolean readOnly) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.viewconsent"));
		this.frame = frame;
		this.readOnly = readOnly;
	}

	
	/**
	 * Action event handler
	 * @param aet the event trigger
	 */
	final public void actionPerformed(ActionEvent aet) {
		new ConfigureConsentGroupDialog(frame, readOnly);
	}
	
}
