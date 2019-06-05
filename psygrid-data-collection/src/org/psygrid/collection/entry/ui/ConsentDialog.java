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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;

import org.jdesktop.swingx.util.WindowUtils;
import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.EntryMessages;
import org.psygrid.collection.entry.event.ConsentEvent;
import org.psygrid.collection.entry.event.ConsentListener;

import com.jgoodies.forms.builder.ButtonBarBuilder;
import org.psygrid.data.model.hibernate.Record;

public class ConsentDialog extends JDialog   {

    private static final long serialVersionUID = 1L;

    private ConsentPanel contentPanel;
    
    private boolean okClicked;
    
    protected JButton cancelButton = null;
    
    public ConsentDialog(Application parent, 
            Record record) {
        super(parent, true);
        init(record);
    }
    
    private void init(Record record) {
        setTitle(Messages.getString("ConsentDialog.title")); //$NON-NLS-1$
        getContentPane().setLayout(new BorderLayout(15,20));
        
        contentPanel = new ConsentPanel(record);
        contentPanel.addConsentListener(new ConsentListener() {
            public void consentChosen(ConsentEvent event) {
                okClicked = event.isOkClicked();
                dispose();
            }
        });

        
        //Add study entry dates
        JPanel entryDates = contentPanel.getStartDatePanel();  
        getContentPane().add(entryDates, BorderLayout.NORTH);
        
        //Add consent forms
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        
        
        contentPanel.setAutoscrolls(true);
        JScrollPane scroll = new JScrollPane(contentPanel);
        scroll.setBorder(null);
        //Scroll bar added for long consent forms. See Bug #866
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        getContentPane().add(scroll, BorderLayout.CENTER);

        
        //Add the okay and cancel buttons at the bottom of the screen
        
        JButton okButton = new JButton(EntryMessages.getString("Entry.ok")); //$NON-NLS-1$
        okButton.setEnabled(true);
        okButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
                contentPanel.setConsent();
           }
        });
        cancelButton = new JButton(EntryMessages.getString("Entry.cancel")); //$NON-NLS-1$
        cancelButton.setEnabled(true);
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                contentPanel.fireConsentEvent(new ConsentEvent(this, false));
            }
         });
        
        //The same as ButtonBarFactory.buildOKCancelBar(okButton, cancelButton)
		//but with increased padding to the side and bottom.
		ButtonBarBuilder builder = new ButtonBarBuilder();
        builder.addGlue();
        builder.addGriddedButtons(new JButton[]{okButton, cancelButton});
        builder.appendRelatedComponentsGapColumn();
        builder.appendUnrelatedComponentsGapRow();
        JPanel okButtonPanel = builder.getPanel();
        getContentPane().add(okButtonPanel, BorderLayout.SOUTH);	
        
        pack();

        Dimension size = getSize();
        //I don't think this works because the size is reported as
        // zero thanks to the JTextField
        if (size.width < 600) {
            setSize(600, size.height);   
        }
        
        //See Bug #866
        double height = size.height*1.08;
		setSize(getSize().width, (int)height);  
		setLocation(WindowUtils.getPointForCentering(this));
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }
        
    public JPanel getContentPanel() {
        return contentPanel;
    }

    public boolean isOkClicked() {
        return okClicked;
    }
    
}
