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

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.collection.entry.model.SectionPresModel;
import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.collection.entry.persistence.RecordsList;
import org.psygrid.collection.entry.remote.RemoteCommitAction;
import org.psygrid.collection.entry.remote.RemoteManager;
import org.psygrid.collection.entry.remote.RemoteServiceFault;
import org.psygrid.collection.entry.remote.RemoteUpdateAction;
import org.psygrid.collection.entry.security.DecryptionException;
import org.psygrid.collection.entry.security.EntrySAMLException;
import org.psygrid.collection.entry.security.SecurityManager;
import org.psygrid.collection.entry.util.IncInteger;
import org.psygrid.collection.entry.util.RecordHelper;
import org.psygrid.common.identifier.InvalidIdentifierException;
import org.psygrid.common.security.LoginInterfaceFrame;
import org.psygrid.common.ui.WrappedJOptionPane;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.repository.DuplicateDocumentsFault;
import org.psygrid.data.repository.RepositoryInvalidIdentifierFault;
import org.psygrid.data.repository.RepositoryNoConsentFault;
import org.psygrid.data.repository.RepositoryOutOfDateFault;
import org.psygrid.data.repository.transformer.TransformerFault;
import org.psygrid.data.utils.security.NotAuthorisedFault;

public class EntryHelper {

	private static final Log LOG = LogFactory.getLog(EntryHelper.class);

	public static List<DocumentInstance> getDocumentInstances(Record record) {
		DataSet dataSet = record.getDataSet();
		List<DocumentInstance> docInstances = new ArrayList<DocumentInstance>();
		for (int i = 0, c = dataSet.numDocuments(); i < c; ++i) {
			Document doc = dataSet.getDocument(i);

			for (int j = 0, d = doc.numOccurrences(); j < d; ++j) {
				DocumentOccurrence docOcc = doc.getOccurrence(j);
				DocumentInstance docInstance = record.getDocumentInstance(docOcc);
				if (docInstance != null) {
					docInstances.add(docInstance);
				}
			}
		}
		return docInstances;
	}

	public static Status getStatus(Document document, DocumentStatus docStatus) {
		for (int i = 0, c = document.numStatus(); i < c; ++i) {
			Status status = document.getStatus(i);
			String statusName = status.getShortName();
			if (statusName.equals(docStatus.toString()))  {
				return status;
			}
		}
		return null;
	}

	/**
	 * If the user is online and has uncommitted documents, asks the user
	 * if he would like to launch the commit dialog box, exit or cancel. If the
	 * user chooses the either of the first two options, a <code>Runnable</code>
	 * that executes the correct action is returned. <code>null</code> is returned
	 * in case the user chooses the last option.
	 * 
	 * If the user is not online or does not have any uncommitted documents, 
	 * asks the user if he really wants to exit the application. A <code>Runnable</code>
	 * that exits the application is returned in case the user chooses the "Yes"
	 * option and <code>null</code> is returned otherwise.
	 * 
	 * @param application Main frame of the application.
	 * @param window <code>Window</code> to be used for positioning the 
	 * dialog boxes that ask the user for confirmation. This may be the same
	 * as <code>application</code> or not.
	 * @return a <code>Runnable</code> that will exit the application or
	 * launch a commit dialog box, or null.
	 */
	public static Runnable exit(final Application application, Window window) {
		if ( isOnline() && !RemoteManager.getInstance().isTestDataset() ) {
			if ( hasUncommittedDocs(window) ){
				String title = EntryMessages.getString("EntryHelper.uncommittedDocsTitle");
				String message = EntryMessages.getString("EntryHelper.uncommittedDocsMessage");
				String launchCommitDialog = EntryMessages.getString("EntryHelper.launchCommitDialogOption");
				String exit = EntryMessages.getString("EntryHelper.exitOption");
				String[] selectionValues = new String[2];
				selectionValues[0] = launchCommitDialog;
				selectionValues[1] = exit;
				Object result = JOptionPane.showInputDialog(window, message, title, JOptionPane.QUESTION_MESSAGE,
						null, selectionValues, launchCommitDialog);
				if (result == null) {
					return null;
				}
				if (result.equals(launchCommitDialog)) {
					Runnable r = new Runnable() {
						public void run() {
							RemoteCommitAction action = new RemoteCommitAction(application);
							action.actionPerformed(new ActionEvent(application, 
									ActionEvent.ACTION_PERFORMED, "")); //$NON-NLS-1$
						}
					};
					return r;
				}
				if (result.equals(exit)) {
					return application.getExitWithoutConfirmationRunnable(true);
				}
			}
		}
		int response = JOptionPane.showConfirmDialog(window, 
				EntryMessages.getString("Application.exitConfirmation"), //$NON-NLS-1$
				EntryMessages.getString("Application.exitTitle"), //$NON-NLS-1$
				JOptionPane.YES_NO_OPTION);
		if (response == JOptionPane.NO_OPTION) {
			return null;
		}

		return application.getExitWithoutConfirmationRunnable(true);
	}

