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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.psygrid.data.model.hibernate.*;
import org.psygrid.datasetdesigner.actions.AddOptionAction;
import org.psygrid.datasetdesigner.actions.RemoveOptionAction;
import org.psygrid.datasetdesigner.controllers.DatasetController;
import org.psygrid.datasetdesigner.custom.CustomCopyPasteJList;
import org.psygrid.datasetdesigner.custom.TextFieldWithStatus;
import org.psygrid.datasetdesigner.model.DocTreeModel;
import org.psygrid.datasetdesigner.renderer.OptionListCellRenderer;
import org.psygrid.datasetdesigner.ui.MainFrame;
import org.psygrid.datasetdesigner.utils.IconsHelper;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.utils.SpringUtilities;

/**
 * The configuration for option entries.  This includes the layout of basic properties
 * and a list of all options assigned to the option entry; also the buttons to 
 * trigger dialogs of editing and removing buttons. 
 * 
 * @author pwhelan
 */
public class OptionEditDialog extends AbstractEditDialog implements ActionListener {

	//buttons to edit and remove options
	private JButton editButton;
	private JButton removeButton;

	//basic option properties
	private TextFieldWithStatus optionNameField;
	private TextFieldWithStatus optionDisplayField;
	private TextFieldWithStatus optionCodeField;
	
	//the list of options for this entry
	private JList optionList;

	//the option entry being configured
	private OptionEntry entry;

	//the parent composite if this option is part of a composite entry
	private CompositeEntry parentEntry;

	//indicates if the option entry is part of a composite or not
	private boolean isCompositeEntry = false;
	
	private Option defaultValue = null;
	
	private JComboBox defaultComboBox;
	
	private DefaultComboBoxModel defaultComboBoxModel;
	
	private ActionListener itemChangedListener;

	//buttons to move the options up and down in the list
	private JButton upButton;
	private JButton downButton;

