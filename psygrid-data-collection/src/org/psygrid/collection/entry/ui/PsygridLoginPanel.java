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
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.net.URL;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.VerticalLayout;
import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.auth.LoginAdapter;
import org.jdesktop.swingx.auth.LoginEvent;
import org.jdesktop.swingx.auth.LoginService;
import org.jdesktop.swingx.util.WindowUtils;
import org.psygrid.collection.entry.security.EntryLoginService;
import org.psygrid.common.ui.ImagePanel;

/**
 * Login panel that is housed by PsygridLoginDialog.
 * <p>
 * Based on {@link org.jdesktop.swingx.JXLoginPanel} but stripped down 
 * and modified to make it suitable for PsyGrid. Much of the dependence
 * on swingx components has been removed.
 * 
 * @author Rob Harper
 *
 */
public class PsygridLoginPanel extends JPanel {

    private static final long serialVersionUID = -98975032798171437L;

    private static final Log LOG = LogFactory.getLog(PsygridLoginPanel.class);
    
    private static final String RESOURCE_PACKAGE = "org/psygrid/collection/entry/ui/resources";
    
    /**
     * Action key for an Action in the ActionMap that initiates the Login
     * procedure
     */
    public static final String LOGIN_ACTION_COMMAND = "login";

    /**
     * Action key for an Action in the ActionMap that initiates the Login
     * procedure
     */
    public static final String SETTINGS_ACTION_COMMAND = "settings";

    /** 
     * Action key for an Action in the ActionMap that cancels the Login
     * procedure
     */
    public static final String CANCEL_LOGIN_ACTION_COMMAND = "cancel-login";

    /**
     * The current login status for this panel
     */
    private Status status = Status.NOT_STARTED;

    /**
     * The LoginService to use. This must be specified for the login dialog to operate.
     * If no LoginService is defined, a default login service is used that simply
     * allows all users access. This is useful for demos or prototypes where a proper login
     * server is not available.
     */
    private LoginService loginService;
    
    /**
     * Listens to login events on the LoginService. Updates the UI and the
     * JXLoginPanel.state as appropriate
     */
    private LoginListenerImpl loginListener;

    /**
     * Text that should appear on the banner
     */
    private String bannerText = "Login";

    /**
     * An optional banner at the top of the panel
     */
    private ImagePanel banner;

    /**
     * Custom label allowing the developer to display some message to the user
     */
    private JLabel messageLabel;

    /**
     * Shows an error message such as "user name or password incorrect" or
     * "could not contact server" or something like that if something
     * goes wrong
     */
    private JLabel errorMessageLabel;

    /**
     * A Panel containing all of the input fields, check boxes, etc necessary
     * for the user to do their job. The items on this panel change whenever
     * the SaveMode changes, so this panel must be recreated at runtime if the
     * SaveMode changes. Thus, I must maintain this reference so I can remove
     * this panel from the content panel at runtime.
     */
    private JPanel loginPanel;

    /**
     * The panel on which the input fields, messageLabel, and errorMessageLabel
     * are placed. While the login thread is running, this panel is removed
     * from the dialog and replaced by the progressPanel
     */
    private JPanel contentPanel;

    /**
     * The username field presented allowing the user to enter their password
     */
    private JTextField userNameField;

    /**
     * The password field presented allowing the user to enter their password
     */
    private JPasswordField passwordField;

    /**
     * A special panel that displays a progress bar and cancel button, and
     * which notify the user of the login process, and allow them to cancel
     * that process.
     */
    private JPanel progressPanel;

    /**
     * A JLabel on the progressPanel that is used for informing the user
     * of the status of the login procedure (logging in..., cancelling login...)
     */
    private JLabel progressMessageLabel;

    /**
     * Tracks the cursor at the time that authentication was started, and restores to that
     * cursor after authentication ends, or is cancelled;
     */
    private Cursor oldCursor;
    
    public PsygridLoginPanel() {
        this(null);
    }

    public PsygridLoginPanel(EntryLoginService service) {
    	this.loginService = service == null ? new NullLoginService() : service;
        
        //create the login and cancel actions, and add them to the action map
        getActionMap().put(LOGIN_ACTION_COMMAND, createLoginAction());
        getActionMap().put(CANCEL_LOGIN_ACTION_COMMAND, createCancelAction());
        getActionMap().put(SETTINGS_ACTION_COMMAND, createSettingsAction());       
        
        loginListener = new LoginListenerImpl(service);
        this.loginService.addLoginListener(loginListener);
        
        // initialize banner text
        bannerText = Messages.getString("LoginPanel.bannerString");
        
        //updateUI();
        initComponents();
        
    }

