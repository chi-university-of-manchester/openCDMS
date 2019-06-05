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

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JList;

import org.psygrid.data.model.hibernate.Option;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;

import org.psygrid.datasetdesigner.ui.configurationdialogs.AddOptionDialog;

/**
 * Action to add an option to an entry
 * @author pwhelan
 *
 */
public class AddOptionAction extends AbstractAction {
	
	//the list of existing options
	private JList optionsList;

	//true if editing an option; false if not
	private boolean edit;
	
	//the owner dialog
	private JDialog parentDialog;
	
	/**
	 * Add an option the existing list
	 * @param parentDialog the owner dialog
	 * @param optionsList the list of existing options
	 */
	public AddOptionAction(JDialog parentDialog,
						   JList optionsList) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.add"));
		this.optionsList = optionsList;
		this.parentDialog = parentDialog;
	}
	
	/**
	 * Add an option the existing list
	 * @param parentDialog the owner dialog
	 * @param optionsList the list of existing options
	 * @param edit the option to edit
	 */
	public AddOptionAction(JDialog parentDialog,
						   JList optionsList,
						   boolean edit) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.edit"));
		this.optionsList = optionsList;
		this.edit = edit;
		this.parentDialog = parentDialog;
	}
	
	/**
	 * Show the add option dialog
	 * @param aet the calling action event
	 */
	public void actionPerformed(ActionEvent aet) {
		if (edit) {
			new AddOptionDialog(parentDialog, optionsList, (Option)optionsList.getSelectedValue());
		} else {
			new AddOptionDialog(parentDialog, optionsList);
		}
	}
	
	
}