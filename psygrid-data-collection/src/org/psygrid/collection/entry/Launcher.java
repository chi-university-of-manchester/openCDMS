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


package org.psygrid.collection.entry;

import java.awt.EventQueue;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXErrorDialog;
import org.jdesktop.swingx.auth.LoginAdapter;
import org.jdesktop.swingx.auth.LoginEvent;
import org.jvnet.substance.SubstanceLookAndFeel;
import org.psygrid.collection.entry.persistence.DataSetSummary;
import org.psygrid.collection.entry.persistence.FileLockException;
import org.psygrid.collection.entry.persistence.LastUsedCoCoAVersionMap;
import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.collection.entry.persistence.UnfinishedDocInstance;
import org.psygrid.collection.entry.persistence.VersionMap;
import org.psygrid.collection.entry.remote.ProxySetting;
import org.psygrid.collection.entry.remote.RemoteManager;
import org.psygrid.collection.entry.remote.RemoteUpdateWorker;
import org.psygrid.collection.entry.security.EntryLoginService;
import org.psygrid.collection.entry.security.SecurityHelper;
import org.psygrid.collection.entry.security.SecurityManager;
import org.psygrid.collection.entry.ui.NoNetworkDialog;
import org.psygrid.collection.entry.ui.PsygridLoginDialog;
import org.psygrid.collection.entry.ui.PsygridLoginPanel;
import org.psygrid.collection.entry.util.RecordHelper;
import org.psygrid.collection.entry.utils.BrowserLauncher;
import org.psygrid.collection.entry.utils.CollectVersionParser;
import org.psygrid.collection.entry.utils.CollectVersionParser.ParsedCollectVersion;
import org.psygrid.common.identifier.InvalidIdentifierException;
import org.psygrid.common.proxy.ProxyAuthenticationMethods;
import org.psygrid.common.proxy.PsygridTransportClientProperties;
import org.psygrid.common.ui.WrappedJOptionPane;
import org.psygrid.data.model.hibernate.Record;
import org.psygrid.security.components.net.PsyGridClientSocketFactory;
import org.psygrid.www.xml.security.core.types.ProjectType;

public class Launcher {

	private static final String CLIENT_VERSION = "client.version"; //$NON-NLS-1$

	private static final Log LOG = LogFactory.getLog(Launcher.class);

	private static boolean doProxyAuth = false;
	
	private static LastUsedCoCoAVersionMap collectVersionMap = new LastUsedCoCoAVersionMap();

