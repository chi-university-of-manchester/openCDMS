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


package org.psygrid.collection.entry.remote;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.auth.LoginAdapter;
import org.jdesktop.swingx.auth.LoginEvent;
import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.EntryMessages;
import org.psygrid.collection.entry.ExceptionsHelper;
import org.psygrid.collection.entry.Selectable;
import org.psygrid.collection.entry.SwingWorkerExecutor;
import org.psygrid.collection.entry.persistence.EslSubjectList;
import org.psygrid.collection.entry.persistence.ExternalIdGetter;
import org.psygrid.collection.entry.persistence.NoExternalIdMappingException;
import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.collection.entry.persistence.RecordsList;
import org.psygrid.collection.entry.persistence.RecordsListWrapper;
import org.psygrid.collection.entry.security.DecryptionException;
import org.psygrid.collection.entry.security.EntryLoginService;
import org.psygrid.collection.entry.security.EntrySAMLException;
import org.psygrid.collection.entry.security.SecurityManager;
import org.psygrid.collection.entry.ui.CommitDialog;
import org.psygrid.collection.entry.ui.CommitTableModel;
import org.psygrid.collection.entry.ui.PsygridLoginDialog;
import org.psygrid.collection.entry.ui.PsygridLoginPanel;
import org.psygrid.collection.entry.ui.RecordDialog;
import org.psygrid.collection.entry.util.ResetWaitRunnable;
import org.psygrid.collection.entry.util.WaitRunnable;
import org.psygrid.common.identifier.InvalidIdentifierException;
import org.psygrid.common.security.LoginInterfaceFrame;
import org.psygrid.data.utils.security.NotAuthorisedFault;
import org.psygrid.esl.model.ISubject;
import org.psygrid.esl.services.ESLDuplicateObjectFault;
import org.psygrid.esl.services.ESLOutOfDateFault;
import org.psygrid.esl.services.ESLSubjectExistsException;

