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

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.PageParameters;
import org.apache.wicket.extensions.markup.html.captcha.CaptchaImageResource;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.protocol.http.RequestUtils;
import org.apache.wicket.validation.validator.StringValidator;
import org.opencdms.web.core.application.OpenCdmsWeb;
import org.psygrid.security.attributeauthority.AttributeAuthorityService;
import org.psygrid.security.attributeauthority.model.hibernate.User;
import org.springframework.mail.MailException;

/**
 * This page allows the user to request a password reset.
 * 
 * If the page is accessed using a url without arguments, a form is presented to request a reset.
 * 
 * A link containing a UUID parameter is then emailed to the user.
 * 
 * The ability to receive the link is proof that the user is the person who has requested the password reset.
 * 
 * When the user follows the link they will be presented with a form to set a new password.
 * 
 * The reset link will be rejected if the user has not followed the link within 30 minutes of a reset request.
 * 
 * @author Terry Child
 *
 */
public class ResetPassword extends WebPage {
	
	private static final Log LOG = LogFactory.getLog(ResetPassword.class);
	
	private static final int TIMEOUT_MS=30*60*1000;
	
    /**
     * Use this constructor so we can call the page at a fixed url with a url parameter.
     */
    public ResetPassword(final PageParameters parameters) {
    	super(parameters);
    }    
       
    /*
     * Components are initialized in the onInitialize() method so we can using resource strings.
     * Resource strings cannot be accessed from a constructor.
     * @see org.apache.wicket.Component#onInitialize()
     */
    @Override
    protected void onInitialize(){

    	super.onInitialize();
    	
    	PageParameters parameters = getPageParameters();

    	String uuid = parameters.getString("q");

    	// On a successful password reset the page is accessed with 'success=true'.
    	String success = parameters.getString("success");
    	
    	String userDN = null;
    	
        // Check uuid and show the appropriate form

    	if(uuid!=null) {
    		AttributeAuthorityService aa = OpenCdmsWeb.get().getAttributeAuthorityService();
        	User user = aa.getPasswordResetUser(uuid);

    		LOG.info("Password reset request uuid='"+uuid+"' user='"+user+"'");
    		
    		// Check to see that the reset request was less than timeout milliseconds ago.
    		if(user!=null){
    			Date requestTime = user.getPasswordResetDate();
    			Date now = new Date();
    			if((now.getTime()-requestTime.getTime())<TIMEOUT_MS){
    				userDN=user.getUserName();
    			}
    			else {
    				getSession().error(getString("timeout"));
    			}
    		}
    		else {
				getSession().error(getString("invalidlink"));    			
    		}
    	}
		add(new Label("pageHeading",new ResourceModel("pageHeading")));
    	add(new FeedbackPanel("feedback"));
		add(new Label("success",new ResourceModel("success")).setVisible(success!=null));
        add(new RequestPasswordResetForm("requestPasswordResetForm").setVisible(userDN==null && success==null));
        add(new PasswordResetForm("passwordResetForm",userDN).setVisible(userDN!=null));
    }
    
    /*
     * This form is used to request a password reset for a given user ID.
     */
    private static final class RequestPasswordResetForm extends Form<Void> {
    	
		private static final long serialVersionUID = 1L;

	    /** Random captcha password to match against. */
	    private final String imagePass = randomString(6, 8);

	    private static int randomInt(int min, int max)
	    {
	        return (int)(Math.random() * (max - min) + min);
	    }

	    private static String randomString(int min, int max)
	    {
	        int num = randomInt(min, max);
	        byte b[] = new byte[num];
	        for (int i = 0; i < num; i++)
	            b[i] = (byte)randomInt('a', 'z');
	        return new String(b);
	    }    
		
		private TextField<String> username;

		private final CaptchaImageResource captchaImageResource;

	    private RequiredTextField<String> captchaText;

		@SuppressWarnings("serial")
		public RequestPasswordResetForm(String id) {
			super(id);
			username = new TextField<String>("username",new Model<String>(""));
			username.setRequired(true);

			add(new MultiLineLabel("help",new ResourceModel("help")));
			add(username);

			captchaImageResource = new CaptchaImageResource(imagePass);
	        add(new Image("captchaImage", captchaImageResource));
	        add(captchaText = new RequiredTextField<String>("captchaText", new Model<String>(""))
	        {
				@Override
	            protected final void onComponentTag(final ComponentTag tag)
	            {
	                super.onComponentTag(tag);
	                // clear the field after each render
	                tag.put("value", "");
	            }
	        });
		}

		@Override
        protected void onSubmit() {
	           if (!imagePass.equalsIgnoreCase(captchaText.getModelObject())) {
	                error("The value you entered '" + captchaText.getModelObject() + "' does not match the image.");
	            }
	           else {
		            String user = username.getModelObject();
		    		AttributeAuthorityService aa = OpenCdmsWeb.get().getAttributeAuthorityService();
					String userDN = aa.getUserDN(user);
		    		String uuid = null;
					if(userDN!=null){
						uuid = aa.generatePasswordResetUUID(userDN);
						if(uuid!=null){
							String link = RequestUtils.toAbsolutePath("resetpassword?q="+uuid);
							try {
								String email = aa.getUserEmailAddress(userDN);
								OpenCdmsWeb.get().sendEmail(getString("emailfrom"),
									getString("emailsubject"), getString("emailbody")+link, email);
								info(getString("emailsent")+" '"+user+"'");
								LOG.info("password reset email sent to user='"+userDN+"' email='"+email+"'");
							}
							catch (MailException ex){
								info(getString("failed.email")+" '"+user+"'. "+getString("contact"));												
							}
				            username.setModelObject("");
						}				
					}
					if(uuid==null) {
						info(getString("failed1")+" '"+user+"'. "+getString("failed2"));				
					}
	           }
				// force redrawing
	            captchaImageResource.invalidate();
        }
		

    }
    
    /*
     * This form is used to reset a user's password.
     * It is displayed only if this page is visited with a valid password reset URL.
     */
	private static final class PasswordResetForm extends Form<Void> {

		private static final long serialVersionUID = 1L;

	    private PasswordTextField newPassword;

		private String userDN;

		public PasswordResetForm(String id, String userDN) {
			super(id);

			this.userDN = userDN;
			
			String userName = "";

			if(userDN!=null){
				userName=userDN.substring(3, userDN.indexOf(",")).replaceAll(" ", "");
			}
									
			newPassword = new PasswordTextField("newPassword",new Model<String>(""));
			newPassword.add(new StringValidator.MinimumLengthValidator(6));			
			PasswordTextField newPassword2=new PasswordTextField("newPassword2",new Model<String>(""));			

			add(new MultiLineLabel("warning",new ResourceModel("warning")));
			add(new Label("userName",new Model<String>(userName)));
			add(newPassword);
			add(newPassword2);		
			add(new EqualPasswordInputValidator(newPassword, newPassword2));
		}

		@Override
		protected void onSubmit() {
			try{
				AttributeAuthorityService aa = OpenCdmsWeb.get().getAttributeAuthorityService();
	    		if(aa.setPassword(userDN,newPassword.getModelObject())){
	    			setResponsePage(ResetPassword.class, new PageParameters("success=true"));
	    		}
	    		else {
	    			getSession().info(getString("failed.update"));	    			
	    		}
			}
			catch(Exception ex){
				LOG.error("Error changing password", ex);
				error(getString("failed.update"));
			}
		}
	}	
		
	
}


