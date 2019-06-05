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

import javax.swing.JDialog;

import org.psygrid.datasetdesigner.ui.wizard.WizardPanelDescriptor;
import org.psygrid.datasetdesigner.ui.wizard.WizardModel;

import org.psygrid.datasetdesigner.ui.wizard.panels.DocumentConfigurationPanel;

/**
 * Wizard descriptor for the document configuration wizard page
 * @author pwhelan
 */
public class DocumentConfigurationDescriptor extends WizardPanelDescriptor {
	
	//identifier for this descriptor
	public static final String IDENTIFIER = "DOCUMENT_CONFIGURATION_PANEL";
    
    /**
     * Constructor
     * @param parentDialog the owner dialog
     * @param wm set the main model of the wizard
     */
    public DocumentConfigurationDescriptor(JDialog parentDialog, WizardModel wm) {
        super(IDENTIFIER, new DocumentConfigurationPanel(parentDialog, wm));
    }
    
    /**
     * Get the next panel descriptor of the wizard
     * @return Object the identifier for the next page of wizard
     */
    public Object getNextPanelDescriptor() {
		return ScheduleDescriptor.IDENTIFIER;
    }

    /**
     * Get the previous panel descriptor of the wizard
     * @return Object the identifier for the previous page of wizard
     */
    public Object getBackPanelDescriptor() {
    	return DocumentGroupsDescriptor.IDENTIFIER;
    }  
	
}