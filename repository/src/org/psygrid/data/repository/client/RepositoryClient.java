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
package org.psygrid.data.repository.client;

import java.io.ByteArrayOutputStream;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.zip.Inflater;

import javax.xml.rpc.ServiceException;

import org.apache.axis.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.common.identifier.InvalidIdentifierException;
import org.psygrid.data.export.NoSuchExportFault;
import org.psygrid.data.export.UnableToCancelExportFault;
import org.psygrid.data.export.hibernate.ExportRequest;
import org.psygrid.data.importing.ImportData;
import org.psygrid.data.importing.ImportStatus;
import org.psygrid.data.model.dto.PersistentDTO;
import org.psygrid.data.model.dto.extra.ConsentStatusResult;
import org.psygrid.data.model.dto.extra.DocInstChangeHistoryResult;
import org.psygrid.data.model.dto.extra.IdentifierData;
import org.psygrid.data.model.dto.extra.ProvenanceForChangeResult;
import org.psygrid.data.model.dto.extra.SearchRecordChangeHistoryResult;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.repository.DuplicateDocumentsFault;
import org.psygrid.data.repository.Repository;
import org.psygrid.data.repository.RepositoryInvalidIdentifierFault;
import org.psygrid.data.repository.RepositoryNoConsentFault;
import org.psygrid.data.repository.RepositoryNoSuchDatasetFault;
import org.psygrid.data.repository.RepositoryOutOfDateFault;
import org.psygrid.data.repository.RepositoryServiceFault;
import org.psygrid.data.repository.RepositoryServiceLocator;
import org.psygrid.data.repository.RepositorySoapBindingStub;
import org.psygrid.data.repository.transformer.TransformerFault;
import org.psygrid.data.sampletracking.ConfigInfo;
import org.psygrid.data.sampletracking.ParticipantInfo;
import org.psygrid.data.sampletracking.SampleInfo;
import org.psygrid.data.utils.security.NotAuthorisedFault;

import com.thoughtworks.xstream.XStream;

/**
 * Class to act as a layer of abstraction between a Java client
 * and the web services exposed by the data repository.
 * 
 * @author Rob Harper
 *
 */
public class RepositoryClient extends org.psygrid.common.AbstractClient {

	private final static Log LOG = LogFactory.getLog(RepositoryClient.class);

	private final Repository service;

	/**
	 * Default no-arg constructor
	 */
	public RepositoryClient(){
		service = getService();
	}

	public RepositoryClient(URL url){
		super(url);
		service = getService();
	}

	/**
	 * Constructor that accepts a value for the url where the web
	 * service is located and the timeout for the web service.
	 * 
	 * @param url
	 * @param timeout
	 */
	public RepositoryClient(URL url, int timeout){
		super(url, timeout);
		service = getService();
	}

	/**
	 * Constructor that accepts a timeout for the web service.
	 * 
	 * @param timeout
	 */
	public RepositoryClient(int timeout){
		super(timeout);
		service = getService();
	}

	public String getVersion() throws ConnectException, SocketTimeoutException {
		try{
			return service.getVersion();
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}    
	}

	/**
	 * Get the list of datasets that have been modified since
	 * a given reference date.
	 * <p>
	 * Only summary details of the modified datasets are retrieved,
	 * not the complete datasets.
	 * 
	 * @param referenceDate The reference date after which datasets
	 * are considered to be modified.
	 * @param saml SAML assertion.
	 * @return List of modified datasets.
	 * @throws ConnectException if the client cannot connect to the remote
	 * web-service host.
	 */
	public List<DataSet> getModifiedDataSets(Date referenceDate, String saml)
	throws ConnectException, NotAuthorisedFault, RepositoryServiceFault, SocketTimeoutException {

		try{
			Calendar cal = Calendar.getInstance();
			cal.setTime(referenceDate);

			org.psygrid.data.model.dto.DataSetDTO[] dtoDSs = service.getModifiedDataSets(cal, saml);

			List<DataSet> dataSets = new ArrayList<DataSet>();
			for ( int i=0; i<dtoDSs.length; i++ ){
				org.psygrid.data.model.dto.DataSetDTO ds = dtoDSs[i];
				if ( null != ds ){
					dataSets.add(ds.toHibernate());
				}
			}

			return dataSets;
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}    
	}

	/**
	 * Get the raw binary data for a binary object.
	 * 
	 * @param dataSetId The ID of the dataset that the binary object
	 * is associated with.
	 * @param binaryObjectId The ID of the binary object to get
	 * that data for.
	 * @param saml SAML assertion.
	 * @return The raw binary data.
	 * @throws ConnectException if the client cannot connect to the remote
	 * web-service host.
	 * @throws NotAuthorisedFault if authorisation fails or returns false.
	 * @throws RepositoryServiceFault could not retrieve binary data because
	 * either the dataset id or binary object id is not valid, or any other
	 * unrecoverable error.
	 */
	public byte[] getBinaryData(Long dataSetId, Long binaryObjectId, String saml) 
	throws ConnectException, SocketTimeoutException, RepositoryServiceFault, NotAuthorisedFault{
		try{
			byte[] data = service.getBinaryData(dataSetId, binaryObjectId, saml);
			return data;
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}    

	}

	/**
	 * Retrieve a dataset from the repository.
	 * 
	 * @param dataSetId The ID of the dataset to retrieve.
	 * @param saml SAML assertion.
	 * @return The dataset.
	 * @throws ConnectException if the client cannot connect to the remote
	 * web-service host.
	 * @throws NotAuthorisedFault if authorisation fails or returns false.
	 * @throws RepositoryServiceFault if no dataset exists for the given id, 
	 * or any other unrecoverable error.
	 */
	public DataSet getDataSet(Long dataSetId, String saml)
	throws ConnectException, SocketTimeoutException, 
	RepositoryServiceFault, NotAuthorisedFault {
		try{
			org.psygrid.data.model.dto.DataSetDTO dtoDS = service.getDataSetComplete(dataSetId, saml);
			DataSet ds = null;
			if ( null != dtoDS ){
				ds = dtoDS.toHibernate();
			}

			return ds;
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}    

	}

	/**
	 * Save a record to the repository.
	 * 
	 * @param record The record to save.
	 * @param saml SAML assertion.
	 * @return The unique identifier of the saved record.
	 * @throws ConnectException if the client cannot connect to the remote
	 * web-service host.
	 * @throws NotAuthorisedFault if authorisation fails or returns false.
	 * @throws RepositoryServiceFault if the record is unable to be saved due
	 * to unrecoverable problems i.e. the client should not have allowed the
	 * record to be saved in this state.
	 * @throws RepositoryOutOfDateFault if the record is unable to be saved
	 * due to concurrency problems i.e. the user is trying to save an out-of-date
	 * record.
	 * @throws TransformerFault if an exception is thrown by one of the 
	 * transformers during the transformation process before the record is
	 * saved.
	 * @throws RepositoryNoConsentFault if the record is unable to be saved
	 * because there is insufficient consent.
	 * @throws RepositoryInvalidIdentifierFault if the record is unable to be 
	 * saved because its identifier is not known by the repository.
	 */
	public Long saveRecord(Record record, boolean discardDuplicates, String saml)
	throws ConnectException, SocketTimeoutException, 
	RepositoryServiceFault, 
	RepositoryOutOfDateFault,
	RepositoryNoConsentFault,
	RepositoryInvalidIdentifierFault,
	TransformerFault, 
	NotAuthorisedFault,
	DuplicateDocumentsFault{

		try{
			org.psygrid.data.model.dto.RecordDTO r = record.toDTO();
			return service.saveRecord(r, discardDuplicates, saml);
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		} catch (RepositoryNoSuchDatasetFault e) {
			LOG.fatal(e.getMessage(), e);
			throw new RuntimeException(e.getCause());
		} 
	}

