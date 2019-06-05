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


package org.psygrid.collection.entry.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.psygrid.collection.entry.model.DatePresModel;
import org.psygrid.collection.entry.ui.DatePicker;

public final class PartialDateModeAction extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final DatePresModel presModel;
    private final DatePicker datePicker;
    private boolean disabled;
    
    public PartialDateModeAction(DatePresModel presModel, DatePicker datePicker, boolean disabled) {
        super();
        this.presModel = presModel;
        this.datePicker = datePicker;
        this.disabled = disabled;
        setEnabled(!disabled);
        setName();
    }
    
    public boolean isDisabled() {
		return disabled;
	}

	public void setName() {
        if (datePicker.isPartialDateMode()) {
            putValue(Action.NAME, Messages.getString("PartialDateModeAction.fullDate")); //$NON-NLS-1$
        }
        else {
            putValue(Action.NAME, Messages.getString("PartialDateModeAction.partialDate")); //$NON-NLS-1$
        }
    }
    
    public void actionPerformed(ActionEvent e) {
        boolean newMode = !datePicker.isPartialDateMode();
        presModel.getDisplayTextModel().setValue(null);
        presModel.getMonthModel().setValue(null);
        presModel.getYearTextModel().setValue(null);
        presModel.getYearModel().setValue(null);
        datePicker.setPartialDateMode(newMode);
        datePicker.revalidate();
        datePicker.repaint();
        setName();
    }
    
}
