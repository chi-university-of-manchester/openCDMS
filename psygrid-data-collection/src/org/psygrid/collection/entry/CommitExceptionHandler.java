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

import java.awt.Component;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JOptionPane;

import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.collection.entry.persistence.RecordsListWrapper;
import org.psygrid.collection.entry.remote.RemoteManager;
import org.psygrid.collection.entry.remote.RemoteServiceFault;
import org.psygrid.collection.entry.security.DecryptionException;
import org.psygrid.data.model.hibernate.DocumentInstance;
import org.psygrid.data.model.hibernate.Record;
import org.psygrid.data.repository.DuplicateDocumentsFault;
import org.psygrid.data.repository.RepositoryInvalidIdentifierFault;
import org.psygrid.data.repository.RepositoryNoConsentFault;
import org.psygrid.data.repository.RepositoryOutOfDateFault;
import org.psygrid.data.repository.transformer.TransformerFault;
import org.psygrid.data.utils.security.NotAuthorisedFault;

/**
 * This is class reports exceptions that occurred during a commit, and directs the user to take any
 * required action. This funcionality was formerly contained within the RemoteCommitWorker class.
 * @author williamvance
 *
 */
public class CommitExceptionHandler {

	private Application application;

	public CommitExceptionHandler(Application application){
		this.application = application;	
	}

	public void handleExceptions(Component parentComponent, Map<RecordsListWrapper.Item, Exception> exceptionMap){

		Iterator<Entry<RecordsListWrapper.Item, Exception>> mapIterator = exceptionMap.entrySet().iterator();
		do{
			Map.Entry<RecordsListWrapper.Item, Exception> exceptionEntry = mapIterator.next();

			handleException(parentComponent, exceptionEntry);

		}while(mapIterator.hasNext());
	}

	public void handleException(Component parentComponent, Map.Entry<RecordsListWrapper.Item, Exception> exceptionEntry){
		Exception exception = exceptionEntry.getValue();
		RecordsListWrapper.Item item = exceptionEntry.getKey();
		String identifier = item.getIdentifier().getIdentifier();
		if (exception instanceof RemoteServiceFault) {
			ExceptionsHelper.handleRemoteServiceFault(parentComponent,
					(RemoteServiceFault) exception);
		}
		else if (exception instanceof RepositoryOutOfDateFault) {
			handleRepositoryOutOfDateFault(parentComponent,
					(RepositoryOutOfDateFault) exception, item, false);
		}
		else if (exception instanceof RepositoryInvalidIdentifierFault) {
			ExceptionsHelper.handleRepositoryInvalidIdentifierFault(parentComponent,
					(RepositoryInvalidIdentifierFault) exception, identifier,
					false);
		}
		else if (exception instanceof NotAuthorisedFault) {
			handleNotAuthorisedFault(parentComponent,
					(NotAuthorisedFault) exception, identifier, false);
		}
		else if (exception instanceof RepositoryNoConsentFault) {
			ExceptionsHelper.handleRepositoryNoConsentFault(parentComponent,
					(RepositoryNoConsentFault) exception, identifier, false);
		}
		else if (exception instanceof TransformerFault) {
			ExceptionsHelper.handleTransformerFault(parentComponent,
					(TransformerFault) exception, false);
		}
		else if (exception instanceof DuplicateDocumentsFault ){
			ExceptionsHelper.handleDuplicateDocumentsFault(parentComponent,
					(DuplicateDocumentsFault) exception);
		}
		else {
			ExceptionsHelper.handleFatalException(exception);
		}
	}

	private void handleRepositoryOutOfDateFault(Component parentComponent,
			RepositoryOutOfDateFault e, RecordsListWrapper.Item item, boolean singleRecord)  {
		String message = ExceptionsHelper.getCommitPrefixMessage(singleRecord);

		String title = EntryMessages.getString("repositoryOutOfDateTitle"); //$NON-NLS-1$
		message += EntryMessages.getString("CommitExceptionHandler.repositoryOutOfDateMessage_p1"); //$NON-NLS-1$
		message += EntryMessages.getString("CommitExceptionHandler.repositonyOutOfDateMessage_p2") + 
		item.getIdentifier().getIdentifier();
		int result = JOptionPane.showConfirmDialog(parentComponent, message, 
				title, JOptionPane.YES_NO_OPTION);
		if (result == JOptionPane.YES_OPTION) {
			PersistenceManager pManager = PersistenceManager.getInstance();
			synchronized (pManager) {
				try {
					pManager.deleteRecord(item);
					pManager.getRecordsList().removeItem(item);
					pManager.getRecordStatusMap().deleteRecord(item.getIdentifier().getIdentifier());
					pManager.saveRecordsList();
					pManager.saveRecordStatusMap();
					if (application != null) {
						application.getModel().clear(true);
						application.removeElements(true);
						application.refreshContentPane();
						PersistenceManager.getInstance().deleteUnfinishedDocumentInstance();
						PersistenceManager.getInstance().deleteAutoSaveDocumentInstance();
					}
					try {
						Record record = RemoteManager.getInstance().getRecordSummary(item.getIdentifier().getIdentifier());
						pManager.getRecordStatusMap().addRecord(record.getIdentifier().getIdentifier(), record.getStatus());
						for (DocumentInstance inst: ((Record)record).getDocInstances()) {
							pManager.getRecordStatusMap().addDocStatus(record.getIdentifier().getIdentifier(),
									inst.getOccurrence(), inst.getStatus());
						}
						pManager.saveRecordStatusMap();
					}
					catch (Exception ex) {
						ExceptionsHelper.handleException(parentComponent, "title", ex, "message", false);
					} 
					
					title = EntryMessages.getString("CommitExceptionHandler.successfulRecordDeletionTitle"); //$NON-NLS-1$
					message = EntryMessages.getString("CommitExceptionHandler.successfulRecordDeletionMessage"); //$NON-NLS-1$
					JOptionPane.showMessageDialog(parentComponent, message, title,
							JOptionPane.INFORMATION_MESSAGE);
				} catch (IOException ioe) {
					ExceptionsHelper.handleIOException(parentComponent, ioe, false);
				} // Should never happen 
				catch (DecryptionException de) {
					ExceptionsHelper.handleFatalException(de);
				}
			}
		}
	}

	private void handleNotAuthorisedFault(Component parentComponent, 
			NotAuthorisedFault e, String identifier, boolean singleFailure) {
		String message = ExceptionsHelper.getCommitPrefixMessage(singleFailure);
		String title = EntryMessages.getString("notAuthorisedTitle"); //$NON-NLS-1$
		message += EntryMessages.getString("notAuthorisedMessage"); //$NON-NLS-1$
		message += ExceptionsHelper.getCommitSuffixMessage(identifier);
		ExceptionsHelper.handleException(parentComponent, title, e, message, false);
	}

}
