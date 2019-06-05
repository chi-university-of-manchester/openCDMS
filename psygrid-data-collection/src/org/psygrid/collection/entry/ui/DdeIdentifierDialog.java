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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.WindowConstants;

import org.jdesktop.swingx.util.WindowUtils;
import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.event.IdentifierEvent;
import org.psygrid.collection.entry.event.IdentifierListener;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Record;

/**
 * Dialog shown when creating a new secondary record to be linked
 * to a primary record.
 * <p>
 * The only difference with {@link NewIdentifierDialog} is that a 
 * list of allowed groups is passed in when initializing the panel.
 * 
 * @author Rob Harper
 *
 */
public class DdeIdentifierDialog extends JDialog {

	private static final long serialVersionUID = 6588503184722871335L;

	private LinkNewIdentifierPanel contentPanel;

	private Record secRecord;
	
	public DdeIdentifierDialog(Application parent, DataSet dataset, List<String> groups) throws IdentifierPanelException {
		super(parent, true);
		init(parent, dataset, groups);
	}
	
	private void init(Application parent, DataSet dataset, List<String> groups) throws IdentifierPanelException {
        setTitle(Messages.getString("IdentifierDialog.screeningCode")); //$NON-NLS-1$
        // Safe not to remove
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
        getContentPane().setLayout(new BorderLayout());
        contentPanel = new LinkNewIdentifierPanel(dataset, groups, this);
        contentPanel.addIdentifierListener(new IdentifierListener() {
        	public void identifierChosen(IdentifierEvent event) {
                dispose();
            }
        });
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        pack();
        Dimension size = getSize();
        if (size.width < 550) {
            setSize(550, size.height);   
        }
        setLocation(WindowUtils.getPointForCentering(this));
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        
        contentPanel.addIdentifierListener(new IdentifierListener(){
			public void identifierChosen(IdentifierEvent event) {
				secRecord = event.getRecord();
				dispose();
			}        	
        });
    }

    public LinkNewIdentifierPanel getContentPanel() {
        return contentPanel;
    }

	public Record getSecRecord() {
		return secRecord;
	}
    	
}

