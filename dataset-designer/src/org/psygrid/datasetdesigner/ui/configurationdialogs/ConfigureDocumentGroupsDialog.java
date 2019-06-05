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
package org.psygrid.datasetdesigner.ui.configurationdialogs;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import java.util.ArrayList;

import org.psygrid.datasetdesigner.actions.AddDocumentGroupAction;
import org.psygrid.datasetdesigner.ui.MainFrame;

import org.psygrid.datasetdesigner.utils.HelpHelper;
import org.psygrid.datasetdesigner.utils.ListModelUtility;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.controllers.DatasetController;

import org.psygrid.data.model.hibernate.DocumentGroup;
import org.psygrid.data.model.hibernate.Status;

/**
 * Class to manage the setting up
 * and editing of document groups (aka study stages)
 * @author pwhelan
 */
public class ConfigureDocumentGroupsDialog extends AbstractConfigurationDialog implements ActionListener {

	private String validationString;
	
	public ConfigureDocumentGroupsDialog(MainFrame frame, boolean readOnly) {
		super(frame, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.configuredocgroups"), false, readOnly, true, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.docgroupsmgmt"), true, true, true);
	}

	protected void init() {
		if (activeDs != null) {
			list.setModel(ListModelUtility.convertArrayListToListModel(activeDs.getDocumentGroups()));
			list.getModel().addListDataListener(new DataChangedListener());
		}
		
		//update the validation label based on currently configured groups
		updateValidationLabel(validateDocGroups(), validationString);
		
	}

	protected void initButtons() {
		super.initButtons();
		addButton = new JButton(new AddDocumentGroupAction(this, list));
		editButton = new JButton(new AddDocumentGroupAction(this, list, true));
		
		if (DatasetController.getInstance().getActiveDs().getDs().isPublished()) {
			addButton.setEnabled(false);
		}
	}

	@Override
	protected void save() {
		if (activeDs != null) {
			activeDs.setDocumentGroups(ListModelUtility.convertListModelToDocGroupList((DefaultListModel)list.getModel()));
		}
	}
	
    protected  JPanel buildValidatePanel() {
    	JPanel validatePanel = new JPanel();
    	validatePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    	validatePanel.add(HelpHelper.getInstance().getHelpButtonWithID("dsddocgroupvalidatepanel"));
    	validatePanel.add(new JLabel("Validated: "));
    	validateLabel = new JLabel(crossIcon);
    	validatePanel.add(validateLabel);
    	return validatePanel;
    }

	
	private boolean validateDocGroups() {
		boolean validated = true;
		
		validationString = null;
		
		//check that prerequisites are higher in the list
		ArrayList<DocumentGroup> docGroups = ListModelUtility.convertListModelToDocGroupList((DefaultListModel)list.getModel());
		
		for (DocumentGroup docGroup: docGroups) {
			for (DocumentGroup group: docGroup.getPrerequisiteGroups()) {
				if (docGroups.indexOf(group) > docGroups.indexOf(docGroup)) {
					if (validationString == null) {
						validationString = "<html>";
					}
					
					validationString += ("The group " + group.getName() + " is a prerequisite of " + docGroup.getName() + " but is positioned before it.<br>");
					validated = false;
				}
			}
			
			//update status must be a valid transition from one of the allowed rec statuses
			if (docGroup != null) {
				if (docGroup.getUpdateStatus() != null) {
					boolean transPossible = false;
					for (Status status: docGroup.getAllowedRecordStatus()) {
						ArrayList<Status> trans = new ArrayList(((Status)status).getStatusTransitions());
						if (trans.contains(docGroup.getUpdateStatus())) {
							transPossible = true;
						}
					}
					
					if (!transPossible) {
						if (validationString == null) {
							validationString = "<html>";
						}
						
						validationString += ("The update status " + docGroup.getUpdateStatus().getShortName()  + " in the document group "  + docGroup.getName() + " is not a valid transition.<br>");
						validated = false;
					}
				}
			}
		}
		
		if (validationString != null) {
			validationString += "</html>";
		}
		
		return validated;
	}

	private class DataChangedListener implements ListDataListener {

		public void contentsChanged(ListDataEvent e) {
			updateValidationLabel(validateDocGroups(), validationString);
		}

		public void intervalAdded(ListDataEvent e) {
			contentsChanged(e);
		}

		public void intervalRemoved(ListDataEvent e) {
			contentsChanged(e);
		}
		
	}
	
}