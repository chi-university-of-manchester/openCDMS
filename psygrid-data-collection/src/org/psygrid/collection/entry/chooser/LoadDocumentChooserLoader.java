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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingworker.SwingWorker;
import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.DocumentStatus;
import org.psygrid.collection.entry.ExceptionsHelper;
import org.psygrid.collection.entry.persistence.DataSetSummary;
import org.psygrid.collection.entry.persistence.ExternalIdGetter;
import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.collection.entry.util.ResetWaitRunnable;
import org.psygrid.data.model.hibernate.DataSet;

/**
 * @author Rob Harper
 *
 */
public class LoadDocumentChooserLoader extends SwingWorker<ChoosableList, Object> {

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
						
						String theIdentifier = identifier;
						
						if(dataSet.getUseExternalIdAsPrimary() == true){
							theIdentifier = ExternalIdGetter.get(identifier);
						}
						
						RemoteChoosableRecord cRecord = new RemoteChoosableRecord(theIdentifier, identifier, status, true, (AbstractChoosableWithChildren)getParent());
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

	private static final Log LOG = LogFactory.getLog(LoadDocumentChooserLoader.class);

	protected Application application;

	public LoadDocumentChooserLoader(Application application){
		this.application = application;
	}

	@Override
	protected ChoosableList doInBackground() throws Exception {
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
		ChoosableList choosableList = null;
		try {
			choosableList = get();
			try{
				if (choosableList.getChildren().isEmpty()) {
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
		new LoadDocumentChooserDialog(application, choosableList, true, null,
				null, null, null);

	}

	private void showEmptyListMessage(){
        String title = Messages.getString("ChooserLoader.noDataSetTitle");
        String message = Messages.getString("ChooserLoader.noDataSetMessage");
        JOptionPane.showMessageDialog(application, message, title,
                JOptionPane.ERROR_MESSAGE);
	}
}
