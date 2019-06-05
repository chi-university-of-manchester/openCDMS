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

package org.psygrid.collection.entry;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutionException;

import org.jdesktop.swingworker.SwingWorker;
import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.collection.entry.remote.RemoteManager;
import org.psygrid.collection.entry.remote.RemoteServiceFault;
import org.psygrid.collection.entry.security.EntrySAMLException;
import org.psygrid.collection.entry.util.ResetWaitRunnable;
import org.psygrid.collection.entry.util.WaitRunnable;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.utils.security.NotAuthorisedFault;

public class SubmitPendingStrategy extends SubmitDocumentStrategy {

    public SubmitPendingStrategy(Application application) {
        super(application);
    }

    @Override
    public void submit(Record currentRecord) {
        final DocumentInstance docInstance =
            application.getModel().getCurrentDocOccurrenceInstance();
        final DocumentStatus newDocStatus;
        if (application.getModel().hasWarnings())
            newDocStatus = DocumentStatus.REJECTED;
        else
            newDocStatus = DocumentStatus.APPROVED;
        SwingWorker<Object, Object> worker = new SwingWorker<Object, Object>() {
            @Override
            protected Object doInBackground() throws ConnectException, SocketTimeoutException,
                    NotAuthorisedFault, IOException, RemoteServiceFault,
                    EntrySAMLException {
                RemoteManager.getInstance().changeDocumentInstanceStatus(docInstance,
                        newDocStatus);

                PersistenceManager.getInstance().changeLocalDocInstanceStatus(docInstance, newDocStatus);

                //DUAL DATA ENTRY
                if ( null != docInstance.getRecord().getSecondaryRecord() ){
                    Record primRecord = docInstance.getRecord();
                    DocumentOccurrence primDocOcc = docInstance.getOccurrence();
                    Document primDoc = primDocOcc.getDocument();
                    Record secRecord = primRecord.getSecondaryRecord();
                    DataSet secDs = secRecord.getDataSet();
                    Document secDoc = secDs.getDocument(primDoc.getSecondaryDocIndex().intValue());
                    DocumentOccurrence secDocOcc = secDoc.getOccurrence(primDocOcc.getSecondaryOccIndex().intValue());
                    DocumentInstance  secDocInst = secRecord.getDocumentInstance(secDocOcc);
                    RemoteManager.getInstance().changeDocumentInstanceStatus(secDocInst, newDocStatus);
                    PersistenceManager.getInstance().changeLocalDocInstanceStatus(secDocInst, newDocStatus);
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    new ResetWaitRunnable(application).run();
                    success();
                } catch (InterruptedException e) {
                    new ResetWaitRunnable(application).run();
                    ExceptionsHelper.handleInterruptedException(e);
                } catch (ExecutionException e) {
                    new ResetWaitRunnable(application).run();
                    Throwable cause = e.getCause();
                    if (cause instanceof ConnectException) {
                        ExceptionsHelper.handleConnectException(
                                application,
                                (ConnectException) cause);
                    } else if (cause instanceof SocketTimeoutException) {
                        ExceptionsHelper.handleSocketTimeoutException(
                                application, 
                                (SocketTimeoutException) cause);
                    } else if (cause instanceof IOException) {
                        ExceptionsHelper.handleIOException(
                                application,
                                (IOException) cause, false);
                    } else if (cause instanceof NotAuthorisedFault) {
                        ExceptionsHelper.handleNotAuthorisedFault(
                                application,
                                (NotAuthorisedFault) cause);
                    } else if (cause instanceof RemoteServiceFault) {
                        ExceptionsHelper.handleRemoteServiceFault(
                                application,
                                (RemoteServiceFault) cause);
                    } else if (cause instanceof EntrySAMLException) {
                        ExceptionsHelper.handleEntrySAMLException(
                                application,
                                (EntrySAMLException) cause);
                    } else {
                        ExceptionsHelper.handleFatalException(cause);
                    }
                    failure();
                }
            }
        };
        new WaitRunnable(application).run();
        SwingWorkerExecutor.getInstance().execute(worker);       
    }

}
