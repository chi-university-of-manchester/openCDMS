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
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;

import javax.swing.DefaultListModel;

import org.psygrid.datasetdesigner.utils.ListModelUtility;
import org.psygrid.datasetdesigner.utils.SpringUtilities;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.custom.TextFieldWithStatus;

import org.psygrid.randomization.model.hibernate.Stratum;

import org.psygrid.datasetdesigner.model.RandomisationHolderModel;

public class AddStratumDialog extends JDialog implements ActionListener {
		
	private JButton okButton;
	private JButton cancelButton;
	
	private JComboBox datasetBox;

	private TextFieldWithStatus nameField;
	
	private Stratum stratum;
	
	private JList stratumList;
	
	private JList strataValueList;
	
	private HashMap stratumMap;
	
	private JButton addStrataValueButton;
	private JButton removeStrataValueButton;
	
	private TextFieldWithStatus strataValueNameField;
	
	public AddStratumDialog(JDialog parentDialog, JComboBox datasetBox, JList stratumList, HashMap stratumMap) {
		super(parentDialog, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.addstratum"));
		this.datasetBox = datasetBox;
		this.stratumList = stratumList;
		this.stratumMap = stratumMap;
		setModal(true);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(buildMainPanel(), BorderLayout.CENTER);
		getContentPane().add(buildButtonPanel(), BorderLayout.SOUTH);
		pack();
		setLocationRelativeTo(null);  
		setVisible(true);
	}
	
	public AddStratumDialog(JDialog parentDialog,
							JComboBox datasetBox, 
							JList stratumList, 
							HashMap stratumMap, 
							Stratum stratum) {
		super(parentDialog, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.editstratum"));
		this.datasetBox = datasetBox;
		this.stratumList = stratumList;
		this.stratumMap = stratumMap;
		this.stratum = stratum;
		setModal(true);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(buildMainPanel(), BorderLayout.CENTER);
		getContentPane().add(buildButtonPanel(), BorderLayout.SOUTH);
		init(stratum);
		pack();
		setLocationRelativeTo(null);  
		setVisible(true);
	}
	
	private void init(Stratum stratum) {
		if (stratum != null) {
			nameField.setText(stratum.getName());
			strataValueList.setModel(ListModelUtility.convertArrayListToListModel(new ArrayList<String>(stratum.getValues())));
		}
	}
	
	private JPanel buildMainPanel() {
		JPanel fullPanel = new JPanel();
		fullPanel.setLayout(new BoxLayout(fullPanel, BoxLayout.Y_AXIS));
		
		JPanel mainPanel = new JPanel(new SpringLayout());
		
		nameField = new TextFieldWithStatus(20, true);
				
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.stratumname")));
		mainPanel.add(nameField);
		
		SpringUtilities.makeCompactGrid(mainPanel,
                1, 2, //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad

		fullPanel.add(mainPanel);
		fullPanel.add(buildStrataPanel());
		
		return fullPanel;
	}
	
	private JPanel buildStrataPanel() {
		JPanel fullStrataPanel = new JPanel();
		fullStrataPanel = new JPanel();
		
		fullStrataPanel.setLayout(new BorderLayout());
		
		JPanel strataPanel = new JPanel();
		strataValueNameField = new TextFieldWithStatus(40, true);
		
		
		addStrataValueButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.addvalue"));
		addStrataValueButton.addActionListener(this);
		removeStrataValueButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.removevalue"));
		removeStrataValueButton.addActionListener(this);
		
		strataPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		strataPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.stratavalue")));
		strataPanel.add(strataValueNameField);
		strataPanel.add(addStrataValueButton);
		strataPanel.add(removeStrataValueButton);

		strataValueList = new JList();
		strataValueList.setMinimumSize(new Dimension(150, 150));
		strataValueList.setMaximumSize(new Dimension(150, 150));
		strataValueList.setPreferredSize(new Dimension(150, 150));
		DefaultListModel valueListModel = new DefaultListModel();
		strataValueList.setModel(valueListModel);
		fullStrataPanel.add(strataPanel, BorderLayout.NORTH);
		fullStrataPanel.add(new JScrollPane(strataValueList), BorderLayout.CENTER);
		
		return fullStrataPanel;
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
			if (stratum == null) {
				stratum = new Stratum();
				stratum.setName(nameField.getText());
				stratum.setValues(ListModelUtility.convertListModelToStringList((DefaultListModel)strataValueList.getModel()));
				((DefaultListModel)stratumList.getModel()).addElement(stratum);
			} else {
				stratum.setName(nameField.getText());
				stratum.setValues(ListModelUtility.convertListModelToStringList((DefaultListModel)strataValueList.getModel()));
			}

			RandomisationHolderModel randomModel;
			if (stratumMap.get(datasetBox.getSelectedItem()) != null) {
				randomModel = (RandomisationHolderModel)stratumMap.get(datasetBox.getSelectedItem());
			} else {
				randomModel = new RandomisationHolderModel();
			}
			randomModel.getRandomisationStrata().add(stratum);
			stratumMap.put(datasetBox.getSelectedItem(), randomModel);
			this.dispose();
		} else if (aet.getSource() == cancelButton) {
			this.dispose();
		} else if (aet.getSource() == addStrataValueButton) {
			((DefaultListModel)strataValueList.getModel()).addElement((strataValueNameField.getText()));
			strataValueNameField.setText("");
		} else if (aet.getSource() == removeStrataValueButton) {
			((DefaultListModel)strataValueList.getModel()).removeElement(strataValueList.getSelectedValue());
		}
	}

}