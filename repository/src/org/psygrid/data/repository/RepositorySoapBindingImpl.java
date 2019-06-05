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
package org.psygrid.data.repository;

import java.net.ConnectException;
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Properties;
import javax.xml.rpc.ServiceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.data.export.NoSuchExportFault;
import org.psygrid.data.export.UnableToCancelExportFault;
import org.psygrid.data.export.dto.ExportRequest;
import org.psygrid.data.model.dto.DataSetDTO;
import org.psygrid.data.model.dto.GroupDTO;
import org.psygrid.data.model.dto.IdentifierDTO;
import org.psygrid.data.model.dto.RecordDTO;
import org.psygrid.data.model.dto.RecordDataDTO;
import org.psygrid.data.model.dto.TransformerDTO;
import org.psygrid.data.model.dto.extra.ConsentStatusResult;
import org.psygrid.data.model.dto.extra.DocInstChangeHistoryResult;
import org.psygrid.data.model.dto.extra.IdentifierData;
import org.psygrid.data.model.dto.extra.ProvenanceForChangeResult;
import org.psygrid.data.model.dto.extra.SearchRecordChangeHistoryResult;
import org.psygrid.data.repository.transformer.TransformerFault;
import org.psygrid.data.sampletracking.ConfigInfo;
import org.psygrid.data.sampletracking.ParticipantInfo;
import org.psygrid.data.sampletracking.SampleInfo;
import org.psygrid.data.importing.ImportData;
import org.psygrid.data.importing.ImportStatus;
import org.psygrid.data.utils.security.NotAuthorisedFault;
import org.psygrid.security.utils.PropertyUtilities;
import org.psygrid.services.SecureSoapBindingImpl;
import org.springframework.context.ApplicationContext;

/**
 * Implementation of data repository web-service.
 * 
 * @author Rob Harper
 *
 */
public class RepositorySoapBindingImpl extends SecureSoapBindingImpl implements Repository {


	/**
	 * Transactional service implementation from application context.
	 */
	Repository service = null;

	
	@Override
	protected void onInit() throws ServiceException {
		super.onInit();
		ApplicationContext ctx = getWebApplicationContext();
		service = (Repository)ctx.getBean("repositoryService");
	}
	
	public String getVersion() throws RemoteException {
		return service.getVersion();
	}

	public org.psygrid.data.model.dto.DataSetDTO getDataSetComplete(long dataSetId, String saml) throws RemoteException, RepositoryServiceFault, NotAuthorisedFault {
		return service.getDataSetComplete(dataSetId, saml);
	}

	public byte[] getBinaryData(long dataSetId, long binaryObjectId, String saml) throws RemoteException, RepositoryServiceFault {
		return service.getBinaryData(dataSetId, binaryObjectId, saml);
	}


	public org.psygrid.data.model.dto.DataSetDTO[] getModifiedDataSets(Calendar referenceDate, String saml) throws RemoteException, RepositoryServiceFault, ConnectException {
		return service.getModifiedDataSets(referenceDate, saml);
	}

	public long saveRecord(org.psygrid.data.model.dto.RecordDTO record,
			boolean discardDuplicates, String saml) throws RemoteException,
			ConnectException, RepositoryServiceFault, RepositoryOutOfDateFault,
			RepositoryNoConsentFault, RepositoryInvalidIdentifierFault,
			TransformerFault, NotAuthorisedFault, DuplicateDocumentsFault,
			RepositoryNoSuchDatasetFault {
		return service.saveRecord(record, discardDuplicates, saml);
	}

	public long saveRecordAsUser(org.psygrid.data.model.dto.RecordDTO record, String user, String saml) 
	throws RemoteException, ConnectException,
	RepositoryServiceFault, 
	RepositoryOutOfDateFault,
	RepositoryNoConsentFault,
	RepositoryInvalidIdentifierFault,
	TransformerFault, 
	NotAuthorisedFault {
		return service.saveRecordAsUser(record, user, saml);
	}

	public org.psygrid.data.model.dto.RecordDTO getRecordComplete(long recordId, String saml) 
	throws RemoteException, RepositoryServiceFault {
		return service.getRecordComplete(recordId, saml);
	}

