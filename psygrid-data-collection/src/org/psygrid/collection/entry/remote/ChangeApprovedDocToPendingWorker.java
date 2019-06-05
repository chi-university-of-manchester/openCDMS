package org.psygrid.collection.entry.remote;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutionException;

import org.jdesktop.swingworker.SwingWorker;
import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.DocumentStatus;
import org.psygrid.collection.entry.ExceptionsHelper;
import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.collection.entry.security.EntrySAMLException;
import org.psygrid.collection.entry.util.DdeHelper;
import org.psygrid.collection.entry.util.ResetWaitRunnable;
import org.psygrid.collection.entry.util.WaitRunnable;
import org.psygrid.common.identifier.InvalidIdentifierException;
import org.psygrid.data.model.hibernate.DocumentInstance;
import org.psygrid.data.model.hibernate.DocumentOccurrence;
import org.psygrid.data.model.hibernate.Record;
import org.psygrid.data.utils.security.NotAuthorisedFault;

public class ChangeApprovedDocToPendingWorker extends SwingWorker<Object, Object>{

	private Application application;
	
	public ChangeApprovedDocToPendingWorker(Application application){
		this.application = application;
	}
	
	@Override
	protected Object doInBackground() throws ConnectException, SocketTimeoutException,
    		NotAuthorisedFault, IOException, RemoteServiceFault,
			EntrySAMLException, InvalidIdentifierException {

		new WaitRunnable(application).run();

		DocumentInstance docInstance = application.getModel().getCurrentDocOccurrenceInstance();
		
		RemoteManager.getInstance().changeDocumentInstanceStatus(docInstance,
				DocumentStatus.PENDING);
		PersistenceManager.getInstance().changeLocalDocInstanceStatus(docInstance, DocumentStatus.PENDING);
		
		//DUAL DATA ENTRY
		if ( null != docInstance.getRecord().getSecondaryRecord() ){
			
			//record has a secondary for this instance
			Record secRecord = RemoteManager.getInstance().getRecordSummary(docInstance.getRecord().getSecondaryIdentifier());
			
			DocumentInstance secDocInst = DdeHelper.findDocInstForSecondary(docInstance, secRecord);
		    RemoteManager.getInstance().changeDocumentInstanceStatus(secDocInst,
		            DocumentStatus.PENDING);
		    PersistenceManager.getInstance().changeLocalDocInstanceStatus(secDocInst, DocumentStatus.PENDING);
		
		}
		
		return null;
	}
	
    @Override
    protected void done() {
        try {
    		new ResetWaitRunnable(application).run();
    		get();
            DocumentOccurrence docOcc = application.getModel().getCurrentDocOccurrence();
            application.clear(false);
            application.setSelectedDocOccurrence(docOcc, DocumentStatus.PENDING);
        } catch (InterruptedException e) {
            ExceptionsHelper.handleInterruptedException(e);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof ConnectException) {
                ExceptionsHelper.handleConnectException(
                        application,
                        (ConnectException) cause);
            } else if (cause instanceof SocketTimeoutException) {
                ExceptionsHelper.handleSocketTimeoutException(
                        application, 
                        (SocketTimeoutException) cause);
            } else if (cause instanceof IOException) {
                ExceptionsHelper.handleIOException(
                        application,
                        (IOException) cause, false);
            } else if (cause instanceof NotAuthorisedFault) {
                ExceptionsHelper.handleNotAuthorisedFault(
                        application,
                        (NotAuthorisedFault) cause);
            } else if (cause instanceof RemoteServiceFault) {
                ExceptionsHelper.handleRemoteServiceFault(
                        application,
                        (RemoteServiceFault) cause);
            } else if (cause instanceof EntrySAMLException) {
                ExceptionsHelper.handleEntrySAMLException(
                        application,
                        (EntrySAMLException) cause);
            } else {
                ExceptionsHelper.handleFatalException(cause);
            }
        }
    }

}
