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
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.psygrid.collection.entry.FormView;
import org.psygrid.collection.entry.renderer.BasicRenderer;
import org.psygrid.collection.entry.renderer.PresModelRenderer;
import org.psygrid.collection.entry.renderer.Renderer;
import org.psygrid.collection.entry.renderer.RendererHandler;
import org.psygrid.collection.entry.ui.DividerLabel;
import org.psygrid.collection.entry.ui.EditableToggleButton;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.validation.view.ValidationComponentUtils;
import org.psygrid.data.model.hibernate.CompositeEntry;
import org.psygrid.data.model.hibernate.Entry;
import org.psygrid.data.model.hibernate.OptionEntry;

/**
 * Responsible for building models of type <code>IOptionEntry</code> when the
 * parent is not a <code>ICompositeEntry</code>.
 * 
 * @author Ismael Juma (ismael@juma.me.uk)
 * @see IOptionEntry
 * @see ICompositeEntry
 */
public class OptionBuilder implements BuilderSPI {

    /**
     * @return <code>true</code> if <code>model</code> is of type 
     * <code>IOptionEntry</code> and <code>parent</code> is not of type
     * <code>ICompositeEntry</code>.
     * 
     * {@inheritDoc}
     */
    public boolean canHandle(Entry model, Entry parent) {
        if (model instanceof OptionEntry && (!(parent instanceof CompositeEntry))) {
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
        BasicRenderer<?> renderer = (BasicRenderer<?>) rendererHandler.getRenderer(
                model, builderData.getModelParentResponse(),
                builderData.getRowIndex(), null, 
                builderData.isCopy());

        build(builder, renderer);
        BuilderHelper.appendDivider(builder, new DividerLabel(model.getName()));
    }
    
    private void build(DefaultFormBuilder builder, Renderer renderer) {
        PresModelRenderer<?> presRenderer = (PresModelRenderer<?>) renderer;
        BuilderHelper.appendHelpLabelAndLabel(builder, presRenderer);
        JComponent label = presRenderer.getLabel();
        JLabel validationLabel = presRenderer.getValidationLabel();
        JLabel helpLabel = presRenderer.getHelpLabel();
        JLabel restrictedLabel = presRenderer.getRestrictedLabel();
        boolean validationLabelAdded = false;
        for (JComponent comp : renderer.getComponents()) {
            if (comp == label || comp == validationLabel || comp == helpLabel 
            		|| comp == restrictedLabel) {
                continue;
            }
            // Even though we are not passing labels, we want these components
            // to be layed out in the same way
            JComponent panel = createComponentPanel(comp);
            if ( comp instanceof EditableToggleButton ){
            	EditableToggleButton etb = (EditableToggleButton)comp;
            	if ( etb.isMandatory() ){
            		etb.setBackground(ValidationComponentUtils.getMandatoryBackground());
            		panel.setBackground(ValidationComponentUtils.getMandatoryBackground());
            	}
            	etb.setBackgroundComponent(panel);
            }
            if ( !validationLabelAdded ){
                BuilderHelper.appendLabel(builder, panel, validationLabel, true);
                validationLabelAdded = true;
            }
            else{
                BuilderHelper.appendLabel(builder, panel);
            }
        }
        BuilderHelper.appendEndingGap(builder);
    }

    /**
     * Creates and returns a <code>JPanel</code> containing exactly one row with 
     * the row specification returned by <code>FormView#getDefaultRowSpec()</code> 
     * and <code>comp</code> in that row. The preferred size of the component is 
     * respected inside the panel. This is useful if <code>comp</code> needs to
     * be inserted into a container with its preferred size, but no bigger.
     * 
     * @param comp JComponent to insert into the JPanel.
     * @return JPanel containing <code>comp</code>.
     */
    private JComponent createComponentPanel(JComponent comp) {
        String encodedStrings = "default"; //$NON-NLS-1$
        FormLayout layout = new FormLayout(encodedStrings);
        DefaultFormBuilder panelBuilder = new DefaultFormBuilder(layout, new JPanel());
        panelBuilder.appendRow(FormView.getDefaultRowSpec());
        panelBuilder.add(comp);
        return panelBuilder.getPanel();
    }
}
