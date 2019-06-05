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

import org.psygrid.datasetdesigner.ui.configurationdialogs.ConfigureGroupsDialog;

import org.psygrid.datasetdesigner.ui.MainFrame;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;

public class ConfigureSitesAction extends AbstractAction {
	
	private MainFrame frame;
	
	private boolean viewOnly;
	
	private boolean readOnly;
	
	/**
	 * Constructor
	 * @param frame the main window of the application
	 */
	public ConfigureSitesAction(MainFrame frame) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.configurecentres"));
		this.frame = frame;
		this.viewOnly = false;
		this.readOnly = false;
	}
	
	/**
	 * Constructor
	 * @param frame
	 * @param viewOnly the presence of this indicates use viewOnly currently
	 */
	public ConfigureSitesAction(MainFrame frame, boolean viewOnly) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.viewcentres"));
		this.frame = frame;
		this.viewOnly = viewOnly;
	}

	/**
	 * Constructor
	 * @param frame
	 * @param viewOnly the presence of this indicates use viewOnly currently
	 * @param readOnly the presence of this indicates use readOnly currently
	 */
	public ConfigureSitesAction(MainFrame frame, boolean viewOnly, boolean readOnly) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.viewcentres"));
		this.frame = frame;
		this.viewOnly = viewOnly;
		this.readOnly = readOnly;
	}
	
	/**
	 * Show the groups dialog
	 * @param aet the trigger event
	 */
	public void actionPerformed(ActionEvent aet) {
		new ConfigureGroupsDialog(frame, viewOnly, readOnly);
	}
}