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
import java.util.concurrent.ExecutionException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingworker.SwingWorker;
import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.ExceptionsHelper;
import org.psygrid.collection.entry.SwingWorkerExecutor;
import org.psygrid.collection.entry.persistence.ExternalIdGetter;
import org.psygrid.collection.entry.remote.RemoteManager;
import org.psygrid.collection.entry.remote.RemoteServiceFault;
import org.psygrid.collection.entry.security.EntrySAMLException;
import org.psygrid.common.ui.WrappedJOptionPane;
import org.psygrid.data.model.hibernate.Record;
import org.psygrid.data.reporting.GroupsNotAllowedException;
import org.psygrid.data.reporting.Report;
import org.psygrid.data.reporting.definition.IReport;
import org.psygrid.data.utils.security.NotAuthorisedFault;

public class ReportChooserDialog extends ChooserDialog  {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private static final Log LOG = LogFactory.getLog(ReportChooserDialog.class);

    private Application application;
    
    public ReportChooserDialog(Application parent, ChoosableList choosableList) {
        super(parent, choosableList, null, Messages.getString("ReportChooserDialog.title"));
        this.application = parent;
    }
    
    @Override
    protected void addChooserSelectedListener() {
        getMainPanel().addReportSelectedListener(new ReportSelectedListener() {
           public void reportSelected(ReportSelectedEvent event) {
               dispose();
               renderReport(event.getReportDefinition(), event.getRecord());
           }
        });
    }
    
    private void renderReport(final IReport reportDefinition, final Record record) {
        SwingWorker<Report, Object> worker = new SwingWorker<Report, Object>(){
          @Override
          protected Report doInBackground() throws EntrySAMLException, SocketTimeoutException, 
                  ConnectException, IOException, RemoteServiceFault, 
                  EntrySAMLException, NotAuthorisedFault, GroupsNotAllowedException   {
              return RemoteManager.getInstance().generateReport(reportDefinition,
                      record);
          }
          @Override
          protected void done() {
              try {
                  Report report = get();
                  setWait(false);
                  getParent().renderReport(report);
              } catch (InterruptedException e) {
                  setWait(false);
                  ExceptionsHelper.handleInterruptedException(e);
              } catch (ExecutionException e) {
                  setWait(false);
                  Throwable cause = e.getCause();
                  if (cause instanceof ConnectException) {
                      ExceptionsHelper.handleConnectException(getParent(), 
                              (ConnectException) cause);
                  } 
                  else if (cause instanceof SocketTimeoutException) {
                      ExceptionsHelper.handleSocketTimeoutException(getParent(), 
                              (SocketTimeoutException) cause);
                  }
                  else if (cause instanceof IOException) {
                      ExceptionsHelper.handleIOException(getParent(), 
                              (IOException) cause, false);
                  }
                  else if (cause instanceof RemoteServiceFault) {
                      ExceptionsHelper.handleRemoteServiceFault(getParent(),
                              (RemoteServiceFault) cause);
                  }
                  else if (cause instanceof NotAuthorisedFault) {
                	  LOG.error("Not authorised to view report",cause);
                	  WrappedJOptionPane.showMessageDialog(getParent(), "You are not authorised to view this report.");
                  }
                  else if (cause instanceof EntrySAMLException) {
                      ExceptionsHelper.handleEntrySAMLException(getParent(),
                              (EntrySAMLException) cause);
                  }
                  else {
                      ExceptionsHelper.handleFatalException(cause);
                  }
              }
          }
        };
        setWait(true);
        SwingWorkerExecutor.getInstance().execute(worker);
    }

    @Override
    protected ChooserPanel createChooserPanel() {
        return new ReportChooserPanel(application, this);
    }
    
    @Override
    public ReportChooserPanel getMainPanel() {
        return (ReportChooserPanel) super.getMainPanel();
    }

	public void eslRecordSelectedAction(Record record) {
		if (record == null) {
			return;
		}
		try {
			AbstractChoosableWithChildren dataset = (AbstractChoosableWithChildren)getMainPanel().getModel().getCurrentTableModel().parent;
			String displayIdentifier = record.getIdentifier().getIdentifier();
			if(record.getUseExternalIdAsPrimary() == true){
				displayIdentifier = ExternalIdGetter.get(record.getIdentifier().getIdentifier());
			}
			ChoosableReportRecord rcr = new ChoosableReportRecord(displayIdentifier, record.getIdentifier().getIdentifier(), dataset);
			dataset.setChildren(new ArrayList<Choosable>());
			dataset.addChild(rcr);

			getMainPanel().getModel().setParentTableModel();
			ChoosableList parent = (ChoosableList)getMainPanel().getModel().getCurrentTableModel().parent;

			int counter = 0;
			for (Choosable c: parent.getChildren()) {
				if (c == dataset) {
					getMainPanel().getModel().loadChoosable(counter);
					break;
				}
				counter++;
			}
			getMainPanel().getModel().getCurrentTableModel().fireTableDataChanged();
		}
		catch (Exception e) {
			ExceptionsHelper.handleException(getParent(), "Problem Occurred", null, "Unable to update the table for the participant.", false);
		}
	}
}
