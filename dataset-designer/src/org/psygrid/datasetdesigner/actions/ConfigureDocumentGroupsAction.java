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

import org.psygrid.datasetdesigner.ui.configurationdialogs.ConfigureDocumentGroupsDialog;

import org.psygrid.datasetdesigner.ui.MainFrame;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;


/**
 * Configure the stages for the study
 * Document groups = Study stages
 * @author pwhelan
 */
public class ConfigureDocumentGroupsAction extends AbstractAction {

	/**
	 * The main window of the application
	 */
	private MainFrame frame;
	
	/**
	 * Open the dialog in read-only mode
	 */
	private boolean readOnly;
	
	/**
	 * The configuration action for the document
	 * @param frame the main window of the application
	 */
	public ConfigureDocumentGroupsAction(MainFrame frame) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.configuredocumentgroups"));
		this.frame = frame;
	}
	
	/**
	 * The configuration action for the document
	 * @param frame the main window of the application
	 */
	public ConfigureDocumentGroupsAction(MainFrame frame, boolean readOnly) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.viewdocumentgroups"));
		this.frame = frame;
		this.readOnly = readOnly;
	}
	
	/**
	 * Display the configuration dialog for the study stages
	 */
	public void actionPerformed(ActionEvent aet) {
		new ConfigureDocumentGroupsDialog(frame, readOnly);
	}
	
}
