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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.border.BevelBorder;

import net.sourceforge.jeval.EvaluationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.common.ui.WrappedJOptionPane;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.datasetdesigner.actions.EditMultipleVariableTestCaseAction;
import org.psygrid.datasetdesigner.actions.RemoveMultipleVariableTestCaseAction;
import org.psygrid.datasetdesigner.controllers.DatasetController;
import org.psygrid.datasetdesigner.ui.configurationdialogs.CreateMultipleVariableTestDialog;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.utils.SpringUtilities;

import com.jgoodies.forms.factories.ButtonBarFactory;

public class MultipleVariableTestPanel extends JPanel implements ActionListener {
	private static final Log LOG = LogFactory.getLog(MultipleVariableTestPanel.class);

	private static final long serialVersionUID = 3793860232341420320L;

	private JDialog parentDialog;

	private DerivedEntry entry;

	private boolean isDEL;

	private boolean viewOnly;

	private JButton addButton;
	private JButton runButton;

	private HibernateFactory factory;

	private boolean firstRun = true;

	public MultipleVariableTestPanel(JDialog parentDialog, Entry entry, boolean viewOnly, boolean isDEL) {
		this.parentDialog = parentDialog;
		this.isDEL = isDEL;
		this.viewOnly = viewOnly;

		if (entry instanceof DerivedEntry) {
			this.entry = (DerivedEntry)entry;
		}

		build();
	}

	public void build() {
		this.setLayout(new BorderLayout());
		if(viewOnly || !isDEL) {
			setBorder(BorderFactory.createTitledBorder(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.test.viewtestcases")));
		}
		else {
			setBorder(BorderFactory.createTitledBorder(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.test.configuretestcases")));
		}
		this.add(createMainTestPanel(), BorderLayout.CENTER);
	}

	private JPanel createMainTestPanel() {
		JPanel testPanel = new JPanel();
		testPanel.setLayout(new BoxLayout(testPanel, BoxLayout.Y_AXIS));

		MultipleVariableTest test = entry.getTest();

		addButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.test.add"));	
		addButton.addActionListener(this);
		runButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.test.run"));
		runButton.addActionListener(this);
		if (test == null || test.getTestCases().size() == 0) {
			if (viewOnly) {
				addButton.setEnabled(false);
			}
			if (isDEL) {
				testPanel.add(ButtonBarFactory.buildCenteredBar(addButton));
			}
		}
		else {
			JPanel testCasesPanel = new JPanel();
			testCasesPanel.setLayout(new BoxLayout(testCasesPanel, BoxLayout.Y_AXIS));

			JScrollPane scrollPane = new JScrollPane(testCasesPanel);
			scrollPane.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createEmptyBorder(10, 15, 18, 10),
					BorderFactory.createBevelBorder(BevelBorder.LOWERED)
			));
			scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			scrollPane.setPreferredSize(new Dimension(180,460));
			testPanel.add(scrollPane);
			for (MultipleVariableTestCase testCase: test.getTestCases()) {
				if (testCase != null) {
					JPanel bob = createSingleTestPanel(testCase);
					testCasesPanel.add(bob);
				}
			}
			if (viewOnly || !isDEL) {
				testPanel.add(ButtonBarFactory.buildRightAlignedBar(runButton));	
			}
			else {
				testPanel.add(ButtonBarFactory.buildRightAlignedBar(addButton, runButton));	
			}
		}

		firstRun = false;
		return testPanel;
	}

	private JPanel createSingleTestPanel(MultipleVariableTestCase testCase) {
		JPanel singleTestPanel = new JPanel(new SpringLayout());
		singleTestPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(5,15,20,15),
				BorderFactory.createLineBorder(Color.GRAY)
		));
		singleTestPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

		singleTestPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.test.testvalues")));
		JTable valuesTable = new JTable();
		valuesTable.setEnabled(true);

		if (testCase != null && testCase.getTestMap() != null) {
			valuesTable.setModel(new TestCaseTableModel(parentDialog, entry.getVariables(), testCase.getTestMap()));	
		}
		else {
			valuesTable.setModel(new TestCaseTableModel(parentDialog, entry.getVariables()));
		}
		valuesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JScrollPane scrollPane = new JScrollPane(valuesTable);
		singleTestPanel.add(scrollPane);
		valuesTable.setEnabled(false);
		scrollPane.setPreferredSize(new Dimension(100, 120));	

		//Add expected resp - right
		JLabel expectedRespLabel = new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.test.expectedresponse"));
		String resp = "";
		if (testCase.getResponse() != null) {
			resp = testCase.getResponse().toString();
		}
		JTextField expectedResp = new JTextField(resp);
		expectedResp.setEnabled(false);
		//Add actual resp - right
		JLabel actualRespLabel = new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.test.actualresponse"));
		JTextField actualResp = new JTextField();
		if (!firstRun && testCase.getResponseToLastTest() != null) {
			actualResp.setText(testCase.getResponseToLastTest().toString());
			if (!expectedResp.getText().equals(actualResp.getText())) {
				actualResp.setBackground(new Color(255, 61, 61));
			}
		}
		actualResp.setEnabled(false);

		singleTestPanel.add(expectedRespLabel);
		singleTestPanel.add(expectedResp);

		singleTestPanel.add(actualRespLabel);
		singleTestPanel.add(actualResp);

		int rows = 3;
		if (!viewOnly && isDEL) {
			JButton edit = new JButton(new EditMultipleVariableTestCaseAction(parentDialog, this, testCase, entry.getVariables()));
			JButton delete = new JButton(new RemoveMultipleVariableTestCaseAction(this, entry.getTest(), testCase));
			JPanel buttonBar = ButtonBarFactory.buildLeftAlignedBar(edit, delete);

			buttonBar.setPreferredSize(new Dimension(20,22));
			singleTestPanel.add(buttonBar);

			singleTestPanel.add(new JLabel(""));
			rows++;
		}

		SpringUtilities.makeCompactGrid(singleTestPanel,
				rows, 2, //rows, cols
				6, 6,        //initX, initY
				6, 6);       //xPad, yPad

		return singleTestPanel;
	}

