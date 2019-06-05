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

package org.psygrid.data.repository.dao;

import java.io.OutputStream;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.xml.stream.XMLStreamException;

import org.psygrid.data.export.hibernate.ExportRequest;
import org.psygrid.data.export.hibernate.ExportSecurityActionMap;
import org.psygrid.data.model.IPersistent;
import org.psygrid.data.model.dto.DataSetDTO;
import org.psygrid.data.model.dto.IdentifierDTO;
import org.psygrid.data.model.dto.RecordDTO;
import org.psygrid.data.model.dto.StandardCodeDTO;
import org.psygrid.data.model.dto.extra.ConsentStatusResult;
import org.psygrid.data.model.dto.extra.DocInstChangeHistoryResult;
import org.psygrid.data.model.dto.extra.GroupSummary;
import org.psygrid.data.model.dto.extra.IdentifierData;
import org.psygrid.data.model.dto.extra.ProvenanceForChangeResult;
import org.psygrid.data.model.dto.extra.SearchRecordChangeHistoryResult;
import org.psygrid.data.model.hibernate.BinaryData;
import org.psygrid.data.model.hibernate.Record;
import org.psygrid.data.model.hibernate.ResponseStatus;
import org.psygrid.data.model.hibernate.Group;
import org.psygrid.data.model.hibernate.RetrieveDepth;
import org.psygrid.data.repository.transformer.TransformerClient;
import org.psygrid.data.repository.transformer.TransformerException;
import org.psygrid.data.utils.esl.EslException;
import org.psygrid.data.utils.security.DocumentSecurityHelper;
import org.springframework.mail.SimpleMailMessage;

/**
 * Repository data access.
 * 
 * @author Rob Harper
 *
 */
public interface RepositoryDAO {

    //methods from DataSetDAO
	
    /**
     * Retrieve a list of available DataSets from the repository.
     * 
     * @return List of DataSets.
     */
    public DataSetDTO[] getDataSets();
    
    /**
     * Retrieve a list of available data sets from the repository that
     * have been modified since a given reference date.
     * 
     * @param The reference date.
     * @return List of data sets modified after the reference date.
     */
    public DataSetDTO[] getModifiedDataSets(Date referenceDate);
    
    /**
     * Retrieve a single DataSet from the data repository.
     * <p>
     * The whole dataset is initialized, right down to entry level.
     * 
     * @param dataSetId Unique identifier of the DataSet to retrieve.
     * @return The DataSet with the unique identifier in the argument.
     * @throws DAOException if no DataSet exists with the unique 
     * identifier specified in the argument.
     */
    public DataSetDTO getDataSet(Long dataSetId)
        throws DAOException;
    
    /**
     * Save a single DataSet to the repository.
     * <p>
     * The dataset object being saved is a dto dataset.
     * 
     * @param dataSet The DataSet to save.
     * @return The unique identifier of the saved dataset.
     * @throws DAOException if the DataSet has been published.
     * @throws ObjectOutOfDateException if the DataSet has been updated
     * or deleted by another session.
     */
    public Long saveDataSet(DataSetDTO dataSet)
        throws DAOException, ObjectOutOfDateException;
    
    /**
     * Remove a single DataSet from the data repository.
     * <p>
     * It is not possible to remove a DataSet that has previously 
     * been published - in this case, an exception will be thrown.
     * 
     * @param dataSetId Unique identifier of the DataSet to remove.
     * @throws DAOException if no DataSet exists with the unique 
     * identifier specified in the argument; if the DataSet trying to be
     * removed is published.
     */
    public void removeDataSet(Long dataSetId) 
        throws DAOException;
    
    /**
     * Remove a single DataSet from the data repository.
     * <p>
     * It is not normally possible to remove a DataSet that has previously 
     * been published, however in this case, with additional checks it is
     * allowed. It is anticipated that it will be used when testing or
     * first loading new projects.
     * 
     * @param dataSetId Unique identifier of the DataSet to remove.
     * @throws DAOException if no DataSet exists with the unique 
     * identifier specified in the argument; if the DataSet unique ID and
     * name do not match.
     */
    public void removePublishedDataSet(Long dataSetId, String dataSetProjectCode) 
        throws DAOException;
    
    /**
     * Publish a single DataSet in the data repository.
     * 
     * @param dataSetId Unique identifier of the DataSet to publish.
     * @throws DAOException if no DataSet exists with the unique 
     * identifier specified in the argument; if the DataSet has already been 
     * published.
     */
    public void publishDataSet(Long dataSetId) 
        throws DAOException;

    /**
     * Generate a batch of identifiers to apply to records associated
     * with a given dataset.
     * 
     * @param projectCode The code of the project to generate identifiers for.
     * @param groupCode The code of the group to generate identifiers for.
     * @param number The number of Identifiers to generate.
     * @param maxSuffix The maximum suffix to generate identifier suffixes up to.
     * @param user The user who the indentifiers are being generated for.
     * @return Array of identifiers.
     * @throws DAOException
     */
    public IdentifierDTO[] generateIdentifiers(String projectCode, String groupCode, int number, int maxSuffix, String user)
        throws DAOException;
    
