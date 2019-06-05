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
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JDialog;

import org.psygrid.data.model.hibernate.MultipleVariableTestCase;
import org.psygrid.datasetdesigner.ui.MultipleVariableTestPanel;
import org.psygrid.datasetdesigner.ui.configurationdialogs.CreateMultipleVariableTestDialog;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;


/**
 * Configure units for the dataset
 * @author pwhelan
 *
 */
public class EditMultipleVariableTestCaseAction extends AbstractAction {
	
	private JDialog parentDialog;
	private MultipleVariableTestCase testCase;
	private Map variables;
	private MultipleVariableTestPanel parent;
	
	/**
	 * Create the action with the main application 
	 * @param frame main window of the application
	 * @param testCase
	 * @param labels the list of labels used to 
	 */
	public EditMultipleVariableTestCaseAction(JDialog parentDialog, MultipleVariableTestPanel parent, MultipleVariableTestCase testCase, Map variables) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.edit"));
		this.parentDialog = parentDialog;
		this.parent = parent;
		this.testCase = testCase;
		this.variables = variables;
	}
		
	public void actionPerformed(ActionEvent aet) {
		new CreateMultipleVariableTestDialog(parentDialog, parent, testCase, variables);
	}
}