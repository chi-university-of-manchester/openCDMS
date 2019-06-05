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

import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.ProjectType;


/**
 * Simple class to wrap a project and group together
 * Used to populate initial selection screen
 * @author pwhelan
 */
public class ProjectGroupWrapper 
{
	private ProjectType project;
	private GroupType group;

	public ProjectGroupWrapper(ProjectType project, GroupType group)
	{
		this.project = project;
		this.group = group;
	}
	
	public GroupType getGroup() {
		return group;
	}
	public void setGroup(GroupType group) {
		this.group = group;
	}
	public ProjectType getProject() {
		return project;
	}
	public void setProject(ProjectType project) {
		this.project = project;
	}
	public String getProjectGroupString() 
	{
		return project.getName() + "-" + group.getName();
	}
	
	public String getProjectGroupCode()
	{
		return project.getIdCode() + group.getIdCode();
	}
	
	
}
