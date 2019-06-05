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

package org.psygrid.datasetdesigner.ui.wizard.descriptors;

import org.psygrid.datasetdesigner.ui.wizard.WizardPanelDescriptor;
import org.psygrid.datasetdesigner.ui.wizard.WizardModel;

import org.psygrid.datasetdesigner.ui.wizard.panels.SchedulePanel;

/**
 * Wizard descriptor for the schedule descriptor wizard page
 * @author pwhelan
 */
public class ScheduleDescriptor extends WizardPanelDescriptor {
	
	//identifier
	public static final String IDENTIFIER = "SCHEDULE_PANEL";
    
	/**
     * Constructor
     * @param parentDialog the owner dialog
     * @param wm set the main model of the wizard
     */
    public ScheduleDescriptor(WizardModel wm) {
        super(IDENTIFIER, new SchedulePanel(wm));
    }
    
    /**
     * Get the next panel descriptor of the wizard
     * @return Object the identifier for the next page of wizard
     */
    public Object getNextPanelDescriptor() {
		return WizardPanelDescriptor.FINISH;
    }
    
    /**
     * Get the previous panel descriptor of the wizard
     * @return Object the identifier for the previous page of wizard
     */
    public Object getBackPanelDescriptor() {
        return DocumentConfigurationDescriptor.IDENTIFIER;
    }  
	
}