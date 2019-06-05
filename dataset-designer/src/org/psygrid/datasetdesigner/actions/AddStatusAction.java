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
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JTable;

import org.psygrid.data.model.hibernate.Status;
import org.psygrid.datasetdesigner.ui.configurationdialogs.AddStatusDialog;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.model.StatusTableModel;

/**
 * Add status action;
 * allows a status to be added to the statuses table 
 * 
 * @author pwhelan
 */
public class AddStatusAction extends AbstractAction {
	
	private JTable statusTable;
	
	private JList availableTransitionsList;
	private JList assignedTransitionsList;
	private JComboBox stateBox;
	
	private boolean edit;
	
	private JDialog parentDialog;
	
	public AddStatusAction(JDialog parentDialog, 
						   JTable statusTable, 
						   JList availableTransitionsList,
						   JList assignedTransitionsList,
						   JComboBox stateBox) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.add"));
		this.parentDialog = parentDialog;
		this.statusTable = statusTable;
		this.availableTransitionsList = availableTransitionsList;
		this.assignedTransitionsList = assignedTransitionsList;
		this.stateBox = stateBox;
	
	}
	
	public AddStatusAction(JDialog parentDialog, 
						   JTable statusTable,
						   boolean edit,
						   JList availableTransitionsList,
						   JList assignedTransitionsList,
						   JComboBox stateBox) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.edit"));
		this.parentDialog = parentDialog;
		this.statusTable = statusTable;
		this.edit=edit;
		this.availableTransitionsList = availableTransitionsList;
		this.assignedTransitionsList = assignedTransitionsList;
		this.stateBox = stateBox;
		
	}
	
	public void actionPerformed(ActionEvent aet) {
		if (edit) {
			new AddStatusDialog(parentDialog,
									 statusTable, 
									 (Status)((StatusTableModel)statusTable.getModel()).getStatusAt(statusTable.getSelectedRow()),
									 availableTransitionsList,
									 assignedTransitionsList,
									 stateBox);
		} else {
			new AddStatusDialog(parentDialog, 
								statusTable, 
								availableTransitionsList,
								assignedTransitionsList,
								stateBox
								);
		}
	}
	
}
