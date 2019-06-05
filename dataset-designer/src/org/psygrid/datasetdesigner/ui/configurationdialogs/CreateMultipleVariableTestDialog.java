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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;

import org.psygrid.common.ui.WrappedJOptionPane;
import org.psygrid.data.model.hibernate.MultipleVariableTestCase;
import org.psygrid.data.model.hibernate.Value;
import org.psygrid.datasetdesigner.ui.MultipleVariableTestPanel;
import org.psygrid.datasetdesigner.ui.TestCaseTableModel;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.utils.SpringUtilities;

import com.jgoodies.forms.factories.ButtonBarFactory;

public class CreateMultipleVariableTestDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 507408891970771127L;

	private JButton okButton;
	private JButton cancelButton;

	private JTable valuesTable;

	private JTextField respField;

	private MultipleVariableTestCase testCase = null;

	private JPanel mainPanel;

	private Map variables;

	private MultipleVariableTestPanel parent;

	public CreateMultipleVariableTestDialog(JDialog parentDialog, MultipleVariableTestPanel parent, Map variables) {
		super(parentDialog, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.test.createtestcase"));
		this.parent = parent;
		this.variables = variables;
		init();
	}

	public CreateMultipleVariableTestDialog(JDialog parentDialog, MultipleVariableTestPanel parent, MultipleVariableTestCase testCase, Map variables) {
		super(parentDialog, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.test.edittestcase"));
		this.parent = parent;
		this.testCase = testCase;
		this.variables = variables;
		init();
	}

	private void init() {
		setModal(true);
		getContentPane().setLayout(new BorderLayout());
		mainPanel = buildMainPanel();
		getContentPane().add(mainPanel, BorderLayout.CENTER);
		getContentPane().add(buildButtonPanel(), BorderLayout.SOUTH);
		pack();

		setLocationRelativeTo(null);  
		setVisible(true);
	}

	private JPanel buildMainPanel() {
		JPanel mainPanel = new JPanel();

		int rows = 3;

		mainPanel.setLayout(new SpringLayout());
		mainPanel.add(new JLabel(""));
		mainPanel.add(new JLabel("Add inputs for testing, e.g \"this is a test\", \"12\" or \"31/12/2007\"."));

		mainPanel.add(new JLabel("Test Values"));

		valuesTable = new JTable();
		valuesTable.setEnabled(true);

		if (testCase != null && testCase.getTestMap() != null) {
			valuesTable.setModel(new TestCaseTableModel(this, variables, testCase.getTestMap()));	
		}
		else {
			valuesTable.setModel(new TestCaseTableModel(this, variables));
		}
		valuesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JScrollPane scrollPane = new JScrollPane(valuesTable);
		scrollPane.setPreferredSize(new Dimension(150, 160));
		mainPanel.add(scrollPane);

		mainPanel.add(new JLabel("Expected Response"));
		respField = new JTextField(20);
		if (testCase != null && testCase.getResponse() != null) {
			respField.setText(testCase.getResponse().toString());
		}
		mainPanel.add(respField);

		SpringUtilities.makeCompactGrid(mainPanel,
				rows, 2, //rows, cols
				6, 6,        //initX, initY
				6, 6);       //xPad, yPad

		return mainPanel;
	}

	private JPanel buildButtonPanel() {
		okButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ok"));
		okButton.addActionListener(this);
		cancelButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.cancel"));
		cancelButton.addActionListener(this);

		JPanel buttonBar = ButtonBarFactory.buildOKCancelBar(okButton, cancelButton);
		buttonBar.setBorder(BorderFactory.createEmptyBorder(14, 8, 6, 8));
		return buttonBar;
	}

	public void actionPerformed(ActionEvent aet) {
		if (aet.getSource() == okButton) {
			//Check results

			Double response = null;
			try {
				response = Double.valueOf(respField.getText());
			}
			catch (NumberFormatException nfe) {
				WrappedJOptionPane.showMessageDialog(this, "The expected response must be a number", "", JOptionPane.INFORMATION_MESSAGE);
				//give up
				return;
			}
			Map<String, Value> testMap = new HashMap<String,Value>();
			List<String> labels = ((TestCaseTableModel)valuesTable.getModel()).getTestLabels();
			List<Value> values = ((TestCaseTableModel)valuesTable.getModel()).getTestValues();
			for (Value v: values) {
				if (v == null || v.getValueAsString() == null || v.getValueAsString().equals("")) {
					WrappedJOptionPane.showMessageDialog(this, "A value must be entered for each entry.\n\nNote: You may need to press enter after entering an input value.", "", JOptionPane.INFORMATION_MESSAGE);
					return;
				}
			}
			for (int i = 0; i < labels.size(); i++) {
				testMap.put(labels.get(i), values.get(i));
			}
			if (testCase == null) {
				testCase = new MultipleVariableTestCase(testMap, response);
			}
			else {
				boolean testCaseChanged = false;
				if (!response.equals(testCase.getResponse())) {
					testCaseChanged = true;
					testCase.setResponse(response);
				}
				boolean valuesChanged = false;
				for (String label: testMap.keySet()) {
					if (!testMap.get(label).equals(testCase.getTestMap().get(label))) {
						valuesChanged = true;
						break;
					}
				}
				if (valuesChanged) {
					testCaseChanged = true;
					testCase.setTestMap(testMap);
				}

				if (testCaseChanged) {
					//mark entry as changed and refresh display
					parent.testChanged();
				}
			}
			this.dispose();
		} 
		else if (aet.getSource() == cancelButton) {
			this.dispose();
		}
	}

	public MultipleVariableTestCase getTestCase() {
		return testCase;
	}

}
