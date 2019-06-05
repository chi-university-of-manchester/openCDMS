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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.psygrid.data.model.hibernate.*;
import org.psygrid.datasetdesigner.actions.AssignOptionAction;
import org.psygrid.datasetdesigner.actions.UnassignOptionAction;
import org.psygrid.datasetdesigner.controllers.DatasetController;
import org.psygrid.datasetdesigner.renderer.EntryTableCellRenderer;
import org.psygrid.datasetdesigner.renderer.OptionListCellRenderer;
import org.psygrid.datasetdesigner.ui.CustomIconButton;
import org.psygrid.datasetdesigner.ui.MainFrame;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;

public class ExternalDerivedEditDialog extends AbstractEditDialog {

	private ExternalDerivedEntry entry;

	private JTable dependentTable;
	private JButton assignVariableButton;
	private JButton unassignVariableButton;

	private JList numericEntryList;

	private JComboBox transformerComboBox;

	public ExternalDerivedEditDialog(MainFrame frame, ExternalDerivedEntry entry, boolean isDEL, boolean canEdit) {
		super(frame, entry, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.configureexternalentry"), true, false, isDEL, canEdit);
		this.entry = entry;
		setModal(true);
		setLocationRelativeTo(null);  
		pack();
	}

	public ExternalDerivedEditDialog(JDialog parent, ExternalDerivedEntry entry, boolean isDEL, boolean canEdit) {
		super(parent, entry, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.viewexternalentry"), true, false, isDEL, canEdit);
		this.entry = entry;
		setModal(true);
		setLocationRelativeTo(null);  
		pack();
	}

	public JPanel getGenericPanel() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(super.getGenericPanel(), BorderLayout.CENTER);
		mainPanel.add(buildVariablePanel(), BorderLayout.SOUTH);
		return mainPanel;
	}

	private JPanel buildVariablePanel() {
		JPanel mainPanel = new JPanel();
		mainPanel.setBorder(BorderFactory.createTitledBorder(viewOnly? "View variable options":PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.configurevariableoptions")));
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		JPanel transformerPanel = new JPanel();
		transformerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		transformerPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.assigntransformer")));
		transformerComboBox = new JComboBox();
		transformerComboBox.setRenderer(new OptionListCellRenderer());
		transformerPanel.add(transformerComboBox);

		JPanel variablePanel = new JPanel();
		numericEntryList = new JList();
		numericEntryList.setCellRenderer(new OptionListCellRenderer());
		numericEntryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		dependentTable = new JTable(new CustomTableModel());
		dependentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		dependentTable.getColumnModel().getColumn(0).setHeaderValue(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.derivedname"));
		dependentTable.getColumnModel().getColumn(1).setHeaderValue(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.derivedlabel"));
		dependentTable.getColumnModel().getColumn(2).setHeaderValue(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.derivedmandatory"));
		TableColumn col = dependentTable.getColumnModel().getColumn(0);
		col.setCellRenderer(new EntryTableCellRenderer());

		if(!viewOnly){
			assignVariableButton   = new CustomIconButton(new AssignOptionAction(numericEntryList, dependentTable, AssignOptionAction.VARIABLE), PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.assignoption"));
			unassignVariableButton = new CustomIconButton(new UnassignOptionAction(numericEntryList, dependentTable), PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.unassignoption"));

			variablePanel.add(createSubPanel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.allvariables"), assignVariableButton, numericEntryList));
			variablePanel.add(createArrowPanel(unassignVariableButton, assignVariableButton));
		}

		variablePanel.add(createSubPanel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.dependentvariables"), unassignVariableButton, dependentTable));

		mainPanel.add(transformerPanel);
		mainPanel.add(variablePanel);

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
	 * @param assignButton
	 * @return the correctly layed out JPanel
	 */
	public JPanel createLabelPanel(String labelString, JComponent list, JButton assignButton)
	{
		JPanel labelPanel = new JPanel();
		labelPanel.setLayout(new BorderLayout());
		labelPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
		labelPanel.add(new JLabel(labelString), BorderLayout.WEST);
		return labelPanel;
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
		subPanel.add(createLabelPanel(labelString, list, button), BorderLayout.NORTH);
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
			if ("".equals(rowData.get(2))) {
				rowData.set(2, Boolean.TRUE);
			}
			rows.add(rowData);
			fireTableDataChanged();
		}

		@Override
		public void removeRow(int row) {
			rows.remove(row);
			fireTableDataChanged();
		}

		@Override
		public int getRowCount() {
			if (rows != null) {
				return rows.size();
			}
			return 0;
		}

		@Override
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

		@Override
		public void setValueAt(Object value, int row, int column) {
			((Vector)rows.get(row)).setElementAt(value, column);
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			if (columnIndex == 2) {
				return Boolean.class;
			}
			return super.getColumnClass(columnIndex);
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

		if (dependentTable.getRowCount() == 0) {
			JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.onevariableentered"));
			return false;
		}

		for (int i=0; i<dependentTable.getRowCount(); i++) {
			if (dependentTable.getValueAt(i, 1).equals("")){
				JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.eachvariablelabel"));
				return false;
			} 

			try {
				Integer a = new Integer(dependentTable.getValueAt(i, 1).toString());
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.eachvariableinteger"));
				return false;
			}
		}

		if (transformerComboBox.getSelectedItem() == null || transformerComboBox.getSelectedItem().equals("")) {
			JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.transformernonempty"));
			return false;
		}

		return true;
	}

	public void ok() {
		entry = (ExternalDerivedEntry)getEntry();

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

		//first remove all variables, then readd
		List<String> names = new ArrayList<String>();
		names.addAll(entry.getVariableNames());
		for (int i=names.size()-1; i >= 0; i--) {
			String name = names.get(i);
			entry.removeVariable(name);
		}

		((ExternalDerivedEntry)entry).setTransformRequiredVariables(new ArrayList<String>());

		for (int i=0; i<dependentTable.getRowCount(); i++) {
			entry.addVariable(dependentTable.getValueAt(i, 1).toString(), 
					(BasicEntry)dependentTable.getValueAt(i, 0));
			Boolean isRequired = false;
			if (dependentTable.getValueAt(i, 2) != null) {
				isRequired = (Boolean)dependentTable.getValueAt(i, 2);
			}
			if (isRequired) {
				entry.addTransformRequiredVariable(dependentTable.getValueAt(i, 1).toString());
			}
			else {
				entry.setTransformWithStdCodes(true);
			}
		}

		if (!((Transformer)transformerComboBox.getSelectedItem()).equals(entry.getExternalTransformer())) {
			changed = true;
			entry.setExternalTransformer((Transformer)transformerComboBox.getSelectedItem());
		}
	}

	public void populate() {
		super.populate();
		DefaultListModel numericEntryModel = new DefaultListModel();
		Document document = DatasetController.getInstance().getActiveDocument();

		if (document != null) {
			for (int i=0; i<document.numEntries(); i++) {

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
		}

		numericEntryList.setModel(numericEntryModel);

		if (getEntry() != null)
		{
			Set variableSet = ((ExternalDerivedEntry)getEntry()).getVariableNames();
			Iterator setIt = variableSet.iterator();
			CustomTableModel dependentEntryModel = new CustomTableModel();
			while(setIt.hasNext()) {
				String variableName = (String)setIt.next();
				Entry curEntry = ((ExternalDerivedEntry)getEntry()).getVariable(variableName);
				Vector newVector = new Vector();
				newVector.add(curEntry);
				newVector.add(variableName);
				boolean isRequired = false;
				if (((ExternalDerivedEntry)getEntry()).isTransformWithStdCodes()
						&& ((ExternalDerivedEntry)getEntry()).getTransformRequiredVariables().contains(variableName)) {
					isRequired = true;
				}
				else if (!((ExternalDerivedEntry)getEntry()).isTransformWithStdCodes()) {
					isRequired = true;
				}
				newVector.add(new Boolean(isRequired));
				numericEntryModel.removeElement(curEntry);
				dependentEntryModel.addRow(newVector);
			}
			dependentTable.setModel(dependentEntryModel);
			dependentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			dependentTable.getColumnModel().getColumn(0).setHeaderValue(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.derivedname"));
			dependentTable.getColumnModel().getColumn(1).setHeaderValue(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.derivedlabel"));
			dependentTable.getColumnModel().getColumn(2).setHeaderValue(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.derivedmandatory"));

			TableColumn col = dependentTable.getColumnModel().getColumn(0);
			col.setCellRenderer(new EntryTableCellRenderer());

			DefaultComboBoxModel comboBoxModel = new DefaultComboBoxModel();

			if(getEntry().getDataSet() != null){
				for (int i=0; i<getEntry().getDataSet().numTransformers(); i++) {
					comboBoxModel.addElement(getEntry().getDataSet().getTransformer(i));
				}
			}

			transformerComboBox.setModel(comboBoxModel);
			if (((ExternalDerivedEntry)getEntry()).getExternalTransformer() != null) {
				transformerComboBox.setSelectedItem(((ExternalDerivedEntry)getEntry()).getExternalTransformer());
			}
		}
	}

}