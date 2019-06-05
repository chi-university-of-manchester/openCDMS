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
 * Interface to represent the definition of a 
 * project.
 * 
 * @author Lucy Bridges
 *
 */
public interface IProject extends IAuditable {

	/**
	 * Get the groups (NHS areas) involved in this project.
	 * 
	 * @return A List of Groups
	 */
	public List<IGroup> getGroups();

	/**
	 * Add a group to be involved in the project
	 * 
	 * @param group
	 */
	public void setGroup(IGroup group);
	
	
	/**
	 * Get the code for this project.
	 * 
	 * @return String projectCode
	 */
	public String getProjectCode();

	/**
	 * Set the code for this project.
	 * 
	 * @param projectCode
	 */
	public void setProjectCode(String projectCode);

	/** 
	 * Get the name for this project.
	 * 
	 * @return String projectName
	 */
	public String getProjectName();

	/**
	 * Set the name of this project.
	 * 
	 * @param projectName
	 */
	public void setProjectName(String projectName);
	
	/**
	 * Return a boolean to indicate whether it is ok to delete data from the ESL or not
	 * @return	The boolean result
	 */
	public boolean getOkToDeleteEslData();
	
	/**
	 * Set whether or not it is ok to delete data from the ESL
	 * @param value The value to set
	 */
	public void setOkToDeleteEslData(boolean value);

	/**
	 * Get the Randomisation object/settings for this project.
	 * 
	 * @return A Randomisation
	 */
	public IRandomisation getRandomisation();

	/**
	 * Set the Randomisation object for the project??
	 * 
	 * @param randomisation
	 */
	public void setRandomisation(IRandomisation randomisation);
	
	/**
	 * Get the number of custom fields for the project.
	 * 
	 * @return Number of custom fields.
	 */
	public int getCustomFieldCount();
	
	/**
	 * Add a custom field to the project.
	 * 
	 * @param field The field to add.
	 * @throws ModelException if the field to add is null
	 */
	public void addCustomField(ICustomField field) throws ModelException;
	
	/**
	 * Get one of the projects custom fields
	 * 
	 * @param index The index of the field to get.
	 * @return The field at the specified index.
	 * @throws ModelException if no field exists at the specified index.
	 */
	public ICustomField getCustomField(int index) throws ModelException;
	
	/**
	 * Remove a custom field from the project.
	 * 
	 * @param index The index of the field to remove.
	 * @throws ModelException if no field exists at the specified index.
	 */
	public void removeCustomField(int index) throws ModelException;
	

	public org.psygrid.esl.model.dto.Project toDTO();
}

