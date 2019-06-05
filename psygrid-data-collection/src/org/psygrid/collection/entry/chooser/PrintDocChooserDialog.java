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

package org.psygrid.collection.entry.chooser;

import java.util.ArrayList;

import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.ExceptionsHelper;
import org.psygrid.collection.entry.persistence.ExternalIdGetter;
import org.psygrid.data.model.hibernate.DocumentInstance;
import org.psygrid.data.model.hibernate.Record;

public class PrintDocChooserDialog extends ChooserDialog {

	private static final long serialVersionUID = 6409108600815354529L;

	protected Application application;

	public PrintDocChooserDialog(Application parent, ChoosableList choosableList) {
		super(parent, choosableList, null, Messages.getString("PrintDocChooserDialog.title"));
		this.application = parent;
	}

	public PrintDocChooserDialog(Application parent, ChoosableList choosableList, String title) {
		super(parent, choosableList, null, title);
		this.application = parent;
	}

	@Override
	protected void addChooserSelectedListener() {
		// Safe not to remove listener
		getMainPanel().addSelectionListener(new ChooserSelectionListener() {
			public boolean selected(ChooserSelectionEvent event) {
				if (event.getSelected() instanceof RemoteChoosableRecord) {
					try {
						((RemoteChoosableRecord)event.getSelected()).getAllChildren();	//Load ALL the documents	
					}
					catch (Exception e) {
						ExceptionsHelper.handleException(PrintDocChooserDialog.this, "Problem opening record", e, "There was a problem retrieving the documents for this record", false);
					}
				}
				else if (event.getSelected() instanceof ChoosableDocInstance) {
					dispose();
					DocumentInstance docInstance =
						((ChoosableDocInstance) event.getSelected()).getDocInstance();
					printDocument(docInstance);
					return false;
				}
				return true;
			}
		});
	}

	public void eslRecordSelectedAction(Record record) {
		if (record == null) {
			return;
		}
		try {
			AbstractChoosableWithChildren dataset = (AbstractChoosableWithChildren)getMainPanel().getModel().getCurrentTableModel().parent;
			String displayIdentifier = record.getIdentifier().getIdentifier();
			if (record.getUseExternalIdAsPrimary() == true){
				displayIdentifier = ExternalIdGetter.get(record.getIdentifier().getIdentifier());
			}
			RemoteChoosableRecord rcr = new RemoteChoosableRecord(displayIdentifier, record.getIdentifier().getIdentifier(), null, true, dataset);
			rcr.getAllChildren();	//Loads the record's document instances
			dataset.setChildren(new ArrayList<Choosable>());
			dataset.addChild(rcr);

			getMainPanel().getModel().setParentTableModel();
			ChoosableList parent = (ChoosableList)getMainPanel().getModel().getCurrentTableModel().parent;

			int counter = 0;
			for (Choosable c: parent.getChildren()) {
				if (c == dataset) {
					getMainPanel().getModel().loadChoosable(counter);
					break;
				}
				counter++;
			}
			getMainPanel().getModel().getCurrentTableModel().fireTableDataChanged();
		}
		catch (Exception e) {
			ExceptionsHelper.handleException(getParent(), "Problem Occurred", null, "Unable to update the table for the participant.", false);
		}
	}

	@Override
	protected ChooserPanel createChooserPanel() {
		return new DocInstanceChooserPanel(application, this);
	}

	@Override
	public DocInstanceChooserPanel getMainPanel() {
		return (DocInstanceChooserPanel) super.getMainPanel();
	}

	protected void printDocument(DocumentInstance  docInst){
		application.printSingleDocument(docInst);
	}

}
