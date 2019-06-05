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

import org.psygrid.datasetdesigner.ui.DocumentConfigurationDialog;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;

/**
 * Create new document
 * @author pwhelan
 */
public class NewDocumentAction extends AbstractAction
{
	/**
	 * Main window of the application
	 */
	private JFrame frame;
	
	/**
	 * Specifies whether the document is a part of the
	 * data element library view.
	 */
	private boolean isDEL = false;
	
	/**
	 * Create a new document for this dataset
	 * @param frame the main window of the application
	 * @param dataset the dataset to which the document will belong
	 * @param isDEL true if member of the DEL; false if not
	 */
	public NewDocumentAction(JFrame frame, boolean isDEL) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.newdocument"));
		this.frame = frame;
		this.isDEL = isDEL;
	}
	
	/**
	 * Show the new document configuration dialog
	 * @param e the calling action event
	 */
	public void actionPerformed(ActionEvent e) {
		new DocumentConfigurationDialog(frame, isDEL, true);
	}

	
	
}