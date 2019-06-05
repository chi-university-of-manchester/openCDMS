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


// Created on Oct 10, 2006 by John Ainsworth

package org.psygrid.security;

import org.psygrid.www.xml.security.core.types.PrivilegeType;
import org.psygrid.www.xml.security.core.types.RoleType;

/**
 * @author jda
 *
 */
public enum RBACRole {
	
	ANY(0, null),
	SystemAdministrator(1, null),
	ChiefInvestigator(2, null),	
	ProjectManager(3, null),	
	PrincipalInvestigator(4, null),
	ProjectAdministrator(5, null),      			
	ClinicalResearchManager(6, null),	
	ClinicalResearchOfficer(7, null),	
	ScientificResearchOfficer(8, null),
	DataAnalyst(9, null),	
	RecruitmentManager(10, null),
	TreatmentAdministrator(11, null),
	NamedInvestigator(12, null),
	
	/* Data Element Library Users */
	DELViewer(13, null),	//User can view the DEL.
	DELAuthor(14, null),	//User can create and modify elements, and delete elements they own that are not approved.  
	DELCurator(15, null),   //User can approve new elements.
	
	/* Study Patching */
	StudyPatcher(16, null), //User must be a study patcher in SYSTEM as well as the study PM in order to patch.

	/* Importing */
	DataImporter(17, null), // User can bulk import data into a study

	/* Query */
	QueryData(18, null), // User can query the data
	
	ViewIdentity(19, null), // User can see documents which identify a participant
	
	Pharmacist(20, null), //A pharmacist has the ability to lookup and distribute medication packages.
	
	CanExportPRData(21, null); // User can export data from the Participant Register
	
	private final int id;
	
	private final String alias;

	RBACRole(int id, String alias) {
		this.id = id;
		this.alias = alias;
	}

	public int id() {
		return id;
	}
	
	public String alias(){
		return alias;
	}
	
	public String idAsString(){
		return new Integer(id).toString();
	}
	
	public  RoleType toRoleType(){
		return new RoleType(toString(), idAsString());
	}
	
	public static RoleType[] allRoles(){
		RBACRole[] rbacaa = RBACRole.values();
		RoleType[] rta = new RoleType[rbacaa.length];
		for(int i=0; i <rbacaa.length; i++){
			rta[i]=new RoleType(rbacaa[i].toString(),rbacaa[i].idAsString());
		}
		return rta;
	}
	
	public static PrivilegeType[] allAsPrivileges(){
		RBACRole[] r = RBACRole.values();
		PrivilegeType[] rta = new PrivilegeType[r.length];
		for(int i=0; i <r.length; i++){
			RoleType rt =new RoleType(r[i].toString(),r[i].idAsString());
			rta[i] = new PrivilegeType();
			rta[i].setRole(rt);
		}
		return rta;
	}
	
	public static RoleType[] noRoles(){
		return new RoleType[]{};
	}
	public PrivilegeType toPrivilegeType(){
		return new PrivilegeType(new RoleType(toString(), idAsString()), null);
	}
}
