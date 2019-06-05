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

import org.psygrid.collection.entry.FormView;
import org.psygrid.collection.entry.renderer.BasicRenderer;
import org.psygrid.collection.entry.renderer.Renderer;
import org.psygrid.collection.entry.renderer.RendererHandler;

import org.psygrid.collection.entry.ui.DividerLabel;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import org.psygrid.data.model.hibernate.BasicEntry;
import org.psygrid.data.model.hibernate.CompositeEntry;
import org.psygrid.data.model.hibernate.Entry;

/**
 * Responsible for building <code>IBasicEntry</code>s that are children of
 * <code>ICompositeEntry</code> but should be laid out outside the composite.
 * 
 * An example of this is when a ICompositeEntry child has to be edited
 * in the <code>EditDialog</code>.
 * 
 * @author Ismael Juma (ismael@juma.me.uk)
 *
 */
public class CompositeItemDetachedBuilder implements BuilderSPI {

    public boolean canHandle(Entry model, Entry parent) {
        if (parent instanceof CompositeEntry && model instanceof BasicEntry) {
            return true;
        }
        return false;
    }
    
    public void build(BuilderData builderData) {
    	//Bug 804 - it is possible for rowIndex > 0
        //BuilderHelper.assertRowIndexEqualsZero(builderData);
        RendererHandler rendererHandler = builderData.getRendererHandler();
        Entry model = builderData.getModel();
        DefaultFormBuilder builder = builderData.getBuilder();
        BasicRenderer<?> renderer = (BasicRenderer<?>) rendererHandler.getRenderer(
                model, builderData.getModelParentResponse(), builderData.getRowIndex(),
                null, builderData.isCopy());

        build(builder, renderer);
        BuilderHelper.appendDivider(builder, new DividerLabel(model.getName()));
    }
    
    private void build(DefaultFormBuilder builder, Renderer renderer) {
        BasicRenderer<?> basicRenderer = (BasicRenderer<?>) renderer;
        BuilderHelper.appendLabel(builder, basicRenderer.getValidationLabel());
        builder.appendRow(FormView.getDefaultRowSpec());
        builder.nextLine();
        int cols = 2;
		builder.append(basicRenderer.getHelpLabel());
		//Updated to fix Bug #960
		if (basicRenderer.getRestrictedLabel() != null && !basicRenderer.getRestrictedLabel().getText().equals("")) {
			builder.append(basicRenderer.getRestrictedLabel());
			cols ++;
		}
		builder.append(basicRenderer.getField(), cols);
        BuilderHelper.appendEndingGap(builder);
    }
}
