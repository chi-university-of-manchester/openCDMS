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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import javax.swing.SpringLayout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.collection.entry.security.SecurityHelper;
import org.psygrid.datasetdesigner.model.DocTreeModel;
import org.psygrid.datasetdesigner.model.GroupModel;
import org.psygrid.datasetdesigner.utils.SpringUtilities;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.controllers.DatasetController;
import org.psygrid.datasetdesigner.custom.TextFieldWithStatus;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Site;

import org.psygrid.data.model.hibernate.Group;

import org.psygrid.security.attributeauthority.client.AAManagementClient;

import org.psygrid.www.xml.security.core.types.ProjectType;

public class ConfigureDatasetPropertiesDialog extends JDialog implements ActionListener {

	private static final Log LOG = LogFactory.getLog(ConfigureDatasetPropertiesDialog.class);
	
	private JButton okButton;
	private JButton cancelButton;
	
	private TextFieldWithStatus nameField;
	private TextFieldWithStatus codeField;
	private TextFieldWithStatus ukcrnCodeField;
	private TextFieldWithStatus scheduleStartField;
	private TextFieldWithStatus descriptionField;
		
	private JCheckBox eslBox;
	private JCheckBox randomisationBox;
	private JCheckBox sendMonthlyEmailsBox;
	private JCheckBox exportSecurityBox;
	private JCheckBox useReviewAndApproveBox;
	
	private DataSet dataset;
	
