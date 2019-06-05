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
package org.psygrid.datasetdesigner.ui.editdialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;

import org.psygrid.collection.entry.jeval.If;
import org.psygrid.collection.entry.jeval.JEvalHelper;
import org.psygrid.common.ui.WrappedJOptionPane;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.datasetdesigner.actions.AssignOptionAction;
import org.psygrid.datasetdesigner.actions.UnassignOptionAction;
import org.psygrid.datasetdesigner.controllers.DatasetController;
import org.psygrid.datasetdesigner.custom.SizeConstrainedTextArea;
import org.psygrid.datasetdesigner.renderer.EntryTableCellRenderer;
import org.psygrid.datasetdesigner.renderer.OptionListCellRenderer;
import org.psygrid.datasetdesigner.ui.CustomIconButton;
import org.psygrid.datasetdesigner.ui.MainFrame;
import org.psygrid.datasetdesigner.utils.IconsHelper;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;

public class DerivedEditDialog extends AbstractEditDialog implements DocumentListener {


	private DerivedEntry entry;

	private JTable dependentTable;
	private JButton assignVariableButton;
	private JButton unassignVariableButton;
	private JButton testFormulaButton; //Tests the formula
	private JLabel formulaSyntaxExceptionLabel;
	private JCheckBox useDefaultValsForDisabledVars;

	private SizeConstrainedTextArea formulaArea;
	private JList numericEntryList;

	private static final String DEFAULT_VALUE = "<None>";

	public DerivedEditDialog(MainFrame frame, DerivedEntry entry, boolean isDEL, boolean canEdit) {
		super(frame, entry, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.configuredderivedentry"), true, false, isDEL, canEdit);
		this.entry = entry;
		setModal(true);
		setLocationRelativeTo(null);  
		pack();
	}

	public DerivedEditDialog(JDialog parent, DerivedEntry entry, Document entryContext, boolean isDEL, boolean canEdit) {
		super(parent, entry, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.viewderivedentry"), true, false, entryContext, isDEL, canEdit);
		this.entry = entry;
		setModal(true);
		setLocationRelativeTo(null);  
		pack();
	}

	public JPanel getGenericPanel() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(super.getGenericPanel(), BorderLayout.NORTH);
		mainPanel.add(buildVariablePanel(), BorderLayout.SOUTH);
		mainPanel.add(buildCalcEvalChoicePanel(), BorderLayout.CENTER);
		return mainPanel;
	}
	
	private JPanel buildCalcEvalChoicePanel() {
		
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder(viewOnly? PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.viewcalcoptions"):PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.configurecalcoptions")));
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		useDefaultValsForDisabledVars = new JCheckBox(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.configurecalcoption1"));
		panel.add(useDefaultValsForDisabledVars);
		if(viewOnly){
			useDefaultValsForDisabledVars.setEnabled(false);
		}
			
		return panel;
		
	}

