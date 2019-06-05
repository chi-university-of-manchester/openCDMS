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


package org.psygrid.web.controllers;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.acegisecurity.context.SecurityContextHolder;
import org.psygrid.security.attributeauthority.client.AAQueryClient;
import org.psygrid.web.forms.Password;
import org.psygrid.web.helpers.ExceptionHelper;
import org.psygrid.web.helpers.ModelHelper;
import org.psygrid.web.ldap.PsygridLdapUserDetailsImpl;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

/**
 * @author Rob Harper
 *
 */
public class ChangePasswordController extends SimpleFormController {

	private AAQueryClient aaqc;
	
	public AAQueryClient getAaqc() {
		return aaqc;
	}

	public void setAaqc(AAQueryClient aaqc) {
		this.aaqc = aaqc;
	}

	@Override
	protected ModelAndView onSubmit(Object command, BindException errors) throws Exception {
		ModelAndView mav = null;
		try{
			mav = super.onSubmit(command, errors);
		}
		catch(ChangePasswordException ex){
			Map<String, Object> model = ModelHelper.getTemplateModel();
			model.put("cperror", "Unable to change your password. Please try again.");
			model.put(getCommandName(), createCommand());
			mav = new ModelAndView(getFormView(),model);
		}
		catch(Exception ex){
			mav = ExceptionHelper.handleWsException(ex);
		}
		return mav;
	}

	@Override
	protected ModelAndView onSubmit(Object command) throws Exception {
		super.onSubmit(command);
		Map<String, Object> model = ModelHelper.getTemplateModel();
		return new ModelAndView(getSuccessView(), model);
	}

	@Override
	protected void doSubmitAction(Object command) throws Exception {
		Password pwd = (Password)command;
		PsygridLdapUserDetailsImpl ud = (PsygridLdapUserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		boolean success = aaqc.changePassword(ud.getUsername(), pwd.getNewPassword1().toCharArray(), pwd.getOldPassword().toCharArray());
		if ( success ){
			//Not really sure if this is the right way to do this, but I need
			//to change the password in the user details object otherwise they'll
			//be forced back to the change password page again!
			ud.setPassword(pwd.getNewPassword1());
		}
		else{
			throw new ChangePasswordException();
		}
	}

	@Override
	protected Map referenceData(HttpServletRequest request) throws Exception {
		Map<String, Object> model = ModelHelper.getTemplateModel();
		return model;
	}

}