	/**
	 * Save a record to the repository, using the
	 * given user name for provenance details rather than that
	 * supplied in the SAML assertion.
	 * 
	 * @param record The record to save.
	 * @param user The user name.
	 * @param saml SAML assertion.
	 * @return The unique identifier of the saved record.
	 * @throws ConnectException if the client cannot connect to the remote
	 * web-service host.
	 * @throws NotAuthorisedFault if authorisation fails or returns false.
	 * @throws RepositoryServiceFault if the record is unable to be saved due
	 * to unrecoverable problems i.e. the client should not have allowed the
	 * record to be saved in this state.
	 * @throws RepositoryOutOfDateFault if the record is unable to be saved
	 * due to concurrency problems i.e. the user is trying to save an out-of-date
	 * record.
	 * @throws TransformerFault if an exception is thrown by one of the 
	 * transformers during the transformation process before the record is
	 * saved.
	 * @throws RepositoryNoConsentFault if the record is unable to be saved
	 * because there is insufficient consent.
	 * @throws RepositoryInvalidIdentifierFault if the record is unable to be 
	 * saved because its identifier is not known by the repository.
	 */
	public Long saveRecordAsUser(Record record, String user, String saml)
	throws ConnectException, SocketTimeoutException, 
	RepositoryServiceFault, 
	RepositoryOutOfDateFault,
	RepositoryNoConsentFault,
	RepositoryInvalidIdentifierFault,
	TransformerFault, 
	NotAuthorisedFault {

		try{
			org.psygrid.data.model.dto.RecordDTO r = record.toDTO();
			return service.saveRecordAsUser(r, user, saml);
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}    
	}

	/**
	 * Retrieve a record from the repository, identified by its database
	 * generated unique identifier.
	 * 
	 * @param ds The dataset that the record is associated with, so that it 
	 * may be reattached.
	 * @param recordId The database unique identifier of the record to retrieve.
	 * @param saml SAML assertion.
	 * @return The record.
	 * @throws ConnectException if the client cannot connect to the remote
	 * web-service host.
	 * @throws NotAuthorisedFault if authorisation fails or returns false.
	 * @throws RepositoryServiceFault if no record exists for the given id, 
	 * or any other unrecoverable error.
	 */
	public Record getRecord(DataSet ds, Long recordId, String saml)
	throws ConnectException, SocketTimeoutException, 
	RepositoryServiceFault, 
	NotAuthorisedFault{

		try{
			org.psygrid.data.model.dto.RecordDTO result = service.getRecordComplete(recordId, saml);
			Record r = result.toHibernate();
			//attach the record to the dataset
			r.attach(ds);
			return r;
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}    

	}

	/**
	 * Retrieve a record from the repository, identified by its project
	 * generated unique identifier (i.e. study number).
	 * 
	 * @param ds The dataset that the record is associated with, so that it 
	 * may be reattached.
	 * @param identifier The project unique identifier of the record to retrieve.
	 * @param saml SAML assertion.
	 * @return The record.
	 * @throws ConnectException if the client cannot connect to the remote
	 * web-service host.
	 * @throws NotAuthorisedFault if authorisation fails or returns false.
	 * @throws RepositoryServiceFault if no record exists for the given id, 
	 * or any other unrecoverable error.
	 */
	public Record getRecord(DataSet ds, String identifier, String saml)
	throws ConnectException, SocketTimeoutException, 
	RepositoryServiceFault, 
	NotAuthorisedFault{

		try{
			org.psygrid.data.model.dto.RecordDTO result = service.getRecordComplete(identifier, saml);
			Record r = result.toHibernate();
			//attach the record to the dataset
			r.attach(ds);
			return r;
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}    

	}

	/**
	 * Retrieve a record from the repository, identified by its unique external identifier.
	 * 
	 * @param ds The dataset that the record is associated with, so that it may be re-attached.
	 * @param externalID The project unique external identifier of the record to retrieve.
	 * @param saml SAML assertion.
	 * @return The record.
	 * @throws ConnectException if the client cannot connect to the remote web-service host.
	 * @throws NotAuthorisedFault if authorisation fails or returns false.
	 * @throws RepositoryServiceFault if no record exists for the given id, or any other unrecoverable error.
	 */
	public Record getRecordByExternalID(DataSet ds, String externalID, String saml)
	throws ConnectException, SocketTimeoutException, RepositoryServiceFault, NotAuthorisedFault{
		try{
			long datasetID = ds.getId();
			org.psygrid.data.model.dto.RecordDTO result = service.getRecordCompleteByExternalID(datasetID,externalID, saml);
			Record r = null;
			if(result!=null){
				r = result.toHibernate();
				//attach the record to the dataset
				r.attach(ds);
			}
			return r;
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}    

	}
	
	
	/**
	 * Retrieve a record from the repository, only a summary of the record is
	 * retrieved, not the whole record.
	 * 
	 * @param ds The dataset that the record is associated with, so that it 
	 * may be reattached.
	 * @param recordId The database-generated unique identifier of the record 
	 * to retrieve.
	 * @param saml SAML assertion.
	 * @return The record.
	 * @throws ConnectException if the client cannot connect to the remote
	 * web-service host.
	 * @throws NotAuthorisedFault if authorisation fails or returns false.
	 * @throws RepositoryServiceFault if no record exists for the given id, 
	 * or any other unrecoverable error.
	 */
	public Record getRecordSummary(DataSet ds, Long recordId, String saml)
	throws ConnectException, SocketTimeoutException, 
	RepositoryServiceFault, 
	NotAuthorisedFault{

		try{
			org.psygrid.data.model.dto.RecordDTO result = service.getRecordSummary(recordId, saml);
			Record r = result.toHibernate();
			//attach the record to the dataset
			r.attach(ds);
			return r;
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}    

	}

	/**
	 * Retrieve a record from the repository, only a summary of the record is
	 * retrieved, not the whole record.
	 * 
	 * @param ds The dataset that the record is associated with, so that it 
	 * may be reattached.
	 * @param recordId The PsyGrid-generated unique identifier of the record 
	 * to retrieve.
	 * @param saml SAML assertion.
	 * @return The record.
	 * @throws ConnectException if the client cannot connect to the remote
	 * web-service host.
	 * @throws NotAuthorisedFault if authorisation fails or returns false.
	 * @throws RepositoryServiceFault if no record exists for the given id, 
	 * or any other unrecoverable error.
	 */
	public Record getRecordSummary(DataSet ds, String identifier, String saml)
	throws ConnectException, SocketTimeoutException, 
	RepositoryServiceFault, 
	NotAuthorisedFault{

		try{
			org.psygrid.data.model.dto.RecordDTO result = service.getRecordSummary(identifier, saml);
			Record r = result.toHibernate();
			//attach the record to the dataset
			r.attach(ds);
			return r;
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}    

	}

	/**
	 * Retrieve a list containing summary details of all records in the 
	 * repository for a given dataset.
	 * 
	 * @param dataSetId The id of the dataset to retrieve records for.
	 * @param saml SAML assertion.
	 * @return List of records for the dataset.
	 * @throws ConnectException if the client cannot connect to the remote
	 * web-service host.
	 * @throws NotAuthorisedFault if authorisation fails or returns false.
	 * @throws RepositoryServiceFault if no dataset exists for the specified id, 
	 * or any other unrecoverable error.
	 */
	public List<Record> getRecords(Long dataSetId, String saml)
	throws ConnectException, SocketTimeoutException, 
	RepositoryServiceFault, 
	NotAuthorisedFault{

		try{
			org.psygrid.data.model.dto.RecordDTO[] dtoRecords = service.getRecords(dataSetId, saml);
			List<Record> records = new ArrayList<Record>();
			for ( int i=0; i<dtoRecords.length; i++ ){
				org.psygrid.data.model.dto.RecordDTO r = dtoRecords[i];
				if ( null != r ){
					records.add(r.toHibernate());
				}
			}
			return records;
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}    

	}

	/**
	 * Retrieve a list containing summary details of all records in the 
	 * repository for a given dataset.
	 * 
	 * @param dataSetId The id of the dataset to retrieve records for.
	 * @param statusId The id of the status to retrieve records for.
	 * @param saml SAML assertion.
	 * @return List of records for the dataset.
	 * @throws ConnectException if the client cannot connect to the remote
	 * web-service host.
	 * @throws NotAuthorisedFault if authorisation fails or returns false.
	 * @throws RepositoryServiceFault if no dataset exists for the given id, 
	 * or any other unrecoverable error.
	 */
	public List<Record> getRecordsByStatus(Long dataSetId, Long statusId, String saml)
	throws ConnectException, SocketTimeoutException, 
	RepositoryServiceFault, 
	NotAuthorisedFault{

		try{
			org.psygrid.data.model.dto.RecordDTO[] dtoRecords = service.getRecordsByStatus(dataSetId, statusId, saml);
			List<Record> records = new ArrayList<Record>();
			for ( int i=0; i<dtoRecords.length; i++ ){
				org.psygrid.data.model.dto.RecordDTO r = dtoRecords[i];
				if ( null != r ){
					records.add(r.toHibernate());
				}
			}
			return records;
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}    

	}

