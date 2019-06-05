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

package org.psygrid.datasetdesigner.ui; 

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.axis.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.auth.LoginAdapter;
import org.jdesktop.swingx.auth.LoginEvent;
import org.jvnet.substance.SubstanceLookAndFeel;
import org.psygrid.collection.entry.persistence.FileLockException;
import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.collection.entry.remote.ProxySetting;
import org.psygrid.collection.entry.remote.RemoteManager;
import org.psygrid.collection.entry.security.EntryLoginService;
import org.psygrid.collection.entry.security.SecurityHelper;
import org.psygrid.collection.entry.security.SecurityManager;
import org.psygrid.collection.entry.ui.PsygridLoginDialog;
import org.psygrid.collection.entry.ui.PsygridLoginPanel;
import org.psygrid.common.proxy.ProxyAuthenticationMethods;
import org.psygrid.common.security.LoginInterfaceFrame;
import org.psygrid.common.ui.WrappedJOptionPane;
import org.psygrid.data.client.DataElementClient;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.LSIDAuthority;
import org.psygrid.data.model.hibernate.StandardCode;
import org.psygrid.data.reporting.client.ReportsClient;
import org.psygrid.data.reporting.definition.IReport;
import org.psygrid.data.repository.RepositoryNoSuchDatasetFault;
import org.psygrid.data.repository.RepositoryServiceFault;
import org.psygrid.data.repository.client.RepositoryClient;
import org.psygrid.data.utils.security.NotAuthorisedFault;
import org.psygrid.datasetdesigner.LogConfigurator;
import org.psygrid.datasetdesigner.controllers.RecentStudiesController;
import org.psygrid.datasetdesigner.listeners.TimerListener;
import org.psygrid.datasetdesigner.model.DSDataSet;
import org.psygrid.datasetdesigner.model.DocTreeModel;
import org.psygrid.datasetdesigner.model.ESLEmailModel;
import org.psygrid.datasetdesigner.model.GroupModel;
import org.psygrid.datasetdesigner.model.RandomisationHolderModel;
import org.psygrid.datasetdesigner.model.StudyDataSet;
import org.psygrid.datasetdesigner.model.TreatmentHolderModel;
import org.psygrid.datasetdesigner.utils.DefaultDSSettings;
import org.psygrid.datasetdesigner.utils.OldDatasetConverter;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.esl.model.IProject;
import org.psygrid.esl.model.IRole;
import org.psygrid.esl.services.ESLServiceFault;
import org.psygrid.esl.services.client.EslClient;
import org.psygrid.randomization.RandomizationFault;
import org.psygrid.randomization.UnknownRandomizerFault;
import org.psygrid.randomization.client.RandomizationClient;
import org.psygrid.randomization.model.hibernate.BlockRandomizer;
import org.psygrid.randomization.model.hibernate.StratifiedRandomizer;
import org.psygrid.randomization.model.hibernate.StratumCombination;
import org.psygrid.randomization.model.hibernate.Treatment;
import org.psygrid.security.PGSecurityException;
import org.psygrid.security.PGSecurityInvalidSAMLException;
import org.psygrid.security.PGSecuritySAMLVerificationException;
import org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage;
import org.psygrid.security.components.net.PsyGridClientSocketFactory;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.ProjectType;
import org.psygrid.www.xml.security.core.types.RoleType;


/**
 * Main application of the DSD
 * Launcher for the DSD 
 * 
 * @author pwhelan
 */
public class Main {

	/**
	 * The logger for this class
	 */
	private static final Log LOG = LogFactory.getLog(Main.class);

	/**
	 * The main window of the DSD
	 */
	private static MainFrame mainFrame;

	/**
	 * Init the proxy, look and feel, home dir, launch the login dialog,
	 * autosave timer and the main window
	 * 
	 * @param args none expected
	 */
	public static void main(String[] args) {
		
		//initialise the look and feel
		initLAF();

		//initialise home dir etc.
		initPersistenceManager();

		//initialise the logger
		initLogger();

		//create main window of the application
		mainFrame = new MainFrame();

		//initialise proxy settings
		initProxy();

		//launch the main login dialog
		launchLoginDialog(mainFrame);
		// We poll at half the timeout interval to reduce the fudge factor
		// introduced by events delivered at just under the timeout value.
		// Timer autosaves active file every 180000ms
		Timer timer = new Timer(180000, new TimerListener());
		timer.setRepeats(true);
		timer.start();
	}

