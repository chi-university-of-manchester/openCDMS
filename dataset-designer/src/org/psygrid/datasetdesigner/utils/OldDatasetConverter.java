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
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.datasetdesigner.model.DELDataSet;
import org.psygrid.datasetdesigner.model.DSDataSet;
import org.psygrid.datasetdesigner.model.StudyDataSet;
import org.psygrid.datasetdesigner.model.DELStudySet;

/**
 * Convert Release 6 Datasets to the new version
 * and store them to a new directory
 * @author pwhelan
 */
public class OldDatasetConverter {

	/**
	 * The logger for this class
	 */
	private static final Log LOG = LogFactory.getLog(OldDatasetConverter.class);
	
	/**
	 * Convert from R6 (and previous) formats to the new format 
	 * >= R6.1.  This inolves copy all the data from 
	 * DSDataSet (old format) to the new StudySet format and
	 * saving the new files to the home directory in the /converted directory
	 * @param dsSet the dataset to convert
	 * @return true if it completes successfully and false if not
	 */
	public static boolean convertAndSaveOldDataset(DSDataSet dsSet) {
		if (dsSet instanceof DELDataSet) {
			String locationToSaveTo = PersistenceManager.getInstance().getUserDirLocation() + "DEL.xml";
			DELStudySet del = new DELStudySet();
			del.setDs(dsSet.getDs());
	        try {
	    		PersistenceManager.getInstance().save(del, locationToSaveTo);
	    		return true;
	        } catch (IOException ioex) {
	        	LOG.error("Error converting DEL to new format ", ioex);
	        }
		} else {
			StudyDataSet studySet = new StudyDataSet();
			studySet.setDs(dsSet.getDs());
			studySet.setGroupModels(dsSet.getGroups());
			studySet.setRandomHolderModel(dsSet.getRandomHolderModel());
			studySet.setReports(dsSet.getReports());
			studySet.setRoles(dsSet.getRoles());
			studySet.setSingleCentreStudy(dsSet.isSingleCentreStudy());
			
			//pre R6.1 datasets had no review and approve option
			studySet.getDs().setNoReviewAndApprove(false);
			
			String locationToSaveTo = PersistenceManager.getInstance().getUserDirLocation()+ "studies" + File.separator + studySet.getDs().getProjectCode() + ".xml";
			
	        try {
	    		PersistenceManager.getInstance().save(studySet, locationToSaveTo);
	    		return true;
	        } catch (IOException ioex) {
	        	LOG.error("Error converted to new format ", ioex);
	        }
		}
		
		return false;
	}
	
	
	
}
