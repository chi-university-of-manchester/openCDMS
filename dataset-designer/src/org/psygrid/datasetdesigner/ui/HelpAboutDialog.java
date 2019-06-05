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

package org.psygrid.datasetdesigner.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

import java.io.IOException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.psygrid.datasetdesigner.utils.IconsHelper;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.util.WindowUtils;

/**
 * JDialog containing About information
 * @author pwhelan
 */
public class HelpAboutDialog extends JDialog {

	private static final Log LOG = LogFactory.getLog(HelpAboutDialog.class);
	
	private final static int HELP_DIALOG_WIDTH = 360;
	private final static int HELP_DIALOG_HEIGHT = 200;
	
    private static final long serialVersionUID = 1L;

    private JButton okButton;
    private JLabel psygridLabel;
    private JLabel clientVersionLabel;
    private JLabel copyrightLabel;
    private JLabel supportLabel;
    
    /**
     * Constructor - sets up the JDialog
     * @param parent
     * @throws HeadlessException
     */
    public HelpAboutDialog(JFrame parent) throws HeadlessException {
        super(parent, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.aboutdesigner"), true);
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
        setVisible(true);
    }
    
    /**
     * Initialise the components required with strings from the properties files
     */
    private void initComponents() {
        psygridLabel = new JLabel(IconsHelper.getInstance().getImageIcon("opencdmslogo2.png"));
        String clientVersion = null;
        
        try {
            Properties props = new Properties();
            props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("client.properties"));
            clientVersion = props.getProperty("client.version");
            clientVersionLabel = new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.versionlabel")+ " " + clientVersion);
        } catch (IOException ioex) {
        	LOG.error("Error fetching the version ", ioex);
        	clientVersionLabel = new JLabel("Version not known");
        }
        
        copyrightLabel = new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.copyright"));
        supportLabel = new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.helpsupport"));
        okButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.actions.ok")); //$NON-NLS-1$
    }

    /**
     * Initialise the event handling
     */
    private void initEventHandling() {
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }
    
    /**
     * Construct panel 
     */
    private void build() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setAlignmentX(BoxLayout.LINE_AXIS);
        
        //center align everything
        psygridLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        clientVersionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        copyrightLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        supportLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        okButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        mainPanel.add(psygridLabel);
        mainPanel.add(Box.createVerticalStrut(12));
        mainPanel.add(clientVersionLabel);
        mainPanel.add(copyrightLabel);
        mainPanel.add(supportLabel);
        mainPanel.add(Box.createVerticalStrut(8));
        mainPanel.add(okButton);
        getContentPane().add(mainPanel);
    }
    
}
