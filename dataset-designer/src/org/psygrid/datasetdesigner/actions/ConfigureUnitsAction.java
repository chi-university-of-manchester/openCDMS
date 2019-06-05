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

import org.psygrid.datasetdesigner.ui.MainFrame;
import org.psygrid.datasetdesigner.ui.configurationdialogs.ConfigureUnitsDialog;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;


/**
 * Configure units for the dataset
 * @author pwhelan
 *
 */
public class ConfigureUnitsAction extends AbstractAction {
	
	/**
	 * Main window of the application
	 */
	private MainFrame frame;
	
	/**
	 * Indicates view only to be used with the DEL
	 */
	private boolean viewOnly;
	
	/**
	 * Indicates read only mode to be used when file is open by another user
	 */
	private boolean readOnly;
		
	/**
	 * Create the action with the main application 
	 * @param frame main window of the application
	 */
	public ConfigureUnitsAction(MainFrame frame) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.configureunits"));
		this.frame = frame;
		this.viewOnly = false;
		this.readOnly = false;
	}
	
	public ConfigureUnitsAction(MainFrame frame, boolean viewOnly) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.viewunits"));
		this.frame = frame;
		this.viewOnly = viewOnly;
		this.readOnly = false;
	}
	
	public ConfigureUnitsAction(MainFrame frame, boolean viewOnly, boolean readOnly) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.viewunits"));
		this.frame = frame;
		this.viewOnly = viewOnly;
		this.readOnly = readOnly;
	}
	
	public void actionPerformed(ActionEvent aet) {
		new ConfigureUnitsDialog(frame, viewOnly, readOnly);
	}
}