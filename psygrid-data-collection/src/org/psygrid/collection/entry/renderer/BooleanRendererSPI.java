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

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;

import org.psygrid.collection.entry.event.RendererCreatedEvent;
import org.psygrid.collection.entry.model.BasicPresModel;
import org.psygrid.collection.entry.ui.AbstractEditable;
import org.psygrid.collection.entry.ui.EditableToggleButton;
import org.psygrid.collection.entry.ui.EntryLabel;
import org.psygrid.data.model.hibernate.*;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.value.ValueModel;

public class BooleanRendererSPI implements RendererSPI {

    public boolean canHandle(Entry modelObject, Entry parent) {
        if (modelObject instanceof BooleanEntry) {
            return true;
        }
        
        return false;
    }

    public Renderer getRenderer(RendererData rendererData) {
        BooleanEntry entry = (BooleanEntry) rendererData.getModel();
        
        BasicResponse response = RendererHelper.getInstance().getModelResponse(rendererData);
        
        BooleanValue value;
        
        boolean copy = rendererData.isCopy();
        
        if (response != null) {
            value = (BooleanValue) RendererHelper.getInstance().checkAndGetValue(response);
            if (copy) {
                value = value.copy();
            }
        }
        else {
            value = entry.generateValue();
            response = RendererHelper.getInstance().processResponse(rendererData, value);
        }
        
        RendererHandler handler = rendererData.getRendererHandler();
        BasicPresModel presModel = handler.createPresModel(this, response, 
                value, rendererData.getValidationPrefix());
        
        ValueModel textModel = presModel.getValueModel();
        
        String displayText = RendererHelper.getInstance().concatEntryLabelAndDisplayText(entry);
        JComponent label = new EntryLabel(displayText);
        
        final JCheckBox cBox = BasicComponentFactory.createCheckBox(textModel, null);
        AbstractEditable editableCBox = new EditableToggleButton(cBox);
        final JLabel validationLabel = new JLabel();
        BasicRenderer<?> renderer = new BasicRenderer<BasicPresModel>(label, validationLabel, 
                editableCBox, presModel);
        
        Status docStatus = rendererData.getDocOccurrenceInstance().getStatus();
        RendererHelper.getInstance().addMouseListeners(rendererData, label, renderer,
                editableCBox, null);
        RendererHelper.getInstance().processAll(label, presModel, editableCBox, validationLabel, 
                editableCBox, copy, docStatus, rendererData.isEditable());
        int rowIndex = rendererData.getRowIndex();
        if (!copy) {
            handler.putRenderer(entry, rowIndex, renderer);
        }
        handler.fireRendererCreatedEvent(new RendererCreatedEvent(this, renderer));
        return renderer;
    }

}
