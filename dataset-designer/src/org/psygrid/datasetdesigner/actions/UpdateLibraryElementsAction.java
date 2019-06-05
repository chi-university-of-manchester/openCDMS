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

import javax.swing.AbstractAction;

import org.psygrid.common.ui.WrappedJOptionPane;
import org.psygrid.datasetdesigner.ui.MainTabbedPane;

/**
 * Query the DEL for updates to any elements checked out locally.
 * 
 * @author Lucy Bridges
 *
 */
public class UpdateLibraryElementsAction extends AbstractAction {

	private static final long serialVersionUID = -8757772764507451916L;

	private MainTabbedPane docPane;
	
	public UpdateLibraryElementsAction(MainTabbedPane docPane) {
		super("Check Library For Updates");
		this.docPane = docPane;
	}

	public void actionPerformed(ActionEvent e) {
		boolean found = docPane.getDelInitializer().checkForUpdates();
		if (!found) {
			WrappedJOptionPane.showWrappedMessageDialog(docPane, "No updates available", "No Updates", WrappedJOptionPane.INFORMATION_MESSAGE);
		}
	}
}
