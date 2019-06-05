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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.prefs.Preferences;

import javax.crypto.BadPaddingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.collection.entry.DocumentStatus;
import org.psygrid.collection.entry.EntryHelper;
import org.psygrid.collection.entry.ExceptionsHelper;
import org.psygrid.collection.entry.chooser.ChoosableException;
import org.psygrid.collection.entry.persistence.RecordsList.Item;
import org.psygrid.collection.entry.remote.ProxySetting;
import org.psygrid.collection.entry.remote.RemoteManager;
import org.psygrid.collection.entry.remote.RemoteServiceFault;
import org.psygrid.collection.entry.security.DecryptionException;
import org.psygrid.collection.entry.security.EntrySAMLException;
import org.psygrid.collection.entry.security.SecurityManager;
import org.psygrid.collection.entry.security.User;
import org.psygrid.common.proxy.ProxyHelper;
import org.psygrid.common.proxy.ProxyPersistence;
import org.psygrid.data.model.dto.ElementInstanceDTO;
import org.psygrid.data.model.dto.SectionDTO;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.utils.security.NotAuthorisedFault;

import com.myjavatools.lib.Files;
import com.thoughtworks.xstream.XStream;

public class PersistenceManager implements ProxyPersistence {

	private static final Log LOG = LogFactory.getLog(PersistenceManager.class);

	private static final String DATASET_PREFIX = "dataset"; //$NON-NLS-1$
	private static final String FILE_SUFFIX = ".xml"; //$NON-NLS-1$
	private static final String RECORD_PREFIX = "record"; //$NON-NLS-1$
	private static PersistenceManager INSTANCE = new PersistenceManager();
	private final static String WRONG_OBJECT_TYPE_MESSAGE = "Wrong object " + //$NON-NLS-1$
	"type found in file."; //$NON-NLS-1$

	private volatile boolean baseDirInitialized = false;
	private volatile boolean userDirInitialized = false;

	private String persistenceDataLocation;
	private String recordsListLocation;
	private String keyStoreLocation;
	private String identifiersLocation;
	private String unfinishedDocumentLocation;
	private String autoSaveDocumentLocation;
	private String standardCodesLocation;
	private String usersLocation;
	private String proxySettingsLocation;
	private String lockLocation;
	private String eslSubjectsLocation;

	private FileLock fileLock;
	private FileChannel lockChannel;

	private XStream xStream;

	private PersistenceData data;

	private RecordsList recordsList;

	private IdentifiersList identifiers;

	private String trustStoreLocation;

	private ConsentMap2 consentMap;
	private String consentMapLocation;
	private String consentMap2Location;

	private SecondaryIdentifierMap secondaryIdentifierMap;
	private String secondaryIdentifierMapLocation;

	private ExternalIdMap externalIdMap;
	private String externalIdMapLocation;

	private RecordStatusMap2 recordStatusMap;
	private String recordStatusMapLocation;
	private String recordStatusMap2Location;

	private VersionMap versionMap;
	private String versionMapLocation;

	private LastUsedCoCoAVersionMap cocoaVersion;
	private String cocoaVersionMapLocation;

	private ClockOffset clockOffset;
	private String clockOffsetLocation;

	private String baseDir;
	private String proxyDir;

	private String userDir;

	private EslSubjectList eslSubjects;

	private boolean firstLogin = false;
	
	private boolean dsdMode = false;
	

	public PersistenceManager() {
		xStream = new XStream();
	}

	private String createBaseDir(boolean alternativeLocation, int iteration, String ext) {

		String dir = getHomeDir(ext);
		if (alternativeLocation) {
			String testSuffix = "test"; //$NON-NLS-1$
			if (iteration != -1) {
				testSuffix += iteration;
			}
			dir = dir + testSuffix;
		}
		dir = dir + File.separatorChar;
		return dir;
	}


	private String createBaseDir(boolean alternativeLocation, int iteration) {

		String dir = getHomeDir();
		if (alternativeLocation) {
			String testSuffix = "test"; //$NON-NLS-1$
			if (iteration != -1) {
				testSuffix += iteration;
			}
			dir = dir + testSuffix;
		}
		dir = dir + File.separatorChar;
		return dir;
	}

	private String getProxyDir(){
		String home = getUserHome();

		//Find the system component for the file path
		String system = getSystemName();

		String dir = home + File.separator + ".psygrid" + File.separator + system + File.separator; //$NON-NLS-1$
		return dir;
	}

	/**
	 * Get the user specified home of psygrid.
	 * 
	 * Used to establish the base location of the .psygrid 
	 * directory by getHomeDir() and getHomeDir(ext).
	 * 
	 * Uses the psygrid.user.home value stored in the user
	 * preferences, otherwise defaults to the user's home 
	 * directory as specified in the user.home system property.
	 * 
	 * @return
	 */
	public String getUserHome() {
		/*
		 * The Preferences class is a way of storing user preferences independent of
		 * an application. The preferences are persistent and user specific.
		 * 
		 *  In Windows the preferences are stored in the registry. In particular,
		 *  user preferences show up at 
		 *  My_Computer\HKEY_CURRENT_USER\Software\JavaSoft\Prefs\org\psygrid\collection\entry\persistence
		 *  and My_Computer\HKEY_USERSsernamexxx\Software\JavaSoft\Prefs\org\psygrid\collection\entry\persistence   
		 */
		Preferences prefs = Preferences.userNodeForPackage(PersistenceManager.class); 

		String home = prefs.get("psygrid.user.home", null);
		LOG.info("Home is: "+home);

		if (home == null) {
			home = System.getProperty("user.home");
			LOG.info("Home now is: "+home);
		}
		return home;
	}

	boolean deleteBaseDir()    {
		if ((!baseDirInitialized) || (!userDirInitialized)) {
			return false;
		}
		boolean allDeleted = true;
		File userDirFile = new File(userDir);
		for (File userFile : userDirFile.listFiles()) {
			boolean success = userFile.delete();
			if (!success) {
				allDeleted = false;
			}
		}
		boolean success = userDirFile.delete();
		if (!success) {
			allDeleted = false;
		}
		if (baseDir.equals(userDir)) {
			return allDeleted;
		}
		File baseDirFile = new File(baseDir);
		for (File baseFile : baseDirFile.listFiles()) {
			success = baseFile.delete();
			if (!success) {
				allDeleted = false;
			}
		}
		success = baseDirFile.delete();
		if (!success) {
			allDeleted = false;
		}
		return allDeleted;
	}

	public void initBaseDir(String ext) throws IOException {
		if (baseDirInitialized) {
			return;
		}
		initBaseDirLocations(!true, -1, ext);

		for (int i = 0; ; ++i)  {
			File lock = new File(lockLocation);
			if (lock.exists() == false) {
				lock.createNewFile();
			}

			lockChannel = new RandomAccessFile(lock, "rw").getChannel(); //$NON-NLS-1$

			fileLock = lockChannel.tryLock();
			if (fileLock == null) {
				if (true) {
					throw new FileLockException("File is already locked."); //$NON-NLS-1$
				}

				initBaseDirLocations(!true, i, ext);

			} else {
				break;
			}
		}

		File usersFile = new File(usersLocation);

		if (usersFile.exists() == false) {
			save(new ArrayList<User>(0), usersLocation);
		}

		// Bug#487
		final String PACKAGE = "org/psygrid/collection/entry/security/"; //$NON-NLS-1$
		File keyStoreFile = new File(keyStoreLocation);
		initStore(PACKAGE + "defaultKeystore.jks", keyStoreFile); //$NON-NLS-1$

		File trustStoreFile = new File(trustStoreLocation);
		initStore(PACKAGE + "truststore.jks", trustStoreFile); //$NON-NLS-1$

		File versionFile = new File(versionMapLocation);
		if ( versionFile.exists() ){
			versionMap = loadVersionMap();
		}
		else{
			versionMap = new VersionMap();
		}

		baseDirInitialized = true;

	}

	public void initBaseDir() throws IOException {
		initBaseDir(true);
	}

	void initBaseDir(boolean doLock) throws IOException {
		if (baseDirInitialized) {
			return;
		}
		initBaseDirLocations(!doLock, -1);

		for (int i = 0; ; ++i)  {
			File lock = new File(lockLocation);
			if (lock.exists() == false) {
				lock.createNewFile();
			}

			lockChannel = new RandomAccessFile(lock, "rw").getChannel(); //$NON-NLS-1$

			fileLock = lockChannel.tryLock();
			if (fileLock == null) {
				if (doLock) {
					throw new FileLockException("File is already locked."); //$NON-NLS-1$
				}

				initBaseDirLocations(!doLock, i);

			} else {
				break;
			}
		}

		File usersFile = new File(usersLocation);

		if (usersFile.exists() == false) {
			save(new ArrayList<User>(0), usersLocation);
		}

		// Bug#487
		final String PACKAGE = "org/psygrid/collection/entry/security/"; //$NON-NLS-1$
		File keyStoreFile = new File(keyStoreLocation);
		initStore(PACKAGE + "defaultKeystore.jks", keyStoreFile); //$NON-NLS-1$

		File trustStoreFile = new File(trustStoreLocation);
		initStore(PACKAGE + "truststore.jks", trustStoreFile); //$NON-NLS-1$

		File versionFile = new File(versionMapLocation);
		if ( versionFile.exists() ){
			versionMap = loadVersionMap();
		}
		else{
			versionMap = new VersionMap();
		}

		File clockFile = new File(clockOffsetLocation);
		if ( clockFile.exists() ){
			clockOffset = loadClockOffset();
		}
		else{
			clockOffset = new ClockOffset();
		}

		baseDirInitialized = true;
	}

