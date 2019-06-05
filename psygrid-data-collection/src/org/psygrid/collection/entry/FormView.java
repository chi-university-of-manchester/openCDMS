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


package org.psygrid.collection.entry;

import java.awt.Dimension;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.collection.entry.event.PostSectionBuiltEvent;
import org.psygrid.collection.entry.event.SectionBuiltEvent;
import org.psygrid.collection.entry.model.SectionPresModel;
import org.psygrid.collection.entry.persistence.PersistenceManager;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.validation.view.ValidationResultViewFactory;
import org.psygrid.data.model.hibernate.Entry;

public class FormView {

    private static final long serialVersionUID = 1L;

    private final static String widthText;
    private final static String heightText;
    static {
        final Dimension d = new JLabel(ValidationResultViewFactory.getErrorIcon()).
                getPreferredSize();
        widthText = d.width + "px"; //$NON-NLS-1$
        heightText = d.height + "px"; //$NON-NLS-1$
    }
    private static final String COLUMN_SPECS = 
        "max(pref;" + widthText + "), 1dlu, 150dlu, default:grow"; //$NON-NLS-1$ //$NON-NLS-2$
    
    private static final Log LOG = LogFactory.getLog(FormView.class);

    private static final RowSpec ROW_SPEC = new RowSpec("max(pref;" + heightText + ")");  //$NON-NLS-1$//$NON-NLS-2$

    private DefaultFormBuilder builder;

    private Application application;

    private ApplicationModel model;

    public FormView(ApplicationModel model) {
    	this.model = model;
    }
    
    public FormView(Application application) {
        this.application = application;
    }

    private FormLayout getFormLayout() {
        return new FormLayout(COLUMN_SPECS);
    }

    public JComponent createPanel() {
        FormLayout fl = getFormLayout();
        JPanel panel = new JPanel();
        //JPanel panel = new FormDebugPanel();
        builder = new DefaultFormBuilder(fl, panel);
        
        if (application != null) {
            model = application.getModel();
        }
        
        if (!buildSectionOccurrence(model.getCurrentSectionOccPresModel(), 
                model.getCurrentEntries())) {
            return null;
        }
        
        if (application != null) {
            model.registerValidationListener();
            model.validateSection(true);
        }
        return panel;
    }
    
    public final static String getColumnSpecs() {
        return COLUMN_SPECS;
    }
    
    public final static RowSpec getDefaultRowSpec() {
        return ROW_SPEC;
    }

    // Render a specific section occurrence on one page
    private boolean buildSectionOccurrence(SectionPresModel sectionOccPresModel, 
            List<Entry> sectionEntries) {

    	builder.setBorder(Borders.DLU2_BORDER);
        builder.appendRelatedComponentsGapRow();
        
        if (application != null) {
             model = application.getModel();
        }
        
        if (LOG.isInfoEnabled()) {
            LOG.info("Building section occurrence [name, id]: " +  //$NON-NLS-1$
                    sectionOccPresModel.getSectionOccurrence().getName() + ", " //$NON-NLS-1$
                    + sectionOccPresModel.getSectionOccurrence().getId());
        }
        
        try {
            for (Entry entry : sectionEntries) {
            	//don't display if the entry is locked
            	if (!entry.isLocked()) {
                    model.getBuilderHandler().build(builder, entry, null, null, false,
                            0);
            	}
            }

            model.getBuilderHandler().fireSectionBuiltEvent(
                    new SectionBuiltEvent(this, sectionOccPresModel));
            model.getBuilderHandler().firePostSectionBuiltEvent(
                    new PostSectionBuiltEvent(this, sectionOccPresModel));
            model.getCurrentRendererHandler().setRendered(true);
            return true;
        } catch (RuntimeException e) {
        	if (application != null) {
                application.clear(false);
        	}
        	
            String title = EntryMessages
                    .getString("FormView.errorRenderingTitle"); //$NON-NLS-1$
            if (LOG.isErrorEnabled()) {
                LOG.error(title, e);
            }
            String baseDir = PersistenceManager.getInstance()
                    .getBaseDirLocation();

            String message = EntryMessages.getString("FormView.emailSupport") + baseDir; //$NON-NLS-1$
            JOptionPane.showMessageDialog(application, message, title,
                    JOptionPane.ERROR_MESSAGE);
            
        }
        return false;
    }
}
