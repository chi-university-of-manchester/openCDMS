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
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.psygrid.data.model.hibernate.DerivedEntry;
import org.psygrid.data.model.hibernate.Section;
import org.psygrid.datasetdesigner.renderer.*;

import org.psygrid.data.model.hibernate.Entry;
import org.psygrid.data.model.hibernate.Document;

import org.psygrid.datasetdesigner.actions.RemoveSectionAction;
import org.psygrid.datasetdesigner.actions.AddSectionAction;
import org.psygrid.datasetdesigner.actions.EditSectionAction;
import org.psygrid.datasetdesigner.custom.CustomCopyPasteJList;

import org.psygrid.datasetdesigner.model.DocTreeModel;

import org.psygrid.datasetdesigner.ui.DocumentPanel;
import org.psygrid.datasetdesigner.controllers.DatasetController;

import org.psygrid.datasetdesigner.utils.ElementUtility;
import org.psygrid.datasetdesigner.utils.Utils;
import org.psygrid.datasetdesigner.utils.IconsHelper;
import org.psygrid.datasetdesigner.utils.ListModelUtility;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;

/**
 * A dialog to show the existing sections of a dialog
 * and allow manipulation and additions of sections
 * @author pwhelan
 */
public class SectionControlDialog extends JDialog implements ActionListener {

	private JButton okButton;
	private JButton cancelButton;
	
	private JList sectionList; 
	
	private Document doc;
	private DocumentPanel docPanel;
	
	private JButton addButton;
	private JButton editButton;
	private JButton removeButton;
	
	private JFrame frame;
	
    /**
     * Move an item up the list
     */
    protected JButton upButton;

