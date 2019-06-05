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


package org.psygrid.collection.entry.persistence;

import org.psygrid.collection.entry.DocumentStatus;
import org.psygrid.data.model.hibernate.DocumentInstance;
import org.psygrid.data.model.hibernate.Record;

public class UnfinishedDocInstance {
    
    private DocumentInstance docOccurrenceInstance;
    private int sectionOccurrenceIndex;
    private boolean readyToCommit;
    private DocumentStatus docStatus;
    
    public UnfinishedDocInstance(DocumentInstance docOccurrenceInstance,
            int sectionOccurrenceIndex, DocumentStatus docStatus) {
        this.docOccurrenceInstance = docOccurrenceInstance;
        this.sectionOccurrenceIndex = sectionOccurrenceIndex;
        this.docStatus = docStatus;
    }

    public final  DocumentInstance  getDocOccurrenceInstance() {
        return docOccurrenceInstance;
    }

    public final int getSectionOccurrenceIndex() {
        return sectionOccurrenceIndex;
    }
    
    public final boolean isReadyToCommit() {
        return readyToCommit;
    }
    
    public final DocumentStatus getDocStatus(){
    	return docStatus;
    }
    
    /**
     * A slightly laborious way of detaching the document
     * instance from the dataset without affecting the original
     * DocumentInstance object
     */
    public void softDetach(){
    	Record r = docOccurrenceInstance.getRecord().toDTO().toHibernate();
    	for ( DocumentInstance di: r.getDocInstances() ){
    		if ( di.getOccurrenceId().equals(docOccurrenceInstance.getOccurrence().getId())){
    			docOccurrenceInstance = di;
    			return;
    		}
    	}
    }
    
}
