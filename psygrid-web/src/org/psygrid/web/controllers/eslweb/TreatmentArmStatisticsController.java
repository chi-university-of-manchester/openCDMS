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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acegisecurity.context.SecurityContextHolder;
import org.psygrid.esl.model.IProject;
import org.psygrid.esl.randomise.StrataStats;
import org.psygrid.esl.services.ESLServiceFault;
import org.psygrid.esl.services.NotAuthorisedFault;
import org.psygrid.esl.services.RandomisationException;
import org.psygrid.esl.services.client.EslClient;
import org.psygrid.web.beans.ErrorBean;
import org.psygrid.web.beans.StatsBean;
import org.psygrid.web.beans.StrataStatsBean;
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
public class TreatmentArmStatisticsController implements Controller {

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.mvc.Controller#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		PsygridUserDetails user = (PsygridUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		EslWebHelper.checkActiveProjectAndGroup(user);

		Map<String, Object> model = ModelHelper.getTemplateModel();

		try {
			EslClient eslClient = new EslClient();

			StatsBean bean = new StatsBean();
			IProject currentProject = eslClient.retrieveProjectByCode(user.getActiveProject().getIdCode(), SamlHelper.getSaAsString(user.getSaml()));
			Map statsMap = eslClient.lookupRandomizerStatistics(currentProject, SamlHelper.getSaAsString(user.getSaml()));
			bean.setOverallStats(statsMap);
			StrataStats[] strataStats = eslClient.lookupStratifiedRandomizerStatistics(currentProject, SamlHelper.getSaAsString(user.getSaml()));
			List<StrataStatsBean> list = new ArrayList<StrataStatsBean>();
			for ( StrataStats s: strataStats ){
				StrataStatsBean sBean = new StrataStatsBean();
				StringBuilder strataBuilder = new StringBuilder();
				for ( int i=0; i<s.getStrata().length; i++ ){
					if ( i > 0 ){
						strataBuilder.append("; ");
					}
					strataBuilder.append(s.getStrata()[i][0]);
					strataBuilder.append(" = ");
					strataBuilder.append(s.getStrata()[i][1]);
				}
				sBean.setStrata(strataBuilder.toString());
				for ( int i=0; i<s.getStats().length; i++ ){
					sBean.getTreatments().put(s.getStats()[i][0], s.getStats()[i][1]);
				}
				list.add(sBean);
			}
			bean.setStrataStats(list);

			model.put("stats", bean);
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
			model.put("error", "An unknown error occurred when retrieving treatment statistics. The error was: "+e.getMessage());
			ErrorBean eb = new ErrorBean(e);
			model.put("exception", eb);
			return new ModelAndView("error", model);
		}
		return new ModelAndView("treatmentarmstats", model);
	}

}
