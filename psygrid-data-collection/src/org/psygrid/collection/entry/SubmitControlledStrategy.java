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
import org.psygrid.collection.entry.remote.RemoteManager;
import org.psygrid.collection.entry.remote.RemoteServiceFault;
import org.psygrid.collection.entry.security.EntrySAMLException;
import org.psygrid.collection.entry.util.ResetWaitRunnable;
import org.psygrid.collection.entry.util.WaitRunnable;
import org.psygrid.data.model.hibernate.DocumentInstance;
import org.psygrid.data.model.hibernate.Record;
import org.psygrid.data.repository.RepositoryInvalidIdentifierFault;
import org.psygrid.data.repository.RepositoryNoConsentFault;
import org.psygrid.data.repository.RepositoryOutOfDateFault;
import org.psygrid.data.repository.transformer.TransformerFault;
import org.psygrid.data.utils.security.NotAuthorisedFault;

public class SubmitControlledStrategy extends SubmitDocumentStrategy {

    public SubmitControlledStrategy(Application application) {
        super(application);
    }
    
    @Override
    public void submit(Record currentRecord) {
        application.getModel().unsetDisabledPresModelsFromDocInstance();
        final DocumentInstance docInstance =
            application.getModel().getCurrentDocOccurrenceInstance();
        SwingWorker<Object, Object> worker = new SwingWorker<Object, Object>(){
            @Override
            protected Object doInBackground() throws ConnectException, SocketTimeoutException, 
                    RepositoryOutOfDateFault, RepositoryNoConsentFault, 
                    RepositoryInvalidIdentifierFault, TransformerFault,
                    NotAuthorisedFault, IOException, RemoteServiceFault, 
                    EntrySAMLException   {
                RemoteManager.getInstance().saveRecord(docInstance.getRecord());
                
                //DUAL DATA ENTRY
                //Need to mirror changes made to the primary document into the secondary document
                if ( null != docInstance.getRecord().getSecondaryRecord() ){
                    Record secondaryRecord = docInstance.getRecord().getSecondaryRecord();
                    RemoteManager.getInstance().saveRecord(secondaryRecord);
                }
                
                return null;
            }
            @Override
            protected void done() {
                try {
                    get();
                    new ResetWaitRunnable(application).run();
                    success();
                } catch(ExecutionException ee) {
                    new ResetWaitRunnable(application).run();
                    Throwable cause = ee.getCause();
                    if (cause instanceof ConnectException) {
                        ExceptionsHelper.handleConnectException(application, 
                                (ConnectException) cause);
                    } 
                    else if (cause instanceof SocketTimeoutException) {
                        ExceptionsHelper.handleSocketTimeoutException(
                                application, 
                                (SocketTimeoutException) cause);
                    }
                    else if (cause instanceof IOException) {
                        ExceptionsHelper.handleIOException(application, 
                                (IOException) cause, false);
                    }
                    else if (cause instanceof RemoteServiceFault) {
                        ExceptionsHelper.handleRemoteServiceFault(application,
                                (RemoteServiceFault) cause);
                    }
                    else if (cause instanceof TransformerFault) {
                        ExceptionsHelper.handleTransformerFault(application,
                                (TransformerFault) cause, true);
                    }
                    else if (cause instanceof NotAuthorisedFault) {
                        ExceptionsHelper.handleNotAuthorisedFault(application,
                                (NotAuthorisedFault) cause);
                    }
                    else if (cause instanceof RepositoryNoConsentFault) {
                        ExceptionsHelper.handleRepositoryNoConsentFault(application,
                                (RepositoryNoConsentFault) cause, null, true);
                    }
                    else if (cause instanceof RepositoryOutOfDateFault) {
                        ExceptionsHelper.handleRepositoryOutOfDateFault(
                                application,
                                (RepositoryOutOfDateFault) cause, null, true);
                    }
                    else if (cause instanceof RepositoryInvalidIdentifierFault) {
                        ExceptionsHelper.handleRepositoryInvalidIdentifierFault(
                                application, 
                                (RepositoryInvalidIdentifierFault) cause, null,
                                true);
                    }
                    else if (cause instanceof EntrySAMLException) {
                        ExceptionsHelper.handleEntrySAMLException(application,
                                (EntrySAMLException) cause);
                    }
                    else {
                        ExceptionsHelper.handleFatalException(cause);
                    }
                    failure();
                } catch (InterruptedException e) {
                    new ResetWaitRunnable(application).run();
                    ExceptionsHelper.handleFatalException(e);
                }
            }
        };
        new WaitRunnable(application).run();
        SwingWorkerExecutor.getInstance().execute(worker);
    }
}
