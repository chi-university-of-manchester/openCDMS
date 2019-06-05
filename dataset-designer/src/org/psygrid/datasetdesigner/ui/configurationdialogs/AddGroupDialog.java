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
import java.awt.Dimension;
import java.awt.FlowLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;

import javax.swing.DefaultListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.psygrid.datasetdesigner.utils.ListModelUtility;
import org.psygrid.datasetdesigner.utils.SpringUtilities;

import org.psygrid.data.model.hibernate.Group;
import org.psygrid.data.model.hibernate.Site;

import org.psygrid.datasetdesigner.renderer.OptionListCellRenderer;

import org.psygrid.datasetdesigner.model.GroupModel;
import org.psygrid.datasetdesigner.controllers.DatasetController;

import org.psygrid.datasetdesigner.utils.ElementUtility;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.custom.TextFieldWithStatus;

public class AddGroupDialog extends JDialog implements ActionListener {
		
	private JButton okButton;
	private JButton cancelButton;
	
	private TextFieldWithStatus groupNameField;
	private TextFieldWithStatus groupIdField;
	
	private JList groupList;
	private JList allSecondaryGroupList;
	private JList siteList;
	
	private JButton addSiteButton;
	private JButton editSiteButton;
	private JButton removeSiteButton;
	
	private GroupModel groupModel;
	
