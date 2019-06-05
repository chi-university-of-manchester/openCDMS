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
import javax.swing.ListSelectionModel;

import org.psygrid.data.model.hibernate.*;
import org.psygrid.datasetdesigner.actions.AssignUnitAction;
import org.psygrid.datasetdesigner.actions.ConfigureValidationRulesAction;
import org.psygrid.datasetdesigner.actions.UnassignUnitAction;
import org.psygrid.datasetdesigner.actions.ViewValidationRulesAction;
import org.psygrid.datasetdesigner.controllers.DatasetController;
import org.psygrid.datasetdesigner.custom.CustomCopyPasteJList;
import org.psygrid.datasetdesigner.model.DummyDocument;
import org.psygrid.datasetdesigner.model.StudyDataSet;
import org.psygrid.datasetdesigner.renderer.OptionListCellRenderer;
import org.psygrid.datasetdesigner.utils.HelpHelper;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.utils.Utils;


/**
 * Validation Panel - reused through to allow adding/removing of validation rules
 * to an entry
 * @author pwhelan
 *
 */
public class ValidationPanel extends JPanel implements ActionListener {

	//lists containing possible and used validation rules
	private JList allOptionsList;

	private JList dependentList;

	//buttons to add/remove validation rules
	private JButton assignButton;
	private JButton unassignButton;

	//the document that this entry belongs to
	private Document document;

	//the entry to add validation rules to
	private BasicEntry entry;

	private boolean isDEL;

