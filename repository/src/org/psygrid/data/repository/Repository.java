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

import org.psygrid.data.export.NoSuchExportFault;
import org.psygrid.data.export.UnableToCancelExportFault;
import org.psygrid.data.export.dto.ExportRequest;
import org.psygrid.data.importing.ImportData;
import org.psygrid.data.importing.ImportStatus;
import org.psygrid.data.model.dto.DataSetDTO;
import org.psygrid.data.model.dto.GroupDTO;
import org.psygrid.data.model.dto.IdentifierDTO;
import org.psygrid.data.model.dto.RecordDTO;
import org.psygrid.data.model.dto.RecordDataDTO;
import org.psygrid.data.model.dto.StandardCodeDTO;
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

/**
 * Web service interface to a data repository.
 * <p>
 * Provides all functionality required for external clients to interact
 * with the repository i.e. retrieve data from it, save data to it, etc.
 *
 * @author Rob Harper
 *
 */
public interface Repository extends java.rmi.Remote {

    /**
     * Get the software version of the repository web-service.
     *
     * @return The software version.
     * @throws RemoteException
     */
    public String getVersion() throws RemoteException;

    /**
     * Retrieve a single data set, complete except for the binary data
     * of any binary objects it contains.
     *
     * @param dataSetId The unique identifier of the data set to retrieve.
     * @param saml SAML assertion for security system.
     * @return The dataset.
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     */
    public DataSetDTO getDataSetComplete(long dataSetId, String saml)
        throws RemoteException, RepositoryServiceFault;

    /**
     * Save a single dataset to the data repository.
     *
     * @param ds The dataset to save.
     * @param saml SAML assertion for security system.
     * @return The unique identifier of the saved dataset.
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws RepositoryOutOfDateFault
     * @throws NotAuthorisedFault
     */
    public long saveDataSet(DataSetDTO ds, String saml)
        throws RemoteException, RepositoryServiceFault,
        RepositoryOutOfDateFault;

    /**
     * Retrieve the binary data for a single binary object.
     *
     * @param dataSetId The ID of the dataset that the binary object
     * is associated with.
     * @param binaryObjectId The ID of the binary object to
     * retrieve the data for.
     * @param saml SAML assertion for security system.
     * @return The binary data.
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     */
    public byte[] getBinaryData(long dataSetId, long binaryObjectId, String saml)
        throws RemoteException, RepositoryServiceFault;

    /**
     * Retrieve the list of datasets that have been modified
     * since a given reference date.
     *
     * @param referenceDate The reference date after which a dataset
     * will be classified as modified.
     * @param saml SAML assertion for security system.
     * @return The list of modified datasets.
     * @throws RemoteException
     */
    public DataSetDTO[] getModifiedDataSets(Calendar referenceDate, String saml)
        throws RemoteException, ConnectException, RepositoryServiceFault;

    /**
     * Save a single record to the data repository.
     *
     * @param saml SAML assertion for security system.
     * @param record The record.
     * @param discardDuplicates If True, don't throw an exception if duplicate
     * documents are found in the record being saved.
     * @return the unique identifier of the saved record.
     * @throws RemoteException
     * @throws ConnectException
     * @throws RepositoryServiceFault
     * @throws RepositoryOutOfDateFault
     * @throws RepositoryNoConsentFault
     * @throws RepositoryInvalidIdentifierFault
     * @throws TransformerFault
     * @throws NotAuthorisedFault
     * @throws DuplicateDocumentsFault
     */
    public long saveRecord(RecordDTO record, boolean discardDuplicates, String saml)
        throws RemoteException, ConnectException,
               RepositoryServiceFault,
               RepositoryOutOfDateFault,
               RepositoryNoConsentFault,
               RepositoryInvalidIdentifierFault,
               TransformerFault,
               DuplicateDocumentsFault,
               RepositoryNoSuchDatasetFault;

    /**
     * Save a single record to the data repository, using the
     * given user name for provenance details rather than that
     * supplied in the SAML assertion.
     *
     * @param saml SAML assertion for security system.
     * @param recordXML The record.
     * @return the unique identifier of the saved record.
     * @throws RemoteException
     * @throws ConnectException
     * @throws RepositoryServiceFault
     * @throws RepositoryOutOfDateFault
     * @throws RepositoryNoConsentFault
     * @throws RepositoryInvalidIdentifierFault
     * @throws TransformerFault
     * @throws NotAuthorisedFault
     */
    public long saveRecordAsUser(RecordDTO record, String user, String saml)
        throws RemoteException, ConnectException,
               RepositoryServiceFault,
               RepositoryOutOfDateFault,
               RepositoryNoConsentFault,
               RepositoryInvalidIdentifierFault,
               TransformerFault;

