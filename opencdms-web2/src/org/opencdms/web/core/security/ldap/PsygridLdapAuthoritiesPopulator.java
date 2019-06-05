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

package org.opencdms.web.core.security.ldap;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;
import org.acegisecurity.ldap.LdapDataAccessException;
import org.acegisecurity.providers.ldap.LdapAuthoritiesPopulator;
import org.acegisecurity.userdetails.ldap.LdapUserDetails;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opensaml.SAMLAssertion;
import org.psygrid.security.RBACAction;
import org.psygrid.security.policyauthority.client.PAQueryClient;
import org.psygrid.security.policyauthority.service.InputFaultMessage;
import org.psygrid.security.policyauthority.service.NotAuthorisedFaultMessage;
import org.psygrid.security.policyauthority.service.ProcessingFaultMessage;
import org.psygrid.www.xml.security.core.types.ActionTargetType;
import org.psygrid.www.xml.security.core.types.ActionType;
import org.psygrid.www.xml.security.core.types.AllowedType;
import org.psygrid.www.xml.security.core.types.ProjectActionType;
import org.psygrid.www.xml.security.core.types.ProjectType;

/**
 * PsyGrid-specific Authorities Populator, as roles are stored
 * by the Attribute Authority, not in the LDAP User Directory.
 * 
 * @author Rob Harper
 *
 */
public class PsygridLdapAuthoritiesPopulator implements LdapAuthoritiesPopulator {

	private static final Log sLog = LogFactory.getLog(PsygridLdapAuthoritiesPopulator.class);
	
	private PAQueryClient paqc;
	
	public GrantedAuthority[] getGrantedAuthorities(LdapUserDetails arg0) throws LdapDataAccessException {
		List<String> roles = new ArrayList<String>();
		roles.add("ROLE_USER");
		//TODO query role
		roles.add("ROLE_QUERY");
		try{
			PsygridLdapUserDetailsImpl userDetails = (PsygridLdapUserDetailsImpl)arg0;
			
			if ( null != userDetails && null != userDetails.getProjects() && null != userDetails.getSaml() ){
				
				List<ProjectType> projects = userDetails.getProjects();
				SAMLAssertion saml = userDetails.getSaml();
				
				//see if the user is able to request export for any of their projects
				List<ProjectType> exportableProjects = filterProjectsByAction(projects, RBACAction.ACTION_DR_REQUEST_EXPORT,saml);
				
				userDetails.setExportableProjects(exportableProjects);
				
				if ( !exportableProjects.isEmpty() ){
					roles.add("ROLE_EXPORT");
				}
				
				//see if the user is able to request IMMEDIATE export for any of their projects
				//no explicit role is added - we assume that anyone able to do
				//immediate export is able to do normal export as well
				List<ProjectType> immediatelyExportableProjects = filterProjectsByAction(projects, RBACAction.ACTION_DR_REQUEST_IMMEDIATE_EXPORT,saml);
				
				userDetails.setImmediatelyExportableProjects(immediatelyExportableProjects);

				
				//see if the user is able to import data for any of their projects
				List<ProjectType> importableProjects = filterProjectsByAction(projects, RBACAction.ACTION_DR_IMPORT_DATA,saml);
				
				userDetails.setImportableProjects(importableProjects);
				
				if ( !importableProjects.isEmpty() ){
					roles.add("ROLE_IMPORT");
				}

				
				//see if the user is able to view the audit log for any of their projects
				List<ProjectType> auditableProjects = filterProjectsByAction(projects, RBACAction.ACTION_DR_AUDIT_BY_PROJECT,saml);
				
				userDetails.setAuditableProjects(auditableProjects);
				
				if ( !auditableProjects.isEmpty() ){
					roles.add("ROLE_AUDIT");
				}

				//see if the user is able to query for any of their projects
				userDetails.setQueryableProjects(exportableProjects);

				/*
				List<ProjectType> queryableProjects = filterProjectsByAction(projects, RBACAction.ACTION_DR_SAVE_QUERY,saml);
	
				userDetails.setQueryableProjects(queryableProjects);
				if ( !queryableProjects.isEmpty() ){
					roles.add("ROLE_QUERY");
				}
				*/
				
				//see if the user is able to dynamically generate reports for any of their projects
				List<ProjectType> reportableProjects = filterProjectsByAction(projects, RBACAction.ACTION_DR_GENERATE_DYNAMIC_REPORT,saml);
				
				if(!reportableProjects.isEmpty()){
								roles.add("ROLE_REPORTS");
				}
				
				
				//see if the user is able to dynamically generate trend reports for any of their projects
				List<ProjectType> trendReportableProjects = filterProjectsByAction(projects, RBACAction.ACTION_DR_GENERATE_DYNAMIC_TRENDS_REPORT,saml);			
				
				if(!trendReportableProjects.isEmpty()){
					roles.add("ROLE_TRENDS_REPORTS");	//trends reports action found so can be added
				}
				
				//see if the user is able to use the ESL - more specific ESL roles are
				//added when the user choose the active project and group
				List<ProjectType> eslProjects = filterProjectsByAction(projects, RBACAction.ACTION_ESL_RETRIEVE_PROJECT,saml);
				
				if(!eslProjects.isEmpty()){
					roles.add("ROLE_ESLWEB");
				}
				
				List<ProjectType> exportPRProjects = filterProjectsByAction(projects, RBACAction.ACTION_ESL_EXPORT, saml);
				
				if(!exportPRProjects.isEmpty()) {
					roles.add("ROLE_EXPORTPR");
				}

				List<ProjectType> adminProjects = filterProjectsByAction(projects, RBACAction.ACTION_DR_PATCH_DATASET, saml);
				
				if(!adminProjects.isEmpty()) {
					roles.add("ROLE_PATCH_DATASET");
				}

			}

		} catch (InputFaultMessage e) {
			sLog.info(e.getFaultDiagnostic());
		} catch (ProcessingFaultMessage e) {
			sLog.info(e.getFaultDiagnostic());
		} catch (NotAuthorisedFaultMessage e) {
			sLog.info(e.getFaultDiagnostic());
		} catch (RemoteException e) {
			sLog.info(e.getMessage());
		}		
		
		GrantedAuthority[] gaa = new GrantedAuthority[roles.size()];
		for ( int i=0; i< roles.size(); i++ ){
			gaa[i] = new GrantedAuthorityImpl(roles.get(i));
		}
		return gaa;
	}