    /**
     * Recreates the login panel, and replaces the current one with the new one
     */
    protected void recreateLoginPanel() {
        contentPanel.remove(loginPanel);
        loginPanel = createLoginPanel();
        loginPanel.setBorder(BorderFactory.createEmptyBorder(0, 36, 7, 11));
        contentPanel.add(loginPanel, 1);
    }
    
    /**
     * Creates and returns a new LoginPanel, based on the SaveMode state of
     * the login panel. Whenever the SaveMode changes, the panel is recreated.
     * I do this rather than hiding/showing components, due to a cleaner
     * implementation (no invisible components, components are not sharing
     * locations in the LayoutManager, etc).
     */
    private JPanel createLoginPanel() {
        JPanel loginPanel = new JPanel();
        
        //create the NameComponent
        userNameField = new JTextField();

        JLabel nameLabel = new JLabel(Messages.getString("LoginPanel.nameString"));
        nameLabel.setLabelFor(userNameField);
        
        //create the password component
        passwordField = new JPasswordField("", 15);
        JLabel passwordLabel = new JLabel(Messages.getString("LoginPanel.passwordString"));
        passwordLabel.setLabelFor(passwordField);
                
        loginPanel.setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new Insets(0, 0, 5, 11);
        loginPanel.add(nameLabel, gridBagConstraints);
        
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.anchor = GridBagConstraints.LINE_START;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(0, 0, 5, 0);
        loginPanel.add(userNameField, gridBagConstraints);
        
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new Insets(0, 0, 5, 11);
        loginPanel.add(passwordLabel, gridBagConstraints);
        
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.anchor = GridBagConstraints.LINE_START;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(0, 0, 5, 0);
        loginPanel.add(passwordField, gridBagConstraints);
        
        return loginPanel;
    }
    
