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
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;

import org.jdesktop.swingworker.SwingWorker;
import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.DocumentStatus;
import org.psygrid.collection.entry.ExceptionsHelper;
import org.psygrid.collection.entry.persistence.DataSetSummary;
import org.psygrid.collection.entry.persistence.ExternalIdGetter;
import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.collection.entry.remote.RemoteServiceFault;
import org.psygrid.collection.entry.security.EntrySAMLException;
import org.psygrid.collection.entry.util.ResetWaitRunnable;
import org.psygrid.collection.entry.util.WaitRunnable;
import org.psygrid.data.utils.security.NotAuthorisedFault;

public class PrintDocInstanceChooserLoader extends SwingWorker<ChoosableList, Object> {

    protected Application application;
    
    public PrintDocInstanceChooserLoader(Application application) {
        this.application = application;
    }
 
    @Override
    protected ChoosableList doInBackground() throws ConnectException, SocketTimeoutException, IOException, 
            NotAuthorisedFault, RemoteServiceFault, EntrySAMLException {
        
        new WaitRunnable(application).run();  
        
        List<DataSetSummary> dssList = 
			PersistenceManager.getInstance().getData().getDataSetSummaries();
        ChoosableList choosableList = null;
        List<Choosable> datasetList = new ArrayList<Choosable>();
		choosableList = new ChoosableList(datasetList);
		for (DataSetSummary dss: dssList) {
			ChoosableDataSet set = new LazyChoosableDataSet(dss, choosableList);
			datasetList.add(set);
		}
		return choosableList;
    }
    
    @Override
    protected void done() {
        ChoosableList choosableList;
        try {
            choosableList = get();
            try{
	            if (choosableList.getChildren().size() == 0) {
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
            // Launches dialog box asynchronously
            launchDialog(choosableList);
        } catch (InterruptedException e) {
            ExceptionsHelper.handleInterruptedException(e);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof ConnectException) {
                ExceptionsHelper.handleConnectException(application, 
                        (ConnectException) cause);
            } 
            else if (cause instanceof SocketTimeoutException) {
                ExceptionsHelper.handleSocketTimeoutException(application, 
                        (SocketTimeoutException) cause);
            }
            else if (cause instanceof IOException) {
                ExceptionsHelper.handleIOException(application, 
                        (IOException) cause, false);
            }
            else if (cause instanceof RemoteServiceFault) {
                ExceptionsHelper.handleRemoteServiceFault(application,
                        (RemoteServiceFault) cause);
            }
            else if (cause instanceof NotAuthorisedFault) {
                ExceptionsHelper.handleNotAuthorisedFault(application,
                        (NotAuthorisedFault) cause);
            }
            else if (cause instanceof EntrySAMLException) {
                ExceptionsHelper.handleEntrySAMLException(application,
                        (EntrySAMLException) cause);
            }
            else {
                ExceptionsHelper.handleFatalException(cause);
            }
        } finally {
            new ResetWaitRunnable(application).run();
        }
    }
    
    protected void launchDialog(ChoosableList choosableList) {
        new PrintDocChooserDialog(application, choosableList);
    }

    private void showEmptyListMessage(){
        new ResetWaitRunnable(application).run();
        //TODO change this message
        JOptionPane.showMessageDialog(application, "No documents in the given state", "No Documents", JOptionPane.INFORMATION_MESSAGE);
        return;
    }
    
    //TODO this is repeated in LoadDocumentChooserLoader - move outside?
	protected final class LazyChoosableDataSet extends ChoosableDataSet {
		private boolean childrenPopulated = false;

		private LazyChoosableDataSet(DataSetSummary dataSet, Choosable parent) {
			super(dataSet, parent);
		}

		@Override
		public List<Choosable> getChildren()
		throws ChoosableException {
			return getChildren(null);
		}

		@Override
		public List<Choosable> getChildren(DocumentStatus status)
		throws ChoosableException {
			synchronized (children) {
				if (childrenPopulated)
					return children;
				try {
					for (String identifier: ChooserHelper.loadChoosableRecords(application,
							this, this, status, false)) {
						
						String displayIdentifier = identifier;
						if(this.dataSet.getUseExternalIdAsPrimary() == true){
							displayIdentifier = ExternalIdGetter.get(identifier);
						}
						RemoteChoosableRecord cRecord = new RemoteChoosableRecord(displayIdentifier, identifier, status, true, (AbstractChoosableWithChildren)getParent());
						children.add(cRecord);
					}
				} catch (Exception e) {
					throw new ChoosableException(e.getMessage(), e);
				}

				childrenPopulated = true;
				return children;
			}
		}
	}
}
