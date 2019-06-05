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

import org.psygrid.www.xml.security.core.types.RoleType;

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
 * The configuration dialog to assign roles to a dataset
 *
 * @author pwhelan
  */
public class ConfigureRolesDialog extends JDialog implements ActionListener {
    /**
     * Ok Button; save changes 
     */
    private JButton okButton;

    /**
     * Cancel button; dismiss changes
     */
    private JButton cancelButton;

    /**
     * Assign roles to a dataset button
     */
    private JButton assignButton;

    /**
     * Unassign roles from a dataset button
     */
    private JButton unassignButton;

    /**
     * List of available roles to choose from 
     */
    private JList availableRolesList;

    /**
     * List of assigned roles to choose from
     */
    private JList assignedRolesList;

    /**
     * The currently active dataset
     */
    private StudyDataSet activeDs;
    
    /**
     * Read only attribute for the roles dialog
     */
    private boolean readOnly;

    /**
     * Constructor Creates a new ConfigureRolesDialog object
     *
     * @param frame the main window of the application
     */
    public ConfigureRolesDialog(MainFrame frame) {
    	this(frame, false);
    }

    /**
     * Constructor Creates a new ConfigureRolesDialog object
     *
     * @param frame the main window of the application
     */
    public ConfigureRolesDialog(MainFrame frame, boolean readOnly) {
        super(frame);
        if (readOnly) {
        	setTitle(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.viewRoles"));
        } else {
            setTitle(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.configureRoles"));
        }
        setModal(true);
        this.readOnly = readOnly;
        
        //set read only flag if dataset is published
        if (DatasetController.getInstance().getActiveDs().getDs().isPublished()) {
        	readOnly = true;
        }

        
        activeDs = DatasetController.getInstance().getActiveDs();
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(Utils.buildDsHeaderPanel(), BorderLayout.NORTH);
        getContentPane().add(buildMainPanel(), BorderLayout.CENTER);
        getContentPane().add(buildButtonPanel(), BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(null);
        init();
        setVisible(true);
    }
    
    /**
     * Populate the two list models with the dataset information
     * All Roles list = default list - assigned roles
     */
    private void init() {
		ArrayList<RoleType> allRoles = DefaultDSSettings.getAllRoles();
		ArrayList<RoleType> dsRoles = new ArrayList<RoleType>();
 		
    	if (activeDs != null) {
    		dsRoles = activeDs.getRoles();
     	}
    	
    	ArrayList<RoleType> removeList = new ArrayList<RoleType>();
    	for (RoleType r: dsRoles) {
    		for (RoleType ar: allRoles) {
    			//compare by name; otherwise can be different
    			//for saved roles
    			if (r.getName().equals(ar.getName())){
    				removeList.add(ar);
    			}
    		}
    	}
    	
    	//remove any that matched up
    	allRoles.removeAll(removeList);
    	
    	availableRolesList.setModel(ListModelUtility.convertArrayListToListModel(allRoles));
    	assignedRolesList.setModel(ListModelUtility.convertArrayListToListModel(dsRoles));
    	    	
    	if (readOnly) {
    		assignButton.setEnabled(false);
    		unassignButton.setEnabled(false);
    	}
    }

    /**
     * Build the button panel containing the ok and cancel buttons
     * @return the configured button panel
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
        
        if (! readOnly) {
            buttonPanel.add(cancelButton);
        }
        return buttonPanel;
    }

    /**
     *
     *
     * @return 
     */
    private JPanel buildMainPanel() {
        JPanel rolePanel = new JPanel();
        rolePanel.setLayout(new BoxLayout(rolePanel,
                BoxLayout.X_AXIS));
        availableRolesList = new CustomCopyPasteJList();
        availableRolesList.setCellRenderer(new OptionListCellRenderer());
        assignedRolesList = new CustomCopyPasteJList();
        assignedRolesList.setCellRenderer(new OptionListCellRenderer());
        assignButton = new JButton(new AssignAction(
                    availableRolesList, assignedRolesList));
        unassignButton = new JButton(new UnassignAction(
                    availableRolesList, assignedRolesList));
        rolePanel.add(Utils.createSubPanel(PropertiesHelper.getStringFor(
                    "org.psygrid.datasetdesigner.ui.availableroles"),
                availableRolesList));
        rolePanel.add(Utils.createArrowPanel(unassignButton, assignButton));
        rolePanel.add(Utils.createSubPanel(PropertiesHelper.getStringFor(
                    "org.psygrid.datasetdesigner.ui.assignedroles"),
                assignedRolesList));
        rolePanel.setBorder(BorderFactory.createTitledBorder(
                PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.configureroles")));
        return rolePanel;
    }

    /**
     * Action event occurred
     * @param aet the trigger action event
     */
    public void actionPerformed(ActionEvent aet) {
        if (aet.getSource() == okButton) {
        	if (!readOnly) {
            	if (activeDs != null) {
            		activeDs.setRoles(ListModelUtility.convertListModelToConsentRoleTypeList(((DefaultListModel)assignedRolesList.getModel())));
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
}
