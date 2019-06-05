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

import java.awt.FlowLayout;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.psygrid.datasetdesigner.actions.AddGroupAction;
import org.psygrid.datasetdesigner.ui.MainFrame;
import org.psygrid.datasetdesigner.utils.ListModelUtility;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;

import org.psygrid.datasetdesigner.controllers.DatasetController;

/**
 * Display the configuration groups dialog to 
 * add/edit and remove groups
 * @author pwhelan
 *
 */
public class ConfigureGroupsDialog extends AbstractConfigurationDialog{

	/**
	 * Constructor
	 * @param frame the main window of the application
	 */
	public ConfigureGroupsDialog(MainFrame frame) {
		super(frame, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.configuregroups"), false, false, false,PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.projectgroupsmgmt"));
	}

	/**
	 * Constructor
	 * @param frame the main window of the application
	 */
	public ConfigureGroupsDialog(MainFrame frame, boolean viewOnly, boolean readOnly) {
		super(frame, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.configuregroups"), viewOnly, readOnly, false, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.projectgroupsmgmt"));
	}
	
	/**
	 * Initialise the list group model
	 */
	protected void init() {
		if (activeDs != null) {
			list.setModel(ListModelUtility.convertArrayListToListModel(activeDs.getGroups()));
		}
	}

	/**
	 * Initialise the add and edit group buttons
	 */
	protected void initButtons() {
		super.initButtons();
		addButton = new JButton(new AddGroupAction(this, list));
		editButton = new JButton(new AddGroupAction(this, list, true));

		//cannot add a centre to a randomized study!!!!
		if (DatasetController.getInstance().getActiveDs().getDs().isPublished()
				&& DatasetController.getInstance().getActiveDs().getDs().isRandomizationRequired()) {
			addButton.setEnabled(false);
		}
	}

	protected void save() {
		if (activeDs != null) {
			activeDs.setGroups(ListModelUtility.convertListModelToGroupModelList((DefaultListModel)list.getModel()));
		}
	}
	
	/**
	 * Override AddRemove - you can edit groups in patch mode
	 */
    /**
     * Build a button panel with add/remove/edit buttons for the units
     * @return the configured add/remove/edit button panel
     */
    protected JPanel buildAddRemovePanel() {
        JPanel addRemovePanel = new JPanel();
        addRemovePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        
        //if readonly don't add any buttons
        if (readOnly){
        	return addRemovePanel;
        }
        
        if (viewOnly) {
            editButton.setEnabled(false);
            addRemovePanel.add(editButton);
        } else {
            addRemovePanel.add(addButton);
            editButton.setEnabled(false);
            addRemovePanel.add(editButton);
        }

        removeButton.setEnabled(false);
        addRemovePanel.add(removeButton);

        //Only enable the buttons if an item is selected
        list.addListSelectionListener(new ListSelectionListener() {
               	public void valueChanged(ListSelectionEvent event) {
                    boolean enabled = true;

                    if ((list == null) || (list.getSelectedValue() == null)) {
                        enabled = false;
                    }

                    if (removeButton != null && !activeDs.getDs().isPublished()) {
                        removeButton.setEnabled(enabled);
                    }
                    
                    //can edit the group name if the study is published but not randomized
                    if (editButton != null && !(activeDs.getDs().isPublished() && activeDs.getDs().isRandomizationRequired())) {
                        editButton.setEnabled(enabled);
                    }
                }
            });

        return addRemovePanel;
    }

}