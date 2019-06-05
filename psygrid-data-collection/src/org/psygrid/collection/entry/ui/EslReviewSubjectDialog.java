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
import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.collection.entry.remote.RemoteManager;
import org.psygrid.collection.entry.remote.RemoteServiceFault;
import org.psygrid.collection.entry.security.EntrySAMLException;
import org.psygrid.common.identifier.InvalidIdentifierException;
import org.psygrid.common.ui.WrappedJOptionPane;
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
public class EslReviewSubjectDialog extends EslDialog {

	private static final long serialVersionUID = -4429430979821383915L;

	private boolean subjectOK = false;
	private boolean subjectEdit = false;
	
	private ISubject subject;
	
	public EslReviewSubjectDialog(Application application, Record record, ISubject subject) {
		super(application, record, subject);
		this.subject = subject;
	}

	public boolean isSubjectOK() {
		return subjectOK;
	}

	public void setSubjectEdit(boolean isEdit) {
		subjectEdit = isEdit;
	}
	
	public boolean isSubjectEdit() {
		return subjectEdit;
	}
	
	public Record getRecord() {
		return record;
	}
	
	public ISubject getSubject() {
		return subject;
	}
	
	@Override
	public void initListeners() {
        contentPanel.addEslListener(new EslListener() {
            public void eslCompleted(EslEvent event) {
                if ( null == event.getEslSubject() ){
                    subjectOK = false;
                    dispose();
                }
                else{
                    subjectOK = true;
                    if ( event.isSaveRequired() ){
                    	saveEslSubject(event.getEslSubject());
                    }
                    else{
                    	dispose();
                    }
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
					setWait(false);
					get();
					String title = "Participant Updated";
					String message = "Participant was successfully updated in the participant register.";
					WrappedJOptionPane.showWrappedMessageDialog(EslReviewSubjectDialog.this, message, title, JOptionPane.INFORMATION_MESSAGE);
				} catch (InterruptedException e) {
					ExceptionsHelper.handleInterruptedException(e);
				} catch (ExecutionException e) {
					//set this to false so if attempt to update subject fails
					//then randomization is cancelled
					subjectOK = false;
					Throwable cause = e.getCause();
					if (cause instanceof ConnectException) {
						ExceptionsHelper.handleConnectException(
								EslReviewSubjectDialog.this,
								(ConnectException) cause);
					} else if (cause instanceof IOException) {
						ExceptionsHelper.handleIOException(
								EslReviewSubjectDialog.this,
								(IOException) cause, false);
					} else if (cause instanceof NotAuthorisedFault) {
						ExceptionsHelper.handleNotAuthorisedFault(
								EslReviewSubjectDialog.this,
								(NotAuthorisedFault) cause);
					} else if (cause instanceof RemoteServiceFault) {
						ExceptionsHelper.handleRemoteServiceFault(
								EslReviewSubjectDialog.this,
								(RemoteServiceFault) cause);
					} else if (cause instanceof EntrySAMLException) {
						ExceptionsHelper.handleEntrySAMLException(
								EslReviewSubjectDialog.this,
								(EntrySAMLException) cause);
					} else if (cause instanceof ESLDuplicateObjectFault) {
						ExceptionsHelper.handleEslDuplicateObjectFault(
								EslReviewSubjectDialog.this,
								(ESLDuplicateObjectFault) cause,
								eslSubject.getStudyNumber());
					} else if (cause instanceof ESLOutOfDateFault) {
						ExceptionsHelper.handleEslOutOfDateFault(
								EslReviewSubjectDialog.this,
								(ESLOutOfDateFault) cause,
								eslSubject.getStudyNumber());
					} else if (cause instanceof InvalidIdentifierException) {
						ExceptionsHelper.handleException(
								EslReviewSubjectDialog.this,
								"Update Failed",
								cause,
								"Unable to update the participant in the participant register.",
								true
						);
					} else if (cause instanceof ESLSubjectExistsException) {
                    	//The subject's unique details are already in the ESL
                    	//The cause message should explain what details are causing the error.
						 ExceptionsHelper.handleEslSubjectExistsException(
	                        		EslReviewSubjectDialog.this,
	                                (ESLSubjectExistsException) cause
	                                );
                    } else {
						ExceptionsHelper.handleException(
								EslReviewSubjectDialog.this,
								"Update Failed",
								cause,
								"Unable to update participant in the participant register.\n\n"+
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
		return new EslViewEditPanel(record, subject);
	}

}
