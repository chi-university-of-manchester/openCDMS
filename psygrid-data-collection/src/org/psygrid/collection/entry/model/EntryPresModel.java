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

import org.psygrid.collection.entry.event.EntryStatusListener;

import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.validation.ValidationResult;
import org.psygrid.data.model.hibernate.DocumentInstance;
import org.psygrid.data.model.hibernate.Entry;

public interface EntryPresModel {

    public Entry getEntry();
    
    public ValueModel getEntryStatusModel();
    
    public DocumentInstance getDocInstance();
    
    public SectionPresModel getSectionOccPresModel();
    
    public void addEntryStatusListener(EntryStatusListener listener);
    
    public void removeEntryStatusListener(EntryStatusListener listener);
    
    public EntryStatusListener[] getEntryStatusListeners();

    public void performValidation(boolean partial);
    
    public ValidationResult validate(boolean partial);
    
    /**
     * Reset a pres model back to it's original state after
     * the entry status has changed back to disabled.
     * <p>
     * After this method has been called the pres model should 
     * be in the same state as it was before it was enabled.
     */
    public void reset();
    
    
    /**
     * The purpose of this is to give the presence model a nudge in order for it to send out
     * events to its listeners. It does NOT permanently change anything in the state of the model.
     * It leaves the model in the same state as it was immediately before the method call.
     */
    public void touch();
    
}