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
package org.psygrid.datasetdesigner.ui.wizard.panels;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.psygrid.datasetdesigner.model.DocTreeModel;
import org.psygrid.datasetdesigner.renderer.OptionListCellRenderer;
import org.psygrid.datasetdesigner.utils.IconsHelper;
import org.psygrid.datasetdesigner.utils.ListModelUtility;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;

import org.psygrid.datasetdesigner.ui.wizard.WizardPanel;
import org.psygrid.datasetdesigner.ui.wizard.WizardModel;

import org.psygrid.datasetdesigner.actions.AddDocumentToWizardAction;
import org.psygrid.datasetdesigner.actions.RemoveDocumentFromWizardAction;
import org.psygrid.datasetdesigner.custom.CustomCopyPasteJList;

import org.psygrid.datasetdesigner.utils.HelpHelper;

/**
 * Wizard panel allowing for adding/removing and changing of study procedures
 * (aka Documents)
 * @author pwhelan
 */
public class DocumentConfigurationPanel extends JPanel implements WizardPanel, 
															ActionListener {

	//the main model of the wizard
	private WizardModel wm;
	
	//the main wizard dialog
	private final JDialog parentDialog;
	
	//up and down buttons for reordering documents
	private JButton upButton;
	private JButton downButton;
	
	//list of documents that are displayed and can be manipulated within this page
	private JList documentList;
	
	//doc edit/remove buttons
	private JButton editDocGroupButton;
	private JButton removeDocGroupButton;
	
	
	/**
	 * Constructor
	 * @param parentDialog the main dialog of the wizard
	 * @param wm the model of the wizard
	 */
	public DocumentConfigurationPanel(JDialog parentDialog, WizardModel wm) {
		super();
		this.parentDialog = parentDialog;
		this.wm = wm;
		setLayout(new BorderLayout());
		add(buildNorthPanel(), BorderLayout.NORTH);
		add(buildDocumentConfigurationListPanel(), BorderLayout.CENTER);
	}

	/**
	 * Configure the header panel
	 * @return the newly configured header panel
	 */
	private JPanel buildNorthPanel() {
		JPanel northPanel = new JPanel();
		northPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		northPanel.add(HelpHelper.getInstance().getHelpButtonWithID("dsdwizardstudyprocedures"));
		northPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.wizard.documentnames")));
		return northPanel;
	}
	
	/**
	 * Configure the main list of documents in the panel
	 * @return the configured document panel
	 */
	private JPanel buildDocumentConfigurationListPanel() {
		JPanel documentGroupPanel = new JPanel();
		documentGroupPanel.setLayout(new BorderLayout());
		documentGroupPanel.setBorder(BorderFactory.createTitledBorder(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.managestudystages")));
		documentList = new CustomCopyPasteJList();
		documentList.addListSelectionListener(new SelectionListener());
		documentList.setCellRenderer(new OptionListCellRenderer());
		documentList.setModel(new DefaultListModel());
		
		JPanel configuredPanel = new JPanel();
		configuredPanel.setLayout(new BorderLayout());
		
		JPanel headerPanel = new JPanel();
		headerPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		headerPanel.add(HelpHelper.getInstance().getHelpButtonWithID("dsdwizardreorderstudyprocedures"));
		headerPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.reorderdocuments")), BorderLayout.NORTH);
		upButton = new JButton(IconsHelper.getInstance().getImageIcon("1uparrow.png"));
		downButton = new JButton(IconsHelper.getInstance().getImageIcon("1downarrow.png"));
		upButton.addActionListener(this);
		downButton.addActionListener(this);
		headerPanel.add(upButton);
		headerPanel.add(downButton);
		
		documentGroupPanel.add(buildAddRemovePanel(), BorderLayout.NORTH);
		configuredPanel.add(new JScrollPane(documentList), BorderLayout.CENTER);
		documentGroupPanel.add(headerPanel, BorderLayout.SOUTH);
		documentGroupPanel.add(configuredPanel, BorderLayout.CENTER);
		
		return documentGroupPanel;
	}
	
	/**
	 * Called when the 'next' button is hit
	 * Save the documents for the dataset
	 */
	public boolean next() {
		wm.getWizardDs().setDocuments(
				ListModelUtility.convertListModelToDocumentList(((DefaultListModel)documentList.getModel())));
		return true;
	}
	
	/**
	 * Panel containing the add/remove/edit buttons 
	 * @return the configured add/remove/edit button panel
	 */
	private JPanel buildAddRemovePanel() {
		JPanel addRemovePanel = new JPanel();
		addRemovePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		
		JButton addDocGroupButton = new JButton(new AddDocumentToWizardAction(parentDialog, documentList, wm));
		editDocGroupButton = new JButton(new AddDocumentToWizardAction(parentDialog, documentList, wm, true));
		editDocGroupButton.setEnabled(false);
		removeDocGroupButton = new JButton(new RemoveDocumentFromWizardAction(documentList));
		removeDocGroupButton.setEnabled(false);
		
		addRemovePanel.add(addDocGroupButton);
		addRemovePanel.add(editDocGroupButton);
		addRemovePanel.add(removeDocGroupButton);
		
		return addRemovePanel;
	}
	
	/**
	 * Method required by the interface
	 */
	public void refreshPanel() {
	}
	
	/**
	 * Swap the position of the items in the list
	 * @param a the position of the first item in the list
	 * @param b the position of the second item in the list 
	 */
	private void swap(int a, int b) {
	       Object aObject = documentList.getModel().getElementAt(a);
	       Object bObject = documentList.getModel().getElementAt(b);
	       ((DefaultListModel)documentList.getModel()).set(a, bObject);
	       ((DefaultListModel)documentList.getModel()).set(b, aObject);
	}
	
	/**
	 * Action event occurred
	 * If up or down button pressed, switch the order of items in the list
	 */
	public void actionPerformed(ActionEvent aet) {
		if (aet.getSource() == upButton) {
			if (documentList.getSelectedIndex() != -1) {
				if ((documentList.getSelectedIndex()-1) >= 0) {
					int selectedIndex = documentList.getSelectedIndex();
					swap(selectedIndex, selectedIndex-1);
					documentList.setSelectedIndex(selectedIndex-1);
				}
			}
		} else if (aet.getSource() == downButton) {
			if (documentList.getSelectedIndex() != -1) {
				if ((documentList.getSelectedIndex()+1)< documentList.getModel().getSize()) {
					int selectedIndex = documentList.getSelectedIndex();
					swap(selectedIndex, selectedIndex+1);
					documentList.setSelectedIndex(selectedIndex+1);
				}
			}
		} 
	}
	
	/**
	 * Class listener for selections in the list
	 * Enables/disables edit and remove buttons based on this
	 * @author pwhelan
	 */
	private class SelectionListener implements ListSelectionListener {

		/**
		 * Disable buttons if no selection or 
		 * enable if there is one
		 * @param ListSelectionEvent the trigger event
		 */
		public void valueChanged(ListSelectionEvent e) {
			if (documentList.getSelectedIndex() == -1) {
				editDocGroupButton.setEnabled(false);
				removeDocGroupButton.setEnabled(false);
			} else {
				editDocGroupButton.setEnabled(true);
				removeDocGroupButton.setEnabled(true);
			}
		}
	}
	
}
