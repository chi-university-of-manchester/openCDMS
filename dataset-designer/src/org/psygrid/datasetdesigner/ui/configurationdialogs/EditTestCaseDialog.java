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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.JXDatePicker;
import org.psygrid.common.ui.WrappedJOptionPane;
import org.psygrid.data.model.hibernate.DateValue;
import org.psygrid.data.model.hibernate.IntegerValue;
import org.psygrid.data.model.hibernate.NumericValue;
import org.psygrid.data.model.hibernate.SingleVariableTest;
import org.psygrid.data.model.hibernate.SingleVariableTestCase;
import org.psygrid.data.model.hibernate.TextValue;
import org.psygrid.data.model.hibernate.Value;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.utils.SpringUtilities;

import com.jgoodies.forms.factories.ButtonBarFactory;

/**
 * @author Lucy Bridges
 *
 */
public class EditTestCaseDialog extends JDialog implements ActionListener {

	private static final Log LOG = LogFactory.getLog(EditTestCaseDialog.class);

	private SingleVariableTest test = null;
	private SingleVariableTestCase testCase = null;

	private JLabel valueLabel;
	private JTextField valueField;
	private JXDatePicker dateValueField;

	private JLabel outcomeLabel;
	private ButtonGroup outcomeGroup;
	private JRadioButton outcomePass;
	private JRadioButton outcomeFail;

	private Value value = null;

	private JButton okButton = null;
	private JButton cancelButton = null;

	private boolean isChanged = false;
	
	public EditTestCaseDialog(JDialog parentDialog, String title, SingleVariableTest test, SingleVariableTestCase testCase) {
		super(parentDialog, title);
		this.test = test;
		this.testCase = testCase;
		init();
		setModal(true);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(buildMainPanel(), BorderLayout.CENTER);
		getContentPane().add(buildButtonPanel(), BorderLayout.SOUTH);
		pack();
		setLocationRelativeTo(null);  
		setVisible(true);
	}

	private void init() {
		valueLabel = new JLabel("Test Value");
		valueField = new JTextField();

		outcomeLabel = new JLabel("Expected Outcome");
		outcomeGroup = new ButtonGroup();
		outcomePass  = new JRadioButton("Pass");
		outcomeFail  = new JRadioButton("Fail");
		outcomeGroup.add(outcomePass);
		outcomeGroup.add(outcomeFail);

		dateValueField = new JXDatePicker();

		java.text.DateFormat[] formats = new java.text.DateFormat[1];
		formats[0] = SimpleDateFormat.getDateInstance(java.text.DateFormat.SHORT); 
		dateValueField.setFormats(formats);
		dateValueField.setDate(null);

		if (testCase.getTestInput() instanceof DateValue) {
			dateValueField.setDate(((DateValue)testCase.getTestInput()).getValue());
		}
		else if (testCase.getTestInput() != null) {
			valueField.setText(testCase.getTestInput().getValueAsString());
		}

		outcomePass.setSelected(testCase.getTestOutput());
		outcomeFail.setSelected(!testCase.getTestOutput());
	}

	private JPanel buildMainPanel() {
		JPanel mainPanel = new JPanel(new SpringLayout());
		mainPanel.add(valueLabel);

		Value value = testCase.getTestInput();
		if (value instanceof DateValue) {
			mainPanel.add(dateValueField);
		}
		else {
			mainPanel.add(valueField);
		}

		mainPanel.add(outcomeLabel);
		mainPanel.add(outcomePass);
		mainPanel.add(new JLabel(""));
		mainPanel.add(outcomeFail);

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

		return ButtonBarFactory.buildOKCancelBar(okButton, cancelButton);
	}

	public void actionPerformed(ActionEvent aet) {
		if (aet.getSource() == okButton) {
			Value oldValue = testCase.getTestInput();
			String input = valueField.getText();
			Value value = null;
			
			if (!input.equals(oldValue.getValueAsString())) {
				isChanged = true;
			}
			if (oldValue instanceof IntegerValue) {

				value = new IntegerValue();
				try {
					((IntegerValue)value).setValue(new Integer(input));
				}
				catch (NumberFormatException nfe) {
					WrappedJOptionPane.showMessageDialog(this, "Must enter an integer value");
					return;
				}
			}
			else if (oldValue instanceof NumericValue) {
				value = new NumericValue();
				try {
					((NumericValue)value).setValue(new Double(input));
				}
				catch (NumberFormatException nfe) {
					WrappedJOptionPane.showMessageDialog(this, "Must enter a numeric value");
					return;
				}
			}
			else if (oldValue instanceof TextValue) {
				value = new TextValue();
				try {
					((TextValue)value).setValue(input);
				}
				catch (Exception e) {
					WrappedJOptionPane.showMessageDialog(this, "Must enter a text value");
					return;
				}
			}
			else if (oldValue instanceof DateValue) {
				value = new DateValue();
				if (dateValueField.getDate() != null) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(dateValueField.getDate());

					((DateValue)value).setValue(dateValueField.getDate());
				}
				else {
					WrappedJOptionPane.showMessageDialog(this, "Must enter date in the format dd/mm/yy");
					return;
				}
			}
			testCase.setTestInput(value);
			
			if (this.outcomePass.isSelected() != testCase.getTestOutput()) {
				isChanged = true;
			}
			if (this.outcomePass.isSelected()) {
				testCase.setTestOutput(true);
			}
			else if (this.outcomeFail.isSelected()) {
				testCase.setTestOutput(false);
			}
			else {
				WrappedJOptionPane.showMessageDialog(this, "Must select an expected outcome");
				return;
			}

			if (!test.getTestCases().contains(testCase)) {
				//Add the new test to the test case
				test.addTest(testCase);
			}
			dispose();
		}
		else if (aet.getSource() == cancelButton) {
			dispose();
		}
	}

	public boolean isChanged() {
		return isChanged;
	}
}