	/**
	 * Generate a list of identifiers to be utilised by records based
	 * upon the given dataset.
	 * 
	 * @param dataSetId The unique identifier of the dataset.
	 * @param groupCode The group code to generate identifiers for.
	 * @param number The number of identifiers to generate.
	 * @param saml SAML assertion.
	 * @return The list of identifiers.
	 * @throws ConnectException if the client cannot connect to the remote
	 * web-service host.
	 * @throws NotAuthorisedFault if authorisation fails or returns false.
	 * @throws RepositoryServiceFault  if no dataset exists for the 
	 * given id, or any other unrecoverable error.
	 */
	public List<Identifier> generateIdentifiers(Long dataSetId, String groupCode, int number, String saml)
	throws ConnectException, SocketTimeoutException, 
	RepositoryServiceFault, NotAuthorisedFault{

		try{
			org.psygrid.data.model.dto.IdentifierDTO[] dtoIds = service.generateIdentifiers(dataSetId, groupCode, number, saml);
			List<Identifier> identifiers = new ArrayList<Identifier>();
			for ( int i=0; i<dtoIds.length; i++ ){
				org.psygrid.data.model.dto.IdentifierDTO id = dtoIds[i];
				if ( null != id ){
					identifiers.add(id.toHibernate());
				}
			}
			return identifiers;
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}    

	}

	/**
	 * Retrieve the list of all standard codes stored in the repository.
	 * 
	 * @param saml SAML assertion.
	 * @return The list of standard codes.
	 * @throws ConnectException if the client cannot connect to the remote
	 * web-service host.
	 */
	public List<StandardCode> getStandardCodes(String saml)
	throws ConnectException, SocketTimeoutException {

		try{
			org.psygrid.data.model.dto.StandardCodeDTO[] dtoCodes = service.getStandardCodes(saml);
			List<StandardCode> codes = new ArrayList<StandardCode>();
			for ( int i=0; i<dtoCodes.length; i++ ){
				org.psygrid.data.model.dto.StandardCodeDTO code = dtoCodes[i];
				if ( null != code ){
					codes.add(code.toHibernate());
				}
			}
			return codes;
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}    

	}

	/**
	 * Save a dataset to the data repository.
	 * 
	 * @param dataSet The dataset to save.
	 * @param saml SAML assertion.
	 * @return The unique identifier of the saved dataset.
	 * @throws ConnectException if the client cannot connect to the remote
	 * web-service host.
	 * @throws NotAuthorisedFault if authorisation fails or returns false.
	 * @throws RepositoryServiceFault if the dataset is unable to be saved due
	 * to unrecoverable problems i.e. the client should not have allowed the
	 * dataset to be saved in this state.
	 * @throws RepositoryOutOfDateFault if the dataset is unable to be saved
	 * due to concurrency problems i.e. the user is trying to save an out-of-date
	 * dataset.
	 */
	public Long saveDataSet(DataSet dataSet, String saml)
	throws ConnectException, SocketTimeoutException,
	RepositoryServiceFault, 
	RepositoryOutOfDateFault, 
	NotAuthorisedFault{

		try{
			org.psygrid.data.model.dto.DataSetDTO ds = dataSet.toDTO();
			return service.saveDataSet(ds, saml);
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}    

	}

	/**
	 * Mark a dataset as published; the dataset can no longer be edited
	 * and records may now be created for it.
	 * 
	 * @param dataSetId The unique identifier of the dataset to publish.
	 * @param saml SAML assertion.
	 * @throws ConnectException if the client cannot connect to the remote
	 * web-service host.
	 * @throws NotAuthorisedFault if authorisation fails or returns false.
	 * @throws RepositoryServiceFault if no dataset exists for the specified 
	 * id, or any other unrecoverable error.
	 */
	public void publishDataSet(Long dataSetId, String saml)
	throws ConnectException, SocketTimeoutException,
	RepositoryServiceFault, NotAuthorisedFault{

		try{
			service.publishDataSet(dataSetId, saml);
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}    

	}

	/**
     * Do a "dry run" of withdrawing consent from a record to tell us what
     * data (if any) will be removed from the record if the consent is
     * withdrawn for real.
	 * 
	 * @param recordId The id of the record from which consent is being withdrawn.
	 * @param consentFormId The id of the consent form for which consent is being 
	 * withdrawn.
	 * @param reason The reason why consent is being withdrawn.
	 * @param saml SAML assertion for security system.
     * @return Array of document occurrence names of those that would be
     * removed if the consent is withdrawn for real.
	 * @throws ConnectException if the client cannot connect to the remote
	 * web-service host.
	 * @throws NotAuthorisedFault if authorisation fails or returns false.
	 * @throws RepositoryServiceFault if no record exists for the given id,
	 * or no consent form exists for the given id, or any other
	 * unrecoverable error.
	 */
	public String[] withdrawConsentDryRun(Long recordId, Long consentFormId, String reason, String saml)
	throws ConnectException, SocketTimeoutException, RepositoryServiceFault, NotAuthorisedFault{

		try{
			return service.withdrawConsentDryRun(recordId, consentFormId, reason, saml);
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}    

	}

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
	 * @param reason The reason why consent is being withdrawn.
	 * @param saml SAML assertion for security system.
	 * @throws ConnectException if the client cannot connect to the remote
	 * web-service host.
	 * @throws NotAuthorisedFault if authorisation fails or returns false.
	 * @throws RepositoryServiceFault if no record exists for the given id,
	 * or no consent form exists for the given id, or any other
	 * unrecoverable error.
	 */
	public void withdrawConsent(Long recordId, Long consentFormId, String reason, String saml)
	throws ConnectException, SocketTimeoutException,
	RepositoryServiceFault, 
	NotAuthorisedFault{

		try{
			service.withdrawConsent(recordId, consentFormId, reason, saml);
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}    

	}

	/**
	 * Add consent for a single consent form of a record.
	 * 
	 * @param recordId The id of the record to which consent is being added.
	 * @param consentFormId The id of the consent form for which consent is being 
	 * added.
	 * @param location The physical lcoation of the consent form.
	 * @param saml SAML assertion for security system.
	 * @throws ConnectException if the client cannot connect to the remote
	 * web-service host.
	 * @throws NotAuthorisedFault if authorisation fails or returns false.
	 * @throws RepositoryServiceFault if no record exists for the given id,
	 * or no consent form exists for the given id, or any other
	 * unrecoverable error.
	 */
	public void addConsent(Long recordId, Long consentFormId, String location, String saml)
	throws ConnectException, SocketTimeoutException,
	RepositoryServiceFault, 
	NotAuthorisedFault{

		try{
			service.addConsent(recordId, consentFormId, location, saml);
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}    

	}

	/**
	 * Change the status of a single statused object.
	 * 
	 * @param statusedInstanceId The unique identifier of the object whose
	 * status is to be changed.
	 * @param newStatusId The unique identifier of the new status to set
	 * for this object.
	 * @param saml SAML assertion for security system.
	 * @throws ConnectException if the client cannot connect to the remote
	 * web-service host.
	 * @throws NotAuthorisedFault if authorisation fails or returns false.
	 * @throws RepositoryServiceFault if no statused instance exists for the
	 * given id, or no status exists for the given new status id, or any other
	 * unrecoverable error.
	 */
	public void changeStatus(Long statusedInstanceId, Long newStatusId, String saml)
	throws ConnectException, SocketTimeoutException,
	RepositoryServiceFault, 
	NotAuthorisedFault{

		try{
			service.changeStatus(statusedInstanceId, newStatusId, saml);
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}    

	}

	public void changeRecordStatus(String identifier, Long newStatusId, final boolean ignorePermittedTransitions, String saml)
	throws ConnectException, SocketTimeoutException,
	RepositoryServiceFault, 
	NotAuthorisedFault{

		try{
			service.changeRecordStatus(identifier, newStatusId, ignorePermittedTransitions, saml);
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}    

	}

	/**
	 * Change the status of a single document instance, the document instance being
	 * identified by the identifier of the record that it belongs to plus the
	 * document occurrence that it is an instance of.
	 * 
	 * @param identifier The identifier of the record.
	 * @param docOccId The unique id of the document occurrence.
	 * @param newStatusId The unique id of the new status.
	 * @param saml SAML assertion for the security system.
	 * @throws ConnectException if the client cannot connect to the remote
	 * web-service host.
	 * @throws NotAuthorisedFault if authorisation fails or returns false.
	 * @throws RepositoryServiceFault if no document instance exists for the
	 * given identifier and document occurrence id, or no status exists for the given 
	 * new status id, or any other unrecoverable error.
	 */
	public void changeDocumentStatus(String identifier, Long docOccId, Long newStatusId, String saml)
	throws ConnectException, SocketTimeoutException,
	RepositoryServiceFault, 
	NotAuthorisedFault{

		try{
			service.changeDocumentStatus(identifier, docOccId, newStatusId, saml);
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}    
	}


