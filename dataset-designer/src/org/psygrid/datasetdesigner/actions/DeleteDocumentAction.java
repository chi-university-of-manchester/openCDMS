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
import javax.swing.JOptionPane;

import org.psygrid.data.model.hibernate.Document;
import org.psygrid.datasetdesigner.controllers.DatasetController;
import org.psygrid.datasetdesigner.model.DocTreeModel;
import org.psygrid.datasetdesigner.model.DummyDocument;
import org.psygrid.datasetdesigner.ui.DocumentPanel;
import org.psygrid.datasetdesigner.ui.MainFrame;
import org.psygrid.datasetdesigner.ui.configurationdialogs.ProvenanceDialog;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;

/**
 * Action that deletes a group from the project for the specified user.
 * @author pwhelan
 */
public class DeleteDocumentAction extends AbstractAction {

	private Document document;
	private MainFrame frame;
	
	private String message;
	
	public DeleteDocumentAction(MainFrame frame, Document document) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.deletedocument"));
		this.document = document;
		this.frame = frame;
		this.message = "Are you sure you want to delete the document?";
	}
	
	public DeleteDocumentAction(MainFrame frame, Document document, String title) {
		super(title);
		this.document = document;
		this.frame = frame;
		this.message = "Are you sure you want to delete the document?";	//Used to delete DEL documents
	}
	
	public DeleteDocumentAction(MainFrame frame, DummyDocument document, String title) {
		super(title);
		this.document = document;
		this.frame = frame;
		this.message = "Are you sure you want to delete this entry?";	//Used to delete 'dummy' documents
	}
	
	public void actionPerformed(ActionEvent aet) {
		int n = JOptionPane.showConfirmDialog(
                frame, message,
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
        if (n == JOptionPane.YES_OPTION) {
        	DocumentPanel panel = frame.getDocPane().getPanelForDocument(document);
        	if (panel != null) {
        		frame.getDocPane().closeTab(panel.getDocument());
        	}
    		DocTreeModel.getInstance().removeDocument(document);
    		
			//if it's a published study, every change must be put in the provenance log
			if (DatasetController.getInstance().getActiveDs().getDs().isPublished()) {
				new ProvenanceDialog(frame, document);
			} 
        } 
	}
	
	
}
