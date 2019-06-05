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

package org.psygrid.collection.entry.remote;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;

import javax.jnlp.BasicService;
import javax.jnlp.ServiceManager;
import javax.jnlp.UnavailableServiceException;
import javax.swing.event.EventListenerList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.event.ProgressEvent;
import org.psygrid.collection.entry.DocumentStatus;
import org.psygrid.collection.entry.EntryHelper;
import org.psygrid.collection.entry.event.CommitProgressListener;
import org.psygrid.collection.entry.event.StandardCodesEvent;
import org.psygrid.collection.entry.event.StandardCodesListener;
import org.psygrid.collection.entry.event.UpdateProgressListener;
import org.psygrid.collection.entry.persistence.ClockOffset;
import org.psygrid.collection.entry.persistence.ClockOffset.OffsetSource;
import org.psygrid.collection.entry.persistence.ConsentMap2;
import org.psygrid.collection.entry.persistence.DataSetSummary;
import org.psygrid.collection.entry.persistence.DatedProjectType;
import org.psygrid.collection.entry.persistence.ExternalIdMap;
import org.psygrid.collection.entry.persistence.IdentifierData;
import org.psygrid.collection.entry.persistence.IdentifiersList;
import org.psygrid.collection.entry.persistence.PersistenceData;
import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.collection.entry.persistence.RecordStatusMap2;
import org.psygrid.collection.entry.persistence.RecordsList;
import org.psygrid.collection.entry.persistence.RecordsListWrapper;
import org.psygrid.collection.entry.persistence.SecondaryIdentifierMap;
import org.psygrid.collection.entry.persistence.VersionMap;
import org.psygrid.collection.entry.security.DecryptionException;
import org.psygrid.collection.entry.security.EntrySAMLException;
import org.psygrid.collection.entry.security.SecurityManager;
import org.psygrid.common.identifier.IdentifierHelper;
import org.psygrid.common.identifier.InvalidIdentifierException;
import org.psygrid.common.proxy.ProxyAuthenticationMethods;
import org.psygrid.common.remote.RemoteManageable;
import org.psygrid.common.time.NtpClockSync;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.model.dto.extra.ConsentStatusResult;
import org.psygrid.data.reporting.GroupsNotAllowedException;
import org.psygrid.data.reporting.Report;
import org.psygrid.data.reporting.client.ReportsClient;
import org.psygrid.data.reporting.definition.IReport;
import org.psygrid.data.repository.DuplicateDocumentsFault;
import org.psygrid.data.repository.RepositoryInvalidIdentifierFault;
import org.psygrid.data.repository.RepositoryNoConsentFault;
import org.psygrid.data.repository.RepositoryNoSuchDatasetFault;
import org.psygrid.data.repository.RepositoryOutOfDateFault;
import org.psygrid.data.repository.RepositoryServiceFault;
import org.psygrid.data.repository.client.RepositoryClient;
import org.psygrid.data.sampletracking.ConfigInfo;
import org.psygrid.data.sampletracking.ParticipantInfo;
import org.psygrid.data.sampletracking.SampleInfo;
import org.psygrid.data.utils.security.NotAuthorisedFault;
import org.psygrid.data.utils.time.TimeOffset;
import org.psygrid.esl.model.IGroup;
import org.psygrid.esl.model.IProject;
import org.psygrid.esl.model.ISubject;
import org.psygrid.esl.model.hibernate.CustomEmailInfo;
import org.psygrid.esl.model.hibernate.Site;
import org.psygrid.esl.services.ESLDuplicateObjectFault;
import org.psygrid.esl.services.ESLOutOfDateFault;
import org.psygrid.esl.services.ESLServiceFault;
import org.psygrid.esl.services.ESLSubjectExistsException;
import org.psygrid.esl.services.RandomisationException;
import org.psygrid.esl.services.client.EslClient;
import org.psygrid.security.components.net.PsyGridClientSocketFactory;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.ProjectType;

public class RemoteManager implements RemoteManageable {

	private static final Log LOG = LogFactory.getLog(RemoteManager.class);

	private static final RemoteManager INSTANCE = new RemoteManager();
	private final RepositoryClient repositoryClient = new RepositoryClient(1200000); //20 minute timeout
	private final ReportsClient reportsClient = new ReportsClient();
	private final EslClient eslClient = new EslClient();


	/**
	 * The following two constants are only used if always online mode is disabled.
	 */
	private static final int MIN_IDENTIFIERS = 5;
	private static final int MAX_IDENTIFIERS = 10;

	private EventListenerList listenerList = new EventListenerList();

	/**
	 * If this is not null, then we do not use the security system to retrieve
	 * the list of projects. We simply retrieve these ones from the repository. 
	 */
	private List<ProjectType> hardCodedProjects;

	/**
	 * If this is not null, then we do not use the security system to load
	 * datasets. Instead, we load the dataset from this path instead.
	 * <p>
	 * Used for testing datasets when building them in the dataset designer.
	 */
	private String testDatasetPath;

	/**
	 * If this is not null, this is the file path of an XML file containing
	 * the standard codes.
	 * <p>
	 * Used for testing datasets when building them in the dataset designer.
	 */
	private String testStdcodePath;
	
	/**
	 * Store a list of datasets that have been patched and alert the user later
	 */
	private String patchedDataSets = new String();

	private RemoteManager()  {
		// Private constructor to enforce singleton pattern
	}

	public static RemoteManager getInstance() {
		return INSTANCE;
	}

	public String getTestDatasetPath() {
		return testDatasetPath;
	}

	public void setTestDatasetPath(String testDatasetPath) {
		this.testDatasetPath = testDatasetPath;
	}

	public String getTestStdcodePath() {
		return testStdcodePath;
	}
	
	public String getPatchedDataSets () {
		return patchedDataSets;
	}

	public void setTestStdcodePath(String testStdcodePath) {
		this.testStdcodePath = testStdcodePath;
	}

	public Report generateReport(IReport report, Record record) throws ConnectException, SocketTimeoutException, IOException, RemoteServiceFault, EntrySAMLException, NotAuthorisedFault, GroupsNotAllowedException {
		PersistenceManager pManager = PersistenceManager.getInstance();
		String saml;
		synchronized (pManager) {
			saml = getSAMLAssertion(pManager.getData(), record.getDataSet());
		}
		try {
			return reportsClient.generateReport(report.getId(), record.getId(), saml);
		} catch (RepositoryServiceFault e) {
			throw new RemoteServiceFault(e);
		}
	}

	public List<IReport> getReports(DataSet dataSet) throws ConnectException, SocketTimeoutException,
	IOException, RemoteServiceFault, EntrySAMLException, 
	NotAuthorisedFault {
		PersistenceManager pManager = PersistenceManager.getInstance();
		String saml;
		synchronized (pManager) {
			saml = getSAMLAssertion(pManager.getData(), dataSet);
		}
		try {
			return reportsClient.getReportsByDataSet(dataSet.getId(), saml);
		} catch (RepositoryServiceFault e) {
			throw new RemoteServiceFault(e);
		}
	}

	/**
	 * Uses <code>hardCodedProjects</code> as the list of projects instead of
	 * retrieving the list of available projects from the security system.
	 * All the other calls to the security system are replaced with something
	 * as close to a no-op as possible.<p>
	 * 
	 * Note: This method should only be used for testing and with an unsecured
	 * repository. If the repository is secured, all operations are likely to
	 * fail.
	 *  
	 * @param hardCodedProjects
	 */
	public void setHardCodedProjects(List<ProjectType> hardCodedProjects) {
		this.hardCodedProjects = hardCodedProjects;
	}

	/**
	 * Return whether hard-coded projects have been set up
	 * 
	 * @return Boolean, True is hard-coded projects have been set up. False
	 * otherwise.
	 */
	public boolean isHardCodedProjects(){
		return ( null != hardCodedProjects && hardCodedProjects.size() > 0 );
	}

	/**
	 * Return whether a test dataset path has been defined for test/preview 
	 * mode.
	 * 
	 * @return Boolean, True if a test dataset path has been defined.
	 * False otherwise.
	 */
	public boolean isTestDataset(){
		return ( null != testDatasetPath );
	}

	public void markResponseAsInvalid(Response response, String annotation)
	throws ConnectException, SocketTimeoutException, RemoteServiceFault, NotAuthorisedFault, 
	IOException, EntrySAMLException {
		Record record = response.getRecord();
		PersistenceManager pManager = PersistenceManager.getInstance();
		String saml = null;
		synchronized (pManager) {
			saml = getSAMLAssertion(pManager.getData(), record.getDataSet());
		}
		try {
			repositoryClient.markResponseAsInvalid(response.getId(), annotation, saml);
		}
		catch (RepositoryServiceFault rsf) {
			throw new RemoteServiceFault(rsf);
		}
	}

	public void markResponseAsValid(Response response) throws ConnectException, SocketTimeoutException,
	NotAuthorisedFault, IOException, RemoteServiceFault, 
	EntrySAMLException  {

		Record record = response.getRecord();
		PersistenceManager pManager = PersistenceManager.getInstance();
		String saml = null;
		synchronized (pManager) {
			saml = getSAMLAssertion(pManager.getData(), record.getDataSet());
		}
		try {
			repositoryClient.markResponseAsValid(response.getId(), saml);
		} catch (RepositoryServiceFault e) {
			throw new RemoteServiceFault(e);  
		}
	}

	public void changeRecordStatus(Record record, Status status)
	throws ConnectException, SocketTimeoutException, NotAuthorisedFault, IOException, 
	RemoteServiceFault, EntrySAMLException {
		PersistenceManager pManager = PersistenceManager.getInstance();
		String saml = null;
		synchronized (pManager) {
			saml = getSAMLAssertion(pManager.getData(),
					record.getIdentifier().getProjectPrefix());
		}
		PersistenceManager.getInstance().updateRecordStatus(record, status);
		try {
			repositoryClient.changeRecordStatus(record.getIdentifier().getIdentifier(), status.getId(), true, saml);
		} catch (Exception e) {
			//Ignore the error (could be a new record or offline), the status will be changed locally only for now.
		} 
	}

	public void changeRecordStatus(String identifier, Status  status)
	throws ConnectException, SocketTimeoutException, NotAuthorisedFault, IOException, 
	RemoteServiceFault, EntrySAMLException, InvalidIdentifierException {
		PersistenceManager pManager = PersistenceManager.getInstance();
		String saml = null;
		synchronized (pManager) {
			saml = getSAMLAssertion(pManager.getData(),
					IdentifierHelper.getProjectCodeFromIdentifier(identifier));
		}
		try {
			repositoryClient.changeRecordStatus(identifier, status.getId(), true, saml);
		} catch (RepositoryServiceFault e) {
			throw new RemoteServiceFault(e);
		} 
	}

	public void addConsent(Record record, ConsentForm cf, String location)
	throws ConnectException, SocketTimeoutException, NotAuthorisedFault, IOException, 
	RemoteServiceFault, EntrySAMLException {
		PersistenceManager pManager = PersistenceManager.getInstance();
		String saml = null;
		try {
			synchronized (pManager) {
				saml = getSAMLAssertion(pManager.getData(),
						record.getDataSet());
			}
			if (record.getId() != null) {
				repositoryClient.addConsent(record.getId(), cf.getId(), location, saml);
			}
		} catch (RepositoryServiceFault e) {
			throw new RemoteServiceFault(e);
		} 
	}

	public void withdrawConsent(Record record, ConsentForm cf, String reason)
	throws ConnectException, SocketTimeoutException, NotAuthorisedFault, IOException, 
	RemoteServiceFault, EntrySAMLException {
		PersistenceManager pManager = PersistenceManager.getInstance();
		String saml = null;
		synchronized (pManager) {
			saml = getSAMLAssertion(pManager.getData(),
					record.getDataSet());
		}
		try {
			if (record.getId() != null) {
				repositoryClient.withdrawConsent(record.getId(), cf.getId(), reason, saml);
			}
		} catch (RepositoryServiceFault e) {
			throw new RemoteServiceFault(e);
		} 
	}

	/**
	 * Perform a "dry run" of removing consent from a record in the database.
	 * <p>
	 * The returned list gives the names of the document instances that will
	 * be deleted if the consent were withdrawn for real.
	 * 
	 * @param record The record
	 * @param cf The consent form for which consent is being withdrawn.
	 * @param reason The reason.
	 * @return List of documents that would be deleted were this action to
	 * be performed.
	 * @throws ConnectException
	 * @throws SocketTimeoutException
	 * @throws NotAuthorisedFault
	 * @throws IOException
	 * @throws RemoteServiceFault
	 * @throws EntrySAMLException
	 */
	public List<String> withdrawConsentDryRun(Record record, ConsentForm cf, String reason)
	throws ConnectException, SocketTimeoutException, NotAuthorisedFault, IOException, 
	RemoteServiceFault, EntrySAMLException {
		PersistenceManager pManager = PersistenceManager.getInstance();
		String saml = null;
		synchronized (pManager) {
			saml = getSAMLAssertion(pManager.getData(),
					record.getDataSet());
		}
		try {
			if (record.getId() != null) {
				return Arrays.asList(repositoryClient.withdrawConsentDryRun(record.getId(), cf.getId(), reason, saml));
			}
			else{
				return new ArrayList<String>();
			}
		} catch (RepositoryServiceFault e) {
			throw new RemoteServiceFault(e);
		} 
	}

