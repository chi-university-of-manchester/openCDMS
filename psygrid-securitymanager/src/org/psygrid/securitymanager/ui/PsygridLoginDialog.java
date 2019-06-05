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
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.Properties;

import javax.help.HelpBroker;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import org.jdesktop.swingx.JXLoginPanel;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXLoginPanel.Status;
import org.jdesktop.swingx.auth.LoginService;
import org.jdesktop.swingx.util.WindowUtils;
import org.psygrid.common.proxy.ChangeProxyDialog;
import org.psygrid.securitymanager.Application;
import org.psygrid.securitymanager.actions.ImportAction;
import org.psygrid.securitymanager.security.PersistenceManager;
import org.psygrid.securitymanager.security.SecurityManager;
import org.psygrid.securitymanager.utils.HelpHelper;
import org.psygrid.securitymanager.utils.PropertiesHelper;

/**
 * Login dialog for the PsyGrid data collection application (CoCoA).
 * <p>
 * Required so that the standard dialog created via JXLoginPanel.showLoginDialog
 * can be extended to add an additional command button that launches the
 * "Settings" dialog.
 * 
 * @author Rob Harper
 *
 */
public class PsygridLoginDialog extends JDialog {

    private static final long serialVersionUID = -3795444569220321103L;
    private PsygridLoginPanel loginPanel = null;
    
    private Application app;
    
    public PsygridLoginDialog(Application app, LoginService svc){
        super(app, true);
        this.app = app;
        loginPanel = new PsygridLoginPanel(svc);
        init(loginPanel);
        
        setDefaultCloseOperation(
        	    JDialog.DO_NOTHING_ON_CLOSE);
        		addWindowListener(new WindowAdapter() {
        	    public void windowClosing(WindowEvent we) {
        	    	System.exit(0);
        	    }
        	});
        
    }
    
    /**
     * Get the status of the dialog.
     * <p>
     * Just returns the status of the contained panel.
     * 
     * @return
     */
    public Status getStatus(){
        return loginPanel.getStatus();
    }
    
    /**
     * Initialize the dialog.
     * <p>
     * Importantly, this sets up the command buttons for the dialog, which
     * in our case are Login, Cancel and Settings.
     * 
     * @param panel The panel that is contained by the dialog.
     */
    private void init(final PsygridLoginPanel panel){
        setTitle("Login");
        
        JPanel versionPanel = new JPanel();
        String clientVersion = null;
        
        this.setLayout(new BorderLayout());
        this.add(panel, BorderLayout.NORTH);
        JButton okButton = new JButton(panel.getActionMap().get(JXLoginPanel.LOGIN_ACTION_COMMAND));
        final JButton cancelButton = new JButton(UIManager.getString("Cancel"));
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //change panel status to cancelled!
            	panel.cancel();
                setVisible(false);
                dispose();
                System.exit(0);
            }
        });
        final JButton settingsButton = new JButton("Settings...");
        settingsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ChangeProxyDialog dlg = new ChangeProxyDialog(app, PersistenceManager.getInstance(), SecurityManager.getInstance());
                dlg.setVisible(true);
            }
        });
        final JButton helpButton = new JButton("Help...");
        HelpBroker hb = HelpHelper.getInstance().getHelpBroker();
        hb.enableHelpOnButton(helpButton, "installlogin", hb.getHelpSet());
        
        JButton importButton = new JButton(new ImportAction(this, app));
        
        panel.addPropertyChangeListener("status", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                JXLoginPanel.Status status = (JXLoginPanel.Status)evt.getNewValue();
                switch (status) {
                    case NOT_STARTED:
                        break;
                    case IN_PROGRESS:
                        cancelButton.setEnabled(false);
                        helpButton.setEnabled(false);
                        break;
                    case CANCELLED:
                        cancelButton.setEnabled(true);
                        helpButton.setEnabled(true);
                        pack();
                        break;
                    case FAILED:
                        cancelButton.setEnabled(true);
                        helpButton.setEnabled(true);
                        pack();
                        break;
                    case SUCCEEDED:
                    	try
                    	{
                        	SecurityManager.getInstance().postProcessLogin(app);
                    	} catch (Exception ex)
                    	{
                    		ex.printStackTrace();
                    		//TODO FIXME
                    	}
                    	setVisible(false);
                        dispose();
                        app.repaint();
                        app.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                        app.init();
                        app.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                        app.setToolsEnabled(true);
                }
            }
        });
        cancelButton.setText("Cancel");
        importButton.setText("Use cert");
        int prefWidth = Math.max(cancelButton.getPreferredSize().width, settingsButton.getPreferredSize().width);
        okButton.setPreferredSize(new Dimension(prefWidth, okButton.getPreferredSize().height));
        cancelButton.setPreferredSize(new Dimension(prefWidth, okButton.getPreferredSize().height));
        importButton.setPreferredSize(new Dimension(prefWidth, okButton.getPreferredSize().height));
        settingsButton.setPreferredSize(new Dimension(prefWidth, okButton.getPreferredSize().height));
        helpButton.setPreferredSize(new Dimension(prefWidth, okButton.getPreferredSize().height));
        
        JXPanel buttonPanel = new JXPanel(new GridBagLayout());
        buttonPanel.add(okButton, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.LINE_END, GridBagConstraints.NONE, new Insets(17, 12, 3, 8), 0, 0));
        buttonPanel.add(cancelButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_END, GridBagConstraints.NONE, new Insets(17, 0, 3, 8), 0, 0));
	    buttonPanel.add(settingsButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_END, GridBagConstraints.NONE, new Insets(17, 0, 2, 11), 0, 0));
        buttonPanel.add(importButton, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_END, GridBagConstraints.NONE, new Insets(3, 0, 11, 8), 0, 0));        
	    buttonPanel.add(helpButton, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_END, GridBagConstraints.NONE, new Insets(3, 0, 11, 11), 0, 0));
        
        add(buttonPanel, BorderLayout.CENTER);            
        addWindowListener(new WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                panel.cancelLogin();
            }
        });
        
        try{
            Properties props = new Properties();
            props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("client.properties"));
            clientVersion = props.getProperty("client.version");
        }
        catch(IOException ex){
            clientVersion = "Unknown";
        }
        catch(NullPointerException ex){
            //if Properties#load can't find the properties file then
            //it seems to (very helpfully!) throw an NPE
            clientVersion = "Unknown";
        }
        
        JLabel versionLabel = new JLabel(PropertiesHelper.getPropertyHelper().getStringFor("org.psygrid.securitymanager.versionlabel")+ " " +clientVersion);
        versionPanel.add(versionLabel, BorderLayout.CENTER);
        
        this.add(versionPanel, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(okButton);
        setResizable(false);
        KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        ActionListener closeAction = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        };
        getRootPane().registerKeyboardAction(closeAction, ks, JComponent.WHEN_IN_FOCUSED_WINDOW);

        pack();
        setLocation(WindowUtils.getPointForCentering(this));
    }
    
}
