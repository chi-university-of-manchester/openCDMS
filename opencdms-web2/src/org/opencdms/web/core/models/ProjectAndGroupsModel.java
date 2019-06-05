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

package org.opencdms.web.core.models;

import java.io.Serializable;
import java.util.List;

import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.ProjectType;

/**
 * @author Rob Harper
 *
 */
public class ProjectAndGroupsModel implements Serializable {

	private static final long serialVersionUID = 1L;

	private ProjectType study;
	private List<GroupType> centres;

	public ProjectAndGroupsModel(){
		super();
	}
	
	public ProjectAndGroupsModel(ProjectType study, List<GroupType> centres){
		this.study = study;
		this.centres = centres;
	}
	
	public ProjectType getStudy() {
		return study;
	}

	public void setStudy(ProjectType study) {
		this.study = study;
	}

	public List<GroupType> getCentres() {
		return centres;
	}

	public void setCentres(List<GroupType> centres) {
		this.centres = centres;
	}

}