    /**
     * Find the project code for a given dataset, identified
     * by its unique identifier in the database.
     * 
     * @param dataSetId The unique identifier of the dataset.
     * @return The project code for the dataset.
     * @throws DAOException if no dataset exists for the given id.
     */
    public String getProjectCodeForDataset(Long dataSetId)
        throws DAOException;
    
    /**
     * Retrieve a summary of a single dataset, identified by its
     * project code.
     * 
     * @param projectCode The project code.
     * @param depth Parameter to define how much of the DataSet's object
     * graph to populate.
     * @return Summary of the dataset.
     * @throws NoDatasetException If no dataset exists for the given project
     * code.
     */
    public DataSetDTO getSummaryForProjectCode(String projectCode, RetrieveDepth depth)
        throws NoDatasetException;
    
    /**
     * Find whether a single dataset, identified by its project code,
     * has been modified since a given reference date.
     * 
     * @param projectCode The project code of the dataset to check.
     * @param referenceDate The reference date
     * @return True if the dataset has been modified since the reference
     * date, False otherwise.
     * @throws NoDatasetException If no dataset exists for the given project
     * code.
     */
    public boolean getDataSetModified(String projectCode, Date referenceDate)
        throws NoDatasetException;
    
    /**
     * Get the short name of a status object, identified by its
     * unique identifier.
     * 
     * @param statusId The unique id of the status.
     * @return The short name of the status
     * @throws DAOException If no status exists for the given id.
     */
    public String getStatusShortName(Long statusId)
        throws DAOException;
    
    /**
     * Reserve space for a given number of identifiers, for the given
     * project and dataset.
     * 
     * @param dataSetId The database id of the dataset.
     * @param group The name of the group.
     * @param nIdentifiers The number of identifiers to reserve.
     * @return 
     * @throws DAOException
     */
    public Integer reserveIdentifierSpace(Long dataSetId, String group, int nIdentifiers)
        throws DAOException;

    /**
     * Patch a single DataSet in the repository.
     * <p>
     * This is essentially the same as saveDataSet, except no check
     * is made for the dataset being already published before trying
     * to save it.
     * 
     * The returned DTO contains incremented autoVersionNos for changed entries and documents
     * 
     * @param dataSet The DataSet to save.
     * @return The dto version of the dataset.
     * @throws DAOException if the DataSet has been published.
     * @throws ObjectOutOfDateException if the DataSet has been updated
     * or deleted by another session.
     */
    public org.psygrid.data.model.dto.DataSetDTO patchDataSet(DataSetDTO dataSet)
        throws DAOException, ObjectOutOfDateException;
        
    public Object[] executeHQLQuery(String query) throws DAOException;
   

    /**
     * Given a list of projects return an equivalent list containing just
     * those that are published.
     * 
     * @param projects List of projects.
     * @return List of published projects.
     */
    public String[] getPublishedDatasets(final String[] projects);
	
    
    /**
     * Retrieve the binary data for a given binary object.
     * 
     * @param binaryObjectId The unique identifier of the
     * binary object to get the binary data for.
     * @return The binary data.
     * @throws ModelException if no binary object exists for the
     * given id.
     */
    public BinaryData getBinaryData(Long binaryObjectId)
        throws DAOException;
            
            
    //methods from PersistentDAO
    
    /**
     * Retrieve a single Element object from the data repository.
     * 
     * @param persistentId Unique identifier of the Element to retrieve
     * @return The Element with the unique identifier in the argument
     * @throws DAOException if no Element exists with the unique 
     * identifier specified in the argument.
     */
    public IPersistent getPersistent(Long persistentId) throws DAOException; 

    public boolean doesObjectExist(String objectName, Long objectId) throws DAOException;

    // Record related methods

    /**
     * Save a single Record to the data repository.
     * <p>
     * The record object being saved is a dto record - this is converted
     * to a hibernate record before the record is saved.
     * 
     * @param record The Record to save.
     * @param discardDuplicates If True, discard duplicate documents (just log 
     * that they were discarded); if False, if duplicate documents are found an
     * exception is thrown.
     * @param DocumentSecurityHelper docHelper 
     * @param userName The name of the user who is saving the record.
     * @return The unique identifier of the saved record.
     * @throws DAOException if the Record cannot be saved; if the record 
     * does not include positive consent.
     * @throws ObjectOutOfDateException if the Record has been
     * concurrently updated by another session.
     * @throws NoConsentException if there is insufficient consent 
     * associated with the record preventing one of more document
     * instances from being saved.
     * @throws DuplicateDocumentsException if one or more document instances
     * could not be saved as they are duplicates of document instances
     * already saved in the repository.
     */
    public Long saveRecord(org.psygrid.data.model.dto.RecordDTO record, boolean discardDuplicates, DocumentSecurityHelper docHelper, String userName) 
            throws DAOException, ObjectOutOfDateException, NoConsentException, DuplicateDocumentsException;
    
