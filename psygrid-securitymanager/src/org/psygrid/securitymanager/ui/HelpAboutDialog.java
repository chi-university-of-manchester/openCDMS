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

package org.psygrid.securitymanager.ui;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Properties;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.psygrid.securitymanager.utils.IconsHelper;
import org.psygrid.securitymanager.utils.PropertiesHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.util.WindowUtils;


public class HelpAboutDialog extends JDialog {

	private static final Log LOG = LogFactory.getLog(HelpAboutDialog.class);
	
	private final static int HELP_DIALOG_WIDTH = 380;
	private final static int HELP_DIALOG_HEIGHT = 200;
	
    private static final long serialVersionUID = 1L;

    private JButton okButton;
    
    private JLabel psygridLabel;
    private JLabel clientVersionLabel;
    private JLabel copyrightLabel;
    private JLabel supportLabel;
    
    public HelpAboutDialog(JFrame parent) throws HeadlessException {
        super(parent, PropertiesHelper.getPropertyHelper().getStringFor("org.psygrid.securitymanager.aboutheader"), true);
        ((java.awt.Frame)getOwner()).setIconImage(IconsHelper.getInstance().getImageIcon("psygrid.jpg").getImage());
        initComponents();
        initEventHandling();
        build();
        pack();
        setLocation(WindowUtils.getPointForCentering(this));
        setSize(new Dimension(HELP_DIALOG_WIDTH, HELP_DIALOG_HEIGHT));
        setMinimumSize(new Dimension(HELP_DIALOG_WIDTH, HELP_DIALOG_HEIGHT));
        setMaximumSize(new Dimension(HELP_DIALOG_WIDTH, HELP_DIALOG_HEIGHT));
        setPreferredSize(new Dimension(HELP_DIALOG_WIDTH, HELP_DIALOG_HEIGHT));
        setResizable(false);
    }
    
    private void initComponents() {
        psygridLabel = new JLabel(IconsHelper.getInstance().getImageIcon("opencdmslogo2.png"));
        String clientVersion = null;
        
        try {
            Properties props = new Properties();
            props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("client.properties"));
            clientVersion = props.getProperty("client.version");
        } catch (IOException ioex) {
        	LOG.error("Error fetching the version ", ioex);
        	clientVersion = "Version not known";
        }
        
        clientVersionLabel = new JLabel(PropertiesHelper.getPropertyHelper().getStringFor("org.psygrid.securitymanager.versionlabel")+ " " + clientVersion);
        copyrightLabel = new JLabel(PropertiesHelper.getPropertyHelper().getStringFor("org.psygrid.securitymanager.copyright"));
        supportLabel = new JLabel(PropertiesHelper.getPropertyHelper().getStringFor("org.psygrid.securitymanager.helpsupport"));
        okButton = new JButton(PropertiesHelper.getPropertyHelper().getStringFor("org.psygrid.securitymanager.actions.ok")); //$NON-NLS-1$
    }

    private void initEventHandling() {
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }
    
    private void build() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setAlignmentX(BoxLayout.LINE_AXIS);
        
        //center align everythign
        psygridLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        clientVersionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        copyrightLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        supportLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        mainPanel.add(psygridLabel);
        mainPanel.add(Box.createVerticalStrut(12));
        mainPanel.add(clientVersionLabel);
        mainPanel.add(copyrightLabel);
        mainPanel.add(supportLabel);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(okButton);
        
        getContentPane().add(mainPanel,BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }
    
}
