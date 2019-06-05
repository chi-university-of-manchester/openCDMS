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

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SpringLayout;
import javax.swing.DefaultListModel;

import org.psygrid.data.model.hibernate.GenericState;
import org.psygrid.data.model.hibernate.Status;
import org.psygrid.datasetdesigner.utils.ElementUtility;
import org.psygrid.datasetdesigner.utils.SpringUtilities;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.custom.TextFieldWithStatus;
import org.psygrid.datasetdesigner.model.StatusTableModel;



public class AddStatusDialog extends JDialog implements ActionListener {
		
	private JButton okButton;
	private JButton cancelButton;
	
	private TextFieldWithStatus shortNameField;
	private TextFieldWithStatus codeField;
	private JComboBox genericStateBox;
	
	private JTable statusTable;
	
	private Status status;
	
	//transition state stuff
	private JList availableTransitionsList;
	private JList assignedTransitionsList;
	private JComboBox stateBox;
	
		
	public AddStatusDialog(JDialog parentDialog, 
						   JTable statusTable, 
						   JList availableTransitionsList,
						   JList assignedTransitionsList,
						   JComboBox stateBox
						   
						   ) {
		super(parentDialog, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.addstatus"));
		
		this.statusTable = statusTable;
		
		//state transition stuff
		this.availableTransitionsList = availableTransitionsList;
		this.assignedTransitionsList = assignedTransitionsList;
		this.stateBox = stateBox;
		
		setModal(true);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(buildMainPanel(), BorderLayout.CENTER);
		getContentPane().add(buildButtonPanel(), BorderLayout.SOUTH);
		init();
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public AddStatusDialog(JDialog parentDialog, 	
						   JTable statusTable,
						   Status status,
						   JList availableTransitionsList,
						   JList assignedTransitionsList,
						   JComboBox stateBox
						   ) {
		super(parentDialog, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.editstatus"));
		this.status = status;
		this.statusTable = statusTable;
		
		//state transition stuff
		this.availableTransitionsList = availableTransitionsList;
		this.assignedTransitionsList = assignedTransitionsList;
		this.stateBox = stateBox;
		
		setModal(true);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(buildMainPanel(), BorderLayout.CENTER);
		getContentPane().add(buildButtonPanel(), BorderLayout.SOUTH);
		init();
		init(status);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		
	}
	
	private void init() {
		DefaultComboBoxModel genStateModel = new DefaultComboBoxModel();
		 for (GenericState state : GenericState.values())
		 {
			genStateModel.addElement(state);
		 }
		 genericStateBox.setModel(genStateModel);
	}
	
	private void init(Status status) {
		shortNameField.setText(status.getShortName());
		codeField.setText(new Integer(status.getCode()).toString());
		genericStateBox.setSelectedItem(status.getGenericState());
	}
		
	private JPanel buildMainPanel() {
		JPanel mainPanel = new JPanel(new SpringLayout());
		
		shortNameField = new TextFieldWithStatus(40, true);
		codeField = new TextFieldWithStatus(20, true);
		genericStateBox = new JComboBox();
		
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.statusname")));
		mainPanel.add(shortNameField);
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.statuscode")));
		mainPanel.add(codeField);
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.genericstate")));
		mainPanel.add(genericStateBox);
			
		SpringUtilities.makeCompactGrid(mainPanel,
                3, 2, //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad

		return mainPanel;
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
		if (shortNameField.getText() == null || shortNameField.getText().equals("")) {
			JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.statusshortnamenonempty"));
			return false;
		}
		
		try {
			new Integer(codeField.getText());
		} catch (NumberFormatException nex) {
			JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.statuscodeinteger"));
			return false;
		}
		
		for (Status curStatus: ((StatusTableModel)statusTable.getModel()).getAllStatuses()) {
			if (status != curStatus ) {
				if (codeField.getText().equals(new Integer(curStatus.getCode()).toString())) {
					JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.statuscodealreadyexists"));
					return false;
				}
				
				if (shortNameField.getText().equals(curStatus.getShortName())) {
					JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.shortnamealreadyexists"));
					return false;
				}
			}
		}
		
		return true;
	}

	
	public void actionPerformed(ActionEvent aet) {
		if (aet.getSource() == okButton) {
			if (validateEntries()) {
				try {
					//this will throw an excption if it's not being edited!
					status.getShortName();

					//store index of previous entry here
					int storedLocationInList = -1;
					//if state box contains the item, refresh it with this value
					
					StatusTableModel statusModel = (StatusTableModel)statusTable.getModel();
					
					for (int i=0; i<statusModel.getRowCount(); i++) {
						if ((statusModel.getStatusAt(i).getShortName()).equals(status.getShortName())) {
							storedLocationInList = i;
						}
					}
					
					//store index of previous entry here
					int storedLocationInBox = -1;
					//if state box contains the item, refresh it with this value
					for (int i=0; i<stateBox.getModel().getSize(); i++) {
						if (((Status)stateBox.getModel().getElementAt(i)).getShortName().equals(status.getShortName())) {
							storedLocationInBox = i;
						}
					}
					
					//remove status from available transitionsList
					((DefaultListModel)availableTransitionsList.getModel()).removeElement(status);

					status.setShortName(shortNameField.getText());
					status.setLongName(shortNameField.getText());
					status.setCode(new Integer(codeField.getText()));
					status.setGenericState((GenericState)genericStateBox.getSelectedItem());
					status.setInactive(false);

					//if state box contains the item, refresh it with this value
					if (storedLocationInList != -1) {
						statusModel.setStatusAt(status, storedLocationInList);
					}
					
					//if state box contains the item, refresh it with this value
					if (storedLocationInBox != -1) {
						((DefaultComboBoxModel)stateBox.getModel()).removeElementAt(storedLocationInBox);
						((DefaultComboBoxModel)stateBox.getModel()).insertElementAt(status, storedLocationInBox);
					}
					
					//readd to available transitions box
					if (!((DefaultListModel)availableTransitionsList.getModel()).contains(status)) {
						if (!((DefaultListModel)assignedTransitionsList.getModel()).contains(status)) {
							((DefaultListModel)availableTransitionsList.getModel()).addElement(status);
						}
					}
				} catch (NullPointerException ex) {
					Status status = ElementUtility.createIStatus(shortNameField.getText(),
                            new Integer(codeField.getText()).intValue(),
                            shortNameField.getText(),
                            (GenericState) genericStateBox.getSelectedItem(),
                            true);
					((StatusTableModel)statusTable.getModel()).addStatus(status);
					
					//update state transition state box
					((DefaultComboBoxModel)stateBox.getModel()).addElement(status);
					
					//update assigned and available transitions box
					if (stateBox.getSelectedItem() != status) {
						if (!((DefaultListModel)availableTransitionsList.getModel()).contains(status)) {
							((DefaultListModel)availableTransitionsList.getModel()).addElement(status);
						}
					}
				}
				this.dispose();
			}
		} else if (aet.getSource() == cancelButton) {
			this.dispose();
		}
	}
	
}