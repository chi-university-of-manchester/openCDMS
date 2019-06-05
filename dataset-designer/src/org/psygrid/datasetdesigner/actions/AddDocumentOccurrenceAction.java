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

import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.DefaultListModel;

import org.psygrid.datasetdesigner.model.DSDocumentOccurrence;

import org.psygrid.datasetdesigner.ui.configurationdialogs.AddDocumentOccurrenceDialog;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;


/**
 * Add a document occurrence action
 * @author pwhelan
 */
public class AddDocumentOccurrenceAction extends AbstractAction {
	
	//dataset selector drop-down box
	private JComboBox datasetBox;
	
	//list of existing document occurrences
	private JList docOccList;
	
	//map of existing document occurrences to datasets
	private HashMap docOccMap;
	
	//owner parent dialog
	private JDialog parentDialog;
	
	//true if document occurrence is being edited; false if not
	private boolean edit = false;
	
	/**
	 * Constructor 
	 * @param parentDialog owner parent dialog
	 * @param datasetBox dataset selector drop-down
	 * @param docOccList list of existing document occurrences list
	 * @param docOccMap map of existing document occurrences
	 */
	public AddDocumentOccurrenceAction(JDialog parentDialog,
			JComboBox datasetBox, JList docOccList, HashMap docOccMap) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.add"));
		this.parentDialog = parentDialog;
		this.datasetBox = datasetBox;
		this.docOccList = docOccList;
		this.docOccMap = docOccMap;
	}
	
	/**
	 * Constructor 
	 * @param parentDialog owner parent dialog
	 * @param datasetBox dataset selector drop-down
	 * @param docOccList list of existing document occurrences list
	 * @param docOccMap map of existing document occurrences
	 * @param edit true if document occurrence is being edited; false if not
	 */
	public AddDocumentOccurrenceAction(JDialog parentDialog, 
									   JComboBox datasetBox, 
									   JList docOccList,
									   HashMap docOccMap,
									   boolean edit) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.edit"));
		this.parentDialog = parentDialog;
		this.datasetBox = datasetBox;
		this.docOccList = docOccList;
		this.docOccMap = docOccMap;
		this.edit = edit;
	}
	
	/**
	 * Show the dialog
	 * @param aet the calling action event
	 */
	public void actionPerformed(ActionEvent aet) {
		if (edit) {
			new AddDocumentOccurrenceDialog(parentDialog, datasetBox, docOccList, docOccMap, ((DSDocumentOccurrence)docOccList.getSelectedValue())).setVisible(true);
		} else {
			new AddDocumentOccurrenceDialog(parentDialog, datasetBox, docOccList, docOccMap).setVisible(true);
		}
	}
	
}
