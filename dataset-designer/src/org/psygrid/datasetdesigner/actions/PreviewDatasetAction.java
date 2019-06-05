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

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.psygrid.collection.entry.Launcher;
import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.data.model.hibernate.DataSet;

import org.psygrid.datasetdesigner.controllers.DatasetController;
import org.psygrid.datasetdesigner.model.StudyDataSet;
import org.psygrid.datasetdesigner.ui.MainFrame;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.utils.LocalFileUtility;

/**
 * @author Rob Harper
 *
 */
public class PreviewDatasetAction extends AbstractAction {

	private static final long serialVersionUID = -5033867169718143936L;

	private MainFrame frame;
	
	public PreviewDatasetAction(MainFrame frame) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.previewdataset"));
		this.frame = frame;
	}

	public void actionPerformed(ActionEvent e) {
		
    	frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		frame.setStarted();

		try{
			PersistenceManager pManager = PersistenceManager.getInstance();
			
			//Step 0. Save all the current datasets to disk - in case anything goes wrong!
			//pManager.save(DocTreeModel.getInstance().getAllDSDatasets(), new File(pManager.getUserDirLocation()+"dsdatasets.xml").toString());
			LocalFileUtility.autosave();
			
			StudyDataSet dsSet = DatasetController.getInstance().getActiveDs();
			
			//Step 1. Create dataset object graph in memory
			String message = dsSet.cleanAndCheckDataset();
			if ( null != message ){
				JOptionPane.showMessageDialog(frame, message);
	        	frame.setFinished();
	        	frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				return;
			}
	
			DataSet dsHibernate = (DataSet)dsSet.getDs(); 
			DataSet.setPrepareElementForNewRevision(true);
			DataSet newCleanDataSet = (dsHibernate).toDTO().toHibernate();
			
			//set the 'editing permitted' flag on the documents
			for ( int i=0, c=newCleanDataSet.numDocuments(); i<c; i++ ){
				newCleanDataSet.getDocument(i).setEditingPermitted(true);
			}
			
			//Step 2. Save object graph as XML
			String dsFilePath = pManager.getUserDirLocation()+newCleanDataSet.getProjectCode()+".xml";
			pManager.save(newCleanDataSet, new File(dsFilePath).toString());
				
			//Step 3. Launch CoCoA in preview mode 
			String args[] = new String[]{"-f", dsFilePath, pManager.getUserDirLocation()+"stdcodes.xml"};
			Launcher.main(args);
			
		}
		catch(IOException ex){
			JOptionPane.showMessageDialog(frame, "An error occurred during dataset preview.");
			ex.printStackTrace();
		}
		finally{
	    	frame.setFinished();
	    	frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}

}