    /**
     * Save a single Record to the data repository.
     * <p>
     * The record object being saved is a hibernate record.
     * 
     * @param record The Record to save.
     * @param discardDuplicates If True, discard duplicate documents (just log 
     * that they were discarded); if False, if duplicate documents are found an
     * exception is thrown.
     * @param DocumentSecurityHelper docHelper 
     * @param userName The name of the user who is saving the record.
     * @return The unique identifier of the saved record.
     * @throws DAOException if the Record cannot be saved; if the record 
     * does not include positive consent.
     * @throws ObjectOutOfDateException if the Record has been
     * concurrently updated by another session.
     * @throws NoConsentException if there is insufficient consent 
     * associated with the record preventing one of more document
     * instances from being saved.
     * @throws DuplicateDocumentsException if one or more document instances
     * could not be saved as they are duplicates of document instances
     * already saved in the repository.
     */
    public Long saveRecord(Record record, boolean discardDuplicates, DocumentSecurityHelper docHelper, String userName)
            throws DAOException, ObjectOutOfDateException, NoConsentException, DuplicateDocumentsException;
    
    /**
     * Retrieve a single record from the data repository, where the
     * record to retrieve is identified by it's database-generated unique
     * identifier.
     * <p>
     * The depth of the record's object graph that is populated is
     * dependent on the RetrieveDepth in the argument
     * 
     * @param recordId The database-generated unique identifier of the Record.
     * @param depth Enum value that defines how much of the records object
     * graph to populate.
     * @return The Record with the unique identifier in the argument.
     * @throws DAOException if no Record exists with the unique
     * identifier specified in the argument.
     */
    public org.psygrid.data.model.dto.RecordDTO getRecord(Long recordId, RetrieveDepth depth) throws DAOException;
    
    /**
     * Retrieve a single record from the data repository, where the
     * record to retrieve is identified by it's PsyGrid-generated unique
     * identifier.
     * <p>
     * The depth of the record's object graph that is populated is
     * dependent on the RetrieveDepth in the argument
     * 
     * @param identifier The PsyGrid-generated unique identifier of the Record.
     * @param depth Enum value that defines how much of the records object
     * graph to populate.
     * @return The Record with the unique identifier in the argument.
     * @throws DAOException if no Record exists with the unique
     * identifier specified in the argument.
     */
    public org.psygrid.data.model.dto.RecordDTO getRecord(String identifier, RetrieveDepth depth) throws DAOException;

    /**
     * Retrieve a single record from the data repository, where the
     * record to retrieve is identified by its unique external identifier.
     * <p>
     * The depth of the record's object graph that is populated is
     * dependent on the RetrieveDepth in the argument
     * 
     * @param externalID the unique external identifier of the Record.
     * @param depth Enum value that defines how much of the records object
     * graph to populate.
     * @return The Record with the unique identifier in the argument.
     * @throws DAOException if no Record exists with the unique
     * identifier specified in the argument.
     */
    public org.psygrid.data.model.dto.RecordDTO getRecordByExternalID(long datasetID, String externalID, RetrieveDepth depth) throws DAOException;

    /**
     * Retrieve a list containing summary details of all records
     * based on a given dataset.
     * 
     * @param dataSetId The unique identifier of the dataset.
     * @return List of records for the given dataset.
     */
    public org.psygrid.data.model.dto.RecordDTO[] getRecords(Long dataSetId, RetrieveDepth rD);
    
    public org.psygrid.data.model.dto.RecordDTO[] getRecords(Long dataSetId);


    /**
     * Retrieve a list containing summary details of all records
     * based on a given dataset having a given status.
     * 
     * @param dataSetId The unique identifier of the dataset.
     * @param statusId The unique identifier of the status.
     * @return List of records for the given dataset.
     */
    public org.psygrid.data.model.dto.RecordDTO[] getRecordsByStatus(Long dataSetId, Long statusId);

    /**
     * Retrieve a list of scheduling reminders that need to be sent
     * to users of the system on the given day, for all datasets.
     * 
     * @param now The date for which to check for scheduled reminders.
     * @return The list of scheduling reminders.
     * @throws DAOException if an error occurs whilst trying to construct 
     * email reminders.
     */
    public List<SimpleMailMessage> getAllScheduledReminders(Date now) throws DAOException;
    
    /**
     * Retrieve a list of scheduling reminders that need to be sent
     * to users of the system on the given day, for the given dataset.
     * 
     * @param now The date for which to check for scheduled reminders.
     * @param dataSetId The unique identifier of the dataset.
     * @return The list of scheduling reminders.
     * @throws DAOException if an error occurs whilst trying to construct 
     * email reminders.
     */
    public List<SimpleMailMessage> getScheduledRemindersForDataset(Date now, Long dataSetId) throws DAOException;
    
    /**
     * Get the project code for a given element instance, identified by
     * its unique identifier in the database.
     * 
     * @param instanceId The unique identifier of the element instance.
     * @return The project code.
     * @throws DAOException if no element instance exists for the given id.
     */
    public String getProjectCodeForInstance(Long instanceId) throws DAOException;

