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

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.psygrid.collection.entry.FormView;
import org.psygrid.collection.entry.Icons;
import org.psygrid.collection.entry.builder.BuilderHandler;
import org.psygrid.collection.entry.event.RendererCreatedEvent;
import org.psygrid.collection.entry.event.SectionBuiltEvent;
import org.psygrid.collection.entry.event.SectionBuiltListener;
import org.psygrid.collection.entry.model.OptionPresModel;
import org.psygrid.collection.entry.model.OptionRadioButtonModel;
import org.psygrid.collection.entry.ui.BasicTextEntryField;
import org.psygrid.collection.entry.ui.EditableRadioButton;
import org.psygrid.collection.entry.ui.EntryLabel;
import org.psygrid.collection.entry.ui.Messages;
import org.psygrid.common.ui.WrappedJOptionPane;
import org.psygrid.data.model.IOptionValue;
import org.psygrid.data.model.hibernate.*;

import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

public class OptionRendererSPIDelegate {

    private OptionPresModel presModel;
    private final RendererData rendererData;
    private OptionEntry entry;
    private boolean hasOptionDependents;
    private JLabel validationLabel;
    private EntryLabel label;
    private BasicRenderer<?> renderer;
    
    public OptionRendererSPIDelegate(RendererData rendererData) {
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
            if (value.isHidden()) {
            	value.setTextValue(AbstractRendererSPI.HIDDEN_VALUE);
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

        String displayText = RendererHelper.getInstance().concatEntryLabelAndDisplayText(entry);
        label = new EntryLabel(displayText, false);
        validationLabel = new JLabel();
        renderer = new BasicRenderer<OptionPresModel>(label, validationLabel, null, 
                presModel);
        List<JComponent> comps = getOtherComponents();

        // We pass the label as the field as we want to have the description on
        // that. We also do the same for each radio button
        RendererHelper.getInstance().processDescription(null, entry, label);
        RendererHelper.getInstance().processValidation(presModel, validationLabel);
        renderer.addComponents(comps);
        
        if (!copy) {
            handler.putRenderer(entry, rendererData.getRowIndex(), renderer);
        }
        
        handler.fireRendererCreatedEvent(new RendererCreatedEvent(this, renderer));
        addSectionBuiltListener();
                
        return renderer;
    }
    
    private void addSectionBuiltListener() {
        if (hasOptionDependents) {
            final BuilderHandler builderHandler = 
                getRendererData().getRendererHandler().getBuilderHandler();
            builderHandler.addSectionBuiltListener(new SectionBuiltListener() {
                        public void sectionBuilt(SectionBuiltEvent event) {
                            addEnabledListener();
                            bindToOptionDependents();
                            builderHandler.removeSectionBuiltListener(this);
                        }
            });
        }
    }
    
    private void addEnabledListener() {
        for (EditableRadioButton radioButton : getRadioButtons(renderer.getComponents())) {
            // Safe not to release listener
            radioButton.addPropertyChangeListener("enabled", //$NON-NLS-1$
                    new PropertyChangeListener() {
                        public void propertyChange(PropertyChangeEvent evt) {
                            EditableRadioButton rButton = (EditableRadioButton) evt
                                    .getSource();
                            if (evt.getNewValue() == Boolean.FALSE
                                    && rButton.isSelected()) {
                                rButton.getModel().setSelected(false);
                            }
                        }
                    });
        }
    }
    
    /**
     * Called for every IOption present in the IOptionEntry.
     * @param option
     * @param radioButton
     */
    private void processOption(List<JComponent> comps, Option option,
            EditableRadioButton radioButton) {
        if (option.isTextEntryAllowed()) {
            BasicTextEntryField field = getTextEntry(radioButton, option);
            comps.add(getCombinedPanel(radioButton, field, option));
        }
        else {
            comps.add(getCombinedPanel(radioButton, null, option));
        }
    }
    
    private JComponent getCombinedPanel(EditableRadioButton button, 
            BasicTextEntryField field, Option option) {
    	if ( null == option.getDescription() && null == field ){
    		return button;
    	}
        FormLayout layout = new FormLayout("10dlu, 100dlu, default:grow"); //$NON-NLS-1$
        DefaultFormBuilder builder = new DefaultFormBuilder(layout, 
                new JPanel());
        FormLayout layout2 = new FormLayout("default, 2dlu, 10dlu");
        DefaultFormBuilder builder2 = new DefaultFormBuilder(layout2, new JPanel());
        builder.appendRow(FormView.getDefaultRowSpec());
        if ( null == option.getDescription() ){
        	builder.append(button, builder.getColumnCount());
        }
        else{
        	builder2.append(button);
        	builder2.append(createHelpLabel(option.getDescription()));
        	if (EditAction.DENY.equals(entry.getEditingPermitted())) {
        		builder2.append(createRestrictedLabel(Messages.getString("Entry.denied")));
        	}
        	else if (EditAction.READONLY.equals(entry.getEditingPermitted())) {
        		builder2.append(createRestrictedLabel(Messages.getString("Entry.readonly")));
        	}
        	builder.append(builder2.getPanel(), builder.getColumnCount());
        }
        if ( null != field ){
	        builder.appendRow(FormView.getDefaultRowSpec());
	        builder.nextLine();
	        builder.nextColumn();
	        builder.append(field);
        }
        return builder.getPanel();
    }
    
    private JLabel createHelpLabel(final String description){
        JLabel helpLabel = new JLabel();
        helpLabel.setIcon(Icons.getInstance().getIcon("help"));
        helpLabel.setToolTipText(Messages.getString("OptionRendererSPIDelegate.helpToolTipText"));
        helpLabel.addMouseListener(
            new MouseAdapter(){
                @Override
                public void mouseClicked(MouseEvent e) {
                    WrappedJOptionPane.showWrappedMessageDialog(
                    		RendererHelper.getInstance().findJFrameFromRenderer(renderer),
                            description,
                            Messages.getString("OptionRendererSPIDelegate.helpTitle"),
                            JOptionPane.INFORMATION_MESSAGE);
                }        
            }
        );
        return helpLabel;
    }
    
    private JLabel createRestrictedLabel(final String description){
        JLabel restrictedLabel = new JLabel();
        restrictedLabel.setIcon(Icons.getInstance().getIcon("entry_locked"));
        restrictedLabel.setToolTipText(description);
        return restrictedLabel;
    }
    
    protected List<JComponent> getOtherComponents() {
        List<StandardCode> stdCodes = rendererData.getStandardCodes();
        
        List<JComponent> comps = new ArrayList<JComponent>(
                entry.numOptions() + stdCodes.size());
        
        for (int i = 0; i < entry.numOptions(); ++i) {
            Option option = entry.getOption(i);
            
            if (option.numOptionDependents() > 0) {
                hasOptionDependents = true;
            }
            
            String optionText = RendererHelper.getInstance().getOptionText(entry, option);

            EditableRadioButton radioButton = getRadioButton(option, optionText);
            org.psygrid.data.model.hibernate.Component modelComponent =
                (option.getDescription() != null ? option : entry);
            RendererHelper.getInstance().processDescription(null, modelComponent, radioButton);
            
            processOption(comps, option, radioButton);
        }

        if(!entry.isDisableStandardCodes()) {
            for (int i = 0, c = stdCodes.size(); i < c; ++i) {
                StandardCode standardCode = stdCodes.get(i);
                String optionText = RendererHelper.getInstance().getStandardCodeText(standardCode);
                EditableRadioButton radioButton = getRadioButton(standardCode, optionText);
                radioButton.setStandardCode(true);
                radioButton.setDisableStandardCodes(entry.isDisableStandardCodes());
                comps.add(radioButton);
            }
        }
        
        List<EditableRadioButton> radioButtons = getRadioButtons(comps);

        MouseListener mouseListener = RendererHelper.getInstance().getMouseListener(rendererData,
                renderer, label, null);
        if (mouseListener != null) {
            label.addMouseListener(mouseListener);
            for (EditableRadioButton radioButton : radioButtons) {
                radioButton.addMouseListener(mouseListener);
            }
        }
        processEntryStatus(radioButtons);
        return comps;
    }
    
    //TODO Think about representing units in radio buttons
    private BasicTextEntryField getTextEntry(EditableRadioButton otherRadioButton,
            Option option) {
        List<Unit> units = new ArrayList<Unit>(entry.numUnits());
        for (int i = 0, c = entry.numUnits(); i < c; ++i) {
            units.add(entry.getUnit(i));
        }
        Option value = (Option) presModel.getValueModel().getValue();
        ValueModel textValueModel = null;
        if (value != null && value.equals(option)) {
            textValueModel = presModel.getTextValueModel();
        }
        BasicTextEntryField field = new BasicTextEntryField(textValueModel, 
                presModel.getUnitModel(), units);
        if (value != null && value.equals(option)) {
        	field.setEnabled(true, false);
        }
        else{
        	field.setEnabled(false, false);
        }
        addItemListener(otherRadioButton, field);
        return field;
    }
    
    /**
     * Returns a List of EditableRadioButtons. It simply
     * iterates through <code>comps</code>, finds instances of EditableRadioButtons
     * and returns all of these as a List.
     * @param comps List of JComponents.
     * @return List of JRadioButtons extracted from <code>comps</code>.
     */
    private List<EditableRadioButton> getRadioButtons(List<? extends Component> comps){
        List<EditableRadioButton> radioButtons = new ArrayList<EditableRadioButton>(comps.size());
        for (Component comp : comps) {
            if (comp instanceof EditableRadioButton) {
                radioButtons.add((EditableRadioButton) comp);
            }
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                List<Component> panelComps = Arrays.asList(panel.getComponents());
                radioButtons.addAll(getRadioButtons(panelComps));
            }
        }
        return radioButtons;
    }
    
