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


package org.psygrid.web.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.userdetails.UserDetails;
import org.psygrid.esl.services.client.EslClient;
import org.psygrid.web.beans.UserBean;
import org.psygrid.web.details.PsygridUserDetails;

/**
 * @author Rob Harper
 *
 */
public class ModelHelper {

	public static final String HAS_ROLE_YES = "yes";
	public static final String HAS_ROLE_NO = "no";

	public static Map<String, Object> getTemplateModel(){
		Map<String, Object> model = new HashMap<String, Object>();
		UserDetails user = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		PsygridUserDetails pgUser = (PsygridUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String canExport = HAS_ROLE_NO;
		String genReports = HAS_ROLE_NO;
		String genTrendsReports = HAS_ROLE_NO;
		String eslWeb = HAS_ROLE_NO;
		String eslWebViewByNumber = HAS_ROLE_NO;
		String eslWebViewByDetails = HAS_ROLE_NO;
		String eslWebEdit = HAS_ROLE_NO;
		String eslWebRndResult = HAS_ROLE_NO;
		String eslWebBreakIn = HAS_ROLE_NO;
		String eslWebStats = HAS_ROLE_NO;
		String canAudit = HAS_ROLE_NO;
		for ( GrantedAuthority ga: user.getAuthorities() ){
			if ( ga.getAuthority().equals("ROLE_EXPORT") ){
				canExport = HAS_ROLE_YES;
			}
			if ( ga.getAuthority().equals("ROLE_REPORTS") ){
				genReports = HAS_ROLE_YES;
			}
			if ( ga.getAuthority().equals("ROLE_TRENDS_REPORTS") ){
				genTrendsReports = HAS_ROLE_YES;
			}
			if ( ga.getAuthority().equals("ROLE_ESLWEB") ){
				eslWeb = HAS_ROLE_YES;
			}
			if ( ga.getAuthority().equals("ROLE_AUDIT") ){
				canAudit = HAS_ROLE_YES;
			}

			boolean eslExists = false;
			//Check whether the selected project exists within the ESL
			if (null != pgUser.getActiveProject() && null != pgUser.getActiveProject().getIdCode() && null != pgUser.getSaml() ) {
				EslClient client = new EslClient();
				try {
					eslExists = client.isEslProject(pgUser.getActiveProject().getIdCode(), pgUser.getSaml().toString());
				}
				catch (Exception e) {
				}
			}
			if (eslExists) {
				if ( ga.getAuthority().equals("ROLE_ESL_VIEW_BY_NUMBER") ){
					eslWebViewByNumber = HAS_ROLE_YES;
				}
				if ( ga.getAuthority().equals("ROLE_ESL_VIEW_BY_DETAILS") ){
					eslWebViewByDetails = HAS_ROLE_YES;
				}
				if ( ga.getAuthority().equals("ROLE_ESL_EDIT") ){
					eslWebEdit = HAS_ROLE_YES;
				}

				boolean randomised = false;
				//Check whether the selected project uses randomisation
				if (null != pgUser.getActiveProject() && null != pgUser.getActiveProject().getIdCode()) {
					EslClient client = new EslClient();
					try {
						randomised = client.isProjectRandomised(pgUser.getActiveProject().getIdCode(), pgUser.getSaml().toString());
					}
					catch (Exception e) {
					}
				}
				if (randomised) {
					if ( ga.getAuthority().equals("ROLE_ESL_TREAT_ARM") ){
						eslWebRndResult = HAS_ROLE_YES;
					}
					if ( ga.getAuthority().equals("ROLE_ESL_BREAK_IN") ){
						eslWebBreakIn = HAS_ROLE_YES;
					}
					if ( ga.getAuthority().equals("ROLE_ESL_STATS") ){
						eslWebStats = HAS_ROLE_YES;
					}
				}
			}
		}
		model.put("exportPrivilege", canExport);
		model.put("reportsPrivilege", genReports);
		model.put("trendsReportsPrivilege", genTrendsReports);
		model.put("eslwebPrivilege", eslWeb);
		model.put("eslWebViewByNumberPrivilege", eslWebViewByNumber);
		model.put("eslWebViewByDetailsPrivilege", eslWebViewByDetails);
		model.put("eslWebEditPrivilege", eslWebEdit);
		model.put("eslWebRndResultPrivilege", eslWebRndResult);
		model.put("eslWebBreakInPrivilege", eslWebBreakIn);
		model.put("eslWebStatsPrivilege", eslWebStats);
		model.put("auditPrivilege", canAudit);

		if ( null != pgUser.getActiveGroup() && null != pgUser.getActiveProject() && HAS_ROLE_YES.equals(eslWeb) ){
			model.put("eslwebsubPrivilege", HAS_ROLE_YES);
		}
		else{
			model.put("eslwebsubPrivilege", HAS_ROLE_NO);
		}

		if ( HAS_ROLE_YES.equals(genReports) ){
			model.put("reportssub", HAS_ROLE_YES);
		}
		else{
			model.put("reportssub", HAS_ROLE_NO);
		}

		model.put("username", user.getUsername());
		if ( null != pgUser.getActiveProject() ){
			model.put("project", pgUser.getActiveProject().getName());
		}
		if ( null != pgUser.getActiveGroup() ){
			model.put("group", pgUser.getActiveGroup().getName());
		}

		return model;
	}

	public static String getExampleStudyNumber(PsygridUserDetails user){
		return user.getActiveProject().getIdCode()+"/"+user.getActiveGroup().getIdCode()+"-11";
	}

}


