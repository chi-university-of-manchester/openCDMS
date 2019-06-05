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

import java.util.EventListener;

import org.psygrid.collection.entry.builder.BuilderHandler;

/**
 * Objects that implement this listener and are added by calling 
 * <code>BuilderHandler#addPostSectionBuiltListener(PostSectionBuiltListener)</code>
 * will be invoked after all the listeners of the <code>SectionBuiltEvent</code> 
 * have been called. This is useful if an object needs to override changes made
 * by the <code>SectionBuiltEvent</code> listeners.
 * 
 * @author Ismael Juma (ismael@juma.me.uk)
 * @see PostSectionBuiltEvent
 * @see BuilderHandler#addPostSectionBuiltListener(PostSectionBuiltListener)
 * @see BuilderHandler#removePostSectionBuiltListener(PostSectionBuiltListener)
 * @see SectionBuiltListener
 * @see SectionBuiltEvent
 */
public interface PostSectionBuiltListener extends EventListener {
    public void postSectionBuilt(PostSectionBuiltEvent event);
}