    /**
     * Retrieve a single record from the data repository, identified by its database
     * generated unique identifier.
     *
     * @param recordId The database unique identifier of the record to retrieve.
     * @param saml SAML assertion for security system.
     * @return The record.
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     */
    public RecordDTO getRecordComplete(long recordId, String saml)
        throws RemoteException, RepositoryServiceFault;

    /**
     * Retrieve a single record from the data repository, identified by its project
     * generated unique identifier (i.e. study number).
     *
     * @param identifier The project unique identifier of the record to retrieve.
     * @param saml SAML assertion for security system.
     * @return The record.
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     */
    public RecordDTO getRecordComplete(String identifier, String saml)
        throws RemoteException, RepositoryServiceFault;

    /**
	 * Retrieve a record from the repository, identified by its unique external identifier.
     *
     * @param the dataset ID
     * @param externalID the dataset unique identifier of the record to retrieve.
     * @param saml SAML assertion for security system.
     * @return The record.
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     */
    public RecordDTO getRecordCompleteByExternalID(long datasetID, String externalID, String saml)
        throws RemoteException, RepositoryServiceFault;


    /**
     * Retrieve a single record from the data repository in a sumary format
     *
     * @param recordId The database-generated unique identifier of the
     * record to retrieve.
     * @param saml SAML assertion for security system.
     * @return The record.
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     */
    public RecordDTO getRecordSummary(long recordId, String saml)
        throws RemoteException, RepositoryServiceFault;

    /**
     * Retrieve a single record from the data repository in a sumary format
     *
     * @param identifier The system-generated unique identifier of the
     * record to retrieve.
     * @param saml SAML assertion for security system.
     * @return The record.
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     */
    public RecordDTO getRecordSummary(String identifier, String saml)
        throws RemoteException, RepositoryServiceFault;

    /**
     * Retrieve a list containing summary details of all records
     * in the data repository that reference a given dataset.
     *
     * @param dataSetId The unique identifier of the dataset to get
     * records for.
     * @param saml SAML assertion for security system.
     * @return List of records for the dataset.
     * @throws RemoteException
     * @throws NotAuthorisedFault
     * @throws RepositoryServiceFault
     */
    public RecordDTO[] getRecords(long dataSetId, String saml)
        throws RemoteException, RepositoryServiceFault;

    /**
     * Retrieve a list containing summary details of all records
     * in the data repository that reference a given dataset and have
     * the given status.
     *
     * @param dataSetId The unique identifier of the dataset to get
     * records for.
     * @param statusId The unique identifier of the status to get
     * records for.
     * @param saml SAML assertion for security system.
     * @return List of records for the dataset.
     * @throws RemoteException
     * @throws NotAuthorisedFault
     * @throws RepositoryServiceFault
     */
    public RecordDTO[] getRecordsByStatus(long dataSetId, long statusId, String saml)
        throws RemoteException, RepositoryServiceFault;

    /**
     * Generate a batch of unique identifiers to be used in records
     * based on the given dataset.
     *
     * @param dataSetId The dataset to generate unique identifiers for.
     * @param groupCode The code of the group to generate identifiers for.
     * @param number The number of unique identifers to generate.
     * @param saml SAML string for security system.
     * @return Array of generated identifiers.
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     */
    public IdentifierDTO[] generateIdentifiers(long dataSetId, String groupCode, int number, String saml)
        throws RemoteException, RepositoryServiceFault;

    /**
     * Retrieve a list containing all standard codes defined in the
     * repository.
     * @param saml SAML assertion for security system.
     *
     * @return Array of standard codes.
     * @throws RemoteException
     */
    public StandardCodeDTO[] getStandardCodes(String saml)
        throws RemoteException;

    /**
     * Mark a dataset as published; the dataset can no longer be edited
     * and records may now be created for it.
     *
     * @param dataSetId The unique identifier of the dataset to mark
     * as published.
     * @param saml SAML assertion for security system.
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     */
    public void publishDataSet(long dataSetId, String saml)
        throws RemoteException, RepositoryServiceFault;

