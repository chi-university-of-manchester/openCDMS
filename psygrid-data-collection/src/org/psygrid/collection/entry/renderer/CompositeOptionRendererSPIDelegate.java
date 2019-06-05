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

import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.ComboBoxEditor;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import org.psygrid.collection.entry.event.RendererCreatedEvent;
import org.psygrid.collection.entry.model.OptionComboBoxModel;
import org.psygrid.collection.entry.model.OptionEditableComboBoxModel;
import org.psygrid.collection.entry.model.OptionPresModel;
import org.psygrid.collection.entry.ui.ComboBoxTextConverter;
import org.psygrid.collection.entry.ui.EditableComboBox;
import org.psygrid.data.model.IOptionValue;
import org.psygrid.data.model.hibernate.*;

import com.jgoodies.binding.value.ValueModel;

public class CompositeOptionRendererSPIDelegate {

    private RendererData rendererData;
    
    private ComboBoxModel comboBoxModel;
    private ComboBoxEditor comboBoxEditor;
    private ValueModel optionModel;
    private ValueModel stdCodeModel;
    private OptionEntry optionEntry;
    private List<StandardCode> standardCodes;
    private List<Option> options;
    private ValueModel textValueModel;

    private ComboBoxTextConverter textConverter;

    public CompositeOptionRendererSPIDelegate(RendererData rendererData) {
        this.rendererData = rendererData;
    }

    public Renderer getRenderer() {
        optionEntry = (OptionEntry) rendererData.getModel();
        
        CompositeResponse parentResponse = rendererData.getModelParentResponse();
        
        int rowIndex = rendererData.getRowIndex();
        
        IOptionValue value;
        
        BasicResponse response = RendererHelper.getInstance().getBasicResponse(parentResponse,
                optionEntry, rowIndex);
        
        boolean copy = rendererData.isCopy();
        boolean useDefaultValue = false;
        if (response != null) {
            value = (IOptionValue) RendererHelper.getInstance().checkAndGetValue(response);
            if (copy) {
                value = value.copy();
            }
        }
        else {
            value = optionEntry.generateValue();
            if (optionEntry.getDefaultValue() != null) {
                useDefaultValue = true;
            }
            response = RendererHelper.getInstance().processResponse(rendererData, value);
        }

        RendererHandler handler = rendererData.getRendererHandler();
        String validationPrefix = rendererData.getValidationPrefix();
        OptionPresModel presModel = handler.createOptionPresModel(this, 
                response, value, validationPrefix);

        optionModel = presModel.getValueModel();
        
        if (useDefaultValue) {
            optionModel.setValue(optionEntry.getDefaultValue());
        }
        stdCodeModel = presModel.getStandardCodeModel();
        textValueModel = presModel.getTextValueModel();

        standardCodes = rendererData.getStandardCodes();

        final EditableComboBox cBox = getComboBox();
        JLabel validationLabel = new JLabel();
        BasicRenderer<?> r = new BasicRenderer<OptionPresModel>(null, validationLabel, cBox, 
                presModel);

        Status docStatus = rendererData.getDocOccurrenceInstance().getStatus();

        RendererHelper.getInstance().processAll(null, presModel, cBox, validationLabel, cBox,
                copy, docStatus, rendererData.isEditable());
        
        MouseListener mouseListener = RendererHelper.getInstance().getMouseListener(rendererData,
                r, cBox, null);
        if (mouseListener != null) {
        	cBox.addMouseListener(mouseListener);
        }

        if (!copy) {
            handler.putRenderer(optionEntry, rowIndex, r);
        }
        handler.fireRendererCreatedEvent(new RendererCreatedEvent(this, r));
        return r;

    }
    
    protected EditableComboBox getComboBox() {
        
        boolean textEntryAllowed = false;
        for (int i = 0; i < optionEntry.numOptions(); ++i) {
            Option option = optionEntry.getOption(i);
            if (option.isTextEntryAllowed()) {
                textEntryAllowed = true;
                break;
            }
        }

        comboBoxModel = createComboBoxModel(textEntryAllowed);
        comboBoxEditor = createComboBoxEditor(textEntryAllowed);

        JComboBox cBox = new JComboBox(comboBoxModel);
        cBox.setEditable(true);
        cBox.setEditor(comboBoxEditor);
        
        EditableComboBox editableCBox = new EditableComboBox(cBox);
        editableCBox.setTextConverter(textConverter);
        return editableCBox;
    }
    
    public ComboBoxModel createComboBoxModel(boolean textEntryAllowed) {
        if (textEntryAllowed) {
            final OptionEditableComboBoxModel model = new OptionEditableComboBoxModel(
                    optionModel,optionEntry, textValueModel,standardCodes, stdCodeModel);
            textConverter = new ComboBoxTextConverter() {
                public String getSelectedItemText(JComboBox comboBox) {
                    return model.getSelectedItemFullText();
                }
            };
            return model;
        }
        return new OptionComboBoxModel(optionModel, optionEntry, standardCodes,
                stdCodeModel);
    }
    
    public ComboBoxEditor createComboBoxEditor(boolean textEntryAllowed) {
        if (textEntryAllowed) {
            return new OptionComboBoxEditor(
                    (OptionEditableComboBoxModel) getComboBoxModel());
        }
        return new OptionComboBoxRenderer();
    }

    protected final OptionEntry getOptionEntry() {
        return optionEntry;
    }

    protected final ValueModel getOptionModel() {
        return optionModel;
    }

    protected final List<Option> getOptions() {
        return options;
    }

    protected final RendererData getRendererData() {
        return rendererData;
    }

    protected final List<StandardCode> getStandardCodes() {
        return standardCodes;
    }

    protected final ValueModel getStdCodeModel() {
        return stdCodeModel;
    }
    
    protected final ComboBoxModel getComboBoxModel() {
        return comboBoxModel;
    }
}
