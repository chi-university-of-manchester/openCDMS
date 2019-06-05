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
package org.psygrid.datasetdesigner.actions;

import java.awt.event.ActionEvent;

import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JList;

import org.psygrid.datasetdesigner.model.GroupModel;

import org.psygrid.datasetdesigner.ui.configurationdialogs.AddGroupDialog;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;

/**
 * Action to add a group to a dataset 
 * Display the add group dialog
 * @author pwhelan
 *
 */
public class AddGroupAction extends AbstractAction {
	
	//the list of existing  groups
	private JList groupsList;
	
	//the owner dialog
	private JDialog parentDialog;

	//true if the document group is being edited; false if not
	private boolean edit;
	
	/**
	 * Constructor
	 * @param parentDialog the owner dialog
	 * @param datasetBox the dataset selector
	 * @param groupsList
	 * @param groupsMap
	 */
	public AddGroupAction(JDialog parentDialog,
						  JList groupsList) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.add"));
		this.groupsList = groupsList;
		this.parentDialog = parentDialog;
	}
	
	/**
	 * Constructor
	 * @param parentDialog the owner dialog
	 * @param datasetBox the dataset selector
	 * @param groupsList
	 * @param groupsMap
	 */
	public AddGroupAction(JDialog parentDialog,
						  JList groupsList, 
						  boolean edit) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.edit"));
		this.groupsList = groupsList;
		this.edit = edit;
		this.parentDialog = parentDialog;
	}
	
	/**
	 * Show the document group dialog
	 * @param aet the calling action event
	 */
	public void actionPerformed(ActionEvent aet) {
		if (edit) {
			new AddGroupDialog(parentDialog,
							   groupsList, 
							   ((GroupModel)groupsList.getSelectedValue()));
		} else {
			new AddGroupDialog(parentDialog,
					           groupsList);
		}
	}
	
}