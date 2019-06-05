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
import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.DocumentStatus;
import org.psygrid.collection.entry.ExceptionsHelper;
import org.psygrid.collection.entry.persistence.DataSetSummary;
import org.psygrid.collection.entry.persistence.ExternalIdGetter;
import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.collection.entry.util.ResetWaitRunnable;
import org.psygrid.common.ui.WrappedJOptionPane;

/**
 * @author Lucy Bridges
 *
 */
public class LoadReportsChooserLoader extends LoadDocumentChooserLoader {

	private static final Log LOG = LogFactory.getLog(LoadReportsChooserLoader.class);

	public LoadReportsChooserLoader(Application application){
		super(application);
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

		new ReportChooserDialog(application, choosableList);
	}

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
					List<String> identifiers = ChooserHelper.loadChoosableRecords(application,
							this, this, status, false);
					children = new ArrayList<Choosable>(identifiers.size());
					ChoosableList choosableList = new ChoosableList(children);
					
					String displayIdentifier = null;
					
					for ( String identifier: identifiers){
						displayIdentifier = identifier;
						if(this.dataSet.getUseExternalIdAsPrimary() == true){
							
							displayIdentifier = ExternalIdGetter.get(identifier);
							
						}
						
						ChoosableReportRecord choosableRecord = 
							new ChoosableReportRecord(displayIdentifier, identifier, choosableList);
						children.add(choosableRecord);
					}            
				} catch (Exception e) {
					throw new ChoosableException(e.getMessage(), e);
				}

				childrenPopulated = true;
				return children;
			}
		}
	}

	private void showEmptyListMessage(){
		new ResetWaitRunnable(application).run();
		String message = Messages.getString("LoadReportsChooserLoader.noRecordsMessage");
		WrappedJOptionPane.showMessageDialog(application, message, 
				Messages.getString("LoadReportsChooserLoader.noRecordsTitle"), JOptionPane.INFORMATION_MESSAGE);
	}
}
