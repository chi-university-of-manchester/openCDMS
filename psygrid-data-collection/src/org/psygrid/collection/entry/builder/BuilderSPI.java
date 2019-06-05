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


package org.psygrid.collection.entry.builder;

import org.psygrid.data.model.hibernate.Entry;

/**
 * Implementors must build (render and layout) visual components
 * from a <code>IEntry</code> model object and other parameters encapsulated in a 
 * <code>BuilderData</code> object.<p>
 * 
 * To integrate a BuilderSPI into the application, you must implement this 
 * interface and then add the fully qualified name of the class into 
 * ${src}/META-INF/services/org.psygrid.collection.entry.builder.BuilderSPI.
 * 
 * <code>BuilderHandler#build</code> will iterate through all the classes
 * specified in that file that implement this interface and call build() on
 * the first one that returns <code>true</code> to BuilderSPI#canHandle(). As
 * a result, the order of the list is important.
 * 
 * @author Ismael Juma (ismael@juma.me.uk)
 * @see BuilderData
 * @see IEntry
 * @see BuilderHandler
 */
public interface BuilderSPI {

    /**
     * Returns whether this BuilderSPI can build <code>model</code>, also
     * considering its <code>parent</code>. An example where the parent is
     * important is when dealing with a model that has a ICompositeEntry as a
     * parent.
     * @param model IEntry to be built.
     * @param parent Parent of <code>model</code>.
     */
    public boolean canHandle(Entry model, Entry parent);

    /**
     * Builds (renders and lays out) a set of visual components according to
     * <code>builderData</code>.
     * 
     * @param builderData Specifies various parameters that determine the
     * behaviour of this method.
     * @see BuilderData
     */
    public void build(BuilderData builderData);
}
