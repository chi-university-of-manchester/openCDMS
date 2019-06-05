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

import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingworker.SwingWorker;
import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.ExceptionsHelper;
import org.psygrid.collection.entry.security.EntrySAMLException;
import org.psygrid.collection.entry.util.ResetWaitRunnable;
import org.psygrid.collection.entry.util.WaitRunnable;
import org.psygrid.common.ui.WrappedJOptionPane;
import org.psygrid.data.utils.security.NotAuthorisedFault;

public class SendLogsToSupportWorker extends SwingWorker<Object, Object> {
    private static final Log LOG = LogFactory.getLog(SendLogsToSupportWorker.class);
    
    private Application parent;
    
    /**
     * If this flag is set to True then we don't handle exceptions in the normal way,
     * instead if there is a problem a simple dialog is shown to the user.
     * <p>
     * This is to handle the case when this worker is triggered from the fatal error
     * dialog, so that we can't get into a circular exception loop.
     */
    private boolean ignoreExceptions;
    
    public SendLogsToSupportWorker(Application parent){
        this.parent = parent;
    }

    public SendLogsToSupportWorker(Application parent, boolean ignoreExceptions){
        this(parent);
        this.ignoreExceptions = ignoreExceptions;
    }
    
    @Override
    protected Object doInBackground() 
            throws ConnectException, SocketTimeoutException, NotAuthorisedFault, RemoteServiceFault, IOException, EntrySAMLException {
        new WaitRunnable(parent).run();
        RemoteManager.getInstance().emailLogFileToSupport();
        return null;
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
            new ResetWaitRunnable(parent).run();
        }
    }

    protected void success() {
        WrappedJOptionPane.showWrappedMessageDialog(parent, 
                "Your log files have been successfully emailed to PsyGrid support. We will contact you in due course to resolve your problem.",
                "Operation Successful",
                JOptionPane.INFORMATION_MESSAGE);
    }

    protected void failure(ExecutionException ee) {
        if ( ignoreExceptions ){
            WrappedJOptionPane.showWrappedMessageDialog(parent, 
                    "The system was not able to send your log file to PsyGrid support. Please email the log file manually to support@psygrid.org.",
                    "Operation Failed",
                    JOptionPane.INFORMATION_MESSAGE);
        }
        else{
            Throwable cause = ee.getCause();
            if (cause instanceof ConnectException){
                ExceptionsHelper.handleConnectException(parent,
                        (ConnectException) cause);
            } 
            else if (cause instanceof SocketTimeoutException) {
                ExceptionsHelper.handleSocketTimeoutException(parent, 
                        (SocketTimeoutException) cause);
            }
            else if (cause instanceof IOException) {
                ExceptionsHelper.handleIOException(parent, 
                        (IOException) cause, false);
            }
            else if (cause instanceof NotAuthorisedFault) {
                ExceptionsHelper.handleNotAuthorisedFault(parent, 
                        (NotAuthorisedFault) cause);                
            }
            else if (cause instanceof EntrySAMLException) {
                ExceptionsHelper.handleEntrySAMLException(parent,
                        (EntrySAMLException) cause);
            }
            else if (cause instanceof RemoteServiceFault) {
                ExceptionsHelper.handleRemoteServiceFault(parent,
                        (RemoteServiceFault) cause);
            }
            else {
                ExceptionsHelper.handleFatalException(cause);
            }
        }
    }
    
}