	/**
	 * Change the status of multiple document instance, the document instance being
	 * identified by the identifier of the record that it belongs to plus the
	 * document occurrence that it is an instance of.
	 * 
	 * @param identifier The identifier of the record.
	 * @param docOccIds The unique ids of the document occurrences.
	 * @param newStatus The short name of the new status.
	 * @param saml SAML assertion for the security system.
     * @return Array of document occurrences for which there was a problem 
     * changing the document status.
	 * @throws ConnectException if the client cannot connect to the remote
	 * web-service host.
	 * @throws NotAuthorisedFault if authorisation fails or returns false.
	 * @throws RepositoryServiceFault if no document instance exists for the
	 * given identifier and document occurrence id, or no status exists for the given 
	 * new status id, or any other unrecoverable error.
	 */
	public long[] changeDocumentStatus(String identifier, List<Long> docOccIds, String newStatus, String saml)
	throws ConnectException, SocketTimeoutException,
	RepositoryServiceFault, 
	NotAuthorisedFault{

		try{
			long[] ids = new long[docOccIds.size()];
			for ( int i=0; i<docOccIds.size(); i++ ){
				ids[i] = docOccIds.get(i);
			}
			return service.changeDocumentStatus(identifier, ids, newStatus, saml);
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return new long[0];
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}    
	}


	/**
	 * Retrieve a summary of a single dataset, identified by its project
	 * code from the security system.
	 * <p>
	 * If the dataset has not been modified since the last time its summary
	 * was retrieved, this date being identified by the referenceDate
	 * argument, then <code>null</code> is returned.
	 * 
	 * @param projectCode The project code.
	 * @param referenceDate The reference date.
	 * @param saml SAML assertion for security system.
	 * @return Summary of the dataset.
	 * @throws ConnectException if the client cannot connect to the remote
	 * web-service host.
	 * @throws NotAuthorisedFault if authorisation fails or returns false.
	 * @throws RepositoryServiceFault if an unrecoverable error occurs
	 * @throws RepositoryNoSuchDatasetFault if no dataset exists for the given
	 * project code.
	 */
	public DataSet getDataSetSummary(String projectCode, Date referenceDate, String saml)
	throws ConnectException, SocketTimeoutException, RepositoryServiceFault, NotAuthorisedFault, RepositoryNoSuchDatasetFault{

		Calendar cal = Calendar.getInstance();
		cal.setTime(referenceDate);

		try{
			org.psygrid.data.model.dto.DataSetDTO dtoDS = service.getDataSetSummary(projectCode, cal, saml);
			DataSet ds = null;
			if ( null != dtoDS ){
				ds = dtoDS.toHibernate();
			}
			return ds;
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}        

	}


	/**
	 * Retrieve a summary of a single dataset, identified by its project
	 * code from the security system. Summary details of all the dataset's
	 * documents are included in the DataSet's object graph.
	 * 
	 * @param projectCode The project code.
	 * @param saml SAML assertion for security system.
	 * @return Summary of the dataset.
	 * @throws ConnectException if the client cannot connect to the remote
	 * web-service host.
	 * @throws NotAuthorisedFault if authorisation fails or returns false.
	 * @throws RepositoryServiceFault if an unrecoverable error occurs
	 * @throws RepositoryNoSuchDatasetFault if no dataset exists for the given
	 * project code.
	 */
	public DataSet getDataSetSummaryWithDocs(String projectCode, String saml)
	throws ConnectException, SocketTimeoutException, RepositoryServiceFault, NotAuthorisedFault, RepositoryNoSuchDatasetFault{

		try{
			org.psygrid.data.model.dto.DataSetDTO dtoDS = service.getDataSetSummaryWithDocs(projectCode, saml);
			DataSet ds = null;
			if ( null != dtoDS ){
				ds = dtoDS.toHibernate();
			}
			return ds;
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}        

	}

	/**
	 * Mark a single response as being invalid, with an accompanying
	 * descriptive annotation if required.
	 * 
	 * @param responseId The unique id of the response to mark as invalid.
	 * @param annotation The annotation to add to the response.
	 * @param saml SAML assertion for the security system.
	 * @throws ConnectException if the client cannot connect to the remote
	 * web-service host.
	 * @throws NotAuthorisedFault if authorisation fails or returns false.
	 * @throws RepositoryServiceFault if no response exists for the given
	 * unique id.
	 */
	public void markResponseAsInvalid(Long responseId, String annotation, String saml)
	throws ConnectException, SocketTimeoutException,
	RepositoryServiceFault, 
	NotAuthorisedFault{

		try{
			service.markResponseAsInvalid(responseId, annotation, saml);
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}           

	}


	/**
	 * Mark a single response as being valid.
	 * 
	 * @param responseId The unique id of the response to mark as valid.
	 * @param saml SAML assertion for the security system.
	 * @throws ConnectException if the client cannot connect to the remote
	 * web-service host.
	 * @throws NotAuthorisedFault if authorisation fails or returns false.
	 * @throws RepositoryServiceFault if no response exists for the given
	 * unique id.
	 */
	public void markResponseAsValid(Long responseId, String saml)
	throws ConnectException, SocketTimeoutException,
	RepositoryServiceFault, 
	NotAuthorisedFault{

		try{
			service.markResponseAsValid(responseId, saml);
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}            

	}


	/**
	 * Retrieve a single record from the data repository, but containing
	 * only its document instances that have the given status.
	 * <p>
	 * Note that the returned record will have its database identifier
	 * wiped. This is so that when it is saved to the repository we know
	 * to re-attach the document instances back to the saved record.
	 * 
	 * @param ds The dataset that the record is associated with, so that it 
	 * may be reattached.
	 * @param identifier The identifier of the record to retrieve.
	 * @param status 
	 * @param saml SAML assertion for the security system.
	 * @throws ConnectException if the client cannot connect to the remote
	 * web-service host.
	 * @throws NotAuthorisedFault if authorisation fails or returns false.
	 * @throws RepositoryServiceFault if no record exists for the given
	 * identifier.
	 */
	public Record getRecordsDocumentsByStatus(DataSet ds, String identifier, String status, String saml)
	throws ConnectException, SocketTimeoutException, RepositoryServiceFault, NotAuthorisedFault {

		try{
			org.psygrid.data.model.dto.RecordDTO result = service.getRecordsDocumentsByStatus(identifier, status, saml);
			Record r = result.toHibernate();
			//attach the record to the dataset
			r.attach(ds);
			return r;
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}                
	}

	/**
	 * Retrieve the identifier of all records that are associated with the 
	 * given project and a group in the given list of groups.
	 * 
	 * @param project The project code.
	 * @param groups The list of group codes.
	 * @param saml SAML assertion for the security system.
	 * @return The list of record identifiers.
	 * @throws ConnectException if the client cannot connect to the remote
	 * web-service host.
	 * @throws NotAuthorisedFault if authorisation fails or returns false.
	 * @throws RepositoryServiceFault
	 */
	public List<String> getRecordsByGroups(String project, List<String> groups, String saml)
	throws ConnectException, SocketTimeoutException, RepositoryServiceFault, NotAuthorisedFault {

		try{
			String[] groupsArray = new String[groups.size()];
			for ( int i=0; i<groups.size(); i++ ){
				groupsArray[i] = groups.get(i);
			}
			String[] result = service.getRecordsByGroups(project, groupsArray, saml);
			return Arrays.asList(result);
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}           

	}

	/**
	 * Retrieve summary details of all records that are associated with the given 
	 * project and a group in the given list of groups, provided that the record's
	 * status and/or consent has been modified since the reference date.
	 * 
	 * @param project The project code.
	 * @param groups The list of group codes.
	 * @param referenceDate The reference date.
	 * @param saml SAML assertion for the security system.
	 * @return The list of record summaries.
	 * @throws ConnectException if the client cannot connect to the remote
	 * web-service host.
	 * @throws NotAuthorisedFault if authorisation fails or returns false.
	 * @throws RepositoryServiceFault
	 */
	public List<Record> getRecordsWithConsentByGroups(String project, List<String> groups, Date referenceDate, String saml)
	throws ConnectException, SocketTimeoutException, RepositoryServiceFault, NotAuthorisedFault {

		try{
			String[] groupsArray = new String[groups.size()];
			for ( int i=0; i<groups.size(); i++ ){
				groupsArray[i] = groups.get(i);
			}
			Calendar cal = Calendar.getInstance();
			cal.setTime(referenceDate);
			org.psygrid.data.model.dto.RecordDTO[] result = service.getRecordsWithConsentByGroups(project, groupsArray, cal, saml);
			List<Record> records = new ArrayList<Record>();
			for ( int i=0; i<result.length; i++ ){
				org.psygrid.data.model.dto.RecordDTO r = result[i];
				if ( null != r ){
					records.add(r.toHibernate());
				}
			}
			return records;
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}           

	}