    /**
     * Do a "dry run" of withdrawing consent from a record to tell us what
     * data (if any) will be removed from the record if the consent is
     * withdrawn for real.
     *
     * @param recordId The id of the record from which consent is being withdrawn.
     * @param consentFormId The id of the consent form for which consent is being
     * withdrawn.
     * @param reason The reason why consent was withdrawn.
     * @param saml SAML assertion for security system.
     * @return Array of document occurrence names of those that would be
     * removed if the consent is withdrawn for real.
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     */
    public String[] withdrawConsentDryRun(long recordId, long consentFormId, String reason, String saml)
        throws RemoteException, RepositoryServiceFault;

    /**
     * Withdraw consent for a single consent form of a record.
     * <p>
     * In response, any documents in the repository that are governed by this
     * consent form which due to its withdrawl no longer have positive consent
     * will be deleted, along with all associated responses.
     *
     * @param recordId The id of the record from which consent is being withdrawn.
     * @param consentFormId The id of the consent form for which consent is being
     * withdrawn.
     * @param reason The reason why consent was withdrawn.
     * @param saml SAML assertion for security system.
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     */
    public void withdrawConsent(long recordId, long consentFormId, String reason, String saml)
        throws RemoteException, RepositoryServiceFault;

    /**
     * Add consent for a single consent form of a record.
     *
     * @param recordId The id of the record to which consent is being added.
     * @param consentFormId The id of the consent form for which consent is being
     * added.
     * @param location Location of the consent form.
     * @param saml SAML assertion for security system.
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     */
    public void addConsent(long recordId, long consentFormId, String location, String saml)
        throws RemoteException, RepositoryServiceFault;

    /**
     * Change the status of a single statused object.
     *
     * @param statusedInstanceId The unique identifier of the object whose
     * status is to be changed.
     * @param newStatusId The unique identifier of the new status to set
     * for this object.
     * @param saml SAML assertion for security system.
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     */
    public void changeStatus(long statusedInstanceId, long newStatusId, String saml)
        throws RemoteException, RepositoryServiceFault;


    /**
     * Change the status of a record.
     *
     * @param identifier The identifier of the record whose
     * status is to be changed.
     * @param newStatusId The unique identifier of the new status to set
     * for this object.
     * @param saml SAML assertion for security system.
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     */
    public void changeRecordStatus(String identifier, long newStatusId, final boolean ignorePermittedTransitions, String saml)
        throws RemoteException, RepositoryServiceFault;


    /**
     * Retrieve a summary of a single dataset, identified by its project
     * code from the security system.
     * <p>
     * If the dataset has not been modified since the last time its summary
     * was retrieved, this date being identified by the referenceDate
     * argument, then <code>null</code> is returned.
     *
     * @param projectCode The project code of the dataset.
     * @param referenceDate The date when this method was last called
     * for the same project code.
     * @param saml SAML assertion for the security system.
     * @return A summary of the dataset identified by the project code, or
     * <code>null</code> if the dataset has not been modified since the
     * last time its summary was retrieved.
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     * @throws RepositoryNoSuchDatasetFault
     */
    public DataSetDTO getDataSetSummary(String projectCode, Calendar referenceDate, String saml)
        throws RemoteException, RepositoryServiceFault, RepositoryNoSuchDatasetFault;


    /**
     * Retrieve a summary of a single dataset, identified by its project
     * code from the security system. Summary details of all the dataset's
     * documents are included in the DataSet's object graph.
     *
     * @param projectCode The project code of the dataset.
     * @param saml SAML assertion for the security system.
     * @return A summary of the dataset identified by the project code.
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     * @throws RepositoryNoSuchDatasetFault
     */
    public DataSetDTO getDataSetSummaryWithDocs(String projectCode, String saml)
        throws RemoteException, RepositoryServiceFault, RepositoryNoSuchDatasetFault;


    /**
     * Mark a single response as being invalid, with an annotation of why
     * it is invalid if appropriate.
     *
     * @param responseId The unique identifier of the response to mark as
     * invalid.
     * @param annotation The annotation describing why the response is invalid
     * @param saml SAML assertion for security system.
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     */
    public void markResponseAsInvalid(long responseId, String annotation, String saml)
        throws RemoteException, RepositoryServiceFault;


