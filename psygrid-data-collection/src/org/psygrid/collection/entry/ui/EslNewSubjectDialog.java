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


package org.psygrid.collection.entry.ui;

import java.io.IOException;
import java.net.ConnectException;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;

import org.jdesktop.swingworker.SwingWorker;
import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.EntryMessages;
import org.psygrid.collection.entry.ExceptionsHelper;
import org.psygrid.collection.entry.SwingWorkerExecutor;
import org.psygrid.collection.entry.event.EslEvent;
import org.psygrid.collection.entry.event.EslListener;
import org.psygrid.collection.entry.persistence.EslSubjectList;
import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.collection.entry.remote.RemoteManager;
import org.psygrid.collection.entry.remote.RemoteServiceFault;
import org.psygrid.collection.entry.security.EntrySAMLException;
import org.psygrid.common.identifier.InvalidIdentifierException;
import org.psygrid.data.model.hibernate.Record;
import org.psygrid.data.utils.security.NotAuthorisedFault;
import org.psygrid.esl.model.ISubject;
import org.psygrid.esl.services.ESLDuplicateObjectFault;
import org.psygrid.esl.services.ESLOutOfDateFault;
import org.psygrid.esl.services.ESLSubjectExistsException;

/**
 * @author Rob Harper
 *
 */
public class EslNewSubjectDialog extends EslDialog {

    private static final long serialVersionUID = 1L;
    
    private boolean eslSaveSuccessful;
    
	public EslNewSubjectDialog(Application parent, Record record) {
		super(parent, record);
	}

    public boolean isEslSaveSuccessful() {
        return eslSaveSuccessful;
    }
    
	@Override
	public void initListeners() {
        contentPanel.addEslListener(new EslListener() {
            public void eslCompleted(EslEvent event) {
                if ( null == event.getEslSubject() ){
                    dispose();
                }
                else{
                    saveEslSubject(event.getEslSubject());
                }
            }
        });
	}

    private void saveEslSubject(final ISubject eslSubject){
        SwingWorker<Object, Object> worker = new SwingWorker<Object, Object>() {
            @Override
            protected Object doInBackground() throws ConnectException,
                    NotAuthorisedFault, IOException, RemoteServiceFault, EntrySAMLException, 
                    ESLOutOfDateFault, ESLDuplicateObjectFault, ESLSubjectExistsException, InvalidIdentifierException {
            	
            	if (RemoteManager.getInstance().isTestDataset()) {
            		return null;
            	}
            	
                RemoteManager.getInstance().eslSaveSubject(eslSubject);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    setWait(false);
                    eslSaveSuccessful = true;
                    String title = Messages.getString("EslNewSubjectDialog.successTitle");
                    String message = Messages.getString("EslNewSubjectDialog.successMessage");
                    JOptionPane.showMessageDialog(EslNewSubjectDialog.this, message, title,
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (InterruptedException e) {
                    setWait(false);
                    ExceptionsHelper.handleInterruptedException(e);
                } catch (ExecutionException e) {
                    Throwable cause = e.getCause();

                    /*
                     * In case of a ConnectException, we call setWait(false)
                     * after attempting to save the EslSubjectsList locally
                     */
                    if (!(cause instanceof ConnectException)) {
                        setWait(false);
                    }
                    
                    if (cause instanceof ConnectException) {
                        //assume that a connect exception is because the user is currently
                        //offline. So we persist the Subject locally then try to save it to
                        //the ESL database the next time they are online.
                        PersistenceManager pManager = PersistenceManager.getInstance();
                        synchronized (pManager) {
                            try{

                                String title = Messages.getString("EslNewSubjectDialog.successTitle");
                                String message = Messages.getString("EslNewSubjectDialog.noNetworkConnectionMessage");

                            	//connect excpetion will also be throw in DSD preview mode
                                if (RemoteManager.getInstance().isTestDataset()) {
                                    eslSaveSuccessful = true;
                                    setWait(false);
                                    JOptionPane.showMessageDialog(EslNewSubjectDialog.this, message, title,
                                            JOptionPane.INFORMATION_MESSAGE);
                            	} else {
                                    EslSubjectList subjects = pManager.getEslSubjectsList();
                                    subjects.addSubject(eslSubject);
                                    pManager.saveEslSubjectsList();
                                    eslSaveSuccessful = true;
                                    setWait(false);
                                    JOptionPane.showMessageDialog(EslNewSubjectDialog.this, message, title,
                                            JOptionPane.INFORMATION_MESSAGE);
                            	}
                            }
                            catch(IOException ex){
                                setWait(false);
                                ExceptionsHelper.handleIOException(EslNewSubjectDialog.this, ex, true);
                            }
                            catch (Exception ex) {
                                setWait(false);
                                ExceptionsHelper.handleFatalException(ex);
                            }
                        }
                    } else if (cause instanceof IOException) {
                        ExceptionsHelper.handleIOException(
                                EslNewSubjectDialog.this,
                                (IOException) cause, false);
                    } else if (cause instanceof NotAuthorisedFault) {
                        ExceptionsHelper.handleNotAuthorisedFault(
                        		EslNewSubjectDialog.this,
                                (NotAuthorisedFault) cause);
                    } else if (cause instanceof RemoteServiceFault) {
                        ExceptionsHelper.handleRemoteServiceFault(
                        		EslNewSubjectDialog.this,
                                (RemoteServiceFault) cause);
                    } else if (cause instanceof EntrySAMLException) {
                        ExceptionsHelper.handleEntrySAMLException(
                        		EslNewSubjectDialog.this,
                                (EntrySAMLException) cause);
                    } else if (cause instanceof ESLDuplicateObjectFault) {
                        ExceptionsHelper.handleEslDuplicateObjectFault(
                        		EslNewSubjectDialog.this,
                                (ESLDuplicateObjectFault) cause,
                                eslSubject.getStudyNumber());
                    } else if (cause instanceof ESLOutOfDateFault) {
                        ExceptionsHelper.handleEslOutOfDateFault(
                        		EslNewSubjectDialog.this,
                                (ESLOutOfDateFault) cause,
                                eslSubject.getStudyNumber());
                    } else if (cause instanceof InvalidIdentifierException) {
                        ExceptionsHelper.handleException(
                        		EslNewSubjectDialog.this,
                                Messages.getString("EslNewSubjectDialog.invalidGroupTitle"),
                                cause,
                                Messages.getString("EslNewSubjectDialog.invalidGroupMessage"),
                                true
                                );
                    } else if (cause instanceof ESLSubjectExistsException) {
                    	//The subject's unique details are already in the ESL
                    	//The cause message should explain what details are causing the error.
                       ExceptionsHelper.handleEslSubjectExistsException(
                        		EslNewSubjectDialog.this,
                                (ESLSubjectExistsException) cause
                                );
                    } else {
                        ExceptionsHelper.handleException(
                        		EslNewSubjectDialog.this,
                                Messages.getString("EslNewSubjectDialog.generalErrorTitle"),
                                cause,
                                Messages.getString("EslNewSubjectDialog.generalErrorMessage")+
                                EntryMessages.getString("DefaultExceptionHandler.message")+
                                PersistenceManager.getInstance().getBaseDirLocation() +
                                EntryMessages.getString("DefaultExceptionHandler.message2"),
                                true);
                        application.exitWithoutConfirmation(false);
                    }
                }
                finally{
                    dispose();
                }
            }
        };
        setWait(true);
        SwingWorkerExecutor.getInstance().execute(worker);
    }

	@Override
	public EslPanel createContentPanel(ISubject subject) {
		return new EslPanel(record, subject);
	}
    
}