	/**
	 * Validation Panel 
	 * @param parentFrame the main calling frame
	 * @param activeDocument the currently active document
	 * @param entry the entry to which the validation rules should be added
	 * @param viewOnly if in view only del mode or not
	 */
	public ValidationPanel(MainFrame parentFrame, Document activeDocument, BasicEntry entry, boolean viewOnly, boolean isDEL) {
		this.entry = entry;
		this.document = activeDocument;
		this.isDEL = isDEL;
		allOptionsList = new CustomCopyPasteJList();
		allOptionsList.setModel(new DefaultListModel());
		allOptionsList.setCellRenderer(new OptionListCellRenderer());
		allOptionsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		dependentList = new CustomCopyPasteJList();
		dependentList.setModel(new DefaultListModel());
		dependentList.setCellRenderer(new OptionListCellRenderer());
		dependentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		assignButton = new CustomIconButton(new AssignUnitAction(allOptionsList, dependentList), PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.assignvalidation"));
		unassignButton = new CustomIconButton(new UnassignUnitAction(allOptionsList, dependentList), PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.unassignvalidation"));

		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.X_AXIS));

		if(!viewOnly){
			centerPanel.add(createSubPanel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.allvalidationrules"), allOptionsList));
			centerPanel.add(createArrowPanel(unassignButton, assignButton));
		}

		centerPanel.add(createSubPanel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.assignedvalidationrules"), dependentList));

		if(viewOnly) {
			setBorder(BorderFactory.createTitledBorder(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.viewvalidationrules")));
		}
		else {
			setBorder(BorderFactory.createTitledBorder(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.configurevalidationrules")));
		}

		populate();

		JPanel topButtonPanel = new JPanel();
		topButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		if (parentFrame != null) {
			topButtonPanel.add(HelpHelper.getInstance().getHelpButtonWithID("dsdmanagevalidationrules"));
			if (viewOnly) {
				topButtonPanel.add(new JButton(new ViewValidationRulesAction(parentFrame, viewOnly)));
			}
			else {
				topButtonPanel.add(new JButton(new ConfigureValidationRulesAction(parentFrame)));
			}
		}
		setLayout(new BorderLayout());
		add(topButtonPanel, BorderLayout.NORTH);

		add(centerPanel, BorderLayout.CENTER);

        //add a listener to repopulate this panel if units are changed from manage units dialog
        StudyDataSet activeDs = DatasetController.getInstance().getActiveDs();

        if (activeDs != null) {
            activeDs.addActionListener(this);
        }

	}

	/**
	 * Populate the lists with possible and assigned validation rules
	 *
	 */
	public void populate() {
		//clear both models before populating!
		((DefaultListModel)allOptionsList.getModel()).clear();
		((DefaultListModel)dependentList.getModel()).clear();

		//first populate all list
		if (document != null) {		//document can be null when viewing entry via DEL search dialog
			int numValidationRules = document.getDataSet().numValidationRules();
			nextRule: for (int i=0; i<numValidationRules; i++) {
				ValidationRule rule = (ValidationRule)document.getDataSet().getValidationRule(i);
				DataElementStatus status = rule.getStatus();

				//Only allow approved or new rules to be added to an entry
				if (DataElementStatus.APPROVED.equals(status)
						|| rule.getLSID() == null) {

					for (ValidationRule r: ((BasicEntry)entry).getValidationRules()) {
						if (r.getLSID()!= null && r.getLSID().equals(rule.getLSID())) {
							//Don't add the rule if the entry already uses it.		
							continue nextRule;
						}
					}

					if (! ((BasicEntry)entry).getValidationRules().contains(rule)) {

						if (rule instanceof TextValidationRule) {
							if (entry instanceof TextEntry || entry instanceof LongTextEntry) {
								((DefaultListModel)allOptionsList.getModel()).addElement(rule);
							}
						} else if (rule instanceof NumericValidationRule || rule instanceof IntegerValidationRule) {
							if (entry instanceof NumericEntry || entry instanceof IntegerEntry) {
								((DefaultListModel)allOptionsList.getModel()).addElement(rule);
							}
						} else if (rule instanceof DateValidationRule) {
							if (entry instanceof DateEntry) {
								((DefaultListModel)allOptionsList.getModel()).addElement(rule);
							}
						}
					}
				}
			}
		}
		int numEntryValidationRules = entry.numValidationRules();

		for (int j=0; j<numEntryValidationRules; j++) {
			//add to right list
			((DefaultListModel)dependentList.getModel()).addElement(entry.getValidationRule(j));
		}

	}

	/**
	 * Save the validation rules
	 *
	 */
	public void saveValidationRules() {
		if (entry.numValidationRules() != dependentList.getModel().getSize()) {
			((BasicEntry)entry).setIsRevisionCandidate(true);
			((Document)DatasetController.getInstance().getActiveDocument()).setIsRevisionCandidate(true);

			//Required for composites..
			if (DatasetController.getInstance().getActiveDocument() instanceof DummyDocument) {
				Entry singleEntry = Utils.getMainEntry((DummyDocument)DatasetController.getInstance().getActiveDocument());
				singleEntry.setIsRevisionCandidate(true);
			}
		}
		List<ValidationRule> oldRules = new ArrayList<ValidationRule>();
		oldRules.addAll(((BasicEntry)entry).getValidationRules());

		//first remove all existing units from the entry
		for (int j=entry.numValidationRules(); j>0; j--){
			entry.removeValidationRule(0);
		}	

		for (int i=0; i<dependentList.getModel().getSize(); i++) {
			if (!oldRules.contains(dependentList.getModel().getElementAt(i))) {
				((BasicEntry)entry).setIsRevisionCandidate(true);
				((Document)DatasetController.getInstance().getActiveDocument()).setIsRevisionCandidate(true);
				//Required for composites..
				if (DatasetController.getInstance().getActiveDocument() instanceof DummyDocument) {
					Entry singleEntry = Utils.getMainEntry((DummyDocument)DatasetController.getInstance().getActiveDocument());
					singleEntry.setIsRevisionCandidate(true);
				}
			}
			entry.addValidationRule((ValidationRule)dependentList.getModel().getElementAt(i));
		}

	}

	/**
	 * Lay out the panel 
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
	 * When action occurs, repopulate the lists
	 * @param e the calling action event
	 */
	public void actionPerformed(ActionEvent e) {
		//calling a populate is too restrictive - it will lose all existing assignments

		DefaultListModel allOptionsModel = (DefaultListModel)allOptionsList.getModel();
		
		//first populate all list
		if (document != null) {		//document can be null when viewing entry via DEL search dialog
			int numValRules = document.getDataSet().numValidationRules();
			
			nextRule: for (int i=0; i<numValRules; i++) {
				ValidationRule rule = (ValidationRule)document.getDataSet().getValidationRule(i);
				DataElementStatus status = rule.getStatus();

				//Only allow approved or new rules to be added to an entry
				if (DataElementStatus.APPROVED.equals(status)
						|| rule.getLSID() == null) {

					for (ValidationRule r: ((BasicEntry)entry).getValidationRules()) {
						if (r.getLSID()!= null && r.getLSID().equals(rule.getLSID())) {
							//Don't add the rule if the entry already uses it.		
							continue nextRule;
						}
					}
				
					if (!allOptionsModel.contains(rule)) {
						if (rule instanceof TextValidationRule) {
							if (entry instanceof TextEntry || entry instanceof LongTextEntry) {
								allOptionsModel.addElement(rule);
							}
						} else if (rule instanceof NumericValidationRule || rule instanceof IntegerValidationRule) {
							if (entry instanceof NumericEntry || entry instanceof IntegerEntry) {
								allOptionsModel.addElement(rule);
							}
						} else if (rule instanceof DateValidationRule) {
							if (entry instanceof DateEntry) {
								allOptionsModel.addElement(rule);
							}
						}
					}
				}
			}

			DefaultListModel assignedModel = (DefaultListModel)dependentList.getModel();
			
			//check individual lists for units that have been removed from the dataset
			DefaultListModel currentValRules = new DefaultListModel();
			
			for (int i=0; i<numValRules; i++) {
				currentValRules.addElement(document.getDataSet().getValidationRule(i));
			}
			
			//check for redundant refs in all model
			for (int j=allOptionsModel.getSize()-1; j>=0; j--) {
				ValidationRule curInAllModel = (ValidationRule)allOptionsModel.getElementAt(j);
				if (!(currentValRules.contains(curInAllModel))) {
					allOptionsModel.removeElement(curInAllModel);
				}
			}

			//check for redundant refs in assigned model
			for (int j=assignedModel.getSize()-1; j>=0; j--) {
				ValidationRule curInAssignedModel = (ValidationRule)assignedModel.getElementAt(j);
				if (!(currentValRules.contains(curInAssignedModel))) {
					assignedModel.removeElement(curInAssignedModel);
				}
			}
			
			//remove assigned items from all model
			for (int j=assignedModel.getSize()-1; j>=0; j--) {
				allOptionsModel.removeElement(assignedModel.getElementAt(j));
			}
			
		}
	}
}