public class RemoteCommitAction extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private static final Log LOG = LogFactory.getLog(RemoteCommitAction.class);
    
    //The following allows us to do conditional compilation of whether reauthentication
    //is required on commit or not
    
    //[ifdef]
    private static final boolean REAUTHENTICATE = false;
    //[enddef]
    /* //[enddef]
    private static final boolean REAUTHENTICATE = true;
    //[ifdef] */
    
    private LoginInterfaceFrame application;
    public RemoteCommitAction(LoginInterfaceFrame application) {
        super(Messages.getString("RemoteCommitAction.commit")); //$NON-NLS-1$
        this.application = application;
    }
    
    protected void finished() {
        if (application instanceof Application)
            ((Application) application).refreshRecordView();
    }

    protected void cancelled() {
    	//empty implementation
    }
    
    public void actionPerformed(ActionEvent event) {
    	if (!RemoteManager.getInstance().isConnectionAvailable()) {
            String title = EntryMessages.getString("noConnectionTitle"); //$NON-NLS-1$
            String message = EntryMessages.getString("noConnectionMessage"); //$NON-NLS-1$
            JOptionPane.showMessageDialog(application, message, title, 
                    JOptionPane.ERROR_MESSAGE);
            finished();
            return;
        }
        
        if ( REAUTHENTICATE ){
	        //force re-authentication before commit
			EntryLoginService service = new EntryLoginService(false, application);
			service.addLoginListener(new LoginAdapter() {
				@Override
				public void loginFailed(LoginEvent source) {
					if (LOG.isInfoEnabled()) {
						LOG.info("Login failed", source.getCause()); //$NON-NLS-1$
					}
				}
			});
	        PsygridLoginDialog dlg = new PsygridLoginDialog(application, service, null, SecurityManager.getInstance().getUserName());
	        dlg.setVisible(true);
	        PsygridLoginPanel.Status status = dlg.getStatus();
	
	        if (status == PsygridLoginPanel.Status.SUCCEEDED)
	        	doCommit();
	        else
	            finished();
        }
        else{
        	doCommit();
        }
    }
        
    private void doCommit(){
        if (shouldUploadEslSubjects()) {
            new WaitRunnable(application).run();
            SwingWorker<?, ?> worker = new SwingWorker<Map<ISubject, Exception>, Object>()    {
                @Override
                protected Map<ISubject, Exception> doInBackground() throws Exception {
                    return uploadEslSubjects();
                }
                @Override
                protected void done() {
                    new ResetWaitRunnable(application).run();
                    try {
                        Map<ISubject, Exception> exceptionMap = get();
                        if (exceptionMap.size() != 0) {
                            handleExceptions(exceptionMap);
                            return;
                        }
                        String title = Messages.getString("RemoteCommitAction.successTitle"); //$NON-NLS-1$
                        String message = Messages.getString("RemoteCommitAction.successfulESLUploadMessage"); //$NON-NLS-1$
                        JOptionPane.showMessageDialog(application, message, title,
                                JOptionPane.INFORMATION_MESSAGE);
                        commit();
                    } catch (InterruptedException e) {
                        ExceptionsHelper.handleInterruptedException(e);
                        finished();
                    } catch (ExecutionException e) {
                        Throwable cause = e.getCause();
                        if (cause instanceof ConnectException) {
                            ExceptionsHelper.handleConnectException(application,
                                    (ConnectException) cause);
                        }
                        else if (cause instanceof EntrySAMLException) {
                            ExceptionsHelper.handleEntrySAMLException(application,
                                    (EntrySAMLException) cause);
                        }
                        else if (cause instanceof IOException) {
                            ExceptionsHelper.handleIOException(application,
                                    (IOException) cause, false);
                        } 
                        else {
                            ExceptionsHelper.handleFatalException(cause);
                        }
                        finished();
                    }
                }
            };
            SwingWorkerExecutor.getInstance().execute(worker);
        }
        else {
            commit();
        }
    }
    
    private boolean shouldUploadEslSubjects()   {
        PersistenceManager pManager = PersistenceManager.getInstance();
        // see if there are any ESL subjects to commit
        EslSubjectList eslSubjects = null;
        synchronized(pManager){
            try{
                eslSubjects = pManager.getEslSubjectsList();
            }
            catch (IOException e) {
                ExceptionsHelper.handleIOException(application, e, false);
            }
            // Should never happen
            catch (DecryptionException e) {
                ExceptionsHelper.handleFatalException(e);
            }
        }
        if ( eslSubjects.getSubjects().size()>0 && 
                RemoteManager.getInstance().isEslConnectionAvailable() ){
                // show user a dialog to let them know what is going on
                JOptionPane.showMessageDialog(application,
                        Messages.getString("RemoteCommitAction.eslSubjectsToUploadMessage"), Messages.getString("RemoteCommitAction.eslSubjectsToUploadTitle"), JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
        return false;
    }

    private void commit() {
        RecordsList recordsList = null;
        RecordsListWrapper recordsListWrapper = new RecordsListWrapper();
        PersistenceManager pManager = PersistenceManager.getInstance();
        synchronized (pManager) {    
            try {
                recordsList = pManager.getRecordsList();
                boolean useExternalIdAsPrimary = false;
                	
            	recordsListWrapper = new RecordsListWrapper();
            	
            	for(RecordsList.Item item : recordsList.getItems()){
            		
            		String projectPrefix = item.getIdentifier().getProjectPrefix();
            		useExternalIdAsPrimary = PersistenceManager.getInstance().getData().getDataSetSummary(projectPrefix).getUseExternalIdAsPrimary();
            		
            		String  idForDisplay = item.getIdentifier().getIdentifier();
            		
            		if(useExternalIdAsPrimary){
            			try {
							idForDisplay = ExternalIdGetter.get(item.getIdentifier().getIdentifier());
						} catch (NoExternalIdMappingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
            		}
            		recordsListWrapper.addItem(item.getIdentifier(), item.isReadyToCommit(), idForDisplay);
            	}
                	
                	
                
                if (recordsList.getSize() == 0) {
                    String title = Messages.getString("RemoteCommitAction.noRecordsToCommitTitle");
                    String message = Messages.getString("RemoteCommitAction.noRecordsToCommitMessage");
                    JOptionPane.showMessageDialog(application, message, title,
                            JOptionPane.ERROR_MESSAGE);
                    finished();
                    return;
                }
            } catch (IOException e) {
                ExceptionsHelper.handleIOException(application, e, false);
                finished();
            }
            // Should never happen
            catch (DecryptionException e) {
                ExceptionsHelper.handleFatalException(e);
                finished();
            }
        }
        
        boolean docIsOpen = ((Application)application).getModel().getCurrentDocOccurrenceInstance() == null ?
        		false : true;
        
        if(docIsOpen){
        	String title = Messages.getString("RemoteCommitAction.documentsStillOpenTitle");
        	String message = Messages.getString("RemoteCommitAction.documentsStillOpenMessage");
        	JOptionPane.showMessageDialog(application, message, title, 
        			JOptionPane.INFORMATION_MESSAGE);
        	finished();
        	return;
        }
        
        CommitDialog dialog = new CommitDialog(null, recordsListWrapper, false);
        addActionListener(dialog);
        dialog.setVisible(true);
        if ( dialog.isCancelled() ){
        	cancelled();
        }
    }

    private void addActionListener(CommitDialog dialog) {
        dialog.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent event) {
               JXTable table = ((RecordDialog) event.getSource()).getTable();
               CommitTableModel model = (CommitTableModel) table.getModel();
               List<Selectable<RecordsListWrapper.Item>> items = model.getSelectedItems();
               handleActionPerformed(items);
            }
        });
        
    }
    
    private void handleActionPerformed(List<Selectable<RecordsListWrapper.Item>> items) {
        List<RecordsListWrapper.Item> itemsToCommit = new ArrayList<RecordsListWrapper.Item>();
        for (Selectable<RecordsListWrapper.Item> item : items) {
            itemsToCommit.add(item.getObject());
        }
        commit(itemsToCommit);
    } 

    private void commit(List<RecordsListWrapper.Item> itemsToCommit) {
        RemoteCommitWorker worker = new RemoteCommitWorker(application,
                itemsToCommit) {
            @Override
            protected void done() {
                super.done();
                finished();
            }
        };
        SwingWorkerExecutor.getInstance().execute(worker);
    }
    
    private Map<ISubject, Exception> uploadEslSubjects() throws ConnectException,
            IOException, DecryptionException, EntrySAMLException {
        PersistenceManager pManager = PersistenceManager.getInstance();
        Map<ISubject, Exception> exceptionMap = new HashMap<ISubject, Exception>();
        synchronized (pManager) {
            RemoteManager rManager = RemoteManager.getInstance();
            EslSubjectList subjectsList = pManager.getEslSubjectsList();
            List<ISubject> uploadedSubjects = new ArrayList<ISubject>();
            uploadEslSubjects(exceptionMap, rManager, subjectsList,
                    uploadedSubjects);
            pManager.saveEslSubjectsList();
        }
        return exceptionMap;
    }
    
    private void uploadEslSubjects(Map<ISubject, Exception> exceptionMap,
            RemoteManager rManager, EslSubjectList subjectsList,
            List<ISubject> uploadedSubjects) throws ConnectException,
                    IOException, EntrySAMLException    {
        for (ISubject subject : subjectsList.getSubjects()) {
            try {
                rManager.eslSaveSubject(subject);
                uploadedSubjects.add(subject);
            } catch (ESLSubjectExistsException ex) {
                exceptionMap.put(subject, ex);
            } catch (ESLDuplicateObjectFault ex) {
                exceptionMap.put(subject, ex);
            } catch (ESLOutOfDateFault ex) {
                exceptionMap.put(subject, ex);
            } catch (InvalidIdentifierException ex) {
                exceptionMap.put(subject, ex);
            } catch (NotAuthorisedFault ex) {
                exceptionMap.put(subject, ex);
            } catch (RemoteServiceFault ex) {
                exceptionMap.put(subject, ex);
            }
        }
        // remove uploaded subjects from the list
        for (ISubject s : uploadedSubjects) {
            subjectsList.removeSubject(s);
        }
    }

    private void handleExceptions(Map.Entry<ISubject, Exception> exceptionEntry,
            boolean singleFailure) {
        Exception exception = exceptionEntry.getValue();
        ISubject subject = exceptionEntry.getKey();
        String studyNumber = subject.getStudyNumber();
        if (exception instanceof RemoteServiceFault) {
            ExceptionsHelper.handleRemoteServiceFault(application,
                    (RemoteServiceFault) exception);
        }
        else if (exception instanceof ESLDuplicateObjectFault) {
            ExceptionsHelper.handleEslDuplicateObjectFault(application,
                    (ESLDuplicateObjectFault) exception, studyNumber);
        }
        else if (exception instanceof ESLOutOfDateFault) {
            ExceptionsHelper.handleEslOutOfDateFault(application,
                    (ESLOutOfDateFault) exception, studyNumber);
        }
        else if (exception instanceof InvalidIdentifierException) {
            handleInvalidIdentifierException(application,
                    (InvalidIdentifierException) exception, studyNumber);
        }
        else if (exception instanceof NotAuthorisedFault) {
            ExceptionsHelper.handleNotAuthorisedFault(application,
                    (NotAuthorisedFault) exception);
        }
        else if (exception instanceof RemoteServiceFault) {
            ExceptionsHelper.handleRemoteServiceFault(application,
                    (RemoteServiceFault) exception);
        }
        else {
            ExceptionsHelper.handleFatalException(exception);
        }
    }
        
    private void handleExceptions(Map<ISubject, Exception> exceptionsMap) {
        if (exceptionsMap.size() < 1) {
            throw new IllegalArgumentException("exceptionsMap must have at least one element."); //$NON-NLS-1$
        }
        //TODO At the moment, we only display one of the exceptions to the user.
        //The idea behind passing this Map is to present all the records and
        //their exceptions to the user in a nice and easy to understand manner
        handleExceptions(exceptionsMap.entrySet().iterator().next(), exceptionsMap.size() == 1);
    }
    
    private void handleInvalidIdentifierException(Component parentComponent, 
            InvalidIdentifierException e, String studyNumber) {
        String message = ""; //$NON-NLS-1$
        String title = EntryMessages.getString("invalidStudyNumberTitle"); //$NON-NLS-1$
        message += EntryMessages.getString("invalidStudyNumberMessage"); //$NON-NLS-1$
        message += ExceptionsHelper.getEslStudyNumberMessage(studyNumber);
        ExceptionsHelper.handleException(parentComponent, title, e, message, false);
    }
    
}
