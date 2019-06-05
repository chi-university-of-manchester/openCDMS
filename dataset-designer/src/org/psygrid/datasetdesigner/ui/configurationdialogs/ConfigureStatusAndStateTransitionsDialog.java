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

import org.psygrid.data.model.hibernate.Status;

import org.psygrid.datasetdesigner.actions.AddStatusAction;
import org.psygrid.datasetdesigner.actions.AssignTransitionAction;
import org.psygrid.datasetdesigner.actions.RemoveStatusAction;
import org.psygrid.datasetdesigner.actions.UnassignTransitionAction;
import org.psygrid.datasetdesigner.controllers.DatasetController;
import org.psygrid.datasetdesigner.custom.CustomCopyPasteJList;
import org.psygrid.datasetdesigner.model.StudyDataSet;
import org.psygrid.datasetdesigner.model.StatusTableModel;
import org.psygrid.datasetdesigner.renderer.OptionListCellRenderer;
import org.psygrid.datasetdesigner.ui.CustomIconButton;
import org.psygrid.datasetdesigner.ui.MainFrame;
import org.psygrid.datasetdesigner.utils.ListModelUtility;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.utils.Utils;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
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
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;


/**
 * Class to manage the configuration of study states and transitions
 * @author pwhelan
 */
public class ConfigureStatusAndStateTransitionsDialog extends JDialog
    implements ActionListener {
    
	/**
     * Ok Button
     */
    private JButton okButton;

    /**
     * Cancel Button
     */
    private JButton cancelButton;

    /**
     * Edit the status button
     */
    private JButton editButton;

    /**
     * Remove the status button
     */
    private JButton removeButton;

    /**
     * Assign a transition button
     */
    private JButton assignButton;

    /**
     * Remove a transition button
     */
    private JButton unassignButton;

    /**
     * State selector box
     */
    private JComboBox stateBox;

    /**
     * List of available transitions for the study status
     */
    private JList availableTransitionsList;

    /**
     * List of assigned transitions for the study status
     */
    private JList assignedTransitionsList;

    /**
     * Table of all statuses
     */
    private JTable statusTable;
    
    /**
     * Main window of the application
     */
    private MainFrame frame;

    /**
     * The currently active study set
     */
    private StudyDataSet activeDs;
    
    /**
     * Indicateds if the study has been opened in read-only mode
     * (if it's already open by another user)
     */
    private boolean readOnly;

    /**
     * Configure the status and state transition
     * @param frame the main window of the application
     */
    public ConfigureStatusAndStateTransitionsDialog(MainFrame frame) {
        super(frame,
            PropertiesHelper.getStringFor(
                "org.psygrid.datasetdesigner.ui.configurestatesandtransitions"));
        this.frame = frame;
        
        activeDs = DatasetController.getInstance().getActiveDs();

        //if the study is published, set the read only to true -- no changes can be made!
        if (activeDs.getDs().isPublished()) {
        	readOnly = true;
        }
        
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(buildMainPanel(), BorderLayout.CENTER);
        getContentPane().add(buildButtonPanel(), BorderLayout.SOUTH);
        init();
        pack();
        setModal(true);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    
    /**
     * Configure the status and state transition
     * @param frame the main window of the application
     */
    public ConfigureStatusAndStateTransitionsDialog(MainFrame frame, boolean readOnly) {
        super(frame,
            PropertiesHelper.getStringFor(
                "org.psygrid.datasetdesigner.ui.viewstatesandtransitions"));
        this.frame = frame;
        this.readOnly = readOnly;
        activeDs = DatasetController.getInstance().getActiveDs();
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(buildMainPanel(), BorderLayout.CENTER);
        getContentPane().add(buildButtonPanel(), BorderLayout.SOUTH);
        init();
        pack();
        setModal(true);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    
    /**
     *
     */
    private void init() {
        DefaultComboBoxModel stateModel = new DefaultComboBoxModel();

        DefaultTableModel statusTableModel = new DefaultTableModel();
        
        if (activeDs != null) {
        	statusTableModel = new StatusTableModel(activeDs.getStatuses());
        	statusTable.setModel(statusTableModel);
        	
        	ArrayList<Status> statuses = activeDs.getStatuses();

            for (int z = 0; z < statuses.size(); z++) {
                stateModel.addElement(statuses.get(z));
            }
        }

        //init the state transition stuff
        stateBox.setModel(stateModel);

        ArrayList<Status> statusArray = ((StatusTableModel)statusTable.getModel()).getAllStatuses();
        
        DefaultListModel availableTransitionModel = ListModelUtility.convertArrayListToListModel(statusArray);
        availableTransitionModel.removeElement(stateBox.getSelectedItem());
        availableTransitionsList.setModel(availableTransitionModel);
        assignedTransitionsList.setModel(new DefaultListModel());

        Object selectedStatus = stateBox.getSelectedItem();

        if (selectedStatus != null) {
            Status selStatus = (Status) selectedStatus;

            if (selStatus != null) {
                for (int i = 0; i < selStatus.numStatusTransitions(); i++) {
                    ((DefaultListModel) assignedTransitionsList.getModel()).addElement(selStatus.getStatusTransition(
                            i));
                    ((DefaultListModel) availableTransitionsList.getModel()).removeElement(selStatus.getStatusTransition(
                            i));
                }
            }
        }
        
        //table headers for the status column
        statusTable.getColumnModel().getColumn(0)
    		.setHeaderValue(PropertiesHelper.getStringFor(
    		"org.psygrid.datasetdesigner.ui.statusname"));
        statusTable.getColumnModel().getColumn(1)
    		.setHeaderValue(PropertiesHelper.getStringFor(
    		"org.psygrid.datasetdesigner.ui.genericstate"));
        statusTable.getColumnModel().getColumn(2)
        	.setHeaderValue(PropertiesHelper.getStringFor(
        	"org.psygrid.datasetdesigner.ui.statuscode"));
        

        if (readOnly) {
        	assignButton.setEnabled(false);
        	unassignButton.setEnabled(false);
        }
    }

    /**
     * Build the main status list panel
     *
     * @return the configured status panel
     */
    public JPanel buildStatusPanel() {
        JPanel statusPanel = new JPanel();
        statusPanel.setBorder(BorderFactory.createTitledBorder(
                PropertiesHelper.getStringFor(
                    "org.psygrid.datasetdesigner.ui.datasetstatusmgmt")));
        statusPanel.setLayout(new BorderLayout());
        statusTable = new JTable();
        statusTable.setModel(new DefaultTableModel());
        JScrollPane sp = new JScrollPane(statusTable);
        
        sp.setMinimumSize(new Dimension(250, 100));
        sp.setPreferredSize(new Dimension(250, 100));
        sp.setMaximumSize(new Dimension(250, 100));

        statusPanel.add(buildAddRemovePanel(), BorderLayout.NORTH);
        statusPanel.add(sp, BorderLayout.CENTER);
        
		//substance 4.0 defaults table headers to the left
		((DefaultTableCellRenderer)statusTable.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

        return statusPanel;
    }

    /**
     * Build the add remove status panel
     *
     * @return the configured add remove panel
     */
    private JPanel buildAddRemovePanel() {
        JPanel addRemovePanel = new JPanel();
        addRemovePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        if (readOnly) {
        	return addRemovePanel;
        }
        
        addRemovePanel.add(new JButton(
                new AddStatusAction(this, statusTable, availableTransitionsList,
                    assignedTransitionsList, stateBox)));

        editButton = new JButton(new AddStatusAction(this, statusTable, true,
                    availableTransitionsList, assignedTransitionsList, stateBox));
        editButton.setEnabled(false);
        addRemovePanel.add(editButton);

        removeButton = new JButton(new RemoveStatusAction(statusTable,
                    availableTransitionsList, assignedTransitionsList, stateBox));
        removeButton.setEnabled(false);
        addRemovePanel.add(removeButton);

        //Only enable the buttons if a status is selected
        statusTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent event) {
                    boolean enabled = true;

                    if ((statusTable == null) ||
                            (statusTable.getSelectedRow() == -1)) {
                        enabled = false;
                    }

                    if (removeButton != null) {
                        removeButton.setEnabled(enabled);
                    }

                    if (editButton != null) {
                        editButton.setEnabled(enabled);
                    }
                }
            });

        return addRemovePanel;
    }

    /**
     * Build the panel for state transitions
     *
     * @return the panel containing the state transitions
     */
    public JPanel buildStateTransitionsPanel() {
        JPanel unitsPanel = new JPanel();
        unitsPanel.setBorder(BorderFactory.createTitledBorder(
                PropertiesHelper.getStringFor(
                    "org.psygrid.datasetdesigner.ui.datasetstatetransmgmt")));
        unitsPanel.setLayout(new BorderLayout());

        JPanel stateBoxPanel = new JPanel();
        stateBoxPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        stateBoxPanel.add(new JLabel(PropertiesHelper.getStringFor(
                    "org.psygrid.datasetdesigner.ui.datasetstate")));
        stateBox = new JComboBox();
        stateBox.setRenderer(new OptionListCellRenderer());

        DefaultComboBoxModel stateBoxModel = new DefaultComboBoxModel();
        stateBox.setModel(stateBoxModel);
        stateBox.addItemListener(new StateBoxChangedListener());
        stateBoxPanel.add(stateBox);

        availableTransitionsList = new CustomCopyPasteJList();
        availableTransitionsList.setModel(new DefaultListModel());
        assignedTransitionsList = new CustomCopyPasteJList();
        assignedTransitionsList.setModel(new DefaultListModel());

        assignButton = new CustomIconButton(new AssignTransitionAction(
                    availableTransitionsList, assignedTransitionsList, stateBox),
                "Assign Option");
        unassignButton = new CustomIconButton(new UnassignTransitionAction(
                    availableTransitionsList, assignedTransitionsList, stateBox),
                "Unassign Option");

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.X_AXIS));
        listPanel.add(Utils.createSubPanel(PropertiesHelper.getStringFor(
                    "org.psygrid.datasetdesigner.ui.availabletransitions"),
                availableTransitionsList));
        listPanel.add(Utils.createArrowPanel(unassignButton, assignButton));
        listPanel.add(Utils.createSubPanel(PropertiesHelper.getStringFor(
                    "org.psygrid.datasetdesigner.ui.assignedtransitions"),
                assignedTransitionsList));

        availableTransitionsList.setCellRenderer(new OptionListCellRenderer());
        availableTransitionsList.setMinimumSize(new Dimension(150, 150));
        availableTransitionsList.setPreferredSize(new Dimension(150, 150));
        availableTransitionsList.setMaximumSize(new Dimension(150, 150));

        assignedTransitionsList.setCellRenderer(new OptionListCellRenderer());
        assignedTransitionsList.setMinimumSize(new Dimension(150, 150));
        assignedTransitionsList.setPreferredSize(new Dimension(150, 150));
        assignedTransitionsList.setMaximumSize(new Dimension(150, 150));

        unitsPanel.add(stateBoxPanel, BorderLayout.NORTH);
        unitsPanel.add(listPanel, BorderLayout.CENTER);

        return unitsPanel;
    }

    /**
     * Put the status list panel on top and
     * the transitions panel on the bottom
     *
     * @return the configured panel
     */
    public JPanel buildMainPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JPanel stateTransitionsPanel = buildStateTransitionsPanel();
        mainPanel.add(buildStatusPanel());
        mainPanel.add(stateTransitionsPanel);

        return mainPanel;
    }

    /**
     * Build the OK cancel button panel
     *
     * @return 
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
     * Check that at least one status has been configured
     *
     * @return 
     */
    public boolean validateEntries() {
        if (statusTable.getModel().getRowCount() == 0) {
            JOptionPane.showMessageDialog(frame,
                PropertiesHelper.getStringFor(
                    "org.psygrid.datasetdesigner.ui.atleastonestatus"));
            return false;
        }
        
        return true;
    }

    /**
     * Handle ok, cancel
     *
     * @param aet 
     */
    public void actionPerformed(ActionEvent aet) {
        if (aet.getSource() == okButton) {
        	if (!readOnly) {
                if (activeDs != null) {
                	
                	ArrayList<Status> statuses = new ArrayList<Status>();
                	
                	for (Status status: ((StatusTableModel)statusTable.getModel()).getAllStatuses()) {
                		statuses.add((Status)status);
                	}
                	
                    activeDs.setStatuses(statuses);
                }
                
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

    /**
     * List for state box changes and update the assigned
     * and available transitions list accordingly
     * @author pwhelan
     *
     */
    private class StateBoxChangedListener implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            //ArrayList<IStatus> statusArray = ListModelUtility.convertListModelToIStatusList(((DefaultListModel) statusList.getModel()));
            ArrayList<Status> statusArray = ((StatusTableModel)statusTable.getModel()).getAllStatuses();
        	
        	DefaultListModel availableTransitionModel = ListModelUtility.convertArrayListToListModel(statusArray);
            availableTransitionModel.removeElement(stateBox.getSelectedItem());
            availableTransitionsList.setModel(availableTransitionModel);
            ((DefaultListModel) assignedTransitionsList.getModel()).clear();

            Object selectedStatus = stateBox.getSelectedItem();

            if (selectedStatus != null) {
                Status selStatus = (Status) selectedStatus;

                for (int i = 0; i < selStatus.numStatusTransitions(); i++) {
                    ((DefaultListModel) assignedTransitionsList.getModel()).addElement(selStatus.getStatusTransition(
                            i));
                    ((DefaultListModel) availableTransitionsList.getModel()).removeElement(selStatus.getStatusTransition(
                            i));
                }
            }
        }
    }
    
    
}