	public Record getCompleteRecord(Record recordSummary) throws IOException,
	NotAuthorisedFault, RemoteServiceFault, EntrySAMLException, 
	ConnectException, SocketTimeoutException {
		PersistenceManager pManager = PersistenceManager.getInstance();
		String saml = null;
		DataSet dataSet = null;
		synchronized (pManager) {
			dataSet = pManager.getData().getCompleteDataSet(
					recordSummary.getIdentifier().getProjectPrefix());
			saml = getSAMLAssertion(pManager.getData(), dataSet);
		}
		try {
			Record record = repositoryClient.getRecordSummary(dataSet, recordSummary.getIdentifier().getIdentifier(), saml);
			return repositoryClient.getRecord(dataSet, record.getId(), saml);
		} catch (RepositoryServiceFault e) {
			throw new RemoteServiceFault(e);
		}
	}

	public Record getCompleteRecord(String identifier) throws IOException,
	NotAuthorisedFault, RemoteServiceFault, EntrySAMLException, 
	ConnectException, SocketTimeoutException, InvalidIdentifierException {
		PersistenceManager pManager = PersistenceManager.getInstance();
		String saml = null;
		DataSet dataSet = null;
		synchronized (pManager) {
			dataSet = pManager.getData().getCompleteDataSet(IdentifierHelper.getProjectCodeFromIdentifier(identifier));
			saml = getSAMLAssertion(pManager.getData(), dataSet);
		}
		try {
			return repositoryClient.getRecord(dataSet, identifier, saml);
		} catch (RepositoryServiceFault e) {
			throw new RemoteServiceFault(e);
		}
	}

	public Record getRecordSummary(String identifier) throws IOException,
	NotAuthorisedFault, RemoteServiceFault, EntrySAMLException, 
	ConnectException, SocketTimeoutException, InvalidIdentifierException {
		PersistenceManager pManager = PersistenceManager.getInstance();
		String saml = null;
		DataSet dataSet = null;
		synchronized (pManager) {
			dataSet = pManager.getData().getCompleteDataSet(IdentifierHelper.getProjectCodeFromIdentifier(identifier));
			saml = getSAMLAssertion(pManager.getData(), dataSet);
		}
		try {
			return repositoryClient.getRecordSummary(dataSet, identifier, saml);
		} catch (RepositoryServiceFault e) {
			throw new RemoteServiceFault(e);
		}
	}   

	public Record getRecordSingleDocument(DocumentInstance docInst) throws IOException,
	NotAuthorisedFault, RemoteServiceFault, EntrySAMLException, 
	ConnectException, SocketTimeoutException {
		PersistenceManager pManager = PersistenceManager.getInstance();
		String saml = null;
		DataSet dataSet = null;
		synchronized (pManager) {
			dataSet = pManager.getData().getCompleteDataSet(
					docInst.getRecord().getIdentifier().getProjectPrefix());
			saml = getSAMLAssertion(pManager.getData(), dataSet);
		}
		try {
			return repositoryClient.getRecordSingleDocument(docInst.getRecord().getId(), docInst.getId(), dataSet, saml);
		} catch (RepositoryServiceFault e) {
			throw new RemoteServiceFault(e);
		}
	}

	public Record getRecordSingleDocumentFromOccurrence(Record record, DocumentOccurrence docOcc) throws IOException,
	NotAuthorisedFault, RemoteServiceFault, EntrySAMLException, 
	ConnectException, SocketTimeoutException {
		PersistenceManager pManager = PersistenceManager.getInstance();
		String saml = null;
		DataSet dataSet = null;
		synchronized (pManager) {
			dataSet = pManager.getData().getCompleteDataSet(
					record.getIdentifier().getProjectPrefix());
			saml = getSAMLAssertion(pManager.getData(), dataSet);
		}
		try {
			return repositoryClient.getRecordSingleDocumentForOccurrence(record.getIdentifier().getIdentifier(), docOcc.getId(), dataSet, saml);
		} catch (RepositoryServiceFault e) {
			throw new RemoteServiceFault(e);
		}
	}

	public List<String> getRecordSummaries(DatedProjectType project, 
			DocumentStatus status) throws NotAuthorisedFault, IOException, 
			RemoteServiceFault, EntrySAMLException, ConnectException, SocketTimeoutException    {

		String saml = getSAMLAssertion(project);
		List<String> groups = new ArrayList<String>();
		for (GroupType group : getUsersGroupsInProject(project)) {
			groups.add(group.getIdCode());
		}
		try {
			return repositoryClient.getRecordsByGroupsAndDocStatus(project.getIdCode(), 
					groups, status.toString(), saml);
		}
		catch (RepositoryServiceFault e) {
			throw new RemoteServiceFault(e);
		}
	}

	private List<String> getRecordSummaries(DatedProjectType project) 
	throws IOException,NotAuthorisedFault, RemoteServiceFault, 
	EntrySAMLException, ConnectException, SocketTimeoutException    {
		String saml = getSAMLAssertion(project);
		List<String> groups = new ArrayList<String>();
		for (GroupType group : getUsersGroupsInProject(project)) {
			groups.add(group.getIdCode());
		}
		try {
			return repositoryClient.getRecordsByGroups(project.getIdCode(), groups, saml);
		} catch (RepositoryServiceFault e) {
			throw new RemoteServiceFault(e);
		}
	}

	public List<String> getRecordSummaries() throws IOException, 
	NotAuthorisedFault, RemoteServiceFault, EntrySAMLException, 
	ConnectException, SocketTimeoutException {
		List<String> records = new ArrayList<String>();
		PersistenceManager pManager = PersistenceManager.getInstance();
		Map<DatedProjectType, NotAuthorisedFault> errors = new HashMap<DatedProjectType, NotAuthorisedFault>();
		synchronized (pManager) {
			PersistenceData pData = pManager.getData();
			List<DatedProjectType> projects = pData.getProjects();
			for (DatedProjectType project : projects) {
				try{
					if (!project.isVirtual()) {
						records.addAll(getRecordSummaries(project));
					}
				}
				catch(NotAuthorisedFault naf){
					LOG.error("NotAuthorisedFault during getRecordSummaries for project "+project.getIdCode(), naf);
					errors.put(project, naf);
				}
			}
			if ( !pData.getProjects().isEmpty() && errors.size() == pData.getProjects().size() ){
				//an error was returned for all projects - throw the error
				//for the first project.
				throw errors.get(projects.get(0));
			}
		}
		return records;
	}

	public List<String> getLinkableRecords() throws IOException, 
	NotAuthorisedFault, RemoteServiceFault, EntrySAMLException, 
	ConnectException, SocketTimeoutException {
		List<String> records = new ArrayList<String>();
		PersistenceManager pManager = PersistenceManager.getInstance();
		synchronized (pManager) {
			PersistenceData pData = pManager.getData();
			List<DatedProjectType> projects = pData.getProjects();
			Map<DatedProjectType, NotAuthorisedFault> errors = new HashMap<DatedProjectType, NotAuthorisedFault>();
			for (DatedProjectType project : projects) {
				try{
					if (!project.isVirtual()) {
						records.addAll(getLinkableRecords(project));
					}
				}
				catch(NotAuthorisedFault naf){
					LOG.error("NotAuthorisedFault during getRecordSummaries for project "+project.getIdCode(), naf);
					errors.put(project, naf);
				}
			}
			if ( !pData.getProjects().isEmpty() && errors.size() == pData.getProjects().size() ){
				//an error was returned for all projects - throw the error
				//for the first project.
				throw errors.get(projects.get(0));
			}
		}
		return records;
	}

	private List<String> getLinkableRecords(DatedProjectType project) 
	throws IOException,NotAuthorisedFault, RemoteServiceFault, 
	EntrySAMLException, ConnectException, SocketTimeoutException    {
		String saml = getSAMLAssertion(project);
		List<String> groups = new ArrayList<String>();
		for (GroupType group : getUsersGroupsInProject(project)) {
			groups.add(group.getIdCode());
		}
		List<String> records = null;
		try {
			records = repositoryClient.getLinkableRecords(project.getIdCode(), groups, saml);
		} catch (RepositoryServiceFault e) {
			throw new RemoteServiceFault(e);
		}
		return records;
	}

	public List<String> getLinkedRecords() throws IOException, 
	NotAuthorisedFault, RemoteServiceFault, EntrySAMLException, 
	ConnectException, SocketTimeoutException {
		List<String> records = new ArrayList<String>();
		PersistenceManager pManager = PersistenceManager.getInstance();
		synchronized (pManager) {
			PersistenceData pData = pManager.getData();
			List<DatedProjectType> projects = pData.getProjects();
			Map<DatedProjectType, NotAuthorisedFault> errors = new HashMap<DatedProjectType, NotAuthorisedFault>();
			for (DatedProjectType project : projects) {
				try{
					if (!project.isVirtual()) {
						records.addAll(getLinkedRecords(project));
					}
				}
				catch(NotAuthorisedFault naf){
					LOG.error("NotAuthorisedFault during getRecordSummaries for project "+project.getIdCode(), naf);
					errors.put(project, naf);
				}
			}
			if ( !pData.getProjects().isEmpty() && errors.size() == pData.getProjects().size() ){
				//an error was returned for all projects - throw the error
				//for the first project.
				throw errors.get(projects.get(0));
			}
		}
		return records;
	}

	private List<String> getLinkedRecords(DatedProjectType project) 
	throws IOException,NotAuthorisedFault, RemoteServiceFault, 
	EntrySAMLException, ConnectException, SocketTimeoutException    {
		String saml = getSAMLAssertion(project);
		List<String> groups = new ArrayList<String>();
		for (GroupType group : getUsersGroupsInProject(project)) {
			groups.add(group.getIdCode());
		}
		List<String> records = null;
		try {
			records = repositoryClient.getLinkedRecords(project.getIdCode(), groups, saml);
		} catch (RepositoryServiceFault e) {
			throw new RemoteServiceFault(e);
		}
		return records;
	}

	public List<String> getRecordSummaries(DocumentStatus docStatus) throws 
	NotAuthorisedFault, IOException, RemoteServiceFault, 
	EntrySAMLException, ConnectException, SocketTimeoutException {
		//List<IRecord> records = new ArrayList<IRecord>();
		List<String> records = new ArrayList<String>();
		PersistenceManager pManager = PersistenceManager.getInstance();
		synchronized (pManager) {
			PersistenceData pData = pManager.getData();
			List<DatedProjectType> projects = pData.getProjects();
			Map<DatedProjectType, NotAuthorisedFault> errors = new HashMap<DatedProjectType, NotAuthorisedFault>();
			for (DatedProjectType project : projects) {				
				try {
					if (!project.isVirtual()) {
						records.addAll(getRecordSummaries(project, docStatus));
					}
				}
				catch(NotAuthorisedFault naf){
					LOG.error("NotAuthorisedFault during getRecordSummaries for project "+project.getIdCode(), naf);
					errors.put(project, naf);
				}
			}
			if ( !pData.getProjects().isEmpty() && errors.size() == pData.getProjects().size() ){
				//an error was returned for all projects - throw the error
				//for the first project.
				throw errors.get(projects.get(0));
			}
		}
		return records;
	}

	public void changeDocumentInstanceStatus(DocumentInstance docInstance,
			DocumentStatus newStatus) throws RemoteServiceFault, 
			NotAuthorisedFault, IOException, EntrySAMLException, ConnectException, SocketTimeoutException {
		if (docInstance.getId() == null)
			throw new IllegalArgumentException("docInstance id should not be null"); //$NON-NLS-1$

		PersistenceManager pManager = PersistenceManager.getInstance();
		String saml = null;
		Document document = docInstance.getOccurrence().getDocument();
		synchronized (pManager) {
			saml = getSAMLAssertion(pManager.getData(), document.getDataSet());
		}
		Status  status = EntryHelper.getStatus(document,  newStatus);

		if (status == null) {
			throw new IllegalArgumentException("document is missing a required status: " +  //$NON-NLS-1$
					newStatus);
		}
		try {
			repositoryClient.changeStatus(docInstance.getId(), status.getId(), saml);
		}
		catch (RepositoryServiceFault e) {
			throw new RemoteServiceFault(e);
		}
	}

