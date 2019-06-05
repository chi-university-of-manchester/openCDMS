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


package org.psygrid.esl.model;

import java.util.List;

/**
 * Interface to represent the definition of a Group (typically an NHS
 * area) within a Project.
 * 
 * @author Lucy Bridges
 *
 */
public interface IGroup extends IAuditable {
	
	/** 
	 * Get the name for this Group.
	 * 
	 * @return String groupName
	 */
	public String getGroupName();

	/**
	 * Set the name of this Group.
	 * 
	 * @param groupName
	 */
	public void setGroupName(String groupName);

	/**
	 * Get the code for this Group.
	 * 
	 * @return String groupCode
	 */
	public String getGroupCode();

	/**
	 * Set the code for this Group.
	 * 
	 * @param groupCode
	 */
	public void setGroupCode(String groupCode);

	/**
	 * Get the Subjects (trial participants) involved in this project.
	 * 
	 * @return A List of Subjects
	 */
	public List<ISubject> getSubjects();

	/**
	 * Add a Subject to be involved in the project
	 * 
	 * @param subject
	 */
	public void setSubject(ISubject subject);
	
	/**
	 * Retrieve the Project that this Group is part of
	 * 
	 * @return IProject
	 */
	public IProject getProject();
	
	/**
	 * Set the Projec this Group is part of
	 * 
	 * @param project
	 */
	public void setProject(org.psygrid.esl.model.hibernate.Project project);
	
	public org.psygrid.esl.model.dto.Group toDTO();
 
}