	private static boolean isOnline() {
		return RemoteManager.getInstance().isConnectionAvailable();
	}

	public static boolean hasUncommittedDocs(Window window) {
		PersistenceManager pManager = PersistenceManager.getInstance();
		synchronized (pManager) {    
			try {
				RecordsList recordsList = pManager.getRecordsList();
				if (recordsList.getSize() == 0) {
					return false;
				}
				return true;
			} catch (IOException e) {
				ExceptionsHelper.handleIOException(window, e, false);
			}
			// Should never happen
			catch (DecryptionException e) {
				ExceptionsHelper.handleFatalException(e);
			}
		}
		return false;
	}

	public static void detachAllDocInstances(Record record) {
		List<DocumentInstance> docInstances = getDocumentInstances(record);
		for (DocumentInstance docInstance : docInstances) {
			record.detachDocumentInstance(docInstance);
		}
	}

	/**
	 * 
	 * @param application
	 * @param sameDocument {@code true} if the user is trying to re-open the
	 * same document. This shows a slightly different message.
	 * @return true if the user clicks on 'Yes'.
	 */
	public static boolean showDocumentWillBeLostDialog(Application application) {
		String title = "Document Will Be Lost";
		String message = "You currently have a document open. If you have not saved that document, it will be lost.\nAre you sure you want to proceed?";
		int result =  JOptionPane.showConfirmDialog(application, message, title, 
				JOptionPane.YES_NO_OPTION);

		if (result == JOptionPane.YES_OPTION) {
			return true;
		}
		return false;
	}

	public static void showSelectedDocumentIsLockedDialog(JFrame frame) {
		JOptionPane.showMessageDialog(
				frame, 
				"The selected document has been locked. Please select another document.",
				"Document locked",
				JOptionPane.INFORMATION_MESSAGE);
	}

	public static void showSelectedDocumentIsAlreadyOpen(JFrame frame) {
		JOptionPane.showMessageDialog(
				frame, 
				"The selected document is already open. Please select another document.",
				"Document Already Open",
				JOptionPane.INFORMATION_MESSAGE);
	}

	public static void showSelectedDocumentIsSecondary(JFrame parent, Record record) {
		JOptionPane.showMessageDialog(
				parent, 
				"The selected document is intended to be completed via automatic data propagation.\n" +
				"To do this, complete the equivalent document in the linked record '"+record.getPrimaryIdentifier()+"'",
				"Document Cannot Be Created",
				JOptionPane.INFORMATION_MESSAGE);
	}