	/**
	 * Retrieve summary details of all records that are associated with the given 
	 * project and a group in the given list of groups, provided that the record's
	 * status and/or consent has been modified since the reference date.
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
	 * @return The list of record summaries.
	 * @throws ConnectException if the client cannot connect to the remote
	 * web-service host.
	 * @throws NotAuthorisedFault if authorisation fails or returns false.
	 * @throws RepositoryServiceFault
	 */
	public List<Record> getRecordsWithConsentByGroups(String project, List<String> groups, Date referenceDate, int batchSize, int offset, String saml)
	throws ConnectException, SocketTimeoutException, RepositoryServiceFault, NotAuthorisedFault {

		try{
			String[] groupsArray = new String[groups.size()];
			for ( int i=0; i<groups.size(); i++ ){
				groupsArray[i] = groups.get(i);
			}
			Calendar cal = Calendar.getInstance();
			cal.setTime(referenceDate);
			org.psygrid.data.model.dto.RecordDTO[] result = service.getRecordsWithConsentByGroups(project, groupsArray, cal, batchSize, offset, saml);
			List<Record> records = new ArrayList<Record>();
			for ( int i=0; i<result.length; i++ ){
				org.psygrid.data.model.dto.RecordDTO r = result[i];
				if ( null != r ){
					records.add(r.toHibernate());
				}
			}
			return records;
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}           

	}

	/**
	 * Retrieve all records that are associated with the given project and
	 * a group in the given list of groups, where one or more of the document
	 * instances associated with the record have the given status.
	 * 
	 * @param project The project code.
	 * @param groups The list of group codes.
	 * @param status The document status.
	 * @param saml SAML assertion for the security system.
	 * @return The list of record summaries.
	 * @throws ConnectException if the client cannot connect to the remote
	 * web-service host.
	 * @throws NotAuthorisedFault if authorisation fails or returns false.
	 * @throws RepositoryServiceFault
	 */
	public List<String> getRecordsByGroupsAndDocStatus(String project, List<String> groups, String status, String saml)
	throws ConnectException, SocketTimeoutException, RepositoryServiceFault, NotAuthorisedFault {

		try{
			String[] groupsArray = new String[groups.size()];
			for ( int i=0; i<groups.size(); i++ ){
				groupsArray[i] = groups.get(i);
			}
			/*org.psygrid.data.model.dto.Record[] result = service.getRecordsByGroupsAndDocStatus(project, groupsArray, status, saml);
			List<IRecord> records = new ArrayList<IRecord>();
			for ( int i=0; i<result.length; i++ ){
				org.psygrid.data.model.dto.Record r = result[i];
				if ( null != r ){
					records.add(r.toHibernate());
				}
			}*/
			String[] result = service.getRecordsByGroupsAndDocStatus(project, groupsArray, status, saml);
			List<String> records = new ArrayList<String>();
			for ( int i=0; i<result.length; i++ ){
				if ( null != result[i] ){
					records.add(result[i]);
				}
			}
			return records;
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}         

	}

	/**
	 * Add a single identifier to the repository.
	 * <p>
	 * This is different from generateIdentifiers in that the specification
	 * of the identifier is entirely defined by the calling application.
	 * Therefore this method is only used in special cases (e.g. performing 
	 * data import) - in normal operation generateIdentifiers should always 
	 * be used to ensure that there are no conflicts when trying to save
	 * identifiers.
	 * 
	 * @param dataSetId The unique identifier of the dataset with which
	 * the identifier being saved is associated.
	 * @param identifier The identifier to save.
	 * @param saml SAML assertion for the security system.
	 * @return The saved identifier object.
	 * @throws ConnectException if the client cannot connect to the remote
	 * web-service host.
	 * @throws NotAuthorisedFault if authorisation fails or returns false.
	 * @throws RepositoryServiceFault if the identifier is unable to be saved
	 * due to unrecoverable problems.
	 */
	public Identifier addIdentifier(Long dataSetId, Identifier identifier, String saml)
	throws ConnectException, SocketTimeoutException, RepositoryServiceFault, NotAuthorisedFault {

		try{
			return service.addIdentifier(dataSetId, identifier.toDTO(), saml).toHibernate();
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}          

	}

	/**
	 * Patch a dataset in the data repository.
	 * 
	 * @param dataSet The dataset to patch.
	 * @param saml SAML assertion.
	 * @return The unique identifier of the patches dataset.
	 * @throws ConnectException if the client cannot connect to the remote
	 * web-service host.
	 * @throws NotAuthorisedFault if authorisation fails or returns false.
	 * @throws RepositoryServiceFault if the dataset is unable to be saved due
	 * to unrecoverable problems i.e. the client should not have allowed the
	 * dataset to be saved in this state.
	 * @throws RepositoryOutOfDateFault if the dataset is unable to be patched
	 * due to concurrency problems i.e. the user is trying to patch an out-of-date
	 * dataset.
	 */
	public Long patchDataSet(DataSet dataSet, String saml)
	throws ConnectException, SocketTimeoutException,
	RepositoryServiceFault, 
	RepositoryOutOfDateFault, 
	NotAuthorisedFault,
	RepositoryNoConsentFault, 
	DuplicateDocumentsFault, 
	RepositoryInvalidIdentifierFault, 
	TransformerFault
	{
		try{
			org.psygrid.data.model.dto.DataSetDTO ds = dataSet.toDTO();
			return service.patchDataSet(ds, saml);
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}    

	}

	/**
	 * Remove a DataSet that has not been published.
	 * 
	 * @param dataSetId The unique identifier of the dataset to be removed.
	 * @param projectCode The unique project code of the dataset.
	 * @param saml SAML assertion for security system.
	 * @throws ConnectException if the client cannot connect to the remote
	 * web-service host.
	 * @throws RemoteException
	 * @throws RepositoryServiceFault
	 * @throws NotAuthorisedFault if authorisation fails or returns false. 
	 */
	public void removeDataSet(long dataSetId, String projectCode, String saml)
	throws ConnectException, SocketTimeoutException, RemoteException, RepositoryServiceFault, NotAuthorisedFault {

		try{
			service.removeDataSet(dataSetId, projectCode, saml);
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}    

	}
	

	/**
	 * Remove a DataSet that has been published, this will remove all
	 * associated reports and records as well.
	 * 
	 * @param dataSetId The unique identifier of the dataset to be removed.
	 * @param projectCode The unique project code of the dataset.
	 * @param saml SAML assertion for security system.
	 * @throws ConnectException if the client cannot connect to the remote
	 * web-service host.
	 * @throws RemoteException
	 * @throws RepositoryServiceFault
	 * @throws NotAuthorisedFault if authorisation fails or returns false. 
	 */
	public void removePublishedDataSet(long dataSetId, String projectCode, String saml)
	throws ConnectException, SocketTimeoutException, RemoteException, RepositoryServiceFault, NotAuthorisedFault {

		try{
			service.removePublishedDataSet(dataSetId, projectCode, saml);
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}    

	}


	/**
	 * Retrieve a document with only the document instance for the single specified 
	 * document in its object graph.
	 * 
	 * @param recordId The database id of the record.
	 * @param docInstId The database id of the document instance.
	 * @param ds The dataset that the record is associated with, so that it 
	 * may be reattached.
	 * @param saml SAML assertion.
	 * @return The record.
	 * @throws ConnectException if the client cannot connect to the remote
	 * web-service host.
	 * @throws NotAuthorisedFault if authorisation fails or returns false.
	 * @throws RepositoryServiceFault if no record exists for the given id, 
	 * or any other unrecoverable error.
	 */
	public Record getRecordSingleDocument(Long recordId, Long docInstId, DataSet ds, String saml)
	throws ConnectException, SocketTimeoutException, RemoteException, RepositoryServiceFault, NotAuthorisedFault {

		try{
			org.psygrid.data.model.dto.RecordDTO result = service.getRecordSingleDocument(recordId, docInstId, saml);
			Record r = result.toHibernate();
			//attach the record to the dataset
			r.attach(ds);
			return r;
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}    

	}