    /**
     * Move an item down the list
     */
    protected JButton downButton;

	
	public SectionControlDialog(JFrame frame, Document doc, DocumentPanel docPanel) {
		super(frame);
		this.frame = frame;
		setTitle(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.sectioncontrol"));
		this.doc = doc;
		this.docPanel = docPanel;
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(buildMainPanel(), BorderLayout.CENTER);
		getContentPane().add(buildButtonPanel(), BorderLayout.SOUTH);
		setModal(true);
		init();
		pack();
		setLocationRelativeTo(null);  
	}
	
	private void init() {
		DefaultListModel sectionModel = new DefaultListModel();
		for (int i=0; i<doc.numSections(); i++) {
			sectionModel.addElement(doc.getSection(i));
		}
		sectionList.setModel(sectionModel);
		
		//Only enable the buttons if a section is selected
		sectionList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				boolean enabled = true;
				if (sectionList == null || sectionList.getSelectedValue() == null
						|| DatasetController.getInstance().getActiveDs().getDs().isPublished()) {
					enabled = false;
				}
				if (removeButton != null) { removeButton.setEnabled(enabled); }
				if (editButton   != null) { editButton.setEnabled(enabled);   }
			}
		});

		//cannot reorder in patching mode
		if (DatasetController.getInstance().getActiveDs().getDs().isPublished()) {
			upButton.setEnabled(false);
			downButton.setEnabled(false);
		}
		
	}
	
	private JPanel buildMainPanel() {
		JPanel mainPanel = new JPanel();
		mainPanel.setBorder(BorderFactory.createTitledBorder(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.sections")));
		mainPanel.setLayout(new BorderLayout());
		sectionList = new CustomCopyPasteJList();
		sectionList.setCellRenderer(new OptionListCellRenderer());
		JScrollPane sectionScrollPane = new JScrollPane(sectionList);
		sectionScrollPane.setMinimumSize(new Dimension(300, 300));
		sectionScrollPane.setPreferredSize(new Dimension(300, 300));
		sectionScrollPane.setMaximumSize(new Dimension(300, 300));
		
		JPanel addRemovePanel = new JPanel();
		addRemovePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		addButton = new JButton(new AddSectionAction(frame, doc, docPanel, sectionList));
		editButton = new JButton(new EditSectionAction(frame, doc, docPanel, sectionList));
		editButton.setEnabled(false);
		removeButton = new JButton(new RemoveSectionAction(sectionList));
		removeButton.setEnabled(false);
		addRemovePanel.add(addButton);
		addRemovePanel.add(editButton);
		addRemovePanel.add(removeButton);
		
		mainPanel.add(addRemovePanel, BorderLayout.NORTH);
		mainPanel.add(sectionScrollPane, BorderLayout.CENTER);
		mainPanel.add(buildReorderPanel(), BorderLayout.SOUTH);
		return mainPanel;
	}
	
	private JPanel buildButtonPanel() {
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		okButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ok"));
		okButton.addActionListener(this);
		cancelButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.cancel"));
		cancelButton.addActionListener(this);
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		return buttonPanel;
	}
	
	/**
     * Build a panel that allows reordering of the items
     * in the list
     * @return the configured panel containing buttons to move 
     * items in the list up and down
     */
    private JPanel buildReorderPanel() {
    	JPanel reorderPanel = new JPanel();
		reorderPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		reorderPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.reorder")), BorderLayout.NORTH);
		upButton = new JButton(IconsHelper.getInstance().getImageIcon("1uparrow.png"));
		downButton = new JButton(IconsHelper.getInstance().getImageIcon("1downarrow.png"));
		upButton.addActionListener(this);
		downButton.addActionListener(this);
		reorderPanel.add(upButton);
		reorderPanel.add(downButton);
    	return reorderPanel;
    }
	
    /**
	 * Swap the position of the items in the list
	 * @param a the position of the first item in the list
	 * @param b the position of the second item in the list 
	 */
	private void swap(int a, int b) {
		Object aObject = sectionList.getModel().getElementAt(a);
	    Object bObject = sectionList.getModel().getElementAt(b);
	    ((DefaultListModel)sectionList.getModel()).set(a, bObject);
	    ((DefaultListModel)sectionList.getModel()).set(b, aObject);
	}
	
	private Document testReorderdDoc() {
		Document reorderedDoc = ElementUtility.createIDummyDocument("reodered test");

		((Document)reorderedDoc).setSections(ListModelUtility.convertListModelToSectionList((DefaultListModel)sectionList.getModel()));
		
		//in case sections have been reordered; update the indexing within the document here
		ArrayList<Entry> docEntries = new ArrayList<Entry>();
		for (int i=0; i<reorderedDoc.numSections(); i++) {
			Section curSection = reorderedDoc.getSection(i);
			for (int j=0; j<doc.numEntries(); j++) {
				if (doc.getEntry(j).getSection().equals(curSection)){
					docEntries.add((Entry)doc.getEntry(j));
				}
			}
		}
		
		((Document)reorderedDoc).setEntries(docEntries);
		
		return reorderedDoc;
	}
	
	/**
	 * Return false if a derived entry will be compromised by this reordering
	 * Can happen in the case where:
	 * - a derived entry is moved backward beyond its constituent variable entries
	 * - a derived entry's constituent entries are moved forward beyond their 
	 * parent derived entry
	 * 
	 * @return true if validation succeeds; false if not 
	 */
	private boolean validateMove(int sectionSourceIndex, int sectionDestinationIndex) {
		boolean canMove = true;
		
		ArrayList<DerivedEntry> derEntries = new ArrayList<DerivedEntry>();
		
		Document reorderedDoc = testReorderdDoc();
		
		
		for (int i=0; i<doc.numEntries(); i++) {
			if (doc.getEntry(i) instanceof DerivedEntry) {
				derEntries.add((DerivedEntry)doc.getEntry(i));
			}
		}
		
		for (DerivedEntry derEntry: derEntries) {
			Iterator it = derEntry.getVariableNames().iterator();
			while (it.hasNext()) {
				Entry entry = derEntry.getVariable((String) it.next());
				
				int derivedEntrySectionIndex = Utils.getSectionForEntry(reorderedDoc, derEntry);
				int variableEntrySectionIndex = Utils.getSectionForEntry(reorderedDoc, entry);
				
				//if the variable entry is >= than the destination section index
				//then you cannot move this section;
				//derived entry variables must always precede the derived entry itself
				if (derivedEntrySectionIndex != variableEntrySectionIndex) 
					//if the variable section index is > than the destination index	
					if (( (variableEntrySectionIndex >= sectionDestinationIndex)
							&& (sectionDestinationIndex >= derivedEntrySectionIndex))
						//if the variable entry section index is the source 
						//and the destination is greater than the derived
						|| (variableEntrySectionIndex == sectionSourceIndex
									&& sectionDestinationIndex >= derivedEntrySectionIndex)
						//or the derived is the source and
						//the destination is greater than the variable
						|| ((derivedEntrySectionIndex == sectionSourceIndex) 
							&& sectionDestinationIndex <= variableEntrySectionIndex))
						{
							JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.cannotreordersection"));
							return false;
				}
			}
		}
		
		return canMove;
	}
    
    
	public void actionPerformed(ActionEvent aet) {
		if (aet.getSource() == okButton) {
			int currentSection = docPanel.getCurrentSection();
						
			//clean up the old entries that belonged to any removed sections
			ArrayList<Section> newSections = ListModelUtility.convertListModelToISectionList((DefaultListModel)sectionList.getModel());
			for (int i=0; i<doc.numSections(); i++) {
				if (!newSections.contains(doc.getSection(i))) {
					((Document)doc).setIsRevisionCandidate(true);	//Mark as revised because section was removed
					for  (int j=doc.numEntries()-1; j>=0; j--) {
						if (doc.getEntry(j).getSection().equals(doc.getSection(i))) {
							DocTreeModel.getInstance().deleteEntry(doc.getEntry(j), doc);
							doc.removeEntry(j);
						}
					}
				}
			}
			
			((Document)doc).setSections(ListModelUtility.convertListModelToSectionList((DefaultListModel)sectionList.getModel()));
			
			//in case sections have been reordered; update the indexing within the document here
			ArrayList<Entry> docEntries = new ArrayList<Entry>();
			for (int i=0; i<doc.numSections(); i++) {
				Section curSection = doc.getSection(i);
				for (int j=0; j<doc.numEntries(); j++) {
					if (doc.getEntry(j).getSection().equals(curSection)){
						docEntries.add((Entry)doc.getEntry(j));
					}
				}
			}

			((Document)doc).setEntries(docEntries);
			
			
			//refresh doc panel and doc tree
			docPanel.refresh(currentSection);
			DocTreeModel.getInstance().refreshDocument(doc);
			
			this.dispose();
		} else if (aet.getSource() == cancelButton) {
			this.dispose();
	 } else if (aet.getSource() == upButton) {
		 int selectedIndex = sectionList.getSelectedIndex();
		 if (validateMove(selectedIndex, selectedIndex-1)) {
				if (sectionList.getSelectedIndex() != -1) {
					if ((sectionList.getSelectedIndex()-1) >= 0) {
						swap(selectedIndex, selectedIndex-1);
						sectionList.setSelectedIndex(selectedIndex-1);
					}
				}
		 }
        } else if (aet.getSource() == downButton) {
			int selectedIndex = sectionList.getSelectedIndex();
        	if (validateMove(selectedIndex, selectedIndex+1)) {
            	if (sectionList.getSelectedIndex() != -1) {
    				if ((sectionList.getSelectedIndex()+1)< sectionList.getModel().getSize()) {
    					swap(selectedIndex, selectedIndex+1);
    					sectionList.setSelectedIndex(selectedIndex+1);
    				}
    			}
        	}
        }
	}
}