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

import org.psygrid.data.model.hibernate.Transformer;
import org.psygrid.datasetdesigner.ui.configurationdialogs.AddTransformerDialog;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;

public class ViewTransformerAction extends AbstractAction {
	
	private JList transformersList;
	
	private JDialog parentDialog;
	
	public ViewTransformerAction(JDialog parentDialog, JList transformersList) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.view"));
		this.transformersList = transformersList;
		this.parentDialog = parentDialog;
	}
	
	public void actionPerformed(ActionEvent aet) {
			new AddTransformerDialog(parentDialog, 
									 transformersList, 
									 (Transformer)transformersList.getSelectedValue(), true);
	}
	
}