	/**
	 * Retrieve a document with only the document instance for the single specified 
	 * document in its object graph.
	 * 
	 * @param recordId The database id of the record.
	 * @param docOccId The database id of the document occurrence.
	 * @param ds The dataset that the record is associated with, so that it 
	 * may be reattached.
	 * @param saml SAML assertion.
	 * @return The record.
	 * @throws ConnectException
	 * @throws SocketTimeoutException
	 * @throws RemoteException
	 * @throws RepositoryServiceFault
	 * @throws NotAuthorisedFault
	 */
	public Record getRecordSingleDocumentForOccurrence(String identifier, Long docOccId, DataSet ds, String saml)
	throws ConnectException, SocketTimeoutException, RemoteException, RepositoryServiceFault, NotAuthorisedFault {

		try{
			org.psygrid.data.model.dto.RecordDTO result = service.getRecordSingleDocumentForOccurrence(identifier, docOccId, saml);
			Record r = result.toHibernate();
			//attach the record to the dataset
			r.attach(ds);
			return r;
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}    
	}

	/**
	 * Send an email to PsyGrid support.
	 * 
	 * @param subject The title of the email
	 * @param body The body of the email.
	 * @param saml SAML assertion for security system.
	 * @throws ConnectException if the client cannot connect to the remote
	 * web-service host.
	 * @throws RepositoryServiceFault if the email cannot be sent.
	 */
	public void emailSupport(String subject, String body, String saml) 
	throws ConnectException, SocketTimeoutException, RepositoryServiceFault {

		try{
			service.emailSupport(subject, body, saml);
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}    
	}

	/**
	 * Request an export of data from the PsyGrid data repository.
	 * <p>
	 * The request will then be serviced at a later date.
	 * 
	 * @param projectCode The project code of the project to export 
	 * data from.
	 * @param groups The list of groups within the project to export data
	 * for.
	 * @param format The format the data will be exported to.
	 * @param saml SAML assertion for the security system.
	 * @throws RemoteException
	 * @throws RepositoryServiceFault
	 * @throws NotAuthorisedFault
	 */
	public void requestExport(ExportRequest exportRequest, String saml)
	throws ConnectException, SocketTimeoutException, RepositoryServiceFault, NotAuthorisedFault, RepositoryNoSuchDatasetFault {
		org.psygrid.data.export.dto.ExportRequest request = exportRequest.toDTO();
		try{
			service.requestExport(request, saml);
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}  
	}

	/**
	 * Request an export of data from the PsyGrid data repository for immediate execution.
	 * 
	 * @param projectCode The project code of the project to export 
	 * data from.
	 * @param groups The list of groups within the project to export data
	 * for.
	 * @param format The format the data will be exported to.
	 * @param saml SAML assertion for the security system.
	 * @throws RemoteException
	 * @throws RepositoryServiceFault
	 * @throws NotAuthorisedFault
	 */
	public void requestImmediateExport(ExportRequest exportRequest, String saml)
	throws ConnectException, SocketTimeoutException, RepositoryServiceFault, NotAuthorisedFault, RepositoryNoSuchDatasetFault {
		org.psygrid.data.export.dto.ExportRequest request = exportRequest.toDTO();
		try{
			service.requestImmediateExport(request, saml);
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}  
	}

	/**
	 * Get the details of all export requests for the given projects submitted
	 * by the calling user.
	 * 
	 * @param projects The list of projects.
	 * @param saml SAML assertion for the security system.
	 * @return List of export requests.
	 * @throws ConnectException
	 * @throws SocketTimeoutException
	 * @throws RepositoryServiceFault
	 * @throws NotAuthorisedFault
	 */
	public List<ExportRequest> getMyExportRequests(String[] projects, String saml)
	throws ConnectException, SocketTimeoutException, RepositoryServiceFault, NotAuthorisedFault {

		try{
			org.psygrid.data.export.dto.ExportRequest[] dtoReqs = service.getMyExportRequests(projects, saml);
			List<ExportRequest> reqs = new ArrayList<ExportRequest>();
			for ( org.psygrid.data.export.dto.ExportRequest dtoReq: dtoReqs ){
				reqs.add(dtoReq.toHibernate());
			}
			return reqs;
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}    
	}

	/**
	 * Download the data for a completed export request.
	 * 
	 * @param exportRequestId The unique id of the export request.
	 * @param saml SAML assertion for the security system.
	 * @return The exported data.
	 * @throws ConnectException
	 * @throws SocketTimeoutException
	 * @throws RepositoryServiceFault
	 * @throws NotAuthorisedFault
	 * @throws NoSuchExportFault
	 */
	public byte[] downloadExport(long exportRequestId, String saml)
	throws ConnectException, SocketTimeoutException, RepositoryServiceFault, NotAuthorisedFault, NoSuchExportFault {
		try{
			return service.downloadExport(exportRequestId, saml);
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}    
	}


	/**
	 * Download a hash file of the zipped export file, in the specified 
	 * format, as a text file. Used to confirm the successful download
	 * of the export file.
	 * 
	 * @param exportRequestId The unique id of the export request.
	 * @param format The format of the hash, either SHA1 or MD5 
	 * @param saml SAML assertion for the security system.
	 * @return The exported data.
	 * @throws ConnectException
	 * @throws SocketTimeoutException
	 * @throws RepositoryServiceFault
	 * @throws NotAuthorisedFault
	 * @throws NoSuchExportFault
	 */
	public byte[] downloadExportHash(long exportRequestId, String format, String saml)
	throws ConnectException, SocketTimeoutException, RepositoryServiceFault, NotAuthorisedFault, NoSuchExportFault {
		try{
			return service.downloadExportHash(exportRequestId, format, saml);
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}    
	}
	
	/**
	 * Retrieve a list of identifiers for records in a given dataset.
	 * 
	 * @param dataSetId
	 * @param saml
	 * @return identifiers
	 * @throws ConnectException
	 * @throws SocketTimeoutException
	 * @throws RemoteException
	 * @throws RepositoryServiceFault
	 * @throws NotAuthorisedFault
	 */
	public List<String> getIdentifiers(Long dataSetId, String saml)
	throws ConnectException, SocketTimeoutException, RemoteException, RepositoryServiceFault, NotAuthorisedFault {

		try{
			String[] results = service.getIdentifiers(dataSetId, saml);

			List<String> identifiers = new ArrayList<String>();
			for (String id: results) {
				identifiers.add(id);
			}
			return identifiers;
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}    

	}

    /**
     * Retrieve a list of identifiers for documents containing a text entry with a given value.
     * 
     * @param projectCode the dataset code
     * @param documentName the document name
     * @param entryName the text entry name
     * @param entryValue the text entry value
     * @return the list of identifiers for records matching the value
     */
    public String[] getIdentifiersByResponse(String projectCode, String documentName, String entryName, String textValue, String saml)
    	throws ConnectException, RemoteException, SocketTimeoutException, RepositoryServiceFault {
		try{
			String[] results = service.getIdentifiersByResponse(projectCode, documentName, entryName, textValue, saml);

			return results;
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}    
    	
    }
	
	/**
	 * Retrieve a list of identifiers for records in a given dataset.
	 * 
	 * @param dataSetId
	 * @param saml
	 * @return identifiers
	 * @throws ConnectException
	 * @throws SocketTimeoutException
	 * @throws RemoteException
	 * @throws RepositoryServiceFault
	 * @throws NotAuthorisedFault
	 */
	public IdentifierData[] getIdentifiersExtended(Long dataSetId, String saml)
	throws ConnectException, SocketTimeoutException, RemoteException, RepositoryServiceFault, NotAuthorisedFault {

		try{
			return service.getIdentifiersExtended(dataSetId, saml);
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}    

	}

	/**
	 * Cancel an export request.
	 * 
	 * @param exportRequestId The unique id of the export request.
	 * @param saml SAML assertion for the security system.
	 * @throws ConnectException
	 * @throws SocketTimeoutException
	 * @throws RepositoryServiceFault
	 * @throws NotAuthorisedFault
	 * @throws UnableToCancelExportFault
	 */
	public void cancelExport(long exportRequestId, String saml)
	throws ConnectException, SocketTimeoutException, RepositoryServiceFault, NotAuthorisedFault, UnableToCancelExportFault {
		try{
			service.cancelExport(exportRequestId, saml);
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}    
	}

