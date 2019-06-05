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
import java.net.SocketTimeoutException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.DocumentStatus;
import org.psygrid.collection.entry.ExceptionsHelper;
import org.psygrid.collection.entry.security.EntrySAMLException;
import org.psygrid.collection.entry.util.ResetWaitRunnable;
import org.psygrid.common.identifier.InvalidIdentifierException;
import org.psygrid.common.ui.WrappedJOptionPane;
import org.psygrid.data.model.hibernate.DocumentOccurrence;
import org.psygrid.data.utils.security.NotAuthorisedFault;

public class ChangeDocStatusWorker extends SynchronizeWorker<List<String>> {

	private Collection<DocumentOccurrence> docOccs;
	private DocumentStatus newStatus;
	private String identifier;
	
	public ChangeDocStatusWorker(Application application, 
			Collection<DocumentOccurrence> docOccs, DocumentStatus newStatus, String identifier){
		super(application);
		this.docOccs = docOccs;
		this.newStatus = newStatus;
		this.identifier = identifier;
	}
	
	@Override
	protected List<String> doInBackground() throws IOException, ConnectException, 
	SocketTimeoutException, RemoteServiceFault, NotAuthorisedFault, EntrySAMLException, InvalidIdentifierException {
		return RemoteManager.getInstance().changeDocumentStatus(docOccs, newStatus, identifier);
	}

	protected void done(){
        new ResetWaitRunnable(application).run();
        try {
        	List<String> notChanged = get();
        	if ( notChanged.isEmpty() ){
        		WrappedJOptionPane.showWrappedMessageDialog(
        				application, 
        				Messages.getString("ChangeDocStatusWorker.successMessage")+newStatus.toStatusLongName(), 
        				Messages.getString("ChangeDocStatusWorker.successTitle"), 
        				WrappedJOptionPane.INFORMATION_MESSAGE);
        	}
        	else{
        		StringBuilder message = new StringBuilder();
        		message.append(Messages.getString("ChangeDocStatusWorker.errorMessage"));
        		for ( String nc: notChanged ){
        			message.append("\n"+nc);
        		}
        		WrappedJOptionPane.showWrappedMessageDialog(
        				application, 
        				message,
        				Messages.getString("ChangeDocStatusWorker.errorTitle"), 
        				WrappedJOptionPane.INFORMATION_MESSAGE);
        	}            
            success();
        } catch (InterruptedException e) {
            new ResetWaitRunnable(application).run();
            ExceptionsHelper.handleInterruptedException(e);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
    		if ( cause instanceof ConnectException ){
    			ExceptionsHelper.handleConnectException(application, (ConnectException)cause);
    		}
    		else if ( cause instanceof SocketTimeoutException ){
    			ExceptionsHelper.handleSocketTimeoutException(application, (SocketTimeoutException)cause);
    		}
    		else if ( cause instanceof IOException ){
    			ExceptionsHelper.handleIOException(application, (IOException)cause, false);
    		}
    		else if ( cause instanceof RemoteServiceFault ){
    			ExceptionsHelper.handleRemoteServiceFault(application, (RemoteServiceFault)cause);
    		}
    		else if ( cause instanceof NotAuthorisedFault ){
    			ExceptionsHelper.handleNotAuthorisedFault(application, (NotAuthorisedFault)cause);
    		}
    		else if ( cause instanceof EntrySAMLException ){
    			ExceptionsHelper.handleEntrySAMLException(application, (EntrySAMLException)cause);            			
    		}
    		else{
    			ExceptionsHelper.handleFatalException(cause);
    		}
        }
    }
	
	protected void success(){
	}
	
}
