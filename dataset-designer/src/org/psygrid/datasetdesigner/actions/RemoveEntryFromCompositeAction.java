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
package org.psygrid.datasetdesigner.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JList;
import javax.swing.JOptionPane;

import org.psygrid.data.model.hibernate.CompositeEntry;

import org.psygrid.datasetdesigner.ui.editdialogs.*;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;

public class RemoveEntryFromCompositeAction extends AbstractAction {
	
	private CompositeEntry parentEntry;
	private JList entryList;
	private CompositeEditDialog editDialog;
	
	private Vector<ActionListener> entriesChangedListeners;
	
	public RemoveEntryFromCompositeAction(CompositeEditDialog editDialog, CompositeEntry parentEntry) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.remove"));
		this.editDialog = editDialog;
		this.parentEntry = parentEntry;
		this.entriesChangedListeners = new Vector<ActionListener>();
	}
	
	public void actionPerformed(ActionEvent aet) {
		String message = PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.confirmremoveentryfromcomposite");
		int n = JOptionPane.showConfirmDialog(
                editDialog, message,
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
        if (n == JOptionPane.YES_OPTION) {
    		for (int i=0; i<parentEntry.numEntries(); i++) {
    			
    			if (editDialog.getSelectedEntry().getName().equals("Fixed Label")) {
    				((CompositeEntry)parentEntry).setRowLabels(new ArrayList());
    			}
    			
    			if (parentEntry.getEntry(i).getName().equals(editDialog.getSelectedEntry().getName())) {
    				parentEntry.removeEntry(i);
    				fireActionEvent();
    			}
    		}	
        }
		
	}
	
	public void addActionListener(ActionListener listener) {
		entriesChangedListeners.add(listener);
	}

	public void removeActionListener(ActionListener listener) {
		entriesChangedListeners.remove(listener);
	}
	
	public void fireActionEvent() {
		Iterator atIt = entriesChangedListeners.iterator();
		while(atIt.hasNext()) {
			ActionListener al = (ActionListener)atIt.next();
			al.actionPerformed(new ActionEvent(this, 1, ""));
		}
	}

	
}
