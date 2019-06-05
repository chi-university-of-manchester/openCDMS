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

package org.psygrid.datasetdesigner.actions;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingworker.SwingWorker;
import org.psygrid.collection.entry.security.SecurityHelper;
import org.psygrid.collection.entry.security.SecurityManager;
import org.psygrid.common.ui.WrappedJOptionPane;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.reporting.client.ReportsClient;
import org.psygrid.data.reporting.definition.IReport;
import org.psygrid.data.repository.client.RepositoryClient;
import org.psygrid.datasetdesigner.controllers.DatasetController;
import org.psygrid.datasetdesigner.model.StudyDataSet;
import org.psygrid.datasetdesigner.ui.MainFrame;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.esl.model.IProject;
import org.psygrid.esl.services.client.EslClient;
import org.psygrid.randomization.client.RandomizationClient;
import org.psygrid.security.attributeauthority.client.AAManagementClient;
import org.psygrid.www.xml.security.core.types.AttributeType;
import org.psygrid.www.xml.security.core.types.ProjectType;


/**
 * Action that deletes a group from the project for the specified user.
 * @author pwhelan
 */
public class DeleteSavedDatasetAction extends AbstractAction {

	private MainFrame frame;
	
	private static final Log LOG = LogFactory.getLog(DeleteSavedDatasetAction.class);

	public DeleteSavedDatasetAction(MainFrame frame) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.deletesaveddataset"));
		this.frame = frame;
	}

	public void actionPerformed(ActionEvent aet) {
		final StudyDataSet dataset = DatasetController.getInstance().getActiveDs();
		int n = JOptionPane.showConfirmDialog (
				frame, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.suretodelete"),
				PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.confirmdelete"),
				JOptionPane.YES_NO_OPTION);
		if (n == JOptionPane.YES_OPTION) {
			refreshSecurityKey();
			//start progress indication
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			frame.setStarted();

			SwingWorker<Object, Object> worker = new SwingWorker<Object, Object>() {
					public Object doInBackground() {
						try {
						String dsCode = dataset.getDs().getProjectCode();;

						RepositoryClient client = new RepositoryClient();
						DataSet dsSummary = client.getDataSetSummary(dsCode,
                                new  Date(0), SecurityHelper.getAAQueryClient().getSAMLAssertion().toString());

						//ensure that the repository copy has definitely NOT
						//been published
						if (dsSummary.isPublished() ) {
							WrappedJOptionPane.showMessageDialog(frame, PropertiesHelper.getStringFor("org.psygridatasetdesigner.actions.deletesaveddataset.nodeletepublished"));
							return null;
						}
						
						//first remove the reports (dataset must in repository for this to succeed)
						removeReports(dsSummary.getId());

						//then remove the ESL - try to remove anyway (the setting may have changed)
						removeESL(dsCode);

						//then remove the randomisation  - try to remove anyway
						removeRandomisation(dsCode);
						
						//remove the dataset
						client.removeDataSet(dsSummary.getId(), dsCode, 
								SecurityHelper.getAAQueryClient().getSAMLAssertion().toString());
						((DataSet)dataset.getDs()).setPublished(false);

						//remove from AA
						removeFromAA(dsCode);

						frame.setFinished();
						frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					} catch (Exception ex) {
						LOG.error("Exception occurred removing the study." + ex.getMessage());
						JOptionPane.showMessageDialog(frame, "An error occurred removing the study.");
						return null;
					}
					JOptionPane.showMessageDialog(frame, "Study was removed.");
					return null;
				} 

				public void done() {
						frame.setFinished();
						frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					}
				};
				worker.execute();
		}
	}




	private boolean removeReports(long dsId) {
		try {
			ReportsClient client = new ReportsClient();
			ArrayList<IReport> allReports = new ArrayList<IReport>(client.getAllReportsByDataSet(dsId, 
					SecurityHelper.getAAQueryClient().getSAMLAssertion().toString()));
			for (int i=0; i<allReports.size(); i++) {
				client.deleteReport(dsId, 
						allReports.get(i).getId(), 
						SecurityHelper.getAAQueryClient().getSAMLAssertion().toString());
			}
			return true;
		} catch (Exception ex) {
			LOG.error("Error removing reports " + ex.getMessage());
			ex.printStackTrace();
		}
		return false;
	}


	private boolean removeESL(String dsCode) {
		try {
			EslClient client = new EslClient(new URL(PropertiesHelper.getESLLocation()));
			IProject p = client.retrieveProjectByCode(dsCode, SecurityHelper.getAAQueryClient().getSAMLAssertion().toString());
			client.deleteProject(p.getId().longValue(), 
					p.getProjectCode(), 
					SecurityHelper.getAAQueryClient().getSAMLAssertion().toString());
			return true;
		} catch (Exception ex) {
			LOG.error("Exception occurred removing the ESL " + ex.getMessage());
			ex.printStackTrace();
		}
		return false;
	}

	private boolean removeFromAA(String dsCode) {

		try {
			//remove project from the AA
			AAManagementClient mc = SecurityHelper.getAAManagementClient();
			AttributeType ats[] = SecurityHelper.getAAQueryClient().getPort().getMyProjects();
			for (AttributeType at: ats){
				if (at.getProject().getIdCode().equals(dsCode)) {
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

	private boolean removeRandomisation(String dsCode) {
		RandomizationClient client = null;

		try {
			client = new RandomizationClient(new URL(PropertiesHelper.getRandomizationLocation()));
			client.deleteRandomization(dsCode, SecurityHelper.getAAQueryClient().getSAMLAssertion().toString());
		} catch (Exception ex) {
			ex.printStackTrace();
			LOG.error("Exception while deleting randomizer " + dsCode + " with exception " + ex.getMessage());
		}

		return false;
	}

	private void refreshSecurityKey() {
		try {
			SecurityManager.getInstance().refreshKey();
		} catch (Exception ex) {
			LOG.error("Error refershing security key",ex);
		}
	}

}
