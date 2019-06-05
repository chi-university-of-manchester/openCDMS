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

package org.psygrid.collection.entry.chooser;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutionException;

import org.jdesktop.swingworker.SwingWorker;
import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.ExceptionsHelper;
import org.psygrid.collection.entry.SwingWorkerExecutor;
import org.psygrid.collection.entry.persistence.ExternalIdGetter;
import org.psygrid.collection.entry.persistence.NoExternalIdMappingException;
import org.psygrid.collection.entry.remote.RemoteServiceFault;
import org.psygrid.collection.entry.security.EntrySAMLException;
import org.psygrid.collection.entry.util.ResetWaitRunnable;
import org.psygrid.collection.entry.util.WaitRunnable;
import org.psygrid.common.identifier.InvalidIdentifierException;
import org.psygrid.data.model.hibernate.Record;
import org.psygrid.data.utils.security.NotAuthorisedFault;

public class PrintRecordChooserDialog extends PrintDocChooserDialog {
	private static final long serialVersionUID = -7726569900742465960L;

	public PrintRecordChooserDialog(Application parent, ChoosableList choosableList) {
		super(parent, choosableList, Messages.getString("PrintRecordChooserDialog.title"));
	}

	@Override
	protected void addChooserSelectedListener() {
		// Safe not to remove listener
		getMainPanel().addSelectionListener(new ChooserSelectionListener() {
			public boolean selected(ChooserSelectionEvent event) {
				if (event.getSelected() instanceof RemoteChoosableRecord) {
					try {
						((RemoteChoosableRecord) event.getSelected()).getAllChildren();	//Force the complete record to load with ALL documents
					}
					catch (Exception e) {
						ExceptionsHelper.handleException(getParent(), "Problem Occurred", null, "Unable to update the table for the participant.", false);
						return false;
					}
					Record record = ((RemoteChoosableRecord) event.getSelected()).completeRecord;
					printRecord(record);
					dispose();
					return false;
				}
				return true;
			}
		});
	}

	public void eslRecordSelectedAction(Record record) {
		if (record == null) {
			return;
		}
		new WaitRunnable(application).run();
		AbstractChoosableWithChildren dataset = (AbstractChoosableWithChildren)getMainPanel().getModel().getCurrentTableModel().parent;
		String displayIdentifier = record.getIdentifier().getIdentifier();


		RemoteChoosableRecord rcr = null; 
		try {
			if (record.getUseExternalIdAsPrimary() == true){
				displayIdentifier = ExternalIdGetter.get(record.getIdentifier().getIdentifier());
			}
			
			rcr = new RemoteChoosableRecord(displayIdentifier, record.getIdentifier().getIdentifier(), null, true, dataset);
			//Update the record with ALL documents
			rcr.getAllChildren();
			record = rcr.getRecord();
		}
		catch (ChoosableException e) {
			ExceptionsHelper.handleException(getParent(), "Problem Occurred", null, "Unable to update the table for the participant.", false);
			return;
		} catch (NoExternalIdMappingException e) {
			ExceptionsHelper.handleException(getParent(), "Problem Occurred", e, "Unable to update the table for the participant.", false);
			return;
		}
		finally {
			new ResetWaitRunnable(application).run();
		}
		printRecord(record);
		dispose();
	}

	@Override
	protected ChooserPanel createChooserPanel() {
		return new PrintRecordChooserPanel(application, this);
	}

	@Override
	public PrintRecordChooserPanel getMainPanel() {
		return (PrintRecordChooserPanel) super.getMainPanel();
	}

	protected void printRecord(Record record){
		final Record printRecord = record;

		SwingWorker<Record, Object> worker = new SwingWorker<Record, Object>() {
			@Override
			protected Record doInBackground() throws ConnectException, SocketTimeoutException,
			NotAuthorisedFault, IOException, RemoteServiceFault,
			EntrySAMLException, InvalidIdentifierException {
				application.printRecord(printRecord);
				return printRecord;
			}

			@Override
			protected void done() {
				try {
					get();
					new ResetWaitRunnable(application).run();       
				} catch (InterruptedException e) {
					new ResetWaitRunnable(application).run();
					ExceptionsHelper.handleInterruptedException(e);
				} catch (ExecutionException e) {
					new ResetWaitRunnable(application).run();
					Throwable cause = e.getCause();
					if (cause instanceof ConnectException) {
						ExceptionsHelper.handleConnectException(application,
								(ConnectException) cause);
					} else if (cause instanceof SocketTimeoutException) {
						ExceptionsHelper.handleSocketTimeoutException(application, 
								(SocketTimeoutException) cause);
					} else if (cause instanceof IOException) {
						ExceptionsHelper.handleIOException(application,
								(IOException) cause, false);
					} else if (cause instanceof NotAuthorisedFault) {
						ExceptionsHelper.handleNotAuthorisedFault(application,
								(NotAuthorisedFault) cause);
					} else if (cause instanceof RemoteServiceFault) {
						ExceptionsHelper.handleRemoteServiceFault(application,
								(RemoteServiceFault) cause);
					} else if (cause instanceof EntrySAMLException) {
						ExceptionsHelper.handleEntrySAMLException(application,
								(EntrySAMLException) cause);
					} else {
						ExceptionsHelper.handleFatalException(cause);
					}
				}
			}
		};
		new WaitRunnable(application).run();
		SwingWorkerExecutor.getInstance().execute(worker);
	}

}