	private void initBaseDirLocations(boolean alternativeLocation, int iteration, String ext) {
		baseDir = createBaseDir(alternativeLocation, iteration, ext);
		proxyDir = getProxyDir();
		usersLocation = baseDir + "users.xml"; //$NON-NLS-1$
		trustStoreLocation = baseDir + "truststore.jks"; //$NON-NLS-1$
		keyStoreLocation = baseDir + "keystore.jks"; //$NON-NLS-1$
		versionMapLocation = baseDir + "versionmap.xml"; //$NON-NLS-1$
		proxySettingsLocation = proxyDir + "proxies.xml"; //$NON-NLS-1$
		clockOffsetLocation = proxyDir + "clock.xml"; //$NON-NLS-1$
		cocoaVersionMapLocation = baseDir + "lastUsedCoCoaVersion.xml"; //$NON-NLS-1$

		File dirFile = new File(baseDir);
		if (dirFile.exists() == false) {
			dirFile.mkdirs();
		}
		lockLocation = baseDir + "lock"; //$NON-NLS-1$

	}

	private void initBaseDirLocations(boolean alternativeLocation, int iteration) {
		baseDir = createBaseDir(alternativeLocation, iteration);
		proxyDir = getProxyDir();
		usersLocation = baseDir + "users.xml"; //$NON-NLS-1$
		trustStoreLocation = baseDir + "truststore.jks"; //$NON-NLS-1$
		keyStoreLocation = baseDir + "keystore.jks"; //$NON-NLS-1$
		versionMapLocation = baseDir + "versionmap.xml"; //$NON-NLS-1$
		proxySettingsLocation = proxyDir + "proxies.xml"; //$NON-NLS-1$
		clockOffsetLocation = proxyDir + "clock.xml"; //$NON-NLS-1$
		cocoaVersionMapLocation = baseDir + "lastUsedCoCoaVersion.xml"; //$NON-NLS-1$

		File dirFile = new File(baseDir);
		if (dirFile.exists() == false) {
			dirFile.mkdirs();
		}
		lockLocation = baseDir + "lock"; //$NON-NLS-1$

	}

	public final String getBaseDirLocation() {
		return baseDir;
	}

	public final String getUserDirLocation() {
		return userDir;
	}

	/**
	 * Creates, if necessary, files in the user directory required for the 
	 * PersistenceManager to work correctly.
	 * 
	 * @throws IOException
	 *             if there is a problem executing these operations.
	 * @see #initBaseDir()
	 */
	public void initUserDir() throws IOException {
		if (userDirInitialized) {
			return;
		}

		setAliases();
		File pData = new File(persistenceDataLocation);

		if (pData.exists()) {
			data = loadPersistenceData();
		}
		else {
			data = new PersistenceData();
			savePersistenceData();
		}

		File identifiersFile = new File(identifiersLocation);

		if (identifiersFile.exists()) {
			identifiers = loadIdentifiers();
		}
		else {
			identifiers = new IdentifiersList();
			saveIdentifiers();
		}

		File stdCodesFile = new File(standardCodesLocation);

		if (stdCodesFile.exists() == false) {
			save(new ArrayList<StandardCode>(0), standardCodesLocation);
		}

		File consentFile = new File(consentMapLocation);
		File consentFile2 = new File(consentMap2Location);
		if ( consentFile2.exists() ){
			consentMap = loadConsentMap();
		}
		else if (consentFile.exists() ){
			//convert existing consent map into new format
			convertOldConsentMap();
		}
		else{
			consentMap = new ConsentMap2();
		}

		File secondidFile = new File(secondaryIdentifierMapLocation);
		if ( secondidFile.exists() ){
			secondaryIdentifierMap = loadSecondaryIdentifierMap();
		}
		else{
			secondaryIdentifierMap = new SecondaryIdentifierMap();
		}

		File externalIdFile = new File(externalIdMapLocation);
		if ( externalIdFile.exists() ){
			externalIdMap = loadExternalIdMap();
		}
		else{
			externalIdMap = new ExternalIdMap();
		}

		File recordStatusFile = new File(recordStatusMapLocation);
		File recordStatusFile2 = new File(recordStatusMap2Location);
		if ( recordStatusFile2.exists() ){
			recordStatusMap = loadRecordStatusMap();
		}
		else if (recordStatusFile.exists() ){
			//convert existing record status map into new format
			convertOldRecordStatusMap();
		}
		else{
			recordStatusMap = new RecordStatusMap2();
		}

		File CoCoAVersionFile = new File(cocoaVersionMapLocation);
		if(CoCoAVersionFile.exists()){
			cocoaVersion = this.loadCoCoAVersionMap();
		}else{
			cocoaVersion = new LastUsedCoCoAVersionMap();
		}

		userDirInitialized = true;
	}

	/**
	 * Initialize the PersistenceManager when running in test/preview mode.
	 * <p>
	 * Only the standard codes are loaded from file. All other objects managed
	 * by the persistence manager are newly constructed here.
	 */
	public void initForPreview(){
		if (userDirInitialized) {
			return;
		}

		setAliases();
		data = new PersistenceData();
		identifiers = new IdentifiersList();

		standardCodesLocation = RemoteManager.getInstance().getTestStdcodePath();

		consentMap = new ConsentMap2();

		secondaryIdentifierMap = new SecondaryIdentifierMap();

		externalIdMap = new ExternalIdMap();

		recordStatusMap = new RecordStatusMap2();

		userDirInitialized = true;
	}

	/**
	 * Set the locations for all files in the user directory.
	 * 
	 * @param userPrefix The name of the users folder in the base directory.
	 */
	public void initUserLocations(String userPrefix) {
		if (userDirInitialized) {
			return;
		}

		userDir = baseDir + userPrefix + File.separator;

		persistenceDataLocation = userDir + "pdata.xml"; //$NON-NLS-1$
		standardCodesLocation = userDir + "stdcodes.xml"; //$NON-NLS-1$
		unfinishedDocumentLocation = userDir + "unfdocinstance.xml"; //$NON-NLS-1$
		autoSaveDocumentLocation = userDir + "autosavedocinstance.xml";
		identifiersLocation = userDir + "identifiers.xml"; //$NON-NLS-1$
		recordsListLocation = userDir + "recordslist.xml"; //$NON-NLS-1$
		consentMapLocation = userDir + "consentmap.xml"; //$NON-NLS-1$
		consentMap2Location = userDir + "consentmap2.xml"; //$NON-NLS-1$
		secondaryIdentifierMapLocation = userDir + "secondidmap.xml"; //$NON-NLS-1$
		externalIdMapLocation = userDir + "externalidmap.xml";
		recordStatusMapLocation = userDir + "recordstatusmap.xml"; //$NON-NLS-1$
		recordStatusMap2Location = userDir + "recordstatusmap2.xml"; //$NON-NLS-1$
		eslSubjectsLocation = userDir + "eslsubjects.xml"; //$NON-NLS-1$

		File dirFile = new File(userDir);
		if (dirFile.exists() == false) {
			firstLogin = true;
			dirFile.mkdirs();
		}
	}

	private void initStore(String storeSource, File storeFile)
	throws IOException {

		if (storeFile.exists()) {
			storeFile.delete();
		}
		storeFile.createNewFile();
		InputStream sourceStream = Thread.currentThread()
		.getContextClassLoader().getResourceAsStream(storeSource);
		try {
			copyInputStreamToFile(sourceStream, storeFile);
		} finally {
			if (sourceStream != null) {
				sourceStream.close();
			}
		}

	}

	private void copyInputStreamToFile(InputStream source, File destination) throws IOException {
		FileOutputStream outputStream = null;

		try {
			outputStream = new FileOutputStream(destination);
			int input;
			while ((input = source.read()) != -1) {
				outputStream.write(input);
			}
			outputStream.flush();
		}
		finally {
			if (outputStream != null) {
				outputStream.close();
			}
		}
	}

	private RecordsList loadRecordsList() throws IOException, 
	DecryptionException    {
		try {
			String cipherText = loadToString(recordsListLocation);
			String xml = SecurityManager.getInstance().decrypt(cipherText);
			Object recList = xStream.fromXML(xml);
			if (recList instanceof RecordsList) {
				return (RecordsList) recList;
			}
			throw new IOException(getWrongObjectTypeFoundMsg(RecordsList.class, 
					recList));
		} catch (BadPaddingException e) {
			throw new DecryptionException(Messages.getString("PersistenceManager.decryptFailureMessage"), e);
		}
	}

