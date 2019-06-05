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
import javax.swing.JOptionPane;

import org.psygrid.data.model.hibernate.*;
import org.psygrid.datasetdesigner.controllers.DatasetController;
import org.psygrid.datasetdesigner.ui.MainFrame;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;

public class NumericEditDialog extends AbstractEditDialog {
	
	private NumericEntry entry;
	private CompositeEntry parentEntry;
	private boolean isCompositeEntry = false;
	
	
	public NumericEditDialog(MainFrame frame, NumericEntry entry, CompositeEntry  parentEntry, boolean isDEL, boolean canEdit) {
		super(frame, entry, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.configurenumericenetry"), false, true, true, isDEL, canEdit);
		this.entry = entry;
		this.parentEntry = parentEntry;
		isCompositeEntry = true;
	}
	
	public NumericEditDialog(MainFrame frame, NumericEntry entry, boolean showOptions, boolean showBasicOptions, boolean isDEL, boolean canEdit) {
		super(frame, entry, "Configure Numeric Entry", showOptions, showBasicOptions, isDEL, canEdit);
		this.entry = entry;
	}
	
	public NumericEditDialog(JDialog parent, NumericEntry entry, boolean showOptions, boolean showBasicOptions, Document entryContext, boolean isDEL, boolean canEdit) {
		super(parent, entry, "View Numeric Entry", showOptions, showBasicOptions, entryContext, isDEL, canEdit);
		this.entry = entry;
	}
	
	public NumericEditDialog(MainFrame frame, NumericEntry entry, boolean isDEL, boolean canEdit) {
		this(frame, entry, true, true, isDEL, canEdit);
		this.entry = entry;
	}

	public NumericEditDialog(JDialog parent, NumericEntry entry, Document entryContext, boolean isDEL, boolean canEdit) {
		this(parent, entry, true, true, entryContext, isDEL, canEdit);
		this.entry = entry;
	}

	public boolean validateEntries() {
		
		if (isCompositeEntry) {
			for (int i=0; i<parentEntry.numEntries(); i++) {
				if (!parentEntry.getEntry(i).equals(entry)) {
					if (parentEntry.getEntry(i).getName().equals(getNameField().getText())) {
						JOptionPane.showMessageDialog(this, "An entry with this name already exists for this composite entry.");
						return false;
					}
					if (parentEntry.getEntry(i).getDisplayText().equals(getDisplayTextField().getText())) {
						JOptionPane.showMessageDialog(this, "An entry with this display text already exists for this composite entry.");
						return false;
					}
				}
			}
		}

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
			

			//units - remove all and readd
			for (int i=entry.numUnits(); i>0; i--) {
				entry.removeUnit(0);
			}
			
			if (getUnitBox().getSelectedItem() != null && !getUnitBox().getSelectedItem().equals("")) {
				entry.addUnit((Unit)getUnitBox().getSelectedItem());
			}
		}
		
		//update if present, add if not
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
				((Entry)parentEntry).setIsRevisionCandidate(true);
				((Entry)entry).setIsRevisionCandidate(true);
				((Document)DatasetController.getInstance().getActiveDocument()).setIsRevisionCandidate(true);
			}
		}

	}

}

