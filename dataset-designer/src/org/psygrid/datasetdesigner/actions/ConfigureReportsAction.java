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

import org.psygrid.datasetdesigner.ui.configurationdialogs.ConfigureReportsDialog;

import org.psygrid.datasetdesigner.ui.MainFrame;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;

public class ConfigureReportsAction extends AbstractAction {
	
	/**
	 * The main window of the application
	 */
	private MainFrame frame;
	
	/**
	 * Indicates read-only mode; default to false
	 */
	private boolean viewOnly = false;
	
	/**
	 * Show the reports dialog action
	 * @param frame the main window of the application
	 */
	public ConfigureReportsAction(MainFrame frame) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.configurereports"));
		this.frame = frame;
	}

	/**
	 * Show the reports dialog action
	 * @param frame the main window of the application
	 * @param viewOnly true if dialog should be displayed in read-only; false if not
	 */
	public ConfigureReportsAction(MainFrame frame, boolean viewOnly) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.viewreports"));
		this.frame = frame;
		this.viewOnly = viewOnly;
	}

	/**
	 * Show the reports dialog
	 * @param aet the trigger event
	 */
	public void actionPerformed(ActionEvent aet) {
		new ConfigureReportsDialog(frame, viewOnly);
	}
}