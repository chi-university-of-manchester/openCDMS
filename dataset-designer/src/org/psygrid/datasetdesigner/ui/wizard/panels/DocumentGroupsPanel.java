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

import org.psygrid.datasetdesigner.actions.AddDocumentGroupToWizardAction;
import org.psygrid.datasetdesigner.actions.RemoveDocumentGroupFromWizardAction;
import org.psygrid.datasetdesigner.custom.CustomCopyPasteJList;

import org.psygrid.datasetdesigner.renderer.OptionListCellRenderer;

import org.psygrid.datasetdesigner.utils.IconsHelper;
import org.psygrid.datasetdesigner.utils.ListModelUtility;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;

import org.psygrid.datasetdesigner.ui.wizard.WizardPanel;
import org.psygrid.datasetdesigner.ui.wizard.WizardModel;

import org.psygrid.datasetdesigner.model.DocTreeModel;

import org.psygrid.datasetdesigner.utils.HelpHelper;


/**
 * Wizard panel allowing for adding/removing and changing of study
 * states (aka Document Groups)
 * @author pwhelan
 */
public class DocumentGroupsPanel extends JPanel implements WizardPanel, 
												ActionListener 
												 {
	
	//a list of document groups to be manipulated
	private JList documentGroupList;
	
	//up and down buttons for reordering document groups
	private JButton upButton;
	private JButton downButton;
	
	//the main model of the wizard
	private WizardModel wm;
	
	//the main wizard dialog
	private JDialog parentDialog;
	
	private JButton editDocGroupButton;
	private JButton removeDocGroupButton;
	
	/**
	 * Constructor - creates the panel and lays out its constituents
	 * @param parentDialog the main wizard dialog
	 * @param wm the wizard model
	 */
	public DocumentGroupsPanel(JDialog parentDialog, WizardModel wm) {
		super();
		this.wm = wm;
		this.parentDialog = parentDialog;
		
		setLayout(new BorderLayout());
		add(buildNorthPanel(), BorderLayout.NORTH);
		add(buildDocumentGroupListPanel(), BorderLayout.CENTER);
	}

	/**
	 * Build the header panel
	 * @return the configured header panel
	 */
	private JPanel buildNorthPanel() {
		JPanel northPanel = new JPanel();
		northPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		northPanel.add(HelpHelper.getInstance().getHelpButtonWithID("dsdwizardstudystages"));
		northPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.wizard.documentgroups")));
		return northPanel;
	}
	
	/**
	 * Build the main panel containing a list of document groups
	 * in the main body and then a reorder section in the bottom 
	 * and a add/remove/edit buttons on top
	 * @return the configured list of documents groups panel with reorder buttons
	 */
	private JPanel buildDocumentGroupListPanel() {
		JPanel documentGroupPanel = new JPanel();
		documentGroupPanel.setLayout(new BorderLayout());
		documentGroupPanel.setBorder(BorderFactory.createTitledBorder(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.managestudystages")));
		documentGroupList = new CustomCopyPasteJList();
		documentGroupList.addListSelectionListener(new SelectionListener());
		documentGroupList.setCellRenderer(new OptionListCellRenderer());
		documentGroupList.setModel(new DefaultListModel());
		
		JPanel configuredPanel = new JPanel();
		configuredPanel.setLayout(new BorderLayout());
		
		JPanel headerPanel = new JPanel();
		headerPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		headerPanel.add(HelpHelper.getInstance().getHelpButtonWithID("dsdwizardreorderstudystages"));
		headerPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.reorderdocumentgroups")), BorderLayout.NORTH);
		upButton = new JButton(IconsHelper.getInstance().getImageIcon("1uparrow.png"));
		downButton = new JButton(IconsHelper.getInstance().getImageIcon("1downarrow.png"));
		upButton.addActionListener(this);
		downButton.addActionListener(this);
		headerPanel.add(upButton);
		headerPanel.add(downButton);
		
		configuredPanel.add(new JScrollPane(documentGroupList), BorderLayout.CENTER);
		documentGroupPanel.add(buildAddRemovePanel(), BorderLayout.NORTH);
		documentGroupPanel.add(configuredPanel, BorderLayout.CENTER);
		configuredPanel.add(headerPanel, BorderLayout.SOUTH);

		return documentGroupPanel;
	}
	
	/**
	 * Create a panel with add/remove/edit buttons
	 * @return the configured panel with add/remove/edit buttons
	 */
	private JPanel buildAddRemovePanel() {
		JPanel addRemovePanel = new JPanel();
		addRemovePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		
		JButton addDocGroupButton = new JButton(new AddDocumentGroupToWizardAction(parentDialog, documentGroupList));
		editDocGroupButton = new JButton(new AddDocumentGroupToWizardAction(parentDialog, documentGroupList, true));
		editDocGroupButton.setEnabled(false);
		
		removeDocGroupButton = new JButton(new RemoveDocumentGroupFromWizardAction(documentGroupList));
		removeDocGroupButton.setEnabled(false);
		
		addRemovePanel.add(addDocGroupButton);
		addRemovePanel.add(editDocGroupButton);
		addRemovePanel.add(removeDocGroupButton);
		
		return addRemovePanel;
	}
	
	/**
	 * Called when the 'next' button is hit on the wizard
	 * @return boolean always true
	 */
	public boolean next() {
		wm.getWizardDs().setDocumentGroups(ListModelUtility.convertListModelToDocGroupList(((DefaultListModel)documentGroupList.getModel())));
		return true;
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
	       Object aObject = documentGroupList.getModel().getElementAt(a);
	       Object bObject = documentGroupList.getModel().getElementAt(b);
	       ((DefaultListModel)documentGroupList.getModel()).set(a, bObject);
	       ((DefaultListModel)documentGroupList.getModel()).set(b, aObject);
	}
	
	/**
	 * Action event occurred
	 * If up or down button pressed, switch the order of items in the list
	 */
	public void actionPerformed(ActionEvent aet) {
		if (aet.getSource() == upButton) {
			if (documentGroupList.getSelectedIndex() != -1) {
				if ((documentGroupList.getSelectedIndex()-1) >= 0) {
					int selectedIndex = documentGroupList.getSelectedIndex();
					swap(selectedIndex, selectedIndex-1);
					documentGroupList.setSelectedIndex(selectedIndex-1);
				}
			}
		} else if (aet.getSource() == downButton) {
			if (documentGroupList.getSelectedIndex() != -1) {
				if ((documentGroupList.getSelectedIndex()+1)< documentGroupList.getModel().getSize()) {
					int selectedIndex = documentGroupList.getSelectedIndex();
					swap(selectedIndex, selectedIndex+1);
					documentGroupList.setSelectedIndex(selectedIndex+1);
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
			if (documentGroupList.getSelectedIndex() == -1) {
				editDocGroupButton.setEnabled(false);
				removeDocGroupButton.setEnabled(false);
			} else {
				editDocGroupButton.setEnabled(true);
				removeDocGroupButton.setEnabled(true);
			}
		}
	}
	
}


