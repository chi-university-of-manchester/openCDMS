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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.collection.entry.security.SecurityHelper;
import org.psygrid.data.model.hibernate.HibernateFactory;
import org.psygrid.datasetdesigner.utils.ElementUtility;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.utils.SpringUtilities;
import org.psygrid.datasetdesigner.custom.TextFieldWithStatus;

import org.psygrid.datasetdesigner.ui.wizard.WizardPanel;
import org.psygrid.datasetdesigner.ui.wizard.WizardModel;

import org.psygrid.data.model.hibernate.ConsentFormGroup;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.PrimaryConsentForm;

import org.psygrid.datasetdesigner.model.GroupModel;

import org.psygrid.datasetdesigner.utils.HelpHelper;
import org.psygrid.security.attributeauthority.client.AAManagementClient;

import org.psygrid.www.xml.security.core.types.ProjectType;

/**
 * Wizard panel containing the basic properties for a dataset
 * study name, code etc.
 * 
 * @author pwhelan
 */
public class BasicPropsPanel extends JPanel implements WizardPanel, ActionListener {
	
	private static final Log LOG = LogFactory.getLog(BasicPropsPanel.class);
	
	private TextFieldWithStatus nameField;
	private TextFieldWithStatus codeField;
	private TextFieldWithStatus ukcrnCodeField;
	private TextFieldWithStatus descriptionField;

	private JCheckBox randomisationBox;

	private JLabel groupLabel;
	private JComboBox groupBox;
	
	private JPanel mainPanel;
	
	private WizardModel wm;
	
	public BasicPropsPanel(WizardModel wm) {
		super();
		this.wm = wm;
		setLayout(new BorderLayout());
		add(buildNorthPanel(), BorderLayout.NORTH);
		add(buildMainPanel(), BorderLayout.CENTER);
		init();
		
	}

	private JPanel buildNorthPanel() {
		JPanel northPanel = new JPanel();
		northPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		northPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.wizard.basic")));
		return northPanel;
	}
	
