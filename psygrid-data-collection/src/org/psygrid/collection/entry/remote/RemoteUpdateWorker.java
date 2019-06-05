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

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutionException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.event.ProgressEvent;
import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.ExceptionsHelper;
import org.psygrid.collection.entry.event.UpdateProgressListener;
import org.psygrid.collection.entry.security.DecryptionException;
import org.psygrid.collection.entry.security.EntrySAMLException;
import org.psygrid.collection.entry.util.ResetWaitRunnable;
import org.psygrid.collection.entry.util.WaitRunnable;
import org.psygrid.common.identifier.InvalidIdentifierException;
import org.psygrid.data.utils.security.NotAuthorisedFault;

public class RemoteUpdateWorker extends SynchronizeWorker<Object> {
    private static final Log LOG = LogFactory.getLog(RemoteUpdateWorker.class);
    
    public RemoteUpdateWorker(Application application) {
        super(application);
    }
    
    @Override
    protected Object doInBackground() throws ConnectException, SocketTimeoutException, IOException, 
            NotAuthorisedFault, EntrySAMLException, RemoteServiceFault, InvalidIdentifierException, DecryptionException  {
        new WaitRunnable(application).run();
        RemoteManager.getInstance().addUpdateProgressListener(
                new UpdateProgressListener() {
                    public void progressStarted(ProgressEvent evt) {
                        progressBarStarted(evt,
                                Messages.getString("RemoteUpdateWorker.updating")); //$NON-NLS-1$);
                    }

                    public void progressEnded(ProgressEvent evt) {
                        progressBarEnded(evt);
                        RemoteManager.getInstance().removeUpdateProgressListener(this);
                    }

                    public void progressIncremented(ProgressEvent evt) {
                        progressBarIncremented(evt);
                    }
                });
        RemoteManager.getInstance().update();

        return null;
    }
    
    protected void failure(ExecutionException ee)   {
        Throwable cause = ee.getCause();
        if (cause instanceof ConnectException){
            ExceptionsHelper.handleConnectException(application,
                    (ConnectException) cause);
        } 
        else if (cause instanceof SocketTimeoutException) {
            ExceptionsHelper.handleSocketTimeoutException(application, 
                    (SocketTimeoutException) cause);
        }
        else if (cause instanceof IOException) {
            ExceptionsHelper.handleIOException(application, 
                    (IOException) cause, false);
        }
        else if (cause instanceof NotAuthorisedFault) {
            ExceptionsHelper.handleNotAuthorisedFault(application, 
                    (NotAuthorisedFault) cause);                
        }
        else if (cause instanceof EntrySAMLException) {
            ExceptionsHelper.handleEntrySAMLException(application,
                    (EntrySAMLException) cause);
        }
        else if (cause instanceof RemoteServiceFault) {
            ExceptionsHelper.handleRemoteServiceFault(application,
                    (RemoteServiceFault) cause);
        }
        else {
            ExceptionsHelper.handleFatalException(cause);
        }
    }
    
    protected void success() {
        // Empty implementation
    }

    @Override
    protected void done() {
        try {
            get();
            success();

        } catch (ExecutionException ee) {
            failure(ee);
        } catch (InterruptedException e) {
            if (LOG.isWarnEnabled()) {
                LOG.warn(e.getMessage(), e);
            }
        }
        finally {
            new ResetWaitRunnable(application).run();
        }
    }
}
