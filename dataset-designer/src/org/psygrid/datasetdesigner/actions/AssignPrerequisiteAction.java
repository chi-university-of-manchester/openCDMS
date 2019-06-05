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
import javax.swing.JList;
import javax.swing.DefaultListModel;

import org.psygrid.datasetdesigner.utils.IconsHelper;

public class AssignPrerequisiteAction extends AbstractAction {
	
	private JList leftList;
	private JList rightList;
	
	public AssignPrerequisiteAction(JList leftList, JList rightList) {
    	super("", IconsHelper.getInstance().getImageIcon("Forward16.png"));
    	this.leftList = leftList;
    	this.rightList = rightList;
	}
	
	public void actionPerformed(ActionEvent aet) {
		Object[] selections = leftList.getSelectedValues();
		for (int i=0; i<selections.length; i++) {
			Object selectedLeft = selections[i];
			((DefaultListModel)rightList.getModel()).addElement(selectedLeft);
			rightList.revalidate();
			rightList.repaint();
			((DefaultListModel)leftList.getModel()).removeElement(selectedLeft);
		}
	}
	
}
