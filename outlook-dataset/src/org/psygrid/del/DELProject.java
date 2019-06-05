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
package org.psygrid.del;

import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.PrivilegeType;
import org.psygrid.www.xml.security.core.types.TargetType;

/**
 * Template for creating a project (authority) within the
 * Data Element Library.<br>
 *
 * Any class extending this one will need to initialise the
 * variables: <br>
 * project<br>
 * projectID<br>
 * aliasName<br>
 * aliasId<br>
 *
 * @author Lucy Bridges
 *
 */
public abstract class DELProject {

	protected String project;
	protected String projectID;
	protected String aliasName;
	protected String aliasId;

	protected final GroupType group1 = new GroupType("Default", "default", project);	//provides a default group

    final GroupType[] gta = new GroupType[]{
    	group1};


	public GroupType[] allGroups(){
		return gta;
	}

	public TargetType[] allAsTargets(){
		TargetType[] tta = new TargetType[gta.length];
		for(int i=0; i <gta.length; i++){
			tta[i]=new TargetType(gta[i].getName(),gta[i].getIdCode());
		}
		return tta;
	}

	public PrivilegeType[] allAsPrivileges(){
		PrivilegeType[] tta = new PrivilegeType[gta.length];
		for(int i=0; i <gta.length; i++){
			tta[i]=new PrivilegeType(null, gta[i]);
		}
		return tta;
	}

	public GroupType[] noGroups(){
		return new GroupType[]{};
	}

	public String getAliasId() {
		return aliasId;
	}

	public String getAliasName() {
		return aliasName;
	}

	public GroupType[] getGta() {
		return gta;
	}

	public String getProject() {
		return project;
	}

	public String getProjectID() {
		return projectID;
	}

}