	/**
	 * 
	 * Filters a list of projects to return those where the user can carry out the specified action.
	 * 
	 * @param projects the list of project of which the user is a member
	 * @param action the action to filter the projects by
	 * @param saml the user's saml assertion
	 * @return the filtered list of projects for which the user may carry out the action
	 * @throws RemoteException
	 * @throws ProcessingFaultMessage
	 * @throws NotAuthorisedFaultMessage
	 * @throws InputFaultMessage
	 */
	private List<ProjectType> filterProjectsByAction(List<ProjectType> projects, RBACAction action, SAMLAssertion saml)
			throws RemoteException, ProcessingFaultMessage,NotAuthorisedFaultMessage, InputFaultMessage {

		ActionType actionType = new ActionType(action.name(), null);

		ProjectActionType[] patas = new ProjectActionType[projects.size()];
		for ( int i=0; i<projects.size(); i++ ){
			ProjectActionType pat = new ProjectActionType();
			pat.setProject(projects.get(i));
			ActionType[] ata = new ActionType[1];
			ata[0] = actionType;
			pat.setAction(ata);
			patas[i] = pat;
		}
		AllowedType[] allowed = paqc.getPort().testPrivileges(patas, saml.toString());

		List<ProjectType> results = new ArrayList<ProjectType>();
		
		// process result
		if ( null != allowed ){
			for(int j=0;j<allowed.length;j++){
				AllowedType at = allowed[j];
				ProjectType pt = at.getProject();
				for(int k=0;k<at.getActionTarget().length;k++){
					ActionTargetType att = at.getActionTarget()[k];
					if ( att.getAction().equals(actionType) ){
						results.add(pt);
						break;
					}
				}
			}
		}
		return results;
	}

	public PAQueryClient getPaqc() {
		return paqc;
	}

	public void setPaqc(PAQueryClient paqc) {
		this.paqc = paqc;
	}

}
