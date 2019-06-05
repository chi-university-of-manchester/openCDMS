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

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.psygrid.common.ui.WrappedJOptionPane;
import org.psygrid.datasetdesigner.controllers.DatasetController;
import org.psygrid.datasetdesigner.controllers.TempFileController;
import org.psygrid.datasetdesigner.model.DocTreeModel;
import org.psygrid.datasetdesigner.model.StudyDataSet;
import org.psygrid.datasetdesigner.ui.MainFrame;
import org.psygrid.datasetdesigner.ui.chooser.StudyChooser;

/**
 * Utility class for saving datsets, closing datasets and save as...
 * Calls the local save or repository save as necessary and updates the
 * status bar in the main window accordingly
 * 
 * @author pwhelan
 */
public class DatasetUtility {
	
	public static int saveDatasetAndExit(MainFrame mainFrame) {
		StudyDataSet activeSet = DatasetController.getInstance().getActiveDs();
		
		//if no location is set, then tell user to use the save as option
		//we do not know where to save to!
		//this should not happen...but just in case!
		if (activeSet.getLastStoredLocation() == null) {
			WrappedJOptionPane.showMessageDialog(mainFrame, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.usesaveas"), PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.usesaveastitle"), WrappedJOptionPane.WARNING_MESSAGE);
			return JFileChooser.CANCEL_OPTION;
		}
		
        if (activeSet.getLastStoredLocation().equals(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.repkey"))) {
        	//it's a repository file; so we need to save it there!
        	
        	//but only if it's fully configured
        	if (activeSet != null && 
        			activeSet.isFullyConfigured() && 
        			!activeSet.getDs().isPublished()) {
            	DatasetSaver dsSaver = new DatasetSaver(mainFrame, activeSet, false);
            	
            	//save dataset updates the status messages in the main frame on completion
            	//TODO : if saving fails, show the user the save as dialog
            	if (dsSaver.saveDataset(true)) {
            		return JFileChooser.APPROVE_OPTION;
            	} else {
            		return DatasetUtility.saveAsDatasetAndExit(mainFrame);
            	}
            //trying to save a dataset that is not fully configured or has been published
        	} else {
        		return DatasetUtility.saveAsDatasetAndExit(mainFrame);
        	}
        } else {
        	//save it to the file destination and update the status bar
        	if (LocalFileUtility.saveActiveDsFile()) {
    			mainFrame.setStatusBarText(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.studysaved") + DatasetController.getInstance().getActiveDs().getLastStoredLocation() + " at " + Utils.getFormattedNow());
    			System.exit(0);
    			return JFileChooser.APPROVE_OPTION;
        	} else {
    			WrappedJOptionPane.showMessageDialog(mainFrame, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.errorsaving"), PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.errorsavingtitle"), WrappedJOptionPane.WARNING_MESSAGE);
    			//clear the status bar
    			mainFrame.setStatusBarText(PropertiesHelper.getStringFor(" "));
    			
    			return JFileChooser.CANCEL_OPTION;
    		}
    	}
	}

	/**
	 * Save the dataset 
	 * @param mainFrame the main window of the application
	 */
	public static int saveDataset(MainFrame mainFrame) {
		StudyDataSet activeSet = DatasetController.getInstance().getActiveDs();
		
		//if no location is set, then tell user to use the save as option
		//we do not know where to save to!
		//this should not happen...but just in case!
		if (activeSet.getLastStoredLocation() == null) {
			WrappedJOptionPane.showMessageDialog(mainFrame, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.usesaveas"), PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.usesaveastitle"), WrappedJOptionPane.WARNING_MESSAGE);
			return JFileChooser.CANCEL_OPTION;
		}
		
