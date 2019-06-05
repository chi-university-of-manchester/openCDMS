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

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acegisecurity.context.SecurityContextHolder;
import org.psygrid.data.export.NoSuchExportFault;
import org.psygrid.data.repository.client.RepositoryClient;
import org.psygrid.web.beans.ErrorBean;
import org.psygrid.web.details.PsygridUserDetails;
import org.psygrid.web.helpers.ExceptionHelper;
import org.psygrid.web.helpers.ModelHelper;
import org.psygrid.web.helpers.SamlHelper;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * @author Rob Harper
 *
 */
public class DownloadExportController implements Controller {

	private static final String CONTENT_TYPE = "contentType";
	private static final String FILE_DATA = "fileData";
	private static final String FILE_NAME = "fileName";

	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Map<String, Object> model = ModelHelper.getTemplateModel();

		long id = Long.parseLong(request.getParameter("id"));

		PsygridUserDetails pgud = (PsygridUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		RepositoryClient client = new RepositoryClient();
		byte[] data = null;


		if (request.getParameter("type") != null
				&& "md5".equals(request.getParameter("type"))) {
			try{
				data = client.downloadExportHash(id, "MD5", SamlHelper.getSaAsString(pgud.getSaml()));
			}
			catch(Exception ex){
				return ExceptionHelper.handleWsException(ex);
			}
			model.put(FILE_NAME, id+".md5");
			model.put(FILE_DATA, data);
			model.put(CONTENT_TYPE, "text/plain");
			return new ModelAndView("zip", model);
		}
		else if (request.getParameter("type") != null
				&& "sha1".equals(request.getParameter("type"))) {
			try{
				data = client.downloadExportHash(id, "SHA1", SamlHelper.getSaAsString(pgud.getSaml()));
			}
			catch(Exception ex){
				return ExceptionHelper.handleWsException(ex);
			}
			model.put(FILE_NAME, id+".sha1");
			model.put(FILE_DATA, data);
			model.put(CONTENT_TYPE, "text/plain");
			return new ModelAndView("zip", model);
		}		
		else {
			try{
				data = client.downloadExport(id, SamlHelper.getSaAsString(pgud.getSaml()));
			}
			catch(NoSuchExportFault ex){
				ErrorBean eb = new ErrorBean(ex);
				model.put("exception", eb);
				return new ModelAndView("error", model);
			}
			catch(Exception ex){
				return ExceptionHelper.handleWsException(ex);
			}

			model.put(FILE_NAME, "export"+id+".zip");
			model.put(FILE_DATA, data);
			model.put(CONTENT_TYPE, "application/zip");
			return new ModelAndView("zip", model);
		}
	}

}
