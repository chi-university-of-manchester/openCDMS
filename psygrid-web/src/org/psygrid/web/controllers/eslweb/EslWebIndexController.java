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

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.security.RBACAction;
import org.psygrid.security.policyauthority.client.PAQueryClient;
import org.psygrid.web.details.PsygridUserDetails;
import org.psygrid.web.forms.ProjectGroup;
import org.psygrid.web.helpers.ModelHelper;
import org.psygrid.web.helpers.ProjectGroupWrapper;
import org.psygrid.web.helpers.SamlHelper;
import org.psygrid.www.xml.security.core.types.ActionType;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.ProjectType;
import org.psygrid.www.xml.security.core.types.TargetType;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

/**
 * @author Rob Harper
 *
 */
public class EslWebIndexController extends SimpleFormController {

	private static final Log LOG = LogFactory.getLog(EslWebIndexController.class);
	
	private PAQueryClient paqc;
	
	public EslWebIndexController(PAQueryClient paqc){
		this.paqc = paqc;
	}
	
	@Override
	protected ModelAndView onSubmit(Object command) throws Exception {
		doSubmitAction(command);
		return new ModelAndView(new RedirectView("studyCodeByPersonalDetails.html"));
	}

	@Override
	protected void doSubmitAction(Object command) throws Exception {
		ProjectGroup projGroup = (ProjectGroup)command;
		LOG.info("ProjectGroup = "+projGroup.getProjGroup());
		PsygridUserDetails user = (PsygridUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		for ( ProjectType pt: user.getProjects() ){
			List<GroupType> groups = user.getGroups().get(pt);
			for ( GroupType gt: groups ){
				ProjectGroupWrapper pgw = new ProjectGroupWrapper(pt, gt);
				if ( pgw.getProjectGroupCode().equals(projGroup.getProjGroup()) ){
					LOG.info("Found study and centre");
					user.setActiveProject(pt);
					user.setActiveGroup(gt);
					configureEslAuthorities(pt, gt, user);
					return;
				}
			}
		}
		//FIXME what to do if active project/group not found??
	}

	@Override
	protected Map referenceData(HttpServletRequest request) throws Exception {
		Map<String, Object> model = ModelHelper.getTemplateModel();
		PsygridUserDetails user = (PsygridUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<ProjectGroupWrapper> projectsgroups = new ArrayList<ProjectGroupWrapper>();
		for ( ProjectType pt: user.getProjects() ){
			List<GroupType> groups = user.getGroups().get(pt);
			for ( GroupType gt: groups ){
				projectsgroups.add(new ProjectGroupWrapper(pt, gt));
			}
		}
		model.put("projectsgroups", projectsgroups);
		return model;
	}

	private void configureEslAuthorities(ProjectType pt, GroupType gt, PsygridUserDetails user) throws Exception {
		//1. View personal details by study number
		final ActionType eslViewByNumberAction = new ActionType(RBACAction.ACTION_ESL_RETRIEVE_SUBJECT_BY_STUDY_NUMBER.toString(), null);
		boolean eslViewByNumberResult = paqc.getPort().makePolicyDecision(
				pt, new TargetType(gt.getName(), gt.getIdCode()), eslViewByNumberAction, SamlHelper.getSaAsString(user.getSaml()));
		
		//2. View study code by personal details
		final ActionType eslViewByDetailsAction = new ActionType(RBACAction.ACTION_ESL_FIND_SUBJECT_BY_EXAMPLE.toString(), null);
		boolean eslViewByDetailsResult = paqc.getPort().makePolicyDecision(
				pt, new TargetType(gt.getName(), gt.getIdCode()), eslViewByDetailsAction, SamlHelper.getSaAsString(user.getSaml()));
				
		//3. Edit personal details
		final ActionType eslEditAction = new ActionType(RBACAction.ACTION_ESL_SAVE_SUBJECT.toString(), null);
		boolean eslEditResult = paqc.getPort().makePolicyDecision(
				pt, new TargetType(gt.getName(), gt.getIdCode()), eslEditAction, SamlHelper.getSaAsString(user.getSaml()));
		
		//4. View treatment arm
		final ActionType eslTreatArmAction = new ActionType(RBACAction.ACTION_ESL_LOOKUP_RANDOMISATION_RESULT.toString(), null);
		boolean eslTreatArmResult = paqc.getPort().makePolicyDecision(
				pt, new TargetType(gt.getName(), gt.getIdCode()), eslTreatArmAction, SamlHelper.getSaAsString(user.getSaml()));
		
		//5. Emergency break-in
		final ActionType eslBreakInAction = new ActionType(RBACAction.ACTION_ESL_EMERGENCY_BREAK_IN.toString(), null);
		boolean eslBreakInResult = paqc.getPort().makePolicyDecision(
				pt, new TargetType(gt.getName(), gt.getIdCode()), eslBreakInAction, SamlHelper.getSaAsString(user.getSaml()));
		
		//6. Randomizer stats
		final ActionType eslStatsAction1 = new ActionType(RBACAction.ACTION_ESL_LOOKUP_RANDOMIZER_STATISTICS.toString(), null);
		boolean eslStatsResult1 = paqc.getPort().makePolicyDecision(
				pt, new TargetType(gt.getName(), gt.getIdCode()), eslStatsAction1, SamlHelper.getSaAsString(user.getSaml()));
		final ActionType eslStatsAction2 = new ActionType(RBACAction.ACTION_ESL_LOOKUP_STRATIFIED_RANDOMIZER_STATISTICS.toString(), null);
		boolean eslStatsResult2 = paqc.getPort().makePolicyDecision(
				pt, new TargetType(gt.getName(), gt.getIdCode()), eslStatsAction2, SamlHelper.getSaAsString(user.getSaml()));
		
		List<GrantedAuthority> eslAuthorities = new ArrayList<GrantedAuthority>();
		if ( eslViewByNumberResult ){
			LOG.info("Adding role ROLE_ESL_VIEW_BY_NUMBER");
			eslAuthorities.add(new GrantedAuthorityImpl("ROLE_ESL_VIEW_BY_NUMBER"));
		}
		if ( eslViewByDetailsResult ){
			LOG.info("Adding role ROLE_ESL_VIEW_BY_DETAILS");
			eslAuthorities.add(new GrantedAuthorityImpl("ROLE_ESL_VIEW_BY_DETAILS"));
		}
		if ( eslEditResult ){
			LOG.info("Adding role ROLE_ESL_EDIT");
			eslAuthorities.add(new GrantedAuthorityImpl("ROLE_ESL_EDIT"));
		}
		if ( eslTreatArmResult ){
			LOG.info("Adding role ROLE_ESL_TREAT_ARM");
			eslAuthorities.add(new GrantedAuthorityImpl("ROLE_ESL_TREAT_ARM"));
		}
		if ( eslBreakInResult ){
			LOG.info("Adding role ROLE_ESL_BREAK_IN");
			eslAuthorities.add(new GrantedAuthorityImpl("ROLE_ESL_BREAK_IN"));
		}
		if ( eslStatsResult1 && eslStatsResult2 ){
			LOG.info("Adding role ROLE_ESL_STATS");
			eslAuthorities.add(new GrantedAuthorityImpl("ROLE_ESL_STATS"));
		}
		
		user.resetAuthorities(eslAuthorities.toArray(new GrantedAuthority[eslAuthorities.size()]));

		/*
		 * Reset the authentication object with the new authorities - if we don't do this
		 * the Acegi authorization system will not know about the authorities we have just
		 * added for the ESL.
		 */
		//TODO this is security configuration and should not really be in the controller!
		//But it seems to work for now. Conside rmoving it to a filter configured in Acegi
		//though
		GrantedAuthority[] allAuthorities = new GrantedAuthority[eslAuthorities.size()+user.getGlobalAuthorities().length];
		int counter = 0;
		for ( GrantedAuthority ga: user.getGlobalAuthorities()){
			allAuthorities[counter] = ga;
			counter++;
		}
		for ( GrantedAuthority ga: eslAuthorities){
			allAuthorities[counter] = ga;
			counter++;
		}

		UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken)SecurityContextHolder.getContext().getAuthentication();
		UsernamePasswordAuthenticationToken newAuth = 
			new UsernamePasswordAuthenticationToken(auth.getPrincipal(), auth.getCredentials(), allAuthorities);
		SecurityContextHolder.getContext().setAuthentication(newAuth);
		
	}
	
}
