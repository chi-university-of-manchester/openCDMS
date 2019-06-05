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

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.ConnectException;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.util.WindowUtils;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;

import org.psygrid.common.security.PasswordStrengthCheck;
import org.psygrid.securitymanager.ExceptionsHelper;

import org.psygrid.securitymanager.utils.PropertiesHelper;

import org.psygrid.securitymanager.SwingWorkerExecutor;

import org.psygrid.securitymanager.security.SecurityManager;

import org.psygrid.security.PGSecurityException;

public class ChangePasswordDialog extends JDialog {


	/**
     * 
     */
    protected static final long serialVersionUID = 1L;

    protected DefaultFormBuilder builder;

    protected JButton okButton;

    protected JButton cancelButton;

    protected JPasswordField password1;

    protected JLabel password1Label;
    
    protected JLabel passwordStrengthLabel;

    protected JLabel passwordStrength;
    
    protected JPasswordField currentPassword;

    protected JLabel currentPasswordLabel;   

    protected JPasswordField password2;

    protected JLabel password2Label;
    
    protected JFrame parent;

    public ChangePasswordDialog(JFrame parent)   {
        super(parent, "Change Password", true);
        this.parent = parent;
        initBuilder();
        initComponents();
        initEventHandling();
        build();
        pack();
        setLocation(WindowUtils.getPointForCentering(this));
    }

    protected void initComponents()   {
        currentPassword = new JPasswordField();
        currentPassword.setEditable(true);
        currentPasswordLabel = new JLabel(PropertiesHelper.getPropertyHelper().getStringFor("ChangePasswordDialog.currentPasswordLabel"));
        passwordStrengthLabel = new JLabel(PropertiesHelper.getPropertyHelper().getStringFor("ChangePasswordDialog.passwordStrengthLabel"));
        passwordStrength = new JLabel();
        password1 = new JPasswordField();
        password1.setEditable(true);
        password1.addKeyListener(new KeyListener(){
			public void keyPressed(KeyEvent arg0) {
			}
			public void keyReleased(KeyEvent arg0) {
				int strength = PasswordStrengthCheck.check(new String(password1.getPassword()));
				passwordStrength.setText(PasswordStrengthCheck.textualResult(strength));
			}
			public void keyTyped(KeyEvent arg0) {
			}
        	
        });
        password1Label = new JLabel(PropertiesHelper.getPropertyHelper().getStringFor("ChangePasswordDialog.newPasswordLabel"));
        password2 = new JPasswordField();
        password2.setEditable(true);
        password2Label = new JLabel(PropertiesHelper.getPropertyHelper().getStringFor("ChangePasswordDialog.confirmPasswordLabel"));
        
        okButton = new JButton(PropertiesHelper.getPropertyHelper().getStringFor("Entry.ok")); //$NON-NLS-1$
        cancelButton = new JButton(PropertiesHelper.getPropertyHelper().getStringFor("Entry.cancel")); //$NON-NLS-1$
    }
    
    protected void initEventHandling() {
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleOk();
            }
        });
    }
    
    protected void handleOk() {

    	if(!SecurityManager.getInstance().comparePasswords(currentPassword.getPassword())){
            String title = PropertiesHelper.getPropertyHelper().getStringFor("ChangePasswordDialog.currentPwdIncorrectTitle");
            String message = PropertiesHelper.getPropertyHelper().getStringFor("ChangePasswordDialog.currentPwdIncorrectMsg");
            JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
            currentPassword.setText(null);
            password1.setText(null);
            password2.setText(null);
            return;	 		
    	}
    	
    	if(password1.getPassword().length<6){
            String title = PropertiesHelper.getPropertyHelper().getStringFor("ChangePasswordDialog.pwdTooShortTitle");
            String message = PropertiesHelper.getPropertyHelper().getStringFor("ChangePasswordDialog.pwdTooShortMsg");
            JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
            currentPassword.setText(null);
            password1.setText(null);
            password2.setText(null);
            return;		
      	}
    	if(!new String(password1.getPassword()).equals(new String(password2.getPassword()))){
          String title = PropertiesHelper.getPropertyHelper().getStringFor("ChangePasswordDialog.pwdsDifferentTitle");
          String message = PropertiesHelper.getPropertyHelper().getStringFor("ChangePasswordDialog.pwdsDifferentMsg");
          JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
          currentPassword.setText(null);
          password1.setText(null);
          password2.setText(null);
          return;		
    	}
        SwingWorker<Object, Object> worker = new SwingWorker<Object, Object>() {
            @Override
            protected Object doInBackground() throws ConnectException,
                    IOException, PGSecurityException {
                SecurityManager.getInstance().changePassword(password1.getPassword());
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    dispose();
                    getParent().setCursor(Cursor.getDefaultCursor());
                    ChangePasswordDialog.this.setCursor(Cursor.getDefaultCursor());
                    String title = PropertiesHelper.getPropertyHelper().getStringFor("ChangePasswordDialog.pwdChangeSuccessfulTitle");
                    String message = PropertiesHelper.getPropertyHelper().getStringFor("ChangePasswordDialog.pwdChangeSuccessfulMessage");
                    JOptionPane.showMessageDialog(getParent(), message, title,
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (InterruptedException e) {
                    getParent().setCursor(Cursor.getDefaultCursor());
                    ChangePasswordDialog.this.setCursor(Cursor.getDefaultCursor());
                    ExceptionsHelper.handleInterruptedException(e);
                } catch (ExecutionException e) {
                    getParent().setCursor(Cursor.getDefaultCursor());
                    ChangePasswordDialog.this.setCursor(Cursor.getDefaultCursor());
                    Throwable cause = e.getCause();
                    if (cause instanceof ConnectException) {
                        ExceptionsHelper.handleConnectException(
                        		ChangePasswordDialog.this,
                                (ConnectException) cause);
                    } else if (cause instanceof IOException) {
                        ExceptionsHelper.handleIOException(
                        		ChangePasswordDialog.this,
                                (IOException) cause, false);
                    } else {
                        ExceptionsHelper.handleUnknownException(cause);
                    }
                }
            }
        };
        getParent().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        SwingWorkerExecutor.getInstance().execute(worker);
        
    }
    
    protected void initBuilder() {
        builder = new DefaultFormBuilder(new FormLayout("right:default,10dlu,75dlu:grow"),  //$NON-NLS-1$
                new JPanel());
        builder.setDefaultDialogBorder();

    }
    
    protected void build() {
        builder.setRowGroupingEnabled(true);
        builder.append(currentPasswordLabel, currentPassword);
        builder.append(password1Label, password1);
        builder.append(password2Label, password2);
        builder.append(passwordStrengthLabel, passwordStrength);
        builder.setRowGroupingEnabled(false);
        builder.appendUnrelatedComponentsGapRow();
        builder.nextLine(2);
        builder.append(buildButtonPanel(), builder.getColumnCount());
        
        getContentPane().add(builder.getPanel());
    }
    
    protected JPanel buildButtonPanel(){
    	return ButtonBarFactory.buildOKCancelBar(okButton, cancelButton);
    }
}
