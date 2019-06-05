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

import org.psygrid.data.model.hibernate.Document;
import org.psygrid.datasetdesigner.ui.wizard.dialogs.AddDocumentToWizardDialog;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;

import org.psygrid.datasetdesigner.ui.wizard.WizardModel;

/**
 * Action to show the add document dialog in the setup wizard
 * @author pwhelan
 */
public class AddDocumentToWizardAction extends AbstractAction {
	
	//list of existing documents
	private JList docList;
	
	//the owner dialog
	private JDialog parentDialog;
	
	//true if document is being edited; false if not
	private boolean edit = false;
	
	//the model for the entire wizard
	private WizardModel wm;
	
	/**
	 * Constructor 
	 * @param parentDialog the owner dialog
	 * @param docGroupsList the list of existing documents
	 * @param wm the model for the overall wizard
	 */
	public AddDocumentToWizardAction(JDialog parentDialog,
								  JList docGroupsList,
								  WizardModel wm) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.add"));
		this.parentDialog = parentDialog;
		this.docList = docGroupsList;
		this.wm = wm;
	}
	
	/**
	 * Constructor 
	 * @param parentDialog the owner dialog
	 * @param docGroupsList the list of existing documents
	 * @param wm the model for the overall wizard
	 * @param edit true if document is being edited; false if not
	 */
	public AddDocumentToWizardAction(JDialog parentDialog,
										  JList documentGroupsList, 
										  WizardModel wm,
										  boolean edit) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.edit"));
		this.parentDialog = parentDialog;
		this.docList = documentGroupsList;
		this.wm = wm;
		this.edit = edit;
	}
	
	/**
	 * Show the add document dialog
	 * @param aet
	 */
	public void actionPerformed(ActionEvent aet) {
		if (edit) {
			new AddDocumentToWizardDialog(parentDialog, 
										docList,
										(Document)docList.getSelectedValue(),
										wm);
		} else {
			new AddDocumentToWizardDialog(parentDialog, docList, wm);
		}
	}
	
}