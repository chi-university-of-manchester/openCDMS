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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jdesktop.swingx.util.WindowUtils;
import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.event.ConnectionAvailableListener;
import org.psygrid.collection.entry.remote.RemoteManager;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;

/**
 * Displayed when the repository can't be reached and always-online mode is
 * enabled. It disappears automatically when the repository can be reached
 * and allows the user to manually force a retry and to exit the application.
 */
public class NoNetworkDialog extends JDialog {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private DefaultFormBuilder builder;

    private JLabel messageLabel;
    
    private JButton exitButton;
    private JButton retryButton;

    private ConnectionAvailableListener connectionAvailableListener;
    
    private final Application application;

    
    public NoNetworkDialog(Application parent) {
        super(parent, Messages.getString("NoNetworkDialog.title"), true); //$NON-NLS-1$
        this.application = parent;
        initBuilder();
        initComponents();
        initEventHandling();
        build();
        pack();
        Dimension size = getSize();
        if (size.width < 415)
            setSize(415, size.height);   
        setLocation(WindowUtils.getPointForCentering(this));
    }

    private void initComponents() {
        messageLabel = new JLabel(Messages.getString("NoNetworkDialog.message")); //$NON-NLS-1$
        retryButton = new JButton("Retry");
        exitButton = new JButton("Exit Application");
    }

    private void initEventHandling() {
        connectionAvailableListener = new ConnectionAvailableListener() {
            public void statusChanged(boolean available) {
                if (available)
                    dispose();
            }
        };
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                application.removeConnectionAvailableListener(connectionAvailableListener);
            }
        });
        
        application.addConnectionAvailableListener(connectionAvailableListener);
        
        /* 
         * We check if the connection is available after adding the listener
         * to avoid any race conditions.
         */
        if (RemoteManager.getInstance().isConnectionAvailable())
            dispose();
        
        exitButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               application.exitWithoutConfirmation(true);
            } 
        });
        retryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (RemoteManager.getInstance().isConnectionAvailable()) {
                    dispose();
                }
            }
        });
    }

    private void initBuilder() {
        builder = new DefaultFormBuilder(new FormLayout("default"), //$NON-NLS-1$
                new JPanel());
        builder.setDefaultDialogBorder();
    }

    private void build() {
        builder.append(messageLabel);
        
        builder.appendUnrelatedComponentsGapRow();
        builder.nextLine(2);
        
        JPanel buttonsPanel = ButtonBarFactory.buildRightAlignedBar(retryButton,
                exitButton);
        builder.append(buttonsPanel, builder.getColumnCount());
        setContentPane(builder.getPanel());
    }
}
