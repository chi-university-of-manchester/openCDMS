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
import java.util.concurrent.ExecutionException;

import org.jdesktop.swingworker.SwingWorker;
import org.psygrid.collection.entry.ExceptionsHelper;
import org.psygrid.collection.entry.action.MarkResponseInvalidAction;
import org.psygrid.collection.entry.model.ResponsePresModel;
import org.psygrid.collection.entry.security.EntrySAMLException;
import org.psygrid.collection.entry.util.DdeHelper;
import org.psygrid.data.model.hibernate.Response;
import org.psygrid.data.utils.security.NotAuthorisedFault;

public class MarkResponseAsInvalidWorker extends SwingWorker<Object, Object> {
    private final String annotation;
    private final MarkResponseInvalidAction action;
    private final ResponsePresModel presModel;
    public MarkResponseAsInvalidWorker(ResponsePresModel presModel, String annotation,
    		MarkResponseInvalidAction action) {
        this.presModel = presModel;
        this.annotation = annotation;
        this.action = action;
    }
        
    @Override
    protected Object doInBackground() throws ConnectException, SocketTimeoutException, RemoteServiceFault,
            NotAuthorisedFault, IOException, EntrySAMLException   {
        RemoteManager.getInstance().markResponseAsInvalid(presModel.getResponse(),
                annotation);
        //DUAL DATA ENTRY
        if ( null != presModel.getResponse().getRecord().getSecondaryRecord() ){
        	//find the equivalent response in the secondary record to
        	//also mark as invalid
        	Response secResp = DdeHelper.findResponseForSecondary(presModel);
            RemoteManager.getInstance().markResponseAsInvalid(secResp, annotation);        
        }
        return null;
    }
    
    @Override
    protected void done() {
        try {
            get();
            action.changeState(annotation);
        } catch (InterruptedException e) {
            ExceptionsHelper.handleInterruptedException(e);
        } catch (ExecutionException e) {
            action.failure(e);
        }
    }
}