        if (activeSet.getLastStoredLocation().equals(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.repkey"))) {
        	//it's a repository file; so we need to save it there!
        	
        	//but only if it's fully configured
        	if (activeSet != null && 
        			activeSet.isFullyConfigured() && 
        			!activeSet.getDs().isPublished()) {
            	DatasetSaver dsSaver = new DatasetSaver(mainFrame, activeSet, false);
            	//save dataset updates the status messages in the main frame on completion
            	//TODO : if saving fails, show the user the save as dialog
            	if (dsSaver.saveDataset()) {
            		return JFileChooser.APPROVE_OPTION;
            	} else {
            		return DatasetUtility.saveAsDataset(mainFrame);
            	}
            //trying to save a dataset that is not fully configured or has been published
        	} else {
        		return DatasetUtility.saveAsDataset(mainFrame);
        	}
        } else {
        	//save it to the file destination and update the status bar
        	if (LocalFileUtility.saveActiveDsFile()) {
    			mainFrame.setStatusBarText(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.studysaved") + DatasetController.getInstance().getActiveDs().getLastStoredLocation() + " at " + Utils.getFormattedNow());

    			return JFileChooser.APPROVE_OPTION;
        	} else {
    			WrappedJOptionPane.showMessageDialog(mainFrame, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.errorsaving"), PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.errorsavingtitle"), WrappedJOptionPane.WARNING_MESSAGE);
    			//clear the status bar
    			mainFrame.setStatusBarText(PropertiesHelper.getStringFor(" "));
    			
    			return JFileChooser.CANCEL_OPTION;
    		}
        	
    	}
	}
	
