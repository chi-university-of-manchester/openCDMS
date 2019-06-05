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


package org.psygrid.collection.entry.event;

import java.util.EventObject;

import org.psygrid.collection.entry.model.EntryPresModel;
import org.psygrid.collection.entry.model.SectionPresModel;
import org.psygrid.data.model.hibernate.EntryStatus;

public final class EntryStatusEvent extends EventObject {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final EntryStatus oldStatus;
    private final EntryStatus currentStatus;
    private final SectionPresModel sectionOccPresModel;
    
    public EntryStatusEvent(EntryPresModel source, EntryStatus oldStatus, 
            EntryStatus currentStatus, SectionPresModel sectionOccPresModel) {
        super(source);
        this.oldStatus = oldStatus;
        this.currentStatus = currentStatus;
        this.sectionOccPresModel = sectionOccPresModel;
    }
    
    public final EntryStatus getCurrentStatus() {
        return currentStatus;
    }
    public final EntryStatus getOldStatus() {
        return oldStatus;
    }
    public final SectionPresModel getSectionOccPresModel() {
        return sectionOccPresModel;
    }
    
}