    /**
     * Get the group code for a given element instance, identified by
     * its unique identifier in the database.
     * 
     * @param instanceId The unique identifier of the element instance.
     * @return The group code.
     * @throws DAOException if no element instance exists for the given id.
     */
    public String getGroupCodeForInstance(Long instanceId) throws DAOException;
    
    /**
     * Get the project and group codes for a given element instance, identified by
     * its unique identifier in the database.
     * 
     * @param instanceId The unique identifier of the element instance.
     * @return Array containing the project code (zeroth element) and group code
     * (first element).
     * @throws DAOException if no element instance exists for the given id.
     */
    public String[] getProjectAndGroupForInstance(Long instanceId) throws DAOException;
    
    
    /**
     * Do a "dry run" of withdrawing consent from a record to tell us what
     * data (if any) will be removed from the record if the consent is
     * withdrawn for real.
     * 
     * @param recordId The unique identifier of the record that the consent
     * to be withdrawn is associated with.
     * @param consentFormId The unique identifier of the consent form to 
     * whose consent is to be withdrawn.
     * @param reason The reason why consent has been withdrawn.
     * @param userName The user name of the user who is withdrawing consent.
     * @param saml
     * @return Array of document occurrence names of those that would be
     * removed if the consent is withdrawn for real.
     * @throws DAOException if no record exists for the given id; if no
     * consent form exists for the given id.
     */
    public String[] withdrawConsentDryRun(Long recordId, Long consentFormId, String reason, String userName, String saml) 
	throws DAOException;
    
    /**
     * Method to represent the withdrawl of consent for a single consent form
     * by the subject of a record.
     * <p>
     * In response, any documents in the repository that are governed by this 
     * consent form which due to its withdrawl no longer have positive consent
     * will be deleted, along with all associated responses.
     * 
     * @param recordId The unique identifier of the record that the consent
     * to be withdrawn is associated with.
     * @param consentFormId The unique identifier of the consent form to 
     * whose consent is to be withdrawn.
     * @param reason The reason why consent has been withdrawn.
     * @param userName The user name of the user who is withdrawing consent.
     * @param saml
     * @throws DAOException if no record exists for the given id; if no
     * consent form exists for the given id.
     * @throws EslException if there was aproblem whilst trying to lock the
     * subject in the ESL.
     */
    public void withdrawConsent(Long recordId, Long consentFormId, String reason, String userName, String saml) 
    		throws DAOException, EslException;

    /**
     * Method to add consent for a single consent form to a record.
     * 
     * @param recordId The unique identifier of the record that the consent
     * to be added is associated with.
     * @param consentFormId The unique identifier of the consent form for
     * which consent is being added.
     * @param location The physical location of the paper-based consent.
     * @param userName The user name of the user who is withdrawing consent.
     * @param saml
     * @throws DAOException if no record exists for the given id; if no
     * consent form exists for the given id.
     */
    public void addConsent(Long recordId, Long consentFormId, String location, String userName, String saml) 
    	throws DAOException, EslException;

    /**
     * Method to change the status of a single statused instance in the
     * repository.
     * 
     * @param statusedInstanceId The unique identifier of the statused instance
     * whose status is to be changed.
     * @param newStatusId The unique identifier of the new status for the
     * statused instance.
     * @param userName The name of the user who is saving the record.
     * @throws DAOException if no statused instance exists for the given id;
     * if no status exists for the given id; if it is not permitted to change
     * the status to the specified value.
     */
    public void changeStatus(Long statusedInstanceId, Long newStatusId, String userName) throws DAOException;

    /**
     * Method to change the status of a single statused instance in the
     * repository.
     * 
     * @param statusedInstanceId The unique identifier of the statused instance
     * whose status is to be changed.
     * @param newStatusId The unique identifier of the new status for the
     * statused instance.
     * @param userName The name of the user who is saving the record.
     * @param ignorePermittedTransitions If True ignore the list of permitted
     * status transitions when changing the status.
     * @throws DAOException if no statused instance exists for the given id;
     * if no status exists for the given id; if it is not permitted to change
     * the status to the specified value.
     */
    public void changeStatus(Long statusedInstanceId, Long newStatusId, String userName, boolean ignorePermittedTransitions) throws DAOException;

    /**
     * Change the status of a single document instance in the repository,
     * where the instance is defined by the identifier of the record it
     * is in, plus the database id of the document occurrence that it
     * references.
     * 
     * @param identifier The identifier of the record.
     * @param docOccId The database id of the document occurrence.
     * @param newStatusId The database id of the new status.
     * @param userName
     * @throws DAOException
     */
    public void changeDocumentStatus(String identifier, Long docOccId, Long newStatusId, String userName) throws DAOException;

    /**
     * Change the status of multiple document instances in the repository,
     * where each instance is defined by the identifier of the record it
     * is in, plus the database id of the document occurrence that it
     * references.
     * 
     * @param identifier The identifier of the record.
     * @param docOccIds The database ids of the document occurrences.
     * @param newStatus The short name of the new status.
     * @param userName
     * @return Array of document occurrences for which there was a problem 
     * changing the document status.
     * @throws DAOException
     */
    public long[] changeDocumentStatus(String identifier, List<Long> docOccIds, String newStatus, String userName) throws DAOException;

