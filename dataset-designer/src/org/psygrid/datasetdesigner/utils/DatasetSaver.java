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
package org.psygrid.datasetdesigner.utils;

import java.awt.Cursor;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingworker.SwingWorker;
import org.psygrid.collection.entry.security.SecurityHelper;
import org.psygrid.collection.entry.security.SecurityManager;
import org.psygrid.common.ui.WrappedJOptionPane;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.reporting.Chart;
import org.psygrid.data.reporting.client.ReportsClient;
import org.psygrid.data.reporting.definition.IBasicStatisticsChart;
import org.psygrid.data.reporting.definition.ICollectionDateChart;
import org.psygrid.data.reporting.definition.IDocumentStatusChart;
import org.psygrid.data.reporting.definition.IGroupsSummaryChart;
import org.psygrid.data.reporting.definition.IManagementReport;
import org.psygrid.data.reporting.definition.IProjectSummaryChart;
import org.psygrid.data.reporting.definition.IReceivingTreatmentChart;
import org.psygrid.data.reporting.definition.IRecordStatusChart;
import org.psygrid.data.reporting.definition.IRecruitmentProgressChart;
import org.psygrid.data.reporting.definition.IReport;
import org.psygrid.data.reporting.definition.IStdCodeStatusChart;
import org.psygrid.data.reporting.definition.IUKCRNSummaryChart;
import org.psygrid.data.reporting.definition.ReportFrequency;
import org.psygrid.data.repository.RepositoryOutOfDateFault;
import org.psygrid.data.repository.client.RepositoryClient;
import org.psygrid.data.utils.security.NotAuthorisedFault;
import org.psygrid.datasetdesigner.controllers.DatasetController;
import org.psygrid.datasetdesigner.model.DocTreeModel;
import org.psygrid.datasetdesigner.model.ESLEmailModel;
import org.psygrid.datasetdesigner.model.GroupModel;
import org.psygrid.datasetdesigner.model.RandomisationHolderModel;
import org.psygrid.datasetdesigner.model.StudyDataSet;
import org.psygrid.datasetdesigner.ui.MainFrame;
import org.psygrid.esl.model.ICustomField;
import org.psygrid.common.email.Email;
import org.psygrid.esl.model.IProject;
import org.psygrid.esl.model.IRandomisation;
import org.psygrid.esl.model.IStrata;
import org.psygrid.esl.randomise.EmailType;
import org.psygrid.esl.services.client.EslClient;
import org.psygrid.randomization.client.RandomizationClient;
import org.psygrid.randomization.model.hibernate.RpmrblRandomizer;
import org.psygrid.randomization.model.hibernate.StratifiedRandomizer;
import org.psygrid.randomization.model.hibernate.Stratum;
import org.psygrid.security.DefaultPolicy2;
import org.psygrid.security.RBACAction;
import org.psygrid.security.RBACRole;
import org.psygrid.security.RBACTarget;
import org.psygrid.security.attributeauthority.client.AAManagementClient;
import org.psygrid.security.policyauthority.client.PAManagementClient;
import org.psygrid.security.utils.TargetAssessor;
import org.psygrid.www.xml.security.core.types.AttributeType;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.PolicyDescriptionType;
import org.psygrid.www.xml.security.core.types.PolicyType;
import org.psygrid.www.xml.security.core.types.PrivilegeType;
import org.psygrid.www.xml.security.core.types.ProjectDescriptionType;
import org.psygrid.www.xml.security.core.types.ProjectType;
import org.psygrid.www.xml.security.core.types.RoleType;
import org.psygrid.www.xml.security.core.types.StatementType;
import org.psygrid.www.xml.security.core.types.TargetType;

import randomX.randomHotBits;
import randomX.randomX;

/**
 * @author pwhelan
 *
 */
public class DatasetSaver {
	
	/**
	 * The logger for this class
	 */
	private static final Log LOG = LogFactory.getLog(DatasetSaver.class);
	
	private static final String STRING_PREFIX = "org.psygrid.datasetdesigner.utils.datasetsaver.";

	private static final String AA_SAVING_SUCCESS = "Project saved to security module. \n";

	private static final String AA_SAVING_FAILED = "Saving project to security module failed.\n.";
	
	private static final String PA_SAVING_SUCCESS = "Policy saved to security module.  \n";
	
	private static final String PA_SAVING_FAILED = "Saving policy to security module failed.  \n";

	private static final String ESL_SAVING_SUCCESS = "Participant register saved.  \n";
	
	private static final String ESL_SAVING_FAILED = "Saving participant register failed. \n";
	
	private static final String RANDOMIZER_SAVING_SUCCESS = "Randomizer saved.  \n";
	
	private static final String RANDOMIZER_SAVING_FAILED = "Saving randomizer failed.  \n";
	
	private static final String REPORTS_SAVING_SUCCESS = "Reports saved.  \n";
	
	private static final String REPORTS_SAVING_FAILED = "Saving reports failed.  \n";
	
	private static final String REPOSITORY_SAVING_SUCCESS = "Study saved to the database. \n";
	
	private static final String REPOSITORY_SAVING_FAILED = "Failed to save study to the database. \n";
	
	private static final String REPOSITORY_PATCHING_FAILED = "Failed to patch the study to the database. \n";
	
	private static final String REPOSITORY_PATCHING_SUCCESS = "Study patched to the database. \n";
	
	private static final String REPORTS_PATCHING_SUCCESS = "Reports patched to the database. \n";

	private static final String REPORTS_PATCHING_FAILED = "Failed to patch reports to the database. \n";
	
	private static final Integer OVERALL_SAVE_SUCCESS = new Integer(0);
	
	private static final Integer OVERALL_SAVE_FAIL = new Integer(-1);
	
	
	
	/**
	 * The dataset to save
	 */
	private StudyDataSet ds;
	
	/**
	 * The main frame of the application
	 */
	private MainFrame frame;

	/**
	 * The saved hibernate id of the dataset
	 */
	private Long savedId = null;

	/**
	 * Client for contacting the repository
	 */
	private RepositoryClient repositoryClient;
	
	/**
	 * String to contain the message of successful saving of components
	 */
	private String successString = "";

	/**
	 * boolean flag to indicate if dataset should be published
	 */
	private boolean publish;
	
	/**
	 * The project type for communicating with the AA
	 */
	private ProjectType project = null;
	
	//exit after saving
	private boolean exit = false;
	
	public DatasetSaver(MainFrame frame, StudyDataSet ds, boolean publish) {
		this.frame = frame;
		this.ds = ds;
		this.publish = publish;
	}
	
	public boolean saveDataset(boolean exit) {
		this.exit = exit;
		return saveDataset();
	}
	
	public boolean doAllPrepStages() {
		//first autosave
		LocalFileUtility.autosave();
		
		//check that all components have been configured successfully before starting to save
		if (!ds.isFullyConfigured()) {
			WrappedJOptionPane.showMessageDialog(frame, PropertiesHelper.getStringFor("org.psygridatasetdesigner.utils.datasetsaver.notfullyconfigured"));
			return false;
		}
		
		//stop here if permission not granted to close the open tabs
		if (!closeOpenTabs()) {
			return false;
		}
		
		//stop here if dataset was not prepared properly
		if (!prepareDataset()) {
			return false;
		}
		
		return true;
	}
	