	public Object transform(Long dsId, Transformer transformer, List<String> responses, String saml)
	throws ConnectException, SocketTimeoutException, RepositoryServiceFault, NotAuthorisedFault, TransformerFault {

		try{
			if (transformer == null) {
				return null;
			}

			org.psygrid.data.model.dto.TransformerDTO dtoTransformer = transformer.toDTO();
			String[] dtoResponses = new String[responses.size()];
			for (int i = 0; i<responses.size(); i++) {
				if (responses.get(i) != null) {
					dtoResponses[i] = responses.get(i);
				}
			}

			Object value = service.transform(dsId, dtoTransformer, dtoResponses, saml);

			return value;
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}  
		return null;
	}

	public void deleteRecord(String identifier, String saml)
	throws ConnectException, SocketTimeoutException, RepositoryServiceFault, NotAuthorisedFault {
		try{
			service.deleteRecord(identifier, saml);
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}  
	}
	
	/**
	 * Retrieve the identifiers of records that have been deleted since the
	 * given reference date, for the given project and groups.
	 * 
	 * @param project The project code.
	 * @param groups The list of group codes.
	 * @param referenceDate The reference date.
	 * @param saml SAML assertion for the security system.
	 * @return The list of identifiers of deleted records.
	 * @throws ConnectException if the client cannot connect to the remote
	 * web-service host.
	 * @throws NotAuthorisedFault if authorisation fails or returns false.
	 * @throws RepositoryServiceFault
	 */
	public List<String> getDeletedRecordsByGroups(String project, List<String> groups, Date referenceDate, String saml)
	throws ConnectException, SocketTimeoutException, RepositoryServiceFault, NotAuthorisedFault {

		try{
			String[] groupsArray = groups.toArray(new String[groups.size()]);
			Calendar cal = Calendar.getInstance();
			cal.setTime(referenceDate);
			String[] result = service.getDeletedRecordsByGroups(project, groupsArray, cal, saml);
			List<String> records = new ArrayList<String>();
			for ( String id: result ){
				records.add(id);
			}
			return records;
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}           

	}


	public void updateRecordMetadata(Record record, RecordData recordData, String reason, String saml)
	throws ConnectException, SocketTimeoutException, RepositoryServiceFault, NotAuthorisedFault {

		try{
			service.updateRecordData(record.getId(), recordData.toDTO(), reason, saml);
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}           

	}

	/**
	 * Get identifiers of all records for which it is possible to link a 
	 * record in another dataset to, for the given project and groups.
	 * 
	 * @param project The project code.
	 * @param groups The list of group codes.
	 * @param saml SAML assertion for the security system.
	 * @return The list of identifiers.
	 * @throws ConnectException if the client cannot connect to the remote
	 * web-service host.
	 * @throws NotAuthorisedFault if authorisation fails or returns false.
	 * @throws RepositoryServiceFault
	 */
	public List<String> getLinkableRecords(String project, List<String> groups, String saml)
	throws ConnectException, SocketTimeoutException, RepositoryServiceFault, NotAuthorisedFault {

		try{
			String[] groupsArray = new String[groups.size()];
			for ( int i=0; i<groups.size(); i++ ){
				groupsArray[i] = groups.get(i);
			}
			String[] result = service.getLinkableRecords(project, groupsArray, saml);
			return Arrays.asList(result);
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}           

	}

	/**
	 * Get identifiers of all records that are primary records, linked 
	 * to secondary records for data replication, for the given project 
	 * and groups.
	 * 
	 * @param project The project code.
	 * @param groups The list of group codes.
	 * @param saml SAML assertion for the security system.
	 * @return The list of identifiers.
	 * @throws ConnectException if the client cannot connect to the remote
	 * web-service host.
	 * @throws NotAuthorisedFault if authorisation fails or returns false.
	 * @throws RepositoryServiceFault
	 */
	public List<String> getLinkedRecords(String project, List<String> groups, String saml)
	throws ConnectException, SocketTimeoutException, RepositoryServiceFault, NotAuthorisedFault {

		try{
			String[] groupsArray = new String[groups.size()];
			for ( int i=0; i<groups.size(); i++ ){
				groupsArray[i] = groups.get(i);
			}
			String[] result = service.getLinkedRecords(project, groupsArray, saml);
			return Arrays.asList(result);
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}           

	}

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
	 * @throws ConnectException
	 * @throws SocketTimeoutException
	 * @throws RepositoryServiceFault
	 * @throws NotAuthorisedFault
	 */
	public boolean updatePrimaryIdentifier(String identifier, String primaryIdentifier, String saml)
	throws ConnectException, SocketTimeoutException, RepositoryServiceFault, NotAuthorisedFault {

		try{
			return service.updatePrimaryIdentifier(identifier, primaryIdentifier, saml);
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return false;
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}           

	}

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
	 * @throws ConnectException
	 * @throws SocketTimeoutException
	 * @throws RepositoryServiceFault
	 * @throws NotAuthorisedFault
	 */
	public boolean updateSecondaryIdentifier(String identifier, String secondaryIdentifier, String saml)
	throws ConnectException, SocketTimeoutException, RepositoryServiceFault, NotAuthorisedFault {

		try{
			return service.updateSecondaryIdentifier(identifier, secondaryIdentifier, saml);
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return false;
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}           

	}

	/**
	 * Synchronize a the statuses of a record's document instances with those
	 * of its linked primary record.
	 * <p>
	 * If the record is not the secondary in a dual data entry relationship,
	 * then nothing will be done.
	 * 
	 * @param identifier The identifier of the record.
	 * @param saml
	 * @throws ConnectException
	 * @throws SocketTimeoutException
	 * @throws RepositoryServiceFault
	 * @throws NotAuthorisedFault
	 */
	public void synchronizeDocumentStatusesWithPrimary(String identifier, String saml)
	throws ConnectException, SocketTimeoutException, RepositoryServiceFault, NotAuthorisedFault {

		try{
			service.synchronizeDocumentStatusesWithPrimary(identifier, saml);
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}           

	}

