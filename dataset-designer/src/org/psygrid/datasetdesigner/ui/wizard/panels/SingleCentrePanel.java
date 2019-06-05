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
import java.awt.Dimension;
import java.awt.FlowLayout;

import java.util.ArrayList;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;

import org.psygrid.data.model.hibernate.Group;
import org.psygrid.data.model.hibernate.Site;

import org.psygrid.datasetdesigner.utils.ElementUtility;
import org.psygrid.datasetdesigner.utils.ListModelUtility;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.utils.SpringUtilities;

import org.psygrid.datasetdesigner.custom.TextFieldWithStatus;

import org.psygrid.datasetdesigner.ui.wizard.WizardPanel;
import org.psygrid.datasetdesigner.ui.wizard.WizardModel;

import org.psygrid.datasetdesigner.model.GroupModel;

public class SingleCentrePanel extends JPanel implements WizardPanel,
														ActionListener 
														{
	
	private TextFieldWithStatus groupNameField;
	private TextFieldWithStatus groupIdField;
	private TextFieldWithStatus geocodeField;
	private TextFieldWithStatus consultantField;
	
	private JList consultantList;
	
	private JButton addConsultantButton;
	private JButton removeConsultantButton;
	
	private WizardModel wm;
	
	public SingleCentrePanel(WizardModel wm) {
		super();
		this.wm = wm;
		setLayout(new BorderLayout());
		add(buildNorthPanel(), BorderLayout.NORTH);
		add(buildMainPanel(), BorderLayout.CENTER);
	}
	
	private JPanel buildNorthPanel() {
		JPanel northPanel = new JPanel();
		northPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		northPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.wizard.centreinformation")));
		return northPanel;
	}
	
	private JPanel buildMainPanel() {
		JPanel holderPanel = new JPanel();
		holderPanel.setLayout(new BorderLayout());
		
		JPanel mainPanel = new JPanel(new SpringLayout());
		groupNameField = new TextFieldWithStatus(40, true);
		groupIdField = new TextFieldWithStatus(40, true);
		geocodeField = new TextFieldWithStatus(20, true);
		consultantList = new JList();
		
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.groupname")));
		mainPanel.add(groupNameField);
		
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.groupid")));
		mainPanel.add(groupIdField);

		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.geocode")));
		mainPanel.add(geocodeField);
		
		SpringUtilities.makeCompactGrid(mainPanel,
                3, 2, //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad
		
		JPanel consultantPanel = new JPanel();
		consultantPanel.setBorder(BorderFactory.createTitledBorder(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.consultants")));
		consultantPanel.setLayout(new BorderLayout());
		JPanel addRemovePanel = new JPanel();
		addRemovePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		consultantField = new TextFieldWithStatus(40, false);
		addConsultantButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.add"));
		addConsultantButton.addActionListener(this);
		removeConsultantButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.remove"));
		removeConsultantButton.addActionListener(this);
		addRemovePanel.add(consultantField);
		addRemovePanel.add(addConsultantButton);
		addRemovePanel.add(removeConsultantButton);
		consultantPanel.add(addRemovePanel, BorderLayout.NORTH);
		consultantList = new JList();
		DefaultListModel consultantModel = new DefaultListModel();
		consultantList.setModel(consultantModel);
		JScrollPane consultantPane = new JScrollPane(consultantList);
		consultantPane.setPreferredSize(new Dimension(200, 100));
		consultantPane.setMinimumSize(new Dimension(200, 100));
		consultantPane.setMaximumSize(new Dimension(200, 100));
		consultantPanel.add(consultantPane, BorderLayout.CENTER);

		holderPanel.add(mainPanel, BorderLayout.NORTH);
		holderPanel.add(consultantPanel, BorderLayout.CENTER);
		
		return holderPanel;
	}

	public void refreshPanel() {
		// TODO Auto-generated method stub
		
	}
	
	public boolean next() {
		ArrayList<GroupModel> groupsList = new ArrayList<GroupModel>();
		GroupModel gm = new GroupModel();
		Site site = ElementUtility.createISite(groupNameField.getText(), 
				   groupIdField.getText(),
				   geocodeField.getText(),
				   ListModelUtility.convertListModelToStringList((DefaultListModel)consultantList.getModel()));
		ArrayList<Site> sites = new ArrayList<Site>();
		sites.add(site);
		Group group = ElementUtility.createIGroup(groupNameField.getText(), groupNameField.getText(), sites, null);
		gm.setGroup(group);
		gm.setId(groupIdField.getText());
		groupsList.add(gm);		
		//wm.getWizardDs().getDs().addGroup(group);
		wm.getWizardDs().setGroupModels(groupsList);
		return true;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == addConsultantButton) {
			((DefaultListModel)consultantList.getModel()).addElement(consultantField.getText());
			consultantField.setText("");
		} else if (e.getSource()== removeConsultantButton) {
			if (consultantList.getSelectedIndex() != -1) {
				((DefaultListModel)consultantList.getModel()).remove(consultantList.getSelectedIndex());
			}
		}
	}
	
	
	
}
