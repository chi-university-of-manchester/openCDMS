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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.Properties;

import javax.help.HelpBroker;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import org.jdesktop.swingx.auth.LoginEvent;
import org.jdesktop.swingx.auth.LoginListener;
import org.jdesktop.swingx.auth.LoginService;
import org.jdesktop.swingx.util.WindowUtils;
import org.psygrid.collection.entry.security.EntryLoginService;
import org.psygrid.collection.entry.security.SecurityManager;
import org.psygrid.collection.entry.ui.PsygridLoginPanel.Status;
import org.psygrid.collection.entry.util.HelpHelper;
import org.psygrid.common.security.LoginInterfaceFrame;
import org.psygrid.common.ui.WrappedJOptionPane;

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
public class PsygridLoginDialog extends JDialog implements LoginListener {

    private static final long serialVersionUID = -3795444569220321103L;

    private PsygridLoginPanel loginPanel = null;

    private String title = null;
    
    private JButton settingsButton;
    
    private LoginService svc;
    
    private int allowedLoginAttempts = 3;
    private int failedLoginAttempts = 0;
    
    public PsygridLoginDialog(JFrame parent, EntryLoginService svc, String title, String username){
        super(parent, true);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        loginPanel = new PsygridLoginPanel(svc);
        loginPanel.setAndLockUserName(username);
        this.svc = svc;
        this.title = title;
        init(loginPanel);
    }
    
    public PsygridLoginDialog(JFrame parent, EntryLoginService svc, String title){
        
        super(parent, true);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        loginPanel = new PsygridLoginPanel(svc);
        this.svc = svc;
        this.title = title;
        init(loginPanel);
    }
    
    public PsygridLoginDialog(JFrame parent, EntryLoginService svc){
        
        super(parent, true);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        loginPanel = new PsygridLoginPanel(svc);
        this.svc = svc;
        
        init(loginPanel);
        
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
        setTitle(Messages.getString("PsygridLoginDialog.panelTitle"));
        svc.addLoginListener(this);
        this.setLayout(new BorderLayout());
        this.add(panel, BorderLayout.NORTH);
        
        JPanel versionPanel = new JPanel();
        String clientVersion = null;

        if (title == null)
        {
            try{
                Properties props = new Properties();
                props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("client.properties"));
                clientVersion = props.getProperty("client.version");
            }
            catch(IOException ex){
                clientVersion = Messages.getString("PsygridLoginDialog.unknownClientVersion");
            }
            catch(NullPointerException ex){
                //if Properties#load can't find the properties file then
                //it seems to (very helpfully!) throw an NPE
                clientVersion = Messages.getString("PsygridLoginDialog.unknownClientVersion");
            }
            
            JLabel versionLabel = new JLabel(Messages.getString("PsygridLoginDialog.versionLabel")+clientVersion);
            versionPanel.add(versionLabel);
        } else {
        	versionPanel.add(new JLabel(title), BorderLayout.SOUTH);
        }
        
        this.add(versionPanel, BorderLayout.SOUTH);
        
