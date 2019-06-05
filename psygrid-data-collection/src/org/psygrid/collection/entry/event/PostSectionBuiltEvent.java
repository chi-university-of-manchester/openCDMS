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

/**
 * It is fired after all the listeners of the <code>SectionBuiltEvent</code> have
 * been called. This is useful if an object needs to override changes made
 * by the <code>SectionBuiltEvent</code> listeners.
 * 
 * @author Ismael Juma (ismael@juma.me.uk)
 * @see PostSectionBuiltListener
 * @see SectionBuiltListener
 * @see SectionBuiltEvent
 *
 */
public final class PostSectionBuiltEvent extends EventObject {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private final SectionPresModel sectionPresModel;
    
    /**
     * Creates a new instance of this object.
     * @param source the value to set the <code>source</code> property to.
     * @param sectionPresModel the value to set the <code>sectionPresModel</code>
     * property to.
     */
    public PostSectionBuiltEvent(Object source, 
            SectionPresModel sectionPresModel) {
        super(source);
        this.sectionPresModel = sectionPresModel;
    }
    
    public final SectionPresModel getSectionPresModel() {
        return sectionPresModel;
    }
}
