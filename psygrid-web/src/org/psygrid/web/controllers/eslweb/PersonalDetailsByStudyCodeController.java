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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acegisecurity.context.SecurityContextHolder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.common.identifier.IdentifierHelper;
import org.psygrid.common.identifier.InvalidIdentifierException;
import org.psygrid.esl.model.IProject;
import org.psygrid.esl.model.ISubject;
import org.psygrid.esl.services.ESLServiceFault;
import org.psygrid.esl.services.ESLSubjectLockedFault;
import org.psygrid.esl.services.client.EslClient;
import org.psygrid.web.details.PsygridUserDetails;
import org.psygrid.web.forms.PersonalDetails;
import org.psygrid.web.helpers.EslWebHelper;
import org.psygrid.web.helpers.ModelHelper;
import org.psygrid.web.helpers.SamlHelper;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractWizardFormController;

/**
 * @author Rob Harper
 *
 */
public class PersonalDetailsByStudyCodeController extends AbstractWizardFormController {

	private static final Log LOG = LogFactory.getLog(PersonalDetailsByStudyCodeController.class);
	
	public PersonalDetailsByStudyCodeController(){
		setPages(new String[]{"bystudycode", "showsubject", "bypersonaldetails", "bypersonaldetails"});
	}
	
	@Override
	protected void onBindAndValidate(HttpServletRequest request, Object command, BindException errors, int page) throws Exception {
		PsygridUserDetails user = (PsygridUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		EslWebHelper.checkActiveProjectAndGroup(user);
	}

	public Object formBackingObject(HttpServletRequest request) throws ServletException {
		return new PersonalDetails();
	}
		
	@Override
	protected void validatePage(Object command, Errors errors, int page) {
		PersonalDetails formSubject = (PersonalDetails)command;
		
		PsygridUserDetails user = (PsygridUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		EslClient eslClient = new EslClient();
		switch (page){
		case 0:
			//Validate entered study number
			try{
				IdentifierHelper.checkIdentifier(formSubject.getStudyNumber());
			}
			catch(InvalidIdentifierException ex){
				formSubject.setStudyNumber(null);
				errors.rejectValue("studyNumber", "error.eslweb.invalidstudynumber");
			}
			break;
			
		case 1:
			//nothing to do
			break;
			
		case 2:
			try{
				IProject project = eslClient.retrieveProjectByCode(user.getActiveProject().getIdCode(), SamlHelper.getSaAsString(user.getSaml()));
				ISubject currentSubject = eslClient.retrieveSubjectByStudyNumber(project, formSubject.getStudyNumber(), SamlHelper.getSaAsString(user.getSaml()));
				ISubject updatedSubject = EslWebHelper.createSubjectFromForm(formSubject, currentSubject);
				eslClient.saveSubject(updatedSubject, SamlHelper.getSaAsString(user.getSaml()));
			}
			catch(Exception ex){
				LOG.error("validatePage - page 2", ex);
				errors.reject("error.eslweb.cantconnect");
			}
		}
	}
	
	@Override
	protected void postProcessPage(HttpServletRequest request, Object command, Errors errors, int page) throws Exception {
		PsygridUserDetails user = (PsygridUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		PersonalDetails formSubject = (PersonalDetails)command;
		EslClient eslClient = new EslClient();
		switch (page){
		case 0:
			if ( null != formSubject.getStudyNumber() ){
				try{
					IProject project = eslClient.retrieveProjectByCode(user.getActiveProject().getIdCode(), SamlHelper.getSaAsString(user.getSaml()));
					try{
						ISubject subject = eslClient.retrieveSubjectByStudyNumber(project, formSubject.getStudyNumber(), SamlHelper.getSaAsString(user.getSaml()));
						//populate form object from subject
						EslWebHelper.populatePersonalDetailsFromSubject(formSubject, subject);
					}
					catch(ESLSubjectLockedFault eslf){
						errors.reject("error.eslweb.subjectlocked");
					}
					catch(ESLServiceFault esf){
						errors.reject("error.eslweb.nosubject");
					}
				}
				catch(Exception ex){
					LOG.error("postProcessPage - page 0", ex);
					errors.reject("error.eslweb.cantconnect");
				}
			}
			break;
			
		case 1:
			//nothing to do
			break;
			
		case 2:
			//handled in validatePage
			break;
		}
	}

	@Override
	protected ModelAndView processFinish(HttpServletRequest request,
			HttpServletResponse response, Object command, BindException errors)
			throws Exception {
		Map<String, Object> model = ModelHelper.getTemplateModel();
		model.put(this.getCommandName(), command);
		model.put("message", "Details updated as shown below.");
		return new ModelAndView("showsubject", model);
	}	

	@Override
	protected ModelAndView processCancel(HttpServletRequest request,
			HttpServletResponse response, Object command, BindException errors)
	throws Exception {
		Map<String, Object> model = this.referenceData(request, 0);
		model.put("personalDetails", new PersonalDetails());
		return new ModelAndView("bystudycode", model);
	}
	
	@Override
	protected Map referenceData(HttpServletRequest request, int page) throws Exception {
		try {
		PsygridUserDetails user = (PsygridUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		EslWebHelper.checkActiveProjectAndGroup(user);
		Map<String, Object> model = ModelHelper.getTemplateModel();
		switch (page){
		case 0:
			model.put("exampleStudyNumber", ModelHelper.getExampleStudyNumber(user));
			model.put("title", "Find personal details by participant identifier");
			model.put("heading", "Find personal details");
			model.put("description", "Enter the participant identifier and click the 'Search' or 'Edit' button to find or edit the personal details.");
			model.put("studycode", "yes");
			break;
		case 1:
			//nothing
			break;
		case 2:
		case 3:
			model.put("title", "Update personal details");
			model.put("heading", "Update personal details");
			model.put("description", "Amend the details of the participant then click the 'Update details' button to save the changes.");
			model.put("editdetails", "yes");
		}
		return model;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
