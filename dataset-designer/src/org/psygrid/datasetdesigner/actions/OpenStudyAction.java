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

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingworker.SwingWorker;
import org.psygrid.common.ui.WrappedJOptionPane;
import org.psygrid.data.model.hibernate.ModelException;
import org.psygrid.datasetdesigner.controllers.DatasetController;
import org.psygrid.datasetdesigner.controllers.TempFileController;
import org.psygrid.datasetdesigner.model.DELStudySet;
import org.psygrid.datasetdesigner.model.DocTreeModel;
import org.psygrid.datasetdesigner.model.StudyDataSet;
import org.psygrid.datasetdesigner.ui.chooser.StudyChooser;
import org.psygrid.datasetdesigner.ui.MainFrame;

import org.psygrid.datasetdesigner.utils.LocalFileUtility;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.utils.DatasetLoader;
import org.psygrid.datasetdesigner.utils.DatasetUtility;

import org.psygrid.www.xml.security.core.types.ProjectType;

/**
 * Action to called to open a study from either a local file, 
 * network file or the repository.  Checks to see if there is a study
 * already open and if so, prompts to close this first.
 *  
 * @author pwhelan
 */
public class OpenStudyAction extends AbstractAction {

	/**
	 * Logger for this class
	 */
	private static final Log LOG = LogFactory.getLog(OpenStudyAction.class);
	
	/**
	 * Main window of the application
	 */
	private MainFrame mainFrame;
	
	/**
	 * Open a new study window
	 *
	 */
	public OpenStudyAction(MainFrame mainFrame) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.openstudy"));
		this.mainFrame = mainFrame;
	}
	
	public void showChooser() {
		StudyChooser sc = new StudyChooser(mainFrame);
		int returnVal = sc.showOpenDialog(mainFrame);
		
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			//if there's still an active dataset at this point, then close it!
			//and remove any temporary file that might exists
			if (DatasetController.getInstance().getActiveDs() != null) {
					DatasetUtility.closeDataset(mainFrame);
					TempFileController.getInstance().deleteTempFile();
			}
			if (sc.getMode() == StudyChooser.CHOOSER_VIEW) {
				//save the file
				boolean loaded = false;
				
				try{
					loaded = LocalFileUtility.loadFile(sc.getSelectedFile());
				}catch (ModelException e){
					//Put up a message dialog stating why the document can't be loaded and to contact support.
					String message = "Could not open file. Error is: '" + e.getMessage() + "' Please contact support";
					String title = "Study File Corruption";
					LOG.error(message);
					WrappedJOptionPane.showWrappedMessageDialog(mainFrame, message, title, JOptionPane.ERROR_MESSAGE);
				}
				if (loaded) {
					//update the last stored location
					//ensure the extension is correct (.xml)
					StringBuffer scFilePath = new StringBuffer(sc.getSelectedFile().getAbsolutePath());
					if (scFilePath.indexOf(".") == -1){
						scFilePath.append(".xml");
					}
					
					mainFrame.setCanPatchDataSet(LocalFileUtility.isCanPatchDataset());
					
					if (DatasetController.getInstance().getActiveDs().isReadOnly()) {
						WrappedJOptionPane.showMessageDialog(mainFrame, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.readonly"));
					}
					
					DatasetController.getInstance().getActiveDs().setLastStoredLocation(scFilePath.toString());
					mainFrame.setStatusBarText(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.studyopened") + scFilePath.toString());
				}
			} else {
				final ProjectType sp = sc.getSelectedProject();
				
				if (sp != null) {
					mainFrame.setStatusBarText("Loading study from database...");
					//start progress indication
					mainFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					mainFrame.setStarted();
					
					SwingWorker worker = 
				          new SwingWorker<StudyDataSet, Void>() {
				          	public StudyDataSet doInBackground() {					
				          		//load the remote dataset; if a publisheddataset (new name and new code are passed here too)
				          		StudyDataSet dsSet = DatasetLoader.loadDatasetFromProject(sp);
				          		mainFrame.setCanPatchDataSet(DatasetLoader.isCanPatchDataset());
				          		return dsSet;
				          	}
				          	
				          	public void done() {
				          		try {
					          		StudyDataSet dsSet  = get();
							          
									if (dsSet == null) {
										//loading failed
										mainFrame.setStatusBarText(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.repstudyopenfailed"));
									} else {
										if (dsSet != null ) {
											//set the last stored location to be the repository; special key to indicate dataset is from rep
											dsSet.setLastStoredLocation(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.repkey"));
											DocTreeModel.getInstance().addDataset(dsSet);
										}
										
										//use the name from the dataset rather than the project in case it's changed because of opening a published study
										mainFrame.setStatusBarText(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.repstudyopened") + dsSet.getDs().getName());
									}
				          		} catch (Exception ex) {
									//assume loading failed
									mainFrame.setStatusBarText(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.repstudyopenfailed"));
				          		}
								
								//update the progress
								mainFrame.setFinished();
					        	mainFrame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				          	}
					};
					
					worker.execute();
					
				
				}
			}
		}
		
		mainFrame.getMainMenuBar().setDelContext(false);

	}
	
	/**
	 * Show the study chooser
	 * @param e the calling event
	 */
	public void actionPerformed(ActionEvent e) {
		if (DatasetController.getInstance().getActiveDs() != null) {
			if (DatasetController.getInstance().getActiveDs() instanceof DELStudySet) {
				int returnValue = JOptionPane.showConfirmDialog(mainFrame, "You are exiting the Library view.  Are you sure you want to do this?", "Exiting Library",  JOptionPane.YES_NO_OPTION);
					if (returnValue == JOptionPane.NO_OPTION) {
						return;
					} else {
						LocalFileUtility.delsave();
						DocTreeModel.getInstance().removeDSDataset(DatasetController.getInstance().getActiveDs());
						mainFrame.getMainMenuBar().setDelContext(false);
						showChooser();
					}
			} else {
				//only prompt if it's changed
				if (DatasetController.getInstance().getActiveDs().isDirty()) {
					int returnVal = WrappedJOptionPane.showConfirmDialog(mainFrame, 
							PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.actions.unsavedchangessave"), "Unsaved changes", JOptionPane.YES_NO_CANCEL_OPTION);
					if (returnVal == JOptionPane.CANCEL_OPTION) {
						//do nothing
					} else if (returnVal == JOptionPane.YES_OPTION) {
						String lastStoredLocation = DatasetController.getInstance().getActiveDs().getLastStoredLocation(); 
						//if last stored location set, save dataset and then close it
						if (lastStoredLocation != null) {
							int saveReturnVal = DatasetUtility.saveDataset(mainFrame);
							if (saveReturnVal == JFileChooser.APPROVE_OPTION) {
								DatasetUtility.closeDataset(mainFrame);
								showChooser();
							} else {
								if (DatasetUtility.saveAsDataset(mainFrame) == JFileChooser.APPROVE_OPTION) {
									DatasetUtility.closeDataset(mainFrame);
									showChooser();
								}
							}
							//if no last stored location set, then save as... dataset and close it
						} else {
							if (DatasetUtility.saveAsDataset(mainFrame) == JFileChooser.APPROVE_OPTION) {
								DatasetUtility.closeDataset(mainFrame);
								showChooser();
							}
						}
					} else if (returnVal == JOptionPane.NO_OPTION){
						showChooser();
					}
					//if it's not dirty, show the chooser directly
				} else {
					showChooser();
				}
			}
		//if none open, just show the chooser
		} else {
			showChooser();
		}
	}
		
}
