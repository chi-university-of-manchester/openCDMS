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

import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.JTextField;

import org.psygrid.collection.entry.Editable;

public abstract class AbstractEditable extends JComponent implements Editable    {

	public static final Color DEFAULT_BACKGROUND = getDefaultBackground();
	
    private boolean editable = true;
    
    private boolean mandatory = true;
    
    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean b) {
        boolean oldValue = this.editable;
        this.editable = b;
        firePropertyChange("editable", Boolean.valueOf(oldValue),  //$NON-NLS-1$
                Boolean.valueOf(this.editable));
    }

    public void setEnabled(boolean b, boolean isStandardCode) {
        super.setEnabled(b && !isStandardCode);
    }

	public boolean isMandatory() {
		return mandatory;
	}

	public void setMandatory(boolean b) {
        boolean oldValue = this.mandatory;
        this.mandatory = b;
        firePropertyChange("mandatory", Boolean.valueOf(oldValue),  //$NON-NLS-1$
                Boolean.valueOf(this.mandatory));
	}

	private static final Color getDefaultBackground(){
		JTextField field = new JTextField();
		return field.getBackground();
	}
	
}
