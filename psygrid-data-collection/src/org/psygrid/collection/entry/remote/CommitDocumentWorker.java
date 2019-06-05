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
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.JXErrorDialog;
import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.EntryHelper;
import org.psygrid.collection.entry.EntryMessages;
import org.psygrid.collection.entry.ExceptionsHelper;
import org.psygrid.collection.entry.persistence.IdentifierData;
import org.psygrid.collection.entry.persistence.IdentifiersList;
import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.collection.entry.security.SecurityManager;
import org.psygrid.collection.entry.util.ResetWaitRunnable;
import org.psygrid.collection.entry.util.WaitRunnable;
import org.psygrid.data.model.hibernate.ChangeHistory;
import org.psygrid.data.model.hibernate.DocumentInstance;
import org.psygrid.data.model.hibernate.Record;
import org.psygrid.data.model.hibernate.Status;
import org.psygrid.data.repository.DuplicateDocumentsFault;
import org.psygrid.data.repository.RepositoryInvalidIdentifierFault;
import org.psygrid.data.repository.RepositoryNoConsentFault;
import org.psygrid.data.repository.RepositoryOutOfDateFault;
import org.psygrid.data.repository.transformer.TransformerFault;
import org.psygrid.data.utils.security.NotAuthorisedFault;

public class CommitDocumentWorker extends SynchronizeWorker<String> {

	private static final Log LOG = LogFactory.getLog(CommitDocumentWorker.class);
	
    private final DocumentInstance docInstance;
    private final boolean readyToSubmit;
    private final String identifier;
    private final Application theApp;
    
    public CommitDocumentWorker(Application application, DocumentInstance docInstance,
            boolean changeStatus) {
        super(application);
        this.docInstance = docInstance;
        this.readyToSubmit = changeStatus;
        this.identifier = docInstance.getRecord().getIdentifier().getIdentifier();
        this.theApp = application;
    }
    
    @Override
    protected String doInBackground() throws Exception {
        new WaitRunnable(application).run();
        addCommitProgressListener();
        //check for changes to the document and add provenance where necessary
        ChangeHistory change = docInstance.addToHistory(SecurityManager.getInstance().getUserName());
        docInstance.checkForChanges(change);
        Record record = docInstance.getRecord();
        PersistenceManager pManager = PersistenceManager.getInstance();
        synchronized (PersistenceManager.getInstance()) {
            IdentifiersList idsList = pManager.getIdentifiers();
            IdentifierData idData = idsList.get(record.getIdentifier());
            if (idData != null && (!idData.isUsed())) {
                idData.setUsed(true);
                pManager.saveIdentifiers();
            }
            try{
	            RemoteManager.getInstance().commit(docInstance, readyToSubmit);
	            Status status = pManager.updateRecordStatus(record, docInstance);
	            RemoteManager.getInstance().changeRecordStatus(record, status);
	            if ( readyToSubmit ){
		            String message = EntryHelper.doDdeCopy(record, docInstance, pManager);
		            if (message != null)
		                return message;
		        }
            }
            catch(DuplicateDocumentsFault ddf){
            	return ddf.getMessage();
            }
        }
        return null;
    }
    
    protected void success() {
        /* Empty implementation by default */
    }

    protected void failure() {
    	/* Empty implementation by default */
    }
    
    @Override
    protected void done() {
        try {
            new ResetWaitRunnable(application).run();
            String message = get();
            if ( null != message ){
                String title = "Warning";
                JOptionPane.showMessageDialog(application, message, title, JOptionPane.ERROR_MESSAGE);
            }
            success();
        } catch (InterruptedException e) {
            new ResetWaitRunnable(application).run();
            ExceptionsHelper.handleInterruptedException(e);
        } catch (ExecutionException e) {
            Throwable exception = e.getCause();
            if (exception instanceof RemoteServiceFault) {
                saveUncommitableDocument(exception);
            }
            else if (exception instanceof TransformerFault) {
    			saveUncommitableDocument(exception);
            }
            else if (exception instanceof RepositoryOutOfDateFault) {
                ExceptionsHelper.handleRepositoryOutOfDateFault(application,
                        (RepositoryOutOfDateFault) exception, identifier, false);
            }
            else if (exception instanceof RepositoryInvalidIdentifierFault) {
                ExceptionsHelper.handleRepositoryInvalidIdentifierFault(application,
                        (RepositoryInvalidIdentifierFault) exception, identifier,
                        false);
            }
            else if (exception instanceof NotAuthorisedFault) {
                ExceptionsHelper.handleNotAuthorisedFault(application,
                        (NotAuthorisedFault) exception);
            }
            else if (exception instanceof RepositoryNoConsentFault) {
                ExceptionsHelper.handleRepositoryNoConsentFault(application,
                        (RepositoryNoConsentFault) exception, identifier, false);
            }
            else if (exception instanceof ConnectException) {
                ExceptionsHelper.handleConnectException(application,
                        (ConnectException) exception);
            }
            else if (exception instanceof IOException) {
                ExceptionsHelper.handleIOException(application, (IOException) exception, false);
            }
            else {
                ExceptionsHelper.handleFatalException(exception);
            }
            failure();
        }
    }
    
    private void saveUncommitableDocument(Throwable t){
    	if (LOG.isErrorEnabled()) {
            LOG.error("Unrecoverable problem during commit; saving document locally in error state", t);
    	}
		String messageTitle = EntryMessages.getString("ApplicationModel.saveProblemTitle");
		String messageContent = EntryMessages.getString("ApplicationModel.unrecoverableSaveProblemMessage");
		JXErrorDialog.showDialog(application, messageTitle, messageContent, t);
		theApp.getModel().saveUncommitableDocument(theApp);
		try{
			RemoteManager.getInstance().emailLogFileToSupport();
		}catch(Exception e){
			//nothing for it
		}
    }
}
