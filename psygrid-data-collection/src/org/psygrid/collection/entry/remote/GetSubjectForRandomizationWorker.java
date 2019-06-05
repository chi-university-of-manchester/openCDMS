/*
Copyright (c) 2006-2009, The University of Manchester, UK.

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

package org.psygrid.collection.entry.remote;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;

import org.jdesktop.swingworker.SwingWorker;
import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.ExceptionsHelper;
import org.psygrid.collection.entry.SwingWorkerExecutor;
import org.psygrid.collection.entry.security.EntrySAMLException;
import org.psygrid.collection.entry.ui.RandomizeDialog;
import org.psygrid.collection.entry.util.ResetWaitRunnable;
import org.psygrid.collection.entry.util.WaitRunnable;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.utils.security.NotAuthorisedFault;

/**
 * @author Rob Harper
 *
 */
public class GetSubjectForRandomizationWorker extends SwingWorker<Record, Object> {

	private final Record record;
		
	private Application application;
	
	private DocumentOccurrence trigger;
	
	private boolean randomized = false;
	
	public GetSubjectForRandomizationWorker(Record record, Application application) {
		super();
		this.record = record;
		this.application = application;
	}

	@Override
	protected Record doInBackground() throws IOException, NotAuthorisedFault, RemoteServiceFault,
		EntrySAMLException, ConnectException, SocketTimeoutException {

		new WaitRunnable(application).run();
		
		//find the randomization trigger
		DataSet ds = record.getDataSet();
		for ( int i=0, c=ds.numDocuments(); i<c; i++ ){
			Document doc = ds.getDocument(i);
			for ( int j=0, d=doc.numOccurrences(); j<d; j++ ){
				DocumentOccurrence docOcc = doc.getOccurrence(j);
				if ( docOcc.isRandomizationTrigger() ){
					trigger = docOcc;
					break;
				}
			}
		}
		
		return RemoteManager.getInstance().getRecordSingleDocumentFromOccurrence(record, trigger);
	}

	@Override
	protected void done() {
		try{
			new ResetWaitRunnable(application).run();

			Record r = get();
			
			DocumentInstance docInstance = r.getDocumentInstance(trigger);
            RandomizeDialog dlg = new RandomizeDialog(application, r, application.getModel().getCurrentRecord().getDataSet().getShowRandomisationTreatment(),
            		application.getModel().getCurrentRecord().getDataSet().getUseMedsService());
            dlg.setVisible(true);
            boolean saveDocInst = false;
            switch (dlg.getButton()){
            case YES:
                switch (dlg.getResult()){
                case FAILURE:
                    //User requested randomization but it failed - in this case
                    //we cannot proceed with saving the document instance, as
                    //otherwise it will not be possible to retry randomization
                    //So we present a message to the user then go back to the 
                    //document, where they can try again or save it as incomplete
                    String message = "The attempt to allocate the subject to a treatment arm via randomization has failed.\n\n" +
                    "In order that you may try to randomize the subject again it is not possible to\n"+
                    "save the document. Instead you will now be returned to the document editing view,\n"+
                    "from which you may try to save the document again (thus trying randomization again\n"+
                    "too) or save the document as incomplete and try again later.\n\n"+
                    "If randomization continues to fail please contact support@psygrid.org.";
                    JOptionPane.showMessageDialog(application, message, "Randomization Failed", JOptionPane.ERROR_MESSAGE);
                    break;
                case OFFLINE:
                    //User requested randomization but is currently offline, so randomization cannot be done.
                    String offlineMessage = "You are currently offine and so cannot randomize the subject at this time.\n\n" +
                    "In order that you may try to randomize the subject in future it is not possible to\n"+
                    "save the document. Instead you will now be returned to the document editing view,\n"+
                    "where you can save the document as incomplete and try again later.";
                    JOptionPane.showMessageDialog(application, offlineMessage, "No Randomization Performed", JOptionPane.ERROR_MESSAGE);
                    break;
                case CANCELLED:
                    break;
                case SUCCESS:
                    //record that randomisation has occurred
                    docInstance.setIsRandomised(true);
                    randomized = true;
                    saveDocInst = true;
                    //do nothing else - execution will continue below and the document will be saved
                }
                break;
            case NO:
                //record that randomisation is not to be used
            	boolean oldIsRandomised = docInstance.getIsRandomised();
                docInstance.setIsRandomised(false); 
                if ( oldIsRandomised != docInstance.getIsRandomised() ){
                	saveDocInst = true;
                }
                break;
            case CANCEL:
            	break;
            }
            
            if ( saveDocInst ){
                SwingWorkerExecutor.getInstance().execute(
                		new CommitDocumentWorker(application, docInstance, true) {
                    @Override
                    protected void success() {
                        GetSubjectForRandomizationWorker.this.success();
                    }
					@Override
					protected void failure() {
						GetSubjectForRandomizationWorker.this.failure();
					}
                });
            }
            
		} catch (InterruptedException e) {
			ExceptionsHelper.handleInterruptedException(e);
		} catch (ExecutionException e) {
			Throwable cause = e.getCause();
			if (cause instanceof ConnectException ) {
				ExceptionsHelper.handleConnectException(
						application,
						(ConnectException) cause);
			} 
			if (cause instanceof SocketTimeoutException) {
				ExceptionsHelper.handleSocketTimeoutException(
						application,
						(SocketTimeoutException) cause);
			} 
			else if (cause instanceof SocketException) {
				ExceptionsHelper.handleException(application, 
						"Socket Exception Occurred", cause, cause.getMessage(), false);
			} 
			else if (cause instanceof IOException) {
				ExceptionsHelper.handleIOException(
						application,
						(IOException) cause, false);
			} 
			else if (cause instanceof NotAuthorisedFault) {
				ExceptionsHelper.handleNotAuthorisedFault(
						application,
						(NotAuthorisedFault) cause);
			} 
			else if (cause instanceof RemoteServiceFault) {
				ExceptionsHelper.handleRemoteServiceFault(
						application,
						(RemoteServiceFault) cause);
			} 
			else if (cause instanceof EntrySAMLException) {
				ExceptionsHelper.handleEntrySAMLException(
						application,
						(EntrySAMLException) cause);
			} 
		}
	}

	private void success(){
		if ( randomized ){
			application.getModel().setCanRandomize(false);
		}
	}
	
	private void failure(){
		//TODO can't save document instance - anything to do?
	}
}
