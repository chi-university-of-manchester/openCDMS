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

import org.psygrid.collection.entry.model.SectionPresModel;

public class SectionChangedEvent extends EventObject    {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final SectionPresModel oldSectionOccPresModel;

    private final SectionPresModel currentSectionOccPresModel;

    public SectionChangedEvent(Object source, 
            SectionPresModel oldSectionOccPresModel, 
            SectionPresModel currentSectionOccPresModel) {
        super(source);
        this.oldSectionOccPresModel = oldSectionOccPresModel;
        this.currentSectionOccPresModel = currentSectionOccPresModel;
    }

    public static final long getSerialVersionUID() {
        return serialVersionUID;
    }

    public final SectionPresModel getCurrentSectionOccPresModel() {
        return currentSectionOccPresModel;
    }

    public final SectionPresModel getOldSectionOccPresModel() {
        return oldSectionOccPresModel;
    }
}
