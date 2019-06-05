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

package org.psygrid.collection.entry.action;

import java.awt.Component;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;

import org.psygrid.collection.entry.ExceptionsHelper;
import org.psygrid.collection.entry.model.ResponsePresModel;
import org.psygrid.collection.entry.remote.RemoteServiceFault;
import org.psygrid.collection.entry.renderer.PresModelRenderer;
import org.psygrid.collection.entry.security.EntrySAMLException;
import org.psygrid.data.utils.security.NotAuthorisedFault;

/**
 * @author Rob Harper
 *
 */
public abstract class MarkResponseAction extends AbstractAction {

    private final PresModelRenderer<? extends ResponsePresModel> renderer;

    public MarkResponseAction(PresModelRenderer<? extends ResponsePresModel> renderer, String name){
    	super(name);
    	this.renderer = renderer;
    }
    
    private Component getParentComponent() {
        return renderer.getComponents().get(0).getParent();
    }
    
    protected ResponsePresModel getPresModel() {
        return renderer.getPresModel();
    }
    
    public PresModelRenderer<? extends ResponsePresModel> getRenderer(){
    	return renderer;
    }

    public void failure(ExecutionException e) {
        Throwable cause = e.getCause();
        if (cause instanceof ConnectException) {
            ExceptionsHelper.handleConnectException(getParentComponent(), 
                    (ConnectException) cause);
        }
        else if (cause instanceof SocketTimeoutException) {
            ExceptionsHelper.handleSocketTimeoutException(getParentComponent(), 
                    (SocketTimeoutException) cause);
        }
        else if (cause instanceof IOException) {
            ExceptionsHelper.handleIOException(getParentComponent(), 
                    (IOException) cause, false);
        }
        else if (cause instanceof NotAuthorisedFault) {
            ExceptionsHelper.handleNotAuthorisedFault(getParentComponent(), 
                    (NotAuthorisedFault) cause);
        }
        else if (cause instanceof RemoteServiceFault) {
            ExceptionsHelper.handleRemoteServiceFault(getParentComponent(), 
                    (RemoteServiceFault) cause);
        }
        else if (cause instanceof EntrySAMLException) {
            ExceptionsHelper.handleEntrySAMLException(getParentComponent(), 
                    (EntrySAMLException) cause);
        }
        else {
            ExceptionsHelper.handleFatalException(cause);
        }
    }

}
