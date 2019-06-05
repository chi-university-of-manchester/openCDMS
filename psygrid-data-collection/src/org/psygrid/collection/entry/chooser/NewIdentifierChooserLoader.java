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


package org.psygrid.collection.entry.chooser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingworker.SwingWorker;
import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.ExceptionsHelper;
import org.psygrid.collection.entry.persistence.DataSetSummary;
import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.collection.entry.remote.RemoteManager;
import org.psygrid.collection.entry.util.ResetWaitRunnable;
import org.psygrid.collection.entry.util.WaitRunnable;
import org.psygrid.data.model.hibernate.DataSet;

/**
 * Load the dialog to select the dataset for the new identifier.
 * 
 */
public class NewIdentifierChooserLoader extends SwingWorker<ChoosableList, Object> {
    private static final Log LOG = LogFactory.getLog(NewIdentifierChooserLoader.class);

    private Application application;

    public NewIdentifierChooserLoader(Application application) {
        this.application = application;
    }

    @Override
    protected ChoosableList doInBackground()    {
        new WaitRunnable(application).run();
        synchronized (PersistenceManager.getInstance()) {

        	ChoosableList choosableList = null;
        	
        	if ( RemoteManager.getInstance().isTestDataset() ){
        		//Running in test/preview mode
        		//Load the dataset from file and use it to create a new DataSetSummary
        		//which is then added as the only item to the choosable list. Note that
        		//the DataSetSummary is constructed by setting both its datasetSummary and 
        		//completeDataset properties to the loaded dataset.
        		try{
        			DataSet ds = PersistenceManager.getInstance().loadDataSet(RemoteManager.getInstance().getTestDatasetPath());
        			DataSetSummary dss = new DataSetSummary(ds, ds);
    	            List<Choosable> datasetList = new ArrayList<Choosable>();
    	            choosableList = new ChoosableList(datasetList);
        			ChoosableDataSet cds = new ChoosableDataSet(dss, choosableList);
        			datasetList.add(cds);
        		}
        		catch(IOException ex){
        			//do nothing - will be handled below as "no dataset available"
        		}
        	}
        	else{
	        	List<DataSetSummary> dssList = PersistenceManager.getInstance().
	                    getData().getDataSetSummaries();
	            
	            List<Choosable> datasetList = new ArrayList<Choosable>();
	            choosableList = new ChoosableList(datasetList);
	            for (DataSetSummary dss: dssList) {
	            	ChoosableDataSet set = new ChoosableDataSet(dss, choosableList);
	            	datasetList.add(set);
	            }
        	}
            return choosableList;
        }
    }
    
    @Override
    protected void done() {
        ChoosableList choosableList = null;
        try {
            choosableList = get();
            try{
	            if ( choosableList.getChildren().isEmpty() ) {
	            	showEmptyListMessage();
	                return;
	            }
            }
            catch(ChoosableException ex){
            	//use same behaviour when there is an error getting
            	//the children as for if there are no children
            	showEmptyListMessage();
                return;
            }
        } catch (ExecutionException ee) {
            ExceptionsHelper.handleFatalException(ee.getCause());
        } catch (InterruptedException e) {
            if (LOG.isWarnEnabled()) {
                LOG.warn(e.getMessage());
            }
        }
        finally {
            new ResetWaitRunnable(application).run();
        }
        new NewIdentifierChooserDialog(application, choosableList, null);
    }
    
    private void showEmptyListMessage(){
    	String title = Messages.getString("ChooserLoader.noDataSetTitle");
        String message = Messages.getString("ChooserLoader.noDataSetMessage");
        JOptionPane.showMessageDialog(application, message, title,
                JOptionPane.ERROR_MESSAGE);
    }
}