	public static void main(String[] args) {

		final DefaultExceptionHandler handler = new DefaultExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(handler);
		try {
			new LogConfigurator();
		} catch (IOException e) {
			LOG.warn("Error creating LogConfigurator", e); //$NON-NLS-1$
		}
		
		PsygridTransportClientProperties.setProperty(PsygridTransportClientProperties.PropertyType.https);

		FileLockException fle = null;
		InitException ie = null;

		try {
			init();
		} catch (FileLockException e) {
			fle = e;
		} catch (InitException e) {
			ie = e;
		}

		// We need to do this because we can only refer to final variables
		// inside
		// the callback
		final FileLockException fileLockException = fle;
		final InitException initException = ie;

		LOG.info("Launching CoCoA"); //$NON-NLS-1$
		LOG.info("Base dir is "+PersistenceManager.getInstance().getBaseDirLocation());

		processArgs(args);
		final boolean skipLogin = RemoteManager.getInstance().isHardCodedProjects();
		final boolean noLogin = RemoteManager.getInstance().isTestDataset();

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				Thread.currentThread().setUncaughtExceptionHandler(handler);
				initSafe();

				final Application application = new Application();

				if (fileLockException != null) {
					if (LOG.isInfoEnabled()) {
						LOG.info(fileLockException.getMessage(),
								fileLockException);
					}
					JOptionPane
					.showMessageDialog(
							application,
							EntryMessages
							.getString("Launcher.onlyOneInstanceMessage"), //$NON-NLS-1$
							EntryMessages
							.getString("Launcher.onlyOneInstanceTitle"), //$NON-NLS-1$
							JOptionPane.ERROR_MESSAGE);

					application.exitWithoutConfirmation(true);
				}
				if (initException != null) {
					if (LOG.isFatalEnabled()) {
						LOG.fatal(initException.getMessage(), initException);
					}
					JXErrorDialog.showDialog(application, EntryMessages
							.getString("Launcher.error"), //$NON-NLS-1$
							initException.getMessage(), initException);
					application.exitWithoutConfirmation(true);
				}

				application.setVisible(true);
				if ( noLogin ){
					//Running in test/preview mode so we skip the login and
					//go straight to start-up
					previewStartup(application);
				}
				else{
					launchLoginDialog(application, skipLogin);
				}
			}
		});
	}

	private static void processArgs(String[] args) {
		if ( args.length > 0 ){
			if ( "-f".equals(args[0])){
				//running CoCoA in DSD test mode, with location of dataset specified
				RemoteManager.getInstance().setTestDatasetPath(args[1]);
				RemoteManager.getInstance().setTestStdcodePath(args[2]);
				return;
			}
			else{
				//running CoCoA in unsecured development testing mode - project codes
				//specified in subsequent arguments
				List<ProjectType> projects = new ArrayList<ProjectType>(args.length);
				for (int i = 0; i < args.length; ++i) {
					projects.add(new ProjectType(args[i], args[i], null, null, false));
				}

				if (projects.size() > 0) {
					PsygridTransportClientProperties.setProperty(PsygridTransportClientProperties.PropertyType.http);
					RemoteManager.getInstance().setHardCodedProjects(projects);
					return;
				}
			}
		}
		return;
	}

	public static void launchLoginDialog(final Application application,
			boolean skipLogin) {
		EntryLoginService service = new EntryLoginService(skipLogin, application);
		service.addLoginListener(new LoginAdapter() {
			@Override
			public void loginFailed(LoginEvent source) {
				if (LOG.isInfoEnabled()) {
					LOG.info("Login failed", source.getCause()); //$NON-NLS-1$
				}
			}
		});
		application.setDoProxyAuth(doProxyAuth);

		PsygridLoginDialog dlg = new PsygridLoginDialog(application, service);


		boolean doPostLoginIntialisation = true;
		if(SecurityManager.getInstance().getLoginStatus() == SecurityManager.LoginStatus.PostInitialLogin) {
			doPostLoginIntialisation = false;
			dlg.configureForSystemLock();
		}

		dlg.setVisible(true);
		PsygridLoginPanel.Status status = dlg.getStatus();

		if (status == PsygridLoginPanel.Status.SUCCEEDED
				&& doPostLoginIntialisation) {
			// This is the first successful login attempt.
			doLogin(application, skipLogin);
			// Launch the change info in a browser window if necessary.
			try {
				boolean launchChangeInfo = getIsFirstUseOfCurrentVersionAfterUpgrade();
				if (launchChangeInfo) {
					try {
						displayVersionChangeInfo(application);
					} catch (Exception e) {
						//This is a RARE occurrence.
						LOG.error("An error occurred when trying to determine whether the software version has changed.", e);
					}
				}
				showNoNetworkDialogIfNecessary(application);
			} catch (IOException e) {
				ExceptionsHelper.handleIOException(application, e, false);
			}
		} else if (status == PsygridLoginPanel.Status.CANCELLED) {
			application.exitWithoutConfirmation(true);
		}
	}


	private static void showNoNetworkDialogIfNecessary(Application application) {
		PersistenceManager pManager = PersistenceManager.getInstance();
		synchronized (pManager) {
			boolean alwaysOnlineMode = pManager.getData().isAlwaysOnlineMode();
			if (alwaysOnlineMode && !RemoteManager.getInstance().isConnectionAvailable()) {
				NoNetworkDialog noNetworkDialog = new NoNetworkDialog(application);
				noNetworkDialog.setVisible(true);
			}
		}
	}

	/**
	 * Start up procedure when using CoCoA to preview a dataset from
	 * file. There is no login in this case.
	 * 
	 * @param application
	 */
	static void previewStartup(final Application application){
		PersistenceManager.getInstance().initForPreview();
		application.populateMenuItems();
		application.refreshContentPane();
	}

	/**
	 * Displays the CoCoA version change info file in a separate browser.
	 * Alerts the user that the browser has been launched with a message dialog.
	 * @param application
	 */
	static void displayVersionChangeInfo(final Application application){

		//Load in the html change info file via the persistence manager.

		//Display using the utils.BrowserLauncher
		try {
			String URLToOpen = Launcher.getClientProperties().getProperty("client.versionUpdateInfoURL");
			BrowserLauncher.openURL(URLToOpen);

			JOptionPane.showMessageDialog(application, EntryMessages.getString("Launcher.versionInfoAvailableMessage"),
					EntryMessages.getString("Launcher.versionInfoAvailableTitle"), JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(application, EntryMessages.getString("Launcher.versionInfoNotFound"), EntryMessages.getString("Launcher.versionFileNotFoundTitle"), JOptionPane.ERROR_MESSAGE);
		}
	}

	//Need to update the map with the new version of Collect, if necessary
	//Need to store the previous version of Collect.
	
	
	/**
	 * Updates the persisted map which contains the last used CoCoA version for users.
	 * Updates to the current client version for the current user.
	 * @throws IOException
	 */
	static void updatePersistedCollectVersionMap() throws IOException {
		
		String clientVersion = getClientProperties().getProperty(CLIENT_VERSION); 

		String userName = SecurityManager.getInstance().getUserName();
		
		LastUsedCoCoAVersionMap map = PersistenceManager.getInstance().getCocoaVersion();
		
		String hashedUserName = SecurityHelper.hash(userName.toCharArray());
		if(map.userExistsInMap(hashedUserName)){
			String lastUsedVersion = map.getLastUsedVersionForUser(hashedUserName);
			if(!lastUsedVersion.equals(clientVersion)){
				map.addLastUsedVersionForUser(hashedUserName, clientVersion);
				PersistenceManager.getInstance().saveCoCoAVersionMap();
			}
		}else{
				map.addLastUsedVersionForUser(hashedUserName, clientVersion);
				PersistenceManager.getInstance().saveCoCoAVersionMap();
		}
	}
	
	/**
	 * Determines whether this is the current user's first use of this version of the client following an upgrade.
	 * If this is the user's first use of this version of the software because it has been newly installed,
	 * then this returns false.
	 * NB: This method relies on Launcher.collectVersionMap having been already initialised!!!!
	 * @return - true or false
	 * @throws IOException
	 */
	static boolean getIsFirstUseOfCurrentVersionAfterUpgrade() throws IOException {
		String lastUsedVersion = null;
		boolean returnValue;

		String clientVersion = getClientProperties().getProperty(CLIENT_VERSION); 

		String userName = SecurityManager.getInstance().getUserName();

		LastUsedCoCoAVersionMap map = Launcher.collectVersionMap;

		String hashedUserName = SecurityHelper.hash(userName.toCharArray());
		if(map.userExistsInMap(hashedUserName)){
			lastUsedVersion = map.getLastUsedVersionForUser(hashedUserName);
			returnValue = !lastUsedVersion.equals(clientVersion);
			
		}else{
			//add the user to the map.
			//Because this user is not in the map, we are in one of two scenarios.
			//The first is that the user has been using a version of CoCoA that predates the creation of the
			//mapping file, explaining why they currently have no mapping. (And in that case, we want to display the change info to them).
			//The second is that the user has never used a previous version of the software before, in which
			//case the change info should NOT be displayed).
			//We can tell the difference between the two scenarios by calling PersistenceManager.isFirstLogin().

			if(PersistenceManager.getInstance().isFirstLogin()){
				returnValue = false;
			}else{
				returnValue = true;
			}
			
		}

		return returnValue;
	}
	
	/**
	 * Gets the last used version of Collect for the current user.
	 * May return null if there is no mapping.
	 * NB - this method depends on Launcher.collectVersionMap having been already initialised!!
	 * @return ParsedCollectVersion
	 * @throws IOException
	 */
	static ParsedCollectVersion getLastUsedVersionForUser() throws IOException {
		
		ParsedCollectVersion lastUsedVersion = null;
		
		String userName = SecurityManager.getInstance().getUserName();

		String hashedUserName = SecurityHelper.hash(userName.toCharArray());
		
		if(Launcher.collectVersionMap.userExistsInMap(hashedUserName)){
			String lastUsedVersionString = Launcher.collectVersionMap.getLastUsedVersionForUser(hashedUserName);
			CollectVersionParser versionParser = new CollectVersionParser();
			lastUsedVersion = versionParser.parseCollectVersionString(lastUsedVersionString);
			
		}
		
		return lastUsedVersion;
	}

	public static Properties getClientProperties() throws IOException {
		Properties props = new Properties();
		InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("client.properties");
		if (stream != null)
			props.load(stream); 
		return props;
	}

	static void doLogin(final Application app, final boolean skipLogin) {

		//complete population of the user directory - done here so that any errors
		//don't appear as login errors - because they aren't
		PersistenceManager pManager = PersistenceManager.getInstance();
		try{
			pManager.initUserDir();
			//Populate collectVersionMap at first available opportunity, which is just after PersistenceManager.initUserDir() has been called.
			Launcher.collectVersionMap = pManager.getCocoaVersion().copy();
			Launcher.updatePersistedCollectVersionMap();
		}
		catch(IOException ex){
			ExceptionsHelper.handleIOException(app, ex, true);
			
		}
		
		
		app.populateMenuItems();
		app.refreshContentPane();

		if( !skipLogin && RemoteManager.getInstance().isConnectionAvailable()){
			SecurityManager.getInstance().postProcessLogin(app);
		}

		//after successful login, start thread that monitors connectivity
		app.updateStatusBar();
		
		boolean alwaysOnlineSet = false;
		boolean newUser = false;
		synchronized (pManager) {
			alwaysOnlineSet = pManager.getData().isAlwaysOnlineModeSet();
			newUser = pManager.isFirstLogin();
		}
		
		//handle switching to always online mode when applicable
		if (!alwaysOnlineSet){
			if ( newUser ){
				//new users assigned to always online mode
				synchronized (pManager){
					pManager.getData().setAlwaysOnlineMode(true);
					try {
						pManager.savePersistenceData();
					} catch (IOException e) {
						ExceptionsHelper.handleIOException(app, e, false);
					}
				}
				startUpdate(app);
			}
			else{
				startUpdateThenSwitchToOnlineMode(app);
			}
		}
		else{
			startUpdate(app);
		}

	}
	
	/**
	 * Wipes the local cache if the user's previous version of Collect is prior to version 1.9.5. This is part of the fix for bug #1434. This method is here to ensure
	 * that any locally stored information about invalid records gets wiped.
	 */
	static void wipeLocalCacheOnVersionUpgradeIfPreviousCollectVersionPriorTo_1_9_5(){

		try {
			boolean upgraded = Launcher.getIsFirstUseOfCurrentVersionAfterUpgrade();
			//if upgraded is true, means that either:
			// - the user had a 'lastUsedVersion' mapping, and this was less than the current client version
			// - the user didn't have a 'lastUsedVersion' mapping, but there was a pre-existing folder for the user, which shows they HAVE
			//		used this instance of Collect before. This could happen if their last used version pre-dated the introduction of the version-tracking map.
			//		Since that feature was introduced years ago, the chances of this scenario are virtually nil.
			
			//if upgraded is false, it means that either:
			// - the user's last used version is the same as the current client version
			// - the user had no mapping, but this is because there was no pre-existing folder for the user, showing that it is their first use of this instance of Collect.
			//		In that case, there are no caches to delete anyway!
			if(upgraded){
				ParsedCollectVersion lastUsedVersion = Launcher.getLastUsedVersionForUser();
				
				if(lastUsedVersion == null){
					//We have already determined that the user has upgraded. In this case, there is no mapping because the user's last-used version pre-dated the
					//introduction of the version-tracking map. In this case, we want to delete the local cache.
					PersistenceManager.getInstance().deleteLocalCaches();
				}else{
					CollectVersionParser parser = new CollectVersionParser();
					ParsedCollectVersion ver1_9_5 = parser.new ParsedCollectVersion(1, 9, 5);
					int comparison = lastUsedVersion.compareTo(ver1_9_5);
					if(comparison < 0){
						PersistenceManager.getInstance().deleteLocalCaches();
					}
				}
				

			}
		} catch (IOException e) {
			//This should happen VERY rarely.
			LOG.error("Unable to check old Collect version.", e);
		}
		
	}

	static void startUpdate(final Application app){

		wipeLocalCacheOnVersionUpgradeIfPreviousCollectVersionPriorTo_1_9_5();

		final SwingWorker<Object, Object> remoteUpdateWorker = new RemoteUpdateWorker(
				app) {
			@Override
			protected void success() {
				checkForUnfinishedDocumentInstance(app);
				app.startAppTimers();
				if (RemoteManager.getInstance().getPatchedDataSets() != null 
						&& !RemoteManager.getInstance().getPatchedDataSets().equals("")) {
					WrappedJOptionPane.showMessageDialog(application, EntryMessages.getString("RemoteManager.patchedDataset") + "\n" + EntryMessages.getString("RemoteManager.impactedStudies") + " " + RemoteManager.getInstance().getPatchedDataSets(),
							EntryMessages.getString("RemoteManager.patchedDatasetTitle"), JOptionPane.INFORMATION_MESSAGE);
				}
			}

			@Override
			protected void failure(ExecutionException ee) {
				if (ee.getCause() instanceof ConnectException) {
					checkForUnfinishedDocumentInstance(app);
				} else {
					super.failure(ee);
				}
				app.startAppTimers();
			}
		};
		SwingWorkerExecutor.getInstance().execute(remoteUpdateWorker);

		
	}
	
	static void startUpdateThenSwitchToOnlineMode(final Application app){
		
		wipeLocalCacheOnVersionUpgradeIfPreviousCollectVersionPriorTo_1_9_5();
		
		final SwingWorker<Object, Object> remoteUpdateWorker = new RemoteUpdateWorker(
				app) {
			@Override
			protected void success() {
				switchToOnlineMode(app);
				if (RemoteManager.getInstance().getPatchedDataSets() != null 
						&& !RemoteManager.getInstance().getPatchedDataSets().equals("")) {
					WrappedJOptionPane.showMessageDialog(application, EntryMessages.getString("RemoteManager.patchedDataset") + "\n" + EntryMessages.getString("RemoteManager.impactedStudies") + " " + RemoteManager.getInstance().getPatchedDataSets(),
							EntryMessages.getString("RemoteManager.patchedDatasetTitle"), JOptionPane.INFORMATION_MESSAGE);
				}
			}

			@Override
			protected void failure(ExecutionException ee) {
				if (ee.getCause() instanceof ConnectException) {
					checkForUnfinishedDocumentInstance(app);
				} else {
					super.failure(ee);
				}
				app.startAppTimers();
			}
		};
		SwingWorkerExecutor.getInstance().execute(remoteUpdateWorker);
		
	}
	
	static void switchToOnlineMode(final Application app){
		
		//existing user - show dialog informing them what 
		//is going on, perform commit then switch to
		//online mode
		WrappedJOptionPane.showWrappedMessageDialog(
				app, 
				EntryMessages.getString("Launcher.changeToOnlineOnlyMessage"), 
				EntryMessages.getString("Launcher.changeToOnlineOnlyTitle"),  
				WrappedJOptionPane.INFORMATION_MESSAGE);
		
		EntryHelper.runWhenNoUncommittedRecords(
				app, 
				EntryMessages.getString("Launcher.changeToOnlineOnlyPrefix"), 
				new Runnable(){
					public void run() {
						//switch to always online mode and save the setting
						PersistenceManager pManager = PersistenceManager.getInstance();
						synchronized (pManager){
							pManager.getData().setAlwaysOnlineMode(true);
							try {
								pManager.savePersistenceData();
							} catch (IOException e) {
								ExceptionsHelper.handleIOException(app, e, false);
							}
						}
						//Notify the user of success
						WrappedJOptionPane.showWrappedMessageDialog(
								app, 
								EntryMessages.getString("Launcher.changeToOnlineOnlySuccessMessage"), 
								EntryMessages.getString("Launcher.changeToOnlineOnlySuccessTitle"), 
								WrappedJOptionPane.INFORMATION_MESSAGE);
						//perform the tasks we normally would on start up
						checkForUnfinishedDocumentInstance(app);
						app.startAppTimers();
					}
				},
				new Runnable(){
					public void run() {
						//Notify the user of failure
						WrappedJOptionPane.showWrappedMessageDialog(
								app, 
								EntryMessages.getString("Launcher.commitCancelledMessage"), 
								EntryMessages.getString("Launcher.commitCancelledTitle"), 
								WrappedJOptionPane.INFORMATION_MESSAGE);
						//perform the tasks we normally would on start up
						checkForUnfinishedDocumentInstance(app);
						app.startAppTimers();
					}
				});

	}

	// TODO Move complicated logic from here to another class and consider
	// using SwingWorker
	static void checkForUnfinishedDocumentInstance(final Application application) {

		//Seems like an odd place to log the versions, but it is actually the first
		//place where there is no distinction between online or offline mode
		logVersions();

		UnfinishedDocInstance unfinishedDocInstance = null;
		UnfinishedDocInstance autoSavedDocInstance = null;
		PersistenceManager pManager = PersistenceManager.getInstance();
		synchronized ( pManager ){
			try {
				unfinishedDocInstance = pManager.loadUnfinishedDocInstance();
			}
			catch(Exception ex){
				//there is no way to recover from having an unfinished document
				//that cannot be loaded so the best we can do is delete the file
				LOG.error("Unable to open unfinished doc instance. Deleting.", ex);
				try{
					pManager.deleteUnfinishedDocumentInstance();
				}
				catch(IOException ioe){
					//nothing we can do here
					LOG.error("Error whilst deleting unfinished doc instance.", ioe);
				}
			}
			if (unfinishedDocInstance != null) {
				try{
					pManager.deleteUnfinishedDocumentInstance();
				}
				catch(IOException ioe){
					//nothing we can do here
					LOG.error("Error whilst deleting unfinished doc instance.", ioe);
				}
			}

			
			if ( null == unfinishedDocInstance ){
				try{
					autoSavedDocInstance = pManager.loadAutoSaveDocInstance();
				}
				catch (Exception ex) {
					//there is no way to recover from having an autosave document
					//that cannot be loaded so the best we can do is delete the file
					LOG.error("Unable to open auto-save doc instance. Deleting.", ex);
					try{
						pManager.deleteAutoSaveDocumentInstance();
					}
					catch(IOException ioe){
						//nothing we can do here
						LOG.error("Error whilst deleting auto-save doc instance.", ioe);
					}
				}
				if (autoSavedDocInstance != null) {
					try{
						pManager.deleteAutoSaveDocumentInstance();
					}
					catch(IOException ioe){
						//nothing we can do here
						LOG.error("Error whilst deleting auto-save doc instance.", ioe);
					}
				}
			}
		}

		if ( null != unfinishedDocInstance ){
			//done this way for backwards compatibility
			DocumentStatus docStatus = unfinishedDocInstance.getDocStatus();
			if ( null == unfinishedDocInstance.getDocStatus() ){
				docStatus = DocumentStatus.valueOf(unfinishedDocInstance.getDocOccurrenceInstance().getStatus());
			}

			//Add the appropriate record to the doc inst
			if (unfinishedDocInstance.getDocOccurrenceInstance().getRecord() == null) {
					System.out.println("No record for unfDocInst!!!!!!");
			}
			else {
				try {
					Record record = (Record)RecordHelper.constructRecord(unfinishedDocInstance.getDocOccurrenceInstance().getRecord());
					application.getModel().setCurrentRecord(record);
				}
				catch (InvalidIdentifierException e) {
					ExceptionsHelper.handleException(application, "Problem opening document", e, "There was a problem opening the unfinished document", false);
				}
			}

			application.setSelectedDocOccurrenceInstance(
					unfinishedDocInstance.getDocOccurrenceInstance(), 
					unfinishedDocInstance.getSectionOccurrenceIndex(), 
					docStatus);
		}
		else if ( null != autoSavedDocInstance ){
			//done this way for backwards compatibility
			DocumentStatus docStatus = autoSavedDocInstance.getDocStatus();
			if ( null == autoSavedDocInstance.getDocStatus() ){
				docStatus = DocumentStatus.valueOf(autoSavedDocInstance.getDocOccurrenceInstance().getStatus());
			}
			
			//Add the appropriate record to the doc inst
			if (autoSavedDocInstance.getDocOccurrenceInstance().getRecord() == null) {
				System.out.println("No record for unfDocInst!!!!!!");
			}
			else {
				try {
					Record record = (Record)RecordHelper.constructRecord(autoSavedDocInstance.getDocOccurrenceInstance().getRecord());
					application.getModel().setCurrentRecord(record);
				}
				catch (InvalidIdentifierException e) {
					ExceptionsHelper.handleException(application, "Problem opening document", e, "There was a problem opening the unfinished document", false);
				}
			}

			
			application.setSelectedDocOccurrenceInstance(
					autoSavedDocInstance.getDocOccurrenceInstance(), 
					autoSavedDocInstance.getSectionOccurrenceIndex(), 
					docStatus);
		}
	}

	static void initSafe() {
		try {
			// System.setProperty("swing.aatext", "true");
			SubstanceLookAndFeel slaf = new SubstanceLookAndFeel();
			UIManager.setLookAndFeel(slaf);
		} catch (UnsupportedLookAndFeelException ilafe) {
			if (LOG.isWarnEnabled()) {
				LOG.warn("Look and Feel not found", ilafe); 
			}
		}
	}

	static void init() throws FileLockException, InitException {
		
		try {
			if (PersistenceManager.getInstance().isBaseDirInitialized() == false) {
				PersistenceManager.getInstance().initBaseDir();
			}
		} catch (FileLockException fle) {
			throw fle;
		} catch (IOException ioe) {
			String errorMessage = "Error initialising the persistence manager. Exiting."; //$NON-NLS-1$
			InitException ie = new InitException(errorMessage, ioe);
			throw ie;
		}
		// Setup the proxy settings to the default
		try {
			List<ProxySetting> lps = PersistenceManager.getInstance().loadProxySettings();
			boolean proxyUsed = false;
			if (null != lps) {
				for ( ProxySetting ps: lps ){
					if ( ps.getDefaultProxy().booleanValue() ){
						// Set the JVM properties
						if (LOG.isInfoEnabled()) {
							LOG.info("Proxy settings: https.proxyHost=" + ps.getServer() + ", https.proxyPort=" + ps.getPort()); //$NON-NLS-1$
						}
						System.setProperty("http.proxyHost", ps.getServer());
						System.setProperty("http.proxyPort", ps.getPort());
						System.setProperty("https.proxyHost", ps.getServer());
						System.setProperty("https.proxyPort", ps.getPort());
						if(!ProxyAuthenticationMethods.NONE.name().equals(ps.getAuthenticationMethod())){
							doProxyAuth = true;
							PsyGridClientSocketFactory.setAuthScheme(ProxyAuthenticationMethods.valueOf(ps.getAuthenticationMethod()));
							if((ProxyAuthenticationMethods.WINDOWS.name().equals(ps.getAuthenticationMethod()))){
								PsyGridClientSocketFactory.setNTdomain(ps.getDomain());
							}
						}
						proxyUsed = true;
					}
				}
			}
			if ( !proxyUsed ){
				if (LOG.isInfoEnabled()) {
					LOG.info("No default proxy setting - using direct connection"); 
				}
			}
		} catch (IOException ioe) {
			if (LOG.isInfoEnabled()) {
				LOG.info("No proxy settings found - using direct connection"); 
			}
		}
	}
	static private void logVersions(){
		String clientVersion = null;
		try{
			Properties props = getClientProperties();
			clientVersion = props.getProperty(CLIENT_VERSION, "Unknown");
		}
		catch(IOException ex){
			clientVersion = "Unknown";
		}
		VersionMap versions = PersistenceManager.getInstance().getVersionMap();
		LOG.info(VersionMap.REPO_NAME+" v "+versions.getVersion(VersionMap.REPO_NAME));
		LOG.info(VersionMap.AA_NAME+" v "+versions.getVersion(VersionMap.AA_NAME));
		LOG.info(VersionMap.PA_NAME+" v "+versions.getVersion(VersionMap.PA_NAME));
		LOG.info("CoCoA v "+clientVersion);

		List<DataSetSummary> dsSummaries = PersistenceManager.getInstance().getData().getDataSetSummaries();
		for ( DataSetSummary dss: dsSummaries ){
			LOG.info(dss.getDisplayText()+" ("+dss.getProjectCode()+") Version "+dss.getVersionNo());
		}
	}
}
