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
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;

import org.jdesktop.swingworker.SwingWorker;
import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.ExceptionsHelper;
import org.psygrid.collection.entry.Selectable;
import org.psygrid.collection.entry.event.RecordSelectedListener;
import org.psygrid.collection.entry.remote.RemoteManager;
import org.psygrid.collection.entry.remote.RemoteServiceFault;
import org.psygrid.collection.entry.security.EntrySAMLException;
import org.psygrid.collection.entry.ui.RecordDialog;
import org.psygrid.collection.entry.ui.RecordsTableModel;
import org.psygrid.collection.entry.ui.RecordsTableModel.Column;
import org.psygrid.collection.entry.util.ResetWaitRunnable;
import org.psygrid.collection.entry.util.WaitRunnable;
import org.psygrid.common.identifier.InvalidIdentifierException;
import org.psygrid.data.utils.security.NotAuthorisedFault;

/**
 * Abstract class to facilitate the loading of a list of records,
 * the display of this in a dialog, and then the action upon the 
 * selection of a record from the list.
 * <p>
 * Concrete implementations of this class will provide the details
 * of how the list of records is obtained, the title of the dialog
 * and the listener that reacts to a record bein selected.
 * 
 * @author Rob Harper
 *
 */
public abstract class RecordChooserLoader extends SwingWorker<List<String>, Object> {

    protected Application application;
    
    public RecordChooserLoader(Application application){
        this.application = application;
    }
    
    @Override
    protected List<String> doInBackground() throws ConnectException, SocketTimeoutException, IOException, 
            NotAuthorisedFault, RemoteServiceFault, EntrySAMLException, InvalidIdentifierException {
        new WaitRunnable(application).run();
        return RemoteManager.getInstance().getRecordSummaries();
    }

    @Override
    protected void done() {
        try {
            List<String> identifiers = get();
            if (identifiers.size() == 0) {
                new ResetWaitRunnable(application).run();
                showNoRecordsMessage();
                return;
            }
            
            List<Selectable<String>> selectableIdentifiers = new ArrayList<Selectable<String>>();
            for ( String identifier: identifiers ){
            	selectableIdentifiers.add(new Selectable<String>(identifier, false));
            }
            
            EnumSet<Column> columns = getEnumSet();
            RecordsTableModel tableModel = new RecordsTableModel(selectableIdentifiers, 
                    columns);
            String message = this.getChooserTitle();
            RecordDialog recordDialog = new RecordDialog(application, tableModel, message, false);
            recordDialog.addRecordSelectedListener(getRecordSelectedListener());
            new ResetWaitRunnable(application).run();
            recordDialog.setVisible(true);
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

    /**
     * Get the title to be used for the dialog.
     * 
     * @return The title.
     */
    protected abstract String getChooserTitle();
    
    /**
     * Get the columns to display in the dialog.
     * <p>
     * By default we just show the Record's identifier, in specific
     * cases this may be overridden to show other columns.
     * 
     * @return
     */
    protected EnumSet<Column> getEnumSet() {
        return EnumSet.of(Column.IDENTIFIER);
    }

    /**
     * Get the listener that reacts when a record is selected.
     * 
     * @return
     */
    protected abstract RecordSelectedListener getRecordSelectedListener();
    
    protected void showNoRecordsMessage(){
        String title = Messages.getString("RecordChooserLoader.noRecordsTitle");
        String message = Messages.getString("RecordChooserLoader.noRecordsMessage");
        JOptionPane.showMessageDialog(application, message, title, 
                JOptionPane.ERROR_MESSAGE);
    }
}
