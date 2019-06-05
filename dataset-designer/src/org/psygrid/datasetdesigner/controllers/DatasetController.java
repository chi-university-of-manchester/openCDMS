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

import org.psygrid.data.model.hibernate.Document;
import org.psygrid.datasetdesigner.model.StudyDataSet;
import org.psygrid.datasetdesigner.model.DocTreeModel;

/**
 * Remembers which dataset and which document are currently active 
 * and open in the study designer
 * 
 * @author pwhelan
 */
public class DatasetController {

	/**
	 * Singleton of the DataSet Controller
	 */
	private static DatasetController dsController;

	/**
	 * The currently open document in the document panel
	 */
	private Document activeDocument;
	
	/**
	 * The currently active dataset; the one with an open document or that is selected
	 * in the tree
	 */
	private StudyDataSet activeSet;
	
	/**
	 * Private Constructor; create the DatasetController
	 */
	private DatasetController() {}
	
	/**
	 * Return the singleton of this class
	 * @return the DatasetController
	 */
	public synchronized static DatasetController getInstance() {
		if (dsController == null) {
			dsController = new DatasetController();
		}
		return dsController;
	}
	
	/**
	 * Set the currently active document; when active document changes, update the active DS
	 * @param activeDocument the currently open document
	 */
	public void setActiveDocument(final Document activeDocument) {
		this.activeDocument = activeDocument;
		
		if (activeDocument != null) {
			StudyDataSet ownerSet = (DocTreeModel.getInstance().getDSDataset(activeDocument.getDataSet().getName()));
			if (ownerSet != activeSet) {
				setActiveDs(ownerSet);
			}
		}
	}
	
	/**
	 * Return the currently open document
	 * @return the currently active document
	 */
	public Document getActiveDocument() {
		return activeDocument;
	}

	/**
	 * Get the active dataset from the tree; always get the latest copy here
	 * @return the active and open dataset in the tree; null if 
	 * no dataset active
	 */
	public StudyDataSet getActiveDs() {
		return activeSet;
	}

	/**
	 * Set the currently active dataset; opens the 
	 * active dataset in the tree and keeps reference to active
	 * document up to date
	 * @param activeDs the active dataset
	 */
	public void setActiveDs(StudyDataSet activeSet) {
		this.activeSet = activeSet;
	}
	
}
