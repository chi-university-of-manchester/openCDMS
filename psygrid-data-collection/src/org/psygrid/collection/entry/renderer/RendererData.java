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

import java.util.List;

import org.psygrid.collection.entry.builder.BuilderHandler;
import org.psygrid.collection.entry.model.SectionPresModel;
import org.psygrid.data.model.hibernate.CompositeResponse;
import org.psygrid.data.model.hibernate.DocumentInstance;
import org.psygrid.data.model.hibernate.Entry;
import org.psygrid.data.model.hibernate.StandardCode;

public final class RendererData {

    private final Entry model;
    private final RendererHandler rendererHandler;
    private final CompositeResponse modelParentResponse;
    private final int rowIndex;
    private final String validationPrefix;
    private final boolean copy;
    private final EditableStatus editable;

    public RendererData(RendererHandler rendererHandler, Entry model,
            CompositeResponse modelParentResponse, int rowIndex,
            String validationPrefix, boolean copy, EditableStatus editable) {
        if (rendererHandler == null) {
            throw new IllegalArgumentException("rendererHandler cannot be null"); //$NON-NLS-1$
        }
        this.rendererHandler = rendererHandler;
        this.model = model;
        this.modelParentResponse = modelParentResponse;
        this.rowIndex = rowIndex;
        this.validationPrefix = validationPrefix;
        this.copy = copy;
        this.editable = editable;
    }
    
    public final int getRowIndex() {
        return rowIndex;
    }
    
    public final List<StandardCode> getStandardCodes() {
        return rendererHandler.getStdCodes();
    }

    public final Entry getModel() {
        return model;
    }

    public final RendererHandler getRendererHandler() {
        return rendererHandler;
    }

    public final Entry getModelParent() {
        if (modelParentResponse == null) {
            return null;
        }
        return modelParentResponse.getEntry();
    }
    
    public final CompositeResponse getModelParentResponse() {
        return modelParentResponse;
    }
    
    public final DocumentInstance getDocOccurrenceInstance() {
        BuilderHandler builderHandler = rendererHandler.getBuilderHandler();
        if (builderHandler != null) {
            return builderHandler.getDocOccurrenceInstance();
        }
        throw new IllegalStateException("builderHandler property is null"); //$NON-NLS-1$
    }
    
    public final SectionPresModel getSectionOccPresModel() {
        return rendererHandler.getSectionPresModel();
    }
    
    public final String getValidationPrefix() {
        return validationPrefix;
    }
    
    public final boolean isCopy() {
        return copy;
    }
    
    public enum EditableStatus { TRUE, FALSE, DEFAULT }
    
    /**
     * Overrides the editable status of the entry, which is usually defined by
     * the document instance status
     */
    public final EditableStatus isEditable() {
        return editable;
    }
    
    @Override
    public final String toString() {
        StringBuilder sb = new StringBuilder(50);
        sb.append("model: ").append(model).append(" - "); //$NON-NLS-1$ //$NON-NLS-2$
        sb.append("modelParent: ").append(getModelParent()).append(" - "); //$NON-NLS-1$ //$NON-NLS-2$
        sb.append("modelParentInstance: ").append(modelParentResponse); //$NON-NLS-1$
        return sb.toString();
    }
}
