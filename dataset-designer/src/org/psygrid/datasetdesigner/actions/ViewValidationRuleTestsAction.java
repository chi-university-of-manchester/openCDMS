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
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JList;

import org.psygrid.data.model.hibernate.ValidationRule;
import org.psygrid.data.model.hibernate.ValidationRule;
import org.psygrid.datasetdesigner.model.DocTreeModel;
import org.psygrid.datasetdesigner.ui.configurationdialogs.ConfigureValidationRuleDialog;
import org.psygrid.datasetdesigner.ui.configurationdialogs.ConfigureValidationRuleTestsDialog;
import org.psygrid.datasetdesigner.ui.configurationdialogs.ConfigureValidationRuleDialog.Location;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;

public class ViewValidationRuleTestsAction extends AbstractAction {
	
	private boolean edit;
	
	private JDialog parentDialog;
	
	private JList rulesList;
	
	private Map<ValidationRule,Location> validationMap;
	
	
	public ViewValidationRuleTestsAction(JDialog parentDialog,
									JList rulesList,
									Map<ValidationRule,Location> validationMap) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.viewvalidationruletests"));
		this.parentDialog = parentDialog;
		this.edit = false;
		this.rulesList = rulesList;
		this.validationMap = validationMap;
	}
	
	public ViewValidationRuleTestsAction(JDialog parentDialog,
								   JList rulesList,
								   Map<ValidationRule, Location> validationMap,
								   boolean edit) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.editvalidationruletests"));
		this.edit = edit;
		this.parentDialog = parentDialog;
		this.rulesList = rulesList;
		this.validationMap = validationMap;
	}
	
	public void actionPerformed(ActionEvent aet) {
		ValidationRule rule = (ValidationRule)rulesList.getSelectedValue();
		if (edit) {
			new ConfigureValidationRuleTestsDialog(parentDialog, rule, edit);
			
			if (rule.getIsRevisionCandidate()) {
				validationMap.put(rule, Location.Edited);
				((ConfigureValidationRuleDialog)parentDialog).refreshTable();
			}
		} else {
			new ConfigureValidationRuleTestsDialog(parentDialog, rule);	
		}
	}
	
	
}