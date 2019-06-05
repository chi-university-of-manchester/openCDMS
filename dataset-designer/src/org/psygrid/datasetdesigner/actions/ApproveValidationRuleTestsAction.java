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

import org.psygrid.data.model.hibernate.ValidationRule;
import org.psygrid.datasetdesigner.ui.configurationdialogs.ConfigureValidationRuleTestsDialog;

public class ApproveValidationRuleTestsAction extends AbstractAction {

	private boolean edit;

	private JDialog parentDialog;

	private JList rulesList;

	public ApproveValidationRuleTestsAction(JDialog parentDialog,
			JList rulesList) {
		super("Test & Approve");
		this.parentDialog = parentDialog;
		this.edit = false;
		this.rulesList = rulesList;
	}

	public ApproveValidationRuleTestsAction(JDialog parentDialog,
			JList rulesList,
			boolean canEdit) {
		super("Test & Approve");
		this.parentDialog = parentDialog;
		this.edit = canEdit;
		this.rulesList = rulesList;
	}

	public void actionPerformed(ActionEvent aet) {
		if (edit) {
			new ConfigureValidationRuleTestsDialog(parentDialog, (ValidationRule)rulesList.getSelectedValue(), edit);
		} else {
			new ConfigureValidationRuleTestsDialog(parentDialog, (ValidationRule)rulesList.getSelectedValue());	
		}
	}


}