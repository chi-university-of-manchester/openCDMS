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
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.HibernateFactory;
import org.psygrid.datasetdesigner.controllers.DatasetController;
import org.psygrid.datasetdesigner.model.DELStudySet;
import org.psygrid.datasetdesigner.model.StudyDataSet;
import org.psygrid.datasetdesigner.model.DocTreeModel;
import org.psygrid.datasetdesigner.ui.MainFrame;
import org.psygrid.datasetdesigner.utils.DatasetUtility;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.utils.LocalFileUtility;

public class OpenLibraryViewAction extends AbstractAction {

	private static final long serialVersionUID = 1L;
	
	private MainFrame frame;
	
	public OpenLibraryViewAction(MainFrame frame) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.openlibraryview"));
		this.frame = frame;
	}

	public void actionPerformed(ActionEvent e) {
		
		if (DatasetController.getInstance().getActiveDs() != null && 
				! (DatasetController.getInstance().getActiveDs() instanceof DELStudySet)) {
			//if dirty, prompt to save
			if (DatasetController.getInstance().getActiveDs().isDirty()) {
				int returnVal = WrappedJOptionPane.showConfirmDialog(frame, 
					PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.actions.unsavedchangessave"), PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.actions.unsavedchangestitle"), JOptionPane.YES_NO_CANCEL_OPTION);
				if (returnVal == JOptionPane.CANCEL_OPTION) {
					//do nothing; exit here!
					return;
				} else if (returnVal == JOptionPane.YES_OPTION) {
					String lastStoredLocation = DatasetController.getInstance().getActiveDs().getLastStoredLocation(); 
					//if last stored location set, save dataset and then close it
					if (lastStoredLocation != null) {
						int saveReturnVal = DatasetUtility.saveDataset(frame);
						if (saveReturnVal == JFileChooser.APPROVE_OPTION) {
							DatasetUtility.closeDataset(frame);
						} else {
							if (DatasetUtility.saveAsDataset(frame) == JFileChooser.APPROVE_OPTION) {
								DatasetUtility.closeDataset(frame);
							}
						}
						//if no last stored location set, then save as... dataset and close it
					} else {
						if (DatasetUtility.saveAsDataset(frame) == JFileChooser.APPROVE_OPTION) {
							DatasetUtility.closeDataset(frame);
						}
					}
				} else {
					DatasetUtility.closeDataset(frame);
				}
			}  else {
				//not dirty os just close it!
				DatasetUtility.closeDataset(frame);
			}
		}
		
		for (StudyDataSet d: DocTreeModel.getInstance().getAllDSDatasets()) {
			if (d instanceof DELStudySet) {
				return;	//DEL already opened, nothing to do.
			}
		}
		
		frame.getMainMenuBar().setDelContext(true);

		//first try to load local copy from file
		if (!LocalFileUtility.loadDEL()) {
			final HibernateFactory factory = new HibernateFactory();
			DataSet ds = factory.createDataset("Library", "Library");
			ds.setProjectCode("Library");
			DELStudySet dataset = new DELStudySet();
			dataset.setDs(ds);
			DocTreeModel.getInstance().addDataset(dataset);
		}
		
	}
	
}