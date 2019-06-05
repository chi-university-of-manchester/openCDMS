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

import org.psygrid.data.model.hibernate.Document;
import org.psygrid.data.model.hibernate.Entry;
import org.psygrid.datasetdesigner.controllers.DatasetController;
import org.psygrid.datasetdesigner.model.DummyDocument;
import org.psygrid.datasetdesigner.ui.DocumentPanel;
import org.psygrid.datasetdesigner.ui.MainFrame;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;

import org.psygrid.datasetdesigner.utils.Utils;

/**
 * Class to display the relevant entry configuration dialog
 * for individual entries
 * @author pwhelan
 */
public class EditEntryAction extends AbstractAction{
	
	/**
	 * The main window the application
	 */
	private MainFrame frame;
	
	/**
	 * The entry to configure
	 */
	private Entry entry;
	
	/**
	 * The panel to which this entry belongs
	 */
	private DocumentPanel docPanel;
	
	/**
	 * Specifies whether the document is part of the data element library view.
	 */
	private boolean isDEL;
	
	/**
	 * Specifies whether the entry properties can be edited
	 */
	private boolean canEdit;
	
	/**
	 * Constructro
	 * @param frame parent window
	 * @param entry the entry to edit
	 * @param docPanel the panel to which this entry belongs
	 * @param isDEL true if entry is part of DEL; false if not
	 */
	public EditEntryAction(MainFrame frame, Entry entry, DocumentPanel docPanel, boolean isDEL) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.properties"));
		this.entry = entry;
		this.frame = frame;
		this.docPanel = docPanel;
		this.isDEL = isDEL;
		this.canEdit = false;
	}
	
	/**
	 * Constructor
	 * @param frame the main window the application
	 * @param entry the entry to configure
	 * @param docPanel the panel to which this entry belongs
	 * @param isDEL true if entry belongs to the DEL; false if not
	 * @param canEdit ture if entry can be edited (in DEL); false if not
	 */
	public EditEntryAction(MainFrame frame, Entry entry, DocumentPanel docPanel, boolean isDEL, boolean canEdit) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.edit"));
		this.entry = entry;
		this.frame = frame;
		this.docPanel = docPanel;
		this.isDEL = isDEL;
		this.canEdit = canEdit;
	}
	
	public void actionPerformed(ActionEvent aet) {
		docPanel.refresh(Utils.getSectionForEntry(docPanel.getDocument(), entry));
		showEntryEditDialog(entry);
	}

	/**
	 * Display the relevant entry configuraiton dialog for a particular entry
	 * @param entry the entry to configure
	 */
	public void showEntryEditDialog(Entry entry) {
		Document doc = (Document)DatasetController.getInstance().getActiveDocument();
		if (!doc.getIsEditable() 
				&& !(doc instanceof DummyDocument)) {
			canEdit = false;
		}
		else if (!((Entry)entry).getIsEditable()){
			//Entry is not editable 
			canEdit = false;
		}
		Utils.showEntryDialog(entry, isDEL, canEdit, docPanel, frame);
	}
	
}