    /**
     * Create all of the UI components for the login panel
     */
    private void initComponents() {
        //create the default banner
        banner = new ImagePanel(getLoginBannerUrl());

        //create the default label
        messageLabel = new JLabel(" ");
        messageLabel.setOpaque(true);
        messageLabel.setFont(messageLabel.getFont().deriveFont(Font.BOLD));

        //create the main components
        loginPanel = createLoginPanel();
        
        //create the message and hyperlink and hide them
        errorMessageLabel = new JLabel(Messages.getString("LoginPanel.errorMessage")); 
        errorMessageLabel.setIcon(UIManager.getIcon("JXLoginDialog.error.icon"));
        errorMessageLabel.setVerticalTextPosition(SwingConstants.TOP);
        errorMessageLabel.setOpaque(true);
        errorMessageLabel.setBackground(new Color(255, 215, 215));//TODO get from UIManager
        errorMessageLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(errorMessageLabel.getBackground().darker()),
                BorderFactory.createEmptyBorder(5, 7, 5, 5))); //TODO get color from UIManager
        errorMessageLabel.setVisible(false);
        
        //aggregate the optional message label, content, and error label into
        //the contentPanel
        contentPanel = new JPanel(new VerticalLayout());
        messageLabel.setBorder(BorderFactory.createEmptyBorder(12, 12, 7, 11));
        contentPanel.add(messageLabel);
        loginPanel.setBorder(BorderFactory.createEmptyBorder(0, 36, 7, 11));
        contentPanel.add(loginPanel);
        errorMessageLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 36, 0, 11, contentPanel.getBackground()),
                errorMessageLabel.getBorder()));
        contentPanel.add(errorMessageLabel);
        
        //create the progress panel
        progressPanel = new JPanel(new GridBagLayout());
        progressMessageLabel = new JLabel(Messages.getString("LoginPanel.pleaseWait"));
        progressMessageLabel.setFont(progressMessageLabel.getFont().deriveFont(Font.BOLD)); //TODO get from UIManager
        JProgressBar pb = new JProgressBar();
        pb.setIndeterminate(true);
        JButton cancelButton = new JButton(getActionMap().get(CANCEL_LOGIN_ACTION_COMMAND));
        progressPanel.add(progressMessageLabel, new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, new Insets(12, 12, 11, 11), 0, 0));
        progressPanel.add(pb, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 24, 11, 7), 0, 0));
        progressPanel.add(cancelButton, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 11, 11), 0, 0));
        
        //layout the panel
        setLayout(new BorderLayout());
        add(banner, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }

    /**
     * Create and return an image to use for the Banner. This may be overridden
     * to return any image you like
     */
    protected URL getLoginBannerUrl() {
        return Thread.currentThread().getContextClassLoader()
        	.getResource(RESOURCE_PACKAGE + "/" + "login_banner.png"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    /**
     * Create and return an Action for logging in
     */
    protected Action createLoginAction() {
    	return new LoginAction(this);
    }
    
    /**
     * Create and return an Action for settings
     */
    protected Action createSettingsAction() {
    	return new SettingsAction(this);
    }
    
    /**
     * Create and return an Action for canceling login
     */
    protected Action createCancelAction() {
    	return new CancelAction(this);
    }
    
    //------------------------------------------------------ Bean Properties
    //TODO need to fire property change events!!!
    /**
     * Sets the <strong>LoginService</strong> for this panel.
     *
     * @param service service
     */
    public void setLoginService(LoginService service) {
        loginService = service;
    }
    
    /**
     * Gets the <strong>LoginService</strong> for this panel.
     *
     * @return service service
     */
    public LoginService getLoginService() {
        return loginService;
    }
    
    /**
     * Sets the <strong>User name</strong> for this panel.
     *
     * @param username User name
     */
    public void setUserName(String username) {
        userNameField.setText(username);
    }
    
    /**
     * Sets the <strong>User name</strong> for this panel,
     * and lock it so that it cannot be changed.
     *
     * @param username User name
     */
    public void setAndLockUserName(String username) {
        userNameField.setText(username);
        userNameField.setEditable(false);
    }
    
    /**
     * Gets the <strong>User name</strong> for this panel.
     * @return the user name
     */
    public String getUserName() {
    	return userNameField.getText();
    }
    
    /**
     * Sets the <strong>Password</strong> for this panel.
     *
     * @param password Password
     */
    public void setPassword(char[] password) {
        passwordField.setText(new String(password));
    }
    
    /**
     * Gets the <strong>Password</strong> for this panel.
     *
     * @return password Password
     */
    public char[] getPassword() {
        return passwordField.getPassword();
    }
    
    /**
     * Return the image used as the banner
     */
    public Image getBanner() {
        return banner.getImage();
    }
    
    /**
     * Set the image to use for the banner
     */
    public void setBanner(Image img) {
        banner.setImage(img);
    }
    
    /**
     * Returns text used when creating the banner
     */
    public String getBannerText() {
        return bannerText;
    }

    /**
     * Returns the custom message for this login panel
     */
    public String getMessage() {
        return messageLabel.getText();
    }
    
    /**
     * Sets a custom message for this login panel
     */
    public void setMessage(String message) {
        messageLabel.setText(message);
    }
    
    /**
     * Returns the error message for this login panel
     */
    public String getErrorMessage() {
        return errorMessageLabel.getText();
    }
    
    /**
     * Sets the error message for this login panel
     */
    public void setErrorMessage(String errorMessage) {
        errorMessageLabel.setText(errorMessage);
    }
    
    /**
     * Returns the panel's status
     */
    public Status getStatus() {
        return status;
    }
    
    /**
     * Change the status
     */
    protected void setStatus(Status newStatus) {
		if (status != newStatus) {
		    Status oldStatus = status;
		    status = newStatus;
		    firePropertyChange("status", oldStatus, newStatus);
		}
    }

    /**
     * Initiates the login procedure. This method is called internally by
     * the LoginAction. This method handles cursor management, and actually
     * calling the LoginService's startAuthentication method.
     */
    protected void startLogin() {
        oldCursor = getCursor();
        try {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            progressMessageLabel.setText(Messages.getString("LoginPanel.pleaseWait"));
            String name = getUserName();
            char[] password = getPassword();
            loginService.startAuthentication(name, password, null);
        } catch(Exception ex) {
	    //The status is set via the loginService listener, so no need to set
	    //the status here. Just log the error.
	    LOG.warn("Authentication exception while logging in", ex);
        } finally {
            setCursor(oldCursor);
        }
    }
    
    protected void changeSettings(){
        status = PsygridLoginPanel.Status.SETTINGS;
    	ChangePasswordDialog dlg = new ChangePasswordDialog((JFrame)WindowUtils.findWindow(getParent()).getParent());
		dlg.setVisible(true);
    }
    
    /**
     * Cancels the login procedure. Handles cursor management and interfacing
     * with the LoginService's cancelAuthentication method
     */
    protected void cancelLogin() {
        progressMessageLabel.setText(Messages.getString("LoginPanel.cancelWait"));
        getActionMap().get(CANCEL_LOGIN_ACTION_COMMAND).setEnabled(false);
        loginService.cancelAuthentication();
        setCursor(oldCursor);
    }
    
    //-------------------------------------------------------------- Methods

    /**
     * Set the status of the panel to "Cancelled"
     */
    public void cancel(){
        setStatus(Status.CANCELLED);
    }
    
    //--------------------------------------------- Listener Implementations
    /*
     
     For Login (initiated in LoginAction):
        0) set the status
        1) Immediately disable the login action
        2) Immediately disable the close action (part of enclosing window)
        3) initialize the progress pane
          a) enable the cancel login action
          b) set the message text
        4) hide the content pane, show the progress pane
     
     When cancelling (initiated in CancelAction):
         0) set the status
         1) Disable the cancel login action
         2) Change the message text on the progress pane
     
     When cancel finishes (handled in LoginListener):
         0) set the status
         1) hide the progress pane, show the content pane
         2) enable the close action (part of enclosing window)
         3) enable the login action
     
     When login fails (handled in LoginListener):
         0) set the status
         1) hide the progress pane, show the content pane
         2) enable the close action (part of enclosing window)
         3) enable the login action
         4) Show the error message
         5) resize the window (part of enclosing window)
     
     When login succeeds (handled in LoginListener):
         0) set the status
         1) close the dialog/frame (part of enclosing window)
     */
    /**
     * Listener class to track state in the LoginService
     */
    protected class LoginListenerImpl extends LoginAdapter {
        
    	private EntryLoginService service;
    	
    	public LoginListenerImpl(EntryLoginService service){
    		this.service = service;
    	}
    	
    	@Override
        public void loginSucceeded(LoginEvent source) {
            setStatus(Status.SUCCEEDED);
        }
            
        @Override
        public void loginStarted(LoginEvent source) {
	    getActionMap().get(LOGIN_ACTION_COMMAND).setEnabled(false);
            getActionMap().get(CANCEL_LOGIN_ACTION_COMMAND).setEnabled(true);
            remove(contentPanel);
            add(progressPanel, BorderLayout.CENTER);
            revalidate();
            repaint();
            setStatus(Status.IN_PROGRESS);
        }

        @Override
        public void loginFailed(LoginEvent source) {
            remove(progressPanel);
            add(contentPanel, BorderLayout.CENTER);
            getActionMap().get(LOGIN_ACTION_COMMAND).setEnabled(true);
            Status newStatus = Status.FAILED;
            switch(service.getResult()){
            case FAILURE:
            	errorMessageLabel.setText(Messages.getString("LoginPanel.errorMessage"));
            	break;
            case FIRST_NO_CONNECT:
            	errorMessageLabel.setText(Messages.getString("LoginPanel.errorMessageFirstNoConnect"));
            	break;
            case LOCKED:
            	newStatus = Status.LOCKED;
            	errorMessageLabel.setText(Messages.getString("LoginPanel.errorMessageLocked"));
            	break;
            }
            errorMessageLabel.setVisible(true);
            revalidate();
            repaint();
            setStatus(newStatus);
        }

        @Override
        public void loginCanceled(LoginEvent source) {
            remove(progressPanel);
            add(contentPanel, BorderLayout.CENTER);
            getActionMap().get(LOGIN_ACTION_COMMAND).setEnabled(true);
            errorMessageLabel.setVisible(false);
            revalidate();
            repaint();
            setStatus(Status.CANCELLED);
        }
    }
    

    private static final class LoginAction extends AbstractActionExt {
		private static final long serialVersionUID = 2225157923509250981L;
		private PsygridLoginPanel panel;
    	public LoginAction(PsygridLoginPanel p) {
    	    super(Messages.getString("LoginPanel.loginString"), LOGIN_ACTION_COMMAND); 
    	    this.panel = p;
    	}
    	public void actionPerformed(ActionEvent e) {
    	    panel.startLogin();
    	}
    	public void itemStateChanged(ItemEvent e) {}
    }
        
    /**
     * Action that initiates changing settings. Delegates to JXLoginPanel.settings
     */
    private static final class SettingsAction extends AbstractActionExt {
		private static final long serialVersionUID = 1459959409111351748L;
		private PsygridLoginPanel panel;
		public SettingsAction(PsygridLoginPanel p) {
		    super(Messages.getString("LoginPanel.loginString"), SETTINGS_ACTION_COMMAND); 
		    this.panel = p;
		}
		public void actionPerformed(ActionEvent e) {
		    panel.changeSettings();
		}
		public void itemStateChanged(ItemEvent e) {}
    }
        
    /**
     * Action that cancels the login procedure. 
     */
    private static final class CancelAction extends AbstractActionExt {
		private static final long serialVersionUID = 3714767543635831981L;
		private PsygridLoginPanel panel;
		public CancelAction(PsygridLoginPanel p) {
		    super(Messages.getString("LoginPanel.cancelLogin"), CANCEL_LOGIN_ACTION_COMMAND); 
		    this.panel = p;
		    this.setEnabled(false);
		}
		public void actionPerformed(ActionEvent e) {
		    panel.cancelLogin();
		}
		public void itemStateChanged(ItemEvent e) {}
    }
        
    /**
     * Simple login service that allows everybody to login. This is useful in demos and allows
     * us to avoid having to check for LoginService being null
     */
    private static final class NullLoginService extends LoginService {
        @Override
        public boolean authenticate(String name, char[] password, String server) throws Exception {
            return true;
        }
    }
    
    /**
     * Returns the status of the login process
     */
    public enum Status {NOT_STARTED, IN_PROGRESS, FAILED, CANCELLED, SUCCEEDED, SETTINGS, LOCKED};

}
