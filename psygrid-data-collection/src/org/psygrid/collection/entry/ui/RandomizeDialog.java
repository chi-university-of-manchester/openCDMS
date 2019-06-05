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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.util.WindowUtils;
import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.EntryMessages;
import org.psygrid.collection.entry.ExceptionsHelper;
import org.psygrid.collection.entry.SwingWorkerExecutor;
import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.collection.entry.remote.ESLSubjectNotFoundFault;
import org.psygrid.collection.entry.remote.RemoteManager;
import org.psygrid.collection.entry.remote.RemoteServiceFault;
import org.psygrid.collection.entry.security.EntrySAMLException;
import org.psygrid.data.model.hibernate.Identifier;
import org.psygrid.data.model.hibernate.Record;
import org.psygrid.data.utils.security.NotAuthorisedFault;
import org.psygrid.esl.model.ISubject;
import org.psygrid.esl.services.RandomisationException;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;

public class RandomizeDialog extends ApplicationDialog {

	private static final long serialVersionUID = -5090992673643068984L;

	private final Record record;

	private RandomizeOutcome result = RandomizeOutcome.FAILURE;

	private ButtonClicked button = ButtonClicked.CANCEL;

	private DefaultFormBuilder builder;

	private JButton yesButton;

	private JButton noButton;

	private JButton cancelButton;
	
	private final boolean showRandomisationTreatment;
	
	private final boolean useMedsService;
	
	private enum TreatmentRetrievalFailReason{
		NOT_APPLICABLE,
		NONE,
		NOT_AUTHORISED,
		OTHER
	};

	public RandomizeDialog(Application application, Record record, boolean showRandomisationTreatment, boolean useMedsService) {
		super(application, Messages.getString("RandomizeDialog.dialogTitle"), true);
		this.record = record;
		this.showRandomisationTreatment = showRandomisationTreatment;
		this.useMedsService = useMedsService;
		initBuilder();
		initComponents();
		pack();
		setLocation(WindowUtils.getPointForCentering(this));
	}

	public RandomizeOutcome getResult() {
		return result;
	}

	public ButtonClicked getButton() {
		return button;
	}

	private void initBuilder() {
		builder = new DefaultFormBuilder(new FormLayout("default"),  //$NON-NLS-1$
				new JPanel());
		builder.setDefaultDialogBorder();

	}