	public OptionEditDialog(MainFrame frame, OptionEntry entry, CompositeEntry parentEntry, boolean isComp, boolean isDEL, boolean canEdit) {
		super(frame, entry, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.configureoptionentry"), false, true, true, isDEL, canEdit);
		postPopulate();
		this.entry = entry;
		this.parentEntry = parentEntry;
		isCompositeEntry = true;
		setModal(true);
		setLocationRelativeTo(null);  
		pack();
	}

	public OptionEditDialog(MainFrame frame, OptionEntry entry, boolean isDEL, boolean canEdit) {
		super(frame, entry, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.configureoptionentry"), true, true, isDEL, canEdit);
		postPopulate();
		this.entry = entry;
		setModal(true);
		setLocationRelativeTo(null);  
		pack();
	}

	public OptionEditDialog(JDialog parent, OptionEntry entry, Document entryContext, boolean isDEL, boolean canEdit) {
		super(parent, entry, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.viewoptionentry"), true, true, entryContext, isDEL, canEdit);
		postPopulate();
		this.entry = entry;
		setModal(true);
		setLocationRelativeTo(null);  
		pack();
	}

	public JPanel getGenericPanel() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(super.getGenericPanel(), BorderLayout.NORTH);
		mainPanel.add(buildOptionList(), BorderLayout.CENTER);
		return mainPanel;
	}

	public JPanel buildMainPanel() {
		JPanel mainPanel = new JPanel();
		mainPanel.add(getGenericPanel(), BorderLayout.CENTER);
		mainPanel.add(buildOptionList(), BorderLayout.SOUTH);
		return mainPanel;
	}

	private JPanel buildOptionList() {
		JPanel optionPanel = new JPanel();
		optionPanel.setLayout(new BorderLayout());
		optionPanel.setBorder(BorderFactory.createTitledBorder(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.options")));

		optionList = new CustomCopyPasteJList();
		optionList.setCellRenderer(new OptionListCellRenderer());
		optionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		optionList.setModel(new DefaultListModel());
		
		//Only enable the buttons if an option is selected
		optionList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				boolean enabled = true;
				if (optionList == null || optionList.getSelectedValue() == null) {
					enabled = false;
				}
				if (removeButton != null) { removeButton.setEnabled(enabled); }
				if (editButton   != null) { editButton.setEnabled(enabled);   }
			}
		});
		
		JPanel configuredPanel = new JPanel();
		configuredPanel.setLayout(new BorderLayout());

		JPanel headerPanel = new JPanel();
		headerPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		headerPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.reorderoptions")), BorderLayout.NORTH);

		upButton = new JButton(IconsHelper.getInstance().getImageIcon("1uparrow.png"));
		downButton = new JButton(IconsHelper.getInstance().getImageIcon("1downarrow.png"));
		upButton.addActionListener(this);
		downButton.addActionListener(this);
		headerPanel.add(upButton);
		headerPanel.add(downButton);

		configuredPanel.add(headerPanel, BorderLayout.SOUTH);
		configuredPanel.add(new JScrollPane(optionList), BorderLayout.CENTER);

		optionPanel.add(configuredPanel, BorderLayout.CENTER);

		if (!viewOnly) {
			JPanel optionButtonPanel = new JPanel();
			optionButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
			JButton addButton = new JButton(new AddOptionAction(this, optionList));
			editButton = new JButton(new AddOptionAction(this, optionList, true));
			editButton.setEnabled(false);
			removeButton = new JButton(new RemoveOptionAction(optionList));
			removeButton.setEnabled(false);
			
			optionButtonPanel.add(addButton);
			optionButtonPanel.add(editButton);
			optionButtonPanel.add(removeButton);
			
			optionPanel.add(optionButtonPanel, BorderLayout.NORTH);
		} else {
			upButton.setEnabled(false);
			downButton.setEnabled(false);
		}
		
		JPanel defaultPanel = new JPanel();
		defaultPanel.setLayout(new SpringLayout());
		defaultPanel.add(new JLabel("Default value:"));
		defaultComboBox = new JComboBox();
		defaultComboBoxModel = new DefaultComboBoxModel();
		defaultComboBox.setModel(defaultComboBoxModel);
		defaultComboBox.setRenderer(new OptionListCellRenderer());
		defaultPanel.add(defaultComboBox);
		itemChangedListener = new ItemChangedListener();
		defaultComboBox.addActionListener(itemChangedListener);
		defaultPanel.add(defaultComboBox);
		
		SpringUtilities.makeCompactGrid(defaultPanel,
				1, 2, //rows, cols
				6, 6,        //initX, initY
				6, 6);       //xPad, yPad
		
		optionPanel.add(defaultPanel, BorderLayout.SOUTH);
		
		return optionPanel;
	}

	public void populate() {
		super.populate();
	}

	public void postPopulate() {
		DefaultListModel optionModel = new DefaultListModel();
		
		//add an empty entry to the defaultComboBoxModel
		defaultComboBoxModel.addElement(null);
		
		try {
			for (int i=0; i<((OptionEntry)getEntry()).numOptions(); i++) {
				Option option = ((OptionEntry)getEntry()).getOption(i);
				optionModel.addElement(option);
				defaultComboBoxModel.addElement(option);
			}
		} catch (org.psygrid.data.model.hibernate.ModelException mex) {
			//reached the end of options
		} catch (NullPointerException nex) {
		}
		optionList.setModel(optionModel);
		optionList.setCellRenderer(new OptionListCellRenderer());
		
		//set the default value
		defaultValue = ((OptionEntry)getEntry()).getDefaultValue();
		
		optionModel.addListDataListener(new ModelChangedListener());
		defaultComboBox.setSelectedItem(defaultValue);
		
	}
	
	public boolean validateEntries() {
		if (getNameField().getText() == null || getNameField().getText().equals("")) {
			JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.optionnamefieldnonempty"));
			return false;
		}

		if (getDisplayTextField().getText() == null || getDisplayTextField().getText().equals("")) {
			JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.optiondisplaytextnonempty"));
			return false;
		}


		if (isCompositeEntry) {
			for (int i=0; i<parentEntry.numEntries(); i++) {
				if (!parentEntry.getEntry(i).equals(entry)) {
					if (parentEntry.getEntry(i).getName().equals(getNameField().getText())) {
						JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.entrycompositeexists"));
						return false;
					}
					if (parentEntry.getEntry(i).getDisplayText().equals(getDisplayTextField().getText())) {
						JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.displaytextcompositeexists"));
						return false;
					}
				}
			}
		} else {
			Document doc = DatasetController.getInstance().getActiveDocument();
			for (int i=0; i<doc.numEntries(); i++) {
				Entry curEntry = doc.getEntry(i);
				if (!curEntry.equals(entry)) {
					if (curEntry.getName().equalsIgnoreCase(getNameField().getText())) {
						JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.optionnamealreadyexists"));
						return false;
					} else if (curEntry.getDisplayText() != null) {
						if (curEntry.getDisplayText().equalsIgnoreCase(getDisplayTextField().getText())) {
							JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.optiondisplaytexexists"));
							return false;
						}
					}
				}
			}
		}


		return true;
	}


	private void swap(int a, int b) {
		Object aObject = optionList.getModel().getElementAt(a);
		Object bObject = optionList.getModel().getElementAt(b);
		((DefaultListModel)optionList.getModel()).set(a, bObject);
		((DefaultListModel)optionList.getModel()).set(b, aObject);
	}

	public void ok() {
		//TODO: Presently if you are only to change the display text on one of the options, changed will NOT
		//be set to TRUE.
		if (entry != null) {
			if (fieldChanged(entry.getDisplayText(),getDisplayTextField().getText())) {
				changed = true;
				entry.setDisplayText(getDisplayTextField().getText());
			}
			if (fieldChanged(entry.getName(),getNameField().getText())) {
				changed = true;
				entry.setName(getNameField().getText());
			}
			
			if (!isCompositeEntry) {
				if (fieldChanged(entry.getEntryStatus().toString(),((EntryStatus)getEntryStatusComboBox().getSelectedItem()).toString())) {
					changed = true;
					entry.setEntryStatus((EntryStatus)getEntryStatusComboBox().getSelectedItem());	
				}
			}

			if (fieldChanged(entry.getLabel(),getLabelField().getText())) {
				changed = true;
				entry.setLabel(getLabelField().getText());	
			}
			if (fieldChanged(entry.getDescription(),getHelpField().getText())) {
				changed = true;
				entry.setDescription(getHelpField().getText());
			}
			if (entry.isDisableStandardCodes() != getDisableStandardCodes().isSelected()) {
				changed = true;
				entry.setDisableStandardCodes(getDisableStandardCodes().isSelected());
			}
			//Not used by the DEL
			entry.setExportSecurity(getExportSecurityBox().getSecurityValue());

			if (entry.isOptionCodesDisplayed() != getOptionCodesBox().isSelected()) {
				changed = true;
				entry.setOptionCodesDisplayed(getOptionCodesBox().isSelected());	
			}

			if (entry.isDropDownDisplay() != getOptionDropDownBox().isSelected()) {
				changed = true;
				entry.setDropDownDisplay(getOptionDropDownBox().isSelected());	
			}
			
			if (entry.numOptions() != optionList.getModel().getSize()) {
				changed = true;
			}
			
			if (entry.getDefaultValue() != defaultValue){
				changed = true;
				entry.setDefaultValue(defaultValue);
			}
			
			else {
				for (int i=0; i<optionList.getModel().getSize(); i++) {
					if (!((OptionEntry)entry).getOptions().contains((Option)optionList.getModel().getElementAt(i))) {
						changed = true;
						break;
					}
				}
			}

			for (int j=entry.numOptions(); j>0; j--) {
				entry.removeOption(0);
			}

			for (int i=0; i<optionList.getModel().getSize(); i++) {
				Option option = (Option)optionList.getModel().getElementAt(i);
				entry.addOption(option);
			}
		}

		if (!isCompositeEntry) {
			saveOptionDepencies();
		}

		saveUnits();
		saveTransformers();

		if (isCompositeEntry) {
			boolean found = false;
			for (int i=0; i<parentEntry.numEntries(); i++) {
				Entry curEntry = parentEntry.getEntry(i);
				if (curEntry.equals(entry)) {
					found = true;
				}
			}
			if (!found) {
				parentEntry.addEntry(entry);
			}
			if (changed) {
				((Document)DatasetController.getInstance().getActiveDocument()).setIsRevisionCandidate(true);
				((Entry)parentEntry).setIsRevisionCandidate(true);
				((Entry)entry).setIsRevisionCandidate(true);
			}
		}

		if (!isCompositeEntry) {
			if (!DocTreeModel.getInstance().updateEntry(entry, DatasetController.getInstance().getActiveDocument())) {
				DocTreeModel.getInstance().addEntry(entry, DatasetController.getInstance().getActiveDocument());
			}
		}
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
	}
	
	private class ModelChangedListener implements ListDataListener {

		public void contentsChanged(ListDataEvent arg0) {
			defaultComboBox.removeActionListener(itemChangedListener);
			
			if (!((DefaultListModel)optionList.getModel()).contains(defaultValue)) {
				defaultValue = null;
			}

			defaultComboBoxModel.removeAllElements();
			defaultComboBoxModel.addElement(null);
			
			for (int i=0; i<optionList.getModel().getSize(); i++) {
				defaultComboBoxModel.addElement(optionList.getModel().getElementAt(i));
			}
			
			defaultComboBoxModel.setSelectedItem(defaultValue);
			
			defaultComboBox.addActionListener(itemChangedListener);
			
		}

		public void intervalAdded(ListDataEvent arg0) {
			contentsChanged(arg0);
			
		}

		public void intervalRemoved(ListDataEvent arg0) {
			contentsChanged(arg0);
		}
		
	}

	private class ItemChangedListener implements ActionListener {

		public void actionPerformed(ActionEvent arg0) {
			if (defaultComboBox.getSelectedItem() == null) {
				defaultValue = null;
			} else {
				defaultValue = (Option)defaultComboBox.getSelectedItem();
			}
		}
	}
	
	public void dispatchEventToSubClass(ActionEvent aet){
		if (aet.getSource() == upButton) {
			if (optionList.getSelectedIndex() != -1) {
				if ((optionList.getSelectedIndex()-1) >= 0) {
					int selectedIndex = optionList.getSelectedIndex();
					swap(selectedIndex, selectedIndex-1);
					optionList.setSelectedIndex(selectedIndex-1);

					//If changes have been made then this item should be marked as being revised 
						((Entry)entry).setIsRevisionCandidate(true);
						((Document)DatasetController.getInstance().getActiveDocument()).setIsRevisionCandidate(true);

					//set the dataset to dirty
					DatasetController.getInstance().getActiveDs().setDirty(true);
				}
			}
		} else if (aet.getSource() == downButton) {
			if (optionList.getSelectedIndex() != -1) {
				if ((optionList.getSelectedIndex()+1)< optionList.getModel().getSize()) {
					int selectedIndex = optionList.getSelectedIndex();
					swap(selectedIndex, selectedIndex+1);
					optionList.setSelectedIndex(selectedIndex+1);

						//If in the DEL View and changes have been made then this item should be marked as being revised 
						((Entry)entry).setIsRevisionCandidate(true);
						((Document)DatasetController.getInstance().getActiveDocument()).setIsRevisionCandidate(true);

					//set the dataset to dirty
					DatasetController.getInstance().getActiveDs().setDirty(true);
				}
			}
		}
	}
	
}

