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

package org.psygrid.collection.entry.ui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import org.jdesktop.swingx.util.WindowUtils;
import org.psygrid.collection.entry.Application;
import org.psygrid.data.model.hibernate.Record;
import org.psygrid.esl.model.ISubject;

public abstract class EslDialog extends ApplicationDialog   {

    protected EslPanel contentPanel;
    
    protected Application application;
    
    protected final Record record;
    
    public EslDialog(Application parent, 
            Record record) {
        this(parent, record, null);
    }

    public EslDialog(Application parent, 
            Record record, ISubject subject) {
        super(parent, Messages.getString("EslDialog.title"), true);
        this.application = parent;
        this.record = record;
        init(subject);
    }

    /**
     * When no subject or record is provided, the dialog
     * is opened in 'search' mode.
     * 
     * @param parent
     */
    public EslDialog(Application application, ApplicationDialog parent) {
        super(parent, application, Messages.getString("EslDialog.title"), true);
        this.application = application;
        this.record = null;
    }
    
    public void init(ISubject subject) {
        getContentPane().setLayout(new BorderLayout());
        contentPanel = createContentPanel(subject);
        JScrollPane scroller = new JScrollPane(contentPanel);
        getContentPane().add(scroller, BorderLayout.CENTER);
        pack();
        initListeners();
        setLocation(WindowUtils.getPointForCentering(this));
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    }
    
    public abstract void initListeners();
    
    public JPanel getContentPanel() {
        return contentPanel;
    }

    public abstract EslPanel createContentPanel(ISubject subject);
    
}
