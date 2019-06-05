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

package org.psygrid.web.forms;

import java.util.ArrayList;
import java.util.List;

public class Hub {

	private String code;
	private String name;
	private List<Trust> trusts = new ArrayList<Trust>();
	
	public Hub(String code, String name) {
		this.code = code;
		this.name = name;
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * Get the name of the hub
	 * 
	 * @return name
	 */
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Get a list of the trusts belonging to this hub.
	 * 
	 * @return trusts
	 */
	public List<Trust> getTrusts() {
		return trusts;
	}
	public void addTrust(Trust trust) {
		this.trusts.add(trust);
	}
	
	public void setTrusts(List<Trust> trusts) {
		this.trusts = trusts;
	}
	
	
}
