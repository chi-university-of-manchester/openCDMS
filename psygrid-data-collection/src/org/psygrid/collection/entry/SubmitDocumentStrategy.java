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

import java.io.IOException;

import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.collection.entry.remote.RemoteManager;
import org.psygrid.data.model.hibernate.Record;

public abstract class SubmitDocumentStrategy {
    
    protected final Application application;
    
    protected SubmitDocumentStrategy(Application application) {
        this.application = application;
    }
    
    public abstract void submit(Record currentRecord);
    
    protected final void success() {
        if ( !RemoteManager.getInstance().isTestDataset() ){
            try{
                PersistenceManager.getInstance().deleteAutoSaveDocumentInstance();
            }
            catch(IOException ex){
                //Should never happen
                ExceptionsHelper.handleIOException(application, ex, false);
            }
        }
        application.clear(false);
        application.refreshRecordView();
    }
    
    protected final void failure(){
    	application.getModel().getForwardAction().setEnabled(true);
    }
}