        final JButton okButton = new JButton(panel.getActionMap().get(PsygridLoginPanel.LOGIN_ACTION_COMMAND));
        final JButton cancelButton = new JButton(UIManager.getString("Cancel"));
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //change panel status to cancelled!
                panel.cancel();
                setVisible(false);
                dispose();
            }
        });
        settingsButton = new JButton(Messages.getString("PsygridLoginDialog.settingsButtonText"));
        settingsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                LoginInterfaceFrame jf = (LoginInterfaceFrame)getParent();
                ChangeSettingsDialog dlg = new ChangeSettingsDialog(jf, false);
                dlg.setVisible(true);
            }
        });
        final JButton helpButton = new JButton(Messages.getString("PsygridLoginDialog.helpButtonText"));
        HelpBroker hb = HelpHelper.getInstance().getHelpBroker();
        if ( null != hb ){
        	hb.enableHelpOnButton(helpButton, "installlogin", hb.getHelpSet());
        }
        else{
        	helpButton.addActionListener(new ActionListener() {
        		public void actionPerformed(ActionEvent e) {
        			WrappedJOptionPane.showWrappedMessageDialog(
        					PsygridLoginDialog.this, "Help is not available", "Error", 
        					WrappedJOptionPane.INFORMATION_MESSAGE);
        		}
        	});
        }
        panel.addPropertyChangeListener("status", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                PsygridLoginPanel.Status status = (PsygridLoginPanel.Status)evt.getNewValue();
                PsygridLoginPanel panel = (PsygridLoginPanel)evt.getSource();
                switch (status) {
                    case NOT_STARTED:
                        break;
                    case IN_PROGRESS:
                        cancelButton.setEnabled(false);
                        settingsButton.setEnabled(false);
                        helpButton.setEnabled(false);
                        break;
                    case CANCELLED:
                        cancelButton.setEnabled(true);
                        settingsButton.setEnabled(true);
                        helpButton.setEnabled(true);
                        pack();
                        break;
                    case LOCKED:
                    	okButton.setEnabled(false);
                        cancelButton.setEnabled(true);
                        settingsButton.setEnabled(false);
                        helpButton.setEnabled(true);
                        pack();
                        break;
                    case FAILED:
                        cancelButton.setEnabled(true);
                        settingsButton.setEnabled(true);
                        helpButton.setEnabled(true);
                        pack();
                        break;
                    case SUCCEEDED:
                        setVisible(false);
                        dispose();
                }
            }
        });
        cancelButton.setText(Messages.getString("PsygridLoginDialog.cancelButtonText"));
        settingsButton.setText(Messages.getString("PsygridLoginDialog.settingsButtonText"));

        int prefWidth = Math.max(cancelButton.getPreferredSize().width, okButton.getPreferredSize().width);
        prefWidth = Math.max(prefWidth, settingsButton.getPreferredSize().width);
        cancelButton.setPreferredSize(new Dimension(prefWidth, okButton.getPreferredSize().height));
        settingsButton.setPreferredSize(new Dimension(prefWidth, okButton.getPreferredSize().height));  
        okButton.setPreferredSize(new Dimension(prefWidth, okButton.getPreferredSize().height));
        helpButton.setPreferredSize(new Dimension(prefWidth, okButton.getPreferredSize().height));

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.add(okButton, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.LINE_END, GridBagConstraints.NONE, new Insets(17, 12, 11, 8), 0, 0));
        buttonPanel.add(cancelButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_END, GridBagConstraints.NONE, new Insets(17, 0, 11, 8), 0, 0));
        buttonPanel.add(settingsButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_END, GridBagConstraints.NONE, new Insets(17, 0, 11, 8), 0, 0));
        buttonPanel.add(helpButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_END, GridBagConstraints.NONE, new Insets(17, 0, 11, 11), 0, 0));
        
        add(buttonPanel, BorderLayout.CENTER);            
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                panel.cancelLogin();
            }
        });


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
    
    public void configureForSystemLock() {
    	setTitle(Messages.getString("PsygridLoginDialog.inactivityTimeoutTitle"));
    	this.loginPanel.setMessage(Messages.getString("PsygridLoginDialog.inactivityTimeoutMessage"));
    	settingsButton.setEnabled(false);
    	String userName = SecurityManager.getInstance().getUserName();
    	this.loginPanel.setAndLockUserName(userName);
    }

	public void loginCanceled(LoginEvent arg0) {
		// TODO Auto-generated method stub
	}

	public void loginFailed(LoginEvent arg0) {
		
		failedLoginAttempts++;
		
		if(SecurityManager.getInstance().getLoginStatus() == SecurityManager.LoginStatus.PreInitialLogin)
			return;
		
		this.settingsButton.setEnabled(false);
		String userName = SecurityManager.getInstance().getUserName();
		if(!userName.equals(loginPanel.getUserName())){
			loginPanel.setErrorMessage(Messages.getString("PsygridLoginDialog.incorrectUserNameMessage_p1") + userName + 
					Messages.getString("PsygridLoginDialog.incorrectUserNameMessage_p2") + (allowedLoginAttempts - failedLoginAttempts) + 
					Messages.getString("PsygridLoginDialog.incorrectUserNameMessage_p3"));
			this.loginPanel.setUserName(userName);
		}else{
			//It's the user's pasword.
			loginPanel.setErrorMessage(Messages.getString("PsygridLoginDialog.incorrectPasswordMessage_p1") + (allowedLoginAttempts - failedLoginAttempts) + 
					Messages.getString("PsygridLoginDialog.incorrectPasswordMessage_p2"));
		}

		if(failedLoginAttempts >= allowedLoginAttempts){
			//Set parameters up to be same as a 'cancel'.
			loginPanel.cancel();
			setVisible(false);
            dispose();
		}
	}

	public void loginStarted(LoginEvent arg0) {
	}

	public void loginSucceeded(LoginEvent arg0) {
	}
    
}
