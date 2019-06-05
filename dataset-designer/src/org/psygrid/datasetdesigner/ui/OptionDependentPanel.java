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
import java.awt.Dimension;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
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
import org.psygrid.datasetdesigner.custom.CustomCopyPasteJList;
import org.psygrid.datasetdesigner.model.DSOption;
import org.psygrid.datasetdesigner.model.DummyDocument;
import org.psygrid.datasetdesigner.renderer.EntryTableCellRenderer;
import org.psygrid.datasetdesigner.renderer.OptionListCellRenderer;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.utils.Utils;

/**
 * The panel containing the option dependencies
 * for a given entry;  also assigning and removing
 * of these dependencies
 * 
 * @author pwhelan
 */
public class OptionDependentPanel extends JPanel {

	/**
	 * List containing all available options
	 */
	private JList allOptionsList;

	/**
	 * The options on which the entry depends
	 */
	private JTable dependentTable;

	/**
	 * Button to create dependencies
	 */
	private JButton assignButton;

	/**
	 * Button to remove dependencies
	 */
	private JButton unassignButton;

	/**
	 * The document to which this entry belongs
	 */
	private Document document;
	
	/**
	 * The entry to which dependencies can be assigned and removed
	 */
	private Entry entry;

	/**
	 * The factory use for creating and manipulating options
	 */
	private HibernateFactory factory;

	/**
	 * Flag to indicate if the mode is using the DEL
	 */
	private boolean isDEL;

