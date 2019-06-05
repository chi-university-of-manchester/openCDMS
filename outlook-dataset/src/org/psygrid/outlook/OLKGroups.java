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

package org.psygrid.outlook;

import org.psygrid.projects.common.ProjectGroups;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.PrivilegeType;
import org.psygrid.www.xml.security.core.types.TargetType;

/**
 * @author jda
 *
 */
public class OLKGroups extends ProjectGroups {

	private static final String project = "Outlook";

	public static final GroupType bristolAvon_AvonandWiltshireMentalHealthPartnership = new GroupType("Bristol Avon-Avon and Wiltshire Mental Health Partnership", "001001", project);
	public static final GroupType eastAnglia_NorfolkandWaveneyMentalHealthPartnershipTrust = new GroupType("East Anglia-Norfolk and Waveney Mental Health Partnership Trust", "002001", project);
	public static final GroupType eastAnglia_CambridgeCAMEO = new GroupType("East Anglia-Cambridge CAMEO", "002002", project);
	public static final GroupType eastMidlands_NottinghamshireHealthcareNHSTrust = new GroupType("East Midlands-Nottinghamshire Healthcare NHS Trust", "003001", project);
	public static final GroupType eastMidlands_LincolnshirePartnershipTrust = new GroupType("East Midlands-Lincolnshire Partnership NHS Foundation Trust", "003002", project);
	public static final GroupType northEast_NewcastleNorthumberlandandNorthTynesideMentalHealthTrust = new GroupType("North East-Newcastle, Northumberland and North Tyneside Mental Health Trust", "004001", project);
	public static final GroupType northLondon_SouthWestLondonandStGeorgesTrust= new GroupType("North London-South West London and St. Georges Trust", "005001", project);
	public static final GroupType northLondon_CentralandWestLondonTrust = new GroupType("North London-Central and West London Trust", "005002", project);
	public static final GroupType northWest_ManchesterMentalHealthandSocialCareTrust = new GroupType("North West-Manchester Mental Health and Social Care Trust", "006001", project);
	public static final GroupType southLondon_SouthLondonandMaudselyTrust = new GroupType("South London-South London and Maudsely Trust", "007001", project);
	public static final GroupType westMidlands_BirminghamandSolihullMentalHealthTrust = new GroupType("West Midlands-Birmingham and Solihull Mental Health Trust", "008001", project);
	public static final GroupType eastAnglia_PeterbroughNHSTrust = new GroupType("East Anglia-Peterbrough NHS Trust", "002003", project);
	public static final GroupType northWest_BoltonSalfordandTraffordMentalHealthTrust = new GroupType("North West-Bolton, Salford and Trafford Mental Health Trust", "006002", project);
	public static final GroupType northWest_LancashireCareTrust = new GroupType("North West-Lancashire Care Trust", "006003", project);


	private static final GroupType[] gta = new GroupType[] {
			bristolAvon_AvonandWiltshireMentalHealthPartnership,
			eastAnglia_NorfolkandWaveneyMentalHealthPartnershipTrust,
			eastAnglia_CambridgeCAMEO,
			eastMidlands_NottinghamshireHealthcareNHSTrust,
			eastMidlands_LincolnshirePartnershipTrust,
			northEast_NewcastleNorthumberlandandNorthTynesideMentalHealthTrust,
			northLondon_SouthWestLondonandStGeorgesTrust,
			northLondon_CentralandWestLondonTrust,
			northWest_ManchesterMentalHealthandSocialCareTrust,
			southLondon_SouthLondonandMaudselyTrust,
			westMidlands_BirminghamandSolihullMentalHealthTrust,
			eastAnglia_PeterbroughNHSTrust,
			northWest_BoltonSalfordandTraffordMentalHealthTrust,
			northWest_LancashireCareTrust };

	public static GroupType[] allGroups(){
		return gta;
	}

	public static TargetType[] allAsTargets(){
		TargetType[] tta = new TargetType[gta.length];
		for(int i=0; i <gta.length; i++){
			tta[i]=new TargetType(gta[i].getName(),gta[i].getIdCode());
		}
		return tta;
	}

	public static PrivilegeType[] allAsPrivileges(){
		PrivilegeType[] tta = new PrivilegeType[gta.length];
		for(int i=0; i <gta.length; i++){
			tta[i]=new PrivilegeType(null, gta[i]);
		}
		return tta;
	}

	public static GroupType[] noGroups(){
		return new GroupType[]{};
	}
}
