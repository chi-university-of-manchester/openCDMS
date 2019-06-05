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


package org.psygrid.web.controllers.export;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acegisecurity.context.SecurityContextHolder;
import org.psygrid.data.export.UnableToCancelExportFault;
import org.psygrid.data.repository.client.RepositoryClient;
import org.psygrid.web.details.PsygridUserDetails;
import org.psygrid.web.helpers.ExceptionHelper;
import org.psygrid.web.helpers.SamlHelper;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.WebUtils;

/**
 * Controller for cancelling an export request.
 * 
 * @author Rob Harper
 *
 */
public class CancelExportController extends ExportStatusController {

	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		long id = Long.parseLong(request.getParameter("id"));
		
		PsygridUserDetails user = (PsygridUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		RepositoryClient client = new RepositoryClient();
		
		String message = null;
		try{
			client.cancelExport(id, SamlHelper.getSaAsString(user.getSaml()));
			message = "Export request successfully cancelled.";
		}
		catch(UnableToCancelExportFault utcef){
			message = utcef.getMessage();
		}
		catch(Exception ex){
			return ExceptionHelper.handleWsException(ex);
		}
		
		//put the message into the session so that we can retrieve it after
		//doing the redirect
		WebUtils.setSessionAttribute(request, "message", message);
		
		return new ModelAndView(new RedirectView("/secure/export/status.html", true));
		
	}

}
