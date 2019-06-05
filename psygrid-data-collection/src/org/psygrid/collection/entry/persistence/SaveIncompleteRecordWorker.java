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
import org.psygrid.collection.entry.persistence.RecordsList.Item;
import org.psygrid.collection.entry.security.DecryptionException;
import org.psygrid.collection.entry.security.SecurityManager;
import org.psygrid.collection.entry.util.WaitRunnable;
import org.psygrid.common.identifier.InvalidIdentifierException;
import org.psygrid.data.model.hibernate.ChangeHistory;
import org.psygrid.data.model.hibernate.DocumentInstance;
import org.psygrid.data.model.hibernate.Record;

/**
 * Worker class to to save a document instance as incomplete.
 * 
 * @author Rob Harper
 *
 */
public class SaveIncompleteRecordWorker extends AbstractSaveRecordWorker {

	private static final Log LOG = LogFactory.getLog(SaveIncompleteRecordWorker.class);

	public SaveIncompleteRecordWorker(Application application, Record record, DocumentInstance docInst) {
		super(application, record, docInst);
	}

	@Override
	protected String doInBackground() throws IOException, DecryptionException, InvalidIdentifierException {
		new WaitRunnable(application).run();
		String message = null;
		
		PersistenceManager pManager = PersistenceManager.getInstance();
		
		synchronized (pManager) {
		
			ChangeHistory change = docInst.addToHistory(SecurityManager.getInstance().getUserName());
			//check for changes to the document and add provenance where necessary
			docInst.checkForChanges(change);
			
			RecordsList recordsList = pManager.getRecordsList();

			//get the incomplete portion of the record and attach the document instance to it
			Item existingItem = pManager.getRecordsList().getItem(record.getIdentifier(), false);
			if (existingItem != null) {
				Record incompleteRecord = pManager.loadRecord(existingItem);
				if (incompleteRecord.getDocumentInstance(docInst.getOccurrence()) != null) {
					incompleteRecord.detachDocumentInstance(incompleteRecord.getDocumentInstance(docInst.getOccurrence()));
				}

				//detach the document instance we are saving from the record in the model
				//and attach it instead to the record loaded from disk.
				docInst.detachFromRecord();
				incompleteRecord.attach(docInst);
				pManager.getRecordsList().addItem(incompleteRecord.getIdentifier(), false);
				pManager.saveRecord(incompleteRecord, false);
				
				//after doing the save we now detach and re-attach the document instance to
				//the record in the model so everything is sane when we come out of this method
				docInst.detachFromRecord();
				record.attach(docInst);

			} else {
				LOG.info("SaveIncompleteRecordWorker - item not in records list, number of document instances is "+record.numDocumentInstances());
				pManager.getRecordsList().addItem(record.getIdentifier(), false);
				pManager.saveRecord(record, false);				
			}

			//Always check for empty complete record, in case the first document of the
			//record is saved as incomplete
			Item item = recordsList.getItem(record.getIdentifier(), true);
			if (item != null ) {
				//If document status was complete then we need to remove the document
				//instance from the complete portion of the record
				Record completeRecord = pManager.loadRecord(item);
				boolean recordModified = false;
				if (completeRecord.getDocumentInstance(docInst.getOccurrence()) != null) {
					completeRecord.detachDocumentInstance(completeRecord.getDocumentInstance(docInst.getOccurrence()));
					recordModified = true;
				}
	
				if (completeRecord.numDocumentInstances() == 0) {
					pManager.deleteRecord(item);
					recordsList.removeItem(item);
				}
				else{
					if ( recordModified ){
						pManager.saveRecord(completeRecord, true);
					}
				}
			}

				
			pManager.updateRecord(record, docInst, false);
			pManager.saveRecordsList();
	
			IdentifiersList idsList = pManager.getIdentifiers();
			IdentifierData idData = idsList.get(record.getIdentifier());
			if (idData != null && (!idData.isUsed())) {
				idData.setUsed(true);
				pManager.saveIdentifiers();
			}

		}
		
		return message;
	}
	
}