	public static boolean isLocked(JFrame parent, DocumentGroup docGroup, Record record) {
		if (record == null) {
			return true;
		}
		String recordIdentifier = record.getIdentifier().getIdentifier();

		Status status = null;
		if ( RemoteManager.getInstance().isTestDataset() ){
			//Running in test/preview mode - get the status from the record, as there is no
			//record status map
			status = record.getStatus();
		}
		else{
			try {
				DataSet ds = PersistenceManager.getInstance().getData().getDataSetSummary(record.getIdentifier().getProjectPrefix()).getCompleteDataSet();
				status = PersistenceManager.getInstance().getRecordStatusMap().getStatusForRecord(recordIdentifier, ds);
			}
			catch (IOException ioe) {
				ExceptionsHelper.handleIOException(parent, ioe, false);
			}
		}

		if ((docGroup.getPrerequisiteGroups() == null || docGroup.getPrerequisiteGroups().size() == 0)
				&& (docGroup.getAllowedRecordStatus() == null || docGroup.getAllowedRecordStatus().size() == 0)) {
			return false; //No dependancies, can be viewed whatever
		}

		if (containsStatus(docGroup.getAllowedRecordStatus(), status)) {  

			if (docGroup.getPrerequisiteGroups() == null || docGroup.getPrerequisiteGroups().size() == 0) {
				return false;
			}
			//check prerequisite document groups status
			// if any are locked then this docGroup is locked
			List<DocumentGroup> prerequisites = docGroup.getPrerequisiteGroups();
			for (DocumentGroup documentGroup : prerequisites) {
				if ( checkPrerequisiteDocGroup(documentGroup, status, record) ) {
					return true;
				}
			}
			return false;   //all prerequisites are allowed
		}

		return true;
	}

	/**
	 * Check whether a DocumentGroup should be locked (if not, check its DocumentGroups) 
	 * 
	 * Checks against current record status and whether all document instances it contains
	 * have been 'completed'. Also checks any prerequisites it has
	 * 
	 * Returns true if this DocumentGroup should be LOCKED.
	 * 
	 * @param prerequisite
	 * @return boolean
	 */
	private static boolean checkPrerequisiteDocGroup(final DocumentGroup prerequisite, final Status status, final Record record) {

		//check that the current record is allowed to access this DocGroup
		if (containsStatus(prerequisite.getAllowedRecordStatus(), status)) {

			//A DG should be locked if any one of its prerequisites have not been completed
			if (!PersistenceManager.getInstance().isDocumentGroupCompleted(record, prerequisite)) {
				return true;
			}

			//now check whether the prerequisite groups are accessible and have been completed.

			if (prerequisite.getPrerequisiteGroups() == null || prerequisite.getPrerequisiteGroups().size() == 0) {
				return false;
			}
			for (DocumentGroup group: prerequisite.getPrerequisiteGroups()) {
				if (checkPrerequisiteDocGroup(group, status, record)) {
					return true;
				}
			}
			return false;   //current group is completed and all prerequisites are allowed
		}
		return true;    //record doesn't have allowed status
	}


