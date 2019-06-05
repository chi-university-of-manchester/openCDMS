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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.psygrid.collection.entry.adapter.NumericValueAdapter;
import org.psygrid.collection.entry.builder.BuilderHandler;
import org.psygrid.collection.entry.event.EditEvent;
import org.psygrid.collection.entry.event.EditListener;
import org.psygrid.collection.entry.event.RendererCreatedEvent;
import org.psygrid.collection.entry.event.SectionBuiltEvent;
import org.psygrid.collection.entry.event.SectionBuiltListener;
import org.psygrid.collection.entry.model.BasicPresModel;
import org.psygrid.collection.entry.model.CompositePresModel;
import org.psygrid.collection.entry.model.DerivedValueModel;
import org.psygrid.collection.entry.model.TextPresModel;
import org.psygrid.collection.entry.ui.BasicTextEntryField;
import org.psygrid.collection.entry.ui.EntryComponent;
import org.psygrid.collection.entry.ui.EntryLabel;
import org.psygrid.collection.entry.validation.DoubleValidationHandler;
import org.psygrid.data.model.INumericValue;
import org.psygrid.data.model.hibernate.*;

import com.jgoodies.binding.value.ValueModel;

public class DerivedRendererSPIDelegate {
    
    private TextPresModel presModel;
    private RendererData rendererData;
    private DerivedEntry entry;
    private DerivedValueModel derivedValueModel;
    private JLabel validationLabel;

    public DerivedRendererSPIDelegate(RendererData rendererData) {
        this.rendererData = rendererData;
    }
    
    public Renderer getRenderer() {
        RendererHandler rendererHandler = rendererData.getRendererHandler();
        final BuilderHandler builderHandler = rendererHandler.getBuilderHandler();
        
        rendererHandler.addEditListener(new EditListener() {
           public void editOccurred(EditEvent event) {
                rebindToDependentEntry(event.getCurrentPresModel(),
                        event.getCopyPresModel());
            }
        });
        
        builderHandler.addSectionBuiltListener(
                new SectionBuiltListener() {
                    public void sectionBuilt(SectionBuiltEvent event) {
                        bindToDependentEntries();
                        builderHandler.removeSectionBuiltListener(this);
                    }
                });
        entry = (DerivedEntry) rendererData.getModel();
        BasicResponse response = RendererHelper.getInstance().getModelResponse(rendererData);
        
        NumericValueAdapter value;
        if (response == null) {
            INumericValue valueDelegate = entry.generateValue();
            value = new NumericValueAdapter(valueDelegate);
            if (valueDelegate.getValue() != null) {
                value.setDisplayText(String.valueOf(valueDelegate.getValue()));
            }
            response = RendererHelper.getInstance().processResponse(rendererData, value);
        }
        else {
            INumericValue valueDelegate = 
                (INumericValue) RendererHelper.getInstance().checkAndGetValue(response);
            value = new NumericValueAdapter(valueDelegate);
        }
        
        presModel = rendererData.getRendererHandler().createDerivedPresModel(
                this, response, value, rendererData.getValidationPrefix());
        
        // Safe not to release listener
        presModel.getDisplayTextModel().addValueChangeListener(
                new DoubleValidationHandler(presModel));//.getValueModel()));

        String displayText = RendererHelper.getInstance().concatEntryLabelAndDisplayText(entry);
        JComponent label = new EntryLabel(displayText);

        EntryComponent field = new BasicTextEntryField(
                presModel.getDisplayTextModel(), presModel.getUnitModel(), 
                RendererHelper.getInstance().getUnitsAsList(entry));
        
        Status docStatus = rendererData.getDocOccurrenceInstance().getStatus();
        RendererHelper.getInstance().processEntryStatus(label, 
                presModel, field, rendererData.isCopy(), 
                docStatus, rendererData.isEditable());
        field.setEditable(false);
        RendererHelper.getInstance().processDescription(label, entry, field.getTextComponent());
        validationLabel = new JLabel();
        Renderer r = new BasicRenderer<TextPresModel>(label, validationLabel, field, presModel);

        int rowIndex = rendererData.getRowIndex();
        rendererHandler.putRenderer(entry, rowIndex, r);
        rendererHandler.fireRendererCreatedEvent(new RendererCreatedEvent(this, 
                r));
        
        RendererHelper.processValidation(presModel, validationLabel);
        
        return r;
    }
    
    private void rebindToDependentEntry(BasicPresModel oldPresModel,
            BasicPresModel newPresModel) {
        Set<String> variableNames = entry.getVariableNames();
        BasicEntry editedEntry = newPresModel.getEntry();
        for (String variableName : variableNames) {
            if (editedEntry.equals(entry.getVariable(variableName))) {
                DerivedValueModel.PresModel newDerivedPresModel = 
                    new DerivedValueModel.PresModel(newPresModel, variableName, entry.getVariableDefaults().get(variableName));
                DerivedValueModel.PresModel oldDerivedPresModel =
                    new DerivedValueModel.PresModel(oldPresModel, variableName, entry.getVariableDefaults().get(variableName));
                derivedValueModel.replacePresModel(oldDerivedPresModel, 
                        newDerivedPresModel);
            }
        }
    }

