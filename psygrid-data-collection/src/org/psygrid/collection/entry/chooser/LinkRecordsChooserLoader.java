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
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;

import org.jdesktop.swingworker.SwingWorker;
import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.ExceptionsHelper;
import org.psygrid.collection.entry.SwingWorkerExecutor;
import org.psygrid.collection.entry.event.IdentifierEvent;
import org.psygrid.collection.entry.event.IdentifierListener;
import org.psygrid.collection.entry.event.RecordSelectedEvent;
import org.psygrid.collection.entry.event.RecordSelectedListener;
import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.collection.entry.persistence.RecordsList;
import org.psygrid.collection.entry.remote.RemoteManager;
import org.psygrid.collection.entry.remote.RemoteServiceFault;
import org.psygrid.collection.entry.replication.PreLinkRecordsWorker;
import org.psygrid.collection.entry.security.DecryptionException;
import org.psygrid.collection.entry.security.EntrySAMLException;
import org.psygrid.collection.entry.ui.ConsentDialog;
import org.psygrid.collection.entry.ui.DdeIdentifierDialog;
import org.psygrid.collection.entry.ui.EslNewSubjectDialog;
import org.psygrid.collection.entry.ui.IdentifierPanelException;
import org.psygrid.collection.entry.ui.LinkNewOrExistingDialog;
import org.psygrid.collection.entry.ui.RecordsTableModel.Column;
import org.psygrid.collection.entry.util.ResetWaitRunnable;
import org.psygrid.collection.entry.util.WaitRunnable;
import org.psygrid.common.identifier.IdentifierHelper;
import org.psygrid.common.identifier.InvalidIdentifierException;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Group;
import org.psygrid.data.model.hibernate.Record;
import org.psygrid.data.utils.security.NotAuthorisedFault;

/**
 * Chooser Loader for the "Link Records" action.
 * <p>
 * A list of records that may be linked is retrieved from the repository;
 * that is, records belonging to a dataset that is the primary in a data
 * replication relationship, and are not already linked to secondary records.
 * <p>
 * These records are displayed in a dialog by their identifier. On selecting
 * a record a dialog is launched for the user to select the secondary record,
 * then the linking process and data replication between the two records is
 * initiated.
 * 
 * @author Rob Harper
 *
 */
public class LinkRecordsChooserLoader extends RecordChooserLoader {

	public LinkRecordsChooserLoader(Application application) {
		super(application);
	}

	@Override
	protected List<String> doInBackground() throws ConnectException, SocketTimeoutException, IOException, NotAuthorisedFault, RemoteServiceFault, EntrySAMLException, InvalidIdentifierException {
        new WaitRunnable(application).run();
        List<String> identifiers = RemoteManager.getInstance().getLinkableRecords();
        //post process the list of linkable records to remove those that don't have
        //any related secondary groups
        //TODO should really be server side...
        List<String> linkableRecords = new ArrayList<String>();
        Map<String, DataSet> dataSets = new HashMap<String, DataSet>();
        PersistenceManager pManager = PersistenceManager.getInstance();
        for ( String identifier: identifiers ){
        	String project = IdentifierHelper.getProjectCodeFromIdentifier(identifier);
        	String group = IdentifierHelper.getGroupCodeFromIdentifier(identifier);
        	DataSet dataSet = dataSets.get(project);
        	if ( null == dataSet ){
        		dataSet = pManager.getData().getCompleteDataSet(project);
        		dataSets.put(project, dataSet);
        	}
        	for ( int i=0, c=dataSet.numGroups(); i<c; i++ ){
        		Group g = dataSet.getGroup(i);
        		if ( g.getName().equals(group) && g.numSecondaryGroups()>0 ){
        			linkableRecords.add(identifier);
        		}
        	}
        }
        return linkableRecords;
	}

    @Override
    protected String getChooserTitle() {
        return "Select the record to link with.";
    }

    @Override
    protected EnumSet<Column> getEnumSet() {
        return EnumSet.of(Column.IDENTIFIER);
    }

	@Override
	protected RecordSelectedListener getRecordSelectedListener() {
        return new RecordSelectedListener(){
            public void recordSelected(RecordSelectedEvent recordSelectedEvent) {
            	linkRecords(recordSelectedEvent.getIdentifier());
            }
        };
	}
	
