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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.psygrid.data.model.hibernate.BasicEntry;
import org.psygrid.data.model.hibernate.Document;
import org.psygrid.data.model.hibernate.Entry;
import org.psygrid.data.model.hibernate.HibernateFactory;
import org.psygrid.data.model.hibernate.Transformer;
import org.psygrid.datasetdesigner.actions.AssignUnitAction;
import org.psygrid.datasetdesigner.actions.ConfigureTransformersAction;
import org.psygrid.datasetdesigner.actions.UnassignUnitAction;
import org.psygrid.datasetdesigner.controllers.DatasetController;
import org.psygrid.datasetdesigner.custom.CustomCopyPasteJList;
import org.psygrid.datasetdesigner.model.DummyDocument;
import org.psygrid.datasetdesigner.model.StudyDataSet;
import org.psygrid.datasetdesigner.renderer.OptionListCellRenderer;
import org.psygrid.datasetdesigner.utils.HelpHelper;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.utils.Utils;

/**
 * Transformers Panel - reused through to allow adding/removing of transformers to 
 * an entry
 * @author pwhelan
 *
 */
public class TransformersPanel extends JPanel implements ActionListener {

	/**
	 * All the transformers in the list
	 */
	private JList allTransformersList;

	/**
	 * All the transformers in the dependent list
	 */
	private JList dependentList;

	/**
	 * Assign transformers to the entry
	 */
	private JButton assignButton;
	
	/**
	 * Remove transformers to the entry
	 */
	private JButton unassignButton;

	/**
	 * The document to which the entry belongs
	 */
	private Document document;

	/**
	 * The entry to which transformers will be assigned
	 */
	private BasicEntry entry;

	
	/**
	 * The Hibernate Factory
	 */
	private HibernateFactory factory;

