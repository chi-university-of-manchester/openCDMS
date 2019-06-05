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
import javax.swing.JList;

import org.psygrid.data.model.hibernate.DocumentGroup;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;

public class RemoveDocumentGroupFromWizardAction extends AbstractAction {

	private JList documentGroupList;
	
	public RemoveDocumentGroupFromWizardAction(JList documentGroupList) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.remove"));
		this.documentGroupList = documentGroupList;
	}

	public void actionPerformed(ActionEvent e) {
		Object[] docGroups = documentGroupList.getSelectedValues();
		
		for (int i=0; i<docGroups.length; i++) {
			DocumentGroup docGroup = (DocumentGroup)docGroups[i];
			((DefaultListModel)documentGroupList.getModel()).removeElement(docGroup);
		}
	}
	
}
