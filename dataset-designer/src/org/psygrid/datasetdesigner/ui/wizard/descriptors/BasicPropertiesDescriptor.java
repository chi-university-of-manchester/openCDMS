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

import org.psygrid.datasetdesigner.ui.wizard.panels.BasicPropsPanel;

/**
 * Descriptor for the Basic Properties page of the wizard
 * @author pwhelan
 */
public class BasicPropertiesDescriptor extends WizardPanelDescriptor {
	
	//identifier
	public static final String IDENTIFIER = "BASIC_PROPERTIES_PANEL";
    
	//the main model of the wizard
    private WizardModel wm;
    
    /**
     * Constructor
     * @param wm set the main model of the wizard
     */
    public BasicPropertiesDescriptor(WizardModel wm) {
        super(IDENTIFIER, new BasicPropsPanel(wm));
        this.wm = wm;
    }
    
    /**
     * Get the next panel descriptor of the wizard
     * @return Object the identifier for the next page of wizard
     */
    public Object getNextPanelDescriptor() {
    	if (wm.getWizardDs() == null) {
    		return DocumentGroupsDescriptor.IDENTIFIER;
    	}
    	
		return MultiCentreDescriptor.IDENTIFIER;
    }
    
    /**
     * Get the next panel descriptor of the wizard
     * @return Object the identifier for the previous page of wizard
     */
    public Object getBackPanelDescriptor() {
        return null;
    }  
    
	
}