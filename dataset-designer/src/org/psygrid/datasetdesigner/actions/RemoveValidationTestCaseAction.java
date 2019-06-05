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
import javax.swing.JDialog;
import javax.swing.JList;

import org.psygrid.common.ui.WrappedJOptionPane;
import org.psygrid.data.model.hibernate.SingleVariableTestCase;
import org.psygrid.data.model.hibernate.ValidationRule;
import org.psygrid.datasetdesigner.ui.configurationdialogs.ConfigureValidationRuleTestsDialog;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;

public class RemoveValidationTestCaseAction extends AbstractAction {

	private static final long serialVersionUID = -1426253024976949980L;

	private JDialog parentDialog;
	private JList testCasesList = null;
	private ValidationRule validationRule;
		
	public RemoveValidationTestCaseAction(JDialog parentDialog, ValidationRule validationRule, JList testCasesList) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.remove"));
		this.parentDialog = parentDialog;
		this.testCasesList = testCasesList;
		this.validationRule = validationRule;
	}
	
	public void actionPerformed(ActionEvent aet) {
		
		SingleVariableTestCase testCase = (SingleVariableTestCase)testCasesList.getSelectedValue();
		if (testCase == null) {
			return;
		}
		
		validationRule.getTest().removeTest(testCase);
		WrappedJOptionPane.showMessageDialog(parentDialog, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.test.testcaseremoved"));
		//Update test cases table
		((ConfigureValidationRuleTestsDialog)parentDialog).refreshTable(validationRule.getTest());
	}
	
}