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

import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.userdetails.UserDetails;
import org.psygrid.web.forms.Password;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * @author Rob Harper
 *
 */
public class ChangePasswordValidator implements Validator {

	public boolean supports(Class clazz) {
		return clazz.equals(Password.class);
	}

	public void validate(Object obj, Errors errors) {
		Password pwd = (Password)obj;
		
		if (null == pwd.getOldPassword() || pwd.getOldPassword().equals(""))
		{
			errors.rejectValue("oldPassword", "error.changepwd.nooldpwd");
		}
		else{			
			UserDetails ud = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			if (!pwd.getOldPassword().equals(ud.getPassword()))
			{
				errors.rejectValue("oldPassword", "error.changepwd.wrongoldpwd");
			}
			else{
				if (!pwd.getNewPassword1().equals(pwd.getNewPassword2()))
				{
					errors.rejectValue("newPassword2", "error.changepwd.wrongnewpwd");
				}
				
				if (pwd.getNewPassword1().length() < 6 )
				{
					errors.rejectValue("newPassword1", "error.changepwd.shortnewpwd");
				}
				
				if (pwd.getNewPassword1().equals(pwd.getOldPassword()) )
				{
					errors.rejectValue("newPassword1", "error.changepwd.samenewpwd");
				}				
			}
		}
	}

}
