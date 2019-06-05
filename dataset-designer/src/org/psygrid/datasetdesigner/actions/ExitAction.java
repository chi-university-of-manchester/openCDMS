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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.datasetdesigner.model.DELStudySet;
import org.psygrid.datasetdesigner.model.DocTreeModel;
import org.psygrid.datasetdesigner.ui.MainFrame;
import org.psygrid.datasetdesigner.utils.DatasetUtility;
import org.psygrid.datasetdesigner.utils.LocalFileUtility;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.controllers.DatasetController;
import org.psygrid.datasetdesigner.controllers.RecentStudiesController;


/**
 * Exit the application; prompt to save before exiting
 * @author pwhelan
 */
public class ExitAction extends AbstractAction {
	
	/**
	 * Main window the application
	 */
	private MainFrame frame;
	
	/**
	 * Logger for this class
	 */
	private static final Log LOG = LogFactory.getLog(ExitAction.class);
	
	/**
	 * Constructor
	 * @param frame the main window of the application
	 */
	public ExitAction(MainFrame frame) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.exit"));
		this.frame = frame;
	}
	
	/**
	 * Exit the application and prompt to save
	 * @param aet The action event
	 */
	public void actionPerformed(ActionEvent aet) {
		//first save everything just in case!
		LocalFileUtility.autosave();
		//this if to exit app - for both yes and no
		//autosave the current file before exiting
		try {
			PersistenceManager.getInstance().save(((MainFrame)frame).getDelInitializer().getAuthoritiesForRoles(), PropertiesHelper.getDELAuthRolesLocation());
			PersistenceManager.getInstance().save(((MainFrame)frame).getDelInitializer().getLSIDAuthorities(), PropertiesHelper.getDELAuthoritiesLocation());
			PersistenceManager.getInstance().save(((MainFrame)frame).getDelInitializer().getTypes(), PropertiesHelper.getDELTypesLocation());
		} catch (Exception ex) {
			LOG.error("Unable to persist data locally", ex);
		} 

		//save the recently opened studies
		RecentStudiesController.getInstance().saveStudiestoFile();
		
		if (DatasetController.getInstance().getActiveDs() != null &&
				DatasetController.getInstance().getActiveDs().isDirty()
				&& !(DatasetController.getInstance().getActiveDs() instanceof DELStudySet))
		{
			
//			Custom button text
			Object[] options = {"Save and Exit",
			                    "Exit without Saving",
			                    "Cancel"};
			int n = JOptionPane.showOptionDialog(
	                frame, "You have unsaved changes to the open study.  Do you want to save before exiting?",
	                "Confirm Save and Exit",
	                JOptionPane.YES_NO_CANCEL_OPTION,
	                JOptionPane.QUESTION_MESSAGE,
	                null,
	                options,
	                options[2]);
			if (n == JOptionPane.CANCEL_OPTION) {
				//do nothing
				return;
			}
			else if (n == JOptionPane.YES_OPTION) {
				String lastStoredLocation = DatasetController.getInstance().getActiveDs().getLastStoredLocation(); 
				//if last stored location set, save dataset and then close it
				if (lastStoredLocation != null) {
					if (DatasetUtility.saveDatasetAndExit(frame) != JFileChooser.APPROVE_OPTION){
						return;
					}
					//exiting anyway, no need to close it!
					//DatasetUtility.closeDataset(frame);
				//if no last stored location set, then save as... dataset and close it
				} else {
					if (DatasetUtility.saveAsDatasetAndExit(frame) != JFileChooser.APPROVE_OPTION) {
						return;
					}
				}
			} else if (n == JOptionPane.NO_OPTION) {
				System.exit(0);
			}

			
		} else {
			int n = JOptionPane.showConfirmDialog(
                frame, "Are you sure you want to exit the application?",
                "Confirm Exit", JOptionPane.YES_NO_OPTION);
			if (n == JOptionPane.YES_OPTION) {
				try {
					//if the del view is open, then save it!
					if (DatasetController.getInstance().getActiveDs() != null
							&& DatasetController.getInstance().getActiveDs() instanceof DELStudySet) {
						LocalFileUtility.delsave();
					}
					//autosave the current file before exiting
					LocalFileUtility.autosave();
					PersistenceManager.getInstance().save(((MainFrame)frame).getDelInitializer().getAuthoritiesForRoles(), PropertiesHelper.getDELAuthRolesLocation());
					PersistenceManager.getInstance().save(((MainFrame)frame).getDelInitializer().getLSIDAuthorities(), PropertiesHelper.getDELAuthoritiesLocation());
					PersistenceManager.getInstance().save(((MainFrame)frame).getDelInitializer().getTypes(), PropertiesHelper.getDELTypesLocation());
				} catch (Exception ex) {
					LOG.error("Unable to persist data locally.", ex);
				}
				System.exit(0);
			}
		}
	}
}