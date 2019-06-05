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

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import org.psygrid.datasetdesigner.custom.TextFieldWithStatus;
import org.psygrid.datasetdesigner.model.CustomFieldValueModel;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.utils.SpringUtilities;

public class AddFieldValueDialog extends JDialog implements ActionListener {
		
	private JButton okButton;
	private JButton cancelButton;
	
	private CustomFieldValueModel value;
	
	private TextFieldWithStatus valueField;
	
	private JList valuesList;
	
	public AddFieldValueDialog(JDialog parentDialog, JList valuesList) {
		super(parentDialog, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.addtreatment"));
		this.valuesList = valuesList;
		setModal(true);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(buildMainPanel(), BorderLayout.CENTER);
		getContentPane().add(buildButtonPanel(), BorderLayout.SOUTH);
		pack();
		setLocationRelativeTo(null);  
		setVisible(true);
	}
	
	public AddFieldValueDialog(JDialog parentDialog, 
							JList valuesList, 
							CustomFieldValueModel value) {
		super(parentDialog, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.edittreatment"));
		this.valuesList = valuesList;
		this.value=value;
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
		if (value != null) {
			valueField.setText(value.getValue());
		}
	}
	
	private JPanel buildMainPanel() {
		JPanel mainPanel = new JPanel(new SpringLayout());
		
		valueField = new TextFieldWithStatus(20, true);
				
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.treatmentname")));
		mainPanel.add(valueField);

		SpringUtilities.makeCompactGrid(mainPanel,
                1, 2, //rows, cols
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
			if (value == null) {
				value = new CustomFieldValueModel(valueField.getText());
				((DefaultListModel)valuesList.getModel()).addElement(value);
			} else {
				value.setValue(valueField.getText());
			}
			this.dispose();
		} else if (aet.getSource() == cancelButton) {
			this.dispose();
		}
	}

}