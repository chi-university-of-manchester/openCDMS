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

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acegisecurity.context.SecurityContextHolder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.esl.model.IProject;
import org.psygrid.esl.model.ISubject;
import org.psygrid.esl.model.hibernate.Group;
import org.psygrid.esl.services.ESLServiceFault;
import org.psygrid.esl.services.client.EslClient;
import org.psygrid.web.details.PsygridUserDetails;
import org.psygrid.web.forms.PersonalDetails;
import org.psygrid.web.helpers.EslWebHelper;
import org.psygrid.web.helpers.ModelHelper;
import org.psygrid.web.helpers.SamlHelper;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

/**
 * @author Rob Harper
 *
 */
public class TreatmentArmByPersonalDetailsController extends SimpleFormController {

	private static final Log LOG = LogFactory.getLog(TreatmentArmByPersonalDetailsController.class);
	
	@Override
	protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {

		PersonalDetails formSubject = (PersonalDetails)command;
		EslClient eslClient = new EslClient();
		ISubject exampleSubject = EslWebHelper.createSubjectFromForm(formSubject);

		//addition limit subject search to active group
		PsygridUserDetails user = (PsygridUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Group currentGroup = new Group();
		currentGroup.setGroupCode(user.getActiveGroup().getIdCode());
		exampleSubject.setGroup(currentGroup);
		
		IProject project = eslClient.retrieveProjectByCode(user.getActiveProject().getIdCode(), SamlHelper.getSaAsString(user.getSaml()));
		
		List<ISubject> subjects = eslClient.findSubjectByExample(project, exampleSubject, SamlHelper.getSaAsString(user.getSaml()));
		
		if (subjects.size() == 1)
		{
			ISubject subject = subjects.get(0);
			String randomName = null;
			try{
				randomName = eslClient.lookupRandomisationResult(project, subject.getStudyNumber(), SamlHelper.getSaAsString(user.getSaml()));
			}
			catch(ESLServiceFault esf){
				LOG.error("lookupRandomisationResult - ESLServiceFault", esf);
				errors.reject("error.eslweb.nosubject");
				return showForm(request, response, errors);
			}
			catch (org.psygrid.esl.services.RandomisationException re) {
				errors.reject("error.eslweb.norandomisation");
				return showForm(request, response, errors);
			}
			
			Map<String, String> treatments = project.getRandomisation().getTreatments();
			String randomResult = null;
			for ( Map.Entry<String, String> entry: treatments.entrySet() ){
		        if (entry.getValue().equals(randomName)) {
		        	randomResult = (String)entry.getKey();
		        }
			}
						
			Map<String, Object> model = ModelHelper.getTemplateModel();
			model.put("subjectNumber", subject.getStudyNumber());
			model.put("randomisation", randomResult);
			model.put("randomName", randomName);	
			return new ModelAndView("showtreatment", model);
			
		}
		else{
			errors.reject("error.eslweb.nouniquematch");
			return showForm(request, response, errors);
		}
		
	}

	@Override
	protected Map referenceData(HttpServletRequest request) throws Exception {
		PsygridUserDetails user = (PsygridUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		EslWebHelper.checkActiveProjectAndGroup(user);
		Map<String, Object> model = ModelHelper.getTemplateModel();
		model.put("title", "Find treatment arm by personal details");
		model.put("heading", "Find treatment arm");
		model.put("description", "Enter as many details as you can and click the 'Find treatment arm' button.");
		model.put("treatmentarm", "yes");
		return model;
	}
	
}
