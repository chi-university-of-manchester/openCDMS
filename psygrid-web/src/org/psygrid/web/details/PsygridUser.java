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


package org.psygrid.web.details;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.userdetails.User;
import org.acegisecurity.userdetails.UserDetails;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opensaml.SAMLAssertion;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.ProjectType;

/**
 * PsyGrid specific User class, designed as a drop-in replacement 
 * for the default Acegi User class.
 * 
 * @author Rob Harper
 *
 */
public class PsygridUser extends User implements PsygridUserDetails {

	private static final Log sLog = LogFactory.getLog(PsygridUser.class);
	
	private static final long serialVersionUID = 1L;

	private SAMLAssertion saml = null;
	private String pgDn = null;
	private List<ProjectType> projects = new ArrayList<ProjectType>();
	private Map<ProjectType, List<GroupType>> groups = new HashMap<ProjectType, List<GroupType>>();
	private ProjectType activeProject;
	private GroupType activeGroup;
	private GrantedAuthority[] globalAuthorities;
	
	public PsygridUser(UserDetails ud, Map<String, String> projectMap, Map<String, List<String>> groups){
		super(ud.getUsername(), ud.getPassword(), 
				ud.isEnabled(), ud.isAccountNonExpired(), 
				ud.isCredentialsNonExpired(), ud.isAccountNonLocked(), 
				ud.getAuthorities());
		globalAuthorities = ud.getAuthorities();
		for ( Entry<String, String> e: projectMap.entrySet() ){
			sLog.info("Adding study "+e.getKey()+" ("+e.getValue()+")");
			ProjectType pt = new ProjectType(e.getKey(), e.getValue(), null, null, false);
			projects.add(pt);
			List<String> groupList = groups.get(e.getValue());
			List<GroupType> groupTypes = new ArrayList<GroupType>();
			for ( String g: groupList ){
				sLog.info("Adding centre '"+g+"' for study '"+e.getValue()+"'");
				groupTypes.add(new GroupType(null, g, null));
			}
			this.groups.put(pt, groupTypes);
		}
	}
	
	public String getPgDn() {
		return pgDn;
	}

	public SAMLAssertion getSaml() {
		return saml;
	}

	public void setSaml(SAMLAssertion sa) {
		this.saml = sa;
	}

	public List<ProjectType> getProjects() {
		return projects;
	}

	public List<ProjectType> getExportableProjects() {
		//NOTE as this is just for testing the projects list and exportable
		//projects lists are the same!
		return projects;
	}

	public List<ProjectType> getImmediatelyExportableProjects() {
		//NOTE as this is just for testing the projects list and exportable
		//projects lists are the same!
		return projects;
	}

	public Map<ProjectType, List<GroupType>> getGroups() {
		return groups;
	}

	public long getServerTimeDifference() {
		return 0;
	}

	public ProjectType getActiveProject() {
		return activeProject;
	}

	public void setActiveProject(ProjectType activeProject) {
		this.activeProject = activeProject;
	}
		
	public GroupType getActiveGroup() {
		return activeGroup;
	}

	public void setActiveGroup(GroupType activeGroup) {
		this.activeGroup = activeGroup;
	}

	public GrantedAuthority[] getGlobalAuthorities() {
		return globalAuthorities;
	}

	public void setGlobalAuthorities(GrantedAuthority[] globalAuthorities) {
		this.globalAuthorities = globalAuthorities;
	}

	public void resetAuthorities(GrantedAuthority[] authorities) {
		GrantedAuthority[] newAuthorities = new GrantedAuthority[authorities.length + globalAuthorities.length];
		int counter = 0;
		for (GrantedAuthority ga: globalAuthorities){
			newAuthorities[counter] = ga;
			counter++;
		}
		for (GrantedAuthority ga: authorities){
			newAuthorities[counter] = ga;
			counter++;
		}
		this.setAuthorities(newAuthorities);
	}
		
	public List<ProjectType> getAuditableProjects() {
		//NOTE as this is just for testing the projects list and auditable
		//projects lists are the same!
		return projects;
	}


}
