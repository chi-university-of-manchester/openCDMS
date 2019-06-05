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
import javax.swing.JFrame;

import org.psygrid.data.model.hibernate.Document;
import org.psygrid.datasetdesigner.ui.DocumentConfigurationDialog;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;

/**
 * Action that deletes a group from the project for the specified user.
 * @author pwhelan
 */
public class ConfigureDocumentPropertiesAction extends AbstractAction {

	private Document document;
	private JFrame frame;
	
	/**
	 * Specifies whether the document is part of the data element library view.
	 */
	private boolean isDEL;
	private boolean edit;
	
	public ConfigureDocumentPropertiesAction(JFrame frame, Document document, boolean isDEL, boolean edit) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.properties"));
		this.document = document;
		this.frame = frame;
		this.isDEL = isDEL;
		this.edit = edit;
	}
	
	public void actionPerformed(ActionEvent aet) {
		new DocumentConfigurationDialog(frame, document, isDEL, edit);
	}
	
	
}
