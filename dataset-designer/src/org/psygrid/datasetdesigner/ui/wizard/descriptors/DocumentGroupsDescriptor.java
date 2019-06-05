/*
Copyright (c) 2006, The University of Manchester, UK.

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
02110-1301, USA.
*/
package org.psygrid.datasetdesigner.ui.wizard.descriptors;

import javax.swing.JDialog;

import org.psygrid.datasetdesigner.ui.wizard.WizardPanelDescriptor;
import org.psygrid.datasetdesigner.ui.wizard.WizardModel;

import org.psygrid.datasetdesigner.ui.wizard.panels.DocumentGroupsPanel;


/**
 * Wizard descriptor for the document groups wizard page
 * @author pwhelan
 */
public class DocumentGroupsDescriptor extends WizardPanelDescriptor {
	
	//identifier for this wizard page
	public static final String IDENTIFIER = "DOCUMENT_GROUPS_PANEL";
    
	//the main wiard model
    private WizardModel wm;
    
    /**
     * Constructor
     * @param wizardDialog the owner dialog
     * @param wm set the main model of the wizard
     */
    public DocumentGroupsDescriptor(JDialog wizardDialog, WizardModel wm) {
        super(IDENTIFIER, new DocumentGroupsPanel(wizardDialog, wm));
        this.wm = wm;
    }

    /**
     * Get the next panel descriptor of the wizard
     * @return Object the identifier for the next page of wizard
     */
    public Object getNextPanelDescriptor() {
    	return DocumentConfigurationDescriptor.IDENTIFIER;
    }
    
    /**
     * Get the previous panel descriptor of the wizard
     * @return Object the identifier for the previous page of wizard
     */
    public Object getBackPanelDescriptor() {
    	
		return MultiCentreDescriptor.IDENTIFIER;
    	
    }  
	
}