	/**
	 * Change the status of the document instance for the given document 
	 * occurrence in the record with the given identifier to the specified new
	 * status.
	 * 
	 * @param identifier The identifier of the record.
	 * @param docOcc The document occurrence
	 * @param newStatus The new status
	 * @throws RemoteServiceFault
	 * @throws NotAuthorisedFault
	 * @throws IOException
	 * @throws EntrySAMLException
	 * @throws ConnectException
	 * @throws SocketTimeoutException
	 */
	public void changeDocumentInstanceStatus(String identifier, DocumentOccurrence docOcc,
			DocumentStatus newStatus) throws RemoteServiceFault, 
			NotAuthorisedFault, IOException, EntrySAMLException, ConnectException, SocketTimeoutException {

		PersistenceManager pManager = PersistenceManager.getInstance();
		String saml = null;
		synchronized (pManager) {
			saml = getSAMLAssertion(pManager.getData(), docOcc.getDocument().getDataSet());
		}
		Status  status = EntryHelper.getStatus(docOcc.getDocument(),  newStatus);

		if (status == null) {
			throw new IllegalArgumentException("document is missing a required status: " +  //$NON-NLS-1$
					newStatus);
		}
		try {
			repositoryClient.changeDocumentStatus(identifier, docOcc.getId(), status.getId(), saml);
		}
		catch (RepositoryServiceFault e) {
			throw new RemoteServiceFault(e);
		}
	}

	public List<Record> getRecordsByStatus(DataSet dataSet, Status  status)
	throws NotAuthorisedFault, IOException, RemoteServiceFault, 
	EntrySAMLException, ConnectException, SocketTimeoutException    {
		PersistenceManager pManager = PersistenceManager.getInstance();
		String saml;
		synchronized (pManager) {
			saml = getSAMLAssertion(pManager.getData(), dataSet);
		}
		try {
			return repositoryClient.getRecordsByStatus(dataSet.getId(), status.getId(), saml);
		} catch (RepositoryServiceFault e) {
			throw new RemoteServiceFault(e);
		}
	}

	public Record getDocumentsByStatus(DataSet dataSet, Identifier identifier,
			DocumentStatus documentStatus) throws NotAuthorisedFault, IOException,
			RemoteServiceFault, EntrySAMLException, ConnectException, SocketTimeoutException    {

		return getDocumentsByStatus(dataSet, identifier.getIdentifier(), 
				documentStatus);
	}

	public Record getDocumentsByStatus(DataSet dataSet, String identifier,
			DocumentStatus documentStatus) throws NotAuthorisedFault, IOException,
			RemoteServiceFault, EntrySAMLException, ConnectException, SocketTimeoutException    {
		PersistenceManager pManager = PersistenceManager.getInstance();
		String saml;
		synchronized (pManager) {
			saml = getSAMLAssertion(pManager.getData(), dataSet);
		}
		try {
			return repositoryClient.getRecordsDocumentsByStatus(dataSet, identifier, 
					documentStatus.toString(), saml);
		} catch (RepositoryServiceFault e) {
			throw new RemoteServiceFault(e);
		}
	}

	public boolean isConnectionAvailable(){
		return isConnectionAvailable(true);
	}

	public boolean isConnectionAvailable(boolean log) {
		if(isWebstartOffline()){
			return offline(log);
		}
		try {
			InetAddress.getByName("localhost");
		} catch (Exception e) {
			if (log && LOG.isInfoEnabled()) {
				LOG.info(e.getMessage()); 
				e.printStackTrace();
			}
		}
		try {
			refreshKey(true);
			if ( isHardCodedProjects() ){
				//Have to use the repository to check for connectivity
				//if we are using "hard coded projects" i.e. we are not
				//using any security system
				repositoryClient.getVersion();
			}
			else{
				SecurityManager.getInstance().getVersion();
			}
		} catch (ConnectException e) {
			return offline(log);
		} catch (IOException ioe) {
			LOG.info("refreshKey failure", ioe);
			return offline(log);
		} catch (RemoteServiceFault rsf) {
			LOG.info("refreshKey failure", rsf);
			return offline(log);
		}
		return online(log);
	}

	public boolean isEslConnectionAvailable() {
		if(isWebstartOffline()){
			return eslOffline();
		}
		try {
			InetAddress.getByName("localhost");
		} catch (Exception e) {
			if (LOG.isInfoEnabled()) {
				LOG.info(e.getMessage()); 
				e.printStackTrace();
			}
		}
		try {
			refreshKey(true);
			eslClient.getVersion();
		} catch (ConnectException e) {
			return eslOffline();
		} catch (IOException ioe) {
			LOG.info("refreshKey failure", ioe);
			return eslOffline();
		} catch (RemoteServiceFault rsf) {
			LOG.info("refreshKey failure", rsf);
			return eslOffline();
		}
		return eslOnline();
	}

	private boolean online(boolean log) {
		if (log && LOG.isInfoEnabled()) {
			LOG.info("Application is online."); //$NON-NLS-1$
		}
		return true;
	}

	private boolean offline(boolean log) {
		if (log && LOG.isInfoEnabled()) {
			LOG.info("Application is offline."); //$NON-NLS-1$
		}
		return false;
	}

	private boolean eslOnline() {
		if (LOG.isInfoEnabled()) {
			LOG.info("ESL is online."); //$NON-NLS-1$
		}
		return true;
	}

	private boolean eslOffline() {
		if (LOG.isInfoEnabled()) {
			LOG.info("ESL is offline."); //$NON-NLS-1$
		}
		return false;
	}

	private boolean isWebstartOffline() {
		boolean answer = false;
		try {
			// Lookup the javax.jnlp.BasicService object
			BasicService bs = (BasicService) ServiceManager
			.lookup("javax.jnlp.BasicService"); //$NON-NLS-1$
			if (bs.isOffline()) {
				if (LOG.isInfoEnabled()) {
					LOG.debug("Webstart is offline.");
				}
				answer = true;
			}
		} catch (UnavailableServiceException ue) {
			//do nothing - this will happen when running app from Eclipse	
		}
		return answer;
	}

	private void updateStandardCodes(String attributes) throws ConnectException, SocketTimeoutException,
	IOException   {

		List<StandardCode> stdCodes = repositoryClient.getStandardCodes(attributes);
		PersistenceManager.getInstance().saveStandardCodes(stdCodes);
		fireStandardCodesEvent(new StandardCodesEvent(this, 
				Collections.unmodifiableList(stdCodes)));
	}

