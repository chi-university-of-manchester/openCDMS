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
package org.psygrid.datasetdesigner.ui.configurationdialogs;

import org.psygrid.datasetdesigner.actions.AssignAction;
import org.psygrid.datasetdesigner.actions.UnassignAction;
import org.psygrid.datasetdesigner.controllers.DatasetController;
import org.psygrid.datasetdesigner.custom.CustomCopyPasteJList;
import org.psygrid.datasetdesigner.model.StudyDataSet;
import org.psygrid.datasetdesigner.renderer.OptionListCellRenderer;
import org.psygrid.datasetdesigner.ui.MainFrame;
import org.psygrid.datasetdesigner.utils.DefaultDSSettings;
import org.psygrid.datasetdesigner.utils.ListModelUtility;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.utils.Utils;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;


/**
 * Configure the reports for the dataset
 *
 * @author pwhelan
 */
public class ConfigureReportsDialog extends JDialog implements ActionListener {
    /**
     * Ok button; save changes and dismiss
     */
    private JButton okButton;

    /**
     * Cancel button; dismiss
     */
    private JButton cancelButton;

    /**
     * Assign a report to the dataset
     */
    private JButton assignButton;

    /**
     * Unassign a report to the dataset
     */
    private JButton unassignButton;

    /**
     * The list of all reports (Default setting)
     */
    private JList availableReports;

    /**
     * The list of assigned reports
     */
    private JList assignedReports;

    /**
     * The currently active dataset
     */
    private StudyDataSet activeDs;

    /**
     * Flag to indicate if the dialog should be opened in read-only
     * mode
     */
    private boolean readOnly;

    
    /**
     * Creates a new ConfigureReportsDialog object.
     *
     * @param frame
     */
    public ConfigureReportsDialog(MainFrame frame) {
    	this(frame, true);
    }
    
    /**
     * Creates a new ConfigureReportsDialog object.
     *
     * @param frame
     */
    public ConfigureReportsDialog(MainFrame frame, boolean readOnly) {
        super(frame);
        if (readOnly) {
        	setTitle(PropertiesHelper.getStringFor(
            "org.psygrid.datasetdesigner.ui.viewreports"));
        } else {
        	setTitle(PropertiesHelper.getStringFor(
            "org.psygrid.datasetdesigner.ui.configurereports"));
        }
        this.readOnly = readOnly;
        
        //set read only flag if dataset is published
        if (DatasetController.getInstance().getActiveDs().getDs().isPublished()) {
        	readOnly = true;
        }
        
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(Utils.buildDsHeaderPanel(), BorderLayout.NORTH);
        getContentPane().add(buildPanels(), BorderLayout.CENTER);
        getContentPane().add(buildButtonPanel(), BorderLayout.SOUTH);
        activeDs = DatasetController.getInstance().getActiveDs();
        init();
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     *Initalise the two report lists
     */
    private void init() {
        ArrayList<String> allReports = DefaultDSSettings.getAllReports();
        ArrayList<String> dsReports = new ArrayList<String>();

        if (activeDs != null) {
            dsReports = activeDs.getReports();
        }

        allReports.removeAll(dsReports);
        availableReports.setModel(ListModelUtility.convertArrayListToListModel(
                allReports));
        assignedReports.setModel(ListModelUtility.convertArrayListToListModel(dsReports));
        
        if (readOnly){
        	assignButton.setEnabled(false);
        	unassignButton.setEnabled(false);
        }
    }

    /**
     * Build the ok cancel button panel
     *
     * @return the ok cancel button panel
     */
    private JPanel buildButtonPanel() {
        okButton = new JButton(PropertiesHelper.getStringFor(
                    "org.psygrid.datasetdesigner.ok"));
        okButton.addActionListener(this);
        cancelButton = new JButton(PropertiesHelper.getStringFor(
                    "org.psygrid.datasetdesigner.cancel"));
        cancelButton.addActionListener(this);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(okButton);
        
        if (!readOnly) {
            buttonPanel.add(cancelButton);
        }

        return buttonPanel;
    }

    /**
     * Bulid the panels
     *
     * @return 
     */
    private JPanel buildPanels() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
        availableReports = new CustomCopyPasteJList();
        availableReports.setCellRenderer(new OptionListCellRenderer());
        assignedReports = new CustomCopyPasteJList();
        assignedReports.setCellRenderer(new OptionListCellRenderer());
        assignButton = new JButton(new AssignAction(availableReports,
                    assignedReports));
        unassignButton = new JButton(new UnassignAction(availableReports,
                    assignedReports));
        mainPanel.add(Utils.createSubPanel(PropertiesHelper.getStringFor(
                    "org.psygrid.datasetdesigner.ui.allreports"),
                availableReports));
        mainPanel.add(Utils.createArrowPanel(unassignButton, assignButton));
        mainPanel.add(Utils.createSubPanel(PropertiesHelper.getStringFor(
                    "org.psygrid.datasetdesigner.ui.assignedreports"),
                assignedReports));
        mainPanel.setBorder(BorderFactory.createTitledBorder(
                PropertiesHelper.getStringFor(
                    "org.psygrid.datasetdesigner.ui.configurereports")));

        return mainPanel;
    }

    /**
     * Handle ok and cancel
     * @param aet the trigger event
     */
    public void actionPerformed(ActionEvent aet) {
        if (aet.getSource() == okButton) {
        	if (!readOnly) {
                DatasetController.getInstance().getActiveDs()
                .setReports(ListModelUtility.convertListModelToStringList(
                		((DefaultListModel) assignedReports.getModel())));
        	
                //if published then show the provenance dialog
				if (DatasetController.getInstance().getActiveDs().getDs().isPublished()) {
					new ProvenanceDialog(this, DatasetController.getInstance().getActiveDs().getDs());
					//don't dispose of dialog
					return;
				} 
        	
        	}
            this.dispose();
        } else if (aet.getSource() == cancelButton) {
            this.dispose();
        }
    }
}