	/**
	 * Constructor - lay out the panel and initialise variables
	 * @param document the parent of the entry
	 * @param entry the entry to which dependencies will be assigned/removed
	 * @param isDEL true if DEL mode is being used; false if not 
	 * @param viewOnly true if entry can only be viewed; false if not
	 */
	public OptionDependentPanel(Document document, Entry entry, boolean isDEL, boolean viewOnly) {
		this.entry = entry;
		this.document = document;
		this.isDEL = isDEL;
		allOptionsList = new CustomCopyPasteJList();
		allOptionsList.setModel(new DefaultListModel());
		allOptionsList.setCellRenderer(new OptionListCellRenderer());
		allOptionsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		dependentTable = new JTable(new CustomTableModel());
		dependentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		dependentTable.getColumnModel().getColumn(0).setHeaderValue(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.optionsname"));
		dependentTable.getColumnModel().getColumn(1).setHeaderValue(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.optionsstatus"));

		TableColumn col = dependentTable.getColumnModel().getColumn(0);
		col.setCellRenderer(new EntryTableCellRenderer());

		TableColumn selectColumn = dependentTable.getColumnModel().getColumn(1);
		JComboBox comboBox = new JComboBox();
		comboBox.addItem(EntryStatus.MANDATORY);
		comboBox.addItem(EntryStatus.DISABLED);
		comboBox.addItem(EntryStatus.OPTIONAL);
		selectColumn.setCellEditor(new DefaultCellEditor(comboBox));

		assignButton = new CustomIconButton(new AssignOptionAction(allOptionsList, dependentTable), "Assign Option");
		unassignButton = new CustomIconButton(new UnassignOptionAction(allOptionsList, dependentTable), "Unassign Option");

		if(!viewOnly){
			add(createSubPanel("All Options", assignButton, allOptionsList));
			add(createArrowPanel(unassignButton, assignButton));
		}

		add(createSubPanel("Dependent Options", unassignButton, dependentTable));

		if(!viewOnly)
			setBorder(BorderFactory.createTitledBorder("Configure option dependencies"));
		else
			setBorder(BorderFactory.createTitledBorder("View option dependencies"));

		populate();
	}

	/**
	 * Populate the table with the existing option dependencies
	 */
	public void populate() {
		Vector<Option> dependents = new Vector<Option>();

		for (int j=0; j<dependentTable.getModel().getRowCount(); j++) {
			((DefaultTableModel)dependentTable.getModel()).removeRow(j);
		}

		//first populate dependencies
		if (document != null) {		//document can be null when viewing entry via DEL search dialog
			for (int i=0; i<document.numEntries(); i++) {
				if (document.getEntry(i) instanceof OptionEntry){
					OptionEntry optEntry = (OptionEntry)document.getEntry(i);
					if (!(optEntry.equals(entry))) {
						for (int j=0; j<optEntry.numOptions(); j++) {
							Option option = optEntry.getOption(j);
							for (int z=0; z<option.numOptionDependents(); z++) {
								OptionDependent dependent = option.getOptionDependent(z);
								if (dependent.getDependentEntry().equals(entry)) {
									((DefaultTableModel)dependentTable.getModel()).addRow(new Object[]{new DSOption(optEntry.getName(), option), dependent.getEntryStatus()});
									dependents.add(option);
								}
							}
						}
					}
				}
			}
		}

		if (document != null) {		//document can be null when viewing entry via DEL search dialog
			for (int z=0; z<document.numEntries(); z++) {
				if (document.getEntry(z) instanceof OptionEntry) {
					OptionEntry optEntry = (OptionEntry)document.getEntry(z);
					if (!(optEntry.equals(entry))) {
						for (int y=0; y<optEntry.numOptions(); y++) {
							Option opt = optEntry.getOption(y);
							if (!dependents.contains(opt)) {
								((DefaultListModel)allOptionsList.getModel()).addElement(new DSOption(optEntry.getName(), opt));
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Save the dependencies set up in this panel
	 */
	public void saveDependencies() {
		for (int i=0; i<dependentTable.getRowCount(); i++) {
			for (int h=0; h<document.numEntries(); h++) {
				if (document.getEntry(h) instanceof OptionEntry){
					OptionEntry optEntry = (OptionEntry)document.getEntry(h);
					if (optEntry.getName().equals(((DSOption)dependentTable.getValueAt(i, 0)).getEntryName())) {
						for (int j=0; j<optEntry.numOptions(); j++) {
							Option option = optEntry.getOption(j);
							if (option.equals(((DSOption)dependentTable.getValueAt(i, 0)).getOption())) {
								boolean foundMatch = false;
								for (int z=0; z<option.numOptionDependents(); z++) {
									OptionDependent dependent = option.getOptionDependent(z);
									if (dependent.getDependentEntry().equals(entry)) {
										if (!dependent.getEntryStatus().equals(dependentTable.getValueAt(i, 1))) {
											dependent.setEntryStatus((EntryStatus)dependentTable.getValueAt(i, 1));
											((Entry)optEntry).setIsRevisionCandidate(true);
											((Document)DatasetController.getInstance().getActiveDocument()).setIsRevisionCandidate(true);

											//Required for composites..
											if (DatasetController.getInstance().getActiveDocument() instanceof DummyDocument) {
												Entry singleEntry = Utils.getMainEntry((DummyDocument)DatasetController.getInstance().getActiveDocument());
												singleEntry.setIsRevisionCandidate(true);
											}
										}

										foundMatch = true;
									}
								}
								if (!foundMatch) { 
									createOptionDependencies(((DSOption)dependentTable.getValueAt(i, 0)).getOption(), 
											(EntryStatus)dependentTable.getValueAt(i, 1));

									//Option has changed so mark as revision candidate
									((Entry)optEntry).setIsRevisionCandidate(true);
									((Document)DatasetController.getInstance().getActiveDocument()).setIsRevisionCandidate(true);
									
									//Required for composites..
									if (DatasetController.getInstance().getActiveDocument() instanceof DummyDocument) {
										Entry singleEntry = Utils.getMainEntry((DummyDocument)DatasetController.getInstance().getActiveDocument());
										singleEntry.setIsRevisionCandidate(true);
									}
									
									foundMatch = false;
								}
							}
						}
					}
				}
			}
		}

		for (int h=0; h<document.numEntries(); h++) {
			if (document.getEntry(h) instanceof OptionEntry) {
				OptionEntry optEntry = (OptionEntry)document.getEntry(h);
				for (int z=0; z<optEntry.numOptions(); z++) {
					Option option = optEntry.getOption(z);
					for (int y=0; y<option.numOptionDependents(); y++) {
						OptionDependent dependent = option.getOptionDependent(y);
						boolean foundIt = false;

						for (int t=0; t<dependentTable.getRowCount(); t++) {
							if (dependent.getDependentEntry().equals(entry)) {
								if(option.getName().equals(((DSOption)dependentTable.getValueAt(t, 0)).getOption().getName())) {
									if(optEntry.getName().equals(((DSOption)dependentTable.getValueAt(t, 0)).getEntryName())) {
										foundIt = true;
									}
								}
							} 
						}
						if (!foundIt) {
							if (option.getOptionDependent(y).getDependentEntry().equals(entry)) {
								option.removeOptionDependent(y);

								//Option has changed so mark as revision candidate
								((Entry)optEntry).setIsRevisionCandidate(true);
								((Document)DatasetController.getInstance().getActiveDocument()).setIsRevisionCandidate(true);

								//Required for composites..
								if (DatasetController.getInstance().getActiveDocument() instanceof DummyDocument) {
									Entry singleEntry = ((Document)DatasetController.getInstance().getActiveDocument()).getEntry(0);
									for (Object entry: ((Document)DatasetController.getInstance().getActiveDocument()).getEntries()) {
										if (entry instanceof ExternalDerivedEntry) {
											singleEntry = (Entry)entry;
										}
										else if (entry instanceof DerivedEntry) {
											singleEntry = (Entry)entry;
										}
										else if (entry instanceof OptionEntry) {
											singleEntry = (Entry)entry;
										}
									}
									singleEntry.setIsRevisionCandidate(true);
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Create an option dependency
	 * @param option the option to assign the dependency
	 * @param status the status of the dependency
	 */
	public void createOptionDependencies(Option option, EntryStatus status) {
		OptionDependent optDep = getFactory().createOptionDependent();
		optDep.setEntryStatus(status);
		option.addOptionDependent(optDep);
		optDep.setDependentEntry(entry);
	}

	/**
	 * Get a singleton factory instance
	 * @return the singleton factory
	 */
	public HibernateFactory getFactory() {
		if (factory == null) {
			factory = new HibernateFactory();
		}
		return factory;
	}


	/**
	 * Layout a panel with a string, button and list
	 * @param labelString the title of the panel
	 * @param button the button to be used 
	 * @param list the list to which things are assigned
	 * @return the configured <code>JComponent</code>
	 */
	private JComponent createSubPanel(String labelString, JButton button, JComponent list)
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

	/**
	 * Create the panel containing the arrows for assigning/removing options
	 * @param rightButton
	 * @param leftButton
	 * @return the configured <code>JPanel</code>
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
	 * Table Model to add/remove option dependencies
	 * @author pwhelan
	 */
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
			return 2;
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			if (column == 1) {
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
		}

	}

}