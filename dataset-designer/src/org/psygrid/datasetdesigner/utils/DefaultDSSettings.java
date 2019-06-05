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
package org.psygrid.datasetdesigner.utils;

import java.util.ArrayList;

import org.psygrid.security.RBACRole;

import org.psygrid.www.xml.security.core.types.RoleType;

/**
 * Controls the default settings for a dataset
 * currentlys just contains a list of roles and reports
 * 
 * @author pwhelan
 */
public class DefaultDSSettings {
	
	/**
	 * The possible roles to assign 
	 * @return the list of available roles
	 */
	public static ArrayList<RoleType> getAllRoles() {
		ArrayList<RoleType> roles = new ArrayList<RoleType>();

		RoleType ci = RBACRole.ChiefInvestigator.toRoleType();
		RoleType pi = RBACRole.PrincipalInvestigator.toRoleType();
		RoleType pa = RBACRole.ProjectAdministrator.toRoleType();
		RoleType crm = RBACRole.ClinicalResearchManager.toRoleType();
		RoleType cro = RBACRole.ClinicalResearchOfficer.toRoleType();
		RoleType sro = RBACRole.ScientificResearchOfficer.toRoleType();
		RoleType ta = RBACRole.TreatmentAdministrator.toRoleType();
		RoleType rm = RBACRole.RecruitmentManager.toRoleType();
		RoleType da = RBACRole.DataAnalyst.toRoleType();
		RoleType dq = RBACRole.QueryData.toRoleType();
		RoleType di = RBACRole.DataImporter.toRoleType();
		RoleType vi = RBACRole.ViewIdentity.toRoleType();
		RoleType ph = RBACRole.Pharmacist.toRoleType();
		RoleType canExportPRData = RBACRole.CanExportPRData.toRoleType();
		
		roles.add(ci);
		roles.add(pi);
		roles.add(pa);
		roles.add(crm);
		roles.add(cro);
		roles.add(sro);
		roles.add(ta);
		roles.add(rm);
		roles.add(da);
		roles.add(dq);
		roles.add(di);
		roles.add(vi);
		roles.add(ph);
		roles.add(canExportPRData);

		return roles;
	}

	/**
	 * Get all the reports
	 * @return the list of reports to choose from
	 */
	public static ArrayList<String> getAllReports() {
		ArrayList<String> reports = new ArrayList<String>();
		reports.add("Document Status Report");
		reports.add("Record Status Report");
		reports.add("UKCRN Report");
		reports.add("Standard Code Status Report");
		reports.add("Basic Statistic Report");
		reports.add("Collection Date Report");
		reports.add("Receiving Treatment Report");
		reports.add("Recruitment Progress Report");
		reports.add("Project Summary Report");
		reports.add("Group Summary Report");
		return reports;
	}

	
}