    /**
     * Change the status of a record in the repository, identifier by its
     * identifier.
     * 
     * @param identifier The record identifier.
     * @param newStatusId The id of th enew status
     * @param userName 
     * @param ignorePermittedTransitions
     * @param saml
     * @throws DAOException
     */
    public void changeRecordStatus(String identifier, Long newStatusId, String userName, boolean ignorePermittedTransitions, String saml) throws DAOException;
    
    /**
     * Get the short name of the current status of a statused instance.
     * 
     * @param statusedInstanceId The unique identifier of the statused
     * instance to get the current status for.
     * @return The short name of the current status.
     * @param newStatusId The unique identifier of the new status for the
     * statused instance.
     * @param userName The name of the user who is saving the record.
     * @throws DAOException if no document instance exists for the given identifier
     * and document occurrence id; if no status exists for the given id; 
     * if it is not permitted to change the status to the specified value.
     */
    public String getShortNameOfCurrentStatus(Long statusedInstanceId) throws DAOException;
    
    /**
     * Get the short name of the current status of a document instance, 
     * where the instance is defined by the identifier of the record it
     * is in, plus the database id of the document occurrence that it
     * references.
     * 
     * @param identifier The identifier of the record.
     * @param docOccId The database id of the document occurrence.
     * @return The short name of the current status.
     * @throws DAOException If no document instance exists for the given
     * identifier and document occurrence.
     */
    public String getShortNameOfCurrentStatus(String identifier, Long docOccId) throws DAOException;

    public void updateResponseStatusAnnotation(Long responseId, ResponseStatus status, String annotation)
        throws DAOException;
    
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
     * @return The record, with only incomplete document instances.
     * @throws DAOException If no record exists for the given identifier.
     */
    public org.psygrid.data.model.dto.RecordDTO getRecordsDocumentsByStatus(String identifier, String status) 
        throws DAOException;
    
    /**
     * Retrieve the identifiers of all records that are associated with the 
     * given project and a group in the given list of groups.
     * 
     * @param project The project code.
     * @param groups The list of group codes.
     * @return Array of record identifiers.
     * @throws DAOException
     */
    public String[] getRecordsByGroups(String project, String[] groups) throws DAOException;

    /**
     * Retrieve all records that are associated with the given project and 
     * a group in the given list of groups, and whose consent and/or status
     * has been modified since the given reference date.
     * 
     * @param project The project code.
     * @param groups The list of group codes.
     * @param referenceDate The reference date.
     * @param depth The retrieve depth to use when converting Hibernate
     * Record objects to DTO Record objects.
     * @return The array of record summaries.
     * @throws DAOException
     */
    public org.psygrid.data.model.dto.RecordDTO[] getRecordsByGroups(String project, String[] groups, Date referenceDate, RetrieveDepth depth) throws DAOException;

    /**
     * Retrieve all records that are associated with the given project and 
     * a group in the given list of groups, and whose consent and/or status
     * has been modified since the given reference date.
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
     * @param depth The retrieve depth to use when converting Hibernate
     * Record objects to DTO Record objects.
     * @return The array of record summaries.
     * @throws DAOException
     */
    public org.psygrid.data.model.dto.RecordDTO[] getRecordsByGroups(String project, String[] groups, Date referenceDate, int batchSize, int offset, RetrieveDepth depth) throws DAOException;

    /**
     * Retrieve all records that are associated with the given project and
     * a group in the given list of groups, where one or more of the document
     * instances associated with the record have the given status.
     * 
     * @param project The project code.
     * @param groups The list of group codes.
     * @param status The document status.
     * @return identifiers
     * @throws DAOException
     */
    public String[] getRecordsByGroupsAndDocStatus(String project, String[] groups, String status) throws DAOException;

    /**
     * Add an identifier to the repository.
     * 
     * @param identifier The identifier to add.
     * @throws DAOException if this identifier already exists.
     */
    public org.psygrid.data.model.dto.IdentifierDTO addIdentifier(org.psygrid.data.model.dto.IdentifierDTO identifier) throws DAOException;
    
    /**
     * Test to see if the object equivalent to the given unique identifier
     * is a Record or not.
     * 
     * @param objectId The unique identifier of the object to test.
     * @return True if the object is a Record, otherwise False.
     */
    public boolean isObjectARecord(Long objectId);
    
    /**
     * Test to see if the object equivalent to the given unique identifier
     * is a DocumentInstance or not.
     * 
     * @param objectId The unique identifier of the object to test.
     * @return True if the object is a DocumentInstance, otherwise False.
     */
    public boolean isObjectADocument(Long objectId);
    
