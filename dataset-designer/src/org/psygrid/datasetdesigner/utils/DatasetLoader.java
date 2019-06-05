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

import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.collection.entry.security.SecurityHelper;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.reporting.client.ReportsClient;
import org.psygrid.data.reporting.definition.IReport;
import org.psygrid.data.repository.RepositoryNoSuchDatasetFault;
import org.psygrid.data.repository.RepositoryServiceFault;
import org.psygrid.data.repository.client.RepositoryClient;
import org.psygrid.data.utils.security.NotAuthorisedFault;
import org.psygrid.datasetdesigner.model.StudyDataSet;
import org.psygrid.datasetdesigner.model.ESLEmailModel;
import org.psygrid.datasetdesigner.model.GroupModel;
import org.psygrid.datasetdesigner.model.RandomisationHolderModel;
import org.psygrid.datasetdesigner.model.TreatmentHolderModel;
import org.psygrid.datasetdesigner.controllers.RecentStudiesController;
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
import org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.ProjectType;
import org.psygrid.www.xml.security.core.types.RoleType;

/**
 * @author pwhelan
 *
 */
public class DatasetLoader extends DsLoader{
	
	/**
	 * The logger for this class
	 */
	private static final Log LOG = LogFactory.getLog(DatasetLoader.class);
	
	public static StudyDataSet loadDatasetFromProject(ProjectType pt) {
		StudyDataSet dsSet = new StudyDataSet();
		
		try {
			String saml = SecurityHelper.getAAQueryClient().getSAMLAssertion().toString();
			//assign the remote dataset
			initRemoteDataset(dsSet, pt.getIdCode(), saml);

			//configure the roles for this dataset
			initRemoteRoles(dsSet, pt);
						
			//configure the remotely assigned groups
			initRemoteGroups(dsSet, pt);
			
			initRemoteReports(dsSet, saml);
		
			
			if (dsSet.getDs().isRandomizationRequired()) {
				initRemoteRandomization(dsSet, saml);
			}
			
			
			//set the UKCRN code
			dsSet.setUkcrnCode(pt.getAliasId());
			
			//ensure dirty flag is set to false
			dsSet.setDirty(false);
			
			DsLoader.setCanPatchDataset(dsSet.getDs());
			
			//if we get to here then the file has been successfully loaded
			//so we add it to the list of recent studies
			RecentStudiesController.getInstance().addStudy(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.repkey") + dsSet.getDs().getName() + "(" + dsSet.getDs().getProjectCode() + ")");
		} catch (NotAuthorisedFault naf) {
			dsSet = null;
			LOG.error("Not authorised to load dataset", naf);
		} catch (Exception e) {
			//set the dsSet to null
			dsSet = null;
			LOG.error("Problem occurred loading the remote dataset ", e);
		}
		
		
		return dsSet;
	}
	
	/**
	 * Check if current dataset exists locally
	 * @param repClient the repository client to query
	 * @param dsSet the DSDataSet which needs a remote dataset
	 * @param projectCode project code of the dataset to fetch
	 */
	private static void initRemoteDataset(StudyDataSet dsSet, 
			String projectCode,
			String saml)
	throws RepositoryServiceFault,
	RepositoryNoSuchDatasetFault,
	NotAuthorisedFault,
	ConnectException,
	SocketTimeoutException {
		RepositoryClient repClient = new RepositoryClient();
		
		//if project code is = -1 then it's the system project so don't try to load it here
		if (!projectCode.equals("-1")) {
			DataSet dsSummary = repClient.getDataSetSummary(projectCode, new Date(0), saml);
			dsSet.setDs(repClient.getDataSet(dsSummary.getId(), saml));
		}
		
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
					ArrayList<Treatment> treatments = new ArrayList<Treatment>((((BlockRandomizer)sCombs.get(0).getRandomizer()).getTreatments()));
					int minBlockSize = sCombs.get(0).getRandomizer().getMinBlockSize() * treatments.size();
					int maxBlockSize = sCombs.get(0).getRandomizer().getMaxBlockSize() * treatments.size();
					randomModel.setMinimumBlockSize(minBlockSize);
					randomModel.setMaximumBlockSize(maxBlockSize);
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
			ESLEmailModel emailModel = new  ESLEmailModel();			
			emailModel.setRoles(eslRoles);
			dsSet.setEslModel(emailModel);
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
		} catch (NullPointerException npe) {
			LOG.error("Error loading randomization" + npe.getMessage());
		}

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
	

}
