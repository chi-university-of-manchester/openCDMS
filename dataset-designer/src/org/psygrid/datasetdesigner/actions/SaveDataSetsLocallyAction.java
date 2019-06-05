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
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.datasetdesigner.model.DocTreeModel;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;

public class SaveDataSetsLocallyAction extends AbstractAction {
	
	private JFrame frame;
	
	public SaveDataSetsLocallyAction(JFrame frame) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.savedatasetslocally"));
		this.frame = frame;
	}
	
	public void actionPerformed(ActionEvent aet) {
		try {
			//never call this or it could overwrite pre 6.1 datasets files
			//PersistenceManager.getInstance().save(DocTreeModel.getInstance().getAllDSDatasets(), new File(PersistenceManager.getInstance().getUserDirLocation()+"dsdatasets.xml").toString());
			JOptionPane.showMessageDialog(frame, "Studies saved successfully.");
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(frame, "An error occurred during study saving.");
			ex.printStackTrace();
		}
	}

}