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

import org.psygrid.data.model.hibernate.MultipleVariableTest;
import org.psygrid.data.model.hibernate.MultipleVariableTestCase;
import org.psygrid.datasetdesigner.ui.MultipleVariableTestPanel;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;


/**
 * Configure units for the dataset
 * @author pwhelan
 *
 */
public class RemoveMultipleVariableTestCaseAction extends AbstractAction {
	
	private MultipleVariableTest test;
	private MultipleVariableTestCase testCase;
	private MultipleVariableTestPanel parent;
	
	/**
	 * Create the action with the main application 
	 * @param frame main window of the application
	 */
	public RemoveMultipleVariableTestCaseAction(MultipleVariableTestPanel parent, MultipleVariableTest test, MultipleVariableTestCase testCase) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.remove"));
		this.test = test;
		this.testCase = testCase;
		this.parent = parent;
	}
		
	public void actionPerformed(ActionEvent aet) {
		test.removeTestCase(testCase);
		parent.testChanged();
	}
}