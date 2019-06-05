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

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.DefaultListModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListDataEvent;

import org.psygrid.data.model.hibernate.Status;
import org.psygrid.datasetdesigner.utils.ListModelUtility;
import org.psygrid.datasetdesigner.utils.SpringUtilities;
import org.psygrid.datasetdesigner.utils.ElementUtility;
import org.psygrid.datasetdesigner.model.StudyDataSet;
import org.psygrid.datasetdesigner.custom.TextFieldWithStatus;

import org.psygrid.datasetdesigner.actions.AssignAllowedRecordAction;
import org.psygrid.datasetdesigner.actions.UnassignAllowedRecordAction;
import org.psygrid.datasetdesigner.actions.AssignPrerequisiteAction;
import org.psygrid.datasetdesigner.actions.UnassignPrerequisiteAction;

import org.psygrid.datasetdesigner.controllers.DatasetController;

import org.psygrid.datasetdesigner.renderer.OptionListCellRenderer;

import org.psygrid.data.model.hibernate.DocumentGroup;

import org.psygrid.datasetdesigner.utils.Utils;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;


/**
 * Configuration dialog for adding a document group to the dataset 
 * 
 * @author pwhelan
 */
public class AddDocumentGroupDialog extends JDialog implements ActionListener {
		
	private JButton okButton;
	private JButton cancelButton;
	
	private TextFieldWithStatus documentGroupLabelField;
	private TextFieldWithStatus documentGroupNameField;
	
	private TextFieldWithStatus documentGroupDisplayTextField;
	private TextFieldWithStatus documentGroupDescriptionField;
	
	private JComboBox updateStatusComboBox;

	private JList allowedRecordsList;
	private JList documentGroupList;
	private JList prerequisiteList;
	private JList allAllowedRecordsList;
	private JList allPrerequisiteList;
	
	private DocumentGroup docGroup;
	
	private boolean edit = false;
	
