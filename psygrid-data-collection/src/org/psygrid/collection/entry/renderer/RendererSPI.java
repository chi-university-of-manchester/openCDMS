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


package org.psygrid.collection.entry.renderer;

import org.psygrid.data.model.hibernate.Entry;

/**
 * Defines contract for renderer service provider interfaces.
 * 
 * @author Ismael Juma
 */
public interface RendererSPI {

    /**
     * Returns whether the class can handle the rendering of the specified model.
     * 
     * @param model model object to be rendered.
     * @param modelParent parent of model object to be rendered. This is
     * necessary for the cases where the parent influences how the model is
     * rendered (e.g. Element of type COMPOSITE).
     * @return whether this class can handle the specified object
     */
    public boolean canHandle(Entry model, Entry modelParent);

    /**
     * Creates a Renderer object containing a list of JComponents that are
     * capable of rendering the specified Object. An object implementing the
     * BuilderSPI interface is responsible for making sure the components are
     * layed out correctly.
     * @param rendererData data object containing information required for the
     * RendererSPI to be able to construct a visual representation for the model
     * object.
     * @return a Renderer containing JComponents that are able to render the
     * specified object.
     */
    public Renderer getRenderer(RendererData rendererData);

}
