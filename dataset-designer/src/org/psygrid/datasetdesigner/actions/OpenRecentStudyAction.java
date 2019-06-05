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

import java.io.File;
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
import org.psygrid.datasetdesigner.model.DELStudySet;
import org.psygrid.datasetdesigner.model.DocTreeModel;
import org.psygrid.datasetdesigner.model.StudyDataSet;
import org.psygrid.datasetdesigner.utils.DatasetUtility;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.utils.DatasetLoader;
import org.psygrid.datasetdesigner.utils.LocalFileUtility;

import org.psygrid.www.xml.security.core.types.ProjectType;

import org.psygrid.datasetdesigner.ui.MainFrame;

public class OpenRecentStudyAction extends AbstractAction {
	
	/**
	 * Logger for this class
	 */
	private static final Log LOG = LogFactory.getLog(OpenRecentStudyAction.class);
	

	private String fileToOpen;
	
	private MainFrame mainFrame;
	
	/**
	 * Open the recent study with this file name 
	 * @param fileToOpen the file to open
	 * @param index index of the file in the recent studies list
	 * used to show a number in the clickable menu item
	 */
	public OpenRecentStudyAction(MainFrame frame, String fileToOpen, int index, String formattedName) {
		super(new Integer(index).toString() + " " + formattedName);
		this.fileToOpen = fileToOpen;
		this.mainFrame = frame;
	}
	
	
	
	public void openFile() {
		if (fileToOpen.contains(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.repkey"))) {
			StringBuffer buffer = new StringBuffer(fileToOpen);
			buffer.delete(0, buffer.lastIndexOf("*")+1);
			int firstBracket = buffer.indexOf("(");
			int endBracket = buffer.indexOf(")");
			CharSequence firstSequence = buffer.subSequence(0, firstBracket);
			String firstBit = new StringBuffer(firstSequence).toString();
			String lastBit = new StringBuffer(buffer.subSequence(firstBracket + 1, endBracket)).toString();
			final ProjectType pt = new ProjectType(firstBit, lastBit, null, null, false);
			
			mainFrame.setStatusBarText("Loading study from database...");
			//start progress indication
			mainFrame.setStarted();
			
			SwingWorker worker = 
		          new SwingWorker<StudyDataSet, Void>() {			
					public StudyDataSet doInBackground() {					
						//load the remote dataset; if a publisheddataset (new name and new code are passed here too)
						StudyDataSet dsSet = DatasetLoader.loadDatasetFromProject(pt);
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
								//set the last stored location to be the repository; special key to indicate dataset is from rep
								dsSet.setLastStoredLocation(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.repkey"));
								DocTreeModel.getInstance().addDataset(dsSet);
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
				
		} else {
			//save the file
			boolean loaded = false;
			
			try{
				loaded = LocalFileUtility.loadFile(new File(fileToOpen));
			}catch (ModelException e){
				//Put a dialog message out that the study can't be loaded and that the user should contact support.
				String message = "Could not open file. Error is: '" + e.getMessage() + "' Please contact support";
				String title = "Study File Corruption";
				LOG.error(message);
				WrappedJOptionPane.showWrappedMessageDialog(mainFrame, message, title, JOptionPane.ERROR_MESSAGE);
			}
			
			
			if (loaded) {
				//update the last stored location
				//ensure the extension is correct (.xml)
				StringBuffer scFilePath = new StringBuffer(fileToOpen);
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
		}
	}

	public void actionPerformed(ActionEvent aet) {
		if (DatasetController.getInstance().getActiveDs() != null) {
			if (DatasetController.getInstance().getActiveDs() instanceof DELStudySet) {
				int returnValue = JOptionPane.showConfirmDialog(mainFrame, "You are exiting the Library view.  Are you sure you want to do this?", "Exiting Library",  JOptionPane.YES_NO_OPTION);
					if (returnValue == JOptionPane.NO_OPTION) {
						return;
					} else {
						LocalFileUtility.delsave();
						DocTreeModel.getInstance().removeDSDataset(DatasetController.getInstance().getActiveDs());
						mainFrame.getMainMenuBar().setDelContext(false);
						openFile();
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
								openFile();
							} else {
								if (DatasetUtility.saveAsDataset(mainFrame) == JFileChooser.APPROVE_OPTION) {
									DatasetUtility.closeDataset(mainFrame);
									openFile();
								}
							}
							//if no last stored location set, then save as... dataset and close it
						} else {
							if (DatasetUtility.saveAsDataset(mainFrame) == JFileChooser.APPROVE_OPTION) {
								DatasetUtility.closeDataset(mainFrame);
								openFile();
							}
						}
					} else if (returnVal == JOptionPane.NO_OPTION){
						DatasetUtility.closeDataset(mainFrame);
						openFile();
					}
					//if it's not dirty, then open the file directly
				} else {
					DatasetUtility.closeDataset(mainFrame);
					openFile();
				}
			}
		//if active set is null, just open the file
		}else {
			openFile();
		}
	} 
}
