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


package org.psygrid.web.controllers.eslweb;

import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acegisecurity.context.SecurityContextHolder;
import org.psygrid.esl.model.IProject;
import org.psygrid.esl.services.ESLServiceFault;
import org.psygrid.esl.services.NotAuthorisedFault;
import org.psygrid.esl.services.RandomisationException;
import org.psygrid.esl.services.client.EslClient;
import org.psygrid.web.beans.ErrorBean;
import org.psygrid.web.details.PsygridUserDetails;
import org.psygrid.web.helpers.EslWebHelper;
import org.psygrid.web.helpers.ModelHelper;
import org.psygrid.web.helpers.SamlHelper;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * @author Rob Harper
 *
 */
public class EmergencyBreakInController implements Controller {

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.mvc.Controller#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		PsygridUserDetails user = (PsygridUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		EslWebHelper.checkActiveProjectAndGroup(user);

		Map<String, Object> model = ModelHelper.getTemplateModel();

		EslClient eslClient = new EslClient();

		IProject currentProject = null;
		Map<String, String> breakInMap = null;

		try {
			currentProject = eslClient.retrieveProjectByCode(user.getActiveProject().getIdCode(), SamlHelper.getSaAsString(user.getSaml()));
					
			breakInMap = eslClient.emergencyBreakIn(currentProject, SamlHelper.getSaAsString(user.getSaml()));
		}
		catch (ESLServiceFault e) {
			model.put("error", "An error occurred in the Participant Register. The error was: "+e.getMessage());
			ErrorBean eb = new ErrorBean(e);
			model.put("exception", eb);
			return new ModelAndView("error", model);
		}
		catch (RandomisationException e) {
			model.put("error", "An error occurred in the Randomisation service. The error was: "+e.getMessage());
			ErrorBean eb = new ErrorBean(e);
			model.put("exception", eb);
			return new ModelAndView("error", model);
		}
		catch (NotAuthorisedFault e) {
			model.put("error", "Sorry, you are not authorised to view this information.");
			ErrorBean eb = new ErrorBean(e);
			model.put("exception", eb);
			return new ModelAndView("error", model);
		}
		catch (Exception e) {
			model.put("error", "An error occurred when retrieving the details. The error was: "+e.getMessage());
			ErrorBean eb = new ErrorBean(e);
			model.put("exception", eb);
			return new ModelAndView("error", model);
		}

		Map treatments = currentProject.getRandomisation().getTreatments();
		int mapsize = treatments.size();
		Iterator it = breakInMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry)it.next();
			String treatmentName = (String)pairs.getValue();
			Iterator keyValuePairs1 = treatments.entrySet().iterator();
			for (int i = 0; i < mapsize; i++)
			{
				Map.Entry entry = (Map.Entry) keyValuePairs1.next();

				Object value = entry.getValue();
				if (value.equals(treatmentName))
				{
					String treatmentCode = (String)entry.getKey();
					if (treatmentCode != null && !treatmentCode.equals(""))
					{
						breakInMap.put(pairs.getKey().toString(), treatmentName + " (" + treatmentCode + ")");
						break;
					}
				}	
			}
		}

		model.put("breakInMap",breakInMap);

		return new ModelAndView("emergencybreakin", model);
	}

}
