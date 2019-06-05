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
import javax.swing.JLabel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.collection.entry.FormView;
import org.psygrid.collection.entry.renderer.PresModelRenderer;
import org.psygrid.collection.entry.renderer.Renderer;
import org.psygrid.collection.entry.renderer.RendererHandler;
import org.psygrid.collection.entry.ui.DividerLabel;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import org.psygrid.data.model.hibernate.Entry;

/**
 * Generic builder that tries to build entries on a best-effort basis. By default,
 * it is configured to be called last to allow more specialised BuilderSPI
 * implementations to override it.
 * 
 * @author Ismael Juma (ismael@juma.me.uk)
 *
 */
public class EntryBuilder implements BuilderSPI {

    private static final Log LOG = LogFactory.getLog(EntryBuilder.class);
    
    /**
     * @return <code>true</code> in all cases.
     * 
     * {@inheritDoc}
     */
    public boolean canHandle(Entry modelObject, Entry parent) {
        return true;
    }

    /**
     * Builds components under the assumption that they are have a label
     * that should be layed out in the first row, followed by one or more 
     * components that should be layed out in the second row.
     * Appends divider after each entry (for use by the DSDesigner).
     * 
     * {@inheritDoc}
     */
    public void build(BuilderData builderData) {
        RendererHandler rendererHandler = builderData.getRendererHandler();
        Entry model = builderData.getModel();
        if (LOG.isInfoEnabled()) {
            LOG.info("Building entry... [name: " + model.getName() +  //$NON-NLS-1$
                    "], [displayText: " + model.getDisplayText() + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        
        DefaultFormBuilder builder = builderData.getBuilder();
        int rowIndex = builderData.getRowIndex();
        Renderer renderer = rendererHandler.getRenderer(model, 
                builderData.getModelParentResponse(), rowIndex, null, builderData.isCopy());
        build(builder, renderer);
        
        BuilderHelper.appendDivider(builder, new DividerLabel(model.getName()));
    }

    private void build(DefaultFormBuilder builder, Renderer renderer) {
        PresModelRenderer<?> presRenderer = (PresModelRenderer<?>) renderer;
        if ( null == presRenderer.getLabel() ){
            List<JComponent> comps = renderer.getComponents();
            for (int i = 0, c = comps.size(); i < c; ++i) {
                if (i == 0) {
                    BuilderHelper.appendLabel(builder, comps.get(i));
                    builder.appendRow(FormView.getDefaultRowSpec());
                    builder.nextLine();
                } else {
                    builder.append(comps.get(i));
                }
            }
        }
        else{
            BuilderHelper.appendHelpLabelAndLabel(builder, presRenderer);
            JComponent label = presRenderer.getLabel();
            JLabel validationLabel = presRenderer.getValidationLabel();
            JLabel helpLabel = presRenderer.getHelpLabel();
            JLabel restrictedLabel = presRenderer.getRestrictedLabel();
            for (JComponent comp : renderer.getComponents()) {
                if (comp == label || comp == validationLabel || comp == helpLabel 
                		|| comp == restrictedLabel) {
                    continue;
                }
                // Even though we are not passing labels, we want these components
                // to be layed out in the same way
                BuilderHelper.appendLabel(builder, comp, validationLabel, false);
            }
        }
        BuilderHelper.appendEndingGap(builder);
    }
}