	/**
	 * Constructor
	 * @param parentFrame the owner the dialog
	 * @param activeDocument the document this entry belongs to
	 * @param entry the current entry
	 * @param viewOnly true if in DEL mode, false if not
	 */
	public TransformersPanel(MainFrame parentFrame, Document activeDocument, BasicEntry entry, boolean viewOnly, boolean isDEL) {
		this.entry = entry;
		this.document = activeDocument;
		
		allTransformersList = new CustomCopyPasteJList();
		allTransformersList.setModel(new DefaultListModel());
		allTransformersList.setCellRenderer(new OptionListCellRenderer());
		
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.X_AXIS));
		setLayout(new BorderLayout());

		dependentList = new CustomCopyPasteJList();
		dependentList.setModel(new DefaultListModel());
		dependentList.setCellRenderer(new OptionListCellRenderer());

		assignButton = new CustomIconButton(new AssignUnitAction(allTransformersList, dependentList), "Assign Transformer");
		unassignButton = new CustomIconButton(new UnassignUnitAction(allTransformersList, dependentList), "Unassign Transformer");

		if(!viewOnly){
			centerPanel.add(createSubPanel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.alltransformers"), allTransformersList));
			centerPanel.add(createArrowPanel(unassignButton, assignButton));
		}

		centerPanel.add(createSubPanel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.assignedtransformers"), dependentList));

		if(viewOnly) {
			setBorder(BorderFactory.createTitledBorder(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.viewtransformers")));
		}
		else {
			setBorder(BorderFactory.createTitledBorder(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.configuretransformers")));
		}

		add(centerPanel, BorderLayout.CENTER);
		
		JPanel topButtonPanel = new JPanel();
		topButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

		if (parentFrame != null) {
			topButtonPanel.add(HelpHelper.getInstance().getHelpButtonWithID("dsdmanagetransformers"));
			if (viewOnly) {
				topButtonPanel.add(new JButton(new ConfigureTransformersAction(parentFrame, viewOnly)));
			}
			else {
				topButtonPanel.add(new JButton(new ConfigureTransformersAction(parentFrame)));	
			}
		}

		add(topButtonPanel, BorderLayout.NORTH);

        //add a listener to repopulate this panel if units are changed from manage units dialog
        StudyDataSet activeDs = DatasetController.getInstance().getActiveDs();

        if (activeDs != null) {
            activeDs.addActionListener(this);
        }

		populate();
	}

	/**
	 * Fill the lists with the current values for study and entry transformers 
	 */
	public void populate() {
		//clear both models before populating!
		((DefaultListModel)allTransformersList.getModel()).clear();
		((DefaultListModel)dependentList.getModel()).clear();

		//first populate all list
		if (document != null) {		//document can be null when viewing entry via DEL search dialog
			int numTransformers = document.getDataSet().numTransformers();
			nextTransformer: for (int i=0; i<numTransformers; i++) {

				for (Transformer t: ((BasicEntry)entry).getTransformers()) {
					if (t.getWsNamespace().equals(document.getDataSet().getTransformer(i).getWsNamespace())
							&& t.getWsOperation().equals(document.getDataSet().getTransformer(i).getWsOperation())
							&& t.getWsUrl().equals(document.getDataSet().getTransformer(i).getWsUrl())
							&& t.getResultClass().equals(document.getDataSet().getTransformer(i).getResultClass())) {
						//Don't add the transformer if the entry already uses it.		
						continue nextTransformer;
					}
				}

				if (!((BasicEntry)entry).getTransformers().contains(document.getDataSet().getTransformer(i))) {
					((DefaultListModel)allTransformersList.getModel()).addElement(document.getDataSet().getTransformer(i));
				}
			}

			int numEntryTransformers = entry.numTransformers();

			for (int j=0; j<numEntryTransformers; j++) {
				//add to right list
				((DefaultListModel)dependentList.getModel()).addElement(entry.getTransformer(j));
			}
		}

	}

	/**
	 * Save the currently selected transformers for this entry
	 */
	public void saveTransformers() {
		List<Transformer> oldTransformers = new ArrayList<Transformer>();
		oldTransformers.addAll(((BasicEntry)entry).getTransformers());

		if (oldTransformers.size() != dependentList.getModel().getSize()) {
			((BasicEntry)entry).setIsRevisionCandidate(true);
			((Document)DatasetController.getInstance().getActiveDocument()).setIsRevisionCandidate(true);
			
			//Required for composites..
			if (DatasetController.getInstance().getActiveDocument() instanceof DummyDocument) {
				Entry singleEntry = Utils.getMainEntry((DummyDocument)DatasetController.getInstance().getActiveDocument());
				singleEntry.setIsRevisionCandidate(true);
			}
		}
		//first remove all existing units from the entry
		for (int j=entry.numTransformers(); j>0; j--){
			entry.removeTransformer(0);
		}	

		for (int i=0; i<dependentList.getModel().getSize(); i++) {
			if (!oldTransformers.contains(dependentList.getModel().getElementAt(i))) {
				((BasicEntry)entry).setIsRevisionCandidate(true);
				((Document)DatasetController.getInstance().getActiveDocument()).setIsRevisionCandidate(true);

				//Required for composites..
				if (DatasetController.getInstance().getActiveDocument() instanceof DummyDocument) {
					Entry singleEntry = Utils.getMainEntry((DummyDocument)DatasetController.getInstance().getActiveDocument());
					singleEntry.setIsRevisionCandidate(true);
				}
			}
			entry.addTransformer((Transformer)dependentList.getModel().getElementAt(i));
		}
	}

	/**
	 * Return the current hibernate factory being used
	 * @return The <code>HibernateFactory</code>
	 */
	public HibernateFactory getFactory() {
		if (factory == null) {
			factory = new HibernateFactory();
		}
		return factory;
	}


	/**
	 * Create the sub panel
	 * @param labelString
	 * @param button
	 * @param list
	 * @return
	 */
	public JComponent createSubPanel(String labelString, JComponent list)
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
	public JPanel createLabelPanel(String labelString)
	{
		JPanel labelPanel = new JPanel();
		labelPanel.setLayout(new BorderLayout());
		labelPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
		labelPanel.add(new JLabel(labelString), BorderLayout.WEST);
		return labelPanel;
	}

	/**
	 * When action is fired repopulate the lists
	 * @param e the calling ActionEvent
	 */
	public void actionPerformed(ActionEvent e) {
		//calling a populate is too restrictive - it will lose all existing assignments

		//first populate all list
		if (document != null) {		//document can be null when viewing entry via DEL search dialog
			int numTransformers = document.getDataSet().numTransformers();

			DefaultListModel allModel = (DefaultListModel)allTransformersList.getModel();
			DefaultListModel assignedModel = (DefaultListModel)dependentList.getModel();
			
			//do one pass through and add to appropriate lists
			for (int i=0; i<numTransformers; i++) {
				Transformer curTrans = document.getDataSet().getTransformer(i);
				
				if (!(assignedModel.contains(curTrans))) {
					if (!(allModel.contains(curTrans))) {
						allModel.addElement(curTrans);
					}
				}
			}
			
			//check individual lists for units that have been removed from the dataset
			DefaultListModel currentUnits = new DefaultListModel();
			
			for (int i=0; i<numTransformers; i++) {
				currentUnits.addElement(document.getDataSet().getTransformer(i));
			}
			
			//check for redundant refs in all model
			for (int j=allModel.getSize()-1; j>=0; j--) {
				Transformer curInAllModel = (Transformer)allModel.getElementAt(j);
				if (!(currentUnits.contains(curInAllModel))) {
					allModel.removeElement(curInAllModel);
				}
			}

			//check for redundant refs in assigned model
			for (int j=assignedModel.getSize()-1; j>=0; j--) {
				Transformer curInAssignedModel = (Transformer)assignedModel.getElementAt(j);
				if (!(currentUnits.contains(curInAssignedModel))) {
					assignedModel.removeElement(curInAssignedModel);
				}
			}
		}

	}
}