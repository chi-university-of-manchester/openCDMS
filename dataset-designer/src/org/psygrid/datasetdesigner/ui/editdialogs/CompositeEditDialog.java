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
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.psygrid.data.model.hibernate.CompositeEntry;
import org.psygrid.data.model.hibernate.Document;
import org.psygrid.data.model.hibernate.Entry;
import org.psygrid.data.model.hibernate.EntryStatus;
import org.psygrid.datasetdesigner.actions.AddEntryToCompositeAction;
import org.psygrid.datasetdesigner.actions.RemoveEntryFromCompositeAction;
import org.psygrid.datasetdesigner.renderer.OptionListCellRenderer;
import org.psygrid.datasetdesigner.ui.MainFrame;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;

/**
 * Configuration dialog for a composite entry
 * @author pwhelan
 */
public class CompositeEditDialog extends AbstractEditDialog {
	
	//the entry to configure
	private CompositeEntry entry;
	
	//the panel containing the basic entries comprising the composite
	private JList entryPanel;
		
	/**
	 * Constructor 
	 * @param frame the parent window
	 * @param entry the entry to configure
	 */
	public CompositeEditDialog(MainFrame frame, Entry entry, boolean isDEL, boolean canEdit) {
		super(frame, entry, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.configurecomposite"), true, false, isDEL, canEdit);
		this.entry = (CompositeEntry)entry;
	}
	
	/**
	 * Constructor 
	 * @param frame the parent window
	 * @param entry the entry to configure
	 * @param entryContext the parent document
	 */
	public CompositeEditDialog(JDialog parent, CompositeEntry entry, Document entryContext, boolean isDEL, boolean canEdit) {
		super(parent, entry, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.viewcomposite"), true, false, entryContext, isDEL, canEdit);
		this.entry = (CompositeEntry)entry;
	}

	/**
	 * Get the generic panel used in all edit dialogs 
	 * and add the composite panel to the bottom
	 * @return the configured panel
	 */
	public JPanel getGenericPanel() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(super.getGenericPanel(), BorderLayout.CENTER);
		mainPanel.add(buildCompositePanel(), BorderLayout.SOUTH);

		return mainPanel;
	}
	
	/**
	 * Populate the fields with the entry values
	 */
	public void populate() {
		super.populate();
		populateEntries();
	}
	
	/**
	 * Populate the child entries with the appropriate values
	 */
	public void populateEntries() {
		DefaultListModel entryModel = new DefaultListModel();
		CompositeEntry compEntry = ((CompositeEntry)getEntry());
		
		for (int i=0; i<compEntry.numEntries(); i++) {
			Entry basicEntry = compEntry.getEntry(i);
			entryModel.addElement(basicEntry);
		}

		entryPanel.setCellRenderer(new OptionListCellRenderer());
		entryPanel.setModel(entryModel);
		
	}

	/**
	 * Configures the composite panel containing child entries
	 * @return the composite panel with all entries
	 */
	public JPanel  buildCompositePanel() {
		JPanel compPanel = new JPanel();
		compPanel.setLayout(new BorderLayout());
		compPanel.setBorder(BorderFactory.createTitledBorder(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.basicentries")));
		
		entryPanel = new JList();
		JScrollPane entryScroll = new JScrollPane(entryPanel);
		compPanel.add(entryScroll, BorderLayout.CENTER);
		
		if(!viewOnly){
			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
			
			JComboBox entryBox = new JComboBox();
			DefaultComboBoxModel boxModel = new DefaultComboBoxModel();
			boxModel.addElement(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.textentry"));
			boxModel.addElement(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.numericentry"));
			boxModel.addElement(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.optionentry"));
			boxModel.addElement(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.booleanentry"));
			boxModel.addElement(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.dateentry"));
			boxModel.addElement(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.fixedlabels"));
			entryBox.setModel(boxModel);	
			
			EntriesChangedListener entriesChangedListener = new EntriesChangedListener();
			
	
			AddEntryToCompositeAction action = new AddEntryToCompositeAction((MainFrame)getParent(), this, entryBox, ((CompositeEntry)getEntry()), entryPanel);
			action.addActionListener(entriesChangedListener);
			JButton addEntryButton = new JButton(action);
			
			AddEntryToCompositeAction editAction = new AddEntryToCompositeAction((MainFrame)getParent(), this, 
																			     entryPanel,
																			     entryBox,
																			     ((CompositeEntry)getEntry()),
																			     true);
			final JButton editButton = new JButton(editAction);
	
			RemoveEntryFromCompositeAction removeAction = new RemoveEntryFromCompositeAction(this, ((CompositeEntry)getEntry()));
			removeAction.addActionListener(entriesChangedListener);
			final JButton removeEntryButton = new JButton(removeAction);
			buttonPanel.add(entryBox);
			buttonPanel.add(addEntryButton);
			buttonPanel.add(editButton);
			editButton.setEnabled(false);
			buttonPanel.add(removeEntryButton);
			removeEntryButton.setEnabled(false);

	        //Only enable the buttons if an item is selected
			entryPanel.addListSelectionListener(new ListSelectionListener() {
	               	public void valueChanged(ListSelectionEvent event) {
	                    
	               		//initialise to false
	               		boolean enabled = true;

	                    if ((entryPanel == null) || (entryPanel.getSelectedValue() == null)) {
	                        enabled = false;
	                    }

	                    if (removeEntryButton != null) {
	                        removeEntryButton.setEnabled(enabled);
	                    }

	                    if (editButton != null) {
	                        editButton.setEnabled(enabled);
	                    }
	                }
	            });

			
			
			compPanel.add(buttonPanel, BorderLayout.NORTH);
		}
		
		return compPanel;
	}
	
	/**
	 * Validate that at least one child entry is set
	 * @return true if child entry is set; false if not
	 */
	public boolean validateEntries() {
		
		if (entryPanel.getModel().getSize() == 0) {
			JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.atleastoneentry"));
			return false;
		}
		
		return true;
	}

	/**
	 * Fired when ok button is pressed
	 * Set the appropriate values
	 */
	public void ok() {
		if (validateEntries()) {
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
			
			saveOptionDepencies();
			
		}
	}
	
	/**
	 * Get the selected entry in the list
	 * @return the entry selected in the list
	 */
	public Entry getSelectedEntry() {
		return (Entry)entryPanel.getSelectedValue();
	}
	
	/**
	 * Listen for changes to the child entries 
	 */
	private class EntriesChangedListener implements ActionListener {
		
		/**
		 * When entries are changes, you cannot cancel the dialog
		 * @param aet the calling action event
		 */
		public void actionPerformed(ActionEvent aet) {
			getCancelButton().setEnabled(false);
			populateEntries();
		}
	}
	
	
	

}