	/**
	 * Save the dataset 
	 * @param mainFrame the main window of the application
	 */
	public static void publishDataset(MainFrame mainFrame) {
		StudyDataSet activeSet = DatasetController.getInstance().getActiveDs();
		
    	//it's a repository file; so we need to save it there!
    	DatasetSaver dsSaver = new DatasetSaver(mainFrame, activeSet, true);
    	//publish dataset (save + true set above)
    	//save dataset updates the status messages in the main frame on completion
    	dsSaver.saveDataset();
	}

	
	/**
	 * Save as; shows the chooser, saves to repository or file
	 * and then updates the main frame status bar
	 * 
	 * @param mainFrame main window of the application
	 * @return the value returned for the chooser (approve selection, cancel etc) 
	 */
	public static int saveAsDataset(MainFrame mainFrame) {
		StudyChooser sc = new StudyChooser(mainFrame);
		//set the study name as the default name to save as
		sc.setSelectedFile(new File(DatasetController.getInstance().getActiveDs().getDs().getName()));
		int returnVal = sc.showSaveDialog(mainFrame);
		
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				if (sc.getMode() == StudyChooser.REPOSITORY_VIEW) {
					//can only be the active dataset here; no other possibilities are permitted!!!  
					//field is uneditable
					StudyDataSet activeSet = DatasetController.getInstance().getActiveDs();
					if (activeSet.getDs().isPublished()) {
						JOptionPane.showMessageDialog(mainFrame, "This is a published study.  It cannot be resaved to the database. \n");
						//try to save as again
						saveAsDataset(mainFrame);;
						return -1;
					}
					
		        	DatasetSaver dsSaver = new DatasetSaver(mainFrame, activeSet, false);
		        	dsSaver.saveDataset();
				} else { 
					//save the file
					StringBuffer scFilePath = new StringBuffer(sc.getSelectedFile().getAbsolutePath());
					if (scFilePath.indexOf(".") == -1){
						scFilePath.append(".xml");
					}
					String filePath = scFilePath.toString();
					
					boolean saved = false;
					
					//if local file exists, warn user that it will be overwritten
					if (new File(filePath).exists()) {
						Object[] options = {"Yes",
								"No"};
						int n = JOptionPane.showOptionDialog(mainFrame, 
								PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.studychooser.overwriteexistingfile"),
								PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.studychooser.overwrite"),
								JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE,
								null,
								options,
								options[0]);
						if (n == JOptionPane.YES_OPTION) {
							saved = LocalFileUtility.saveAsActiveDsFile(filePath, true);
						//if no, reopen the study chooser
						} else if (n == JOptionPane.NO_OPTION) {
							saveAsDataset(mainFrame);
						}
					} else {
						saved = LocalFileUtility.saveAsActiveDsFile(filePath);
					}
					
					
					if (saved) {
						//has just been saved so set dirty to false
						DatasetController.getInstance().getActiveDs().setDirty(false);
						
						//update the last stored location and the status bar
						DatasetController.getInstance().getActiveDs().setLastStoredLocation(filePath);
					
						mainFrame.setStatusBarText(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.studysaved") + filePath + " at " + Utils.getFormattedNow());
					} else {
						mainFrame.setStatusBarText(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.studysavedfailed") + filePath);
					}
				}
			}
			
			return returnVal;
		}
	
	
	/**
	 * Save as; shows the chooser, saves to repository or file
	 * and then updates the main frame status bar
	 * 
	 * @param mainFrame main window of the application
	 * @return the value returned for the chooser (approve selection, cancel etc) 
	 */
	public static int saveAsDatasetAndExit(MainFrame mainFrame) {
		StudyChooser sc = new StudyChooser(mainFrame);
		//set the study name as the default name to save as
		sc.setSelectedFile(new File(DatasetController.getInstance().getActiveDs().getDs().getName()));
		int returnVal = sc.showSaveDialog(mainFrame);
		
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				if (sc.getMode() == StudyChooser.REPOSITORY_VIEW) {
					//can only be the active dataset here; no other possibilities are permitted!!!  
					//field is uneditable
					StudyDataSet activeSet = DatasetController.getInstance().getActiveDs();
					if (activeSet.getDs().isPublished()) {
						JOptionPane.showMessageDialog(mainFrame, "This is a published study.  It cannot be resaved to the database. \n");
						//try to save as again
						saveAsDatasetAndExit(mainFrame);
						return -1;
					}
					
		        	DatasetSaver dsSaver = new DatasetSaver(mainFrame, activeSet, false);
		        	dsSaver.saveDataset(true);
				} else { 
					//save the file
					StringBuffer scFilePath = new StringBuffer(sc.getSelectedFile().getAbsolutePath());
					if (scFilePath.indexOf(".") == -1){
						scFilePath.append(".xml");
					}
					String filePath = scFilePath.toString();
					
					boolean saved = false;
					
					//if local file exists, warn user that it will be overwritten
					if (new File(filePath).exists()) {
						Object[] options = {"Yes",
								"No"};
						int n = JOptionPane.showOptionDialog(mainFrame, 
								PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.studychooser.overwriteexistingfile"),
								PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.studychooser.overwrite"),
								JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE,
								null,
								options,
								options[0]);
						if (n == JOptionPane.YES_OPTION) {
							saved = LocalFileUtility.saveAsActiveDsFile(filePath);
						//if no, reopen the study chooser
						} else if (n == JOptionPane.NO_OPTION) {
							saveAsDataset(mainFrame);
						}
					} else {
						saved = LocalFileUtility.saveAsActiveDsFile(filePath);
						//this is save as and then exit
					}
					
					
					if (saved) {
						//has just been saved so set dirty to false
						DatasetController.getInstance().getActiveDs().setDirty(false);
						
						//update the last stored location and the status bar
						DatasetController.getInstance().getActiveDs().setLastStoredLocation(filePath);
					
						mainFrame.setStatusBarText(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.studysaved") + filePath + " at " + Utils.getFormattedNow());
						//exit after local file save
						System.exit(0);
					} else {
						mainFrame.setStatusBarText(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.studysavedfailed") + filePath);
					}
				}
			}
			
			return returnVal;
		}
	
	public static void patchDataset(MainFrame mainFrame) {
		StudyDataSet activeSet = DatasetController.getInstance().getActiveDs();
		DatasetSaver dsSaver = new DatasetSaver(mainFrame, activeSet, false);
    	dsSaver.patchDataset();
	}
	
	
	/**
	 * Close the dataset; remove it from the tree
	 * Close all document tabs currently open
	 * Update the status bar
	 * @param mainFrame the main window of the application
	 */
	public static void closeDataset(MainFrame mainFrame) {
		//autosave just before closing!
		LocalFileUtility.autosave();
		
		//if temp file exists, this must be removed
		TempFileController.getInstance().deleteTempFile();
		DocTreeModel.getInstance().removeDSDataset(DatasetController.getInstance().getActiveDs());
		mainFrame.getDocPane().removeAll();
		mainFrame.setStatusBarText(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.studyclosed"));
	}
	
}
