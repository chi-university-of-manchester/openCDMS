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
import javax.swing.Action;

import org.psygrid.collection.entry.action.SettingsAction;
import org.psygrid.datasetdesigner.ui.MainFrame;
import org.psygrid.datasetdesigner.ui.configurationdialogs.ConfigureTransformersDialog;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;

/**
 * Action to produce the manage transformers dialog in either view only
 * or fully editable mode
 * 
 * @author pwhelan
 */
public class ConfigureTransformersAction extends AbstractAction {

	/**
	 * Main Frame of the application
	 */
	private MainFrame frame;

	/**
	 * Show dialog in view only mode
	 */
	private boolean viewOnly;

	/**
	 * Show dialog in read only mode (file open by someone else)
	 */
	private boolean readOnly;
	
	/**
	 * Constructor: configure the action
	 * @param frame the main window of the application
	 */
	public ConfigureTransformersAction(MainFrame frame) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.configuretransformers"));
		this.frame = frame;
		this.viewOnly = false;
		this.readOnly = false;
	}

	/**
	 * Configure the action
	 * @param frame the main window of the application
	 * @param viewOnly true if the dialog is shown in view only mode
	 */
	public ConfigureTransformersAction(MainFrame frame, boolean viewOnly) {
		super();
		if (viewOnly) {
			putValue(Action.NAME, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.viewtransformers"));
		} else {
			putValue(Action.NAME, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.configuretransformers"));
		}
		this.frame = frame;
		this.viewOnly = viewOnly;
		this.readOnly = false;
	}

	/**
	 * Configure the action
	 * @param frame the main window of the application
	 * @param viewOnly true if the dialog is shown in view only mode
	 */
	public ConfigureTransformersAction(MainFrame frame, boolean viewOnly, boolean readOnly) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.viewtransformers"));
		this.frame = frame;
		this.viewOnly = viewOnly;
		this.readOnly = readOnly;
	}

	
	/**
	 * Show the configure transformers dialog
	 * @param aet the trigger event
	 */
	public void actionPerformed(ActionEvent aet) {
		new ConfigureTransformersDialog(frame, viewOnly, readOnly);
	}
}