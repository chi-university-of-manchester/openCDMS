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

import org.psygrid.collection.entry.model.DatePresModel;

public class DateEnableEditingAction extends EnableEditingAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private DatePresModel datePresModel;
    private PartialDateModeAction enablePartialAction;
    
    public DateEnableEditingAction(DatePresModel datePresModel, 
    		PartialDateModeAction enablePartialAction) {
        super(datePresModel.getDisplayTextModel(), 
                datePresModel.getStandardCodeModel());
        this.datePresModel = datePresModel;
        this.enablePartialAction = enablePartialAction;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
    	if ( !enablePartialAction.isDisabled() ){
	        enablePartialAction.setEnabled(true);
	        datePresModel.getYearTextModel().setValue(null);
	        datePresModel.getMonthModel().setValue(null);
    	}
        super.actionPerformed(e);
    }

}