	public org.psygrid.data.model.dto.RecordDTO getRecordComplete(String identifier, String saml) 
	throws RemoteException, RepositoryServiceFault {
		return service.getRecordComplete(identifier, saml);
	}

	public org.psygrid.data.model.dto.RecordDTO getRecordCompleteByExternalID(long datasetID, String externalID, String saml) 
	throws RemoteException, RepositoryServiceFault {
		return service.getRecordCompleteByExternalID(datasetID, externalID, saml);
	}

	
	public org.psygrid.data.model.dto.RecordDTO getRecordSummary(long recordId, String saml) 
	throws RemoteException, RepositoryServiceFault {		
		return service.getRecordSummary(recordId, saml);
	}

	public org.psygrid.data.model.dto.RecordDTO getRecordSummary(String identifier, String saml) 
	throws RemoteException, RepositoryServiceFault {
		return service.getRecordSummary(identifier, saml);
	}

	public org.psygrid.data.model.dto.RecordDTO[] getRecords(long dataSetId, String saml) 
	throws RemoteException, RepositoryServiceFault {
		return service.getRecords(dataSetId, saml);
	}

	public org.psygrid.data.model.dto.RecordDTO[] getRecordsByStatus(long dataSetId, long statusId, String saml) 
	throws RemoteException, RepositoryServiceFault {
		return service.getRecordsByStatus(dataSetId, statusId, saml);
	}

	public org.psygrid.data.model.dto.IdentifierDTO[] generateIdentifiers(long dataSetId, String groupCode, int count, String saml) 
	throws RemoteException, RepositoryServiceFault {
		return service.generateIdentifiers(dataSetId, groupCode, count, saml);
	}

	public org.psygrid.data.model.dto.StandardCodeDTO[] getStandardCodes(String saml) 
	throws RemoteException {
		return service.getStandardCodes(saml);
	}

	public long saveDataSet(DataSetDTO ds, String saml) 
	throws RemoteException, 
	RepositoryServiceFault, 
	RepositoryOutOfDateFault {
		return service.saveDataSet(ds, saml);
	}

	public void publishDataSet(long dataSetId, String saml) throws RemoteException, RepositoryServiceFault {		
		service.publishDataSet(dataSetId, saml);
	}

	public String[] withdrawConsentDryRun(long recordId, long consentFormId, String reason, String saml) throws RemoteException, RepositoryServiceFault {
		return service.withdrawConsentDryRun(recordId, consentFormId, reason, saml);
	}

	public void withdrawConsent(long recordId, long consentFormId, String reason, String saml) throws RemoteException, RepositoryServiceFault {
		service.withdrawConsent(recordId, consentFormId, reason, saml);
	}

	public void changeStatus(long statusedInstanceId, long newStatusId, String saml) throws RemoteException, RepositoryServiceFault, NotAuthorisedFault {
		service.changeStatus(statusedInstanceId, newStatusId, saml);
	}

	public void changeRecordStatus(String identifier, long newStatusId, final boolean ignorePermittedTransitions, String saml) throws RemoteException, RepositoryServiceFault {
		service.changeRecordStatus(identifier, newStatusId, ignorePermittedTransitions, saml);
	}

	public DataSetDTO getDataSetSummary(String projectCode, Calendar referenceDate, String saml) throws RemoteException, RepositoryServiceFault, RepositoryNoSuchDatasetFault {
		return service.getDataSetSummary(projectCode, referenceDate, saml);
	}

	public DataSetDTO getDataSetSummaryWithDocs(String projectCode, String saml) throws RemoteException, RepositoryServiceFault, RepositoryNoSuchDatasetFault {
		return service.getDataSetSummaryWithDocs(projectCode, saml);
	}

	public void markResponseAsInvalid(long responseId, String annotation, String saml) throws RemoteException, RepositoryServiceFault {
		service.markResponseAsInvalid(responseId, annotation, saml);
	}

	public void markResponseAsValid(long responseId, String saml) throws RemoteException, RepositoryServiceFault {
		service.markResponseAsValid(responseId, saml);
	}

