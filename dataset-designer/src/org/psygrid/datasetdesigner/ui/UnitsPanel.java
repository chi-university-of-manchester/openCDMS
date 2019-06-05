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

import org.psygrid.data.model.hibernate.BasicEntry;
import org.psygrid.data.model.hibernate.DerivedEntry;
import org.psygrid.data.model.hibernate.Document;
import org.psygrid.data.model.hibernate.Entry;
import org.psygrid.data.model.hibernate.ExternalDerivedEntry;
import org.psygrid.data.model.hibernate.OptionEntry;
import org.psygrid.data.model.hibernate.Unit;

import org.psygrid.datasetdesigner.actions.AssignUnitAction;
import org.psygrid.datasetdesigner.actions.ConfigureUnitsAction;
import org.psygrid.datasetdesigner.actions.UnassignUnitAction;
import org.psygrid.datasetdesigner.controllers.DatasetController;
import org.psygrid.datasetdesigner.custom.CustomCopyPasteJList;
import org.psygrid.datasetdesigner.model.StudyDataSet;
import org.psygrid.datasetdesigner.model.DummyDocument;
import org.psygrid.datasetdesigner.utils.HelpHelper;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.utils.Utils;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;


/**
 * Units Panel - reused through to allow adding/removing of units to
 * an entry
 * @author pwhelan
 *
 */
public class UnitsPanel extends JPanel implements ActionListener {
    /**
     * All available units for the dataset
     */
    private JList allOptionsList;

    /**
     * The units assigned to this entry
     */
    private JList dependentList;

    /**
     * Assign button for adding units
     */
    private JButton assignButton;

    /**
     * Remove button for removing units
     */
    private JButton unassignButton;

    /**
     * The document to which the entry belongs
     */
    private Document document;

    /**
     * The entry for unit assignment
     */
    private BasicEntry entry;

    /**
     * The main window of the application
     */
    private MainFrame parentFrame;

    /**
     * If DEL is used in read only mode
     */
    private boolean viewOnly;
    
    /**
     * Indicates if the ds is the DEL
     */
    private boolean isDEL;

    /**
     * Constructor
     * @param parentFrame the owner the dialog
     * @param document the document this entry belongs to
     * @param entry the current entry
     * @param viewOnly true if in DEL mode, false if not
     */
    public UnitsPanel(MainFrame parentFrame, Document document,
        BasicEntry entry, boolean viewOnly) {
        this.entry = entry;
        this.document = document;
        this.parentFrame = parentFrame;
        this.viewOnly = viewOnly;
        this.isDEL = isDEL;

        if (!viewOnly) {
            setBorder(BorderFactory.createTitledBorder(
                    PropertiesHelper.getStringFor(
                        "org.psygrid.datasetdesigner.ui.configureunitsshort")));
        } else {
            setBorder(BorderFactory.createTitledBorder(
                    PropertiesHelper.getStringFor(
                        "org.psygrid.datasetdesigner.ui.viewunits")));
        }

        add(buildCenterPanel(), BorderLayout.CENTER);
        add(buildTopPanel(), BorderLayout.NORTH);

        //add a listener to repopulate this panel if units are changed from manage units dialog
        StudyDataSet activeDs = DatasetController.getInstance().getActiveDs();

        if (activeDs != null) {
            activeDs.addActionListener(this);
        }

        populate();
    }

