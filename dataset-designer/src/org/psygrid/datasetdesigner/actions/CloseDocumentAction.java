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
import javax.swing.JOptionPane;

import org.psygrid.data.model.hibernate.Document;
import org.psygrid.datasetdesigner.model.DocTreeModel;
import org.psygrid.datasetdesigner.model.DummyDocument;

/**
 * Action that deletes a document locally from the DEL project.
 * 
 * @author Lucy Bridges
 */
public class CloseDocumentAction extends AbstractAction {

	private Document document;
	private JFrame frame;
	
	private String message;
	
	public CloseDocumentAction(JFrame frame, Document document) {
		super("Close document");
		this.document = document;
		this.frame = frame;
		this.message = "Are you sure you want to close this document?";
	}
	
	public CloseDocumentAction(JFrame frame, DummyDocument document) {
		super("Close Entry");
		this.document = document;
		this.frame = frame;
		this.message = "Are you sure you want to close this entry?";
	}
	
	public void actionPerformed(ActionEvent aet) {
		int n = JOptionPane.showConfirmDialog(
                frame, message,
                "Confirm Close",
                JOptionPane.YES_NO_OPTION);
        if (n == JOptionPane.YES_OPTION) {
    		DocTreeModel.getInstance().removeDocument(document);
        } else if (n == JOptionPane.NO_OPTION) {
          //nothing then  
        } 

		
	}
	
	
}
