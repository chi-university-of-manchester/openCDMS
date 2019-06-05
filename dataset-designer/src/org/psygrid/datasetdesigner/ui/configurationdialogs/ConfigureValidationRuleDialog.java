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

import org.psygrid.data.model.hibernate.DataElementStatus;
import org.psygrid.data.model.hibernate.ValidationRule;
import org.psygrid.data.model.hibernate.ValidationRule;

import org.psygrid.datasetdesigner.actions.AddValidationRuleAction;
import org.psygrid.datasetdesigner.actions.ApproveValidationRuleTestsAction;
import org.psygrid.datasetdesigner.actions.RemoveValidationRuleAction;
import org.psygrid.datasetdesigner.actions.ViewValidationRuleAction;
import org.psygrid.datasetdesigner.actions.ViewValidationRuleTestsAction;
import org.psygrid.datasetdesigner.controllers.DatasetController;
import org.psygrid.datasetdesigner.custom.CustomCopyPasteJList;
import org.psygrid.datasetdesigner.model.StudyDataSet;
import org.psygrid.datasetdesigner.ui.MainFrame;
import org.psygrid.datasetdesigner.ui.dataelementfacilities.DELSecurity;
import org.psygrid.datasetdesigner.utils.ListModelUtility;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.utils.Utils;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;


/**
 * Dialog to configure validation rules
 * simple list with existing validation rules
 * and butttons to add/remove/edit
 *
 * @author pwhelan
 */
