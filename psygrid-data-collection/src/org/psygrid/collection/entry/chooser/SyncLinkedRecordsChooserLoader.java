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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;

import org.jdesktop.swingworker.SwingWorker;
import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.ExceptionsHelper;
import org.psygrid.collection.entry.SwingWorkerExecutor;
import org.psygrid.collection.entry.event.RecordSelectedEvent;
import org.psygrid.collection.entry.event.RecordSelectedListener;
import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.collection.entry.persistence.RecordsList;
import org.psygrid.collection.entry.remote.RemoteManager;
import org.psygrid.collection.entry.remote.RemoteServiceFault;
import org.psygrid.collection.entry.replication.PreLinkRecordsWorker;
import org.psygrid.collection.entry.security.DecryptionException;
import org.psygrid.collection.entry.security.EntrySAMLException;
import org.psygrid.collection.entry.ui.RecordsTableModel.Column;
import org.psygrid.collection.entry.util.RecordHelper;
import org.psygrid.collection.entry.util.ResetWaitRunnable;
import org.psygrid.collection.entry.util.WaitRunnable;
import org.psygrid.common.identifier.InvalidIdentifierException;
import org.psygrid.data.model.hibernate.Identifier;
import org.psygrid.data.model.hibernate.Record;
import org.psygrid.data.model.hibernate.RecordData;
import org.psygrid.data.utils.security.NotAuthorisedFault;

/**
 * Chooser Loader for the "Synchronize Linked Records" action.
 * <p>
 * A list of records that are linked to secondary records is retrieved
 * from the repository, and displayed in a dialog by their Identifier.
 * When a record is selected the process to synchronize is initiated.
 * 
 * @author Rob Harper
 *
 */
public class SyncLinkedRecordsChooserLoader extends RecordChooserLoader {

	public SyncLinkedRecordsChooserLoader(Application application) {
		super(application);
	}

	@Override
	protected List<String> doInBackground() throws ConnectException, SocketTimeoutException, IOException, NotAuthorisedFault, RemoteServiceFault, EntrySAMLException {
        new WaitRunnable(application).run();
        List<String> records = RemoteManager.getInstance().getLinkedRecords();
        return records;
	}

    @Override
    protected String getChooserTitle() {
        return "Select the record to synchronize.";
    }

    @Override
    protected EnumSet<Column> getEnumSet() {
        return EnumSet.of(Column.IDENTIFIER);
    }

	@Override
	protected RecordSelectedListener getRecordSelectedListener() {
        return new RecordSelectedListener(){
            public void recordSelected(RecordSelectedEvent recordSelectedEvent) {
            	synchronize(recordSelectedEvent.getIdentifier());
            }
        };
	}
	
	private void synchronize(final String identifier){
		
        SwingWorker<Record, Object> worker = new SwingWorker<Record, Object>() {
		    @Override
		    protected Record doInBackground() throws ConnectException, SocketTimeoutException,
		            IOException, NotAuthorisedFault, RemoteServiceFault,
		            EntrySAMLException, InvalidIdentifierException {
		        
		        new WaitRunnable(application).run();
				return RemoteManager.getInstance().getRecordSummary(identifier);		
		    }
		
		    @Override
		    protected void done() {
		        try {
		            final  Record  recordSummary = get();
		    		PersistenceManager pManager = PersistenceManager.getInstance();
		    		String secondaryId = recordSummary.getSecondaryIdentifier();
		    		Record  secondaryRecord = null;
		    		
		    		//get the local record for this identifier
		            List<RecordsList.Item> items = null;
		            try {
		            	items = pManager.getRecordsList().getItems();
		            } catch (IOException e) {
		            	ExceptionsHelper.handleIOException(application, e, false);
		            	return;
		            }
		            // Should never happen
		            catch (DecryptionException e) {
		            	ExceptionsHelper.handleFatalException(e);
		            }
		            
		            for (RecordsList.Item item : items) {
		            	Identifier identifier = item.getIdentifier();
		            	String identifierText = identifier.getIdentifier();
		            	if (identifierText.equals(secondaryId)) {
		            		try {
		            			Record  record = pManager.loadRecord(item);
		            			// We are looking to attach to a IRecord that has documents that
		            			// are ready to be committed.
		            			if (!item.isReadyToCommit()) {
		            				continue;
		            			}
		            			secondaryRecord = record;
		            			return;
		            		} catch (IOException e) {
		            			ExceptionsHelper.handleIOException(application, e, false);
		            			return;
		            		}
		            		// Should never happen
		            		catch (DecryptionException e) {
		            			ExceptionsHelper.handleFatalException(e);
		            			return;
		            		}
		            	}
		            }

		            try{
		            	secondaryRecord = RecordHelper.constructRecord(secondaryId, pManager);
		            }
		            catch(InvalidIdentifierException ex){
		        		//should never happen
		    			ExceptionsHelper.handleFatalException(ex);
		    			return;
		            }
		            catch(IOException ex){
		            	ExceptionsHelper.handleIOException(application, ex, false);
		            }
		            
		            //load the incomplete docs side of the record to get the schedule start date
		            //and copy it over into this record
		            try{
		            	Record  incompleteRecord = pManager.loadRecord(secondaryRecord.getIdentifier(),  false);
		            	RecordData rd = secondaryRecord.generateRecordData();
		            	rd.setScheduleStartDate(incompleteRecord.getScheduleStartDate());
		            	rd.setStudyEntryDate(incompleteRecord.getStudyEntryDate());
		            }
		    		catch(FileNotFoundException ex){
		    			//There is no locally stored incomplete record for this identifier
		    			//This is fine - just continue execution below
		    		}
		            catch(IOException ex){
		            	ExceptionsHelper.handleIOException(application, ex, false);
		            	return;
		            }
		            catch(DecryptionException ex){
		            	ExceptionsHelper.handleFatalException(ex);
		            	return;
		            }
		    		
		            //start the process of copying records to the secondary
		            new WaitRunnable(application).run();
		            PreLinkRecordsWorker worker = new PreLinkRecordsWorker(recordSummary, secondaryRecord, false, true, application);
		            SwingWorkerExecutor.getInstance().execute(worker);
		            
		        } catch (InterruptedException e) {
		            ExceptionsHelper.handleInterruptedException(e);
		        } catch (ExecutionException e) {
		            Throwable cause = e.getCause();
		            if (cause instanceof ConnectException) {
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
		            else if (cause instanceof RemoteServiceFault) {
		                ExceptionsHelper.handleRemoteServiceFault(application,
		                        (RemoteServiceFault) cause);
		            }
		            else if (cause instanceof NotAuthorisedFault) {
		                ExceptionsHelper.handleNotAuthorisedFault(application,
		                        (NotAuthorisedFault) cause);
		            }
		            else if (cause instanceof EntrySAMLException) {
		                ExceptionsHelper.handleEntrySAMLException(application,
		                        (EntrySAMLException) cause);
		            }
		            else {
		                ExceptionsHelper.handleFatalException(cause);
		            }
		        } finally {
		            new ResetWaitRunnable(application).run();
		        }
		    }
		};

		SwingWorkerExecutor.getInstance().execute(worker);
		
    }

    protected void showNoRecordsMessage(){
        String title = Messages.getString("SyncLinkedRecordsChooserLoader.noLinkedRecordsTitle");
        String message = Messages.getString("SyncLinkedRecordsChooserLoader.noLinkedRecordsMessage");
        JOptionPane.showMessageDialog(application, message, title, 
                JOptionPane.ERROR_MESSAGE);
    }


}
