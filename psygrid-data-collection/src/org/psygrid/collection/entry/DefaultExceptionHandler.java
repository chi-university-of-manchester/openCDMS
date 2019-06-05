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

import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Window;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;

import javax.swing.JFrame;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.JXErrorDialog;
import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.collection.entry.remote.RemoteManager;
import org.psygrid.collection.entry.utils.BrowserLauncher;

/**
 * Provides functionality to log an error, display it to the user and exit
 * the application afterwards. You can enable this handler for one thread, one
 * thread group or as the default handler for any thread that doesn't have one
 * set. See {@link #uncaughtException(Thread, Throwable)} for more information.
 * 
 * @see Thread#setUncaughtExceptionHandler(UncaughtExceptionHandler)
 * @see Thread#setDefaultUncaughtExceptionHandler(UncaughtExceptionHandler)
 * 
 * @author Ismael Juma (ismael@juma.me.uk)
 */
public class DefaultExceptionHandler implements UncaughtExceptionHandler  {
    
    private static final Log LOG = LogFactory.getLog(DefaultExceptionHandler.class);
    
    /**
     * Logs the exception to the configured log subsystem, displays a dialog box
     * informing the user that the application will exit due to an unexpected
     * error and exits the application after that.
     * 
     * Implementation note: With the Sun JDK 5, this method will not be invoked
     * when an exception is thrown from a modal Dialog. A way to avoid this
     * behaviour using standard API is not available. Bug #4499199 explains the
     * reason for the behaviour.
     * 
     * A workaround is to call this method manually like so:
     * 
     * <pre>
     * Thread.getDefaultUncaughtExceptionHandler().uncaughtException(
     *     Thread.currentThread(), e);
     * </pre>
     * 
     * The above assumes that an object of this class was set as the default
     * uncaught exception handler.
     * 
     * @see <a
     *      href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4499199">
     *      SDN Bug #4499199</a>
     */
    public void uncaughtException(Thread t, final Throwable e) {
        final String message = EntryMessages
                .getString("DefaultExceptionHandler.message") //$NON-NLS-1$
                + PersistenceManager.getInstance().getBaseDirLocation()
                + EntryMessages.getString("DefaultExceptionHandler.message2"); //$NON-NLS-1$

        if (LOG.isFatalEnabled()) {
            LOG.fatal(message, e);
        }

        // try to email logs to support
        try {
            RemoteManager.getInstance().emailLogFileToSupport();
        } catch (Exception ex) {
            // do nothing - if an exception is thrown when trying to
            // email the logs then we just have to live with getting
            // the user to email them to us manually
        }
        if (EventQueue.isDispatchThread()) {
            disposeWindowsAndShowErrorDialog(e, message);
        } else {
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    disposeWindowsAndShowErrorDialog(e, message);
                }
            });
        }
    }

    private void disposeWindowsAndShowErrorDialog(Throwable e, String message) {
        try {
            disposeWindows();
            JFrame frame = new JFrame();
            String url = null;
            if (e.getCause()!=null && e.getCause() instanceof org.xml.sax.SAXException){
            	message = EntryMessages.getString("DefaultExceptionHandler.failedupdate1");
				try {
					url = Launcher.getClientProperties().getProperty("webstart.url");
					if(url!=null){ 
						message+=EntryMessages.getString("DefaultExceptionHandler.failedupdate2")+url;
						url+="/psygrid-data-client/app/psygrid-data-client.jnlp";
					}
					else {
						message+=EntryMessages.getString("DefaultExceptionHandler.failedupdate3");						
					}
				} catch (IOException e1) {
					message+=EntryMessages.getString("DefaultExceptionHandler.failedupdate3");
				}
            }
            JXErrorDialog.showDialog(frame, EntryMessages
                    .getString("DefaultExceptionHandler.title"), message, e); //$NON-NLS-1$
            if (url!=null){
					BrowserLauncher.openURL(url);
            }
            frame.dispose();
        } finally {
            System.exit(-1);
        }
    }

    private void disposeWindows() {
        disposeWindow(Frame.getFrames());
    }

    private void disposeWindow(Window[] windows) {
        for (Window window : windows) {
            disposeWindow(window.getOwnedWindows());
            window.dispose();
        }
    }
}
