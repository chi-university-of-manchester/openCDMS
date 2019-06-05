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

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingworker.SwingWorker;
import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.ExceptionsHelper;
import org.psygrid.collection.entry.persistence.IdentifierData;
import org.psygrid.collection.entry.persistence.IdentifiersList;
import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.collection.entry.persistence.RecordsList;
import org.psygrid.collection.entry.persistence.SecondaryIdentifierMap;
import org.psygrid.collection.entry.persistence.RecordsList.Item;
import org.psygrid.collection.entry.remote.RemoteManager;
import org.psygrid.collection.entry.remote.RemoteServiceFault;
import org.psygrid.collection.entry.security.DecryptionException;
import org.psygrid.collection.entry.security.EntrySAMLException;
import org.psygrid.collection.entry.util.ResetWaitRunnable;
import org.psygrid.common.identifier.InvalidIdentifierException;
import org.psygrid.data.model.hibernate.Record;import org.psygrid.data.repository.DuplicateDocumentsFault;
import org.psygrid.data.repository.RepositoryInvalidIdentifierFault;
import org.psygrid.data.repository.RepositoryNoConsentFault;
import org.psygrid.data.repository.RepositoryOutOfDateFault;
import org.psygrid.data.repository.transformer.TransformerFault;
import org.psygrid.data.utils.security.NotAuthorisedFault;

/**
 * Worker class to perform linking of two records for data replication, or
 * synchronization of two records that are already linked.
 * <p>
 * The process is as follows. First, the primary and secondary records
 * are retrieved from the data repository as appropriate. Pre-linking checks 
 * are performed to see if there are any conflicts between document instances
 * in the records (e.g. document instances for equivalent documents existing
 * in both records already). If conflicts are present then dialogs are
 * presented to the user informing them, and in some cases they must choose 
 * how to proceed, or cancel the linking process entirely.
 * <p>
 * The linking of the two records is then performed. The linked records are 
 * then persisted locally and/or saved back to the repository, depending
 * upon what had to be done during the linking. Finally the message that will
 * be displayed to the user on completion is generated.
 * 
 * @author Rob Harper
 *
 */
public class LinkRecordsWorker extends SwingWorker<String, Object> {
	
	private static final Log LOG = LogFactory.getLog(LinkRecordsWorker.class);
	
	private boolean synchronize;
	private final Application application;
	
	private final boolean reverseCopy;
	private final boolean incompleteReverseCopy;
	private final Record secRecord;
	private Record localPrimRecord;
	private final Record remotePrimRecord;
	private Record localPrimIncompleteRecord;
	private final Record remoteSecRecord;
	private final Record localSecIncompleteRecord;
	
	public LinkRecordsWorker(boolean newRecord, boolean synchronize, boolean reverseCopy, 
			boolean incompleteReverseCopy, Application application, Record localPrimRecord,
			Record remotePrimRecord, Record localPrimIncompleteRecord, Record remoteSecRecord,
			Record localSecIncompleteRecord, Record secRecord ){
		this.synchronize = synchronize;
		this.reverseCopy = reverseCopy;
		this.incompleteReverseCopy = incompleteReverseCopy;
		this.application = application;
		this.secRecord = secRecord;
		this.localPrimRecord = localPrimRecord;
		this.remotePrimRecord = remotePrimRecord;
		this.localPrimIncompleteRecord = localPrimIncompleteRecord;
		this.remoteSecRecord = remoteSecRecord;
		this.localSecIncompleteRecord = localSecIncompleteRecord;
	}
	
