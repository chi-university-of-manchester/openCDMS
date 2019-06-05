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

package org.psygrid.web.inmemory;

import java.util.List;
import java.util.Map;

import org.acegisecurity.userdetails.UserDetails;
import org.acegisecurity.userdetails.UsernameNotFoundException;
import org.acegisecurity.userdetails.memory.InMemoryDaoImpl;
import org.psygrid.web.details.PsygridUser;
import org.springframework.dao.DataAccessException;

/**
 * PsyGrid specific implementation of Acegi's InMemoryDaoImpl
 * to make the UserDetails objects returned be implementations
 * of PsygridUserDetails.
 * 
 * @author Rob Harper
 *
 */
public class PsygridInMemoryDaoImpl extends InMemoryDaoImpl {

	Map<String, String> projects = null;
	
	Map<String, List<String>> groups = null;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
		UserDetails ud = super.loadUserByUsername(username);
		//convert the native UserDetails implementation to a
		//PsyGrid specific one
		return new PsygridUser(ud, projects, groups);
	}

	public Map<String, String> getProjects() {
		return projects;
	}

	public void setProjects(Map<String, String> projects) {
		this.projects = projects;
	}

	public Map<String, List<String>> getGroups() {
		return groups;
	}

	public void setGroups(Map<String, List<String>> groups) {
		this.groups = groups;
	}

}