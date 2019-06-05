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
package org.psygrid.securitymanager.utils;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;

import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.ProjectType;
import org.psygrid.www.xml.security.core.types.RoleType;

/**
 * Class that formats a copy action from a JList
 * and copies sensible string values for objects
 * (rather than the underlying implementation)
 * 
 * @author pwhelan
 */
public class JListTransferHandler extends TransferHandler{
	
	public JListTransferHandler() {
		
	}

    /**
     * Bundle up the selected items in a single list for export.
     * Each line is separated by a newline.
     */
    protected Transferable createTransferable(JComponent c) {
        JList list = (JList)c;
        Object[] values = list.getSelectedValues();
        
        StringBuffer buff = new StringBuffer();
        
        for (int i = 0; i < values.length; i++) {
	        Object value = values[i];
	        
			if (value instanceof UserType) {
				value = ((UserType)value).getName();
			} else if (value instanceof GroupType) {
				value = ((GroupType)value).getName();
			} else if (value instanceof UserType) {
				value = ((UserType)value).getName();
			} else if (value instanceof ProjectType) {
				value = ((ProjectType)value).getName();
			} else if (value instanceof RoleType) {
				value = ((RoleType)value).getName();
			} 
			
			if (value == null) {
				value = " ";
			}
            
            buff.append(value);
            if (i != values.length - 1) {
                buff.append("\n");
            }
        }
        
        return new StringSelection(buff.toString());
    }
    
    /**
     * We support both copy and move actions.
     */
    public int getSourceActions(JComponent c) {
        return TransferHandler.COPY;
    }

	@Override
	public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
		return false;
	}
	
}