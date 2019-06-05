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

import org.psygrid.data.model.hibernate.DateValidationRule;
import org.psygrid.data.model.hibernate.DateValue;
import org.psygrid.data.model.hibernate.IntegerValidationRule;
import org.psygrid.data.model.hibernate.IntegerValue;
import org.psygrid.data.model.hibernate.NumericValidationRule;
import org.psygrid.data.model.hibernate.NumericValue;
import org.psygrid.data.model.hibernate.SingleVariableTestCase;
import org.psygrid.data.model.hibernate.TextValidationRule;
import org.psygrid.data.model.hibernate.TextValue;
import org.psygrid.data.model.hibernate.ValidationRule;
import org.psygrid.data.model.hibernate.Value;
import org.psygrid.datasetdesigner.ui.configurationdialogs.ConfigureValidationRuleTestsDialog;
import org.psygrid.datasetdesigner.ui.configurationdialogs.EditTestCaseDialog;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;

public class AddEditValidationTestCaseAction extends AbstractAction {

	private static final long serialVersionUID = -7854474663747302156L;
	
	private JDialog parentDialog;
	private JList testCasesList = null;
	private ValidationRule validationRule;
	
	public AddEditValidationTestCaseAction(JDialog parentDialog, ValidationRule validationRule) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.add"));
		this.parentDialog = parentDialog;
		this.validationRule = validationRule;
	}
	
	public AddEditValidationTestCaseAction(JDialog parentDialog, ValidationRule validationRule, JList testCasesList) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.edit"));
		this.parentDialog = parentDialog;
		this.testCasesList = testCasesList;
		this.validationRule = validationRule;
	}
	
	public void actionPerformed(ActionEvent aet) {
		String title = "";
		SingleVariableTestCase testCase = null;
		
		if (testCasesList == null) {
			title = "Add new test case";

			Value value = null;
			if (validationRule instanceof IntegerValidationRule) {
				value = new IntegerValue();
			}
			else if (validationRule instanceof NumericValidationRule) {
				value = new NumericValue();
			}
			else if (validationRule instanceof TextValidationRule) {
				value = new TextValue();
			}
			else if (validationRule instanceof DateValidationRule) {
				value = new DateValue();
			}
			
			testCase = new SingleVariableTestCase(value, true);
		}
		else {
			title = "Edit test case";
			testCase = (SingleVariableTestCase)testCasesList.getSelectedValue();
		}
		
		//Dialog 
		EditTestCaseDialog dialog = new EditTestCaseDialog(parentDialog, title, validationRule.getTest(), testCase);
		if (dialog.isChanged()) {
			validationRule.setIsRevisionCandidate(true);

			//Update test cases table
			((ConfigureValidationRuleTestsDialog)parentDialog).refreshTable(validationRule.getTest());
		}
	}
	
}