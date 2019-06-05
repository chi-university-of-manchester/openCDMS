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

import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.utils.DatasetSaver;
import org.psygrid.datasetdesigner.controllers.DatasetController;

import org.psygrid.datasetdesigner.ui.MainFrame;

import org.psygrid.common.ui.WrappedJOptionPane;

/**
 * Update the dataset policy.
 * @author pwhelan
 */
public class UpdatePolicyAction extends AbstractAction {
	
	/**
	 * String that prefixes all strings in this class in the properties file
	 */
	private final static String STRING_PREFIX = "org.psygrid.datasetdesigner.actions.updatepolicy.";
	
	/**
	 * The main window of the application
	 */
	private MainFrame frame;
	
	/**
	 * Constructor
	 * @param frame the main window of the application
	 */
	public UpdatePolicyAction(MainFrame frame) {
		super(PropertiesHelper.getStringFor(STRING_PREFIX + "updatepolicy"));
		this.frame = frame;
	}

	/**
	 * Action event handler
	 * Update the policy and display 
	 * a success/failure message
	 * 
	 * @param e the action event
	 */
	public void actionPerformed(ActionEvent e) {
		DatasetSaver dsSaver = new DatasetSaver(frame, DatasetController.getInstance().getActiveDs(), false);
		dsSaver.updatePolicy();
	}
	
}