    /**
     * Mark a single response as being valid.
     *
     * @param responseId The unique identifier of the response to mark as
     * valid.
     * @param saml SAML assertion for security system.
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     */
    public void markResponseAsValid(long responseId, String saml)
        throws RemoteException, RepositoryServiceFault;


    /**
     * Retrieve a single record from the data repository, but containing
     * only its document instances that have the given status.
     * <p>
     * Note that the returned record will have its database identifier
     * wiped. This is so that when it is saved to the repository we know
     * to re-attach the document instances back to the saved record.
     *
     * @param identifier The identifier of the record to retrieve.
     * @param status The name of the status.
     * @param saml SAML assertion for security system.
     * @return The record.
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     */
    public RecordDTO getRecordsDocumentsByStatus(String identifier, String status, String saml)
        throws RemoteException, RepositoryServiceFault;

    /**
     * Retrieve all records that are associated with the given project,
     * a group in the given list of groups, and have had consent or status
     * modified since the reference date.
     * <p>
     * The record objects returned are summaries, containing consent information.
     *
     * @param project The project code.
     * @param groups The list of group codes.
     * @param referenceDate The reference date.
     * @param saml SAML assertion for the security system.
     * @return The array of record summaries.
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     */
    public RecordDTO[] getRecordsWithConsentByGroups(String project, String[] groups, Calendar referenceDate, String saml)
        throws RemoteException, RepositoryServiceFault;

    /**
     * Retrieve all records that are associated with the given project,
     * a group in the given list of groups, and have had consent or status
     * modified since the reference date.
     * <p>
     * The record objects returned are summaries, containing consent information.
     * <p>
     * Only <code>batchSize</code> records are retrieved at a time. To retrieve all records, first call
     * with <code>offset=0</code>, then with <code>offset=batchSize</code>, <code>offset=2*batchSize</code> etc until the call
     * retrieves fewer than <code>batchSize</code> records.
     *
     * @param project The project code.
     * @param groups The list of group codes.
     * @param referenceDate The reference date.
     * @param batchSize The maximum number of records that will be retrieved by one call.
     * @param offset The starting point in the overall list of records to retrieve the batch from.
     * @param saml SAML assertion for the security system.
     * @return The array of record summaries.
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     */
    public RecordDTO[] getRecordsWithConsentByGroups(String project, String[] groups, Calendar referenceDate, int batchSize, int offset, String saml)
        throws RemoteException, RepositoryServiceFault;

    /**
     * Retrieve the identifiers of all records that are associated with the given
     * project and a group in the given list of groups.
     *
     * @param project The project code.
     * @param groups The list of group codes.
     * @param saml SAML assertion for the security system.
     * @return Array of record identifiers.
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     */
    public String[] getRecordsByGroups(String project, String[] groups, String saml)
        throws RemoteException, RepositoryServiceFault;

    /**
     * Retrieve all records that are associated with the given project and
     * a group in the given list of groups, where one or more of the document
     * instances associated with the record have the given status.
     *
     * @param project The project code.
     * @param groups The list of group codes.
     * @param status The document status.
     * @param saml SAML assertion for the security system.
     * @return The array of record summaries.
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     */
    public String[] getRecordsByGroupsAndDocStatus(String project, String[] groups, String status, String saml)
        throws RemoteException, RepositoryServiceFault;

    /**
     * Change the status of a single document instance, the document instance being
     * identified by the identifier of the record that it belongs to plus the
     * document occurrence that it is an instance of.
     *
     * @param identifier The identifier of the record.
     * @param docOccId The unique id of the document occurrence.
     * @param newStatusId The unique id of the new status.
     * @param saml SAML assertion for the security system.
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     */
    public void changeDocumentStatus(String identifier, long docOccId, long newStatusId, String saml)
        throws RemoteException, RepositoryServiceFault;

    /**
     * Change the status of multiple document instances, each being
     * identified by the identifier of the record that it belongs to plus the
     * document occurrence that it is an instance of.
     *
     * @param identifier The identifier of the record.
     * @param docOccId Array of unique ids of the document occurrences.
     * @param newStatus The short name of the new status.
     * @param saml SAML assertion for the security system.
     * @return Array of document occurrences for which there was a problem
     * changing the document status.
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     */
    public long[] changeDocumentStatus(String identifier, long[] docOccId, String newStatus, String saml)
        throws RemoteException, RepositoryServiceFault;