	private void fireStandardCodesEvent(StandardCodesEvent event) {
		Object[] listeners = listenerList.getListenerList();

		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == StandardCodesListener.class) {
				((StandardCodesListener) listeners[i + 1]).standardCodesUpdated(
						event);
			}
		}
	}

	public void addStandardCodesListener(StandardCodesListener listener) {
		listenerList.add(StandardCodesListener.class, listener);
	}

	public void removeStandardCodesListener(StandardCodesListener listener) {
		listenerList.remove(StandardCodesListener.class, listener);
	}

	public void addUpdateProgressListener(UpdateProgressListener listener) {
		listenerList.add(UpdateProgressListener.class, listener);
	}

	public void removeUpdateProgressListener(UpdateProgressListener listener) {
		listenerList.remove(UpdateProgressListener.class, listener);
	}

	public void addCommitProgressListener(CommitProgressListener listener) {
		listenerList.add(CommitProgressListener.class, listener);
	}

	public void removeCommitProgressListener(CommitProgressListener listener) {
		listenerList.remove(CommitProgressListener.class, listener);
	}

	protected void fireUpdateProgressStarted(ProgressEvent event) {
		Object[] listeners = listenerList.getListenerList();

		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == UpdateProgressListener.class) {
				((UpdateProgressListener) listeners[i + 1]).progressStarted(event);
			}
		}
	}

	protected void fireUpdateProgressEnded(ProgressEvent event) {
		Object[] listeners = listenerList.getListenerList();

		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == UpdateProgressListener.class) {
				((UpdateProgressListener) listeners[i + 1]).progressEnded(event);
			}
		}
	}

	protected void fireUpdateProgressIncremented(ProgressEvent event) {
		Object[] listeners = listenerList.getListenerList();

		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == UpdateProgressListener.class) {
				((UpdateProgressListener) listeners[i + 1]).progressIncremented(event);
			}
		}
	}

	protected void fireCommitProgressStarted(ProgressEvent event) {
		Object[] listeners = listenerList.getListenerList();

		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == CommitProgressListener.class) {
				((CommitProgressListener) listeners[i + 1]).progressStarted(event);
			}
		}
	}

	protected void fireCommitProgressEnded(ProgressEvent event) {
		Object[] listeners = listenerList.getListenerList();

		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == CommitProgressListener.class) {
				((CommitProgressListener) listeners[i + 1]).progressEnded(event);
			}
		}
	}

	protected void fireCommitProgressIncremented(ProgressEvent event) {
		Object[] listeners = listenerList.getListenerList();

		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == CommitProgressListener.class) {
				((CommitProgressListener) listeners[i + 1]).progressIncremented(event);
			}
		}
	}

	/**
	 * Commits a IDocumentInstance that is held in memory. This method should
	 * only be called for IDocumentInstances that have not been saved to disk.
	 */
	public void commit(DocumentInstance docInstance, boolean isComplete)
	throws ConnectException, SocketTimeoutException, IOException, RemoteServiceFault, 
	EntrySAMLException, NotAuthorisedFault, RepositoryOutOfDateFault, 
	RepositoryNoConsentFault, RepositoryInvalidIdentifierFault, 
	org.psygrid.data.repository.transformer.TransformerFault, DuplicateDocumentsFault {
		Record record = docInstance.getRecord();
		List<DocumentInstance> documentInstances = EntryHelper.getDocumentInstances(record);
		documentInstances.remove(docInstance);
		fireCommitProgressStarted(new ProgressEvent(this));
		/* 
		 * We detach all document instances apart from the one we're submitting
		 * and we re-attach later. Not sure if this is the best way, but I
		 * was getting a few exceptions when I tried to create a new Record.
		 */
		for (DocumentInstance instance : documentInstances) {
			record.detachDocumentInstance(instance);
		}
		try {
			synchronized (PersistenceManager.getInstance()) {
				commit(PersistenceManager.getInstance().getData(), record, isComplete, false);				
			}
		} finally {
			fireCommitProgressEnded(new ProgressEvent(this));
			for (DocumentInstance instance : documentInstances)
				record.addDocumentInstance(instance);
		}
	}

	/**
	 * Commit a record held in memory. The record is expected to be empty i.e.
	 * contain no document instances.
	 * 
	 * @param record
	 * @throws ConnectException
	 * @throws SocketTimeoutException
	 * @throws IOException
	 * @throws RemoteServiceFault
	 * @throws EntrySAMLException
	 * @throws NotAuthorisedFault
	 * @throws RepositoryOutOfDateFault
	 * @throws RepositoryNoConsentFault
	 * @throws RepositoryInvalidIdentifierFault
	 * @throws org.psygrid.data.repository.transformer.TransformerFault
	 */
	public void commit(Record record) throws ConnectException, SocketTimeoutException,
	IOException, RemoteServiceFault, EntrySAMLException, NotAuthorisedFault, 
	RepositoryOutOfDateFault, RepositoryNoConsentFault, RepositoryInvalidIdentifierFault, 
	org.psygrid.data.repository.transformer.TransformerFault, DuplicateDocumentsFault {
		fireCommitProgressStarted(new ProgressEvent(this));
		try {
			PersistenceManager pManager = PersistenceManager.getInstance();
			synchronized (pManager) {
				DuplicateDocumentsFault ddf = null;
				try{
					commit(pManager.getData(), record, true, true);
				}
				catch(DuplicateDocumentsFault ex){
					//we don't want a duplicate documents exception aborting execution
					//so we catch it here and throw it on once everything is done
					ddf = ex;
				}
				if ( pManager.getConsentMap().addRecordNoOverwrite(
						record.getIdentifier().getIdentifier(), 
						record.getAllConsents()) ){
					pManager.saveConsentMap();
				}
				if ( pManager.getRecordStatusMap().addRecordNoOverwrite(
						record.getIdentifier().getIdentifier(),
						record.getStatus()) ){
					pManager.saveRecordStatusMap();
				}
				if ( record.getDataSet().isExternalIdUsed() ){
					if ( pManager.getExternalIdMap().addNoOverwrite(
							record.getIdentifier().getIdentifier(),
							record.getExternalIdentifier()) ){
						pManager.saveExternalIdMap();
					}
				}

				if ( null != ddf ){
					throw ddf;
				}
				
			}
		} finally {
			fireCommitProgressEnded(new ProgressEvent(this));
		}

	}

	public void commit(RecordsListWrapper.Item itemToCommit) throws ConnectException, SocketTimeoutException,
	IOException, RepositoryOutOfDateFault, org.psygrid.data.repository.transformer.TransformerFault,
	NotAuthorisedFault, DecryptionException, RepositoryNoConsentFault,
	RepositoryInvalidIdentifierFault, RemoteServiceFault,
	EntrySAMLException, DuplicateDocumentsFault {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Entering commit()"); 
		}
		try {
			fireCommitProgressStarted(new ProgressEvent(this));

			PersistenceManager pManager = PersistenceManager.getInstance();
			synchronized (pManager) {

				RecordsList recordsList = pManager.getRecordsList();
				PersistenceData pData = pManager.getData();
				Record record = pManager.loadRecord(itemToCommit);
				DuplicateDocumentsFault ddf = null;
				try{
					commit(pData, record,
							itemToCommit.isReadyToCommit(), true);
				}
				catch (DuplicateDocumentsFault ex){
					//we don't want a duplicate documents exception aborting execution
					//so we catch it here and throw it on once everything is done
					ddf = ex;
				}
				if ( LOG.isInfoEnabled() ){
					LOG.info("Commit: deleting record...");
				}
				pManager.deleteRecord(itemToCommit);
				if ( LOG.isInfoEnabled() ){
					LOG.info("Commit: removing item from record list...");
				}
				recordsList.removeItem(itemToCommit);
				if ( LOG.isInfoEnabled() ){
					LOG.info("Commit: saving records list...");
				}
				pManager.saveRecordsList();
				Identifier identifier = record.getIdentifier();
				if (LOG.isInfoEnabled()) {
					LOG.info("Record committed, identifier: " + 
							identifier.getIdentifier());
				}

				if ( null != ddf ){
					throw ddf;
				}
			}
		} finally {
			fireCommitProgressEnded(new ProgressEvent(this));
		}
	}

	/**
	 * Commit a record to the central data repository.
	 * <p>
	 * The process is:
	 * <ul>
	 * <li>Save the record to the repository (handling duplicate documents
	 * if found).</li>
	 * <li>(If appropriate) change the statuses of the documents being saved to Complete</li>
	 * <li>Update the local map of document statuses</li>
	 * <li>(If appropriate) synchronize the document statuses of the secondary
	 * record with those in its primary.</li>
	 * <li>(If appropriate) update the status of the record in the repository</li>
	 * </ul>
	 * 
	 * @param pData Persistence data (used to get SAML assertion)
	 * @param record The record to commit
	 * @param isComplete True if the record represents complete documents; False
	 * otherwise.
	 * @param isLocal True if the record has been stored locally; False if it
	 * is being saved straight to the repository.
	 * @return
	 * @throws IOException
	 * @throws RemoteServiceFault
	 * @throws EntrySAMLException
	 * @throws NotAuthorisedFault
	 * @throws ConnectException
	 * @throws SocketTimeoutException
	 * @throws RepositoryOutOfDateFault
	 * @throws RepositoryNoConsentFault
	 * @throws RepositoryInvalidIdentifierFault
	 * @throws org.psygrid.data.repository.transformer.TransformerFault
	 * @throws DuplicateDocumentsFault
	 */
	public void commit(PersistenceData pData,
			Record record, boolean isComplete, boolean isLocal) throws IOException,
			RemoteServiceFault, EntrySAMLException, NotAuthorisedFault,
			ConnectException, SocketTimeoutException, RepositoryOutOfDateFault,
			RepositoryNoConsentFault, RepositoryInvalidIdentifierFault,
			org.psygrid.data.repository.transformer.TransformerFault, DuplicateDocumentsFault {
		Long[] discardedDocuments = new Long[0];
		String saml = getSAMLAssertion(pData, record.getDataSet());
		DuplicateDocumentsFault ddf = null;
		
		//Save the record to the repository
		try {
			if ( LOG.isInfoEnabled() ){
				LOG.info("Commit: saving the record...");
			}
			
			repositoryClient.saveRecord(record, false, saml);
		}
		catch (DuplicateDocumentsFault ex){
			//Duplicate documents fault received - we record this, get the list of
			//documents in the record that were discarded because they were
			//duplicates, then save again specifying the flag to ignore duplicates
			//and just discard them
			ddf = ex;
			if ( LOG.isInfoEnabled() ){
				LOG.info("Commit: duplicate documents found...");
			}
			discardedDocuments = ex.getDiscards();
			try{
				if ( LOG.isInfoEnabled() ){
					LOG.info("Commit: saving the record again...");
				}
				repositoryClient.saveRecord(record, true, saml);
			}
			catch (DuplicateDocumentsFault e){
				//do nothing - this exception cannot be thrown if discardDuplicates=true
			}
			catch(RepositoryServiceFault e){
				throw new RemoteServiceFault(e);
			}
		}
		catch (RepositoryServiceFault e) {
			throw new RemoteServiceFault(e);
		}
		
		//If saving as complete call the repository again to change the doc
		//statuses. Also need to store locally the document statuses
		List<Long> notChanged = new ArrayList<Long>();
		if (isComplete) {
			if ( LOG.isInfoEnabled() ){
				LOG.info("Commit: changing remote document status(es)...");
			}
			notChanged = changeCompletedDocInstancesStatus(record, discardedDocuments);
		}
		if ( LOG.isInfoEnabled() ){
			LOG.info("Commit: changing local document status...");
		}
		PersistenceManager.getInstance().changeLocalDocInstancesStatus(record, discardedDocuments, isComplete, notChanged);
		
		//If this is a secondary record then we make sure that the document
		//statuses are synchronized with their equivalents in the primary record
		if ( null != record.getPrimaryIdentifier() ){
			try{
				if ( LOG.isInfoEnabled() ){
					LOG.info("Commit: syncronizing document statuses with primary...");
				}
				repositoryClient.synchronizeDocumentStatusesWithPrimary(
						record.getIdentifier().getIdentifier(), saml);
			}
			catch ( RepositoryServiceFault e ){
				throw new RemoteServiceFault(e);
			}
		}
		
		//If the record has been stored locally then we (possibly) need to change
		//its record status (in case all documents in a study stage have been 
		//completed thus triggering automatic record status change).
		if ( isLocal ){
			if ( LOG.isInfoEnabled() ){
				LOG.info("Commit: changing remote record status...");
			}
			try {
				updateRecordStatus(record, saml);
			}
			catch (RemoteServiceFault rsf) {
				//We don't consider an error whilst trying to change the record status 
				//a terminal error so it is just logged and execution continues
				LOG.error("Unable to update status of record "+record.getIdentifier().getIdentifier(), rsf);
			}
		}
		
		if ( null != ddf ){
			//all the work has been done, but if there was a duplicate documents
			//exception we throw it on now so we can report to the user that
			//duplicates were discarded
			throw ddf;
		}
		if ( !notChanged.isEmpty() ){
			//some of the document statuses could not be changed. Not sure if this 
			//is the best way to handle this situation, but gives the same result
			//as you got before the change status method was changed to return
			//a list of document occurrences whose status could not be changed due
			//to error!
			throw new RemoteServiceFault("The statuses of one or more documents could not be changed " +
					"to Complete; please see the server logs for more details.");
		}
	}


	public void synchronizeDocumentStatusesWithPrimary(Record record)
	throws ConnectException, IOException, NotAuthorisedFault, 
	EntrySAMLException, RemoteServiceFault, DecryptionException
	{
		PersistenceManager pManager = PersistenceManager.getInstance();
		PersistenceData pData = null;
		synchronized (pManager) {
			pData = pManager.getData();
		}
		Identifier identifier = record.getIdentifier();
		if ( null != record.getPrimaryIdentifier() ){
			String saml = getSAMLAssertion(pData, identifier.getProjectPrefix());
			try{
				repositoryClient.synchronizeDocumentStatusesWithPrimary(identifier.getIdentifier(), saml);
			}
			catch(RepositoryServiceFault ex){
				throw new RemoteServiceFault(ex);
			}
		}
	}

	private void updateRecordStatus(Record record, String saml)
	throws ConnectException, SocketTimeoutException, IOException,
	NotAuthorisedFault, RemoteServiceFault {
		PersistenceManager pManager = PersistenceManager.getInstance();
		synchronized (pManager) {
			try {

				Record tmprecord = repositoryClient.getRecordSummary(record.getDataSet(), record.getIdentifier().getIdentifier(), saml);

				Long recordId = tmprecord.getId();

				Status  status = PersistenceManager.getInstance().getRecordStatusMap().getStatusForRecord(record.getIdentifier().getIdentifier());

				if ( status == null ) {
					status = record.getStatus();
				}

				//Update for a change in record status..
				if ( status != null && tmprecord.getStatus().getShortName() != status.getShortName() ) {
					repositoryClient.changeStatus(recordId, status.getId(), saml);
				}
			} catch (RepositoryServiceFault e) {
				throw new RemoteServiceFault(e);
			} 
		}
	}

	public void saveRecord(Record record) throws ConnectException, SocketTimeoutException,
	IOException, RepositoryOutOfDateFault, 
	RepositoryNoConsentFault, RepositoryInvalidIdentifierFault,
	org.psygrid.data.repository.transformer.TransformerFault, NotAuthorisedFault, RemoteServiceFault, 
	EntrySAMLException  {
		PersistenceManager pManager = PersistenceManager.getInstance();
		synchronized (pManager) {
			String saml = getSAMLAssertion(pManager.getData(), record.getDataSet());
			try {
				repositoryClient.saveRecord(record, true, saml);
				updateRecordStatus(record, saml);
			}
			catch (DuplicateDocumentsFault e){
				//do nothing - this exception cannot be thrown if discardDuplicates=true
			}
			catch (RepositoryServiceFault e) {
				throw new RemoteServiceFault(e);
			}
		}
	}

	/**
	 * Contact the repository to change the statuses of the document instances
	 * in a record to Complete, ignoring any instances for document instances 
	 * whose id is in the array of discarded documents.
	 * 
	 * @param record The record.
	 * @param discardedDocuments The array of discarded document occurrence ids.
	 * @return List of document occurrences whose status could not be changed.
	 * @throws NotAuthorisedFault
	 * @throws IOException
	 * @throws RemoteServiceFault
	 * @throws EntrySAMLException
	 * @throws ConnectException
	 * @throws SocketTimeoutException
	 */
	private List<Long> changeCompletedDocInstancesStatus(Record record, Long[] discardedDocuments) throws
	NotAuthorisedFault, IOException, RemoteServiceFault, 
	EntrySAMLException, ConnectException, SocketTimeoutException {
		List<Long> docOccIds = new ArrayList<Long>();
		for (DocumentInstance docInstance : EntryHelper.getDocumentInstances(record)) {
			Long occId = docInstance.getOccurrence().getId();
			//check that this document instance has not been discarded
			boolean discarded = false;
			for ( int i=0; i<discardedDocuments.length; i++ ){
				if ( occId.equals(discardedDocuments[i]) ){
					discarded = true;
					break;
				}
			}
			if ( !discarded ){
				docOccIds.add(occId);
			}
		}
		List<Long> notChanged = new ArrayList<Long>();
		if ( docOccIds.size() > 0 ){
			PersistenceManager pManager = PersistenceManager.getInstance();
			String saml = null;
			synchronized (pManager) {
				saml = getSAMLAssertion(pManager.getData(),
						record.getDataSet());
			}
			try {
				String newStatus = DocumentStatus.COMPLETE.toString();
				long[] result = repositoryClient.changeDocumentStatus(
						record.getIdentifier().getIdentifier(), 
						docOccIds, 
						newStatus, saml);
				for ( int i=0, c=result.length; i<c; i++ ){
					notChanged.add(Long.valueOf(result[i]));
				}
			} catch (RepositoryServiceFault e) {
				throw new RemoteServiceFault(e);
			}
		}
		return notChanged;
	}


	public List<Long> changeCompletedDocInstancesStatus(Record record, List<DocumentInstance> docInsts) throws
	NotAuthorisedFault, IOException, RemoteServiceFault, 
	EntrySAMLException, ConnectException, SocketTimeoutException {
		List<Long> docOccIds = new ArrayList<Long>();
		for ( DocumentInstance docInst: docInsts ){
			docOccIds.add(docInst.getOccurrence().getId());
		}
		List<Long> resultList = new ArrayList<Long>();
		if ( docOccIds.size() > 0 ){
			PersistenceManager pManager = PersistenceManager.getInstance();
			String saml = null;
			synchronized (pManager) {
				saml = getSAMLAssertion(pManager.getData(),
						record.getDataSet());
			}
			try {
				String newStatus = DocumentStatus.COMPLETE.toString();
				long[] result = repositoryClient.changeDocumentStatus(
						record.getIdentifier().getIdentifier(), 
						docOccIds, 
						newStatus, saml);
				for ( int i=0, c=result.length; i<c; i++){
					resultList.add(Long.valueOf(result[i]));
				}

			} catch (RepositoryServiceFault e) {
				throw new RemoteServiceFault(e);
			}
		}
		return resultList;
	}

	/**
	 * Change the document status of the document instances for a list of
	 * document occurrences in a record.
	 * <p>
	 * The return value is an array of document occurrence ids for which
	 * there was a problem and the status could not be changed.
	 * 
	 * @param identifier The identifier of the record.
	 * @param docOccs List of document occurrence ids.
	 * @param status The new status
	 * @return List of document occurrence ids for which
	 * there was a problem and the status could not be changed.
	 * @throws NotAuthorisedFault
	 * @throws IOException
	 * @throws RemoteServiceFault
	 * @throws EntrySAMLException
	 * @throws ConnectException
	 * @throws SocketTimeoutException
	 */
	public List<Long> changeDocInstancesStatus(String identifier, Collection<DocumentOccurrence> docOccs, DocumentStatus status) throws
	NotAuthorisedFault, IOException, RemoteServiceFault, InvalidIdentifierException, 
	EntrySAMLException, ConnectException, SocketTimeoutException {
		List<Long> docOccIds = new ArrayList<Long>();
		for ( DocumentOccurrence  docOcc: docOccs ){
			docOccIds.add(docOcc.getId());
		}
		List<Long> resultList = new ArrayList<Long>();
		if ( docOccIds.size() > 0 ){
			PersistenceManager pManager = PersistenceManager.getInstance();
			String saml = null;
			synchronized (pManager) {
				saml = getSAMLAssertion(pManager.getData(), IdentifierHelper.getProjectCodeFromIdentifier(identifier));
			}
			try {
				long[] result = repositoryClient.changeDocumentStatus(
									identifier, 
									docOccIds, 
									status.toString(), 
									saml);
				
				for ( int i=0, c=result.length; i<c; i++){
					resultList.add(Long.valueOf(result[i]));
				}
			} catch (RepositoryServiceFault e) {
				throw new RemoteServiceFault(e);
			}
		}
		return resultList;
	}

	public void updateIdentifiersOnly(DataSet dataSet, String groupCode) throws
	IOException, ConnectException, NotAuthorisedFault, RemoteServiceFault,
	EntrySAMLException, SocketTimeoutException   {
		PersistenceManager pManager = PersistenceManager.getInstance();
		synchronized (PersistenceManager.getInstance()) {
			IdentifiersList identifiers = pManager.getIdentifiers();
			String saml = getSAMLAssertion(pManager.getData(), dataSet);
			try {
				updateIdentifiers(identifiers, dataSet.getId(), groupCode, saml);
			} catch (RepositoryServiceFault e) {
				throw new RemoteServiceFault(e);
			}

			PersistenceManager.getInstance().saveIdentifiers();

		}
	}

	public void updateConsentOnly(DataSet dataSet, Record record) throws
	IOException, ConnectException, NotAuthorisedFault, RemoteServiceFault,
	EntrySAMLException, SocketTimeoutException   {
		PersistenceManager pManager = PersistenceManager.getInstance();
		String saml = null;
		DatedProjectType project = null;
		synchronized (pManager) {
			saml = getSAMLAssertion(pManager.getData(), dataSet);
			project = pManager.getData().getProject(dataSet.getProjectCode());
		}
		List<String> groups = new ArrayList<String>();
		groups.add(record.getIdentifier().getGroupPrefix());
		updateConsent(project, groups, saml);
		synchronized (pManager) {
			pManager.saveConsentMap();
			//NOTE we do not update the lastModifiedDate of the dated project 
			//as we have only updated the consent, not the entire dataset.
			//This means that the consent we have just retrieved will be
			//got again in the next full update
		}
	}

	public void updateRecordStatusOnly(DataSet dataSet, Record record) throws
	IOException, ConnectException, NotAuthorisedFault, RemoteServiceFault,
	EntrySAMLException, SocketTimeoutException   {
		PersistenceManager pManager = PersistenceManager.getInstance();
		String saml = null;
		DatedProjectType project = null;
		synchronized (pManager) {
			saml = getSAMLAssertion(pManager.getData(), dataSet);
			project = pManager.getData().getProject(dataSet.getProjectCode());
		}
		List<String> groups = new ArrayList<String>();
		groups.add(record.getIdentifier().getGroupPrefix());
		updateRecordStatus(project, groups, saml);
		synchronized (pManager) {
			pManager.saveRecordStatusMap();
			//NOTE we do not update the lastModifiedDate of the dated project 
			//as we have only updated the record statuses, not the entire dataset.
			//This means that the record statuses we have just retrieved will be
			//got again in the next full update
		}
	}

	private void logNewIds(List<Identifier> newIds) {
		StringBuilder logMessage = new StringBuilder(50);
		logMessage.append("New identifiers received:\n"); 
		for (Identifier id : newIds) {
			logMessage.append("\t" + id.getIdentifier() + "\n"); 
		}
		LOG.info(logMessage);
	}

	/**
	 * Returns the number of identifiers needed. The number returned will depend
	 * on whether always online mode is enabled. If it is enabled, 0 or 1 will
	 * be returned. If it's not enabled, then the number of unused identifiers
	 * is maintained between {@link #MIN_IDENTIFIERS} and
	 * {@link #MAX_IDENTIFIERS}.
	 */
	private int getNumberOfIdsNeeded(IdentifiersList identifiers, Long dataSetId,
			String groupCode) {

		int numUnused = identifiers.getNumUnused(dataSetId.longValue(),
				groupCode);
		synchronized (PersistenceManager.getInstance()) {
			if (PersistenceManager.getInstance().getData().isAlwaysOnlineMode()) {
				if (numUnused == 0)
					return 1;
				return 0;
			}
		}

		if (numUnused >= MIN_IDENTIFIERS)
			return 0;

		return MAX_IDENTIFIERS - numUnused;
	}

	/**
	 * Updates identifiers. The amount of identifiers is determined by
	 * {@link #getNumberOfIdsNeeded(IdentifiersList, Long, String)} and takes into
	 * account if always online mode is being used.
	 */
	private void updateIdentifiers(IdentifiersList identifiers, Long dataSetId, 
			String groupCode, String saml) throws IOException, NotAuthorisedFault, 
			RepositoryServiceFault, ConnectException, SocketTimeoutException {

		if (LOG.isDebugEnabled())
			LOG.debug("Entering updateIdentifiers()"); 

		int numberNeeded = getNumberOfIdsNeeded(identifiers, dataSetId, groupCode);
		if (numberNeeded <= 0)
			return;

		List<Identifier> newIds = repositoryClient.generateIdentifiers(dataSetId,
				groupCode, numberNeeded, saml);

		if (LOG.isInfoEnabled())
			logNewIds(newIds);

		for (Identifier id : newIds)
			identifiers.add(new IdentifierData(id, dataSetId.longValue()));

		if (LOG.isDebugEnabled())
			LOG.debug("Leaving updateIdentifiers()"); 
	}

	private List<ProjectType> getUserProjects() throws EntrySAMLException, 
	RemoteServiceFault, NotAuthorisedFault, IOException {
		if (hardCodedProjects != null && hardCodedProjects.size() > 0) {
			return hardCodedProjects;
		}
		refreshKey(true);
		SecurityManager secManager = SecurityManager.getInstance();
		List<ProjectType> projects;
		synchronized (secManager) {
			projects = secManager.getUserProjects();
		}
		return projects;
	}

	private void refreshKey(boolean onlyIfNeeded) throws ConnectException, 
	IOException, RemoteServiceFault {
		if (hardCodedProjects != null && hardCodedProjects.size() > 0) {
			return;
		}
		SecurityManager secManager = SecurityManager.getInstance();
		synchronized(secManager) {
			Date keyValidity = secManager.getKeyValidity();
			//check to see that there is a key to refresh
			if (keyValidity != null) {
				if (onlyIfNeeded) {
					Date now = new Date();
					long thirtySeconds = 30 * 1000;
					now.setTime(now.getTime() + thirtySeconds);
					if (keyValidity.getTime() < now.getTime()) {
						secManager.refreshKey();
					}
				} else {
					secManager.refreshKey();
				}
			}
		}
	}

	/**
	 * Invokes callable and if a NotAuthorisedFault is issued, refreshes the key
	 * and tries a second time. If the exception is thrown again, simply rethrow
	 * it.
	 * 
	 * @param callable
	 * @throws NotAuthorisedFault
	 * @throws Exception
	 */
	private <T>T tryCallWithAuthorizationException(Callable<T> callable) throws 
	NotAuthorisedFault, ConnectException, SocketTimeoutException, IOException, Exception {
		boolean executed = false;
		while (true) {
			try {
				return callable.call();
			}
			catch (NotAuthorisedFault naf) {
				if (executed) {
					throw naf;
				}
				executed = true;
				refreshKey(false);
			}  
		}
	}

	private boolean canLoadPendingDocuments() throws ConnectException, 
	IOException, RemoteServiceFault, EntrySAMLException, NotAuthorisedFault {

		if (hardCodedProjects != null && hardCodedProjects.size() > 0) {
			return true;
		}
		refreshKey(true);
		SecurityManager secManager = SecurityManager.getInstance();
		synchronized (secManager) {
			return secManager.canLoadPendingDocuments();
		}
	}

	public void update() throws IOException, ConnectException, SocketTimeoutException, 
	NotAuthorisedFault, EntrySAMLException, RemoteServiceFault, InvalidIdentifierException, DecryptionException  {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Entering update()"); 
		}
		try {
			fireUpdateProgressStarted(new ProgressEvent(this));
			PersistenceManager pManager = PersistenceManager.getInstance();
			synchronized (pManager) {

				if( isConnectionAvailable() && null == getTestDatasetPath() ){
					updateVersions();
					updateClockOffset();

					List<ProjectType> projects = getUserProjects();
					List<ProjectType> publishedProjects = getPublishedProjects(projects);
					
					final PersistenceData pData = pManager.getData();
					pData.setCanLoadPendingDocuments(canLoadPendingDocuments());

					//update the list of projects and delete those they are no
					//longer a member of
					List<DatedProjectType> deletedProjects = pData.updateProjects(publishedProjects);
					for (DatedProjectType deletedProject : deletedProjects) {
						DataSetSummary dss = pData.removeDataSetSummary(deletedProject);
						if (dss != null) {
							pManager.deleteDataSet(dss);
						}
						else{
							LOG.info("No dataset summary for project "+deletedProject.getIdCode());
						}
					}
					pManager.savePersistenceData();

					boolean stdCodesUpdated = false;
					Map<DatedProjectType, NotAuthorisedFault> errors = new HashMap<DatedProjectType, NotAuthorisedFault>();
					for (DatedProjectType datedProject : pData.getProjects()) {
						if (datedProject.isVirtual()) {
							continue;
						}
						try{
							String saml = getSAMLAssertion(datedProject);
							if (!stdCodesUpdated) {
								updateStandardCodes(saml);
								stdCodesUpdated = true;
							}

							//before updating check if the dataset has been patched
							
							Integer storedDataSetVersionNo = null; 
							try {
								storedDataSetVersionNo = pData.getDataSetSummary(datedProject).getAutoversionNum();
							} catch (Exception ex) {
								LOG.debug("No autoversion number for project " + datedProject.getIdCode());
							}
							
							DataSetSummary dss = updateDataSet(datedProject, pData,
									saml);
							if (dss != null && dss.getAutoversionNum() != null) {
								if ((storedDataSetVersionNo == null ||
										storedDataSetVersionNo.intValue() < dss.getAutoversionNum().intValue())
										&& dss.getAutoversionNum() > 0) {
									patchedDataSets += " " + dss.getProjectCode();
								}
							} 
							
							if (dss == null) {
								dss = pData.getDataSetSummary(datedProject);
							}
							if (dss != null) {
								updateAllIdentifiers(dss.getId(), datedProject, saml);
								updateConsentAndStatus(datedProject);
							}

							datedProject.setLastModified(new Date());
						}
						catch(NotAuthorisedFault naf){
							LOG.error("NotAuthorisedFault during update process for project "+datedProject.getIdCode(), naf);
							errors.put(datedProject, naf);
						}

					}

					if ( !pData.getProjects().isEmpty() && pData.getProjects().size() == errors.size() ){
						//A NotAuthorizedFault was thrown for all projects - throw the
						//NotAuthorizedFault from the first project in the list
						throw errors.get(pData.getProjects().get(0));
					}

					pManager.savePersistenceData();
					pManager.saveIdentifiers();
					pManager.saveConsentMap();
					pManager.saveRecordStatusMap();
					pManager.saveSecondaryIdentifierMap();
					pManager.saveExternalIdMap();
					pManager.saveVersionMap();
					pManager.saveClockOffset();

				}

				//Set whether the user has any studies that are linked
				//This doesn't strictly need to be done on every update (just when
				//the studies change) but it needs to be done like this for the
				//first login by existing users, and isn't doing any harm
				final PersistenceData pData = pManager.getData();
				boolean linkedStudies = false;
				for ( DataSetSummary ds: pData.getDataSetSummaries() ){
					if ( null != ds.getSecondaryProjectCode() ){
						linkedStudies = true;
					}
				}
				pData.setLinkedStudies(linkedStudies);
				pManager.savePersistenceData();
			}
		}
		finally {
			//set the clock offset for repository objects
			TimeOffset.getInstance().setOffset(PersistenceManager.getInstance().getClockOffset().getOffset());
			fireUpdateProgressEnded(new ProgressEvent(this));
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("Leaving update()"); 
		}
	}


	private String getSAMLAssertion(PersistenceData pData, DataSet dataSet)
	throws IOException, RemoteServiceFault, EntrySAMLException, 
	NotAuthorisedFault, ConnectException {
		DatedProjectType datedProject = pData.getProject(dataSet.getProjectCode());
		return getSAMLAssertion(datedProject);
	}

	private String getSAMLAssertion(PersistenceData pData, String projectCode) 
	throws IOException, RemoteServiceFault, EntrySAMLException, 
	NotAuthorisedFault, ConnectException {
		DatedProjectType datedProject = pData.getProject(projectCode);
		return getSAMLAssertion(datedProject);
	}

	private String getSAMLAssertion(DatedProjectType datedProject) 
	throws RemoteServiceFault, IOException, EntrySAMLException, 
	NotAuthorisedFault, ConnectException  {
		if (hardCodedProjects != null && hardCodedProjects.size() > 0) {
			return ""; 
		}
		refreshKey(true);
		SecurityManager secManager = SecurityManager.getInstance();
		String saml = secManager.getSAMLAssertion(
				datedProject.getProject());
		return saml;
	}

	private String getSAMLAssertion() 
	throws RemoteServiceFault, IOException, EntrySAMLException, 
	NotAuthorisedFault, ConnectException  {
		if (hardCodedProjects != null && hardCodedProjects.size() > 0) {
			return ""; 
		}
		refreshKey(true);
		SecurityManager secManager = SecurityManager.getInstance();
		String saml = secManager.getSAMLAssertion();
		return saml;
	}
	

	private void updateAllIdentifiers(Long dataSetId, DatedProjectType project,
			String saml) throws IOException, NotAuthorisedFault, 
			EntrySAMLException, RemoteServiceFault, ConnectException, SocketTimeoutException  {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Entering updateAllIdentifiers()"); 
		}

		IdentifiersList identifiers = 
			PersistenceManager.getInstance().getIdentifiers();
		List<GroupType> groups = getUsersGroupsInProject(project);

		identifiers.synchronizeWithGroups(project, groups);

		/* 
		 * We return early in always online mode because it would
		 * mean that we get identifiers before we need it. If we remove the
		 * early return, we'd only get a single identifier at most for each
		 * group though.
		 */
		synchronized (PersistenceManager.getInstance()) {
			if (PersistenceManager.getInstance().getData().isAlwaysOnlineMode())
				return;
		}

		try {
			for (GroupType group : groups) {
				boolean autoGenerateIdentifiers = false;
				if (hardCodedProjects != null && hardCodedProjects.size() > 0)
					autoGenerateIdentifiers = true;
				else
					autoGenerateIdentifiers = 
						SecurityManager.getInstance().autoGenerateIdentifiers(project.getProject(), group, saml);

				if (autoGenerateIdentifiers)
					updateIdentifiers(identifiers, dataSetId, group.getIdCode(),
							saml);
			}
		} catch (RepositoryServiceFault e) {
			throw new RemoteServiceFault(e);
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("Leaving updateAllIdentifiers()"); 
		}
	}

	private void updateConsentAndStatus(DatedProjectType project)
	throws ConnectException, SocketTimeoutException, NotAuthorisedFault, RemoteServiceFault, EntrySAMLException, IOException, InvalidIdentifierException {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Entering updateConsentAndStatus()"); 
		}

		List<String> groups = new ArrayList<String>();
		for ( GroupType grp: getUsersGroupsInProject(project)){
			groups.add(grp.getIdCode());
			if (LOG.isDebugEnabled()) {
				LOG.debug("Added group "+grp.getIdCode()); 
			}
		}

		PersistenceManager pManager = PersistenceManager.getInstance();
		ConsentMap2 consent = null;
		RecordStatusMap2 status = null;
		SecondaryIdentifierMap secondid = null;
		ExternalIdMap extIdMap = null;
		synchronized (pManager) {
			consent = pManager.getConsentMap();
			status = pManager.getRecordStatusMap();
			secondid = pManager.getSecondaryIdentifierMap();
			extIdMap = pManager.getExternalIdMap();
		}

		consent.synchronizeWithGroups(project, groups);
		status.synchronizeWithGroups(project, groups);
		secondid.synchronizeWithGroups(project, groups);
		extIdMap.synchronizeWithGroups(project, groups);

		boolean emptyMap = false;
		if ( consent.noConsentForProject(project.getIdCode()) || status.noStatusesForProject(project.getIdCode()) 
				|| !status.documentStatusMapExists() ){
			//if either the consent map or status map have no entries for this project 
			//or the documentStatusMap hasn't yet been populated then 
			//the last modified date is ignored in the subsequent call to getRecordsWithConsentByGroups 
			//to ensure that these maps are re-populated in the event that they are deleted
			if ( LOG.isDebugEnabled() ){
				LOG.debug("No consent or statuses for project "+project.getIdCode()+" - ignoring last modified date");
			}
			emptyMap = true;
		}

		try{
			Date lastModifiedDate = null;
			List<String> newGroups = null;
			if ( emptyMap ){
				lastModifiedDate = new Date(0);
				newGroups = new ArrayList<String>(0);
			}
			else{
				lastModifiedDate = project.getLastModified();
				//remove deleted records from the maps
				List<String> deletedIds = repositoryClient.getDeletedRecordsByGroups(project.getIdCode(), groups, lastModifiedDate, getSAMLAssertion(project));
				for ( String id: deletedIds ){
					consent.deleteRecord(id);
					status.deleteRecord(id);
					secondid.remove(id);
					extIdMap.remove(id);
				}
				//look for any groups for which we don't have any status info -
				//we assume that these are new groups added to the user so we need
				//to retrieve all consent/status/etc info for them
				newGroups = status.findGroupsWithNoRecords(project, groups);
			}

			String saml = getSAMLAssertion(project);
			ConsentStatusResult result = repositoryClient.getConsentAndStatusInfoForGroups(project.getIdCode(), groups, lastModifiedDate, saml);

			consent.addFromConsentStatusResult(result);
			status.addFromConsentStatusResult(result);
			secondid.addFromConsentStatusResult(result);
			extIdMap.addFromConsentStatusResult(result);

			if ( !newGroups.isEmpty() ){
				saml = getSAMLAssertion(project);
				result = repositoryClient.getConsentAndStatusInfoForGroups(project.getIdCode(), newGroups, new Date(0), saml);

				consent.addFromConsentStatusResult(result);
				status.addFromConsentStatusResult(result);
				secondid.addFromConsentStatusResult(result);
				extIdMap.addFromConsentStatusResult(result);
			}

		}
		catch(RepositoryServiceFault ex){
			throw new RemoteServiceFault(ex);
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("Leaving updateConsentAndStatus()"); 
		}

	}

	private void updateConsent(DatedProjectType project, List<String> groups, String saml)
	throws ConnectException, SocketTimeoutException, NotAuthorisedFault, RemoteServiceFault, IOException {
		PersistenceManager pManager = PersistenceManager.getInstance();
		ConsentMap2 consent = null;
		synchronized (pManager) {
			consent = PersistenceManager.getInstance().getConsentMap();
		}
		try{
			ConsentStatusResult result = repositoryClient.getConsentAndStatusInfoForGroups(project.getIdCode(), groups, project.getLastModified(), saml);
			consent.addFromConsentStatusResult(result);
		}
		catch(RepositoryServiceFault ex){
			throw new RemoteServiceFault(ex);
		}
	}

	private void updateRecordStatus(DatedProjectType project, List<String> groups, String saml)
	throws ConnectException, SocketTimeoutException, NotAuthorisedFault, RemoteServiceFault, IOException {
		PersistenceManager pManager = PersistenceManager.getInstance();
		RecordStatusMap2 rsMap = null;
		synchronized (pManager) {
			rsMap = PersistenceManager.getInstance().getRecordStatusMap();
		}
		try{
			ConsentStatusResult result = repositoryClient.getConsentAndStatusInfoForGroups(project.getIdCode(), groups, project.getLastModified(), saml);
			rsMap.addFromConsentStatusResult(result);
		}
		catch(RepositoryServiceFault ex){
			throw new RemoteServiceFault(ex);
		}
	}

	private void updateClockOffset()  {
		PersistenceManager pManager = PersistenceManager.getInstance();
		ClockOffset clock = null;
		synchronized (pManager) {
			clock = pManager.getClockOffset();
		}

		String ntpServer = null;
		try{
			Properties props = new Properties();
			props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("client.properties"));
			ntpServer = props.getProperty("client.ntpserver");
		}
		catch(IOException ex){
			//do nothing - no ntp server specified is handled below
		}
		catch(NullPointerException ex){
			//do nothing - no ntp server specified is handled below
		}

		try{
			if ( null == ntpServer ){
				//no NTP server specified so we have to synchronize our clock
				//with the repository
				LOG.info("No NTP server specified - calculating clock offset from repository");
				clock.setOffset(repositoryClient.getClockOffset());
				clock.setSource(OffsetSource.SERVER);
			}
			else{
				LOG.info("Calculating clock offset from NTP server "+ntpServer);
				NtpClockSync ntpcs = new NtpClockSync(ntpServer);
				try{
					clock.setOffset(ntpcs.getOffset());
					clock.setSource(OffsetSource.NTP);
				}
				catch(Exception ex){
					//try to sync with the repository instead
					LOG.error("Failed to calculate clock offset using NTP server "+ntpServer+". Using repository instead.", ex);
					clock.setOffset(repositoryClient.getClockOffset());
					clock.setSource(OffsetSource.SERVER);        		
				}
			}
		}
		catch(Exception ex){
			LOG.error("Unable to update clock offset.", ex);
		}

	}

	private void updateVersions() throws ConnectException, SocketTimeoutException, RemoteServiceFault, IOException {
		PersistenceManager pManager = PersistenceManager.getInstance();
		VersionMap versions = null;
		synchronized (pManager) {
			versions = pManager.getVersionMap();
		}

		refreshKey(true);
		String repoVersion = repositoryClient.getVersion();
		versions.addVersion(VersionMap.REPO_NAME, repoVersion);
		String eslVersion = "Unknown";
		try{
			eslVersion = eslClient.getVersion();
		}
		catch (Exception ex){
			//do nothing - if there is an exception when trying to connect
			//to the ESL web service then the version will just stay as "Unknown"
		}
		versions.addVersion(VersionMap.ESL_NAME, eslVersion);
		if (hardCodedProjects != null && hardCodedProjects.size() > 0) {
			versions.addVersion(VersionMap.AA_NAME, "Unknown");
			versions.addVersion(VersionMap.PA_NAME, "Unknown");
		}
		else{
			versions.addVersion(VersionMap.AA_NAME, SecurityManager.getInstance().getAaVersion());
			versions.addVersion(VersionMap.PA_NAME, SecurityManager.getInstance().getPaVersion());
		}
	}

	public List<GroupType> getUsersGroupsInProject(DatedProjectType project) 
	throws ConnectException, EntrySAMLException, RemoteServiceFault, 
	NotAuthorisedFault   {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Entering getUsersGroupsInProject()"); 
		}
		List<GroupType> groupTypes;
		if (hardCodedProjects != null && hardCodedProjects.size() > 0) {
			groupTypes = getTestingGroups(project);
		}
		else{
			SecurityManager secManager = SecurityManager.getInstance();
			groupTypes = secManager.getUsersGroupsInProject(project.getProject());
			if (groupTypes.size() == 0) {
				throw new NotAuthorisedFault("User does not belong to any group.");
			}
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("Leaving getUserGroupsInProject()"); 
		}
		return groupTypes;
	}

	private List<GroupType> getTestingGroups(DatedProjectType project){
		List<GroupType> groupTypes = new ArrayList<GroupType>();
		if ( project.getIdCode().equals("OLK")){
			groupTypes.add(new GroupType("SomeGroup", "002001", "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			groupTypes.add(new GroupType("AnotherGroup", "002002", ""));   
		}
		if ( project.getIdCode().equals("TST")){
			groupTypes.add(new GroupType("SomeGroup", "002001", "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			groupTypes.add(new GroupType("AnotherGroup", "002002", ""));   
		}
		if ( project.getIdCode().equals("NED") ){
			groupTypes.add(new GroupType("SomeGroup", "001001", "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			groupTypes.add(new GroupType("AnotherGroup", "002001", ""));   
			groupTypes.add(new GroupType("AnotherGroup2", "003001", ""));   
			groupTypes.add(new GroupType("AnotherGroup3", "004001", ""));   
			groupTypes.add(new GroupType("AnotherGroup4", "004002", ""));   
			groupTypes.add(new GroupType("AnotherGroup5", "005001", ""));   
			groupTypes.add(new GroupType("AnotherGroup6", "006001", ""));   
			groupTypes.add(new GroupType("AnotherGroup7", "007001", ""));   
			groupTypes.add(new GroupType("AnotherGroup8", "007002", ""));   
			groupTypes.add(new GroupType("AnotherGroup9", "008001", ""));   
		}
		if ( project.getIdCode().equals("ED2") ){
			groupTypes.add(new GroupType("001001", "001001", "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			groupTypes.add(new GroupType("002001", "002001", ""));   
			groupTypes.add(new GroupType("003001", "003001", ""));   
			groupTypes.add(new GroupType("004001", "004001", ""));   
			groupTypes.add(new GroupType("005001", "005001", ""));   
		}
		if ( project.getIdCode().equals("EDT") ){
			groupTypes.add(new GroupType("001001", "001001", "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			groupTypes.add(new GroupType("002001", "002001", ""));   
			groupTypes.add(new GroupType("003001", "003001", ""));   
			groupTypes.add(new GroupType("004001", "004001", ""));   
			groupTypes.add(new GroupType("005001", "005001", ""));   
		}
		if ( project.getIdCode().equals("MTS") ){
			groupTypes.add(new GroupType("001001", "001001", "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			groupTypes.add(new GroupType("002001", "002001", ""));   
			groupTypes.add(new GroupType("003001", "003001", ""));   
			groupTypes.add(new GroupType("004001", "004001", ""));   
		}
		if ( project.getIdCode().equals("ADD") ){
			groupTypes.add(new GroupType("640001", "640001", "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			groupTypes.add(new GroupType("640002", "640002", "")); 
			groupTypes.add(new GroupType("640003", "640003", "")); 
			groupTypes.add(new GroupType("640004", "640004", "")); 
			groupTypes.add(new GroupType("653001", "653001", "")); 
			groupTypes.add(new GroupType("653002", "653002", "")); 
			groupTypes.add(new GroupType("653003", "653003", "")); 
		}
		if ( project.getIdCode().equals("COM") ){
			groupTypes.add(new GroupType("001001", "001001", "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			groupTypes.add(new GroupType("002001", "002001", "")); 
			groupTypes.add(new GroupType("003001", "003001", "")); 
		}
		return groupTypes;

	}

	/**
	 * If the IDataSet for <code>projectType</code> has been modified, it is
	 * retrieved from the server and saved.
	 * 
	 * @return the DataSetSummary for the IDataSet saved
	 * @throws RemoteServiceFault 
	 */
	private DataSetSummary updateDataSet(DatedProjectType project,
			PersistenceData pData, String saml) throws IOException,
			NotAuthorisedFault, ConnectException, RemoteServiceFault {

		if (LOG.isDebugEnabled()) {
			LOG.debug("Entering updateDataSet() for project: " + project.getIdCode()); 
		}

		try {
			DataSetSummaryGetter dssGetter = new DataSetSummaryGetter(project,
					saml);
			DataSet dataSetSummary = tryCallWithAuthorizationException(dssGetter);

			if (dataSetSummary == null) {
				System.out.println("update dataset returning null");
				return null;
			}
		
			DataSetSummary dss = new DataSetSummary(dataSetSummary);
			DataSetGetter dataSetGetter = new DataSetGetter(dataSetSummary,
					saml);
			DataSet dataSet = tryCallWithAuthorizationException(dataSetGetter);
		
			
			PersistenceManager.getInstance().saveDataSet(dataSet);
			pData.addDataSetSummary(dss);
			return dss;

		} catch (RepositoryNoSuchDatasetFault rnsdf) {
			if (LOG.isInfoEnabled()) {
				LOG.info(rnsdf.getMessage(), rnsdf);
			}
		}
		// We rethrow the exception with the correct type. We know all
		// possible
		catch (Exception e) {
			processUpdateDataSetException(e);
		}
		return null;
	}

	private void processUpdateDataSetException(Exception e) throws ConnectException, 
	RemoteServiceFault, NotAuthorisedFault {
		if (e instanceof RuntimeException) {
			throw (RuntimeException) e;
		}
		if (e instanceof ConnectException) {
			throw (ConnectException) e;
		}
		if (e instanceof RepositoryServiceFault) {
			throw new RemoteServiceFault(e);
		}
		if (e instanceof NotAuthorisedFault) {
			throw (NotAuthorisedFault) e;
		}
		// Should never happen
		if (LOG.isErrorEnabled()) {
			LOG.error("Unexpected exception was thrown.", e); 
		}
	}

	private class DataSetSummaryGetter implements Callable<DataSet>{

		private final DatedProjectType project;
		private final String saml;

		private DataSetSummaryGetter(final DatedProjectType project, final String saml) {
			super();
			this.project = project;
			this.saml = saml;
		}

		public DataSet call() throws ConnectException, SocketTimeoutException, RepositoryServiceFault,
		NotAuthorisedFault, RepositoryNoSuchDatasetFault {
			Date lastModified = project.getLastModified();
			
			System.out.println("last modified" + lastModified.toString());
			DataSet dss = repositoryClient.getDataSetSummary(project.getIdCode(), lastModified,
					saml);
			
			System.out.println("dss is " + dss);
			
			if (dss != null && LOG.isInfoEnabled()) {
				LOG.info("New dataset summary retrieved: " + dss +", lastModified: " +  
						lastModified);
			}
			return dss;
		}
	}
	private class DataSetGetter implements Callable<DataSet>{

		private final DataSet dataSet;
		private final String saml;

		private DataSetGetter(final DataSet dataSet, final String saml) {
			super();
			this.dataSet = dataSet;
			this.saml = saml;
		}

		public DataSet call() throws ConnectException, SocketTimeoutException, RepositoryServiceFault,
		NotAuthorisedFault {
			DataSet completeDataSet = repositoryClient.getDataSet(dataSet.getId(), saml);
			if (LOG.isInfoEnabled()) {
				LOG.info("New complete dataset retrieved: " + completeDataSet.getDisplayText()); 
			}
			return completeDataSet;
		}
	}

	public IProject eslRetrieveProjectByCode(DataSet dataSet)
	throws IOException, NotAuthorisedFault, EntrySAMLException, RemoteServiceFault {
		PersistenceManager pManager = PersistenceManager.getInstance();
		String saml;
		synchronized (pManager) {
			saml = getSAMLAssertion(pManager.getData(), dataSet);
		}
		try {
			return eslClient.retrieveProjectByCode(dataSet.getProjectCode(), saml);
		} catch (ESLServiceFault e) {
			throw new RemoteServiceFault(e);
		} catch (org.psygrid.esl.services.NotAuthorisedFault e){
			throw new NotAuthorisedFault(e);
		}

	}
	
	public String allocateMedsPackage(String participantIdentifier, String projectCode, String centreCode) throws RemoteServiceFault, NotAuthorisedFault, IOException, EntrySAMLException{
		String medsPackageId = null;
		PersistenceManager pManager = PersistenceManager.getInstance();
		String saml;
		synchronized (pManager) {
			saml = getSAMLAssertion(pManager.getData(), projectCode);
		}
		
		try{
			medsPackageId = eslClient.allocateMedicationPackage(projectCode, centreCode, participantIdentifier, saml);
		}catch(ESLServiceFault e1){
			throw new RemoteServiceFault(e1);
		}catch(org.psygrid.esl.services.NotAuthorisedFault e){
			throw new NotAuthorisedFault(e);
		}
		
		return medsPackageId;
	}

	public void eslSaveSubject(ISubject eslSubject)
	throws IOException, SocketTimeoutException, NotAuthorisedFault, EntrySAMLException, RemoteServiceFault,
	ESLDuplicateObjectFault, ESLOutOfDateFault, ESLSubjectExistsException, InvalidIdentifierException {
		PersistenceManager pManager = PersistenceManager.getInstance();
		String saml;
		String project = IdentifierHelper.getProjectCodeFromIdentifier(eslSubject.getStudyNumber());
		synchronized (pManager) {
			saml = getSAMLAssertion(pManager.getData(), project);
		}
		try{
			IProject eslProject = eslClient.retrieveProjectByCode(project, saml);
			String group = IdentifierHelper.getGroupCodeFromIdentifier(eslSubject.getStudyNumber());
			for ( IGroup eslGroup: eslProject.getGroups() ){
				if ( eslGroup.getGroupCode().equals(group) ){
					eslSubject.setGroup(eslGroup);
					break;
				}
			}
			eslClient.saveSubject(eslSubject, saml);
		} catch (ESLServiceFault e) {
			throw new RemoteServiceFault(e);
		} catch (org.psygrid.esl.services.NotAuthorisedFault e){
			throw new NotAuthorisedFault(e);
		}
	}

	private CustomEmailInfo buildCustomEmailInfo(Record record){
		
		org.psygrid.data.model.hibernate.Site repositorySite = record.getSite();
		
		CustomEmailInfo customInfo = new CustomEmailInfo();
		Site eslSite = new Site();
		customInfo.setSite(eslSite);
		
		eslSite.setGeographicCode(repositorySite.getGeographicCode());
		eslSite.setSiteId(repositorySite.getSiteId());
		eslSite.setSiteName(repositorySite.getSiteName());
				
		return customInfo;
	}
	
	public void eslRandomiseSubject(Record record)
	throws ConnectException,SocketTimeoutException, RemoteServiceFault, NotAuthorisedFault, IOException,
	EntrySAMLException, RandomisationException {
		PersistenceManager pManager = PersistenceManager.getInstance();
		String saml;
		synchronized (pManager) {
			saml = getSAMLAssertion(pManager.getData(), record.getDataSet());
		}
		try{
			IProject project = eslClient.retrieveProjectByCode(record.getDataSet().getProjectCode(), saml);
			ISubject subject = eslClient.retrieveSubjectByStudyNumber(project, record.getIdentifier().getIdentifier(), saml);
			
			CustomEmailInfo customInfo = buildCustomEmailInfo(record);
			
			eslClient.randomiseSubject(subject, customInfo, saml);
		} catch (ESLServiceFault e) {
			throw new RemoteServiceFault(e);
		} catch (org.psygrid.esl.services.NotAuthorisedFault e){
			throw new NotAuthorisedFault(e);
		}
	}
	
	public String eslRetrieveRandomisationResult(Record record)
	throws RemoteServiceFault, NotAuthorisedFault, IOException, EntrySAMLException, ConnectException, SocketTimeoutException,
	RandomisationException
	{
		PersistenceManager pManager = PersistenceManager.getInstance();
		String saml;
		String treatmentName = null;
		synchronized (pManager) {
			saml = getSAMLAssertion(pManager.getData(), record.getDataSet());
		}
		try{
				IProject project = eslClient.retrieveProjectByCode(record.getDataSet().getProjectCode(), saml);
				String participantIdentifier = record.getIdentifier().getIdentifier();
				treatmentName = eslClient.lookupRandomisationResult(project, participantIdentifier, saml);
		}catch(ESLServiceFault e){
			throw new RemoteServiceFault(e);
		}catch(org.psygrid.esl.services.NotAuthorisedFault e){
			throw new NotAuthorisedFault(e);
		}
		return treatmentName;
	}

	public ISubject eslRetrieveSubject(Record record)
	throws ConnectException, SocketTimeoutException, RemoteServiceFault, NotAuthorisedFault, IOException,
	EntrySAMLException, ESLSubjectNotFoundFault {
		PersistenceManager pManager = PersistenceManager.getInstance();
		String saml;
		synchronized (pManager) {
			saml = getSAMLAssertion(pManager.getData(), record.getDataSet());
		}
		try{
			IProject project = eslClient.retrieveProjectByCode(record.getDataSet().getProjectCode(), saml);
			return eslClient.retrieveSubjectByStudyNumber(project, record.getIdentifier().getIdentifier(), saml);
		} catch (org.psygrid.esl.services.ESLSubjectNotFoundFault e) {
			throw new ESLSubjectNotFoundFault(e);
		} catch (ESLServiceFault e) {
			throw new RemoteServiceFault(e);
		} catch (org.psygrid.esl.services.NotAuthorisedFault e){
			throw new NotAuthorisedFault(e);
		}
	}

	public List<ISubject> eslSearchForSubject(ISubject exampleSubject, DataSet dataset)
	throws ConnectException, SocketTimeoutException, RemoteServiceFault, NotAuthorisedFault, IOException,
	EntrySAMLException {
		PersistenceManager pManager = PersistenceManager.getInstance();
		String saml;
		synchronized (pManager) {
			saml = getSAMLAssertion(pManager.getData(), dataset);
		}
		try{
			IProject project = eslClient.retrieveProjectByCode(dataset.getProjectCode(), saml);
			return eslClient.findSubjectByExample(project, exampleSubject, saml);
		} catch (ESLServiceFault e) {
			throw new RemoteServiceFault(e);
		} catch (org.psygrid.esl.services.NotAuthorisedFault e){
			throw new NotAuthorisedFault(e);
		}
	}

	public void emailLogFileToSupport()
	throws ConnectException, SocketTimeoutException, RemoteServiceFault, NotAuthorisedFault, IOException, EntrySAMLException{
		try{
			fireUpdateProgressStarted(new ProgressEvent(this));
			PersistenceManager pManager = PersistenceManager.getInstance();
			String logFile = null;
			synchronized (pManager) {
				logFile = pManager.getCurrentLogFile();
			}

			String saml = getSAMLAssertion();
			repositoryClient.emailSupport("PsyGrid log file", logFile, saml);
		}
		catch(RepositoryServiceFault ex){
			throw new RemoteServiceFault(ex);
		}
		finally {
			fireUpdateProgressEnded(new ProgressEvent(this));
		}

	}

	public void emailAllLogFilesToSupport()
	throws ConnectException, SocketTimeoutException, RemoteServiceFault, NotAuthorisedFault, IOException, EntrySAMLException{
		try{
			fireUpdateProgressStarted(new ProgressEvent(this));
			PersistenceManager pManager = PersistenceManager.getInstance();
			String logFile = null;
			synchronized (pManager) {
				logFile = pManager.getAllLogFiles();
			}

			String saml = getSAMLAssertion();
			repositoryClient.emailSupport("PsyGrid log file", logFile, saml);
		}
		catch(RepositoryServiceFault ex){
			throw new RemoteServiceFault(ex);
		}
		finally {
			fireUpdateProgressEnded(new ProgressEvent(this));
		}

	}

	public boolean updatePrimaryIdentifier(Record record, String primaryIdentifier)
	throws IOException, NotAuthorisedFault, RemoteServiceFault, EntrySAMLException  {
		PersistenceManager pManager = PersistenceManager.getInstance();
		synchronized (pManager) {
			String saml = getSAMLAssertion(pManager.getData(), record.getDataSet());
			try {
				return repositoryClient.updatePrimaryIdentifier(record.getIdentifier().getIdentifier(), primaryIdentifier, saml);
			}
			catch (RepositoryServiceFault e) {
				throw new RemoteServiceFault(e);
			}
		}

	}


	public boolean updateSecondaryIdentifier(Record record, String secondaryIdentifier)
	throws IOException, NotAuthorisedFault, RemoteServiceFault, EntrySAMLException  {
		PersistenceManager pManager = PersistenceManager.getInstance();
		synchronized (pManager) {
			String saml = getSAMLAssertion(pManager.getData(), record.getDataSet());
			try {
				return repositoryClient.updateSecondaryIdentifier(record.getIdentifier().getIdentifier(), secondaryIdentifier, saml);
			}
			catch (RepositoryServiceFault e) {
				throw new RemoteServiceFault(e);
			}
		}

	}


	/**
	 * Apply a transformer to a list of basic entries and return
	 * the result.
	 * 
	 * @param dsId
	 * @param transformer
	 * @param variables
	 * @return result
	 */
	public Object transform(Long dsId, Transformer transformer, List<String> responses)
	throws RemoteServiceFault, TransformerFault {

		try {
			String saml = getSAMLAssertion();
			return repositoryClient.transform(dsId, transformer, responses, saml);
		}
		catch (org.psygrid.data.repository.transformer.TransformerFault tf) {
			LOG.error("Problem with transformer: ", tf); //$NON-NLS-1$				
			throw new TransformerFault("Transformer fault occurred for "+transformer.getWsOperation(), tf);		
		}
		catch(RepositoryServiceFault ex){
			throw new RemoteServiceFault(ex);
		}
		catch(Exception e) {
			LOG.error("Problem with transformer: ", e); //$NON-NLS-1$
			throw new TransformerFault("General problem with transformer: "+transformer.getWsOperation(), e);				
		}
	}

	public void updateRecordMetadata(Record record, RecordData recordData, String reason)
	throws IOException, RemoteServiceFault, NotAuthorisedFault, EntrySAMLException
	{
		PersistenceManager pManager = PersistenceManager.getInstance();
		String saml = null;
		DataSet dataSet = null;
		synchronized (pManager) {
			dataSet = pManager.getData().getCompleteDataSet(
					record.getIdentifier().getProjectPrefix());
			saml = getSAMLAssertion(pManager.getData(), dataSet);
		}
		System.out.println("Updating metadata");
		PersistenceManager.getInstance().updateRecordMetadata(record, recordData);
		try {
			if (record.getId() != null) {
				System.out.println("Updating metadata remotely");
				repositoryClient.updateRecordMetadata(record, recordData, reason, saml);
			}
		} catch (RepositoryServiceFault e) {
			throw new RemoteServiceFault(e);
		}
	}

	/**
	 * Find out whether the current user can update the metdata
	 * for the given record.
	 * 
	 * @param record
	 * @return boolean
	 * @throws IOException
	 * @throws RemoteServiceFault
	 * @throws NotAuthorisedFault
	 * @throws EntrySAMLException
	 */
	public boolean canUpdateRecordMetadata(Record record)
	throws IOException, RemoteServiceFault, NotAuthorisedFault, EntrySAMLException {

		PersistenceManager pManager = PersistenceManager.getInstance();
		String saml = null;
		DataSet dataSet = null;
		synchronized (pManager) {
			dataSet = pManager.getData().getCompleteDataSet(
					record.getIdentifier().getProjectPrefix());
			saml = getSAMLAssertion(pManager.getData(), dataSet);
		}
		String groupCode = record.getIdentifier().getGroupPrefix();
		try {
			return repositoryClient.canUpdateRecordMetadata(dataSet.getProjectCode(), groupCode, saml);
		} catch (RepositoryServiceFault e) {
			throw new RemoteServiceFault(e);
		}
	}

	public void configureProxyAuthentication(
			ProxyAuthenticationMethods authScheme, String ntDomain) {
		PsyGridClientSocketFactory.setAuthScheme(authScheme);
		PsyGridClientSocketFactory.setNTdomain(ntDomain);
	}

	public String getNtDomain() {
		return PsyGridClientSocketFactory.getNTdomain();
	}

	public ProxyAuthenticationMethods getProxyAuthenticationMethod() {
		return PsyGridClientSocketFactory.getAuthScheme();
	}
	
	public List<String> changeDocumentStatus(
			Collection<DocumentOccurrence> docOccs, DocumentStatus newStatus, String identifier)
			throws IOException, ConnectException, SocketTimeoutException, EntrySAMLException,
			NotAuthorisedFault, RemoteServiceFault, InvalidIdentifierException {
		fireCommitProgressStarted(new ProgressEvent(this));
		List<String> ncNames = new ArrayList<String>();
		try{
			PersistenceManager pManager = PersistenceManager.getInstance();
			List<Long> notChanged = changeDocInstancesStatus(identifier, docOccs, newStatus);
			List<DocumentOccurrence> secDocOccs = new ArrayList<DocumentOccurrence>();
			String secondaryIdentifier = null;
			for (DocumentOccurrence  docOcc : docOccs) {
				if ( !notChanged.contains(docOcc.getId())){
					pManager.changeLocalDocInstanceStatus(
							identifier, docOcc, newStatus);
					String secProjectCode = docOcc.getDocument().getDataSet().getSecondaryProjectCode();
					if ( null != secProjectCode ){
						if ( null == secondaryIdentifier ){
							secondaryIdentifier = pManager.getSecondaryIdentifierMap().get(identifier);
						}
						if ( null != secondaryIdentifier && 
								 null != docOcc.getDocument().getSecondaryDocIndex() && 
								 null != docOcc.getSecondaryOccIndex() ){
							DataSet secondaryDs = pManager.loadDataSet(pManager.getData().getDataSetSummary(secProjectCode));
							Document secDoc = secondaryDs.getDocument(docOcc.getDocument().getSecondaryDocIndex().intValue());
							DocumentOccurrence  secDocOcc = secDoc.getOccurrence(docOcc.getSecondaryOccIndex().intValue());
							if ( !secDocOcc.isLocked() ){
								secDocOccs.add(secDocOcc);
							}
						}
					}
				}
				else{
					ncNames.add(docOcc.getCombinedDisplayText());
				}
			}
			
			//deal with secondary documents (if there are any)
			if ( !secDocOccs.isEmpty() && null != secondaryIdentifier ){
				List<Long> secNotChanged = changeDocInstancesStatus(identifier, secDocOccs, newStatus);
				for ( DocumentOccurrence  secDocOcc: secDocOccs ){
					if ( !secNotChanged.contains(secDocOcc.getId())){
						pManager.changeLocalDocInstanceStatus(
								secondaryIdentifier, secDocOcc, newStatus);
					}
				}
			}
		}
		finally{
			fireCommitProgressEnded(new ProgressEvent(this));
		}
		return ncNames;
	}
	
	/**
	 * Filter a list of ProjectTypes to contain only those that are
	 * marked in the repository as published.
	 * 
	 * @param projects List of projects to filter.
	 * @return Filtered list of projects
	 * @throws ConnectException
	 * @throws SocketTimeoutException
	 */
	private List<ProjectType> getPublishedProjects(List<ProjectType> projects) 
		throws ConnectException, SocketTimeoutException {

		String[] pa = new String[projects.size()];
		for ( int i=0, c=projects.size(); i<c; i++ ){
			pa[i] = projects.get(i).getIdCode();
		}
		String[] ppa = repositoryClient.getPublishedDatasets(pa);
		List<ProjectType> publishedProjects = new ArrayList<ProjectType>();
		for ( ProjectType project: projects ){
			for ( int i=0, c=ppa.length; i<c; i++ ){
				if ( project.getIdCode().equals(ppa[i]) ){
					publishedProjects.add(project);
					break;
				}
			}
		}
		return publishedProjects;
	}
	
	public  Status  getStatusForDocument(Record  record,  long  docOccId)
	throws IOException, RemoteServiceFault, NotAuthorisedFault, EntrySAMLException{
		PersistenceManager pManager = PersistenceManager.getInstance();
		String saml = null;
		synchronized (pManager) {
			saml = getSAMLAssertion(pManager.getData(), record.getDataSet());
		}
		try{
			return repositoryClient.getStatusForDocument(
				record.getIdentifier().getIdentifier(), docOccId, record.getDataSet(), saml);
		}
		catch (RepositoryServiceFault e) {
			throw new RemoteServiceFault(e);
		}

	}

	public boolean canRecordBeRandomized(Record record)
	throws IOException, RemoteServiceFault, NotAuthorisedFault, EntrySAMLException{
		PersistenceManager pManager = PersistenceManager.getInstance();
		String saml = null;
		synchronized (pManager) {
			saml = getSAMLAssertion(pManager.getData(), record.getDataSet());
		}
		try{
			return repositoryClient.canRecordBeRandomized(
				record.getIdentifier().getIdentifier(), saml);
		}
		catch (RepositoryServiceFault e) {
			throw new RemoteServiceFault(e);
		}

	}

	/********************** Sample Tracking *****************************/
	
	/* 
	 * It is pointless declaring a bunch of checked exceptions if you can't recover from them 
	 * and the default exception handler checks their type in any case.
	 */

	public ConfigInfo getSampleConfig(DataSet dataSet) throws Exception {
		PersistenceManager pManager = PersistenceManager.getInstance();
		synchronized (pManager) {
			String saml = getSAMLAssertion(pManager.getData(), dataSet);
			return repositoryClient.getSampleConfig(dataSet.getProjectCode(), saml);
		}
	}

	public ParticipantInfo getSampleParticipant(Record record) throws Exception {
		PersistenceManager pManager = PersistenceManager.getInstance();
		synchronized (pManager) {
			String saml = getSAMLAssertion(pManager.getData(), record.getDataSet());
			return repositoryClient.getSampleParticipant(record.getIdentifier().getIdentifier(), saml);
		}
	}

	public void saveSampleParticipant(Record record, ParticipantInfo participant) throws Exception {
		PersistenceManager pManager = PersistenceManager.getInstance();
		synchronized (pManager) {
			String saml = getSAMLAssertion(pManager.getData(), record.getDataSet());
			repositoryClient.saveSampleParticipant(participant, saml);
		}
	}

	
	public SampleInfo[] getSamples(Record record) throws Exception {
		PersistenceManager pManager = PersistenceManager.getInstance();
		synchronized (pManager) {
			String saml = getSAMLAssertion(pManager.getData(), record.getDataSet());
			return repositoryClient.getSamples(record.getIdentifier().getIdentifier(), saml);
		}
	}

	public SampleInfo saveSample(Record record, SampleInfo sample) throws Exception {
		PersistenceManager pManager = PersistenceManager.getInstance();
		synchronized (pManager) {
			String saml = getSAMLAssertion(pManager.getData(), record.getDataSet());
			return repositoryClient.saveSample(sample, saml);
		}
	}

	public SampleInfo[] getSampleRevisions(Record record,long sampleID) throws Exception {
		PersistenceManager pManager = PersistenceManager.getInstance();
		synchronized (pManager) {
			String saml = getSAMLAssertion(pManager.getData(), record.getDataSet());
			return repositoryClient.getSampleRevisions(sampleID, saml);
		}
	}

	public long getNextSampleNumber(DataSet dataSet) throws Exception {
		PersistenceManager pManager = PersistenceManager.getInstance();
		synchronized (pManager) {
			String saml = getSAMLAssertion(pManager.getData(), dataSet);
			return repositoryClient.getNextSampleNumber(dataSet.getProjectCode(), saml);
		}
	}
/*	

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
*/
	/********************** End Sample Tracking *****************************/
	

}
