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

package org.psygrid.web.beans;

/**
 * Bean class to store the DN and actual name of a user
 * of the PsyGrid system.
 * 
 * @author Rob Harper
 *
 */
public class UserBean {

	private String dn;
	
	private String name;
	
	public UserBean(){}
	
	public UserBean(String dn){
		this.dn = dn;
		this.name = dn.substring(dn.indexOf("=")+1, dn.indexOf(","));
	}

	public UserBean(String dn, String name){
		this.dn = dn;
		this.name = name;
	}

	public String getDn() {
		return dn;
	}

	public void setDn(String dn) {
		this.dn = dn;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
