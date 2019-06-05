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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.jdesktop.swingx.util.WindowUtils;
import org.psygrid.collection.entry.EntryMessages;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;

public class AnnotationDialog extends JDialog {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private JLabel annotationLabel;
    private JTextArea annotationField;
    private DefaultFormBuilder builder;

    private JButton okButton;

    private JButton cancelButton;
    
    private boolean okSelected = false;
    
    public AnnotationDialog(JFrame frame) {
        super(frame, true);
        setTitle(Messages.getString("AnnotationDialog.enter")); //$NON-NLS-1$
        init(Messages.getString("AnnotationDialog.reason")); //$NON-NLS-1$
    }
    
    public AnnotationDialog(JFrame frame, String title, String labelText) {
        super(frame, title, true);
        init(labelText);
    }
    
    private void init(String labelText) {
        initBuilder();
        initComponents(labelText);
        initEventHandling();
        build();
        
        pack();
        setLocation(WindowUtils.getPointForCentering(this));
    }
    
    public boolean isOkSelected() {
        return okSelected;
    }
    
    private void initEventHandling() {
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                acceptChanges();
            }
        });
        
    }

    private void acceptChanges() {
        if (annotationField.getText().length() == 0) {
            JOptionPane.showMessageDialog(this, Messages.getString("AnnotationDialog.errorMessage"), //$NON-NLS-1$
                    Messages.getString("AnnotationDialog.errorTitle"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
            return;
        }
        okSelected = true;
        dispose();
    }

    private void initComponents(String labelText) {
        annotationLabel = new JLabel(labelText);
        annotationField = new JTextArea(3, 10);
        annotationField.setLineWrap(true);
        annotationField.setWrapStyleWord(true);
        okButton = new JButton(EntryMessages.getString("Entry.ok")); //$NON-NLS-1$
        cancelButton = new JButton(EntryMessages.getString("Entry.cancel")); //$NON-NLS-1$
    }
    
    private void initBuilder() {
        builder = new DefaultFormBuilder(new FormLayout("default"), new JPanel()); //$NON-NLS-1$
        builder.setDefaultDialogBorder();
    }
    
    public String getAnnotation() {
        return annotationField.getText();
    }
    
    private void build() {
        builder.append(annotationLabel);
        builder.append(annotationField);
        
        builder.appendUnrelatedComponentsGapRow();
        builder.nextLine(2);
        JPanel buttonsPanel = ButtonBarFactory.buildOKCancelBar(okButton, 
                cancelButton);
        builder.append(buttonsPanel);
        
        getContentPane().add(builder.getPanel());
    }
}
