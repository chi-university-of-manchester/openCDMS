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

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXStatusBar;
import org.jdesktop.swingx.event.ProgressEvent;
import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.event.CommitProgressListener;

public abstract class SynchronizeWorker<T> extends SwingWorker<T, Object> {
    
    private interface WithStatusBar {
        void op(JXStatusBar statusBar);
    }
    
    private final Log LOG = LogFactory.getLog(SynchronizeWorker.class);
    protected final JFrame application;
    protected boolean successful = false;
    
    public SynchronizeWorker(JFrame application) {
        this.application = application;
    }

    protected void progressBarStarted(final ProgressEvent evt, 
            final String message) {
        onEventThread(new WithStatusBar() {
            public void op(JXStatusBar statusBar) {
                statusBar.progressStarted(evt);
                statusBar.setLeadingMessage(message);
            }
        });
    }
    
    private void onEventThread(final WithStatusBar o) {
        if (!(application instanceof Application))
            return;
        
        final JXStatusBar statusBar = ((Application) application).getStatusBar();
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                o.op(statusBar);
            }
        });
    }

    protected void progressBarEnded(final ProgressEvent evt) {
        onEventThread(new WithStatusBar() {
            public void op(JXStatusBar statusBar) {
                statusBar.progressEnded(evt);
                statusBar.setLeadingMessage("");
            }
        });
    }
    
    protected void progressBarIncremented(final ProgressEvent evt) {
        onEventThread(new WithStatusBar() {
            public void op(JXStatusBar statusBar) {
                statusBar.progressIncremented(evt);
            }
        });
    }

    /**
     * Adds a commit progress listener that calls the appropriate methdos to
     * show progress feedback. Note that it will remove itself automatically
     * when the commit is finished.
     */
    protected void addCommitProgressListener() {
        RemoteManager.getInstance().addCommitProgressListener(new CommitProgressListener() {
            public void progressStarted(ProgressEvent evt) {
                progressBarStarted(evt, Messages
                        .getString("RemoteCommitWorker.committing")); 
            }
    
            public void progressEnded(ProgressEvent evt) {
                progressBarEnded(evt);
                RemoteManager.getInstance().removeCommitProgressListener(this);
            }
    
            public void progressIncremented(ProgressEvent evt) {
                progressBarIncremented(evt);
            }
        });
    }
    
    protected void error(String title, String message, Throwable cause) {
        if (LOG.isErrorEnabled()) {
            LOG.error(message, cause);
        }
        JOptionPane.showMessageDialog(application, message, title, 
                JOptionPane.ERROR_MESSAGE);
    }
    
    public boolean isSuccessful() {
        return successful;
    }
}
