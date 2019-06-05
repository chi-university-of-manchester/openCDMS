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

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.EntryHelper;
import org.psygrid.collection.entry.remote.RemoteManager;
import org.psygrid.collection.entry.ui.ChangePasswordDialog;

public class ChangePasswordAction extends AbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Application application;

	public ChangePasswordAction(Application application) {
		super(Messages.getString("ChangePasswordAction.password")); //$NON-NLS-1$
		this.application = application;
	}

	public void actionPerformed(ActionEvent e) {
		if (!RemoteManager.getInstance().isConnectionAvailable()) {
			String title = Messages.getString("ChangePasswordAction.offlineNotificationTitle"); //$NON-NLS-1$
			String message = Messages.getString("ChangePasswordAction.offlineNotificationMessage"); //$NON-NLS-1$
			JOptionPane.showMessageDialog(application, message, title,
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		String messagePrefix = Messages.getString("ChangePasswordAction.runWhenOnUncommittedMessagePrefix"); //$NON-NLS-1$
		EntryHelper.runWhenNoUncommittedRecords(application, messagePrefix,
		        new Runnable() {
				    public void run() {
				        ChangePasswordDialog dlg = new ChangePasswordDialog(application);
				        dlg.setVisible(true);
				    }
				},
				null);
	}
}