    protected void showNoRecordsMessage(){
        String title = Messages.getString("LinkRecordsChooserLoader.noLinkableRecordsTitle");
        String message = Messages.getString("LinkRecordsChooserLoader.noLinkableRecordsMessage");
        JOptionPane.showMessageDialog(application, message, title, 
                JOptionPane.ERROR_MESSAGE);
    }

	/**
	 * Once the primary record we want to link to has been selected the 
	 * next step is to select what we want to link to. This can either
	 * be a new record for the secondary dataset, or an existing record.
	 * <p>
	 * A simple dialog is shown with two options - new record or existing 
	 * record - and cancel.
	 * 
	 * @param identifier The identifier of the primary record.
	 */
	private void linkRecords(final String identifier){

        SwingWorker<Record, Object> worker = new SwingWorker<Record, Object>() {
		    @Override
		    protected Record doInBackground() throws ConnectException, SocketTimeoutException,
		            IOException, NotAuthorisedFault, RemoteServiceFault,
		            EntrySAMLException, InvalidIdentifierException {
		        
		        new WaitRunnable(application).run();
				return RemoteManager.getInstance().getRecordSummary(identifier);		
		    }
		
		    @Override
		    protected void done() {
		        try {
		            new ResetWaitRunnable(application).run();
		            final  Record  recordSummary = get();
		    		PersistenceManager pManager = PersistenceManager.getInstance();
		    		DataSet secDs = null;
		    		synchronized (pManager){
		    			try{
		    				secDs = pManager.getData().getCompleteDataSet(recordSummary.getDataSet().getSecondaryProjectCode());
		    			}
		    			catch(IOException ex){
		                    ExceptionsHelper.handleIOException(application, ex, false);
		    			}
		    		}
		            final LinkNewOrExistingDialog identifierDialog = 
		            	new LinkNewOrExistingDialog(application, recordSummary, secDs);
		            identifierDialog.setVisible(true);
		            switch(identifierDialog.getResult()){
		            case NEW:
		            	showNewIdentifierDialog(recordSummary, secDs);
		            	break;
		            case EXISTING:
		            	showExistingIdentifierDialog(recordSummary, secDs);
		            	break;
		            case CANCEL:	
		            	//do nothing
		            	break;
		            }
		            
		        } catch (InterruptedException e) {
		            new ResetWaitRunnable(application).run();
		            ExceptionsHelper.handleInterruptedException(e);
		        } catch (ExecutionException e) {
		            new ResetWaitRunnable(application).run();
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
		        } 
		    }
		};

		SwingWorkerExecutor.getInstance().execute(worker);
		
    }

	/**
	 * The secondary record has been selected. If it is a new record we collect
	 * consent and complete the participant register (if applicable).
	 * <p>
	 * The the actual linking and data replication is started.
	 * 
	 * @param primRecSummary The primary record (summary)
	 * @param secRecord The secondary record (local)
	 * @param newRecord True if the secondary record is new; False if
	 * it is already existing.
	 */
	private void secondarySelected(final  Record  primRecSummary, final  Record  secRecord, final boolean newRecord ){

        if ( null != secRecord ){
            //Assume that a non-null record implies that OK was clicked
            //in the Identifier Dialog
            if ( newRecord ){
                //if there is consent to gather, or a schedule start question to answer,
                //then we need to show the consent dialog
                DataSet ds = secRecord.getDataSet();
                if ( ds.numAllConsentFormGroups() > 0 || null != ds.getScheduleStartQuestion() ){
                    ConsentDialog dialog = new ConsentDialog(this.application, secRecord);
                    dialog.setVisible(true);
                    if ( !dialog.isOkClicked() ){
                        return;
                    }
                    
                    if ( ds.isEslUsed() ){
                        //launch ESL dialog
                        EslNewSubjectDialog eslDialog = new EslNewSubjectDialog(this.application, secRecord);
                        eslDialog.setVisible(true);
                        if ( !eslDialog.isEslSaveSuccessful() ){
                            return;
                        }
                    }
                    
                }
                
            }
            
            //start the process of copying records to the secondary
            new WaitRunnable(application).run();
            PreLinkRecordsWorker worker = new PreLinkRecordsWorker(primRecSummary, secRecord, newRecord, application);
            SwingWorkerExecutor.getInstance().execute(worker);
            
        }
		
	}
	