	public AddDocumentGroupDialog(JDialog parentDialog, 
						JList documentGroupList) {
		super(parentDialog, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.adddocumentgroup"));
		this.documentGroupList = documentGroupList;
		setModal(true);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(buildMainPanel(), BorderLayout.CENTER);
		getContentPane().add(buildButtonPanel(), BorderLayout.SOUTH);
		init();
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	public AddDocumentGroupDialog(JDialog parentDialog,
								  JList documentGroupList, 
								  DocumentGroup docGroup) {
		super(parentDialog, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.editdocumentgroup")); 
		this.edit = true;
		this.documentGroupList = documentGroupList;
		this.docGroup = docGroup;
		setModal(true);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(buildMainPanel(), BorderLayout.CENTER);
		getContentPane().add(buildButtonPanel(), BorderLayout.SOUTH);
		init();
		pack();
		init(docGroup);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	private void init() {
		StudyDataSet dsSet = DatasetController.getInstance().getActiveDs();
		ArrayList<Status> statuses = dsSet.getStatuses();
		DefaultComboBoxModel statusModel = new DefaultComboBoxModel();
		DefaultListModel allRecordedStatusModel = new DefaultListModel();
		DefaultListModel allPrerequisiteModel = new DefaultListModel();
		DefaultComboBoxModel updateStatusBoxModel = new DefaultComboBoxModel();
		
		for (Status status: statuses) {
			statusModel.addElement(status);
			allRecordedStatusModel.addElement(status);
			updateStatusBoxModel.addElement(status);
		}
		
		updateStatusBoxModel.addElement(null);
		
		allAllowedRecordsList.setModel(allRecordedStatusModel);
		
		//if editing then, only show doc groups that are indexed higher in the list
		if (docGroup != null) {
			ArrayList<DocumentGroup> docGroups = ListModelUtility.convertListModelToDocGroupList((DefaultListModel)documentGroupList.getModel());
			for (DocumentGroup group : dsSet.getDocumentGroups()) {
				if (docGroups.indexOf(group) < docGroups.indexOf(docGroup)) {
					allPrerequisiteModel.addElement(group);
				}
			}
		} else {
			//if adding a new group, add all prereq groups
			for (DocumentGroup group : dsSet.getDocumentGroups()) {
					allPrerequisiteModel.addElement(group);
			}
		}
		
		allPrerequisiteList.setModel(allPrerequisiteModel);
		updateStatusComboBox.setModel(updateStatusBoxModel);
		prerequisiteList.setModel(new DefaultListModel());
		allowedRecordsList.setModel(new DefaultListModel());
	}

	private void init(DocumentGroup docGroup) {
		documentGroupLabelField.setText(docGroup.getLabel());
		documentGroupNameField.setText(docGroup.getName());
		documentGroupDisplayTextField.setText(docGroup.getDisplayText());
		documentGroupDescriptionField.setText(docGroup.getDescription());
		
		updateStatusComboBox.setSelectedItem(docGroup.getUpdateStatus());
		
		for (Status group: docGroup.getAllowedRecordStatus()) {
			((DefaultListModel)allAllowedRecordsList.getModel()).removeElement(group);
			((DefaultListModel)allowedRecordsList.getModel()).addElement(group);
		}

		if (((DefaultListModel)allPrerequisiteList.getModel()).contains(docGroup)) {
			((DefaultListModel)allPrerequisiteList.getModel()).removeElement(docGroup);
		}
		
		for (DocumentGroup group: docGroup.getPrerequisiteGroups()) {
			((DefaultListModel)allPrerequisiteList.getModel()).removeElement(group);
			((DefaultListModel)prerequisiteList.getModel()).addElement(group);
			
		}
		
	}
	
	private JPanel buildMainPanel() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(buildDetailsPanel());
		mainPanel.add(buildAllowedRecordPanel());
		mainPanel.add(buildPrerequisitePanel());
		return mainPanel;
	}
	
	private JPanel buildDetailsPanel() {
		JPanel mainPanel = new JPanel(new SpringLayout());
		documentGroupLabelField = new TextFieldWithStatus(40, false);
		documentGroupNameField = new TextFieldWithStatus(40, true);
		documentGroupDisplayTextField = new TextFieldWithStatus(40, true);
		documentGroupDescriptionField = new TextFieldWithStatus(40, false);
		
		updateStatusComboBox = new JComboBox();
		
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.documentgroupname")));
		mainPanel.add(documentGroupNameField);
		
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.documentgroupdisplaytext")));
		mainPanel.add(documentGroupDisplayTextField);
		
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.documentgrouplabel")));
		mainPanel.add(documentGroupLabelField);
		
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.documentgroupdescription")));
		mainPanel.add(documentGroupDescriptionField);
		
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.updatestatus")));
		mainPanel.add(updateStatusComboBox);
		updateStatusComboBox.setRenderer(new OptionListCellRenderer());
		
		SpringUtilities.makeCompactGrid(mainPanel,
                5, 2, //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad

		return mainPanel;
	}
	
	private JPanel buildAllowedRecordPanel() {
		JPanel allowedRecordPanel = new JPanel();
		allowedRecordPanel.setLayout(new BoxLayout(allowedRecordPanel, BoxLayout.X_AXIS));
		allAllowedRecordsList = new JList();
		allAllowedRecordsList.setCellRenderer(new OptionListCellRenderer());
		allowedRecordsList = new JList();
		allowedRecordsList.setCellRenderer(new OptionListCellRenderer());
		JButton assignButton = new JButton(new AssignAllowedRecordAction(allAllowedRecordsList, allowedRecordsList));
		JButton unassignButton = new JButton(new UnassignAllowedRecordAction(allAllowedRecordsList, allowedRecordsList));
		allowedRecordPanel.add(Utils.createSubPanel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.allallowedrecordstatus"), allAllowedRecordsList));
		allowedRecordPanel.add(Utils.createArrowPanel(unassignButton, assignButton));
		allowedRecordPanel.add(Utils.createSubPanel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.allowedrecordstatus"), allowedRecordsList));
		return allowedRecordPanel;
	}
	
	private JPanel buildPrerequisitePanel() {
		JPanel prerequisitePanel = new JPanel();
		prerequisitePanel.setLayout(new BoxLayout(prerequisitePanel, BoxLayout.X_AXIS));
		allPrerequisiteList = new JList();
		allPrerequisiteList.setCellRenderer(new OptionListCellRenderer());
		prerequisiteList = new JList();
		prerequisiteList.setCellRenderer(new OptionListCellRenderer());
		JButton assignButton = new JButton(new AssignPrerequisiteAction(allPrerequisiteList, prerequisiteList));
		JButton unassignButton = new JButton(new UnassignPrerequisiteAction(allPrerequisiteList, prerequisiteList));
		prerequisitePanel.add(Utils.createSubPanel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.allallowedprerequisites"), allPrerequisiteList));
		prerequisitePanel.add(Utils.createArrowPanel(unassignButton, assignButton));
		prerequisitePanel.add(Utils.createSubPanel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.prerequisites") , prerequisiteList));
		return prerequisitePanel;
	}
	
	public JPanel buildButtonPanel(){
		okButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ok"));
		okButton.addActionListener(this);
		cancelButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.cancel"));
		cancelButton.addActionListener(this);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		return buttonPanel;
	}
	
	public boolean validateEntries() {
		if (documentGroupNameField.getText() == null || documentGroupNameField.getText().equals("")) {
			JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.nonemptydocumentgroup"));
			return false;
		}

		if (documentGroupDisplayTextField.getText() == null || documentGroupDisplayTextField.getText().equals("")) {
			JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.nonemptydisplaytext"));
			return false;
		}
		
		if (allowedRecordsList.getModel().getSize() == 0) {
			JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.atleastoneassignedstatus"));
			return false;
		}
		
		return true;
	}
	
	/** 
	 * Apply changes
	 */
	public void ok() {
		if (!edit)
		{
			DocumentGroup docGroup = ElementUtility.createIDocumentGroup(documentGroupNameField.getText(),
                    documentGroupDisplayTextField.getText(),
                    documentGroupLabelField.getText(),
                    documentGroupDescriptionField.getText(),
                    ListModelUtility.convertListModelToIStatusList((DefaultListModel) allowedRecordsList.getModel()),
                    ListModelUtility.convertListModelToIDocGroupList((DefaultListModel) prerequisiteList.getModel()),
                    ((Status) updateStatusComboBox.getSelectedItem()));
						if (documentGroupList.getModel() == null) {
							documentGroupList.setModel(new DefaultListModel());
					}
			((DefaultListModel)documentGroupList.getModel()).addElement(docGroup);
		} else {
			docGroup.setName(documentGroupNameField.getText());
			docGroup.setDisplayText(documentGroupDisplayTextField.getText());
			docGroup.setDescription(documentGroupDescriptionField.getText());
			docGroup.setLabel(documentGroupLabelField.getText());
			docGroup.setAllowedRecordStatus(ListModelUtility.convertListModelToIStatusList(((DefaultListModel)allowedRecordsList.getModel())));
			ArrayList<DocumentGroup> newprereqs = ListModelUtility.convertListModelToIDocGroupList((DefaultListModel)prerequisiteList.getModel());
			((DocumentGroup)docGroup).setPrerequisiteGroups(newprereqs);
			docGroup.setUpdateStatus(((Status)updateStatusComboBox.getSelectedItem()));

			//fire contents_changed event to ensure validation is updated
			if (documentGroupList.getModel() != null) {
				ListDataListener[] listeners = ((DefaultListModel)documentGroupList.getModel()).getListDataListeners();
				for (int i=0; i<listeners.length; i++) {
					listeners[i].contentsChanged(new ListDataEvent(documentGroupList, ListDataEvent.CONTENTS_CHANGED, 0, documentGroupList.getModel().getSize()));
				}
			}
		}
	}
	
	public void actionPerformed(ActionEvent aet) {
		if (aet.getSource() == okButton) {
			if (validateEntries()) {
				ok();
				this.dispose();
			}
		} else if (aet.getSource() == cancelButton) {
			this.dispose();
		} 
	}
	
}