    @Override
    protected String doInBackground() throws ConnectException, SocketTimeoutException,
            NotAuthorisedFault, IOException, RemoteServiceFault,
            EntrySAMLException, DecryptionException, InvalidIdentifierException,
            RepositoryOutOfDateFault, RepositoryInvalidIdentifierFault, RepositoryNoConsentFault, 
            TransformerFault, DuplicateDocumentsFault {

    	LOG.info("Linking records "+remotePrimRecord.getIdentifier().getIdentifier()+" and "+secRecord.getIdentifier().getIdentifier()+"...");
    	
    	PersistenceManager pManager = PersistenceManager.getInstance();

		LOG.info("Options: reverseCopy="+reverseCopy+"; incompleteReverseCopy="+incompleteReverseCopy);
    	
    	//copy the documents involved in dual data entry into the secondary
    	DdeCopyResult result = LinkRecordsHelper.ddeCopy(
    			localPrimRecord, remotePrimRecord, localPrimIncompleteRecord, 
    			secRecord, remoteSecRecord, localSecIncompleteRecord, reverseCopy, incompleteReverseCopy);
    	
    	LOG.info("Result: savePrimaryLocalIncompRecord="+result.isSavePrimaryLocalIncompRecord()+
    			"; savePrimaryLocalRecord="+result.isSavePrimaryLocalRecord()+
    			"; savePrimaryRemoteRecord="+result.isSavePrimaryRemoteRecord()+
    			"; saveSecondaryLocalIncompRecord="+result.isSaveSecondaryLocalIncompRecord()+
    			"; saveSecondaryRemoteRecord="+result.isSaveSecondaryRemoteRecord());
    	
    	//either save the remote primary record, or at least update its secondary
    	//identifier property in the repository
    	if ( result.isSavePrimaryRemoteRecord() ){
    		LOG.info("Saving primary record");
    		remotePrimRecord.setSecondaryIdentifier(secRecord.getIdentifier().getIdentifier());
    		RemoteManager.getInstance().saveRecord(remotePrimRecord);
    	}
    	else{
    		//update the remote primary record to store the identifier of the secondary record.
    		RemoteManager.getInstance().updateSecondaryIdentifier(remotePrimRecord, secRecord.getIdentifier().getIdentifier());
    	}
    		
    	//change the status of document instances newly added to the primary remote
    	//record (after reverse copy from secondary).
    	List<Long> ccdisResult = RemoteManager.getInstance().changeCompletedDocInstancesStatus(remotePrimRecord, result.getDocsToSetToPending());
    	if ( !ccdisResult.isEmpty() ){
			throw new RemoteServiceFault("The statuses of one or more documents could not be changed " +
			"to Complete; please see the server logs for more details.");
    	}
    	
        //upload the remote secondary record if there is one
        //and it has been modified
        if ( result.isSaveSecondaryRemoteRecord() && null != remoteSecRecord ){
    		LOG.info("Saving secondary record");
        	remoteSecRecord.setPrimaryIdentifier(remotePrimRecord.getIdentifier().getIdentifier());
        	RemoteManager.getInstance().saveRecord(remoteSecRecord);
        	RemoteManager.getInstance().synchronizeDocumentStatusesWithPrimary(remoteSecRecord);
        }
        else if ( null != remoteSecRecord ){
    		//update the remote secondary record to store the identifier of the priamry record.
    		RemoteManager.getInstance().updatePrimaryIdentifier(remoteSecRecord, remotePrimRecord.getIdentifier().getIdentifier());
        }
        
    	//update identifiers list
        synchronized (pManager){
            IdentifiersList idsList = pManager.getIdentifiers();
            IdentifierData idData = idsList.get(secRecord.getIdentifier());
            if (idData != null && (!idData.isUsed())) {
                idData.setUsed(true);
                pManager.saveIdentifiers();
            }
            pManager.saveSecondaryIdentifierMap();
        }
        
    	if ( result.isSavePrimaryLocalRecord() ){
    		localPrimRecord = result.getPrimaryLocalRecord();
    	}
    	if ( result.isSavePrimaryLocalIncompRecord() ){
    		localPrimIncompleteRecord = result.getPrimaryLocalIncompRecord();
    	}

        //Make sure all the records correctly reference their primary or secondary
        secRecord.setPrimaryIdentifier(remotePrimRecord.getIdentifier().getIdentifier());
    	if ( result.isSaveSecondaryLocalIncompRecord() && null != localSecIncompleteRecord ){
    		localSecIncompleteRecord.setPrimaryIdentifier(remotePrimRecord.getIdentifier().getIdentifier());
    	}
    	if ( null != localPrimRecord ){
    		localPrimRecord.setSecondaryIdentifier(secRecord.getIdentifier().getIdentifier());
    	}
    	if ( null != localPrimIncompleteRecord ){
    		localPrimIncompleteRecord.setSecondaryIdentifier(secRecord.getIdentifier().getIdentifier());
    	}

    	DuplicateDocumentsFault secRecordDdf = null;
    	DuplicateDocumentsFault localSecIncompleteRecordDdf = null;
    	DuplicateDocumentsFault localPrimRecordDdf = null;
    	DuplicateDocumentsFault localPrimIncompleteRecordDdf = null;
        if ( pManager.getData().isAlwaysOnlineMode() ){
        	/*
        	 * Always online mode - commit everything straight to the repository
        	 */
        	
        	//complete secondary record
        	try{
        		RemoteManager.getInstance().commit(pManager.getData(), secRecord, true, true);
        	}
        	catch(DuplicateDocumentsFault ddf){
        		secRecordDdf = ddf;
        	}
        	//incomplete secondary record
	    	if ( result.isSaveSecondaryLocalIncompRecord() && null != localSecIncompleteRecord ){
	    		try{
	    			RemoteManager.getInstance().commit(pManager.getData(), localSecIncompleteRecord, false, true);
	    		}
	        	catch(DuplicateDocumentsFault ddf){
	        		localSecIncompleteRecordDdf = ddf;
	        	}
	    	}
	    	//complete primary record
	    	if ( null != localPrimRecord ){
	    		try{
	    			RemoteManager.getInstance().commit(pManager.getData(), localPrimRecord, true, true);
	    		}
	        	catch(DuplicateDocumentsFault ddf){
	        		localPrimRecordDdf = ddf;
	        	}
	    	}
	    	//incomplete primary record 
	    	if ( null != localPrimIncompleteRecord ){
	    		try{
	    			RemoteManager.getInstance().commit(pManager.getData(), localPrimIncompleteRecord, false, true);
	    		}
	        	catch(DuplicateDocumentsFault ddf){
	        		localPrimIncompleteRecordDdf = ddf;
	        	}
	    	}

	    	//update the local maps for the secondary record - this is done in 
	    	//PersistenceManager#saveRecord when we are saving the records to disk 
	    	//for online/offline mode
	    	synchronized (pManager) {
				if ( pManager.getConsentMap().addRecordNoOverwrite(
						secRecord.getIdentifier().getIdentifier(), 
						secRecord.getAllConsents()) ){
					pManager.saveConsentMap();
				}
				if ( pManager.getRecordStatusMap().addRecordNoOverwrite(
						secRecord.getIdentifier().getIdentifier(),
						secRecord.getStatus()) ){
					pManager.saveRecordStatusMap();
				}
				if ( secRecord.getDataSet().isExternalIdUsed() ){
					if ( pManager.getExternalIdMap().addNoOverwrite(
							secRecord.getIdentifier().getIdentifier(),
							secRecord.getExternalIdentifier()) ){
						pManager.saveExternalIdMap();
					}
				}
	    	}
	    	
        }
        else{
        	/*
        	 * Online + offline mode - save records to disk 
        	 */
        	
	        //save the local secondary record 
	    	synchronized (pManager) {
				pManager.saveRecord(secRecord, true);
			}
	    	
	    	//save the local incomplete secondary record (if necessary)
	    	if ( result.isSaveSecondaryLocalIncompRecord() && null != localSecIncompleteRecord ){
	    		synchronized (pManager){
	    			pManager.saveRecord(localSecIncompleteRecord, false);
	    		}
	    	}
	    	
	    	//if there is a local primary record then save it
	    	if ( null != localPrimRecord ){
	    		synchronized (pManager){
	    			pManager.saveRecord(localPrimRecord, true);
	    		}
	    	}
	    	
	    	//if there is a local incomplete primary record then save it
	    	if ( null != localPrimIncompleteRecord ){
	    		synchronized (pManager){
	    			pManager.saveRecord(localPrimIncompleteRecord, false);
	    		}
	    	}
    	
	        //tidy up - delete local records that are no longer required
	        synchronized (pManager) {
	        	List<Item> itemsToDelete = new ArrayList<Item>();
	        	RecordsList recordsList = pManager.getRecordsList();
	        	for ( Item item: recordsList.getItems() ){
	        		Record record = pManager.loadRecord(item);
	        		if ( 0 == record.numDocumentInstances() ){
	        			//just check that this isn't the local complete record for
	        			//the secondary in the case that it has never been committed - if so
	        			//we do not want to delete it here...
	        			if ( item.isReadyToCommit() && item.getIdentifier().getIdentifier().equals(secRecord.getIdentifier().getIdentifier()) && null == remoteSecRecord ){
	        				//do nothing - we don't want to add the empty local complete record
	        				//to the list of local records to delete as it needs to be committed.
	        			}
	        			else{
	        				itemsToDelete.add(item);
	        			}
	        		}
	        	}
	        	for ( Item item: itemsToDelete){
	                pManager.deleteRecord(item);
	                recordsList.removeItem(item);
	        	}
	        	pManager.saveRecordsList();
			}

        }
	    	
        //add to the secondary identifier map
        SecondaryIdentifierMap secondidmap = pManager.getSecondaryIdentifierMap();
        secondidmap.add(remotePrimRecord.getIdentifier().getIdentifier(), secRecord.getIdentifier().getIdentifier());
        PersistenceManager.getInstance().saveSecondaryIdentifierMap();
        
        //reset document instance statuses of secondaries as required
        ccdisResult = RemoteManager.getInstance().changeCompletedDocInstancesStatus(remoteSecRecord, result.getDocsToResetStatus());
    	if ( !ccdisResult.isEmpty() ){
			throw new RemoteServiceFault("The statuses of one or more documents could not be changed " +
			"to Complete; please see the server logs for more details.");
    	}

        //refresh record status map
        RemoteManager.getInstance().updateRecordStatusOnly(remotePrimRecord.getDataSet(), remotePrimRecord);
        RemoteManager.getInstance().updateRecordStatusOnly(secRecord.getDataSet(), secRecord);
        
        //format duplicate documents message
        StringBuilder secDdf = null;;
        if ( null != secRecordDdf && null != localSecIncompleteRecordDdf ){
        	secDdf = new StringBuilder();
        	secDdf.append(Messages.getString("LinkRecordsWorker.secRecordDuplicates"));
        	if ( null != secRecordDdf ){
        		secDdf.append(secRecordDdf.getDuplicateList());
        		secDdf.append("\n");
        	}
        	if ( null != localSecIncompleteRecordDdf ){
        		secDdf.append(localSecIncompleteRecordDdf.getDuplicateList());
        		secDdf.append("\n");
        	}
        }
        StringBuilder primDdf = null;
        if ( null != localPrimRecordDdf && null != localPrimIncompleteRecordDdf ){
        	primDdf = new StringBuilder();
        	primDdf.append(Messages.getString("LinkRecordsWorker.primRecordDuplicates"));
        	if ( null != localPrimRecordDdf ){
        		primDdf.append(localPrimRecordDdf.getDuplicateList());
        		primDdf.append("\n");
        	}
        	if ( null != localPrimIncompleteRecordDdf ){
        		primDdf.append(localPrimIncompleteRecordDdf.getDuplicateList());
        		primDdf.append("\n");
        	}
        }
        
    	//Format message to display in info dialog to user
        StringBuilder message = new StringBuilder();
    	if ( 0 == result.getDocsNotCopied().size() ){
    		if ( synchronize ){
    			message.append(Messages.getString("LinkRecordsWorker.successfulSyncMessage_p1"));
    			message.append(remotePrimRecord.getIdentifier().getIdentifier());
    			message.append(Messages.getString("LinkRecordsWorker.successfulSyncMessage_p2"));
    			message.append(secRecord.getIdentifier().getIdentifier());
    			message.append(Messages.getString("LinkRecordsWorker.successfulSyncMessage_p3"));
    			if ( null != primDdf ){
    				message.append(primDdf.toString());
    			}
    			if ( null != secDdf ){
    				message.append(secDdf.toString());
    			}
    			if ( !pManager.getData().isAlwaysOnlineMode() ){
    				message.append(Messages.getString("LinkRecordsWorker.successfulSyncMessage_p4"));
    			}
    		}
    		else{
    			message.append(Messages.getString("LinkRecordsWorker.successfulLinkMessage_p1"));
    			message.append(secRecord.getIdentifier().getIdentifier());
    			message.append(Messages.getString("LinkRecordsWorker.successfulLinkMessage_p2"));
    			message.append(remotePrimRecord.getIdentifier().getIdentifier());
    			message.append(Messages.getString("LinkRecordsWorker.successfulLinkMessage_p3"));
    			if ( null != primDdf ){
    				message.append(primDdf.toString());
    			}
    			if ( null != secDdf ){
    				message.append(secDdf.toString());
    			}
    			if ( !pManager.getData().isAlwaysOnlineMode() ){
    				message.append(Messages.getString("LinkRecordsWorker.successfulLinkMessage_p4"));
    			}
    		}
    	}
    	else{
    		if ( synchronize ){
        		message.append(Messages.getString("LinkRecordsWorker.partSuccessfulSyncMessage_p1"));
        		message.append(remotePrimRecord.getIdentifier().getIdentifier());
        		message.append(Messages.getString("LinkRecordsWorker.partSuccessfulSyncMessage_p2"));
        		message.append(secRecord.getIdentifier().getIdentifier());
        		message.append(Messages.getString("LinkRecordsWorker.partSuccessfulSyncMessage_p3"));
    		}
    		else{
        		message.append(Messages.getString("LinkRecordsWorker.partSuccessfulLinkMessage_p1"));
        		message.append(secRecord.getIdentifier().getIdentifier());
        		message.append(Messages.getString("LinkRecordsWorker.partSuccessfulLinkMessage_p2"));
        		message.append(remotePrimRecord.getIdentifier().getIdentifier());
        		message.append(Messages.getString("LinkRecordsWorker.partSuccessfulLinkMessage_p3"));
    		}
    		message.append(Messages.getString("LinkRecordsWorker.docsCouldNotBeCopiedMessage_p1"));
    		message.append(secRecord.getIdentifier().getIdentifier());
    		message.append(Messages.getString("LinkRecordsWorker.docsCouldNotBeCopiedMessage_p2"));
    		for ( String s: result.getDocsNotCopied() ){
    			message.append(s+"\n");
    		}
			if ( null != primDdf ){
				message.append(primDdf.toString());
			}
			if ( null != secDdf ){
				message.append(secDdf.toString());
			}
    		if ( !pManager.getData().isAlwaysOnlineMode() ){
    			message.append(Messages.getString("LinkRecordsWorker.recommendCommitMessage"));
    		}
    	}
		return message.toString();    	
    }

    @Override
    protected void done() {
        try {
            String message = get();
            new ResetWaitRunnable(application).run();
            String title = null;
            if ( this.synchronize ){
            	title = Messages.getString("LinkRecordsWorker.recordsSynchronizedTitle");
            }
            else{
            	title = Messages.getString("LinkRecordsWorker.recordsLinkedTitle");
            }
            JOptionPane.showMessageDialog(application, message, title, JOptionPane.INFORMATION_MESSAGE);
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
}