    /**
     * Remove all records belonging to a specified DataSet from the data 
     * repository. It is anticipated that it will be used when testing or
     * first loading new projects.
     * 
     * @param dataSetId The unique identifier of the Data Set the records belong to.
     * @param projectCode The project code of the Data Set.
     * @throws DAOException if no DataSet exists with the unique 
     * identifier specified in the argument; if the DataSet unique ID and
     * name do not match.
     */
    public void removeRecordsForDataSet(Long dataSetId, String projectCode) throws DAOException;
    
    /**
     * Remove a single record belonging to the specified dataset. This is intended to 
     * be used to remove <i>all</i> records for the given dataset, but is implemented
     * to remove them one at a time to avoid problems with very large transactions.
     * Returns True is there are still records to delete for the dataset, False
     * otherwise.
     * 
     * @param dataSetId The unique identifier of the Data Set the records belong to.
     * @param projectCode The project code of the Data Set.
     * @return Boolean, True is there are still records to delete for the dataset, False
     * otherwise.
     * @throws DAOException if no DataSet exists with the unique 
     * identifier specified in the argument; if the DataSet unique ID and
     * name do not match.
     */
    public boolean removeRecordForDataSet(Long dataSetId, String projectCode) throws DAOException;
    
    /**
     * Remove a single record from the repository, specified by its identifier.
     * 
     * @param identifier The identifier of the record to remove.
     * @throws DAOException if no Record exists for the specified identifier.
     */
    public void removeRecord(final String identifier) throws DAOException;
    
    /**
     * The Record object has a circular reference with itself (via its
     * inheritance from ElementInstance. This needs to be removed before we can try
     * to delete the record.
     * <p>
     * This method clears this reference for all records associated with a
     * specified dataset.
     * 
     * @param dataSetId The id of the dataset.
     * @param projectCode The project code of the dataset.
     * @throws DAOException if no DataSet exists with the unique 
     * identifier specified in the argument; if the DataSet unique ID and
     * name do not match.
     */
    public void removeRecordCircularRef(Long dataSetId, String projectCode) throws DAOException;

    /**
     * The Record object has a circular reference with itself (via its
     * inheritance from ElementInstance. This needs to be removed before we can try
     * to delete the record.
     * <p>
     * This method clears this reference for a single record specified by its
     * identifier.
     * 
     * @param identifier The identifier of the record.
     * @throws DAOException if no Record exists with the specified identifier.
     */
    public void removeRecordCircularRef(String identifier) throws DAOException;

    /**
     * Retrieve a document with only the document instance for the single specified 
     * document in its object graph.
     * 
     * @param recordId The database id of the record.
     * @param documentId The database id of the document to get the instance for.
     * @return The record.
     * @throws DAOException if no Record exists with the specified identifier.
     */
    public RecordDTO getRecordSingleDocument(Long recordId, Long documentId) throws DAOException;

    /**
     * Retrieve a list of monthly summary emails that need to be sent
     * to users of the system on the given day, for all datasets.
     * <p>
     * Each monthly summary email will contain details of the documents
     * that need to be completed in the next calendar month for a record.
     * 
     * @param now The date for which the monthly summary is being created.
     * @return The list of monthly summary emails.
     * @throws DAOException if an error occurs whilst trying to construct 
     * monthly summary emails.
     */
    public List<SimpleMailMessage> getAllMonthlySummaries(Date now) throws DAOException;
    
    /**
     * Retrieve a list of monthly summary emails that need to be sent
     * to users of the system on the given day, for the given dataset.
     * <p>
     * Each monthly summary email will contain details of the documents
     * that need to be completed in the next calendar month for a record.
     * 
     * @param now The date for which the monthly summary is being created.
     * @param dataSetId The unique identifier of the dataset.
     * @return The list of monthly summary emails.
     * @throws DAOException if an error occurs whilst trying to construct 
     * monthly summary emails.
     */
    public List<SimpleMailMessage> getMonthlySummariesForDataset(Date now, Long dataSetId) throws DAOException;
    
    /**
     * Retrieve a list of identifiers from the records in a given dataset.
     * 
     * @param dataSetId
     * @return identifiers
     */
    public String[] getIdentifiers(final Long dataSetId);
    
    /**
     * Retrieve a list of identifiers for documents containing a text entry with a given value.
     * 
     * @param projectCode the dataset code
     * @param documentName the document name
     * @param entryName the text entry name
     * @param entryValue the text entry value
     * @return the list of identifiers for records matching the value
     */
   public String[] getIdentifiersByResponse(String projectCode, String documentName, String entryName, String textValue);

    /**
     * Retrieve a list of extended identifier data from the records in a given dataset.
     * 
     * @param dataSetId
     * @return identifiers
     */
    public IdentifierData[] getIdentifiersExtended(final Long dataSetId);
    
    /**
     * Get the identifiers of all records for which it is possible to link a record 
     * in another dataset to, that are associated with the given project and a 
     * group in the given list of groups.
     * 
     * @param project The project code.
     * @param groups The list of group codes.
     * @return Array of identifiers.
     * @throws DAOException
     */
    public String[] getLinkableRecords(String project, String[] groups) throws DAOException;

