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

import org.psygrid.collection.entry.builder.BuilderHandler;
import org.psygrid.collection.entry.event.RendererCreatedEvent;
import org.psygrid.collection.entry.event.SectionBuiltEvent;
import org.psygrid.collection.entry.event.SectionBuiltListener;
import org.psygrid.collection.entry.model.OptionComboBoxModel;
import org.psygrid.collection.entry.model.OptionEditableComboBoxModel;
import org.psygrid.collection.entry.model.OptionPresModel;
import org.psygrid.collection.entry.ui.ComboBoxTextConverter;
import org.psygrid.collection.entry.ui.EditableComboBox;
import org.psygrid.collection.entry.ui.EntryLabel;
import org.psygrid.data.model.IOptionValue;
import org.psygrid.data.model.hibernate.*;

import com.jgoodies.binding.value.ValueModel;

/**
 * @author pwhelan
 *
 */
public class OptionDropDownSPIDelegate {

    private RendererData rendererData;
    
    private OptionPresModel presModel;
    private ComboBoxModel comboBoxModel;
    private ComboBoxEditor comboBoxEditor;
    private ValueModel optionModel;
    private ValueModel stdCodeModel;
    private OptionEntry entry;
    private List<StandardCode> standardCodes;
    private List<Option> options;
    private ValueModel textValueModel;
    private EntryLabel label;
    private BasicRenderer<?> renderer;
    private JLabel validationLabel;
    private boolean hasOptionDependents;
    
    private ComboBoxTextConverter textConverter;

    public OptionDropDownSPIDelegate(RendererData rendererData) {
        this.rendererData = rendererData;
    }

    public Renderer getRenderer() {
        entry = (OptionEntry) rendererData.getModel();
        
        BasicResponse response = RendererHelper.getInstance().getModelResponse(rendererData);
        
        IOptionValue value;
        
        boolean copy = rendererData.isCopy();
        if (response != null) {
            value = (IOptionValue) 
                    RendererHelper.getInstance().checkAndGetValue(response);
            if (copy) {
                value = value.copy();
            }
        }
        else    {
            Option defaultOption = entry.getDefaultValue();
            value = entry.generateValue();
            if (defaultOption != null) {
                value.setValue(defaultOption);
            }
            response = RendererHelper.getInstance().processResponse(rendererData, value);
        }
        RendererHandler handler = rendererData.getRendererHandler();
        
        presModel = handler.createOptionPresModel(this, response, value,
                rendererData.getValidationPrefix());
        
        optionModel = presModel.getValueModel();

        stdCodeModel = presModel.getStandardCodeModel();
        textValueModel = presModel.getTextValueModel();

        standardCodes = rendererData.getStandardCodes();

        final EditableComboBox cBox = getComboBox();
        
        String displayText = RendererHelper.getInstance().concatEntryLabelAndDisplayText(entry);
        label = new EntryLabel(displayText, false);
        validationLabel = new JLabel();
        
		renderer = new BasicRenderer<OptionPresModel>(label, validationLabel, cBox, 
		        presModel);

        Status docStatus = rendererData.getDocOccurrenceInstance().getStatus();

        RendererHelper.getInstance().processAll(null, presModel, cBox, validationLabel, cBox,
                copy, docStatus, rendererData.isEditable());
        
        MouseListener mouseListener = RendererHelper.getInstance().getMouseListener(rendererData,
                renderer, cBox, null);
        if (mouseListener != null) {
        	cBox.addMouseListener(mouseListener);
        }
        
        if (!copy) {
        	handler.putRenderer(entry, rendererData.getRowIndex(), renderer);
        }
        handler.fireRendererCreatedEvent(new RendererCreatedEvent(this, renderer));
        addSectionBuiltListener();
        return renderer;
    }
    
    protected EditableComboBox getComboBox() {
        
        boolean textEntryAllowed = false;
        for (int i = 0; i < entry.numOptions(); ++i) {
            Option option = entry.getOption(i);
            if (option.isTextEntryAllowed()) {
                textEntryAllowed = true;
                break;
            }
            
            if (option.numOptionDependents() > 0) {
                hasOptionDependents = true;
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
                    optionModel,entry, textValueModel,standardCodes, stdCodeModel);
            textConverter = new ComboBoxTextConverter() {
                public String getSelectedItemText(JComboBox comboBox) {
                    return model.getSelectedItemFullText();
                }
            };
            return model;
        }
        return new OptionComboBoxModel(optionModel, entry, standardCodes,
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
        return entry;
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
    
    private void addSectionBuiltListener() {
        if (hasOptionDependents) {
            final BuilderHandler builderHandler = 
                getRendererData().getRendererHandler().getBuilderHandler();
            builderHandler.addSectionBuiltListener(new SectionBuiltListener() {
                        public void sectionBuilt(SectionBuiltEvent event) {
                            bindToOptionDependents();
                            builderHandler.removeSectionBuiltListener(this);
                        }
            });
        }
    }
    
    public void bindToOptionDependents() {
        RendererHelper.getInstance().bindToOptionDependents(getRendererData(), presModel);
    }
    
}