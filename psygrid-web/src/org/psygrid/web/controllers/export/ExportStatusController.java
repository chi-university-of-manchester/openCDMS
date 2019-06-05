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

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acegisecurity.context.SecurityContextHolder;
import org.psygrid.data.export.hibernate.ExportRequest;
import org.psygrid.data.repository.client.RepositoryClient;
import org.psygrid.web.details.PsygridUserDetails;
import org.psygrid.web.helpers.ExceptionHelper;
import org.psygrid.web.helpers.ModelHelper;
import org.psygrid.web.helpers.SamlHelper;
import org.psygrid.www.xml.security.core.types.ProjectType;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.util.WebUtils;

/**
 * Controller to get the status of the users requested exports.
 * 
 * @author Rob Harper
 *
 */
public class ExportStatusController implements Controller {

	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> model = ModelHelper.getTemplateModel();
		String message = (String)WebUtils.getSessionAttribute(request, "message");
		if ( null != message ){
			//remove the attribute from the session
			WebUtils.setSessionAttribute(request, "message", null);
			model.put("message", message);
		}

		PsygridUserDetails user = (PsygridUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		RepositoryClient client = new RepositoryClient();
		List<ProjectType> ptl = user.getExportableProjects();
		String[] projects = new String[ptl.size()];
		for ( int i=0; i<ptl.size(); i++ ){
			projects[i] = ptl.get(i).getIdCode();
		}
		List<ExportRequest> reqs = null;
		try{
			reqs = client.getMyExportRequests(projects, SamlHelper.getSaAsString(user.getSaml()));
		}
		catch(Exception ex){
			return ExceptionHelper.handleWsException(ex);
		}
		model.put("requests", reqs);
		return new ModelAndView("exportstatus", model);
	}

}
