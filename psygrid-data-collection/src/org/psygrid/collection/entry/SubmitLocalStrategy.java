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

package org.psygrid.collection.entry;

import java.io.IOException;

import javax.swing.JOptionPane;

import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.collection.entry.persistence.SaveRecordWorker;
import org.psygrid.collection.entry.remote.CommitDocumentWorker;
import org.psygrid.collection.entry.remote.RemoteManager;
import org.psygrid.collection.entry.ui.RandomizeDialog;
import org.psygrid.data.model.hibernate.DocumentInstance;
import org.psygrid.data.model.hibernate.Record;

public class SubmitLocalStrategy extends SubmitDocumentStrategy {

    public SubmitLocalStrategy(Application application) {
        super(application);
    }

    @Override
    public void submit(Record currentRecord) {
        commitOrSave(currentRecord, isAlwaysOnlineMode());
    }
    
    private void commitOrSave(Record currentRecord,
            boolean alwaysOnlineMode) {
        final ApplicationModel parentModel = application.getModel();
        DocumentInstance docInstance = parentModel.getCurrentDocOccurrenceInstance();
        Record record = docInstance.getRecord();
        
        if (!randomizeIfNecessary(application, record, docInstance))
            return;
        parentModel.unsetDisabledPresModelsFromDocInstance();
        if ( RemoteManager.getInstance().isTestDataset() ){
            //Running in test/preview mode.
            //Try to update the record status (need to do this to unlock
            //subsequent document groups) then go straight to showing chooser 
            //again/exiting as we do not save anything to file.
            try{
                PersistenceManager.getInstance().updateRecordStatus(record, docInstance);
            }
            catch(IOException ex){
                //do nothing - in test/preview mode we never write to disk so
                //it should not be possible to get an IOException
            }
            success();
        }
        else{
            //Running in normal mode
            if (alwaysOnlineMode){
                SwingWorkerExecutor.getInstance().execute(
                		new CommitDocumentWorker(application, docInstance, true) {
                    @Override
                    protected void success() {
                        SubmitLocalStrategy.this.success();
                    }
					@Override
					protected void failure() {
						SubmitLocalStrategy.this.failure();
					}
                });
        	}
            else
                SwingWorkerExecutor.getInstance().execute(
                		new SaveRecordWorker(application, record, docInstance) {
                    @Override
                    protected void success() {
                        SubmitLocalStrategy.this.success();
                    }
					@Override
					protected void failure() {
						SubmitLocalStrategy.this.failure();
					}
                });
        }
    }

    /**
     * @return {@code true} if successful, {@code false} otherwise.
     */
    private boolean randomizeIfNecessary(Application application, Record record,
            DocumentInstance docInstance) {
           //see if we need to trigger randomization
    	

    	
    	boolean returnValue = true;
        if ( record.getDataSet().isEslUsed() && record.getDataSet().isRandomizationRequired() ){
            if ( docInstance.getOccurrence().isRandomizationTrigger() 
                    && docInstance.getIsRandomised() == null){
            	
            	//Need to grab a version of the record that has Site info.
            	Record record2 = null;
            	boolean retrieveRecordSummarySuccess = false;
            	try {
        			record2 = RemoteManager.getInstance().getRecordSummary(record.getIdentifier().getIdentifier());
        			retrieveRecordSummarySuccess = true;
        		} catch (Exception e) {
        			// TODO Auto-generated catch block
        			String message = "A technical problem occurred before randomization could take place. Please try again later.";
        			JOptionPane.showMessageDialog(application, message, "Randomization Not Attempted", JOptionPane.INFORMATION_MESSAGE);
        			returnValue = false;
        		} 
            	
        		if(retrieveRecordSummarySuccess){
	                RandomizeDialog dlg = new RandomizeDialog(application, record2, application.getModel().getCurrentRecord().getDataSet().getShowRandomisationTreatment(),
	                		application.getModel().getCurrentRecord().getDataSet().getUseMedsService());
	                dlg.setVisible(true);
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
	                        returnValue = false;
	                        break;
	                    case OFFLINE:
	                        //User requested randomization but is currently offline, so randomization cannot be done.
	                        String offlineMessage = "You are currently offine and so cannot randomize the subject at this time.\n\n" +
	                        "In order that you may try to randomize the subject in future it is not possible to\n"+
	                        "save the document. Instead you will now be returned to the document editing view,\n"+
	                        "where you can save the document as incomplete and try again later.";
	                        JOptionPane.showMessageDialog(application, offlineMessage, "No Randomization Performed", JOptionPane.ERROR_MESSAGE);
	                        returnValue = false;
	                        break;
	                    case CANCELLED:
	                    	returnValue = false;
	                        break;
	                    case SUCCESS:
	                        //record that randomisation has occurred
	                        docInstance.setIsRandomised(true);
	                        //do nothing else - execution will continue below and the document will be saved
	                    }
	                    break;
	                case NO:
	                    //record that randomisation is not to be used
	                    docInstance.setIsRandomised(false); 
	                    break;
	                case CANCEL:
	                	returnValue = false;
	                	break;
	                }
        		}
            }
        }
        if ( !returnValue ){
        	//re-enable the forward button as we are cancelling the randomization
        	//and going back to the document view
        	application.getModel().getForwardAction().setEnabled(true);
        }
        return returnValue;
    }
    
    private boolean isAlwaysOnlineMode() {
        synchronized (PersistenceManager.getInstance()) {
            return PersistenceManager.getInstance().getData().isAlwaysOnlineMode();
        }
    }
}
