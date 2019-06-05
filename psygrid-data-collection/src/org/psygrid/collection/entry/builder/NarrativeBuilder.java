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

import javax.swing.JComponent;

import org.psygrid.collection.entry.renderer.Renderer;
import org.psygrid.collection.entry.renderer.RendererHandler;
import org.psygrid.collection.entry.ui.DividerLabel;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import org.psygrid.data.model.hibernate.Entry;
import org.psygrid.data.model.hibernate.NarrativeEntry;

/**
 * Responsible for building models of type <code>INarrativeEntry</code>.
 * 
 * @author Ismael Juma (ismael@juma.me.uk)
 *
 * @see INarrativeEntry
 */
public class NarrativeBuilder implements BuilderSPI    {

    /**
     * @return <code>true</code> if <code>model</code> is of type 
     * <code>INarrativeEntry</code>.
     * 
     * {@inheritDoc}
     */
    public boolean canHandle(Entry model, Entry modelParent) {
        if (model instanceof NarrativeEntry) {
            return true;
        }
        
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void build(BuilderData builderData) {
        BuilderHelper.assertRowIndexEqualsZero(builderData);
        RendererHandler rendererHandler = builderData.getRendererHandler();
        Entry model = builderData.getModel();
        DefaultFormBuilder builder = builderData.getBuilder();
        Renderer renderer = rendererHandler.getRenderer(model,
                builderData.getModelParentResponse(), builderData.getRowIndex(),
                null, builderData.isCopy());
        build(builder, renderer);
        BuilderHelper.appendDivider(builder, new DividerLabel(model.getName()));
    }

    private void build(DefaultFormBuilder builder, Renderer renderer) {
        JComponent label = renderer.getLabel();
        BuilderHelper.appendLabel(builder, label);
        BuilderHelper.appendEndingGap(builder);
    }
}
