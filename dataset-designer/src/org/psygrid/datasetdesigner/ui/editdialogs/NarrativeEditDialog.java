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

import org.psygrid.data.model.hibernate.Document;
import org.psygrid.data.model.hibernate.NarrativeEntry;
import org.psygrid.data.model.hibernate.NarrativeStyle;
import org.psygrid.datasetdesigner.ui.MainFrame;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;

public class NarrativeEditDialog extends AbstractEditDialog {

	private NarrativeEntry entry;

	public NarrativeEditDialog(MainFrame frame, NarrativeEntry entry, boolean isDEL, boolean canEdit) {
		super(frame, entry, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.configurenarrativentry"), false, false, isDEL, canEdit);
		this.entry = entry;
	}

	public NarrativeEditDialog(JDialog parent, NarrativeEntry entry, Document entryContext, boolean isDEL, boolean canEdit) {
		super(parent, entry, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.viewnarrativenetry"), false, false, entryContext, isDEL, canEdit);
		this.entry = entry;
	}

	public boolean validateEntries() {
		return true;
	}

	public void ok() {
		if (entry != null) {
			if (fieldChanged(entry.getDisplayText(), getDisplayTextField().getText())) {
				changed = true;
				entry.setDisplayText(getDisplayTextField().getText());
			}
			if (fieldChanged(entry.getName(), getNameField().getText())) {
				changed = true;
				entry.setName(getNameField().getText());
			}
//			if (fieldChanged(entry.getEntryStatus().toString(),((EntryStatus)getEntryStatusComboBox().getSelectedItem()).toString())) {
//				changed = true;
//				entry.setEntryStatus((EntryStatus)getEntryStatusComboBox().getSelectedItem());	
//			}
//			if (fieldChanged(entry.getLabel(),getLabelField().getText())) {
//				changed = true;
//				entry.setLabel(getLabelField().getText());	
//			}
//			if (fieldChanged(entry.getDescription(),getHelpField().getText())) {
//				changed = true;
//				entry.setDescription(getHelpField().getText());
//			}
			//Not used by the DEL
			entry.setExportSecurity(getExportSecurityBox().getSecurityValue());

			if ((entry.getStyle() == null && (NarrativeStyle)getNarrativeStyleBox().getSelectedItem() != null)
					|| ((NarrativeStyle)getNarrativeStyleBox().getSelectedItem() == null && entry.getStyle() != null)
					||	!entry.getStyle().equals((NarrativeStyle)getNarrativeStyleBox().getSelectedItem())) {
				changed = true;
				entry.setStyle((NarrativeStyle)getNarrativeStyleBox().getSelectedItem());	
			}


		}
	}

}
