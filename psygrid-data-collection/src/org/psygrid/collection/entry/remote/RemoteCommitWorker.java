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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.collection.entry.ExceptionsHelper;
import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.collection.entry.persistence.RecordsList;
import org.psygrid.collection.entry.persistence.RecordsListWrapper;
import org.psygrid.collection.entry.persistence.SecondaryIdentifierMap;
import org.psygrid.collection.entry.persistence.RecordsList.Item;
import org.psygrid.collection.entry.security.DecryptionException;
import org.psygrid.collection.entry.security.EntrySAMLException;
import org.psygrid.collection.entry.ui.CommitDialog;
import org.psygrid.collection.entry.util.ResetWaitRunnable;
import org.psygrid.collection.entry.util.WaitRunnable;
import org.psygrid.data.repository.DuplicateDocumentsFault;
import org.psygrid.data.repository.RepositoryInvalidIdentifierFault;
import org.psygrid.data.repository.RepositoryNoConsentFault;
import org.psygrid.data.repository.RepositoryOutOfDateFault;
import org.psygrid.data.repository.transformer.TransformerFault;
import org.psygrid.data.utils.security.NotAuthorisedFault;

public class RemoteCommitWorker extends SynchronizeWorker<Map<RecordsListWrapper.Item, Exception>> {

	private static final Log LOG = LogFactory.getLog(RemoteCommitWorker.class);
	
    private final List<RecordsListWrapper.Item> itemsToCommit;
    
    public RemoteCommitWorker(JFrame application, 
            List<RecordsListWrapper.Item> itemsToCommit) {
        super(application);
        this.itemsToCommit = itemsToCommit;
    }
    
    
    @Override
    protected Map<RecordsListWrapper.Item, Exception> doInBackground() throws ConnectException, SocketTimeoutException, 
            IOException, DecryptionException, EntrySAMLException {
        new WaitRunnable(application).run();
        addCommitProgressListener();
        Map<RecordsListWrapper.Item, Exception> exceptionsMap = new HashMap<RecordsListWrapper.Item, Exception>(itemsToCommit.size());
        PersistenceManager pManager = PersistenceManager.getInstance();
        SecondaryIdentifierMap sidMap = null;
        synchronized (pManager) {
			sidMap = pManager.getSecondaryIdentifierMap();
		}
        //make three passes through the list of items to commit
        //First pass, commit records with neither a primary or secondary partner
        //Second pass, commit records with a secondary partner (i.e. primary records)
        //Third pass, commit records with a primary partner (i.e. secondary records)
        for ( int i=0; i<3; i++ ){
        	LOG.info("Commit pass "+i);
	        for (RecordsListWrapper.Item itemToCommit: itemsToCommit ) {
	        	String primId = sidMap.getPrimary(itemToCommit.getIdentifier().getIdentifier());
	        	String secId = sidMap.get(itemToCommit.getIdentifier().getIdentifier());
	        	boolean commit = true;
	        	if ( 0 == i ){
	        		//First pass, just looking for records with no associated primary or
	        		//secondary i.e. records not involved in data replication
	        		if ( null != primId || null != secId ){
	        			commit = false;
	        		}
	        	}
	        	else if ( 1 == i ){
	        		//Second pass, just looking for primary records i.e. records with a 
	        		//non-null secondary
	        		if ( null == secId ){
	        			commit = false;
	        		}
	        	}
	        	else if ( 2 == i ){
	        		//Third pass, just looking for secondary records i.e. records with a 
	        		//non-null primary
	        		if ( null == primId ){
	        			commit = false;
	        		}
	        	}
	        	if ( commit ){
		            try {
		            	if ( LOG.isInfoEnabled() ){
		            		LOG.info("Committing item "+itemToCommit.getIdentifier().getIdentifier());
		            	}
		                RemoteManager.getInstance().commit(itemToCommit);
		                itemToCommit.setResult(RecordsList.Result.SUCCESS);
		            } catch (DuplicateDocumentsFault e){
		            	itemToCommit.setResult(RecordsList.Result.DUPLICATES);
		            	exceptionsMap.put(itemToCommit, e);
		            } catch (RepositoryOutOfDateFault e) {
		                exceptionsMap.put(itemToCommit, e);
		            } catch (TransformerFault e) {
		                exceptionsMap.put(itemToCommit, e);
		            } catch (NotAuthorisedFault e) {
		                exceptionsMap.put(itemToCommit, e);
		            } catch (RepositoryNoConsentFault e) {
		                exceptionsMap.put(itemToCommit, e);
		            } catch (RepositoryInvalidIdentifierFault e) {
		                exceptionsMap.put(itemToCommit, e);
		            } catch (RemoteServiceFault e) {
		                exceptionsMap.put(itemToCommit, e);
		            }
	        	}
	        }
        }
        
        return exceptionsMap;
    }

    @Override
    protected void done() {
        new ResetWaitRunnable(application).run();
        try {
            Map<RecordsListWrapper.Item, Exception> exceptionsMap = get();
            if (exceptionsMap.size() != 0) {
            	
            	//At this point, the user requires a summary dialog of records that committed successfully and
            	//those that failed. That way, they at least know that the commit was a partial success.
            	//The dialog can be based on the commit dialog itself...
            	CommitDialog dialog = new CommitDialog(application, itemsToCommit, true);
            	dialog.setExceptionsMap(exceptionsMap);
            	dialog.setVisible(true);
                return;
            }
            String title = Messages.getString("RemoteCommitWorker.commitSuccessfulTitle"); //$NON-NLS-1$
            String message = Messages.getString("RemoteCommitWorker.commitSuccessfulMessage"); //$NON-NLS-1$
            JOptionPane.showMessageDialog(application, message, title,
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (ExecutionException ee) {
            Throwable cause = ee.getCause();
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
            else if (cause instanceof EntrySAMLException) {
                ExceptionsHelper.handleEntrySAMLException(application,
                        (EntrySAMLException) cause);
            }
            else {
                ExceptionsHelper.handleFatalException(cause);
            }
        } catch (InterruptedException e) {
            ExceptionsHelper.handleInterruptedException(e);
        }
    }
    
}