	/**
	 * Show the dialog for selecting a new secondary record.
	 * 
	 * @param primRecord The primary record.
	 * @param secDs The secondary dataset.
	 */
	private void showNewIdentifierDialog(final  Record  primRecord, DataSet secDs){
		List<String> secGroups = getSecondaryCentres(primRecord);
		try{
			final DdeIdentifierDialog dialog = new DdeIdentifierDialog(application, secDs, secGroups);
			dialog.getContentPanel().addIdentifierListener(new IdentifierListener() {
                public void identifierChosen(IdentifierEvent event) {
                	dialog.dispose();
                    secondarySelected(primRecord, event.getRecord(), true);
                }
            });
			dialog.setVisible(true);
		}
        catch ( IdentifierPanelException ex ){
        	ExceptionsHelper.handleFatalException(ex);
        }

	}
	
	/**
	 * Show the dialog for selecting an existing secondary record.
	 * 
	 * @param primRecord The primary record.
	 * @param secDs The secondary dataset.
	 */
	private void showExistingIdentifierDialog(final  Record  primRecord, final DataSet secDs){
		List<String> secGroups = getSecondaryCentres(primRecord);
		RecordSelectedListener listener = new RecordSelectedListener(){
			public void recordSelected(RecordSelectedEvent event) {
				existingIdentifierSelected(event.getIdentifier(), primRecord, secDs);
			}			
		};
		LinkExistingRecordChooserLoader loader = 
			new LinkExistingRecordChooserLoader(application, secGroups, secDs, listener);
		SwingWorkerExecutor.getInstance().execute(loader);
	}
	
	/**
	 * Get the list of allowed secondary centres, one of which the 
	 * secondary record must be a member of.
	 * 
	 * @param primRecord The primary record.
	 * @return List of allowed centres for the secondary record.
	 */
    private List<String> getSecondaryCentres(Record  primRecord){
		DataSet primDs = primRecord.getDataSet();
		String primGroup = primRecord.getIdentifier().getGroupPrefix();
		List<String> secGroups = null;
		for ( int i=0, c=primDs.numGroups(); i<c; i++ ){
			Group g = primDs.getGroup(i);
			if ( g.getName().equals(primGroup) ){
				secGroups = g.getSecondaryGroups();
				break;
			}
		}
		return secGroups;
    }

    /**
     * For an existing secondary record, takes the selected identifier
     * and finds or constructs the equivalent record.
     * 
     * @param identifier The selected identifier of the secondary.
     * @param primRecord The primary record.
     * @param secDs The secondary dataset.
     */
    private void existingIdentifierSelected(String identifier, Record  primRecord, DataSet secDs){

    	Record  secRecord = null;
    	
        PersistenceManager pManager = PersistenceManager.getInstance();
        List<RecordsList.Item> items = null;
        try {
        	items = pManager.getRecordsList().getItems();
        } catch (IOException e) {
        	ExceptionsHelper.handleIOException(application, e, false);
        	return;
        }
        // Should never happen
        catch (DecryptionException e) {
        	ExceptionsHelper.handleFatalException(e);
        }
        
        for (RecordsList.Item item : items) {
        	if (item.getIdentifier().getIdentifier().equals(identifier)) {
        		try {
        			if (!item.isReadyToCommit()) {
            			// We are looking to attach to a IRecord that has documents that
            			// are ready to be committed.
        				continue;
        			}
        			secRecord = pManager.loadRecord(item);
        		} catch (IOException e) {
        			ExceptionsHelper.handleIOException(application, e, false);
        			return;
        		}
        		// Should never happen
        		catch (DecryptionException e) {
        			ExceptionsHelper.handleFatalException(e);
        			return;
        		}
        	}
        }

        if ( null == secRecord ){
	        //Secondary record does not exist locally, so construct a new empty
        	//record with the selected identifier
        	secRecord = secDs.generateInstance();
        	try {
        		secRecord.generateIdentifier(identifier);
        	} catch (InvalidIdentifierException ex) {
        		//Should not be possible to reach this point
        		ExceptionsHelper.handleFatalException(ex);
        	}
	        pManager.getConsentMap().addConsentFromMapToRecord(secRecord);
        }
                
        secondarySelected(primRecord, secRecord, false);
        
    }
    
}
