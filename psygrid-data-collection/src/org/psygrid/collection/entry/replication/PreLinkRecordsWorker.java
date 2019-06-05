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
package org.psygrid.collection.entry.replication;

import java.io.FileNotFoundException;
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
import org.psygrid.collection.entry.SwingWorkerExecutor;
import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.collection.entry.remote.RemoteManager;
import org.psygrid.collection.entry.remote.RemoteServiceFault;
import org.psygrid.collection.entry.security.DecryptionException;
import org.psygrid.collection.entry.security.EntrySAMLException;
import org.psygrid.collection.entry.util.ResetWaitRunnable;
import org.psygrid.collection.entry.util.WaitRunnable;
import org.psygrid.common.identifier.InvalidIdentifierException;
import org.psygrid.common.ui.WrappedJOptionPane;
import org.psygrid.data.model.hibernate.Record;
import org.psygrid.data.utils.security.NotAuthorisedFault;

/**
 * 
 * @author Rob Harper
 *
 */
public class PreLinkRecordsWorker extends SwingWorker<DdeCheckResult, Object> {

	private static final Log LOG = LogFactory.getLog(PreLinkRecordsWorker.class);

	private Record primRecSummary;
	private Record secRecord;
	private boolean newRecord;
	private boolean synchronize;
	private final Application application;
	
	private Record remotePrimRecord;
	private Record localPrimRecord;
	private Record localPrimIncompleteRecord;
	private Record remoteSecRecord;
	private Record localSecIncompleteRecord;
	
	public PreLinkRecordsWorker(Record primRecSummary, Record secRecord, boolean newRecord, Application application){
		this(primRecSummary, secRecord, newRecord, false, application);
	}

	public PreLinkRecordsWorker(Record primRecSummary, Record secRecord, boolean newRecord, boolean synchronize, Application application){
		this.primRecSummary = primRecSummary;
		this.secRecord = secRecord;
		this.newRecord = newRecord;
		this.synchronize = synchronize;
		this.application = application;
	}
	
	@Override
	protected DdeCheckResult doInBackground() throws SocketTimeoutException, ConnectException,
	EntrySAMLException, RemoteServiceFault, NotAuthorisedFault, IOException, DecryptionException, InvalidIdentifierException {
    	LOG.info("Pre-link checking for records "+primRecSummary.getIdentifier().getIdentifier()+" and "+secRecord.getIdentifier().getIdentifier()+"...");
    	
    	PersistenceManager pManager = PersistenceManager.getInstance();

    	//get the complete primary record from the repository
    	remotePrimRecord = RemoteManager.getInstance().getCompleteRecord(primRecSummary);
    	
    	//also need to see if there are any local complete and/or incomplete documents for 
    	//the primary record too
    	synchronized (pManager) {
    		try{
    			localPrimRecord = pManager.loadRecord(remotePrimRecord.getIdentifier(), true);
    		}
    		catch (FileNotFoundException ex){
    			//do nothing - just means that there is no local record with complete documents
    		}
    		try{
    			localPrimIncompleteRecord = pManager.loadRecord(remotePrimRecord.getIdentifier(), false);
    		}
    		catch (FileNotFoundException ex){
    			//do nothing - just means that there is no local record with complete documents
    		}
		}

    	//get the remote secondary record if applicable
    	if ( !newRecord ){
    		try{
    			remoteSecRecord = RemoteManager.getInstance().getCompleteRecord(secRecord.getIdentifier().getIdentifier());
    		}
    		catch(RemoteServiceFault rsf){
    			//No record exists in the repository for the selected secondary
    		}
    	}
    	
    	//finally, incomplete documents for local secondary record too!
    	synchronized (pManager) {
    		try{
    			localSecIncompleteRecord = pManager.loadRecord(secRecord.getIdentifier(), false);
    		}
    		catch (FileNotFoundException ex){
    			//do nothing - just means that there is no local record with complete documents
    		}
		}
    	
    	//check for conflicts between the primary and secondary records
    	return LinkRecordsHelper.ddeCheckBeforeCopy(
    			localPrimRecord, remotePrimRecord, localPrimIncompleteRecord, 
    			secRecord, remoteSecRecord, localSecIncompleteRecord);
	}

	@Override
	protected void done() {
        try {
            new ResetWaitRunnable(application).run();
            DdeCheckResult checkResult = get();
            
    		boolean reverseCopy = false;
    		boolean incompleteReverseCopy = false;
    		if ( checkResult.isNoPrimaryYesSeconday() ){
    			String message = Messages.getString("LinkRecordsWorker.docInstancesInSecondaryNoConflictMessage");
    			int result = JOptionPane.showConfirmDialog(application, message);
    			if ( JOptionPane.CANCEL_OPTION == result ){
    				showMessage("Operation cancelled");
    				return;
    			}
    			if ( JOptionPane.YES_OPTION == result ){
    				reverseCopy = true;
    			}
    		}
    		if ( checkResult.isYesIncompPrimYesSecondary() ){
    			String message = Messages.getString("LinkRecordsWorker.docInstancesInSecondaryConflictWithIncompletePrimaryMessage");
    			int result = JOptionPane.showConfirmDialog(application, message);
    			if ( JOptionPane.CANCEL_OPTION == result ){
    				showMessage("Operation cancelled");
    				return;
    			}
    			if ( JOptionPane.YES_OPTION == result ){
    				incompleteReverseCopy = true;
    			}
    		}
    		if ( checkResult.isYesPrimaryYesSecondary() && !synchronize ){
    			String message = Messages.getString("LinkRecordsWorker.docInstancesInSecondaryConflictWithCompletePrimaryDocumentsMessage");
    			int result = JOptionPane.showConfirmDialog(application, message, null, JOptionPane.OK_CANCEL_OPTION);
    			if ( JOptionPane.CANCEL_OPTION == result ){
    				showMessage("Operation cancelled");
    				return;
    			}
    		}
    		if ( checkResult.isNoPrimaryYesIncompSecondary() && !synchronize ){
    			String message = Messages.getString("LinkRecordsWorker.copyIncompleteSecondariesToPrimaryMessage");
    			int result = JOptionPane.showConfirmDialog(application, message, null, JOptionPane.OK_CANCEL_OPTION);
    			if ( JOptionPane.CANCEL_OPTION == result ){
    				showMessage("Operation cancelled");
    				return;
    			}
    		}

    		//if execution reaches this point then we proceed to actually doing the linking
            LinkRecordsWorker worker = new LinkRecordsWorker(newRecord, synchronize, reverseCopy, 
            		incompleteReverseCopy, application, localPrimRecord, 
        			remotePrimRecord, localPrimIncompleteRecord, remoteSecRecord, 
        			localSecIncompleteRecord, secRecord);
            new WaitRunnable(application).run();
            SwingWorkerExecutor.getInstance().execute(worker);
    		
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

	private void showMessage(String message){
        String title = null;
        if ( this.synchronize ){
        	title = Messages.getString("LinkRecordsWorker.recordsSynchronizedTitle");
        }
        else{
        	title = Messages.getString("LinkRecordsWorker.recordsLinkedTitle");
        }
		WrappedJOptionPane.showWrappedMessageDialog(
				application, message, title, WrappedJOptionPane.INFORMATION_MESSAGE);
	}
}
