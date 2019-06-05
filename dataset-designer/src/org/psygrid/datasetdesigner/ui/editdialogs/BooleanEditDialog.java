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
package org.psygrid.datasetdesigner.ui.editdialogs;

import javax.swing.JDialog;

import org.psygrid.data.model.hibernate.BooleanEntry;
import org.psygrid.data.model.hibernate.CompositeEntry;
import org.psygrid.data.model.hibernate.Document;
import org.psygrid.data.model.hibernate.Entry;
import org.psygrid.data.model.hibernate.EntryStatus;
import org.psygrid.datasetdesigner.controllers.DatasetController;
import org.psygrid.datasetdesigner.ui.MainFrame;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;

/**
 * Configuration dialog for a boolean entry
 * 
 * @author pwhelan
 */
public class BooleanEditDialog extends AbstractEditDialog {
	
	private BooleanEntry entry;

	private boolean isCompositeEntry = false;
	
	private CompositeEntry parentEntry = null;
	
	/**
	 * Constructor 
	 * @param frame the main window of the application
	 * @param entry the entry to configure
	 */
	public BooleanEditDialog(MainFrame frame, BooleanEntry entry, boolean isDEL, boolean canEdit) {
		super(frame, entry, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.configurebooleanentry"), isDEL, canEdit);
		this.entry = entry;
	}
		
		/**
	 * Constructor 
	 * @param frame the main window of the application
	 * @param entry the entry to configure
	 * @param entryContext the parent document
	 */
	public BooleanEditDialog(JDialog parent, BooleanEntry entry, Document entryContext, boolean isDEL, boolean canEdit) {
		super(parent, entry, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.viewbooleanentry"), entryContext, isDEL, canEdit);
		this.entry = entry;
	}
	
	public BooleanEditDialog(MainFrame frame, BooleanEntry entry, CompositeEntry parentEntry, boolean isComp, boolean isDEL) {
		super(frame, entry, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.configurebooleanentry"), false, true, true, isDEL, true);
		this.entry = entry;
		this.parentEntry = parentEntry;
		isCompositeEntry = true;
	}
	
	/**
	 * check if the boolean adheres to the required validaton rules
	 * - none to check here yet; maybe more to come later
	 */
	public boolean validateEntries() {
		return true;
	}

	/**
	 * When ok is pressed set the appropriate values
	 */
	public void ok() {
		if (entry != null) {
			if (fieldChanged(entry.getDisplayText(),getDisplayTextField().getText())) {
				changed = true;
				entry.setDisplayText(getDisplayTextField().getText());
			}
			if (fieldChanged(entry.getName(),getNameField().getText())) {
				changed = true;
				entry.setName(getNameField().getText());
			}
			
			if (!isCompositeEntry) {
				if (fieldChanged(entry.getEntryStatus().toString(),((EntryStatus)getEntryStatusComboBox().getSelectedItem()).toString())) {
					changed = true;
					entry.setEntryStatus((EntryStatus)getEntryStatusComboBox().getSelectedItem());	
				}
			}

			if (fieldChanged(entry.getLabel(),getLabelField().getText())) {
				changed = true;
				entry.setLabel(getLabelField().getText());	
			}
			if (fieldChanged(entry.getDescription(),getHelpField().getText())) {
				changed = true;
				entry.setDescription(getHelpField().getText());
			}

			//std codes not used by boolean entries
//			if (entry.isDisableStandardCodes() != getDisableStandardCodes().isSelected()) {
//				changed = true;
//				entry.setDisableStandardCodes(getDisableStandardCodes().isSelected());
//			}
			//Not used by the DEL
			entry.setExportSecurity(getExportSecurityBox().getSecurityValue());
		}
		
		if (isCompositeEntry) {
			boolean found = false;
			for (int i=0; i<parentEntry.numEntries(); i++) {
				Entry curEntry = parentEntry.getEntry(i);
				if (curEntry.equals(entry)) {
					found = true;
				}
			}
			if (!found) {
				parentEntry.addEntry(entry);
			}
			if (changed) {
				((Document)DatasetController.getInstance().getActiveDocument()).setIsRevisionCandidate(true);
				((Entry)parentEntry).setIsRevisionCandidate(true);
				((Entry)entry).setIsRevisionCandidate(true);
			}
		}
		
	}
	
}