	public RecordsList getRecordsList() throws IOException, DecryptionException {
		if (recordsList == null) {
			initRecordsList();
		}
		return recordsList;
	}

	public void deleteRecordsList() throws IOException {
		File f = new File(recordsListLocation);
		if (f.exists()) {
			delete(recordsListLocation);
		}
	}

	public void deleteEslSubjectsList() throws IOException {
		File f = new File(eslSubjectsLocation);
		if (f.exists()) {
			delete(eslSubjectsLocation);
		}
	}

	// TODO Add more aliases
	public void setAliases() {
		//hibernate
		String hibernatePrefix = "p.m.h."; //$NON-NLS-1$
		xStream.alias(hibernatePrefix + "resp", Response.class); //$NON-NLS-1$
		xStream.alias(hibernatePrefix + "doc", Document.class); //$NON-NLS-1$
		xStream.alias(hibernatePrefix + "elem", Element.class); //$NON-NLS-1$
		xStream.alias(hibernatePrefix + "eleminst", ElementInstanceDTO.class); //$NON-NLS-1$
		xStream.alias(hibernatePrefix + "dateentry", DateEntry.class); //$NON-NLS-1$
		xStream.alias(hibernatePrefix + "txtentry", TextEntry.class); //$NON-NLS-1$
		xStream.alias(hibernatePrefix + "numentry", NumericEntry.class); //$NON-NLS-1$
		xStream.alias(hibernatePrefix + "optentry", OptionEntry.class); //$NON-NLS-1$
		xStream.alias(hibernatePrefix + "longtxtentry", LongTextEntry.class); //$NON-NLS-1$
		xStream.alias(hibernatePrefix + "option", Option.class); //$NON-NLS-1$
		xStream.alias(hibernatePrefix + "docgroup", DocumentGroup.class); //$NON-NLS-1$
		xStream.alias(hibernatePrefix + "dococc",  //$NON-NLS-1$
				DocumentOccurrence.class);
		xStream.alias(hibernatePrefix + "dataset", DataSet.class); //$NON-NLS-1$
		xStream.alias(hibernatePrefix + "numvalrule",  //$NON-NLS-1$
				NumericValidationRule.class);
		xStream.alias(hibernatePrefix + "txtvalrule",  //$NON-NLS-1$
				TextValidationRule.class);
		xStream.alias(hibernatePrefix + "datevalrule",  //$NON-NLS-1$
				DateValidationRule.class);
		xStream.alias(hibernatePrefix + "status", Status.class); //$NON-NLS-1$
		xStream.alias(hibernatePrefix + "sec", SectionDTO.class); //$NON-NLS-1$
		xStream.alias(hibernatePrefix + "secocc", SectionOccurrence.class); //$NON-NLS-1$
		xStream.alias(hibernatePrefix + "primconsent", PrimaryConsentForm.class); //$NON-NLS-1$
		xStream.alias(hibernatePrefix + "consentgroup", ConsentFormGroup.class); //$NON-NLS-1$
		xStream.alias(hibernatePrefix + "optdep", OptionDependent.class); //$NON-NLS-1$
		xStream.alias(hibernatePrefix + "compentry", CompositeEntry.class); //$NON-NLS-1$

		//client classes
		String clientPrefix = "p.c.e.p."; //$NON-NLS-1$
		xStream.alias(clientPrefix + "dss", DataSetSummary.class); //$NON-NLS-1$
	}

	public boolean isBaseDirInitialized() {
		return baseDirInitialized;
	}

	public void savePersistenceData() throws IOException {
		save(data, persistenceDataLocation);
	}

	
	private UnfinishedDocInstance loadUnfinishedDocInstance(String fileName) 
	throws FileNotFoundException, IOException, DecryptionException  {
		String cipher = loadToString(fileName);
		String clearText;
		try {
			clearText = SecurityManager.getInstance().decrypt(cipher);
		} catch (BadPaddingException e) {
			throw new DecryptionException("Failed to decrypt the document", e);
		}
		Object obj = xStream.fromXML(clearText);
		if (obj instanceof UnfinishedDocInstance) {
			UnfinishedDocInstance docInstance = (UnfinishedDocInstance) obj;
			Record record = docInstance.getDocOccurrenceInstance().getRecord();
			DataSet dataSet = data.getCompleteDataSet(
                    record.getIdentifier().getProjectPrefix());

			record.attach(dataSet);
			return docInstance;
		}

		throw new IOException(getWrongObjectTypeFoundMsg(
				UnfinishedDocInstance.class, obj));
	}

	public Record loadRecord(RecordsListWrapper.Item item)
	throws FileNotFoundException, IOException, DecryptionException {
		return loadRecord(item.getIdentifier(), item.isReadyToCommit());
	}
	
	public Record loadRecord(RecordsList.Item item)
	throws FileNotFoundException, IOException, DecryptionException {
		return loadRecord(item.getIdentifier(), item.isReadyToCommit());
	}

	public Record loadRecord(Identifier identifier, boolean readyToCommit)
	throws FileNotFoundException, IOException, DecryptionException    {
		String fullFileName = getRecordFileName(identifier, readyToCommit);
		return loadRecord(fullFileName);
	}

	public Record loadRecord(String identifier, boolean readyToCommit)
	throws FileNotFoundException, IOException, DecryptionException {
		String fullFileName = getRecordFileName(identifier, readyToCommit);
		return loadRecord(fullFileName);
	}

	private Record loadRecord(String fileName)
	throws FileNotFoundException, IOException, DecryptionException {
		String cipherText = loadToString(fileName);
		String clearText;
		try {
			clearText = SecurityManager.getInstance().decrypt(cipherText);
			Object obj = xStream.fromXML(clearText);
			if (obj instanceof Record) {
				Record record = (Record) obj;
				DataSet dataSet = data.getCompleteDataSet(
                        record.getIdentifier().getProjectPrefix());
				record.attach(dataSet);
				return record;
			}

			throw new IOException(getWrongObjectTypeFoundMsg(Record.class,
					obj));

		} catch (BadPaddingException e) {
			throw new DecryptionException("Failed to decrypt the record", e);
		}
	}

	public UnfinishedDocInstance loadUnfinishedDocInstance() 
	throws IOException, DecryptionException {
		try {
			return loadUnfinishedDocInstance(
					unfinishedDocumentLocation);
		}
		catch(FileNotFoundException fne) {
			return null;
		}
	}

	public void deleteUnfinishedDocumentInstance() throws IOException {
		delete(unfinishedDocumentLocation);
	}

	public UnfinishedDocInstance loadAutoSaveDocInstance() 
	throws IOException, DecryptionException {
		try {
			return loadUnfinishedDocInstance(
					autoSaveDocumentLocation);
		}
		catch(FileNotFoundException fne) {
			return null;
		}
	}
	
	public UnfinishedDocInstance loadUncommitableDocInstance(Record record, DocumentOccurrence docOccurrence)
		throws IOException, DecryptionException {
				
		String fileName = this.getUncommitableDocumentInstanceFileName(record, docOccurrence);
		
		String cipher = loadToString(fileName);
		String clearText;
		try {
			clearText = SecurityManager.getInstance().decrypt(cipher);
		} catch (BadPaddingException e) {
			throw new DecryptionException("Failed to decrypt the document", e);
		}
		Object obj = xStream.fromXML(clearText);
		if (obj instanceof UnfinishedDocInstance) {
			UnfinishedDocInstance docInstance = (UnfinishedDocInstance) obj;
			//docInstance.getDocOccurrenceInstance().detachFromRecord();

			return docInstance;
		}
		
		throw new IOException(getWrongObjectTypeFoundMsg(
				UnfinishedDocInstance.class, obj));
	}

	public void deleteAutoSaveDocumentInstance() throws IOException {
		//If cancel is clicked from the login dialog autoSaveDocumentLocation
		//will be null...
		if ( null != autoSaveDocumentLocation ){
			File file = new File(autoSaveDocumentLocation);
			if ( file.exists()){
				delete(autoSaveDocumentLocation);
			}
		}
	}
	
	public void deleteCommitFailedDocFile(Record record, DocumentOccurrence docOcc) throws IOException{
		String fileName = getUncommitableDocumentInstanceFileName(record, docOcc);
		File file = new File(fileName);
		if(file.exists()){
			delete(fileName);
		}
	}
	
	/**
	 * Saves an uncommitable document instance. This may occur, for example if the document instance
	 * has un-transformable data or there is a general save error at the database.
	 * Updates the doc status of the instance to COMMIT_FAILED.
	 * @param docInstance
	 * @throws IOException
	 */
	public synchronized void saveUncommitableDocumentInstance(UnfinishedDocInstance unfDocInstance) throws IOException{
		String destinationFileName = getUncommitableDocumentInstanceFileName(
				unfDocInstance.getDocOccurrenceInstance().getRecord(), 
				unfDocInstance.getDocOccurrenceInstance().getOccurrence());
		//TODO does it need to be done this way, or can we use a normal detach?
		unfDocInstance.softDetach();
		String xml = xStream.toXML(unfDocInstance);
		String cipher = SecurityManager.getInstance().encrypt(xml);
		saveString(cipher, destinationFileName);
	}
	