	public ConfigureDatasetPropertiesDialog(JFrame frame) {
		super(frame, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.configuredatasetproperties"));
		this.dataset = DatasetController.getInstance().getActiveDs().getDs();
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(buildMainPanel(), BorderLayout.CENTER);
		getContentPane().add(buildButtonPanel(), BorderLayout.SOUTH);
		init();
		pack();
		if (DatasetController.getInstance().getActiveDs().isReadOnly()
				|| DatasetController.getInstance().getActiveDs().getDs().isPublished()) {
			setEnabledComponents(false);
		}
		
		//if ds is published, then enable the ukcrn code field
		if (DatasetController.getInstance().getActiveDs().getDs().isPublished()) {
			ukcrnCodeField.setEnabled(true);
		}
		
		setLocationRelativeTo(null);  
		setVisible(true);
	}
	
	private void setEnabledComponents(boolean enabled) {
		nameField.setEnabled(enabled);
		codeField.setEnabled(enabled);
		eslBox.setEnabled(enabled);
		randomisationBox.setEnabled(enabled);
		ukcrnCodeField.setEnabled(enabled);
		useReviewAndApproveBox.setEnabled(enabled);
		sendMonthlyEmailsBox.setEnabled(enabled);
		scheduleStartField.setEnabled(enabled);
		descriptionField.setEnabled(enabled);
	}
	
	private void init() {
		nameField.setText(dataset.getName());
		codeField.setText(dataset.getProjectCode());
		//set the ukcrn code field here
		ukcrnCodeField.setText(DatasetController.getInstance().getActiveDs().getUkcrnCode());
		scheduleStartField.setText(dataset.getScheduleStartQuestion());
		eslBox.setSelected(dataset.isEslUsed());
		randomisationBox.setSelected(dataset.isRandomizationRequired());
		if (dataset.isRandomizationRequired()) {
			eslBox.setEnabled(false);
		}
		descriptionField.setText(dataset.getDescription());
		randomisationBox.addActionListener(new CheckBoxListener());
		sendMonthlyEmailsBox.setSelected(dataset.isSendMonthlySummaries());
		useReviewAndApproveBox.setSelected(!dataset.isNoReviewAndApprove());
		
//		exportSecurityBox.setSelected(dataset.getExportSecurityActive());
	}
	
	private JPanel buildMainPanel() {
		JPanel holderPanel = new JPanel();
		holderPanel.setLayout(new BorderLayout());
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new SpringLayout());
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.datasetname")));
		nameField = new TextFieldWithStatus(15, true);
		mainPanel.add(nameField);
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.datasetcode")));
		codeField = new TextFieldWithStatus(15, true);
		mainPanel.add(codeField);
		ukcrnCodeField = new TextFieldWithStatus(15, true);
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.datasetukcrncode")));
		mainPanel.add(ukcrnCodeField);
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.datasetdescription")));
		descriptionField = new TextFieldWithStatus(15, false);
		mainPanel.add(descriptionField);
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.schedulestartquestion")));
		scheduleStartField = new TextFieldWithStatus(50, false);
		mainPanel.add(scheduleStartField);
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.useelectronicscreeninglog")));
		eslBox = new JCheckBox();
		mainPanel.add(eslBox);
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.userandomisation")));
		randomisationBox = new JCheckBox();
		mainPanel.add(randomisationBox);
		sendMonthlyEmailsBox = new JCheckBox();
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.sendmonthlyemails")));
		mainPanel.add(sendMonthlyEmailsBox);
		useReviewAndApproveBox = new JCheckBox();
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.usereviewandapprove")));
		mainPanel.add(useReviewAndApproveBox);
//		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.useexportsecurity")));
//		exportSecurityBox = new JCheckBox();
//		mainPanel.add(exportSecurityBox);
		
		SpringUtilities.makeCompactGrid(mainPanel,
                9, 2, 			//rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad

		holderPanel.add(mainPanel, BorderLayout.NORTH);
		//holderPanel.add(new JPanel(), BorderLayout.CENTER);
		holderPanel.add(buildSouthPanel(), BorderLayout.CENTER);
		
		return holderPanel;
	}
	
	private JScrollPane buildSouthPanel() {
		JPanel southPanel = new JPanel();
		southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
		ArrayList<GroupModel> groupModels = DatasetController.getInstance().getActiveDs().getGroups();

		for (GroupModel group: groupModels) {
			Group g = (Group)group.getGroup();
			southPanel.add(new JLabel("\t \t Centre: " + g.getLongName() + " (" + g.getName() + ")" + "\n"));
			for (Site site: g.getSites()) {
				southPanel.add(new JLabel("\n \t \t \t \t \tSite: " + site.getSiteName() +  " (" + site.getSiteId() + ")" + "\n"));
			}
		}
		
		//southPanel.setPreferredSize(new Dimension(200,100));
		//southPanel.setMaximumSize(new Dimension(200,100));
		JScrollPane southScroll = new JScrollPane(southPanel);
		//southScroll.setPreferredSize(new Dimension(200,100));
		//southScroll.setMaximumSize(new Dimension(200,100));
		
		return southScroll;
	}
	
	private JPanel buildButtonPanel(){
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
	
	public boolean validatePanel() {
		if (nameField.getText().equals("")) {
			JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.wizard.nonemptyname"));
			return false;
		}
			
		if (codeField.getText().equals("")) {
			JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.wizard.nonemptycode"));
			return false;
		}
		
		String name = nameField.getText();
		String code = codeField.getText();

		if (code.indexOf("-")!= -1 || code.indexOf("/") != -1){
			JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.wizard.invalidchars"));
			return false;
		}
		
		ArrayList<DataSet> dsSets = DocTreeModel.getInstance().getAllDatasets();
		for (int i= 0; i<dsSets.size(); i++) {
			DataSet curSet = dsSets.get(i);
			
			if (!dataset.equals(curSet)) {
				if (curSet.getName().equalsIgnoreCase(name)) {
					JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.wizard.samename"));
					return false;
				} else if (curSet.getProjectCode().equalsIgnoreCase(code)) {
					JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.wizard.samecode"));
					return false;
				}
			}
		}
		
		//if the name has changed
		//if a project of this name already exists in the repository, return false for
		//validation and show message dialog to user
		if (!dataset.getName().equals(name) || !dataset.getProjectCode().equals(code)) {
			try {
				AAManagementClient amclient = SecurityHelper.getAAManagementClient();
				if (amclient.getPort().projectExists(new ProjectType(name, code, null, null, false)))
				{
					JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.wizard.projectexists"));
					return false;
				}
			} catch (Exception ex) {
				LOG.error("Error checking if project exists" ,ex);
				return false;
			}
		}
		
		
		return true;
	}
	
	/**
	 * Checks if the active study's published flag is set to true
	 * If it is, then check if the new name and code are a new project
	 * --> if so, set the published flag to false 
	 *
	 */
	private void checkIfActiveStudyWasPublished() {
		DataSet activeDs = DatasetController.getInstance().getActiveDs().getDs();
		String newName = nameField.getText();
		String newCode = codeField.getText();
		
		//if the active ds is set to published
		//and both name and code have been changed
		//then check if the new project exists in the AA 
		//this checks for all users in all projects
		//if not, safe to assume that it's a new project
		//so set the published flag to false
		//otherwise, users will never be able to save 
		//renamed published datasets
		if (activeDs.isPublished()) {
			if (!newName.equals(activeDs.getName()) &&
				 !newCode.equals(activeDs.getProjectCode())) {
				try {
					AAManagementClient amclient = SecurityHelper.getAAManagementClient();
					if (!amclient.getPort().projectExists(new ProjectType(newName, newCode, null, null, false)))
					{
					 	((DataSet)activeDs).setPublished(false);
					 	((DataSet)activeDs).setId(null);
					}
				} catch (Exception ex) {
					LOG.error("Error checking if project exists" ,ex);
				}
			 }
		 }
	}
	
	public void actionPerformed(ActionEvent aet) {
		if (aet.getSource() == okButton) {
			if (!DatasetController.getInstance().getActiveDs().isReadOnly()) {
				if (validatePanel()) {
					//if new name and code passed & ds was published previously, then 
					//set this to unpublished status now
					checkIfActiveStudyWasPublished();
					
					//set the UKCRN code
					DatasetController.getInstance().getActiveDs().setUkcrnCode(ukcrnCodeField.getText());
					
					dataset.setName(nameField.getText());
					//also update the display text here so that the preview shows correctly
					dataset.setDisplayText(nameField.getText());
					dataset.setEslUsed(eslBox.isSelected());
					dataset.setRandomizationRequired(randomisationBox.isSelected());
//					dataset.setExportSecurityActive(exportSecurityBox.isSelected());
					if (scheduleStartField.getText().equals("")) {
						dataset.setScheduleStartQuestion(null);
					} else {
						dataset.setScheduleStartQuestion(scheduleStartField.getText());
					}
					
					dataset.setNoReviewAndApprove(!useReviewAndApproveBox.isSelected());
					dataset.setDescription(descriptionField.getText());
					dataset.setProjectCode(codeField.getText());
					dataset.setSendMonthlySummaries(sendMonthlyEmailsBox.isSelected());
					this.dispose();
				}
			} else {
				this.dispose();
			}
		} else if (aet.getSource() == cancelButton) {
			this.dispose();
		}
	}
	
	private class CheckBoxListener implements ActionListener {
		
		public void actionPerformed(ActionEvent aet) {
			if (randomisationBox.isSelected()) {
				eslBox.setSelected(true);
				eslBox.setEnabled(false);
			} else {
				eslBox.setEnabled(true);
			}
		}
	}
	
	
}