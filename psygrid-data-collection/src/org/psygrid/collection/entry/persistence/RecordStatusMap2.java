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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.psygrid.collection.entry.DocumentStatus;
import org.psygrid.collection.entry.remote.RemoteManager;
import org.psygrid.common.identifier.IdentifierHelper;
import org.psygrid.common.identifier.InvalidIdentifierException;
import org.psygrid.data.model.dto.extra.ConsentStatusResult;
import org.psygrid.data.model.dto.extra.StatusResult;
import org.psygrid.data.model.hibernate.*;

/**
 * @author Rob Harper
 *
 */
public class RecordStatusMap2 {

	public static final Long LOCALLY_INCOMPLETE_ID = new Long(-1);
	public static final Long READY_TO_SUBMIT_ID = new Long(-2);
	public static final Long COMMIT_FAILED_ID = new Long(-3);

	/**
	 * Map of identifier to database id of its status
	 */
	private Map<String, Long> statusMap;

	/**
	 * Map of identifier to map of document occurrence id to database id of status
	 * of its instance
	 */
	private Map<String, Map<Long, Long>> documentStatusMap;

	private transient Map<String, DataSet> dataSetMap = new HashMap<String, DataSet>();

	private Object readResolve(){
		dataSetMap = new HashMap<String, DataSet>();
		return this;
	}

	public RecordStatusMap2(){
		statusMap = new HashMap<String, Long>();
		documentStatusMap = new HashMap<String, Map<Long, Long>>();
	}

	public boolean documentStatusMapExists(){
		return (null != documentStatusMap );
	}

	public final void addRecord(String identifier, Status status){
		statusMap.put(identifier, status.getId());
	}

	public final void addRecord(String identifier, Long statusId){
		statusMap.put(identifier, statusId);
	}

	public final boolean addRecordNoOverwrite(String identifier, Status status){
		if ( statusMap.containsKey(identifier) ){
			return false;
		}
		statusMap.put(identifier, status.getId());
		return true;
	}

	/**
	 * For a particular record, add a status for the instance of the given IDocumentOccurrence.
	 * 
	 * @param identifier
	 * @param docOcc
	 * @param status
	 */
	public final void addDocStatus(String identifier, DocumentOccurrence docOcc, Status status) {
		addDocStatus(identifier, docOcc, status.getId());
	}

	/**
	 * For a particular record, add a status for the instance of the given IDocumentOccurrence,
	 * with the Status identified by its database unique identifier.
	 * 
	 * @param identifier
	 * @param docOcc
	 * @param statusId
	 */
	public final void addDocStatus(String identifier, DocumentOccurrence docOcc, Long statusId) {
		if (documentStatusMap == null) {
			documentStatusMap = new HashMap<String, Map<Long, Long>>();
		}
		if (!this.documentStatusMap.containsKey(identifier)) {
			this.documentStatusMap.put(identifier, new HashMap<Long, Long>());
		}
		this.documentStatusMap.get(identifier).put(docOcc.getId(), statusId);
	}

	/**
	 * For a particular record, add a status for the instance of the given DocumentOccurrence.
	 * 
	 * @param identifier
	 * @param docOcc
	 * @param documentStatus
	 */
	public final void addDocStatus(String identifier, DocumentOccurrence docOcc, DocumentStatus status) {
		Long statusId = null;
		try{
			statusId = DocumentStatus.toIStatus(docOcc.getDocument(), status).getId();
		}
		catch(IllegalArgumentException ex){
			switch(status){
			case LOCALLY_INCOMPLETE:
				statusId = LOCALLY_INCOMPLETE_ID;
				break;
			case READY_TO_SUBMIT:
				statusId = READY_TO_SUBMIT_ID;
				break;
			case COMMIT_FAILED:
				statusId = COMMIT_FAILED_ID;
				break;
			default:
				throw ex;
			}
		}
		addDocStatus(identifier, docOcc, statusId);
	}

	public final Status getStatusForRecord(String identifier){
		try{
			String project = IdentifierHelper.getProjectCodeFromIdentifier(identifier);
			DataSet ds = dataSetMap.get(project);
			if ( null == ds ){
				PersistenceManager pManager = PersistenceManager.getInstance();
				ds = pManager.loadDataSet(pManager.getData().getDataSetSummary(IdentifierHelper.getProjectCodeFromIdentifier(identifier)));
				dataSetMap.put(project, ds);
			}
			return getStatusForRecord(identifier, ds);
		}
		catch(IOException ex){
			return null;
		}
		catch(InvalidIdentifierException ex){
			return null;
		}
	}

	public final Status getStatusForRecord(String identifier, DataSet ds){
		Long statusId = statusMap.get(identifier);
		for ( int i=0, c=ds.numStatus(); i<c; i++ ){
			Status s = ds.getStatus(i);
			if ( s.getId().equals(statusId) ){
				return s;
			}
		}
		return null;
	}

