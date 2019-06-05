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


package org.psygrid.common.security;

import javax.swing.JFrame;

public abstract class LoginInterfaceFrame extends JFrame {
	
	protected boolean doProxyAuth = false;

	private String userName; 
	
	/**
	 * @return Returns the doProxyAuth.
	 */
	public boolean isDoProxyAuth() {
		return doProxyAuth;
	}

	/**
	 * @param doProxyAuth The doProxyAuth to set.
	 */
	public void setDoProxyAuth(boolean doProxyAuth) {
		this.doProxyAuth = doProxyAuth;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void refreshContentPane() {
		//to be overridden
	}
	
}