	/**
	 * Constructor : used for adding
	 * @param parentDialog the owner
	 * @param groupList the list to populate the dialog with
	 */
	public AddGroupDialog(JDialog parentDialog, JList groupList) {
		this(parentDialog, groupList, null);
		setTitle(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.addgroup"));
	}

	/**
	 * Constructor : used for editing 
	 * @param parentDialog the owner
	 * @param groupList the list 
	 * @param groupModel the model of groups
	 */
	public AddGroupDialog(JDialog parentDialog,					  
						  JList groupList, 
						  GroupModel groupModel) {
		super(parentDialog, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.editgroup"));
		this.groupList = groupList;
		this.groupModel = groupModel;
		setModal(true);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(buildMainPanel(), BorderLayout.CENTER);
		getContentPane().add(buildButtonPanel(), BorderLayout.SOUTH);
		pack();
		init(groupModel);
		//disabled fields depending on published status of datasets
		enableFields();
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	/**
	 * Set the group model
	 * @param groupModel
	 */
	private void init(GroupModel groupModel) {
		if (groupModel != null) {
			groupNameField.setText(groupModel.getGroup().getLongName());
			groupIdField.setText(groupModel.getGroup().getName());
			
			//set site model
			DefaultListModel siteModel = new DefaultListModel();
			for (int i=0; i<groupModel.getGroup().numSites(); i++) {
				siteModel.addElement(groupModel.getGroup().getSite(i));
			}
			siteList.setModel(siteModel);
			
			//all secondary sites
			DefaultListModel secondaryGroupModel = new DefaultListModel();
			for (int j=0; j<groupModel.getGroup().numSecondaryGroups(); j++) {
				secondaryGroupModel.addElement(groupModel.getGroup().getSecondaryGroup(j));
				((DefaultListModel)allSecondaryGroupList.getModel()).removeElement(groupModel.getGroup().getSecondaryGroup(j));
			}
		}
	}
	
	/**
	 * Enable fields based on published status of the dataset
	 * 
	 */
	private void enableFields() {
		//if dataset is published
		if (DatasetController.getInstance().getActiveDs().getDs().isPublished() && groupModel != null) {
			groupIdField.setEnabled(false);
		}
	}
	
	
	/**
	 * Show the details and site panel
	 * @return the configured main panel
	 */
	private JPanel buildMainPanel() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(buildDetailsPanel());
		mainPanel.add(buildSitesPanel());
		return mainPanel;
	}
	
	/**
	 * Build the details panel to take name
	 * and id 
	 * @return the details panel
	 */
	private JPanel buildDetailsPanel() {
		JPanel mainPanel = new JPanel(new SpringLayout());
		groupNameField = new TextFieldWithStatus(40, true);
		groupIdField = new TextFieldWithStatus(40, true);
		
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.groupname")));
		mainPanel.add(groupNameField);
		
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.groupid")));
		mainPanel.add(groupIdField);
		
		SpringUtilities.makeCompactGrid(mainPanel,
                2, 2, //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad

		return mainPanel;
	}
	
	/**
	 * Build the sites panel
	 * @return the configured sites panel
	 */
	private JPanel buildSitesPanel() {
		JPanel sitesPanel = new JPanel();
		sitesPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		sitesPanel.setLayout(new BorderLayout());
		siteList = new JList();
		siteList.setCellRenderer(new OptionListCellRenderer());
		siteList.setModel(new DefaultListModel());
		JScrollPane sitePane = new JScrollPane(siteList);
		sitePane.setMinimumSize(new Dimension(200, 200));
		sitePane.setPreferredSize(new Dimension(200, 200));
		sitesPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.groupsites")), BorderLayout.NORTH);
		sitesPanel.add(sitePane, BorderLayout.CENTER);
		addSiteButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.addsite"));
		addSiteButton.addActionListener(this);
		editSiteButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.editsite"));
		editSiteButton.addActionListener(this);
		editSiteButton.setEnabled(false);
		removeSiteButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.removesite"));
		removeSiteButton.addActionListener(this);
		removeSiteButton.setEnabled(false);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.add(addSiteButton);
		buttonPanel.add(editSiteButton);
		buttonPanel.add(removeSiteButton);

		sitesPanel.add(buttonPanel, BorderLayout.SOUTH);

        //Only enable the buttons if an item is selected
		siteList.addListSelectionListener(new ListSelectionListener() {
               	public void valueChanged(ListSelectionEvent event) {
                    boolean enabled = true;

                    if ((siteList == null) || (siteList.getSelectedValue() == null)) {
                        enabled = false;
                    }

                    if (removeSiteButton != null && !(DatasetController.getInstance().getActiveDs().getDs().isPublished())) {
                        removeSiteButton.setEnabled(enabled);
                    }

                    if (editSiteButton != null && !(DatasetController.getInstance().getActiveDs().getDs().isPublished())) {
                        editSiteButton.setEnabled(enabled);
                    }
                }
            });


		return sitesPanel;
	}
	
	/** 
	 * Build the ok/ cancel button panel
	 */
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
	
	/**
	 * Validate the entries for groups
	 * @return
	 */
	public boolean validateEntries() {
		String groupName = groupNameField.getText();

		if (groupName == null || groupName.equals("")) {
			JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.groupnamenotempty"));
			return false;
		}
		if (groupIdField.getText().indexOf("-")!= -1 || groupIdField.getText().indexOf("/") != -1){
			JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.groupcodefieldinvalidchars"));
			return false;
		}

		if (groupIdField.getText() == null || groupIdField.getText().equals("")) {
			JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.groupidnotempty"));
			return false;
		}
		
		ArrayList<GroupModel> groups = ListModelUtility.convertListModelToGroupModelList((DefaultListModel)groupList.getModel());
		for (GroupModel group: groups) {
			if (group != groupModel) {
				if (group.getGroup().getName().equals(groupName)) {
					JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.groupnamexists"));
					return false;
				}
			}
		}
		
		if (siteList.getModel().getSize() ==0 ){
			JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.atleastonesite"));
			return false;
		}
		
		return true;
	}
	
	/**
	 * Handle ok, cancel etc.
	 * @param aet the calling event
	 */
	public void actionPerformed(ActionEvent aet) {
		if (aet.getSource() == okButton) {
			if (validateEntries()) {
				if (groupModel == null) {
					Group docGroup = ElementUtility.createIGroup(groupIdField.getText(),
                            groupNameField.getText(),
                            ListModelUtility.convertListModelToISiteList((DefaultListModel) siteList.getModel()),
                            null);
					GroupModel gModel = new GroupModel();
					gModel.setGroup(docGroup);
					gModel.setId(groupIdField.getText());
				    ((DefaultListModel)groupList.getModel()).addElement(gModel);
				} else {
					groupModel.getGroup().getName();
					((Group)groupModel.getGroup()).setName(groupIdField.getText());
					((Group)groupModel.getGroup()).setLongName(groupNameField.getText());
					groupModel.setId(groupIdField.getText());
					((Group)groupModel.getGroup()).setSites(ListModelUtility.convertListModelToSiteList((DefaultListModel)siteList.getModel()));
				} 
				this.dispose();
			}
	} else if (aet.getSource() == cancelButton) {
			this.dispose();
	} else if (aet.getSource() == addSiteButton) {
			new AddSiteDialog(this, siteList);
	} else if (aet.getSource() == editSiteButton) {
		if (siteList.getSelectedIndex() != -1) {
				new AddSiteDialog(this, siteList, (Site)siteList.getSelectedValue());
		}
	} else if (aet.getSource() == removeSiteButton) {
			((DefaultListModel)siteList.getModel()).removeElement(siteList.getSelectedValue());
		}
	}
}