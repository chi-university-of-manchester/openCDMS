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

import org.psygrid.collection.entry.Application;
import org.psygrid.data.model.hibernate.Record;

/**
 * @author Rob Harper
 *
 */
public class TemplateDocumentChooserDialog extends ChooserDialog {

	private static final long serialVersionUID = 4138334121794808409L;

	private Application application;
	
	public TemplateDocumentChooserDialog(Application parent, ChoosableList choosableList, ChooserModel chooserModel) {
		super(parent, choosableList, chooserModel, Messages.getString("TemplateDocumentChooserDialog.title"));
		this.application = parent;
	}

	@Override
	protected void addChooserSelectedListener() {
		getMainPanel().addDocOccurrenceSelectedListener(new TemplateDocOccurrenceSelectedListener() {
            public void docSelected(TemplateDocOccurrenceSelectedEvent event) {
            	dispose();
            	getParent().printTemplateDocument(event.getDocOccurrence());
            }
		});
	}

	@Override
	protected ChooserPanel createChooserPanel() {
		return new TemplateDocumentChooserPanel(application, this);
	}

    @Override
    public TemplateDocumentChooserPanel getMainPanel() {
       return (TemplateDocumentChooserPanel) super.getMainPanel();
    }

	public void eslRecordSelectedAction(Record record) {
		//Not required
	}
}