    private void bindToDependentEntries() {
        BuilderHandler builderHandler = 
            rendererData.getRendererHandler().getBuilderHandler();
        
        CompositeEntry compEntry = checkCompEntry(entry.getComposite());
        CompositePresModel compPresModel = getCompPresModel(builderHandler,
                compEntry);

        Set<String> variableNames = entry.getVariableNames();

        Map<BasicEntry, List<DerivedValueModel.PresModel>> presModelsMap =
            new LinkedHashMap<BasicEntry, List<DerivedValueModel.PresModel>>();
        for (String variableName : variableNames) {
            BasicEntry variable = entry.getVariable(variableName);
            List<DerivedValueModel.PresModel> derivedPresModels = 
                checkAndGetPresModels(builderHandler, variable, variableName, entry.getVariableDefaults().get(variableName));
            presModelsMap.put(variable, derivedPresModels);
        }
        StandardCode derivedEntryStdCode = getDerivedEntryStdCode(builderHandler);
        derivedValueModel = new DerivedValueModel(presModelsMap, compPresModel, 
                entry.getAggregateOperator(), entry.getFormula(), derivedEntryStdCode, entry.getUseDefaultValuesForDisabledEntriesInCalculation());
        // Safe not to release listener
        derivedValueModel.addValueChangeListener(new Updater());
        calculateValue();
    }
    
    private StandardCode getDerivedEntryStdCode(BuilderHandler builderHandler) {
        for (StandardCode stdCode : builderHandler.getStandardCodes()) {
            if (stdCode.isUsedForDerivedEntry()) {
                return stdCode;
            }
        }
        return null;
    }

    private CompositePresModel getCompPresModel(BuilderHandler builderHandler,
            CompositeEntry compEntry) {
        if (compEntry == null) {
            return null;
        }
        
        CompositePresModel compPresModel = builderHandler.getCompositePresModel(compEntry, 0);
        if (compPresModel == null) {
            throw new IllegalStateException("An existing CompositeEntry was " + //$NON-NLS-1$
                    "not found in the renderersMap"); //$NON-NLS-1$
        }
        return compPresModel;
        
    }

    private List<DerivedValueModel.PresModel> checkAndGetPresModels(
            BuilderHandler builderHandler, BasicEntry variable,
            String variableName, NumericValue variableDefaultValue) {
        
        List<BasicPresModel> variablePresModels = 
            builderHandler.getBasicPresModels(variable);
        
        if (variablePresModels.size() == 0) {
            throw new IllegalStateException("The variable: " + variable //$NON-NLS-1$
                    + " is not an IEntry in the IDocument."); //$NON-NLS-1$
        }
        List<DerivedValueModel.PresModel> derivedPresModels = 
            new ArrayList<DerivedValueModel.PresModel>(variablePresModels.size());
        
        for (BasicPresModel basicPresModel : variablePresModels) {
            derivedPresModels.add(new DerivedValueModel.PresModel(basicPresModel, 
                    variableName, variableDefaultValue));
        }
        return derivedPresModels;
    }

    private CompositeEntry checkCompEntry(CompositeEntry compEntry) {
        if (compEntry != null && entry.getAggregateOperator() == null) {
                throw new IllegalArgumentException("DerivedEntry with a non-null " + //$NON-NLS-1$
                        "composite property must also have a non-null " + //$NON-NLS-1$
                        "aggregateOperator"); //$NON-NLS-1$
            
        }
        if (compEntry == null && entry.getAggregateOperator() != null) {
                throw new IllegalArgumentException("DerivedEntry with a null " + //$NON-NLS-1$
                        "composite property must also have a null " + //$NON-NLS-1$
                        "aggregateOperator"); //$NON-NLS-1$
        }
        
        return compEntry;
    }

    private void calculateValue() {
        ValueModel displayTextModel = presModel.getDisplayTextModel();
        StandardCode stdCode = derivedValueModel.getStandardCode();
        if (stdCode != null) {
            presModel.getStandardCodeModel().setValue(stdCode);
            displayTextModel.setValue(RendererHelper.getInstance().getStandardCodeText(stdCode));
            return;
        }
        presModel.getStandardCodeModel().setValue(null);
        String value = getDoubleAsString(derivedValueModel.getValue());
        displayTextModel.setValue(value);
    }
    
    private String getDoubleAsString(Double number) {
        if (number == null) {
            return ""; //$NON-NLS-1$
        }
        String string = String.valueOf(number);
        return stripTrailingZeros(string);
    }
    
    private String stripTrailingZeros(String string) {
        int index = string.indexOf('.');
        if (index == -1) {
            return string;
        }
        
        // Remove trailing zeros and the '.' if the number has only zeros after
        // the period
        for (int i = string.length() - 1; i >= index; --i) {
            if (i == index) {
                return string.substring(0, index);
            }
            if (string.charAt(i) != '0') {
                return string.substring(0, i + 1);
            }
        }
        return string;
    }
    
    private class Updater implements PropertyChangeListener   {
        
        public void propertyChange(PropertyChangeEvent evt) {
            calculateValue();
            RendererHelper.processValidation(presModel, validationLabel);
        }
    }
}
