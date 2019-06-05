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
package org.psygrid.datasetdesigner.controllers;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.collection.entry.persistence.PersistenceManager;


/**
 * Remembers which studies have been recently opened by the logged in user
 * Methods to load these from file on initial start up
 * save the new study when opened 
 * and save when exiting the application
 * 
 * @author pwhelan
 */
public class RecentStudiesController {
	
	private final static String RECENT_STUDIES_LOCATION = "recent_studies.xml";
	
	/**
	 * The logger for this class
	 */
	private static final Log LOG = LogFactory.getLog(RecentStudiesController.class);
	
	/**
	 * Singleton of the DataSet Controller
	 */
	private static RecentStudiesController recentStudiesController;
	
	private ArrayList<String> recentStudies;
	
	private RecentStudiesController() {
		recentStudies = new ArrayList<String>(); 
	}
	
	public static RecentStudiesController getInstance() {
		if (recentStudiesController == null) {
			recentStudiesController = new RecentStudiesController();
		}
		return recentStudiesController;
	}
	
	public void addStudy(String location) {
		//if already exists remove it because we want it in the no. 1 location
		if (recentStudies.contains(location)) {
			recentStudies.remove(location);
		}
		
		recentStudies.add(0, location);
		//make sure there is never more than 5 studies in the list
		trimToFive();

	}
	
	private void trimToFive() {
		while (recentStudies.size() > 5) {
			recentStudies.remove(recentStudies.size()-1);
		}
	}

	public void saveStudiestoFile() {
		try {
			 PersistenceManager.getInstance().save(recentStudies, PersistenceManager.getInstance().getUserDirLocation() + RECENT_STUDIES_LOCATION);
		} catch (Exception ex) {
			LOG.error("Exception trying to persist the recent studies file", ex);
		}
	}
	
	public void loadStudiesFromFile() {
		try {
			Object studiesFromFile = PersistenceManager.getInstance().load(PersistenceManager.getInstance().getUserDirLocation() + RECENT_STUDIES_LOCATION);
			if (studiesFromFile instanceof ArrayList) {
				recentStudies = (ArrayList<String>)studiesFromFile;
			}
		} catch (Exception ex) {
			LOG.error("Exception trying to load the recent studies file", ex);
		}
	}

	public ArrayList<String> getStudies() {
		return recentStudies;
	}


}
