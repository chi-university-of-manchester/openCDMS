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

import org.psygrid.securitymanager.ui.MultiUserPanel;

import org.psygrid.securitymanager.wizard.WizardPanelDescriptor;

public class MultiUserPanelDescriptor extends WizardPanelDescriptor {
    
    public static final String IDENTIFIER = "MULTI_USER_PANEL";

    public static final int DELETE_USER = 0;
    public static final int MODIFY_USER = 1;
    public static final int PASSWORD = 2;
    
    public boolean modify = true;
    private int type = -1;
    
    public MultiUserPanelDescriptor(int type) {
    	super(IDENTIFIER, new MultiUserPanel());
    	this.type = type;
    }
    
    public MultiUserPanelDescriptor(boolean modify) {
    	super(IDENTIFIER, new MultiUserPanel());
    	this.modify = modify;
    }
    
    public MultiUserPanelDescriptor() {
        super(IDENTIFIER, new MultiUserPanel());
    }
    
    public Object getNextPanelDescriptor() {
    	if (type == DELETE_USER) {
    		return DeleteProjectsDescriptor.IDENTIFIER;
    	} else if (type == MODIFY_USER) {
        	return AddProjectsDescriptor.IDENTIFIER;
    	} else {
    		return ResetPasswordPanelDescriptor.IDENTIFIER;
    	}
    }
    
    public Object getBackPanelDescriptor() {
    	if (type == DELETE_USER) {
    		return DeleteUserDescriptor.IDENTIFIER;
    	} else if (type == MODIFY_USER) {
            return ModifyUserDescriptor.IDENTIFIER;
    	} else {
    		return ResetPasswordUserPanelDescriptor.IDENTIFIER;
    	}
    }  
    
}
