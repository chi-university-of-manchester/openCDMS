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

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.psygrid.common.ui.WrappedJOptionPane;
import org.psygrid.datasetdesigner.controllers.DatasetController;
import org.psygrid.datasetdesigner.model.DELStudySet;
import org.psygrid.datasetdesigner.model.DocTreeModel;
import org.psygrid.datasetdesigner.utils.LocalFileUtility;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.utils.DatasetUtility;
import org.psygrid.datasetdesigner.ui.MainFrame;

/**
 * Action to called to close a study 
 *  
 * @author pwhelan
 */
public class CloseStudyAction extends AbstractAction {

	/**
	 * Main window of the application
	 */
	private MainFrame mainFrame;
	
	/**
	 * Constructor; close the study
	 * @param mainFrame the main window of the application
	 */
	public CloseStudyAction(MainFrame mainFrame) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.closestudy"));
		this.mainFrame = mainFrame;
	}
	
	/**
	 * Close the currently open study
	 * Remove it from the tree; remove all open documents; update the status bar text
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
					}
				}  else {
					//only prompt if it's changed
					if (DatasetController.getInstance().getActiveDs().isDirty()) {
						int returnVal = WrappedJOptionPane.showConfirmDialog(mainFrame, 
							PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.actions.unsavedchangessave"), PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.actions.unsavedchangestitle"), JOptionPane.YES_NO_CANCEL_OPTION);
					if (returnVal == JOptionPane.CANCEL_OPTION) {
						//do nothing
					} else if (returnVal == JOptionPane.YES_OPTION) {
						String lastStoredLocation = DatasetController.getInstance().getActiveDs().getLastStoredLocation(); 
						//if last stored location set, save dataset and then close it
						if (lastStoredLocation != null) {
							DatasetUtility.saveDataset(mainFrame);
							DatasetUtility.closeDataset(mainFrame);
						//if no last stored location set, then save as... dataset and close it
						} else {
							if (DatasetUtility.saveAsDataset(mainFrame) == JFileChooser.APPROVE_OPTION) {
								DatasetUtility.closeDataset(mainFrame);
							}
						}
					} else if (returnVal == JOptionPane.NO_OPTION){
						DatasetUtility.closeDataset(mainFrame);
					}
				//if it's not dirty, show the chooser directly
				} else {
					DatasetUtility.closeDataset(mainFrame);
				}
			}
		}
		
		//whatever was closed, it can't be in del context now
		//and no panes should be open
		mainFrame.getDocPane().closeAll();
		mainFrame.getMainMenuBar().setDelContext(false);
		
	}
	
}
