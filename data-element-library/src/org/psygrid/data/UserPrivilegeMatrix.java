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
package org.psygrid.data;

import java.util.HashMap;
import java.util.Map;

public class UserPrivilegeMatrix {
	private String saml;
	
	private Map<String, Boolean> authorMap;
	private Map<String, Boolean> curatorMap;
	private Map<String, Boolean> viewerMap;
	
	public UserPrivilegeMatrix(String saml){
		this.saml = saml;
		authorMap = new HashMap<String, Boolean>();
		curatorMap = new HashMap<String, Boolean>();
		viewerMap = new HashMap<String, Boolean>();
	}
	
	public void specifyIsAuthorForAuthority(String authority, boolean isAuthor){
		authorMap.put(authority, isAuthor);
	}
	
	public boolean getIsAuthorForAuthority(String authority){
		boolean isAuthor = authorMap.get(authority) == null ? false : authorMap.get(authority);
		return isAuthor;
	}
	
	public void specifyIsCuratorForAuthority(String authority, boolean isCurator){
		curatorMap.put(authority, isCurator);
	}
	
	public boolean getIsCuratorForAuthority(String authority){
		boolean isCurator = curatorMap.get(authority) == null ? false : curatorMap.get(authority);
		return isCurator;
	}
	
	public void specifyIsViewerForAuthority(String authority, boolean isViewer){
		viewerMap.put(authority, isViewer);
	}
	
	public boolean getIsViewerForAuthority(String authority){
		boolean isCurator = viewerMap.get(authority) == null ? false : viewerMap.get(authority);
		return isCurator;
	}
	
	
}
