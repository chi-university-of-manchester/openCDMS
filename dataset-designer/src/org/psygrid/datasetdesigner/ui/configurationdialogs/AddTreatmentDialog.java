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

import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import javax.swing.DefaultListModel;

import org.psygrid.datasetdesigner.model.RandomisationHolderModel;
import org.psygrid.datasetdesigner.model.TreatmentHolderModel;

import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.utils.SpringUtilities;
import org.psygrid.datasetdesigner.custom.TextFieldWithStatus;

public class AddTreatmentDialog extends JDialog implements ActionListener {
		
	private JButton okButton;
	private JButton cancelButton;
	
	private TreatmentHolderModel treatment;
	
	private TextFieldWithStatus nameField;
	private TextFieldWithStatus codeField;
	
	private JList treatmentList;
	
	public AddTreatmentDialog(JDialog parentDialog, JList treatmentList) {
		super(parentDialog, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.addtreatment"));
		this.treatmentList = treatmentList;
		setModal(true);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(buildMainPanel(), BorderLayout.CENTER);
		getContentPane().add(buildButtonPanel(), BorderLayout.SOUTH);
		pack();
		setLocationRelativeTo(null);  
		setVisible(true);
	}
	
	public AddTreatmentDialog(JDialog parentDialog, 
							JList treatmentList, 
							TreatmentHolderModel treatment) {
		super(parentDialog, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.edittreatment"));
		this.treatmentList = treatmentList;
		this.treatment=treatment;
		setModal(true);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(buildMainPanel(), BorderLayout.CENTER);
		getContentPane().add(buildButtonPanel(), BorderLayout.SOUTH);
		init();
		pack();
		setLocationRelativeTo(null);  
		setVisible(true);
	}
	
	private void init() {
		if (treatment != null) {
			nameField.setText(treatment.getTreatmentName());
			codeField.setText(treatment.getTreatmentCode());
		}
	}
	
	private JPanel buildMainPanel() {
		JPanel mainPanel = new JPanel(new SpringLayout());
		
		nameField = new TextFieldWithStatus(20, true);
		codeField = new TextFieldWithStatus(20, true);
				
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.treatmentname")));
		mainPanel.add(nameField);

		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.treatmentcode")));
		mainPanel.add(codeField);
		
		SpringUtilities.makeCompactGrid(mainPanel,
                2, 2, //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad

		return mainPanel;
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
	
	public void actionPerformed(ActionEvent aet) {
		if (aet.getSource() == okButton) {
			if (treatment == null) {
				treatment = new TreatmentHolderModel();
				treatment.setTreatmentName(nameField.getText());
				treatment.setTreatmentCode(codeField.getText());
				((DefaultListModel)treatmentList.getModel()).addElement(treatment);
			} else {
				treatment.setTreatmentName(nameField.getText());
				treatment.setTreatmentCode(codeField.getText());
			}
			this.dispose();
		} else if (aet.getSource() == cancelButton) {
			this.dispose();
		}
	}

}