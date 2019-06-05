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

package org.psygrid.matisse.test;

import org.psygrid.projects.common.ProjectGroups;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.PrivilegeType;
import org.psygrid.www.xml.security.core.types.TargetType;

/**
 * @author pwhelan
 */
public class MTSTGroups extends ProjectGroups {

	private static final String project = "Matisse Test";

	public static final GroupType west_London = new GroupType("Central and North West London", "001001", project);
	public static final GroupType camden_islington = new GroupType("Camden and Islington", "002001", project);
	public static final GroupType west_England = new GroupType("West England", "003001", project);
	public static final GroupType belfast = new GroupType("Belfast", "004001", project);

	private static final GroupType[] gta = new GroupType[] {
		west_London,
		camden_islington,
		west_England,
		belfast
	};

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