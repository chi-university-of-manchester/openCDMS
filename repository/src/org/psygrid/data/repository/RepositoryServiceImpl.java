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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.Deflater;

import javax.servlet.ServletContext;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.common.identifier.IdentifierHelper;
import org.psygrid.data.export.ExportService;
import org.psygrid.data.export.NoSuchExportFault;
import org.psygrid.data.export.UnableToCancelExportFault;
import org.psygrid.data.export.dto.ExportRequest;
import org.psygrid.data.export.hibernate.ExportSecurityActionMap;
import org.psygrid.data.model.dto.DataSetDTO;
import org.psygrid.data.model.dto.DocumentDTO;
import org.psygrid.data.model.dto.GroupDTO;
import org.psygrid.data.model.dto.IdentifierDTO;
import org.psygrid.data.model.dto.PersistentDTO;
import org.psygrid.data.model.dto.RecordDTO;
import org.psygrid.data.model.dto.RecordDataDTO;
import org.psygrid.data.model.dto.TransformerDTO;
import org.psygrid.data.model.dto.extra.ConsentStatusResult;
import org.psygrid.data.model.dto.extra.DocInstChangeHistoryResult;
import org.psygrid.data.model.dto.extra.GroupSummary;
import org.psygrid.data.model.dto.extra.IdentifierData;
import org.psygrid.data.model.dto.extra.ProvenanceForChangeResult;
import org.psygrid.data.model.dto.extra.SearchRecordChangeHistoryResult;
import org.psygrid.data.model.hibernate.BinaryData;
import org.psygrid.data.model.hibernate.Group;
import org.psygrid.data.model.hibernate.ResponseStatus;
import org.psygrid.data.model.hibernate.RetrieveDepth;
import org.psygrid.data.repository.dao.DAOException;
import org.psygrid.data.repository.dao.DuplicateDocumentsException;
import org.psygrid.data.repository.dao.JdbcDAO;
import org.psygrid.data.repository.dao.NoConsentException;
import org.psygrid.data.repository.dao.NoDatasetException;
import org.psygrid.data.repository.dao.ObjectOutOfDateException;
import org.psygrid.data.repository.dao.RepositoryDAO;
import org.psygrid.data.repository.dao.UnknownIdentifierException;
import org.psygrid.data.repository.transformer.InputTransformer;
import org.psygrid.data.repository.transformer.TransformerClient;
import org.psygrid.data.repository.transformer.TransformerException;
import org.psygrid.data.repository.transformer.TransformerFault;
import org.psygrid.data.sampletracking.ConfigInfo;
import org.psygrid.data.sampletracking.ParticipantInfo;
import org.psygrid.data.sampletracking.SampleInfo;
import org.psygrid.data.sampletracking.SampleTrackingService;
import org.psygrid.data.importing.ImportService;
import org.psygrid.data.importing.ImportData;
import org.psygrid.data.importing.ImportStatus;
import org.psygrid.data.utils.email.MailClient;
import org.psygrid.data.utils.esl.EslException;
import org.psygrid.data.utils.security.NotAuthorisedFault;
import org.psygrid.data.utils.service.AbstractServiceImpl;
import org.psygrid.logging.AuditLogger;
import org.psygrid.security.PGSecurityConstants;
import org.psygrid.security.PGSecurityException;
import org.psygrid.security.PGSecurityInvalidSAMLException;
import org.psygrid.security.PGSecuritySAMLVerificationException;
import org.psygrid.security.RBACAction;
import org.psygrid.security.accesscontrol.AEFAction;
import org.psygrid.security.accesscontrol.AEFGroup;
import org.psygrid.security.accesscontrol.AEFProject;
import org.psygrid.security.utils.PropertyUtilities;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;

import com.thoughtworks.xstream.XStream;

/**
 * Implementation of data repository service.
 *
 * @author Rob Harper
 *
 */
public class RepositoryServiceImpl extends AbstractServiceImpl implements RepositoryServiceInternal {

	/**
	 * Name of the component, used for audit logging
	 */
	private static final String COMPONENT_NAME = "Repository";

	private static final String ACTION_SEPARATOR = "_";

	/**
	 * General purpose logger
	 */
	private static Log sLog = LogFactory.getLog(RepositoryServiceImpl.class);

	/**
	 * Audit logger
	 */
	private static AuditLogger logHelper = new AuditLogger(RepositoryServiceImpl.class);


	private JdbcDAO jdbcDao = null;

	/**
	 * Bean that performs transformations on incoming data.
	 */
	private InputTransformer inputTransformer = null;

	/**
	 * Bean to send emails
	 */
	private MailClient mailClient;

	// TODO: the SampleTrackingService and ExportService should be implemented as separate web services.
	private SampleTrackingService sampleTrackingService = null;

	private ExportService exportService = null;


	protected String getComponentName() {
		return COMPONENT_NAME;
	}

	public RepositoryDAO getDao() {
		return repositoryDAO;
	}

	public void setDao(RepositoryDAO dao) {
		this.repositoryDAO = dao;
	}

	public JdbcDAO getJdbcDao() {
		return jdbcDao;
	}

	public void setJdbcDao(JdbcDAO jdbcDao) {
		this.jdbcDao = jdbcDao;
	}

	public ExportService getExportService() {
		return exportService;
	}

	public void setExportService(ExportService exportService) {
		this.exportService = exportService;
	}

	public InputTransformer getInputTransformer() {
		return inputTransformer;
	}

	public void setInputTransformer(InputTransformer inputTransformer) {
		this.inputTransformer = inputTransformer;
	}

	public MailClient getMailClient() {
		return mailClient;
	}

	public void setMailClient(MailClient mailClient) {
		this.mailClient = mailClient;
	}

	public SampleTrackingService getSampleTrackingService() {
		return sampleTrackingService;
	}

	public void setSampleTrackingService(SampleTrackingService sampleTrackingService) {
		this.sampleTrackingService = sampleTrackingService;
	}

	public String getVersion(){

		String version = null;
		try{
			Properties props = PropertyUtilities.getProperties("repository.properties");
			version = props.getProperty("org.psygrid.repository.version");
		}
		catch(Exception ex){
			//can't load the version, so set it to Unknown
			sLog.error(ex.getMessage(),ex);
			version = "Unknown";
		}
		return version;
	}


	public org.psygrid.data.model.dto.DataSetDTO getDataSetComplete(long dataSetId, String saml) {
			String projectCode = repositoryDAO.getProjectCodeForDataset(dataSetId);
			checkPermissionsByProject(saml, "getDataSetComplete",RBACAction.ACTION_DR_GET_DATASET_COMPLETE, projectCode);
			DataSetDTO dataset = repositoryDAO.getDataSet(dataSetId);
			String userName = findUserName(saml);
			docHelper.setSaml(saml);
			dataset.setDocuments(docHelper.getAllowedDocuments(dataset.getDocuments(), projectCode, userName));
			return dataset;
	}


	public byte[] getBinaryData(long dataSetId, long binaryObjectId, String saml) {
			String projectCode = repositoryDAO.getProjectCodeForDataset(dataSetId);
			checkPermissionsByProject(saml,"getBinaryData",RBACAction.ACTION_DR_GET_BINARY_DATA,projectCode);
			BinaryData bd = repositoryDAO.getBinaryData(binaryObjectId);
			return bd.getData();
	}

	public org.psygrid.data.model.dto.DataSetDTO[] getModifiedDataSets(Calendar referenceDate, String saml) {

		final String METHOD_NAME = "getModifiedDataSets";
		String userName = findUserName(saml);
		String callerIdentity = accessControl.getCallersIdentity();
		logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, userName, callerIdentity);

		DataSetDTO[] datasets = repositoryDAO.getModifiedDataSets(referenceDate.getTime());

