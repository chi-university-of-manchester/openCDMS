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
import javax.swing.DefaultListModel;

import javax.swing.JComboBox;
import javax.swing.JList;

import org.psygrid.data.model.hibernate.Status;
import org.psygrid.datasetdesigner.utils.IconsHelper;

public class UnassignTransitionAction extends AbstractAction {

	private JList leftList;
	private JList rightList;
	private JComboBox statusBox;
	
	public UnassignTransitionAction(JList leftList, 
									JList rightList,
									JComboBox statusBox) {
		super("", IconsHelper.getInstance().getImageIcon("Back16.png"));
    	this.leftList = leftList;
    	this.rightList = rightList;
    	this.statusBox = statusBox;
	}
	
	public void actionPerformed(ActionEvent aet) {
		Object[] selections = rightList.getSelectedValues();
		for (int j=0; j<selections.length; j++) {
			Status selectedAssign = (Status)selections[j];
			Status selectedStatus = (Status)statusBox.getSelectedItem();
			
			for (int i=0; i<selectedStatus.numStatusTransitions(); i++) {
				if ((selectedStatus.getStatusTransition(i).getShortName()).equals(selectedAssign.getShortName())) {
					selectedStatus.removeStatusTransition(i);
				}
			}

			((DefaultListModel)leftList.getModel()).addElement(selectedAssign);
			((DefaultListModel)rightList.getModel()).removeElement(selectedAssign);
		}
	}
	
	
}