public class ConfigureValidationRuleDialog extends JDialog
    implements ActionListener {
    /**
     * For serialization
     */
    private static final long serialVersionUID = -5544878797942149142L;

    /**
     * The list containing validation rules; used in DSD mode
     */
    private JList validationJList;

    /**
     * The table containing validation rules and statuses;
     * used in DEL mode
     */
    private ValidationTable table;

    /**
     * Map of validation rules to their status (pending, approved)
     */
    private Map<ValidationRule, Location> validationMap; //list of validation rules for the dataset currently selected

    /**
     * Save; save changes and dismiss 
     */
    private JButton okButton;

    /**
     * Cancel button; dismiss dialog
     */
    private JButton cancelButton;

    /**
     * View Test; display the Test configuration dialog
     */
    private JButton viewTests = null;

    /**
     * Add a new validation rule
     */
    private JButton addRule = null;

    /**
     * Edit a validation rule
     */
    private JButton editRule = null;

    /**
     * Remove a validation rule
     */
    private JButton removeRule = null;

    /**
     * The currently active dataset
     */
    private StudyDataSet activeDs;

    /**
     * True if dialog is called from DEL mode; false if not
     */
    private boolean isDEL;

    /**
     * The main window of the application
     */
    private MainFrame frame;

    /**
     * Indicated dialog should be shown in read-only mode; defaults to false
     */
    private boolean readOnly = false;

    
    /**
     * Creates a new ConfigureValidationRuleDialog object.
     *
     * @param frame the main window of the application
     * @param title the name of the dialog
     * @param isDEL true if in DEL mode; false if not
     */
    public ConfigureValidationRuleDialog(MainFrame frame, String title,
        boolean isDEL, boolean readOnly) {
        super(frame, title);
        this.isDEL = isDEL;
        this.frame = frame;
        this.readOnly = readOnly;
        validationMap = new HashMap<ValidationRule, Location>();
        getContentPane().add(buildMainPanel());
        init();
        pack();
        setModal(true);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Creates a new ConfigureValidationRuleDialog object.
     *
     * @param frame main window of the application 
     * @param isDEL true if in DEL mode; false if not
     */
    public ConfigureValidationRuleDialog(MainFrame frame, boolean isDEL) {
        this(frame,
            PropertiesHelper.getStringFor(
                "org.psygrid.datasetdesigner.ui.configurevalidationrule"), isDEL, false);
    }
    
    /**
     * Creates a new ConfigureValidationRuleDialog object.
     *
     * @param frame main window of the application
     * @param isDEL true if in DEL mode; false if not
     * @param viewOnly only indicates different title?
     */
    public ConfigureValidationRuleDialog(MainFrame frame, String title, boolean viewOnly) {
        this(frame, title, viewOnly, false);
    }

    /**
     * Creates a new ConfigureValidationRuleDialog object.
     *
     * @param frame main window of the application
     * @param isDEL true if in DEL mode; false if not
     * @param viewOnly only indicates different title?
     */
    public ConfigureValidationRuleDialog(MainFrame frame, boolean isDEL,
        boolean viewOnly, boolean readOnly) {
        this(frame,
            PropertiesHelper.getStringFor(
                "org.psygrid.datasetdesigner.actions.viewvalidationrules"),
            isDEL, readOnly);
    }

    /**
     * Layout the panels - datasetbox chooser,
     * validation rules panel and button panel
     *
     * @return the configured JPanel
     */
    private JPanel buildMainPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(Utils.buildDsHeaderPanel(), BorderLayout.NORTH);
        mainPanel.add(buildUnitsPanel(), BorderLayout.CENTER);
        mainPanel.add(buildButtonPanel(), BorderLayout.SOUTH);
        mainPanel.setMinimumSize(new Dimension(380, 522));
        mainPanel.setPreferredSize(new Dimension(380, 522));
        mainPanel.setMaximumSize(new Dimension(380, 522));

        return mainPanel;
    }

    /**
     * Configure the existing validation rules for the selected dataset
     */
    private void init() {
        activeDs = DatasetController.getInstance().getActiveDs();
        
        if (activeDs != null) {
            StudyDataSet dsSet = activeDs;
            ArrayList<ValidationRule> rules = dsSet.getValidationRules();
            

            for (ValidationRule rule : rules) {
                String status = ((ValidationRule) rule).getEnumStatus();

                if (((ValidationRule) rule).getIsRevisionCandidate() &&
                        (((ValidationRule) rule).getLSID() != null)) {
                    validationMap.put(rule, Location.Edited);
                } else if ("approved".equalsIgnoreCase(status)) {
                    validationMap.put(rule, Location.Approved);
                } else if ("pending".equalsIgnoreCase(status)) {
                    validationMap.put(rule, Location.Pending);
                } else {
                    validationMap.put(rule, Location.New);
                }
            }

            if (isDEL) {
                ValidationTableModel myModel = new ValidationTableModel(validationMap);
                table.setModel(myModel);
            } else {
                validationJList.setModel(ListModelUtility.convertListToListModel(
                        rules));
            }
        }
    }

    /**
     * List of the existing validation rules
     * @return the list of existing validation rules
     */
    private JPanel buildUnitsPanel() {
        JPanel unitsPanel = new JPanel();
        unitsPanel.setBorder(BorderFactory.createTitledBorder(
                PropertiesHelper.getStringFor(
                    "org.psygrid.datasetdesigner.ui.validationrulesmgmt")));
        unitsPanel.setLayout(new BorderLayout());

        JScrollPane validationPane;

        //list is used for both DEL and plain DSD
        validationJList = new CustomCopyPasteJList();
        
        if (isDEL) {
            table = new ValidationTable();
            table.setPreferredScrollableViewportSize(new Dimension(500, 70));
            validationPane = new JScrollPane(table);
        } else {
            validationPane = new JScrollPane(validationJList);
        }

        Utils.sizeComponent(validationPane, new Dimension(300, 400));
        unitsPanel.add(buildAddRemovePanel(), BorderLayout.NORTH);
        unitsPanel.add(validationPane, BorderLayout.CENTER);

        return unitsPanel;
    }

    /**
     * The button panel containing the add, remove and edit buttons
     * @return the configured button panel
     */
    private JPanel buildAddRemovePanel() {
        final JPanel addRemovePanel = new JPanel();
        addRemovePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        
        if (readOnly) {
        	return addRemovePanel;
        }
        	

        //Add rule button
        if (!(isDEL && !DELSecurity.getInstance().canEditElements())) {
            addRule = new JButton(new AddValidationRuleAction(this,
                        validationJList, validationMap));
            addRule.setEnabled(true);
            addRemovePanel.add(addRule);
        }

        //Edit rule button
        if (isDEL && !DELSecurity.getInstance().canEditElements()) {
            //In the DEL view only authors can edit validation rules properties
            editRule = new JButton(new ViewValidationRuleAction(this,
                        validationJList, validationMap));
            addRemovePanel.add(editRule);
        } else {
            editRule = new JButton(new AddValidationRuleAction(this,
                        validationJList, true, validationMap));
            addRemovePanel.add(editRule);
        }

        editRule.setEnabled(false);

        //Remove rule button
        removeRule = new JButton(new RemoveValidationRuleAction(this,
                    validationJList, validationMap));
        removeRule.setEnabled(false);
        addRemovePanel.add(removeRule);

        //Only enable the buttons if a validation rule is selected
        validationJList.addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent event) {
                    boolean enabled = true;

                    if ((validationJList == null) ||
                            (validationJList.getSelectedValue() == null)) {
                        enabled = false;
                    }

                    if (removeRule != null && !activeDs.getDs().isPublished()) {
                        removeRule.setEnabled(enabled);
                    }

                    if (editRule != null && !activeDs.getDs().isPublished()) {
                        editRule.setEnabled(enabled);
                    }

                    //if (viewTests != null)  { viewTests.setEnabled(enabled);  }
                }
            });

        //Tests are editable only by authors in the DEL view. Anyone in normal view, or curators/viewers, can only view the tests.
        if (isDEL && DELSecurity.getInstance().canEditElements() &&
                !DELSecurity.getInstance().canApproveElements()) {
            addRemovePanel.add(new JButton(
                    new ViewValidationRuleTestsAction(this, validationJList,
                        validationMap, true)));
        } else {
            viewTests = new JButton(new ViewValidationRuleTestsAction(this,
                        validationJList, validationMap));

            viewTests.setEnabled(false);
            addRemovePanel.add(viewTests);

            final ConfigureValidationRuleDialog thisDialog = this;

            //Only enable the view tests button if the selected validation rule has a test!
            validationJList.addListSelectionListener(new ListSelectionListener() {
                    public void valueChanged(ListSelectionEvent event) {
                        addRemovePanel.remove(viewTests);

                        if ((validationJList == null) ||
                                (validationJList.getSelectedValue() == null)) {
                            viewTests.setEnabled(false);
                        } else if ((isDEL &&
                                DELSecurity.getInstance().canApproveElements())) {
                            if (DataElementStatus.PENDING.equals(
                                        ((ValidationRule) validationJList.getSelectedValue()).getStatus())) {
                                //View and approve
                                viewTests = new JButton(new ApproveValidationRuleTestsAction(
                                            thisDialog, validationJList,
                                            DELSecurity.getInstance()
                                                       .canEditElements()));
                                viewTests.setEnabled(true);
                            } else if (DELSecurity.getInstance()
                                                      .canEditElements()) {
                                //Edit tests
                                viewTests = new JButton(new ViewValidationRuleTestsAction(
                                            thisDialog, validationJList,
                                            validationMap, true));
                                viewTests.setEnabled(true);
                            } else {
                                //View tests
                                viewTests = new JButton(new ViewValidationRuleTestsAction(
                                            thisDialog, validationJList,
                                            validationMap));
                                viewTests.setEnabled(true);
                            }
                        } else if ((((ValidationRule) validationJList.getSelectedValue()).getTest() == null) &&
                                !(isDEL &&
                                ((ValidationRule) validationJList.getSelectedValue()).getIsEditable())) {
                            viewTests = new JButton(new ViewValidationRuleTestsAction(
                                        thisDialog, validationJList,
                                        validationMap));
                            viewTests.setEnabled(false);
                        } else {
                            viewTests = new JButton(new ViewValidationRuleTestsAction(
                                        thisDialog, validationJList,
                                        validationMap));
                            viewTests.setEnabled(true);
                        }

                        addRemovePanel.add(viewTests);
                        addRemovePanel.revalidate();
                        addRemovePanel.updateUI();
                        thisDialog.invalidate();
                        thisDialog.validate();
                        thisDialog.repaint();
                    }
                });
        }

        return addRemovePanel;
    }

    /**
     * The ok/cancel button panel
     * @return the configured ok/cancel button panel
     */
    private JPanel buildButtonPanel() {
        okButton = new JButton(PropertiesHelper.getStringFor(
                    "org.psygrid.datasetdesigner.ok"));
        okButton.addActionListener(this);
        cancelButton = new JButton(PropertiesHelper.getStringFor(
                    "org.psygrid.datasetdesigner.cancel"));
        cancelButton.addActionListener(this);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(okButton);
        
        if (!readOnly) {
            buttonPanel.add(cancelButton);
        }
        return buttonPanel;
    }

    /**
     * Save rules on ok, discard on cancel
     */
    public void actionPerformed(ActionEvent aet) {
        if (aet.getSource() == okButton) {
            if (activeDs != null) {
                ArrayList<ValidationRule> localRules = new ArrayList<ValidationRule>();
                for (ValidationRule rule : validationMap.keySet()) {
                    localRules.add((ValidationRule) rule);
                }
                activeDs.setValidationRules(localRules);
                
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
     *
     */
    public void refreshTable() {
        ArrayList<ValidationRule> listRules = new ArrayList<ValidationRule>(validationMap.size());

        for (ValidationRule rule : validationMap.keySet()) {
            listRules.add(rule);
        }

        validationJList.setModel(ListModelUtility.convertArrayListToListModel(
                listRules));

        if (isDEL) {
            table.setModel(new ValidationTableModel(validationMap));
            table.validate();
        }
    }

    /**
     *
     *
     * @return 
     */
    public MainFrame getFrame() {
        return frame;
    }

    /**
     *
     *
     * @return 
     */
    public boolean isDEL() {
        return isDEL;
    }

    /**
     *
     *
     * @return 
     */
    public Map<ValidationRule, Location> getValidationMap() {
        return validationMap;
    }

    private class ValidationTable extends JTable {
        public ValidationTable() {
        	//empty constructor;
        }

        public ValidationTable(TableModel tableModel) {
            super(tableModel);
        }

        public void changeSelection(int rowIndex, int columnIndex,
            boolean toggle, boolean extend) {
            super.changeSelection(rowIndex, columnIndex, toggle, extend);
            validationJList.setSelectedIndex(rowIndex);
        }
    }


    private class ValidationTableModel extends AbstractTableModel {
        final String[] columnNames = { "Validation Rule", "Status" };
        private ArrayList<ValidationRule> validations = new ArrayList<ValidationRule>();
        private ArrayList<Location> locations = new ArrayList<Location>();

        public ValidationTableModel(
            Map<ValidationRule, Location> validationList) {
            for (ValidationRule rule : validationList.keySet()) {
                validations.add(rule);
                locations.add(validationList.get(rule));
            }
        }

        public int getColumnCount() {
            return columnNames.length;
        }

        public int getRowCount() {
            return validations.size();
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col) {
            if (col == 1) {
                return locations.get(row);
            }

            return validations.get(row).getDescription();
        }

        /*
         * JTable uses this method to determine the default renderer
         * editor for each cell.  If we didn't implement this method,
         * then the last column would contain text ("true"/"false"),
         * rather than a check box.
         */
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }
    }
    public enum Location {New, Edited, Pending, Approved, Unknown;
    }
}
