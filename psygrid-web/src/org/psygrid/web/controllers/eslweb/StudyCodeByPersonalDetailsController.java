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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acegisecurity.context.SecurityContextHolder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.esl.model.IProject;
import org.psygrid.esl.model.ISubject;
import org.psygrid.esl.model.hibernate.Group;
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
public class StudyCodeByPersonalDetailsController extends
AbstractWizardFormController {

	private static final Log LOG = LogFactory.getLog(StudyCodeByPersonalDetailsController.class);

	/**
	 * The maximum number of results that can be shown. An error message
	 * will be displayed if the number of results returned is larger than
	 * this number.
	 */
	private static final int MAX_RESULTS = 10;

	List<ISubject> subjects = null;
	String selectedStudyNumber = null;

	public StudyCodeByPersonalDetailsController(){
		setPages(new String[]{"bypersonaldetails", "matchingsubjects", "showsubject", "bypersonaldetails"});
	}

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.mvc.AbstractWizardFormController#processFinish(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
	 */
	@Override
	protected ModelAndView processFinish(HttpServletRequest request,
			HttpServletResponse response, Object command, BindException errors)
	throws Exception {
		Map<String, Object> model = ModelHelper.getTemplateModel();
		model.put(this.getCommandName(), command);
		model.put("message", "Details updated as shown below.");
		return new ModelAndView("showsubject", model);
	}

	public Object formBackingObject(HttpServletRequest request) throws ServletException {
		return new PersonalDetails();
	}

	@Override
	protected ModelAndView processCancel(HttpServletRequest request,
			HttpServletResponse response, Object command, BindException errors)
	throws Exception {
		Map<String, Object> model = this.referenceData(request, 0);
		model.put("personalDetails", new PersonalDetails());
		return new ModelAndView("bypersonaldetails", model);
	}

	@Override
	protected void onBindAndValidate(HttpServletRequest request, Object command, BindException errors, int page) throws Exception {
		PsygridUserDetails user = (PsygridUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		EslWebHelper.checkActiveProjectAndGroup(user);
	}

	@Override
	protected void validatePage(Object command, Errors errors, int page) {
		LOG.info("validate Page, page="+page);
		PersonalDetails formSubject = (PersonalDetails)command;
		PsygridUserDetails user = (PsygridUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		EslClient eslClient = new EslClient();
		switch (page){
		case 0:
			//Also handled in postProcessPage
			break;

		case 1:
			//nothing to do
			break;
		case 2:
			//nothing to do
			break;
		case 3:
			try{
				IProject project = eslClient.retrieveProjectByCode(user.getActiveProject().getIdCode(), SamlHelper.getSaAsString(user.getSaml()));
				ISubject currentSubject = eslClient.retrieveSubjectByStudyNumber(project, formSubject.getStudyNumber(), SamlHelper.getSaAsString(user.getSaml()));
				ISubject updatedSubject = EslWebHelper.createSubjectFromForm(formSubject, currentSubject);
				eslClient.saveSubject(updatedSubject, SamlHelper.getSaAsString(user.getSaml()));
			}
			catch(Exception ex){
				LOG.error("validatePage: page 3", ex);
				errors.reject("error.eslweb.cantconnect");
			}
		}
	}

	/**
	 * Check that at least one of the essential fields have been completed.
	 * 
	 * @param formSubject
	 * @return boolean
	 */
	private boolean checkFieldsEntered(PersonalDetails formSubject) {
		if ((formSubject.getAddress1().equals("") || formSubject.getAddress1().equals("%")) 
				&& (formSubject.getPostCode().equals("") || formSubject.getPostCode().equals("%"))
				&& (formSubject.getCentreNumber().equals("") || formSubject.getCentreNumber().equals("%"))
				&& (formSubject.getDateOfBirth().equals("") || formSubject.getDateOfBirth().equals("%"))
				&& (formSubject.getLastName().equals("") || formSubject.getLastName().equals("%"))
				&& (formSubject.getNhsNumber().equals("") || formSubject.getNhsNumber().equals("%"))
				&& (formSubject.getHospitalNumber().equals("") || formSubject.getHospitalNumber().equals("%"))) {
			//At least one of these fields must be entered
			return false;
		}
		return true;
	}

	@Override
	protected void postProcessPage(HttpServletRequest request, Object command, Errors errors, int page) throws Exception {
		LOG.info("postProcessPage, page="+page);
		PersonalDetails formSubject = (PersonalDetails)command;
		PsygridUserDetails user = (PsygridUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		EslClient eslClient = new EslClient();
		switch (page){
		case 0:
			if (!checkFieldsEntered(formSubject)) {
				errors.reject("error.eslweb.morefields");
				return;	//No point in continuing because not enough data has been entered.
			}

			//addition limit subject search to active group
			Group currentGroup = new Group();
			currentGroup.setGroupCode(user.getActiveGroup().getIdCode());
			ISubject exampleSubject = EslWebHelper.createSubjectFromForm(formSubject);
			exampleSubject.setGroup(currentGroup);

			try{
				IProject project = eslClient.retrieveProjectByCode(user.getActiveProject().getIdCode(), SamlHelper.getSaAsString(user.getSaml()));				
				subjects = eslClient.findSubjectByExample(project, exampleSubject, SamlHelper.getSaAsString(user.getSaml()));
				if (subjects.size() == 1)
				{ 
					//populate form object from subject
					EslWebHelper.populatePersonalDetailsFromSubject(formSubject, subjects.get(0));
				}
				else if (subjects.size() > MAX_RESULTS) {
					errors.reject("error.eslweb.toomanyresults");
				}
				else{
					request.setAttribute("subjects", subjects);
				}
			}
			catch(Exception ex){
				LOG.error("postProcessPage: page 0", ex);
				errors.reject("error.eslweb.cantconnect");
			}
			break;

		case 1:
			ISubject selected = null;
			if (subjects != null) {
				for (ISubject subject: subjects) {
					if (subject.getStudyNumber().equals(formSubject.getStudyNumber())) {
						selected = subject;
						break;
					}
				}
				//populate form object from subject
				EslWebHelper.populatePersonalDetailsFromSubject(formSubject, selected);
			}
			break;
		case 2:
			break;
		case 3:
			//handled in validatePage
			break;
		}
	}


	@Override
	protected Map referenceData(HttpServletRequest request, int page) throws Exception {
		PsygridUserDetails user = (PsygridUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		EslWebHelper.checkActiveProjectAndGroup(user);
		Map<String, Object> model = ModelHelper.getTemplateModel();
		switch (page){
		case 0:
			model.put("title", "Find study code by personal details");
			model.put("heading", "Find participant identifier");
			model.put("description", "Enter as many details as you can and click the 'Search' button.");
			model.put("studycode", "yes");
			break;
		case 1:
			//nothing
			break;
		case 2:
			//nothing
			break;
		case 3:
			model.put("title", "Update personal details");
			model.put("heading", "Update personal details");
			model.put("description", "Amend the details of the participant then click the 'Update details' button to save the changes.");
			model.put("editdetails", "yes");
		}
		return model;
	}

	@Override
	protected int getTargetPage(HttpServletRequest request, Object command, Errors errors, int currentPage) {
		LOG.info("getTargetPage, page="+currentPage);
		if (currentPage == 0 && subjects != null && subjects.size() == 1) {
			//Skip the list of subjects
			int next = currentPage += 2;
			return next;
		}

		int target = super.getTargetPage(request, command, errors, currentPage);
		if (currentPage == 2 && target == 1) {
			//Browser back button has been used, so go back to the search page..
			target = 0;
		}
		return target;
	}

}
