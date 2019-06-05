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


package org.psygrid.collection.entry.remote;

import java.awt.event.ActionEvent;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.SwingWorkerExecutor;
import org.psygrid.common.ui.WrappedJOptionPane;

public class RemoteUpdateAction extends AbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Application application;
	public RemoteUpdateAction(Application application) {
		super(Messages.getString("RemoteUpdateAction.update")); //$NON-NLS-1$
		this.application = application;
	}
	public void actionPerformed(ActionEvent e) {
		if (!RemoteManager.getInstance().isConnectionAvailable()) {
			String title   = Messages.getString("RemoteUpdateAction.offlinetitle");
			String message = Messages.getString("RemoteUpdateAction.offlinemessage");
			WrappedJOptionPane.showWrappedMessageDialog(application, message, title, 
					JOptionPane.ERROR_MESSAGE);
			RemoteUpdateAction.this.failure();
			return;
		}
		final RemoteUpdateWorker worker = new RemoteUpdateWorker(application) {
			@Override
			protected void success() {
				RemoteUpdateAction.this.success();
			}
			@Override
			protected void failure(ExecutionException ee) {
				super.failure(ee);
				RemoteUpdateAction.this.failure();
			}
		};
		SwingWorkerExecutor.getInstance().execute(worker);
	}
	
	protected void failure(){
		//empty implementation
	}
	
	protected void success(){
		showSuccessMessage();
		application.refreshRecordView();
	}
	
	protected void showSuccessMessage(){
		String title = Messages.getString("RemoteUpdateAction.successfulTitle"); //$NON-NLS-1$
		String message = Messages.getString("RemoteUpdateAction.successfulMessage"); //$NON-NLS-1$
		JOptionPane.showMessageDialog(application, message, title, 
				JOptionPane.INFORMATION_MESSAGE);
	}
}