	public RecordDTO getRecordsDocumentsByStatus(String identifier, String status, String saml) 
	throws RemoteException, RepositoryServiceFault {
		return service.getRecordsDocumentsByStatus(identifier, status, saml);
	}

	public String[] getRecordsByGroups(String project, String[] groups, String saml) 
	throws RemoteException, RepositoryServiceFault {
		return service.getRecordsByGroups(project, groups, saml);
	}

	public RecordDTO[] getRecordsWithConsentByGroups(String project, String[] groups, Calendar referenceDate, String saml) throws RemoteException, RepositoryServiceFault{
		return service.getRecordsWithConsentByGroups(project, groups, referenceDate, saml);
	}

	public RecordDTO[] getRecordsWithConsentByGroups(String project, String[] groups, Calendar referenceDate, int batchSize, int offset, String saml) throws RemoteException, RepositoryServiceFault {
		return service.getRecordsWithConsentByGroups(project, groups, referenceDate, batchSize, offset, saml);
	}

	public String[] getRecordsByGroupsAndDocStatus(String project, String[] groups, String status, String saml) 
	throws RemoteException, RepositoryServiceFault {
		return service.getRecordsByGroupsAndDocStatus(project, groups, status, saml);
	}

	public void changeDocumentStatus(String identifier, long docOccId, long newStatusId, String saml) throws RemoteException, RepositoryServiceFault {
		service.changeDocumentStatus(identifier, docOccId, newStatusId, saml);
	}

	public long[] changeDocumentStatus(String identifier, long[] docOccIds, String newStatus, String saml) throws RemoteException, RepositoryServiceFault {
		return service.changeDocumentStatus(identifier, docOccIds, newStatus, saml);
	}

	public void addConsent(long recordId, long consentFormId, String location, String saml) throws RemoteException, RepositoryServiceFault, NotAuthorisedFault {
		service.addConsent(recordId, consentFormId, location, saml);
	}

	public IdentifierDTO addIdentifier(long dataSetId, org.psygrid.data.model.dto.IdentifierDTO identifier, String saml) throws RemoteException, RepositoryServiceFault, NotAuthorisedFault {
		return service.addIdentifier(dataSetId, identifier, saml);
	}

	public long patchDataSet(org.psygrid.data.model.dto.DataSetDTO ds, String saml) throws RemoteException, RepositoryServiceFault, 
			RepositoryOutOfDateFault, NotAuthorisedFault,
			RepositoryNoConsentFault, DuplicateDocumentsFault, ConnectException,
			RepositoryInvalidIdentifierFault, TransformerFault {
		
		return service.patchDataSet(ds, saml);
	}

	public void removeDataSet(long dataSetId, String projectCode, String saml) throws RemoteException, RepositoryServiceFault, NotAuthorisedFault {
		service.removeDataSet(dataSetId, projectCode, saml);
	}

	public void removePublishedDataSet(long dataSetId, String projectCode, String saml) throws RemoteException, RepositoryServiceFault, NotAuthorisedFault {
		service.removePublishedDataSet(dataSetId, projectCode, saml);
	}
	
	public RecordDTO getRecordSingleDocument(long recordId, long docInstId, String saml) throws RemoteException, RepositoryServiceFault, NotAuthorisedFault {
		return service.getRecordSingleDocument(recordId, docInstId, saml);
	}

	public void emailSupport(String subject, String body, String saml) throws RemoteException, RepositoryServiceFault {
		service.emailSupport(subject, body, saml);
	}

	public void requestExport(ExportRequest exportRequest, String saml) throws RemoteException, RepositoryServiceFault, NotAuthorisedFault, RepositoryNoSuchDatasetFault {
		service.requestExport(exportRequest, saml);
	}

	public void requestImmediateExport(ExportRequest exportRequest, String saml) throws RemoteException,
			RepositoryServiceFault, NotAuthorisedFault,
			RepositoryNoSuchDatasetFault {
		service.requestImmediateExport(exportRequest, saml);
	}