	private void initComponents() {

		builder.append(
				new JLabel(
						Messages.getString("RandomizeDialog.randomizeSubjectQuestion_p1")+record.getIdentifier().getIdentifier()+Messages.getString("RandomizeDialog.randomizeSubjectQuestion_p2")));
		builder.appendUnrelatedComponentsGapRow();
		builder.nextLine(2);
		builder.append(
				new JLabel(
						Messages.getString("RandomizeDialog.ifSatisfiesInclusionCriteriaMessage")));
		builder.append(
				new JLabel(
						Messages.getString("RandomizeDialog.ifNotSatisfiesInclusionCriteriaMessage")));
		builder.append(
				new JLabel(
						Messages.getString("RandomizeDialog.cancelRandomizationMessage")));

		yesButton = new JButton(EntryMessages.getString("Entry.yes")); //$NON-NLS-1$
		yesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				button = ButtonClicked.YES;
				handleYes();
			}
		});

		noButton = new JButton(EntryMessages.getString("Entry.no")); //$NON-NLS-1$
		noButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				button = ButtonClicked.NO;
				dispose();
			}
		});

		cancelButton = new JButton(EntryMessages.getString("Entry.cancel")); //$NON-NLS-1$
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				button = ButtonClicked.CANCEL;
				dispose();
			}
		});

		JPanel buttonsPanel = ButtonBarFactory.buildOKCancelHelpBar(yesButton, noButton, cancelButton);
		builder.appendUnrelatedComponentsGapRow();
		builder.nextLine(2);
		builder.append(buttonsPanel);

		getContentPane().add(builder.getPanel());
	}

	private void showSuccessDialog(boolean showRandomisationTreatment, String randomisationTreatment, TreatmentRetrievalFailReason reason) {
		result = RandomizeOutcome.SUCCESS;
		String title = Messages.getString("RandomizeDialog.randomizeSuccessTitle");
		
		String message = null;
		
		if(!showRandomisationTreatment){
			message = Messages.getString("RandomizeDialog.randomizeSuccessMessage");
		}else if(reason == TreatmentRetrievalFailReason.NONE){
			message = Messages.getString("RandomizeDialog.treatmentMessage");
			message = message.replace("%", randomisationTreatment);
		}else if(reason == TreatmentRetrievalFailReason.NOT_AUTHORISED){
			message = Messages.getString("RandomizeDialog.randomizeSuccessButNotAuthorizedForTreatmentInfoMessage");
		}else if(reason == TreatmentRetrievalFailReason.OTHER){
			message = Messages.getString("RandomizeDialog.randomizeSuccessButNoTreatmentInfoMessage");
		}
		JOptionPane.showMessageDialog(getParent(), message, title,
				JOptionPane.INFORMATION_MESSAGE);
	}
	
	private void handleYes(){
		//in preview, show success dialog and then exit randomization dialog
		if (RemoteManager.getInstance().isTestDataset()) {
			showSuccessDialog(showRandomisationTreatment, "Sample Treatment", TreatmentRetrievalFailReason.NOT_APPLICABLE);
			dispose();
		} else {
			//disable buttons - prevent multiple clicks
			yesButton.setEnabled(false);
			noButton.setEnabled(false);
			cancelButton.setEnabled(false);
			SwingWorker<ISubject, Object> worker = new GetSubjectWorker();
			setWait(true);
			SwingWorkerExecutor.getInstance().execute(worker);
		}
	}

	public enum ButtonClicked{
		YES,
		NO,
		CANCEL
	}

	public enum RandomizeOutcome{
		SUCCESS,
		FAILURE,
		OFFLINE,
		CANCELLED
	}

	private class GetSubjectWorker extends SwingWorker<ISubject, Object> {
		@Override
		protected ISubject doInBackground() throws ConnectException,
		SocketTimeoutException, NotAuthorisedFault, IOException, RemoteServiceFault,
		EntrySAMLException, ESLSubjectNotFoundFault {
			return RemoteManager.getInstance().eslRetrieveSubject(record);
		}

		@Override
		protected void done() {
			try {            	
				ISubject subject = get();
				EslPreRandomDialog dlg = new EslPreRandomDialog(
						getParent(), record, subject);
				dlg.setVisible(true);
				if ( dlg.isSubjectOK() ){
					SwingWorker<Object, Object> worker = new RandomizeSubjectWorker();
					SwingWorkerExecutor.getInstance().execute(worker);
				}
				else{
					result = RandomizeOutcome.CANCELLED;
					dispose();
					setWait(false);
				}

			} catch (InterruptedException e) {
				setWait(false);
				ExceptionsHelper.handleInterruptedException(e);
			} catch (ExecutionException e) {
				dispose();
				setWait(false);
				Throwable cause = e.getCause();
				if (cause instanceof ConnectException) {
					result = RandomizeOutcome.OFFLINE;
				} else if (cause instanceof SocketTimeoutException) {
					result = RandomizeOutcome.OFFLINE;
				} else if (cause instanceof SocketException) {
					result = RandomizeOutcome.OFFLINE;
				} else if (cause instanceof IOException) {
					ExceptionsHelper.handleIOException(
							RandomizeDialog.this,
							(IOException) cause, false);
				} else if (cause instanceof NotAuthorisedFault) {
					ExceptionsHelper.handleNotAuthorisedFault(
							RandomizeDialog.this,
							(NotAuthorisedFault) cause);
				}else if (cause instanceof ESLSubjectNotFoundFault) {
					String title = Messages.getString("RandomizeDialog.RandomizationFailedTitle");
					String message = Messages.getString("RandomizeDialog.RandomizationFailedMessage");
					ExceptionsHelper.handleException(RandomizeDialog.this, 
							title, cause, message, false);
				} 
				else if (cause instanceof RemoteServiceFault) {
					ExceptionsHelper.handleRemoteServiceFault(
							RandomizeDialog.this,
							(RemoteServiceFault) cause);
				} else if (cause instanceof EntrySAMLException) {
					ExceptionsHelper.handleEntrySAMLException(
							RandomizeDialog.this,
							(EntrySAMLException) cause);
				} else if (cause instanceof RandomisationException) {
					ExceptionsHelper.handleEslRandomisationException(
							RandomizeDialog.this,
							(RandomisationException) cause,
							record.getIdentifier().getIdentifier());
				} else {
					//Set button clicked to null so that the "Randomization failed" dialog is
					//not shown momentarily as the app is closing
					button = null;
					ExceptionsHelper.handleException(
							RandomizeDialog.this,
							Messages.getString("RandomizeDialog.generalRandomizeErrorTitle"),
							cause,
							Messages.getString("RandomizeDialog.generalRandomizeErrorMessage")+
							EntryMessages.getString("DefaultExceptionHandler.message")+
							PersistenceManager.getInstance().getBaseDirLocation() +
							EntryMessages.getString("DefaultExceptionHandler.message2"),
							true
					);
					//exit - note that we save the state of the application, which means
					//that the completed document is not lost.
					getParent().exitWithoutConfirmation(true);
				}
			}
		}		
	}

	private class RandomizeSubjectWorker extends SwingWorker<Object, Object> {
		@Override
		protected Object doInBackground() throws ConnectException,
		NotAuthorisedFault, IOException, RemoteServiceFault,
		EntrySAMLException, RandomisationException, SocketTimeoutException, SocketException {
			RemoteManager.getInstance().eslRandomiseSubject(record);
			return null;
		}

		@Override
		protected void done() {
			dispose();
			setWait(false);
			try {
				get();
				String randomisationTreatment = null;
				TreatmentRetrievalFailReason failReason = TreatmentRetrievalFailReason.NONE;
				if(showRandomisationTreatment){
					try {
						randomisationTreatment = RemoteManager.getInstance().eslRetrieveRandomisationResult(record);
					} catch (NotAuthorisedFault e) {
						//Raise dialog to notify that user doesn't have the required privilege level to obtain info about the randomisation result.
						//Tell the user that they need to be given 'Treatment Administrator' status first.
						failReason = TreatmentRetrievalFailReason.NOT_AUTHORISED;
					}  catch(Exception e){
						//Need to just say that the randomisation treatment couldn't be retrieved at this time. And to try later from the main menu option.
						failReason = TreatmentRetrievalFailReason.OTHER;
					}
				}
				showSuccessDialog(showRandomisationTreatment, randomisationTreatment, failReason);
				
				
				if(useMedsService){
					try{
						try {
							
							Identifier identifier = record.getIdentifier();
							String centreCode = identifier.getGroupPrefix();
							String idString = identifier.getIdentifier();
							String projectCode = identifier.getProjectPrefix();
							
							//TreatmentRetrievalFailReason failReason = TreatmentRetrievalFailReason.NONE;
							String medsPackage = RemoteManager.getInstance().allocateMedsPackage(idString, projectCode, centreCode);
							showSuccessDialog(true, medsPackage, failReason);
						} catch (ConnectException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (RemoteServiceFault e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (NotAuthorisedFault e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (EntrySAMLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}finally{
						
					}
				}
				
				
			} catch (InterruptedException e) {
				ExceptionsHelper.handleInterruptedException(e);
			} catch (ExecutionException e) {
				Throwable cause = e.getCause();
				if (cause instanceof ConnectException) {
					result = RandomizeOutcome.OFFLINE;
				} else if (cause instanceof SocketTimeoutException) {
					result = RandomizeOutcome.OFFLINE;
				} else if (cause instanceof SocketException) {
					result = RandomizeOutcome.OFFLINE;
				} else if (cause instanceof IOException) {
					ExceptionsHelper.handleIOException(
							RandomizeDialog.this,
							(IOException) cause, false);
				} else if (cause instanceof NotAuthorisedFault) {
					ExceptionsHelper.handleNotAuthorisedFault(
							RandomizeDialog.this,
							(NotAuthorisedFault) cause);
				} else if (cause instanceof RemoteServiceFault) {
					ExceptionsHelper.handleRemoteServiceFault(
							RandomizeDialog.this,
							(RemoteServiceFault) cause);
				} else if (cause instanceof EntrySAMLException) {
					ExceptionsHelper.handleEntrySAMLException(
							RandomizeDialog.this,
							(EntrySAMLException) cause);
				} else if (cause instanceof RandomisationException) {
					ExceptionsHelper.handleEslRandomisationException(
							RandomizeDialog.this,
							(RandomisationException) cause,
							record.getIdentifier().getIdentifier());
				} else {
					//Set button clicked to null so that the "Randomization failed" dialog is
					//not shown momentarily as the app is closing
					button = null;
					ExceptionsHelper.handleException(
							RandomizeDialog.this,
							Messages.getString("RandomizeDialog.generalRandomizeErrorTitle"),
							cause,
							Messages.getString("RandomizeDialog.generalRandomizeErrorMessage")+
							EntryMessages.getString("DefaultExceptionHandler.message")+
							PersistenceManager.getInstance().getBaseDirLocation() +
							EntryMessages.getString("DefaultExceptionHandler.message2"),
							true
					);
					//exit - note that we save the state of the application, which means
					//that the completed document is not lost.
					getParent().exitWithoutConfirmation(true);
				}
			}
		}
	}

}
