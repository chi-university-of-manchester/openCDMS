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

package org.psygrid.collection.entry.persistence;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.DocumentStatus;
import org.psygrid.collection.entry.EntryHelper;
import org.psygrid.collection.entry.persistence.RecordsList.Item;
import org.psygrid.collection.entry.remote.RemoteServiceFault;
import org.psygrid.collection.entry.security.DecryptionException;
import org.psygrid.collection.entry.security.EntrySAMLException;
import org.psygrid.collection.entry.util.WaitRunnable;
import org.psygrid.common.identifier.InvalidIdentifierException;
import org.psygrid.data.model.hibernate.DocumentInstance;
import org.psygrid.data.model.hibernate.Record;
import org.psygrid.data.model.hibernate.Status;
import org.psygrid.data.repository.DuplicateDocumentsFault;
import org.psygrid.data.repository.RepositoryInvalidIdentifierFault;
import org.psygrid.data.repository.RepositoryNoConsentFault;
import org.psygrid.data.repository.RepositoryOutOfDateFault;
import org.psygrid.data.repository.transformer.TransformerFault;
import org.psygrid.data.utils.security.NotAuthorisedFault;

/**
 * Worker class to save the document instance just edited into its record.
 * <p>
 * The document instance is either new, or previously existed as complete
 * and ready to submit.
 * 
 * @author Rob Harper
 *
 */
public class SaveRecordWorker extends AbstractSaveRecordWorker {

	private static final Log LOG = LogFactory.getLog(SaveRecordWorker.class);
	
	public SaveRecordWorker(Application application, Record record, DocumentInstance docInst) {
		super(application, record, docInst);
	}

	@Override
	protected String doInBackground() throws IOException, DecryptionException, InvalidIdentifierException {
		new WaitRunnable(application).run();
		String message = null;
		
		PersistenceManager pManager = PersistenceManager.getInstance();		
		synchronized (pManager) {

			RecordsList recordsList = pManager.getRecordsList();

			//Find the current status of this document instance (if any) and so
			//decide whether it was incomplete but is now being saved as complete
			boolean wasIncomplete = false;
			Status currentStatus = pManager.getStatusOfDocument(record, docInst);
			if ( null != currentStatus ){
				if ( currentStatus.getShortName().equals(DocumentStatus.INCOMPLETE.toString()) ||
					 currentStatus.getShortName().equals(DocumentStatus.LOCALLY_INCOMPLETE.toString()) ){
					//trying to save as complete and an incomplete version of the document instance exists
					wasIncomplete = true;
				}
			}
			
			//get the complete portion of the record and attach the document instance to it
			Item existingItem = pManager.getRecordsList().getItem(record.getIdentifier(), true);
			if (existingItem != null) {
				Record completeRecord = pManager.loadRecord(existingItem);
				if (completeRecord.getDocumentInstance(docInst.getOccurrence()) != null) {
					completeRecord.detachDocumentInstance(completeRecord.getDocumentInstance(docInst.getOccurrence()));
				}

				//detach the document instance we are saving from the record in the model
				//and attach it instead to the record loaded from disk.
				docInst.detachFromRecord();
				completeRecord.attach(docInst);
				pManager.getRecordsList().addItem(completeRecord.getIdentifier(), true);
				pManager.saveRecord(completeRecord, true);
				
				//after doing the save we now detach and re-attach the document instance to
				//the record in the model so everything is sane when we come out of this method
				docInst.detachFromRecord();
				record.attach(docInst);
	
			} else {
				LOG.info("SaveRecordWorker - item not in records list, number of document instances is "+record.numDocumentInstances());
				pManager.getRecordsList().addItem(record.getIdentifier(), true);
				pManager.saveRecord(record, true);				
			}

			if ( wasIncomplete ){
			
				//See if there exists an incomplete portion of the record 
				Item item = recordsList.getItem(record.getIdentifier(), false);
				if (item != null && wasIncomplete) {
					//If document status was incomplete then we need to remove the document
					//instance from the incomplete portion of the record
					Record incompleteRecord = pManager.loadRecord(item);
					if (incompleteRecord.getDocumentInstance(docInst.getOccurrence()) != null) {
						incompleteRecord.detachDocumentInstance(incompleteRecord.getDocumentInstance(docInst.getOccurrence()));
					}
		
					if (incompleteRecord.numDocumentInstances() == 0) {
						pManager.deleteRecord(item);
						recordsList.removeItem(item);
					}
					else{
						pManager.saveRecord(incompleteRecord, false);
					}
				}
		
			}
			
			pManager.updateRecord(record, docInst, true);
			pManager.saveRecordsList();
	
			IdentifiersList idsList = pManager.getIdentifiers();
			IdentifierData idData = idsList.get(record.getIdentifier());
			if (idData != null && (!idData.isUsed())) {
				idData.setUsed(true);
				pManager.saveIdentifiers();
			}
				
			try{
				message = EntryHelper.doDdeCopy(record, docInst, pManager);
			}
			catch(DuplicateDocumentsFault ex){
				//do nothing - can't be thrown in this context
			}
			catch(RepositoryInvalidIdentifierFault ex){
				//do nothing - can't be thrown in this context
			}
			catch(RepositoryOutOfDateFault ex){
				//do nothing - can't be thrown in this context				
			}
			catch(TransformerFault ex){
				//do nothing - can't be thrown in this context
			}
			catch(NotAuthorisedFault ex){
				//do nothing - can't be thrown in this context
			}
			catch(EntrySAMLException ex){
				//do nothing - can't be thrown in this context
			}
			catch(RepositoryNoConsentFault ex){
				//do nothing - can't be thrown in this context
			}
			catch(RemoteServiceFault ex){
				//do nothing - can't be thrown in this context
			}
		}
		
		return message;
	}
}