	public void actionPerformed(ActionEvent aet) {		
		if (aet.getSource() == addButton) {
			Map variables = entry.getVariables();
			CreateMultipleVariableTestDialog testDialog = new CreateMultipleVariableTestDialog(parentDialog, this, variables);
			if (entry.getTest() == null) {
				entry.setTest(new MultipleVariableTest());
			}
			if (testDialog.getTestCase() != null) {
				entry.getTest().addTestCase(testDialog.getTestCase());
				testChanged();
			}
		} else if (aet.getSource() == runButton) {
			runTests();
		}
	}

	/**
	 * Update the entry when a test case is edited.
	 *
	 */
	public void testChanged() {
		//Remove previous results
		entry.resetTest();
		entry.setIsRevisionCandidate(true);
		((Document)DatasetController.getInstance().getActiveDocument()).setIsRevisionCandidate(true);

		removeAll();
		build();
		validate();
		setVisible(true);
	}

	private void runTests() {
		entry.resetTest();
		try {
			boolean passed = entry.runTest();
			
			for (MultipleVariableTestCase testCase: entry.getTest().getTestCases()) {
				if (testCase.getResponseToLastTest() == null) {
					testCase.setResponseToLastTest(testCase.getResponse());
				}
			}
			
			//Refresh tables
			removeAll();
			build();
			validate();
			setVisible(true);
		}
		catch (EvaluationException ex) {
			LOG.error("A problem occurred when running tests for "+entry.getDisplayText(), ex);
			String errorMessageString = new String(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.syntaxerrormsg1"));
			String problem = ex.getCause() != null ? ex.getCause().toString() : ex.getMessage();
			errorMessageString = errorMessageString.concat(" " + problem);
			WrappedJOptionPane.showMessageDialog(this, errorMessageString, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.syntaxerrordlgtitle"), WrappedJOptionPane.ERROR_MESSAGE);
		}
	}

	public HibernateFactory getFactory() {
		if (factory == null) {
			factory = new HibernateFactory();
		}
		return factory;
	}

}