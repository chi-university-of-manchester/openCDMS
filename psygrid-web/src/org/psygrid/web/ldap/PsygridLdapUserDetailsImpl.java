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

package org.psygrid.web.ldap;

import java.util.List;
import java.util.Map;

import javax.naming.directory.Attributes;
import javax.naming.ldap.Control;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.userdetails.ldap.LdapUserDetails;
import org.opensaml.SAMLAssertion;
import org.psygrid.web.details.PsygridUserDetails;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.ProjectType;

/**
 * UserDetails implementation class for PsyGrid, designed as
 * a drop-in replacement for the default Acegi LdapUserDetailsImpl
 * class.
 * 
 * @author Rob Harper
 *
 */
public class PsygridLdapUserDetailsImpl implements LdapUserDetails, PsygridUserDetails {

	private static final long serialVersionUID = 1L;

	private Attributes attributes;
    private String dn;
    private String password;
    private String username;
    private GrantedAuthority[] authorities;
    private Control[] controls;
    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;
    private boolean enabled = true;

    private SAMLAssertion saml;
    private String pgDn;
    private List<ProjectType> projects;
    private List<ProjectType> exportableProjects;
    private List<ProjectType> auditableProjects;
    private List<ProjectType> immediatelyExportableProjects;
    private Map<ProjectType, List<GroupType>> groups;
    private long serverTimeDifference;
    private ProjectType activeProject;
    private GroupType activeGroup;
    private GrantedAuthority[] globalAuthorities;
    
    public PsygridLdapUserDetailsImpl(LdapUserDetails lud, SAMLAssertion saml, String pgDn, 
    		List<ProjectType> projects, Map<ProjectType, List<GroupType>> groups, long timeDifference){
		 this.dn = lud.getDn();
		 this.password = lud.getPassword();
		 this.username = lud.getUsername();
		 this.attributes = lud.getAttributes();
		 this.authorities = lud.getAuthorities();
		 this.controls = lud.getControls();
		 this.accountNonExpired = lud.isAccountNonExpired();
		 this.accountNonLocked = lud.isAccountNonLocked();
		 this.credentialsNonExpired = lud.isCredentialsNonExpired();
		 this.enabled = lud.isEnabled();
		 this.saml = saml;
		 this.pgDn = pgDn;
		 this.projects = projects;
		 this.groups = groups;
		 this.serverTimeDifference = timeDifference;
	}

	public Attributes getAttributes() {
		return attributes;
	}

	public Control[] getControls() {
		return controls;
	}

	public String getDn() {
		return dn;
	}

	public GrantedAuthority[] getAuthorities() {
		return authorities;
	}

	public String getPassword() {
		return password;
	}

	public String getUsername() {
		return username;
	}

	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}

	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public SAMLAssertion getSaml() {
		return saml;
	}

	public void setSaml(SAMLAssertion sa) {
		this.saml = sa;
	}

	public String getPgDn() {
		return pgDn;
	}

	public List<ProjectType> getProjects() {
		return projects;
	}

	public List<ProjectType> getExportableProjects() {
		return exportableProjects;
	}

	public void setExportableProjects(List<ProjectType> exportableProjects) {
		this.exportableProjects = exportableProjects;
	}

	public List<ProjectType> getAuditableProjects() {
		return auditableProjects;
	}

	public void setAuditableProjects(List<ProjectType> auditableProjects) {
		this.auditableProjects = auditableProjects;
	}

	public List<ProjectType> getImmediatelyExportableProjects() {
		return immediatelyExportableProjects;
	}

	public void setImmediatelyExportableProjects(
			List<ProjectType> immediatelyExportableProjects) {
		this.immediatelyExportableProjects = immediatelyExportableProjects;
	}

	public Map<ProjectType, List<GroupType>> getGroups() {
		return groups;
	}

	public long getServerTimeDifference() {
		return serverTimeDifference;
	}

	public void addAuthorities(GrantedAuthority[] authorities){
		GrantedAuthority[] currentAuthorities = this.authorities;
		this.authorities = new GrantedAuthority[this.authorities.length + authorities.length];
		int counter = 0;
		for (GrantedAuthority ga: currentAuthorities){
			this.authorities[counter] = ga;
			counter++;
		}
		for (GrantedAuthority ga: authorities){
			this.authorities[counter] = ga;
			counter++;
		}
	}
	
	public void setPassword(String password){
		this.password = password;
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
		
	public void resetAuthorities(GrantedAuthority[] authorities){
		this.authorities = new GrantedAuthority[authorities.length + globalAuthorities.length];
		int counter = 0;
		for (GrantedAuthority ga: globalAuthorities){
			this.authorities[counter] = ga;
			counter++;
		}
		for (GrantedAuthority ga: authorities){
			this.authorities[counter] = ga;
			counter++;
		}
	}
	
}