	private JPanel buildVariablePanel() {
		JPanel mainPanel = new JPanel();
		mainPanel.setBorder(BorderFactory.createTitledBorder(viewOnly? "View variable options":PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.configurevariableoptions")));
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		JPanel variablePanel = new JPanel();
		numericEntryList = new JList();
		numericEntryList.setCellRenderer(new OptionListCellRenderer());
		numericEntryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		CustomTableModel model = new CustomTableModel();
		dependentTable = new JTable(model);
		dependentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		dependentTable.getColumnModel().getColumn(0).setHeaderValue(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.derivedname"));
		dependentTable.getColumnModel().getColumn(1).setHeaderValue(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.derivedlabel"));
		dependentTable.getColumnModel().getColumn(2).setHeaderValue(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.deriveddefault"));
		TableColumn col = dependentTable.getColumnModel().getColumn(0);
		col.setCellRenderer(new EntryTableCellRenderer());

		if(!viewOnly){
			assignVariableButton = new CustomIconButton(new AssignOptionAction(numericEntryList, dependentTable, AssignOptionAction.VARIABLE), PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.assignoption"));
			unassignVariableButton = new CustomIconButton(new UnassignOptionAction(numericEntryList, dependentTable), PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.unassignoption"));

			variablePanel.add(createSubPanel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.allvariables"), assignVariableButton, numericEntryList));
			variablePanel.add(createArrowPanel(unassignVariableButton, assignVariableButton));
		}

		variablePanel.add(createSubPanel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.dependentvariables"), unassignVariableButton, dependentTable));

		mainPanel.add(variablePanel);
		mainPanel.add(buildFormulaPanel());

		return mainPanel;
	}


	/**
	 * Create the panel containing the arrows for assigning/removing options
	 * @param rightButton
	 * @param leftButton
	 * @return
	 */
	public JPanel createArrowPanel(JButton rightButton, JButton leftButton)
	{
		JPanel arrowPanel = new JPanel();
		arrowPanel.setLayout(new BoxLayout(arrowPanel, BoxLayout.Y_AXIS));
		arrowPanel.add(leftButton);
		arrowPanel.add(Box.createVerticalStrut(6));
		arrowPanel.add(rightButton);
		return arrowPanel;
	}

	/**
	 * Creates the header panel for the listbox seen in multiple wizard components.
	 * @param labelString
	 * @param list
	 * @return the correctly layed out JPanel
	 */
	public JPanel createLabelPanel(String labelString)
	{
		JPanel labelPanel = new JPanel();
		labelPanel.setLayout(new BorderLayout());
		labelPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
		labelPanel.add(new JLabel(labelString), BorderLayout.WEST);
		return labelPanel;
	}

	public JComponent buildFormulaPanel() {
		JPanel subPanel = new JPanel();
		subPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		subPanel.setLayout(new BorderLayout());
		formulaArea = new SizeConstrainedTextArea(4000);
		formulaArea.getDocument().addDocumentListener(this);
		
		String test = PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.syntaxtestbuttontext");
		testFormulaButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.syntaxtestbuttontext"));
		testFormulaButton.addActionListener(this);
		
		JPanel formulaPanel = new JPanel();
		formulaPanel.setBorder(BorderFactory.createTitledBorder(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.formula")));
		BoxLayout bLayout = new BoxLayout(formulaPanel, BoxLayout.Y_AXIS);
		formulaPanel.setLayout(new BoxLayout(formulaPanel, BoxLayout.Y_AXIS));
		
		JScrollPane scroller = new JScrollPane(formulaArea);
		scroller.setPreferredSize(new Dimension(250, 40));
		
		formulaPanel.add(scroller);
		formulaPanel.add(Box.createVerticalStrut(3));
		
		//Need to add a Panel to contain the button because the BoxLayout is awkward - won't let me left-align the button.
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		buttonPanel.add(testFormulaButton);
		formulaSyntaxExceptionLabel = new JLabel();
		buttonPanel.add(formulaSyntaxExceptionLabel);
		
		formulaPanel.add(buttonPanel);
		
		subPanel.add(formulaPanel, BorderLayout.CENTER);

		return subPanel;
	}


	/**
	 * @param labelString
	 * @param button
	 * @param list
	 * @return
	 */
	public JComponent createSubPanel(String labelString, JButton button, JComponent list)
	{
		JPanel subPanel = new JPanel();
		subPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		subPanel.setLayout(new BorderLayout());
		subPanel.add(createLabelPanel(labelString), BorderLayout.NORTH);
		JScrollPane scroller = new JScrollPane(list);
		scroller.setPreferredSize(new Dimension(250, 200));
		subPanel.add(scroller, BorderLayout.CENTER);
		return subPanel;
	}


	private class CustomTableModel extends DefaultTableModel {

		private Vector rows;

		public CustomTableModel() {
			rows = new Vector();
		}

		@Override
		public void addRow(Vector rowData) {
			rows.add(rowData);
			fireTableDataChanged();
		}

		public void removeRow(int row) {
			rows.remove(row);
			fireTableDataChanged();
		}

		public int getRowCount() {
			if (rows != null) {
				return rows.size();
			}
			return 0;
		}

		public int getColumnCount() {
			return 3;
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			if ((column == 1 || column == 2) && !viewOnly) {
				return true;
			}

			return false;
		}

		@Override
		public Object getValueAt(int row, int column) {
			Vector rowData = (Vector)rows.get(row);
			return rowData.get(column);
		}

		public void setValueAt(Object value, int row, int column) {
			((Vector)rows.get(row)).setElementAt(value, column);
			fireTableDataChanged();
		}

		public void fireTableDataChanged() {
			for (int i = 0; i < rows.size(); i++) {
				if (((Vector)rows.get(i)).get(1) instanceof String
						&& "".equals(((Vector)rows.get(i)).get(1))) {
					JOptionPane.showMessageDialog(DerivedEditDialog.this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.variablelabel"));
				}
				
				Object defaultValue = ((Vector)rows.get(i)).get(2);
				if (defaultValue instanceof Double
						|| DEFAULT_VALUE.equalsIgnoreCase((String)defaultValue)) {
					//Do nothing
				}
				else if (defaultValue == null || defaultValue.equals("")) {
					((Vector)rows.get(i)).set(2, DEFAULT_VALUE);
				}
				else {
					boolean invalidNumber = false;
					try {
						Double.parseDouble((String)defaultValue);
					}
					catch (NumberFormatException nfe) {
						invalidNumber = true;
					}
					if (invalidNumber){
						JOptionPane.showMessageDialog(DerivedEditDialog.this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.defaultvaluenumber"));
					}
				}
			}
			super.fireTableDataChanged();
		}
	}

	public boolean validateEntries() {
		if (getNameField().getText() == null || getNameField().getText().equals("")) {
			JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.nonemptyentry"));
			return false;
		}

		if (getDisplayTextField().getText() == null || getDisplayTextField().getText().equals("")) {
			JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.entrydisplaytextempty"));
			return false;
		}
		for (int i=0; i<dependentTable.getRowCount(); i++) {
			if (dependentTable.getValueAt(i, 1).equals("")){
				JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.variablelabel"));
				return false;
			}
			Object defaultValue = dependentTable.getValueAt(i, 2);
			if (defaultValue instanceof Double
					|| DEFAULT_VALUE.equalsIgnoreCase((String)defaultValue)) {
				//Do nothing
			}
			else if (defaultValue == null || defaultValue.equals("")) {
				dependentTable.setValueAt(DEFAULT_VALUE, i, 2);
			}
			else {
				boolean invalidNumber = false;
				try {
					Double.parseDouble((String)defaultValue);
				}
				catch (NumberFormatException nfe) {
					invalidNumber = true;
				}
				if (invalidNumber){
					JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.defaultvaluenumber"));
					return false;
				}
			}
		}
		return true;

	}

	public void ok() {
		if (validateEntries()) {
			entry = (DerivedEntry)getEntry();

			entry.setUseDefaultValuesForDisabledEntriesInCalculation(this.useDefaultValsForDisabledVars.isSelected());
			
			if (entry != null) {
				if (fieldChanged(entry.getDisplayText(),getDisplayTextField().getText())) {
					changed = true;
					entry.setDisplayText(getDisplayTextField().getText());
				}
				if (fieldChanged(entry.getName(),getNameField().getText())) {
					changed = true;
					entry.setName(getNameField().getText());
				}
				if (fieldChanged(entry.getEntryStatus().toString(),((EntryStatus)getEntryStatusComboBox().getSelectedItem()).toString())) {
					changed = true;
					entry.setEntryStatus((EntryStatus)getEntryStatusComboBox().getSelectedItem());	
				}
				if (fieldChanged(entry.getLabel(),getLabelField().getText())) {
					changed = true;
					entry.setLabel(getLabelField().getText());	
				}
				if (fieldChanged(entry.getDescription(),getHelpField().getText())) {
					changed = true;
					entry.setDescription(getHelpField().getText());
				}
				//Not used by the DEL
				entry.setExportSecurity(getExportSecurityBox().getSecurityValue());
			}

			//first remove all variables, then readd
			List<String> entryVas = new ArrayList<String>(entry.getVariableNames());

			//Check for changed size
			if (dependentTable.getRowCount() != entry.getVariableNames().size()) {
				changed = true;
			}
			else {
				//Check every element exists and has same variable..
				for (int i=0; i<dependentTable.getRowCount(); i++) {
					if (!entryVas.contains(dependentTable.getValueAt(i, 1).toString())) {
						changed = true;
						break;
					}
					BasicEntry orgEntry = entry.getVariable(dependentTable.getValueAt(i, 1).toString());
					BasicEntry newEntry = (BasicEntry)dependentTable.getValueAt(i, 0);
					if (orgEntry != newEntry) {
						//Variable name is now pointing to a different entry
						changed = true;
						break;
					}
				}
			}


			for (int i=entryVas.size()-1; i>=0; i--) {
				((DerivedEntry)entry).removeVariable(((String)entryVas.get(i)));
			}

			for (int i=0; i<dependentTable.getRowCount(); i++) {
				entry.addVariable(dependentTable.getValueAt(i, 1).toString(), 
						(BasicEntry)dependentTable.getValueAt(i, 0));
				if (dependentTable.getValueAt(i, 2) != null 
						&& !DEFAULT_VALUE.equalsIgnoreCase(dependentTable.getValueAt(i, 2).toString())) {
					String defaultValue = dependentTable.getValueAt(i, 2).toString();
					NumericValue numValue = new NumericValue(Double.parseDouble(defaultValue));
					entry.addVariableDefault(dependentTable.getValueAt(i, 1).toString(), numValue);
				}
				else if (entry.getVariableDefaults().containsKey(dependentTable.getValueAt(i, 1).toString())) {
					entry.removeVariableDefault(dependentTable.getValueAt(i, 1).toString());
				}
			}
			if (fieldChanged(entry.getFormula(),formulaArea.getText())) {
				changed = true;
				entry.setFormula(formulaArea.getText());	
			}

			saveOptionDepencies();
		}
		
	}
	
	/**
	 * Returns the variables that the user has defined in the dialog.
	 * The returned map contains the variable name as the key, and the assigned default value.
	 * @return
	 */
	public Map<String, NumericValue> getDefinedVariables(){
		
		Map<String, NumericValue> returnMap = new HashMap<String, NumericValue>();
		
		for (int i=0; i<dependentTable.getRowCount(); i++) {
			
			String variableName = dependentTable.getValueAt(i, 1).toString();
			NumericValue defaultVal = null;
		
			if (dependentTable.getValueAt(i, 2) != null 
					&& !DEFAULT_VALUE.equalsIgnoreCase(dependentTable.getValueAt(i, 2).toString())) {
				String defaultValue = dependentTable.getValueAt(i, 2).toString();
				defaultVal = new NumericValue(Double.parseDouble(defaultValue));	
			}
			
			returnMap.put(variableName, defaultVal);
			
		}
		
		return returnMap;
	}

	public void populate() {
		super.populate();
		DefaultListModel numericEntryModel = new DefaultListModel();
		Document document = DatasetController.getInstance().getActiveDocument();

		useDefaultValsForDisabledVars.setSelected(((DerivedEntry)getEntry()).getUseDefaultValuesForDisabledEntriesInCalculation());

		//including numeric entries and dates for now!
		for (int i=0; i<document.numEntries(); i++) {
			//don't include entries after the current entry
			if (document.getEntry(i).equals(getEntry())) {
				break;
			} else if (document.getEntry(i) instanceof NumericEntry
					|| document.getEntry(i) instanceof DateEntry
					|| document.getEntry(i) instanceof IntegerEntry
					|| document.getEntry(i) instanceof OptionEntry) {
				numericEntryModel.addElement(document.getEntry(i));
			} else if (document.getEntry(i) instanceof DerivedEntry
					&& !(document.getEntry(i).equals(getEntry()))) {
				numericEntryModel.addElement(document.getEntry(i));
			}
		}
		numericEntryList.setModel(numericEntryModel);

		if (getEntry() != null)
		{
			Set variableSet = ((DerivedEntry)getEntry()).getVariableNames();
			Iterator setIt = variableSet.iterator();
			CustomTableModel dependentEntryModel = new CustomTableModel();
			while(setIt.hasNext()) {
				String variableName = (String)setIt.next();
				Entry curEntry = ((DerivedEntry)getEntry()).getVariable(variableName);
				NumericValue defaultValue = ((DerivedEntry)getEntry()).getVariableDefaults().get(variableName);
				Vector newVector = new Vector();
				newVector.add(curEntry);
				newVector.add(variableName);
				if (defaultValue == null || defaultValue.isNull()) {
					newVector.add(DEFAULT_VALUE);	
				}
				else {
					newVector.add(defaultValue.getTheValue());
				}

				numericEntryModel.removeElement(curEntry);
				dependentEntryModel.addRow(newVector);
			}
			dependentTable.setModel(dependentEntryModel);
			dependentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			dependentTable.getColumnModel().getColumn(0).setHeaderValue(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.derivedname"));
			dependentTable.getColumnModel().getColumn(1).setHeaderValue(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.derivedlabel"));
			dependentTable.getColumnModel().getColumn(2).setHeaderValue(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.deriveddefault"));
			TableColumn col = dependentTable.getColumnModel().getColumn(0);
			col.setCellRenderer(new EntryTableCellRenderer());
			formulaArea.setText(((DerivedEntry)getEntry()).getFormula());
		}
	}
	
	/**
	 * I extended Evaluator to get public access to the protected 'isExpressionString' method, which was
	 * apparently useful for testing the validity of an expression string without actually evaluating it.
	 * But it doesn't work, even though the same supposedly invalid expression can be numerically evaluated. 
	 * @author Bill
	 *
	 */
	private class CustomEvaluator extends Evaluator{

		public CustomEvaluator(){
			super();
		}
		
		public boolean isExpressionValid(String expressionString) throws EvaluationException{
			return this.isExpressionString(expressionString);
		}
		
	}
	
	@Override
	public void actionPerformed(ActionEvent aet) {
		// TODO Auto-generated method stub
		super.actionPerformed(aet);
		
		if(aet.getSource() == testFormulaButton){
			testFormulaSyntax(new StringBuffer());
		}
		
		if(aet.getSource() == okButton){
			StringBuffer errorBuf = new StringBuffer();
			boolean syntaxIsValid = testFormulaSyntax(errorBuf);
			
			if(!syntaxIsValid){
				
			}else{
				ok();
			}
		}
		

	}
	
	/**
	 * Tests the syntax validity of the currently-entered formula.
	 * @param errorString - a non-null String object that will contain the error message, if any.
	 * @return - true or false
	 */
	private boolean testFormulaSyntax(StringBuffer errorString){
		
		if(errorString == null || errorString.length() != 0)
			throw new IllegalArgumentException(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.testformulasyntaxparamerror"));
		
		String formula = formulaArea.getText();
		
		CustomEvaluator evaluator = new CustomEvaluator();
		evaluator.putFunction(new If());
		
		Map<String, NumericValue> variablesMap = getDefinedVariables();
		
		for(String key : variablesMap.keySet()){
			String variableVal = variablesMap.get(key) == null ? new Double(0).toString() : variablesMap.get(key).getValueAsString();
			evaluator.putVariable(key, variableVal);
		}
		
		String[] varNameArray = new String[0];
		
		varNameArray = variablesMap.keySet().toArray(varNameArray);
		
		List<String> listOfVariables = Arrays.asList(varNameArray);
		
    	String jevalFormula = JEvalHelper.escapeVariablesInFormula(formula, listOfVariables);
    	
    	boolean isValid = false;
    	
    	try {
			evaluator.getNumberResult(jevalFormula);
			isValid = true;
		} catch (EvaluationException e) {
			isValid = false;
			String problem = e.getCause() != null ? e.getCause().toString() : e.getMessage();
			errorString.append(problem);
		}			
		
		if(isValid){
			testFormulaButton.setIcon(IconsHelper.getInstance().getImageIcon("check.png"));
			
		}else{
			testFormulaButton.setIcon(IconsHelper.getInstance().getImageIcon("cross.png"));
			this.formulaSyntaxExceptionLabel.setText(errorString.toString());
		}
		
		return isValid;
	}

	private void clearErrorReporting(){
		testFormulaButton.setIcon(null);
		formulaSyntaxExceptionLabel.setText(null);
	}
	
	
	public void changedUpdate(DocumentEvent arg0) {
		
		clearErrorReporting();
		
	}

	
	public void insertUpdate(DocumentEvent arg0) {
		
		clearErrorReporting();
		
	}

	
	public void removeUpdate(DocumentEvent arg0) {
		
		clearErrorReporting();
		
	}
	
	@Override
	protected boolean doExtensionSpecificValidation() {
		
		StringBuffer errorString = new StringBuffer();
		boolean response = this.testFormulaSyntax(errorString);
		
		if(!response){
			
			//Need to put up a dialog here asking if the user wants to dismiss the dialog or not.
			String warningMessageString = new String(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.syntaxerrormsg1"));
			warningMessageString = warningMessageString.concat(" ");
			warningMessageString = warningMessageString.concat(errorString.toString());
			warningMessageString = warningMessageString.concat(" ");
			warningMessageString = warningMessageString.concat(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.syntaxerrormsg2"));
			
			int answer = WrappedJOptionPane.showConfirmDialog(this, warningMessageString, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.syntaxerrordlgtitle"), WrappedJOptionPane.YES_NO_OPTION, WrappedJOptionPane.WARNING_MESSAGE);
			
			switch (answer) {
			case WrappedJOptionPane.YES_OPTION:
				response = true;
				break;
			case WrappedJOptionPane.NO_OPTION:
				response = false;
				break;
			}
			
		}

		return response;
	}

}