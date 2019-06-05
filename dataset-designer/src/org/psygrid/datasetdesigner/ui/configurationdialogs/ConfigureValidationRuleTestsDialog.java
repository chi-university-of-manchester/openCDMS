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
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.common.ui.WrappedJOptionPane;
import org.psygrid.data.model.hibernate.DataElementStatus;
import org.psygrid.data.model.hibernate.SingleVariableTest;
import org.psygrid.data.model.hibernate.SingleVariableTestCase;
import org.psygrid.data.model.hibernate.ValidationRule;
import org.psygrid.data.model.hibernate.Value;
import org.psygrid.datasetdesigner.actions.AddEditValidationTestCaseAction;
import org.psygrid.datasetdesigner.actions.ApproveElementAction;
import org.psygrid.datasetdesigner.actions.RemoveValidationTestCaseAction;
import org.psygrid.datasetdesigner.custom.ValidationTestButton;
import org.psygrid.datasetdesigner.ui.dataelementfacilities.DELSecurity;
import org.psygrid.datasetdesigner.utils.ListModelUtility;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;

import com.jgoodies.forms.factories.ButtonBarFactory;

public class ConfigureValidationRuleTestsDialog extends JDialog implements ActionListener, WindowListener {

	private static final long serialVersionUID = 7224966881525899611L;

	private ValidationTable validationTable;

	private JButton closeButton;
	private JButton saveButton;
	private JButton approveButton;

	private JButton addButton;
	private ValidationTestButton removeButton;
	private JButton runAllButton;

	private boolean edit;

	private JDialog parentDialog;

	private ValidationRule validationRule;

	private JList validationJList;

	private static final Log LOG = LogFactory.getLog(ConfigureValidationRuleTestsDialog.class);

	public ConfigureValidationRuleTestsDialog(JDialog parentDialog, ValidationRule validationRule) {
		this(parentDialog, validationRule, false);
	}

	public ConfigureValidationRuleTestsDialog(JDialog parentDialog, ValidationRule validationRule, boolean edit) {
		super(parentDialog, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.configurevalidationtests"));
		this.parentDialog = parentDialog;
		this.edit = edit;
		this.validationRule = validationRule;
		init();
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);	//force window closing events to use our method
		addWindowListener(this);
		getContentPane().add(buildMainPanel());
		pack();
		setModal(true);
		setLocationRelativeTo(null);  
		setVisible(true);
	}

