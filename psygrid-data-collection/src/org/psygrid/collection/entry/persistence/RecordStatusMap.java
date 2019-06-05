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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.psygrid.collection.entry.DocumentStatus;
import org.psygrid.common.identifier.IdentifierHelper;
import org.psygrid.common.identifier.InvalidIdentifierException;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Document;
import org.psygrid.data.model.hibernate.DocumentOccurrence;
import org.psygrid.data.model.hibernate.Status;

public class RecordStatusMap {

	private Map<String, Status> statusMap;

	/**
	 * A list of identifiers: having a list of DocOccurrences (combined name) and current Status of the DocOccInstance 
	 */
	private Map<String, Map<String, Status>> documentStatusMap;

	public RecordStatusMap(){
		statusMap = new HashMap<String, Status>();
		documentStatusMap = new HashMap<String, Map<String, Status>>();
	}

	public final Map<String, Status> getRecordStatusMap(){
		return Collections.unmodifiableMap(this.statusMap);
	}
	
	public final Map<String, Map<String, Status>> getDocumentStatusMap(){
		if (this.documentStatusMap == null) {
			return null;
		}
		return Collections.unmodifiableMap(this.documentStatusMap);
	}
	
	public final void addRecord(String identifier, Status status){
		statusMap.put(identifier, status);
	}

	public final boolean addRecordNoOverwrite(String identifier, Status status){
		if ( statusMap.containsKey(identifier) ){
			return false;
		}
		statusMap.put(identifier, status);
		return true;
	}

	public final Status getStatusForRecord(String identifier){
		return this.statusMap.get(identifier);
	}

	public final boolean noStatusesForProject(String projectCode){
		for ( String key: statusMap.keySet() ){
			if ( key.startsWith(projectCode) ){
				return false;
			}
		}
		return true;
	}

	/**
	 * For a particular record retrieve the status of the instance of the given IDocumentOccurrence.
	 * 
	 * @param identifier
	 * @param docOcc
	 * @return status
	 */
	public final Status getStatusOfDocumentInstance(String identifier, DocumentOccurrence docOcc) {
		//RecordStatusMaps created before documentStatusMaps were added need to have a documentStatusMap created
		if (documentStatusMap == null) {
			documentStatusMap = new HashMap<String, Map<String, Status>>();
		}
		if (this.documentStatusMap.containsKey(identifier)) {
			return this.documentStatusMap.get(identifier).get(docOcc.getCombinedName());	//by id or name or documentocc??
		}
		return null;
	}

	/**
	 * For a particular record, add a status for the instance of the given IDocumentOccurrence.
	 * 
	 * @param identifier
	 * @param docOcc
	 * @param status
	 */
	public final void addDocStatus(String identifier, DocumentOccurrence docOcc, Status status) {
		//RecordStatusMaps created before documentStatusMaps were added need to have a documentStatusMap created
		if (documentStatusMap == null) {
			documentStatusMap = new HashMap<String, Map<String, Status>>();
		}
		if (this.documentStatusMap.containsKey(identifier)) {
			this.documentStatusMap.get(identifier).put(docOcc.getCombinedName(), status);
		}
		else {
			this.documentStatusMap.put(identifier, new HashMap<String, Status>());
			this.documentStatusMap.get(identifier).put(docOcc.getCombinedName(), status);
		}
	}

	/**
	 * For a particular record, add a status for the instance of the given DocumentOccurrence.
	 * 
	 * @param identifier
	 * @param docOcc
	 * @param documentStatus
	 */
	public final void addDocStatus(String identifier, DocumentOccurrence docOcc, DocumentStatus status) {
		if (documentStatusMap == null) {
			documentStatusMap = new HashMap<String, Map<String, Status>>();
		}
		Status docInstStatus = new Status();
		docInstStatus.setLongName(status.toStatusLongName());
		docInstStatus.setShortName(status.toString());
		if (this.documentStatusMap.containsKey(identifier)) {
			this.documentStatusMap.get(identifier).put(docOcc.getCombinedName(), docInstStatus);
		}
		else {
			this.documentStatusMap.put(identifier, new HashMap<String, Status>());
			this.documentStatusMap.get(identifier).put(docOcc.getCombinedName(), docInstStatus);
		}
	}

	public final void deleteRecord(String identifier){
		statusMap.remove(identifier);
	}

	public final RecordStatusMap2 convertToNewFormat() throws IOException{
		RecordStatusMap2 newMap = new RecordStatusMap2();
		for ( Map.Entry<String, Status> record: statusMap.entrySet() ){
			newMap.addRecordNoOverwrite(record.getKey(), record.getValue());
		}
		Map<String, DataSet> dataSets = new HashMap<String, DataSet>();
		for ( Map.Entry<String, Map<String, Status>> record: documentStatusMap.entrySet() ){
			String identifier = record.getKey();
			String project = null;
			try{
				project = IdentifierHelper.getProjectCodeFromIdentifier(identifier);
			}
			catch(InvalidIdentifierException ex){
				//Throw an IOException for easier handling up the stack
				//Not ideal but I don't want to alter lots of the code above
				//as this method will only be used once per user
				throw new IOException(ex.getMessage());
			}
			Map<String, Status> docs = record.getValue();
			DataSet ds = dataSets.get(project);
			if ( null == ds ){
				PersistenceManager pManager = PersistenceManager.getInstance();
				ds = pManager.loadDataSet(pManager.getData().getDataSetSummary(project));
				dataSets.put(project, ds);
			}
			for ( int i=0, c=ds.numDocuments(); i<c; i++ ){
				Document doc = ds.getDocument(i);
				for ( int j=0, d=doc.numOccurrences(); j<d; j++ ){
					DocumentOccurrence occ = doc.getOccurrence(j);
					if ( docs.containsKey(occ.getCombinedName())){
						Status status = docs.get(occ.getCombinedName());
						Long statusId = null;
						if ( null != status.getId() ){
							statusId = status.getId();
						}
						else{
							if ( status.getShortName().equals(DocumentStatus.LOCALLY_INCOMPLETE.toString()) ){
								statusId = RecordStatusMap2.LOCALLY_INCOMPLETE_ID;
							}
							else if ( status.getShortName().equals(DocumentStatus.READY_TO_SUBMIT.toString()) ){
								statusId = RecordStatusMap2.READY_TO_SUBMIT_ID;
							}
							else{
								//TODO - what to do here? Shouldn't really happen but...
							}
						}
						if ( null != statusId ){
							newMap.addDocStatus(identifier, occ, statusId);
						}
					}
				}
			}
		}
		return newMap;
	}
	
}