    private void processEntryStatus(final List<EditableRadioButton> radioButtons) {
        for (EditableRadioButton radioButton : radioButtons) {
            RendererHelper.getInstance().processEntryStatus(label, presModel, radioButton,
                    rendererData.isCopy(),
                    rendererData.getDocOccurrenceInstance().getStatus(),
                    rendererData.isEditable());
        }
    }
    
    private EditableRadioButton getRadioButton(Object choice, String text) {
        EditableRadioButton radioButton = new EditableRadioButton(new JRadioButton(text));
        boolean enabled = radioButton.getModel().isEnabled();
        radioButton.setModel(new OptionRadioButtonModel(presModel.getValueModel(),
                presModel.getStandardCodeModel(), choice));
        radioButton.setEnabled(enabled);
        
        return radioButton;
    }
    
    protected final RendererData getRendererData() {
        return rendererData;
    }

    protected final OptionPresModel getPresModel() {
        return presModel;
    }

    public void bindToOptionDependents() {
        RendererHelper.getInstance().bindToOptionDependents(getRendererData(), getPresModel());
    }

    private void addItemListener(EditableRadioButton radioButton,
            final BasicTextEntryField textField) {
        textField.setEnabled(radioButton.isSelected());

        // Safe not to release listener
        radioButton.getModel().addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    textField.setTextModel(presModel.getTextValueModel());
                    textField.setEnabled(true, false);
                    textField.setMandatory(true);
                } else {
                    textField.setEnabled(false, false);
                    textField.setMandatory(false);
                    textField.getTextComponent().setText(""); //$NON-NLS-1$
                    textField.setTextModel(null);
                }
            }
        });

        // Safe not to release listener
        radioButton.addPropertyChangeListener("enabled", //$NON-NLS-1$
                new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        EditableRadioButton rButton = (EditableRadioButton) evt.getSource();
                        if (evt.getNewValue() == Boolean.TRUE
                                && rButton.isSelected()) {
                            textField.setEnabled(true);
                            textField.setMandatory(true);
                        } else {
                            textField.setEnabled(false);
                            textField.setMandatory(false);
                        }
                    }
                });
    }
}