    /**
     * Build the center panel; based on viewonly mode setting
     * @return configured center panel
     */
    private JPanel buildCenterPanel() {
        JPanel centerPanel = new JPanel();
        setLayout(new BorderLayout());
        allOptionsList = new CustomCopyPasteJList();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.X_AXIS));
        dependentList = new CustomCopyPasteJList();
        assignButton = new CustomIconButton(new AssignUnitAction(
                    allOptionsList, dependentList),
                PropertiesHelper.getStringFor(
                    "org.psygrid.datasetdesigner.ui.assignunit"));
        unassignButton = new CustomIconButton(new UnassignUnitAction(
                    allOptionsList, dependentList),
                PropertiesHelper.getStringFor(
                    "org.psygrid.datasetdesigner.ui.unassignunit"));

        if (!viewOnly) {
            centerPanel.add(Utils.createSubPanel(PropertiesHelper.getStringFor(
                        "org.psygrid.datasetdesigner.ui.allunits"),
                    allOptionsList));
            centerPanel.add(Utils.createArrowPanel(unassignButton, assignButton));
        }

        centerPanel.add(Utils.createSubPanel(PropertiesHelper.getStringFor(
                    "org.psygrid.datasetdesigner.ui.assignedunits"),
                dependentList));

        return centerPanel;
    }

    /**
     * Build the top button with the configure units button
     * configure according to view only mode
     * @return the configured button to manage units
     */
    private JPanel buildTopPanel() {
        JPanel topButtonPanel = new JPanel();
        topButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        topButtonPanel.add(HelpHelper.getInstance()
                                     .getHelpButtonWithID("dsdmanageunits"));

		if (viewOnly) {
			topButtonPanel.add(new JButton(new ConfigureUnitsAction(parentFrame, viewOnly)));
		}
		else {
			topButtonPanel.add(new JButton(new ConfigureUnitsAction(parentFrame)));	
		}

        return topButtonPanel;
    }

    /**
     * Fill the lists with the current values for study and entry units
     */
    public void populate() {
        //clear both models before populating!
        ((DefaultListModel) allOptionsList.getModel()).clear();
        ((DefaultListModel) dependentList.getModel()).clear();

        //first populate all list
        if (document != null) { //document can be null when viewing entry via DEL search dialog

            int numUnits = document.getDataSet().numUnits();

            for (int i = 0; i < numUnits; i++) {
                ((DefaultListModel) allOptionsList.getModel()).addElement(document.getDataSet()
                                                                                  .getUnit(i));
            }
        }

        int numUnitsForEntry = entry.numUnits();

        for (int j = 0; j < numUnitsForEntry; j++) {
            //remove from left list
            ((DefaultListModel) allOptionsList.getModel()).removeElement(entry.getUnit(
                    j));

            //add to right list
            ((DefaultListModel) dependentList.getModel()).addElement(entry.getUnit(
                    j));
        }
    }

    /**
     * Save the currently selected units for this entry
     */
    public void saveUnits() {
        List<Unit> oldUnits = new ArrayList<Unit>();
        oldUnits.addAll(((BasicEntry) entry).getUnits());

        if (oldUnits.size() != dependentList.getModel().getSize()) {
            ((BasicEntry) entry).setIsRevisionCandidate(true);
            ((Document) DatasetController.getInstance().getActiveDocument()).setIsRevisionCandidate(true);

            //Required for composites..
            if (DatasetController.getInstance().getActiveDocument() instanceof DummyDocument) {
                Entry singleEntry = ((Document) DatasetController.getInstance()
                                                                 .getActiveDocument()).getEntry(0);

                for (Object curEntry : ((Document) DatasetController.getInstance()
                                                                    .getActiveDocument()).getEntries()) {
                    if (curEntry instanceof ExternalDerivedEntry) {
                        singleEntry = (Entry) curEntry;
                    } else if (curEntry instanceof DerivedEntry) {
                        singleEntry = (Entry) curEntry;
                    } else if (curEntry instanceof OptionEntry) {
                        singleEntry = (Entry) curEntry;
                    }
                }

                singleEntry.setIsRevisionCandidate(true);
            }
        }

        //first remove all existing units from the entry
        for (int j = entry.numUnits(); j > 0; j--) {
            entry.removeUnit(0);
        }

        for (int i = 0; i < dependentList.getModel().getSize(); i++) {
            if (!oldUnits.contains((dependentList.getModel().getElementAt(i)))) {
                ((BasicEntry) entry).setIsRevisionCandidate(true);
                ((Document) DatasetController.getInstance().getActiveDocument()).setIsRevisionCandidate(true);

                //Required for composites..
                if (DatasetController.getInstance().getActiveDocument() instanceof DummyDocument) {
                    Entry singleEntry = Utils.getMainEntry((DummyDocument) DatasetController.getInstance()
                                                                                            .getActiveDocument());
                    singleEntry.setIsRevisionCandidate(true);
                }
            }

            entry.addUnit((Unit) dependentList.getModel().getElementAt(i));
        }
    }

    /**
     * Action event - repopulate the lists
     * @param e the calling action event
     */
    public void actionPerformed(ActionEvent e) {
        //calling a populate is too restrictive - it will lose all existing assignments

        //first populate all list
        if (document != null) { //document can be null when viewing entry via DEL search dialog

            int numUnits = document.getDataSet().numUnits();

            DefaultListModel allModel = (DefaultListModel) allOptionsList.getModel();
            DefaultListModel assignedModel = (DefaultListModel) dependentList.getModel();

            //do one pass through and add to appropriate lists
            for (int i = 0; i < numUnits; i++) {
                Unit curUnit = document.getDataSet().getUnit(i);

                if (!(assignedModel.contains(curUnit))) {
                    if (!(allModel.contains(curUnit))) {
                        allModel.addElement(curUnit);
                    }
                }
            }

            //check individual lists for units that have been removed from the dataset
            DefaultListModel currentUnits = new DefaultListModel();

            for (int i = 0; i < numUnits; i++) {
                currentUnits.addElement(document.getDataSet().getUnit(i));
            }

            //check for redundant refs in all model
            for (int j = allModel.getSize() - 1; j >= 0; j--) {
                Unit curInAllModel = (Unit) allModel.getElementAt(j);

                if (!(currentUnits.contains(curInAllModel))) {
                    allModel.removeElement(curInAllModel);
                }
            }

            //check for redundant refs in assigned model
            for (int j = assignedModel.getSize() - 1; j >= 0; j--) {
                Unit curInAssignedModel = (Unit) assignedModel.getElementAt(j);

                if (!(currentUnits.contains(curInAssignedModel))) {
                    assignedModel.removeElement(curInAssignedModel);
                }
            }
        }
    }
}