    /**
     * Manually add an identifier to the repository.
     *
     * @param dataSetId The database identifier of the dataset to which the
     * identifier being saved is associated with.
     * @param identifier The identifier to add.
     * @param saml SAML assertion for the security system.
     * @return the new identifier
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     */
    public IdentifierDTO addIdentifier(long dataSetId, org.psygrid.data.model.dto.IdentifierDTO identifier, String saml)
        throws RemoteException, RepositoryServiceFault;

    /**
     * Patch an existing dataset in the data repository.
     *
     * @param ds The dataset to save.
     * @param saml SAML assertion for security system.
     * @return The unique identifier of the patched dataset.
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws RepositoryOutOfDateFault
     * @throws NotAuthorisedFault
     */
    public long patchDataSet(DataSetDTO ds, String saml)
        throws RemoteException, RepositoryServiceFault,
        RepositoryOutOfDateFault, RepositoryNoConsentFault,
        DuplicateDocumentsFault, ConnectException, RepositoryInvalidIdentifierFault,
        TransformerFault;

    /**
     * Remove a DataSet that has been saved (but not published).
     *
     * @param dataSetId The unique identifier of the dataset to be removed.
     * @param projectCode The unique project code of the dataset.
     * @param saml SAML assertion for security system.
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     */
    public void removeDataSet(long dataSetId, String projectCode, String saml)
        throws RemoteException, RepositoryServiceFault;

    /**
     * Remove a DataSet that has been published, this will remove all
     * associated reports and records as well.
     *
     * @param dataSetId The unique identifier of the dataset to be removed.
     * @param projectCode The unique project code of the dataset.
     * @param saml SAML assertion for security system.
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     */
    public void removePublishedDataSet(long dataSetId, String projectCode, String saml)
        throws RemoteException, RepositoryServiceFault;

    /**
     * Retrieve a document with only the document instance for the single specified
     * document in its object graph.
     *
     * @param recordId The database id of the record.
     * @param docInstId The database id of the document instance.
     * @param saml SAML assertion for security system.
     * @return The record
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     */
    public RecordDTO getRecordSingleDocument(long recordId, long docInstId, String saml)
        throws RemoteException, RepositoryServiceFault;

    /**
     * Send an email to PsyGrid support.
     *
     * @param subject The title of the email
     * @param body The body of the email.
     * @param saml SAML assertion for security system.
     * @throws RepositoryServiceFault
     * @throws RemoteException
     */
    public void emailSupport(String subject, String body, String saml) throws RemoteException, RepositoryServiceFault;

    /**
     * Request an export of data from the PsyGrid data repository.
     * <p>
     * The request will then be serviced at a later date.
     *
     * @param projectCode The project code of the project to export
     * data from.
     * @param groups The list of groups within the project to export data
     * for.
     * @param docOccs The list of document occurrences within the project
     * to export data for.
     * @param format The format the data should be exported into.
     * @param saml SAML assertion for the security system.
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     */
    public void requestExport(ExportRequest exportRequest, String saml)
		throws RemoteException, RepositoryServiceFault, RepositoryNoSuchDatasetFault;

    /**
     * Request an export of data from the PsyGrid data repository for immediate execution.
     *
     * @param projectCode The project code of the project to export
     * data from.
     * @param groups The list of groups within the project to export data
     * for.
     * @param docOccs The list of document occurrences within the project
     * to export data for.
     * @param format The format the data should be exported into.
     * @param saml SAML assertion for the security system.
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     */
    public void requestImmediateExport(ExportRequest exportRequest, String saml)
		throws RemoteException, RepositoryServiceFault, RepositoryNoSuchDatasetFault;

    /**
     * Get the details of all export requests for the given projects submitted
     * by the calling user.
     *
     * @param projects The list of projects.
     * @param saml SAML assertion for the security system.
     * @return Array of export requests.
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     */
    public org.psygrid.data.export.dto.ExportRequest[] getMyExportRequests(String projects[], String saml)
    	throws RemoteException, RepositoryServiceFault;

