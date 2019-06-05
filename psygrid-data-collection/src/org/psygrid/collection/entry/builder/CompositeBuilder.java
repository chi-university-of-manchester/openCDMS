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

import javax.swing.JComponent;

import org.psygrid.collection.entry.ui.DividerLabel;

import org.psygrid.collection.entry.FormView;
import org.psygrid.collection.entry.renderer.PresModelRenderer;
import org.psygrid.collection.entry.renderer.Renderer;
import org.psygrid.collection.entry.renderer.RendererHandler;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import org.psygrid.data.model.hibernate.CompositeEntry;
import org.psygrid.data.model.hibernate.Entry;

/**
 * Responsible for building models of type <code>ICompositeEntry</code>.
 * 
 * @author Ismael Juma (ismael@juma.me.uk)
 */
public class CompositeBuilder implements BuilderSPI {

    /**
     * @return <code>true</code> if <code>model</code> is of type 
     * <code>ICompositeEntry</code>.
     * 
     * {@inheritDoc}
     */
    public boolean canHandle(Entry model, Entry parent) {
        if (model instanceof CompositeEntry) {
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
        Entry modelObject = builderData.getModel();
        DefaultFormBuilder builder = builderData.getBuilder();
        Renderer renderer = rendererHandler.getRenderer(modelObject,
                builderData.getModelParentResponse(), builderData.getRowIndex(),
                null, builderData.isCopy());
        build(builder, renderer);
        BuilderHelper.appendDivider(builder, new DividerLabel(modelObject.getName()));
    }

    private void build(DefaultFormBuilder builder, Renderer renderer) {
        List<JComponent> comps = renderer.getComponents();
        BuilderHelper.appendHelpLabelAndLabel(builder,
                (PresModelRenderer<?>) renderer);
        builder.appendRow(FormView.getDefaultRowSpec());
        builder.nextLine();
        builder.append(comps.get(3), builder.getColumnCount());

        BuilderHelper.appendEndingGap(builder);
    }

}