	public org.psygrid.data.export.dto.ExportRequest[] getMyExportRequests(String[] projects, String saml) throws RemoteException, RepositoryServiceFault, NotAuthorisedFault {
		return service.getMyExportRequests(projects, saml);
	}

	public byte[] downloadExport(long exportRequestId, String saml) throws RemoteException, RepositoryServiceFault, NotAuthorisedFault, NoSuchExportFault {
		return service.downloadExport(exportRequestId, saml);
	}

	public byte[] downloadExportHash(long exportRequestId, String format, String saml) throws RemoteException, RepositoryServiceFault, NotAuthorisedFault, NoSuchExportFault {
		return service.downloadExportHash(exportRequestId, format, saml);
	}
	
	public String[] getIdentifiers(final long dataSetId, String saml) 
	throws RemoteException, RepositoryServiceFault, NotAuthorisedFault {
		return service.getIdentifiers(dataSetId, saml);
	}

	public String[] getIdentifiersByResponse(String projectCode, String documentName, String entryName, String textValue, String saml)
        	throws RemoteException, RepositoryServiceFault {
		return service.getIdentifiersByResponse(projectCode, documentName, entryName, textValue, saml);
	}

	public IdentifierData[] getIdentifiersExtended(final long dataSetId, String saml) 
	throws RemoteException, RepositoryServiceFault, NotAuthorisedFault {
		return service.getIdentifiersExtended(dataSetId, saml);
	}
		
	public void cancelExport(long exportRequestId, String saml) 
	throws RemoteException, RepositoryServiceFault, NotAuthorisedFault, UnableToCancelExportFault {
		service.cancelExport(exportRequestId, saml);
	}

	public Object transform(long dsId, TransformerDTO transformer, String[] responses, String saml) 
	throws RemoteException, RepositoryServiceFault, NotAuthorisedFault, TransformerFault {
		return service.transform(dsId, transformer, responses, saml);
	}

	public void deleteRecord(String identifier, String saml) throws RemoteException, RepositoryServiceFault, NotAuthorisedFault {
		service.deleteRecord(identifier, saml);
	}

	public String[] getLinkableRecords(String projectCode, String[] groups, String saml) throws RemoteException, RepositoryServiceFault, 
	NotAuthorisedFault {
		return service.getLinkableRecords(projectCode, groups, saml);
	}

	public String[] getLinkedRecords(String projectCode, String[] groups, String saml) throws RemoteException, RepositoryServiceFault, 
	NotAuthorisedFault {
		return service.getLinkedRecords(projectCode, groups, saml);
	}

	public boolean updatePrimaryIdentifier(String identifier, String primaryIdentifier, String saml) throws RemoteException, RepositoryServiceFault, NotAuthorisedFault {
		return service.updatePrimaryIdentifier(identifier, primaryIdentifier, saml);
	}

	public boolean updateSecondaryIdentifier(String identifier, String secondaryIdentifier, String saml) throws RemoteException, RepositoryServiceFault, NotAuthorisedFault {
		return service.updateSecondaryIdentifier(identifier, secondaryIdentifier, saml);
	}

	public String[] getDeletedRecordsByGroups(String project, String[] groups, Calendar referenceDate, String saml) throws RemoteException, RepositoryServiceFault, NotAuthorisedFault {
		return service.getDeletedRecordsByGroups(project, groups, referenceDate, saml);
	}

	public void updateRecordData(long recordId, RecordDataDTO data, String reason, String saml) throws RemoteException, RepositoryServiceFault, NotAuthorisedFault {
		service.updateRecordData(recordId, data, reason, saml);
	}

	public boolean canUpdateRecordData(String projectCode, String groupCode, String saml) throws RemoteException, RepositoryServiceFault, NotAuthorisedFault {
		return service.canUpdateRecordData(projectCode, groupCode, saml);
	}
	
	public void synchronizeDocumentStatusesWithPrimary(String identifier, String saml) throws RemoteException, RepositoryServiceFault, NotAuthorisedFault {
		service.synchronizeDocumentStatusesWithPrimary(identifier, saml);
	}