    /**
     * Get the identifiers of all records that are linked to a secondary record, 
     * for the given groups of the given project.
     * 
     * @param project
     * @param groups
     * @return Array of identifiers.
     * @throws DAOException
     */
	public String[] getLinkedRecords(final String project, final String[] groups) throws DAOException;

	/**
	 * Update the value for the primary identifier of a record defined
	 * by its identifier.
	 * <p>
	 * Used when an already saved record has a primary record linked to it.
	 * 
     * @param identifier The identifier of the record to update.
     * @param primaryIdentifier The primary identifier to be applied to the record.
	 * @return Boolean, True if primary identifier updated OK, False if no record exists
	 * for the given identifier
     */
    public boolean updatePrimaryIdentifier(final String identifier, final String primaryIdentifier);
    
    /**
	 * Update the value for the secondary identifier of a record defined
	 * by its identifier.
	 * <p>
	 * Used when an already saved record has a secondary record linked to it.
	 * 
	 * @param identifier The identifier of the record to update.
	 * @param secondaryIdentifier The secondary identifier to be applied to the record.
	 * @return Boolean, True if secondary identifier updated OK, False if no record exists
	 * for the given identifier
     */
    public boolean updateSecondaryIdentifier(final String identifier, final String secondaryIdentifier);
    
    /**
     * Delete a record.
     * <p>
     * Deleting a record consists of removing all of its data and just leaving the
     * Record object itself, which has the deleted flag set to True.
     * 
     * @param identifier The identifier of the Record to delete.
     * @throws DAOException
     */
    public void deleteRecord(String identifier) throws DAOException;
    
    /**
     * Retrieve the identifiers of records that have been deleted since the
     * given reference date, for the given project and groups.
     * 
     * @param project The project code.
     * @param groups The list of group codes.
     * @param referenceDate The reference date.
     * @return Array of identifiers of deleted records
     * @throws DAOException
     */
    public String[] getDeletedRecordsByGroups(String project, String[] groups, Date referenceDate) throws DAOException;
    
    /**
     * Synchronize a the statuses of a record's document instances with those
     * of its linked primary record.
     * <p>
     * If the record is not the secondary in a dual data entry relationship,
     * then nothing will be done.
     * 
     * @param identifier The identifier of the record.
     * @throws DAOException If ne record exists for the specified identifier.
     */
    public void synchronizeDocumentStatusesWithPrimary(String identifier) throws DAOException;
    
    /**
     * the metadata (study entry date, schedule start date, etc.) for 
     * a record, identified by its database-generated unique identifier.
     * 
     * @param recordId The id of the record.
     * @param data The new metadata to apply to the record.
     * @param reason The reason why the metadata is being changed.
     * @param userName The name of the user who is changing the metadata.
     * @throws DAOException if no record exists for the given id.
     */
    public void updateRecordMetadata(Long recordId, org.psygrid.data.model.dto.RecordDataDTO recordData, String reason, String userName) throws DAOException;
    
    /**
     * Get the consent and status (record and document instance) details
     * of record int he given groups of the given project where consent
     * or status has been updated since the given reference date.
     * 
     * @param project  The project code.
     * @param groups The list of group codes
     * @param lastModifiedDate The reference date
     * @return Consent and status details.
     */
    public ConsentStatusResult getConsentAndStatusInfoForGroups(final String project, final String[] groups, final Date lastModifiedDate);
    
    public org.psygrid.data.model.dto.RecordDTO getRecordSingleDocumentForOccurrence(final String identifier, final Long docOccId) throws DAOException;
    
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
     * @return Search results object.
     * @throws DAOException
     */
    public SearchRecordChangeHistoryResult searchRecordChangeHistory(final String project, final Date start, final Date end, final String user, final String identifier, final int startIndex) throws DAOException;

    /**
     * Search the change history for items related to Document Instances that
     * have the given parent id, that are part of the given record.
     * <p>
     * The parent id should be the id of a change history item related to a
     * record, so this method returns the change history at the document instance
     * level for all changes saved with the record that time.
     * 
     * @param identifier 
     * @param parentId The database id of the parent change history item.
     * @return Array of matching document instance change history items.
     * @throws DAOException
     */
    public DocInstChangeHistoryResult[] searchDocInstChangeHistory(final String identifier, final Long parentId) throws DAOException;
    
    /**
     * Get all provenance items relating to response-level changes that
     * were created as part of the specified document-level change history
     * item, for the specified record.
     * 
     * @param identifier The identifier fo the record.
     * @param changeId The id of the change history item
     * @return Array of matching provenance items.
     * @throws DAOException
     */
    public ProvenanceForChangeResult[] getProvenanceForChange(final String identifier, final Long changeId) throws DAOException;

