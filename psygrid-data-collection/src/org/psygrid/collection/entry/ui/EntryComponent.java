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

import java.awt.event.MouseListener;

import javax.swing.text.JTextComponent;

import com.jgoodies.validation.view.ValidationComponentUtils;

public abstract class EntryComponent extends AbstractEditable {
        
    public abstract JTextComponent getTextComponent();
    
    @Override
    public void setEditable(boolean b) {
        getTextComponent().setEditable(b);
        super.setEditable(b);
    }
    
    @Override
    public void addMouseListener(MouseListener listener) {
        getTextComponent().addMouseListener(listener);
    }
    
    @Override
    public void removeMouseListener(MouseListener listener) {
        getTextComponent().removeMouseListener(listener);
    }
    
    @Override
    public void setEnabled(boolean b, boolean isStandardCode) {
        if ( (b && !isStandardCode) == isEnabled()) {
            return;
        }
        getTextComponent().setEnabled(b && !isStandardCode);
        super.setEnabled(b, isStandardCode);
    }

	@Override
	public void setMandatory(boolean b) {
		if (b) {
			ValidationComponentUtils.setMandatoryBackground(getTextComponent());
		}
		else{
			getTextComponent().setBackground(DEFAULT_BACKGROUND);
		}
		super.setMandatory(b);
	}
    
}
