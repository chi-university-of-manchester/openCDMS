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
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JTable;

import org.psygrid.data.model.hibernate.Status;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.model.StatusTableModel;

public class RemoveStatusAction extends AbstractAction {
	
	private JTable statusTable;
	
	private JList availableTransitionsList;
	private JList assignedTransitionsList;
	private JComboBox stateBox;
	
	public RemoveStatusAction(JTable statusTable,
							  JList availableTransitionsList,
							  JList assignedTransitionsList,
							  JComboBox stateBox) {						  
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.remove"));
		this.statusTable = statusTable;
		this.availableTransitionsList = availableTransitionsList;
		this.assignedTransitionsList = assignedTransitionsList;
		this.stateBox = stateBox;
	}
	
	public void actionPerformed(ActionEvent aet) {
		Object selection = ((StatusTableModel)statusTable.getModel()).getStatusAt(statusTable.getSelectedRow());
		((StatusTableModel)statusTable.getModel()).removeStatus((Status)selection);
		((DefaultListModel)availableTransitionsList.getModel()).removeElement(selection);
		((DefaultListModel)assignedTransitionsList.getModel()).removeElement(selection);
		((DefaultComboBoxModel)stateBox.getModel()).removeElement(selection);
		
		//iterate all the transitions and remove any that reference this removed one
		for (int j=0; j<statusTable.getModel().getRowCount(); j++) {
			ArrayList<Status> transToRemove = new ArrayList<Status>();
			Status selStatus = (Status)selection;
			Status curInListStatus = ((StatusTableModel)statusTable.getModel()).getStatusAt(j);
			for (int i=0; i<curInListStatus.numStatusTransitions(); i++) {
				Status curSelStatusTransition = curInListStatus.getStatusTransition(i);
				if (selStatus.equals(curSelStatusTransition)) {
					//DefaultListModel statusModel = (DefaultListModel)unitsList.getModel();
					ArrayList<Status> statuses = ((StatusTableModel)statusTable.getModel()).getAllStatuses();
					
					//if (!(ListModelUtility.convertListModelToIStatusList(statusModel)).contains(curSelStatusTransition)) {
					if (!statuses.contains(curSelStatusTransition)) {
						transToRemove.add(curSelStatusTransition);
						continue;
					}
				}
			}

			//then clean up redundant transitions
			for (int n=0; n<transToRemove.size(); n++) {
				Status curRemove = transToRemove.get(n);
				for (int z=0; z<curInListStatus.numStatusTransitions(); z++) {
					if (curInListStatus.getStatusTransition(z).equals(curRemove)) {
						curInListStatus.removeStatusTransition(z);
					}
				}
			}
		}
	}
	
}