    /**
     * Download the data for a completed export request.
     *
     * @param exportRequestId The unique id of the export request.
     * @param saml SAML assertion for the security system.
     * @return The exported data.
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     * @throws NoSuchExportFault
     */
    public byte[] downloadExport(long exportRequestId, String saml)
    	throws RemoteException, RepositoryServiceFault, NoSuchExportFault;

    /**
     * Download the hash file for the export file in the given format.
     *
     * @param exportRequestId
     * @param format
     * @param saml
     * @return hashfile
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     * @throws NoSuchExportFault
     */
    public byte[] downloadExportHash(long exportRequestId, String format, String saml)
        throws RemoteException, RepositoryServiceFault, NoSuchExportFault;

    /**
     * Cancel an export request.
     *
     * @param exportRequestId The unique id of the export request.
     * @param saml SAML assertion for the security system.
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     * @throws UnableToCancelExportFault
     */
    public void cancelExport(long exportRequestId, String saml)
    	throws RemoteException, RepositoryServiceFault, UnableToCancelExportFault;

    /**
     * Retrieve a list of identifiers for records in the given dataset
     *
     * @param dataSetId
     * @param saml
     * @return identifiers
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     */
    public String[] getIdentifiers(final long dataSetId, String saml)
    	throws RemoteException, RepositoryServiceFault;

    /**
     * Retrieve a list of identifiers with a response matching text value.
     * 
     * This method assumes one occurrence of the given document in the dataset.
     * 
     * @param projectCode the dataset code
     * @param documentName the document name
     * @param entryName the text entry name
     * @param entryValue the text entry value
     * @return the list of identifiers for records matching the value
     */
    public String[] getIdentifiersByResponse(String projectCode, String documentName, String entryName, String entryValue, String saml)
    	throws RemoteException, RepositoryServiceFault;

    /**
     * Retrieve a list of extended identifier data for records in the given dataset
     *
     * @param dataSetId
     * @param saml
     * @return identifiers
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     */
    public IdentifierData[] getIdentifiersExtended(final long dataSetId, String saml)
    	throws RemoteException, RepositoryServiceFault;

    /**
     * Perform a transformation on the responses provided and return the
     * transformed result value.
     *
     * @param dsId
     * @param transformer
     * @param responses
     * @param saml
     * @return result
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     */
	public Object transform(long dsId, TransformerDTO transformer, String[] responses, String saml)
		throws RemoteException, RepositoryServiceFault, TransformerFault;


	/**
	 * Permanently delete a record from the repository.
	 *
	 * @param identifier The identifier string of the record to delete.
	 * @param saml
	 * @throws RemoteException
	 * @throws RepositoryServiceFault
	 * @throws NotAuthorisedFault
	 */
	public void deleteRecord(String identifier, String saml)
		throws RemoteException, RepositoryServiceFault;


	/**
	 * Get identifiers of all records that are primary records, linked
	 * to secondary records for data replication, for the given project
	 * and groups.
	 *
	 * @param projectCode The project code.
	 * @param groups Array of group codes.
	 * @param saml
	 * @return Array of identifiers
	 * @throws RemoteException
	 * @throws RepositoryServiceFault
	 * @throws NotAuthorisedFault
	 */
	public String[] getLinkedRecords(String projectCode, String[] groups, String saml)
		throws RemoteException, RepositoryServiceFault;


	/**
	 * Get identifiers of all records for which it is possible to link a
	 * record in another dataset to, for the given project and groups.
	 *
	 * @param projectCode The project code.
	 * @param groups Array of group codes.
	 * @param saml
	 * @return Array of identifiers
	 * @throws RemoteException
	 * @throws RepositoryServiceFault
	 * @throws NotAuthorisedFault
	 */
	public String[] getLinkableRecords(String projectCode, String[] groups, String saml)
		throws RemoteException, RepositoryServiceFault;


	/**
	 * Update the value for the primary identifier of a record defined
	 * by its identifier.
	 * <p>
	 * Used when an already saved record has a primary record linked to it.
	 *
	 * @param identifier The identifier of the record.
	 * @param primaryIdentifier The primary identifier to be applied to the record.
	 * @param saml
	 * @return Boolean, True if primary identifier updated OK, False if no record exists
	 * for the given identifier
	 * @throws RemoteException
	 * @throws RepositoryServiceFault
	 * @throws NotAuthorisedFault
	 */
	public boolean updatePrimaryIdentifier(String identifier, String primaryIdentifier, String saml)
		throws RemoteException, RepositoryServiceFault;

