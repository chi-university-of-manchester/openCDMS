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
package org.psygrid.datasetdesigner.model;

import org.psygrid.esl.model.IRole;

import java.util.ArrayList;

/**
 * Model to store the roles for the ESL randomization notifications
 * @author pwhelan
 */
public class ESLEmailModel  {

	/**
	 * A list of roles for the ESL
	 */
	private ArrayList<IRole> roles;
	
	/**
	 * Set the roles
	 * @param roles the roles to set
	 */
	public void setRoles(ArrayList<IRole> roles) {
		this.roles = roles;
	}
	
	/**
	 * Get the roles
	 * @return the ESL roles for notifications
	 */
	public ArrayList<IRole> getRoles() {
		return roles;
	}
	
}