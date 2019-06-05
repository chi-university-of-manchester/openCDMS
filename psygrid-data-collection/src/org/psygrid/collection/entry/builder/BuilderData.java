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

import java.util.List;

import org.psygrid.collection.entry.renderer.RendererHandler;
import org.psygrid.collection.entry.renderer.RendererSPI;
import org.psygrid.data.model.hibernate.CompositeResponse;
import org.psygrid.data.model.hibernate.DocumentInstance;
import org.psygrid.data.model.hibernate.Entry;
import org.psygrid.data.model.hibernate.StandardCode;

import com.jgoodies.forms.builder.DefaultFormBuilder;

/**
 * Contains the information required by a <code>BuilderSPI</code> to build
 * a <code>IEntry</code>.
 * 
 * @author Ismael Juma (ismael@juma.me.uk)
 */
public final class BuilderData {

    private final RendererHandler rendererHandler;

    private final DefaultFormBuilder builder;

    private final Entry model;

    private final CompositeResponse modelParentResponse;
    
    private final boolean copy;

    private final int rowIndex;
    
    /**
     * Creates an instance of this object.
     * 
     * @param rendererHandler The renderer handler to be used by the BuilderSPI
     * to control the rendering process.
     * @param builder DefaultFormBuilder to add visual elements to.
     * @param model The IEntry that has to be built.
     * @param modelParentResponse The parent of <code>model</code> or null if
     * it has no parent.
     * @param copy True if the element is to be built outside the main frame.
     * This is useful in cases like the EditDialog where certain restrictions
     * should not be applied. This is explained in more detail in 
     * <code>RendererSPI</code>.
     * @param rowIndex The row index the element belongs to. This is 0 if the
     * element has no parent. If the element has a parent, then it should specify
     * in what position it belongs in the parent.
     */    
    public BuilderData(RendererHandler rendererHandler,
            DefaultFormBuilder builder, Entry model,
            CompositeResponse modelParentResponse, boolean copy, int rowIndex) {
        this.rendererHandler = rendererHandler;
        this.builder = builder;
        this.model = model;
        this.modelParentResponse = modelParentResponse;
        this.copy = copy;
        this.rowIndex = rowIndex;
    }

    /**
     * @return the builder property.
     */
    public final DefaultFormBuilder getBuilder() {
        return builder;
    }

    /**
     * @return the model property.
     */
    public final  Entry  getModel() {
        return model;
    }

    /**
     * @return the rendererHandler property.
     */
    public final RendererHandler getRendererHandler() {
        return rendererHandler;
    }
    
    /**
     * Convenience method that gets the builderHandler property from the
     * rendererHandler.
     * 
     * @return BuilderHandler used while building the document.
     */
    public final BuilderHandler getBuilderHandler() {
        return rendererHandler.getBuilderHandler();
    }
    
    /**
     * Convenience method that gets the standardCodes from the builder handler
     * contained in the rendererHandler object.
     * 
     * @return the standardCodes from the repository being used.
     */
    public final List<StandardCode> getStandardCodes() {
        return rendererHandler.getBuilderHandler().getStandardCodes();
    }
    
    /**
     * Convenience method that retrieves the parent of the entry through
     * the <code>modelParentResponse</code> property.
     * @return the parent of the <code>model</code> property or null if it
     * has no parent.
     */
    public final  Entry  getModelParent() {
        if (modelParentResponse == null) {
            return null;
        }
        return modelParentResponse.getEntry();
    }

    /**
     * @return the response of <code>model</code>'s parent or null if it has
     * no parent, or if the parent has no response.
     */
    public final CompositeResponse getModelParentResponse() {
        return modelParentResponse;
    }
    
    /**
     * Convenience method that retrieves the document occurrence instance from
     * the builderHandler that is in turn retrieved from rendererHandler.
     * 
     * @return document occurrence instance that the element being built belongs
     * to.
     */
    public final DocumentInstance getDocOccurrenceInstance() {
        return rendererHandler.getBuilderHandler().getDocOccurrenceInstance();
    }
    
    /**
     * @return whether the item should be built as a copy.
     * 
     * @see RendererSPI
     */
    public final boolean isCopy() {
        return copy;
    }
    
    /**
     * @return 0 or a higher integer representing the position of <code>model</code>
     * in its parent. If <code>model</code> has no parent, 0 will be returned.
     */
    public final int getRowIndex() {
        return rowIndex;
    }
}
