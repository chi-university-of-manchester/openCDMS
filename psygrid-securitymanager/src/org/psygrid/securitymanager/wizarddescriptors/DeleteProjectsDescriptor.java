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

package org.psygrid.securitymanager.wizarddescriptors;

import org.psygrid.securitymanager.ui.DeleteProjectsPanel;

import org.psygrid.securitymanager.wizard.WizardPanelDescriptor;


/**
 * Wizard descriptor for the add projects panel.
 * 
 * @author pwhelan
 */
public class DeleteProjectsDescriptor extends WizardPanelDescriptor {
    
    public static final String IDENTIFIER = "DELETE_PROJECTS_PANEL";
    
    //indicates if this is the panel which follows the modify panel 
    //or the add panel
    private boolean modify = false;
    
    public DeleteProjectsDescriptor() {
        super(IDENTIFIER, new DeleteProjectsPanel());
        this.modify = modify;
    }
    
    public Object getNextPanelDescriptor() {
    	return ConfirmDeletionDescriptor.IDENTIFIER;
    }
    
    public Object getBackPanelDescriptor() {
    	return DeleteUserDescriptor.IDENTIFIER;
    }  
   
}
