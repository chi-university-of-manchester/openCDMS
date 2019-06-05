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

import javax.swing.JPanel;

import org.psygrid.collection.entry.Application;

public class PrintRecordChooserPanel extends DocInstanceChooserPanel {
	private static final long serialVersionUID = -3884360882146564109L;

	public PrintRecordChooserPanel(Application application, ChooserDialog dialog) {
        super(application, dialog);
    }
    
	@Override
	protected void processSelectionAction(int row, Choosable selectedValue) {
		ChoosableType type = selectedValue.getType();
		switch(type) {
		case DATASET:
		case RECORD:
			loadChoosable(row);
			break;
		default: throw new IllegalArgumentException("Unexpected type: " + type); //$NON-NLS-1$
		}
	}
	
	@Override
	protected JPanel createStatusSelectionPanel() {
		//No status selection is required here
		return new JPanel();
	}

	protected void activateStatusDropdown(){
		//No status selection is required here
	}

	@Override
	protected void resetStatusSelectionPanel() {
		//No status selection is required here
	}
}
