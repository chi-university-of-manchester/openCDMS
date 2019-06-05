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

package org.psygrid.web.ldap;

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
	
	public GrantedAuthority[] getGrantedAuthorities(LdapUserDetails arg0)
			throws LdapDataAccessException {
		List<String> roles = new ArrayList<String>();
		roles.add("ROLE_USER");
		try{
			PsygridLdapUserDetailsImpl userDetails = (PsygridLdapUserDetailsImpl)arg0;
			
			if ( null != userDetails && null != userDetails.getProjects() && null != userDetails.getSaml() ){
				
				//see if the user is able to request export for any of their projects
				final ActionType exportAction = new ActionType(RBACAction.ACTION_DR_REQUEST_EXPORT.name(), null);
				ProjectActionType[] exportPata = new ProjectActionType[userDetails.getProjects().size()];
				for ( int i=0; i<userDetails.getProjects().size(); i++ ){
					ProjectActionType pat = new ProjectActionType();
					pat.setProject(userDetails.getProjects().get(i));
					ActionType[] ata = new ActionType[1];
					ata[0] = exportAction;
					pat.setAction(ata);
					exportPata[i] = pat;
				}
				AllowedType[] exportResult = paqc.getPort().testPrivileges(exportPata, userDetails.getSaml().toString());
	
				List<ProjectType> exportableProjects = new ArrayList<ProjectType>();
				
				// process result
				if ( null != exportResult ){
					for(int j=0;j<exportResult.length;j++){
						AllowedType at = exportResult[j];
						ProjectType pt = at.getProject();
						for(int k=0;k<at.getActionTarget().length;k++){
							ActionTargetType att = at.getActionTarget()[k];
							if ( att.getAction().equals(exportAction) ){
								exportableProjects.add(pt);
								break;
							}
						}
					}
				}
	
				userDetails.setExportableProjects(exportableProjects);
				if ( !exportableProjects.isEmpty() ){
					roles.add("ROLE_EXPORT");
				}
				
				//see if the user is able to request IMMEDIATE export for any of their projects
				final ActionType immediateExportAction = new ActionType(RBACAction.ACTION_DR_REQUEST_IMMEDIATE_EXPORT.name(), null);
				ProjectActionType[] immediateExportPata = new ProjectActionType[userDetails.getProjects().size()];
				for ( int i=0; i<userDetails.getProjects().size(); i++ ){
					ProjectActionType pat = new ProjectActionType();
					pat.setProject(userDetails.getProjects().get(i));
					ActionType[] ata = new ActionType[1];
					ata[0] = immediateExportAction;
					pat.setAction(ata);
					immediateExportPata[i] = pat;
				}
				AllowedType[] immediateExportResult = paqc.getPort().testPrivileges(immediateExportPata, userDetails.getSaml().toString());
	
				List<ProjectType> immediatelyExportableProjects = new ArrayList<ProjectType>();
				
				// process result
				if ( null != immediateExportResult ){
					for(int j=0;j<immediateExportResult.length;j++){
						AllowedType at = immediateExportResult[j];
						ProjectType pt = at.getProject();
						for(int k=0;k<at.getActionTarget().length;k++){
							ActionTargetType att = at.getActionTarget()[k];
							if ( att.getAction().equals(immediateExportAction) ){
								immediatelyExportableProjects.add(pt);
								break;
							}
						}
					}
				}
	
				userDetails.setImmediatelyExportableProjects(immediatelyExportableProjects);
				//no explicit role is added - we assume that anyone able to do
				//immediate export is able to do normal export as well
				
				
				//see if the user is able to view the audit log for any of their projects
				final ActionType auditAction = new ActionType(RBACAction.ACTION_DR_AUDIT_BY_PROJECT.name(), null);
				ProjectActionType[] auditPata = new ProjectActionType[userDetails.getProjects().size()];
				for ( int i=0; i<userDetails.getProjects().size(); i++ ){
					ProjectActionType pat = new ProjectActionType();
					pat.setProject(userDetails.getProjects().get(i));
					ActionType[] ata = new ActionType[1];
					ata[0] = auditAction;
					pat.setAction(ata);
					auditPata[i] = pat;
				}
				AllowedType[] auditResult = paqc.getPort().testPrivileges(auditPata, userDetails.getSaml().toString());
	
				List<ProjectType> auditableProjects = new ArrayList<ProjectType>();
				
				// process result
				if ( null != auditResult ){
					for(int j=0;j<auditResult.length;j++){
						AllowedType at = auditResult[j];
						ProjectType pt = at.getProject();
						for(int k=0;k<at.getActionTarget().length;k++){
							ActionTargetType att = at.getActionTarget()[k];
							if ( att.getAction().equals(auditAction) ){
								auditableProjects.add(pt);
								break;
							}
						}
					}
				}
	
				userDetails.setAuditableProjects(auditableProjects);
				if ( !auditableProjects.isEmpty() ){
					roles.add("ROLE_AUDIT");
				}

				//see if the user is able to dynamically generate reports for any of their projects
				final ActionType reportAction = new ActionType(RBACAction.ACTION_DR_GENERATE_DYNAMIC_REPORT.toString(), null);
				ProjectActionType[] reportPata = new ProjectActionType[userDetails.getProjects().size()];
				for ( int i=0; i<userDetails.getProjects().size(); i++ ){
					ProjectActionType pat = new ProjectActionType();
					pat.setProject(userDetails.getProjects().get(i));
					ActionType[] ata = new ActionType[1];
					ata[0] = reportAction;
					pat.setAction(ata);
					reportPata[i] = pat;
				}
				AllowedType[] reportResult = paqc.getPort().testPrivileges(reportPata, userDetails.getSaml().toString());
				// process result
				if ( null != reportResult ){
					for(int j=0;j<reportResult.length;j++){
						AllowedType at = reportResult[j];
						for(int k=0;k<at.getActionTarget().length;k++){
							ActionTargetType att = at.getActionTarget()[k];
							if ( att.getAction().equals(reportAction) ){
								roles.add("ROLE_REPORTS");	//reports action found so can be added
								break;
							}
						}
					}
				}
				
				//see if the user is able to dynamically generate reports for any of their projects
				final ActionType trendsReportAction = new ActionType(RBACAction.ACTION_DR_GENERATE_DYNAMIC_TRENDS_REPORT.toString(), null);
				ProjectActionType[] trendsReportPata = new ProjectActionType[userDetails.getProjects().size()];
				for ( int i=0; i<userDetails.getProjects().size(); i++ ){
					ProjectActionType pat = new ProjectActionType();
					pat.setProject(userDetails.getProjects().get(i));
					ActionType[] ata = new ActionType[1];
					ata[0] = trendsReportAction;
					pat.setAction(ata);
					trendsReportPata[i] = pat;
				}
				AllowedType[] trendsReportResult = paqc.getPort().testPrivileges(trendsReportPata, userDetails.getSaml().toString());
				// process result
				if ( null != trendsReportResult ){
					for(int j=0;j<trendsReportResult.length;j++){
						AllowedType at = trendsReportResult[j];
						for(int k=0;k<at.getActionTarget().length;k++){
							ActionTargetType att = at.getActionTarget()[k];
							if ( att.getAction().equals(trendsReportAction) ){
								roles.add("ROLE_TRENDS_REPORTS");	//trends reports action found so can be added
								break;
							}
						}
					}
				}
				
				//see if the user is able to use the ESL - more specific ESL roles are
				//added when the user choose the active project and group
				final ActionType eslAction = new ActionType(RBACAction.ACTION_ESL_RETRIEVE_PROJECT.toString(), null);
				ProjectActionType[] eslPata = new ProjectActionType[userDetails.getProjects().size()];
				for ( int i=0; i<userDetails.getProjects().size(); i++ ){
					ProjectActionType pat = new ProjectActionType();
					pat.setProject(userDetails.getProjects().get(i));
					ActionType[] ata = new ActionType[1];
					ata[0] = eslAction;
					pat.setAction(ata);
					eslPata[i] = pat;
				}
				AllowedType[] eslResult = paqc.getPort().testPrivileges(eslPata, userDetails.getSaml().toString());
				// process result
				if ( null != eslResult ){
					for(int j=0;j<eslResult.length;j++){
						AllowedType at = eslResult[j];
						for(int k=0;k<at.getActionTarget().length;k++){
							ActionTargetType att = at.getActionTarget()[k];
							if ( att.getAction().equals(eslAction) ){
								roles.add("ROLE_ESLWEB");	//reports action found so can be added
								break;
							}
						}
					}
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

	public PAQueryClient getPaqc() {
		return paqc;
	}

	public void setPaqc(PAQueryClient paqc) {
		this.paqc = paqc;
	}

}