	/**
	 * Display the main login dialog and handle basic login actions
	 * 
	 * @param frame The Main frame of the application
	 */
	private static void launchLoginDialog(final LoginInterfaceFrame frame) {

		EntryLoginService service = new EntryLoginService(false, frame);
		service.addLoginListener(new LoginAdapter() {

			public void loginCanceled(LoginEvent source) {
				frame.setVisible(false);
				System.exit(-1);
			}

			@Override
			public void loginFailed(LoginEvent source) {
				if (LOG.isInfoEnabled()) {
					LOG.info("Login failed", source.getCause()); //$NON-NLS-1$
				}
			}
		});

		String clientVersion = PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.versionlabel") + " ";
		
        try{
            Properties props = new Properties();
            props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("client.properties"));
            clientVersion += props.getProperty("client.version");
        }
        catch(IOException ex){
            clientVersion += "Unknown";
        }
        catch(NullPointerException ex){
            //if Properties#load can't find the properties file then
            //it seems to (very helpfully!) throw an NPE
            clientVersion += "Unknown";
        }
        
		PsygridLoginDialog dlg = new PsygridLoginDialog(frame, service, clientVersion);
		dlg.setVisible(true);
		PsygridLoginPanel.Status status = dlg.getStatus();
		if (status == PsygridLoginPanel.Status.SUCCEEDED) {
			try {
				SecurityManager.getInstance().postProcessLogin(mainFrame);
			} catch (Exception ex) {
				ex.printStackTrace();
				frame.setVisible(false);
				System.exit(0);
			} 
			mainFrame.setStarted(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.mainframe.loading"));
			doLogin();
		} else if (status == PsygridLoginPanel.Status.CANCELLED) {
			frame.setVisible(false);
			System.exit(0);
		}
	}

