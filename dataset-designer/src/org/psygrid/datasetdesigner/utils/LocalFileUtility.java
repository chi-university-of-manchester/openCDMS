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

import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.ModelException;
import org.psygrid.datasetdesigner.controllers.DatasetController;
import org.psygrid.datasetdesigner.controllers.TempFileController;
import org.psygrid.datasetdesigner.controllers.RecentStudiesController;
import org.psygrid.datasetdesigner.model.StudyDataSet;
import org.psygrid.datasetdesigner.model.DELStudySet;
import org.psygrid.datasetdesigner.model.DocTreeModel;


/**
 * @author pwhelan
 *
 */
public class LocalFileUtility extends DsLoader {

	/**
	 * The logger for this class
	 */
	private static final Log LOG = LogFactory.getLog(LocalFileUtility.class);

	/**
	 * Save the currently open file
	 * @param location the location to save to
	 * @return true if save is successful; false if not
	 */
	public synchronized static boolean saveAsActiveDsFile(String location) {
		return saveAsActiveDsFile(location, false);
	}

	/**
	 * Save the currently open file
	 * @param location the location to save to
	 * @param overwrite if true don't check on temp file existence 
	 * @return true if save is successful; false if not
	 */
	public synchronized static boolean saveAsActiveDsFile(String location, boolean overwrite) {
        try  {
            StudyDataSet activeSet = DatasetController.getInstance().getActiveDs();

            //if a temp file exists for the new location, can't save it!
            if (!overwrite) {
                if (TempFileController.getInstance().tempFileExists(new File(location), activeSet.getDs().getProjectCode())) {
                	return false;
                } 
            }
            
            //closes the old file and sets the new one
			TempFileController.getInstance().createTmpFile(new File(location), activeSet.getDs().getProjectCode());
			PersistenceManager.getInstance().save(activeSet, location);
			
            //has been saved again as a new file so can't be read only
            activeSet.setReadOnly(false);
        } catch (Exception ex) {
           	LOG.error("Exception saving dataset as : ", ex);
           	return false;
        }
		return true;
	}
	
	public synchronized static boolean saveActiveDsFile() {
        try  {
            StudyDataSet activeSet = DatasetController.getInstance().getActiveDs();
            PersistenceManager.getInstance().save(activeSet, activeSet.getLastStoredLocation());
            DatasetController.getInstance().getActiveDs().setDirty(false);
        } catch (Exception ex) {
           	LOG.error("Exception saving dataset : ", ex);
           	return false;
        }
		return true;
	}
	
	public synchronized static boolean loadFile(File fileToOpen) throws ModelException{
		try {
			String absolutePath = fileToOpen.getAbsolutePath();
			
			Object dsSet = PersistenceManager.getInstance().load(absolutePath);
			
			if (dsSet instanceof StudyDataSet) {
				// check for existence of temp file indicating that the study is 
				// in use by another user
				
				//Check the integrity of the dataset.
				DatasetIntegrityChecker.checkDatasetIntegrity((DataSet)((StudyDataSet)dsSet).getDs());
				
				setCanPatchDataset(((StudyDataSet)dsSet).getDs());
				
				if (TempFileController.getInstance().tempFileExists(fileToOpen, ((StudyDataSet)dsSet).getDs().getProjectCode())) {
					((StudyDataSet)dsSet).setReadOnly(true);	
				} else {
					((StudyDataSet)dsSet).setReadOnly(false);
					TempFileController.getInstance().createTmpFile(fileToOpen, ((StudyDataSet)dsSet).getDs().getProjectCode());
				}
				((StudyDataSet)dsSet).setDirty(false);
				DocTreeModel.getInstance().addDataset((StudyDataSet)dsSet);
				
				//ok, we've loaded it so add it to the recent studies list
				RecentStudiesController.getInstance().addStudy(fileToOpen.getAbsolutePath());
				
				return true;
			}

		} catch (Exception ex) {
			LOG.error("Error loading local datasets :" , ex);
			if(ex instanceof ModelException){
				throw (ModelException)ex;
			}
		}
		return false;
	}
	
	/**
	 * Save the DEL
	 * @return
	 */
	public synchronized static boolean delsave() {
		try {
			if (DatasetController.getInstance().getActiveDs() != null
					&& DatasetController.getInstance().getActiveDs() instanceof DELStudySet) {
				PersistenceManager.getInstance().save(DatasetController.getInstance().getActiveDs(),
						PersistenceManager.getInstance().getUserDirLocation() + "DEL.xml");
				return true;
			}
		} catch (Exception ex)
		{
			LOG.error ("Problem occurred saving the DEL", ex);
		}
		
		return false;
	}
	
	public synchronized static boolean loadDEL() {
		String DELLocation = PersistenceManager.getInstance().getUserDirLocation() + "DEL.xml";
		if (new File(DELLocation).exists()) {
			try {
				Object loadedSet = PersistenceManager.getInstance().load(new File(DELLocation).toString());
				if (loadedSet instanceof DELStudySet) {
					DocTreeModel.getInstance().addDataset((DELStudySet)loadedSet);
					return true;
				}
			} catch (Exception ex) {
				LOG.error("Excpetion loading the DEL library", ex);
			}
		}
		return false;
	}
	
	public synchronized static boolean autosave() {
        try {
        	StudyDataSet activeSet = DatasetController.getInstance().getActiveDs();
        	if (activeSet != null) {
        		
        		String autoSaveLocation = PersistenceManager.getInstance().getUserDirLocation() + activeSet.getDs().getName() + "-autosave.xml";
        		//make sure that the last saved location of this dataset is not the same as the 
        		//autosave location
        		if (!autoSaveLocation.equals(activeSet.getLastStoredLocation())) {
        			PersistenceManager.getInstance().save(activeSet, autoSaveLocation);
                	return true;
        		} 
        		LOG.error("Did not autosave because autosave location matches last saved location ");
        	}
        } catch (Exception ex) {
        	LOG.error("Exception auto-saving dataset ", ex);
        }
        return false;
	}


	
}