	private JPanel buildMainPanel() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(buildTitlePanel());
		mainPanel.add(buildTestsPanel());
		mainPanel.add(buildButtonPanel());
		return mainPanel;
	}

	private void init() {
	}

	private JPanel buildTitlePanel() {
		JPanel titlePanel = new JPanel();
		titlePanel.setLayout(new BorderLayout());
		JLabel ruleName = new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.validationrule2")+": "+validationRule.getDescription());
		Font font = ruleName.getFont();
		ruleName.setFont(new Font(font.getFamily(), Font.BOLD, font.getSize()+2));
		ruleName.setBorder(BorderFactory.createEmptyBorder(5,1,10,1));
		titlePanel.add(ruleName, BorderLayout.CENTER);
		return titlePanel;
	}

	private JPanel buildTestsPanel() {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.validationrulestest")));
		panel.setLayout(new BorderLayout());

		JPanel testsPanel = null;
		if (validationRule.getTest() != null) {
			testsPanel = createSingleTestPanel(validationRule, true);
		}
		else {
			testsPanel = new JPanel();
			if (edit) {
				addButton = new ValidationTestButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.validationruleaddtest"));
				addButton.addActionListener(this);
				JPanel addPanel = ButtonBarFactory.buildCenteredBar(addButton);
				testsPanel.add(addPanel);
			}
		}
		testsPanel.setMinimumSize(new Dimension(550, 280));
		testsPanel.setPreferredSize(new Dimension(550, 280));
		testsPanel.setMaximumSize(new Dimension(550, 280));

		panel.add(testsPanel, BorderLayout.CENTER);

		return panel;
	}

	private JPanel createSingleTestPanel(ValidationRule rule, boolean editable) {
		editable &= edit;	//Associated rules should not be edited, but the test for the current rule should be, if we have edit permission.

		SingleVariableTest test = rule.getTest();
		JPanel singleTestPanel = new JPanel(new BorderLayout());
		singleTestPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(5,15,20,15),
				BorderFactory.createLineBorder(Color.GRAY)
		));
		singleTestPanel.setAlignmentX(Component.CENTER_ALIGNMENT);


		/*
		 * Create the title panel for this test
		 */
		JPanel titlePanel = new JPanel(new BorderLayout());
		titlePanel.setBorder(BorderFactory.createEmptyBorder(5,5,10,5));

		if (editable) {
			removeButton = new ValidationTestButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.validationruleremovetest"));
			removeButton.setTest(test);
			removeButton.addActionListener(this);
			titlePanel.add(removeButton, BorderLayout.EAST);
		}

		singleTestPanel.add(titlePanel, BorderLayout.NORTH);

		/*
		 * Add the test cases as a table for this test
		 */
		validationJList = new JList();
		validationJList.setModel(ListModelUtility.convertArrayListToListModel((ArrayList<SingleVariableTestCase>)test.getTestCases()));

		ValidationTableModel myModel = new ValidationTableModel(test.getTestCases());
		
		validationTable = new ValidationTable(myModel, test);
		
		

		JScrollPane tablePane = new JScrollPane(validationTable);
		tablePane.setMinimumSize(new Dimension(400, 100));
		tablePane.setPreferredSize(new Dimension(400, 100));
		tablePane.setMaximumSize(new Dimension(400, 100));

		singleTestPanel.add(tablePane, BorderLayout.CENTER);
		singleTestPanel.add(buildAddRemoveTestCasePanel(rule, editable), BorderLayout.SOUTH);

		return singleTestPanel;
	}

	private JPanel buildAddRemoveTestCasePanel(ValidationRule rule, boolean editable){
		JButton addCaseButton = null;
		JButton removeCaseButton = null;
		JButton editCaseButton = null;

		if (editable) {
			addCaseButton    = new JButton(new AddEditValidationTestCaseAction(this, rule));
			editCaseButton   = new JButton(new AddEditValidationTestCaseAction(this, rule, validationJList));
			removeCaseButton = new JButton(new RemoveValidationTestCaseAction(this, rule, validationJList));
		}
		runAllButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.validationruleruntest"));
		runAllButton.addActionListener(this);
		if (validationRule.getTest() == null) {
			runAllButton.setEnabled(false);
		}

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		if (editable) {
			buttonPanel.add(addCaseButton);
			buttonPanel.add(editCaseButton);
			buttonPanel.add(removeCaseButton);
		}
		buttonPanel.add(runAllButton);
		return buttonPanel;
	}

	private JPanel buildButtonPanel(){
		if (edit) {
			//TODO fix saving (it currently removes all other validation rules)
			//saveButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.save"));
			//	saveButton.addActionListener(this);
		}
		if (parentDialog instanceof ConfigureValidationRuleDialog
				&& ((ConfigureValidationRuleDialog)parentDialog).isDEL()
				&& DELSecurity.getInstance().canApproveElements()
				&& DataElementStatus.PENDING.equals((this.validationRule.getStatus()))) {
			approveButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.approve"));
			approveButton = new JButton(new ApproveElementAction(this, ((ConfigureValidationRuleDialog)this.parentDialog).getFrame().getDocPane(), validationRule));
			approveButton.addActionListener(this);
			approveButton.setEnabled(false);

			if (this.validationRule.getTest() == null) {
				approveButton.setEnabled(true);
			}
		}
		closeButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.close"));
		closeButton.addActionListener(this);

		JPanel buttonPanel;
		if (edit && approveButton == null) {
			//	buttonPanel = ButtonBarFactory.buildRightAlignedBar(saveButton,closeButton);
			buttonPanel = ButtonBarFactory.buildRightAlignedBar(closeButton);
		}
		else {
			if (approveButton != null) {
				buttonPanel = ButtonBarFactory.buildRightAlignedBar(approveButton, closeButton);
			}
			else {
				buttonPanel = ButtonBarFactory.buildRightAlignedBar(closeButton);
			}
		}
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(10,10,8,10));
		return buttonPanel;
	}

	public void actionPerformed(ActionEvent aet) {
		if (aet.getSource() == addButton) {
			validationRule.setTest(new SingleVariableTest("Validation Rule Test", "Validation Rule Test"));
			validationRule.setIsRevisionCandidate(true);
			((ConfigureValidationRuleDialog)getParent()).refreshTable();

			addButton = null;
			getContentPane().removeAll();
			getContentPane().add(buildMainPanel());
			pack();
			validate();

			setVisible(true);
		} else if (aet.getSource() == runAllButton) {
			runAllTests();
		} else if (aet.getSource() == removeButton) {
			removeTest(validationRule);
			validationRule.setIsRevisionCandidate(true);
		} else if (aet.getSource() == saveButton) {
			//saveTests();
		} else if (aet.getSource() == closeButton) {
			this.close();
		}
	}

	/**
	 * Update the table with the results of running the particular test
	 * @param test
	 */
	private void refreshTableWithTestResult(SingleVariableTest test) {
		validationJList.setModel(ListModelUtility.convertArrayListToListModel((ArrayList<SingleVariableTestCase>)test.getTestCases()));
		Map<SingleVariableTestCase,TestStatus> testStatuses =  new HashMap<SingleVariableTestCase,TestStatus>();
		for (SingleVariableTestCase testCase: test.getTestCases()) {
			testStatuses.put(testCase, TestStatus.Pass);
			for (SingleVariableTestCase failure: test.getFailedTestCases()) {
				if (testCase.getTestInput().equals(failure.getTestInput())
						&& testCase.getTestOutput() == failure.getTestOutput()) {
					testStatuses.put(testCase, TestStatus.Fail);
					break;
				}
			}
		}
		validationTable.setModel(new ValidationTableModel(test.getTestCases(), testStatuses));
		validationTable.validate();
	}

	/**
	 * Update (reset) the table
	 * 
	 * @param test
	 */
	public void refreshTable(SingleVariableTest test) {
		validationJList.setModel(ListModelUtility.convertArrayListToListModel((ArrayList<SingleVariableTestCase>)test.getTestCases()));
		validationTable.setModel(new ValidationTableModel(test.getTestCases()));
		validationTable.validate();
	}


	private class ValidationTable extends JTable {
		private static final long serialVersionUID = 1L;

		private SingleVariableTest test;

		public ValidationTable(TableModel tableModel, SingleVariableTest test) {
			super(tableModel);
			this.test = test;
		}

		public void changeSelection(int rowIndex,
				int columnIndex,
				boolean toggle,
				boolean extend) {
			super.changeSelection(rowIndex, columnIndex, toggle, extend);
			validationJList.setSelectedIndex(rowIndex);
		}

		public Component prepareRenderer(TableCellRenderer renderer, int row, int column){
			Component returnComp = super.prepareRenderer(renderer, row, column);
			Color alternateColour = new Color(255, 61, 61);
			Color originalColour = new Color(240, 245, 250);

			//Object expectedOutput = dataModel.getValueAt(row, 1);
			Object actualOutput  = dataModel.getValueAt(row, 2);
			//Only make changes if the output was different to the expected output, after running the test cases
			if (actualOutput instanceof Boolean
					&& !((Boolean)actualOutput)) {
				returnComp.setBackground(alternateColour);
			}
			else {
				returnComp.setBackground(originalColour);
			}
			return returnComp;
		}

	}

	static enum TestStatus{Pass, Fail, Untested}
	private class ValidationTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 1L;

		final String[] columnNames = {PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.testvalue"), 
				PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.expectedoutcome"), 
				PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.actualoutcome")};

		//Rows
		private List<SingleVariableTestCase> testCases = new ArrayList<SingleVariableTestCase>();

		private Map<SingleVariableTestCase,TestStatus> testStatuses =  new HashMap<SingleVariableTestCase,TestStatus>();

		public ValidationTableModel(List<SingleVariableTestCase> testCases) {
			this.testCases = testCases;
			for (SingleVariableTestCase testCase: testCases) {
				testStatuses.put(testCase, TestStatus.Untested);
			}
		}

		public ValidationTableModel(List<SingleVariableTestCase> testCases, Map<SingleVariableTestCase,TestStatus> testStatuses) {
			this.testCases = testCases;
			this.testStatuses = testStatuses;
		}

		public int getColumnCount() {
			return columnNames.length;
		}

		public int getRowCount() {
			return testCases.size();
		}

		public String getColumnName(int col) {
			return columnNames[col];
		}

		public Object getValueAt(int row, int col) {
			switch (col) {
			case 0:
				Value value = testCases.get(row).getTestInput();
				return value.getValueAsString();
			case 1:
				boolean output = testCases.get(row).getTestOutput();
				if (output) {
					return "Pass";
				}
				return "Fail"; 
			case 2:
				switch (testStatuses.get(testCases.get(row))) {
				case Untested:
					return "?";
				case Pass:
					//return PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.pass");
					return true;
				case Fail:
					//return PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.fail");
					return false;
				}
			default:
				return "";
			}
		}

		/*
		 * JTable uses this method to determine the default renderer/
		 * editor for each cell.  If we didn't implement this method,
		 * then the last column would contain text ("true"/"false"),
		 * rather than a check box.
		 */
		public Class getColumnClass(int c) {
			if (getRowCount() > 0) {
				return getValueAt(0, c).getClass();
			} else {
				return super.getColumnClass(c);
			}
		}

		public Map<SingleVariableTestCase, TestStatus> getTestStatuses() {
			return testStatuses;
		}

		public void setTestStatuses(Map<SingleVariableTestCase, TestStatus> testStatuses) {
			this.testStatuses = testStatuses;
		}

	}

	private void close() {
		//This automatically saves any changes, so will need to be changed if a save button is ever used.
		this.dispose();
	}

	private void runAllTests() {
		this.validationRule.resetTest();
		boolean passed = this.validationRule.runTest();
		refreshTableWithTestResult(validationRule.getTest());

		if (approveButton != null) {
			approveButton.setEnabled(passed);
		}
	}

	private void removeTest(ValidationRule rule) {
		int result = WrappedJOptionPane.showConfirmDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.removetest"), "", WrappedJOptionPane.YES_NO_OPTION);
		if (WrappedJOptionPane.NO_OPTION == result) {
			return;
		}
		else if (WrappedJOptionPane.OK_OPTION == result) {
			rule.setTest(null);
			getContentPane().removeAll();
			validationTable = null;
			getContentPane().add(buildMainPanel());
			pack();
			setVisible(true);
		}
	}

	public void windowClosing(WindowEvent e) {
		close();
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowDeactivated(WindowEvent e) {
	}

	public void windowActivated(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowOpened(WindowEvent e) {
	}

}