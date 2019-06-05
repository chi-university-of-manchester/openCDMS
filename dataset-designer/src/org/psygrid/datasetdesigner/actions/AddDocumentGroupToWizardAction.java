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

import org.psygrid.data.model.hibernate.DocumentGroup;
import org.psygrid.datasetdesigner.ui.wizard.dialogs.AddDocumentGroupToWizardDialog;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;


/**
 * Action to enable adding of document group
 * within the wizard
 * @author pwhelan
 */
public class AddDocumentGroupToWizardAction extends AbstractAction {
	
	//list of existing document groups
	private JList documentGroupsList;
	
	//owner dialog
	private JDialog parentDialog;
	
	//true if editing a document group; false if not
	private boolean edit = false;
	
	/**
	 * Constructor - create the action
	 * @param parentDialog - the owner dialog
	 * @param documentGroupsList the list of existing doc groups
	 */
	public AddDocumentGroupToWizardAction(JDialog parentDialog,
								  JList documentGroupsList) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.add"));
		this.parentDialog = parentDialog;
		this.documentGroupsList = documentGroupsList;
	}
	
	/**
	 * Constructor - create the action
	 * @param parentDialog - the owner dialog
	 * @param documentGroupsList the list of existing doc groups
	 * @param edit true if editing doc group; false if not
	 */
	public AddDocumentGroupToWizardAction(JDialog parentDialog,
										  JList documentGroupsList, 
										  boolean edit) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.edit"));
		this.parentDialog = parentDialog;
		this.documentGroupsList = documentGroupsList;
		this.edit = edit;
	}
	
	/**
	 * Action Event 
	 * @param aet Show the add document group wizard dialog
	 */
	public void actionPerformed(ActionEvent aet) {
		
		if (edit) {
			new AddDocumentGroupToWizardDialog(parentDialog, 
										documentGroupsList,
										(DocumentGroup)documentGroupsList.getSelectedValue());
		} else {
			new AddDocumentGroupToWizardDialog(parentDialog, documentGroupsList);
		}
	}
	
}