    /**
     * Export data from the database into an intermediate XML format. The
     * XML is written directly to an output stream.
     * <p>
     * The data exported is defined by the study code, the list of group codes 
     * and the list of document occurrences.
     * 
     * @param request The ExportRequest object
     * @param group The group that the ExportRequest is to be applied to
     * @param actionMap The mapping between security tag values to the export action that must be taken
     * @param context This is used to facilitate transforms. 
     * @param applyExportSecurity Specifies whether export security will be applied. 
     * @param requestor The user who requested the export.
     * @param out The output stream to write the exported data to.
     * @param meta Metadata about the exported data.
     * @return False if the export request returned no data; True otherwise
     * @throws DAOException
     * @throws TransformerException 
     * @throws NoDatasetException 
     * @throws RemoteException 
     * @throws XMLStreamException
     */
    public void exportToXml(ExportRequest request, String group, List<ExportSecurityActionMap> actionMap, 
    		OutputStream out, org.psygrid.data.export.metadata.DataSetMetaData meta) 
    		throws DAOException, RemoteException, NoDatasetException, TransformerException, XMLStreamException;
    
    public void exportToXml(
			ExportRequest request, List<String> identifiers,
			List<ExportSecurityActionMap> actionMap, OutputStream out, org.psygrid.data.export.metadata.DataSetMetaData meta) 
	throws DAOException, RemoteException, NoDatasetException, TransformerException, XMLStreamException;
    
    /**
     * Find the id of the status for the document instance related to the
     * specified document occurrence and identifier.
     * 
     * @param identifier The record identifier
     * @param docOccId The id of the document occurrence
     * @return the id of the document status
     * @throws DAOException
     */
    public long getStatusIdForDocument(final String identifier, final long docOccId) throws DAOException;

   /**
     * Decide whether a Record may be randomized i.e. a document instance exists
     * for the occurrence that is the randomization trigger, it is not incomplete
     * and randomization has not already been performed.
     * 
     * @param identifier The identifier of the record.
     * @return True if record can be randomized; False if it can't.
     */
    public boolean canRecordBeRandomized(final String identifier);


    /**
     * Get the records that require patching
     * 
     * @param patchedDs the patched dataset
     * @param prepatchedDataSet the dataset before it was patched
     * @param stdCode the standard code
     * @return a list of records that require patching
     * @throws DAOException
     */
    public List<org.psygrid.data.model.dto.RecordDTO> getRecordsToPatch(DataSetDTO patchedDs, DataSetDTO prepatchedDataSet, StandardCodeDTO stdCode, String userName) throws DAOException; 


	public String[] importDocuments(long datasetID,long occurrenceID,String csvData) throws DAOException;	
    
    
    //methods from StandardCodeDAO
    
    /**
     * Save a single standard code object to the data repository.
     * 
     * @param standardCode The standard code to save.
     * @return The unique identifier of the standard code.
     * @throws ObjectOutOfDateException if the standard code
     * object has been concurrently saved by another session.
     */
    public Long saveStandardCode(StandardCodeDTO standardCode)
        throws ObjectOutOfDateException;
    
    /**
     * Retrieve all standard codes from the repository.
     * 
     * @return Array of all standard codes in the repository.
     */
    public StandardCodeDTO[] getStandardCodes();
    
    /**
     * Retrieve a single standard code from the repository.
     * 
     * @param standardCodeId The unique identifier of the standard
     * code to retrieve.
     * @return The standard code.
     * @throws DAOException if no standard code exists for the given
     * unique identifier.
     */
    public StandardCodeDTO getStandardCode(Long standardCodeId)
            throws DAOException;
    
    
    //methods from TransformerDAO
    /**
     * Retrieve a Map of transformer id to transformer web-service 
     * clients for all transformers defined for a given dataset.
     * 
     * @param dsId The unique identifier of the dataset.
     * @return The map of transformer web-service clients.
     * @throws DAOException
     */
    public Map<Long, TransformerClient> getTransformerClients(Long dsId) throws DAOException;    
	
    public List<Long> getTransformerIds(Long entryId) throws DAOException;

    public List<org.psygrid.data.model.dto.TransformerDTO> getTransformers(Long entryId) throws DAOException;

    public List<Long> getOutputTransformerIds(Long entryId) throws DAOException;

    public String createTransformerErrorMessage(String identifier, Long docOccId, 
		Long secOccId, Long respId, Long childRespId, Integer row, String error) throws DAOException;
    

    // Methods used by the groups admin page

    /**
     * Retrieve the group summaries for a list of projects.
     * 
     * @param projectCodes
     * @return a list of group info
     */
	public List<GroupSummary> getGroupSummary(List<String> projectCodes);
    
	/**
	 * Returns true if a project uses randomisation.
	 * 
	 * @param projectCode a existing project code.
	 * @return true if randomised
	 */
	public boolean isProjectRandomized(String projectCode);

    /**
     * Retrieve a group with its sites and their consultants.
     * 
     * @param groupID
     * @return
     */
    public Group getGroup(final Long groupID);

    /**
     * Update an existing group.
     * 
     * @param group - the group
     */
    public void updateGroup(Group group);

    /**
     * Add a new group to a dataset.
     * 
     * @param projectCode - the dataset code
     * @param group - the group
     */
    public void addGroup(String projectCode, Group group);

	/**
	 * Delete a group.
	 * 
	 * @param groupID the group id
	 */
	public void deleteGroup(Long groupID);

}