	/**
	 * Get the consent and status (record and document instance) details
	 * of record int he given groups of the given project where consent
	 * or status has been updated since the given reference date.
	 * 
	 * @param project  The project code.
	 * @param groups The list of group codes
	 * @param referenceDate The reference date
	 * @param saml SAML assertion for the security system.
	 * @return Consent and status details.
	 * @throws ConnectException if the client cannot connect to the remote
	 * web-service host.
	 * @throws NotAuthorisedFault if authorisation fails or returns false.
	 * @throws RepositoryServiceFault
	 */
	public ConsentStatusResult getConsentAndStatusInfoForGroups(String project, List<String> groups, Date referenceDate, String saml)
	throws ConnectException, SocketTimeoutException, RepositoryServiceFault, NotAuthorisedFault {

		try{
			String[] groupsArray = new String[groups.size()];
			for ( int i=0; i<groups.size(); i++ ){
				groupsArray[i] = groups.get(i);
			}

			Calendar cal = Calendar.getInstance();
			cal.setTime(referenceDate);

			// Call the server function that returns the results as a compressed byte array
			// this is a solution to Axis 1.4. memory problems during de-serialization
			byte[] bytes = service.getConsentAndStatusInfoForGroupsCompressed(project, groupsArray, cal, saml);
			
			// Inflater
			Inflater decompressor = new Inflater();
			decompressor.setInput(bytes);

			// Create an expandable byte array to hold the decompressed data
			ByteArrayOutputStream bos = new ByteArrayOutputStream();

			// Decompress the data
			byte[] buf = new byte[1024];
			while (!decompressor.finished()) {
				int count = decompressor.inflate(buf);
				bos.write(buf, 0, count);
			}
			bos.close();

			// Get the decompressed data
			byte[] decompressedData = bos.toByteArray();
			
			// Convert back to java objects
			String xml = new String(decompressedData, "UTF-8");
			XStream xstream = new XStream();
			ConsentStatusResult result = (ConsentStatusResult)xstream.fromXML(xml);
			
			return result;
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		} catch (Exception ex) {
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex);
		}         
	}

	/**
	 * Find out whether the user has permission to update the metadata
	 * for the given group and project.
	 * 
	 * @param projectCode
	 * @param groupCode
	 * @param saml
	 * @return boolean
	 * @throws ConnectException
	 * @throws SocketTimeoutException
	 * @throws RepositoryServiceFault
	 * @throws NotAuthorisedFault
	 */
	public boolean canUpdateRecordMetadata(String projectCode, String groupCode, String saml)
	throws ConnectException, SocketTimeoutException, RepositoryServiceFault, NotAuthorisedFault {
		try{
			Repository service = getService();
			return service.canUpdateRecordData(projectCode, groupCode, saml);
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return false;
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}           
	}

	/**
	 * Get the current system time of the repository.
	 * <p>
	 * Used to synchronize clocks for clients of the repository when they
	 * do not have access to direct NTP.
	 * 
	 * @return Date, the current system time.
	 */
	public Date getSystemTime() 
	throws ConnectException, SocketTimeoutException {
		try{
			return service.getSystemTime().getTime();
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}           
	}

	/**
	 * Get the offset between the local clock and the clock of the
	 * central data repository.
	 * <p>
	 * Used to synchronize clocks for clients of the repository when they
	 * do not have access to direct NTP.
	 * <p>
	 * Network latency is accounted for, and the returned value is the 
	 * average of five time requests.
	 * 
	 * @return Long, the offset in milliseconds between the local clock
	 * and the repository clock.
	 */
	public long getClockOffset() 
	throws ConnectException, SocketTimeoutException {
		try{
			long offsetsSum = 0;
			final int attempts = 5;
			for ( int i=0; i<attempts; i++ ){
				Date start = new Date();
				Calendar sysTime = service.getSystemTime();
				Date finish = new Date();
				long latency = (finish.getTime() - start.getTime()) / 2;
				offsetsSum += ((sysTime.getTime().getTime() + latency) - finish.getTime());    			
			}
			return offsetsSum / attempts;
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return 0;
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}           
	}

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
	 * @throws ConnectException
	 * @throws SocketTimeoutException
	 * @throws RepositoryServiceFault
	 * @throws NotAuthorisedFault
	 */
	public SearchRecordChangeHistoryResult searchRecordChangeHistory(String project, Date start, Date end, String user, String identifier, int startIndex, String saml)
	throws ConnectException, SocketTimeoutException, RepositoryServiceFault, NotAuthorisedFault {

		try{

			Calendar startCal = null;
			if ( null != start ){
				startCal = Calendar.getInstance();
				startCal.setTime(start);
			}

			Calendar endCal = null;
			if ( null != end ){
				endCal = Calendar.getInstance();
				endCal.setTime(end);
			}

			return service.searchRecordChangeHistory(project, startCal, endCal, user, identifier, startIndex, saml);
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}           

	}

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
	 * @throws ConnectException
	 * @throws SocketTimeoutException
	 * @throws RepositoryServiceFault
	 * @throws InvalidIdentifierException 
	 * @throws NotAuthorisedFault
	 */
	public DocInstChangeHistoryResult[] searchDocInstChangeHistory(String identifier, long parentId, String saml)
	throws ConnectException, SocketTimeoutException, InvalidIdentifierException {

		try{
			return service.searchDocInstChangeHistory(identifier, parentId, saml);
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}           

	}

	public ProvenanceForChangeResult[] getProvenanceForChange(
			String identifier, long changeId, String saml)
	throws ConnectException, SocketTimeoutException, NotAuthorisedFault, InvalidIdentifierException {

		try{
			return service.getProvenanceForChange(identifier, changeId, saml);
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}           

	}

	public Status getStatusForDocument(String identifier, long docOccId, DataSet ds, String saml)
	throws ConnectException, SocketTimeoutException, RepositoryServiceFault, NotAuthorisedFault {

		try{
			long statusId = service.getStatusIdForDocument(identifier, docOccId, saml);
			for ( int i=0, c=ds.numDocuments(); i<c; i++ ){
				Document doc = ds.getDocument(i);
				for ( int j=0, d=doc.numOccurrences(); j<d; j++ ){
					DocumentOccurrence occ = doc.getOccurrence(j);
					if ( docOccId == occ.getId().longValue() ){
						for ( int k=0, e=doc.numStatus(); k<e; k++ ){
							Status s = doc.getStatus(k);
							if ( statusId == s.getId().longValue() ){
								return s;
							}
						}
					}
				}
			}
			return null;
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}           


	}
	
	public String[] getPublishedDatasets(String[] projects)
	throws ConnectException, SocketTimeoutException {
		
		try{
			return service.getPublishedDatasets(projects);
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}           

	}
	
	/**
     * Decide whether a Record may be randomized i.e. a document instance exists
     * for the occurrence that is the randomization trigger, it is not incomplete
     * and randomization has not already been performed.
     * 
     * @param identifier The identifier of the record.
	 * @param saml
     * @return True if record can be randomized; False if it can't.
	 * @throws ConnectException
	 * @throws SocketTimeoutException
	 * @throws RepositoryServiceFault
	 * @throws NotAuthorisedFault
	 */
	public boolean canRecordBeRandomized(String identifier, String saml)
	throws ConnectException, SocketTimeoutException, RepositoryServiceFault, NotAuthorisedFault {

		try{
			return service.canRecordBeRandomized(identifier, saml);
		}
		catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return false;
		}
		catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}           
	}
	

	/********************** Sample Tracking *****************************/
	
	public ConfigInfo getSampleConfig(String projectCode, String saml)
			throws ConnectException, SocketTimeoutException,
			RepositoryServiceFault, NotAuthorisedFault {
		try {
			return service.getSampleConfig(projectCode, saml);
		} catch (AxisFault fault) {
			handleAxisFault(fault, LOG);
			return null;
		} catch (RemoteException ex) {
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}
	}

	public ParticipantInfo getSampleParticipant(String recordID, String saml)
			throws ConnectException, SocketTimeoutException,
			RepositoryServiceFault, NotAuthorisedFault {
		try {
			return service.getSampleParticipant(recordID, saml);
		} catch (AxisFault fault) {
			handleAxisFault(fault, LOG);
			return null;
		} catch (RemoteException ex) {
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}
	}

	public void saveSampleParticipant(ParticipantInfo participant, String saml)
			throws ConnectException, SocketTimeoutException,
			RepositoryServiceFault, NotAuthorisedFault {
		try {
			service.saveSampleParticipant(participant, saml);
		} catch (AxisFault fault) {
			handleAxisFault(fault, LOG);
		} catch (RemoteException ex) {
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}
	}
	
	public SampleInfo[] getSampleRevisions(long sampleID, String saml)
			throws ConnectException, SocketTimeoutException,
			RepositoryServiceFault, NotAuthorisedFault {
		try {
			return service.getSampleRevisions(sampleID, saml);
		} catch (AxisFault fault) {
			handleAxisFault(fault, LOG);
			return null;
		} catch (RemoteException ex) {
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}
	}

	public SampleInfo[] getSamples(String recordID, String saml)
			throws ConnectException, SocketTimeoutException,
			RepositoryServiceFault, NotAuthorisedFault {
		try {
			return service.getSamples(recordID, saml);
		} catch (AxisFault fault) {
			handleAxisFault(fault, LOG);
			return null;
		} catch (RemoteException ex) {
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}
	}

	public SampleInfo saveSample(SampleInfo sample, String saml)
			throws ConnectException, SocketTimeoutException,
			RepositoryServiceFault, NotAuthorisedFault {
		try {
			return service.saveSample(sample, saml);
		} catch (AxisFault fault) {
			handleAxisFault(fault, LOG);
			return null;
		} catch (RemoteException ex) {
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}
	}

	public void saveSampleConfig(ConfigInfo conf, String saml)
			throws ConnectException, SocketTimeoutException,
			RepositoryServiceFault, NotAuthorisedFault {
		try {
			service.saveSampleConfig(conf, saml);
		} catch (AxisFault fault) {
			handleAxisFault(fault, LOG);
		} catch (RemoteException ex) {
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}
	}	
	
	public long getNextSampleNumber(String projectCode, String saml)
			throws ConnectException, SocketTimeoutException,
			RepositoryServiceFault, NotAuthorisedFault {
		try {
			return service.getNextSampleNumber(projectCode, saml);
		} catch (AxisFault fault) {
			handleAxisFault(fault, LOG);
			return -1; // Should never be reached
		} catch (RemoteException ex) {
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}
	}

	/********************** End Sample Tracking *****************************/
	
	private Repository getService() {
		RepositoryServiceLocator locator = new RepositoryServiceLocator();
		Repository service = null;
		try{
			if ( null == this.url ){
				service = locator.getrepository();
			}
			else{
				service = locator.getrepository(url);
			}
		}
		catch(ServiceException ex){
			//this can only happen if the repository was built with
			//an incorrect URL
			throw new RuntimeException("Repository URL is invalid!", ex);
		}
		if ( this.timeout >= 0 ){
			RepositorySoapBindingStub stub  = (RepositorySoapBindingStub)service;
			stub.setTimeout(this.timeout);
		}
		return service;
	}

}