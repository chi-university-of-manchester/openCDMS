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

import javax.swing.Icon;

import org.psygrid.collection.entry.Icons;

public class ReportChooserTableRenderer extends ChooserTableRenderer {
    
    //TODO Find an icon for report
    private final Icon REPORT_ICON = Icons.getInstance().getIcon("document_occurrence"); //$NON-NLS-1$
    //TODO Find an icon for record
    private final Icon RECORD_ICON = Icons.getInstance().getIcon("dataset"); //$NON-NLS-1$
    
    public ReportChooserTableRenderer() {
        super();
    }
    
    @Override
    protected void setIcon(Choosable choosable) {
    	ChoosableType type = choosable.getType();
        switch (type) {
        case DATASET:
        case RECORD:
            label.setIcon(RECORD_ICON);
            break;
        case REPORT:
            label.setIcon(REPORT_ICON);
            break;
        }
    }

}