	public ConsentStatusResult getConsentAndStatusInfoForGroups(String project, String[] groups, Calendar referenceDate, String saml) throws RemoteException, RepositoryServiceFault, NotAuthorisedFault {
		return service.getConsentAndStatusInfoForGroups(project, groups, referenceDate, saml);
	}

	/**
	 * Shoves the ConsentStatusResult into a compressed byte array to get around Axis 1.4 memory hungry serialisation of large objects..
	 */
	public byte[] getConsentAndStatusInfoForGroupsCompressed(String project, String[] groups, Calendar referenceDate, String saml) throws RemoteException, RepositoryServiceFault, NotAuthorisedFault {
		return service.getConsentAndStatusInfoForGroupsCompressed(project, groups, referenceDate, saml);
	}
	
	
	public RecordDTO getRecordSingleDocumentForOccurrence(String identifier, long docOccId, String saml) throws RemoteException, 
	RepositoryServiceFault, NotAuthorisedFault {
		return service.getRecordSingleDocumentForOccurrence(identifier, docOccId, saml);
	}

	
	public SearchRecordChangeHistoryResult searchRecordChangeHistory(
			String project, Calendar start, Calendar end, String user, 
			String identifier, int startIndex, String saml) 
			throws RemoteException, RepositoryServiceFault, NotAuthorisedFault {
		return service.searchRecordChangeHistory(project, start, end, user, identifier, startIndex, saml);
	}

	public DocInstChangeHistoryResult[] searchDocInstChangeHistory(
			String identifier, long parentId, String saml) throws RemoteException {
		return service.searchDocInstChangeHistory(identifier, parentId, saml);
	}

	public ProvenanceForChangeResult[] getProvenanceForChange(
			String identifier, long changeId, String saml)
			throws RemoteException {
		return service.getProvenanceForChange(identifier, changeId, saml);
	}

	public Calendar getSystemTime() throws RemoteException {
		return Calendar.getInstance();
	}

	public long getStatusIdForDocument(String identifier, long docOccId,
			String saml) throws RemoteException, RepositoryServiceFault,
			NotAuthorisedFault {
		return service.getStatusIdForDocument(identifier, docOccId, saml);
	}

	public String[] getPublishedDatasets(String[] projects) throws RemoteException {
		return service.getPublishedDatasets(projects);
	}

	public boolean canRecordBeRandomized(String identifier, String saml)
			throws RemoteException, NotAuthorisedFault, RepositoryServiceFault {
		return service.canRecordBeRandomized(identifier, saml);
	}
		
	/************************ Sample Tracking  ******************************/

	public ConfigInfo getSampleConfig(String projectCode, String saml) throws NotAuthorisedFault, RepositoryServiceFault, RemoteException {
		return service.getSampleConfig(projectCode, saml);
	}

	public void saveSampleConfig(ConfigInfo conf, String saml) throws NotAuthorisedFault, RemoteException, RepositoryServiceFault {
		service.saveSampleConfig(conf, saml);
	}

	public ParticipantInfo getSampleParticipant(String recordID, String saml) throws NotAuthorisedFault, RemoteException, RepositoryServiceFault {
		return service.getSampleParticipant(recordID, saml);
	}

	public void saveSampleParticipant(ParticipantInfo participant, String saml) throws NotAuthorisedFault, RemoteException, RepositoryServiceFault {
		service.saveSampleParticipant(participant, saml);
	}
	
	public SampleInfo[] getSamples(String recordID, String saml) throws NotAuthorisedFault, RemoteException, RepositoryServiceFault {
		return service.getSamples(recordID, saml);
	}

	public SampleInfo saveSample(SampleInfo sample, String saml) throws NotAuthorisedFault, RemoteException, RepositoryServiceFault {
		return service.saveSample(sample, saml);
	}

	public SampleInfo[] getSampleRevisions(long sampleID, String saml) throws NotAuthorisedFault, RemoteException, RepositoryServiceFault {
		return service.getSampleRevisions(sampleID, saml);
	}
	
	// Should probably be record identifier as parameter
	public long getNextSampleNumber(String projectCode,String saml) throws NotAuthorisedFault, RemoteException, RepositoryServiceFault{
		return service.getNextSampleNumber(projectCode, saml);
	}

}

