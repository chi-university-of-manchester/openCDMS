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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.psygrid.collection.entry.event.PostEditDialogBuiltEvent;
import org.psygrid.collection.entry.event.PostEditDialogBuiltListener;
import org.psygrid.collection.entry.event.PostSectionBuiltEvent;
import org.psygrid.collection.entry.event.PostSectionBuiltListener;
import org.psygrid.collection.entry.event.RendererCreatedEvent;
import org.psygrid.collection.entry.model.BasicPresModel;
import org.psygrid.collection.entry.ui.EntryComponent;
import org.psygrid.collection.entry.ui.EntryLabel;
import org.psygrid.data.model.IValue;
import org.psygrid.data.model.hibernate.*;

public abstract class AbstractBasicRendererSPI implements RendererSPI {
     
    public Renderer getRenderer(final RendererData rendererData) {
        BasicEntry entry = (BasicEntry) rendererData.getModel();
        
        BasicResponse response = RendererHelper.getInstance().getModelResponse(rendererData);
        
        boolean copy = rendererData.isCopy();
        IValue value = getValue(entry, response, copy);
        if (response == null) {
            response = RendererHelper.getInstance().processResponse(rendererData, value);
        }
        final RendererHandler handler = rendererData.getRendererHandler();
        
        String validationPrefix = rendererData.getValidationPrefix();
        BasicPresModel presModel = createPresModel(handler, response, 
                value, validationPrefix);
        
        String displayText = RendererHelper.getInstance().concatEntryLabelAndDisplayText(entry);
        JComponent label = new EntryLabel(displayText);
        
        List<Unit> units = RendererHelper.getInstance().getUnitsAsList(entry);
        List<StandardCode> stdCodes = rendererData.getStandardCodes();

        EntryComponent field = createField(presModel, units, stdCodes, entry.isDisableStandardCodes());
        
        final JLabel validationLabel = new JLabel();
        final BasicRenderer<?> renderer = new BasicRenderer<BasicPresModel>(label,
                validationLabel, field, presModel);
        Status docStatus = rendererData.getDocOccurrenceInstance().getStatus();
        RendererHelper.getInstance().processAll(label, presModel, field, validationLabel, 
                field.getTextComponent(), copy, docStatus, rendererData.isEditable());
        
        PostEditDialogBuiltListener postEditDialogBuiltListener = 
                new PostEditDialogBuiltListener() {
            public void postBuilt(PostEditDialogBuiltEvent event) {
                done(event.getCopyRenderer());
            }
        };
        MouseListener listener = RendererHelper.getInstance().getMouseListener(rendererData,
                renderer, field, postEditDialogBuiltListener);

        MouseListener compoundListener = createMouseListener(presModel,
                field, listener, docStatus);
        label.addMouseListener(compoundListener);
        field.addMouseListener(compoundListener);
        
        handler.getBuilderHandler().addPostSectionBuiltListener(new PostSectionBuiltListener() {
            public void postSectionBuilt(PostSectionBuiltEvent event) {
                done(renderer);
            }
        });
        
        if (!copy) {
            handler.putRenderer(entry, rendererData.getRowIndex(), renderer);
        }
        handler.fireRendererCreatedEvent(new RendererCreatedEvent(this, renderer));
        return renderer;
    }
    
    private final ValueFactory VALUE_FACTORY = new ValueFactory() {
        public IValue createValue(final BasicEntry entry){
            return AbstractBasicRendererSPI.this.createValue(entry);
        }
    };

    protected MouseListener createMouseListener(final BasicPresModel presModel,
            final EntryComponent field, final MouseListener listener,
            final Status docStatus) {
        MouseAdapter mouseListener = new BasicRendererMouseListener(presModel,
                listener, field, docStatus, VALUE_FACTORY);
        return mouseListener;
    }
    
    protected abstract IValue createValue(BasicEntry response);

    public void done(BasicRenderer<?> renderer) {
        // Empty implementation. Subclasses can override
    }
    
    protected abstract BasicPresModel createPresModel(
            RendererHandler rendererHandler, 
            BasicResponse response, IValue value,
            String validationPrefix);

    protected abstract EntryComponent createField(BasicPresModel model,
            List<Unit> units, List<StandardCode> stdCodes, boolean disableStandardCodes);
    
    protected abstract IValue getValue(BasicEntry entry,
            BasicResponse  esponse, boolean copy);
}
