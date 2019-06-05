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
import org.psygrid.collection.entry.DocumentStatus;
import org.psygrid.collection.entry.ExceptionsHelper;
import org.psygrid.collection.entry.SwingWorkerExecutor;
import org.psygrid.collection.entry.remote.RemoteServiceFault;
import org.psygrid.collection.entry.security.EntrySAMLException;
import org.psygrid.collection.entry.util.RecordHelper;
import org.psygrid.common.identifier.InvalidIdentifierException;
import org.psygrid.data.model.hibernate.DocumentGroup;
import org.psygrid.data.model.hibernate.Record;
import org.psygrid.data.utils.security.NotAuthorisedFault;

/**
 * @author Rob Harper
 *
 */
public class LoadDocumentChooserDialog extends ChooserDialog {

	private static final long serialVersionUID = 9072637628855687242L;
    
    protected final boolean readyToCommit;
    
	public LoadDocumentChooserDialog(Application parent,
			ChoosableList choosableList, boolean readyToCommit,
			ChooserModel chooserModel, DocumentStatus docStatus,
			Record record, DocumentGroup docGroup) {
		 super(parent, choosableList, chooserModel);
	        this.readyToCommit = readyToCommit;
	}

	@Override
	protected ChooserPanel createChooserPanel() {
		return new DocInstanceChooserPanel(application, this);
	}
	
	@Override
	protected void addChooserSelectedListener() {
        // Safe not to remove listener
        getMainPanel().addSelectionListener(new ChooserSelectionListener() {
            public boolean selected(ChooserSelectionEvent event) {
                if (event.getSelected() instanceof AbstractChoosableRecord<?>) {
                    dispose();
                    loadRecord(((AbstractChoosableRecord<?>) event.getSelected()));
                    return false;
                }
                return true;
            }
        });
	}
	
	   /**
     * Set when a record is selected via the EslSearchPanel
     * @param record
     */
    public void eslRecordSelectedAction(Record record) {
    	if (record == null) {
			return;
		}
    	application.setSelectedRecord(record);
    	dispose();
    }
	
    private void loadRecord(final AbstractChoosableRecord<?> cRecord){
        SwingWorker<Record, Object> worker = new SwingWorker<Record, Object>() {
            @Override
            protected Record doInBackground() throws ChoosableException, InvalidIdentifierException, IOException {
                return RecordHelper.constructRecord(cRecord.getSysIdentifier());
            }

            @Override
            protected void done() {
                try {
                    Record rec = get();
                    setWait(false);
                    getParent().setSelectedRecord(rec);
                } catch (InterruptedException e) {
                    setWait(false);
                    ExceptionsHelper.handleInterruptedException(e);
                } catch (ExecutionException e) {
                    setWait(false);
                    Throwable cause = e.getCause();
                    if (cause instanceof ChoosableException)
                        cause = cause.getCause();
                    if (cause instanceof ConnectException) {
                        ExceptionsHelper.handleConnectException(
                                getParent(),
                                (ConnectException) cause);
                    } else if (cause instanceof SocketTimeoutException) {
                        ExceptionsHelper.handleSocketTimeoutException(
                                getParent(), 
                                (SocketTimeoutException) cause);
                    } else if (cause instanceof IOException) {
                        ExceptionsHelper.handleIOException(
                                getParent(),
                                (IOException) cause, false);
                    } else if (cause instanceof NotAuthorisedFault) {
                        ExceptionsHelper.handleNotAuthorisedFault(
                                getParent(),
                                (NotAuthorisedFault) cause);
                    } else if (cause instanceof RemoteServiceFault) {
                        ExceptionsHelper.handleRemoteServiceFault(
                                getParent(),
                                (RemoteServiceFault) cause);
                    } else if (cause instanceof EntrySAMLException) {
                        ExceptionsHelper.handleEntrySAMLException(
                                getParent(),
                                (EntrySAMLException) cause);
                    } else {
                        ExceptionsHelper.handleFatalException(cause);
                    }
                }
            }
        };
        setWait(true);
        SwingWorkerExecutor.getInstance().execute(worker);
    }
}
