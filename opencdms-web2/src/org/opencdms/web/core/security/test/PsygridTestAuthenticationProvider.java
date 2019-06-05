/*
Copyright (c) 2006-2009, The University of Manchester, UK.

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

package org.opencdms.web.core.security.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.acegisecurity.AuthenticationException;
import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.acegisecurity.providers.dao.AbstractUserDetailsAuthenticationProvider;
import org.acegisecurity.userdetails.UserDetails;
import org.opencdms.web.core.security.ldap.PsygridLdapUserDetailsImpl;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.ProjectType;

/**
 * @author rsh
 *
 */
public class PsygridTestAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

	@Override
	protected void additionalAuthenticationChecks(UserDetails userDetails,
			UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected UserDetails retrieveUser(String username,
			UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException {
		List<ProjectType> projects = new ArrayList<ProjectType>();
		projects.add(new ProjectType("Outlook", "OLK", "Outlook", "OLK", false));
		Map<ProjectType, List<GroupType>> groups = new HashMap<ProjectType, List<GroupType>>();
		List<GroupType> olkGroups = new ArrayList<GroupType>();
		olkGroups.add(new GroupType("East Anglia-Norfolk and Waveney Mental Health Partnership Trust", "002001", null));
		olkGroups.add(new GroupType("East Anglia-Cambridge CAMEO", "002002", null));
		groups.put(projects.get(0), olkGroups);
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new GrantedAuthorityImpl("ROLE_USER"));
		authorities.add(new GrantedAuthorityImpl("ROLE_QUERY"));
		authorities.add(new GrantedAuthorityImpl("ROLE_EXPORT"));
		authorities.add(new GrantedAuthorityImpl("ROLE_AUDIT"));
		authorities.add(new GrantedAuthorityImpl("ROLE_ESLWEB"));
		authorities.add(new GrantedAuthorityImpl("ROLE_REPORTS"));
		PsygridLdapUserDetailsImpl pgud = 
			new PsygridLdapUserDetailsImpl(username, (String)authentication.getCredentials(), 
					"NoUser", projects, groups, authorities.toArray(new GrantedAuthority[authorities.size()]));
		return pgud;

	}

}