	private JPanel buildMainPanel() {
		JPanel holderPanel = new JPanel();
		holderPanel.setLayout(new BorderLayout());
		
		int rows;
		
		mainPanel = new JPanel();
		mainPanel.setLayout(new SpringLayout());
		
		NextButtonListener docListener = new NextButtonListener();

		mainPanel.add(HelpHelper.getInstance().getHelpButtonWithID("dsdwizardstudyname"));
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.datasetname")));
		nameField = new TextFieldWithStatus(15, true);
		nameField.getDocument().addDocumentListener(docListener);
		mainPanel.add(nameField);
		
		mainPanel.add(HelpHelper.getInstance().getHelpButtonWithID("dsdwizardstudycode"));
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.datasetcode")));
		codeField = new TextFieldWithStatus(15, true);
		codeField.getDocument().addDocumentListener(docListener);
		mainPanel.add(codeField);
		
		mainPanel.add(HelpHelper.getInstance().getHelpButtonWithID("dsdukcrncode"));
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.datasetukcrncode")));
		ukcrnCodeField = new TextFieldWithStatus(15, true);
		mainPanel.add(ukcrnCodeField);
		
		mainPanel.add(HelpHelper.getInstance().getHelpButtonWithID("dsdwizardstudydescription"));
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.datasetdescription")));
		descriptionField = new TextFieldWithStatus(15, false);
		mainPanel.add(descriptionField);
		
		rows = 5;

		mainPanel.add(HelpHelper.getInstance().getHelpButtonWithID("dsdwizarduserandomisation"));
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.userandomisation")));
		randomisationBox = new JCheckBox();
		mainPanel.add(randomisationBox);
		
		SpringUtilities.makeCompactGrid(mainPanel,
                rows, 3, 			//rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad

		holderPanel.add(mainPanel, BorderLayout.NORTH);
		holderPanel.add(new JPanel(), BorderLayout.CENTER);
		
		wm.setNextFinishButtonEnabled(false);
		
		return holderPanel;
	}
	
	public JPanel buildGroupPanel() {
		JPanel groupPanel = new JPanel(new SpringLayout());
		groupBox = new JComboBox();
		
		SpringUtilities.makeCompactGrid(groupPanel,
                1, 2, 			//rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad
		
		return groupPanel;
	}

	public boolean validatePanel(boolean showDialog) {
		if (nameField.getText().equals("")) {
			if (showDialog) {
				JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.wizard.nonemptyname"));
			}
			return false;
		}
			
		if (codeField.getText().equals("")) {
			if (showDialog) {
				JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.wizard.nonemptycode"));
			}
			return false;
		}
		
		if (codeField.getText().indexOf("-")!= -1 || codeField.getText().indexOf("/") != -1){
			if (showDialog){
				JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.wizard.invalidchars"));
			}
			return false;
		}

		String name = nameField.getText();
		String code = codeField.getText();
		
		//if a project of this name already exists in the repository, return false for
		//validation and how message dialog to user
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
		
		return true;
		
	}
	
	public boolean next() {
		HibernateFactory factory = new HibernateFactory();
		DataSet dSet = factory.createDataset(nameField.getText(), codeField.getText());
		dSet.setRandomizationRequired(randomisationBox.isSelected());
		dSet.setProjectCode(codeField.getText());
		wm.getWizardDs().setDs(dSet);
		wm.getWizardDs().setSingleCentreStudy(false);
		
		//set the UKCRN code
		wm.getWizardDs().setUkcrnCode(ukcrnCodeField.getText());
		
		//use ESL
		wm.getWizardDs().getDs().setEslUsed(true);
		
		ConsentFormGroup cfg = ElementUtility.createIConsentFormGroup();
		cfg.setDescription(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.wizard.mainconsent"));
		cfg.setEslTrigger(true);
		PrimaryConsentForm pcf = ElementUtility.createIPrimaryConsent(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.wizard.subjectagreed"), " ");
		cfg.addConsentForm(pcf);
		ArrayList<ConsentFormGroup> cfgs = new ArrayList<ConsentFormGroup>();
		cfgs.add(cfg);
		((DataSet)wm.getWizardDs().getDs()).setAllConsentFormGroups(cfgs);
		
		
		wm.getWizardDs().getDs().setDisplayText(codeField.getText());
		wm.getWizardDs().assignDefaultStatuses();
		wm.getWizardDs().assignDefaultRoles();
		wm.getWizardDs().getDs().setDisplayText(nameField.getText());
		wm.getWizardDs().getDs().setDescription(descriptionField.getText());
		wm.getWizardDs().getDs().setSendMonthlySummaries(true);
		
		//set this to dirty so that it will prompt to save on immediate close
		wm.getWizardDs().setDirty(true);

		//if it's a single centre, get from drop-down box, otherwise, this will be set later on
		if (wm.getWizardDs().isSingleCentreStudy()) {
			ArrayList<GroupModel> oneModel = new ArrayList<GroupModel>();
			oneModel.add((GroupModel)groupBox.getSelectedItem());
			wm.getWizardDs().setGroups(oneModel);
		}
		
		return true;
	}
	
	private void init() {
		//nothing to init here yet
		
	}

	public void refreshPanel() {
	}

	public void actionPerformed(ActionEvent e) {
		groupBox.setSelectedItem("");
		groupBox.setEnabled(false);
		groupLabel.setEnabled(false);
	}

	private class NextButtonListener implements DocumentListener {

		public void changedUpdate(DocumentEvent e) {
			if (nameField.getText().equals("")) {
				wm.setNextFinishButtonEnabled(false);
				return;
			}
			
			if (codeField.getText().equals("")) {
				wm.setNextFinishButtonEnabled(false);
				return;
			}
			
			wm.setNextFinishButtonEnabled(true);
		}

		public void insertUpdate(DocumentEvent e) {
				changedUpdate(e);
		}

		public void removeUpdate(DocumentEvent e) {
			changedUpdate(e);			
		}
		
		
	}
	
}