	/**
	 * Update the value for the secondary identifier of a record defined
	 * by its identifier.
	 * <p>
	 * Used when an already saved record has a secondary record linked to it.
	 *
	 * @param identifier The identifier of the record.
	 * @param secondaryIdentifier The secondary identifier to be applied to the record.
	 * @param saml
	 * @return Boolean, True if secondary identifier updated OK, False if no record exists
	 * for the given identifier
	 * @throws RemoteException
	 * @throws RepositoryServiceFault
	 * @throws NotAuthorisedFault
	 */
	public boolean updateSecondaryIdentifier(String identifier, String secondaryIdentifier, String saml)
		throws RemoteException, RepositoryServiceFault;


    /**
     * Retrieve the identifiers of records that have been deleted since the
     * given reference date, for the given project and groups.
     *
     * @param project The project code.
     * @param groups The list of group codes.
     * @param referenceDate The reference date.
     * @param saml SAML assertion for the security system.
     * @return The array of record identifiers.
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     */
    public String[] getDeletedRecordsByGroups(String project, String[] groups, Calendar referenceDate, String saml)
        throws RemoteException, RepositoryServiceFault;

    /**
     * Synchronize a the statuses of a record's document instances with those
     * of its linked primary record.
     * <p>
     * If the record is not the secondary in a dual data entry relationship,
     * then nothing will be done.
     *
     * @param identifier The identifier of the record.
     * @param saml SAML assertion for the security system.
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     */
    public void synchronizeDocumentStatusesWithPrimary(String identifier, String saml)
    	throws RemoteException, RepositoryServiceFault;

    /**
     * Update the metadata (study entry date, schedule start date, etc.) for
     * a record, identified by its database-generated unique identifier.
     *
     * @param recordId The id of the record.
     * @param data The new metadata to apply to the record.
     * @param reason The reason why the metadata is being changed.
     * @param saml SAML assertion for the security system.
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     */
    public void updateRecordData(long recordId, RecordDataDTO data, String reason, String saml)
    	throws RemoteException, RepositoryServiceFault;
    /**
     * Get the consent and status (record and document instance) details
     * of record in the given groups of the given project where consent
     * or status has been updated since the given reference date.
     *
     * @param project  The project code.
     * @param groups The list of group codes
     * @param referenceDate The reference date
     * @param saml SAML assertion for the security system.
     * @return Consent and status details.
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     */
    public ConsentStatusResult getConsentAndStatusInfoForGroups(String project, String[] groups, Calendar referenceDate, String saml)
    	throws RemoteException, RepositoryServiceFault;

    /**
     * Get the consent and status (record and document instance) details
     * of record in the given groups of the given project where consent
     * or status has been updated since the given reference date.
     * 
     * This version is a fudge because Axis 1.4 uses all the client heap for large results.
     *
     * @param project  The project code.
     * @param groups The list of group codes
     * @param referenceDate The reference date
     * @param saml SAML assertion for the security system.
     * @return String containing an ConsentStatusResult as a stream .
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     */
    public byte[] getConsentAndStatusInfoForGroupsCompressed(String project, String[] groups, Calendar referenceDate, String saml)
    	throws RemoteException, RepositoryServiceFault;
    
    
    public RecordDTO getRecordSingleDocumentForOccurrence(String identifier, long docOccId, String saml) throws RemoteException,
	RepositoryServiceFault;

    /**
     * Find out whether the user has permission to update record data in
     * the given group and project.
     *
     * @param projectCode
     * @param groupCode
     * @param saml
     * @return boolean
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     */
	public boolean canUpdateRecordData(String projectCode, String groupCode, String saml) throws RemoteException, RepositoryServiceFault;


    /**
     * Get the current system time of the repository.
     * <p>
     * Used to synchronize clocks for clients of the repository when they
     * do not have access to direct NTP.
     *
     * @return The current system time.
     * @throws RemoteException
     */
    public Calendar getSystemTime()
    	throws RemoteException;

