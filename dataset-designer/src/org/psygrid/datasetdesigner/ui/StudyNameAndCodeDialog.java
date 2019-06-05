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
package org.psygrid.datasetdesigner.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import org.jdesktop.swingx.util.WindowUtils;
import org.psygrid.datasetdesigner.custom.TextFieldWithStatus;
import org.psygrid.datasetdesigner.ui.MainFrame;

import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.utils.SpringUtilities;

import org.psygrid.data.model.hibernate.DataSet;

/**
 * Simple dialog to allows the user to 
 * update the name and code of the active study
 * 
 * @author pwhelan
 */
public class StudyNameAndCodeDialog extends JDialog  implements ActionListener {
	
	/**
	 * OK button
	 */
	private JButton okButton;
	
	/**
	 * Cancel button
	 */
	private JButton cancelButton;
	
	/**
	 * Name field for the study name
	 */
	private TextFieldWithStatus nameField;
	
	/**
	 * Code field for the study code
	 */
	private TextFieldWithStatus codeField;
	
	/**
	 * Dataset whose name/code need to be changed
	 */
	private DataSet ds;
	
	/**
	 * Constructor 	
	 * Must use the ds passed here because the activeDS might not be set in this context (it 
	 * is used pre-loading of dataset from repository)
	 * @param frame - the main window of the application
	 */
	public StudyNameAndCodeDialog(MainFrame frame, DataSet ds) {
		super(frame, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.studynameandcode.studydetails"), true);
		this.ds = ds;
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(buildMainPanel(), BorderLayout.CENTER);
		getContentPane().add(buildButtonPanel(), BorderLayout.SOUTH);
		init();
        pack();
        setLocation(WindowUtils.getPointForCentering(this));
        setVisible(true);
	}
	
	/**
	 * TODO : must validate this against existing projects!!
	 * @return true if name and code are not null and do not exist; false if not
	 */
	private boolean validateEntries() {
		if (nameField.getText().equals("")) {
			JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.wizard.nonemptyname"));
			return false;
		}
			
		if (codeField.getText().equals("")) {
			JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.wizard.nonemptycode"));
			return false;
		}
		
		return true;
	}

	/**
	 * Fill fields with the settings for the current dataset.
	 */
	private void init(){
		if (ds != null) {
			nameField.setText(ds.getName());
			codeField.setText(ds.getProjectCode());
		}
	}
	
	/**
	 * Build the main panel
	 * @return the configured main panel
	 */
	private JPanel buildMainPanel() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new SpringLayout());
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.datasetname")));
		nameField = new TextFieldWithStatus(15, true);
		mainPanel.add(nameField);
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.datasetcode")));
		codeField = new TextFieldWithStatus(15, true);
		mainPanel.add(codeField);
		
		SpringUtilities.makeCompactGrid(mainPanel,
				2, 2, 			//rows, cols
				6, 6,        //initX, initY
				6, 6);       //xPad, yPad

		return mainPanel;
	}
	
	/**
	 * Build the button panel
	 * @return return the configured button panel
	 */
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

	/**
	 * If ok, validate the user entries; if ok update dataset details
	 * and set the published flag to false.  If cancel, dismiss the dialog
	 * @param e the calling event
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == okButton) {
			if (validateEntries()) {
				ds.setProjectCode(codeField.getText());
				ds.setName(nameField.getText());
				ds.setDisplayText(nameField.getText());
				//changing the name and code to valid values means this study has not been published
				//so update the published flag!
				ds.setPublished(false);
			}
			this.dispose();
		} else if (e.getSource() == cancelButton) {
			this.dispose();
		}
	}
	
	
}