	private static boolean containsStatus(List<Status> statuses, Status status) {
		if (status != null) {
			for (Status s: statuses) {
				if (s != null && s.getShortName().equals(status.getShortName())) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean anyHasLabel(List<SectionPresModel> sectionOccPresModels) {
		for (SectionPresModel sectionOccPresModel : sectionOccPresModels) {
			SectionOccurrence sectionOcc = sectionOccPresModel.getSectionOccurrence();
			if (sectionOcc.getLabel() != null
					&& (!sectionOcc.getLabel().equals(""))) { //$NON-NLS-1$
				return true;
			}

		}
		return false;
	}

	public static String getSectionLabelText(SectionPresModel sectionPresModel, Map<SectionOccurrence, IncInteger> secOccInstCounter) {
		SectionOccurrence sectionOcc = sectionPresModel.getSectionOccurrence();
		StringBuilder sectionLabelText = new StringBuilder();
		sectionLabelText.append(sectionOcc.getSection().getDisplayText());
		if (sectionOcc.getDisplayText() != null)   {
			sectionLabelText.append(" - ");
			sectionLabelText.append(sectionOcc.getDisplayText());
		}
		if ( sectionOcc.isMultipleAllowed() ){
			//append index of this runtime instance of the section occurrence
			IncInteger count = secOccInstCounter.get(sectionOcc);
			if ( null == count ){
				count = new IncInteger(0);
				secOccInstCounter.put(sectionOcc, count);
			}
			sectionLabelText.append(" ");
			sectionLabelText.append(count.increment());
		}
		return sectionLabelText.toString();
	}

	public static String doDdeCopy(Record record, DocumentInstance docInst,
			PersistenceManager pManager) 
	throws IOException, DecryptionException, InvalidIdentifierException, 
	RepositoryInvalidIdentifierFault, RepositoryOutOfDateFault, TransformerFault,
    NotAuthorisedFault, EntrySAMLException, RepositoryNoConsentFault, 
    RemoteServiceFault, DuplicateDocumentsFault {
		//DUAL DATA ENTRY
		String message = null;
		if ( null != record.getSecondaryIdentifier() ){
			//This record has a secondary record linked to it.
			//See if we need to copy the document instance over to 
			//the secondary
			DocumentOccurrence occ = docInst.getOccurrence();
			Document doc = occ.getDocument();
			if ( null != doc.getSecondaryDocIndex() && null != occ.getSecondaryOccIndex() ){

				Record secRecord = null;
				DataSet secDs = null;
				Document secDoc = null;
				DocumentOccurrence secOcc = null;
				DocumentInstance secDocInst = null;

				//check to see if the secondary record has already been retrieved from the repository
				if ( null != record.getSecondaryRecord() ){
					secRecord = record.getSecondaryRecord();
					secDs = secRecord.getDataSet();
					secDoc = secDs.getDocument(doc.getSecondaryDocIndex().intValue());
					secOcc = secDoc.getOccurrence(occ.getSecondaryOccIndex().intValue());
					secDocInst = secRecord.getDocumentInstance(secOcc);
					//TODO assuming here that secDocInst will always be non-null in this scenario!
				}
				else{

					//Find the secondary dataset
					secDs = pManager.getData().getCompleteDataSet(record.getDataSet().getSecondaryProjectCode());
					//find the relevant document occurrence in the secondary dataset
					secDoc = secDs.getDocument(doc.getSecondaryDocIndex().intValue());
					secOcc = secDoc.getOccurrence(occ.getSecondaryOccIndex().intValue());

					//check that the secondary occurrence is not locked
					if ( secOcc.isLocked() ){
						return "The following document could not be added to the secondary participant:\n\n"+
						secOcc.getCombinedDisplayText()+"\n\nThe document has been locked.";
					}

					secDocInst = secDoc.generateInstance(secOcc);

					//find the secondary record
					try{
						LOG.info("Trying to load secondary record...");
						secRecord = pManager.loadRecord(record.getSecondaryIdentifier(), true);
						//see if there is already a document instance for the same document -
						//if so, then detach it and replace with the new copied doc inst
						DocumentInstance oldSecDocInst = secRecord.getDocumentInstance(secOcc);
						if ( null != oldSecDocInst ){
							//we make the assumption here that the secondary record will not have
							//been saved to the repository. Therefore it is enough to just detach
							//the old document instance, which will then be garbage collected
							secRecord.detachDocumentInstance(oldSecDocInst);
						}
					}
					catch(FileNotFoundException ex){
						LOG.info("Secondary record not found locally...");
						//no record exists for this identifier - assume that a record has already
						//been committed to the repository for this identifier so just create a new 
						//record (in the same way we would if we were creating a new document for an
						//existing record)
						secRecord = RecordHelper.constructRecord(record.getSecondaryIdentifier(), pManager);
					}
					if ( !secRecord.checkConsent(secDocInst) ){
						return "The following document could not be added to the secondary participant:\n\n"+
						secOcc.getCombinedDisplayText()+
						"\n\nThere was insufficient consent to add it to the participant record.";
					}
					secRecord.addDocumentInstance(secDocInst);
				}

				//copy the document instance from the primary to the secondary
				docInst.ddeCopy(secDocInst);
				secDocInst.addToHistory(SecurityManager.getInstance().getUserName(), ChangeHistory.DATA_REP);

				if ( pManager.getData().isAlwaysOnlineMode() ){
					RemoteManager.getInstance().commit(secDocInst, true);
				}
				else{
					pManager.saveRecord(secRecord, true);
					pManager.updateRecord(secRecord, secDocInst, true);                        	
				}
			}
		}
		return message;
	}

	/**
	 * Calls {@code r} if there are no uncommitted records. In order to
	 * establish this, this method will ask the user to commit any uncommitted
	 * records (if there are any) repeatedly until the user selects "No" in the
	 * initial dialog box, "Cancel" in the Commit dialog box or there are no
	 * uncommitted records left. In other words, if the user unselects some
	 * records in the commit dialog box, we ask again instead of just cancelling
	 * the operation.
	 * 
	 * Note that if there are no uncommitted records when this method is
	 * initially called, {@code r} will be called immediately without any user
	 * action.
	 * 
	 * @param parent
	 * @param messagePrefix
	 *            A prefix to be added to the standard message asking the user
	 *            if he/she would like the CommitDialog to be launched.
	 */
	public static final void runWhenNoUncommittedRecords(final LoginInterfaceFrame parent,
			final String messagePrefix, final Runnable r, final Runnable rCancel) {
		if (!hasUncommittedDocs(parent)) {
			r.run();
			return;
		}
        
        //If there are uncommitted documents AND the user has a document still open, inform them that they have
        //uncommitted documents and that they must close their open document.
        
        if(parent instanceof Application){
	        boolean docIsOpen = ((Application)parent).getModel().getCurrentDocOccurrenceInstance() == null ?
	        		false : true;
	        
	        if(docIsOpen){
	        	String title = EntryMessages.getString("EntryHelper.mustCommitTitle");
	        	String message = EntryMessages.getString("EntryHelper.mustCloseDocumentFirstMessage");
	        	WrappedJOptionPane.showWrappedMessageDialog(parent, message, title, 
	        			WrappedJOptionPane.INFORMATION_MESSAGE);
	        	return;
	        }
        }
        
        String title = EntryMessages.getString("EntryHelper.mustCommitTitle");
        String messageSuffix = EntryMessages.getString("EntryHelper.launchCommitDialogQuestion");
		String message;
		if (0 == messagePrefix.length())
			message = messageSuffix;
		else
			message = messagePrefix + " " + messageSuffix;
		int result = JOptionPane.showConfirmDialog(parent, message, title, JOptionPane.YES_NO_OPTION);
		if (result != JOptionPane.YES_OPTION){
			if ( null != rCancel ){
				rCancel.run();
			}
			return;
		}
		RemoteCommitAction action = new RemoteCommitAction(parent) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void finished() {
				super.finished();
				runWhenNoUncommittedRecords(parent, messagePrefix, r, rCancel);
			}

			@Override
			protected void cancelled() {
				if ( null != rCancel ){
					rCancel.run();
				}
			}
			
			
			
		};
		action.actionPerformed(new ActionEvent(parent, 
				ActionEvent.ACTION_PERFORMED, "")); //$NON-NLS-1$
	}    
	
	public static final void runWhenMoveToOnlineOfflineMode(final LoginInterfaceFrame parent,
			final Runnable rFailure, final Runnable rSuccess) {
		
		WrappedJOptionPane.showWrappedMessageDialog(
				parent, 
				EntryMessages.getString("EntryHelper.performUpdateMessage"), 
				EntryMessages.getString("EntryHelper.performUpdateTitle"), 
				WrappedJOptionPane.INFORMATION_MESSAGE);
		
		RemoteUpdateAction action = new RemoteUpdateAction( (Application)parent ) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void failure() {
				rFailure.run();
			}

			@Override
			protected void success() {
				rSuccess.run();
			}
		};
		action.actionPerformed(new ActionEvent(parent, 
				ActionEvent.ACTION_PERFORMED, "")); //$NON-NLS-1$

	}
	
}