	/**
	 * This is similar to
	 * {@link #getStatusOfDocumentInstance(org.psygrid.data.model.hibernate.Record, org.psygrid.data.model.hibernate.DocumentOccurrence)} but it
	 * returns a DocumentStatus and it's never {@code null}. For the case where
	 * {@link #getStatusOfDocumentInstance(org.psygrid.data.model.hibernate.Record, org.psygrid.data.model.hibernate.DocumentOccurrence)} returns
	 * {@code null}, this method returns {@link DocumentStatus#NOT_STARTED}.
	 * 
	 * TODO It may be a good idea to change {@link #getStatusOfDocumentInstance(org.psygrid.data.model.hibernate.Record, org.psygrid.data.model.hibernate.DocumentOccurrence)}
	 * to use a similar approach since it's nicer than having to check for
	 * null everywhere, but I was afraid of breaking code that calls it.
	 */
	public final DocumentStatus getDocumentStatus(Record record,
			DocumentOccurrence docOcc) {
		Status status = getStatusOfDocumentInstance(record, docOcc);
		if (status == null)
			return DocumentStatus.NOT_STARTED;
		return DocumentStatus.valueOf(status);
	}

	/**
	 * For a particular record retrieve the status of the instance of the given IDocumentOccurrence.
	 * 
	 * @param identifier
	 * @param docOcc
	 * @return status
	 */
	public final Status getStatusOfDocumentInstance(Record record, DocumentOccurrence docOcc) {
		String identifier = record.getIdentifier().getIdentifier();
		//RecordStatusMaps created before documentStatusMaps were added need to have a documentStatusMap created
		if (documentStatusMap == null) {
			documentStatusMap = new HashMap<String, Map<Long, Long>>();
		}
		if (!this.documentStatusMap.containsKey(identifier)) {
			if ( RemoteManager.getInstance().isTestDataset() ){
				//running in test/preview mode
				//If a doc instance exists, return the "Ready to submit" status
				//It is not possible to save a document as incomplete
				DocumentInstance docInst = record.getDocumentInstance(docOcc);
				if ( null == docInst ){
					return null;
				}
				return new Status(DocumentStatus.READY_TO_SUBMIT.toString(), DocumentStatus.READY_TO_SUBMIT.toStatusLongName(), 0);
			}
			else{
				return null;
			}
		}
		Long statusId = this.documentStatusMap.get(identifier).get(docOcc.getId());	//by id or name or documentocc??
		if ( null == statusId ){
			return null;
		}
		Document doc = docOcc.getDocument();
		for (int i=0, c=doc.numStatus(); i<c; i++){
			Status s = doc.getStatus(i);
			if ( s.getId().equals(statusId) ){
				return s;
			}
		}

		//TODO linked directly to Status implementation - bad
		if ( LOCALLY_INCOMPLETE_ID.equals(statusId) ){
			return new Status(DocumentStatus.LOCALLY_INCOMPLETE.toString(), DocumentStatus.LOCALLY_INCOMPLETE.toStatusLongName(), 0);
		}
		if ( READY_TO_SUBMIT_ID.equals(statusId) ){
			return new Status(DocumentStatus.READY_TO_SUBMIT.toString(), DocumentStatus.READY_TO_SUBMIT.toStatusLongName(), 0);
		}
		
		if( COMMIT_FAILED_ID.equals(statusId)){
			return new Status(DocumentStatus.COMMIT_FAILED.toString(), DocumentStatus.COMMIT_FAILED.toStatusLongName(), 0);
		}

		return null;
	}

	public final boolean noStatusesForProject(String projectCode){
		for ( String key: statusMap.keySet() ){
			if ( key.startsWith(projectCode) ){
				return false;
			}
		}
		return true;
	}

	public final void deleteRecord(String identifier){
		statusMap.remove(identifier);
		documentStatusMap.remove(identifier);
	}

	public final void addFromConsentStatusResult(ConsentStatusResult result){
		String lastIdentifier = null;
		Map<Long, Long> dsmap = null;
		for ( StatusResult sr: result.getStatusResults() ){
			if ( !sr.getIdentifier().equals(lastIdentifier) ){
				dsmap = documentStatusMap.get(sr.getIdentifier());
				if ( null == dsmap ){
					dsmap = new HashMap<Long, Long>();
					documentStatusMap.put(sr.getIdentifier(), dsmap);
				}
				statusMap.put(sr.getIdentifier(), sr.getRecStatusId());
			}
			if ( null != sr.getOccurrenceId() ){
				dsmap.put(sr.getOccurrenceId(), sr.getDocStatusId());
			}
			lastIdentifier = sr.getIdentifier();
		}
	}

