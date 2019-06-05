package org.psygrid.collection.entry;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutionException;

import org.jdesktop.swingworker.SwingWorker;
import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.collection.entry.remote.RemoteManager;
import org.psygrid.collection.entry.remote.RemoteServiceFault;
import org.psygrid.collection.entry.security.EntrySAMLException;
import org.psygrid.collection.entry.util.ResetWaitRunnable;
import org.psygrid.data.model.hibernate.Record;
import org.psygrid.data.repository.RepositoryInvalidIdentifierFault;
import org.psygrid.data.repository.RepositoryNoConsentFault;
import org.psygrid.data.repository.RepositoryOutOfDateFault;
import org.psygrid.data.repository.transformer.TransformerFault;
import org.psygrid.data.utils.security.NotAuthorisedFault;

public class SubmitRecordStrategy extends SubmitDocumentStrategy {
	
	protected final Application application;

	public SubmitRecordStrategy(Application application) {
		super(application);
		this.application = application;
	}

	public void submit(final Record currentRecord) {

        SwingWorker<Object, Object> worker = new SwingWorker<Object, Object>(){
            @Override
            protected Object doInBackground() throws ConnectException, SocketTimeoutException, 
                    RepositoryOutOfDateFault, RepositoryNoConsentFault, 
                    RepositoryInvalidIdentifierFault, TransformerFault,
                    NotAuthorisedFault, IOException, RemoteServiceFault, 
                    EntrySAMLException   {
                RemoteManager.getInstance().saveRecord(currentRecord);
                PersistenceManager.getInstance().getExternalIdMap().remove(currentRecord.getIdentifier().getIdentifier());
                PersistenceManager.getInstance().getExternalIdMap().add(currentRecord.getIdentifier().getIdentifier(), currentRecord.getExternalIdentifier());
                PersistenceManager.getInstance().saveExternalIdMap();
                return null;
            }
            @Override
            protected void done() {
                try {
                    get();
                    new ResetWaitRunnable(application).run();
                    success();
                } catch(ExecutionException ee) {
                    new ResetWaitRunnable(application).run();
                    Throwable cause = ee.getCause();
                    if (cause instanceof ConnectException) {
                        ExceptionsHelper.handleConnectException(application, 
                                (ConnectException) cause);
                    } 
                    else if (cause instanceof SocketTimeoutException) {
                        ExceptionsHelper.handleSocketTimeoutException(
                                application, 
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
                    else if (cause instanceof TransformerFault) {
                        ExceptionsHelper.handleTransformerFault(application,
                                (TransformerFault) cause, true);
                    }
                    else if (cause instanceof NotAuthorisedFault) {
                        ExceptionsHelper.handleNotAuthorisedFault(application,
                                (NotAuthorisedFault) cause);
                    }
                    else if (cause instanceof RepositoryNoConsentFault) {
                        ExceptionsHelper.handleRepositoryNoConsentFault(application,
                                (RepositoryNoConsentFault) cause, null, true);
                    }
                    else if (cause instanceof RepositoryOutOfDateFault) {
                        ExceptionsHelper.handleRepositoryOutOfDateFault(
                                application,
                                (RepositoryOutOfDateFault) cause, null, true);
                    }
                    else if (cause instanceof RepositoryInvalidIdentifierFault) {
                        ExceptionsHelper.handleRepositoryInvalidIdentifierFault(
                                application, 
                                (RepositoryInvalidIdentifierFault) cause, null,
                                true);
                    }
                    else if (cause instanceof EntrySAMLException) {
                        ExceptionsHelper.handleEntrySAMLException(application,
                                (EntrySAMLException) cause);
                    }
                    else {
                        ExceptionsHelper.handleFatalException(cause);
                    }
                    failure();
                } catch (InterruptedException e) {
                    new ResetWaitRunnable(application).run();
                    ExceptionsHelper.handleFatalException(e);
                }
            }
        };

        worker.execute();
	}

}
