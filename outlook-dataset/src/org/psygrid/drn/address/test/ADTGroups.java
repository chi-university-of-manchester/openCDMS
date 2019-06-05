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


package org.psygrid.drn.address.test;

import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.PrivilegeType;
import org.psygrid.www.xml.security.core.types.TargetType;

/**
 * @author Rob Harper
 *
 */
public class ADTGroups {

	private static final String project = "ADDRESS TEST";

	public static final GroupType group1 = new GroupType("TEST", "001001", project);

    static final GroupType[] gta = new GroupType[]{group1};


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
	}}
