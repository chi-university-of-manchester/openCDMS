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

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutionException;

import org.jdesktop.swingworker.SwingWorker;
import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.ExceptionsHelper;
import org.psygrid.collection.entry.security.EntrySAMLException;
import org.psygrid.collection.entry.ui.EslNewSubjectDialog;
import org.psygrid.collection.entry.ui.EslReviewSubjectDialog;
import org.psygrid.collection.entry.ui.Messages;
import org.psygrid.collection.entry.util.ResetWaitRunnable;
import org.psygrid.collection.entry.util.WaitRunnable;
import org.psygrid.data.model.hibernate.Record;
import org.psygrid.data.utils.security.NotAuthorisedFault;
import org.psygrid.esl.model.ISubject;

/**
 * @author Rob Harper
 *
 */
public class GetRegisterSubjectWorker extends SwingWorker<ISubject, Object> {
	
	private Record record;
	
	private Application application;
	
	public GetRegisterSubjectWorker(Record record, Application parent){
		this.record = record;
		this.application = parent;
	}
	
	@Override
	protected ISubject doInBackground() throws ConnectException,
	NotAuthorisedFault, IOException, RemoteServiceFault,
	EntrySAMLException, ESLSubjectNotFoundFault {
		new WaitRunnable(application).run();
		return RemoteManager.getInstance().eslRetrieveSubject(record);
	}
	
	@Override
	protected void done() {
		try {       
			new ResetWaitRunnable(application).run();
			ISubject subject = get();
			EslReviewSubjectDialog dlg = new EslReviewSubjectDialog(application, record, subject);
			dlg.setVisible(true);
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
			else if (cause instanceof ESLSubjectNotFoundFault) {

				String title = org.psygrid.collection.entry.remote.Messages.getString("RemoteEslWorker.NoSubject");
				boolean sufficientConsent = record.checkConsentForEsl();
				String message = null;
				if(sufficientConsent){
					message = org.psygrid.collection.entry.remote.Messages.getString("RemoteEslWorker.CreateEntryNowMessage");
				}else{
					message = org.psygrid.collection.entry.remote.Messages.getString("RemoteEslWorker.InsufficientConsentMessage");
				}
				
				ExceptionsHelper.handleException(application, 
						title, cause, message, false);
				
				if(sufficientConsent){
					EslNewSubjectDialog eslDialog = new EslNewSubjectDialog(application, record);
					eslDialog.setVisible(true);
				}
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

}