	/**
	 * Handle success login attempt and load datasets 
	 * @param frame main frame of the application
	 * @param skipLogin
	 */
	private static void doLogin() {
		SwingWorker worker = new SwingWorker() {
			public Object doInBackground() {
				PersistenceManager pManager = PersistenceManager.getInstance();
				loadLocalDatasets(pManager);
				
				
				//load the standard codes
				//get the standard codes - required for preview in CoCoA
				try {
					RepositoryClient client = new RepositoryClient();
					String saml = SecurityHelper.getAAQueryClient().getSAMLAssertion().toString();
					List<StandardCode> stdCodes = client.getStandardCodes(saml);
					pManager.save(stdCodes, pManager.getUserDirLocation()+"stdcodes.xml");
				} catch (Exception ex) {
					LOG.error("Exception loading std codes", ex);
					//can't do anything if this happens..
				}
				return null;
			}

			public void done() {
				if (!RemoteManager.getInstance().isConnectionAvailable()) {
					WrappedJOptionPane.showMessageDialog(mainFrame, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.main.internetconnectionneeded"));
					System.exit(-1);
				}
				
				//load recent studies from file
				RecentStudiesController.getInstance().loadStudiesFromFile();

				mainFrame.init();
			}
		};

		worker.execute();
	}

	/**
	 * Initialise the logger
	 *
	 */
	private static void initLogger() {
		try {
			new LogConfigurator();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initalise the look and feel - currently use SubstanceLookAndFeel to match CoCoA
	 *
	 */
	private static void initLAF() {
		try {
			UIManager.setLookAndFeel(new SubstanceLookAndFeel());
		} catch (UnsupportedLookAndFeelException ilafe) {
			LOG.error("Error initialising the Look and Feel", ilafe);
		} catch (Exception e) {
			LOG.error("Exception" , e);
		}
	}

	/**
	 * Initialise the Persistence Manager and home directory etc.
	 *
	 */
	private static void initPersistenceManager() {
		try
		{
			PersistenceManager pManager = PersistenceManager.getInstance();
			pManager.initBaseDir("ext");
			pManager.setAliases();
			pManager.setDsdMode(true);
		} catch (FileLockException fex)
		{
			JOptionPane.showMessageDialog(
					new JDialog(),
					PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.oneinstance"), //$NON-NLS-1$
					PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.justoneinstance"), //$NON-NLS-1$
					JOptionPane.ERROR_MESSAGE);
			System.exit(-1);

		} catch (Exception ex)
		{
			LOG.error("Error initialising PersistenceManager", ex);
			System.exit(-1);
		}
	}

	/**
	 * Initialise the proxy settings.
	 *
	 */
	private static void initProxy(){
		// Setup the proxy settings to the default
		try {
			List<ProxySetting> lps = PersistenceManager.getInstance()
			.loadProxySettings();
			if (lps != null && lps.size() > 0) {
				// The first in the list is the default
				ProxySetting ps = lps.get(0);
				// Set the JVM properties
				System.setProperty("http.proxyHost", ps.getServer());
				System.setProperty("http.proxyPort", ps.getPort());
				System.setProperty("https.proxyHost", ps.getServer());
				System.setProperty("https.proxyPort", ps.getPort());
				if(!ProxyAuthenticationMethods.NONE.name().equals(ps.getAuthenticationMethod())){
					mainFrame.setDoProxyAuth(true);
					PsyGridClientSocketFactory.setAuthScheme(ProxyAuthenticationMethods.valueOf(ps.getAuthenticationMethod()));
					if((ProxyAuthenticationMethods.WINDOWS.name().equals(ps.getAuthenticationMethod()))){
						PsyGridClientSocketFactory.setNTdomain(ps.getDomain());
					}
				}
				if (LOG.isInfoEnabled()) {
					LOG.info("Proxy settings: https.proxyHost="+ps.getServer()+", https.proxyPort="+ps.getPort()); //$NON-NLS-1$
				}
			} else {
				if (LOG.isInfoEnabled()) {
					LOG.info("No proxy settings found"); //$NON-NLS-1$
				}			
			}
		} catch (IOException ioe) {
			if (LOG.isInfoEnabled()) {
				LOG.info("No proxy settings found"); //$NON-NLS-1$
			}
		}		
	}

	/**
	 * Load the datasets from the dsdatasets.xml file
	 * Iterate through and store each into the newly created /studies
	 * directory
	 * @param pManager
	 * @return An arraylist containing the local datasets
	 */
	private static void loadLocalDatasets(PersistenceManager pManager) {
		//the presence of the studies folder indicates that this has been done once
		//so exit here
		if (new File(pManager.getUserDirLocation() + File.separator + "studies").exists()) {
			return;
		}
		
		//iterate through all old datasets and convert and save them to new location
		try {
			ArrayList<DSDataSet> dataSets = new ArrayList<DSDataSet>();
			//create the new studies folder to store the inidividual studies file to
			File f = new File((PersistenceManager.getInstance().getUserDirLocation())+ "studies"); 
			f.mkdir();
			
			Object dsSets = pManager.load(new File(pManager.getUserDirLocation()+"dsdatasets.xml").toString());
			if (dsSets instanceof ArrayList) {
				dataSets = (ArrayList<DSDataSet>)dsSets;
				for (int i=0; i<dataSets.size(); i++) {
					DSDataSet firstInlist = dataSets.get(i);
					OldDatasetConverter.convertAndSaveOldDataset(firstInlist);
				}
			}
			String convertedString = PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.main.convert");
			WrappedJOptionPane.showMessageDialog(mainFrame, dataSets.size() + " " + convertedString +  " \n" + PersistenceManager.getInstance().getUserDirLocation()+ "studies \n" + PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.main.usefileopenforconverted"));
			
		} catch (Exception ex) {
			LOG.error("Error loading local datasets " + ex.getMessage());
		}
	}

	/**
	 * Load the remote datasets from the repository
	 * @param pManager  The persistence manager
	 * @param dataSets local datasets
	 */
	private static void loadRemoteDatasets(PersistenceManager pManager, ArrayList<StudyDataSet> dataSets) {
		try {
			ArrayList<ProjectType> projects = new ArrayList<ProjectType>(SecurityHelper.getAAQueryClient().getMyProjects());
			RepositoryClient client = new RepositoryClient();
			String saml = SecurityHelper.getAAQueryClient().getSAMLAssertion().toString();
			//get the standard codes - required for preview in CoCoA
			List<StandardCode> stdCodes = client.getStandardCodes(saml);
			pManager.save(stdCodes, pManager.getUserDirLocation()+"stdcodes.xml");

			//Retrieve the list of DEL projects, so we can ignore them when contacting the repository
			List<String> delProjects = new ArrayList<String>(0);
			try {
				//Can't use the DELSecurity class here because the connection to the DEL hasn't been initialized yet.
				DataElementClient delClient = new DataElementClient();
				LSIDAuthority[] lsidAuths = delClient.getLSIDAuthorities(saml);
				if (lsidAuths != null) {
					for (int i = 0; i < lsidAuths.length; i++) {
						if (lsidAuths[i] != null) {
							delProjects.add(lsidAuths[i].getAuthorityID());
						}
					}
				}
			}
			catch (NotAuthorisedFault naf) {
				//User doesn't have access to the DEL
			}
			catch (Exception e) {
				LOG.error("Problem occurred connecting to the DEL", e);
			}

			for (int i=0; i<projects.size(); i++) {
				StudyDataSet dsSet = new StudyDataSet();
				if (!(projects.get(i).getIdCode().equals("-1"))) {
					ProjectType pt = projects.get(i);
					String projectCode = projects.get(i).getIdCode();
					try {
						//Ignore all DEL projects.
						//Check if local copy exists and if not, use remote copy.
						if (!delProjects.contains(projectCode) &&
								!doesLocalCopyExist(client, dataSets, projectCode, saml)) {

							//configure the roles for this dataset
							initRemoteRoles(dsSet, pt);

							//assign the remote dataset
							initRemoteDataset(client, dsSet, projectCode, saml);

							//create the ds (which has now been set by initRemoteDataSet
							DataSet ds = dsSet.getDs();

							//configure the remotely assigned groups
							initRemoteGroups(dsSet, pt);

							// add it to the tree model
							DocTreeModel.getInstance().addDataset(dsSet);

							initRemoteReports(dsSet, saml);

							// if dataset has randomization configured, set it up here
							if (ds.isRandomizationRequired()) {
								initRemoteRandomization(dsSet, saml);
							}
						}
					} catch (RepositoryServiceFault rsf) {
						LOG.error("Error loading remote dataset " + rsf.getMessage());
						rsf.printStackTrace();
						continue;
					} catch (RepositoryNoSuchDatasetFault rnsdf) {
						LOG.error("Error loading remote dataset " + rnsdf.getMessage());
						rnsdf.printStackTrace();
						continue;
					} catch (NotAuthorisedFault naf) {
						LOG.error("Error loading remote dataset " + naf.getMessage());
						naf.printStackTrace();
						continue;
					} catch (ConnectException cex) {
						LOG.error("Error loading remote dataset Error connecting : "  + cex.getMessage());
						cex.printStackTrace();
						continue;
					} catch (AxisFault af) {
						LOG.error("Error loading remote dataset axis fault " + af.getMessage());
						continue;
					} catch (Exception ex) {
						LOG.error("Error general exception ", ex);
						continue;
					}
				}
			}
		} catch (ConnectException cex) {
			LOG.error("Error loading remote datasets " + cex.getMessage());
			cex.printStackTrace();
		} catch (SocketTimeoutException ste) {
			LOG.error("Error loading remote datasets " + ste.getMessage());
			ste.printStackTrace();
		} catch (IOException ioex) {
			LOG.error("Error loading remote datasets " + ioex.getMessage());
			ioex.printStackTrace();
		} catch (PGSecurityException pgse){
			LOG.error("Error loading remote datasets " + pgse.getMessage());
			pgse.printStackTrace();
		} catch (PGSecurityInvalidSAMLException pginsam) {
			LOG.error("Error loading remote datasets " + pginsam.getMessage());
			pginsam.printStackTrace();
		} catch (PGSecuritySAMLVerificationException pgsamlsec) { 
			LOG.error("Error loading remote datasets " + pgsamlsec.getMessage());
			pgsamlsec.printStackTrace();
		} 
	}

	/**
	 * Set the roles for the dataset
	 * 
	 * @param dsSet the dataset 
	 * @param pt the ProjectType to get the roles for
	 */
	private static void initRemoteRoles(StudyDataSet dsSet, ProjectType pt) 
				throws PGSecurityException  {

		try {
			RoleType[] roles = SecurityHelper.getAAQueryClient().getRolesInProject(pt);
			ArrayList<RoleType> rts = new ArrayList<RoleType>();
			for (RoleType role: roles) {
				//don't show PM role here
				if (!role.getName().equals("ProjectManager")
						&& !role.getName().equals("SystemAdministrator")){
					rts.add(role);
				}
			}
			dsSet.setRoles(rts);
		} catch (NotAuthorisedFaultMessage naf) {
			LOG.error("Error initialising remote roles " + naf.getMessage());
			naf.printStackTrace();
		} catch (ConnectException cex) {
			LOG.error("Error initialising remote roles " + cex.getMessage());
			cex.printStackTrace();
		}
	}

	/**
	 * Check if current dataset exists locally
	 * @param repClient the repository client to query
	 * @param localSets array of locally stored datasets in dsdatasets.xml file
	 * @param projectCode project code of the dataset to check existence of locally
	 * @return true if dataset exists locally; false if not
	 */
	private static boolean doesLocalCopyExist(RepositoryClient repClient,
			ArrayList<StudyDataSet> localSets, 
			String projectCode,
			String saml) 
	throws RepositoryNoSuchDatasetFault,
	ConnectException,
	RepositoryServiceFault,
	SocketTimeoutException,
	NotAuthorisedFault
	{
		boolean localCopyExists = false;
		DataSet dsSummary = repClient.getDataSetSummary(projectCode, new Date(0), saml);

		//iterate local sets and check if datasetname exists there
		for (int j=0; j<localSets.size(); j++) {
			if (localSets.get(j).getDs().getName().equals(dsSummary.getName())) {
				localCopyExists = true;
			}
		}

		return localCopyExists;

	}

	/**
	 * Check if current dataset exists locally
	 * @param repClient the repository client to query
	 * @param dsSet the DSDataSet which needs a remote dataset
	 * @param projectCode project code of the dataset to fetch
	 */
	private static void initRemoteDataset(RepositoryClient repClient,
			StudyDataSet dsSet, 
			String projectCode,
			String saml)
	throws RepositoryServiceFault,
	RepositoryNoSuchDatasetFault,
	NotAuthorisedFault,
	ConnectException,
	SocketTimeoutException {
		DataSet dsSummary = repClient.getDataSetSummary(projectCode, new Date(0), saml);
		dsSet.setDs(repClient.getDataSet(dsSummary.getId(), saml));
	}

	/**
	 * Set the roles for the dataset
	 * 
	 * @param dsSet the dataset 
	 * @param pt the ProjectType to get the roles for
	 */
	private static void initRemoteGroups(StudyDataSet dsSet, ProjectType pt) 
							throws PGSecurityException, 
							ConnectException,
							NotAuthorisedFaultMessage {
		ArrayList<GroupModel> groups = new ArrayList<GroupModel>();
		DataSet ds = dsSet.getDs();
		for (int z=0; z<ds.numGroups(); z++) {
			GroupModel gm = new GroupModel();
			gm.setGroup(ds.getGroup(z));
			GroupType[] gts = SecurityHelper.getAAQueryClient().getGroupsInProject(pt);
			for (int g=0; g<gts.length; g++) {
				if (gts[g].getName().equals(ds.getGroup(z).getName())){
					gm.setId(gts[g].getIdCode());
				}
			}
			groups.add(gm);
		}
		dsSet.setGroupModels(groups);
	}

	/**
	 * Set the roles for the dataset
	 * 
	 * @param dsSet the dataset 
	 * @param pt the ProjectType to get the roles for
	 */
	private static void initRemoteReports(StudyDataSet dsSet, String saml) 
		throws ConnectException,
			RepositoryNoSuchDatasetFault,
			SocketTimeoutException,
			NotAuthorisedFault,
			RepositoryServiceFault
			 {
		RepositoryClient client = new RepositoryClient();
		
		DataSet dsSummary = client.getDataSetSummary(dsSet.getDs().getProjectCode(),
                new  Date(0),  saml);

		ReportsClient reportsClient = new ReportsClient();
		
		ArrayList<IReport> allReports = new ArrayList<IReport>(reportsClient.getAllReportsByDataSet(dsSummary.getId(), 
				saml));
		
		for (IReport r: allReports) {
			for (String defaultRep: DefaultDSSettings.getAllReports()) {
				if (r.getTitle().contains(new StringBuffer(defaultRep))) {
					dsSet.getReports().add(defaultRep);
				}
			}
		}
	}

	/**
	 * Set the randomization parameters for this dataset
	 * @param dsSet the DSDataSet to configure
	 * @throws PGSecurityException
	 * @throws PGSecurityInvalidSAMLException
	 * @throws ConnectException
	 * @throws NotAuthorisedFaultMessage
	 * @throws SocketTimeoutException
	 */
	private static void initRemoteRandomization(StudyDataSet dsSet, String saml) 
					throws ConnectException,
						SocketTimeoutException
	{

		RandomisationHolderModel randomModel = new RandomisationHolderModel();
		
		try {
			RandomizationClient randomClient = new RandomizationClient(new URL(PropertiesHelper.getRandomizationLocation()));
			Object rndmzr = randomClient.getRandomizer(dsSet.getDs().getProjectCode(), 
					saml);
			if (rndmzr instanceof StratifiedRandomizer) {
				StratifiedRandomizer srndmzr = (StratifiedRandomizer)rndmzr;
				randomModel.setRandomisationStrata(new ArrayList(srndmzr.getStrata()));
				srndmzr.getCombinations();
				ArrayList<StratumCombination> sCombs = new ArrayList<StratumCombination>(srndmzr.getCombinations());
				//find the block size 
				if (sCombs.size() > 0) {
					int minBlockSize = sCombs.get(0).getRandomizer().getMinBlockSize() * (srndmzr.getStrata().size());
					int maxBlockSize = sCombs.get(0).getRandomizer().getMaxBlockSize() * (srndmzr.getStrata().size());
					randomModel.setMinimumBlockSize(minBlockSize);
					randomModel.setMaximumBlockSize(maxBlockSize);
					ArrayList<Treatment> treatments = new ArrayList<Treatment>((((BlockRandomizer)sCombs.get(0).getRandomizer()).getTreatments()));
					ArrayList<TreatmentHolderModel> thms = new ArrayList<TreatmentHolderModel>();
					for (int i=0; i<treatments.size(); i++) {
						TreatmentHolderModel thm = new TreatmentHolderModel();
						thm.setTreatmentCode(treatments.get(i).getCode());
						thm.setTreatmentName(treatments.get(i).getName());
						thms.add(thm);
					}
					randomModel.setRandomisationTreatments(thms);
				}
			}			
			
			dsSet.setRandomHolderModel(randomModel);
			EslClient eslClient = new EslClient(new URL(PropertiesHelper.getESLLocation()));
			IProject project = eslClient.retrieveProjectByCode(dsSet.getDs().getProjectCode(), 
					saml);
			ArrayList<IRole> eslRoles = new ArrayList<IRole>(project.getRandomisation().getRolesToNotify());
			ESLEmailModel emailModel = new ESLEmailModel();
			emailModel.setRoles(eslRoles);
		} catch (RandomizationFault raf) {
			LOG.error("Randomization fault is " + raf.getMessage());
			raf.printStackTrace();
		} catch (UnknownRandomizerFault uaf) {
			LOG.error("Unknown randomizer fault is "  + uaf.getMessage());
			uaf.printStackTrace();
		} catch (org.psygrid.randomization.NotAuthorisedFault naf) {
			LOG.error("Not authorised fault " + naf.getMessage());
			naf.printStackTrace();
		} catch (ESLServiceFault esf) {
			LOG.error("ESL service fault " + esf.getMessage());
			esf.printStackTrace();
		} catch (org.psygrid.esl.services.NotAuthorisedFault nesf) {
			LOG.error("Not authorised fault " + nesf.getMessage());
			nesf.printStackTrace();
		} catch (MalformedURLException murlex) {
			LOG.error("Malformed URL exception " + murlex.getMessage());
			murlex.printStackTrace();
		}

	}

}



