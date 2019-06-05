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


package org.psygrid.collection.entry.ui;

import javax.swing.JRadioButton;

public class EditableRadioButton extends EditableToggleButton   {

    private static final long serialVersionUID = 1L;
    
    /**
     * True if the radio button represents a standard code
     */
    private boolean standardCode;
    
    /**
     * True if the parent entries disable standard codes flag is also True
     */
    private boolean disableStandardCodes;
    
    public EditableRadioButton(JRadioButton radioButton) {
        super(radioButton);
    }

	public boolean isDisableStandardCodes() {
		return disableStandardCodes;
	}

	public void setDisableStandardCodes(boolean disableStandardCodes) {
		this.disableStandardCodes = disableStandardCodes;
	}

	public boolean isStandardCode() {
		return standardCode;
	}

	public void setStandardCode(boolean standardCode) {
		this.standardCode = standardCode;
	}

	@Override
	public void setEnabled(boolean b, boolean isStandardCode) {
		if ( standardCode && disableStandardCodes ){
			//if this radio button is for a standard code, and the entry it is
			//for has the disableStandardCodes flag set to true, then the radio
			//button must always be disabled.
			super.setEnabled(false, isStandardCode);
		}
		else{
			super.setEnabled(b, isStandardCode);
		}
	}
}