		for (DataSetDTO dataset: datasets) {
			docHelper.setSaml(saml);
			dataset.setDocuments(docHelper.getAllowedDocuments(dataset.getDocuments(), dataset.getProjectCode(), userName));
		}
		return datasets;
	}


	public long saveRecord(org.psygrid.data.model.dto.RecordDTO record, boolean discardDuplicates, String saml)
	throws RemoteException, ConnectException,
	RepositoryServiceFault,
	RepositoryOutOfDateFault,
	RepositoryNoConsentFault,
	RepositoryInvalidIdentifierFault,
	TransformerFault,
	DuplicateDocumentsFault {

		try{

			String projectCode = record.getIdentifier().getProjectPrefix();
			String groupCode = record.getIdentifier().getGroupPrefix();
			checkPermissionsByGroup(saml, "saveRecord", RBACAction.ACTION_DR_SAVE_RECORD, projectCode, groupCode);

			String userName = findUserName(saml);
			docHelper.setSaml(saml);
			org.psygrid.data.model.hibernate.Record r = record.toHibernate();
			r.setDocInstances(docHelper.getAllowedDocumentInstances(r.getDocInstances(), projectCode, groupCode, userName, false));
			Map<Long, TransformerClient> transformerClients = repositoryDAO.getTransformerClients(r.getDataSetId());
			//if there are no transformer clients defined for the dataset then there
			//is no point in trying to do any transformation
			if ( transformerClients != null && transformerClients.size() > 0 ){
				inputTransformer.transform(r, transformerClients);
			}

			return repositoryDAO.saveRecord(r, discardDuplicates, docHelper, userName);
		}
		catch(TransformerException ex){
			throw new TransformerFault(ex);
		}
		catch(ObjectOutOfDateException ex){
			throw new RepositoryOutOfDateFault(ex);
		}
		catch(NoConsentException ex){
			throw new RepositoryNoConsentFault(ex);
		}
		catch(DuplicateDocumentsException ex){
			throw new DuplicateDocumentsFault(ex, ex.getTitle(), ex.getDiscards(), ex.getDuplicateList());
		}
		catch(UnknownIdentifierException ex){
			throw new RepositoryInvalidIdentifierFault(ex);
		}
	}

	public long saveRecordAsUser(org.psygrid.data.model.dto.RecordDTO record, String user, String saml)
	throws RepositoryServiceFault,
	RepositoryOutOfDateFault,
	RepositoryNoConsentFault,
	RepositoryInvalidIdentifierFault,
	TransformerFault, RemoteException {

		try{

			String projectCode = record.getIdentifier().getProjectPrefix();
			String groupCode = record.getIdentifier().getGroupPrefix();

			checkPermissionsByGroup(saml, "saveRecordAsUser", RBACAction.ACTION_DR_SAVE_RECORD_AS_USER, projectCode, groupCode);

			String userName = findUserName(saml);
			docHelper.setSaml(saml);
			org.psygrid.data.model.hibernate.Record r = record.toHibernate();
			r.setDocInstances(docHelper.getAllowedDocumentInstances(r.getDocInstances(), projectCode, groupCode, userName, false));

			Map<Long, TransformerClient> transformerClients = repositoryDAO.getTransformerClients(r.getDataSetId());
			//if there are no transformer clients defined for the dataset then there
			//is no point in trying to do any transformation
			if ( transformerClients.size() > 0 ){
				inputTransformer.transform(r, transformerClients);
			}
			return repositoryDAO.saveRecord(r, true, docHelper, user);
		}
		catch(TransformerException ex){
			throw new TransformerFault(ex);
		}
		catch(ObjectOutOfDateException ex){
			throw new RepositoryOutOfDateFault(ex);
		}
		catch(NoConsentException ex){
			throw new RepositoryNoConsentFault(ex);
		}
		catch(UnknownIdentifierException ex){
			throw new RepositoryInvalidIdentifierFault(ex);
		}
	}

	public org.psygrid.data.model.dto.RecordDTO getRecordComplete(long recordId, String saml) {

		String[] codes = repositoryDAO.getProjectAndGroupForInstance(recordId);

		checkPermissionsByGroup(saml, "getRecordComplete", RBACAction.ACTION_DR_GET_RECORD_COMPLETE, codes[0], codes[1]);

		RecordDTO record = repositoryDAO.getRecord(recordId, RetrieveDepth.RS_NO_BINARY);
		String userName = findUserName(saml);
		docHelper.setSaml(saml);

		org.psygrid.data.model.hibernate.Record r = record.toHibernate();
		r.setDocInstances(docHelper.getAllowedDocumentInstances(r.getDocInstances(), codes[0], codes[1], userName));
		return r.toDTO();
	}

	public org.psygrid.data.model.dto.RecordDTO getRecordComplete(String identifier, String saml) {

		checkPermissionsByIdentifier(saml, "getRecordComplete", RBACAction.ACTION_DR_GET_RECORD_COMPLETE, identifier);

		String groupCode = IdentifierHelper.getGroupCodeFromIdentifier(identifier);
		String projectCode = IdentifierHelper.getProjectCodeFromIdentifier(identifier);
		String userName = findUserName(saml);
		RecordDTO record = repositoryDAO.getRecord(identifier, RetrieveDepth.RS_NO_BINARY);
		docHelper.setSaml(saml);
		org.psygrid.data.model.hibernate.Record r = record.toHibernate();
		r.setDocInstances(docHelper.getAllowedDocumentInstances(r.getDocInstances(), projectCode, groupCode, userName));
		return r.toDTO();
	}

	public org.psygrid.data.model.dto.RecordDTO getRecordCompleteByExternalID(long datasetID, String externalID, String saml) {

		// Get the record before checking access control so we have an identifier to work with
		RecordDTO record = repositoryDAO.getRecordByExternalID(datasetID,externalID, RetrieveDepth.RS_NO_BINARY);

		if(record!=null){

			String identifier = record.getIdentifier().getIdentifier();
			checkPermissionsByIdentifier(saml, "getRecordCompleteByExternalID", RBACAction.ACTION_DR_GET_RECORD_COMPLETE, identifier);

			String userName = findUserName(saml);
			String groupCode = IdentifierHelper.getGroupCodeFromIdentifier(identifier);
			String projectCode = IdentifierHelper.getProjectCodeFromIdentifier(identifier);
			docHelper.setSaml(saml);
			org.psygrid.data.model.hibernate.Record r = record.toHibernate();
			r.setDocInstances(docHelper.getAllowedDocumentInstances(r.getDocInstances(), projectCode, groupCode, userName));
			record = r.toDTO();
		}
		return record;
	}


	public org.psygrid.data.model.dto.RecordDTO getRecordSummary(long recordId, String saml) {

		String[] codes = repositoryDAO.getProjectAndGroupForInstance(recordId);
		checkPermissionsByGroup(saml, "getRecordSummary", RBACAction.ACTION_DR_GET_RECORD_SUMMARY, codes[0], codes[1]);

		RecordDTO record = repositoryDAO.getRecord(recordId, RetrieveDepth.RS_SUMMARY);
		String userName = findUserName(saml);
		docHelper.setSaml(saml);
		org.psygrid.data.model.hibernate.Record r = record.toHibernate();
		r.setDocInstances(docHelper.getAllowedDocumentInstances(r.getDocInstances(), codes[0], codes[1], userName));
		return r.toDTO();
	}

	public org.psygrid.data.model.dto.RecordDTO getRecordSummary(String identifier, String saml) {

		checkPermissionsByIdentifier(saml, "getRecordSummary", RBACAction.ACTION_DR_GET_RECORD_SUMMARY, identifier);

		String userName = findUserName(saml);
		String groupCode = IdentifierHelper.getGroupCodeFromIdentifier(identifier);
		String projectCode = IdentifierHelper.getProjectCodeFromIdentifier(identifier);
		RecordDTO record = repositoryDAO.getRecord(identifier, RetrieveDepth.RS_SUMMARY);
		docHelper.setSaml(saml);
		org.psygrid.data.model.hibernate.Record r = record.toHibernate();
		r.setDocInstances(docHelper.getAllowedDocumentInstances(r.getDocInstances(), projectCode, groupCode, userName));
		return r.toDTO();
	}

	public org.psygrid.data.model.dto.RecordDTO[] getRecords(long dataSetId, String saml) {

		String projectCode = repositoryDAO.getProjectCodeForDataset(dataSetId);
		checkPermissionsByProject(saml, "getRecords",RBACAction.ACTION_DR_GET_RECORD_COMPLETE, projectCode);

		RecordDTO[] records = repositoryDAO.getRecords(dataSetId);

		String userName = findUserName(saml);

		for (RecordDTO record: records) {
			String groupCode = record.getIdentifier().getGroupPrefix();
			docHelper.setSaml(saml);
			org.psygrid.data.model.hibernate.Record r = record.toHibernate();
			r.setDocInstances(docHelper.getAllowedDocumentInstances(r.getDocInstances(), projectCode, groupCode, userName));
			record = r.toDTO();
		}
		return records;
	}

	public org.psygrid.data.model.dto.RecordDTO[] getRecordsByStatus(long dataSetId, long statusId, String saml) {

		String projectCode = repositoryDAO.getProjectCodeForDataset(dataSetId);
		checkPermissionsByProject(saml, "getRecordsByStatus",RBACAction.ACTION_DR_GET_RECORDS_BY_STATUS, projectCode);

		RecordDTO[] records = repositoryDAO.getRecordsByStatus(dataSetId, statusId);

		String userName = findUserName(saml);
		for (RecordDTO record: records) {
			String groupCode = record.getIdentifier().getGroupPrefix();
			docHelper.setSaml(saml);
			org.psygrid.data.model.hibernate.Record r = record.toHibernate();
			r.setDocInstances(docHelper.getAllowedDocumentInstances(r.getDocInstances(), projectCode, groupCode, userName));
			record = r.toDTO();
		}
		return records;
	}

	public org.psygrid.data.model.dto.IdentifierDTO[] generateIdentifiers(long dataSetId, String groupCode, int count, String saml) {
		String projectCode = repositoryDAO.getProjectCodeForDataset(dataSetId);
		checkPermissionsByGroup(saml, "generateIdentifiers", RBACAction.ACTION_DR_GENERATE_IDENTIFIERS, projectCode, groupCode);
		Integer maxSuffix = jdbcDao.reserveIdentifierSpace(dataSetId, groupCode, count);
		String userName = findUserName(saml);
		return repositoryDAO.generateIdentifiers(projectCode, groupCode, count, maxSuffix, userName);
	}

	public org.psygrid.data.model.dto.StandardCodeDTO[] getStandardCodes(String saml) {
		String userName = findUserName(saml);
		String callerIdentity = accessControl.getCallersIdentity();
		logHelper.logMethodCall(COMPONENT_NAME, "getStandardCodes", userName, callerIdentity);
		return repositoryDAO.getStandardCodes();
	}

	public long saveDataSet(DataSetDTO ds, String saml) throws RepositoryOutOfDateFault {
		try {
			String projectCode = ds.getProjectCode();
			checkPermissionsByProject(saml, "saveDataSet",RBACAction.ACTION_DR_SAVE_DATASET, projectCode);
			return repositoryDAO.saveDataSet(ds);
		} catch (ObjectOutOfDateException ex) {
			throw new RepositoryOutOfDateFault(ex);
		}
	}

	public void publishDataSet(long dataSetId, String saml) {
			String projectCode = repositoryDAO.getProjectCodeForDataset(dataSetId);
			checkPermissionsByProject(saml,"publishDataSet",RBACAction.ACTION_DR_PUBLISH_DATASET,projectCode);
			repositoryDAO.publishDataSet(dataSetId);
	}

	public String[] withdrawConsentDryRun(long recordId, long consentFormId, String reason, String saml) {
			//NB. Reusing policy for Withdraw Consent
			String[] codes = repositoryDAO.getProjectAndGroupForInstance(recordId);
			checkPermissionsByGroup(saml, "withdrawConsent", RBACAction.ACTION_DR_WITHDRAW_CONSENT, codes[0], codes[1]);
			String userName = findUserName(saml);
			return repositoryDAO.withdrawConsentDryRun(recordId, consentFormId, reason, userName, saml);
	}

	public void withdrawConsent(long recordId, long consentFormId, String reason, String saml) throws RepositoryServiceFault {
		try{
			String[] codes = repositoryDAO.getProjectAndGroupForInstance(recordId);
			checkPermissionsByGroup(saml, "withdrawConsent", RBACAction.ACTION_DR_WITHDRAW_CONSENT, codes[0], codes[1]);
			String userName = findUserName(saml);
			repositoryDAO.withdrawConsent(recordId, consentFormId, reason, userName, saml);
		}
		catch(EslException ex){
			throw new RepositoryServiceFault(ex);
		}
	}

	public void changeStatus(long statusedInstanceId, long newStatusId, String saml) throws RepositoryServiceFault{
		final String METHOD_NAME = "changeStatus";

		try{
			String userName = findUserName(saml);
			String callerIdentity = accessControl.getCallersIdentity();
			logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, userName, callerIdentity);

			String projectCode = repositoryDAO.getProjectCodeForInstance(statusedInstanceId);
			String groupCode = repositoryDAO.getGroupCodeForInstance(statusedInstanceId);

			//build the action for the authorisation call
			//actions is of form changeStatus_<R|D>_<currentActionName>_<newActionName>
			String action = null;
			if ( repositoryDAO.isObjectARecord(statusedInstanceId) ){
				action = RBACAction.ACTION_DR_CHANGE_RECORD_STATUS.toString();
			}
			else if ( repositoryDAO.isObjectADocument(statusedInstanceId) ){
				String fromStatus = null;
				String toStatus = null;
				try{
					fromStatus = repositoryDAO.getShortNameOfCurrentStatus(statusedInstanceId);
					toStatus = repositoryDAO.getStatusShortName(newStatusId);
				}
				catch(DAOException ex){
					throw new RepositoryServiceFault(ex);
				}

				action = METHOD_NAME+
				ACTION_SEPARATOR+"D"+
				ACTION_SEPARATOR+fromStatus+
				ACTION_SEPARATOR+toStatus;
			}
			else{
				//Statused object is not a record or a document instance
				throw new NotAuthorisedFault("Cannot create valid action");
			}

			if ( !accessControl.authoriseUser(saml,
					new AEFGroup(null, groupCode, null),
					new AEFAction(action, null),
					new AEFProject(null, projectCode,false) ) ){

				logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, userName, callerIdentity);
				throw new NotAuthorisedFault("User '"+userName+"' is not authorised to perform the action '"+action+"' for project '"+projectCode+"'");
			}

			//see if the user is allowed to ignore the defined status transitions
			//TODO note that we are re-using the ACTION_DR_GET_RECORDS_BY_GROUPS_AND_DOC_STATUS_PENDING action here, which
			//is probably not a great idea. We use it under the assumption that users who have the privileges to reject
			//documents (i.e. CPMs) also have the privileges to change a record's status to anything, rather than one of
			//the permitted status transitions defined for the current status of the object.
			boolean ignorePermittedTransitions =
				accessControl.authoriseUser(
						saml,
						new AEFGroup(null, groupCode, null),
						RBACAction.getRecordsByGroupsAndDocStatus_Pending.toAEFAction(),
						new AEFProject(null, projectCode,false) );

			repositoryDAO.changeStatus(statusedInstanceId, newStatusId, userName, ignorePermittedTransitions);
		}
		catch(PGSecurityInvalidSAMLException ex){
			throw new NotAuthorisedFault("The supplied SAML assertion has expired", ex);
		}
		catch(PGSecuritySAMLVerificationException ex){
			throw new NotAuthorisedFault("The supplied SAML assertion does not come from a trusted issuer", ex);
		}
		catch(PGSecurityException ex){
			throw new NotAuthorisedFault("An error occurred during authorisation", ex);
		}
	}

	public void changeRecordStatus(String identifier, long newStatusId, final boolean ignorePermittedTransitions, String saml) {
			checkPermissionsByIdentifier(saml, "changeRecordStatus", RBACAction.ACTION_DR_CHANGE_RECORD_STATUS,identifier);
			String userName = findUserName(saml);
			repositoryDAO.changeRecordStatus(identifier, newStatusId, userName, ignorePermittedTransitions, saml);
	}

	public DataSetDTO getDataSetSummary(String projectCode, Calendar referenceDate, String saml) throws RepositoryNoSuchDatasetFault {

		try{
			checkPermissionsByProject(saml,"getDataSetSummary",RBACAction.ACTION_DR_GET_DATASET_SUMMARY,projectCode);

			DataSetDTO ds = null;
			if ( repositoryDAO.getDataSetModified(projectCode, referenceDate.getTime()) ){
				ds = repositoryDAO.getSummaryForProjectCode(projectCode, RetrieveDepth.DS_SUMMARY);
			}

			if (ds!= null && ds.getDocuments() != null && ds.getDocuments().length > 0) {
				String userName = findUserName(saml);
				docHelper.setSaml(saml);
				DocumentDTO[] documents = docHelper.getAllowedDocuments(ds.getDocuments(), projectCode, userName);
				ds.setDocuments(documents);
			}
			return ds;
		}
		catch(NoDatasetException ex){
			throw new RepositoryNoSuchDatasetFault(ex);
		}
	}

	public DataSetDTO getDataSetSummaryWithDocs(String projectCode, String saml) throws RepositoryNoSuchDatasetFault {

		try{
			//TODO policy
			checkPermissionsByProject(saml,"getDataSetSummaryWithDocs",RBACAction.ACTION_DR_GET_DATASET_SUMMARY,projectCode);
			DataSetDTO dataset = repositoryDAO.getSummaryForProjectCode(projectCode, RetrieveDepth.DS_WITH_DOCS);
			docHelper.setSaml(saml);
			String userName = findUserName(saml);
			dataset.setDocuments(docHelper.getAllowedDocuments(dataset.getDocuments(), projectCode, userName));
			return dataset;
		}
		catch(NoDatasetException ex){
			throw new RepositoryNoSuchDatasetFault(ex);
		}
	}

	public void markResponseAsInvalid(long responseId, String annotation, String saml) {
		String projectCode = repositoryDAO.getProjectCodeForInstance(responseId);
		checkPermissionsByProject(saml,"markResponseAsInvalid",RBACAction.ACTION_DR_MARK_RESPONSE_AS_INVALID,projectCode);
		repositoryDAO.updateResponseStatusAnnotation(responseId, ResponseStatus.FLAGGED_INVALID, annotation);
	}

	public void markResponseAsValid(long responseId, String saml) {
		String projectCode = repositoryDAO.getProjectCodeForInstance(responseId);
		checkPermissionsByProject(saml,"markResponseAsValid",RBACAction.ACTION_DR_MARK_RESPONSE_AS_VALID,projectCode);
		repositoryDAO.updateResponseStatusAnnotation(responseId, ResponseStatus.NORMAL, null);
	}

	public RecordDTO getRecordsDocumentsByStatus(String identifier, String status, String saml) {

		final String METHOD_NAME = "getRecordsDocumentsByStatus";

		String userName = findUserName(saml);

		String projectCode = IdentifierHelper.getProjectCodeFromIdentifier(identifier);
		String groupCode = IdentifierHelper.getGroupCodeFromIdentifier(identifier);

		String action = METHOD_NAME + ACTION_SEPARATOR + status;
		checkPermissionsByGroup(saml, "getRecordsDocumentsByStatus", RBACAction.valueOf(action), projectCode, groupCode);
		RecordDTO record = repositoryDAO.getRecordsDocumentsByStatus(identifier, status);
		docHelper.setSaml(saml);
		org.psygrid.data.model.hibernate.Record r = record.toHibernate();
		r.setDocInstances(docHelper.getAllowedDocumentInstances(r.getDocInstances(), projectCode, groupCode, userName));
		return r.toDTO();
	}

	public String[] getRecordsByGroups(String project, String[] groups, String saml) {
			if ( groups.length < 1 ){
				throw new IllegalArgumentException("At least one group is required.");
			}
			checkPermissionsByGroups(saml, "getRecordsByGroups", RBACAction.ACTION_DR_GET_RECORDS_BY_GROUPS, project, groups);
			return repositoryDAO.getRecordsByGroups(project, groups);
	}

	public RecordDTO[] getRecordsWithConsentByGroups(String project, String[] groups, Calendar referenceDate, String saml) throws RepositoryServiceFault {

		final String METHOD_NAME = "getRecordsWithConsentByGroups";

		try{

			if ( groups.length < 1 ){
				throw new RepositoryServiceFault("At least one group is required.");
			}

			String userName = findUserName(saml);
			String callerIdentity = accessControl.getCallersIdentity();
			logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, userName, callerIdentity);

			List<String> authorisedGroups = new ArrayList<String>();
			for ( int i=0; i<groups.length; i++ ){
				if ( !accessControl.authoriseUser(saml, new AEFGroup(null, groups[i], null), RBACAction.ACTION_DR_GET_RECORDS_WITH_CONSENT_BY_GROUPS.toAEFAction(), new AEFProject(null, project, false) ) ){
					logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, userName, callerIdentity);
				}
				else{
					authorisedGroups.add(groups[i]);
				}
			}
			if ( 0 == authorisedGroups.size() ){
				throw new NotAuthorisedFault("User '"+userName+"' is not authorised to perform the action '"+RBACAction.ACTION_DR_GET_RECORDS_WITH_CONSENT_BY_GROUPS.toString()+"' for project '"+project+"'");
			}

			RecordDTO[] records = repositoryDAO.getRecordsByGroups(project,
					authorisedGroups.toArray(new String[authorisedGroups.size()]),
					referenceDate.getTime(),
					RetrieveDepth.RS_SUMMARY);


			for (RecordDTO record: records) {
				String groupCode = record.getIdentifier().getGroupPrefix();
				docHelper.setSaml(saml);
				org.psygrid.data.model.hibernate.Record r = record.toHibernate();
				r.setDocInstances(docHelper.getAllowedDocumentInstances(r.getDocInstances(), project, groupCode, userName));
				record = r.toDTO();
			}
			return records;
		}
		catch(PGSecurityInvalidSAMLException ex){
			throw new NotAuthorisedFault("The supplied SAML assertion has expired", ex);
		}
		catch(PGSecuritySAMLVerificationException ex){
			throw new NotAuthorisedFault("The supplied SAML assertion does not come from a trusted issuer", ex);
		}
		catch(PGSecurityException ex){
			throw new NotAuthorisedFault("An error occurred during authorisation", ex);
		}
	}

	public RecordDTO[] getRecordsWithConsentByGroups(String project, String[] groups, Calendar referenceDate, int batchSize, int offset, String saml) {

		if ( groups.length < 1 ){
			throw new IllegalArgumentException("At least one group is required.");
		}

		checkPermissionsByGroups(saml, "getRecordsWithConsentByGroups", RBACAction.ACTION_DR_GET_RECORDS_WITH_CONSENT_BY_GROUPS, project, groups);

		RecordDTO[] records = repositoryDAO.getRecordsByGroups(project,
				groups,
				referenceDate.getTime(),
				batchSize,
				offset,
				RetrieveDepth.RS_SUMMARY);

		for (RecordDTO record: records) {
			String groupCode = record.getIdentifier().getGroupPrefix();
			docHelper.setSaml(saml);
			org.psygrid.data.model.hibernate.Record r = record.toHibernate();
			String userName = findUserName(saml);
			r.setDocInstances(docHelper.getAllowedDocumentInstances(r.getDocInstances(), project, groupCode, userName));
			record = r.toDTO();
		}
		return records;
	}

	public String[] getRecordsByGroupsAndDocStatus(String project, String[] groups, String status, String saml) {

		final String METHOD_NAME = "getRecordsByGroupsAndDocStatus";

		try{
			if ( groups.length < 1 ){
				throw new IllegalArgumentException("At least one group is required.");
			}

			String userName = findUserName(saml);
			String callerIdentity = accessControl.getCallersIdentity();
			logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, userName, callerIdentity);

			String action = METHOD_NAME + ACTION_SEPARATOR + status;
			List<String> authorisedGroups = new ArrayList<String>();
			for ( int i=0; i<groups.length; i++ ){
				if ( !accessControl.authoriseUser(saml, new AEFGroup(null, groups[i], null), new AEFAction(action, null), new AEFProject(null, project, false) ) ){
					logHelper.logAccessDenied(COMPONENT_NAME, action, userName, callerIdentity);
				}
				else{
					authorisedGroups.add(groups[i]);
				}
			}
			if ( 0 == authorisedGroups.size() ){
				throw new NotAuthorisedFault("User '"+userName+"' is not authorised to perform the action '"+action+"' for project '"+project+"'");
			}

			String[] records = repositoryDAO.getRecordsByGroupsAndDocStatus(project, authorisedGroups.toArray(new String[authorisedGroups.size()]), status);
			return records;
		}
		catch(PGSecurityInvalidSAMLException ex){
			throw new NotAuthorisedFault("The supplied SAML assertion has expired", ex);
		}
		catch(PGSecuritySAMLVerificationException ex){
			throw new NotAuthorisedFault("The supplied SAML assertion does not come from a trusted issuer", ex);
		}
		catch(PGSecurityException ex){
			throw new NotAuthorisedFault("An error occurred during authorisation", ex);
		}
	}

	public void changeDocumentStatus(String identifier, long docOccId, long newStatusId, String saml) {

		final String METHOD_NAME = "changeDocumentStatus";

		String projectCode = IdentifierHelper.getProjectCodeFromIdentifier(identifier);
		String groupCode = IdentifierHelper.getGroupCodeFromIdentifier(identifier);

		//build the action for the authorisation call
		//action is of form changeDocumentStatus_<currentStatusName>_<newStatusName>
		String fromStatus = repositoryDAO.getShortNameOfCurrentStatus(identifier, docOccId);
		String toStatus = repositoryDAO.getStatusShortName(newStatusId);

		//don't do anything if current status and new status are the same
		if ( !toStatus.equals(fromStatus) ){

			String action = METHOD_NAME+ACTION_SEPARATOR+fromStatus+ACTION_SEPARATOR+toStatus;
			checkPermissionsByGroup(saml, "changeDocumentStatus", RBACAction.valueOf(action), projectCode, groupCode);

			String userName = findUserName(saml);
			repositoryDAO.changeDocumentStatus(identifier, docOccId, newStatusId, userName);
		}
	}

	public long[] changeDocumentStatus(String identifier, long[] docOccIds, String newStatus, String saml) {

		final String METHOD_NAME = "changeDocumentStatus";

		String userName = findUserName(saml);
		String callerIdentity = accessControl.getCallersIdentity();
		logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, userName, callerIdentity);

		String projectCode = IdentifierHelper.getProjectCodeFromIdentifier(identifier);
		String groupCode = IdentifierHelper.getGroupCodeFromIdentifier(identifier);

		//action is of form changeDocumentStatus_<currentStatusName>_<newStatusName>
		List<Long> ids = new ArrayList<Long>();
		for ( int i=0; i<docOccIds.length; i++ ){
			String fromStatus = repositoryDAO.getShortNameOfCurrentStatus(identifier, docOccIds[i]);
			//skip doc occs where current and new status are the same
			if ( !newStatus.equals(fromStatus) ){
				ids.add(new Long(docOccIds[i]));
				String action = METHOD_NAME+ACTION_SEPARATOR+fromStatus+ACTION_SEPARATOR+newStatus;
				checkPermissionsByGroup(saml, "changeDocumentStatus", RBACAction.valueOf(action), projectCode, groupCode);
			}
		}

		return repositoryDAO.changeDocumentStatus(identifier, ids, newStatus, userName);
	}

	public void addConsent(long recordId, long consentFormId, String location, String saml) throws RepositoryServiceFault {

		try{
			String[] codes = repositoryDAO.getProjectAndGroupForInstance(recordId);

			checkPermissionsByGroup(saml, "addConsent", RBACAction.ACTION_DR_ADD_CONSENT, codes[0], codes[1]);

			String userName = findUserName(saml);
			repositoryDAO.addConsent(recordId, consentFormId, location, userName, saml);
		}
		catch(EslException ex){
			throw new RepositoryServiceFault(ex);
		}
	}

	public IdentifierDTO addIdentifier(long dataSetId, org.psygrid.data.model.dto.IdentifierDTO identifier, String saml) {
			String project = identifier.getProjectPrefix();
			checkPermissionsByProject(saml,"addIdentifier",RBACAction.ACTION_DR_ADD_IDENTIFIER,project);
			jdbcDao.reserveIdentifier(dataSetId, identifier.getGroupPrefix(), identifier.getSuffix() );
			return repositoryDAO.addIdentifier(identifier);
	}

	public long patchDataSet(org.psygrid.data.model.dto.DataSetDTO ds, String saml) throws RepositoryServiceFault,
			RepositoryOutOfDateFault,RepositoryNoConsentFault, DuplicateDocumentsFault, RepositoryInvalidIdentifierFault, TransformerFault, ConnectException, RemoteException {

		checkPermissionsByProject(saml,"patchDataSet",RBACAction.ACTION_DR_PATCH_DATASET,ds.getProjectCode());
		//The users must also have this privilege in the SYSTEM dataset
		checkPermissionsByProject(saml,"patchDataSet",RBACAction.ACTION_DR_PATCH_DATASET,PGSecurityConstants.SYSTEM_PROJECT_ID);

		//get the dataset before patching
		DataSetDTO prePatchedDs = repositoryDAO.getDataSet(ds.getId());

		//patch the dataset and return the patched dataset with incremented autoVersionNos
		DataSetDTO patchedDs = repositoryDAO.patchDataSet(ds);

		//get the standard codes of the dataset
		org.psygrid.data.model.dto.StandardCodeDTO[] stdCodes = getStandardCodes(saml);

		//get the records to patch; fill appropriate entries with std code or rejected responses
		if (stdCodes == null || stdCodes.length != 4) {
			throw new RuntimeException("Unable to retrieve standard codes");
		}

		/** Don't save records for now
		List<Record> recordsToPatch = repositoryDAO.getRecordsToPatch(patchedDs, prePatchedDs, stdCodes[3], findUserName(saml));

		//save the changed records
		for (Record r: recordsToPatch) {
			saveRecord(r, true, saml);
		}
		*/

		return patchedDs.getId();
	}

	public void removeDataSet(long dataSetId, String projectCode, String saml) {
		checkPermissionsByProject(saml,"removeDataSet",RBACAction.ACTION_DR_REMOVE_DATASET,projectCode);
		repositoryDAO.removeDataSet(dataSetId);
	}

	public void removePublishedDataSet(long dataSetId, String projectCode, String saml) {

		checkPermissionsByProject(saml,"removePublishedDataSet",RBACAction.ACTION_DR_REMOVE_PUBLISHED_DATASET,projectCode);

		//remove circular reference
		sLog.info("Remove circular references in records to be deleted");
		repositoryDAO.removeRecordCircularRef(dataSetId, projectCode);

		//remove the records
		sLog.info("Remove records");
		boolean moreRecords = true;
		int counter = 0;
		while ( moreRecords ){
			moreRecords = repositoryDAO.removeRecordForDataSet(dataSetId, projectCode);
			counter++;
			sLog.info("Deleted record "+counter);
		}

		//remove the dataset
		sLog.info("Remove dataset");
		repositoryDAO.removePublishedDataSet(dataSetId, projectCode);
		// TODO: Dependencies like this should be done using an event model.
    	//reportingDAO.removeReports(dataSetId, projectCode);
	}



	public RecordDTO getRecordSingleDocument(long recordId, long docInstId, String saml) {

		String[] codes = repositoryDAO.getProjectAndGroupForInstance(recordId);

		checkPermissionsByGroup(saml, "getRecordSingleDocument", RBACAction.ACTION_DR_GET_RECORD_SINGLE_DOCUMENT, codes[0], codes[1]);

		RecordDTO record = repositoryDAO.getRecordSingleDocument(recordId, docInstId);

		if(record != null){
			docHelper.setSaml(saml);
			String userName = findUserName(saml);
			org.psygrid.data.model.hibernate.Record r = record.toHibernate();
			r.setDocInstances(docHelper.getAllowedDocumentInstances(r.getDocInstances(), codes[0], codes[1], userName));
			record = r.toDTO();
		}

		return record;
	}

	public void emailSupport(String subject, String body, String saml) throws RepositoryServiceFault {

		final String METHOD_NAME = "emailSupport";

		try{
			// TODO: we should probably have some permissions on this.
			String userName = findUserName(saml);
			String callerIdentity = accessControl.getCallersIdentity();
			logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, userName, callerIdentity);
			mailClient.sendSupportEmail(subject, body, userName);
		}
		catch(MailException ex){
			throw new RepositoryServiceFault(ex);
		}
	}

	// TODO:
	// The export service should really stand on it's own - and NOT be wrapped by the repository service
	// ====================== Start Export Service =========================================
	public void requestExport(ExportRequest exportRequest, String saml) throws RemoteException, RepositoryServiceFault, NotAuthorisedFault, RepositoryNoSuchDatasetFault {
		exportService.requestExport(exportRequest, saml);
	}

	public void requestImmediateExport(ExportRequest exportRequest, String saml) throws RemoteException,
			RepositoryServiceFault, NotAuthorisedFault,
			RepositoryNoSuchDatasetFault {
		exportService.requestImmediateExport(exportRequest, saml);
	}

	public org.psygrid.data.export.dto.ExportRequest[] getMyExportRequests(String[] projects, String saml) throws RemoteException, RepositoryServiceFault, NotAuthorisedFault {
		return exportService.getMyExportRequests(projects, saml);
	}

	public byte[] downloadExport(long exportRequestId, String saml) throws RemoteException, RepositoryServiceFault, NotAuthorisedFault, NoSuchExportFault {
		return exportService.downloadExport(exportRequestId, saml);
	}

	public byte[] downloadExportHash(long exportRequestId, String format, String saml) throws RemoteException, RepositoryServiceFault, NoSuchExportFault {
		return exportService.downloadExportHash(exportRequestId, format, saml);
	}

	public String[] getIdentifiers(final long dataSetId, String saml)
	throws RemoteException, RepositoryServiceFault, NotAuthorisedFault {
			String projectCode = repositoryDAO.getProjectCodeForDataset(dataSetId);
			checkPermissionsByProject(saml,"getIdentifiers",RBACAction.ACTION_DR_GET_IDENTIFIERS,projectCode);
			return repositoryDAO.getIdentifiers(dataSetId);
	}

    public String[] getIdentifiersByResponse(String projectCode, String documentName, String entryName, String entryValue, String saml)
    		throws RemoteException, RepositoryServiceFault, NotAuthorisedFault {
		checkPermissionsByProject(saml,"getIdentifiers",RBACAction.ACTION_DR_GET_IDENTIFIERS,projectCode);
		return repositoryDAO.getIdentifiersByResponse(projectCode, documentName, entryName, entryValue);
    }


	public void cancelExport(long exportRequestId, String saml)
	throws RemoteException, RepositoryServiceFault, UnableToCancelExportFault {
		exportService.cancelExport(exportRequestId, saml);
	}

	// ====================== End Export Service =========================================

	public IdentifierData[] getIdentifiersExtended(final long dataSetId, String saml) {
			String projectCode = repositoryDAO.getProjectCodeForDataset(dataSetId);
			checkPermissionsByProject(saml,"getIdentifiersExtended",RBACAction.ACTION_DR_GET_IDENTIFIERS,projectCode);
			return repositoryDAO.getIdentifiersExtended(dataSetId);
	}

	public Object transform(long dsId, TransformerDTO transformer, String[] responses, String saml) throws TransformerFault {
		try{
			String project = repositoryDAO.getProjectCodeForDataset(dsId);
			checkPermissionsByProject(saml,"transform",RBACAction.ACTION_DR_TRANSFORM,project);
			return inputTransformer.externalTransform(dsId, transformer, responses, saml);
		}
		catch(TransformerException ex){
			throw new TransformerFault(ex.getMessage(), ex);
		}
	}

	public void deleteRecord(String identifier, String saml) {
			checkPermissionsByIdentifier(saml, "deleteRecord", RBACAction.ACTION_DR_DELETE_RECORD,identifier);
			repositoryDAO.deleteRecord(identifier);
	}

	public String[] getLinkableRecords(String projectCode, String[] groups, String saml) throws RepositoryServiceFault {

		final String METHOD_NAME = RBACAction.ACTION_DR_GET_LINKABLE_RECORDS.toString();

		try{

			if ( groups.length < 1 ){
				throw new RepositoryServiceFault("At least one group is required.");
			}

			String userName = findUserName(saml);
			String callerIdentity = accessControl.getCallersIdentity();
			logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, userName, callerIdentity);

			List<String> authorisedGroups = new ArrayList<String>();
			for ( int i=0; i<groups.length; i++ ){
				if ( !accessControl.authoriseUser(saml, new AEFGroup(null, groups[i], null),
						RBACAction.ACTION_DR_GET_LINKABLE_RECORDS.toAEFAction(),
						new AEFProject(null, projectCode, false) ) ){
					logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, userName, callerIdentity);
				}
				else{
					authorisedGroups.add(groups[i]);
				}
			}
			if ( 0 == authorisedGroups.size() ){
				throw new NotAuthorisedFault("User '"+userName+"' is not authorised to perform the action '"+METHOD_NAME+"' for project '"+projectCode+"'");
			}

			return repositoryDAO.getLinkableRecords(projectCode, authorisedGroups.toArray(new String[authorisedGroups.size()]));

		}
		catch(PGSecurityInvalidSAMLException ex){
			throw new NotAuthorisedFault("The supplied SAML assertion has expired", ex);
		}
		catch(PGSecuritySAMLVerificationException ex){
			throw new NotAuthorisedFault("The supplied SAML assertion does not come from a trusted issuer", ex);
		}
		catch(PGSecurityException ex){
			throw new NotAuthorisedFault("An error occurred during authorisation", ex);
		}
	}

	public String[] getLinkedRecords(String projectCode, String[] groups, String saml) throws RemoteException, RepositoryServiceFault {

		final String METHOD_NAME = RBACAction.ACTION_DR_GET_LINKED_RECORDS.toString();

		try{

			if ( groups.length < 1 ){
				throw new IllegalArgumentException("At least one group is required.");
			}

			String userName = findUserName(saml);
			String callerIdentity = accessControl.getCallersIdentity();
			logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, userName, callerIdentity);

			List<String> authorisedGroups = new ArrayList<String>();
			for ( int i=0; i<groups.length; i++ ){
				if ( !accessControl.authoriseUser(saml, new AEFGroup(null, groups[i], null),
						RBACAction.ACTION_DR_GET_LINKED_RECORDS.toAEFAction(),
						new AEFProject(null, projectCode, false) ) ){
					logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, userName, callerIdentity);
				}
				else{
					authorisedGroups.add(groups[i]);
				}
			}
			if ( 0 == authorisedGroups.size() ){
				throw new NotAuthorisedFault("User '"+userName+"' is not authorised to perform the action '"+METHOD_NAME+"' for project '"+projectCode+"'");
			}

			return repositoryDAO.getLinkedRecords(projectCode, authorisedGroups.toArray(new String[authorisedGroups.size()]));

		}
		catch(PGSecurityInvalidSAMLException ex){
			throw new NotAuthorisedFault("The supplied SAML assertion has expired", ex);
		}
		catch(PGSecuritySAMLVerificationException ex){
			throw new NotAuthorisedFault("The supplied SAML assertion does not come from a trusted issuer", ex);
		}
		catch(PGSecurityException ex){
			throw new NotAuthorisedFault("An error occurred during authorisation", ex);
		}
	}

	public boolean updatePrimaryIdentifier(String identifier, String primaryIdentifier, String saml) {
			checkPermissionsByIdentifier(saml, "updatePrimaryIdentifier", RBACAction.ACTION_DR_UPDATE_PRIMARY_IDENTIFIER,identifier);
			return repositoryDAO.updatePrimaryIdentifier(identifier, primaryIdentifier);
	}

	public boolean updateSecondaryIdentifier(String identifier, String secondaryIdentifier, String saml) {
			checkPermissionsByIdentifier(saml, "updateSecondaryIdentifier", RBACAction.ACTION_DR_UPDATE_SECONDARY_IDENTIFIER,identifier);
			return repositoryDAO.updateSecondaryIdentifier(identifier, secondaryIdentifier);
	}

	public String[] getDeletedRecordsByGroups(String project, String[] groups, Calendar referenceDate, String saml) throws RemoteException, RepositoryServiceFault, NotAuthorisedFault {
		final String METHOD_NAME = RBACAction.ACTION_DR_GET_DELETED_RECORDS_BY_GROUPS.toString();

		try{

			if ( groups.length < 1 ){
				throw new RepositoryServiceFault("At least one group is required.");
			}

			String userName = findUserName(saml);
			String callerIdentity = accessControl.getCallersIdentity();
			logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, userName, callerIdentity);

			List<String> authorisedGroups = new ArrayList<String>();
			for ( int i=0; i<groups.length; i++ ){
				if ( !accessControl.authoriseUser(saml, new AEFGroup(null, groups[i], null),
						RBACAction.ACTION_DR_GET_DELETED_RECORDS_BY_GROUPS.toAEFAction(),
						new AEFProject(null, project, false) ) ){
					logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, userName, callerIdentity);
				}
				else{
					authorisedGroups.add(groups[i]);
				}
			}
			if ( 0 == authorisedGroups.size() ){
				throw new NotAuthorisedFault("User '"+userName+"' is not authorised to perform the action '"+METHOD_NAME+"' for project '"+project+"'");
			}

			return repositoryDAO.getDeletedRecordsByGroups(project,
					authorisedGroups.toArray(new String[authorisedGroups.size()]),
					referenceDate.getTime());
		}
		catch(PGSecurityInvalidSAMLException ex){
			throw new NotAuthorisedFault("The supplied SAML assertion has expired", ex);
		}
		catch(PGSecuritySAMLVerificationException ex){
			throw new NotAuthorisedFault("The supplied SAML assertion does not come from a trusted issuer", ex);
		}
		catch(PGSecurityException ex){
			throw new NotAuthorisedFault("An error occurred during authorisation", ex);
		}
	}

	public void updateRecordData(long recordId, RecordDataDTO data, String reason, String saml) {
			String[] codes = repositoryDAO.getProjectAndGroupForInstance(recordId);
			checkPermissionsByGroup(saml, "updateRecordData", RBACAction.ACTION_DR_UPDATE_RECORD_METADATA, codes[0], codes[1]);
			String userName = findUserName(saml);
			repositoryDAO.updateRecordMetadata(recordId, data, reason, userName);
	}

	public boolean canUpdateRecordData(String projectCode, String groupCode, String saml) {
			checkPermissionsByGroup(saml, "canUpdateRecordData", RBACAction.ACTION_DR_UPDATE_RECORD_METADATA, projectCode, groupCode);
			return true;
	}

	public void synchronizeDocumentStatusesWithPrimary(String identifier, String saml) {
			checkPermissionsByIdentifier(saml, "synchronizeDocumentStatusesWithPrimary", RBACAction.ACTION_DR_SYNC_DOC_STAT_WITH_PRIMARY,identifier);
			repositoryDAO.synchronizeDocumentStatusesWithPrimary(identifier);
	}

	public ConsentStatusResult getConsentAndStatusInfoForGroups(String project, String[] groups, Calendar referenceDate, String saml) {

		final String METHOD_NAME = RBACAction.ACTION_DR_GET_CONSENT_AND_STATUS_INFO_FOR_GROUPS.toString();

		try{
			if ( groups.length < 1 ){
				throw new IllegalArgumentException("At least one group is required.");
			}

			String userName = findUserName(saml);
			String callerIdentity = accessControl.getCallersIdentity();
			logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, userName, callerIdentity);

			List<String> authorisedGroups = new ArrayList<String>();
			for ( int i=0; i<groups.length; i++ ){
				if ( !accessControl.authoriseUser(saml, new AEFGroup(null, groups[i], null),
						RBACAction.ACTION_DR_GET_CONSENT_AND_STATUS_INFO_FOR_GROUPS.toAEFAction(),
						new AEFProject(null, project, false) ) ){
					logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, userName, callerIdentity);
				}
				else{
					authorisedGroups.add(groups[i]);
				}
			}
			if ( 0 == authorisedGroups.size() ){
				throw new NotAuthorisedFault("User '"+userName+"' is not authorised to perform the action '"+METHOD_NAME+"' for project '"+project+"'");
			}

			return repositoryDAO.getConsentAndStatusInfoForGroups(
					project,
					authorisedGroups.toArray(new String[authorisedGroups.size()]),
					referenceDate.getTime());
		}
		catch(PGSecurityInvalidSAMLException ex){
			throw new NotAuthorisedFault("The supplied SAML assertion has expired", ex);
		}
		catch(PGSecuritySAMLVerificationException ex){
			throw new NotAuthorisedFault("The supplied SAML assertion does not come from a trusted issuer", ex);
		}
		catch(PGSecurityException ex){
			throw new NotAuthorisedFault("An error occurred during authorisation", ex);
		}
	}

	public byte[] getConsentAndStatusInfoForGroupsCompressed(String project, String[] groups, Calendar referenceDate, String saml) throws RepositoryServiceFault {

		byte[] result=null;

		try{

			// Call the usual method
			ConsentStatusResult csr = getConsentAndStatusInfoForGroups(project,groups,referenceDate,saml);

			// Convert the result to an xml string
			XStream xstream = new XStream();
			String xml = xstream.toXML(csr);

			byte[] input = xml.getBytes("UTF-8");

			// Compress with highest level of compression
			Deflater compressor = new Deflater();
			compressor.setLevel(Deflater.BEST_COMPRESSION);

			compressor.setInput(input);
			compressor.finish();

			// Create an expandable byte array to hold the compressed data.
			ByteArrayOutputStream bos = new ByteArrayOutputStream();

			// Compress the data
			byte[] buf = new byte[1024];
			while (!compressor.finished()) {
				int count = compressor.deflate(buf);
				bos.write(buf, 0, count);
			}
			bos.close();

			// Get the compressed data
			result = bos.toByteArray();
		}
		catch(IOException ex){
			throw new RepositoryServiceFault("Problem streaming consent and status info",ex);
		}
		return result;
	}


	public RecordDTO getRecordSingleDocumentForOccurrence(String identifier, long docOccId, String saml) {

		checkPermissionsByIdentifier(saml, "getRecordSingleDocumentForOccurrence", RBACAction.ACTION_DR_GET_RECORD_SINGLE_DOCUMENT,identifier);

		RecordDTO record = repositoryDAO.getRecordSingleDocumentForOccurrence(identifier, docOccId);

		if(record != null){
			String userName = findUserName(saml);
			String projectCode = IdentifierHelper.getProjectCodeFromIdentifier(identifier);
			String groupCode = IdentifierHelper.getGroupCodeFromIdentifier(identifier);
			docHelper.setSaml(saml);
			org.psygrid.data.model.hibernate.Record r = record.toHibernate();
			r.setDocInstances(docHelper.getAllowedDocumentInstances(r.getDocInstances(), projectCode, groupCode, userName));
			record = r.toDTO();
		}

		return record;
	}

	public SearchRecordChangeHistoryResult searchRecordChangeHistory(
			String project, Calendar start, Calendar end, String user,
			String identifier, int startIndex, String saml){

			checkPermissionsByProject(saml,"searchRecordChangeHistory",RBACAction.ACTION_DR_AUDIT_BY_PROJECT,project);

			Date startDate = null;
			if ( null != start ){
				startDate = start.getTime();
			}
			Date endDate = null;
			if ( null != end ){
				endDate = end.getTime();
			}
			return repositoryDAO.searchRecordChangeHistory(project, startDate, endDate, user, identifier, startIndex);
	}

	public DocInstChangeHistoryResult[] searchDocInstChangeHistory(String identifier, long parentId, String saml)  {
			String projectCode = IdentifierHelper.getProjectCodeFromIdentifier(identifier);
			checkPermissionsByProject(saml,"searchDocInstChangeHistory",RBACAction.ACTION_DR_AUDIT_BY_RECORD,projectCode);
			return repositoryDAO.searchDocInstChangeHistory(identifier, parentId);
	}

	public ProvenanceForChangeResult[] getProvenanceForChange(String identifier, long changeId, String saml) {
			String projectCode = IdentifierHelper.getProjectCodeFromIdentifier(identifier);
			checkPermissionsByProject(saml,"getProvenanceForChange",RBACAction.ACTION_DR_GET_PROVENANCE_FOR_CHANGE,projectCode);
			return repositoryDAO.getProvenanceForChange(identifier, changeId);
	}

	public Calendar getSystemTime() {
		return Calendar.getInstance();
	}

	public long getStatusIdForDocument(String identifier, long docOccId,String saml) {
			checkPermissionsByIdentifier(saml, "getStatusIdForDocument", RBACAction.ACTION_DR_CHANGE_RECORD_STATUS,identifier);
			return repositoryDAO.getStatusIdForDocument(identifier, docOccId);
	}

	public String[] getPublishedDatasets(String[] projects) {
			return repositoryDAO.getPublishedDatasets(projects);
	}

	public boolean canRecordBeRandomized(String identifier, String saml)
			throws RemoteException, NotAuthorisedFault, RepositoryServiceFault {
			checkPermissionsByIdentifier(saml, "canRecordBeRandomized", RBACAction.ACTION_DR_CAN_RECORD_BE_RANDOMIZED,identifier);
			return repositoryDAO.canRecordBeRandomized(identifier);
	}


	public String[] importDocuments(long datasetID,long occurrenceID,String csvData,String saml) {
			checkPermissionsByProject(saml,"importDocuments",RBACAction.ACTION_DR_IMPORT_DATA,repositoryDAO.getProjectCodeForDataset(datasetID));
			//return repositoryDAO.importDocuments(datasetID,occurrenceID,csvData);
			return null;
	}

	/************************ Sample Tracking ******************************/
	// TODO:
	// The Sample Tracking service should really stand on it's own - and NOT be wrapped by the repository service

	public ConfigInfo getSampleConfig(String projectCode, String saml) {
		checkPermissionsByProject(saml,"getSampleConfig",RBACAction.ACTION_DR_VIEW_SAMPLES_CONFIG,projectCode);
		return sampleTrackingService.getSampleConfig(projectCode);
	}

	public void saveSampleConfig(ConfigInfo conf, String saml) {
		checkPermissionsByProject(saml,"saveSampleConfig",RBACAction.ACTION_DR_EDIT_SAMPLES_CONFIG,conf.getProjectCode());
		sampleTrackingService.saveSampleConfig(conf);
	}

	public ParticipantInfo getSampleParticipant(String recordID, String saml) {
		checkPermissionsByIdentifier(saml, "getPaticipant", RBACAction.ACTION_DR_VIEW_SAMPLES, recordID);
		return sampleTrackingService.getParticipant(recordID);
	}

	public void saveSampleParticipant(ParticipantInfo participant, String saml) {
		checkPermissionsByIdentifier(saml, "saveSampleParticipant", RBACAction.ACTION_DR_EDIT_SAMPLES,participant.getRecordID());
		sampleTrackingService.saveParticipant(participant);
	}

	public SampleInfo[] getSamples(String recordID, String saml) {
		checkPermissionsByIdentifier(saml, "getSamples", RBACAction.ACTION_DR_VIEW_SAMPLES, recordID);
		return sampleTrackingService.getSamples(recordID);
	}

	public SampleInfo saveSample(SampleInfo sample, String saml) {
		checkPermissionsByIdentifier(saml, "saveSample", RBACAction.ACTION_DR_EDIT_SAMPLES,sample.getRecordID());
		sample.setUser(findUserName(saml));
		return sampleTrackingService.saveSample(sample);
	}

	public SampleInfo[] getSampleRevisions(long sampleID, String saml) {
		// Need to grab a revision to get the record ID to check for permissions
		SampleInfo[] revisions = sampleTrackingService.getSampleRevisions(sampleID);
		if(revisions.length>0){
			String identifier = revisions[0].getRecordID();
			checkPermissionsByIdentifier(saml, "getSampleRevisions", RBACAction.ACTION_DR_VIEW_SAMPLES, identifier);
		}
		return revisions;
	}

	// Should probably be record identifier as parameter
	public long getNextSampleNumber(String projectCode,String saml) {
		checkPermissionsByProject(saml,"getNextSampleNumber",RBACAction.ACTION_DR_VIEW_SAMPLES_CONFIG,projectCode);
		return sampleTrackingService.getNextSampleNumber(projectCode);
	}

	public void exportToXml(
			org.psygrid.data.export.hibernate.ExportRequest request,
			String group, List<ExportSecurityActionMap> actionMap,
			OutputStream out, org.psygrid.data.export.metadata.DataSetMetaData meta) throws DAOException,
			RemoteException, NoDatasetException, TransformerException,
			XMLStreamException {
		 repositoryDAO.exportToXml(request, group, actionMap, out, meta);
	}

	public void exportToXml(
			org.psygrid.data.export.hibernate.ExportRequest request,
			List<String> identifiers, List<ExportSecurityActionMap> actionMap,
			OutputStream out, org.psygrid.data.export.metadata.DataSetMetaData meta) throws DAOException,
			RemoteException, NoDatasetException, TransformerException,
			XMLStreamException {
		repositoryDAO.exportToXml(request, identifiers, actionMap, out, meta);
	}

	public List<SimpleMailMessage> getAllMonthlySummaries(Date now) throws DAOException {
		return repositoryDAO.getAllMonthlySummaries(now);
	}

	public List<SimpleMailMessage> getAllScheduledReminders(Date now) throws DAOException {
		return repositoryDAO.getAllScheduledReminders(now);
	}

	public List<GroupSummary> getGroupSummary(List<String> projectCodes) {
		return repositoryDAO.getGroupSummary(projectCodes);
	}

	public boolean isProjectRandomized(String projectCode) {
		return repositoryDAO.isProjectRandomized(projectCode);
	}

	public Group getGroup(Long groupID) {
		return repositoryDAO.getGroup(groupID);
	}

	public void addGroup(String projectCode, Group group) {
		repositoryDAO.addGroup(projectCode,group);
	}

	public void updateGroup(Group group) {
		repositoryDAO.updateGroup(group);
	}

	public void deleteGroup(Long groupID) {
		repositoryDAO.deleteGroup(groupID);
	}


}

