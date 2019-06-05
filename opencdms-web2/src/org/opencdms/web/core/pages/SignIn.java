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

package org.opencdms.web.core.pages;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.PageParameters;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.opencdms.web.core.application.OpenCdmsWebSession;
import org.opencdms.web.core.security.SamlHelper;

/**
 * @author Rob Harper
 *
 */
public class SignIn extends WebPage {
	
	private static final Log LOG = LogFactory.getLog(SignIn.class);
	
    public SignIn() {
        this(null);
    }

    /**
     * Creates a new sign-in page with the given parameters (ignored).
     * @param parameters page parameters (ignored)
     */
    public SignIn(final PageParameters parameters) {
        add(new SignInForm("loginform", new Model<SimpleUser>(new SimpleUser())));
        OpenCdmsWebSession session = getOpenCdmsWebSession();
        if (session.isSignedIn()) {
            error(getLocalizer().getString("login.errors.alreadysignedin", SignIn.this));
        }
    }

    /**
     * The class <code>SignInForm</code> is a subclass of the Wicket
     * {@link Form} class that attempts to authenticate the login request using
     * Wicket auth (which again delegates to Acegi Security).
     */
    public final class SignInForm extends Form<SimpleUser> {
    	
		private static final long serialVersionUID = 1L;

		public SignInForm(String id, IModel<SimpleUser> model) {
            super(id, model);
            add(new FeedbackPanel("feedback"));
            add(new TextField<String>("username", new PropertyModel<String>(model, "username")).setRequired(true));
            add(new PasswordTextField("password", new PropertyModel<String>(model, "password")).setResetPassword(true));
            add(new BookmarkablePageLink<Object>("resetPassword", ResetPassword.class));
        }

        /**
         * Called upon form submit. Attempts to authenticate the user.
         */
        protected void onSubmit() {
            if (getOpenCdmsWebSession().isSignedIn()) {
                // Already logged in, ignore the submit.
                error(getLocalizer().getString("login.errors.alreadysignedin", SignIn.this));

            } else {
                SimpleUser user = getModel().getObject();
                String username = user.getUsername();
                String password = user.getPassword();

                // Attempt to authenticate.
                OpenCdmsWebSession session = getOpenCdmsWebSession();
                if (session.signIn(username, password)) {

                    boolean forcePasswordChange = false;
                    try{
                    	forcePasswordChange = SamlHelper.forcePasswordChange(session.getUser().getUsername());
                    }
                    catch(Exception ex){
                    	//unable to see if password change is required - log but do nothing
                    	LOG.error("Unable to find is password change is required", ex);
                    }
                    if ( forcePasswordChange ){ 
                    	session.setForcePasswordChange(true);
                    	setResponsePage(ChangePassword.class);
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("User '" + username + "' redirected to change password page.");
                        }
                    }
                    else if (!continueToOriginalDestination()) {
                        // Continue to the application home page.
                        setResponsePage(getApplication().getHomePage());

                        if (LOG.isDebugEnabled()) {
                            LOG.debug("User '" + username + "' directed to application"
                                + " homepage (" + getApplication().getHomePage().getName() + ").");
                        }

                    } else {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("User '" + username + "' continues to original destination.");
                        }
                    }

                } else {
                    LOG.info("Could not authenticate user '" + username + "'. Transferring back to sign-in page.");
                    error(getLocalizer().getString("login.errors.invalidCredentials", SignIn.this));
                }
            }

            // ALWAYS do a redirect, no matter where we are going to. The point is that the
            // submitting page should be gone from the browsers history.
            setRedirect(true);
        }
    }

    private static OpenCdmsWebSession getOpenCdmsWebSession(){
    	return (OpenCdmsWebSession)Session.get();
    }
    
    /**
     * Simple bean that represents the properties for a login attempt (username
     * and clear text password).
     */
    public static class SimpleUser implements Serializable {
        private static final long serialVersionUID = -5617176504597041829L;

        private String username;
        private String password;

        public String getUsername() { 
        	return username; 
        }
        
        public void setUsername(String username) { 
        	this.username = username;
        }
        
        public String getPassword() { 
        	return password;
        }
        
        public void setPassword(String password) { 
        	this.password = password;
        }
        
    }
}
