/*
Copyright (c) 2006-2009, The University of Manchester, UK.

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

package org.opencdms.web.core.panels;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.markup.html.panel.ComponentFeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;
import org.apache.wicket.validation.validator.StringValidator;
import org.opencdms.web.core.application.OpenCdmsWebSession;
import org.opencdms.web.core.models.ChangePasswordModel;
import org.opencdms.web.core.panels.common.ErrorPanel;
import org.opencdms.web.core.security.ldap.PsygridLdapUserDetailsImpl;
import org.psygrid.security.attributeauthority.client.AAQueryClient;

/**
 * @author Rob Harper
 *
 */
public class ChangePasswordFormPanel extends Panel {

	private static final long serialVersionUID = 1L;
	
	private static final Log LOG = LogFactory.getLog(ChangePasswordFormPanel.class);

	public ChangePasswordFormPanel(String id, Component container) {
		super(id);
		setOutputMarkupId(true);
		add(new ChangePasswordForm("requestForm",  new CompoundPropertyModel<ChangePasswordModel>(new ChangePasswordModel()), container));
	}

	public static class ChangePasswordForm extends Form<ChangePasswordModel>{

		private static final long serialVersionUID = 1L;

		private final Component container;
		
		public Component getContainer() {
			return container;
		}

		public ChangePasswordForm(String id, final IModel<ChangePasswordModel> model, Component container) {
			super(id, model);
			this.container = container;
			
			final OpenCdmsWebSession session = (OpenCdmsWebSession)getSession();
			
			final PasswordTextField currentPassword = new PasswordTextField("currentPassword");
			currentPassword.add(new CurrentPasswordValidator(session));
			
			final PasswordTextField newPassword = new PasswordTextField("newPassword");
			newPassword.add(new StringValidator.MinimumLengthValidator(6));
			newPassword.add(new NewPasswordValidator(session));
			
			final PasswordTextField newPassword2 = new PasswordTextField("newPassword2");

			@SuppressWarnings("serial")
			final AjaxButton submit = new AjaxButton("submit"){

				@Override
				protected void onError(AjaxRequestTarget target, Form<?> form) {
					super.onError(target, form);
					target.addComponent(ChangePasswordForm.this.container);
				}

				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					try{
						AAQueryClient aaqc = new AAQueryClient("aaclient.properties");
						PsygridLdapUserDetailsImpl ud = session.getUser();
						boolean success = aaqc.changePassword(
								ud.getUsername(), 
								model.getObject().getNewPassword().toCharArray(), 
								ud.getPassword().toCharArray());
						if ( success ){
							//Not really sure if this is the right way to do this, but I need
							//to change the password in the user details object otherwise they'll
							//be forced back to the change password page again!
							ud.setPassword(model.getObject().getNewPassword());
							session.setForcePasswordChange(false);
						}
						
						ChangePasswordForm.this.getParent().replaceWith(new ChangePasswordSuccessPanel(ChangePasswordForm.this.getParent().getId()));
						target.addComponent(ChangePasswordForm.this.getContainer());
					}
					catch(Exception ex){
						LOG.error("Error changing password", ex);
						ErrorPanel.show(ChangePasswordForm.this.getParent(), ChangePasswordForm.this.getContainer(), target, ex);
					}
				}
				
			};
						
			add(currentPassword);
			add(new ComponentFeedbackPanel("currentPasswordFeedback", currentPassword));
			add(newPassword);
			add(new ComponentFeedbackPanel("newPasswordFeedback", newPassword));
			add(newPassword2);
			add(new ComponentFeedbackPanel("newPassword2Feedback", newPassword2));
			add(submit);
			
			add(new EqualPasswordInputValidator(newPassword, newPassword2));
		}
		
		
	}
	
	public static class CurrentPasswordValidator extends AbstractValidator<String>{

		private static final long serialVersionUID = 1L;

		private OpenCdmsWebSession session;
		
		public CurrentPasswordValidator(OpenCdmsWebSession session) {
			super();
			this.session = session;
		}

		@Override
		protected void onValidate(IValidatable<String> validatable) {
			if ( !validatable.getValue().equals(
					session.getUser().getPassword())){
				error(validatable);
			}
		}

		@Override
		protected String resourceKey() {
			return "CurrentPasswordValidator";
		}

	}
	
	public static class NewPasswordValidator extends AbstractValidator<String>{

		private static final long serialVersionUID = 1L;

		private OpenCdmsWebSession session;
		
		public NewPasswordValidator(OpenCdmsWebSession session) {
			super();
			this.session = session;
		}

		@Override
		protected void onValidate(IValidatable<String> validatable) {
			if ( validatable.getValue().equals(
					session.getUser().getPassword())){
				error(validatable);
			}
		}

		@Override
		protected String resourceKey() {
			return "NewPasswordValidator";
		}

	}
	
}
