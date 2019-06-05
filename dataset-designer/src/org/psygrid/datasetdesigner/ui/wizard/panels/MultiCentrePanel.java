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
package org.psygrid.datasetdesigner.ui.wizard.panels;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import java.awt.Dimension;
import java.awt.FlowLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.psygrid.datasetdesigner.renderer.OptionListCellRenderer;
import org.psygrid.datasetdesigner.utils.ListModelUtility;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.utils.SpringUtilities;
import org.psygrid.datasetdesigner.custom.CustomCopyPasteJList;
import org.psygrid.datasetdesigner.custom.TextFieldWithStatus;

import org.psygrid.datasetdesigner.model.GroupModel;

import org.psygrid.datasetdesigner.utils.ElementUtility;

import org.psygrid.data.model.hibernate.Group;

import org.psygrid.datasetdesigner.ui.configurationdialogs.AddSiteDialog;
import org.psygrid.datasetdesigner.ui.wizard.WizardPanel;
import org.psygrid.datasetdesigner.ui.wizard.WizardModel;

public class MultiCentrePanel extends JPanel implements WizardPanel,
														ActionListener 
														{
	
	private TextFieldWithStatus groupNameField;
	private TextFieldWithStatus groupIdField;
	
	private JButton addSiteButton;
	private JButton editSiteButton;
	private JButton removeSiteButton;
	
	private JButton addCentreButton;
	private JButton editCentreButton;
	private JButton removeCentreButton;
	
	private JList siteList;
	
	private WizardModel wm;
	
	private JList groupsList;
	
	private JDialog parentDialog;
	
	public MultiCentrePanel(JDialog parentDialog, WizardModel wm) {
		super();
		this.parentDialog = parentDialog;
		this.wm = wm;
		setLayout(new BorderLayout());
		add(buildNorthPanel(), BorderLayout.NORTH);
		add(buildMainPanel(), BorderLayout.CENTER);
	}
	
	private JPanel buildNorthPanel() {
		JPanel northPanel = new JPanel();
		northPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		northPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.wizard.multicentreinformation")));
		return northPanel;
	}
	
	private JPanel buildMainPanel() {
		JPanel holderPanel = new JPanel();
		JPanel mainPanel = new JPanel(new SpringLayout());
		groupNameField = new TextFieldWithStatus(20, true);
		groupNameField.getDocument().addDocumentListener(new NameAndIdListener());
		groupIdField = new TextFieldWithStatus(20, true);
		groupIdField.getDocument().addDocumentListener(new NameAndIdListener());
		
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.groupname")));
		mainPanel.add(groupNameField);
		
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.groupid")));
		mainPanel.add(groupIdField);
		
		SpringUtilities.makeCompactGrid(mainPanel,
                2, 2, //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());
		topPanel.add(buildAddRemovePanel(), BorderLayout.NORTH);
		topPanel.add(mainPanel, BorderLayout.CENTER);
		
		JPanel leftPanel = new JPanel();
		leftPanel.setBorder(BorderFactory.createTitledBorder(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.managecentres")));
		leftPanel.setLayout(new BorderLayout());
		leftPanel.add(topPanel, BorderLayout.NORTH);
		leftPanel.add(buildSitesPanel(),  BorderLayout.CENTER);

		holderPanel.setLayout(new GridLayout(1, 2));
		holderPanel.add(leftPanel);
		holderPanel.add(buildGroupsListPanel());
		return holderPanel;
	}
	
	private JPanel buildSitesPanel() {
		JPanel sitesPanel = new JPanel();
		sitesPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		sitesPanel.setLayout(new BorderLayout());
		siteList = new CustomCopyPasteJList();
		siteList.setCellRenderer(new OptionListCellRenderer());
		
		siteList.setModel(new DefaultListModel());
		siteList.getModel().addListDataListener(new SiteListListener());
		JScrollPane sitePane = new JScrollPane(siteList);
		sitePane.setMinimumSize(new Dimension(80, 40));
		sitePane.setMaximumSize(new Dimension(80, 40));
		sitePane.setPreferredSize(new Dimension(80, 40));
		
		JPanel northPanel = new JPanel();
		JPanel buttonPanel = new JPanel();
		
		addSiteButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.addsite"));
		addSiteButton.addActionListener(this);
		editSiteButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.editsite "));
		editSiteButton.addActionListener(this);
		removeSiteButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.removesite"));
		removeSiteButton.addActionListener(this);
		
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.add(addSiteButton);
		buttonPanel.add(editSiteButton);
		buttonPanel.add(removeSiteButton);
		
		addSiteButton.setEnabled(false);
		removeSiteButton.setEnabled(false);

		siteList.addListSelectionListener(new ListSelectionListener() {
           	public void valueChanged(ListSelectionEvent event) {
                boolean enabled = true;

                if ((siteList == null) || (siteList.getSelectedValue() == null)) {
                    enabled = false;
                }

                if (removeSiteButton != null) {
                    removeSiteButton.setEnabled(enabled);
                }

                if (editSiteButton != null) {
                    editSiteButton.setEnabled(enabled);
                }
            }
        });
		
		northPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		northPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.groupsites")));
		northPanel.add(buttonPanel);
		sitesPanel.add(northPanel, BorderLayout.NORTH);
		
		sitesPanel.add(sitePane, BorderLayout.CENTER);
		return sitesPanel;
	}
	
	private JPanel buildGroupsListPanel() {
		JPanel groupsPanel = new JPanel();
		groupsPanel.setBorder(BorderFactory.createTitledBorder(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.projectgroupsmgmt")));
		groupsPanel.setLayout(new BorderLayout());

		JPanel northPanel = new JPanel();
		northPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		removeCentreButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.remove"));
		removeCentreButton.addActionListener(this);
		removeCentreButton.setEnabled(false);
		northPanel.add(removeCentreButton);
		
		groupsList = new CustomCopyPasteJList();
		groupsList.setModel(new DefaultListModel());
		groupsList.setCellRenderer(new OptionListCellRenderer());
		
		groupsList.addListSelectionListener(new ListSelectionListener() {
               	public void valueChanged(ListSelectionEvent event) {
                    boolean enabled = true;

                    if ((groupsList == null) || (groupsList.getSelectedValue() == null)) {
                        enabled = false;
                    }

                    if (removeCentreButton != null) {
                        removeCentreButton.setEnabled(enabled);
                    }
                }
            });
		
		JScrollPane groupPane = new JScrollPane(groupsList);
		groupPane.setMinimumSize(new Dimension(80, 80));
		groupPane.setPreferredSize(new Dimension(80, 80));
		groupPane.setMaximumSize(new Dimension(80, 80));
		groupsPanel.add(northPanel, BorderLayout.NORTH);
		groupsPanel.add(groupPane, BorderLayout.CENTER);
		return groupsPanel;

	}

	private JPanel buildAddRemovePanel() {
		JPanel addRemovePanel = new JPanel();
		addRemovePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		addCentreButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.addcentre"));
		addRemovePanel.add(addCentreButton);
		addCentreButton.addActionListener(this);
		addCentreButton.setEnabled(false);
		return addRemovePanel;
	}
	
	public boolean validatePanel() {
		boolean validated = true;
		
		if (groupsList.getModel().getSize() == 0 ) {
			JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.wizard.atleastonegroup"));
			validated = false;
		}
		
		for (int i=0; i<groupsList.getModel().getSize(); i++) {
			GroupModel gm = (GroupModel)groupsList.getModel().getElementAt(i);
			if (gm.getGroup().numSites() == 0) {
				JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.wizard.atleastonesite"));
				validated = false;
				break;
			}
		}
		
		return validated;
	}
	
	public void refreshPanel() {
	}
	
	public boolean next() {
		wm.getWizardDs().setGroups(ListModelUtility.convertListModelToGroupModelList((DefaultListModel)groupsList.getModel()));
		return true; 
	}

	private void clearEntryFields() {
		groupNameField.setText("");
		groupIdField.setText("");
		((DefaultListModel)siteList.getModel()).clear();
	}
	
	private boolean validateCentre() {
		boolean validCentre = true;
		
		String groupName = groupNameField.getText();

		if (groupName == null || groupName.equals("")) {
			JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.groupnamenotempty"));
			addSiteButton.setEnabled(false);
			return false;
		}

		if (groupIdField.getText() == null || groupIdField.getText().equals("")) {
			JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.groupidnotempty"));
			addSiteButton.setEnabled(false);
			return false;
		}
		
		//Figure out whether the groupFieldId is a numeric value that is between 0-1000 inclusive. If it is, disallow it.
		String centreCode = groupIdField.getText();
		int centreCodeNumeric = -999;
		boolean centreCodeIsAnInteger = true;
		try{
			centreCodeNumeric = Integer.parseInt(centreCode);
		}catch (NumberFormatException e){
			centreCodeIsAnInteger = false;
		}
		
		
		if(centreCodeIsAnInteger == true && centreCodeNumeric >= 0 && centreCodeNumeric <= 1000 && Integer.toString(centreCodeNumeric).equals(centreCode)) {		
			JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.groupidcannotbeinteger"));
			addSiteButton.setEnabled(false);
			return false;
		}
		
		if (groupIdField.getText().indexOf("-")!= -1 || groupIdField.getText().indexOf("/") != -1){
			JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.groupcodefieldinvalidchars"));
			return false;
		}

		
		//if it gets this far enabled add site
		addSiteButton.setEnabled(true);
		
		ArrayList<GroupModel> groups = ListModelUtility.convertListModelToGroupModelList((DefaultListModel)groupsList.getModel());
		for (GroupModel group: groups) {
			if (group.getGroup().getName().equals(groupName)) {
				JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.groupnamexists"));
				return false;
			}
		}
		
		if (siteList.getModel().getSize() ==0 ){
			JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.atleastonesite"));
			return false;
		}
		
		return validCentre;
	}
	
	private void updateAddButton() {
		boolean enableAdd = true;
		
		if (groupNameField.getText() == null || groupNameField.getText().equals("")) {
			enableAdd = false;
		}
		
		if (groupIdField.getText() == null || groupIdField.getText().equals("")) {
			enableAdd = false;
		}
		

		//if enable add true at this point, enable add site point
		addSiteButton.setEnabled(enableAdd);
		
		if (siteList.getModel().getSize() == 0) {
			enableAdd = false;
		}
		
		addCentreButton.setEnabled(enableAdd);
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == addSiteButton) {
			new AddSiteDialog(parentDialog, siteList, groupNameField.getText());
		} else if (e.getSource() == removeSiteButton) {
			if (siteList.getSelectedIndex() != -1) {
				((DefaultListModel)siteList.getModel()).remove(siteList.getSelectedIndex());
			}
		} else if (e.getSource() == addCentreButton) {
			if (validateCentre()) {
				Group group = ElementUtility.createIGroup(groupIdField.getText(),
                        groupNameField.getText(),
                        ListModelUtility.convertListModelToISiteList((DefaultListModel) siteList.getModel()),
                        null);
				GroupModel newGroupModel = new GroupModel();
				newGroupModel.setGroup(group);
				newGroupModel.setId(groupIdField.getText());

				((DefaultListModel)groupsList.getModel()).addElement(newGroupModel);
				//	clear the fields ready for a new entry
				clearEntryFields();
			}
		} else if (e.getSource() == removeCentreButton) {
			if (groupsList.getSelectedIndex() != -1) {
				//remove from the list
				((DefaultListModel)groupsList.getModel()).remove(groupsList.getSelectedIndex());
			}
		} else if (e.getSource() == editCentreButton) {
			if (groupsList.getSelectedIndex() != -1) {
				//remove from the list
				GroupModel group = (GroupModel)((DefaultListModel)groupsList.getModel()).getElementAt(groupsList.getSelectedIndex());
				groupNameField.setText(group.getGroup().getLongName());
				groupIdField.setText(group.getGroup().getName());
				siteList.setModel(ListModelUtility.convertArrayListToListModel(new ArrayList(((Group)group.getGroup()).getSites())));
			}
		}
	}
	

	private class NameAndIdListener implements DocumentListener {

		public void changedUpdate(DocumentEvent e) {
			updateAddButton();
		}

		public void insertUpdate(DocumentEvent e) {
			changedUpdate(e);
		}

		public void removeUpdate(DocumentEvent e) {
			changedUpdate(e);
		}
		
	}

	private class SiteListListener implements ListDataListener {

		public void contentsChanged(ListDataEvent e) {
			updateAddButton();
		}

		public void intervalAdded(ListDataEvent e) {
			contentsChanged(e);
		}

		public void intervalRemoved(ListDataEvent e) {
			contentsChanged(e);
		}
		
	}
	
	
}
