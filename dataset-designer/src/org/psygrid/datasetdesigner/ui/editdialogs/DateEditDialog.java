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

import org.psygrid.data.model.hibernate.CompositeEntry;
import org.psygrid.data.model.hibernate.DateEntry;
import org.psygrid.data.model.hibernate.Document;
import org.psygrid.data.model.hibernate.Entry;
import org.psygrid.data.model.hibernate.EntryStatus;
import org.psygrid.datasetdesigner.controllers.DatasetController;
import org.psygrid.datasetdesigner.ui.MainFrame;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;

public class DateEditDialog extends AbstractEditDialog {
	
	private DateEntry entry;

	private boolean isCompositeEntry = false;
	
	private CompositeEntry parentEntry = null;

	public DateEditDialog(MainFrame frame, DateEntry entry, boolean isDEL, boolean canEdit) {
		super(frame, entry, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.configuredateentry"), isDEL, canEdit);
		this.entry = entry;
	}
	
	public DateEditDialog(MainFrame frame, DateEntry entry, CompositeEntry parentEntry, boolean isComp, boolean isDEL) {
		super(frame, entry, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.configuredateentry"), false, true, true, isDEL, true);
		this.entry = entry;
		this.parentEntry = parentEntry;
		isCompositeEntry = true;
	}

	
	public DateEditDialog(MainFrame frame, DateEntry entry, boolean isDEL) {
		super(frame, entry, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.configuredateentry"), isDEL, true);
		this.entry = entry;
	}
	
	public DateEditDialog(JDialog parent, DateEntry entry, Document entryContext, boolean isDEL, boolean canEdit) {
		super(parent, entry, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.viewdateentry"), entryContext, isDEL, canEdit);
		this.entry = entry;
	}

	@Override
	public boolean validateEntries() {
		return true;
	}
	
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
			if (entry.isDisableStandardCodes() != getDisableStandardCodes().isSelected()) {
				changed = true;
				entry.setDisableStandardCodes(getDisableStandardCodes().isSelected());
			}
			//Not used by the DEL
			entry.setExportSecurity(getExportSecurityBox().getSecurityValue());
			entry.setDisableStandardCodes(getDisableStandardCodes().isSelected());
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
