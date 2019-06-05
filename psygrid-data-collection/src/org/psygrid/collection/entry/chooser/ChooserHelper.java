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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.DocumentStatus;
import org.psygrid.collection.entry.ExceptionsHelper;
import org.psygrid.collection.entry.persistence.ConsentMap2;
import org.psygrid.collection.entry.persistence.DataSetSummary;
import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.collection.entry.remote.RemoteManager;
import org.psygrid.collection.entry.remote.RemoteServiceFault;
import org.psygrid.collection.entry.security.EntrySAMLException;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.utils.security.NotAuthorisedFault;

public class ChooserHelper {
	public static String getDocumentDisplayText(
			DocumentOccurrence documentOccurrence) {

		Document document = documentOccurrence.getDocument();
		String docText = document.getDisplayText();
		String docOccText = documentOccurrence.getDisplayText();
		if (docOccText == null) {
			return docText;
		}
		StringBuilder displayText = new StringBuilder(docText);
		displayText.append(" - ").append(docOccText); //$NON-NLS-1$
		return displayText.toString();
	}

	public static boolean checkDocumentInstanceDde(Record record, DocumentOccurrence docOcc){
		//check to see if this document should be completed via propagation
		//of data from a primary record
		if ( null != record.getPrimaryIdentifier() && null != docOcc.getPrimaryOccIndex() ){
			return true;
		}
		return false;
	}

	public static void getDocumentOccsForDataset(DataSetSummary dss, ChoosableDataSet cds) {

		List<DocumentOccurrence> docOccs = new ArrayList<DocumentOccurrence>();

		DataSet dataSet = null;

		try {
			if ( RemoteManager.getInstance().isTestDataset() ){
				dataSet = dss.getCompleteDataSet();
			}
			else{
				dataSet = PersistenceManager.getInstance().loadDataSet(dss);
			}
		}
		catch (IOException ioe) {
			ExceptionsHelper.handleIOException(null, ioe, false);	
		}
		for (Document doc: ((DataSet)dataSet).getDocuments()) {	
			docOccs.addAll(doc.getOccurrences());
		}
		//Add each DocumentGroup to the choosable list
		for (int i = 0; i < dataSet.numDocumentGroups(); i++) {

			ChoosableTemplateDocGroup group = 
				new ChoosableTemplateDocGroup(dataSet.getDocumentGroup(i), cds);

			//Add each DocOcc to the choosable list
			for (DocumentOccurrence docOcc: docOccs) {
				if (docOcc.getDocumentGroup().getName().equals(dataSet.getDocumentGroup(i).getName())) {
					new ChoosableDocOccurrence(docOcc, group);
				}
			}

		}

	}

	/**
	 * Retrieve a list of sorted record identifiers having documents with the given status.
	 * 
	 * Searches both locally and remotely for the identifiers.
	 * 
	 * @param application
	 * @param cds The dataset
	 * @param parent The parent choosable
	 * @param docStatus A DocumentStatus or {@code null} if documents with any
	 * status should be returned. 
	 * @param useCache
	 * @throws EntrySAMLException 
	 * @throws RemoteServiceFault 
	 * @throws NotAuthorisedFault 
	 * @throws IOException 
	 * @throws SocketTimeoutException 
	 * @throws ConnectException 
	 */
	public static  List<String> loadChoosableRecords(Application application,
			ChoosableDataSet cds, AbstractChoosableWithChildren<? extends Choosable> parent, DocumentStatus docStatus, boolean useCache) 
			throws ConnectException, SocketTimeoutException, IOException, NotAuthorisedFault, RemoteServiceFault, EntrySAMLException {

		List<String> records = new ArrayList<String>();
		if ( RemoteManager.getInstance().isTestDataset() ){
			//Running in test/preview mode
			//Return a single possible identifier, the identifier of the
			//current record (if there is one)
			Record r = application.getRecord();
			if ( null != r ){
				String identifier = r.getIdentifier().getIdentifier();
				records.add(identifier);
			}				
			return records;
		}
		else{
			//Running in normal mode

			String projectCode = cds.getDataSet().getProjectCode();
			PersistenceManager pManager = PersistenceManager.getInstance();

			//The identifiers to include
			List<String> identifiers  = new ArrayList<String>();

			//get all used identifiers from the consent map
			ConsentMap2 consents = pManager.getConsentMap();
			Set<String> usedIdentifiers = consents.getIdentifiers();
			
			if (docStatus == null) {
				// narrow these down to just the identifiers relating to the current
				// dataset
				for (String id : usedIdentifiers) {
					if (id.startsWith(projectCode)) {
						identifiers.add(id);
					}
				}
			}
			else {
				switch (docStatus) {
				case LOCALLY_INCOMPLETE:
					for (String identifier: usedIdentifiers) {
						if (identifier.startsWith(projectCode)) {
							try {
								pManager.loadRecord(identifier, false);
								identifiers.add(identifier);
							}
							catch (FileNotFoundException e) {
								//Safe to ignore. Record has no incomplete docs
							}
							catch (Exception e) {
								//TODO handle error
								e.printStackTrace();
							}
						}
					}
					break;
				case READY_TO_SUBMIT:
					//test if held locally..
					for (String identifier: usedIdentifiers) {
						if (identifier.startsWith(projectCode)) {
							try {
								//TODO is there a better way, other than trying to load the record?
								Record record = pManager.loadRecord(identifier, true);
								if (record.numDocumentInstances() > 0) {
									identifiers.add(identifier);
								}
							}
							catch (FileNotFoundException e) {
								//Safe to ignore. Record has no pending docs
							}
							catch (Exception e) {
								//TODO handle error
								e.printStackTrace();
							}
						}
					}
					break;
				case NOT_STARTED:
					identifiers = pManager.getRecordStatusMap().getRecordsWithNotStartedDocuments(projectCode);
					break;
				default:
					//All other cases
					identifiers = pManager.getRecordStatusMap().getRecordsForDocumentStatus(projectCode, docStatus);
				}
			}

			/*
			 * Sort the identifiers
			 */
			List<String> ids = new ArrayList<String>();

			List<Identifier> realIds = new ArrayList<Identifier>();
			for (String id: identifiers) {
				Identifier i =new Identifier();
				try {
					i.initialize(id);
					realIds.add(i);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}	
			java.util.Collections.sort(realIds);
			for (Identifier id: realIds) {
				ids.add(id.getIdentifier());
			}

			return ids;
		}
	}
}