	public String getUncommitableDocumentInstanceFileName(Record record, DocumentOccurrence docOccurrence){
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(userDir);
		strBuilder.append("error");
		String firstPart = record.getIdentifier().getIdentifier();
		firstPart = firstPart.replace("/", "-");
		strBuilder.append(firstPart);
		strBuilder.append("-");
		strBuilder.append(docOccurrence.getId().toString());
		strBuilder.append(FILE_SUFFIX);
		return strBuilder.toString();
	}

	public void saveUnfinishedDocumentInstance(UnfinishedDocInstance docInstance) 
	throws IOException {
		docInstance.getDocOccurrenceInstance().getRecord().detach();
		String xml = xStream.toXML(docInstance);
		String cipher = SecurityManager.getInstance().encrypt(xml);
		saveString(cipher, unfinishedDocumentLocation);
	}

	public synchronized void saveAutoSaveDocumentInstance(UnfinishedDocInstance docInstance) 
	throws IOException {
		docInstance.softDetach();
		String xml = xStream.toXML(docInstance);
		String cipher = SecurityManager.getInstance().encrypt(xml);
		saveString(cipher, autoSaveDocumentLocation);
	}

	private String getReadyToCommitSuffix(boolean readyToCommit) {
		return readyToCommit ? "-1" : "-0"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	private String getRecordFileName(Identifier identifier, boolean readyToCommit) {
		return getRecordFileName(identifier.getIdentifier(), readyToCommit);
	}

	private String getRecordFileName(String identifier, boolean readyToCommit) {
		StringBuilder fullFileName = new StringBuilder(50);
		String identifierText = getProcessedIdentifier(identifier);
		fullFileName.append(userDir).append(RECORD_PREFIX).append(identifierText);
		fullFileName.append(getReadyToCommitSuffix(readyToCommit));
		fullFileName.append(FILE_SUFFIX);
		return fullFileName.toString();
	}

	/**
	 * Saves the record. It detaches the dataSet before the operation and
	 * attaches back afterwards. <code>record</code> should not be used
	 * while this operation is in progress.
	 * 
	 * @param record
	 * @param readyToCommit
	 * @throws IOException
	 * @throws DecryptionException
	 */
	public void saveRecord(Record record, boolean readyToCommit) throws IOException, DecryptionException   {
		if (recordsList == null) {
			initRecordsList();
		}
		DataSet dataSet = record.getDataSet();
		record.detach();
		String xml = xStream.toXML(record);
		record.attach(dataSet);
		String cipherText = SecurityManager.getInstance().encrypt(xml);
		saveString(cipherText, getRecordFileName(record.getIdentifier(), 
				readyToCommit));
		recordsList.addItem(record.getIdentifier(), readyToCommit);
		saveRecordsList();
		if ( consentMap.addRecordNoOverwrite(
				record.getIdentifier().getIdentifier(), 
				record.getAllConsents()) ){
			saveConsentMap();
		}
		if ( recordStatusMap.addRecordNoOverwrite(
				record.getIdentifier().getIdentifier(),
				record.getStatus()) ){
			saveRecordStatusMap();
		}
		if ( dataSet.isExternalIdUsed() ){
			if ( externalIdMap.addNoOverwrite(
					record.getIdentifier().getIdentifier(),
					record.getExternalIdentifier()) ){
				saveExternalIdMap();
			}
		}
	}

	/**
	 * Attach a document instance to a record by adding the given document instance to 
	 * the record status map for the specified record.
	 * 
	 * @param record
	 * @param docInst
	 * @param readyToCommit
	 * @throws IOException
	 * @throws DecryptionException
	 */
	public void updateRecord(Record record, DocumentInstance docInst, boolean readyToCommit) throws IOException, DecryptionException {
		//For Locally saved records (these always have a status of incomplete..)

		//Document has been completed
		if (readyToCommit) {
			recordStatusMap.addDocStatus(record.getIdentifier().getIdentifier(), docInst.getOccurrence(), DocumentStatus.READY_TO_SUBMIT);
		}
		else {
			recordStatusMap.addDocStatus(record.getIdentifier().getIdentifier(), docInst.getOccurrence(), DocumentStatus.LOCALLY_INCOMPLETE);	
		}
		saveRecordStatusMap();
		updateRecordStatus(record, docInst);
	}
	
	public void updateRecord(Record record, DocumentInstance docInst, DocumentStatus newDocumentStatus) throws IOException{
		if ( DocumentStatus.NOT_STARTED == newDocumentStatus){
			recordStatusMap.removeDocStatus(record.getIdentifier().getIdentifier(), docInst.getOccurrence());
		}
		else{
			recordStatusMap.addDocStatus(record.getIdentifier().getIdentifier(), docInst.getOccurrence(), newDocumentStatus);
		}
		saveRecordStatusMap();
		updateRecordStatus(record, docInst);
		
	}
	
	/**
	 * Get the current status of a document instance as stored in the record status map.
	 * 
	 * @param record The record
	 * @param docInst The document instance
	 * @return The status.
	 */
	public Status getStatusOfDocument(Record record, DocumentInstance docInst){
		return recordStatusMap.getStatusOfDocumentInstance(record, docInst.getOccurrence());
	}

	/**
	 * Update the status of the recently saved document instance in the RecordStatusMap
	 * 
	 * @throws IOException 
	 */
	public void changeLocalDocInstanceStatus(DocumentInstance docInst, DocumentStatus status) throws IOException {

		Record record = docInst.getRecord();

		if (record == null) {
			return;
		}

		//Update status
		PersistenceManager pManager = PersistenceManager.getInstance();
		synchronized (pManager) {
			pManager.getRecordStatusMap().addDocStatus(record.getIdentifier().getIdentifier(), docInst.getOccurrence(), status);
			pManager.saveRecordStatusMap();
			//Don't automatically change the record status - only pending/approved/rejected documents call this method so it's not required
			//This also only changes the local record and not the one in the repository, so it doesn't really work!
			//updateRecordStatus(record, docInst);
		}
	}

	/**
	 * Update the status stored in the record status map for the given document 
	 * occurrence and paticipant identifier.
	 * 
	 * @param identifier The participant identifier
	 * @param docOcc The document occurrence
	 * @param status The new status
	 * @throws IOException
	 */
	public void changeLocalDocInstanceStatus(String identifier, DocumentOccurrence docOcc, DocumentStatus status) throws IOException {

		//Update status
		PersistenceManager pManager = PersistenceManager.getInstance();
		synchronized (pManager) {
			pManager.getRecordStatusMap().addDocStatus(identifier, docOcc, status);
			pManager.saveRecordStatusMap();
		}
	}

	/**
	 * Used to update the record status on completion of a document if
	 * this means that its parent document group is now complete
	 * 
	 * @param record The record
	 * @param docInst The document instance just completed
	 * @return 
	 * @throws IOException
	 */
	public Status updateRecordStatus(Record record, DocumentInstance docInst) throws IOException {
		DocumentGroup docGroup = docInst.getOccurrence().getDocumentGroup();
		Status status = docGroup.getUpdateStatus();
		Status oldStatus = null;
		if ( RemoteManager.getInstance().isTestDataset() ){
			//Running in test/preview mode
			//Get the current status straight from the record
			oldStatus = record.getStatus();
		}
		else{
			//Running in normal mode
			//Get the current status from the record status map
			oldStatus = recordStatusMap.getStatusForRecord(record.getIdentifier().getIdentifier());
		}

		if (status != null && oldStatus != null) {	
			//if document group is now complete
			if ( isDocumentGroupCompleted(record, docGroup) ) {
				//update record with given status (assuming that the status is allowed..)
				if ( !oldStatus.getShortName().equals(status.getShortName()) ) {
					boolean statusAllowed = false;
					for (int i = 0; i <oldStatus.numStatusTransitions(); i++) {
						if ( oldStatus.getStatusTransition(i).getShortName().equals(status.getShortName()) ) {
							statusAllowed = true;
							break;
						}
					}

					if (statusAllowed) {
						updateRecordStatus(record, status);
						return status;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Update the record status locally.
	 * 
	 * @param record The record
	 * @param newStatus The new status
	 * @throws IOException
	 */
	public void updateRecordStatus(Record record, Status newStatus) throws IOException {
		if (newStatus != null) {
			((Record)record).setStatus(newStatus);
			if ( RemoteManager.getInstance().isTestDataset() ){
				//Running in test/preview mode
				//Just change the status
				return;
			}
			//Running in normal mode
			if (GenericState.INACTIVE.equals(newStatus.getGenericState())) {
				//Moving to an inactive state so all documents will have been removed, so remove from the docStatusMap
				recordStatusMap.deleteRecord(record.getIdentifier().getIdentifier());
				((Record)record).getDocInstances().clear();
			}

			recordStatusMap.addRecord(
					record.getIdentifier().getIdentifier(),
					newStatus);
			saveRecordStatusMap();

			//look for any locally persisted records that need updating too
			//not sure if this is strictly necessary, but doing it anyway
			//for completeness
			try{
				Record completeRecord = loadRecord(record.getIdentifier(), true);
				((Record)completeRecord).setStatus(newStatus);
				saveRecord(completeRecord, true);
			}
			catch(FileNotFoundException ex){
				//do nothing - no complete record exists locally
			}
			catch(DecryptionException ex){
				//should never happen
				ExceptionsHelper.handleFatalException(ex);
			}
			try{
				Record completeRecord = loadRecord(record.getIdentifier(), false);
				((Record)completeRecord).setStatus(newStatus);
				saveRecord(completeRecord, false);
			}
			catch(FileNotFoundException ex){
				//do nothing - no incomplete record exists locally
			}
			catch(DecryptionException ex){
				//should never happen
				ExceptionsHelper.handleFatalException(ex);
			}

		}
		return;
	}

	/**
	 * Update the record status locally.
	 * 
	 * @param record
	 * @param newStatus
	 * @throws IOException
	 */
	public void updateRecordMetadata(Record record, RecordData recordData) throws IOException {
		((Record)record).setTheRecordData((RecordData)recordData);

		if ( RemoteManager.getInstance().isTestDataset() ){
			//Running in test/preview mode
			return;
		}

		//look for any locally persisted records that need updating too
		//not sure if this is strictly necessary, but doing it anyway
		//for completeness
		try{
			Record completeRecord = loadRecord(record.getIdentifier(), true);
			((Record)completeRecord).setTheRecordData((RecordData)recordData);
			saveRecord(completeRecord, true);
		}
		catch(FileNotFoundException ex){
			//do nothing - no complete record exists locally
		}
		catch(DecryptionException ex){
			//should never happen
			ExceptionsHelper.handleFatalException(ex);
		}
		try{
			Record completeRecord = loadRecord(record.getIdentifier(), false);
			((Record)completeRecord).setTheRecordData((RecordData)recordData);
			saveRecord(completeRecord, false);
		}
		catch(FileNotFoundException ex){
			//do nothing - no incomplete record exists locally
		}
		catch(DecryptionException ex){
			//should never happen
			ExceptionsHelper.handleFatalException(ex);
		}
		return;
	}

	/**
	 * Determine whether a document group has been completed for the given record.
	 * 
	 * This checks the status of all relevant document instances and returns true if
	 * all document instances have been completed, i.e are ready to commit, pending 
	 * approval or rejected, but not incomplete. Any document occurrences to be 
	 * completed by a linked record using dual data entry are ignored.
	 * 
	 * @param record
	 * @param docGroup
	 * @return isCompleted
	 */
	public boolean isDocumentGroupCompleted(Record record, DocumentGroup docGroup) {

		List<DocumentOccurrence> documentOccs = new ArrayList<DocumentOccurrence>();

		if (record != null && record.getDataSet() != null && ((DataSet)record.getDataSet()).getDocuments() != null) {
			for (Document document: ((DataSet)record.getDataSet()).getDocuments()) {
				for (DocumentOccurrence docOcc: document.getOccurrences()) {				
					if (docOcc.getDocumentGroup().getName().equals(docGroup.getName())) {
						documentOccs.add(docOcc);
					}
				}
			}
		}
		else {
			return false;		//no documents found
		}

		//Look at all Document Occurrences for the Document Group
		for (DocumentOccurrence occurrence: documentOccs) {
			if (occurrence.getDocumentGroup().getName().equals(docGroup.getName())) {
				if (occurrence.isLocked() || checkDocumentDde(record, occurrence)) {
					continue;	//ignores locked (inactive) documents and DDE documents
				}

				Status status = this.recordStatusMap.getStatusOfDocumentInstance(record, occurrence);

				//if a documentInstance is not present or not 'completed' then this DocGroup has not been completed
				if (status == null) {
					return false;
				}

				if ( DocumentStatus.INCOMPLETE.toString().equals(status.getShortName()) 
						|| DocumentStatus.LOCALLY_INCOMPLETE.toString().equals(status.getShortName())) {
					return false;
				}

			}
		}

		return true;
	}

	private String getProcessedIdentifier(String identifier) {
		String identifierString = identifier;
		return identifierString.replace("/", "-");  //$NON-NLS-1$//$NON-NLS-2$
	}

	private void initRecordsList() throws IOException, DecryptionException   {
		File recordsListFile = new File(recordsListLocation);

		if (recordsListFile.exists()) {
			recordsList = loadRecordsList();
		}
		else {
			recordsList = new RecordsList();
			saveRecordsList();
		}
		recordsList.sort();
	}

	public final void saveRecordsList() throws IOException, DecryptionException   {
		if (recordsList == null) {
			initRecordsList();
		}
		String xml = xStream.toXML(recordsList);
		String cipherText = SecurityManager.getInstance().encrypt(xml);
		saveString(cipherText, recordsListLocation);
	}

	public void saveConsentMap() throws IOException {
		save(consentMap, consentMap2Location);
	}

	public void saveSecondaryIdentifierMap() throws IOException {
		save(secondaryIdentifierMap, secondaryIdentifierMapLocation);
	}

	public void saveExternalIdMap() throws IOException {
		save(externalIdMap, externalIdMapLocation);
	}

	public void saveRecordStatusMap() throws IOException {
		save(recordStatusMap, recordStatusMap2Location);
	}

	public void saveVersionMap() throws IOException {
		save(versionMap, versionMapLocation);
	}

	public EslSubjectList getEslSubjectsList() throws IOException, DecryptionException {
		if (eslSubjects == null) {
			initEslSubjectsList();
		}
		return eslSubjects;
	}

	private void initEslSubjectsList() throws IOException, DecryptionException   {
		File eslSubjectsListFile = new File(eslSubjectsLocation);

		if (eslSubjectsListFile.exists()) {
			eslSubjects = loadEslSubjectsList();
		}
		else {
			eslSubjects = new EslSubjectList();
			saveEslSubjectsList();
		}
	}

	private EslSubjectList loadEslSubjectsList() throws IOException, DecryptionException    {
		try {
			String cipherText = loadToString(eslSubjectsLocation);
			String xml = SecurityManager.getInstance().decrypt(cipherText);
			Object eslList = xStream.fromXML(xml);
			if (eslList instanceof EslSubjectList) {
				return (EslSubjectList) eslList;
			}
			throw new IOException(getWrongObjectTypeFoundMsg(RecordsList.class, 
					eslList));
		} catch (BadPaddingException e) {
			throw new DecryptionException("Failed to decrypt records list", e);
		}
	}

	public final void saveEslSubjectsList() throws IOException, DecryptionException   {
		if (eslSubjects == null) {
			initEslSubjectsList();
		}
		String xml = xStream.toXML(eslSubjects);
		String cipherText = SecurityManager.getInstance().encrypt(xml);
		saveString(cipherText, eslSubjectsLocation);
	}

	void saveString(String string, String file) throws IOException  {
		BufferedWriter out = new BufferedWriter(new FileWriter(file));
		try {
			out.write(string);
			out.flush();
		} finally {
			out.close();
		}
	}

	void saveBytes(byte[] bytes, String file) throws IOException    {
		BufferedOutputStream out = new BufferedOutputStream(
				new FileOutputStream(file));
		try {
			out.write(bytes);
			out.flush();
		} finally {
			out.close();
		}
	}

	/**
	 * Saves <code>persistable</code> as an XML representation in the location
	 * given by <code>file</code>.
	 */
	public void save(Object persistable, String file) throws IOException   {
		String xml = xStream.toXML(persistable);
		saveString(xml, file);
	}

	private String getDataSetLocation(Long dataSetId) {
		StringBuilder file = new StringBuilder(50);
		file.append(userDir).append(DATASET_PREFIX);
		file.append(dataSetId).append(FILE_SUFFIX);
		return file.toString();
	}

	public void saveDataSet(DataSet dataSet) throws IOException {
		save(dataSet, getDataSetLocation(dataSet.getId()));
	}

	public DataSet loadDataSet(DataSetSummary summary) throws FileNotFoundException, IOException {
		return loadDataSet(getDataSetLocation(summary.getId()));
	}

	@SuppressWarnings("unchecked")
	public List<User> loadUsers() throws IOException    {
		Object usersObj = load(usersLocation);
		List<User> users = new ArrayList<User>();
		if (usersObj instanceof List) {
			List<?> rawUsers = (List<?>) usersObj;
			for (Object rawUser : rawUsers) {
				if (rawUser instanceof User) {
					users.add((User) rawUser);
				}
				else {
					throw new IOException(WRONG_OBJECT_TYPE_MESSAGE + " " + //$NON-NLS-1$
							"List<User> expected, but object of type " + //$NON-NLS-1$
							rawUser.getClass() + " was found inside the List");                             //$NON-NLS-1$
				}
			}
			return users;
		}

		throw new IOException(getWrongObjectTypeFoundMsg(List.class, 
				usersObj));
	}

	public void saveUsers(List<User> users) throws IOException  {
		save(users, usersLocation);
	}

	@SuppressWarnings("unchecked")
	public List<ProxySetting> loadProxySettings() throws IOException    {
		Object proxySettingsObj = load(proxySettingsLocation);
		List<ProxySetting> proxySettings = new ArrayList<ProxySetting>();
		if (proxySettingsObj instanceof List) {
			List<?> rawProxySettings = (List<?>) proxySettingsObj;
			for (Object rawProxySetting : rawProxySettings) {
				if (rawProxySetting instanceof ProxySetting) {
					proxySettings.add((ProxySetting) rawProxySetting);
				}
				else {
					throw new IOException(WRONG_OBJECT_TYPE_MESSAGE + " " + //$NON-NLS-1$
							"List<ProxySetting> expected, but object of type " + //$NON-NLS-1$
							rawProxySetting.getClass() + " was found inside the List");                             //$NON-NLS-1$
				}
			}
			if ( ProxyHelper.convertProxies(proxySettings) ){
				saveProxySettings(proxySettings);
			}
			return proxySettings;
		}

		throw new IOException(getWrongObjectTypeFoundMsg(List.class, 
				proxySettingsObj));
	}

	public void saveProxySettings(List<ProxySetting> proxySettings) throws IOException  {
		save(proxySettings, proxySettingsLocation);
	}

	@SuppressWarnings("unchecked")
	public List<StandardCode> loadStandardCodes() throws IOException   {
		Object stdCodesObj = load(standardCodesLocation);
		List<StandardCode> stdCodes = new ArrayList<StandardCode>();
		if (stdCodesObj instanceof List) {
			List<?> rawStdCodes = (List<?>) stdCodesObj;
			for (Object rawStdCode : rawStdCodes) {
				if (rawStdCode instanceof StandardCode) {
					stdCodes.add((StandardCode) rawStdCode);
				}
				else {
					throw new IOException(WRONG_OBJECT_TYPE_MESSAGE + " " + //$NON-NLS-1$
							"List<StandardCode> expected, but object of type " + //$NON-NLS-1$
							rawStdCode.getClass() + " was found inside the List."); //$NON-NLS-1$
				}
			}
			return stdCodes;
		}

		throw new IOException(getWrongObjectTypeFoundMsg(List.class,
				stdCodesObj));
	}

	public DataSet loadDataSet(String file) throws FileNotFoundException,
	IOException {

		Object dataSet = load(file);
		if (dataSet instanceof  DataSet) {
			return (DataSet) dataSet;
		}
		throw new IOException(getWrongObjectTypeFoundMsg(DataSet.class,
				dataSet));
	}

	/**
	 * 
	 * @param expected Class expected.
	 * @param found Object type received.
	 * @return A error message describing what was expected and what was
	 * received.
	 */
	private String getWrongObjectTypeFoundMsg(Class<?> expected, Object found) {
		String objectFoundMsg = found == null ? "null" : found.getClass().toString(); //$NON-NLS-1$
		return WRONG_OBJECT_TYPE_MESSAGE + " " + objectFoundMsg + //$NON-NLS-1$
		" found, but " + expected + " expected."; //$NON-NLS-1$ //$NON-NLS-2$
	}

	private PersistenceData loadPersistenceData() throws IOException {
		Object pData = load(persistenceDataLocation);
		if (pData instanceof PersistenceData) {
			return (PersistenceData) pData;
		}
		throw new IOException(getWrongObjectTypeFoundMsg(PersistenceData.class, 
				pData));
	}

	String loadToString(String file) throws FileNotFoundException, IOException {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(file));
			StringBuilder builder = new StringBuilder(2500);
			String s = null;
			while ((s = in.readLine()) != null) {
				builder.append(s);
				builder.append(System.getProperty("line.separator")); //$NON-NLS-1$
			}
			return builder.toString();
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

	public Object load(String file) throws FileNotFoundException, IOException {
		return xStream.fromXML(loadToString(file));
	}

	public static PersistenceManager getInstance() {
		return INSTANCE;
	}

	public final IdentifiersList getIdentifiers() {
		return identifiers;
	}

	private final IdentifiersList loadIdentifiers() throws IOException  {
		Object identifiersObj = load(identifiersLocation);

		if (identifiersObj instanceof IdentifiersList) {
			return (IdentifiersList) identifiersObj;
		}

		throw new IOException(getWrongObjectTypeFoundMsg(IdentifiersList.class, 
				identifiersObj));
	}

	public final void saveIdentifiers() 
	throws IOException    {
		save(identifiers, identifiersLocation);
	}

	public final void saveCoCoAVersionMap() throws IOException {
		save(cocoaVersion, cocoaVersionMapLocation);
	}

	private final LastUsedCoCoAVersionMap loadCoCoAVersionMap() throws IOException{

		Object lastUsedVersionObj = load(cocoaVersionMapLocation);

		if(lastUsedVersionObj instanceof LastUsedCoCoAVersionMap){
			return (LastUsedCoCoAVersionMap) lastUsedVersionObj;
		}

		throw new IOException(getWrongObjectTypeFoundMsg(LastUsedCoCoAVersionMap.class, 
				lastUsedVersionObj));
	}

	private final ConsentMap2 loadConsentMap() throws IOException  {
		Object consentObj = load(consentMap2Location);

		if (consentObj instanceof ConsentMap2) {
			return (ConsentMap2) consentObj;
		}

		throw new IOException(getWrongObjectTypeFoundMsg(ConsentMap2.class, 
				consentObj));
	}

	public final ConsentMap2 getConsentMap(){
		return consentMap;
	}

	private final SecondaryIdentifierMap loadSecondaryIdentifierMap() throws IOException  {
		Object secondidObj = load(secondaryIdentifierMapLocation);

		if (secondidObj instanceof SecondaryIdentifierMap) {
			return (SecondaryIdentifierMap) secondidObj;
		}

		throw new IOException(getWrongObjectTypeFoundMsg(SecondaryIdentifierMap.class, 
				secondidObj));
	}

	public final SecondaryIdentifierMap getSecondaryIdentifierMap(){
		return secondaryIdentifierMap;
	}

	private final ExternalIdMap loadExternalIdMap() throws IOException  {
		Object externalIdObj = load(externalIdMapLocation);

		if (externalIdObj instanceof ExternalIdMap) {
			return (ExternalIdMap) externalIdObj;
		}

		throw new IOException(getWrongObjectTypeFoundMsg(ExternalIdMap.class, 
				externalIdObj));
	}

	public final ExternalIdMap getExternalIdMap(){
		return externalIdMap;
	}

	private final RecordStatusMap2 loadRecordStatusMap() throws IOException  {
		Object statusObj = load(recordStatusMap2Location);

		if (statusObj instanceof RecordStatusMap2) {
			return (RecordStatusMap2) statusObj;
		}

		throw new IOException(getWrongObjectTypeFoundMsg(RecordStatusMap2.class, 
				statusObj));
	}

	public final RecordStatusMap2 getRecordStatusMap(){
		return recordStatusMap;
	}

	private final VersionMap loadVersionMap() throws IOException {
		Object versionObj = load(versionMapLocation);

		if (versionObj instanceof VersionMap) {
			return (VersionMap) versionObj;
		}

		throw new IOException(getWrongObjectTypeFoundMsg(VersionMap.class, 
				versionObj));
	}

	public final VersionMap getVersionMap(){
		return versionMap;
	}

	private final ClockOffset loadClockOffset() throws IOException {
		Object clockObj = load(clockOffsetLocation);

		if (clockObj instanceof ClockOffset) {
			return (ClockOffset) clockObj;
		}

		throw new IOException(getWrongObjectTypeFoundMsg(ClockOffset.class, 
				clockObj));
	}

	public final ClockOffset getClockOffset(){
		return clockOffset;
	}

	public void saveClockOffset() throws IOException {
		save(clockOffset, clockOffsetLocation);
	}

	public final PersistenceData getData() {
		return data;
	}

	public void saveStandardCodes(List<StandardCode> stdCodes) throws IOException  {
		save(stdCodes, standardCodesLocation);
	}

	final void delete(String location) throws IOException {
		File file = new File(location);
		if (file.delete() == false) {
			String errorMessage = 
				"Error deleting file: " + file.getAbsolutePath();
			if (LOG.isWarnEnabled()) {
				LOG.warn(errorMessage);
			}
			if (file.exists()) {
				throw new IOException(errorMessage);
			}
		}
	}
	
	public final void deleteRecord(RecordsList.Item item) throws IOException {
		if (!recordsList.hasItem(item)) {
			throw new IllegalArgumentException("record was never saved and as a " + //$NON-NLS-1$
					"result it cannot be deleted, identifier: " + //$NON-NLS-1$
					item.getIdentifier().getIdentifier());
		}
		delete(getRecordFileName(item.getIdentifier(), item.isReadyToCommit()));
	}

	public final void deleteRecord(RecordsListWrapper.Item item) throws IOException {
		if (!recordsList.hasItem(item)) {
			throw new IllegalArgumentException("record was never saved and as a " + //$NON-NLS-1$
					"result it cannot be deleted, identifier: " + //$NON-NLS-1$
					item.getIdentifier().getIdentifier());
		}
		delete(getRecordFileName(item.getIdentifier(), item.isReadyToCommit()));
	}

	public final void deleteDataSet(DataSetSummary summary) throws IOException, DecryptionException {
		//delete all records for the dataset being deleted
		List<Item> itemsToDelete = new ArrayList<Item>();
		for ( Item item: getRecordsList().getItems() ){
			if ( item.getIdentifier().getProjectPrefix().equals(summary.getProjectCode()) ){
				itemsToDelete.add(item);
			}
		}
		for ( Item item: itemsToDelete ){
			deleteRecord(item);
			recordsList.removeItem(item);
		}
		saveRecordsList();
		//clean up maps in the local cache
		getRecordStatusMap().removeForProject(summary.getProjectCode());
		saveRecordStatusMap();
		getConsentMap().removeForProject(summary.getProjectCode());
		saveConsentMap();
		getSecondaryIdentifierMap().removeForProject(summary.getProjectCode());
		saveSecondaryIdentifierMap();
		getExternalIdMap().removeForProject(summary.getProjectCode());
		saveExternalIdMap();
		//delete the dataset
		delete(getDataSetLocation(summary.getId()));
	}

	public final void dispose() throws IOException  {
		if (fileLock != null && fileLock.isValid()) {
			fileLock.release();
		}
		if (lockChannel != null && lockChannel.isOpen()) {
			lockChannel.close();
		}
	}

	public final void saveKeyStore(byte[] keyStore) throws IOException {
		saveBytes(keyStore, keyStoreLocation);
	}

	public final String getKeyStoreLocation() {
		return keyStoreLocation;
	}

	public final InputStream loadKeyStore() throws IOException {
		BufferedInputStream inputStream = new BufferedInputStream(
				new FileInputStream(keyStoreLocation));

		return inputStream;
	}

	public final String getTrustStoreLocation() {
		return trustStoreLocation;
	}
	// Bug#487
	public final void restoreDefaultKeystore() throws IOException{
		final String PACKAGE = "org/psygrid/collection/entry/security/"; //$NON-NLS-1$
		File keyStoreFile = new File(keyStoreLocation);
		initStore(PACKAGE + "defaultKeystore.jks", keyStoreFile); //$NON-NLS-1$
	}

	public String getCurrentLogFile() throws IOException {
		File logDir = new File(baseDir);
		File[] logFiles = logDir.listFiles(new LogFileFilter());
		File currentLog = null;
		for ( File f: logFiles ){
			if ( null == currentLog ){
				currentLog = f;
			}
			else{
				if ( f.lastModified() > currentLog.lastModified() ){
					currentLog = f;
				}
			}
		}
		return loadToString(currentLog.getPath());
	}

	public String getAllLogFiles() throws IOException {
		File logDir = new File(baseDir);
		File[] logFiles = logDir.listFiles(new LogFileFilter());
		Arrays.sort(logFiles, new LogFileComparator());
		StringBuilder builder = new StringBuilder();
		for ( File f: logFiles ){
			builder.append(f.getName());
			builder.append("\n\n");
			builder.append(loadToString(f.getPath()));
			builder.append("\n\n\n\n");
		}
		return builder.toString();
	}
	
	public DocumentInstance getDocumentInstanceForUncommitableRecord(Record record, DocumentOccurrence docOccurrence){
		
		
		
		return null;
	}

	@SuppressWarnings("unchecked")
	public DocumentInstance getDocumentInstance(Record record, DocumentOccurrence docOccurrence) throws IOException, DecryptionException, ChoosableException {
		DocumentInstance instance = null;
		PersistenceManager pManager = PersistenceManager.getInstance();

		//Done this way because I can't find the incomplete documents anywhere else, as they aren't attached to the currentRecord
		RecordsList recordsList = pManager.getRecordsList();

		for (Item item : recordsList.getItems()) {
			if (item.getIdentifier().getIdentifier().equals(record.getIdentifier().getIdentifier())) {
				Record r = pManager.loadRecord(item);
				if ( null != r ){
					if (null != r.getDocumentInstance(docOccurrence)) {
						instance = r.getDocumentInstance(docOccurrence);
					}
				}
			}
		}
		return instance;
	}

	private class LogFileFilter implements FileFilter{

		public boolean accept(File pathname) {
			if ( pathname.getName().endsWith(".log") ){
				return true;
			}
			return false;
		}

	}

	private class LogFileComparator implements Comparator<File> {

		public int compare(File o1, File o2) {
			if ( o1.lastModified() < o2.lastModified() ){
				return -1;
			}
			else if (o1.lastModified() > o2.lastModified() ){
				return 1;
			}
			else{
				return 0;
			}
		}

	}

	/**
	 * Checks to see if this document should be completed via propagation
	 * of data from a primary record.
	 * 
	 * @param record
	 * @param docOcc
	 * @return isPartOfDDE
	 */
	private boolean checkDocumentDde(Record record, DocumentOccurrence docOcc){
		if ( null != record.getPrimaryIdentifier() && null != docOcc.getPrimaryOccIndex() ){
			return true;
		}
		return false;
	}

	private void convertOldConsentMap() throws IOException  {
		Object consentObj = load(consentMapLocation);

		if (!(consentObj instanceof ConsentMap)) {
			throw new IOException(getWrongObjectTypeFoundMsg(ConsentMap.class, consentObj));

		}
		ConsentMap oldMap = (ConsentMap) consentObj;
		consentMap = oldMap.convertToNewFormat();
		saveConsentMap();
	}

	private void convertOldRecordStatusMap() throws IOException {
		Object statusObj = load(recordStatusMapLocation);

		if (!(statusObj instanceof RecordStatusMap)) {
			throw new IOException(getWrongObjectTypeFoundMsg(RecordStatusMap.class, statusObj));

		}
		RecordStatusMap oldMap = (RecordStatusMap) statusObj;
		recordStatusMap = oldMap.convertToNewFormat();
		saveRecordStatusMap();
	}

	/**
	 * Get the full path of the .psygrid CoCoA directory.
	 * 
	 * Uses the psygrid.user.home value stored in the user
	 * preferences for the base location of .psygrid. 
	 * Defaults to the user's home directory as specified
	 * in the user.home system property otherwise.
	 * 
	 * @return path
	 */
	public String getHomeDir() {
		String home = getUserHome();

		//Find the system component for the file path
		String system = getSystemName();

		//If a .psygrid/entry folder exists then rename it for the current system
		//(assuming at this point that all users will only be using a single system,
		//so this is safe to do!)
		File oldDir = new File(home+File.separator + ".psygrid" + File.separator + "entry");
		File newDir = new File(home+File.separator + ".psygrid" + File.separator + system + File.separator + "cocoa");
		if ( oldDir.exists() ){
			LOG.info("Restructuring for multi-system usage");
			boolean resultCopy = Files.copy(oldDir, newDir);
			if ( resultCopy ){
				Files.deleteFile(oldDir);
				//copy the proxies.xml file
				File oldProxy = new File(home+File.separator + ".psygrid" + File.separator + system + File.separator + "cocoa" + File.separator + "proxies.xml");
				File newProxy = new File(home+File.separator + ".psygrid" + File.separator + system + File.separator + "proxies.xml");
				if ( oldProxy.exists() && !newProxy.exists() ){
					Files.copy(oldProxy, newProxy);
				}
			}
		}

		String dir = home + File.separator + ".psygrid" + File.separator + system + File.separator + "cocoa"; //$NON-NLS-1$
		return dir;
	}

	/**
	 * Get the full path of the .psygrid/entry directory.
	 * 
	 * Uses the psygrid.user.home value stored in the user
	 * preferences for the base location of .psygrid. 
	 * Defaults to the user's home directory as specified
	 * in the user.home system property otherwise.
	 * 
	 * @return path
	 */
	public String getHomeDir(String ext) {

		String home = getUserHome();

		//Find the system component for the file path
		String system = getSystemName();

		//If a .psygrid-dsdesigner folder exists then we need to move all of its contents
		//into the system specific folder one level down
		File oldDir = new File(home+File.separator + ".psygrid-dsdesigner");
		File newDir = new File(home + File.separator + ".psygrid" + File.separator + system + File.separator + "dsd");
		if ( oldDir.exists() ){
			LOG.info("Restructuring for multi-system usage");
			boolean resultCopy = Files.copy(oldDir, newDir);
			if ( resultCopy ){
				Files.deleteFile(oldDir);
				//copy the proxies.xml file
				File oldProxy = new File(home+File.separator + ".psygrid" + File.separator + system + File.separator + "dsd" + File.separator + "proxies.xml");
				File newProxy = new File(home+File.separator + ".psygrid" + File.separator + system + File.separator + "proxies.xml");
				if ( oldProxy.exists() && !newProxy.exists() ){
					Files.copy(oldProxy, newProxy);
				}
			}
		}

		String dir = home + File.separator + ".psygrid" + File.separator + system + File.separator + "dsd"; //$NON-NLS-1$//$NON-NLS-2$
		return dir;
	}

	private String getSystemName(){
		//Find the system component for the file path
		String system = null;
		try{
			Properties props = new Properties();
			props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("client.properties"));
			system = props.getProperty("client.system");
		}
		catch(IOException ex){
			system = "Unspecified";
		}
		catch(NullPointerException ex){
			//if Properties#load can't find the properties file then
			//it seems to (very helpfully!) throw an NPE
			system = "Unspecified";
		}
		if ( null == system || 0 == system.length() ){
			system = "Unspecified";
		}
		LOG.info("getHomeDir: system="+system);
		return system;
	}


	/**
	 * Set the location used for the .psygrid directory, in 
	 * place of the default user home directory.
	 * 
	 * This is stored using java.util.prefs.Preferences. It 
	 * provides a platform independent way of storing user 
	 * preferences for an application. It is persistent, user 
	 * and application specific. In Windows, the registry is 
	 * used, whereas on other platforms it is normally a 
	 * hidden directory in the user's home directory.
	 *  
	 * @param dir
	 * @throws Exception
	 */
	public void updateHomeDirLocation(String dir) throws Exception {

		if (dir == null || dir.equals("")) {
			throw new Exception("No directory was specified");
		}

		if (dir.contains(".psygrid")) {
			//User has included the .psygrid directory in the location of the new base directory for the .psygrid directory
			dir = dir.split(".psygrid")[0];
		}

		LOG.info("User home directory was "+getHomeDir());
		Preferences prefs = Preferences.userNodeForPackage(PersistenceManager.class); 

		File test = new File(dir);
		if (!test.canRead()) {
			throw new Exception("Unable to read the contents of "+dir);
		}

		if (!test.canWrite()) {
			throw new Exception("Unable to write to "+dir);
		}
		System.out.println("Setting psygrid.user.home to "+dir);
		prefs.put("psygrid.user.home", dir);

		LOG.info("Home directory updated to "+getHomeDir());
	}

	public LastUsedCoCoAVersionMap getCocoaVersion() {
		return cocoaVersion;
	}

	/**
	 * Determined by whether a user directory was already present for this user when they logged in.
	 * If not, it is assumed that this is the user's first use of CoCoA from a particular machine.
	 * @return - whether this is the user's first login to a CoCoA installation.
	 */
	public boolean isFirstLogin() {
		return firstLogin;
	}

	/**
	 * This returns the location of the version change information file.
	 * 
	 * @return
	 */
	public String getChangeInfoFileLocation() throws FileNotFoundException {
		String fileLocation = "changeinfo/changeInfo.html";
		File changeInfoFile = new File(fileLocation);
		if(!changeInfoFile.exists()){
			throw new FileNotFoundException(fileLocation + "not found.");
		}
		return fileLocation;
	}

	public void exportProxySettingsFile(File toFile) throws IOException {
		Files.copy(new File(proxySettingsLocation), toFile);
	}

	public void exportProxySettingsFile(File toFile, List<ProxySetting> proxies) throws IOException {
		save(proxies, toFile.getAbsolutePath());
	}

	public void importProxySettingsFile(File fromFile) throws IOException {
		Files.copy(fromFile, new File(proxySettingsLocation));
	}

	public boolean checkProxySettings(File file) {
		try{
			Object proxySettingsObj = load(file.getPath());
			List<ProxySetting> proxySettings = new ArrayList<ProxySetting>();
			if (proxySettingsObj instanceof List) {
				List<?> rawProxySettings = (List<?>) proxySettingsObj;
				for (Object rawProxySetting : rawProxySettings) {
					if (rawProxySetting instanceof ProxySetting) {
						proxySettings.add((ProxySetting) rawProxySetting);
					}
					else {
						throw new IOException(WRONG_OBJECT_TYPE_MESSAGE + " " + //$NON-NLS-1$
								"List<ProxySetting> expected, but object of type " + //$NON-NLS-1$
								rawProxySetting.getClass() + " was found inside the List");                             //$NON-NLS-1$
					}
				}
			}
			else{
				throw new IOException(getWrongObjectTypeFoundMsg(List.class, 
						proxySettingsObj));
			}
		}
		catch(Exception ex){
			return false;
		}
		return true;
	}

	/**
	 * Add consent locally i.e. update the consent map.
	 * 
	 * @param record The record
	 * @param cf The consent form for which consent is being added
	 */
	public void addLocalConsent(Record record, ConsentForm cf) {

		Consent consent = cf.generateConsent();
		consent.setConsentForm(cf);
		consent.setConsentGiven(true);
		record.addConsent(consent);

		try {
			//Update the consent map
			PersistenceManager.getInstance().getConsentMap().deleteRecord(record.getIdentifier().getIdentifier());
			PersistenceManager.getInstance().getConsentMap().addRecordNoOverwrite(record.getIdentifier().getIdentifier(), record.getAllConsents());
			PersistenceManager.getInstance().saveConsentMap();

		}
		catch(Exception ex){
			//should never happen
			ExceptionsHelper.handleFatalException(ex);
		}
	}

	/**
	 * Remove consent locally i.e. update the consent map.
	 * 
	 * @param record The record
	 * @param cf The consent form for which consent is being removed
	 * @param reason The reason
	 */
	public void removeLocalConsent(Record record, ConsentForm cf, String reason) {
		try {
			//Remove consent from record
			Consent consent = (Consent)record.getConsent(cf);
			try {
				((Record)record).removeConsent(consent, reason);
			}
			catch (ModelException me) {
				//Ignore
				//This can be thrown when consent is added and not saved, before being removed again
			}

			//Update the consent map
			PersistenceManager.getInstance().getConsentMap().deleteRecord(record.getIdentifier().getIdentifier());
			PersistenceManager.getInstance().getConsentMap().addRecordNoOverwrite(record.getIdentifier().getIdentifier(), record.getAllConsents());
			PersistenceManager.getInstance().saveConsentMap();

		}
		catch(Exception ex){
			//should never happen
			ExceptionsHelper.handleFatalException(ex);
		}
	}
	
	public void setDsdMode(boolean dsdMode) {
		this.dsdMode = dsdMode;
	}
	
	public boolean isDsdMode() {
		return dsdMode;
	}
	
	public void deleteLocalCaches() throws IOException {
		consentMap = new ConsentMap2();
		recordStatusMap = new RecordStatusMap2();
		secondaryIdentifierMap = new SecondaryIdentifierMap();
		externalIdMap = new ExternalIdMap();
		saveConsentMap();
		saveRecordStatusMap();
		saveSecondaryIdentifierMap();
		saveExternalIdMap();
	}
	
	/**
	 * Update the status of all document instances in the local RecordStatusMap 
	 * for the newly saved Record.
	 * 
	 * @param record
	 * @param discardedDocuments
	 * @throws NotAuthorisedFault
	 * @throws IOException
	 * @throws RemoteServiceFault
	 * @throws EntrySAMLException
	 * @throws ConnectException
	 * @throws SocketTimeoutException
	 */
	public void changeLocalDocInstancesStatus(Record record, Long[] discardedDocuments, boolean complete, List<Long> notChanged) throws
	NotAuthorisedFault, IOException, RemoteServiceFault, 
	EntrySAMLException, ConnectException, SocketTimeoutException {

		//Update status of documents belonging to the saved record
		if ( EntryHelper.getDocumentInstances(record).size() > 0 ){
			for (DocumentInstance docInst: EntryHelper.getDocumentInstances(record)) {
				DocumentOccurrence docOcc = docInst.getOccurrence();

				//check that this document instance has not been discarded
				Long occId = docOcc.getId();

				boolean discarded = false;
				for ( int i=0; i<discardedDocuments.length; i++ ){
					if ( occId.equals(discardedDocuments[i]) ){
						discarded = true;
						break;
					}
				}
				if ( discarded || notChanged.contains(occId)){
					//for discarded duplicate documents we get the status from the repository and
					//put this into the record status map to ensure that it is correct
					Status s = RemoteManager.getInstance().getStatusForDocument(record, occId);
					getRecordStatusMap().addDocStatus(record.getIdentifier().getIdentifier(), docOcc, DocumentStatus.valueOf(s));						
				}
				else{
					if ( complete ) {
						getRecordStatusMap().addDocStatus(record.getIdentifier().getIdentifier(), docOcc, DocumentStatus.COMPLETE);
					}
					else{
						getRecordStatusMap().addDocStatus(record.getIdentifier().getIdentifier(), docOcc, DocumentStatus.INCOMPLETE);
					}
				}
				saveRecordStatusMap();
			}
		}
	}

	public void removeRecordFromLocalCaches(String identifier) throws IOException {
		recordStatusMap.deleteRecord(identifier);
		consentMap.deleteRecord(identifier);
		secondaryIdentifierMap.remove(identifier);
		externalIdMap.remove(identifier);
		saveRecordStatusMap();
		saveConsentMap();
		saveSecondaryIdentifierMap();
		saveExternalIdMap();
	}

	/**
	 * Returns true if the record with the given identifier is ready to be committed.
	 * 
	 * @param identifier the record identifier
	 * @return true if the record is ready to commit
	 */
	public boolean recordReadyToCommit(Identifier identifier){
		boolean result = false;
        try {
			RecordsList recordsList = getRecordsList();
			Item item = recordsList.getItem(identifier, true);
			result = item!=null;
		} catch (Exception ex) {
            ExceptionsHelper.handleFatalException(ex);
		}
        return result;
	}
}
