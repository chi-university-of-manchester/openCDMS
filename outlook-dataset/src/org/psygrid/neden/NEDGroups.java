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

package org.psygrid.neden;

import org.psygrid.projects.common.ProjectGroups;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.PrivilegeType;
import org.psygrid.www.xml.security.core.types.TargetType;

/**
 * @author jda
 *
 */
public class NEDGroups extends ProjectGroups {

	private static final String project = "National EDEN";

	public static final GroupType heart_of_Birmingham_West_EIS = new GroupType("Heart of Birmingham - West EIS", "001001", project);
	public static final GroupType heart_of_Birmingham_East_EIS = new GroupType("Heart of Birmingham - East EIS", "002001", project);
	public static final GroupType east_PCT_Brimingham = new GroupType("East PCT Birmingham", "003001", project);
	public static final GroupType lancashire_001_400 = new GroupType("Lancashire 001-400", "004001", project);
	public static final GroupType lancashire_401_800 = new GroupType("Lancashire 401-800", "004002", project);
	public static final GroupType norfolk = new GroupType("Norfolk", "005001", project);
	public static final GroupType cambridge_CAMEO = new GroupType("Cambridge CAMEO", "006001", project);
	public static final GroupType cornwall_001_500 = new GroupType("Cornwall 001-500", "007001", project);
	public static final GroupType cornwall_501_1000 = new GroupType("Cornwall 501-1000", "007002", project);
	public static final GroupType brimingham_South = new GroupType("Birmingham South", "008001", project);
	public static final GroupType lancashire_Blackpool_and_Morecambe = new GroupType("Lancashire-Blackpool and Morecambe", "004003", project);
	public static final GroupType kings_lynn = new GroupType("Kings Lynn", "009001", project);
	public static final GroupType solihull = new GroupType("Solihull", "010001", project);
	public static final GroupType cheshire_wirral = new GroupType("Cheshire and Wirral", "011001", project);
	public static final GroupType huntingdon = new GroupType("Huntingdon", "012001", project);
	public static final GroupType peterborough = new GroupType("Peterborough", "006002", project);

    private static final GroupType[] gta = new GroupType[] {
			heart_of_Birmingham_West_EIS, heart_of_Birmingham_East_EIS,
			east_PCT_Brimingham, lancashire_001_400, lancashire_401_800,
			norfolk, cambridge_CAMEO, cornwall_001_500, cornwall_501_1000,
			brimingham_South, lancashire_Blackpool_and_Morecambe, kings_lynn, solihull, cheshire_wirral, huntingdon, peterborough };

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