    /**
     * Search the change history of records for the given project.
     * <p>
     * The change history items returned can optionally be restricted
     * by a start date, an end date, a user and/or an identifier.
     * <p>
     * Only upto 30 change history items will be returned each call;
     * which 30 is determined by the startIndex argument.
     *
     * @param project The project code of the project.
     * @param start A date that represents the lower limit for the
     * change history items returned, or <code>null</code> if no
     * lower limit is to be applied.
     * @param end A date that represents the upper limit for the
     * change history items returned, or <code>null</code> if no
     * upper limit is to be applied.
     * @param user The DN of the user to restrict the returned
     * change history items by, or <code>null</code> if no restriction
     * on the user is to be applied.
     * @param identifier The identifier of a record to restrict the returned
     * change history items by, or <code>null</code> if no restriction
     * on the identifier is to be applied.
     * @param startIndex The index of the first item to return in the result;
     * if this is 0 then items 0-29 are returned; if 30 then 30-59 etc etc.
     * @param saml SAML assertion for the security system.
     * @return Search results object.
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     */
    public SearchRecordChangeHistoryResult searchRecordChangeHistory(String project, Calendar start, Calendar end, String user, String identifier, int startIndex, String saml)
    	throws RemoteException, RepositoryServiceFault;

    /**
     * Search the change history for items related to Document Instances that
     * have the given parent id, that are part of the given record.
     * <p>
     * The parent id should be the id of a change history item related to a
     * record, so this method returns the change history at the document instance
     * level for all changes saved with the record that time.
     *
     * @param identifier The identifier of the record.
     * @param parentId The database id of the parent change history item.
     * @param saml SAML assertion for the security system.
     * @return Array of matching document instance change history items.
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     */
    public DocInstChangeHistoryResult[] searchDocInstChangeHistory(String identifier, long parentId, String saml)
    	throws RemoteException;


    /**
     * Get all provenance items relating to response-level changes that
     * were created as part of the specified document-level change history
     * item, for the specified record.
     *
     * @param identifier The identifier fo the record.
     * @param changeId The id of the change history item
     * @param saml SAML assertion for the security system.
     * @return Array of matching provenance items.
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     */
    public ProvenanceForChangeResult[] getProvenanceForChange(String identifier, long changeId, String saml)
    	throws RemoteException;


    /**
     * Find the id of the status for the document instance related to the
     * specified document occurrence and identifier.
     *
     * @param identifier The record identifier
     * @param docOccId The id of the document occurrence.
     * @param saml SAML assertion for the security system.
     * @return id of the status.
     * @throws RemoteException
     * @throws RepositoryServiceFault
     * @throws NotAuthorisedFault
     */
    public long getStatusIdForDocument(String identifier, long docOccId, String saml)
    	throws RemoteException, RepositoryServiceFault;


    /**
     * Given a list of projects return an equivalent list containing just
     * those that are published.
     *
     * @param projects List of projects.
     * @return List of published projects.
     * @throws RemoteException
     */
    public String[] getPublishedDatasets(String[] projects) throws RemoteException;

    /**
     * Decide whether a Record may be randomized i.e. a document instance exists
     * for the occurrence that is the randomization trigger, it is not incomplete
     * and randomization has not already been performed.
     *
     * @param identifier The identifier of the record.
     * @param saml
     * @return True if record can be randomized; False if it can't.
     * @throws RemoteException
     * @throws NotAuthorisedFault
     * @throws RepositoryServiceFault
     */
    public boolean canRecordBeRandomized(String identifier, String saml)
    	throws RemoteException, RepositoryServiceFault;

    
    /****************** Sample Tracking **********************/

    ConfigInfo getSampleConfig(String projectCode,String saml) throws RemoteException, RepositoryServiceFault;

	void saveSampleConfig(ConfigInfo conf,String saml) throws RemoteException, RepositoryServiceFault;

	ParticipantInfo getSampleParticipant(String recordID,String saml) throws RemoteException, RepositoryServiceFault;

	void saveSampleParticipant(ParticipantInfo participant,String saml) throws RemoteException, RepositoryServiceFault;
	
	SampleInfo saveSample(SampleInfo sample,String saml) throws RemoteException, RepositoryServiceFault;
	
	SampleInfo[] getSamples(String recordID,String saml) throws RemoteException, RepositoryServiceFault;
	
	SampleInfo[] getSampleRevisions(long sampleID,String saml) throws RemoteException, RepositoryServiceFault;

	long getNextSampleNumber(String projectCode,String saml) throws RemoteException, RepositoryServiceFault;

	/****************** End Sample Tracking **********************/        
    
}