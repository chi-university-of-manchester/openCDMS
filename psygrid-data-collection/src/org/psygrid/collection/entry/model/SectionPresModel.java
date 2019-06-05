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


package org.psygrid.collection.entry.model;

import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;
import org.psygrid.data.model.hibernate.SecOccInstance;
import org.psygrid.data.model.hibernate.SectionOccurrence;

public final class SectionPresModel {
    
    private final ValueModel entryStatusModel;
    private final SectionOccurrence sectionOccurrence;
    private final SecOccInstance secOccInstance;
    
    public SectionPresModel(SectionOccurrence sectionOccurrence) {
        this.sectionOccurrence = sectionOccurrence;
        this.entryStatusModel = createEntryStatusModel();
        secOccInstance = null;
    }
    
    private ValueModel createEntryStatusModel() {
        return new ValueHolder(sectionOccurrence.getEntryStatus(), 
                true);
    }

    public SectionPresModel(SecOccInstance secOccInstance) {
        this.sectionOccurrence = secOccInstance.getSectionOccurrence();
        this.secOccInstance = secOccInstance;
        this.entryStatusModel = createEntryStatusModel();
    }
    
    public final SecOccInstance getSecOccInstance() {
        return secOccInstance;
    }
    
    public final SectionOccurrence getSectionOccurrence() {
        return sectionOccurrence;
    }
    
    public final ValueModel getEntryStatusValueModel() {
        return entryStatusModel;
    }
}