	public boolean patchDataset() {
		if (!doAllPrepStages()) {
			return false;
		}
		
		//start progress indication
		frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		frame.setStarted();
		
		SwingWorker worker = 
	          new SwingWorker<Integer, Void>() {
	          public Integer doInBackground() {
	        	  
	        	  
	        	    //save to the AA
	        	  
		      		String aaResult = saveToAA();
		      		successString += aaResult;
	      			if (aaResult.equals(AA_SAVING_FAILED)) {
	      				return OVERALL_SAVE_FAIL;
	      			}
	      			
	      			//We no longer need to update the pa once pa statement migration has been done.
	      			
	      			//save to repository; if not successful, try to remove from AA & PA
	      			String repResult = patchToRepository();
	      			successString += repResult;
	      			if (repResult.contains(REPOSITORY_PATCHING_FAILED)) {
	      				return OVERALL_SAVE_FAIL;
	      			}
	      			
	      			
	      			//save the ESL
		    		if (ds.getDs().isEslUsed()) {
		    			successString += patchBasicESL();
		    			if (successString.contains(ESL_SAVING_FAILED)) {
		    				return OVERALL_SAVE_FAIL;
		    			}
		    		}
	      			
		    		//save the reports;
		    		String reportPatching = patchReports();
		    		successString += reportPatching;
		    		if (repResult.contains(REPORTS_PATCHING_FAILED)) {
		    			return OVERALL_SAVE_FAIL;
		    		}
		    		
	      			return OVERALL_SAVE_SUCCESS;
	      			
	          }
	          public void done() {
	        	  try {
		        	  //all saved fine, update status bar accordingly
		        	  if (get().equals(OVERALL_SAVE_SUCCESS)) {
		          		//check here if it is still the active ds - it runs in thread
		          		//so active ds might have changed
		        		if (DatasetController.getInstance().getActiveDs() != null) {
			          		if (DatasetController.getInstance().getActiveDs().getDs().getProjectCode().equals(ds.getDs().getProjectCode())) {
			          			if (project != null) {
				              		ds = DatasetLoader.loadDatasetFromProject(project);
			              		}
			              		DatasetController.getInstance().setActiveDs(ds);
			              		//refresh the three
			              		DocTreeModel.getInstance().refreshDataset(ds);
			              		frame.getTree().revalidate();
			          		}
		        		}
		        		  
		  	    		//different messages for published or saved
		  	    		JOptionPane.showMessageDialog(frame, "Study patched successfully. \n" + successString);
	        			frame.setStatusBarText(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.studypatchedtorep") + " " + ds.getDs().getName()+ " at " + Utils.getFormattedNow());
		        		//update the last stored location
			    		DatasetController.getInstance().getActiveDs().setLastStoredLocation(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.repkey"));
			    		  
		        	  } else {
		        		  WrappedJOptionPane.showMessageDialog(frame, successString, PropertiesHelper.getStringFor("org.psygridatasetdesigner.utils.datasetsaver.datasetsaving"), JOptionPane.ERROR_MESSAGE);
			          	  frame.setStatusBarText(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.studypatchedtorepfailed") + " " + ds.getDs().getName() + " at " + Utils.getFormattedNow());
		        	  }
		        	  
		        	  frame.setFinished();
		        	  frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	        	  } catch (Exception ex){
	        		  LOG.error("Exception updating status after saving", ex);
	        		  ex.printStackTrace();
	        	  }
	          }
	       };
	       
	    worker.execute();
	        	  
		return true;
		
	}
	
	public boolean saveDataset() {

		if (!doAllPrepStages()) {
			return false;
		}
		
		//start progress indication
		frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		frame.setStarted();
		
		SwingWorker worker = 
	          new SwingWorker<Integer, Void>() {
	          public Integer doInBackground() {
	      		//refresh the saml before starting deletion
	      		refreshKey();
	      		
	      		//try to remove the ESL, the randomizer and the reports in case they already exist
	      		removeESL();
	      		removeRandomisation();
	      		removeReports();
	      		
      			//save to the AA
	      		String aaResult = saveToAA();
	      		successString += aaResult;
      			if (aaResult.equals(AA_SAVING_FAILED)) {
      				return OVERALL_SAVE_FAIL;
      			}

      			//save to the PA; if not successful, try to remove from AA & PA
      			String paResult = saveToPA();
      			successString += paResult;
      			if (paResult.equals(PA_SAVING_FAILED)) {
      				return OVERALL_SAVE_FAIL;
      			}

	    		//save the ESL
	    		if (ds.getDs().isEslUsed()) {
	    			successString += saveBasicESL();
	    			if (successString.contains(ESL_SAVING_FAILED)) {
	    				return OVERALL_SAVE_FAIL;
	    			}
	    		}

	    		// randomization is set so should be configured
	    		if (ds.getDs().isRandomizationRequired()) {
	    			successString += saveRandomization(ds);
	    			if (successString.contains(RANDOMIZER_SAVING_FAILED)) {
	    				return OVERALL_SAVE_FAIL;
	    			}
	    		}
      			
      			//save to repository; if not successful, try to remove from AA & PA
      			String repResult = saveToRepository(publish);
      			successString += repResult;
      			if (repResult.contains(REPOSITORY_SAVING_FAILED)) {
      				return OVERALL_SAVE_FAIL;
      			} 
      			
	    		//save the reports; not really fatal.
	    		if (ds.getReports().size() > 0) {
	    			String reportsResult = saveReports(ds.getDs());
	    		 	successString += reportsResult;
	    		 	if (reportsResult.contains(REPORTS_SAVING_FAILED)) {
	    		 		return OVERALL_SAVE_FAIL;
	    		 	}
	    		}

	    		//if we get here, everything has saved OK, so add OVERALL save to the string
	    		return OVERALL_SAVE_SUCCESS;
	          }
	          public void done() {
	        	  try {
		        	  //all saved fine, update status bar accordingly
		        	  if (get().equals(OVERALL_SAVE_SUCCESS)) {
		          		//check here if it is still the acitve ds - it runs in thread
		          		//so active ds might have changed
		        		if (DatasetController.getInstance().getActiveDs() != null) {
			          		if (DatasetController.getInstance().getActiveDs().getDs().getProjectCode().equals(ds.getDs().getProjectCode())) {
			              		//reload the dataset to get the most recent ids etc
			              		if (project != null) {
				              		ds = DatasetLoader.loadDatasetFromProject(project);
			              		}
			              		DatasetController.getInstance().setActiveDs(ds);
			              		
			              		//refresh the three
			              		DocTreeModel.getInstance().refreshDataset(ds);
			              		frame.getTree().revalidate();
			          		}
		        		}
		        		  
		  	    		  //different messages for published or saved
		        		  if (publish) {
			  	    		  JOptionPane.showMessageDialog(frame, "Study saved and published successfully. \n" + successString);
		        			  frame.setStatusBarText(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.studypublishedtorep") + " " + ds.getDs().getName() + " " + Utils.getFormattedNow());
		        		  } else {
			  	    		  JOptionPane.showMessageDialog(frame, "Study saved successfully. \n" + successString);
		        			  frame.setStatusBarText(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.studysavedtorep") + " " + ds.getDs().getName()+ " at " + Utils.getFormattedNow());
		        		  }
		        		  //update the last stored location
			    		  DatasetController.getInstance().getActiveDs().setLastStoredLocation(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.repkey"));
			    		  
			    		  //if using exit call, then exit here
			        	  if (exit) {
			        		  System.exit(0);
			        	  }
			    		  
		        	  } else {
		        		  WrappedJOptionPane.showMessageDialog(frame, successString, PropertiesHelper.getStringFor("org.psygridatasetdesigner.utils.datasetsaver.datasetsaving"), JOptionPane.ERROR_MESSAGE);
		        		  if (publish) {
				          	 frame.setStatusBarText(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.studysavingandpublishingtorepfailed") + " " + ds.getDs().getName() + " at " + Utils.getFormattedNow());
		        		  } else {
		        			 frame.setStatusBarText(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.studysavedtorepfailed") + " " + ds.getDs().getName());
		        		  }
		        	  }
		        	  
		        	  frame.setFinished();
		        	  frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	        	  } catch (Exception ex){
	        		  LOG.error("Exception updating status after saving", ex);
	        		  ex.printStackTrace();
	        	  }
	          }
	       };
	       
	       worker.execute();
	       
	       return true;
	    }

	/*
	 * Get the groups for the dataset
	 * @return an array of <code>GroupType</code> containing the groups for the dataset
	 */
	
	private GroupType[] getGroups() {
		GroupType[] gta = null;
		ArrayList<GroupModel> groups = ds.getGroups();
		if (gta == null) {
			gta = new GroupType[groups.size()];
			for (int i=0; i<groups.size(); i++) {
				gta[i] = new GroupType(groups.get(i).getGroup().getLongName(), 
						groups.get(i).getGroup().getName(), 
						ds.getDs().getProjectCode());
			}
		}
		return gta;
	}
	
	
	/*
	 * Get the roles for the dataset
	 * @return an array of <code>RoleType</code> containing the roles for the dataset
	 */
	private RoleType[] getRoles() {
		//always add the project manager; needed for install project
		RoleType pm = RBACRole.ProjectManager.toRoleType();
		RoleType[] roles = new RoleType[ds.getRoles().size()+2];
		roles[0] = pm;
	
		RoleType sa = RBACRole.SystemAdministrator.toRoleType();
		roles[1] = sa;
	
		for (int j=2; j<ds.getRoles().size()+2; j++) {
			roles[j] = (RoleType)ds.getRoles().get(j-2);
		}
	
		return roles;
	}
	
	
	/**
	 * Install the default policy for the project
	 * @param projName name of project
	 * @param projCode code of project
	 * @return turn if intallation of policy is successful; false if not
	 */
	private boolean installProjectPolicy(String projName, String projCode) {
	
		PolicyDescriptionType pdt = new PolicyDescriptionType();
		pdt.setActions(RBACAction.allActions());		
		pdt.setTargets(RBACTarget.allAsTargets());
		pdt.setPrivileges(RBACRole.allAsPrivileges());
	
		try {
			PAManagementClient mc = SecurityHelper.getPAManagementClient();
			List<StatementType> lst = DefaultPolicy2.buildStatements();
	
			for(StatementType s : lst){
				//Print out those actions that resulted in a target that's a centre
				TargetType t = s.getTarget();
				if(TargetAssessor.targetIsCentre(t)){
					System.out.println("Action: " + s.getAction().getName());
				}
			}
			
			try {
				StatementType[] sta = new StatementType[lst.size()];
				int i = 0;
				for (StatementType st : lst) {
					sta[i] = st;
					i++;
				}
	
				PolicyType pt = new PolicyType(projName, projCode, pdt, sta);
				PolicyType[] pta = new PolicyType[] { pt };
				boolean r = mc.getPort().addPolicy(pta);
				return r;
			} catch (Exception e) {
				e.printStackTrace();
				LOG.error("Error occurred installing the policy" + e.getMessage());
				return false;
			}
	
		} catch (Exception pgse) {
			pgse.printStackTrace();
			LOG.error("Error occurred fetching the PA and policy statements " + pgse.getMessage());
			return false;
		}
	}
	
	/**
	 * Get all the targets to be used with policy
	 * @param gta the groups of the dataset
	 * @return the <code>TargetType</code> of the dataset
	 */
	public static TargetType[] allAsTargets(GroupType[] gta){
		TargetType[] tta = new TargetType[gta.length];
		for(int i=0; i <gta.length; i++){
			tta[i]=new TargetType(gta[i].getName(),gta[i].getIdCode());
		}
		return tta;
	}
	
	/**
	 * Get all the privilege types to be used with policy
	 * @param gta the groups of the dataset
	 * @return the <code>PrivilegeType</code> of the dataset
	 */
	public static PrivilegeType[] allAsPrivileges(GroupType[] gta){
		PrivilegeType[] tta = new PrivilegeType[gta.length];
		for(int i=0; i <gta.length; i++){
			tta[i]=new PrivilegeType(null, gta[i]);
		}
		return tta;
	}
	
	/**
	 * Get an array of empty groups
	 * @return an empty array of no groups
	 */
	public static GroupType[] noGroups(){
		return new GroupType[]{};
	}
	
	private String saveStratifiedRandomization(StudyDataSet dSet){
		String randomResult = RANDOMIZER_SAVING_FAILED;
	
		try{
			RandomizationClient client;
			client = new RandomizationClient(new URL(PropertiesHelper.getRandomizationLocation()));
			StratifiedRandomizer rnd = new StratifiedRandomizer(dSet.getDs().getProjectCode());
			RandomisationHolderModel rhm  = ds.getRandomHolderModel();
			
			if (rhm != null && rhm.getRandomisationStrata() != null &&
					rhm.getRandomisationStrata().size() > 0) {
				int seedlen = 0;
				for (int j=0; j<rhm.getRandomisationStrata().size(); j++) {
					Stratum s = rhm.getRandomisationStrata().get(j);

					configureStrataValues(s);

					rnd.addStratum(s);

					if (seedlen == 0) {
						seedlen = s.getValues().size();
					} else {
						seedlen *= s.getValues().size();
					}
				}

				int minBlockSize = rhm.getMinimumBlockSize();
				int maxBlockSize = rhm.getMaximumBlockSize();
				minBlockSize /= rhm.getRandomisationTreatments().size();
				maxBlockSize /= rhm.getRandomisationTreatments().size();

				rnd.generateCombinations("org.psygrid.randomization.model.hibernate.IBRpbrblRandomizer", minBlockSize, maxBlockSize);
				long[] seeds= new long[seedlen];

				for (int i=0; i<seedlen; i++) {
					seeds[i] = getSeed();
				}

				rnd.createRngs(seeds);

				if (rhm.getRandomisationTreatments() != null) {
					for (int i=0; i<rhm.getRandomisationTreatments().size(); i++) {
						rnd.addTreatment(rhm.getRandomisationTreatments().get(i).getTreatmentName(), rhm.getRandomisationTreatments().get(i).getTreatmentCode());
					}
				}

				client.saveRandomizer(rnd, SecurityManager.getInstance().getSAMLAssertion());

				randomResult = RANDOMIZER_SAVING_SUCCESS;
			}
		}catch(Exception ex){
			ex.printStackTrace();
			LOG.error("Error saving the randomisation service ", ex);
		}
		
		return randomResult;
	}
	
	private String saveBlockRandomization(StudyDataSet dSet){
		
		String randomResult = RANDOMIZER_SAVING_FAILED;
		
		try{
			RandomizationClient client;
			client = new RandomizationClient(new URL(PropertiesHelper.getRandomizationLocation()));
			RpmrblRandomizer rnd = new RpmrblRandomizer(dSet.getDs().getProjectCode());
			RandomisationHolderModel rhm  = ds.getRandomHolderModel();
			
			int minBlockSize = rhm.getMinimumBlockSize();
			int maxBlockSize = rhm.getMaximumBlockSize();
			minBlockSize /= rhm.getRandomisationTreatments().size();
			maxBlockSize /= rhm.getRandomisationTreatments().size();
			
			rnd.setMaxBlockSize(maxBlockSize);
			rnd.setMinBlockSize(minBlockSize);
			
			rnd.createRng(getSeed());
			
			if (rhm.getRandomisationTreatments() != null) {
				for (int i=0; i<rhm.getRandomisationTreatments().size(); i++) {
					rnd.addTreatment(rhm.getRandomisationTreatments().get(i).getTreatmentName(), rhm.getRandomisationTreatments().get(i).getTreatmentCode());
				}
			}
			
			client.saveRandomizer(rnd, SecurityManager.getInstance().getSAMLAssertion());

			randomResult = RANDOMIZER_SAVING_SUCCESS;
			
		}catch(Exception ex){
			ex.printStackTrace();
			LOG.error("Error saving the randomisation service ", ex);
		}
					
		return randomResult;
	}

	/**
	 * Save the randomisation for this dataset
	 * @param dSet the dataset
	 * @param saml the saml of the current user
	 * @return true if saving is successful; false if not
	 */
	public String saveRandomization(StudyDataSet dSet) {
		String randomResult = RANDOMIZER_SAVING_FAILED;

			
		RandomisationHolderModel rhm = dSet.getRandomHolderModel();
		ArrayList<Stratum> strataList = rhm.getRandomisationStrata();

		if(strataList != null){ //This list should never be null.
			if(strataList.size() > 0){
				randomResult = this.saveStratifiedRandomization(dSet);
			}else{
				randomResult = this.saveBlockRandomization(dSet);
			}
		}
			
		return randomResult;
	}


	/**
	 * Save the reports for the dataset
	 * @param savedSet the dataset to save
	 * @param saml saml of current user
	 * @return true if saving is successful; false if not
	 */
	public String saveReports(DataSet savedSet) {
		String reportsResults = REPORTS_SAVING_SUCCESS;
		try {
			ReportsClient client = new ReportsClient();
			org.psygrid.data.reporting.definition.hibernate.HibernateFactory factory = new org.psygrid.data.reporting.definition.hibernate.HibernateFactory();
			ArrayList<IManagementReport> mgmtReports = new ArrayList<IManagementReport>();
	
			if (ds.getReports() == null) {
				return REPORTS_SAVING_FAILED;
			}
	
			for (int i=0; i<ds.getReports().size(); i++) {	
				String reportName = ds.getReports().get(i);
	
				if (reportName.equals("Record Status Report")) {
					IManagementReport report = factory.createManagementReport(savedSet, savedSet.getName() + " - Record Status Report");
					report.setDataSet(savedSet);
					report.setEmailAction(RBACAction.ACTION_DR_CRM_REPORT);
					report.setViewAction(RBACAction.ACTION_DR_STATUS_REPORT);
					report.setWithRawData(true);
					report.setTemplate(true);
					report.setFrequency(ReportFrequency.NEVER);
					/* 
					 * Create a tabular chart showing the records in the groups provided and their current status
					 */
					IRecordStatusChart chart = factory.createRecordStatusChart(Chart.CHART_TABLE, "Records ");
					chart.setRangeAxisLabel("");	//y-axis label
					report.addManagementChart(chart);
	
					mgmtReports.add(report);
				} else if (reportName.equals("Document Status Report")) {
					IManagementReport report = factory.createManagementReport(savedSet, savedSet.getName() + " - Document Status Report");
					report.setDataSet(savedSet);
					report.setEmailAction(RBACAction.ACTION_DR_CRM_REPORT);
					report.setViewAction(RBACAction.ACTION_DR_STATUS_REPORT);
					report.setWithRawData(true);
					report.setTemplate(true);
					report.setFrequency(ReportFrequency.NEVER);
	
					/* 
					 * Create a tabular chart showing the records in the groups provided and their current status
					 */
					IDocumentStatusChart chart = factory.createDocumentStatusChart(Chart.CHART_TABLE, "Documents ");
					//chart.setRangeAxisLabel("");	//y-axis label
	
					//check
					for (int j=0; j<savedSet.numGroups(); j++) {
						chart.addGroup(savedSet.getGroup(j));
					}
					report.addManagementChart(chart);
					mgmtReports.add(report);
				} else if (reportName.equals("Recruitment Progress Report")) {
					IManagementReport report = factory.createManagementReport(savedSet, savedSet.getName() + " - Recruitment Progress Report");
					report.setDataSet(savedSet);
					report.setEmailAction(RBACAction.ACTION_DR_INVESTIGATOR_REPORT);
					report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
					//report.setRole(role);
					report.setWithRawData(true);
					report.setFrequency(ReportFrequency.MONTHLY);
	
					/* 
					 * Create a timeseries chart showing the number of subjects consented
					 * into the trial against the targets set for each month, giving
					 * a view of the trial's progress. 
					 */
					IRecruitmentProgressChart chart = factory.createRecruitmentProgressChart(Chart.CHART_TIME_SERIES, "Recruitment Progress");
	
					//Set this automatically when the chart is generated (will show previous 6 months by default)
					chart.setTimePeriod(null, null);
	
					chart.setRangeAxisLabel("Total Number of Participants");	//y-axis label
	
					//check
					for (int j=0; j<savedSet.numGroups(); j++) {
						chart.addGroup(savedSet.getGroup(j));
					}
	
					report.addManagementChart(chart);
					mgmtReports.add(report);
	
				} else if (reportName.equals("UKCRN Report")) {
					IManagementReport report = factory.createManagementReport(savedSet, savedSet.getName() + " - UKCRN Report");
					report.setDataSet(savedSet);
					report.setEmailAction(RBACAction.ACTION_DR_UKCRN_REPORT);
					report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
					report.setWithRawData(true);
					report.setFrequency(ReportFrequency.MONTHLY);
					report.setShowHeader(false);
	
					IUKCRNSummaryChart chart = factory.createUKCRNSummaryChart(org.psygrid.data.reporting.Chart.CHART_TABLE, ds.getDs().getName());
					report.addChart(chart);
	
					//This will be set automatically when the report is generated to create
					//a for the current financial year (assuming may-april)
					chart.setTimePeriod(null, null);
					mgmtReports.add(report);
				} else if (reportName.equals("Standard Code Status Report")) {
					IManagementReport report = factory.createManagementReport(savedSet, savedSet.getName() + " - Standard Code Status Report");
					report.setEmailAction(RBACAction.ACTION_DR_INVESTIGATOR_REPORT);
					report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
					report.setWithRawData(true);
					report.setTemplate(true);
					report.setFrequency(ReportFrequency.NEVER);
	
					/* 
					 * Create a chart showing percentage occurrence of std codes for entries in the documents specified for the records in the 
					 * groups provided
					 */	
					IStdCodeStatusChart pansschart2 = factory.createStdCodeStatusChart(Chart.CHART_BAR, 
					"Document Percentage Usage");
					pansschart2.setUsePercentages(true);
					pansschart2.setPerDocument(true);
					pansschart2.setRangeAxisLabel("");	//y-axis label
	
					report.addManagementChart(pansschart2);
	
					/* 
					 * Create a chart showing percentage occurrence of std codes for entries in the documents specified for the records in the 
					 * groups provided
					 */	
					IStdCodeStatusChart pansschart = factory.createStdCodeStatusChart(Chart.CHART_TABLE, 
					"Question Percentage Usage");
					pansschart.setUsePercentages(true);
					pansschart.setPerEntry(true);
					pansschart.setRangeAxisLabel("");	//y-axis label
	
					report.addManagementChart(pansschart);
	
					/* 
					 * Create a chart showing usage of std codes for entries in the documents specified for the records in the 
					 * groups provided
					 */	
					IStdCodeStatusChart pansschart1 = factory.createStdCodeStatusChart(Chart.CHART_TABLE, 
					"Usage Per Patient");
					pansschart1.setUsePercentages(false);
					pansschart1.setRangeAxisLabel("");	//y-axis label
	
					report.addManagementChart(pansschart1);
					mgmtReports.add(report);
				} else if (reportName.equals("Basic Statistic Report")) {
					IManagementReport report = factory.createManagementReport(savedSet, savedSet.getName() + " - Basic Statistic Report");
					report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
					report.setWithRawData(false);
					report.setTemplate(true);
					report.setFrequency(ReportFrequency.NEVER);
	
					/* 
					 * Create a chart showing percentage occurrence of std codes for entries in the documents specified for the records in the 
					 * groups provided
					 */	
					IBasicStatisticsChart basicStatsChart = factory.createBasicStatisticsChart(Chart.CHART_TABLE, 
					"Statistics");
					basicStatsChart.setUsePercentages(false);
					basicStatsChart.setRangeAxisLabel("");	//y-axis label
	
					report.addChart(basicStatsChart);
					mgmtReports.add(report);
				} else if (reportName.equals("Collection Date Report")) {
					IManagementReport report = factory.createManagementReport(savedSet, savedSet.getName() + " - Collection Date Report");
					report.setEmailAction(RBACAction.ACTION_DR_INVESTIGATOR_REPORT);
					report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
					report.setWithRawData(true);
					report.setTemplate(true);
					report.setFrequency(ReportFrequency.NEVER);
	
					/* 
					 * Create a tabular chart showing the records in the groups provided and their current status
					 */
					ICollectionDateChart chart = factory.createCollectionDateChart(Chart.CHART_TABLE, "Documents ");
					chart.setRangeAxisLabel("");	//y-axis label
	
					//check
					for (int j=0; j<savedSet.numGroups(); j++) {
						chart.addGroup(savedSet.getGroup(j));
					}
	
					report.addManagementChart(chart);
					mgmtReports.add(report);
					// no email action set here
				} else if (reportName.equals("Project Summary Report")) {
					IManagementReport report = factory.createManagementReport(savedSet, savedSet.getName() + " - Project Summary Report");
					report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
					report.setWithRawData(false);
	
					IProjectSummaryChart total = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_TABLE, "Total Clients");
					total.setShowTotal(true);
					report.addChart(total);
					IProjectSummaryChart chrt1 = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Whole Project");
					report.addChart(chrt1);
	
					for (int z=0; z<savedSet.numGroups(); z++) {
						IGroupsSummaryChart chrt = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, 
								savedSet.getDataSet().getGroup(z).getLongName());
						chrt.addGroup(savedSet.getGroup(z));
						report.addChart(chrt);
					}
	
					mgmtReports.add(report);
	
					// no email action set here
				} else if (reportName.equals("Group Summary Report")) {
					IManagementReport report = factory.createManagementReport(savedSet, savedSet.getName() + " - Group Summary Report");
					report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
					report.setWithRawData(true);
	//				report.setTemplate(false);
	
					for (int z=0; z<savedSet.numGroups(); z++) {
						IGroupsSummaryChart chrt = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, 
								savedSet.getDataSet().getGroup(z).getLongName());
						chrt.addGroup(savedSet.getGroup(z));
						report.addChart(chrt);
					}
	
					mgmtReports.add(report);
				} else if (reportName.equals("Receiving Treatment Report")) {
					IManagementReport report = factory.createManagementReport(savedSet, savedSet.getDataSet().getName()+" - Receiving Treatment Report");
					report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
					//report.setRole(role);
					report.setWithRawData(true);
					report.setFrequency(ReportFrequency.NEVER);
	
					/* 
					 * Create a chart showing a list of subjects for each treatment assigned
					 * by the randomisation service. 
					 */
					IReceivingTreatmentChart chart = factory.createReceivingTreatmentChart(Chart.CHART_TABLE, savedSet.getName());
	
					//The time period will be set through psygridweb, if not a chart will 
					//be generated for the previous six months based on current date.
					chart.setTimePeriod(null, null);
	
					for (int w=0; w<savedSet.numGroups(); w++) {
						chart.addGroup(savedSet.getGroup(w));
					}
	
					report.addManagementChart(chart);
					mgmtReports.add(report);
				}
			}
			for (int y=0; y<mgmtReports.size(); y++) {
				client.saveReport(mgmtReports.get(y), SecurityManager.getInstance().getSAMLAssertion());
			}
		} catch (Exception ex) {
			reportsResults = REPORTS_SAVING_FAILED;
			LOG.error("Error configuring the reports ", ex);
		}
			
		return reportsResults;
	}
	
	/**
	 * Save the basic ESL config for the dataset
	 * @param savedSet the dataset to save
	 * @param saml saml of current user
	 * @return true if saving is successful; false if not
	 */
	public String saveBasicESL() {
	
		String eslResult = ESL_SAVING_FAILED;
		
		//save basic proj info
		try {
			EslClient eslClient = new EslClient(new URL(PropertiesHelper.getESLLocation()));
			org.psygrid.esl.model.hibernate.HibernateFactory eslFactory = new org.psygrid.esl.model.hibernate.HibernateFactory();
	
			//basic project info
			IProject project = eslFactory.createProject(ds.getDs().getProjectCode());
			project.setProjectName(ds.getDs().getName());
			project.setProjectCode(ds.getDs().getProjectCode());
	
			for (int i=0; i<ds.getDs().numGroups(); i++) {
				project.setGroup(eslFactory.createGroup(ds.getDs().getGroup(i).getLongName(), 
						ds.getDs().getGroup(i).getName().toString()));	
			}
			
			//custom fields
			for (int i=0, c=ds.getDs().getEslCustomFieldCount(); i<c; i++ ){
				EslCustomField field = ds.getDs().getEslCustomField(i);
				ICustomField eslField = eslFactory.createCustomField(field.getName());
				for ( int j=0, d=field.getValueCount(); j<d; j++ ){
					eslField.addValue(field.getValue(j));
				}
				project.addCustomField(eslField);
			}
			
			if (ds.getDs().isRandomizationRequired()) {
				LOG.info("saving ESL randomisation found the project " + project.getProjectCode());
				//set up default emails - not customisable 
				Map<String,Email> emails = new HashMap<String,Email>();
				Email e1 = eslFactory.createEmail();
				e1.setSubject("Notification of Invocation");
				e1.setBody("Notification of Invocation");
				Email e2 = eslFactory.createEmail();
				e2.setSubject("Notification of Decision");
				e2.setBody("A treatment arm has been allocated to the subject '%subjectCode%'.");
				Email e3 = eslFactory.createEmail();
				e3.setSubject("Notification of Treatment");
				e3.setBody("The subject '%subjectCode%' has been allocated the treatment %treatment% (code: %treatmentCode%).\n\n" +
						"The subject has the following risk issues:\n\n" +
				"%riskIssues%");

				emails.put(EmailType.INVOCATION.type(), e1);
				emails.put(EmailType.DECISION.type(), e2);
				emails.put(EmailType.TREATMENT.type(), e3);
				
				//setUpRandomisation
				IRandomisation r = null;
				r = project.getRandomisation();

				if ( r == null) {
					r = eslFactory.createRandomisation(ds.getDs().getProjectCode());
				}

				RandomisationHolderModel rhm = ds.getRandomHolderModel();
				if (rhm != null) {
					ArrayList<Stratum> eslStrata = rhm.getRandomisationStrata();
					ArrayList<IStrata> iStrata = new ArrayList<IStrata>();
					//configure strata and add to randomisation
					for (int j=0; j<eslStrata.size(); j++) {
						IStrata s = eslFactory.createStrata(eslStrata.get(j).getName());
						configureEslStrataValues((org.psygrid.esl.model.hibernate.Strata)s);
						iStrata.add(s);
					}
					
					r.setStrata(iStrata);

					//configure treatments for randomisation
					for (int i=0; i<rhm.getRandomisationTreatments().size(); i++) {
						r.getTreatments().put(rhm.getRandomisationTreatments().get(i).getTreatmentCode(), 
								rhm.getRandomisationTreatments().get(i).getTreatmentName());
					}
	
					//configure email settings for randomisation
					r.setRolesToNotify(ds.getEslModel().getRoles());
					r.setEmails(emails);
					project.setRandomisation(r);
				}
			}	
			//save the project
			eslClient.saveProject(project, SecurityManager.getInstance().getSAMLAssertion());
			eslResult = ESL_SAVING_SUCCESS;
		} catch (Exception ex) {
			LOG.error("Exception saving participant register", ex);
		}
	
		return eslResult;
	}	
	
	
	/**
	 * Save the basic ESL config for the dataset
	 * @param savedSet the dataset to save
	 * @param saml saml of current user
	 * @return true if saving is successful; false if not
	 */
	public String patchBasicESL() {
	
		String eslResult = ESL_SAVING_FAILED;
		
		//save basic proj info
		try {
			EslClient eslClient = new EslClient(new URL(PropertiesHelper.getESLLocation()));
			org.psygrid.esl.model.hibernate.HibernateFactory eslFactory = new org.psygrid.esl.model.hibernate.HibernateFactory();
			IProject project = eslClient.retrieveProjectByCode(ds.getDs().getProjectCode(), SecurityManager.getInstance().getSAMLAssertion());
			
			//create list of existing group names
			List<String> groupCodes = new ArrayList<String>();
			for (org.psygrid.esl.model.IGroup g: project.getGroups()) {
				groupCodes.add(g.getGroupCode());
			}

			int newCentreCount = 0;
			
			if (project != null) {
				for (int i=0; i<ds.getDs().numGroups(); i++) {
					Group g = ds.getDs().getGroup(i);
					if (!groupCodes.contains(g.getName())) {
						project.setGroup(eslFactory.createGroup(g.getLongName(), 
								g.getName().toString()));
						
						newCentreCount++;
					}
				}
				
				//save the project
				eslClient.saveProject(project, SecurityManager.getInstance().getSAMLAssertion());
				eslResult = ESL_SAVING_SUCCESS;
			}
		} catch (Exception ex) {
			LOG.error("Exception saving participant register", ex);
		}
	
		return eslResult;
	}	
	
	
	public String patchReports() {

		String patchReportsResult = REPORTS_PATCHING_FAILED;
		
		try {
			DataSet dsSummary = repositoryClient.getDataSetSummary(ds.getDs().getProjectCode(),
					new Date(0), SecurityManager.getInstance().getSAMLAssertion());

			ReportsClient reportsClient = new ReportsClient();
			
			ArrayList<IReport> allReports = new ArrayList<IReport>(reportsClient.getAllReportsByDataSet(dsSummary.getId(), 
					SecurityManager.getInstance().getSAMLAssertion()));
			
			for (IReport r: allReports) {
				reportsClient.deleteReport(dsSummary.getId(), r.getId(), SecurityManager.getInstance().getSAMLAssertion());
			}
			
			// else save the reports again (this will save them with any new groups)
			if ((saveReports(ds.getDs())).equals(REPORTS_SAVING_SUCCESS)){
				return REPORTS_PATCHING_SUCCESS;
			}
			
		} catch (Exception ex) {
			LOG.error("Exception patching reports ", ex);
		}
		
		return patchReportsResult;
		
	}
	/**
	 * Clean up any transient references that are wrongly(!) still lingering about
	 * @param dataset the dataset to clean
	 */
	private void cleanTransients(DataSet dataset) {
		
		//clean entries from docs
		for (int i=0; i<dataset.numDocuments(); i++) {
			Document curDoc = dataset.getDocument(i);
	
			ArrayList<Entry> origEntries = new ArrayList<Entry>();
			for (int j=0; j<curDoc.numEntries(); j++) {
				origEntries.add(dataset.getDocument(i).getEntry(j));
			}
	
			for (int z=0; z<curDoc.numEntries(); z++) {
				Entry curEntry = curDoc.getEntry(z);
	
				if (curEntry instanceof OptionEntry) {
					int numOptions = ((OptionEntry)curEntry).numOptions();
					for (int y=0; y<numOptions; y++) {
						ArrayList<OptionDependent> toRemove = new ArrayList<OptionDependent>();
						Option option = ((OptionEntry)curEntry).getOption(y);
						for (int h=0; h<option.numOptionDependents(); h++) {
							OptionDependent dep = option.getOptionDependent(h);
							if (!(origEntries.contains(dep.getDependentEntry()))) {
								toRemove.add(dep);
							}
						}
	
						//actually remove redundant ones
						for (int n=0; n<toRemove.size(); n++) {
							for (int v=option.numOptionDependents()-1; v>=0; v--) {
								if (toRemove.get(n).equals(option.getOptionDependent(v))) {
									option.removeOptionDependent(v);
								}
							}
						}
					}
				}
	
				if (curEntry instanceof DerivedEntry) {
					DerivedEntry derEntry = (DerivedEntry)curEntry;
					Set<String> varNames = derEntry.getVariableNames();
					for (String varName: varNames) {
						BasicEntry bEntry = (derEntry.getVariable(varName));
	
						//if entries not contained, remove and set the formula to empty
						if (!(origEntries.contains(bEntry))) {
							derEntry.removeVariable(varName);
							derEntry.setFormula("");
						}
					}
				}
	
			}
		}
	
		//clean up old state transitions
		ArrayList<Status> allStatuses = new ArrayList<Status>(((DataSet)dataset).getStatuses());
		for (int f=0; f<allStatuses.size(); f++) {
			Status curStatus = allStatuses.get(f);
			for (int j=curStatus.numStatusTransitions()-1; j>=0; j--) {
				if (!allStatuses.contains(curStatus.getStatusTransition(j))) {
					curStatus.removeStatusTransition(j);
				}
			}
		}
	
		//clean statuses from document groups
		ArrayList<Status> statuses = new ArrayList<Status>(((DataSet)dataset).getStatuses());
		for (int i=0; i<dataset.numDocumentGroups(); i++) {
			DocumentGroup docGroup = dataset.getDocumentGroup(i);
	
			Status updateStatus = docGroup.getUpdateStatus();
			if (!statuses.contains(updateStatus)) {
				docGroup.setUpdateStatus(null);
			}
	
			ArrayList<Status> allowedStatuses = new ArrayList<Status>(docGroup.getAllowedRecordStatus());
			ArrayList<Status> newAllowedStatuses = new ArrayList<Status>();
	
			for (int j=0; j<allowedStatuses.size(); j++) {
				if (statuses.contains(allowedStatuses.get(j))){
					newAllowedStatuses.add(allowedStatuses.get(j));
				}
			}
			((DocumentGroup)docGroup).setAllowedRecordStatus(newAllowedStatuses);
		}
		
		// clean document groups from doc occurrences
		for (int t=0; t<dataset.numDocuments(); t++) {
			ArrayList<DocumentGroup> groups = new ArrayList(((DataSet)dataset).getDocumentGroups());
			for (int j=0; j<dataset.numDocuments(); j++) {
				for (int z=0; z<dataset.getDocument(j).numOccurrences(); z++) {
					if (!groups.contains(dataset.getDocument(j).getOccurrence(z).getDocumentGroup())) {
						dataset.getDocument(j).getOccurrence(z).setDocumentGroup(null);
					}
				}
			}
		}
	
		//clean up prerequisite groups
		ArrayList<DocumentGroup> docGroups = new ArrayList(((DataSet)dataset).getDocumentGroups());
		for (int y=0; y<docGroups.size(); y++) {
			DocumentGroup curGroup = docGroups.get(y);
			ArrayList<DocumentGroup> newPreReqs = new ArrayList<DocumentGroup>();
			for (int n=0; n<curGroup.getPrerequisiteGroups().size(); n++) {
				DocumentGroup curPreReqGroup = (DocumentGroup)curGroup.getPrerequisiteGroups().get(n);
				if (docGroups.contains(curPreReqGroup)) {
					newPreReqs.add(curPreReqGroup);
				}
			}
			curGroup.setPrerequisiteGroups(newPreReqs);			
		}
	}
	
	private void cleanIds() {
		
		//clean role ids from the esl
		ESLEmailModel eslEmailModel = ds.getEslModel();
		if (eslEmailModel != null) {
			for (org.psygrid.esl.model.IRole role: eslEmailModel.getRoles()) {
				((org.psygrid.esl.model.hibernate.Role)role).setId(null);
			}
		}
		
		//clean randomisation treatments and strata ids
		RandomisationHolderModel rhm = ds.getRandomHolderModel();
		if (rhm != null) {
			for (Stratum s: rhm.getRandomisationStrata()) {
				s.setId(null);
			}
		}

		//clean the deleted objects from the dataset too
		((DataSet)ds.getDs()).setDeletedObjects(new ArrayList<Persistent>());
		
	}
	
	/**
	 * Get the seed for randomisation
	 * First try randomX; if this fails, use random.org
	 * @return the long of the seed
	 */
	private Long getSeed() {
		Long randomLong = null;
		try {
			randomX randomizer = new randomHotBits();
			randomLong = randomizer.nextLong();
		} catch (Exception ex) {
			LOG.error("Problem connecting to randomX hotbits, trying random.org instead", ex);
			randomLong = RandomGenerator.getInstance().nextLong();
		}

		//if randomLong still hasn't  been set then throw a new runtime exception
		if (randomLong == null) {
			LOG.error("Problem connecting to both randomization services: randomX and random.org");
			throw new RuntimeException("Cannot connect to either random number generators (hotbits or random.org)");
		}
		
		return randomLong;
	}
	
	
	/**
	 * Remove reports 
	 * @param dsId the dataset id of the reports to remove
	 * @return true if removal is successful; false if not
	 */
	private boolean removeReports() {
		try {
			ReportsClient client = new ReportsClient();
			RepositoryClient repClient = new RepositoryClient();
			String saml = SecurityManager.getInstance().getSAMLAssertion();
			
			DataSet dsSummary = repClient.getDataSetSummary(ds.getDs().getProjectCode(),
					new Date(0), saml);

			ArrayList<IReport> allReports = new ArrayList<IReport>(client.getAllReportsByDataSet(dsSummary.getId(), 
					SecurityHelper.getAAQueryClient().getSAMLAssertion().toString()));
			for (int i=0; i<allReports.size(); i++) {
				client.deleteReport(dsSummary.getId(), 
						allReports.get(i).getId(), 
						saml);
			}
			return true;
		} catch (Exception ex) {
			LOG.info("Did not remove custom reports " + ex.getMessage());
		}
		return false;
	}
	
		
	/**
	 * Remove ESL 
	 * @return true if removal is successful; false if not
	 */
	private boolean removeESL() {
		try {
			EslClient client = new EslClient(new URL(PropertiesHelper.getESLLocation()));
			IProject p = client.retrieveProjectByCode(ds.getDs().getProjectCode(), SecurityHelper.getAAQueryClient().getSAMLAssertion().toString());
			client.deleteProject(p.getId().longValue(), 
					p.getProjectCode(), 
					SecurityHelper.getAAQueryClient().getSAMLAssertion().toString());
			return true;
		} catch (Exception ex) {
			LOG.info("Did not remove the ESL" + ex.getMessage());
		}
		return false;
	}
	
	/**
	 * Remove project from the AA 
	 * @return true if removal is successful; false if not
	 */
	private boolean removeFromAA() {
		try {
			//remove project from the AA
			AAManagementClient mc = SecurityHelper.getAAManagementClient();
			AttributeType ats[] = SecurityHelper.getAAQueryClient().getPort().getMyProjects();
			for (AttributeType at: ats){
				if (at.getProject().getIdCode().equals(ds.getDs().getProjectCode())) {
					boolean r = mc.getPort().deleteProjectAndPolicy(new ProjectType[]{at.getProject()});
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			LOG.error("Exception occurred removing the AA " + ex.getMessage());
			return false;
		}
	
		return true;
	
	}
	
	/**
	 * Remove randomisation 
	 * @return true if removal is successful; false if not
	 */
	private boolean removeRandomisation() {
		RandomizationClient client = null;
	
		try {
			client = new RandomizationClient(new URL(PropertiesHelper.getRandomizationLocation()));
			client.deleteRandomization(ds.getDs().getProjectCode(), SecurityHelper.getAAQueryClient().getSAMLAssertion().toString());
		} catch (Exception ex) {
			LOG.info("No randomizer found for " + ds.getDs().getProjectCode() + ", " + ex.getMessage());
		}
	
		return false;
	}
	
	/**
	 * Remove the dataset 
	 * @return true if removal is successful; false if not
	 */
	private boolean removeDataSet() {
		boolean removed = false;
	
		RepositoryClient repositoryClient = null;
	
		try {
			repositoryClient.removePublishedDataSet(savedId, ds.getDs().getProjectCode(), 
					SecurityHelper.getAAQueryClient().getSAMLAssertion().toString());
			((DataSet)ds.getDs()).setPublished(false);
			removed = true;
		} catch (Exception ex) {
			ex.printStackTrace();
			LOG.error("Exception while deleting dataset " + ds.getDs().getProjectCode() + " with exception " + ex.getMessage());
		}
	
		return removed;
	}
	
	/**
	 * Refresh the saml before starting to save
	 */
	private void refreshKey() {
		try {
			SecurityManager.getInstance().refreshKey();
		} catch (Exception ex) {
			LOG.error("Error refershing security key",ex);
		}
	}
	
	/**
	 * Check that project code ane name are unique
	 * @return a string to indicate if an error was detect; returns null on no error
	 */
	private String checkUniqueNameAndCode() {
		try {
			ArrayList<ProjectType> projects = new ArrayList<ProjectType>(SecurityHelper.getAAQueryClient().getMyProjects());
			for (int i=0; i<projects.size(); i++) {
				if (projects.get(i).getName().equals(ds.getDs().getName())){
					return PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.studynameexistsoverwrite");
				}
	
				if (projects.get(i).getIdCode().equals(ds.getDs().getProjectCode())) {
					return PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.studycodeexistsoverwrite");
				}
			}
		} catch (Exception ex) {
			LOG.error("Exception occurred checking the unique name and code of the study", ex);
			ex.printStackTrace();
			return PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.studycodeexistsoverwrite");
		}
		
		return "";
	}
	
	/**
	 * Save to the Security AA
	 * Try to save to the security aa, if error encountered, show dialog
	 * if successful append the success string with the success message
	 * @return true if save is sucessful, false if not
	 */
	private String saveToAA() {
		
		//initialise to failed, change if successful
		String aasaving = AA_SAVING_FAILED;
		
		//	if alias code is set, use it; otherwise use the project code
		String code = ds.getUkcrnCode();
		if (code == null) {
			code = ds.getDs().getProjectCode();
		}
		
		project = new ProjectType(ds.getDs().getName(), 
			ds.getDs().getProjectCode(), 
			ds.getDs().getName(), 
			code, false);
	
		AAManagementClient mc = SecurityHelper.getAAManagementClient();
		ProjectDescriptionType pdt = new ProjectDescriptionType(project, getGroups(), getRoles());
		ProjectDescriptionType[] pdta = new ProjectDescriptionType[]{pdt};
		
		try {
			boolean result = mc.getPort().addProject(pdta);
			if (result) {
				//first string so do not append
//				successString = "Project saved \n";
				aasaving =  AA_SAVING_SUCCESS;
			} else {
				aasaving =  AA_SAVING_FAILED;
				//WrappedJOptionPane.showMessageDialog(frame, PropertiesHelper.getStringFor("org.psygridatasetdesigner.utils.datasetsaver.projectsavingfailed"), PropertiesHelper.getStringFor("org.psygridatasetdesigner.utils.datasetsaver.datasetsaving"), JOptionPane.ERROR_MESSAGE);
			}
		} catch (Exception ex) {
			LOG.error("Project saving failed" , ex);
			aasaving = AA_SAVING_FAILED;
		}
		
		return aasaving;
		
		//return r;
	}
	
	/**
	 * Save to the Security PA
	 * Try to save to the security PA, if error encountered, show dialog
	 * if successful append the success string with the success message
	 * @return true if save is sucessful, false if not
	 */
	private String saveToPA() {
		String paResult = PA_SAVING_FAILED;
		//first install the policy
		if ((installProjectPolicy(ds.getDs().getName(), ds.getDs().getProjectCode()))) {
			paResult = PA_SAVING_SUCCESS;
		} 
		
		return paResult;
	}
	
	/**
	 * Save the dataset to the repository
	 * Try to save to the repository, if error encountered, show dialog;
	 * if successful append the success string with the success message
	 * @return true if save is sucessful, false if not
	 */
	private String saveToRepository(boolean publish) {
		String repResult = REPOSITORY_SAVING_FAILED;
		
    	try {
    		DataSet newCleanDataSet = null;
    		repositoryClient = new RepositoryClient();
    		String saml = SecurityHelper.getAAQueryClient().getSAMLAssertion().toString();
    		//clean up all the old references
    		DataSet dsHibernate = ((DataSet)ds.getDs());
    		
    		//in case of saving across different systems, deleted datasets etc. 
    		//hibernate info is not correct; so check here if the dataset exists
    		//in the repository; if not prepareElementForNewRevision
    		try {
        		repositoryClient.getDataSetSummary(ds.getDs().getProjectCode(), new Date(0), saml);
        		//if it doesn't exist, prepare el for new revision
        		//if it doesn't exist; clear id's
    		} catch (org.psygrid.data.repository.RepositoryNoSuchDatasetFault nsdf) {
    			dsHibernate.setPrepareElementForNewRevision(true);
    		}
    		
    		newCleanDataSet = dsHibernate.toDTO().toHibernate();
    		newCleanDataSet.setVersionNo("1.0.0");
    		((DataSet)newCleanDataSet).setPublished(false);
    		
    		savedId = repositoryClient.saveDataSet(newCleanDataSet, saml);
    		
    		if (publish) {
    			repositoryClient.publishDataSet(savedId, saml);
    			((DataSet)ds.getDs()).setPublished(true);
    		}
    		
    		dsHibernate.setPrepareElementForNewRevision(false);
    		
    		ds.setDirty(false);
    		//refresh copy in memory with latest from repository to keep refs up to date
    		ds.setDs((DataSet)repositoryClient.getDataSet(savedId, saml));
    		    		
    		repResult = REPOSITORY_SAVING_SUCCESS;
    	} catch (RepositoryOutOfDateFault roof) {
    		repResult += "Saving failed because you are using an outdated xml file. \n ";
    		LOG.error("Exception saving the dataset to the repository", roof);
    	} catch (NotAuthorisedFault naf) { 
    		repResult += "Saving failed because you are not authorised to save to the datbase. \n ";
    		LOG.error("Exception saving the dataset to the repository", naf);
    	} catch (Exception ex) {
    		LOG.error("Exception saving the dataset to the repository", ex);
    	}
    	
    	return repResult;
	}

	/**
	 * Patch the dataset to the repository
	 * Try to patch the dataset, if error encountered, show dialog;
	 * if successful append the success string with the success message
	 * @return true if save is sucessful, false if not
	 */
	private String patchToRepository() {
		String repResult = REPOSITORY_PATCHING_FAILED;
		try {
    		DataSet newCleanDataSet = null;
    		repositoryClient = new RepositoryClient();
    		String saml = SecurityHelper.getAAQueryClient().getSAMLAssertion().toString();
    		//clean up all the old references
    		DataSet dsHibernate = ((DataSet)ds.getDs());
    		
    		//in case of saving across different systems, deleted datasets etc. 
    		//hibernate info is not correct; so check here if the dataset exists
    		//in the repository; if not prepareElementForNewRevision
    		try {
        		repositoryClient.getDataSetSummary(ds.getDs().getProjectCode(), new Date(0), saml);
        	//if the study doesn't exist, we cannot patch it so exit here!
    		} catch (org.psygrid.data.repository.RepositoryNoSuchDatasetFault nsdf) {
    			return repResult;
    		}
    		
    		newCleanDataSet = dsHibernate.toDTO().toHibernate();
    		//TODO: need to figure out the versioning here!
    		
    		savedId = repositoryClient.patchDataSet(newCleanDataSet, saml);
    		
//    		dsHibernate.setPrepareElementForNewRevision(false);
    		
    		ds.setDirty(false);
    		//refresh copy in memory with latest from repository to keep refs up to date
    		ds.setDs((DataSet)repositoryClient.getDataSet(savedId, saml));
    		    		
    		repResult = REPOSITORY_PATCHING_SUCCESS;
    	} catch (RepositoryOutOfDateFault roof) {
    		repResult += "Patching failed because you are using an outdated xml file. \n ";
    		LOG.error("Exception saving the dataset to the repository", roof);
    	} catch (NotAuthorisedFault naf) { 
    		repResult += "Patching failed because you are not authorised to patch to the datbase. \n ";
    		LOG.error("Exception saving the dataset to the repository", naf);
    	} catch (Exception ex) {
    		LOG.error("Exception saving the dataset to the repository", ex);
    	}
    	
    	return repResult;
	}
	
	
	/**
	 * Check if tabs are open and if so, close them
	 * @return true if no open tabs or tabs were closed successfully; false if permission
	 * was not granted to close open tabs
	 */
	private boolean closeOpenTabs() {
		if (frame.getDocPane().getTabCount() > 0) {
			int n = JOptionPane.showConfirmDialog (
					frame, PropertiesHelper.getStringFor(STRING_PREFIX + "docwinsopen"),
					PropertiesHelper.getStringFor(STRING_PREFIX + "docclosepresaving"),
					JOptionPane.YES_NO_OPTION);
			//if no is s
			if (n == JOptionPane.NO_OPTION) {
				return false;
			} 
			//close all open tabs first
			frame.getDocPane().closeAll();
		}
		return true;
	}
	
	/**
	 * Prepare the dataset before saving. This involves
	 * setting the display text of the dataset
	 * cleaning any transient entries
	 * checking that at least one group is assigned
	 * checking that project code and name are unique (if not, prompt to overwrite)
	 * verifying that all documents have at least one document occurrence assigned
	 * @return true if dataset is prepared successfully; false if error encountered
	 * (no doc occs configured for >0 documents) or user does not want to overwrite 
	 * existing dataset, or groups <=0
	 */
	private boolean prepareDataset() {
		//set the display text for the dataset
		ds.getDs().setDisplayText(ds.getDs().getName());
	
		//clean any transient objects
		cleanTransients(ds.getDs());
		
		//clean esl and randomization hibernate ids (these should always be
		//cleared before resaving)
		cleanIds();
		
		//check that at least one group is configured
		if (getGroups().length == 0) {
			WrappedJOptionPane.showMessageDialog(frame, PropertiesHelper.getStringFor("org.psygridatasetdesigner.utils.datasetsaver.configureatleastonegroup"));
			return false;
		}
		
		//verify that all documents have at least one occurrence
		//and set document actions and statuses (if none set)
		String message = ds.cleanAndCheckDataset();
		if ( null != message ){
			WrappedJOptionPane.showMessageDialog(frame, message);
			return false;
		}
		
		return true;
	}
	
	private void configureEslStrataValues(org.psygrid.esl.model.hibernate.Strata s) {
		if (s.getName().equals("sex")) {
			ArrayList<String> sexValues = new ArrayList<String>(); 
			sexValues.add("Male");
			sexValues.add("Female");
			s.setValues(sexValues);
		}
		else if (s.getName().equals("centreNumber")) {
			ArrayList<String> centreValues = new ArrayList<String>();
			for ( GroupType g: getGroups() ) {
				centreValues.add(g.getIdCode());
			}
			s.setValues(centreValues);
	
		}
		else{
			for ( EslCustomField field: ds.getEslCustomFields()){
				ArrayList<String> customFields = new ArrayList<String>();
				if ( s.getName().equals(field.getName()) ){
					for ( int i=0, c=field.getValueCount(); i<c; i++ ){
						customFields.add(field.getValue(i));
					}
				}
				s.setValues(customFields);
			}
		}
	}

	
	private void configureStrataValues(Stratum s) {
		if (s.getName().equals("sex")) {
			ArrayList<String> sexValues = new ArrayList<String>(); 
			sexValues.add("Male");
			sexValues.add("Female");
			s.setValues(sexValues);
		}
		else if (s.getName().equals("centreNumber")) {
			ArrayList<String> centreValues = new ArrayList<String>();
			for ( GroupType g: getGroups() ) {
				centreValues.add(g.getIdCode());
			}
			s.setValues(centreValues);

		}
		else{
			for ( EslCustomField field: ds.getEslCustomFields()){
				ArrayList<String> customFields = new ArrayList<String>();
				if ( s.getName().equals(field.getName()) ){
					for ( int i=0, c=field.getValueCount(); i<c; i++ ){
						customFields.add(field.getValue(i));
					}
				}
				s.setValues(customFields);
			}
		}
	}
	
	/**
	 * A call that just updates the policy.
	 * It does not call any other saving methods
	 * on the dataset itself. 
	 * @return true if save is successful; false if not
	 */
	public boolean updatePolicy() {
		//	irst autosave
		LocalFileUtility.autosave();
		
		//start progress indication
		frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		frame.setStarted();
		
		SwingWorker worker = 
	          new SwingWorker<String, Void>() {
	          
			public String doInBackground() {
				return saveToPA();
	        }
	          
        	  public void done() {
        		  try {
            		  if (get().equals(PA_SAVING_SUCCESS)) {
            			  WrappedJOptionPane.showMessageDialog(frame, PropertiesHelper.getStringFor(STRING_PREFIX + "updatepolicysucceeded"));
            		  } else {
            			  WrappedJOptionPane.showMessageDialog(frame, PropertiesHelper.getStringFor(STRING_PREFIX + "updatepolicyfailed"));
            		  }
        		  } catch (Exception ex) {
        			  LOG.error("Error updating policy", ex);
        		  }
        		  frame.setFinished();
        		  frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        		  return;
        	  }
		};
		
		worker.execute();
		
		return true;
	}
	
	
}
