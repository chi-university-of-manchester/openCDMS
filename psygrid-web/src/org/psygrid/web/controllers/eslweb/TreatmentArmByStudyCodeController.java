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

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acegisecurity.context.SecurityContextHolder;
import org.psygrid.common.identifier.IdentifierHelper;
import org.psygrid.common.identifier.InvalidIdentifierException;
import org.psygrid.esl.model.IProject;
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
public class TreatmentArmByStudyCodeController extends SimpleFormController {

	@Override
	protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {

		EslClient eslClient = new EslClient();
		
		PersonalDetails details = (PersonalDetails)command;
		
		PsygridUserDetails user = (PsygridUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		IProject project = eslClient.retrieveProjectByCode(user.getActiveProject().getIdCode(), SamlHelper.getSaAsString(user.getSaml()));

		try{
			IdentifierHelper.checkIdentifier(details.getStudyNumber());
		}
		catch(InvalidIdentifierException ex){
			details.setStudyNumber(null);
			errors.rejectValue("studyNumber", "error.eslweb.invalidstudynumber");
			return showForm(request, response, errors);
		}
		
		String randomName = null;
		try{
			randomName = eslClient.lookupRandomisationResult(project, details.getStudyNumber(), SamlHelper.getSaAsString(user.getSaml()));
		}
		catch(ESLServiceFault esf){
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
		model.put("subjectNumber", details.getStudyNumber());
		model.put("randomisation", randomResult);
		model.put("randomName", randomName);	
		
		return new ModelAndView("showtreatment", model);
		
	}
		
	
	@Override
	protected Map referenceData(HttpServletRequest request) throws Exception {
		PsygridUserDetails user = (PsygridUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		EslWebHelper.checkActiveProjectAndGroup(user);
		Map<String, Object> model = ModelHelper.getTemplateModel();
		model.put("exampleStudyNumber", ModelHelper.getExampleStudyNumber(user));
		model.put("title", "Find treatment arm by participant identifier");
		model.put("heading", "Find treatment arm");
		model.put("description", "Enter the participant identifier and click the 'Search' button to find the randomised treatment.");
		model.put("treatmentarm", "yes");
		return model;
	}
}
