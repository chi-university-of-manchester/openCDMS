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
import java.awt.Dimension;

import javax.swing.JDialog;
import javax.swing.WindowConstants;

import org.jdesktop.swingx.util.WindowUtils;
import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.event.IdentifierEvent;
import org.psygrid.collection.entry.event.IdentifierListener;
import org.psygrid.data.model.hibernate.DataSet;

/**
 * Dialog used to select the group, site and consultant for
 * a new record, and generate an identifier for the record.
 * 
 * @author Rob Harper
 *
 */
public class NewIdentifierDialog extends JDialog  {
  
    private static final long serialVersionUID = 1L;
    
    private NewIdentifierPanel contentPanel; 
    
    protected final boolean forceRecordCreation;
    
    public NewIdentifierDialog(Application parent, DataSet dataSet) throws IdentifierPanelException {
        super(parent, true);
        forceRecordCreation = false;
        init(parent, dataSet);
        
    }
    
    
    protected NewIdentifierDialog(Application parent, DataSet dataSet, boolean forceRecordCreation) throws IdentifierPanelException {
        super(parent, true);
        this.forceRecordCreation = forceRecordCreation;
        init(parent, dataSet);
    }
    
    
    private void init(Application parent, DataSet dataSet) throws IdentifierPanelException {
        setTitle(Messages.getString("IdentifierDialog.screeningCode")); //$NON-NLS-1$

        getContentPane().setLayout(new BorderLayout());
        contentPanel = (this.forceRecordCreation ? new NewEnforceRecordCreationIdentifierPanel(dataSet, this) : new NewIdentifierPanel(dataSet, this));
        contentPanel.addIdentifierListener(new IdentifierListener() {
            public void identifierChosen(IdentifierEvent event) {
                dispose();
            }
        });
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        pack();
        Dimension size = getSize();
        if (size.width < 415) {
            setSize(415, size.height);   
        }
        setLocation(WindowUtils.getPointForCentering(this));
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    }
    
    public NewIdentifierPanel getContentPanel() {
        return contentPanel;
    }
    
}