	public final void removeDocStatus(String identifier, DocumentOccurrence docOcc){
		if (documentStatusMap != null) {
			if (this.documentStatusMap.containsKey(identifier)) {
				this.documentStatusMap.get(identifier).remove(docOcc.getId());
			}
		}
	}

	public final void removeForProject(String projectCode) {
		Iterator<Map.Entry<String, Long>> recIt = statusMap.entrySet().iterator();
		while ( recIt.hasNext() ){
			Map.Entry<String, Long> e = recIt.next();
			try{
				if ( projectCode.equals(IdentifierHelper.getProjectCodeFromIdentifier(e.getKey()))){
					recIt.remove();
				}
			}
			catch(InvalidIdentifierException ex){
				//do nothing - should never happen
			}
		}
		
		Iterator<Map.Entry<String, Map<Long, Long>>> docIt = documentStatusMap.entrySet().iterator();
		while ( docIt.hasNext() ){
			Map.Entry<String, Map<Long, Long>> e = docIt.next();
			try{
				if ( projectCode.equals(IdentifierHelper.getProjectCodeFromIdentifier(e.getKey()))){
					docIt.remove();
				}
			}
			catch(InvalidIdentifierException ex){
				//do nothing - should never happen
			}
		}
	}
	
	public final void synchronizeWithGroups(DatedProjectType project, List<String> groups) throws InvalidIdentifierException {
		String projectCode = project.getIdCode();
		List<String> toDelete = new ArrayList<String>();
		for ( String identifier: statusMap.keySet() ){
			if ( IdentifierHelper.getProjectCodeFromIdentifier(identifier).equals(projectCode) ){
				if ( !groups.contains(IdentifierHelper.getGroupCodeFromIdentifier(identifier)) ){
					toDelete.add(identifier);
				}
			}
		}
		for ( String identifier: toDelete ){
			statusMap.remove(identifier);
			documentStatusMap.remove(identifier);
		}
	}

	public List<String> findGroupsWithNoRecords(DatedProjectType project, List<String> groups) throws InvalidIdentifierException {
		String projectCode = project.getIdCode();
		Set<String> statusGroups = new HashSet<String>();
		for ( String identifier: statusMap.keySet() ){
			if ( IdentifierHelper.getProjectCodeFromIdentifier(identifier).equals(projectCode) ){
				statusGroups.add(IdentifierHelper.getGroupCodeFromIdentifier(identifier));
			}
		}
		List<String> missingGroups = new ArrayList<String>();
		for ( String group: groups ){
			if ( !statusGroups.contains(group) ){
				missingGroups.add(group);
			}
		}
		return missingGroups;
	}

	public List<String> getRecordsForDocumentStatus(String projectCode, DocumentStatus status) {
		List<String> identifiers = new ArrayList<String>();
		try {
			DataSet dataset = PersistenceManager.getInstance().loadDataSet(PersistenceManager.getInstance().getData().getDataSetSummary(projectCode));

			nextIdentifier: for ( String identifier: statusMap.keySet() ){
				if ( identifier.startsWith(projectCode) ){
					Map<Long,Long> documents = documentStatusMap.get(identifier);
					if (documents != null) {
						for (Long docId: documents.keySet()) {
							Long statusId = documents.get(docId);
							for ( int j=0, d=dataset.numDocuments(); j<d; j++ ){
								Document doc = dataset.getDocument(j);
								for (int i=0, c=doc.numStatus(); i<c; i++){
									Status s = doc.getStatus(i);
									if ( s.getId().equals(statusId) 
											&& s.getLongName().equals(status.toStatusLongName())){
										identifiers.add(identifier);
										continue nextIdentifier;
									}
								}
							}
						}
					}
				}
			}
		}
		catch (Exception e) {
			//TODO handle error
			e.printStackTrace();
		}
		return identifiers;
	}
	
	public List<String> getRecordsWithNotStartedDocuments(String projectCode){
		List<String> identifiers = new ArrayList<String>();
		try {
			DataSet dataset =
				PersistenceManager.getInstance().loadDataSet(PersistenceManager.getInstance().getData().getDataSetSummary(projectCode));
			nextIdentifier: for ( String identifier: statusMap.keySet() ){
				if ( identifier.startsWith(projectCode) ){
					Map<Long,Long> documents = documentStatusMap.get(identifier);
					if ( null == documents ){
						identifiers.add(identifier);
						continue nextIdentifier;
					}
					for ( int i=0, c=dataset.numDocuments(); i<c; i++ ){
						Document doc = dataset.getDocument(i);
						for ( int j=0, d=doc.numOccurrences(); j<d; j++ ){
							DocumentOccurrence occ = doc.getOccurrence(j);
							if ( null == documents.get(occ.getId())){
								identifiers.add(identifier);
								continue nextIdentifier;
							}
						}
					}
				}
			}
		}
		catch (Exception e) {
			//TODO handle error
			e.printStackTrace();
		}
		return identifiers;
	}
}
