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
 * Select the dataset to create the new identifier for.
 *
 */
public class NewIdentifierChooserDialog extends ChooserDialog {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private Application application;
    
    public NewIdentifierChooserDialog(Application parent, ChoosableList choosableList, ChooserModel chooserModel) {
        super(parent, choosableList, chooserModel, Messages.getString("ChooserDialog.chooseDataSet"));	//$NON-NLS-1$
        this.application = parent;
    }

    @Override
    protected ChooserPanel createChooserPanel() {
        return new DocChooserPanel(application, this);
    }

    @Override
    protected void addChooserSelectedListener() {
        // Safe not to remove listener
    	getMainPanel().addDataSetSelectedListener(new DataSetSelectedListener() {
            public void docSelected(DataSetSelectedEvent event) {
                dispose();
                getParent().setSelectedDataSet(event.getDataSet());
            }
        });

    }
    
    @Override
    public DocChooserPanel getMainPanel() {
        return (DocChooserPanel) super.getMainPanel();
    }
    
	public void eslRecordSelectedAction(Record record) {
		//Not required
	}
}
