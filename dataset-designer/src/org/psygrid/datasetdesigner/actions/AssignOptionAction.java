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

import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.DefaultListModel;
import javax.swing.table.DefaultTableModel;

import org.psygrid.datasetdesigner.utils.IconsHelper;

import org.psygrid.data.model.hibernate.EntryStatus;

public class AssignOptionAction extends AbstractAction {
	
	public final static int OPTION = 1;
	public final static int  VARIABLE = 2;
	
	private JList leftList;
	private JTable rightList;
	private int type;
	
	public AssignOptionAction(JList leftList, JTable rightList, int type) {
    	super("", IconsHelper.getInstance().getImageIcon("Forward16.png"));
    	this.leftList = leftList;
    	this.rightList = rightList;
    	this.type = type;
	}	    
	
	public AssignOptionAction(JList leftList, JTable rightList) {
    	super("", IconsHelper.getInstance().getImageIcon("Forward16.png"));
    	this.leftList = leftList;
    	this.rightList = rightList;
    	//default is option type - was originally an option action!
    	type = OPTION;
	}
	
	public void actionPerformed(ActionEvent aet) {
		if (leftList.getSelectedValue() != null) {
			Object selectedLeft = leftList.getSelectedValue();
			Vector newRow = new Vector();
			newRow.add(selectedLeft);
			
			if (type == OPTION) {
				newRow.add(EntryStatus.DISABLED);
			} else if (type == VARIABLE) {
				newRow.add("");
				newRow.add("");
			}
			
			((DefaultTableModel)rightList.getModel()).addRow(newRow);
			rightList.revalidate();
			rightList.repaint();

			((DefaultListModel)leftList.getModel()).removeElement(selectedLeft);
		}
	}

	
}