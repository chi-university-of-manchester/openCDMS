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
package org.psygrid.collection.entry.util;

import java.io.IOException;
import java.net.ConnectException;
import java.util.List;

import javax.swing.JOptionPane;

import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.EntryMessages;
import org.psygrid.collection.entry.ExceptionsHelper;
import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.collection.entry.persistence.RecordsList;
import org.psygrid.collection.entry.remote.RemoteManager;
import org.psygrid.collection.entry.remote.RemoteServiceFault;
import org.psygrid.collection.entry.security.DecryptionException;
import org.psygrid.collection.entry.security.EntrySAMLException;
import org.psygrid.collection.entry.ui.ApplicationDialog;
import org.psygrid.collection.entry.ui.EslReviewSubjectDialog;
import org.psygrid.collection.entry.ui.EslSubjectsFoundDialog;
import org.psygrid.common.identifier.InvalidIdentifierException;
import org.psygrid.common.ui.WrappedJOptionPane;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Identifier;
import org.psygrid.data.model.hibernate.Record;
import org.psygrid.data.utils.security.NotAuthorisedFault;
import org.psygrid.esl.model.ISubject;

/**
 * A utility class for common ESL related functions
 * 
 * @author Lucy Bridges
 *
 */
public class EslHelper {

	private static final int MAX_RESULTS = 15;

	/**
	 * Search the ESL for subjects matching the criteria provided in the 
	 * example subject.
	 * 
	 * @param eslSubject
	 * @return results
	 */
	public static List<ISubject> searchEslSubject(Application application, ApplicationDialog parent, final ISubject eslSubject, DataSet dataset){

		/**
		 * The subjects retrieved from the ESL using the search criteria provided.
		 */
		List<ISubject> subjects = null;

		if (RemoteManager.getInstance().isTestDataset()) {
			return null;
		}
		parent.setWait(true);
		try {
			subjects = RemoteManager.getInstance().eslSearchForSubject(eslSubject, dataset);
		}
		catch (ConnectException ce) {
			//assume that a connect exception is because the user is currently
			//offline.
			String title = "Unable to search";
			String message = "Unable to search the participant register at this point\n" +
			"as you do not currently have a network connection.\n\n";

			//connect exception will also be throw in DSD preview mode
			JOptionPane.showMessageDialog(parent, message, title,
					JOptionPane.INFORMATION_MESSAGE);
			return null;
		} catch (IOException ioe) {
			ExceptionsHelper.handleIOException(
					parent, ioe, false);
			return null;
		} catch (NotAuthorisedFault naf) {
			ExceptionsHelper.handleNotAuthorisedFault(
					parent, naf);
			return null;
		} catch (RemoteServiceFault rsf) {
			ExceptionsHelper.handleRemoteServiceFault(
					parent, rsf);
			return null;
		} catch (EntrySAMLException ex) {
			ExceptionsHelper.handleEntrySAMLException(
					parent, ex);
			return null;
		} catch (Exception ex) {
			ExceptionsHelper.handleException(
					parent,
					"Error",
					ex,
					"An error occurred whilst trying search the Participant Register\n\n"+
					EntryMessages.getString("DefaultExceptionHandler.message")+
					PersistenceManager.getInstance().getBaseDirLocation() +
					EntryMessages.getString("DefaultExceptionHandler.message2"),
					true);
			application.exitWithoutConfirmation(false);
			return null;
		}
		finally{
			parent.setWait(false);
		}

		return subjects;
	}

	/**
	 * Display the results of the search for subjects in the appropriate manner.
	 * 
	 * If one subject is found the details will be displayed for review. If more
	 * than one is found then a list will be shown allowing the subject to be 
	 * chosen and then reviewed.
	 * 
	 * @param application
	 * @param parent
	 * @param subjects
	 * @param dataset
	 */
	public static Record displaySearchResults(Application application, ApplicationDialog parent, List<ISubject> subjects, DataSet dataset) {
		if (subjects == null) {
			//Assume that an error occurred when attempting the search, so an error would have been displayed. 
			return null;
		}
		if (subjects.size() == 0) {
			WrappedJOptionPane.showWrappedMessageDialog(parent, "No subjects matching the criteria were found.", "No Subjects Found", JOptionPane.INFORMATION_MESSAGE);
			return null;
		}
		if (subjects.size() == 1) {
			//Retrieve the record based on the study number of the selected subject
			String studyNumber = subjects.get(0).getStudyNumber();
			Record record = null;
			try{
				record = RecordHelper.constructRecord(studyNumber);
			}
			catch(InvalidIdentifierException ex){
				ExceptionsHelper.handleFatalException(ex);
			}
			catch(IOException ex){
				ExceptionsHelper.handleIOException(application, ex, false);
			}
			EslReviewSubjectDialog subjectDialog = new EslReviewSubjectDialog(application, record, subjects.get(0));
			subjectDialog.setVisible(true);
			if (subjectDialog.isSubjectEdit()) {
				parent.dispose();
			}
			if (subjectDialog.isSubjectOK()) {
				return record;
			}
			return null;
		}
		else if (subjects.size() > MAX_RESULTS) {
			WrappedJOptionPane.showWrappedMessageDialog(parent, "Too many subjects were found. Please refine the search criteria.", "Too Many Subjects Found", JOptionPane.INFORMATION_MESSAGE);
			return null;
		}

		//If we get this far, show a list of subjects that have been found
		EslSubjectsFoundDialog foundSubjects = new EslSubjectsFoundDialog(application, parent, subjects, dataset);
		foundSubjects.setVisible(true);
		return foundSubjects.getSelectedRecord();
	}

	/**
	 * Retrieve the record for the study number of the selected subject.
	 * 
	 * This first attempts to load the record locally, otherwise it
	 * attempts to retrieve it from the repository.
	 * 
	 * @param application
	 * @param parent
	 * @param studyNumber
	 * @return
	 */
	public static Record retrieveRecord(Application application, ApplicationDialog parent, String studyNumber) {
		Record record = null;

		try {
			for (RecordsList.Item item : PersistenceManager.getInstance().getRecordsList().getItems()) {
				Identifier identifier = item.getIdentifier();
				String identifierText = identifier.getIdentifier();
				if (identifierText.equals(studyNumber)) {
					try {
						record = PersistenceManager.getInstance().loadRecord(item);
					} catch (IOException e) {
						ExceptionsHelper.handleIOException(parent, e,
								false);
						return record;
					}
					// Should never happen
					catch (DecryptionException e) {
						ExceptionsHelper.handleFatalException(e);
						return null;
					}
				}
			}
		} catch (IOException e) {
			ExceptionsHelper.handleIOException(parent, e,
					false);
			return null;
		}// Should never happen
		catch (DecryptionException e) {
			ExceptionsHelper.handleFatalException(e);
			return null;
		}

		if (record == null) {
			try {
				record = RemoteManager.getInstance().getCompleteRecord(studyNumber);
			} catch (Exception ex) {
				ExceptionsHelper.handleException(parent, "Problem occurred", null, "There was a problem retrieving the record for: "+studyNumber, false);
				return null;
			} 

			//Record still not found?!
			if (record == null) {
				ExceptionsHelper.handleException(parent, "No record found", null, "No record was found for the participant identifier: "+studyNumber, false);
				return null;
			}
		}

		return record;
	}
}
